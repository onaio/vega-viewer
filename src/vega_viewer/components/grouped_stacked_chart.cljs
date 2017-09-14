(ns vega-viewer.components.grouped-stacked-chart
  (:require [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]
            [vega-viewer.components.vega-viewer :refer [vega-viewer]]
            [vega-viewer.vega.specs.grouped-stacked-chart
             :refer [generate-grouped-stacked-chart-vega-spec]]))

(defn grouped-stacked-chart
  [cursor owner {:keys [user-defined-palette]
                 :as opts}]
  (reify
    om/IRender
    (render [_]
      (let [vega-spec
            (generate-grouped-stacked-chart-vega-spec cursor
                                                      :user-defined-palette
                                                      user-defined-palette)]
        (html (om/build vega-viewer vega-spec {:opts opts}))))))
