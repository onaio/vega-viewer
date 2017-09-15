(defproject onaio/vega-viewer "0.8.6"
  :description "Om component that renders a vega chart from a spec"
  :url "https://github.com/onaio/vega-viewer"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.229"]
                 [cljsjs/tv4 "1.2.7-0"]
                 [devcards "0.2.2"]
                 [sablono "0.4.0"]
                 [org.omcljs/om "0.9.0"]
                 [cljsjs/vega "2.6.0-0"]]
  :plugins [[lein-auto "0.1.2"]
            [lein-bikeshed-ona "0.2.1"]
            [lein-cljfmt "0.3.0"]
            [lein-cljsbuild "1.1.2"]
            [lein-figwheel "0.5.0-1"]
            [lein-kibit "0.1.2"]]
  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"]
  :source-paths ["src"]
  :cljsbuild {:builds [{:id "devcards"
                        :source-paths ["src"]
                        :figwheel {:devcards true}
                        :compiler {:main       "vega-viewer.dev-cards"
                                   :asset-path "js/compiled/devcards_out"
                                   :output-to  "resources/public/js/compiled/vega_viewer_devcards.js"
                                   :output-dir "resources/public/js/compiled/devcards_out"
                                   :source-map-timestamp true}}
                       {:id "dev"
                        :source-paths ["src"]
                        :figwheel true
                        :compiler {:asset-path "js/compiled/out"
                                   :output-to  "resources/public/js/compiled/vega_viewer.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :source-map-timestamp true}}
                       {:id "prod"
                        :source-paths ["src"]
                        :compiler {:asset-path "js/compiled/out"
                                   :output-to  "resources/public/js/compiled/vega_viewer.js"
                                   :optimizations :advanced}}
                       {:id "test"
                        :source-paths ["src" "test"]
                        :compiler {:output-to "target/main-test.js"
                                   :optimizations :whitespace
                                   :pretty-print true}
                        :notify-command ["phantomjs"
                                         "phantom/unit-test.js"
                                         "phantom/unit-test.html"
                                         "target/main-test.js"]}]
              :test-commands {"unit-test"
                              ["phantomjs"
                               "phantom/unit-test.js"
                               "phantom/unit-test.html"
                               "target/main-test.js"]}}

  :figwheel {:css-dirs ["resources/public/css"]})
