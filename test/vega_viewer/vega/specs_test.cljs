(ns vega-viewer.vega.specs-test
  (:require-macros [vega-viewer.macros :refer [read-file]])
  (:require [cljs.test :refer-macros [deftest is]]
            [cljsjs.tv4]
            [vega-viewer.vega.specs
             :refer [generate-horizontal-bar-chart-vega-spec]]))

(deftest spec-is-vega-compliant
  (let [data [{"category" "something" "frequency" 2}
              {"category" "something-else" "frequency" 3}]
        spec (generate-horizontal-bar-chart-vega-spec data)
        json js/JSON
        spec-as-json (-> spec
                         clj->js
                         json.stringify
                         json.parse)
        vega-schema (-> "test/fixtures/vega-schema.json"
                        read-file
                        js/JSON.parse)
        valid? (.validate js/tv4 spec-as-json vega-schema)
        error-report (when-not valid?
                       {:message js/tv4.error.message
                        :code js/tv4.error.code
                        :data-path js/tv4.error.dataPath
                        :spec spec
                        :spec-as-json (json.stringify spec-as-json)
                        :sub-errors js/tv4.error.subErrors})]
    (is valid? error-report)))
