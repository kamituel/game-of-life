(ns game-of-life.utils)


(defn add-index [xs]

  (map-indexed
   (fn [idx x] [idx x])
   xs))


(defn measure-time [f]

  (let [start-time (js/performance.now)]
    (f)
    (- (js/performance.now) start-time)))
