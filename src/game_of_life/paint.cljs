(ns game-of-life.paint)


(defn find-canvas-or-fail []

  (or (js/document.getElementById "canvas")
      (throw (js/Error. "No <canvas> found"))))


(defn resize-canvas! [config canvas]

  (let [{:keys [width height scale]} config]
    (set! (.. canvas -width) (* scale width))
    (set! (.. canvas -height) (* scale height))))


(defn paint-board! [config canvas board]

  (let [ctx    (.getContext canvas "2d")
        height (:height config)
        width  (:width config)
        scale  (:scale config)]
    (dotimes [pos (* height width)]
      (let [row-index (int (/ pos width))
            col-index (rem pos width)
            y         (* scale row-index)
            x         (* scale col-index)
            alive?    (= 1 (aget board pos))
            color     (if alive?
                        "black"
                        "white")]
        (set! (.. ctx -fillStyle) color)
        (.fillRect ctx x y scale scale)))))
