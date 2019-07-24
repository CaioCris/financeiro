(ns financeiro.saldo-aceitacao-test
  (:require [midje.sweet :refer :all]
            [cheshire.core :as json]
            [financeiro.auxiliares :refer :all]
            [financeiro.db :as db]
            [clj-http.client :as http]))


(against-background [(before :facts [(iniciar-servidor porta-padrao) (db/limpar)])
                     (after :facts (parar-servidor))]
                    (fact "O Saldo inicial � 0" :aceitacao
                          (json/parse-string (conteudo "/saldo") true) => {:saldo 0})

                    (fact "O saldo � 10 quando a �nica transa��o � uma receita de 10" :aceitacao
                          (http/post (endereco-para "/transacoes") (receita 10))
                          (json/parse-string (conteudo "/saldo") true) => {:saldo 10})

                    (fact "O saldo � 10000 quando criamos duas receitas de 2000 e uma despesa de 3000" :aceitacao
                          (http/post (endereco-para "/transacoes") (receita 2000))
                          (http/post (endereco-para "/transacoes") (receita 2000))
                          (http/post (endereco-para "/transacoes") (despesa 3000))
                          (json/parse-string (conteudo "/saldo") true) => {:saldo 1000})

                    (fact "Rejeita uma transa��o sem valor" :aceitacao
                          (let [resposta (http/post (endereco-para "/transacoes")
                                                    (conteudo-como-json {:tipo "receita"}))]
                            (:status resposta) => 422))

                    (fact "Rejeita uma transa��o com valor negativo" :aceitacao
                          (let [resposta (http/post (endereco-para "/transacoes")
                                                    (receita -100))]
                            (:status resposta) => 422))

                    (fact "Rejeita uma transa��o com valor que n�o � um n�mero" :aceitacao
                          (let [resposta (http/post (endereco-para "/transacoes")
                                                    (receita "mil"))]
                            (:status resposta) => 422))

                    (fact "Rejeita uma transa��o com tipo desconhecido" :aceitacao
                          (let [resposta (http/post (endereco-para "/transacoes")
                                                    (conteudo-como-json {:valor 70, :tipo "investimento"}))]
                            (:status resposta) => 422)))






