(ns game-of-life.templates
  
  (:require [clojure.string :as str]))


(def string-templates

  [{:id              :pulsar
    :name            "Pulsar (Conway)"
    :category        "Oscillators"
    :template-string "
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
                     "}

   {:id              :blinker
    :name            "Blinker (Conway, 1970)"
    :category        "Oscillators"
    :template-string "
                       OOO
                     "}

   {:id              :toad
    :name            "Toad (Norton, 1970)"
    :category        "Oscillators"
    :template-string "
                       .OOO
                       OOO.
                     "}

   {:id              :beacon
    :name            "Beacon (Conway, 1970)"
    :category        "Oscillators"
    :template-string "
                       OO...
                       OO...
                       ..OO
                       ..OO
                     "}

   {:id              :glider
    :name            "Glider"
    :category        "Spaceships"
    :template-string "
                       ..O
                       O.O
                       .OO
                     "}

   {:id              :r-pentomino
    :name            "R-pentomino"
    :category        "Other"
    :template-string "
                       .OO
                       OO.
                       .O.
                     "}

   {:id              :gosper-glider-gun
    :name            "Glider Gun (Gosper, 1970)"
    :category        "Guns"
    :template-string "
                       ........................O...........
                       ......................O.O...........
                       ............OO......OO............OO
                       ...........O...O....OO............OO
                       OO........O.....O...OO..............
                       OO........O...O.OO....O.O...........
                       ..........O.....O.......O...........
                       ...........O...O....................
                       ............OO......................
                     "}

   {:id              :lightweight-space-ship
    :name            "Lightweight Space Ship (Conway, 1970)"
    :category        "Spaceships"
    :template-string "
                       .O..O
                       O....
                       O...O
                       OOOO.
                     "}

   {:id              :puffer-train-1
    :name            "Puffer Train (Gosper, 1971)"
    :category        "Puffers"
    :template-string "
                       .OOO......O.....O......OOO.
                       O..O.....OOO...OOO.....O..O
                       ...O....OO.O...O.OO....O...
                       ...O...................O...
                       ...O..O.............O..O...
                       ...O..OO...........OO..O...
                       ..O...OO...........OO...O..
                     "}

   {:id              :puffer-train-2
    :name            "Puffer Train (Schank, 2014)"
    :category        "Puffers"
    :template-string "
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
                     "}

   {:id              :p60-gun
    :name            "p60 gun (Gosper, 1970)"
    :category        "Guns"
    :template-string "
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
                     "}

   {:id              :shuttle
    :name            "Shuttle (Beluchenko, 2004)"
    :category        "Oscillators"
    :template-string "
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
                     "}

   {:id              :washerwoman
    :name            "Washerwoman (Abbe, 1971)"
    :category        "Other"
    :template-string "
	                     O.......................................................
	                     OO....O.....O.....O.....O.....O.....O.....O.....O.....O.
	                     OOO..O.O...O.O...O.O...O.O...O.O...O.O...O.O...O.O...O.O
	                     OO....O.....O.....O.....O.....O.....O.....O.....O.....O.
	                     O.......................................................
                     "}

   {:id              :weekender
    :name            "Weekender (Eppstein, 2000)"
    :category        "Other"
    :template-string "
	                     .O............O.
	                     .O............O.
	                     O.O..........O.O
	                     .O............O.
	                     .O............O.
	                     ..O...OOOO...O..
	                     ......OOOO......
	                     ..OOOO....OOOO..
	                     ................
	                     ....O......O....
	                     .....OO..OO.....
                     "}

   {:id              :wickstretcher
    :name            "Wickstretcher"
    :category        "Other"
    :template-string "
	                     .................OO..............................
	                     .............OO....O.............................
	                     ............OOO.O................................
	                     O.OO..OO...O...OOOO.O.O....OO.......OO...........
	                     O....OO..O........O.OOO....O....OO.O..O.OO.O.....
	                     O.OO....OO.OO....O...........O...O.O.OO.O.OO.....
	                     ......O.......O.............OO.....O..O.O...OO...
	                     .....O.........O.O....OOO...O....O..O.O.OOO...O..
	                     .....O.........O.O....OOO.OO.O..OO.O.O...O..OO.O.
	                     ......O.......O.............OO.O...OO....OO....O.
	                     O.OO....OO.OO....O..........O........OO.O.O.OO.OO
	                     O....OO..O........O.OOO........O...O...OO.O..O.O.
	                     O.OO..OO...O...OOOO.O.O.......O.O...OO....O..O.O.
	                     ............OOO.O..............O.....O.OOO....O..
	                     .............OO....O.................O.O.........
	                     .................OO...................O..........
                     "}])


(defn parse-template [string-template]

  (some-> string-template
          (str/trim)
          (str/split #"\n")
          (->> (map str/trim))))


(defn get-template-string [id]

  (->> string-templates
       (filter #(= id (:id %)))
       first
       :template-string
       parse-template))


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
