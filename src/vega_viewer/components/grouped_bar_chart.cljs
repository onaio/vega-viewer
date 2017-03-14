(ns vega-viewer.components.grouped-bar-chart
  (:require [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]
            [vega-viewer.components.vega-viewer :refer [vega-viewer]]
            [vega-viewer.vega.specs.grouped-bar-chart
             :refer [generate-grouped-bar-chart-vega-spec]]))

(defn grouped-bar-chart
  [cursor owner opts]
  (reify
    om/IRender
    (render [_]
      (let [vega-spec
            (generate-grouped-bar-chart-vega-spec cursor)]
        (html (om/build vega-viewer vega-spec {:opts opts}))))))
