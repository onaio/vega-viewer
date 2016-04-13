(ns vega-viewer.vega.specs.histogram-test
  (:require-macros [cljs.test :refer [deftest testing]])
  (:require [vega-viewer.vega.specs-test-utils
             :refer [test-validity]]
            [vega-viewer.vega.specs.histogram
             :refer [generate-histogram-chart-vega-spec]]))

(deftest histogram-spec-is-vega-compliant
  (testing "generated histogram spec is vega compliant"
    (let [data [0 0 1 2 3 3 4 4]
          spec (generate-histogram-chart-vega-spec data)]
      (test-validity spec))))
