(ns vega-viewer.vega.specs.grouped-stacked-chart
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

(def grouped-stacked-chart-spec-template
  {:data [{:name "source"
           :values []}
          {:name "summary"
           :source "source"
           :transform [{:type "aggregate"
                        :groupby ["x", "z", "u"]
                        :summarize {:y ["sum"]}}]}
          {:name "stacked_scale"
           :source "summary"
           :transform [{:type "aggregate"
                        :summarize [{:ops ["sum"], :field "sum_y"}]
                        :groupby ["x", "u"]}]}
          {:name "layout"
           :source "summary"
           :transform [{:type "aggregate"
                        :summarize [{:field "u"
                                     :ops ["distinct"]}
                                    {:field "x"
                                     :ops ["distinct"]}]}
                       {:type "formula"
                        :field "child_width"
                        :expr "(datum[\"distinct_x\"] + 1) * 21"}
                       {:type "formula"
                        :field "width"
                        :expr "(datum[\"child_width\"] + 16) *
                        datum[\"distinct_u\"]"}
                       {:type "formula"
                        :field "child_height"
                        :expr "300"}
                       {:type "formula"
                        :field "height"
                        :expr "datum[\"child_height\"] + 20"}]}]
   :marks [{:name "root"
            :type "group"
            :from {:data "layout"}
            :properties
            {:update {:width  {:field "width"}
                      :height {:field "height"}}}
            :marks [{:name "x-axes"
                     :type "group"
                     :from {:data "summary"
                            :transform [{:type "aggregate"
                                         :groupby ["u"]
                                         :summarize {:* ["count"]}}]}
                     :properties
                     {:update {:width {:field {:parent "child_width"}}
                               :height {:field {:group "height"}}
                               :x {:scale "column"
                                   :field "u"
                                   :offset 8}}}
                     :axes [{:type "x"
                             :scale "x"
                             :grid false
                             :ticks 5
                             :properties
                             {:labels
                              {:text
                               {:template "{{ datum.data | truncate:25 }}"}
                               :angle {:value 270}
                               :align {:value "right"}
                               :baseline {:value "middle"}}}}]}
                    {:name "y-axes"
                     :type "group"
                     :properties
                     {:update {:width {:field {:group "width"}}
                               :height {:field {:parent "child_height"}}
                               :y {:value 8}}}
                     :axes [{:type "y"
                             :scale "y"
                             :format "s"
                             :grid false}]}
                    {:name "cell"
                     :type "group"
                     :from {:data "summary"
                            :transform [{:type "facet"
                                         :groupby ["u"]}]}
                     :properties
                     {:update {:x {:scale "column"
                                   :field "u"
                                   :offset 8}
                               :y {:value 8}
                               :width {:field {:parent "child_width"}}
                               :height {:field {:parent "child_height"}}
                               :stroke {:value "#CCC"}
                               :strokeWidth {:value 1}}}
                     :marks [{:name "child_marks"
                              :type "rect"
                              :from {:transform [{:type "stack"
                                                  :groupby ["x"]
                                                  :field "sum_y"
                                                  :sortby ["-z"]
                                                  :output
                                                  {:start "sum_y_start"
                                                   :end "sum_y_end"}
                                                  :offset "zero"}]}
                              :properties
                              {:update {:xc {:scale "x"
                                             :field "x"}
                                        :width {:value 20}
                                        :y {:scale "y"
                                            :field "sum_y_start"}
                                        :y2 {:scale "y"
                                             :field "sum_y_end"}
                                        :fill {:scale "color"
                                               :field "z"}}}}]
                     :axes [{:type "y"
                             :scale "y"
                             :grid true
                             :tickSize 0
                             :properties
                             {:labels {:text {:value ""}}
                              :axis {:stroke {:value "transparent"}}}
                             :layer "back"}]}]
            :scales [{:name "column"
                      :type "ordinal"
                      :domain {:data "summary"
                               :field "u"
                               :sort true}
                      :range "width"
                      :round true}
                     {:name "x"
                      :type "ordinal"
                      :domain {:data "summary"
                               :field "x"
                               :sort true}
                      :bandSize 21
                      :round true
                      :points true
                      :padding 1}
                     {:name "y"
                      :type "linear"
                      :domain {:data "stacked_scale"
                               :field "sum_sum_y"}
                      :rangeMin 300
                      :rangeMax 0
                      :round true
                      :nice true
                      :zero true}
                     {:name "color"
                      :type "ordinal"
                      :domain {:data "summary"
                               :field "z"
                               :sort true}}]
            :axes [{:type "x"
                    :scale "column"
                    :orient "top"
                    :tickSize 0
                    :properties
                    {:axis {:strokeWidth {:value 0}}}}]
            :legends [{:fill "color"
                       :properties
                       {:symbols {:shape {:value "circle"}
                                  :strokeWidth {:value 0}}}}]}
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
                                            :value-field "sum_y"})}]
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

(defn generate-grouped-stacked-chart-vega-spec
  [{:keys [data height width status-text
           chart-text maximum-y-axis-label-length]}
   & {:keys [responsive? user-defined-palette]}]
  (let [chart-height (min (or height (* (count data) band-width)) max-height)
        chart-width (or width
                        (and (not responsive?)
                             default-chart-width))]
    (-> grouped-stacked-chart-spec-template
        (assoc-in [:data 0 :values] data)
        (assoc :height 100)
        (assoc :width chart-width)
        (assoc-in [:marks 0 :scales 3 :range] (if (seq user-defined-palette)
                                                user-defined-palette
                                                palette))
        (set-tooltip-bounds :visualization-height chart-height)
        (set-tooltip-bounds :visualization-width chart-width)
        (set-status-text status-text chart-height)
        (chart-title-text chart-text chart-height))))
