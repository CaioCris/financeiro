(ns financeiro.auxiliares
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [financeiro.handler :refer [app]]
            [clj-http.client :as http]))

(def servidor (atom nil))

(defn iniciar-servidor [porta]
  (swap! servidor
         (fn [_] (run-jetty app {:port porta :join? false}))))

(defn parar-servidor []
  (.stop @servidor))

(def porta-padrao 3001)

(defn endereco-para [rota]
  (str "http://localhost:" porta-padrao rota))

(def requisacao-para (comp http/get endereco-para))

;comp � a mesma coisa que:
;(defn requisacao-para [rota]
;  (-> (endereco-para rota)
;      (http/get)))

;que � a mesma coisa que:
;(defn requisacao-para [rota]
;  (http/get (endereco-para rota)))

(defn conteudo [rota]
  (:body (requisacao-para rota)))
