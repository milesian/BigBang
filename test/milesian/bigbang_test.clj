(ns milesian.bigbang-test
  (:require [clojure.repl :refer (apropos dir doc find-doc pst source)]
            [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [milesian.bigbang :as bigbang]
            [milesian.aop :as aop]
            [milesian.aop.matchers :as aop-matchers]
            [milesian.aop.utils  :refer (logging-function-invocation)]
            [milesian.identity :as identity]
            [clojure.string :as st]
            [defrecord-wrapper.aop :refer (new-simple-protocol-matcher)]
            [milesian.system-examples :refer (new-system-map listening talking Listen Talk)]))



(def system-map (new-system-map))

(def system (bigbang/expand system-map {:before-start [[identity/add-meta-key system-map]
                                                       [identity/assoc-meta-who-to-deps]]
                                        :after-start [[aop/wrap (new-simple-protocol-matcher
                                                                      :protocols [Listen #_Talk]
                                                                      :fn logging-function-invocation)]

                                                           #_[aop/wrap (aop-matchers/new-component-matcher :system system-map :components [:c] :fn logging-function-invocation)]

                                                      ]}))


(listening (:b system))
(talking (:c system))
