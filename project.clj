(defproject milesian/bigbang "0.1.2-SNAPSHOT"
  :description "expanding stuartsierra/component update-system functionality"
  :url "https://github.com/milesian/BigBang"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.stuartsierra/component "0.2.2"]]
  :profiles {:dev {:dependencies [[milesian/aop "0.1.3"]
                                  [milesian/identity "0.1.3"]
                                  [milesian/system-examples "0.1.1"]]}})
