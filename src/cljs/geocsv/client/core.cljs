(ns geocsv.client.core
  (:require
    [day8.re-frame.http-fx]
    [reagent.core :as r]
    [re-frame.core :as rf]
    [geocsv.client.gis :as gis]
    [geocsv.client.views.map :as mv]
    [goog.events :as events]
    [goog.history.EventType :as HistoryEventType]
    [markdown.core :refer [md->html]]
    [geocsv.client.ajax :as ajax]
    [geocsv.client.events]
    [reitit.core :as reitit]
    [reitit.frontend.easy :as rfe]
    [clojure.string :as s])
  (:import goog.History))

(defn nav-link [uri title page]
  [:a.navbar-item
   {:href   uri
    :class (when (= page @(rf/subscribe [:page])) :is-active)}
   title])

(defn navbar []
  (r/with-let [expanded? (r/atom false)]
    [:nav.navbar.is-info>div.container
     [:div.navbar-brand
      [:a.navbar-item {:href "/" :style {:font-weight :bold}} "geocsv"]
      [:span.navbar-burger.burger
       {:data-target :nav-menu
        :on-click #(swap! expanded? not)
        :class (when @expanded? :is-active)}
       [:span][:span][:span]]]
     [:div#nav-menu.navbar-menu
      {:class (when @expanded? :is-active)}
      [:div.navbar-start
       [nav-link "#/" "Map" :map]
       [nav-link "#/about" "About" :about]]]]))

(defn about-page []
  [:section.section>div.container>div.content
   [:div
    (when-let [docs @(rf/subscribe [:docs])]
      [:div {:dangerouslySetInnerHTML {:__html (md->html docs)}}])
    (when-let [images @(rf/subscribe [:available-pin-images])]
      [:div
       [:h2 "The following pin images are available on this server"]
       (apply
         vector
         (cons
           :ol
           (map
             #(vector
                :ol
                [:img
                 {:src
                  (str
                    "img/map-pins/"
                    (s/capitalize
                      (s/replace
                        (s/lower-case
                          (str %))
                        #"[^a-z0-9]" "-"))
                    "-pin.png")
                  :alt %}]
                " "
                %)
             (sort images))))])]])

(defn map-page []
    "Return the content for the main map page. Map showing current location."
  (js/console.log "Attempting to switch to map: 1")
  (mv/panel))

(def pages
  {:map #'map-page
   :about #'about-page})

(defn page []
  (if-let [page @(rf/subscribe [:page])]
    [:div
     [navbar]
     [page]]))

(defn navigate! [match _]
  (rf/dispatch [:navigate match]))

(def router
  (reitit/router
    [["/" {:name        :map
           :view        #'map-page
           :controllers [{:start (fn [_] (rf/dispatch [:init-map]))}]}]
     ["/about" {:name :about
                :view #'about-page
                :controllers [{:start (fn [_] (rf/dispatch [:init-about]))}]}]]))

(defn start-router! []
  (rfe/start!
    router
    navigate!
    {}))

;; -------------------------
;; Initialize app
(defn mount-components []
  (rf/clear-subscription-cache!)
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (rf/dispatch-sync [:initialise-db])
  (rf/dispatch [:fetch-pin-image-names])
  (start-router!)
  (ajax/load-interceptors!)
  (mount-components))
