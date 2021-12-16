(ns game-of-life.boards)


(defn set-cell [board row-index col-index status]

  (assoc-in board [row-index col-index] status))


(defn random-board [width height]

  (repeatedly height
              (fn []
                (repeatedly width
                            #(rand-nth [:dead :alive])))))


(defn sample-board []

  (let [board (mapv identity
                    (repeat 50 (mapv identity
                                     (repeat 50 :dead))))]
    (-> board
        ;; Oscillator - blinker
        (set-cell 20 20 :alive)
        (set-cell 20 21 :alive)
        (set-cell 20 22 :alive)
        ;; Oscillator - beacon
        (set-cell 30 20 :alive)
        (set-cell 30 21 :alive)
        (set-cell 31 20 :alive)
        (set-cell 32 23 :alive)
        (set-cell 33 22 :alive)
        (set-cell 33 23 :alive)
        ;; Glider
        (set-cell 20 47 :alive)
        (set-cell 20 48 :alive)
        (set-cell 20 49 :alive)
        (set-cell 19 49 :alive)
        (set-cell 18 48 :alive))))
