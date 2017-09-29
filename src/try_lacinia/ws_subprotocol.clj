(ns try-lacinia.ws-subprotocol
  (:require [clojure.string :refer [split trim lower-case]]
            [org.httpkit.server :refer [sec-websocket-accept]])
  (:import (org.httpkit.server AsyncChannel)))

(defn origin-match? [origin-re req]
  (if-let [req-origin (get-in req [:headers "origin"])]
    (re-matches origin-re req-origin)))

(defn subprotocol? [proto req]
  (if-let [protocols (get-in req [:headers "sec-websocket-protocol"])]
    (some #{proto}
          (map #(lower-case (trim %))
               (split protocols #",")))))

(defmacro with-subproto-channel
  [request ch-name origin-re subproto & body]
  `(let [~ch-name (:async-channel ~request)]
     (if (:websocket? ~request)
       (if-let [key# (get-in ~request [:headers "sec-websocket-key"])]
         (if (origin-match? ~origin-re ~request)
           (if (subprotocol? ~subproto ~request)
             (do
               (.sendHandshake ~(with-meta ch-name {:tag `AsyncChannel})
                               {"Upgrade"                "websocket"
                                "Connection"             "Upgrade"
                                "Sec-WebSocket-Accept"   (sec-websocket-accept key#)
                                "Sec-WebSocket-Protocol" ~subproto})
               ~@body
               {:body ~ch-name})
             {:status 400 :body "missing or bad WebSocket-Protocol"})
           {:status 400 :body "missing or bad WebSocket-Origin"})
         {:status 400 :body "missing or bad WebSocket-Key"})
       {:status 400 :body "not websocket protocol"})))