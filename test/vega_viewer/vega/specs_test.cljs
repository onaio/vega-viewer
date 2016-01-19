(ns vega-viewer.vega.specs-test
  (:require-macros [vega-viewer.macros :refer [read-file]])
  (:require [cljs.test :refer-macros [deftest is]]
            [cljsjs.vega]
            [cljsjs.llexus-validate]
            [vega-viewer.vega.specs
             :refer [generate-horizontal-bar-chart-vega-spec]]))

(deftest spec-is-vega-compliant
  (let [data [{"category" "something" "frequency" 2}
              {"category" "something-else" "frequency" 3}]
        spec (generate-horizontal-bar-chart-vega-spec data)
        vega-schema (read-file "test/fixtures/vega-schema.json")]
    (is true)))
