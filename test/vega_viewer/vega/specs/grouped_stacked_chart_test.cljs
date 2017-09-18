(ns vega-viewer.vega.specs.grouped-stacked-chart-test
  (:require-macros [cljs.test :refer [deftest testing]])
  (:require [vega-viewer.vega.specs-test-utils
             :refer [test-validity]]
            [vega-viewer.vega.specs.grouped-stacked-chart
             :refer [generate-grouped-stacked-chart-vega-spec]]))

(deftest grouped-stacked-chart-spec-is-vega-compliant
  (let [data  [{"u" "Acceptable" "x" "male" "z" "Amhara" "y" 27}
               {"u" "Poor" "x" "female" "z" "Oromiya" "y" 16}
               {"u" "Boderline" "x" "female" "z" "Nairobi","y" 4}
               {"u" "Poor" "x" "male" "z" "Mombasa" "y" 100}
               {"u" "Acceptable" "x" "male" "z" "Kisumu" "y" 66}]]
    (testing "generated grouped bar chart spec is vega compliant"
      (let [spec (generate-grouped-stacked-chart-vega-spec {:data data})]
        (test-validity spec)))))
