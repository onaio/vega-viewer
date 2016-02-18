(ns vega-viewer.vega.specs)

(def bar-height 27)
(def bar-height-offset -2)
(def band-width (+ 3 bar-height))
(def y-offset 3)
(def histogram-height 200)
(def default-bin-size 15)
(def default-chart-width 600)
(def bar-color "#24B3B5")

(def palette ["#24B3B5"
              "#F05C3E"
              "#FFCD33"
              "#AEAEAF"
              "#2C435E"
              "#AD3627"
              "#6BCECE"
              "#EA843F"
              "#68932A"
              "#722625"
              "#B7A65E"
              "#0F9BB2"
              "#7EC451"
              "#9467BD"
              "#DBDB8D"
              "#E377C2"
              "#1F77B4"
              "#F7B6D2"
              "#787773"
              "#C5B0D5"])

(def tooltip-height 36)
(def tooltip-width 200)
(def tooltip-offset 5)
(def tooltip-opacity 0.95)
(def tooltip-stroke-color "#bbb")

(defn get-tooltip-text-marks
  [label-field value-field]
  (let [label-text-x-displacement 10
        value-text-x-displacement 180
        rule-x-displacement 160
        rule-y-displacement 0
        rule-height tooltip-height
        tooltip-text-y-displacement 22
        tooltip-text-color "#444"]
    [{:type "text"
      :properties {:enter {:align {:value "left"}
                           :fill {:value tooltip-text-color}}
                   :update {:y {:value tooltip-text-y-displacement}
                            :x {:value label-text-x-displacement}
                            :text {:signal (str "tooltipData." label-field)}}}}
     {:type "text"
      :properties {:enter {:align {:value "center"}
                           :fill {:value tooltip-text-color}}
                   :update {:y {:value tooltip-text-y-displacement}
                            :x {:value value-text-x-displacement}
                            :text
                            {:signal (str "tooltipData." value-field)}}}}
     {:type "rule"
      :properties
      {:update
       {:x {:value rule-x-displacement}
        :y {:value rule-y-displacement}
        :stroke {:value tooltip-stroke-color}
        :y2 {:rule [{:predicate {:name "isTooltipVisible?"}
                     :value 0}
                    {:value rule-height}]}
        :strokeWidth {:value 1}}}}]))

(def horizontal-bar-chart-spec-template
  {:data [{:name "entries"
           :values []}]
   :scales [{:name "category"
             :type "ordinal"
             :domain {:data "entries" :field "category"}
             :bandWidth band-width
             :range "height"}
            {:name "frequency"
             :type "linear"
             :range "width"
             :round true
             :domain {:data "entries" :field "frequency"}}]
   :axes [{:scale "frequency"
           :type "x"
           :layer "back"
           :properties {:labels {:text {:template "{{datum.data}}"}}}}
          {:scale "category" :type "y" :layer "back"}]
   :marks [{:name "bars"
            :from {:data "entries"}
            :type "rect"
            :properties {:enter {:y {:scale "category"
                                     :field "category"
                                     :offset y-offset}
                                 :height {:value bar-height
                                          :offset bar-height-offset}
                                 :x {:scale "frequency" :field "frequency"}
                                 :x2 {:value 0}}
                         :update {:fill {:value bar-color}}}}
           {:type "text"
            :from {:data "entries"}
            :properties {:enter {:x {:scale "frequency"
                                     :field "frequency"
                                     :offset 3}
                                 :y {:scale "category"
                                     :field "category"
                                     :offset (/ bar-height 2)}
                                 :dy {:field "height" :mult 0.5}
                                 :fill {:value "black"}
                                 :baseline {:value "middle"}
                                 :text {:field "frequency"}}}}
           {:type "group"
            :properties {:enter {:align {:value "center"}
                                 :fill {:value "#fff"}}
                         :update {:y {:signal "tooltipY"
                                      :offset tooltip-offset}
                                  :x {:signal "tooltipX"
                                      :offset tooltip-offset}
                                  :height {:rule [{:predicate
                                                   {:name "isTooltipVisible?"}
                                                   :value 0}
                                                  {:value tooltip-height}]}
                                  :width {:value tooltip-width}
                                  :fillOpacity {:value tooltip-opacity}
                                  :stroke {:value tooltip-stroke-color}
                                  :strokeWidth
                                  {:rule
                                   [{:predicate {:name "isTooltipVisible?"}
                                     :value 0}
                                    {:value 1}]}}}
            :marks (get-tooltip-text-marks "category" "frequency")}]
   :signals [{:name "tooltipData"
              :init {}
              :streams [{:type "rect:mouseover" :expr "datum"}
                        {:type "rect:mouseout" :expr "{}"}]}
             {:name "tooltipX"
              :init {}
              :streams [{:type "mousemove" :expr "eventX()"}]}
             {:name "tooltipY"
              :init {}
              :streams [{:type "mousemove" :expr "eventY()"}]}]
   :predicates [{:name "isTooltipVisible?"
                 :type "==",
                 :operands [{:signal "tooltipData._id"}
                            {:arg "id"}]}]})

