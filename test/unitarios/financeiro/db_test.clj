(ns financeiro.db-test
  (:require [midje.sweet :refer :all]
            [financeiro.db :refer :all]))

(facts "Guarda uma transa��o num �tomo"
       (against-background [(before :facts (limpar))]
                           (fact "a cole��o de transa��es inicia vazia"
                                 (count (transacoes)) => 0)

                           (fact "a transa��o � o primeiro registro"
                                 (registrar {:valor 7, :tipo "receita"}) => {:id 1, :valor 7, :tipo "receita"}
                                 (count (transacoes)) => 1)))

(facts "Calcula o saldo dada uma cole��o de transa��es"
       (against-background [(before :facts (limpar))]
                           (fact "saldo � positivo quando s� tem receita"
                                 (registrar {:valor 1, :tipo "receita"})
                                 (registrar {:valor 10, :tipo "receita"})
                                 (registrar {:valor 100, :tipo "receita"})
                                 (registrar {:valor 1000, :tipo "receita"})
                                 (saldo) => 1111)
                           (fact "saldo � negativo quando s� tem despesa"
                                 (registrar {:valor 2, :tipo "despesa"})
                                 (registrar {:valor 20, :tipo "despesa"})
                                 (registrar {:valor 200, :tipo "despesa"})
                                 (registrar {:valor 2000, :tipo "despesa"})
                                 (saldo) => -2222)
                           (fact "saldo � a soma das receitas menos a soma das despesas"
                                 (registrar {:valor 2, :tipo "despesa"})
                                 (registrar {:valor 10, :tipo "receita"})
                                 (registrar {:valor 200, :tipo "despesa"})
                                 (registrar {:valor 1000, :tipo "receita"})
                                 (saldo) => 808)))


