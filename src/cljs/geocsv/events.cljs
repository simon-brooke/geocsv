(ns geocsv.events
  (:require
    [re-frame.core :as rf]
    [ajax.core :as ajax]
    [reitit.frontend.easy :as rfe]
    [reitit.frontend.controllers :as rfc]))

;;dispatchers

(rf/reg-event-db
  :navigate
  (fn [db [_ match]]
    (let [old-match (:common/route db)
          new-match (assoc match :controllers
                                 (rfc/apply-controllers (:controllers old-match) match))]
      (assoc db :route new-match))))

(rf/reg-fx
  :navigate-fx!
  (fn [[k & [params query]]]
    (rfe/push-state k params query)))

(rf/reg-event-fx
  :navigate!
  (fn [_ [_ url-key params query]]
    {:navigate-fx! [url-key params query]}))

(rf/reg-event-db
  :set-docs
  (fn [db [_ docs]]
    (assoc db :docs docs)))

(rf/reg-event-fx
  :fetch-docs
  (fn [_ _]
    {:http-xhrio {:method          :get
                  :uri             "/docs"
                  :response-format (ajax/raw-response-format)
                  :on-success       [:set-docs]}}))

(rf/reg-event-db
  :common/set-error
  (fn [db [_ error]]
    (assoc db :common/error error)))

(reg-event-db
  :set-view
  (fn [db [_ view]]
    (assoc db :view view)))

(rf/reg-event-fx
  :page/init-home
  (fn [_ _]
    {:dispatch [:fetch-docs]}))

(rf/reg-event-fx
  :page/init-maps
  (fn [_ _]
    {:dispatch [:fetch-data]}))


(re-frame/reg-event-fx
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

(re-frame/reg-event-fx
  :process-data
  ;; TODO: why is this an `-fx`? Does it need to be?
  (fn
    [{db :db} [_ response]]
    (let [data (js->clj response)]
    (js/console.log (str ":process-data: " response))
    {:db (refresh-map-pins (assoc db :data data))})))

(re-frame/reg-event-fx
  :bad-data
  ;; TODO: why is this an `-fx`? Does it need to be?
  (fn
    [{db :db} [_ response]]
    ;; TODO: signal something has failed? It doesn't matter very much, unless it keeps failing.
    (js/console.log (str "Failed to fetch data data" response))
    db))


;;subscriptions

(rf/reg-sub
  :route
  (fn [db _]
    (-> db :route)))

(rf/reg-sub
  :page-id
  :<- [:route]
  (fn [route _]
    (-> route :data :name)))

(rf/reg-sub
  :page
  :<- [:route]
  (fn [route _]
    (-> route :data :view)))

(rf/reg-sub
  :docs
  (fn [db _]
    (:docs db)))

(rf/reg-sub
  :common/error
  (fn [db _]
    (:common/error db)))
