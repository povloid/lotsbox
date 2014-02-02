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

            [clj-time.core :as tco]
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

;; UTILS -------------------------------------------------------------------------------------------

(defn ->->> [x f & xx]
  "Функция - модификатор вызова в потоке"
  (apply f (conj (vec xx) x)))

;; PARSER ------------------------------------------------------------------------------------------

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

;; tender.sk.kz ------------------------------------------------------------------

(defn tender-sk-kz-lots []
  "Получение данных лотов по адресу http://tender.sk.kz/index.php/ru/lots"
  (let [res  "tender.sk.kz"
        url  "http://tender.sk.kz/index.php/ru/lots"]
    (-> (h/html-resource (java.net.URL. url))
        (h/select [:.showtab :tr])
        rest ;; Убираем шапку таблицы, берем только тело
        (->->> map #(h/select % [:td]))
        (->->> map (fn [x] (map #(first (% :content)) x)))

        ;; Деструктуризация первой страници
        (->->> do-parse-rows {:keyname [0,str]
                              :caption [2, #(first (% :content))]
                              :description [3,str]
                              :sum_all [6, #(bigdec (-> %
                                                        (clojure.string/replace #" " "")
                                                        (clojure.string/replace #"," ".")))]

                              :bdate [8,#(tc/to-sql-date (tf/parse (tf/formatter "dd-MM-yyyy") %)) ]
                              :edate [9,#(tc/to-sql-date (tf/parse (tf/formatter "dd-MM-yyyy") %)) ]

                              :place_to [10,str]

                              :TMP_URL [2, #((% :attrs) :href)]

                              } )


        ;; Далее получаем информацию еще с одной страници по url
        (->->> map (fn [x]
                     (merge (dissoc x :TMP_URL) ;; Сливаем результаты и удаляем ненужное :TMP_URL
                            (-> (h/html-resource (java.net.URL. (x :TMP_URL))) ;; здесь :TMP_URL еще есть
                                (h/select [:.showtab])
                                first ;; Берем первую таблицу
                                (h/select [:td])
                                rest
                                (->->> take-nth 2)
                                (->->> map #(first (% :content)))

                                (->->> get-cell-and-parse {:zak_name [1,str]
                                                           :org [2,str]
                                                           :place_in [3,str]
                                                           :method [4,str]
                                                           })
                                ))))

        ;; Добавление служебной информации
        (->->> map #(assoc % :url url :res res :updated (tc/to-sql-time (tco/now)) ))

        ;; удобно при отладке
        ;;first

        )))

;;(insert lots (values (tender-sk-kz-lots)))


;; tender.sk.kz ...


(defn insert-update-rows [rows]
  "Дополнить/обновить записи"
  (->> rows
       (map (fn [{res :res keyname :keyname  :as row}]
              (let [r (update lots (set-fields row) (where (and (= :res res) (= :keyname keyname))))]
                (if (nil? r) row nil))))

       (filter #(not (nil? %)))

       (#(if (empty? %) :only-updated
             (do (insert lots (values %)) :updated-and-inserted)))))





;; SHEDULE -----------------------------------------------------------------------------------------

(defn task-fn-1 []
  (println (insert-update-rows(tender-sk-kz-lots)))
  )

(def my-pool (at/mk-pool))

(defn run-task1 []
  (at/every (* 5 60 1000)
            task-fn-1
            my-pool))

;; INIT --------------------------------------------------------------------------------------------

(defn init []
  (println "INIT SYSTEM")

  (print "STARTING SCHEDULLER -> ")

  (print "[ok]")

  )




;; WEB SERVER --------------------------------------------------------------------------------------

(defroutes app-routes
  ;;(GET "/" [] "Hello World!!!!!!")
  (GET "/" [] "<html><head></head><body></body></html>") ;; В таком варианте появляется скрипт для автообновления страници который включается от :auto-refresh? true
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
