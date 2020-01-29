(ns geocsv.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [compojure.route :as route]
            [geocsv.env :refer [defaults]]
            [geocsv.middleware :as middleware]
            [geocsv.layout :refer [error-page]]
            [geocsv.routes.home :refer [home-routes]]
            [geocsv.routes.rest :refer [rest-routes]]
            [reitit.ring :as ring]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.webjars :refer [wrap-webjars]]
            [mount.core :as mount]))

(mount/defstate init-app
  :start ((or (:init defaults) (fn [])))
  :stop  ((or (:stop defaults) (fn []))))

(mount/defstate app-routes
  :start
  ;; This is an older way of doing routing and Dmitri Sotnikov now does it
  ;; another way which is almost certainly better but I can't make it work.
  (routes
    (-> #'home-routes
        (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats))
    (-> #'rest-routes
        (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats))
    (ring/create-resource-handler
         {:path "/"})
;;    (route/resources "/")
    (route/not-found
      (:body
        (error-page {:status 404
                     :title "Page not found"
                     :message "The page you requested has not yet been implemented"})))))


;;   (ring/ring-handler
;;     (ring/router
;;       [(home-routes)
;; ;;          (-> rest-routes
;; ;;              (wrap-routes middleware/wrap-csrf)
;; ;;              (wrap-routes middleware/wrap-formats))
;;        ])
;;     (ring/routes
;;       (ring/create-resource-handler
;;         {:path "/"})
;;       (wrap-content-type
;;         (wrap-webjars (constantly nil)))
;;       (ring/create-default-handler
;;         {:not-found
;;          (constantly (error-page {:status 404, :title "404 - Page not found"}))
;;          :method-not-allowed
;;          (constantly (error-page {:status 405, :title "405 - Not allowed"}))
;;          :not-acceptable
;;          (constantly (error-page {:status 406, :title "406 - Not acceptable"}))}))))

(defn app []
  (middleware/wrap-base #'app-routes))
