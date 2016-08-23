(ns vega-viewer.vega.specs.histogram
  (:require [vega-viewer.vega.specs.constants
             :refer [bar-color maximum-bin-count default-chart-width
                     default-histogram-tick-count histogram-height max-height]]
            [vega-viewer.vega.specs.utils :refer [set-status-text]]))

(def histogram-spec-template
  {:data [{:name "entries"
           :values []
           :transform [{:type "bin"
                        :field "value"
                        :maxbins maximum-bin-count
                        :minstep 1}]}
          {:name "summary"
           :source "entries"
           :transform [{:type "aggregate"
                        :groupby ["bin_start" "bin_end"]
                        :summarize {:* ["count"]}}]}]
   :scales [{:name "x"
             :type "linear"
             :range "width"
             :domain {:data "summary" :field ["bin_start" "bin_end"]}}
            {:name "y"
             :type "linear"
             :domain {:data "summary" :field "count" :sort true}
             :range "height"}]
   :axes [{:type "x"
           :scale "x"
           :ticks default-histogram-tick-count
           :layer "back"}
          {:type "y" :scale "y" :layer "back"}]
   :marks [{:type "rect"
            :properties {:enter
                         {:x {:scale "x" :field "bin_start" :offset 1}
                          :x2 {:scale "x" :field "bin_end"}
                          :y {:scale "y" :field "count"}
                          :y2 {:field {:group "height"}}
                          :fill {:value bar-color}}}
            :from {:data "summary"}}
           {:type "text"
            :from {:mark "rect"}
            :properties {:enter {:align {:value "center"}
                                 :x {:field "x"}
                                 :y {:field "y"}
                                 :dx {:field "width" :mult 0.5}
                                 :dy {:value -7}
                                 :fill {:value "black"}
                                 :text {:field "datum.count"}}}}]})

(defn generate-histogram-chart-vega-spec
  [{values :data :keys [height status-text width]}
   & {:keys [responsive? abbreviate-x-axis-tick-labels?]}]
  (let [height (min (or height histogram-height) max-height)
        abbreviate-x-axis-tick-labels
        (fn [spec]
          (if abbreviate-x-axis-tick-labels?
            (assoc-in spec
                      [:axes 0 :properties]
                      {:labels
                       {:text
                        {:template
                         "{{ datum.data | number:'.3s'}}"}}})
            spec))]
    (-> histogram-spec-template
        abbreviate-x-axis-tick-labels
        (assoc-in [:data 0 :values] (map (fn [value]
                                           {"value" value})
                                         values))
        (assoc :height height)
        (assoc :width (or width
                          (and (not responsive?)
                               default-chart-width)))
        (set-status-text status-text histogram-height))))
