(ns geocsv.routes.home
  (:require [clojure.java.io :as io]
            [compojure.core :refer [defroutes GET POST]]
            [geocsv.layout :as layout]
            [geocsv.middleware :as middleware]
            [ring.util.response]
            [ring.util.http-response :as response]))

(defn home-page [request]
  "Serve the home page, in the process merging any parameters passed
  in the request into the session."
  (assoc
    (layout/render request "home.html")
    :session
    (merge
      (:session request)
      (:params request))))

(defroutes home-routes
  (GET "/" request (home-page request))
  (GET "/docs" _ (fn [_]
                    (->
                      (response/ok
                        (->
                          "docs/docs.md"
                          io/resource
                          slurp))
                        (response/header "Content-Type" "text/plain; charset=utf-8")))))

