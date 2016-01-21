(ns vega-viewer.vega.specs-test
  (:require-macros [vega-viewer.macros :refer [read-file]])
  (:require [cljs.test :refer-macros [deftest testing is]]
            [cljsjs.tv4]
            [vega-viewer.vega.specs
             :refer [generate-histogram-chart-vega-spec
                     generate-horizontal-bar-chart-vega-spec]]))

(deftest spec-is-vega-compliant

  (testing "generatated category chart spec is vega compliant"
    (let [data
          [{"category" "something" "frequency" 2}
           {"category" "something-else" "frequency" 3}]
          spec (generate-horizontal-bar-chart-vega-spec data)
          json js/JSON
          spec-as-json (-> spec
                           clj->js
                           json.stringify
                           json.parse)
          vega-schema (-> "test/fixtures/vega-schema.json"
                          read-file
                          json.parse)
          valid? (.validate js/tv4 spec-as-json vega-schema)
          error-report (when-not valid?
                         {:message js/tv4.error.message
                          :code js/tv4.error.code
                          :data-path js/tv4.error.dataPath
                          :spec spec
                          :spec-as-json (json.stringify spec-as-json)
                          :sub-errors js/tv4.error.subErrors})]
      (is valid? error-report)))

  (testing "generated histogram spec is vega compliant"
    (let [data [{:Q1_5_6 "0"} {:Q1_5_6 "0"} {:Q1_5_6 "1"} {:Q1_5_6 "2"}
                {:Q1_5_6 "3"} {:Q1_5_6 "3"} {:Q1_5_6 "4"} {:Q1_5_6 "4"}]])))
