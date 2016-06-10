(ns vega-viewer.vega.specs.stacked-horizontal-bar-chart
  (:require [vega-viewer.vega.specs.constants
             :refer [band-width bar-height bar-height-offset
                     default-chart-width palette tooltip-height
                     tooltip-offset tooltip-opacity tooltip-stroke-color
                     tooltip-width y-offset]]
            [vega-viewer.vega.specs.utils
             :refer [get-tooltip-text-marks
                     set-status-text
                     set-tooltip-bounds
                     show-percent-sign-on-tooltip]]))

(def inline-stack-label-color "#fff")
(def inline-stack-label-y-offset
  "This is based on the height of the bar to ensure proper alignment"
  (-> bar-height
      (* 0.5)
      (+ 5)))

(def stacked-horizontal-bar-chart-spec-template
  {:data [{:name "table"}
          {:name "stats"
           :source "table"
           :transform
           [{:type "aggregate"
             :groupby ["category"]
             :summarize [{:field "frequency"
                          :ops ["sum"]}]}]}
          {:name "grouped-data"
           :source "table"
           :transform [{:type "stack"
                        :groupby ["category"]
                        :sortby ["group-ranking", "group"]
                        :field "frequency"}]}]
   :scales [{:name "y"
             :type "ordinal"
             :range "height"
             :bandWidth band-width
             :domain {:data "table"
                      :field "category"}}
            {:name "x"
             :type "linear"
             :range "width"
             :nice true
             :domain {:data "stats"
                      :field "sum_frequency"}}
            {:name "color"
             :type "ordinal"
             :domain {:data "table"
                      :field "group"
                      :sort true}}]
   :axes [{:type "y"
           :scale "y"
           :layer "back"}
          {:type "x"
           :scale "x"
           :layer "back"}]
   :legends [{:fill "color"
              :properties {:labels
                           {:text
                            {:template "{{ datum.data | truncate:25 }}"}}
                           :symbols {:shape {:value "square"}
                                     :stroke {:value "transparent"}}}}]
   :marks [{:type "rect"
            :from {:data "grouped-data"}
            :properties {:enter {:y {:scale "y"
                                     :field "category"
                                     :offset y-offset}
                                 :height {:value bar-height
                                          :offset bar-height-offset}
                                 :x {:scale "x"
                                     :field "layout_end"}
                                 :x2 {:scale "x"
                                      :field "layout_start"
                                      :offset 1}
                                 :fill {:scale "color"
                                        :field "group"}}
                         :update {:fillOpacity {:value 1}}
                         :hover {:fillOpacity {:value 0.9}}}}
           {:type "text"
            :from {:data "grouped-data"}
            :properties {:enter {:align {:value "center"}
                                 :y {:scale "y"
                                     :field "category"
                                     :offset inline-stack-label-y-offset}
                                 :x {:scale "x"
                                     :field "layout_mid"}
                                 :fill {:value inline-stack-label-color}
                                 :text {:template "{{datum.frequency}}"}}}}
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
            :marks (get-tooltip-text-marks "group" "frequency")}]
   :signals [{:name "tooltipData"
              :init {}
              :streams [{:type "rect:mouseover" :expr "datum"}
                        {:type "rect:mouseout" :expr "{}"}]}
             {:name "tooltipX"
              :streams [{:type "mousemove"
                         :expr "eventX()"}]}
             {:name "tooltipY"
              :streams [{:type "mousemove"
                         :expr "eventY()"}]}]
   :predicates [{:name "isTooltipVisible?"
                 :type "=="
                 :operands [{:signal "tooltipData._id"} {:arg "id"}]}]})

(defn- get-category-count
  [data]
  (->> data
       (map #(get-in % ["category"]))
       (set)
       (count)))

(defn generate-stacked-horizontal-bar-chart-vega-spec
  [{:keys [data height width show-count-or-percent? status-text]}
   & {:keys [responsive? user-defined-palette]}]
  (let [count-or-percent #(if (= show-count-or-percent? :percent)
                            (->
                             %
                             (assoc-in [:marks 0 :from :transform 0 :offset]
                                       "normalize")
                             (assoc-in [:scales 1 :domainMax] 1)
                             (assoc-in [:axes 1 :format] "%")
                             (assoc-in [:marks 1 :marks 1 :properties
                                        :update :text]
                                       {:rule
                                        [{:predicate
                                          {:name "tooltipVisible"}}
                                         {:template
                                          "{{tooltipData.frequency}}%"}]})
                             (show-percent-sign-on-tooltip 1))
                            %)
        chart-height (or height (* (get-category-count data)
                                   band-width))
        chart-width (or width
                        (and (not responsive?)
                             default-chart-width))
        get-ranking #(get % "group-ranking")
        set-legend-values (fn [spec]
                            (if (every? get-ranking data)
                              (->> data
                                   (sort-by get-ranking)
                                   (map #(get % "group"))
                                   vec
                                   (assoc-in spec
                                             [:legends 0 :values]))
                              spec))]
    (-> stacked-horizontal-bar-chart-spec-template
        (assoc-in [:data 0 :values] data)
        (assoc :height chart-height)
        (assoc :width chart-width)
        (assoc-in [:scales 2 :range] (if (seq user-defined-palette)
                                       user-defined-palette
                                       palette))
        (set-tooltip-bounds :visualization-height chart-height)
        (set-tooltip-bounds :visualization-width chart-width)
        set-legend-values
        (set-status-text status-text chart-height)
        count-or-percent)))
