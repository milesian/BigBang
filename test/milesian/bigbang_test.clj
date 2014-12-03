(ns milesian.bigbang-test
  (:require [clojure.repl :refer (apropos dir doc find-doc pst source)]
            [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [clojure.string :as st]
            [milesian.bigbang :as bigbang]
            [milesian.aop :as aop]
            [milesian.identity :as identity]
            [milesian.bigbang.system-examples :refer (new-system-map listening talking)]
            [tangrammer.component.co-dependency :as co-dependency]
            ))




(def system-map (co-dependency/system-co-using (new-system-map) {:a [:b]}))

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
                              ) (name component-key) fn-name formatted-args))
  )

(defn logging-function-invocation
  [*fn* this & args]
  (println (function-invocation *fn* this args))
  (apply *fn* (conj args this)))


(def system (bigbang/expand system-map
                            {:before-start [[identity/add-meta-key system-map]]
                             :on-start     [[identity/assoc-meta-who-to-deps]
                                            ;;                                            [component/start]
                                            [co-dependency/assoc-co-deps-and-start (atom system-map)]
                                            [aop/wrap logging-function-invocation]]
                             :after-start  []}))


(listening (:b system))
(talking (:c system))
@(:b (:a system))
