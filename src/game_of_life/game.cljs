(ns game-of-life.game)


(defmulti init

  (fn [implementation-id]
    implementation-id))


(defmulti evolve

  (fn [implementation-id _board _height _width]
    implementation-id))
