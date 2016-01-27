(ns vega-viewer.vega.specs-test
  (:require-macros [vega-viewer.macros :refer [read-file]])
  (:require [cljs.test :refer-macros [deftest testing is]]
            [cljsjs.tv4]
            [vega-viewer.vega.specs
             :refer [generate-histogram-chart-vega-spec
                     generate-horizontal-bar-chart-vega-spec]]))

(def json js/JSON)

(def vega-schema (-> "test/fixtures/vega-schema.json"
                     read-file
                     json.parse))

(defn generate-error-report
  [spec]
  (when-not js/tv4.valid
    {:message js/tv4.error.message
     :code js/tv4.error.code
     :data-path js/tv4.error.dataPath
     :spec spec
     :sub-errors js/tv4.error.subErrors}))

(deftest histogram-spec-is-vega-compliant
  (testing "generated histogram spec is vega compliant"
    (let [data [0 0 1 2 3 3 4 4]
          spec (generate-histogram-chart-vega-spec data)
          spec-as-json (clj->js spec)
          valid? (.validate js/tv4
                            spec-as-json
                            vega-schema
                            true
                            true)]
      (is valid? (generate-error-report spec)))))

(deftest horizontal-bar-chart-spec-is-vega-compliant
  (testing "generated category chart spec is vega compliant"
    (let [data
          [{"category" "something" "frequency" 2}
           {"category" "something-else" "frequency" 3}]
          spec (generate-horizontal-bar-chart-vega-spec {:data data})
          spec-as-json (clj->js spec)
          valid? (.validate js/tv4
                            spec-as-json
                            vega-schema
                            true
                            true)]
      (is valid? (generate-error-report spec)))))
