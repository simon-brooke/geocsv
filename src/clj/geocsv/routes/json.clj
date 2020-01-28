(ns geocsv.routes.json
  "JSON routes for geocsv."
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
  [request]
  (ar/do-or-server-fail
    (map
      #(s/replace (.getName %) #"-pin\.png$" "")
      (let [grammar-matcher (.getPathMatcher
                            (java.nio.file.FileSystems/getDefault)
                            "glob:*-pin.png")]
      (->> "resources/public/img/map-pins"
           clojure.java.io/file
           file-seq
           (filter #(.isFile %))
           (filter #(.matches grammar-matcher (.getFileName (.toPath %)))))))
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
  in the directory `resources/public/data`. TODO: There is a safe way to
  access the content of the resource directory but I don't recall it just now."
  [filename]
  (csv->json (io/reader (io/file (str "resources/public/data/" filename)))))

(defn get-data
  [request]
  (ar/do-or-server-fail
    (let [params (ac/massage-params request)]
    (cond
      (:docid params) (get-data-google (:docid params))
      (:uri params) (get-data-uri (:uri params))
      (:file params) (get-data-file (:file params))
      :else (get-data-file "data.csv")))
    200))

(defroutes json-routes
  (GET "/get-pin-image-names" request (get-pin-image-names request))
  (POST "/get-pin-image-names" request (get-pin-image-names request))
  (GET "/get-data" request (get-data request)))
