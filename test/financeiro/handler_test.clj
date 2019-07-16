(ns financeiro.handler-test
  (:require [midje.sweet :refer :all]
            [ring.mock.request :as mock]
            [financeiro.handler :refer :all]))

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

