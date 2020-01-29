(defproject geocsv "0.1.1-SNAPSHOT"

  :description "A wee tool to show comma-separated value data on a map."
  :url "http://example.com/FIXME"

  :dependencies [[adl-support "0.1.6"]
                 [ch.qos.logback/logback-classic "1.2.3"]
                 [cheshire "5.9.0"]
                 [cljs-ajax "0.8.0"]
                 [cljsjs/leaflet "1.2.0-0"]
                 [clojure.java-time "0.3.2"]
                 [com.cemerick/url "0.1.1"]
                 [com.cognitect/transit-clj "0.8.319"]
                 [compojure "1.6.1"]
                 [cprop "0.1.15"]
                 [csv2edn "0.1.6"]
                 [day8.re-frame/http-fx "0.1.6"]
                 [expound "0.8.3"]
                 [funcool/struct "1.4.0"]
                 [lib-noir "0.9.9" :exclusions [org.clojure/tools.reader]]
                 [luminus-jetty "0.1.7"]
                 [luminus-transit "0.1.2"]
                 [luminus/ring-ttl-session "0.3.3"]
                 [markdown-clj "1.10.1"]
                 [metosin/muuntaja "0.6.6"]
                 [metosin/reitit "0.3.10"]
                 [metosin/ring-http-response "0.9.1"]
                 [mount "0.1.16"]
                 [nrepl "0.6.0"]
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.597" :scope "provided"]
                 [org.clojure/tools.cli "0.4.2"]
                 [org.clojure/tools.logging "0.5.0"]
                 [org.webjars.npm/bulma "0.8.0"]
                 [org.webjars.npm/material-icons "0.3.1"]
                 [org.webjars/webjars-locator "0.38"]
                 [re-frame "0.10.9"]
                 [reagent "0.9.0-rc3"]
                 [recalcitrant "0.1.2"]
                 [ring-webjars "0.2.0"]
                 [ring/ring-core "1.8.0"]
                 [ring/ring-defaults "0.3.2"]
                 [selmer "1.12.18"]]

  :min-lein-version "2.0.0"
  :npm {:dependencies [[bulma "0.8.0"]
                       [leaflet "1.3.1"]
                       [material-design-icons "3.0.1"]]
        :root "resources/public/js/lib/node_modules"}

  :source-paths ["src/clj" "src/cljs" "src/cljc"]
  :test-paths ["test/clj"]
  :resource-paths ["resources" "target/cljsbuild"]
  :target-path "target/%s/"
  :main ^:skip-aot geocsv.core

  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-codox "0.10.7"]
            [lein-npm "0.6.2"]
            [lein-release "1.0.5"]]

  :clean-targets ^{:protect false}
  [:target-path [:cljsbuild :builds :app :compiler :output-dir] [:cljsbuild :builds :app :compiler :output-to]]
  :figwheel
  {:http-server-root "public"
   :server-logfile "log/figwheel-logfile.log"
   :nrepl-port 7002
   :css-dirs ["resources/public/css"]
   :nrepl-middleware [cider.piggieback/wrap-cljs-repl]}


  :profiles
  {:uberjar {:omit-source true
             :prep-tasks ["compile" ["cljsbuild" "once" "min"]]
             :cljsbuild{:builds
              {:min
               {:source-paths ["src/cljc" "src/cljs" "env/prod/cljs"]
                :compiler
                {:output-dir "target/cljsbuild/public/js"
                 :output-to "target/cljsbuild/public/js/app.js"
                 :source-map "target/cljsbuild/public/js/app.js.map"
                 :optimizations :advanced
                 :pretty-print false
                 :infer-externs true
                 :closure-warnings
                 {:externs-validation :off :non-standard-jsdoc :off}
                 :externs ["react/externs/react.js"]}}}}

             :aot :all
             :uberjar-name "geocsv.jar"
             :source-paths ["env/prod/clj"]
             :resource-paths ["env/prod/resources"]}

   :dev           [:project/dev :profiles/dev]
   :test          [:project/dev :project/test :profiles/test]

   :project/dev  {:jvm-opts ["-Dconf=dev-config.edn"]
                  :dependencies [[binaryage/devtools "0.9.11"]
                                 [cider/piggieback "0.4.2"]
                                 [doo "0.1.11"]
                                 [figwheel-sidecar "0.5.19"]
                                 [pjstadig/humane-test-output "0.10.0"]
                                 [prone "2019-07-08"]
                                 [re-frisk "0.5.4.1"]
                                 [ring/ring-devel "1.8.0"]
                                 [ring/ring-mock "0.4.0"]]
                  :plugins      [[com.jakemccrary/lein-test-refresh "0.24.1"]
                                 [jonase/eastwood "0.3.5"]
                                 [lein-doo "0.1.11"]
                                 [lein-figwheel "0.5.19"]]
                  :cljsbuild{:builds
                   {:app
                    {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
                     :figwheel {:on-jsload "geocsv.core/mount-components"}
                     :compiler
                     {:output-dir "target/cljsbuild/public/js/out"
                      :closure-defines {"re_frame.trace.trace_enabled_QMARK_" true}
                      :optimizations :none
                      :preloads [re-frisk.preload]
                      :output-to "target/cljsbuild/public/js/app.js"
                      :asset-path "/js/out"
                      :source-map true
                      :main "geocsv.app"
                      :pretty-print true}}}}


                  :doo {:build "test"}
                  :source-paths ["env/dev/clj"]
                  :resource-paths ["env/dev/resources"]
                  :repl-options {:init-ns user}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]}
   :project/test {:jvm-opts ["-Dconf=test-config.edn"]
                  :resource-paths ["env/test/resources"]
                  :cljsbuild
                  {:builds
                   {:test
                    {:source-paths ["src/cljc" "src/cljs" "test/cljs"]
                     :compiler
                     {:output-to "target/test.js"
                      :main "geocsv.doo-runner"
                      :optimizations :whitespace
                      :pretty-print true}}}}

                  }
   :profiles/dev {}
   :profiles/test {}})
