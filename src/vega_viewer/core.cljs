(ns vega-viewer.core
  (:require
   [cljsjs.vega]
   [om.core :as om :include-macros true]
   [sablono.core :as sab :include-macros true])
  (:require-macros
   [devcards.core :as dc :refer [defcard deftest]]))

(enable-console-print!)

(def vega-spec
  (atom {:width 200
         :height 200
         :data [{:name "fruits"
                 :values [{"name" "apple"
                           "number" 23}
                          {"name" "oranges"
                           "number" 42}]}]
         :scales [{:name "category"
                   :type "ordinal"
                   :domain {:data "fruits" :field "name"}
                   :range "width"}
                  {:name "frequency"
                   :type "linear"
                   :range "height"
                   :domain {:data "fruits" :field "number"}}]
         :axes [{:scale "frequency" :type "y"}
                {:scale "category" :type "x"}]
         :marks [{:from {:data "fruits"}
                  :type "rect"
                  :properties {:enter {:x {:scale "category" :field "name"}
                                       :width {:scale "category" :band true :offset -1}
                                       :y {:scale "frequency" :field "number"}
                                       :y2 {:scale "frequency" :value 0}}
                               :update {:fill {:value "steelblue"}}}}]}))

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
    (render
        [_]
      (sab/html
       [:div [:h1 "Vega Viewer"]
        [:div {:ref "vega-container"}]]))))

(defcard vega-chart
  (om/build vega-viewer vega-spec))

(defn main []
  ;; conditionally start the app based on wether the #main-app-area
  ;; node is on the page
  (if-let [node (.getElementById js/document "main-app-area")]
    (js/React.render (sab/html [:div "This is working"]) node)))

(main)

;; remember to run lein figwheel and then browse to
;; http://localhost:3449/cards.html

