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
           :values [{"x" "bangaladesh" "y" 292 "c" "Cynthia Akinyi"}
                    {"x" "bangaladesh" "y" 303 "c" "Fatuma Mwakusema"}
                    {"x" "bangaladesh" "y" 193 "c" "Gibson Kea"}
                    {"x" "bangaladesh" "y" 252 "c" "Grace Nyambu"}
                    {"x" "bangaladesh" "y" 342 "c" "Jane Kabui"}
                    {"x" "bangaladesh" "y" 216 "c" "Joseph Ondiso"}
                    {"x" "bangaladesh" "y" 260 "c" "Leonard Lector"}
                    {"x" "bangaladesh" "y" 153 "c" "Peris Wambui"}
                    {"x" "moroto" "y" 294 "c" "Andrew Mweteeli"}
                    {"x" "moroto" "y" 183 "c" "Angeline Kache"}
                    {"x" "moroto" "y" 231 "c" "Donald Akutoi"}
                    {"x" "moroto" "y" 188 "c" "Fauzia  Shivisi"}
                    {"x" "moroto" "y" 207 "c" "Kimm Nauli"}
                    {"x" "moroto" "y" 356 "c" "Penina Muthoki"}
                    {"x" "moroto" "y" 167 "c" "Teresia Wangeci"}]}
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
             :domain {:data "table" :field "c"}}]
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
                                :sortby ["c"]}]}
            :properties {:enter
                         {:x {:scale "x" :field "x"}
                          :width {:scale "x" :band true :offset -3}
                          :y {:scale "y" :field "layout_start"}
                          :y2 {:scale "y" :field "layout_end"}
                          :fill {:scale "color" :field "c"}}
                         :update
                         {:fillOpacity {:value 1}}
                         :hover
                         {:fillOpacity {:value 0.5}}}}]})

(defn generate-stacked-bar-chart-vega-spec
  [{:keys [data height width status-text chart-text
           maximum-y-axis-label-length]}
   & {:keys [responsive? user-defined-palette]}]
  (let [chart-height (min (or height (* (count data) band-width)) max-height)
        chart-width (or width
                        (and (not responsive?)
                             default-chart-width))]
    (-> stacked-bar-chart-spec-template
        ;; (assoc-in [:data 0 :values] data)
        (assoc :height chart-height :width chart-width)
        (assoc-in [:marks 0 :scales 2 :range] (or (seq user-defined-palette)
                                                  palette))
        (set-status-text status-text chart-height)
        (chart-title-text chart-text chart-height)
        (truncate-y-axis-labels maximum-y-axis-label-length))))
