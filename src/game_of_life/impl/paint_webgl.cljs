(ns game-of-life.impl.paint-webgl)


(def shader2-vs
  
  "
    attribute vec2 a_position;

    uniform vec2 u_resolution;

    void main() {
        // convert the rectangle from pixels to 0.0 to 1.0
        vec2 zeroToOne = a_position / u_resolution;

        // convert from 0 -> 1 to 0 -> 2
        vec2 zeroToTwo = zeroToOne * 2.0;

        // convert from 0 -> 2 to -1 -> +1 (clipspace)
        vec2 clipSpace = zeroToTwo - 1.0;

        // Flip 0,0 from bottom left to conventional 2D top left.
        gl_Position = vec4(clipSpace * vec2(1, -1), 0, 1);
   
        gl_PointSize = 1.0;
     }
  ")


(def shader-vs

  "
    attribute vec4 a_Position;
    void main() {
        gl_Position = a_Position;
    } 
  ")


(def shader-fs

  "
    precision mediump float;
    uniform vec4 u_FragColor;
    void main() {
        gl_FragColor = u_FragColor;
    }
  ")


(defn- make-shader

  [ctx source-string type]

  (let [shader (.createShader ctx type)]

    (.shaderSource ctx shader source-string)
    (.compileShader ctx shader)

    (when-not (.getShaderParameter ctx shader (.-COMPILE_STATUS ctx))
      (throw (js/Error. "Could not compile shader")))

    shader))


(defn- attach-shaders!
  
  [ctx program]
  
  (let [vertex-shader   (make-shader ctx shader2-vs (.-VERTEX_SHADER ctx))
        fragment-shader (make-shader ctx shader-fs (.-FRAGMENT_SHADER ctx))]

    (doto ctx
      (.attachShader program vertex-shader)
      (.attachShader program fragment-shader))))


(defn- link-program-or-throw!
  
  [ctx program]
  
  (.linkProgram ctx program)
  
  (when-not (.getProgramParameter ctx program (.-LINK_STATUS ctx))
    (throw (js/Error. "Unable to link a program to the WebGL context"))))


(defn- init-program!

  [ctx]

  (let [program (.createProgram ctx)]

    (doto ctx
      (attach-shaders! program)
      (link-program-or-throw! program)
      (.useProgram program))

    program))


(defn init!

  [ctx]

  (let [program (init-program! ctx)
        buffer  (.createBuffer ctx)]

    (.bindBuffer ctx (.-ARRAY_BUFFER ctx) buffer)

    (let [color-location      (.getUniformLocation ctx program "u_color")
          resolution-location (.getUniformLocation ctx program "u_resolution")
          position-location   (.getAttribLocation ctx program "a_position")]

      (.uniform2f ctx resolution-location
                  (.. ctx -canvas -width)
                  (.. ctx -canvas -height))

      (.uniform4f ctx color-location 0 0 0 1)

      (.enableVertexAttribArray ctx position-location)
      (.vertexAttribPointer ctx position-location 2 (.-FLOAT ctx) false 0 0))

    (let[temp-array (js/Float32Array. 1000 #_(* (.. ctx -canvas -width)
                                     (.. ctx -canvas -height)))]
        (.bufferData ctx
                     (.-ARRAY_BUFFER ctx)
                     temp-array
                     (.-STATIC_DRAW ctx))

    {:ctx ctx
     ;; FIXME: shold take into account :scale.
     :temp-array temp-array})))


#_(defn- draw-rectangle!

  [ctx program x y width height]
  
  (let [vertices (js/Float32Array. [-0.5, 0.5,
                                    0.5, 0.5,
                                    0.5, -0.5
                                    ;-0.5, 0.5, 0.5, -0.5, -0.5, -0.5
                                    ])
        alive-color #js [1 0 0 1]
        buffer (.createBuffer ctx)
        dim 2]

    (.bindBuffer ctx (.-ARRAY_BUFFER ctx) buffer)
    (.bufferData ctx (.-ARRAY_BUFFER ctx) vertices (.-STATIC_DRAW ctx))

    (let [pos-attribute (.getAttribLocation ctx program "a_Position")]
      (.vertexAttribPointer ctx pos-attribute dim (.-FLOAT ctx) false 0 0)
      (.enableVertexAttribArray ctx pos-attribute))

    (let [frag-color (.getUniformLocation ctx program "u_FragColor")]
      (.uniform4fv ctx frag-color alive-color))

    (.drawArrays ctx (.-TRIANGLES ctx) 0 (int (/ (.-length vertices)
                                                 dim)))))


(defn- clear-canvas!

  [ctx]

  (.clearColor ctx 0 1 0 1)
  (.clear ctx (.-COLOR_BUFFER_BIT ctx)))


(defn paint-board!

  [paint-map]

  (let [{:keys [board
                scale
                height
                width
                webgl]} paint-map

        {:keys [ctx
                temp-array]} webgl]

    (clear-canvas! ctx)

    #_(draw-rectangle! ctx webgl-program 0 0 0 0)

    (let [last-position
          (loop [position      0
                 temp-position 0]

            (if (= position (.-length board))

              temp-position

              (let [alive? (= 1 (aget board position))]

                (if alive?

                  (let [row-index (int (/ position width))
                        col-index (rem position width)
                        y         (* scale row-index)
                        x         (* scale col-index)]
                    (aset temp-array temp-position x)
                    (aset temp-array (inc temp-position) y)
                    (recur (inc position)
                           (+ temp-position 2)))

                  (recur (inc position) temp-position)))))]

      

      (.drawArrays ctx (.-POINTS ctx) 0 (/ last-position 1)))))
