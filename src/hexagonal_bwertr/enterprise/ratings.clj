(ns hexagonal-bwertr.enterprise.ratings
  (:require [schema.core :as s]
            [hexagonal-bwertr.enterprise.schemas :as schemas]))

(s/defschema RatingValue (schemas/lower-upper-bound-number 1 10))
(s/defschema Rating {:value RatingValue})

(def validate-rating (partial s/validate Rating))

(s/with-fn-validation
  (s/defn create-rating :- Rating [value]
    {:value value}))

(defprotocol RatingsRepository
  (store! [this rating])
  (retrieve-all [this]))

(defprotocol Ratings
  (add! [this rating])
  (count* [this])
  (stats [this])
  (average [this]))

(s/defn add-rating! [rating :- Rating repository]
  (store! repository rating))

(defn- count-ratings [repository]
  (count (retrieve-all repository)))

(defn- rating-stats [repository]
  (let [all-ratings (retrieve-all repository)
        all-values (map :value all-ratings)]
    (frequencies all-values)))

(defn- average-rating [repository]
  (when-let [all-ratings (retrieve-all repository)]
    (let [all-values (map :value all-ratings)]
      (/ (reduce + all-values) (count all-ratings)))))

(defrecord RatingsComponent [repository]
  Ratings
  (add! [this rating]
    (add-rating! rating repository))
  (count* [this]
    (count-ratings repository))
  (stats [this]
    (rating-stats repository))
  (average [this]
    (average-rating repository)))

(defn new-ratings []
  (map->RatingsComponent {}))









