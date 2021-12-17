(ns game-of-life.boards
  
  (:require [clojure.string :as str]
            [game-of-life.game :as game]
            [game-of-life.utils :as utils]))


(defmulti generate-board
  (fn [board-type height width]
    board-type))


(defn set-cell [board width row-index col-index status]

  (aset board (+ (* row-index width) col-index) status))


(def pulsar-template
  ["..XXX...XXX.."
   "............."
   "X....X.X....X"
   "X....X.X....X"
   "X....X.X....X"
   "..XXX...XXX.."
   "............."
   "..XXX...XXX.."
   "X....X.X....X"
   "X....X.X....X"
   "X....X.X....X"
   "............."
   "..XXX...XXX.."])


(def blinker-template
  
  ["XXX"])


(def toad-template
  
  [".XXX"
   "XXX."])


(def beacon-template
  
  ["XX..."
   "XX..."
   "..XX"
   "..XX"])


(def glider-template
  
  ["..X"
   "X.X"
   ".XX"])


(def r-pentomino-template
  
  [".XX"
   "XX."
   ".X."])


(def gosper-glider-gun
  
  [
   "........................X..........."
   "......................X.X..........."
   "............XX......XX............XX"
   "...........X...X....XX............XX"
   "XX........X.....X...XX.............."
   "XX........X...X.XX....X.X..........."
   "..........X.....X.......X..........."
   "...........X...X...................."
   "............XX......................"])


(def lightweight-space-ship-template

  [".X..X"
   "X...."
   "X...X"
   "XXXX."])


(defn draw-template [board width row-index col-index template]

  (doseq [[row-offset row] (utils/add-index template)]
    (doseq [[col-offset status] (-> row
                                    (str/split #"")
                                    (->> (map #(if (= "X" %) game/alive game/dead)))
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
