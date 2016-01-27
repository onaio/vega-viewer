(ns vega-viewer.vega.specs)

(def bar-height 31)
(def histogram-height 200)
(def default-bin-size 15)

(def vega-spec-template
  {:data [{:name "entries"
           :values []}]
   :scales [{:name "category"
             :type "ordinal"
             :domain {:data "entries" :field "category"}
             :bandWidth bar-height
             :range "height"}
            {:name "frequency"
             :type "linear"
             :range "width"
             :round true
             :domain {:data "entries" :field "frequency"}}]
   :axes [{:scale "frequency" :type "x"}
          {:scale "category" :type "y"}]
   :marks [{:from {:data "entries"}
            :type "rect"
            :properties {:enter {:y {:scale "category" :field "category"}
                                 :height {:value 30
                                          :offset -1}
                                 :x {:scale "frequency" :field "frequency"}
                                 :x2 {:value 0}}
                         :update {:fill {:value "steelblue"}}}}
           {:type "text"
            :from {:mark "rect"}
            :properties {:enter {:x {:field "x2" :offset 1}
                                 :y {:field "y"}
                                 :dy {:field "height" :mult 0.5}
                                 :fill {:value "black"}
                                 :baseline {:value "middle"}
                                 :text {:field "datum.frequency"}}}}]})

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
   :height histogram-height
   :scales [{:name "x"
             :type "linear"
             :range "width"
             :domain {:data "summary" :field ["bin_start" "bin_end"]}}
            {:name "y"
             :type "linear"
             :domain {:data "summary" :field "count" :sort true}
             :range "height"}]
   :axes [{:type "x" :scale "x" :ticks 10}
          {:type "y" :scale "y"}]
   :marks [{:type "rect"
            :properties {:enter
                         {:x {:scale "x" :field "bin_start" :offset 1}
                          :x2 {:scale "x" :field "bin_end"}
                          :y {:scale "y" :field "count"}
                          :y2 {:field {:group "height"}}
                          :fill {:value "#4682b4"}}}
            :from {:data "summary"}}
           {:type "text"
            :from {:mark "rect"}
            :properties {:enter {:x {:field "x"}
                                 :y {:field "y"}
                                 :dy {:field "dy"}
                                 :fill {:value "black"}
                                 :baseline {:value "bottom"}
                                 :text {:field "datum.count"}}}}]})

(defn generate-horizontal-bar-chart-vega-spec
  [data]
  (-> vega-spec-template
      (assoc-in [:data 0 :values] data)
      (assoc-in [:height] (* (count data) bar-height))))

(defn generate-histogram-chart-vega-spec
  [values]
  (assoc-in histogram-spec-template
            [:data 0 :values] (map (fn [value]
                                     {"value" value})
                                   values)))
