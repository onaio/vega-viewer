(ns vega-viewer.components.horizontal-bar-chart
  (:require [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]
            [vega-viewer.components.vega-viewer :refer [vega-viewer]]
            [vega-viewer.vega.specs
             :refer [generate-horizontal-bar-chart-vega-spec]]))

(defn horizontal-bar-chart
  [cursor owner]
  (reify
    om/IRender
    (render [_]
      (let [vega-spec (generate-horizontal-bar-chart-vega-spec cursor)]
        (html (om/build vega-viewer vega-spec))))))
