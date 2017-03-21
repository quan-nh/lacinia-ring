(ns try-lacinia.core
  (:require [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [response]]
            [ring.util.request :refer [body-string]]
            [compojure.core :refer [POST defroutes]]
            [com.walmartlabs.lacinia :as ql]
            [try-lacinia.schema :as schema]))

(defn handler [request]
  (response (ql/execute schema/star-wars-schema
                        (body-string request)
                        nil nil)))

(defroutes my-routes
           (POST "/graphql" request (handler request)))

(def app
  (-> my-routes
      wrap-json-response))
