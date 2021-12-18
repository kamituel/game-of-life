(ns game-of-life.boards
  
  (:require [clojure.string :as str]
            [game-of-life.game :as game]
            [game-of-life.templates :as templates]
            [game-of-life.utils :as utils]))


(defmulti generate-board
  (fn [board-type _height _width]
    board-type))


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


(defmethod generate-board :random-50 [_ height width]

  (let [board (make-array (* height width))]

    (dotimes [pos (* width height)]
      (aset board pos (rand-nth [0 1])))

    board))


(defmethod generate-board :sample-1 [_ height width]

  (let [board (make-array (* height width))]
    
    (dotimes [pos (* width height)]
      (aset board pos 0))

    (doto board
      (draw-template width 20 20 (templates/get-template :blinker-template))
      (draw-template width 20 30 (templates/get-template :toad-template))
      (draw-template width 20 40 (templates/get-template :pulsar-template))
      (draw-template width 20 60 (templates/get-template :beacon-template))
      (draw-template width 60 220 (templates/get-template :lightweight-space-ship-template))
      (draw-template width 40 20 (templates/get-template :gosper-glider-gun)))))


(defmethod generate-board :r-pentomino [_ height width]

  (doto (all-dead-board height width)
    (draw-template
     width
     (int (/ height 2))
     (int (/ width 2))
     (templates/get-template :r-pentomino-template))))


(defmethod generate-board :puffer-train-1 [_ height width]
  
  (let [template (->> (templates/get-template :puffer-train-1-template)
                      reverse
                      templates/rotate-template)]
    (doto (all-dead-board height width)
      (draw-template width
                     (int (- (/ height 2)
                             (/ (templates/template-height template) 2)))
                     2
                     template))))


(defmethod generate-board :puffer-train-2 [_ height width]

  (let [template (->> (templates/get-template :puffer-train-2-template)
                      reverse
                      templates/rotate-template)]
    (doto (all-dead-board height width)
      (draw-template width
                     (int (- (/ height 2)
                             (/ (templates/template-height template) 2)))
                     2
                     template))))


(defmethod generate-board :p60-gun [_ height width]
  
  (doto (all-dead-board height width)
    (draw-template width
                   10
                   10
                   (templates/get-template :p60-gun-template))))


(defmethod generate-board :shuttle [_ height width]

  (let [template (templates/get-template :shuttle)]
    (doto (all-dead-board height width)
      (draw-template width
                     (int (/ (- height (templates/template-height template)) 2))
                     (int (/ (- width (templates/template-width template)) 2))
                     template))))
