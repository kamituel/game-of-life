(ns game-of-life.main

  (:require [game-of-life.boards :as boards]
            [game-of-life.dom :as dom]
            [game-of-life.game :as game]
            [game-of-life.impl.game-cljs]
            [game-of-life.impl.game-wasm]
            [game-of-life.paint :as paint]
            [game-of-life.templates :as templates]
            [game-of-life.utils :as utils]
            [game-of-life.view :as view]))


(def initial-state
  
  {:paint     {:board  nil
               :scale  5
               :height nil
               :width  nil
               :webgl  nil
               :engine :canvas-2d-image-data}

   :gameplay  {:auto-step?                  false
               :interval-ms                 0
               :evolution-implementation-id :wasm-rust}

   :view      {:board-type      :predefined
               :template-id     :puffer-train-2
               :template-string ".O.\nO.O\n.O."}

   :stats     {:generation     0
               :evolution-time 0
               :paint-time     0
               :full-step-time 0}

   :templates templates/string-templates})


(defn paint! [canvas state]

  (paint/paint-board! canvas (:paint state)))


(defn advance-game! [canvas !state render-fn]
  
  (let [state @!state
        
        {:keys [auto-step? interval-ms]}
        (:gameplay state)

        {:keys [board height width]}
        (:paint state)

        [evolution-time new-board]
        (utils/measure-time #(game/evolve (-> state :gameplay :evolution-implementation-id)
                                          board
                                          height
                                          width))

        [paint-time _]
        (utils/measure-time
         #(paint! canvas (assoc-in state [:paint :board] new-board)))]

    (reset! !state
            (-> state
                (assoc-in [:paint :board] new-board)
                (assoc-in [:stats :evolution-time] evolution-time)
                (assoc-in [:stats :paint-time] paint-time)
                (update-in [:stats :generation] inc)))

    (render-fn)

    (when auto-step?
      (js/window.setTimeout
       #(advance-game! canvas !state render-fn)
       (max (- interval-ms evolution-time paint-time)
            0)))))


(defn- update-board! [!state]
  
  (let [{:keys [board-type
                template-id
                template-string]}
        (:view @!state)

        {:keys [height width]}
        (:paint @!state)

        new-board
        (case board-type
          :random
          (boards/generate-random-board height width)

          :predefined
          (boards/generate-board-from-template
           (templates/get-template-string template-id)
           height
           width)

          :custom
          (boards/generate-board-from-template
           (templates/parse-template template-string)
           height
           width))]

    (swap! !state assoc-in [:paint :board] new-board)))


(defmulti handle-event (fn [event-id & _] event-id))


(defmethod handle-event :default
  [event-id & _]
  (throw (js/Error. (str "Unknown event: '" event-id "'."))))


(defmethod handle-event :board-type-changed

  [_ !state _ _ board-type]

  (swap! !state assoc-in [:view :board-type] board-type)
  (update-board! !state))


(defmethod handle-event :predefined-template-selected

  [_ !state _ _ template-id]

  (swap! !state assoc-in [:view :template-id] template-id)
  (update-board! !state))


(defmethod handle-event :custom-template-updated

  [_ !state _ _ template-string]

  (swap! !state assoc-in [:view :template-string] template-string)
  (update-board! !state))


(defmethod handle-event :start-auto-play
  
  [_ !state refs render-fn]

  (swap! !state assoc-in [:gameplay :auto-step?] true)
  (advance-game! (:canvas refs) !state render-fn))


(defmethod handle-event :stop-auto-play

  [_ !state _ _]
  
  (swap! !state assoc-in [:gameplay :auto-step?] false))


(defmethod handle-event :advance-one-generation
  
  [_ !state refs render-fn]
  
  (advance-game! (:canvas refs) !state render-fn))


(defn- measure-and-resize-canvas! [canvas !state]

  (let [[height width] (paint/board-dimensions
                        canvas
                        (-> @!state :paint :scale))]
    
    (prn "Canvas measured at" height "x" width)

    (paint/resize-canvas!
     canvas height width (-> @!state :paint :scale))

    (swap! !state update :paint assoc
           :height height
           :width width)))


(defn -main []
  
  (let [container (dom/by-id "app")

        !state    (atom initial-state)

        !refs     (atom {})

        emit-fn   (fn emit-fn [event-id & args]
                    (let [render-fn #(view/render container @!state emit-fn !refs)]
                      (apply handle-event event-id !state @!refs render-fn args)
                      (paint! (:canvas @!refs) @!state)
                      (render-fn)))]

    (view/render
     container @!state emit-fn !refs)
    
    (measure-and-resize-canvas!
     (:canvas @!refs) !state)
    
    (game/init (-> @!state :gameplay :evolution-implementation-id))
    
    #_(swap! !state assoc-in [:paint :webgl]
           (paint/init-webgl! (:canvas @!refs)))
    
    (update-board! !state)
    (paint! (:canvas @!refs) @!state))) 


(-main)
