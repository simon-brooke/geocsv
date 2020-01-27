(ns geocsv.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [geocsv.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[geocsv started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[geocsv has shut down successfully]=-"))
   :middleware wrap-dev})
