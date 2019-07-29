(ns financeiro.filtros-aceitacao-test
  (:require [midje.sweet :refer :all]
            [cheshire.core :as json]
            [financeiro.auxiliares :refer :all]
            [financeiro.db :as db]))

(def transacoes-aleatorias
  '({:valor 7.0M :tipo "despesa" :rotulos ["sorvete" "entretenimento"]}
    {:valor 88.0M :tipo "despesa" :rotulos ["livro" "educa��o"]}
    {:valor 106.0M :tipo "despesa" :rotulos ["curso" "educa��o"]}
    {:valor 8000.0M :tipo "receita" :rotulos ["sal�rio"]}))

(against-background
  [(before :facts
           [(iniciar-servidor porta-padrao)
            (db/limpar)])
   (after :facts (parar-servidor))]
  (fact "N�o existem receitas" :aceitacao
        (json/parse-string (conteudo "/receitas") true) => {:transacoes '()})

  (fact "N�o existem rdespesas" :aceitacao
        (json/parse-string (conteudo "/despesas") true) => {:transacoes '()})

  (fact "N�o existem transacoes" :aceitacao
        (json/parse-string (conteudo "/transacoes") true) => {:transacoes '()})

  (against-background
    [(before :facts [(db/limpar)
                     (doseq [transacao transacoes-aleatorias]
                       (db/registrar transacao))])
     (after :facts (db/limpar))]
    (fact "Existem 3 despesas" :aceitacao
          (count (:transacoes (json/parse-string (conteudo "/despesas") true))) => 3)
    (fact "Existem 1 receita" :aceitacao
          (count (:transacoes (json/parse-string (conteudo "/receitas") true))) => 1)
    (fact "Existem 4 transacoes" :aceitacao
          (count (:transacoes (json/parse-string (conteudo "/transacoes") true))) => 4)
    (fact "Existe 1 receita com r�tulo 'sal�rio'"
          (count (:transacoes (json/parse-string (conteudo "/transacoes?rotulos=sal�rio") true))) => 1)
    (fact "Existem 2 despesas com r�tulo 'livro' ou 'curso'"
          (count (:transacoes (json/parse-string (conteudo "/transacoes?rotulos=livro&rotulos=curso") true))) => 2)
    (fact "Existem 2 despesas com r�tulo 'educa��o'"
          (count (:transacoes (json/parse-string (conteudo "/transacoes?rotulos=educa��o") true))) => 2)))






