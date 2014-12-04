(ns milesian.bigbang-test
  (:require [clojure.repl :refer (apropos dir doc find-doc pst source)]
            [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [milesian.bigbang :as bigbang]
            [milesian.aop :as aop :refer (new-component-matcher)]
            [milesian.identity :as identity]
            [clojure.string :as st]
            [defrecord-wrapper.aop :refer (new-simple-protocol-matcher)]
            [milesian.system-examples :refer (new-system-map listening talking Listen Talk)]))



(defn function-invocation
  [*fn* this args]
  (let [component-key (:bigbang/key (meta this))
        [fn-name fn-args]((juxt :function-name :function-args) (meta *fn*))
        who (:bigbang/who (meta (:wrapper (meta *fn*))))
        formatted-args (-> (st/replace (str fn-args) #"\[" "")
                                           (st/replace #"\]" "")
                                           (st/split #" ")
                                           (next)
                                           ((partial st/join " " )))]
    (format "%s->%s: %s %s" (if-not (nil? who)
                              (name who)
                              "REPL"
                              ) (name component-key) fn-name formatted-args)))

(defn logging-function-invocation
  [*fn* this & args]
  (println (function-invocation *fn* this args))
  (apply *fn* (conj args this)))

(def system-map (new-system-map))

(def system (bigbang/expand system-map {:before-start [[identity/add-meta-key system-map]]
                                        :on-start [[identity/assoc-meta-who-to-deps]
                                                   [component/start]
                                                   [aop/wrap (new-simple-protocol-matcher :protocols [Listen #_Talk] :fn logging-function-invocation)]
                                                   #_[aop/wrap (aop/new-component-matcher :system system-map :components [:c] :fn logging-function-invocation)]
                                                   ]
                                        :after-start []}))


(listening (:b system))
(talking (:c system))
