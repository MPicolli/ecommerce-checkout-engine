package com.unisul.ecommerce.service;

import com.unisul.ecommerce.model.Carrinho;
import com.unisul.ecommerce.model.ItemCarrinho;
import java.math.BigDecimal;

public class FreteService {

    // Regras de Negócio (Constantes)
    private static final BigDecimal LIMITE_FRETE_GRATIS = new BigDecimal("200.00");
    private static final BigDecimal FRETE_SUL_SUDESTE = new BigDecimal("15.00");
    private static final BigDecimal FRETE_OUTROS = new BigDecimal("30.00");
    private static final BigDecimal ADICIONAL_POR_KG = new BigDecimal("2.00");

    // Calcula o frete baseado no carrinho (peso e valor total) e no CEP do cliente.
    public BigDecimal calcularFrete(Carrinho carrinho, String cep) {

        // RN04: Frete grátis para compras acima de R$ 200,00
        if (carrinho.getValorTotal() != null && carrinho.getValorTotal().compareTo(LIMITE_FRETE_GRATIS) >= 0) {
            return BigDecimal.ZERO;
        }

        // RF03: Definir custo base de envio baseado na região do CEP
        BigDecimal valorFinal = isSulSudeste(cep) ? FRETE_SUL_SUDESTE : FRETE_OUTROS;

        // Cálculo do Peso Total dos itens
        BigDecimal pesoTotal = BigDecimal.ZERO;
        if (carrinho.getItens() != null) {
            for (ItemCarrinho item : carrinho.getItens()) {
                BigDecimal pesoProduto = item.getProduto().getPesoKg();
                BigDecimal quantidade = BigDecimal.valueOf(item.getQuantidade());
                pesoTotal = pesoTotal.add(pesoProduto.multiply(quantidade));
            }
        }

        // RN05: Acréscimo de R$ 2,00 por kg (aplicado sobre o peso total)
        if (pesoTotal.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal custoAdicional = pesoTotal.multiply(ADICIONAL_POR_KG);
            valorFinal = valorFinal.add(custoAdicional);
        }

        return valorFinal;
    }

    // Método auxiliar para descobrir a região pelo CEP.
    // Os CEPs no Brasil são organizados por prefixos numéricos.
    private boolean isSulSudeste(String cep) {
        if (cep == null || cep.isBlank()) {
            throw new IllegalArgumentException("O CEP não pode ser nulo ou vazio para o cálculo de frete.");
        }

        // Remove o traço do CEP caso o usuário digite com máscara (ex: 88000-000 vira
        // 88000000)
        String cepLimpo = cep.replaceAll("[^0-9]", "");

        if (cepLimpo.length() != 8) {
            throw new IllegalArgumentException("CEP inválido. O CEP deve conter 8 dígitos.");
        }

        // Pega apenas o primeiro dígito do CEP para identificar o estado
        char primeiroDigito = cepLimpo.charAt(0);

        // Prefixos do Sudeste: 0 a 3 (SP, RJ, ES, MG)
        // Prefixos do Sul: 8 e 9 (PR, SC, RS)
        return primeiroDigito == '0' || primeiroDigito == '1' ||
                primeiroDigito == '2' || primeiroDigito == '3' ||
                primeiroDigito == '8' || primeiroDigito == '9';
    }
}