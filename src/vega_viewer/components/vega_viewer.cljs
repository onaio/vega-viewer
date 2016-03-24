(ns vega-viewer.components.vega-viewer
  (:require [cljsjs.vega]
            [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]))

(defn set-resize-handler
  [owner]
  (let [resize-handler
        (.addEventListener js/window "resize"
                           (fn [event]
                             (om/set-state! owner
                                            :container-width
                                            (- (.-clientWidth (om/get-node owner "vega-container"))
                                               30))))]
    (om/set-state! owner :resize-handler resize-handler)))

(defn vega-viewer
  [{:keys [width] :as vega-spec} owner {:keys [responsive?]}]
  (reify
    om/IWillUnmount
    (will-unmount [_]
      (let [{:keys [resize-handler]} (om/get-state owner)]
        (.removeEventListener js/window "resize" resize-handler)))

    om/IDidUpdate
    (did-update [_ _ {previous-container-width :container-width}]
      (let [vega-container (om/get-node owner "vega-container")
            container-width (.-clientWidth vega-container)
            vega-spec-as-js (clj->js (if responsive?
                                       (assoc vega-spec
                                              :width
                                              (* container-width
                                                 0.8))
                                       vega-spec))]
        (when (not= previous-container-width container-width)
          (js/vg.parse.spec vega-spec-as-js
                            (fn [chart]
                              (let [view (chart #js {:el vega-container})]
                                (.update view)))))))

    om/IDidMount
    (did-mount [_]
      (set-resize-handler owner)
      (let [vega-container (om/get-node owner "vega-container")
            container-width (.-clientWidth vega-container)
            vega-spec-as-js (clj->js (if responsive?
                                       (assoc vega-spec
                                              :width
                                              (* container-width
                                                 0.8))
                                       vega-spec))]
        (js/vg.parse.spec vega-spec-as-js
                          (fn [chart]
                            (let [view (chart #js {:el vega-container})]
                              (.update view))))))
    om/IRender
    (render [_]
      (html
       [:div {:ref "vega-container"
              :style {:width "100%"}}]))))
