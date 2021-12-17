(ns game-of-life.dom)


(defn by-id [element-id]
  
  (js/document.getElementById element-id))


(defn set-text-content! [dom-element text-content]

  (set! (.. dom-element -textContent) text-content))


(defn bounding-client-rect [element]
  
  (let [rect (.getBoundingClientRect element)]
    {:width (.-width rect)
     :height (.-height rect)}))
