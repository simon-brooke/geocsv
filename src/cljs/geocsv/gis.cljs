(ns ^{:doc "geocsv app map stuff."
      :author "Simon Brooke"}
  geocsv.gis
  (:require [ajax.core :refer [GET]]
            [ajax.json :refer [json-request-format json-response-format]]
            [cljs.reader :refer [read-string]]
            [clojure.string :refer [capitalize lower-case]]
            [cemerick.url :refer (url url-encode)]
            [day8.re-frame.http-fx]
            [re-frame.core :refer [dispatch reg-event-db reg-event-fx subscribe]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;
;;;; geocsv.gis: stuff to do with maps.
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

;; map stuff.

(defn get-current-location []
  "Return the current location from the device, setting it in the database and
  returning the locality."
  (try
    (if (.-geolocation js/navigator)
      (.getCurrentPosition
        (.-geolocation js/navigator)
        (fn [position]
          (let [view @(subscribe [:view])
                lat (.-latitude (.-coords position))
                lng (.-longitude (.-coords position))]
            (js/console.log (str "Current location is: " lat ", " lng))
            (if
              (and view (float? lat) (float? lng))
              (do
                (dispatch [:set-latitude lat])
                (dispatch [:set-longitude lng])
                (.panTo view (.latLng js/L lat lng))
                (locality lat lng))
              (do
                (js/console.log
                  (if view
                    (str "Geolocation failed lat: '" lat "'; lng '" lng "'")
                    "No value for subscription to [:view]"))
                0)))))
      (do
        (js/console.log "Geolocation not available")
        0))
    (catch js/Object any
      (js/console.log "Exception while trying to access location: " + any)
      0)))

(defn map-pin-click-handler
  [id]
  (js/console.log (str "Click handler for record #" id)))

(defn pin-image
  "Return the name of a suitable pin image for this `record`."
  [record]
  (if
    (:category record)
    (str
      (s/capitalize
        (s/replace (s/lower-case (str (:category record))) #"[^a-z0-9]" "-")) "-pin")
    "unknown-pin"))

(defn add-map-pin
  "Add an appropriate map-pin for this `record` in this map `view`."
  [record index view]
  (let [lat (:latitude record)
        lng (:longitude record)
        pin (.icon js/L
                   (clj->js
                    {:iconAnchor [16 41]
                     :iconSize [32 42]
                     :iconUrl (str "img/map-pins/" (pin-image record) ".png")
                     :riseOnHover true
                     :shadowAnchor [16 23]
                     :shadowSize [57 24]
                     :shadowUrl "img/map-pins/shadow_pin.png"}))
        marker (.marker js/L
                        (.latLng js/L lat lng)
                        (clj->js {:icon pin
                                  :title (:name record)}))]
    (.on
        (.addTo marker view)
        "click"
        (fn [_] (map-pin-click-handler index)))
    marker))

(defn map-remove-pins
  "Remove all pins from this map `view`. Side-effecty; liable to be
    problematic."
  [view]
  (if view
    (.eachLayer view
                #(if
                   (instance? js/L.Marker %)
                   (.removeLayer view %)))
    view))


(defn refresh-map-pins
  "Refresh the map pins on the current map. Side-effecty; liable to be
    problematic."
  [db]
  (let [view (map-remove-pins @(re-frame/subscribe [:view]))
        data (:data db)]
    (if
      view
      (do
        (js/console.log (str "Adding " (count data) " pins"))
        (doall (map #(add-map-pin %1 %2 view) data (range))))
      (js/console.log "View is not yet ready"))
    db))

