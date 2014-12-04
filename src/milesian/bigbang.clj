(ns milesian.bigbang
  (:require [com.stuartsierra.component :as component]))

(defn expand
  [system-map {:keys [before-start after-start]}]
  (let [on-start-sequence (apply conj before-start (cons [component/start] after-start))
        bigbang-start (fn [c & args]
                            (apply (->> (mapv (fn [[f & args]] #(apply f (conj args %))) on-start-sequence)
                                        reverse
                                        (apply comp)) (conj args c)))]

    (component/update-system system-map (keys system-map) bigbang-start))

  )
