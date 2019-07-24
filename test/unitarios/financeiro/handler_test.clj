(ns financeiro.handler-test
  (:require [midje.sweet :refer :all]
            [ring.mock.request :as mock]
            [cheshire.core :as json]
            [financeiro.handler :refer :all]
            [financeiro.db :as db]))

(facts "D� um 'Hello World' na rota raiz"
       (let [response (app (mock/request :get "/"))]
         (fact "o status da resposta � 200"
               (:status response) => 200)

         (fact "o texto do corpo � 'Hello World'"
               (:body response) => "Hello World")))

(facts "Rota inv�lida n�o existe"
       (let [response (app (mock/request :get "/invalid"))]
         (fact "o c�digo de erro � 404"
               (:status response) => 404)

         (fact "o texto do corpo � 'Not Found'"
               (:body response) => "Not Found")))

(facts "Saldo inicial � 0"
       (against-background [(json/generate-string {:saldo 0}) => "{\"saldo\":0}"
                            (db/saldo) => 0])
       (let [response (app (mock/request :get "/saldo"))]
         (fact "o formato � 'application/json'"
               (get-in response [:headers "Content-Type"]) => "application/json; charset=utf-8")

         (fact "o status da resposta � 200"
               (:status response) => 200)

         (fact "o texto do corpo � um JSON cuja chave � saldo e o valor � 0"
               (:body response) => "{\"saldo\":0}")))

(facts "Registrar uma receita no valor de 10"
       (against-background (db/registrar {:valor 10
                                          :tipo "receita"}) => {:id 1, :valor 10, :tipo "receita"})
       (let [response (app (-> (mock/request :post "/transacoes")
                               (mock/json-body {:valor 10, :tipo "receita"})))]
         (fact "o status da resposta � 201"
               (:status response) => 201)
         (fact "o texto do corpo � um JSON com o conte�do enviado e um id"
               (:body response) => "{\"id\":1,\"valor\":10,\"tipo\":\"receita\"}")))