(ns vega-viewer.vega.specs.utils
  (:require [clojure.string :refer [join]]
            [vega-viewer.vega.specs.constants
             :refer [band-width tooltip-height tooltip-stroke-color
                     tooltip-width duration-tooltip-width
                     tooltip-offset tooltip-opacity]]))

(defn get-tooltip-text-marks
  [{:keys [label-field value-field submitted-by-chart?]}]
  (let [label-text-x-displacement 10
        value-text-x-displacement 180
        rule-x-displacement 160
        rule-y-displacement 0
        rule-height tooltip-height
        tooltip-text-y-displacement 22
        tooltip-text-color "#444"]
    [{:type "text"
      :properties {:enter {:align {:value "left"}
                           :fill {:value tooltip-text-color}}
                   :update {:y {:value tooltip-text-y-displacement}
                            :x {:value label-text-x-displacement}
                            :fillOpacity
                            {:rule [{:predicate
                                     {:name "isTooltipVisible?"}
                                     :value 0}
                                    {:value 1}]}
                            :text
                            {:template (str "{{ tooltipData."
                                            label-field
                                            " | truncate:25 }}")}}}}
     {:type "text"
      :properties {:enter {:align {:value (when-not submitted-by-chart?
                                            (str "center"))}
                           :fill {:value tooltip-text-color}}
                   :update {:y {:value tooltip-text-y-displacement}
                            :x {:value value-text-x-displacement}
                            :fillOpacity
                            {:rule [{:predicate
                                     {:name "isTooltipVisible?"}
                                     :value 0}
                                    {:value 1}]}
                            :text
                            {:template (if submitted-by-chart?
                                         (str "{{ tooltipData."
                                              value-field
                                              " | truncate: 25 }}"
                                              " Submission(s)")
                                         (str "{{ tooltipData."
                                              value-field
                                              " | truncate: 10 }}"))}}}}
     {:type "rule"
      :properties
      {:update
       {:x {:value rule-x-displacement}
        :y {:value rule-y-displacement}
        :stroke {:value tooltip-stroke-color}
        :y2 {:rule [{:predicate {:name "isTooltipVisible?"}
                     :value 0}
                    {:value rule-height}]}
        :strokeWidth {:value 1}}}}]))

(defn get-duration-chart-tooltip-text-marks
  [{:keys [label-field start-value end-value]}]
  (let [format-specifier (str " | number:'.3s' }}")
        tooltip-text-y-displacement 22
        label-text-x-displacement 10
        rule-x-displacement 160]
    [{:type "text"
      :properties {:enter {:align {:value "left"}
                           :fill {:value "#444"}}
                   :update {:y {:value tooltip-text-y-displacement}
                            :x {:value label-text-x-displacement}
                            :fillOpacity
                            {:rule [{:predicate
                                     {:name "isTooltipVisible?"}
                                     :value 0}
                                    {:value 1}]}
                            :text
                            {:template
                             (str "{{ tooltipData."
                                  label-field " | truncate:8 }}"
                                  " Submission(s) between "
                                  "{{ tooltipData."
                                  start-value format-specifier
                                  " and "
                                  "{{ tooltipData."
                                  end-value format-specifier " min")}}}}
     {:type "rule"
      :properties
      {:update
       {:x {:value rule-x-displacement}
        :stroke {:value tooltip-stroke-color}
        :y2 {:rule [{:predicate {:name "isTooltipVisible?"}
                     :value 0}
                    {:value tooltip-height}]}
        :strokeWidth {:value 1}}}}]))

(defn set-status-text
  [spec status-text chart-height]
  (update spec
          :marks
          (fn [marks]
            (if status-text
              (conj marks
                    {:type "text"
                     :name "status-text"
                     :properties
                     {:enter
                      {:fill {:value "#999"}
                       :text {:value status-text}
                       :y {:offset 40
                           :value chart-height}}}})
              marks))))

(defn chart-title-text
  [spec chart-text chart-height]
  (update spec
          :marks
          (fn [marks]
            (if chart-text
              (conj marks
                    {:type "text"
                     :name "chart-text"
                     :properties
                     {:enter
                      {:fill {:value "#292929"}
                       :text {:value chart-text}
                       :font {:value "sans-serif"}
                       :fontSize {:value 18}
                       :y {:offset (* -1 (+ 30 chart-height))
                           :value chart-height}}}})
              marks))))

