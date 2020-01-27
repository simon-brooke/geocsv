(ns geocsv.app
  (:require [geocsv.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
