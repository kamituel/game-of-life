(ns game-of-life.paint
  
  (:require [game-of-life.dom :as dom]))


(defn find-canvas-or-fail []

  (or (js/document.getElementById "canvas")
      (throw (js/Error. "No <canvas> found"))))


(defn resize-canvas! [canvas height width scale]

  (set! (.. canvas -width) (* scale width))
  (set! (.. canvas -height) (* scale height)))


(defn board-dimensions [canvas-element scale]
  
 (let [{:keys [width height]} (dom/bounding-client-rect canvas-element)]
   [(js/Math.floor (/ height scale))
    (js/Math.floor (/ width scale))]))


(defn paint-board! [canvas board height width scale]

  (let [ctx (.getContext canvas "2d")]
    (dotimes [pos (* height width)]
      (let [row-index (int (/ pos width))
            col-index (rem pos width)
            y         (* scale row-index)
            x         (* scale col-index)
            alive?    (= 1 (aget board pos))
            color     (if alive?
                        "white"
                        "black")]
        (set! (.. ctx -fillStyle) color)
        (.fillRect ctx x y scale scale)))))
