# Intro

vega-viewer is an experimental Om component that accepts a vega specification
parsed into a Clojure data structure and renders the resulting chart.

## Usage

```clojure
(def vega-spec
     (atom {:data [{:name "fruits"
                    :values [{"name" "apple"
                              "number" 23}
                             {"name" "oranges"
                              "number" 42}]}]
            :scales [{:name "category"
                      :type "ordinal"
                      :domain {:data "fruits" :field "name"}
                      :range "width"}
                     {:name "frequency"
                      :type "linear"
                      :range "height"
                      :domain {:data "fruits" :field "number"}}]
            :axes [{:scale "frequency" :type "y"}
                   {:scale "category" :type "x"}]
            :marks [{:type "rect"
                     :properties {:enter {:fill "steelblue"
                                          :x {:scale "category" field "name"}
                                          :width {:scale "category" :band true :offset -1}
                                          :y {:scale "frequency" :field "number"}
                                          :y2 {:scale "frequency" :value 0}}}}]))

(om/build vega-viewer vega-spec)
```
