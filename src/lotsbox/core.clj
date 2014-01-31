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

            [net.cgrand.enlive-html :as h]

            [overtone.at-at :as at]
            [clj-time.format :as tf]
            [clj-time.coerce :as tc]
            )
  )



;; DATA BASE ---------------------------------------------------------------------------------------

(def pg-lotsbox (postgres {:host "localhost"
                           :port 5433
                           :db "lotsbox"
                           :make-pool? true
                           :user "lotsbox"
                           :password "paradox"}))


(defdb korma-db pg-lotsbox)


(declare lots)

(defentity lots
  (table :lots)
  (pk :id))


;; PARSER ------------------------------------------------------------------------------------------


;; (defn comic-titles
;;   [n]
;;   (let [dom (h/html-resource
;;              (java.net.URL. "http://xkcd.com/archive"))
;;         title-nodes (h/select dom [:#middleContainer :a])
;;         titles (map h/text title-nodes)]
;;     (take n titles)))

;; (defn parse1 [] (h/html-resource
;;              (java.net.URL. "http://linux.org.ru")))



;; (defn tender-sk-kz-lots []
;;   "Распарсить сайт"
  
;;   (let [dom  (h/html-resource  (java.net.URL. "http://tender.sk.kz/index.php/ru/lots"))
;;         rows (h/select dom [:.showtab :tr])
        
;;         ]

;;     (first (map
;;             #(h/select % [:td])
           
;;             (rest rows)))

;;     ))

;; (defn tender-sk-kz-lots []
;;   "Распарсить сайт"
;;   ((comp 

;;     first
;;     (partial map
;;              (fn [x] (map #(first (% :content)) x)))
    
;;     (partial map #(h/select % [:td]))
;;     rest
;;     #(h/select % [:.showtab :tr])    
;;     ) (h/html-resource (java.net.URL. "http://tender.sk.kz/index.php/ru/lots"))))


;; (def column-keys
;;   "Колонки метаданных"
;;   [:stat 
;;    :keyname
;;    :caption
;;    :description
;;    :sum_all
;;    :place
;;    :bdate
;;    :edate])


(defn get-two-row [i1 i2 rows]
  "Тестовая функция"
  [(get (vec rows) i1) (get (vec rows) i2)])


(defn get-cell-and-parse [parser h-cells]
  "Функция разбора значений строки"
  (let [ks (keys parser)
        cells (vec h-cells)]
    (reduce

     (fn [a k]
       (let [[i,f]  (k parser)]
         (assoc a k (f (get cells i)))))
     
     {} ks)))


(defn do-parse-rows [parser h-rows]
  "Разбор всех строк"
  (map #(get-cell-and-parse parser %) h-rows))


(defn ->->> [x f & xx]
  "Функция - модификатор вызова"
  (apply f (conj (vec xx) x)))

 
(defn tender-sk-kz-lots []
  (-> (h/html-resource (java.net.URL. "http://tender.sk.kz/index.php/ru/lots"))
      (h/select [:.showtab :tr])
      rest ;; Убираем шапку таблицы
      (->->> map #(h/select % [:td]))
      (->->> map (fn [x] (map #(first (% :content)) x)))
      (->->> do-parse-rows {:keyname [0,str]
                            :caption [2,#(first (% :content))]
                            :description [3,str]
                            :sum_all [6, #(bigdec (-> %
                                                    (clojure.string/replace #" " "")
                                                    (clojure.string/replace #"," ".")))]
                            
                            :bdate [8,#(tc/to-sql-date (tf/parse (tf/formatter "dd-MM-yyyy") %)) ]
                            :edate [9,#(tc/to-sql-date (tf/parse (tf/formatter "dd-MM-yyyy") %)) ]

                            :place [10,str]

                            } )
      ;;first
      ))








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