(def histogram-spec-template
  {:data [{:name "entries"
           :values []
           :transform [{:type "bin"
                        :field "value"
                        :maxbins default-bin-size
                        :minstep 1}]}
          {:name "summary"
           :source "entries"
           :transform [{:type "aggregate"
                        :groupby ["bin_start" "bin_end"]
                        :summarize {:* ["count"]}}]}]
   :scales [{:name "x"
             :type "linear"
             :range "width"
             :domain {:data "summary" :field ["bin_start" "bin_end"]}}
            {:name "y"
             :type "linear"
             :domain {:data "summary" :field "count" :sort true}
             :range "height"}]
   :axes [{:type "x" :scale "x" :ticks 10 :layer "back"}
          {:type "y" :scale "y" :layer "back"}]
   :marks [{:type "rect"
            :properties {:enter
                         {:x {:scale "x" :field "bin_start" :offset 1}
                          :x2 {:scale "x" :field "bin_end"}
                          :y {:scale "y" :field "count"}
                          :y2 {:field {:group "height"}}
                          :fill {:value bar-color}}}
            :from {:data "summary"}}
           {:type "text"
            :from {:mark "rect"}
            :properties {:enter {:align {:value "center"}
                                 :x {:field "x"}
                                 :y {:field "y"}
                                 :dx {:field "width" :mult 0.5}
                                 :dy {:value -7}
                                 :fill {:value "black"}
                                 :text {:field "datum.count"}}}}]})

(def stacked-horizontal-bar-chart-spec-template
  {:data [{:name "table"}
          {:name "stats"
           :source "table"
           :transform
           [{:type "aggregate"
             :groupby ["category"]
             :summarize [{:field "frequency"
                          :ops ["sum"]}]}]}]
   :scales [{:name "y"
             :type "ordinal"
             :range "height"
             :bandWidth band-width
             :domain {:data "table"
                      :field "category"}}
            {:name "x"
             :type "linear"
             :range "width"
             :nice true
             :domain {:data "stats"
                      :field "sum_frequency"}}
            {:name "color"
             :type "ordinal"
             :range palette
             :domain {:data "table"
                      :field "group"
                      :sort true}}]
   :axes [{:type "y"
           :scale "y"
           :layer "back"}
          {:type "x"
           :scale "x"
           :layer "back"}]
   :legends [{:fill "color"
              :properties {:labels
                           {:text
                            {:template "{{ datum.data | truncate:25 }}"}}
                           :symbols {:shape {:value "square"}
                                     :stroke {:value "transparent"}}}}]
   :marks [{:type "rect"
            :from {:data "table"
                   :transform [{:type "stack"
                                :groupby ["category"]
                                :sortby ["group"]
                                :field "frequency"}]}
            :properties {:enter {:y {:scale "y"
                                     :field "category"
                                     :offset y-offset}
                                 :height {:value bar-height
                                          :offset bar-height-offset}
                                 :x {:scale "x"
                                     :field "layout_end"}
                                 :x2 {:scale "x"
                                      :field "layout_start"}
                                 :fill {:scale "color"
                                        :field "group"}}
                         :update {:fillOpacity {:value 1}}
                         :hover {:fillOpacity {:value 0.9}}}}
           {:type "group"
            :properties {:enter {:align {:value "center"}
                                 :fill {:value "#fff"}}
                         :update {:y {:signal "tooltipY"
                                      :offset tooltip-offset}
                                  :x {:signal "tooltipX"
                                      :offset tooltip-offset}
                                  :height {:rule
                                           [{:predicate
                                             {:name "isTooltipVisible?"}
                                             :value 0}
                                            {:value tooltip-height}]}
                                  :width {:value tooltip-width}
                                  :fillOpacity {:value tooltip-opacity}
                                  :stroke {:value tooltip-stroke-color}
                                  :strokeWidth
                                  {:rule
                                   [{:predicate {:name "isTooltipVisible?"}
                                     :value 0}
                                    {:value 1}]}}}
            :marks (get-tooltip-text-marks "group" "frequency")}]
   :signals [{:name "tooltipData"
              :init {}
              :streams [{:type "rect:mouseover" :expr "datum"}
                        {:type "rect:mouseout" :expr "{}"}]}
             {:name "tooltipX"
              :streams [{:type "mousemove"
                         :expr "eventX()"}]}
             {:name "tooltipY"
              :streams [{:type "mousemove"
                         :expr "eventY()"}]}]
   :predicates [{:name "isTooltipVisible?"
                 :type "=="
                 :operands [{:signal "tooltipData._id"} {:arg "id"}]}]})

