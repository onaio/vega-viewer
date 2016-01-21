(ns vega-viewer.components.histogram
(:require [om.core :as om :include-macros true]
  [sablono.core :refer-macros [html]]
  [vega-viewer.components.vega-viewer :refer [vega-viewer]]
  [vega-viewer.vega.specs
   :refer [generate-histogram-chart-vega-spec]]))

(defn histogram-chart
  [{:keys [field_xpath data field_label]} owner]
  (reify
    om/IRender
    (render [_]
      (let [vega-spec (generate-histogram-chart-vega-spec data field_xpath
                                                          field_label)
            _ (.log js/console (.stringify js/JSON (clj->js vega-spec)))]
        (html (om/build vega-viewer vega-spec))))))
