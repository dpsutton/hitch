(ns hitch.selectors.http-test
  (:require-macros [hitch.selector :refer [defselector]])
  (:require [cljs.test :refer [] :refer-macros [is run-tests async]]
            [devcards.core :as dc :refer-macros [deftest]]
            [hitch.core :as core]
            [hitch.selectors.http :refer [HTTPSelector http]]
            [hitch.graph :as graph]
            [hitch.mutable.graph :as mgraph]))


(deftest firstt3
         (let [graph (mgraph/graph)]

           (async done
             (graph/hook graph (fn [v]
                                 (is (= v "cat"))
                                 (done))
                         http "http://0.0.0.0:3449/test.json" :get nil  (.-parse js/JSON) nil nil))))

