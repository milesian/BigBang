(defproject milesian/bigbang "0.1.1-SNAPSHOT"
  :description "expanding stuartsierra/component update-system functionality"
  :url "https://github.com/milesian/bigbang"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.stuartsierra/component "0.2.2"]]
  :profiles {:dev {:dependencies [[milesian/epi-component "0.1.1"]
                                  [milesian/id-component "0.1.1"]]}})
