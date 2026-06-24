package com.unisul.ecommerce.service;

import com.unisul.ecommerce.model.Carrinho;
import com.unisul.ecommerce.model.ItemCarrinho;
import java.math.BigDecimal;

public class FreteService {

    private static final BigDecimal LIMITE_FRETE_GRATIS = new BigDecimal("200.00");
    private static final BigDecimal FRETE_SUL_SUDESTE = new BigDecimal("15.00");
    private static final BigDecimal FRETE_OUTROS = new BigDecimal("30.00");
    private static final BigDecimal ADICIONAL_POR_KG = new BigDecimal("2.00");

    public BigDecimal calcularFrete(Carrinho carrinho, String cep) {

        if (carrinho.getValorTotal().compareTo(LIMITE_FRETE_GRATIS) >= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal valorFinal = isSulSudeste(cep) ? FRETE_SUL_SUDESTE : FRETE_OUTROS;

        BigDecimal pesoTotal = BigDecimal.ZERO;
        if (carrinho.getItens() != null) {
            for (ItemCarrinho item : carrinho.getItens()) {
                BigDecimal pesoProduto = item.getProduto().getPesoKg();
                BigDecimal quantidade = BigDecimal.valueOf(item.getQuantidade());
                pesoTotal = pesoTotal.add(pesoProduto.multiply(quantidade));
            }
        }

        if (pesoTotal.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal custoAdicional = pesoTotal.multiply(ADICIONAL_POR_KG);
            valorFinal = valorFinal.add(custoAdicional);
        }

        return valorFinal;
    }

    private boolean isSulSudeste(String cep) {
        if (cep == null || cep.isBlank()) {
            throw new IllegalArgumentException("O CEP não pode ser nulo ou vazio para o cálculo de frete.");
        }

        String cepLimpo = cep.replaceAll("[^0-9]", "");

        if (cepLimpo.length() != 8) {
            throw new IllegalArgumentException("CEP inválido. O CEP deve conter 8 dígitos.");
        }

        char primeiroDigito = cepLimpo.charAt(0);

        return primeiroDigito == '0' || primeiroDigito == '1' ||
                primeiroDigito == '2' || primeiroDigito == '3' ||
                primeiroDigito == '8' || primeiroDigito == '9';
    }
}