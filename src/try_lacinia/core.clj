(ns try-lacinia.core
  (:require [com.walmartlabs.lacinia :as ql]
            [com.walmartlabs.lacinia.constants :as constants]
            [com.walmartlabs.lacinia.executor :as executor]
            [com.walmartlabs.lacinia.parser :as parser]
            [compojure.core :refer [GET POST defroutes]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.util.request :refer [body-string]]
            [ring.util.response :as resp :refer [response]]
            [org.httpkit.server :refer [run-server with-channel on-close on-receive send!]]
            [try-lacinia.schema :as schema])
  (:import (clojure.lang ExceptionInfo)))

(defn graphql-handler [request]
  (response (ql/execute schema/star-wars-schema
                        (body-string request)
                        nil nil)))

(defn subscription [channel query-string]
  (let [[prepared-query errors] (try
                                  [(-> schema/ping-schema
                                       (parser/parse-query query-string)
                                       (parser/prepare-with-query-variables {:msg "test"}))]
                                  (catch ExceptionInfo e
                                    [nil e]))]
    (if (some? errors)
      (send! channel (str (ex-data errors)))
      (let [cleanup-fn (executor/invoke-streamer {constants/parsed-query-key prepared-query} #(send! channel (str %)))]
        (println "call cleanup-fn return" (cleanup-fn))))))

(defn graphql-ws-handler [request]
  (with-channel request channel
                (println "New WebSocket channel:" channel)
                (on-receive channel (fn [msg]
                                      (println "on-receive:" msg)
                                      (subscription channel msg)))
                (on-close channel (fn [status] (println "on-close:" status)))))

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