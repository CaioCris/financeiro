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

(facts "Filtra transa��es por tipo"
       (def transacoes-aleatorias '({:valor 2 :tipo "despesa"}
                                    {:valor 10 :tipo "receita"}
                                    {:valor 200 :tipo "despesa"}
                                    {:valor 1000 :tipo "receita"}))
       (against-background [(before :facts [(limpar)
                                            (doseq [transacao transacoes-aleatorias]
                                              (registrar transacao))])]

                           (fact "encontra apenas as receitas"
                                 (transacoes-do-tipo "receita") => '({:valor 10 :tipo "receita"}
                                                                     {:valor 1000 :tipo "receita"}))
                           (fact "encontra apenas as despesas"
                                 (transacoes-do-tipo "despesa") => '({:valor 2 :tipo "despesa"}
                                                                     {:valor 200 :tipo "despesa"}))))

(facts "Filtra transa��es por r�tulo"
       (def transacoes-aleatorias
         '({:valor   7.0M :tipo "despesa"
            :rotulos ["sorvete" "entretenimento"]}
           {:valor   88.0M :tipo "despesa"
            :rotulos ["livro" "educa��o"]}
           {:valor   106.0M :tipo "despesa"
            :rotulos ["curso" "educa��o"]}
           {:valor   8000.0M :tipo "receita"
            :rotulos ["sal�rio"]}))
       (against-background
         [(before :facts [(limpar)
                          (doseq [transacao transacoes-aleatorias]
                            (registrar transacao))])]
         (fact "encontra as 2 transa��es com r�tulo 'sal�rio'"
               (transacoes-com-filtro {:rotulos ["sal�rio"]}) => '({:valor 8000.0M :tipo "receita" :rotulos ["sal�rio"]}))
         (fact "encontra as 2 transa��es com r�tulo 'educa��o'"
               (transacoes-com-filtro {:rotulos ["educa��o"]}) => '({:valor 88.0M :tipo "despesa" :rotulos ["livro" "educa��o"]}
                                                                    {:valor 106.0M :tipo "despesa" :rotulos ["curso" "educa��o"]}))
         (fact "encontra as 2 transa��es com r�tulo 'livro' ou 'curso'"
               (transacoes-com-filtro {:rotulos ["livro" "curso"]}) => '({:valor 88.0M :tipo "despesa" :rotulos ["livro" "educa��o"]}
                                                                         {:valor 106.0M :tipo "despesa" :rotulos ["curso" "educa��o"]}))))


