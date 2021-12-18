(ns game-of-life.main

  (:require [game-of-life.boards :as boards]
            [game-of-life.game :as game]
            [game-of-life.paint :as paint]
            [game-of-life.templates :as templates]
            [game-of-life.utils :as utils]))


(defn step [!game]
  
  (let [{:keys [board canvas height width scale
                auto-step? interval-ms callback] :as game}
        @!game

        [evolution-time new-board]
        (utils/measure-time #(game/evolve board height width))

        [paint-time _]
        (utils/measure-time #(paint/paint-board! canvas new-board height width scale))]

    (swap! !game assoc
           :board new-board
           :generation (inc (:generation game))
           :evolution-time evolution-time
           :paint-time paint-time)
    
    (when callback
      (callback
       (clj->js
        {:evolution_time evolution-time
         :paint_time paint-time
         :generation (inc (:generation game))})))
    
    (when auto-step?
      (js/window.setTimeout
       #(step !game)
       (max (- interval-ms evolution-time paint-time)
            0)))))


(defn ^:export create-game [canvas-element callback]
  
  (let [scale          5
        [height width] (paint/board-dimensions canvas-element scale)]

    (paint/resize-canvas!
     canvas-element height width scale)

    (atom {:scale       scale
           :width       width
           :height      height
           :canvas      canvas-element
           :generation  0
           :interval-ms 0
           :callback    callback})))


(defn- set-board [!game board]
  
  (let [{:keys [canvas
                height
                width
                scale]} @!game]

    (swap! !game assoc :board board)
    (paint/paint-board! canvas board height width scale)))


(defn ^:export set_random_board [!game]

  (set-board !game
             (boards/generate-random-board
              (:height @!game)
              (:width @!game))))


(defn ^:export set_board_from_template_id [!game template-id]

  (set-board !game
             (boards/generate-board-from-template
              (templates/get-template (keyword template-id))
              (:height @!game)
              (:width @!game))))


(defn ^:export set_board_from_template_string [!game template-string]
  
  (set-board !game
             (boards/generate-board-from-template
              (templates/parse-template template-string)
              (:height @!game)
              (:width @!game))))


(defn ^:export start-gameplay [!game]

  (swap! !game assoc :auto-step? true)
  (step !game))


(defn ^:export stop-gameplay [!game]
  
  (swap! !game assoc :auto-step? false))


(defn ^:export step-gameplay [!game]
  
  (step !game))
