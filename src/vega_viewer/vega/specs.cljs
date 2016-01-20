(ns vega-viewer.vega.specs)

(def bar-height 31)

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
                         :update {:fill {:value "steelblue"}}}}]})

(defn generate-horizontal-bar-chart-vega-spec
  [data]
  (let [tick-count (->> data
                        (map #(get-in % ["frequency"]))
                        (apply max))]
    (-> vega-spec-template
        (assoc-in [:data 0 :values] data)
        (assoc-in [:height] (* (count data) bar-height))
        (assoc-in [:axes 0 :ticks] tick-count))))
