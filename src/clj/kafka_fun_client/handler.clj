(ns kafka-fun-client.handler
  (:require [compojure.core :refer [GET POST defroutes]]
            [compojure.route :refer [not-found resources]]
            [hiccup.page :refer [include-js include-css html5]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [kafka-fun-client.middleware :refer [wrap-middleware]]
            [kinsky.client :as client]
            [config.core :refer [env]]
            [clojure.string :as str]))

(def p (client/producer {:bootstrap.servers "localhost:9092"}
                        (client/keyword-serializer)
                        (client/edn-serializer)))

(def mount-target
  [:div#app
      [:h3 "ClojureScript has not been compiled!"]
      [:p "please run "
       [:b "lein figwheel"]
       " in order to start the compiler"]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))])

(defn loading-page []
  (html5
    (head)
    [:body {:class "body-container"}
     mount-target
     (include-js "/js/app.js")]))

(defn send [page-url user-agent]
  (client/send! p "user-tracking" :user { :timestamp (int (/ (.getTime (java.util.Date.)) 1000))
                                          :page-url page-url :user-agent user-agent })
  ;; TODO: make some kind of error handling here.
  "OK")

(defroutes routes
  (GET "/" [] (loading-page))
  (GET "/about" [] (loading-page))
  (GET "/contact" [] (loading-page))
  (GET "/send-data" [page-url user-agent]
    (send page-url user-agent))

  (resources "/")
  (not-found "Not Found"))

(def app
  (wrap-defaults
    #'routes
    ;; This is ofcourse not cool but hey its a demo...
    (assoc-in site-defaults [:security :anti-forgery] false)))
