(ns geocsv.routes.rest
  "REST routes for geocsv."
  (:require [adl-support.core :as ac]
            [adl-support.rest-support :as ar]
            [clojure.core.memoize :as memo]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.tools.logging :as log]
            [cpath-clj.core :as cp]
            [compojure.core :refer [defroutes GET POST]]
            [csv2edn.csv2edn :refer :all]
            [noir.response :as nresponse]
            [noir.util.route :as route]
            [ring.util.http-response :as response]
            ))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;
;;;; geocsv.routes.json: data service handlers.
;;;;
;;;; This program is free software; you can redistribute it and/or
;;;; modify it under the terms of the GNU General Public License
;;;; as published by the Free Software Foundation; either version 2
;;;; of the License, or (at your option) any later version.
;;;;
;;;; This program is distributed in the hope that it will be useful,
;;;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;;;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;;;; GNU General Public License for more details.
;;;;
;;;; You should have received a copy of the GNU General Public License
;;;; along with this program; if not, write to the Free Software
;;;; Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
;;;; USA.
;;;;
;;;; Copyright (C) 2016 Simon Brooke for Radical Independence Campaign
;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-pin-image-names
  "Return the category names for which we have pin images; `request` is ignored.

  This looks odd - why not file-seq over the directory? - but the answer is we
  may be running in a jar file, and if we are that will fail."
  [request]
  (ar/do-or-server-fail
    (map
      #(s/replace (s/replace (str %) #"-pin\.png$" "") "/" "")
      (->> "public/img/map-pins"
           cp/resources
           keys
           (filter #(re-find #".*-pin.png" %))))
    200))

(defn get-data-uri
  "Return JSON formatted data taken from the CSV file at this URI. The file
  must exist, be publicly readable, and use commas as separators."
  [uri]
  (csv->json uri))

(defn get-data-google
  "Return JSON formatted data taken from the Google Sheets spreadsheet with
  this `docid`. The spreadsheet must exist and must be publicly readable."
  [docid]
  (get-data-uri
    (str
      "https://docs.google.com/spreadsheets/d/"
      docid
      "/export?format=csv")))

(defn get-data-file
  "Return JSON formatted data taken from the CSV file with the name `filename`
  in the directory `resources/public/data`."
  [filename]
  (-> (str "public/data/" filename) io/resource io/reader csv->json))

(defn get-data
  "Return JSON formatted data from the source implied by this `request`."
  [request]
  (ar/do-or-server-fail
    ;; We're merging the parameters from the request with the key/value
    ;; pairs already in the session, so that parame put into the session
    ;; by calls to the home page can be used here.
    (let [params (merge
                   (:session request)
                   (ac/massage-params request))]
    (cond
      (:docid params) (get-data-google (:docid params))
      (:uri params) (get-data-uri (:uri params))
      (:file params) (get-data-file (:file params))
      :else (get-data-file "data.csv")))
    200))

(defroutes rest-routes
  (GET "/get-pin-image-names" request (get-pin-image-names request))
  (POST "/get-pin-image-names" request (get-pin-image-names request))
  (GET "/get-data" request (get-data request))
  (POST "/get-data" request (get-data request)))
