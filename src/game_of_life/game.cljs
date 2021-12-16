(ns game-of-life.game
  
  (:require [game-of-life.utils :as utils]))


(defn offset-position [size pos offset]
  
  (let [result (+ pos offset)]

    (cond
      (< result 0)
      (+ size result)

      (> result (dec size))
      (- size result)

      :else
      result)))

;; 0 1 2
;; 3 . 4
;; 5 6 7
(defn all-neighbours-positions [height width row-index col-index]

  [[(offset-position height row-index -1)
    (offset-position width col-index -1)]
   [(offset-position height row-index -1)
    col-index]
   [(offset-position height row-index -1)
    (offset-position width col-index 1)]
   [row-index
    (offset-position width col-index -1)]
   [row-index
    (offset-position width col-index 1)]
   [(offset-position height row-index 1)
    (offset-position width col-index -1)]
   [(offset-position height row-index 1)
    col-index]
   [(offset-position height row-index 1)
    (offset-position width col-index 1)]])


(defn get-cell [board row-index col-index]
  
  (-> board
      (nth row-index)
      (nth col-index)))



(defn evolve [board config]
  
  (let [{:keys [width height]} config]

    (doall
     (for [[row-index row] (utils/add-index board)]
       (doall
        (for [[col-index status] (utils/add-index row)]

          (let [neighbours-indexes (all-neighbours-positions height width row-index col-index)
                neigbours (map (fn [[row-index col-index]]
                                 (get-cell board row-index col-index))
                               neighbours-indexes)
                number-of-alive? (count (filter #(= :alive %) neigbours))]

            (condp = [status number-of-alive?]
              [:alive 2] :alive
              [:alive 3] :alive
              [:dead 3] :alive
              :dead))))))))
