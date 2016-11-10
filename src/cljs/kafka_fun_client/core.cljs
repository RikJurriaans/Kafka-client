(ns kafka-fun-client.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [ajax.core :refer [POST]]))

(enable-console-print!)

(defn send-data []
  (POST "/send-data" {:params {:component-url (-> js/window .-location .-href)
                               :user-agent (-> js/navigator .-userAgent)}}))

;; TODO: maybe make macro
(defn home-component []
  (send-data)
  [:div [:h2 "Welcome to kafka-fun-client"]
   [:div [:a {:href "/about"} "go to about component"]]
   [:div [:a {:href "/contact"} "go to the contact component"]]])

(defn about-component []
  (send-data)
  [:div [:h2 "About kafka-fun-client"]
   [:div [:a {:href "/"} "go to the home component"]]
   [:div [:a {:href "/contact"} "go to the contact component"]]])

(defn contact-component []
  (send-data)
  [:div [:h2 "Contact kafka-fun-client"]
   [:div [:a {:href "/"} "go to the home component"]]
   [:div [:a {:href "/about"} "go to about component"]]])

(defn current-component []
  [:div [(session/get :current-component)]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-component #'home-component))

(secretary/defroute "/about" []
  (session/put! :current-component #'about-component))

(secretary/defroute "/contact" []
  (session/put! :current-component #'contact-component))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-component] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
