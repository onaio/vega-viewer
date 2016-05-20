(ns vega-viewer.components.vega-viewer
  (:require [cljsjs.vega]
            [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]
            [vega-viewer.vega.specs.utils :refer [set-tooltip-bounds]]))

(def chart-width-proportion 0.8)

(defn set-resize-handler
  "Add a window.resize event handler that changes the :container-width state of
   a vega-viewer instance when the window is resized"
  [owner]
  (let [container (om/get-node owner "vega-container")
        resize-handler
        (.addEventListener js/window "resize"
                           (fn [event]
                             (om/set-state!
                              owner
                              :container-width
                              (.-clientWidth container))))]
    (om/set-state! owner :resize-handler resize-handler)))

(defn- update-width-and-tooltip-bounds
  [spec width]
  (-> spec
      (assoc :width
             (* width
                chart-width-proportion))
      (set-tooltip-bounds :visualization-width (* width
                                                  chart-width-proportion))))

(defn render-vega-visualization
  "Render a Vega specification in the supplied container"
  [spec container responsive?]
  (let [container-width (.-clientWidth container)
        spec-as-js (clj->js (if responsive?
                              (update-width-and-tooltip-bounds spec
                                                               container-width)
                              spec))]
    (js/vg.parse.spec spec-as-js
                      (fn [chart]
                        (let [view (chart #js {:el container})]
                          (.update view))))))

(defn vega-viewer
  "Return an Om component that renders a Vega specification"
  [{:keys [width] :as vega-spec} owner {:keys [responsive?]}]
  (reify
    om/IWillUnmount
    (will-unmount [_]
      (let [{:keys [resize-handler]} (om/get-state owner)]
        (.removeEventListener js/window "resize" resize-handler)))

    om/IDidUpdate
    (did-update [_ _ _]
      (render-vega-visualization vega-spec
                                 (om/get-node owner "vega-container")
                                 responsive?))

    om/IDidMount
    (did-mount [_]
      (set-resize-handler owner)
      (render-vega-visualization vega-spec
                                 (om/get-node owner "vega-container")
                                 responsive?))

    om/IRender
    (render [_]
      (html
       [:div {:ref "vega-container"
              :style {:width "100%"}}]))))
