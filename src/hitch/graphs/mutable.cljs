(ns hitch.graphs.mutable
  (:require [hitch.protocols :as proto]
            [hitch.nodes.simple :refer [node]]))

;; "deps is a map from graphs => (maps of DataSelectors => DataSelectors state)"
(deftype DependencyGraph [^:mutable nodemap ^:mutable tempstate ^:mutable internal-invalidated
                          ^:mutable effects ^:mutable external-invalidate!]
  proto/IDependencyGraph
  (peek-node [this data-selector]
    (get nodemap data-selector))
  (create-node! [this data-selector]
    (let [new-node (node data-selector)]
      (when (satisfies? proto/StatefulSelector data-selector)
        (set! (.-state new-node) (proto/init data-selector)))
      (set! nodemap (assoc nodemap data-selector new-node))
      new-node))
  (clear-graph! [dgraph]
    (doseq [node (vals nodemap)]
      (proto/clear-node! node dgraph))
    (set! nodemap {})
    (set! tempstate  {}))
  (gc [this data-selector]
    #_(do (doseq [d (proto/selector-dependencies data-selector)]
            (proto/undepend! this d))
          true))
  proto/IBatching
  (-request-invalidations [graph invalidaiton]
    (if internal-invalidated
      (set! internal-invalidated (conj! internal-invalidated invalidaiton))
      (set! internal-invalidated (transient [invalidaiton]))))
  (peek-invalidations [graph]
    internal-invalidated)
  (take-invalidations! [graph]
    (when-let [ret internal-invalidated]
      (set! internal-invalidated nil)
      (persistent! ret))))

(defn graph []
  (DependencyGraph. {}  {} (transient []) (transient []) identity))

(defn get-node-map [graph]
  (.-nodemap graph))


