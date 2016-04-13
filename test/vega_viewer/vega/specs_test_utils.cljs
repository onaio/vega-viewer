(ns vega-viewer.vega.specs-test-utils
  (:require-macros [vega-viewer.macros :refer [read-file]])
  (:require [cljs.test :refer-macros [deftest testing is]]
            [cljsjs.tv4]))

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
