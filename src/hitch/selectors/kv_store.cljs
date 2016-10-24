(ns hitch.selectors.kv-store
  (:refer-clojure :exclude [key])
  (:require [hitch.oldprotocols :as oldproto]
            [hitch.protocol :as proto]
            [hitch.graph :as graph]
            [hitch.graph :as graph]
            [hitch.selector :refer-macros [defselector]])
  )

(declare  keyspace)
;(defrecord KeySelector [ks k]
;  proto/Selector
;  (value [this graph state]
;    (let [keyspace-selector (proto/-selector keyspace ks)]
;      (if-let [kstore (some-> (proto/peek-node graph keyspace-selector)
;                              async/poll!)]
;
;        (->Realized (get kstore k) #{keyspace-selector})
;        (->NotRealized #{keyspace-selector})))
;    ))
;
;(def key
;  (reify
;    IFn
;    (-invoke [this graph ks k]
;      (assert nil "alias is stateful and should not be evaled"))
;    proto/ISelectorFactory
;    (-selector [this ks k]
;      (->KeySelector ks k))))

(defselector key [graph sel k]
  (get (graph/dget-sel! graph sel {}) k)
  )

(defrecord KVStoreServiceSelector [keyspace]
  proto/StatefulSelector
  (create [selector]
    {:val  oldproto/NOT-FOUND-SENTINEL
     :deps #{}})
  (destroy [selector state])
  proto/InformedSelector
  proto/CommandableSelector
  (command-accumulator
    [s state] state)
  (command-step [s acc event]
    ;(prn "effect " event)
    (let [[key] event]
      (case key
        :clear (assoc acc :val oldproto/NOT-FOUND-SENTINEL)
        :add-dep (update acc :deps conj (second event))
        :remove-dep (update acc :deps disj (second event))
        :set-value (let [new-value (second event)]
                     (assoc acc :val new-value)
                     ))))
  (command-result [s acc]
    ;(prn "acc" acc)
    (proto/->State acc))
  proto/Selector
  (value [this graph state]
    ;(prn "state" state)
    (if (identical? (:val state) oldproto/NOT-FOUND-SENTINEL)
      (proto/->SelectorUnresolved nil)
      (proto/->SelectorValue (:val state) nil))))

(def keyspace
  (reify
    IFn
    (-invoke [this ks]
      (->KVStoreServiceSelector ks))))

