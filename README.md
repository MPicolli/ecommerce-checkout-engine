# E-Commerce Checkout Engine

## Integrantes
* [Matheus Picolli Ishibashi](https://github.com/MPicolli)
* [Vithor Massing dos Santos](https://github.com/VithorSantos)
* [José Augusto Ferreira](https://github.com/testerapido157-star)
* [Eric Levi Sena Silveira](https://github.com/ezlss)
* [Leonardo de Medeiros Binatti](https://github.com/BinattiLeonardo)

## Descrição do Projeto
Trabalho desenvolvido para a disciplina de Qualidade de Software na Unisul 2026/1. Este projeto consiste em um motor de processamento de checkout via terminal, focado na validação rigorosa de regras de negócio em um sistema de vendas. 

O objetivo principal não é a interface gráfica, mas sim a construção de uma arquitetura resiliente e altamente testável. O sistema lida com cenários reais de entrada de usuário, como validação de cupons (validade e valor mínimo), verificação de estoque disponível e cálculo dinâmico de frete por CEP. A garantia de qualidade é assegurada por uma ampla suíte de testes unitários que cobrem tanto o "caminho feliz" quanto cenários de exceção e análise de valor limite.

## Tecnologias Previstas
* **Linguagem:** Java 17+
* **Interface:** Terminal (Console)
* **Gerenciador de Dependências:** Maven
* **Framework de Testes:** JUnit 5 (Jupiter)
* **Simulação de Dependências:** Mockito
* **Métrica de Cobertura:** JaCoCo (Java Code Coverage)

## Workflow de Versionamento
Adotamos o GitHub Flow para o desenvolvimento do projeto.

### Estrutura de Branches
* `main`: contém a versão estável do sistema
* `feature/nome-da-feature`: utilizada para desenvolvimento de novas funcionalidades, criadas a partir da `main`

### Fluxo de Trabalho
1. Criar uma branch a partir da `main`
2. Desenvolver a funcionalidade na branch criada
3. Realizar commits seguindo o padrão Conventional Commits
4. Abrir um Pull Request (PR) para a branch `main`
5. Outro integrante revisa o código
6. Após aprovação, realizar o merge na `main`

## Padrão de Commits
Adotamos o padrão Conventional Commits para manter o histórico organizado e compreensível.

### Tipos de commit utilizados:
* `feat`: nova funcionalidade
* `fix`: correção de bug
* `docs`: alterações na documentação
* `test`: criação ou alteração de testes
* `refactor`: melhoria no código sem alterar comportamento
* `chore` : tarefas de manutenção, configurações de build ou dependências

### Exemplos:
* `feat: adiciona cálculo de frete`
* `fix: corrige validação de cupom expirado`
* `docs: atualiza README com workflow`
* `test: adiciona testes do checkout`

