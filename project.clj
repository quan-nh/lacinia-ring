(defproject try-lacinia "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-beta1"]
                 [ring/ring-core "1.6.2"]
                 [http-kit "2.2.0"]
                 [ring/ring-json "0.4.0"]
                 [compojure "1.6.0"]
                 [com.walmartlabs/lacinia "0.21.0"]]
  :main try-lacinia.core)
