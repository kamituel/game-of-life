(ns game-of-life.main

  (:require [game-of-life.boards :as boards]
            [game-of-life.dom :as dom]
            [game-of-life.game :as game]
            [game-of-life.paint :as paint]
            [game-of-life.utils :as utils]))


(defn step [config canvas !board]

  (let [evolution-time (utils/measure-time #(swap! !board game/evolve config))]
    (dom/set-text-content! (dom/by-id "evolution-time") evolution-time))

  (let [paint-time (utils/measure-time #(paint/paint-board! config canvas @!board))]
    (dom/set-text-content! (dom/by-id "paint-time]") paint-time))

  #_(print-board! @!board))



(defn -main []
  (let [config  {:width       100
                 :height      50
                 :scale       10
                 :interval-ms 0}

        canvas  (paint/find-canvas-or-fail)

        !board  (atom #_(oscillator)
                      (boards/random-board
                       (:width config)
                       (:height config)))]

    (paint/resize-canvas! config canvas)
    (paint/paint-board! config canvas @!board)

    (.addEventListener
     (js/document.getElementById "evolve-button")
     "click"
     (partial step config canvas !board))

    (js/window.setInterval
     (partial step config canvas !board)
     (:interval-ms config))))


(-main)
