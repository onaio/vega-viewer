(ns vega-viewer.vega.specs.horizontal-bar-chart-test
  (:require-macros [cljs.test :refer [deftest testing]])
  (:require [vega-viewer.vega.specs-test-utils
             :refer [test-validity]]
            [vega-viewer.vega.specs.horizontal-bar-chart
             :refer [generate-horizontal-bar-chart-vega-spec]]))

(deftest horizontal-bar-chart-spec-is-vega-compliant
  (testing "generated category chart spec is vega compliant"
    (let [data
          [{"category" "something" "frequency" 2}
           {"category" "something-else" "frequency" 3}]
          spec (generate-horizontal-bar-chart-vega-spec {:data data})]
      (test-validity spec))))
