(ns geocsv.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[geocsv started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[geocsv has shut down successfully]=-"))
   :middleware identity})
