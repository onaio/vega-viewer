(ns vega-viewer.vega.specs.utils
  (:require [vega-viewer.vega.specs.constants
             :refer [band-width tooltip-height tooltip-stroke-color]]))

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
  [spec status-text number-of-entries]
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
                           :value (* band-width number-of-entries)}}}})
              marks))))

(defn show-percent-sign-on-tooltip
  [spec tooltip-mark-index]
  (assoc-in spec
            [:marks tooltip-mark-index :marks 1 :properties :update :text]
            {:rule [{:predicate {:name "isTooltipVisible?"}}
                    {:template "{{tooltipData.frequency}} %"}]}))
