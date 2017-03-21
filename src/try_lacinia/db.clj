(ns try-lacinia.db)

(defn resolve-hero [context arguments value]
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

(defn resolve-human [context arguments value]
  (let [{:keys [id]} arguments]
    (if (= id 1001)
      {:id          1001
       :name        "Darth Vader"
       :home-planet "Tatooine"
       :appears-in  ["NEWHOPE" "EMPIRE" "JEDI"]}
      {:id          2000
       :name        "Lando Calrissian"
       :home-planet "Socorro"
       :appears-in  ["EMPIRE" "JEDI"]})))

(defn resolve-droid [context arguments value]
  (constantly {}))

(defn resolve-friends [context arguments value]
  (constantly {}))
