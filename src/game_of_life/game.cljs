(ns game-of-life.game)


(def alive 1)
(def dead 0)


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

    (if (= neighbour-index
           (count neighbours-coordinates-offsets))

      cnt

      (let [coordinate-offset   (nth neighbours-coordinates-offsets neighbour-index)
            neighbour-row-index (offset-position height row-index (first coordinate-offset))
            neighbour-col-index (offset-position width col-index (second coordinate-offset))
            neighbour-pos       (+ (* neighbour-row-index width)
                                   neighbour-col-index)]

        (recur (+ cnt (aget board neighbour-pos))
               (inc neighbour-index))))))


(defn evolve [board height width]
  
  (let [new-board (make-array (* height width))]

    (dotimes [pos (* height width)]
      (let [row-index (int (/ pos width))
            col-index (rem pos width)
            status    (aget board pos)
            number-of-alive-neighbours (count-alive-neighbours board height width row-index col-index)]

        (aset new-board pos
              (condp = [status number-of-alive-neighbours]
                [alive 2] alive
                [alive 3] alive
                [dead 3] alive
                dead))))

    new-board))
