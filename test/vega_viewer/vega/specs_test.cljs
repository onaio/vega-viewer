(ns vega-viewer.vega.specs-test
  (:require-macros [vega-viewer.macros :refer [read-file]])
  (:require [cljs.test :refer-macros [deftest testing is]]
            [cljsjs.tv4]
            [vega-viewer.vega.specs
             :refer [generate-histogram-chart-vega-spec
                     generate-horizontal-bar-chart-vega-spec
                     generate-stacked-horizontal-bar-chart-vega-spec]]))

(def json js/JSON)

(def vega-schema (-> "test/fixtures/vega-schema.json"
                     read-file
                     json.parse))

(defn generate-error-report
  "Generate a detailed message to show in case of test failures"
  [spec]
  (when-not js/tv4.valid
    {:message js/tv4.error.message
     :code js/tv4.error.code
     :data-path js/tv4.error.dataPath
     :spec spec
     :sub-errors js/tv4.error.subErrors}))

(defn valid?
  "Check the supplied spec against the Vega JSON Schema"
  [spec]
  (.validate js/tv4
             spec
             vega-schema
             true
             true))

(defn test-validity
  "Check if the supplied spec is valid. Generate an error report if not."
  [spec]
  (is (valid? (clj->js spec))
      (generate-error-report spec)))

(deftest histogram-spec-is-vega-compliant
  (testing "generated histogram spec is vega compliant"
    (let [data [0 0 1 2 3 3 4 4]
          spec (generate-histogram-chart-vega-spec data)]
      (test-validity spec))))

(deftest horizontal-bar-chart-spec-is-vega-compliant
  (testing "generated category chart spec is vega compliant"
    (let [data
          [{"category" "something" "frequency" 2}
           {"category" "something-else" "frequency" 3}]
          spec (generate-horizontal-bar-chart-vega-spec {:data data})]
      (test-validity spec))))

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
