(ns geocsv.routes.resources
  "Serve resources for geocsv."
  (:require [adl-support.core :as ac]
            [adl-support.rest-support :as ar]
            [clojure.core.memoize :as memo]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.tools.logging :as log]
            [compojure.core :refer [defroutes GET POST]]
            [csv2edn.csv2edn :refer :all]
            [noir.response :as nresponse]
            [noir.util.route :as route]
            [ring.util.http-response :as response]
            ))
