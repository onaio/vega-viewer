(ns vega-viewer.components.stacked-horizontal-bar-chart
  (:require [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]
            [vega-viewer.components.vega-viewer :refer [vega-viewer]]
            [vega-viewer.vega.specs
             :refer [generate-stacked-horizontal-bar-chart-vega-spec]]))

(defn stacked-horizontal-bar-chart
  [cursor owner opts]
  (reify
    om/IRender
    (render [_]
      (let [vega-spec (generate-stacked-horizontal-bar-chart-vega-spec cursor)]
        (html (om/build vega-viewer vega-spec {:opts opts}))))))
