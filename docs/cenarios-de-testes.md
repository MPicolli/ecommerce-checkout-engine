# Cenários de Teste

## Objetivo

Este documento descreve os cenários de teste planejados e implementados para validação das regras de negócio do sistema, seguindo a especificação técnica definida para a Sprint 2. A estratégia adota testes unitários por serviço, uso de Mockito para simulação de dependências e análise de valor limite nos cenários críticos.

## Estratégia de Qualidade

| Tipo de Teste | Ferramenta | Objetivo |
|---|---|---|
| Unitário | JUnit 5 (Jupiter) | Validar cada serviço de forma isolada |
| Simulação de dependências | Mockito | Testar o CheckoutService sem depender dos demais serviços reais |
| Cobertura de código | JaCoCo | Garantir cobertura mínima de 80% nas classes de serviço implementadas |

## FreteService — Motor de Cálculo de Frete

### Regras de negócio cobertas
- RN05: Frete grátis para compras com valor total maior ou igual a R$ 200,00
- RN06: Frete base de R$ 15,00 para região Sul/Sudeste e R$ 30,00 para demais regiões
- RN07: Acréscimo de R$ 2,00 por quilograma sobre o peso total dos produtos
- RN08: Validação do CEP informado

### Cenários implementados

| # | Cenário | Entrada | Resultado Esperado | Regra/Requisito | Status |
|---|---|---|---|---|---|
| 1 | Frete grátis acima do limite | Carrinho R$ 200,01 | `BigDecimal.ZERO` | RN05 | Implementado |
| 2 | Frete grátis no limite exato (valor limite) | Carrinho R$ 200,00 | `BigDecimal.ZERO` | RN05 | Implementado |
| 3 | Frete base Sul/Sudeste | CEPs prefixo 01, 13, 20, 30, 88, 90 | R$ 15,00 | RN06 | Implementado |
| 4 | Frete base outras regiões | CEPs prefixo 40, 50, 60, 70 | R$ 30,00 | RN06 | Implementado |
| 5 | Acréscimo por peso | 2 itens de 1,5kg = 3kg totais | R$ 15,00 + R$ 6,00 = R$ 21,00 | RN07 | Implementado |
| 6 | Carrinho sem itens | Sem produtos adicionados | R$ 15,00 (apenas base) | RN06 | Implementado |
| 7 | CEP nulo | `null` | `IllegalArgumentException` | RN08 | Implementado |
| 8 | CEP vazio ou em branco | `""` ou `"   "` | `IllegalArgumentException` | RN08 | Implementado |
| 9 | CEP com tamanho inválido | `"88000"` ou `"88000-0000"` | `IllegalArgumentException` | RN08 | Implementado |
| 10 | Carrinho nulo | `null` | `NullPointerException` | Validação defensiva | Implementado |

## CupomService — Validação e Aplicação de Cupons

### Regras de negócio cobertas
- RN01: Cupons não são cumulativos
- RN02: Desconto fixo não pode resultar em valor negativo (mínimo R$ 0,00)
- RN03: Lançar `CupomInvalidoException` para cupom inativo, inexistente ou valor mínimo não atingido
- RN04: Cupons percentuais não podem ultrapassar 100% de desconto

### Cenários implementados

| # | Cenário | Entrada | Resultado Esperado | Regra/Requisito | Status |
|---|---|---|---|---|---|
| 1 | Cupom percentual válido | Cupom 10%, carrinho R$ 100,00 | R$ 90,00 | RF02 | Implementado |
| 2 | Cupom fixo maior que o total | Cupom R$ 50,00, carrinho R$ 40,00 | R$ 0,00 (nunca negativo) | RN02 | Implementado |
| 3 | Cupom inativo | `ativo = false` | `CupomInvalidoException` | RN03 | Implementado |
| 4 | Valor abaixo do mínimo exigido | Cupom exige R$ 100, carrinho R$ 40 | `CupomInvalidoException` | RN03 | Implementado |
| 5 | Cupom fixo válido | Cupom R$ 25,00, carrinho R$ 150,00 | R$ 125,00 | RN02 | Implementado |

## FidelidadeService — Pontos de Fidelidade

### Regras de negócio cobertas
- RN06: Acúmulo de 1 ponto a cada R$ 10,00 gastos
- RN07: Resgate mínimo de 100 pontos

