(ns hitch.protocols)

(def ^:dynamic *dependent* nil)

(def ^:dynamic *dynamic-dep-tx* nil)

(defprotocol IDataSelectorDynamicRefs)
(defprotocol IDataSelectorStaticRefs
  (selector-dependencies [this]
                         "returns a list of data selectors this dataselector depends on"))
(defprotocol IDataSelector
  "The interface implemented by dataselector records to realize their results"
  (get-value! [this deps]
             "returns the value for the dataselector"))

(defprotocol IDependentTransaction
  (start [_] "initialize transaction")
  (add-dep [_ dep] "track new dependency")
  (commit [_] "commit transaction"))

(defprotocol IDependencyGraph
  "Implemented by function and component caches"
  (get-node [this data-selector] "gets node for dataselector")
  (create-node! [this data-selector]
            "create node and follow init lifecycle")
  (gc [this data-selector]
      "Schedule clean up of the resources for dataselector")
  )

(defprotocol IDependencyTracker
  "Implemented by function and component caches"
  (depend! [this dependent]
            "Dependency sources call this method if a tracker is bound in the current
             context with dependencies that are encountered during query processing.")
  (un-depend! [this dependent]))

(defprotocol IDependencyNode
  "A utility API for tracking dependencies, allows us to provide more
   advanced options for assembling tracker policies"
  ;(reset! [this] "Clear cache")
  (get-value [this]
             "Returns cached value if exists for params")
  (resolve-value! [this]
                  "Informs store that a particular params yeilds value given current store + deps")
  ;(rem-value! [this params])
  (invalidate! [this])
  (dependents [this]
                "The current dependencies encountered by this tracker"))

(defprotocol IKeyStore
  "public interface"
  (get-key [this key])
  (set-key [this key value])
  (swap-key! [this fn])
  (clear! [this]))