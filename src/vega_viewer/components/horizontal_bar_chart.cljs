(ns vega-viewer.components.horizontal-bar-chart
  (:require [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]
            [vega-viewer.components.vega-viewer :refer [vega-viewer]]))

(def bar-height 31)

(def vega-spec-template
  {:data [{:name "entries"
           :values []}]
   :scales [{:name "category"
             :type "ordinal"
             :domain {:data "entries" :field "category"}
             :bandWidth bar-height
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
                                          :value 30
                                          :band true
                                          :offset -1}
                                 :x {:scale "frequency" :field "frequency"}
                                 :x2 {:value 0}}
                         :update {:fill {:value "steelblue"}}}}]})


(defn generate-horizontal-bar-chart-vega-spec
  [data]
  (let [tick-count (->> data
                        (map #(get-in % ["frequency"]))
                        (apply max))]
    (-> vega-spec-template
        (assoc-in [:data 0 :values] data)
        (assoc-in [:height] (* (count data) bar-height))
        (assoc-in [:axes 0 :ticks] tick-count))))

(defn horizontal-bar-chart
  [cursor owner]
  (reify
    om/IRender
    (render [_]
      (let [vega-spec (generate-horizontal-bar-chart-vega-spec cursor)]
        (html (om/build vega-viewer vega-spec))))))
