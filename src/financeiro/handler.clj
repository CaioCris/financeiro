(ns financeiro.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [cheshire.core :as json]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defn saldo-como-json []
  {:headers {"Content-Type" "application/json; charset=utf-8"}
   :body    (json/generate-string {:saldo 0})})

(defroutes app-routes
           (GET "/" [] "Hello World")
           (GET "/saldo" [] (saldo-como-json))
           (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
