package com.unisul.ecommerce.service;

import com.unisul.ecommerce.exception.CarrinhoVazioException;
import com.unisul.ecommerce.exception.CupomInvalidoException;
import com.unisul.ecommerce.model.Carrinho;
import com.unisul.ecommerce.model.Cliente;
import com.unisul.ecommerce.model.ResumoPedido;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CheckoutService {

    private final CupomService cupomService;
    private final FreteService freteService;
    private final FidelidadeService fidelidadeService;

    public CheckoutService(CupomService cupomService, FreteService freteService, FidelidadeService fidelidadeService) {
        this.cupomService = cupomService;
        this.freteService = freteService;
        this.fidelidadeService = fidelidadeService;
    }

    /**
     * Processa o checkout e gera o resumo do pedido.
     * 
     * @param carrinho o carrinho com itens e cliente
     * @return ResumoPedido com todos os cálculos finalizados
     * @throws CarrinhoVazioException se o carrinho estiver vazio
     * @throws CupomInvalidoException se o cupom for inválido
     */
    public ResumoPedido finalizarPedido(Carrinho carrinho) throws CarrinhoVazioException, CupomInvalidoException {
        // Validar se o carrinho possui itens
        validarCarrinho(carrinho);

        // 1. Calcular o subtotal do carrinho
        BigDecimal subtotal = carrinho.getValorTotal();

        // 2. Aplicar cupom se existir
        BigDecimal valorComDesconto = subtotal;
        BigDecimal valorDescontos = BigDecimal.ZERO;

        if (carrinho.getCupomAplicado() != null) {
            valorComDesconto = cupomService.aplicarCupom(subtotal, carrinho.getCupomAplicado());
            valorDescontos = subtotal.subtract(valorComDesconto);
        }

        // 3. Calcular frete baseado no CEP do cliente
        BigDecimal valorFrete = freteService.calcularFrete(carrinho, carrinho.getCliente().getCep());

        // 4. Calcular o total final (com desconto + frete)
        BigDecimal totalFinal = valorComDesconto.add(valorFrete).setScale(2, RoundingMode.HALF_UP);

        // 5. Calcular e acumular pontos de fidelidade
        int pontosGanhos = calcularPontosGanhos(totalFinal);
        fidelidadeService.acumularPontos(totalFinal);

        // Atualizar saldo de pontos do cliente também (sincronizar estado local)
        Cliente cliente = carrinho.getCliente();
        if (cliente != null) {
            int novosPontos = cliente.getSaldoPontos() + pontosGanhos;
            cliente.setSaldoPontos(novosPontos);
        }

        // 6. Montar o resumo do pedido
        ResumoPedido resumo = new ResumoPedido(
                carrinho,
                subtotal,
                valorDescontos,
                valorFrete,
                pontosGanhos,
                totalFinal);

        return resumo;
    }

    /**
     * Processa o checkout com uso de pontos de fidelidade.
     * 
     * @param carrinho    o carrinho com itens e cliente
     * @param pontosAUsar quantidade de pontos a serem resgatados
     * @return ResumoPedido com desconto aplicado pelos pontos
     * @throws CarrinhoVazioException se o carrinho estiver vazio
     * @throws CupomInvalidoException se o cupom for inválido
     */
    public ResumoPedido finalizarPedidoComPontos(Carrinho carrinho, int pontosAUsar)
            throws CarrinhoVazioException, CupomInvalidoException {

        ResumoPedido resumo = finalizarPedido(carrinho);

        if (pontosAUsar > 0) {
            // Resgatar pontos (lança exceção se insuficientes)
            fidelidadeService.resgatar(pontosAUsar);

            // Converter pontos em desconto (1 ponto = R$ 0,10)
            BigDecimal descontoPontos = BigDecimal.valueOf(pontosAUsar).multiply(new BigDecimal("0.10"))
                    .setScale(2, RoundingMode.HALF_UP);

            // Aplicar desconto ao total
            BigDecimal totalComDescontoPontos = resumo.getTotalFinal().subtract(descontoPontos);
            if (totalComDescontoPontos.compareTo(BigDecimal.ZERO) < 0) {
                totalComDescontoPontos = BigDecimal.ZERO;
            }

            resumo.setValorDescontos(resumo.getValorDescontos().add(descontoPontos));
            resumo.setTotalFinal(totalComDescontoPontos.setScale(2, RoundingMode.HALF_UP));

            // Atualizar saldo de pontos do cliente localmente após resgate
            Cliente cliente = carrinho.getCliente();
            if (cliente != null) {
                int novos = cliente.getSaldoPontos() - pontosAUsar;
                cliente.setSaldoPontos(Math.max(0, novos));
            }
        }

        return resumo;
    }

    /**
     * Valida se o carrinho está pronto para checkout.
     */
    private void validarCarrinho(Carrinho carrinho) throws CarrinhoVazioException {
        if (carrinho == null || carrinho.getItens() == null || carrinho.getItens().isEmpty()) {
            throw new CarrinhoVazioException();
        }

        if (carrinho.getCliente() == null || carrinho.getCliente().getCep() == null) {
            throw new IllegalArgumentException("Cliente e CEP são obrigatórios para finalizar o pedido.");
        }
    }

    /**
     * Calcula os pontos de fidelidade ganhos com a compra.
     * Regra: 1 ponto a cada R$ 10,00 gastos
     */
    private int calcularPontosGanhos(BigDecimal valorTotal) {
        return valorTotal.divide(BigDecimal.TEN, 0, RoundingMode.DOWN).intValue();
    }
}
