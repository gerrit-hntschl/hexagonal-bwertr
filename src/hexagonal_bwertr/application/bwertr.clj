(ns hexagonal-bwertr.application.bwertr
  (:require [hexagonal-bwertr.enterprise.ratings :as ratings]
            [schema.core :as s]))

(s/defschema RatingStats {:average-rating s/Num
                          :number-of-ratings s/Int})

(s/defschema DetailedRatingStats (assoc RatingStats :frequencies {s/Int s/Int}))
(s/defschema OwnRatingStats (assoc RatingStats :own-rating s/Int))

(s/with-fn-validation
  (s/defn rate-with*! :- OwnRatingStats
    [rating :- ratings/Rating ratings]
    (do
      (ratings/add! ratings rating)
      (let [average-rating (ratings/average ratings)
            number-of-ratings (ratings/count* ratings)]
        {:own-rating rating :average-rating average-rating :number-of-ratings number-of-ratings}))))

(s/with-fn-validation
  (s/defn rating-stats :- DetailedRatingStats
    [ratings :- [ratings/Rating]]
    (let [average-rating (ratings/average ratings)
          number-of-ratings (ratings/count* ratings)
          frequencies (ratings/stats ratings)]
      {:average-rating average-rating :number-of-ratings number-of-ratings :frequencies frequencies})))

(defprotocol BwertrApplication
  (rate-with! [this rating])
  (statistics [this]))

(defrecord BwertrApplicationComponent [ratings]
  BwertrApplication
  (rate-with! [this rating]
    (rate-with*! rating ratings))
  (statistics [this]
    (rating-stats ratings)))

(defn new-bwertr-application []
  (map->BwertrApplicationComponent {}))











