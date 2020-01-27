(ns geocsv.events
  (:require [ajax.core :as ajax]
            [ajax.json :refer [json-request-format json-response-format]]
            [cemerick.url :refer [url url-encode]]
            [geocsv.db :refer [default-db]]
            [geocsv.gis :refer [refresh-map-pins]]
            [re-frame.core :as rf]
            [reitit.frontend.easy :as rfe]
            [reitit.frontend.controllers :as rfc]))


(def source-host
  "The base URL of the host from which the app was loaded."
  (assoc
    (url js/window.location)
    :path "/"
    :query nil
    :anchor nil))


;;dispatchers: keep in alphabetical order, please.
(rf/reg-event-fx
  :bad-data
  ;; TODO: why is this an `-fx`? Does it need to be?
  (fn
    [{db :db} [_ response]]
    ;; TODO: signal something has failed? It doesn't matter very much, unless it keeps failing.
    (js/console.log (str "Failed to fetch data data" response))
    db))

(rf/reg-event-db
  :common/set-error
  (fn [db [_ error]]
    (assoc db :common/error error)))

(rf/reg-event-fx
 :fetch-data
 (fn [{db :db} _]
   (let [uri (assoc source-host
                  :path "/data/data.json")]
         (js/console.log
          (str
           "Fetching data: " uri))
         ;; we return a map of (side) effects
         {:http-xhrio {:method          :get
                       :uri             uri
                       :format          (json-request-format)
                       :response-format (json-response-format {:keywords? true})
                       :on-success      [:process-data]
                       :on-failure      [:bad-data]}
          :db  db})))

(rf/reg-event-fx
  :fetch-docs
  (fn [_ _]
    {:http-xhrio {:method          :get
                  :uri             "/docs"
                  :response-format (ajax/raw-response-format)
                  :on-success       [:set-docs]}}))

(rf/reg-event-fx
  :init-home
  (fn [_ _]
    {:dispatch [:fetch-docs]}))

(rf/reg-event-fx
  :init-map
  (fn [_ _]
    {:dispatch [:fetch-data]}))

(rf/reg-event-db
 :initialise-db
 (fn  [_ _]
   (js/console.log "Initialising state.")
   default-db))

(rf/reg-event-db
  :navigate
  (fn [db [_ match]]
    (let [old-match (:common/route db)
          new-match (assoc match :controllers
                                 (rfc/apply-controllers (:controllers old-match) match))]
      (assoc db :route new-match))))

(rf/reg-event-fx
  :navigate!
  (fn [_ [_ url-key params query]]
    {:navigate-fx! [url-key params query]}))

(rf/reg-fx
  :navigate-fx!
  (fn [[k & [params query]]]
    (rfe/push-state k params query)))

(rf/reg-event-fx
  :process-data
  ;; TODO: why is this an `-fx`? Does it need to be?
  (fn
    [{db :db} [_ response]]
    (let [data (js->clj response)]
    (js/console.log (str "processing fetched JSON data"))
    {:db (if
           (:view db)
           (refresh-map-pins (assoc db :data data))
           db)})))

(rf/reg-event-db
  :set-docs
  (fn [db [_ docs]]
    (assoc db :docs docs)))

(rf/reg-event-db
  :set-view
  (fn [db [_ view]]
    (assoc db :view view)))

;;subscriptions. Keep in alphabetical order, please

(rf/reg-sub
  :common/error
  (fn [db _]
    (:common/error db)))

(rf/reg-sub
  :docs
  (fn [db _]
    (:docs db)))

(rf/reg-sub
  :latitude
  (fn [db _]
    (let [v (or (first (:map-centre (:map db))) 54)]
      (js/console.log (str "Fetching latitude" v))
      v)))

(rf/reg-sub
  :longitude
  (fn [db _]
    (let [v (or (nth (:map-centre (:map db)) 1) -4)]
      (js/console.log (str "Fetching longitude" v))
      v)))

(rf/reg-sub
  :map
  (fn [db _]
    (:map db)))

(rf/reg-sub
  :route
  (fn [db _]
    (-> db :route)))

(rf/reg-sub
  :page
  :<- [:route]
  (fn [route _]
    (-> route :data :view)))

(rf/reg-sub
  :page-id
  :<- [:route]
  (fn [route _]
    (-> route :data :name)))

(rf/reg-sub
  :zoom
  (fn [db _]
    (:map-zoom (:map db))))

