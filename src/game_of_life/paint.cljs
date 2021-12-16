(ns game-of-life.paint
  
  (:require [game-of-life.utils :as utils]))


(defn find-canvas-or-fail []

  (or (js/document.getElementById "canvas")
      (throw (js/Error. "No <canvas> found"))))


(defn resize-canvas! [config canvas]

  (let [{:keys [width height scale]} config]
    (set! (.. canvas -width) (* scale width))
    (set! (.. canvas -height) (* scale height))))


(defn paint-board! [config canvas board]

  (let [ctx (.getContext canvas "2d")]
    (doseq [[row-index row] (utils/add-index board)]
      (doseq [[col-index status] (utils/add-index row)]
        (let [color (if (= :alive status)
                      "black"
                      "white")
              scale (:scale config)
              x     (* col-index scale)
              y     (* row-index scale)]
          (set! (.. ctx -fillStyle) color)
          (.fillRect ctx x y scale scale))))))
