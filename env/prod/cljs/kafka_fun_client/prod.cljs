(ns kafka-fun-client.prod
  (:require [kafka-fun-client.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
