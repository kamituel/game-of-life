(ns game-of-life.paint

  (:require [game-of-life.dom :as dom]
            [game-of-life.impl.paint-webgl :as paint-webgl]))


(defn resize-canvas! [canvas height width scale]

  (set! (.. canvas -width) (* scale width))
  (set! (.. canvas -height) (* scale height)))


(defn board-dimensions [canvas-element scale]

  (let [{:keys [width height]} (dom/bounding-client-rect canvas-element)]
    [(js/Math.floor (/ height scale))
     (js/Math.floor (/ width scale))]))


(defmulti paint-board!
  (fn [_canvas paint-map]
    (:engine paint-map)))


(defmethod paint-board! :canvas-2d

  [canvas {:keys [board height width scale]}]

  (let [ctx (.getContext canvas "2d")]
    (set! (.. ctx -fillStyle) "black")
    (.fillRect ctx 0 0 (* width scale) (* height scale))
    (set! (.. ctx -fillStyle) "white")

    (dotimes [pos (* height width)]
      (let [alive? (= 1 (aget board pos))]
        (when alive?
          (let [row-index (int (/ pos width))
                col-index (rem pos width)
                y         (* scale row-index)
                x         (* scale col-index)]
            (.fillRect ctx x y scale scale)))))))


;; This is much faster than :canvas-2d.
(defmethod paint-board! :canvas-2d-image-data

  [canvas {:keys [board height width scale]}]

  (let [ctx (.getContext canvas "2d")]

    (let [xs-width  (* width scale 4)
          xs-height (* height scale)
          xs        (js/Uint8ClampedArray.
                     (* xs-width xs-height))]
      (dotimes [pos (* height width)]
        (let [luminocity (* 255 (aget board pos))
              row-index  (int (/ pos width))
              col-index  (rem pos width)]
          (dotimes [scale-row-index scale]
            (dotimes [scale-col-index scale]
              (let [xs-position (+ (* (+ (* row-index scale) scale-row-index) xs-width)
                                   (* 4 (+ (* col-index scale) (* scale-col-index))))]
                (doto xs
                  (aset (+ xs-position 0) luminocity)
                  (aset (+ xs-position 1) luminocity)
                  (aset (+ xs-position 2) luminocity)
                  (aset (+ xs-position 3) 255)))))))

      xs

      (.putImageData ctx (js/ImageData. xs (* width scale)) 0 0))))


(defn init-webgl!

  [canvas]

  (when-let [ctx (.getContext canvas "webgl"
                              #js {:antialias false})]
    (paint-webgl/init! ctx)))


(defmethod paint-board! :canvas-webgl

  [_canvas paint-map]

  (paint-webgl/paint-board! paint-map))
