(ns financeiro.transacoes-test
  (:require [midje.sweet :refer :all]
            [financeiro.transacoes :refer :all]))

(fact "Uma transa��o sem valor n�o � v�lida"
      (valida? {:tipo "receita"}) => false)

(fact "Uma transa��o com valor neativo n�o � v�lida"
      (valida? {:valor -10, :tipo "receita"}) => false)

(fact "Uma transa��o com valor n�o num�rico n�o � v�lida"
      (valida? {:valor "mil", :tipo "receita"}) => false)

(fact "Uma transa��o com tipo desconhecido n�o � v�lida"
      (valida? {:valor 8, :tipo "investimento"}) => false)

(fact "Uma transa��o com valor num�rico positivo e com tipo conhecido � v�lida"
      (valida? {:valor 230, :tipo "receita"}) => true)