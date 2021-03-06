(ns vega-viewer.vega.specs.horizontal-bar-chart
  (:require [vega-viewer.vega.specs.constants
             :refer [band-width
                     bar-color
                     bar-height
                     bar-height-offset
                     default-chart-width
                     tooltip-height
                     tooltip-width
                     tooltip-offset
                     tooltip-opacity
                     tooltip-stroke-color
                     y-offset max-height
                     palette]]
            [vega-viewer.vega.specs.utils
             :refer [get-tooltip-text-marks
                     custom-chart-tooltips
                     set-status-text
                     chart-title-text
                     set-tooltip-bounds
                     show-percent-sign-on-tooltip
                     update-x-axis-tick-labels
                     truncate-y-axis-labels]]))

(def horizontal-bar-chart-spec-template
  {:data [{:name "entries"
           :values []}]
   :scales [{:name "category"
             :type "ordinal"
             :domain {:data "entries" :field "category"}
             :bandWidth band-width
             :range "height"}
            {:name "frequency"
             :type "linear"
             :range "width"
             :round true
             :domain {:data "entries" :field "frequency"}}
            {:name "color"
             :type "ordinal"
             :domain {:data "entries"
                      :field "category"
                      :sort true}}]
   :axes [{:scale "frequency"
           :type "x"
           :layer "back"
           :properties {:labels {:text {:template "{{datum.data}}"}}}}
          {:scale "category"
           :type "y"
           :layer "back"}]
   :marks [{:name "bars"
            :from {:data "entries"}
            :type "rect"
            :properties {:enter {:y {:scale "category"
                                     :field "category"
                                     :offset y-offset}
                                 :height {:value bar-height
                                          :offset bar-height-offset}
                                 :x {:scale "frequency" :field "frequency"}
                                 :x2 {:value 1}}
                         :update {:fill {:scale "color" :field "category"}}}}
           {:type "text"
            :from {:data "entries"}
            :properties {:enter {:x {:scale "frequency"
                                     :field "frequency"
                                     :offset 3}
                                 :y {:scale "category"
                                     :field "category"
                                     :offset (/ bar-height 2)}
                                 :dy {:field "height" :mult 0.5}
                                 :fill {:value "black"}
                                 :baseline {:value "middle"}
                                 :text {:field "frequency"}}}}
           {:type "group"
            :properties {:enter {:align {:value "center"}
                                 :fill {:value "#fff"}}
                         :update {:y {:signal "tooltipY"
                                      :offset tooltip-offset}
                                  :x {:signal "tooltipX"
                                      :offset tooltip-offset}
                                  :height {:rule [{:predicate
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
            :marks (get-tooltip-text-marks {:label-field "category"
                                            :value-field "frequency"})}]
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

(defn generate-horizontal-bar-chart-vega-spec
  [{:keys [data height width show-count-or-percent? status-text
           chart-text maximum-y-axis-label-length]}
   & {:keys [responsive? x-axis-tick-label-format submitted-by-tooltips
             user-defined-palette]}]
  (let [count-or-percent #(if (= show-count-or-percent? :percent)
                            (-> %
                                (assoc-in [:axes 0 :properties :labels :text
                                           :template]
                                          "{{datum.data}} %")
                                (assoc-in [:marks 1 :properties :enter :text]
                                          {:template "{{datum.frequency}}%"})
                                (show-percent-sign-on-tooltip 2))
                            %)
        chart-height (min (or height (* (count data) band-width)) max-height)
        chart-width (or width
                        (and (not responsive?)
                             default-chart-width))]
    (-> horizontal-bar-chart-spec-template
        (update-x-axis-tick-labels x-axis-tick-label-format)
        (custom-chart-tooltips {:submitted-by-tooltips submitted-by-tooltips})
        (assoc-in [:data 0 :values] data)
        (assoc :height chart-height)
        (assoc :width chart-width)
        (assoc-in [:scales 2 :range] (or (seq user-defined-palette)
                                         palette))
        (set-tooltip-bounds :visualization-height chart-height)
        (set-tooltip-bounds :visualization-width chart-width)
        (set-status-text status-text chart-height)
        (chart-title-text chart-text chart-height)
        (truncate-y-axis-labels maximum-y-axis-label-length)
        count-or-percent)))
