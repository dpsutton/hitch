(defproject hitch "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-3211"]
                 ]
  :profiles {
             :dev {
                   :dependencies [[org.clojure/core.async "0.1.346.0-17112a-alpha"]]
                   :plugins [[lein-cljsbuild "1.0.5"]
                             [lein-figwheel "0.3.1"]]
                   :figwheel {
                              ;; :http-server-root "public" ;; default and assumes "resources"
                              ;; :server-port 3449 ;; default
                              :css-dirs ["resources/public/css"] ;; watch and update CSS

                              ;; Start an nREPL server into the running figwheel process
                              :nrepl-port 7888

                              ;; Server Ring Handler (optional)
                              ;; if you want to embed a ring handler into the figwheel http-kit
                              ;; server, this is for simple ring servers, if this
                              ;; doesn't work for you just run your own server :)
                              ;; :ring-handler hello_world.server/handler

                              ;; To be able to open files in your editor from the heads up display
                              ;; you will need to put a script on your path.
                              ;; that script will have to take a file path and a line number
                              ;; ie. in  ~/bin/myfile-opener
                              ;; #! /bin/sh
                              ;; emacsclient -n +$2 $1
                              ;;
                              ;; :open-file-command "myfile-opener"

                              ;; if you want to disable the REPL
                              ;; :repl false

                              ;; to configure a different figwheel logfile path
                              ;; :server-logfile "tmp/logs/figwheel-logfile.log"
                              }}}
  :source-paths ["src"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]
  
  :cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src"]

              :figwheel { :on-jsload "hitch.core/on-js-reload" }

              :compiler {:main hitch.core
                         :asset-path "js/compiled/out"
                         :output-to "resources/public/js/compiled/hitch.js"
                         :output-dir "resources/public/js/compiled/out"
                         :optimizations :none
                         :source-map true
                         :source-map-timestamp true
                         :cache-analysis true }}
             {:id "test"
              :source-paths ["src" "test"]

              :figwheel { :on-jsload "hitch.test-runner/on-js-reload" }

              :compiler {:main hitch.test-runner
                         :asset-path "js/compiled/testout"
                         :output-to "resources/public/js/compiled/hitch_test.js"
                         :output-dir "resources/public/js/compiled/testout"
                         :optimizations :none
                         :source-map true
                         :source-map-timestamp true
                         :cache-analysis true }}
             {:id "min"
              :source-paths ["src"]
              :compiler {:output-to "resources/public/js/compiled/hitch.js"
                         :main hitch.core
                         :optimizations :advanced
                         :pretty-print false}}]}

            )
