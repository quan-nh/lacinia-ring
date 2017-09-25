(ns try-lacinia.core
  (:require [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.util.response :as resp :refer [response]]
            [ring.util.request :refer [body-string]]
            [compojure.core :refer [GET POST defroutes]]
            [com.walmartlabs.lacinia :as ql]
            [try-lacinia.schema :as schema]))

(defn handler [request]
  (response (ql/execute schema/star-wars-schema
                        (body-string request)
                        nil nil)))

(defroutes my-routes
           (GET "/" [] (resp/redirect "/index.html"))
           (POST "/graphql" request (handler request)))

(def app
  (-> my-routes
      (wrap-resource "graphiql")
      wrap-json-response))
