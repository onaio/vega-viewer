(ns vega-viewer.components.histogram
  (:require [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]
            [vega-viewer.components.vega-viewer :refer [vega-viewer]]
            [vega-viewer.vega.specs.histogram
             :refer [generate-histogram-chart-vega-spec]]))

(defn histogram-chart
  [cursor owner {:as opts
                 :keys [x-axis-tick-label-format
                        x-axis-title
                        y-axis-title
                        duration-chart-tooltips]}]
  (reify
    om/IRender
    (render [_]
      (let [vega-spec
            (generate-histogram-chart-vega-spec cursor
                                                :x-axis-tick-label-format
                                                x-axis-tick-label-format
                                                :x-axis-title x-axis-title
                                                :y-axis-title y-axis-title
                                                :duration-chart-tooltips
                                                duration-chart-tooltips)]
        (html (om/build vega-viewer vega-spec {:opts opts}))))))
