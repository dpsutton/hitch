(ns hitch.selector
    (:refer-clojure :only [])
    (:require [cljs.core :refer :all]
      [clojure.walk :refer [postwalk]]
      [clojure.string :as str]))

(clojure.core/defn eval-selector [eval-fn-name constructor-binding-forms body]
  `(defn ~eval-fn-name ~(clojure.core/into ['graph] (clojure.core/map clojure.core/second) constructor-binding-forms)
     (hitch.eager-go/eager-go
       ~@body)))

(clojure.core/defn selector-record [selector-name eval-fn-name constructor-binding-forms body]
  `(defrecord ~selector-name ~(clojure.core/into [] (clojure.core/map clojure.core/first) constructor-binding-forms)
     hitch.protocols/ICreateNode
     (~'-create-node [~'this ~'graph]
       (hitch.nodes.simple/node ~'this))
     cljs.core/IFn
     (~'-invoke [~'this ~'graph]
       (assert ~'graph)
       ~(clojure.core/->> constructor-binding-forms
                          (clojure.core/map clojure.core/first)
                          (clojure.core/cons (clojure.core/symbol "graph"))
                          (clojure.core/cons eval-fn-name)))))

(clojure.core/defn sel-constructor [name eval-fn-name selector-name constructor-binding-forms body]
  `(def ~name
     (cljs.core/reify
       hitch.protocols/ISelectorFactory
       (~'-eval ~(clojure.core/into ['this 'graph] (clojure.core/map clojure.core/first) constructor-binding-forms)
         (assert ~'graph)
         ~(clojure.core/->> constructor-binding-forms
                            (clojure.core/map clojure.core/first)
                            (clojure.core/cons (clojure.core/symbol "graph"))
                            (clojure.core/cons eval-fn-name))
         )
       (~'-selector ~(clojure.core/into ['this 'graph] (clojure.core/map clojure.core/first) constructor-binding-forms)
         (assert ~'graph)
         ~(clojure.core/->> constructor-binding-forms
                            (clojure.core/map clojure.core/first)
                            (clojure.core/cons (clojure.core/symbol (clojure.core/str "->" selector-name)))))
       )))
(clojure.core/defn create-binding-syms [binding-form]
  (clojure.core/mapv (clojure.core/juxt clojure.core/gensym clojure.core/identity) binding-form))

(clojure.core/defmacro def-selector [name constructor-binding-forms & body]
  (clojure.core/assert (clojure.core/every? clojure.core/symbol? constructor-binding-forms))
  ;(prn constructor-binding-forms)
  (let [symbol-binding-pairs (create-binding-syms constructor-binding-forms)
        eval-fn-name (clojure.core/gensym (clojure.core/str name "-eval-fn"))
        selector-name (clojure.core/gensym (clojure.core/str name "-selector"))]
    `(do
       ~(eval-selector eval-fn-name symbol-binding-pairs body)
       ~(selector-record selector-name eval-fn-name symbol-binding-pairs body)
       ~(sel-constructor name eval-fn-name selector-name symbol-binding-pairs body)
       )))

