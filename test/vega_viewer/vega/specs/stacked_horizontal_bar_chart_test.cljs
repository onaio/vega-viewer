(ns vega-viewer.vega.specs.stacked-horizontal-bar-chart-test
  (:require-macros [cljs.test :refer [deftest is testing]])
  (:require [vega-viewer.vega.specs-test-utils
             :refer [test-validity]]
            [vega-viewer.vega.specs.stacked-horizontal-bar-chart
             :refer [generate-stacked-horizontal-bar-chart-vega-spec]]))

(deftest stacked-horizontal-bar-chart-spec-is-vega-compliant
  (let [data [{"category" "area1" "group" ["group1"] "frequency" 5}
              {"category" "area1" "group" ["group2"] "frequency" 4}
              {"category" "area2" "group" ["group1"] "frequency" 2}
              {"category" "area2" "group" ["group1"] "frequency" 1}
              {"category" "area3" "group" ["group2"] "frequency" 10}
              {"category" "area3" "group" ["group1"] "frequency" 4}
              {"category" "area3" "group" ["group1"] "frequency" 6}]]
    (testing "generated stacked horizontal bar chart spec is vega compliant"
      (let [spec (generate-stacked-horizontal-bar-chart-vega-spec {:data data})]
        (test-validity spec)))
    (testing "generated stacked horizontal bar chart with percent display is
              vega compliant"
      (let [spec (generate-stacked-horizontal-bar-chart-vega-spec
                  {:data data
                   :show-count-or-percent? true})]
        (test-validity spec)))
    (let [user-defined-palette ["blue" "red" "green" "aqua" "fuchsia" "black"
                                "pink"]
          spec (generate-stacked-horizontal-bar-chart-vega-spec
                {:data data}
                :user-defined-palette user-defined-palette)
          {[_ _ {palette :range}] :scales} spec]
      (testing "generated stacked horizontal-bar-chart-spec is vega compliant
                when using a user defined palette"
        (test-validity spec))
      (testing "generated spec contains user defined palette"
        (is (= user-defined-palette
               palette))))))
