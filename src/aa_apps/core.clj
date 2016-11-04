(ns aa-apps.core
  (:require [clojure.data.csv :as csv]
            [twilio.core :as twilio])
  (:gen-class))

(def twilio-sid "AC86ed6e75f3739b290c6a234dd75db78f")

(def twilio-auth-token "ff43fe567f3d88789be945fc31af2b34")

(def my-twilio-number "+17204524168")

(defn get-data []
  (rest (csv/read-csv (slurp "data4.csv"))))

(defn select-winners
  "Randomly select n winners from the pool of possible winners"
  [n]
  (let [contestants (get-data)
        results (take n (shuffle (get-data)))]
    (println "Selecting" n "winners from" (count contestants) "contestants")
    (println "and the winners are:")
    results))

(defn send-sms [message number]
  (twilio/with-auth twilio-sid twilio-auth-token
    (twilio/send-sms
      {:From my-twilio-number
       :To number
       :Body message})))

(defn contestant->winning-message [contestant]
  (str "Congratulations " (nth contestant 2) "! You've been selected to meet American Authors! Show this text to Jesse at the merch booth at 10:45pm and he'll take you and ONE guest to meet the band. Thanks for using Shazam!"))

(defn entry->confirmation-str [entry]
  (str "[" (nth entry 2) " " (nth entry 3) " "(nth entry 4) "]"))

(defn -main [& args]
  (let [winners (select-winners 4)]
    (doseq [winner winners]
      (println (entry->confirmation-str winner))
      (println @(send-sms (contestant->winning-message winner) (str "+1" (nth winner 4)))))))

(def winner-str
  "Winners for tonight:
  [Nikole Okhman 3104678030]
  [Sanaz Sarshad 3104359104]
  [Skye Swanson 9284586341]
  [Kimberly  Barahona 3236795922]
  [Leonardo Romero 7147091249]")
