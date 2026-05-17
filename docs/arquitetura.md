# Arquitetura do Projeto

## Objetivo

O projeto consiste em um motor de processamento de checkout para e-commerce, desenvolvido como aplicação de terminal (console). O foco principal não é a interface gráfica, mas a construção de uma arquitetura resiliente e altamente testável, com validação rigorosa das regras de negócio envolvidas em um fluxo de compra.

## Organização em Pacotes

O sistema foi estruturado em pacotes com responsabilidades bem definidas, seguindo o princípio de separação de responsabilidades, visando facilitar manutenção, legibilidade e testes automatizados.

```text
src/main/java/com/unisul/ecommerce/
├── model/      → entidades do domínio e validações
├── service/    → regras de negócio e processamento
└── exception/  → exceções customizadas de negócio
```

## Camada `model`

Contém as entidades principais do domínio da aplicação:

| Classe | Responsabilidade |
|---|---|
| `Produto` | Representa um produto disponível para compra, com preço e peso |
| `Cliente` | Representa o cliente com CEP e saldo de pontos de fidelidade |
| `Carrinho` | Agrupa os itens e o cupom aplicado pelo cliente |
| `ItemCarrinho` | Associa um produto a uma quantidade, calculando o subtotal |
| `Cupom` | Representa um cupom de desconto fixo ou percentual |
| `ResumoPedido` | Consolida o resultado final do checkout com todos os valores |
| `TipoCupom` | Define os tipos de desconto: `FIXO` ou `PERCENTUAL` |

As entidades implementam validações defensivas para preservar a integridade dos dados e reduzir a possibilidade de estados inválidos durante o processamento do checkout.

## Camada `service`

Responsável pela implementação das regras de negócio do sistema, separadas por responsabilidade:

| Serviço | Responsabilidade | Status |
|---|---|---|
| `FreteService` | Calcula o frete com base no CEP e peso total dos itens | Implementado |
| `CupomService` | Valida e aplica descontos fixos ou percentuais | Implementado |
| `FidelidadeService` | Gerencia acúmulo e resgate de pontos de fidelidade | Em andamento |
| `CheckoutService` | Consolida o fluxo completo do checkout | Em andamento |

A separação por serviços facilita manutenção, testes unitários isolados e utilização de mocks para simulação de dependências.

## Camada `exception`

Contém exceções customizadas que representam erros específicos de negócio:

| Exceção | Quando é lançada |
|---|---|
| `CarrinhoVazioException` | Tentativa de finalizar compra com carrinho sem itens |
| `CupomInvalidoException` | Cupom inativo, inexistente ou valor mínimo não atingido |
| `PontosInsuficientesException` | Tentativa de resgate com saldo de pontos insuficiente |

O uso de exceções específicas melhora a legibilidade do código e facilita o rastreamento de falhas durante os testes e execução do sistema.

## Fluxo Geral do Sistema

```text
Cliente
   └── Carrinho
         ├── List<ItemCarrinho>
         │       └── Produto
         └── Cupom (opcional)

              ↓ processado por

         CheckoutService
         ├── FreteService     → calcula frete por CEP e peso
         ├── CupomService     → valida e aplica desconto
         └── FidelidadeService → calcula e resgata pontos

              ↓ gera

         ResumoPedido
         (subtotal, descontos, frete, pontos e total final)
```

## Decisões de Arquitetura

### Uso de `BigDecimal` para valores monetários
Valores monetários utilizam `BigDecimal` em vez de `double` ou `float`, evitando problemas de precisão e arredondamento comuns em operações financeiras.

### Programação defensiva nas entidades
As entidades validam os próprios dados nos setters para evitar estados inválidos:
- preços e pesos negativos são ajustados para zero;
- quantidades menores que 1 são ajustadas para 1;
- saldo de pontos nunca fica negativo.

Essa abordagem garante que objetos em estado inválido nunca circulem pelo sistema.

### Uso de exceções customizadas
Exceções específicas de negócio foram utilizadas em vez de exceções genéricas, tornando o código mais legível e facilitando a identificação das causas das falhas.

### Isolamento das regras de negócio
As regras de negócio são separadas em serviços independentes, permitindo testes unitários isolados e simulação de dependências.

### Controle de versão com GitHub Flow
O projeto utiliza GitHub Flow com branches por funcionalidade, Pull Requests e revisão de código antes do merge na branch `main`, garantindo estabilidade e rastreabilidade do desenvolvimento.

## Tecnologias Utilizadas

| Tecnologia | Função |
|---|---|
| Java 17+ | Linguagem principal |
| Maven | Gerenciamento de dependências e build |
| JUnit 5 (Jupiter) | Testes unitários |
| Mockito | Simulação de dependências |
| JaCoCo | Cobertura de código |
