(ns hexagonal-bwertr.interface.data-access
  (:require [hexagonal-bwertr.enterprise.ratings :as ratings]
            [yesql.core :refer [defquery]]
            [clojure.java.jdbc :as jdbc]))

(defquery store-query! "queries/ratings/store.sql")

(defquery retrieve-all-query "queries/ratings/retrieve-all.sql")

(defn- store-rating! [rating db-spec]
  (jdbc/with-db-transaction [connection db-spec]
    (store-query! connection (:value rating))))

(defn- retrieve-all-ratings [db-spec]
  (jdbc/with-db-transaction [connection db-spec]
    (into []
          ratings/validate-rating
          (retrieve-all-query connection))))

(defrecord DataAccessComponent [database]
  ratings/RatingsRepository
  (store! [this rating]
    (store-rating! rating (:db-spec database)))
  (retrieve-all [this]
    (retrieve-all-ratings (:db-spec database))))

(defn new-data-access []
  (map->DataAccessComponent {}))















