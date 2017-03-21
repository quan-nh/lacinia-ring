(ns try-lacinia.db)

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
