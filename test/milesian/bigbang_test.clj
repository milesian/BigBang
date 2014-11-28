(ns milesian.bigbang-test
  (:require [clojure.repl :refer (apropos dir doc find-doc pst source)]
            [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [milesian.bigbang :as bigbang]
            [milesian.epi-component :as epi-co]
            [milesian.id-component :as id-co]
            [milesian.bigbang.system-examples :refer (new-system-map listening talking)]))




(def system-map (new-system-map))

(def system (bigbang/expand system-map {:before-start [[id-co/add-meta-key system-map]]
                                        :on-start [[epi-co/assoc-meta-who-to-deps]
                                                   [component/start]
                                                   [epi-co/wrap improved-logging]]
                                        :after-start []}))


(listening (:b system))
(talking (:c system))
