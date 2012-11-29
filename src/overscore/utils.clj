;;; Collection of utilities functions
(ns overscore.utils
  (:import java.awt.image.BufferedImage))

(defn parse-int
  "Parse an integer, possibly from a floating-point representation"
  [n]
  (try
    (int (Double. n))
    (catch Exception e
      (println "Error when parsing integer from" n ":" e)
      0)))

(defn square
  "Return x squared"
  [x]
  (* x x))

;;;;;;;;;;;;;;;;;;;;;;
;;; List utilities ;;;
;;;;;;;;;;;;;;;;;;;;;;

(defn maximize
  "Find the element of a collection for which the value f returns with
  it as argument is the maximum.
  For example, (maximize #(* -1 %) [1 2 3]) is 1"
  [f coll]
  (let [helper (fn helper [[x & xs]]
                 (if (empty? xs)
                   [x (f x)]
                   (let [[max-item max-value] (helper xs)
                         value (f x)]
                     (if (> value max-value)
                       [x value]
                       [max-item max-value]))))]
    (first (helper coll))))

(defn one-each-two
  "Return an element every two elements for the argument. For
  example, (one-each-two [1 2 3 4]) returns [1 3]"
  [coll]
  (loop [l coll
         acc []]
    (if (empty? l)
      (reverse acc)
      (recur (rest (rest l)) (cons (first l) acc)))))

;;;;;;;;;;;;;;;;;;;;;;;
;;; Debug utilities ;;;
;;;;;;;;;;;;;;;;;;;;;;;

(defmacro debug
  "Print an optional message and the value of x, then returns x"
  ([x]
     `(let [res# ~x]
        (println res#)
        res#))
  ([msg x]
     `(let [res# ~x]
        (println ~msg res#)
        res#)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; BufferedImage and pixel manipulation functions ;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn apply-to-pixels
  "Change each pixels according to the function f. f takes as argument
  the rgb value of the pixel, and returns the new pixel rgb value"
  [^BufferedImage img f]
  (doseq [x (range (.getWidth img))
          y (range (.getHeight img))]
    (.setRGB img x y (f (.getRGB img x y)))))

;; TODO: see extract-rgb
(defn extract-gray
  "Return the gray value of a pixel. Assume we already have a
  grayscale image (in fact, it returns the R value, which is the same
  as the G and B ones for a grayscale image)."
  [^BufferedImage img ^long pixel]
  (bit-and 0xFF pixel))

;; TODO: It would be more clean to extract the values using the
;; ColorModel of the BufferedImage, though it might affect
;; performance. As is, this function will work with (A)RGB images, but
;; won't with other encodings (RGBA, (A)BGR, ...). However, those
;; encodings don't seem to be widely spread (I don't know if they're
;; used at all)
(defn extract-rgb
  "Extract the R, G and B values of a pixel in an image, return them
  as a vector"
  [^BufferedImage img ^long x ^long y]
  (let [pixel (.getRGB img x y)]
    [(bit-and 0xFF (bit-shift-right pixel 16))
     (bit-and 0xFF (bit-shift-right pixel 8))
     (bit-and 0xFF pixel)]))