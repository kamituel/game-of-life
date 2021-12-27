(ns game-of-life.impl.game-cljs
  
  (:require [game-of-life.boards :as boards]
            [game-of-life.game :as game]))


(defn offset-position [size pos offset]

  (let [result (+ pos offset)]

    (cond
      (< result 0)
      (+ size result)

      (> result (dec size))
      (- size result)

      :else
      result)))


(def neighbours-coordinates-offsets

  [[-1 -1]
   [-1 0]
   [-1 1]
   [0 -1]
   [0 1]
   [1 -1]
   [1 0]
   [1 1]])


(defn count-alive-neighbours [board height width row-index col-index]

  (loop [cnt             0
         neighbour-index 0]

    (cond

      (= neighbour-index
         (count neighbours-coordinates-offsets))
      cnt

      ;; All cells with 4 neighbours or more will die
      ;; anyway, so no need to count all the neighbours.
      (>= cnt 4)
      4

      :else
      (let [coordinate-offset   (nth neighbours-coordinates-offsets neighbour-index)
            neighbour-row-index (offset-position height row-index (first coordinate-offset))
            neighbour-col-index (offset-position width col-index (second coordinate-offset))
            neighbour-pos       (+ (* neighbour-row-index width)
                                   neighbour-col-index)]

        (recur (+ cnt (aget board neighbour-pos))
               (inc neighbour-index))))))


(defmethod game/init :cljs

  []

  ;; No initialization necessary.

  )


(defmethod game/evolve :cljs

  [_ board height width]

  (let [new-board (boards/create-board height width)]

    (dotimes [pos (* height width)]
      (let [row-index (int (/ pos width))
            col-index (rem pos width)
            status    (aget board pos)
            number-of-alive-neighbours (count-alive-neighbours
                                        board height width row-index col-index)]

        (aset new-board pos
              (condp = [status number-of-alive-neighbours]
                [boards/alive 2] boards/alive
                [boards/alive 3] boards/alive
                [boards/dead 3] boards/alive
                boards/dead))))

    new-board))
