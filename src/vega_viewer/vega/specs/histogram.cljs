(ns vega-viewer.vega.specs.histogram
  (:require [vega-viewer.vega.specs.constants
             :refer [bar-color default-bin-size default-chart-width
                     histogram-height]]))

(def histogram-spec-template
  {:data [{:name "entries"
           :values []
           :transform [{:type "bin"
                        :field "value"
                        :maxbins default-bin-size
                        :minstep 1}]}
          {:name "summary"
           :source "entries"
           :transform [{:type "aggregate"
                        :groupby ["bin_start" "bin_end"]
                        :summarize {:* ["count"]}}]}]
   :scales [{:name "x"
             :type "linear"
             :range "width"
             :domain {:data "summary" :field ["bin_start" "bin_end"]}}
            {:name "y"
             :type "linear"
             :domain {:data "summary" :field "count" :sort true}
             :range "height"}]
   :axes [{:type "x" :scale "x" :ticks 10 :layer "back"}
          {:type "y" :scale "y" :layer "back"}]
   :marks [{:type "rect"
            :properties {:enter
                         {:x {:scale "x" :field "bin_start" :offset 1}
                          :x2 {:scale "x" :field "bin_end"}
                          :y {:scale "y" :field "count"}
                          :y2 {:field {:group "height"}}
                          :fill {:value bar-color}}}
            :from {:data "summary"}}
           {:type "text"
            :from {:mark "rect"}
            :properties {:enter {:align {:value "center"}
                                 :x {:field "x"}
                                 :y {:field "y"}
                                 :dx {:field "width" :mult 0.5}
                                 :dy {:value -7}
                                 :fill {:value "black"}
                                 :text {:field "datum.count"}}}}]})

(defn generate-histogram-chart-vega-spec
  [{values :data :keys [height width]} & {:keys [responsive?]}]
  (-> histogram-spec-template
      (assoc-in [:data 0 :values] (map (fn [value]
                                         {"value" value})
                                       values))
      (assoc-in [:height] (or height histogram-height))
      (assoc-in [:width] (or width
                             (and (not responsive?)
                                  default-chart-width)))))
