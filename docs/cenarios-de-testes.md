# Cenários de Teste

## Objetivo

Este documento descreve os cenários de teste planejados e implementados para validação das regras de negócio do sistema, seguindo a especificação técnica definida para a Sprint 2. A estratégia adota testes unitários por serviço, uso de Mockito para simulação de dependências e análise de valor limite nos cenários críticos.

## Estratégia de Qualidade

| Tipo de Teste | Ferramenta | Objetivo |
|---|---|---|
| Unitário | JUnit 5 (Jupiter) | Validar cada serviço de forma isolada |
| Simulação de dependências | Mockito | Testar o CheckoutService sem depender dos demais serviços reais |
| Cobertura de código | JaCoCo | Garantir cobertura mínima de 100% nas classes de serviço |

## FreteService — Motor de Cálculo de Frete

### Regras de negócio cobertas
- RN04: Frete grátis para compras iguais ou superiores a R$ 200,00
- RN05: O cálculo segue a tabela regional (Sul/Sudeste: R$15,00 | Outros: R$30,00), acrescido de R$2,00 por kg do peso total dos produtos
- RF03: Validação do formato do CEP informado

### Cenários implementados

| # | Cenário | Entrada | Resultado Esperado | Status |
|---|---|---|---|---|
| 1 | Frete grátis acima do limite | Carrinho R$ 200,01 | `BigDecimal.ZERO` | Implementado |
| 2 | Frete grátis no limite exato (valor limite) | Carrinho R$ 200,00 | `BigDecimal.ZERO` | Implementado |
| 3 | Frete base Sul/Sudeste | CEPs prefixo 01, 13, 20, 30, 88, 90 | R$ 15,00 | Implementado |
| 4 | Frete base outras regiões | CEPs prefixo 40, 50, 60, 70 | R$ 30,00 | Implementado |
| 5 | Acréscimo por peso | 2 itens de 1,5kg = 3kg totais | R$ 15,00 + R$ 6,00 = R$ 21,00 | Implementado |
| 6 | Carrinho sem itens | Sem produtos adicionados | R$ 15,00 (apenas base) | Implementado |
| 7 | CEP nulo | `null` | `IllegalArgumentException` | Implementado |
| 8 | CEP vazio ou em branco | `""` ou `"   "` | `IllegalArgumentException` | Implementado |
| 9 | CEP com tamanho inválido | `"88000"` ou `"88000-0000"` | `IllegalArgumentException` | Implementado |
| 10 | Carrinho nulo | `null` | `NullPointerException` | Implementado |

## CupomService — Validação e Aplicação de Cupons

### Regras de negócio cobertas
- RN01: Cupons não são cumulativos
- RN02: Desconto fixo não pode resultar em valor negativo (mínimo R$ 0,00)
- RN03: Lançar `CupomInvalidoException` para cupom inativo ou valor mínimo não atingido
- RF02: Aplicação de desconto percentual e fixo

### Cenários implementados

| # | Cenário | Entrada | Resultado Esperado | Status |
|---|---|---|---|---|
| 1 | Cupom percentual válido | Cupom 10%, carrinho R$ 100,00 | R$ 90,00 | Implementado |
| 2 | Cupom fixo maior que o total | Cupom R$ 50,00, carrinho R$ 40,00 | R$ 0,00 (nunca negativo) | Implementado |
| 3 | Cupom inativo | `ativo = false` | `CupomInvalidoException` | Implementado |
| 4 | Valor abaixo do mínimo exigido | Cupom exige R$ 100, carrinho R$ 40 | `CupomInvalidoException` | Pendente |
| 5 | Cupom fixo válido | Cupom R$ 20,00, carrinho R$ 80,00 | R$ 60,00 | Pendente |

## FidelidadeService — Pontos de Fidelidade

### Regras de negócio cobertas
- RN06: Acúmulo de 1 ponto a cada R$ 10,00 gastos
- RN07: Resgate mínimo de 100 pontos

### Cenários planejados

| # | Cenário | Entrada | Resultado Esperado | Status |
|---|---|---|---|---|
| 1 | Acúmulo de pontos | Compra R$ 150,00 | 15 pontos acumulados | Pendente |
| 2 | Acúmulo com valor não múltiplo de 10 | Compra R$ 155,00 | 15 pontos (truncado) | Pendente |
| 3 | Resgate com saldo suficiente | 150 pontos, resgatar 100 | Saldo: 50 pontos | Pendente |
| 4 | Resgate abaixo do mínimo (valor limite) | 80 pontos disponíveis | `IllegalStateException` | Pendente |
| 5 | Resgate maior que o saldo | Resgatar 200 com 100 disponíveis | `IllegalArgumentException` | Pendente |
| 6 | Valor gasto zero ou negativo | `valorGasto = 0` | Nenhum ponto acumulado | Pendente |

## CheckoutService — Processamento do Pedido

### Regras de negócio cobertas
- RF01: Cálculo do subtotal dos produtos
- RF05: Geração do resumo completo da compra

### Cenários planejados

| # | Cenário | Entrada | Resultado Esperado | Status |
|---|---|---|---|---|
| 1 | Checkout completo (caminho feliz) | Carrinho com itens, cupom válido, CEP válido | `ResumoPedido` com totais corretos |  Pendente |
| 2 | Checkout com carrinho vazio | Carrinho sem itens | `CarrinhoVazioException` |  Pendente |
| 3 | Checkout sem cupom | Carrinho com itens, sem cupom | `ResumoPedido` sem desconto |  Pendente |
| 4 | Checkout com frete grátis | Carrinho acima de R$ 200,00 | Frete zerado no resumo |  Pendente |
| 5 | Simulação de estoque vazio | Mock retorna sem estoque | Sistema rejeita o pedido |  Pendente |

> Os testes do `CheckoutService` utilizarão **Mockito** para simular `FreteService`, `CupomService` e `FidelidadeService`, isolando a lógica de consolidação do pedido das implementações reais de cada serviço.
