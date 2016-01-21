(ns vega-viewer.components.histogram
(:require [om.core :as om :include-macros true]
  [sablono.core :refer-macros [html]]
  [vega-viewer.components.vega-viewer :refer [vega-viewer]]
  [vega-viewer.vega.specs
   :refer [generate-histogram-chart-vega-spec]]))

(defn histogram-chart
  [data owner]
  (reify
    om/IRender
    (render [_]
      (let [vega-spec (generate-histogram-chart-vega-spec data)]
        (html (om/build vega-viewer vega-spec))))))
