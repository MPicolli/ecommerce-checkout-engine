# BDD - Especificação por comportamento
## E-Commerce Checkout Engine

Este documento descreve o comportamento esperado do sistema do ponto de vista do usuário,
utilizando o formato Gherkin (Given/When/Then). Cada Feature corresponde a uma
funcionalidade prevista no escopo do projeto e reflete o comportamento real implementado
nos serviços `FreteService`, `CupomService`, `FidelidadeService` e `CheckoutService`.

## Feature: Cálculo de Subtotal

> RF01 - O sistema deve calcular a soma dos produtos (Preço x Quantidade).

```gherkin
Feature: Cálculo de Subtotal do Carrinho

  Scenario: Calcular subtotal com um único produto
    Given um carrinho com 1 unidade de "Notebook" no valor de R$ 2.500,00
    When o subtotal do carrinho é calculado
    Then o subtotal deve ser R$ 2.500,00

  Scenario: Calcular subtotal com múltiplos produtos
    Given um carrinho com 1 unidade de "Notebook" no valor de R$ 2.000,00
    And 2 unidades de "Mouse" no valor de R$ 50,00 cada
    When o subtotal do carrinho é calculado
    Then o subtotal deve ser R$ 2.100,00

  Scenario: Calcular subtotal com carrinho vazio
    Given um carrinho sem produtos
    When o subtotal do carrinho é calculado
    Then o subtotal deve ser R$ 0,00

  Scenario: Adicionar o mesmo produto duas vezes ao carrinho
    Given um carrinho com 1 unidade de "Teclado" no valor de R$ 150,00
    When o cliente adiciona mais 2 unidades do mesmo "Teclado"
    Then o carrinho deve conter 3 unidades de "Teclado"
    And o subtotal deve ser R$ 450,00
```

## Feature: Aplicação de Cupons

> RF02 - O sistema deve validar e aplicar descontos percentuais ou fixos.

```gherkin
Feature: Aplicação de Cupons de Desconto

  Scenario: Aplicar cupom de desconto percentual válido
    Given um carrinho com subtotal de R$ 100,00
    And um cupom percentual de 10% ativo
    When o cupom é aplicado ao carrinho
    Then o valor final deve ser R$ 90,00

  Scenario: Aplicar cupom de desconto fixo válido
    Given um carrinho com subtotal de R$ 150,00
    And um cupom fixo de R$ 25,00 ativo
    When o cupom é aplicado ao carrinho
    Then o valor final deve ser R$ 125,00

  Scenario: Cupom fixo não pode resultar em valor negativo
    Given um carrinho com subtotal de R$ 40,00
    And um cupom fixo de R$ 50,00 ativo
    When o cupom é aplicado ao carrinho
    Then o valor final deve ser R$ 0,00

  Scenario: Cupom percentual não pode ultrapassar 100% de desconto
    Given um cupom percentual configurado com 150% de desconto
    When o cupom é cadastrado no sistema
    Then o percentual de desconto é ajustado automaticamente para 100%

  Scenario: Cupom inativo não pode ser utilizado
    Given um carrinho com subtotal de R$ 100,00
    And um cupom de R$ 10,00 inativo
    When o cliente tenta aplicar o cupom
    Then o sistema deve lançar CupomInvalidoException

  Scenario: Cupom abaixo do valor mínimo de compra não pode ser utilizado
    Given um carrinho com subtotal de R$ 40,00
    And um cupom que exige valor mínimo de R$ 100,00
    When o cliente tenta aplicar o cupom
    Then o sistema deve lançar CupomInvalidoException

  Scenario: O carrinho permite apenas um cupom aplicado por vez
    Given um carrinho com o cupom "PROMO10" já aplicado
    When o cliente aplica o cupom "PROMO20"
    Then o cupom "PROMO20" substitui o cupom "PROMO10"
    And apenas um desconto é considerado no cálculo do checkout
```

## Feature: Cálculo de Frete

> RF03 - O sistema deve definir o custo de envio baseado em CEP e peso.

