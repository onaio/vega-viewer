(ns test-runner
  (:require [cljs.test
             :as test
             :refer-macros [run-tests]
             :refer [report successful?]]
            [vega-viewer.vega.specs-test]))

(enable-console-print!)

(defmethod report [::test/default :summary] [m]
  (println "\nRan" (:test m) "tests containing"
           (+ (:pass m) (:fail m) (:error m)) "assertions.")
  (println (:fail m) "failures," (:error m) "errors.")
  (aset js/window "test-failures" (+ (:fail m) (:error m))))

(defn runner []
  (if (successful?
       (run-tests
        (test/empty-env ::test/default)
        'vega-viewer.vega.specs-test))
    0
    1))
