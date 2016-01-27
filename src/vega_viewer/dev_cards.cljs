(ns vega-viewer.dev-cards
  (:require [vega-viewer.components.vega-viewer :refer [vega-viewer]]
            [vega-viewer.components.horizontal-bar-chart
             :refer [horizontal-bar-chart]]
            [vega-viewer.components.histogram
             :refer [histogram-chart]]
            [devcards.core :refer-macros [defcard om-root]]))

(def vega-spec
  (atom {:width 200
         :height 200
         :data [{:name "fruits"
                 :values [{"name" "apple"
                           "number" 23}
                          {"name" "oranges"
                           "number" 42}]}]
         :scales [{:name "category"
                   :type "ordinal"
                   :domain {:data "fruits" :field "name"}
                   :range "width"}
                  {:name "frequency"
                   :type "linear"
                   :range "height"
                   :domain {:data "fruits" :field "number"}}]
         :axes [{:scale "frequency" :type "y"}
                {:scale "category" :type "x"}]
         :marks [{:from {:data "fruits"}
                  :type "rect"
                  :properties {:enter {:x {:scale "category" :field "name"}
                                       :width {:scale "category"
                                               :band true
                                               :offset -1}
                                       :y {:scale "frequency" :field "number"}
                                       :y2 {:scale "frequency" :value 0}}
                               :update {:fill {:value "steelblue"}}}}]}))

(defcard vega-chart
  (om-root vega-viewer)
  vega-spec)

(defcard category-chart
  (om-root horizontal-bar-chart)
  {:data [{"category" "Taxi" "frequency" 12}
          {"category" "Boda boda" "frequency" 300}
          {"category" "Matatu" "frequency" 1872}]})

(defcard histogram
  (om-root histogram-chart)
  {:data [1 1 2 45 50 12 35 37]})
