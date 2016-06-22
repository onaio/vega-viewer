(ns vega-viewer.vega.specs.utils
  (:require [clojure.string :refer [join]]
            [vega-viewer.vega.specs.constants
             :refer [band-width tooltip-height tooltip-stroke-color
                     tooltip-width]]))

(defn get-tooltip-text-marks
  [label-field value-field]
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
      :properties {:enter {:align {:value "center"}
                           :fill {:value tooltip-text-color}}
                   :update {:y {:value tooltip-text-y-displacement}
                            :x {:value value-text-x-displacement}
                            :text
                            {:signal (str "tooltipData." value-field)}}}}
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

(defn show-percent-sign-on-tooltip
  [spec tooltip-mark-index]
  (assoc-in spec
            [:marks tooltip-mark-index :marks 1 :properties :update :text]
            {:rule [{:predicate {:name "isTooltipVisible?"}}
                    {:template "{{tooltipData.frequency}} %"}]}))

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