### Regras de negócio cobertas
- RN09: Acúmulo de 1 ponto a cada R$ 10,00 gastos
- RN10: Resgate permitido apenas com saldo mínimo de 100 pontos
- RN11: Exceção ao tentar resgatar quantidade superior ao saldo disponível

### Cenários implementados

| # | Cenário | Entrada | Resultado Esperado | Regra/Requisito | Status |
|---|---|---|---|---|---|
| 1 | Inicialização com zero pontos (construtor padrão) | `new FidelidadeService()` | 0 pontos | — | Implementado |
| 2 | Inicialização com pontos positivos (construtor sobrecarregado) | `new FidelidadeService(150)` | 150 pontos | — | Implementado |
| 3 | Construtor com pontos negativos | `new FidelidadeService(-10)` | IllegalArgumentException | Validação defensiva | Implementado |
| 4 | Acúmulo com valor não múltiplo de 10 | Compra R$ 29,90 | 2 pontos (truncado) | RN09 | Implementado |
| 5 | Valor gasto menor que R$ 10,00 | Compra R$ 9,99 | 0 pontos | RN09 | Implementado |
| 6 | Valor gasto nulo | null | Nenhum ponto acumulado | RN09 | Implementado |
| 7 | Valor gasto zero ou negativo | 0 ou -50,00 | Nenhum ponto acumulado | RN09 | Implementado |
| 8 | Resgate com saldo suficiente | 150 pontos, resgatar 50 | Saldo: 100 pontos | RN10 | Implementado |
| 9 | Resgate com saldo exato | 100 pontos, resgatar 100 | Saldo: 0 pontos | RN10 | Implementado |
| 10 | Resgate com saldo abaixo do mínimo (valor limite: 99) | 99 pontos disponíveis | IllegalStateException | RN10 | Implementado |
| 11 | Resgate maior que o saldo | Resgatar 200 com 150 disponíveis | IllegalArgumentException | RN11 | Implementado |
| 12 | Resgate de zero ou valor negativo | resgatar(0) ou resgatar(-50) | IllegalArgumentException | Validação defensiva | Implementado |
| 13 | Verificação de elegibilidade para resgate (true) | 100 ou 101 pontos | true | RN10 | Implementado |
| 14 | Verificação de elegibilidade para resgate (false) | 99 pontos | false | RN10 | Implementado |

## CheckoutService — Processamento do Pedido

### Regras de negócio cobertas
- RF01: Cálculo do subtotal dos produtos
- RF05: Geração do resumo completo da compra
- RN08: Validação de cliente e CEP
- RN10: Uso de pontos de fidelidade no checkout
- RN11: Tratamento de pontos insuficientes para resgate

### Cenários implementados

| # | Cenário | Entrada | Resultado Esperado | Regra/Requisito | Status |
|---|---|---|---|---|---|
| 1 | Checkout completo sem cupom | Carrinho com itens, CEP válido | `ResumoPedido` com totais corretos | RF01, RF05 | Implementado |
| 2 | Checkout com cupom percentual | Cupom 10%, carrinho R$ 200,00 | Desconto de R$ 20,00 aplicado | RF02 | Implementado |
| 3 | Checkout com cupom fixo | Cupom R$ 25,00, carrinho R$ 150,00 | Total R$ 155,00 com frete | RN02 | Implementado |
| 4 | Checkout com carrinho vazio | Carrinho sem itens | `CarrinhoVazioException` | RF05 | Implementado |
| 5 | Checkout sem cliente ou CEP | Cliente nulo | `IllegalArgumentException` | RN08 | Implementado |
| 6 | Checkout com uso de pontos | 100 pontos = R$ 10,00 de desconto | Total reduzido corretamente | RN10 | Implementado |
| 7 | Checkout com pontos insuficientes | Saldo insuficiente para resgate | `IllegalArgumentException` | RN11 | Implementado |
| 8 | Frete integrado com peso | Produto 5kg, CEP SP | Frete R$ 25,00 | RN07 | Implementado |
| 9 | Checkout com múltiplos itens | 2 produtos diferentes | Subtotal e total corretos | RF01 | Implementado |

> Os testes do `CheckoutService` utilizam Mockito para simular `FreteService`, `CupomService` e `FidelidadeService`, isolando a lógica de consolidação do pedido das implementações reais de cada serviço.
