package com.unisul.ecommerce.ui;

import com.unisul.ecommerce.model.*;
import com.unisul.ecommerce.service.*;
import com.unisul.ecommerce.exception.CupomInvalidoException;
import com.unisul.ecommerce.exception.CarrinhoVazioException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainApp extends Application {

    // "Banco de dados" fictício para a demonstração
    public static final List<Produto> LISTA_PRODUTOS = new ArrayList<>();
    public static final List<Cupom> LISTA_CUPONS = new ArrayList<>();

    // Serviços REAIS desenvolvidos pelo grupo
    private final CupomService cupomService = new CupomService();
    private final FreteService freteService = new FreteService();
    private final FidelidadeService fidelidadeService = new FidelidadeService(150); // Cliente começa com 150 pontos para teste
    private final CheckoutService checkoutService = new CheckoutService(cupomService, freteService, fidelidadeService);

    // Estado da venda atual na tela
    private final Carrinho carrinhoAtual = new Carrinho();
    private final Cliente clienteSimulado = new Cliente("Cliente Demonstração", "88000-000");

    // Formatador nativo para moeda brasileira (R$ 0,00)
    private final NumberFormat formatadorMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    // Elementos visuais que precisamos atualizar dinamicamente
    private ListView<String> vistaCarrinho;
    private Label lblSubtotal, lblFrete, lblDescontos, lblTotal, lblPontosCliente;
    private TextField txtCep, txtCupom, txtPontos;
    private ComboBox<String> cbProdutos;
    private Spinner<Integer> spQuantidade;

    // Variáveis de controle para reter os BigDecimals calculados de forma limpa
    private BigDecimal subtotalAtual = BigDecimal.ZERO;
    private BigDecimal freteAtual = BigDecimal.ZERO;
    private BigDecimal descontoAtual = BigDecimal.ZERO;

    @Override
    public void init() {
        // Carga de dados iniciais baseada nos modelos do grupo
        LISTA_PRODUTOS.add(new Produto(1L, "Item 1 (Leve/Barato)", new BigDecimal("25.50"), new BigDecimal("0.200")));
        LISTA_PRODUTOS.add(new Produto(2L, "Item 2 (Pesado/Interm.)", new BigDecimal("89.90"), new BigDecimal("5.000")));
        LISTA_PRODUTOS.add(new Produto(3L, "Item 3 (Caro/Frete Grátis)", new BigDecimal("210.00"), new BigDecimal("1.200")));

        LISTA_CUPONS.add(new Cupom(1L, "CUPOM10", new BigDecimal("10.00"), BigDecimal.ZERO, TipoCupom.PERCENTUAL, true));
        LISTA_CUPONS.add(new Cupom(2L, "FIXO30", new BigDecimal("30.00"), new BigDecimal("100.00"), TipoCupom.FIXO, true));
        LISTA_CUPONS.add(new Cupom(3L, "EXPIRADO", new BigDecimal("15.00"), BigDecimal.ZERO, TipoCupom.FIXO, false));

        // Inicializa o carrinho com o cliente simulado
        carrinhoAtual.setCliente(clienteSimulado);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Módulo de Checkout - Engine Validation");

        // Estilos CSS Reutilizáveis para Componentes Modernos
        String estiloInput = "-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #cbd5e0; -fx-padding: 6 10; -fx-font-family: 'Segoe UI', Arial;";
        String estiloLabelTitulo = "-fx-font-family: 'Segoe UI', Arial; -fx-font-weight: bold; -fx-text-fill: #4a5568; -fx-font-size: 13px;";
        String estiloTextoInformativo = "-fx-font-family: 'Segoe UI', Arial; -fx-font-style: italic; -fx-text-fill: #718096; -fx-font-size: 13px;";
        
        // Estilo unificado para os botões de aplicar: Azul pastel sutil com alto contraste
        String estiloBotaoLateral = "-fx-background-color: #e0f2fe; -fx-text-fill: #0369a1; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-border-color: #bae6fd; -fx-border-width: 1; -fx-pref-width: 110px; -fx-font-family: 'Segoe UI', Arial;";

        // --- COLUNA ESQUERDA: Adicionar e Remover Itens ---
        VBox colEsquerda = new VBox(15);
        colEsquerda.setPadding(new Insets(25));
        colEsquerda.setPrefWidth(430);
        colEsquerda.setStyle("-fx-background-color: #f7fafc; -fx-border-color: #e2e8f0; -fx-border-width: 0 1 0 0;");

        Label titleLoja = new Label("Catálogo de Produtos");
        titleLoja.setStyle("-fx-font-family: 'Segoe UI', Arial; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");

        cbProdutos = new ComboBox<>();
        for (Produto p : LISTA_PRODUTOS) {
            cbProdutos.getItems().add(p.getNome() + " - R$ " + p.getPreco());
        }
        cbProdutos.getSelectionModel().selectFirst();
        cbProdutos.setMaxWidth(Double.MAX_VALUE);
        cbProdutos.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #cbd5e0; -fx-padding: 4;");

        Label lblQtd = new Label("Quantidade:");
        lblQtd.setStyle(estiloLabelTitulo);
        
        spQuantidade = new Spinner<>(1, 10, 1);
        spQuantidade.setMaxWidth(Double.MAX_VALUE);
        spQuantidade.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #cbd5e0;");

        Button btnAdicionar = new Button("Adicionar ao Carrinho");
        btnAdicionar.setMaxWidth(Double.MAX_VALUE);
        btnAdicionar.setPrefHeight(38);
        btnAdicionar.setStyle("-fx-background-color: #38a169; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 6; -fx-cursor: hand;");
        btnAdicionar.setOnAction(e -> acaoAdicionarItem());

        Label titleCarrinho = new Label("Itens no Carrinho:");
        titleCarrinho.setStyle("-fx-font-family: 'Segoe UI', Arial; -fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2d3748;");
        
        // Quadrado do carrinho com contraste cinza sutil 
        vistaCarrinho = new ListView<>();
        vistaCarrinho.setPrefHeight(280);
        vistaCarrinho.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #cbd5e0; -fx-font-family: 'Segoe UI', Arial; -fx-background-color: #f1f5f9; -fx-control-inner-background: #ffffff;");

        Button btnRemover = new Button("Remover Item Selecionado");
        btnRemover.setMaxWidth(Double.MAX_VALUE);
        btnRemover.setPrefHeight(32);
        btnRemover.setStyle("-fx-background-color: #e53e3e; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
        btnRemover.setOnAction(e -> acaoRemoverItem());

        colEsquerda.getChildren().addAll(titleLoja, cbProdutos, lblQtd, spQuantidade, btnAdicionar, titleCarrinho, vistaCarrinho, btnRemover);

        // --- COLUNA DIREITA: Fechamento de Pedido ---
        VBox colDireita = new VBox(14);
        colDireita.setPadding(new Insets(25));
        colDireita.setPrefWidth(470);
        colDireita.setStyle("-fx-background-color: #ffffff;");

        Label titleCheckout = new Label("Resumo do Checkout");
        titleCheckout.setStyle("-fx-font-family: 'Segoe UI', Arial; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");

        lblPontosCliente = new Label("Seu saldo de pontos fidelidade: " + clienteSimulado.getSaldoPontos());
        lblPontosCliente.setStyle(estiloTextoInformativo);

        // CEP de Entrega + Botão Lateral
        Label lblCepTit = new Label("CEP de Entrega:"); lblCepTit.setStyle(estiloLabelTitulo);
        txtCep = new TextField(clienteSimulado.getCep());
        txtCep.setPromptText("Digite o CEP (Ex: 88000-000)");
        txtCep.setStyle(estiloInput);
        HBox.setHgrow(txtCep, Priority.ALWAYS);
        Button btnAplicarCep = new Button("Aplicar CEP");
        btnAplicarCep.setStyle(estiloBotaoLateral);
        btnAplicarCep.setPrefHeight(34);
        btnAplicarCep.setOnAction(e -> acaoAplicarCepIsolado());
        HBox boxCep = new HBox(8, txtCep, btnAplicarCep);
        boxCep.setAlignment(Pos.CENTER_LEFT);
        
        // Cupom Promocional + Botão Lateral
        Label lblCupTit = new Label("Cupom Promocional:"); lblCupTit.setStyle(estiloLabelTitulo);
        txtCupom = new TextField();
        txtCupom.setPromptText("Cupom de Desconto (Ex: CUPOM10)");
        txtCupom.setStyle(estiloInput);
        HBox.setHgrow(txtCupom, Priority.ALWAYS);
        Button btnAplicarCupom = new Button("Aplicar Cupom");
        btnAplicarCupom.setStyle(estiloBotaoLateral);
        btnAplicarCupom.setPrefHeight(34);
        btnAplicarCupom.setOnAction(e -> acaoAplicarCupomIsolado());
        HBox boxCupom = new HBox(8, txtCupom, btnAplicarCupom);
        boxCupom.setAlignment(Pos.CENTER_LEFT);

        // Pontos Fidelidade + Botão Lateral
        Label lblFidTit = new Label("Usar Pontos Fidelidade (Mínimo 100):"); lblFidTit.setStyle(estiloLabelTitulo);
        txtPontos = new TextField("0");
        txtPontos.setPromptText("Quantidade de pontos a resgatar");
        txtPontos.setStyle(estiloInput);
        HBox.setHgrow(txtPontos, Priority.ALWAYS);
        Button btnAplicarPontos = new Button("Aplicar Pontos");
        btnAplicarPontos.setStyle(estiloBotaoLateral);
        btnAplicarPontos.setPrefHeight(34);
        btnAplicarPontos.setOnAction(e -> acaoAplicarPontosIsolado());
        HBox boxPontos = new HBox(8, txtPontos, btnAplicarPontos);
        boxPontos.setAlignment(Pos.CENTER_LEFT);

        // Painel de Totais (Valores finais da Engine)
        GridPane painelValores = new GridPane();
        painelValores.setHgap(20);
        painelValores.setVgap(12);
        painelValores.setPadding(new Insets(10, 15, 10, 15));
        painelValores.setStyle("-fx-background-color: #f7fafc; -fx-background-radius: 8; -fx-border-color: #edf2f7; -fx-border-radius: 8;");

        lblSubtotal = new Label(formatadorMoeda.format(BigDecimal.ZERO));
        lblSubtotal.setStyle("-fx-font-family: 'Segoe UI', Arial; -fx-font-size: 13px; -fx-text-fill: #4a5568;");
        lblFrete = new Label(formatadorMoeda.format(BigDecimal.ZERO));
        lblFrete.setStyle("-fx-font-family: 'Segoe UI', Arial; -fx-font-size: 13px; -fx-text-fill: #4a5568;");
        lblDescontos = new Label(formatadorMoeda.format(BigDecimal.ZERO));
        lblDescontos.setStyle("-fx-font-family: 'Segoe UI', Arial; -fx-font-size: 13px; -fx-text-fill: #e53e3e;");
        lblTotal = new Label(formatadorMoeda.format(BigDecimal.ZERO));
        lblTotal.setStyle("-fx-font-family: 'Segoe UI', Arial; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2b6cb0;");

        Label lblSubTitle = new Label("Subtotal:"); lblSubTitle.setStyle("-fx-font-family: 'Segoe UI', Arial; -fx-text-fill: #718096;");
        Label lblFreteTitle = new Label("Frete dinâmico:"); lblFreteTitle.setStyle("-fx-font-family: 'Segoe UI', Arial; -fx-text-fill: #718096;");
        Label lblDescTitle = new Label("Descontos aplicados:"); lblDescTitle.setStyle("-fx-font-family: 'Segoe UI', Arial; -fx-text-fill: #718096;");
        Label lblTotalTitle = new Label("Total Geral:"); lblTotalTitle.setStyle("-fx-font-family: 'Segoe UI', Arial; -fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");

        painelValores.add(lblSubTitle, 0, 0);
        painelValores.add(lblSubtotal, 1, 0);
        painelValores.add(lblFreteTitle, 0, 1);
        painelValores.add(lblFrete, 1, 1);
        painelValores.add(lblDescTitle, 0, 2);
        painelValores.add(lblDescontos, 1, 2);
        painelValores.add(lblTotalTitle, 0, 3);
        painelValores.add(lblTotal, 1, 3);

        Button btnCalcular = new Button("Calcular Valores / Processar");
        btnCalcular.setMaxWidth(Double.MAX_VALUE);
        btnCalcular.setPrefHeight(42);
        btnCalcular.setStyle("-fx-background-color: #3182ce; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 6; -fx-cursor: hand;");
        btnCalcular.setOnAction(e -> acaoProcessarCheckout());

        // LISTA DE CUPONS DISPONÍVEIS
        Label lblCuponsDisponiveis = new Label("Cupons: CUPOM10 (10%), FIXO30 (R$30 em compras acima de R$100)");
        lblCuponsDisponiveis.setStyle(estiloTextoInformativo);
        lblCuponsDisponiveis.setWrapText(true);

        colDireita.getChildren().addAll(titleCheckout, lblPontosCliente, 
                lblCepTit, boxCep, 
                lblCupTit, boxCupom,
                lblFidTit, boxPontos, 
                new Separator(), painelValores, btnCalcular, lblCuponsDisponiveis);

        // --- LAYOUT E CENA PRINCIPAL ---
        HBox root = new HBox(colEsquerda, colDireita);
        Scene scene = new Scene(root, 920, 610);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void acaoAdicionarItem() {
        int index = cbProdutos.getSelectionModel().getSelectedIndex();
        Produto produtoSelecionado = LISTA_PRODUTOS.get(index);
        int qtd = spQuantidade.getValue();

        carrinhoAtual.adicionarItem(produtoSelecionado, qtd);
        atualizarListaVisualECalculos();
    }

    private void acaoRemoverItem() {
        int indexSelecionado = vistaCarrinho.getSelectionModel().getSelectedIndex();

        if (indexSelecionado >= 0) {
            carrinhoAtual.getItens().remove(indexSelecionado);
            atualizarListaVisualECalculos();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Atenção");
            alert.setHeaderText(null);
            alert.setContentText("Selecione um item da lista do carrinho para poder removê-lo.");
            alert.showAndWait();
        }
    }

    private void atualizarListaVisualECalculos() {
        vistaCarrinho.getItems().clear();
        for (ItemCarrinho item : carrinhoAtual.getItens()) {
            vistaCarrinho.getItems().add(item.getProduto().getNome() + " (x" + item.getQuantidade() + ") - Sub: " + formatadorMoeda.format(item.getSubtotal()));
        }
        
        subtotalAtual = carrinhoAtual.getValorTotal();
        freteAtual = BigDecimal.ZERO;
        descontoAtual = BigDecimal.ZERO;

        lblSubtotal.setText(formatadorMoeda.format(subtotalAtual));
        lblFrete.setText(formatadorMoeda.format(freteAtual));
        lblDescontos.setText(formatadorMoeda.format(descontoAtual));
        lblTotal.setText(formatadorMoeda.format(subtotalAtual)); 
    }

    // 1. AÇÃO ISOLADA: CEP
    private void acaoAplicarCepIsolado() {
        try {
            validarCarrinhoNaoVazio();
            clienteSimulado.setCep(txtCep.getText().trim());
            atualizarValoresPelaEngine();
        } catch (IllegalArgumentException ex) {
            exibirErro("Regra de Frete", ex.getMessage());
        } catch (Exception ex) {
            exibirErro("Erro de Frete", "Verifique o CEP informado.");
        }
    }

    // 2. AÇÃO ISOLADA: Cupom
    private void acaoAplicarCupomIsolado() {
        try {
            validarCarrinhoNaoVazio();
            String codigoDigitado = txtCupom.getText().trim().toUpperCase();
            
            Cupom cupomEncontrado = null;
            if (!codigoDigitado.isEmpty()) {
                for (Cupom c : LISTA_CUPONS) {
                    if (c.getCodigo().equals(codigoDigitado)) {
                        cupomEncontrado = c;
                        break;
                    }
                }
                if (cupomEncontrado == null) {
                    throw new CupomInvalidoException("O cupom '" + codigoDigitado + "' não existe.");
                }
            }
            carrinhoAtual.setCupomAplicado(cupomEncontrado);
            atualizarValoresPelaEngine();
        } catch (CupomInvalidoException ex) {
            exibirErro("Cupom Rejeitado", ex.getMessage());
        } catch (Exception ex) {
            exibirErro("Erro de Cupom", "Ocorreu um erro ao aplicar o cupom.");
        }
    }

    // 3. AÇÃO ISOLADA: Fidelidade
    private void acaoAplicarPontosIsolado() {
        try {
            validarCarrinhoNaoVazio();
            int pontosAUsar = Integer.parseInt(txtPontos.getText().trim());

            if (pontosAUsar > 0) {
                FidelidadeService simuladorFidelidade = new FidelidadeService(clienteSimulado.getSaldoPontos());
                simuladorFidelidade.resgatar(pontosAUsar); 
            }
            atualizarValoresPelaEngine();
        } catch (NumberFormatException ex) {
            exibirErro("Formato Inválido", "Digite um valor numérico inteiro para os pontos.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            exibirErro("Regra de Fidelidade", ex.getMessage());
        } catch (Exception ex) {
            exibirErro("Erro de Fidelidade", "Não foi possível aplicar os pontos.");
        }
    }

    // Método centralizador cumulativo real da Engine de regras
    private void atualizarValoresPelaEngine() {
        try {
            int pontosAUsar = 0;
            try {
                pontosAUsar = Integer.parseInt(txtPontos.getText().trim());
            } catch (NumberFormatException ignored) {}

            ResumoPedido resumo;
            if (pontosAUsar > 0) {
                resumo = checkoutService.finalizarPedidoComPontos(carrinhoAtual, pontosAUsar);
            } else {
                resumo = checkoutService.finalizarPedido(carrinhoAtual);
            }

            subtotalAtual = resumo.getSubtotal();
            freteAtual = resumo.getValorFrete();
            descontoAtual = resumo.getValorDescontos();

            lblSubtotal.setText(formatadorMoeda.format(subtotalAtual));
            lblFrete.setText(formatadorMoeda.format(freteAtual));
            lblDescontos.setText(formatadorMoeda.format(descontoAtual));
            lblTotal.setText(formatadorMoeda.format(resumo.getTotalFinal()));
            
        } catch (Exception ignored) {}
    }

    private void validarCarrinhoNaoVazio() throws CarrinhoVazioException {
        if (carrinhoAtual.getItens().isEmpty()) {
            throw new CarrinhoVazioException();
        }
    }

    private void acaoProcessarCheckout() {
        try {
            clienteSimulado.setCep(txtCep.getText());

            String codigoDigitado = txtCupom.getText().trim().toUpperCase();
            Cupom cupomEncontrado = null;
            if (!codigoDigitado.isEmpty()) {
                for (Cupom c : LISTA_CUPONS) {
                    if (c.getCodigo().equals(codigoDigitado)) {
                        cupomEncontrado = c;
                        break;
                    }
                }
                if (cupomEncontrado == null) {
                    cupomEncontrado = new Cupom();
                    cupomEncontrado.setCodigo(codigoDigitado);
                }
            }
            carrinhoAtual.setCupomAplicado(cupomEncontrado);

            int pointsToUse = Integer.parseInt(txtPontos.getText().trim());

            ResumoPedido resumo;
            if (pointsToUse > 0) {
                resumo = checkoutService.finalizarPedidoComPontos(carrinhoAtual, pointsToUse);
            } else {
                resumo = checkoutService.finalizarPedido(carrinhoAtual);
            }

            subtotalAtual = resumo.getSubtotal();
            freteAtual = resumo.getValorFrete();
            descontoAtual = resumo.getValorDescontos();

            lblSubtotal.setText(formatadorMoeda.format(subtotalAtual));
            lblFrete.setText(formatadorMoeda.format(freteAtual));
            lblDescontos.setText(formatadorMoeda.format(descontoAtual));
            lblTotal.setText(formatadorMoeda.format(resumo.getTotalFinal()));
            lblPontosCliente.setText("Seu saldo de pontos fidelidade: " + clienteSimulado.getSaldoPontos());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso!");
            alert.setHeaderText("Pedido Processado com Sucesso pela Engine!");
            alert.setContentText("Valor Total da Compra: " + formatadorMoeda.format(resumo.getTotalFinal()) + 
                                 "\n\nPontos obtidos nesta compra: " + resumo.getPontosGanhos());
            alert.showAndWait();

        } catch (CarrinhoVazioException ex) {
            exibirErro("Erro de Checkout", ex.getMessage());
        } catch (CupomInvalidoException ex) {
            exibirErro("Cupom Rejeitado", ex.getMessage());
        } catch (IllegalArgumentException | IllegalStateException ex) {
            exibirErro("Validação de Regra", ex.getMessage());
        } catch (Exception ex) {
            exibirErro("Erro Inesperado", "Verifique os dados inseridos: " + ex.getMessage());
        }
    }

    private void exibirErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText("A Regra de Negócio Bloqueou a Operação!");
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}