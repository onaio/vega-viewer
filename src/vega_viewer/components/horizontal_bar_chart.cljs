(ns vega-viewer.components.horizontal-bar-chart
  (:require [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]
            [vega-viewer.components.vega-viewer :refer [vega-viewer]]
            [vega-viewer.vega.specs.horizontal-bar-chart
             :refer [generate-horizontal-bar-chart-vega-spec]]))

(defn horizontal-bar-chart
  "Return an Om component that renders a horizontal bar chart
   the cursor is of the form {:data `data` :height `height` :width `width`}
   `data` [required] is a vector of maps of the form
   {\"key\": <string>
    \"value\": <integer>}
   `height` is an integer representing the height of the generated chart
   `width` is an integer representing the height of the generated chart"
  [cursor owner {:as opts
                 :keys [x-axis-tick-label-format submitted-by-tooltips
                        user-defined-palette]}]
  (reify
    om/IRender
    (render [_]
      (let [vega-spec (generate-horizontal-bar-chart-vega-spec
                       cursor
                       :x-axis-tick-label-format
                       x-axis-tick-label-format
                       :submitted-by-tooltips
                       submitted-by-tooltips
                       :user-defined-palette
                       user-defined-palette)]
        (html (om/build vega-viewer vega-spec {:opts opts}))))))
