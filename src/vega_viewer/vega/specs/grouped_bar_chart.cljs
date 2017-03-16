(ns vega-viewer.vega.specs.grouped-bar-chart
  (:require [vega-viewer.vega.specs.constants
             :refer [band-width bar-color bar-height bar-height-offset
                     default-chart-width tooltip-height tooltip-width
                     tooltip-offset tooltip-opacity tooltip-stroke-color
                     y-offset max-height]]
            [vega-viewer.vega.specs.utils
             :refer [get-tooltip-text-marks
                     set-status-text
                     set-tooltip-bounds
                     show-percent-sign-on-tooltip
                     truncate-y-axis-labels]]))

(def grouped-bar-chart-spec-template
  {:data [{:name "source"
           :values []
           :transform [
                         {
                          :type "formula",
                          :field "gender",
                          :expr "datum.sex == 2 ? \"Female\" : \"Male\""
                          }]}
          {:name "summary"
           :source "source"
           :transform [{:type "aggregate"
                        :groupby ["gender", "age"]
                        :summarize {:people ["sum"]}}]}
          {:name "stacked_scale"
           :source "summary"
           :transform [{:type "aggregate"
                        :summarize [{:ops ["sum"], :field "sum_people"}]
                        :groupby ["gender", "age"]}]}
          {:name "layout"
           :source "summary"
           :transform [{:type "aggregate"
                        :summarize [{:field "age"
                                     :ops ["distinct"]}
                                    {:field "gender"
                                     :ops ["distinct"]}]}
                       {:type "formula"
                        :field "child_width"
                        :expr "(datum[\"distinct_gender\"] + 1) * 6"}
                       {:type "formula"
                        :field "width"
                        :expr "(datum[\"child_width\"] + 4) * datum[\"distinct_age\"]"}
                       {:type "formula"
                        :field "child_height"
                        :expr "200"}
                       {:type "formula"
                        :field "height"
                        :expr "datum[\"child_height\"] + 16"}]}]
   :marks [{:name "root"
            :type "group"
            :from {:data "layout"}
            :properties {:update {:width {:field "width"}
                                  :height {:field "height"}}}
            :marks [{:name "y-axes"
                     :type "group"
                     :properties {:update {:width {:field {:group "width"}}
                                           :height {:field {:parent "child_height"}}
                                           :y {:value 8}}}
                     :axes [{:type "y"
                             :scale "y"
                             :format "s"
                             :grid false
                             :title "Number of people"}]}
                    {:name "cell"
                     :type "group"
                     :from {:data "summary"
                            :transform [{:type "facet"
                                        :groupby ["age"]}]}
                     :properties {:update {:x {:scale "column"
                                               :field "age"
                                               :offset 2}
                                           :y {:value 8}
                                           :width {:field {:parent "child_width"}}
                                           :height {:field {:parent "child_height"}}
                                           :stroke {:value "#CCC"}
                                           :strokeWidth {:value 0}}}
                     :marks [{:name "child_marks"
                              :type "rect"
                              :from {:transform [{:type "stack"
                                                  :groupby ["gender"]
                                                  :field "sum_people"
                                                  :sortby ["-gender"]
                                                  :output {:start "sum_people_start"
                                                           :end "sum_people_end"}
                                                  :offset "zero"}]}
                              :properties {:update {:xc {:scale "x"
                                                         :field "gender"}
                                                    :width {:value 5}
                                                    :y {:scale "y"
                                                        :field "sum_people_start"}
                                                    :y2 {:scale "y"
                                                         :field "sum_people_end"}
                                                    :fill {:scale "color"
                                                           :field "gender"}}}}]}]
            :scales [{:name "column"
                      :type "ordinal"
                      :domain {:data "summary"
                               :field "age"
                               :sort true}
                      :range "width"
                      :round true}
                     {:name "x"
                      :type "ordinal"
                      :domain {:data "summary"
                               :field "gender"
                               :sort true}
                      :bandSize 6
                      :round true
                      :points true
                      :padding 1}
                     {:name "y"
                      :type "linear"
                      :domain {:data "stacked_scale"
                               :field "sum_sum_people"}
                      :rangeMin 200
                      :rangeMax 0
                      :round true
                      :nice true
                      :zero true}
                     {:name "color"
                      :type "ordinal"
                      :domain {:data "summary"
                               :field "gender"
                               :sort true}
                      :range ["#EA98D2", "#659CCA"]}]
            :axes [{:type "x"
                    :scale "column"
                    :offset -8
                    :orient "bottom"
                    :tickSize 5
                    :title "age"
                    :properties {:axis {:strokeWidth {:value 1}}}}]
            :legends [{:fill "color"
                       :title "gender"
                       :properties {:symbols {:shape {:value "circle"}
                                              :strokeWidth {:value 0}}}}]}]})

(defn generate-grouped-bar-chart-vega-spec
  [{:keys [data height width status-text
           maximum-y-axis-label-length]}
   & {:keys [responsive?]}]
  (let [chart-height (min (or height (* (count data) band-width)) max-height)
        chart-width (or width
                        (and (not responsive?)
                             default-chart-width))]
    (-> grouped-bar-chart-spec-template
        (assoc-in [:data 0 :values] data)
        (assoc :height chart-height)
        (assoc :width chart-width)
        (set-status-text status-text chart-height)
        (truncate-y-axis-labels maximum-y-axis-label-length))))
