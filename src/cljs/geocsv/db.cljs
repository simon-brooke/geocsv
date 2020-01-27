(ns ^{:doc "geocsv app initial database."
      :author "Simon Brooke"}
  geocsv.db)

(def default-db
  {:page :home
   :map {:map-centre [56 -4]
         :map-zoom  6}})
