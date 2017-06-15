(ns vega-viewer.vega.specs.grouped-bar-chart
  (:require [vega-viewer.vega.specs.constants
             :refer [band-width bar-color bar-height bar-height-offset
                     default-chart-width palette tooltip-height
                     tooltip-width tooltip-offset tooltip-opacity
                     tooltip-stroke-color y-offset max-height]]
            [vega-viewer.vega.specs.utils
             :refer [get-tooltip-text-marks
                     set-status-text
                     set-tooltip-bounds
                     show-percent-sign-on-tooltip
                     truncate-y-axis-labels]]))

(def grouped-bar-chart-spec-template
  {:data [{:name "source"
           :values []}
          {:name "summary"
           :source "source"
           :transform [{:type "aggregate"
                        :groupby ["z", "x"]
                        :summarize {:y ["sum"]}}]}
          {:name "stacked_scale"
           :source "summary"
           :transform [{:type "aggregate"
                        :summarize [{:ops ["sum"], :field "sum_y"}]
                        :groupby ["z", "x"]}]}
          {:name "layout"
           :source "summary"
           :transform [{:type "aggregate"
                        :summarize [{:field "x"
                                     :ops ["distinct"]}
                                    {:field "z"
                                     :ops ["distinct"]}]}
                       {:type "formula"
                        :field "child_width"
                        :expr "(datum[\"distinct_z\"] + 1) * 6"}
                       {:type "formula"
                        :field "width"
                        :expr "(datum[\"child_width\"] + 25) *
                        datum[\"distinct_x\"]"}
                       {:type "formula"
                        :field "child_height"
                        :expr "200"}
                       {:type "formula"
                        :field "height"
                        :expr "datum[\"child_height\"] + 16"}]}]
   :marks [{:name "root"
            :type "group"
            :from {:data "layout"}
            :properties
            {:update {:width {:field "width"}
                      :height {:field "height"}}}
            :marks [{:name "y-axes"
                     :type "group"
                     :properties
                     {:update
                      {:width {:field {:group "width"}}
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
                                         :groupby ["x"]}]}
                     :properties
                     {:update
                      {:x {:scale "column"
                           :field "x"
                           :offset 5}
                       :y {:value 8}
                       :width {:field {:parent "child_width"}}
                       :height {:field {:parent "child_height"}}
                       :stroke {:value "#CCC"}
                       :strokeWidth {:value 0}}}
                     :marks [{:name "child_marks"
                              :type "rect"
                              :from {:transform [{:type "stack"
                                                  :groupby ["z"]
                                                  :field "sum_y"
                                                  :sortby ["-z"]
                                                  :output {:start "sum_y_start"
                                                           :end "sum_y_end"}
                                                  :offset "zero"}]}
                              :properties {:update {:xc {:scale "x"
                                                         :field "z"}
                                                    :width {:value 15}
                                                    :y {:scale "y"
                                                        :field "sum_y_start"}
                                                    :y2 {:scale "y"
                                                         :field "sum_y_end"}
                                                    :fill {:scale "color"
                                                           :field "z"}}}}]}]
            :scales [{:name "column"
                      :type "ordinal"
                      :domain {:data "summary"
                               :field "x"}
                      :range "width"
                      :round true}
                     {:name "x"
                      :type "ordinal"
                      :domain {:data "summary"
                               :field "z"
                               :sort true}
                      :bandSize 12
                      :round true
                      :points true
                      :padding 1}
                     {:name "y"
                      :type "linear"
                      :domain {:data "stacked_scale"
                               :field "sum_sum_y"}
                      :rangeMin 200
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
                    :offset -6.5
                    :orient "bottom"
                    :tickSize 5
                    :properties {:axis {:strokeWidth {:value 1}}
                                 :labels {:angle {:value 300}
                                          :dx {:value 0}
                                          :dy {:value -7}
                                          :align {:value "right"}}}}]
            :legends [{:fill "color"
                       :offset 100
                       :properties {:symbols {:shape       {:value "circle"}
                                              :strokeWidth {:value 0}}}}]}]})

(defn generate-grouped-bar-chart-vega-spec
  [{:keys [data height width status-text
           maximum-y-axis-label-length]}
   & {:keys [responsive? user-defined-palette]}]
  (let [chart-height (min (or height (* (count data) band-width)) max-height)
        chart-width (or width
                        (and (not responsive?)
                             default-chart-width))]
    (-> grouped-bar-chart-spec-template
        (assoc-in [:data 0 :values] data)
        (assoc :height chart-height)
        (assoc :width chart-width)
        (assoc-in [:marks 0 :scales 3 :range] (if (seq user-defined-palette)
                                                user-defined-palette
                                                palette))
        (set-status-text status-text chart-height)
        (truncate-y-axis-labels maximum-y-axis-label-length))))
