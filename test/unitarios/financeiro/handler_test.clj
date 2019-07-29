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
                                          :tipo  "receita"}) => {:id 1, :valor 10, :tipo "receita"})
       (let [response (app (-> (mock/request :post "/transacoes")
                               (mock/json-body {:valor 10, :tipo "receita"})))]
         (fact "o status da resposta � 201"
               (:status response) => 201)
         (fact "o texto do corpo � um JSON com o conte�do enviado e um id"
               (:body response) => "{\"id\":1,\"valor\":10,\"tipo\":\"receita\"}")))

(facts "Existe rota para lidar com filtro de transa��o por tipo"
       (against-background
         [(db/transacoes-do-tipo "receita") => '({:id 1 :valor 2000 :tipo "receita"})
          (db/transacoes-do-tipo "despesa") => '({:id 2 :valor 89 :tipo "despesa"})
          (db/transacoes) => '({:id 1 :valor 2000 :tipo "receita"}
                               {:id 2 :valor 89 :tipo "despesa"})]
         (fact "Filtro por receita"
               (let [response (app (mock/request :get "/receitas"))]
                 (:status response) => 200
                 (:body response) => (json/generate-string {:transacoes '({:id 1 :valor 2000 :tipo "receita"})})))
         (fact "Filtro por despesa"
               (let [response (app (mock/request :get "/despesas"))]
                 (:status response) => 200
                 (:body response) => (json/generate-string {:transacoes '({:id 2 :valor 89 :tipo "despesa"})})))
         (fact "Sem filtro"
               (let [response (app (mock/request :get "/transacoes"))]
                 (:status response) => 200
                 (:body response) => (json/generate-string {:transacoes '({:id 1 :valor 2000 :tipo "receita"}
                                                                          {:id 2 :valor 89 :tipo "despesa"})})))))

(facts "Filtra transa��es por par�metros de busca na URL"
       (def livro {:id 1 :valor 88 :tipo "despesa" :rotulos ["livro" "educa��o"]})
       (def curso {:id 2 :valor 106 :tipo "despesa" :rotulos ["curso" "educa��o"]})
       (def salario {:id 3 :valor 8000 :tipo "receita" :rotulos ["sal�rio"]})

       (against-background
         [(db/transacoes-com-filtro {:rotulos ["livro" "curso"]}) => [livro curso]
          (db/transacoes-com-filtro {:rotulos "sal�rio"}) => [salario]]
         (fact "Filtro m�ltiplos r�tulos"
               (let [response (app (mock/request :get "/transacoes?rotulos=livro&rotulos=curso"))]
                 (:status response) => 200
                 (:body response) => (json/generate-string {:transacoes [livro curso]})))
         (fact "Filtro com �nico r�tulo"
               (let [response (app (mock/request :get "/transacoes?rotulos=sal�rio"))]
                 (:status response) => 200
                 (:body response) => (json/generate-string {:transacoes [salario]})))))




       
