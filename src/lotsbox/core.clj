(ns lotsbox.core
  (:use compojure.core)

  (:use hiccup.core)
  (:use hiccup.page)
  (:use hiccup.form)
  (:use hiccup.element)

  (:use korma.db)
  (:use korma.core)

  (:require [clojure.java.io :as io]
            [ring.middleware.reload :refer (wrap-reload)] ;; reload temlates
            
            [compojure.handler :as handler]
            [compojure.route :as route]

            ;;[net.cgrand.enlive-html :as h]

            [overtone.at-at :as at]
            )
  )



;; DATA BASE ---------------------------------------------------------------------------------------

(def pg-lotsbox (postgres {:host "localhost" 
                   :port 5433 
                   :db "lotsbox" 
                   :make-pool? true 
                   :user "lotsbox" 
                   :password "lotsbox"}))


(defdb korma-db pg-lotsbox)


(declare lots)

(defentity lots
  (table :lots)
  (pk :id))




;; PARSER ------------------------------------------------------------------------------------------









;; INIT --------------------------------------------------------------------------------------------

(defn init []
  (println "INIT SYSTEM")

  (print "STARTING SCHEDULLER -> ")

  (print "[ok]")

  )




;; WEB SERVER --------------------------------------------------------------------------------------

(defroutes app-routes
  (GET "/" [] "Hello World!")
  ;;(GET "/" [] (main-page))

  (route/resources "/")
  (route/not-found "Not Found"))

(defn spec-middle 
  "Дополнительняа промежуточная функция"
  [handler]
  (fn [request]
    #_(println request)
    #_(use 'politrend.handler :reload) ;; needs for enlive

    (handler request)))



(def site
  (-> app-routes spec-middle))
      
(def app
  (handler/site site))



