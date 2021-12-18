(ns game-of-life.templates
  
  (:require [clojure.string :as str]))


(def string-templates
  
  {:pulsar-template
   "
     ..OOO...OOO..
     .............
     O....O.O....O
     O....O.O....O
     O....O.O....O
     ..OOO...OOO..
     .............
     ..OOO...OOO..
     O....O.O....O
     O....O.O....O
     O....O.O....O
     .............
     ..OOO...OOO..
   "


   :blinker-template
   "
     OOO
   "


   :toad-template
   "
     .OOO
     OOO.
   "


   :beacon-template
   "
     OO...
     OO...
     ..OO
     ..OO
   "


   :glider-template
   "
     ..O
     O.O
     .OO
   "


   :r-pentomino-template
   "
     .OO
     OO.
     .O.
   "


   :gosper-glider-gun
   "
     ........................O...........
     ......................O.O...........
     ............OO......OO............OO
     ...........O...O....OO............OO
     OO........O.....O...OO..............
     OO........O...O.OO....O.O...........
     ..........O.....O.......O...........
     ...........O...O....................
     ............OO......................
   "


   :lightweight-space-ship-template
   "
     .O..O
     O....
     O...O
     OOOO.
   "


   ;; By Bill Gosper, 1971
   :puffer-train-1-template
   "
     .OOO......O.....O......OOO.
     O..O.....OOO...OOO.....O..O
     ...O....OO.O...O.OO....O...
     ...O...................O...
     ...O..O.............O..O...
     ...O..OO...........OO..O...
     ..O...OO...........OO...O..
   "


   ;; By Richard Schank, 2014
   :puffer-train-2-template
   "
     ...O.......O...
     ..OOO.....OOO..
     .OO..O...O..OO.
     ...OOO...OOO...
     ...............
     ....O.....O....
     ..O..O...O..O..
     O.....O.O.....O
     OO....O.O....OO
     ......O.O......
     ...O.O...O.O...
     ....O.....O....
   "


   ;; By Bill Gosper, 1970
   :p60-gun-template
   "
	   ............................O..........
	   ............................O.O........
	   ...........OO..................OO......
	   .........O...O.................OO....OO
	   ...OO...O.....O................OO....OO
	   ...OO..OO.O...O.............O.O........
	   ........O.....O.............O..........
	   .........O...O.........................
	   ...........OO..........................
	   .......................................
	   .......................................
	   .......................................
	   .......................................
	   .......................................
	   .......................................
	   .......................................
	   ..........O.O..........................
	   .........O..O...OO.....................
	   OO......OO.....OOO.OO..OO..............
	   OO....OO...O...O...O...O.O.............
	   ........OO.....O.O........O............
	   .........O..O..OO......O..O............
	   ..........O.O.............O............
	   .......................O.O.......OO....
	   .......................OO........O.O...
	   ...................................O...
	   ...................................OO..
   "
   
   ;; By Nicolay Beluchenko 2004.
   :shuttle
   "
     O.............
	   OOO...........
	   ...O..........
	   ..OO..........
	   ..............
	   ......O.......
	   .....OOOO.....
	   ......O..O....
	   .......OOO....
	   ..............
	   ..........OO..
	   ..........O...
	   ...........OOO
	   .............O
    "})


(defn parse-template [string-template]

  (some-> string-template
          (str/trim)
          (str/split #"\n")
          (->> (map str/trim))))


(defn get-template [id]
  
  (parse-template
   (get string-templates id)))


(defn rotate-template [template]
  
  (loop [n                0
         rotated-template []]

    (if (= n (count (first template)))

      (map str/join rotated-template)

      (recur (inc n)
             (conj rotated-template
                   (map #(nth % n) template))))))


(defn template-width [template]

  (count (first template)))


(defn template-height [template]

  (count template))
