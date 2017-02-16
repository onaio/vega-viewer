(ns vega-viewer.vega.specs.histogram
  (:require [vega-viewer.vega.specs.constants
             :refer [bar-color
                     default-chart-width
                     default-histogram-tick-count
                     histogram-height
                     maximum-bin-count
                     max-height]]
            [vega-viewer.vega.specs.utils :refer [set-status-text
                                                  update-x-axis-tick-labels]]))

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
           :layer "back"
           :title nil}
          {:type "y"
           :scale "y"
           :layer "back"
           :title nil}]
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
   & {:keys [responsive?
             x-axis-tick-label-format
             x-axis-title
             y-axis-title]}]
  (let [height (min (or height histogram-height) max-height)]
    (-> histogram-spec-template
        (update-x-axis-tick-labels x-axis-tick-label-format)
        (assoc-in [:data 0 :values] (map (fn [value] {"value" value}) values))
        (assoc-in [:axes 0 :title] x-axis-title)
        (assoc-in [:axes 1 :title] y-axis-title)
        (assoc :height height)
        (assoc :width (or width
                          (and (not responsive?)
                               default-chart-width)))
        (set-status-text status-text histogram-height))))
