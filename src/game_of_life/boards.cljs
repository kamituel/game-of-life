(ns game-of-life.boards
  
  (:require [clojure.string :as str]
            [game-of-life.game :as game]
            [game-of-life.utils :as utils]))


(defmulti generate-board
  (fn [board-type height width]
    board-type))


(defn all-dead-board [height width]

  (let [board (make-array (* height width))]
    (dotimes [pos (* width height)]
      (aset board pos game/dead))
    board))


(defn set-cell [board width row-index col-index status]

  (aset board (+ (* row-index width) col-index) status))


(def pulsar-template
  ["..OOO...OOO.."
   "............."
   "O....O.O....O"
   "O....O.O....O"
   "O....O.O....O"
   "..OOO...OOO.."
   "............."
   "..OOO...OOO.."
   "O....O.O....O"
   "O....O.O....O"
   "O....O.O....O"
   "............."
   "..OOO...OOO.."])


(def blinker-template
  
  ["OOO"])


(def toad-template
  
  [".OOO"
   "OOO."])


(def beacon-template
  
  ["OO..."
   "OO..."
   "..OO"
   "..OO"])


(def glider-template
  
  ["..O"
   "O.O"
   ".OO"])


(def r-pentomino-template
  
  [".OO"
   "OO."
   ".O."])


(def gosper-glider-gun
  
  [
   "........................O..........."
   "......................O.O..........."
   "............OO......OO............OO"
   "...........O...O....OO............OO"
   "OO........O.....O...OO.............."
   "OO........O...O.OO....O.O..........."
   "..........O.....O.......O..........."
   "...........O...O...................."
   "............OO......................"])


(def lightweight-space-ship-template

  [".O..O"
   "O...."
   "O...O"
   "OOOO."])


;; By Bill Gosper, 1971
(def puffer-train-1-template

  [".OOO......O.....O......OOO."
   "O..O.....OOO...OOO.....O..O"
   "...O....OO.O...O.OO....O..."
   "...O...................O..."
   "...O..O.............O..O..."
   "...O..OO...........OO..O..."
   "..O...OO...........OO...O.."])


;; By Richard Schank, 2014
(def puffer-train-2-template
  
  ["...O.......O..."
   "..OOO.....OOO.."
   ".OO..O...O..OO."
   "...OOO...OOO..."
   "..............."
   "....O.....O...."
   "..O..O...O..O.."
   "O.....O.O.....O"
   "OO....O.O....OO"
   "......O.O......"
   "...O.O...O.O..."
   "....O.....O...."])


(defn rotate-template [template]

  (loop [n                0
         rotated-template []]
    
    (if (= n (count (first template)))

      (map str/join rotated-template)

      (recur (inc n)
             (conj rotated-template
                   (map #(nth % n) template))))))


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
      (draw-template width 20 20 blinker-template)
      (draw-template width 20 30 toad-template)
      (draw-template width 20 40 pulsar-template)
      #_(draw-template width 20 60 glider-template)
      (draw-template width 20 60 beacon-template)
      (draw-template width 60 220 lightweight-space-ship-template)
      (draw-template width 40 20 gosper-glider-gun)
      #_(draw-template width 20 90 r-pentomino-template))))


(defmethod generate-board :r-pentomino [_ height width]

  (doto (all-dead-board height width)
    (draw-template width (int (/ height 2)) (int (/ width 2)) r-pentomino-template)))


(defmethod generate-board :puffer-train-1 [_ height width]
  
  (let [template (->> puffer-train-1-template
                      reverse
                      rotate-template)]
    (doto (all-dead-board height width)
      (draw-template width
                     (int (- (/ height 2)
                             (/ (count template) 2)))
                     2
                     template))))


(defmethod generate-board :puffer-train-2 [_ height width]

  (let [template (->> puffer-train-2-template
                      reverse
                      rotate-template)]
    (doto (all-dead-board height width)
      (draw-template width
                     (int (- (/ height 2)
                             (/ (count template) 2)))
                     2
                     template))))