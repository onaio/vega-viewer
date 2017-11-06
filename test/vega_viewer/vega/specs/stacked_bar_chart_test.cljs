(ns vega-viewer.vega.specs.stacked-bar-chart-test
  (:require-macros [cljs.test :refer [deftest testing]])
  (:require [vega-viewer.vega.specs-test-utils
             :refer [test-validity]]
            [vega-viewer.vega.specs.stacked-bar-chart
             :refer [generate-stacked-bar-chart-vega-spec]]))

(deftest stacked-bar-chart-spec-is-vega-compliant
  (let [data  [{"x" "bangaladesh" "y" 292  "z" "Cynthia Akinyi"}
               {"x" "bangaladesh" "y" 303 "z" "Fatuma Mwakusema"}
               {"x" "bangaladesh" "y" 193 "z" "Gibson Kea"}
               {"x" "moroto" "y" 183 "z" "Angeline Kache"}
               {"x" "moroto"  "y" 231 "z" "Donald Akutoi"}]]
    (testing "generated stacked bar chart spec is vega compliant"
      (let [spec (generate-stacked-bar-chart-vega-spec {:data data})]
        (test-validity spec)))))
