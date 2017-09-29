(ns try-lacinia.core
  (:require [cheshire.core :as json]
            [com.walmartlabs.lacinia :as ql]
            [com.walmartlabs.lacinia.constants :as constants]
            [com.walmartlabs.lacinia.executor :as executor]
            [com.walmartlabs.lacinia.parser :as parser]
            [compojure.core :refer [GET POST defroutes]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.util.request :refer [body-string]]
            [ring.util.response :as resp :refer [response]]
            [org.httpkit.server :refer [run-server with-channel on-close on-receive send!]]
            [try-lacinia.schema :as schema]
            [try-lacinia.ws-subprotocol :refer [with-subproto-channel]])
  (:import (clojure.lang ExceptionInfo)))

(defn graphql-handler [request]
  (response (ql/execute schema/star-wars-schema
                        (body-string request)
                        nil nil)))

(defn subscription [channel id query-string]
  (let [[prepared-query errors] (try
                                  [(-> schema/ping-schema
                                       (parser/parse-query query-string)
                                       (parser/prepare-with-query-variables {:msg "test"}))]
                                  (catch ExceptionInfo e
                                    [nil e]))
        source-stream-fn (fn [data]
                           (println "sending" data)
                           (send! channel (json/generate-string {:type    :data
                                                                 :id      id
                                                                 :payload {:data data}})))]
    (if (some? errors)
      (send! channel (str (ex-data errors)))
      (let [cleanup-fn (executor/invoke-streamer {constants/parsed-query-key prepared-query} source-stream-fn)]
        (println "call cleanup-fn return" (cleanup-fn))))))

(defn graphql-ws-handler [request]
  (with-subproto-channel request channel #".*" "graphql-ws"
                         (println "New WebSocket channel:" channel)
                         (on-receive channel
                                     (fn [data]
                                       (let [{:keys [id payload type]} (json/parse-string data keyword)]
                                         (case type
                                           "connection_init"
                                           (send! channel (json/generate-string {:type "connection_ack"}))

                                           "start"
                                           (subscription channel id (:query payload))

                                           "stop"
                                           (prn "todo")

                                           "connection_terminate"
                                           (prn :todo)

                                           ;; Not recognized!
                                           (prn type)))))

                         (on-close channel
                                   ;; todo: cleanup subscritions
                                   (fn [status] (println "on-close:" status)))))

(defroutes my-routes
           (GET "/" [] (resp/redirect "/index.html"))
           (POST "/graphql" request (graphql-handler request))
           (GET "/graphql-ws" request (graphql-ws-handler request)))

(def app
  (-> my-routes
      (wrap-resource "graphiql")
      wrap-json-response))

(defn -main [& args]
  (run-server app {:port 3000}))