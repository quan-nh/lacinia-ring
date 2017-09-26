(ns try-lacinia.subscription
  (:require [clojure.pprint :refer [pprint]]
            [com.walmartlabs.lacinia.constants :as constants]
            [com.walmartlabs.lacinia.executor :as executor]
            [com.walmartlabs.lacinia.parser :as parser]
            [try-lacinia.schema :as schema])
  (:import (clojure.lang ExceptionInfo)))

(let [query-string "subscription ($msg: String) { ping (count: 4 message: $msg) { message }}"
      [prepared-query errors] (try
                                [(-> schema/ping-schema
                                     (parser/parse-query query-string)
                                     (parser/prepare-with-query-variables {:msg "test"}))]
                                (catch ExceptionInfo e
                                  [nil e]))]
  (if (some? errors)
    (pprint (ex-data errors))
    (let [cleanup-fn (executor/invoke-streamer {constants/parsed-query-key prepared-query} prn)]
      (println "call cleanup-fn return" (cleanup-fn)))))
