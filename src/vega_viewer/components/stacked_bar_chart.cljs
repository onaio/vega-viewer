(ns vega-viewer.components.stacked-bar-chart
  (:require [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]
            [vega-viewer.components.vega-viewer :refer [vega-viewer]]
            [vega-viewer.vega.specs.stacked-bar-chart
             :refer [generate-stacked-bar-chart-vega-spec]]))

(defn stacked-bar-chart
  [cursor owner {:keys [user-defined-palette]
                 :as opts}]
  (reify
    om/IRender
    (render [_]
      (let [vega-spec
            (generate-stacked-bar-chart-vega-spec cursor
                                                  :user-defined-palette
                                                  user-defined-palette)]
        (html (om/build vega-viewer vega-spec {:opts opts}))))))