(defn show-percent-sign-on-tooltip
  [spec tooltip-mark-index & {:keys [is-grouped-stacked-chart?]}]
  (assoc-in spec
            [:marks tooltip-mark-index :marks 1 :properties :update :text]
            {:rule [{:predicate {:name "isTooltipVisible?"}}
                    {:template (if is-grouped-stacked-chart?
                                 "{{tooltipData.sum_y}} %"
                                 "{{tooltipData.frequency}} %")}]}))

(defn set-tooltip-bounds
  [{:keys [signals] :as spec}
   & {:keys [visualization-height visualization-width]}]
  (if signals
    (let [signal-index (cond
                         visualization-height 2
                         visualization-width 1)
          event-fn-literal (cond
                             visualization-height "eventY()"
                             visualization-width "eventX()")]
      (assoc-in spec [:signals signal-index :streams 0 :expr]
                (str "min("
                     (when visualization-width
                       (- visualization-width
                          tooltip-width))
                     (when visualization-height
                       (- visualization-height
                          tooltip-height))
                     ","
                     event-fn-literal
                     ")")))
    spec))

(def x-axis-index 0)
(def y-axis-index 1)

(defn truncate-y-axis-labels
  [spec maximum-text-length]
  (if (pos? maximum-text-length)
    (assoc-in spec
              [:axes y-axis-index
               :properties :labels
               :text :template]
              (str "{{datum.data | truncate:"
                   maximum-text-length
                   "}}"))
    spec))

(defn update-x-axis-tick-labels
  [spec x-axis-tick-label-format]
  (cond-> spec
    x-axis-tick-label-format
    (assoc-in [:axes 0 :properties]
              {:labels
               {:text
                {:template
                 (str
                  "{{ datum.data | "
                  (condp = x-axis-tick-label-format
                    :abbreviate-numbers "number:'.3s'"
                    :time-based "number: '.3s'")
                  "}}")}}})))

(defn custom-chart-tooltips
  [spec {:keys [duration-chart-tooltips submitted-by-tooltips]}]
  (let [duration-tooltip-y-offset 5
        duration-tooltip-x-offset -50
        duration-tooltip-width 265
        submitted-by-tooltip-width 300]
    (update spec
            :marks
            (fn [marks]
              (if (or duration-chart-tooltips submitted-by-tooltips)
                (conj marks
                      {:type "group"
                       :properties
                       {:enter {:align {:value "center"}
                                :fill {:value "#fff"}}
                        :update {:y      {:signal "tooltipY"
                                          :offset
                                          (cond
                                            duration-chart-tooltips
                                            duration-tooltip-y-offset
                                            submitted-by-tooltips
                                            tooltip-offset)}
                                 :x      {:signal "tooltipX"
                                          :offset
                                          (cond
                                            duration-chart-tooltips
                                            duration-tooltip-x-offset
                                            submitted-by-tooltips
                                            tooltip-offset)}
                                 :height {:rule [{:predicate
                                                  {:name
                                                   "isTooltipVisible?"}
                                                  :value 0}
                                                 {:value tooltip-height}]}
                                 :width   {:value
                                           (cond
                                             duration-chart-tooltips
                                             duration-tooltip-width
                                             submitted-by-tooltips
                                             submitted-by-tooltip-width)}
                                 :fillOpacity {:value 1}
                                 :stroke      {:value tooltip-stroke-color}
                                 :strokeWidth
                                 {:rule
                                  [{:predicate {:name "isTooltipVisible?"}
                                    :value 0}
                                   {:value 1}]}}}
                       :marks
                       (cond
                         duration-chart-tooltips
                         (get-duration-chart-tooltip-text-marks
                          {:label-field  "count"
                           :start-value  "bin_start"
                           :end-value    "bin_end"})
                         submitted-by-tooltips
                         (get-tooltip-text-marks
                          {:label-field "category"
                           :value-field "frequency"
                           :submitted-by-chart? true}))})
                marks)))))
