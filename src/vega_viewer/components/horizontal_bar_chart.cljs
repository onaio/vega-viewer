(ns vega-viewer.components.horizontal-bar-chart
  (:require [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]
            [vega-viewer.components.vega-viewer :refer [vega-viewer]]))

(def vega-spec-template
  {:width 200
   :height 200
   :data [{:name "entries"
           :values []}]
   :scales [{:name "category"
             :type "ordinal"
             :domain {:data "entries" :field "category"}
             :range "height"}
            {:name "frequency"
             :type "linear"
             :range "width"
             :domain {:data "entries" :field "frequency"}}]
   :axes [{:scale "frequency" :type "x"}
          {:scale "category" :type "y"}]
   :marks [{:from {:data "entries"}
            :type "rect"
            :properties {:enter {:y {:scale "category" :field "category"}
                                 :height {:scale "category"
                                          :band true
                                          :offset -1}
                                 :x {:scale "frequency" :field "frequency"}
                                 :x2 {:value 0}}
                         :update {:fill {:value "steelblue"}}}}]})


(defn generate-horizontal-bar-chart-vega-spec
  [data]
  (assoc-in vega-spec-template [:data 0 :values] data))

(defn horizontal-bar-chart
  [cursor owner]
  (reify
    om/IRender
    (render [_]
      (let [vega-spec (generate-horizontal-bar-chart-vega-spec cursor)]
        (html (om/build vega-viewer vega-spec))))))