```gherkin
Feature: Cálculo de Frete por Região e Peso

  Scenario: Frete grátis para compras com valor igual a R$ 200,00
    Given um carrinho com valor total de R$ 200,00
    When o frete é calculado para o CEP "88000-000"
    Then o frete deve ser R$ 0,00

  Scenario: Frete grátis para compras acima de R$ 200,00
    Given um carrinho com valor total de R$ 350,00
    When o frete é calculado para o CEP "01000-000"
    Then o frete deve ser R$ 0,00

  Scenario: Frete base para região Sul/Sudeste
    Given um carrinho com valor total de R$ 100,00 e peso de 0kg
    When o frete é calculado para o CEP "88000-000"
    Then o frete deve ser R$ 15,00

  Scenario: Frete base para demais regiões
    Given um carrinho com valor total de R$ 100,00 e peso de 0kg
    When o frete é calculado para o CEP "40000-000"
    Then o frete deve ser R$ 30,00

  Scenario: Acréscimo de R$ 2,00 por kg no frete
    Given um carrinho com valor total de R$ 50,00
    And produtos com peso total de 3kg
    When o frete é calculado para o CEP "88000-000"
    Then o frete deve ser R$ 21,00

  Scenario: CEP nulo não é aceito pelo sistema
    Given um carrinho com produtos
    When o cliente tenta calcular o frete sem informar o CEP
    Then o sistema deve lançar IllegalArgumentException

  Scenario: CEP vazio ou em branco não é aceito pelo sistema
    Given um carrinho com produtos
    When o cliente informa o CEP "" ou "   "
    Then o sistema deve lançar IllegalArgumentException

  Scenario: CEP com formato inválido não é aceito pelo sistema
    Given um carrinho com produtos
    When o cliente informa o CEP "88000" ou "88000-0000"
    Then o sistema deve lançar IllegalArgumentException
```

## Feature: Gestão de Fidelidade

> RF04 - O sistema deve permitir acúmulo e resgate de pontos conforme as regras definidas.

```gherkin
Feature: Acúmulo e Resgate de Pontos de Fidelidade

  Scenario: Inicializar serviço com zero pontos
    Given um FidelidadeService criado com o construtor padrão
    Then o saldo inicial deve ser 0 pontos

  Scenario: Inicializar serviço com pontos existentes
    Given um FidelidadeService criado com saldo inicial de 150 pontos
    Then o saldo deve ser 150 pontos

  Scenario: Saldo inicial negativo não é permitido
    Given um FidelidadeService sendo criado com saldo inicial de -10 pontos
    Then o sistema deve lançar IllegalArgumentException

  Scenario: Acumular pontos após uma compra com valor não múltiplo de R$ 10,00
    Given um cliente com saldo de 0 pontos
    When o cliente realiza uma compra no valor de R$ 29,90
    Then o cliente deve acumular 2 pontos

  Scenario: Compra com valor menor que R$ 10,00 não gera pontos
    Given um cliente com saldo de 0 pontos
    When o cliente realiza uma compra no valor de R$ 9,99
    Then o cliente deve continuar com 0 pontos

  Scenario: Compra com valor nulo não gera pontos
    Given um cliente com saldo de 0 pontos
    When o sistema tenta acumular pontos com valor nulo
    Then o cliente deve continuar com 0 pontos

  Scenario: Compra com valor zero ou negativo não gera pontos
    Given um cliente com saldo de 0 pontos
    When o sistema tenta acumular pontos com valor R$ 0,00 ou negativo
    Then o cliente deve continuar com 0 pontos

  Scenario: Resgatar pontos com saldo suficiente
    Given um cliente com saldo de 150 pontos
    When o cliente resgata 50 pontos
    Then o saldo do cliente deve ser 100 pontos

  Scenario: Resgatar todo o saldo disponível
    Given um cliente com saldo de 100 pontos
    When o cliente resgata 100 pontos
    Then o saldo do cliente deve ser 0 pontos

  Scenario: Resgate não permitido com saldo abaixo de 100 pontos (valor limite: 99)
    Given um cliente com saldo de 99 pontos
    When o cliente tenta resgatar pontos
    Then o sistema deve lançar IllegalStateException

  Scenario: Resgate não permitido acima do saldo disponível
    Given um cliente com saldo de 150 pontos
    When o cliente tenta resgatar 200 pontos
    Then o sistema deve lançar IllegalArgumentException

  Scenario: Resgate de quantidade zero ou negativa não é permitido
    Given um cliente com saldo de 150 pontos
    When o cliente tenta resgatar 0 pontos
    Then o sistema deve lançar IllegalArgumentException

  Scenario: Cliente com saldo igual ou acima de 100 pontos está elegível para resgate
    Given um cliente com saldo de 100 pontos
    When o sistema verifica a elegibilidade para resgate
    Then o resultado deve ser verdadeiro

  Scenario: Cliente com saldo abaixo de 100 pontos não está elegível para resgate
    Given um cliente com saldo de 99 pontos
    When o sistema verifica a elegibilidade para resgate
    Then o resultado deve ser falso

  Scenario: Cada ponto resgatado equivale a R$ 0,10 de desconto
    Given um cliente com saldo de 100 pontos
    When o cliente resgata 100 pontos no checkout
    Then o valor final do pedido deve ser reduzido em R$ 10,00
```

