(ns try-lacinia.core
  (:require [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.util.response :as resp :refer [response]]
            [ring.util.request :refer [body-string]]
            [compojure.core :refer [GET POST defroutes]]
            [com.walmartlabs.lacinia :as ql]
            [org.httpkit.server :refer [run-server with-channel on-close on-receive send!]]
            [try-lacinia.schema :as schema]))

(defn graphql-handler [request]
  (response (ql/execute schema/star-wars-schema
                        (body-string request)
                        nil nil)))

(defn graphql-ws-handler [request]
  (with-channel request ch
                (println "New WebSocket channel:" ch)
                (on-receive ch (fn [msg] (println "on-receive:" msg)))
                (on-close ch (fn [status] (println "on-close:" status)))
                (send! ch "First chat message!")))

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