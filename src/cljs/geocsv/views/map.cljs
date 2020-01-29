(ns ^{:doc "a map onto which to project CSV data."
      :author "Simon Brooke"}
  geocsv.views.map
  (:require [cljsjs.leaflet]
            [re-frame.core :refer [reg-sub subscribe dispatch dispatch-sync]]
            [reagent.core :as reagent]
            [recalcitrant.core :refer [error-boundary]]
            [geocsv.gis :refer [refresh-map-pins get-current-location]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;
;;;; geocsv.map: a map onto which to project CSV data.
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

;;; Cribbed heavily from
;;;   https://github.com/reagent-project/reagent-cookbook/tree/master/recipes/leaflet
;;; but using OSM data because we can't afford commercial, so also cribbed from
;;;   https://switch2osm.org/using-tiles/getting-started-with-leaflet/
;;; Note that this is raw reagent stylee; it should be refactoed into re-frame stylee
;;; when I understand it better.

;; which provider to use
(def ^:dynamic *map-provider* :osm)

(def osm-url "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png")
(def osm-attrib "Map data &copy; <a href='http://openstreetmap.org'>OpenStreetMap</a> contributors")

;; My gods mapbox is user-hostile!
(defn map-did-mount-mapbox
  "Did-mount function loading map tile data from MapBox (proprietary)."
  []
  (let [view (.setView
               (.map js/L "map" (clj->js {:zoomControl "false"})))]
    ;; NEED TO REPLACE FIXME with your mapID!
    (.addTo (.tileLayer js/L "http://{s}.tiles.mapbox.com/v3/FIXME/{z}/{x}/{y}.png"
                        (clj->js {:attribution "Map data &copy; [...]"
                                  :maxZoom 18})))
    view))

(defn map-did-mount-osm
  "Did-mount function loading map tile data from Open Street Map."
  []
  (let [view (.setView
               (.map js/L
                     "map"
                     ;; (clj->js {:zoomControl false})
                     )
               #js [56 -4] ;;[@(subscribe [:latitude]) @(subscribe [:longitude])]
               @(subscribe [:zoom]))]
    (.addTo (.tileLayer js/L osm-url
                        (clj->js {:attribution osm-attrib
                                  :maxZoom 18}))
            view)
    (dispatch-sync [:set-view view])
    view))

(defn map-did-mount
  "Select the actual map provider to use."
  []
  (dispatch-sync [:set-view (case *map-provider*
                              :mapbox (map-did-mount-mapbox)
                              :osm (map-did-mount-osm)
                              ;; potentially others
                              )]))

(defn map-render
  "Render the actual div containing the map."
  []
  [:div#map {:style {:height "1000px"}}])

(defn panel
  "A reagent class for the map object."
  []
  ;; (get-current-location)
  (js/console.log "Attempting to switch to map: 2")
  (reagent/create-class {:reagent-render map-render
                         :component-did-mount map-did-mount}))
