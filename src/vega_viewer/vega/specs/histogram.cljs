(ns vega-viewer.vega.specs.histogram
  (:require [vega-viewer.vega.specs.constants
             :refer [bar-color
                     band-width
                     default-chart-width
                     default-histogram-tick-count
                     histogram-height
                     maximum-bin-count
                     max-height
                     tooltip-height
                     tooltip-width
                     tooltip-offset
                     tooltip-opacity
                     tooltip-stroke-color
                     y-offset]]
            [vega-viewer.vega.specs.utils :refer [set-status-text
                                                  chart-title-text
                                                  update-x-axis-tick-labels
                                                  get-tooltip-text-marks
                                                  set-tooltip-bounds
                                                  custom-chart-tooltips]]))

(def histogram-spec-template
  {:data [{:name "entries"
           :values []
           :transform [{:type    "bin"
                        :field   "value"
                        :maxbins maximum-bin-count
                        :minstep 1}]}
          {:name      "summary"
           :source    "entries"
           :transform [{:type      "aggregate"
                        :groupby   ["bin_start" "bin_end"]
                        :summarize {:* ["count"]}}]}]
   :scales [{:name   "x"
             :type   "linear"
             :range  "width"
             :domain {:data "summary" :field ["bin_start" "bin_end"]}}
            {:name   "y"
             :type   "linear"
             :domain {:data "summary" :field "count" :sort true}
             :range  "height"}]
   :axes [{:type     "x"
           :scale    "x"
           :ticks    default-histogram-tick-count
           :layer    "back"}
          {:type     "y"
           :scale    "y"
           :layer    "back"}]
   :marks [{:type       "rect"
            :properties {:enter
                         {:x    {:scale "x" :field "bin_start" :offset 1}
                          :x2   {:scale "x" :field "bin_end"}
                          :y    {:scale "y" :field "count"}
                          :y2   {:field {:group "height"}}
                          :fill {:value bar-color}}}
            :from       {:data "summary"}}
           {:type       "text"
            :from       {:mark "rect"}
            :properties {:enter {:align {:value "center"}
                                 :x     {:field "x"}
                                 :y     {:field "y"}
                                 :dx    {:field "width" :mult 0.5}
                                 :dy    {:value -7}
                                 :fill  {:value "black"}
                                 :text  {:field "datum.count"}}}}]
   :signals [{:name "tooltipData"
              :init {}
              :streams [{:type "rect:mouseover" :expr "datum"}
                        {:type "rect:mouseout" :expr "{}"}]}
             {:name "tooltipX"
              :init {}
              :streams [{:type "mousemove" :expr "eventX()"}]}
             {:name "tooltipY"
              :init {}
              :streams [{:type "mousemove" :expr "eventY()"}]}]
   :predicates [{:name "isTooltipVisible?"
                 :type "==",
                 :operands [{:signal "tooltipData._id"}
                            {:arg "id"}]}]})

(defn generate-histogram-chart-vega-spec
  [{values :data :keys [height status-text chart-text width]}
   & {:keys [responsive?
             x-axis-tick-label-format
             x-axis-title
             y-axis-title
             duration-chart-tooltips]}]
  (let [height (min (or height histogram-height) max-height)
        chart-height (min (or height (* (count :data) band-width)) max-height)
        chart-width (or width
                        (and (not responsive?)
                             default-chart-width))]
    (cond-> histogram-spec-template
      x-axis-tick-label-format
      (update-x-axis-tick-labels x-axis-tick-label-format)
      duration-chart-tooltips
      (custom-chart-tooltips {:duration-chart-tooltips duration-chart-tooltips})
      true (assoc-in [:data 0 :values]
                     (map (fn [value] {"value" value}) values))
      x-axis-title (assoc-in [:axes 0 :title] x-axis-title)
      y-axis-title (assoc-in [:axes 1 :title] y-axis-title)
      height (assoc :height height)
      true (assoc :width (or width
                             (and (not responsive?)
                                  default-chart-width)))
      (set-tooltip-bounds :visualization-height chart-height)
      (set-tooltip-bounds :visualization-width chart-width)
      status-text (set-status-text status-text histogram-height)
      chart-text (chart-title-text chart-text histogram-height))))
