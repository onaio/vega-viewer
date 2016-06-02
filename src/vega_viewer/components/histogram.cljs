(ns vega-viewer.components.histogram
  (:require [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]
            [vega-viewer.components.vega-viewer :refer [vega-viewer]]
            [vega-viewer.vega.specs.histogram
             :refer [generate-histogram-chart-vega-spec]]))

(defn histogram-chart
  [cursor owner {:as opts
                 :keys [abbreviate-x-axis-tick-labels?]}]
  (reify
    om/IRender
    (render [_]
      (let [vega-spec
            (generate-histogram-chart-vega-spec cursor
                                                :abbreviate-x-axis-tick-labels?
                                                abbreviate-x-axis-tick-labels?)]
        (html (om/build vega-viewer vega-spec {:opts opts}))))))
