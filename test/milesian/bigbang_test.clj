(ns milesian.bigbang-test
  (:require [clojure.repl :refer (apropos dir doc find-doc pst source)]
            [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [milesian.bigbang :as bigbang]
            [milesian.aop :as aop]
            [milesian.identity :as identity]
            [milesian.bigbang.system-examples :refer (new-system-map listening talking)]))




(def system-map (new-system-map))

(def system (bigbang/expand system-map {:before-start [[identity/add-meta-key system-map]]
                                        :on-start [[identity/assoc-meta-who-to-deps]
                                                   [component/start]
                                                   [aop/wrap improved-logging]]
                                        :after-start []}))


(listening (:b system))
(talking (:c system))
