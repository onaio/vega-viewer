(defproject onaio/vega-viewer "0.1.0-SNAPSHOT"
  :description "Om component that renders a vega chart from a spec"
  :url "https://github.com/onaio/vega-viewer"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [devcards "0.2.1"]
                 [sablono "0.4.0"]
                 [org.omcljs/om "0.9.0"]
                 [cljsjs/vega "2.3.1-0"]]

  :plugins [[lein-bikeshed-ona "0.2.1"]
            [lein-cljfmt "0.3.0"]
            [lein-cljsbuild "1.1.1"]
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
                        :compiler {:main       "vega-viewer.core"
                                   :asset-path "js/compiled/out"
                                   :output-to  "resources/public/js/compiled/vega_viewer.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :source-map-timestamp true}}
                       {:id "prod"
                        :source-paths ["src"]
                        :compiler {:main       "vega-viewer.core"
                                   :asset-path "js/compiled/out"
                                   :output-to  "resources/public/js/compiled/vega_viewer.js"
                                   :optimizations :advanced}}]}

  :figwheel {:css-dirs ["resources/public/css"]})
