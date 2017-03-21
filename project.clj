(defproject try-lacinia "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring/ring-jetty-adapter "1.5.1"]
                 [ring/ring-json "0.4.0"]
                 [compojure "1.5.2"]
                 [com.walmartlabs/lacinia "0.13.0"]]

  :plugins [[lein-ring "0.11.0"]]
  
  :ring {:handler try-lacinia.core/app})
