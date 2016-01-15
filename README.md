# Intro

vega-viewer is an experimental [Om](https://github.com/omcljs/om) component that
accepts a [vega](https://github.com/vega/vega) specification parsed into a
Clojure data structure and renders the resulting chart.

## Usage

```clojure
(def vega-spec
  (atom {:width 200
         :height 200
         :data [{:name "fruits"
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
         :marks [{:from {:data "fruits"}
                  :type "rect"
                  :properties {:enter {:x {:scale "category" :field "name"}
                                       :width {:scale "category"
                                               :band true
                                               :offset -1}
                                       :y {:scale "frequency" :field "number"}
                                       :y2 {:scale "frequency" :value 0}}
                               :update {:fill {:value "steelblue"}}}}]}))

(om/build vega-viewer vega-spec)
```

## Development

Run `lein-figwheel` in the project root to explore the included devcard

## License

Copyright © 2016 Okal Otieno & Ona

Distributed under the [MIT License](https://opensource.org/licenses/MIT).
