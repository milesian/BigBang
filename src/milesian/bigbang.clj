(ns milesian.bigbang
  (:require [com.stuartsierra.component :as component]))


(defn expand
  [system-map {:keys [before-start on-start after-start]}]
  (let [before-started (reduce (fn [s [f args]]
                                  (component/update-system s (keys s) f args)) system-map before-start)
        system-started (->>
                        (fn [c & args]
                          (apply (->> (mapv (fn [[f & args]] #(apply f (conj args %))) on-start)
                                reverse
                                (apply comp)) (conj args c)))
                        (component/update-system before-started (keys before-started)))
         after-started (reduce (fn [s [f args]]
                                 (component/update-system s (keys s) f args)) system-started after-start)]
    after-started))