(defn show-percent-sign-on-tooltip
  [spec tooltip-mark-index]
  (assoc-in spec
            [:marks tooltip-mark-index :marks 1 :properties :update :text]
            {:rule [{:predicate {:name "isTooltipVisible?"}}
                    {:template "{{tooltipData.frequency}} %"}]}))

(defn generate-horizontal-bar-chart-vega-spec
  [{:keys [data height width show-count-or-percent?]}]
  (let [count-or-percent #(if (= show-count-or-percent? :percent)
                            (-> %
                                (assoc-in [:axes 0 :properties :labels :text
                                           :template]
                                          "{{datum.data}} %")
                                (assoc-in [:marks 1 :properties :enter :text]
                                          {:template "{{datum.frequency}}%"})
                                (show-percent-sign-on-tooltip 2))
                            %)]
    (-> horizontal-bar-chart-spec-template
        (assoc-in [:data 0 :values] data)
        (assoc-in [:height] (or height
                                (* (count data) band-width)))
        (assoc-in [:width] (or width default-chart-width))
        count-or-percent)))

(defn generate-histogram-chart-vega-spec
  [{values :data :keys [height width]}]
  (-> histogram-spec-template
      (assoc-in [:data 0 :values] (map (fn [value]
                                         {"value" value})
                                       values))
      (assoc-in [:height] (or height histogram-height))
      (assoc-in [:width] (or width default-chart-width))))

(defn generate-stacked-horizontal-bar-chart-vega-spec
  [{:keys [data height width show-count-or-percent?]}]
  (let [count-or-percent #(if (= show-count-or-percent? :percent)
                            (->
                             %
                             (assoc-in [:marks 0 :from :transform 0 :offset]
                                       "normalize")
                             (assoc-in [:scales 1 :domainMax] 1)
                             (assoc-in [:axes 1 :format] "%")
                             (assoc-in [:marks 1 :marks 1 :properties
                                        :update :text]
                                       {:rule
                                        [{:predicate
                                          {:name "tooltipVisible"}}
                                         {:template
                                          "{{tooltipData.frequency}}%"}]})
                             (show-percent-sign-on-tooltip 1))
                            %)]
    (-> stacked-horizontal-bar-chart-spec-template
        (assoc-in [:data 0 :values] data)
        (assoc-in [:height] (or height
                                (->> data
                                     (map #(get-in % ["category"]))
                                     (set)
                                     (count)
                                     (* band-width))))
        (assoc-in [:width] (or width default-chart-width))
        count-or-percent)))
