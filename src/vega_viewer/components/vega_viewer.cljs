(ns vega-viewer.components.vega-viewer
  (:require [cljsjs.vega]
            [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]))

(defn vega-viewer
  [vega-spec owner]
  (reify
    om/IDidUpdate
    (did-update [_ _ _]
      (let [vega-container (om/get-node owner "vega-container")
            vega-spec-as-js (clj->js @vega-spec)]
        (js/vg.parse.spec vega-spec-as-js
                          (fn [chart]
                            (let [view (chart #js {:el vega-container})]
                              (.update view))))))
    om/IDidMount
    (did-mount [_]
      (let [vega-container (om/get-node owner "vega-container")
            vega-spec-as-js (clj->js @vega-spec)]
        (js/vg.parse.spec vega-spec-as-js
                          (fn [chart]
                            (let [view (chart #js {:el vega-container})]
                              (.update view))))))
    om/IRender
    (render [_]
      (html
       [:div [:h1 "Vega Viewer"]
        [:div {:ref "vega-container"}]]))))
