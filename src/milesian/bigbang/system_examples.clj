(ns milesian.bigbang.system-examples
  (:require [com.stuartsierra.component :as component]))

(defn- create-state [k]
  {:state (str "state " k ": "  (rand-int Integer/MAX_VALUE))})

(defprotocol Listen
  (listening [_]))

(defprotocol Talk
  (talking [_]))

(defrecord ComponentA [state]
  component/Lifecycle
  (start [this]
    this)
  (stop [this]
    this))

(defn component-a [] (->ComponentA (create-state :a)))

(defrecord ComponentB [state a]
  component/Lifecycle
  (start [this]
    this)
  (stop [this]
    this)

  Listen
  (listening [this]
    "I'm component :b, listening now ..."))

(defn component-b [] (map->ComponentB (create-state :b)))

(defrecord ComponentC [state a b]
  Talk
  (talking [this]
    (str state "I'm :c, I'm talking and now listening to :b " (listening b)))
  )

(defn component-c [] (map->ComponentC (create-state :c)))

(defrecord System1 [a b c])

(defn system-1 []
  (map->System1 {:a (-> (component-a))
                 :b (-> (component-b)
                        (component/using [:a]))
                 :c (-> (component-c)
                        (component/using [:a :b]))}))
