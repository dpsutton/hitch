(ns hitch.selectors.kv-store-test
  (:require-macros [hitch.eager :refer [go]]
                   [cljs.core.async.macros]
                   [hitch.selector :refer [defselector]]
                   [devcards.core :as dc :refer [deftest]])
  (:require [cljs.test :refer [] :refer-macros [is run-tests async]]
            [hitch.core :as core]
            [hitch.protocols :as proto]
            [hitch.selectors.kv-store :as kv :refer [keyspace key]]
            [hitch.graph :as graph]
            [cljs.core.async :as async]
            [hitch.graphs.mutable :as mgraph]))


(deftest firstt
  (let [graph (mgraph/graph)]
    (async done
      (let [ks-sel (proto/-selector keyspace :main)
            node1 (graph/get-or-create-node! graph (proto/-selector key ks-sel :test))
            ks-node (graph/hook graph keyspace :main)
            ]
        (is (= (async/poll! node1) nil))
        (graph/apply-effects graph [[ks-sel [:set-value {:test :cat}]]])
        (go
          (is (= (async/<! node1) :cat))
          (done))
        ))))

(deftest firstasync
  (let [graph (mgraph/graph)]
    (async done
      (let [testsel (proto/-selector keyspace :main)        ;(proto/-selector key :main :test)
            node1 (graph/get-or-create-node! graph (proto/-selector key testsel :test))
            ks-node (graph/hook graph keyspace :main)

            ks (async/poll! ks-node)]
        (prn node1)
        (go
          ;(prn 1)
          (is (= (async/<! (graph/hook graph key testsel :test)) 7))
          ;(prn 2)
          (proto/clear-node! node1 graph)
          (is (= (async/<! (graph/hook graph key testsel :test)) 8))
          ;(prn 3)
          (proto/clear-node! node1 graph)
          (is (= (async/<! (graph/hook graph key testsel :test)) 9))
          ;(prn 4)
          (done))
        (graph/apply-effects graph [[testsel [:set-value {:test 7}]]])
        (graph/apply-effects graph [[testsel [:set-value {:test 8}]]])
        (graph/apply-effects graph [[testsel [:set-value {:test 9}]]])

        ))))

