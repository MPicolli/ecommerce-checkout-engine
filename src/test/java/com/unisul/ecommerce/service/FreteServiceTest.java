package com.unisul.ecommerce.service;

import com.unisul.ecommerce.model.Carrinho;
import com.unisul.ecommerce.model.ItemCarrinho;
import com.unisul.ecommerce.model.Produto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FreteServiceTest {

    private FreteService freteService;
    private Carrinho carrinho;

    @BeforeEach
    public void setUp() {
        freteService = new FreteService();
        carrinho = new Carrinho();
        // Inicializamos a lista para os testes padrão
        carrinho.setItens(new ArrayList<>());
    }

    // --- COBERTURA: FRETE GRÁTIS ---

    @Test
    public void deveRetornarFreteGratis_QuandoValorTotalForMaiorQue200() {
        // Adicionamos um produto de 200.01 para bater a meta do frete grátis
        adicionarProdutoAoCarrinho("200.01", "1.0", 1);

        BigDecimal frete = freteService.calcularFrete(carrinho, "88000-000");
        assertEquals(BigDecimal.ZERO, frete);
    }

    @Test
    public void deveRetornarFreteGratis_QuandoValorTotalForExatamente200() {
        // Adicionamos um produto de exatos 200.00
        adicionarProdutoAoCarrinho("200.00", "1.0", 1);

        BigDecimal frete = freteService.calcularFrete(carrinho, "88000-000");
        assertEquals(BigDecimal.ZERO, frete);
    }

    // --- COBERTURA: MÚLTIPLOS PREFIXOS DE CEP (Substituindo o Parameterized) ---

    @Test
    public void deveCobrarFreteBaseSulSudeste_ParaTodosOsPrefixosDaRegiao() {
        adicionarProdutoAoCarrinho("100.00", "0.0", 1); // Carrinho de 100 reais, sem peso extra

        String[] cepsSulSudeste = { "01000-000", "13000-000", "20000-000", "30000-000", "88000-000", "90000-000" };

        for (String cep : cepsSulSudeste) {
            BigDecimal frete = freteService.calcularFrete(carrinho, cep);
            assertEquals(0, new BigDecimal("15.00").compareTo(frete), "Falhou no CEP: " + cep);
        }
    }

    @Test
    public void deveCobrarFreteBaseOutros_ParaTodosOsPrefixosRestantes() {
        adicionarProdutoAoCarrinho("100.00", "0.0", 1);

        String[] cepsOutros = { "40000-000", "50000-000", "60000-000", "70000-000" };

        for (String cep : cepsOutros) {
            BigDecimal frete = freteService.calcularFrete(carrinho, cep);
            assertEquals(0, new BigDecimal("30.00").compareTo(frete), "Falhou no CEP: " + cep);
        }
    }

    // --- COBERTURA: CÁLCULO DE PESO ---

    @Test
    public void deveAdicionarCustoDePeso_QuandoPesoForMaiorQueZero() {
        // 2 itens de 1.5kg = 3kg totais. Custo adicional = 3 * 2.00 = 6.00
        adicionarProdutoAoCarrinho("50.00", "1.5", 2);

        BigDecimal frete = freteService.calcularFrete(carrinho, "88000-000"); // Base Sul: 15.00
        assertEquals(0, new BigDecimal("21.00").compareTo(frete)); // 15.00 + 6.00
    }

    @Test
    public void deveCobrarApenasBase_QuandoCarrinhoVazioSemPeso() {
        // Sem adicionar itens, o carrinho tem valor zero e peso zero (cai fora da regra
        // de frete grátis tbm)
        BigDecimal frete = freteService.calcularFrete(carrinho, "88000-000");
        assertEquals(0, new BigDecimal("15.00").compareTo(frete));
    }

    // --- COBERTURA: EXCEÇÕES DO CEP ---

    @Test
    public void deveLancarExcecao_QuandoCepForNulo() {
        assertThrows(IllegalArgumentException.class, () -> {
            freteService.calcularFrete(carrinho, null);
        });
    }

    @Test
    public void deveLancarExcecao_QuandoCepForVazioOuEmBranco() {
        String[] cepsVazios = { "", "   " };
        for (String cep : cepsVazios) {
            assertThrows(IllegalArgumentException.class, () -> {
                freteService.calcularFrete(carrinho, cep);
            });
        }
    }

    @Test
    public void deveLancarExcecao_QuandoCepTiverTamanhoInvalido() {
        String[] cepsInvalidos = { "88000", "88000-0000", "ABCDE-FGH" };
        for (String cep : cepsInvalidos) {
            assertThrows(IllegalArgumentException.class, () -> {
                freteService.calcularFrete(carrinho, cep);
            });
        }
    }

    @Test
    public void deveLancarExcecao_QuandoCarrinhoForNulo() {
        assertThrows(NullPointerException.class, () -> {
            freteService.calcularFrete(null, "88000-000");
        });
    }

    // --- MÉTODO AUXILIAR ---

    private void adicionarProdutoAoCarrinho(String preco, String peso, int quantidade) {
        Produto produto = new Produto();
        produto.setPreco(new BigDecimal(preco));
        produto.setPesoKg(new BigDecimal(peso));
        carrinho.getItens().add(new ItemCarrinho(produto, quantidade));
    }
}