## Feature: Resumo de Compra

> RF05 - O sistema deve gerar o resumo com todos os valores e validações do pedido.

```gherkin
Feature: Processamento e Resumo do Checkout

  Scenario: Finalizar pedido com sucesso sem cupom
    Given um carrinho com produtos no valor de R$ 2.500,00
    And um cliente com CEP "88000-000"
    When o checkout é finalizado
    Then o resumo do pedido deve conter subtotal de R$ 2.500,00
    And o frete deve ser R$ 15,00
    And o total final deve ser R$ 2.515,00
    And o cliente deve acumular 251 pontos

  Scenario: Finalizar pedido com cupom percentual aplicado
    Given um carrinho com produtos no valor de R$ 200,00
    And um cupom percentual de 10% ativo
    And um cliente com CEP "01000-000"
    When o checkout é finalizado
    Then o resumo deve conter desconto de R$ 20,00
    And o frete deve ser R$ 0,00
    And o total final deve ser R$ 180,00
    And o cliente deve acumular 18 pontos

  Scenario: Finalizar pedido com cupom fixo aplicado
    Given um carrinho com 1 unidade de "Teclado" de R$ 150,00 e 0,8kg
    And um cupom fixo de R$ 25,00 ativo
    And um cliente com CEP "90000-000"
    When o checkout é finalizado
    Then o resumo deve conter desconto de R$ 25,00
    And o frete deve ser R$ 16,60
    And o total final deve ser R$ 141,60
    And o cliente deve acumular 14 pontos

  Scenario: Finalizar pedido com múltiplos produtos no carrinho
    Given um carrinho com 1 unidade de "Notebook" no valor de R$ 2.000,00
    And 2 unidades de "Mouse" no valor de R$ 50,00 cada
    And um cliente com CEP "88000-000"
    When o checkout é finalizado
    Then o subtotal do resumo deve ser R$ 2.100,00
    And o frete deve ser R$ 0,00 por ultrapassar o limite de frete grátis
    And o total final deve ser R$ 2.100,00
    And o cliente deve acumular 210 pontos

  Scenario: Finalizar pedido com produto pesado aplicando acréscimo de frete
    Given um carrinho com 1 unidade de "Produto Pesado" de R$ 500,00 e 5kg
    And um cliente com CEP "02000-000"
    When o checkout é finalizado
    Then o frete do resumo deve ser R$ 25,00
    And o total final deve ser R$ 525,00
    And o cliente deve acumular 52 pontos

  Scenario: Finalizar pedido com uso de pontos de fidelidade
    Given um carrinho com produtos no valor de R$ 600,00
    And um cliente com saldo de 500 pontos e CEP "88000-000"
    When o checkout é finalizado com resgate de 100 pontos
    Then o resumo deve conter desconto de R$ 10,00 referente aos pontos resgatados
    And o total final deve ser R$ 610,00
    And o cliente deve acumular 61 novos pontos sobre o total final após o resgate

  Scenario: Não é possível finalizar checkout com resgate de pontos insuficientes
    Given um cliente com saldo de 50 pontos e CEP "88000-000"
    And um carrinho com produtos
    When o cliente tenta finalizar o checkout resgatando 150 pontos
    Then o sistema deve lançar IllegalStateException

  Scenario: Não é possível finalizar pedido com carrinho vazio
    Given um carrinho sem produtos
    When o cliente tenta finalizar o checkout
    Then o sistema deve lançar CarrinhoVazioException

  Scenario: Não é possível finalizar pedido sem informar o cliente ou o CEP
    Given um carrinho com produtos
    And nenhum cliente associado ao carrinho
    When o cliente tenta finalizar o checkout
    Then o sistema deve lançar IllegalArgumentException
```
