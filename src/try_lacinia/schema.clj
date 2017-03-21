(ns try-lacinia.schema
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [com.walmartlabs.lacinia.util :refer [attach-resolvers]]
            [com.walmartlabs.lacinia.schema :as schema]
            [try-lacinia.db :as db]))

(def star-wars-schema
  (-> (io/resource "star-wars-schema.edn")
      slurp
      edn/read-string
      (attach-resolvers {:resolve-hero    db/resolve-hero
                         :resolve-human   db/resolve-human
                         :resolve-droid   db/resolve-droid
                         :resolve-friends db/resolve-friends})
      schema/compile))
