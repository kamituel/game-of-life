(ns game-of-life
  
  (:require [clojure.string :as str]))


(defn add-index [xs]

  (map-indexed
   (fn [idx x] [idx x])
   xs))


(defn measure-time [f]
  
  (let [start-time (js/performance.now)]
    (f)
    (- (js/performance.now) start-time)))


(defn find-canvas-or-fail []

  (or (js/document.getElementById "canvas")
      (throw (js/Error. "No <canvas> found"))))


(defn resize-canvas! [config canvas]

  (let [{:keys [width height scale]} config]
    (set! (.. canvas -width) (* scale width))
    (set! (.. canvas -height) (* scale height))))


(defn paint-board! [config canvas board]

  (let [ctx (.getContext canvas "2d")]
    (doseq [[row-index row] (add-index board)]
      (doseq [[col-index status] (add-index row)]
        (let [color (if (= :alive status)
                      "black"
                      "white")
              scale (:scale config)
              x     (* col-index scale)
              y     (* row-index scale)]
          (set! (.. ctx -fillStyle) color)
          (.fillRect ctx x y scale scale))))))


(defn random-board [width height]
  
  (repeatedly height
              (fn []
                (repeatedly width
                            #(rand-nth [:dead :alive])))))


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


(defn set-cell [board row-index col-index status]
  (assoc-in board [row-index col-index] status))


(defn evolve [board config]
  
  (let [{:keys [width height]} config]

    (doall
     (for [[row-index row] (add-index board)]
       (doall
        (for [[col-index status] (add-index row)]

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
        (set-cell 18 48 :alive)
        )))


(defn print-board! [board]
  (doseq [[row-index row] (add-index board)]
    (js/console.log
     (if (< row-index 10)
       (str " " row-index)
       (str row-index))
     (str/join "" (map #(if (= :alive %) 1 0) row)))))


(defn step [config canvas !board]

  (let [evolution-time (measure-time #(swap! !board evolve config))]
    (set! (.. (js/document.getElementById "evolution-time") -textContent) evolution-time))
  
  (let [paint-time (measure-time #(paint-board! config canvas @!board))]
    (set! (.. (js/document.getElementById "paint-time") -textContent) paint-time))
  
  #_(print-board! @!board))


(defn -main []
  (let [config  {:width       100
                 :height      50
                 :scale       10
                 :interval-ms 0}
        canvas  (find-canvas-or-fail)
        !board  (atom #_(oscillator)
                      (random-board
                         (:width config)
                         (:height config)))]

    (resize-canvas! config canvas)
    (paint-board! config canvas @!board)

    (.addEventListener
     (js/document.getElementById "evolve-button")
     "click"
     (partial step config canvas !board ))

    (js/window.setInterval
     (partial step config canvas !board)
     (:interval-ms config))))



(-main)
