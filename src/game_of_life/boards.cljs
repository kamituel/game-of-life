(ns game-of-life.boards
  
  (:require [clojure.string :as str]
            [game-of-life.game :as game]
            [game-of-life.templates :as templates]
            [game-of-life.utils :as utils]))


(defn- all-dead-board [height width]

  (let [board (make-array (* height width))]
    (dotimes [pos (* width height)]
      (aset board pos game/dead))
    board))


(defn- set-cell [board width row-index col-index status]

  (aset board (+ (* row-index width) col-index) status))


(defn draw-template [board width row-index col-index template]

  (doseq [[row-offset row] (utils/add-index template)]
    (doseq [[col-offset status] (-> row
                                    (str/split #"")
                                    (->> (map #(if (= "O" %) game/alive game/dead)))
                                    utils/add-index)]
      (set-cell board width (+ row-index row-offset) (+ col-index col-offset) status))))


(defn generate-random-board [height width]

  (let [board (make-array (* height width))]

    (dotimes [pos (* width height)]
      (aset board pos (rand-nth [0 1])))

    board))


(defn generate-board-from-template [template height width]

  (doto (all-dead-board height width)
    (draw-template
     width
     (int (/ (- height (templates/template-height template)) 2))
     (int (/ (- width (templates/template-width template)) 2))
     template)))