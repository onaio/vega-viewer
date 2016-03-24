(ns vega-viewer.components.vega-viewer
  (:require [cljsjs.vega]
            [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]))

(def chart-width-proportion 0.8)

(defn set-resize-handler
  "Add a window.resize event handler that changes the :container-width state of
   a vega-viewer instance when the window is resized"
  [owner]
  (let [resize-handler
        (.addEventListener js/window "resize"
                           (fn [event]
                             (om/set-state!
                              owner
                              :container-width
                              (.-clientWidth
                               (om/get-node owner "vega-container")))))]
    (om/set-state! owner :resize-handler resize-handler)))

(defn render-vega-visualization
  "Render a Vega specification in the supplied container"
  [spec container responsive?]
  (let [container-width (.-clientWidth container)
        spec-as-js (clj->js (if responsive?
                                   (assoc spec
                                          :width
                                          (* container-width
                                             chart-width-proportion))
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
    (did-update [_ _ {previous-container-width :container-width}]
      (let [{:keys [container-width]} (om/get-state owner)]
        (when (not= previous-container-width container-width)
          (render-vega-visualization vega-spec
                                     (om/get-node owner "vega-container")
                                     responsive?))))

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
