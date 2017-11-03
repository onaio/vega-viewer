(ns vega-viewer.vega.specs.stacked-bar-chart
  (:require [vega-viewer.vega.specs.constants
             :refer [band-width bar-color bar-height bar-height-offset
                     default-chart-width palette tooltip-height
                     tooltip-width tooltip-offset tooltip-opacity
                     tooltip-stroke-color y-offset max-height]]
            [vega-viewer.vega.specs.utils
             :refer [get-tooltip-text-marks
                     set-status-text
                     chart-title-text
                     set-tooltip-bounds
                     show-percent-sign-on-tooltip
                     truncate-y-axis-labels]]))

(def stacked-bar-chart-spec-template
  {:data [{:name "table"
           :values []}
          {:name "stats"
           :source "table"
           :transform [{:type "aggregate"
                        :groupby ["x"]
                        :summarize [{:field "y" :ops ["sum"]}]}]}]
   :scales [{:name "x"
             :type "ordinal"
             :range "width"
             :domain {:data "table" :field "x"}}
            {:name "y"
             :type "linear"
             :range "height"
             :nice true
             :domain {:data "stats" :field "sum_y"}}
            {:name "color"
             :type "ordinal"
             :range "category10"
             :domain {:data "table" :field "z"}}]
   :axes [{:type "x"
           :scale "x"}
          {:type "y"
           :scale "y"}]
   :legends [{:fill "color"
              :properties {:symbols {:shape {:value "circle"}
                                     :strokeWidth {:value 0}}}}]
   :marks [{:type "rect"
            :from {:data "table"
                   :transform [{:type "stack"
                                :groupby ["x"]
                                :field "y"
                                :sortby ["z"]}]}
            :properties {:enter
                         {:x {:scale "x" :field "x"}
                          :width {:scale "x" :band true :offset -3}
                          :y {:scale "y" :field "layout_start"}
                          :y2 {:scale "y" :field "layout_end"}
                          :fill {:scale "color" :field "z"}}
                         :update
                         {:fillOpacity {:value 1}}
                         :hover
                         {:fillOpacity {:value 0.5}}}}
             {:type "group"
                :properties {:enter {:align {:value "center"}
                                        :fill {:value "#fff"}}
                                :update {:y {:signal "tooltipY"
                                        :offset tooltip-offset}
                                        :x {:signal "tooltipX"
                                        :offset tooltip-offset}
                                        :height {:rule
                                                [{:predicate
                                                {:name "isTooltipVisible?"}
                                                :value 0}
                                                {:value tooltip-height}]}
                                        :width {:value tooltip-width}
                                        :fillOpacity {:value tooltip-opacity}
                                        :stroke {:value tooltip-stroke-color}
                                        :strokeWidth
                                        {:rule
                                        [{:predicate {:name "isTooltipVisible?"}
                                        :value 0}
                                        {:value 1}]}}}
                :marks (get-tooltip-text-marks {:label-field "z"
                                            :value-field "y"})}]
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

(defn generate-stacked-bar-chart-vega-spec
  [{:keys [data height width status-text chart-text
           maximum-y-axis-label-length]}
   & {:keys [responsive? user-defined-palette]}]
  (let [chart-height (min (or height (* (count data) band-width)) max-height)
        chart-width (or width
                        (and (not responsive?)
                             default-chart-width))]
    (-> stacked-bar-chart-spec-template
        (assoc-in [:data 0 :values] data)
        (assoc :height chart-height :width chart-width)
        (assoc-in [:marks 0 :scales 2 :range] (or (seq user-defined-palette)
                                                  palette))
        (set-tooltip-bounds :visualization-height chart-height)
        (set-tooltip-bounds :visualization-width chart-width)
        (set-status-text status-text chart-height)
        (chart-title-text chart-text chart-height)
        (truncate-y-axis-labels maximum-y-axis-label-length))))
