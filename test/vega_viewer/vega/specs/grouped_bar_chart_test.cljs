(ns vega-viewer.vega.specs.grouped-bar-chart-test
  (:require-macros [cljs.test :refer [deftest testing]])
  (:require [vega-viewer.vega.specs-test-utils
             :refer [test-validity]]
            [vega-viewer.vega.specs.grouped-bar-chart
             :refer [generate-grouped-bar-chart-vega-spec]]))

(deftest grouped-bar-chart-spec-is-vega-compliant
  (let [data  [{"x" 90 "y" 1261660  "z" "male"}
               {"x" 75 "y" 18789353 "z" "female"}
               {"x" 35 "y" 68778127 "z" "female"}
               {"x" 15 "y" 83318554 "z" "male"}
               {"x" 5  "y" 91319913 "z" "female"}]]
    (testing "generated grouped bar chart spec is vega compliant"
      (let [spec (generate-grouped-bar-chart-vega-spec {:data data})]
        (test-validity spec)))))
