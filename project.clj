(defproject lotsbox "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]

                 [ring "1.2.1"]
                 [compojure "1.1.6"]

                 [enlive "1.1.5"]
                 [hiccup "1.0.4"]

                 [clj-time "0.6.0"]
                 
                 [overtone/at-at "1.2.0"]

                 [org.postgresql/postgresql "9.2-1002-jdbc4"]
                 [korma "0.3.0-RC6"]
                 
                 [actus "0.1.0-SNAPSHOT"]
                 ]

  :plugins [[lein-ring "0.8.10"]]

  :ring {:handler lotsbox.core/app
         :init lotsbox.core/init
         :auto-reload? true
         :auto-refresh? false
         :nrepl {:start? true}}

  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]
                        [ring-serve "0.1.2"]]}})


