(ns game-of-life.view
  
  (:require [shadow.cljs.modern :refer [js-template]]
            ["uhtml" :as uhtml :refer [html]]))


(defn predefine-template-select [on-change all-templates selected-template-id disabled?]
  
  (let [onchange  (fn [event]
                    (let [template-id (keyword (.. event -target -value))]
                      (on-change template-id)))
        
        options   (partial map (fn [template]
                                 (js-template html
                                   "<option value=" (name (:id template)) "
                                            ?selected=" (= selected-template-id (:id template)) ">
                             " (:name template) "
                           </option>")))

        optgroups (->> all-templates
                       (sort-by :name)
                       (sort-by :category)
                       (group-by :category)
                       (map (fn [[category-name category-templates]]
                              (js-template html
                                "<optgroup label=" category-name ">
                                  " (clj->js (options category-templates)) "
                                 </optgroup>"))))]
    
  (js-template html
    "<select onchange= " onchange " ?disabled=" disabled? ">
      " (clj->js optgroups) "
    </select>")))


(defn section-with-header [header contents]

  (js-template html
    "<section class='small-gap'>
      <h2>" header "</h2>
      " contents "
    </section>"))


(defn board-section [emit state]
  
  (let [{:keys [view templates gameplay]} state
        {:keys [board-type template-id template-string]} view
        {:keys [auto-step?]} gameplay
        disabled? (true? auto-step?)]
  
     (js-template html
       "<form class='small-gap'>
         <select ?disabled=" disabled? " onchange=" #(emit :board-type-changed (keyword (.. % -target -value))) ">
           <option ?selected=" (= :random board-type) " value='random'>Random (50% alive)</option>
           <option ?selected=" (= :predefined board-type) " value='predefined'>Predefined template</option>
           <option ?selected=" (= :custom board-type) " value='custom'>Custom template</option>
         </select>
        "
          (when (= :predefined board-type)
            (predefine-template-select
             #(emit :predefined-template-selected %)
             templates
             template-id
             disabled?))

          (when (= :custom board-type)
            (js-template html
              "<textarea onchange=" #(emit :custom-template-updated (.. % -target -value)) "
                         .value=" template-string "
                         ?disabled=" disabled? ">
               </textarea>"))
       "</form>")))


(defn gameplay-section [emit gameplay]
  
  (let [{:keys [auto-step?]} gameplay]

    (js-template html
      "<div class=horizontal-equal>
         <button onclick=" #(emit :start-auto-play) " ?disabled=" (true? auto-step?) ">
           Start
         </button>
         <button onclick=" #(emit :stop-auto-play) " ?disabled=" (false? auto-step?) ">
           Stop
         </button>
         <button onclick=" #(emit :advance-one-generation) " ?disabled=" (true? auto-step?) ">
           Step
         </button>
       </div>")))


(defn stats-section [stats]
  
  (js-template html
    "<div class=stats>
       <p>
         <span>Generation</span>
         <span>" (:generation stats) "</span>
       </p>
       <p>
         <span>Evolution time</span>
         <span>" (.toFixed (:evolution-time stats) 0) " ms</span>
       </p>
       <p>
         <span>Paint time</span>
         <span>" (.toFixed (:paint-time stats) 0) " ms</span>
       </p>
     </div>"))


(defn aside [state emit]

  (js-template html
    "
      <aside>
        " (section-with-header "Board" (board-section emit state)) "
        " (section-with-header "Gameplay" (gameplay-section emit (:gameplay state))) "                                                                                                                      
        " (section-with-header "Stats" (stats-section (:stats state))) "
      </aside>
    "))


(defn app [state emit !refs]
  
  (js-template html
    "<div class='app'>
     
       <canvas ref=" #(swap! !refs assoc :canvas %) "></canvas>

       <h1>
         Game of Life
       </h1>
     
       " (aside state emit) "

     </div>"))


(defn render [container-element state emit !refs]
  (uhtml/render container-element (app state emit !refs)))
