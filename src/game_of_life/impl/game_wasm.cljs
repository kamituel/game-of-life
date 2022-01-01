(ns game-of-life.impl.game-wasm
  
  (:require [game-of-life.game :as game]))


;; Ideally, WASM shim would've been loaded as an ES6 module.
;; This would be done by adding the following to shadow-cljs.edn:
;;
;; :js-options {:resolve {"wasm-evolution"
;;                        {:target :file
;;                         :file   "wasm/pkg/wasm.js"}}}
;;
;; Then in this namespace we'd be able to require it:
;;
;; (:require ["wasm-evolution" :as wasm-evolution])
;;
;; And finally use the shim to load the WASM bundle:
;;
;; (.then (wasm-evolution/default "/js/wasm_bg.wasm")
;;        (fn [_wasm] ... success! ...)
;;        (fn [_error] ... failure! ...))
;;
;; Unfortunately however, the wasm/pkg/wasm.js that is generated
;; when using the following command:
;;
;;    $ wasm-pack build --target web ...
;;
;; contains the "import.meta".url" bit which is apparently
;; not supported by ClojureScript / ShadowCLJS.
;;
;; For that reason WASM is imported as a old fashioned
;; JS file that creates a global `wasm_bindgen` object.


(defmethod game/init :wasm-rust
  
  []
  
  (.then
   (js/wasm_bindgen "/js/wasm_bg.wasm")
   #(js/console.log "Evolutionj algorithm in WASM has been loaded.")
   #(js/console.error "Could not load evolution algorithm in WASM.")))


(defmethod game/evolve :wasm-rust

  [_ board _height width]

  (let [result (js/window.Uint8Array. (.-length board))]
    (js/wasm_bindgen.evolve board width result)
    result))
