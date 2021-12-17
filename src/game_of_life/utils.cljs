(ns game-of-life.utils
  
  (:require [clojure.string :as str]))


(defn add-index [xs]

  (map-indexed
   (fn [idx x] [idx x])
   xs))


(defn measure-time [f]

  (let [start-time (js/performance.now)
        result     (f)]
    [(- (js/performance.now) start-time)
     result]))


(defn print-board! [board]

  (doseq [[row-index row] (add-index board)]
    (js/console.log
     (if (< row-index 10)
       (str " " row-index)
       (str row-index))
     (str/join "" (map #(if (= :alive %) 1 0) row)))))
