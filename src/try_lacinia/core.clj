(ns try-lacinia.core
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [com.walmartlabs.lacinia :as ql]
            [com.walmartlabs.lacinia.util :refer [attach-resolvers]]
            [com.walmartlabs.lacinia.schema :as schema]))

(defn get-hero [context arguments value]
  (let [{:keys [episode]} arguments]
    (if (= episode "NEWHOPE")
      {:id          1000
       :name        "Luke"
       :home-planet "Tatooine"
       :appears-in  ["NEWHOPE" "EMPIRE" "JEDI"]}
      {:id          2000
       :name        "Lando Calrissian"
       :home-planet "Socorro"
       :appears-in  ["EMPIRE" "JEDI"]})))

(def star-wars-schema
  (-> (io/resource "star-wars-schema.edn")
      slurp
      edn/read-string
      (attach-resolvers {:get-hero  get-hero
                         :get-droid (constantly {})})
      schema/compile))

(ql/execute star-wars-schema
            "query { hero { id name }}"
            nil nil)
;;=> {:data {:hero #ordered/map([:id 2000] [:name "Lando Calrissian"])}}

(ql/execute star-wars-schema
            "query { droid { id name }}"
            nil nil)
;;=> {:data {:droid #ordered/map([:id nil] [:name nil])}}

(ql/execute star-wars-schema
            "query { hero(episode: NEWHOPE){ id name appears_in}}"
            nil nil)
;;=> {:data {:hero #ordered/map([:id 1000] [:name "Luke"] [:appears_in ["NEWHOPE" "EMPIRE" "JEDI"]])}}

(ql/execute star-wars-schema
            "query { hero(id: 1000){ id name appears_in}}"
            nil nil)
;;=>
{:errors [{:message "Exception applying arguments to field `hero': Unknown argument `id'.",
           :query-path [],
           :locations [{:line 1, :column 6}],
           :field :hero,
           :argument :id,
           :defined-arguments (:episode)}]}