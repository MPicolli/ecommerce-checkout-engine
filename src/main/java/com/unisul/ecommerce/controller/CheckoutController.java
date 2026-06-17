package com.unisul.ecommerce.controller;

import com.unisul.ecommerce.model.*;
import com.unisul.ecommerce.service.*;
import com.unisul.ecommerce.exception.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CheckoutController {

    @FXML private TableView<Produto> tabelaProdutos;
    @FXML private TableColumn<Produto, String> colProdutoNome;
    @FXML private TableColumn<Produto, BigDecimal> colProdutoPreco;
    @FXML private TableColumn<Produto, BigDecimal> colProdutoPeso;
    @FXML private Spinner<Integer> spinnerQuantidade;

    @FXML private TableView<ItemCarrinho> tabelaCarrinho;
    @FXML private TableColumn<ItemCarrinho, String> colCarrinhoNome;
    @FXML private TableColumn<ItemCarrinho, BigDecimal> colCarrinhoPreco;
    @FXML private TableColumn<ItemCarrinho, Integer> colCarrinhoQtd;
    @FXML private TableColumn<ItemCarrinho, BigDecimal> colCarrinhoTotal;

    @FXML private TextField txtCep;
    @FXML private TextField txtCupom;
    @FXML private CheckBox chkUtilizarPontos;
    @FXML private TextField txtPontosResgate;

    @FXML private Label lblSubtotal;
    @FXML private Label lblDescontoCupom;
    @FXML private Label lblFrete;
    @FXML private Label lblDescontoPontos;
    @FXML private Label lblTotalFinal;
    @FXML private Label lblPontosGanhos;

    private CheckoutService checkoutService;
    private List<Cupom> cuponsMock;
    private Carrinho carrinhoAtual;
    private Cliente clienteFicticio;

    @FXML
    public void initialize() {
        CupomService cupomService = new CupomService();
        FreteService freteService = new FreteService();
        FidelidadeService fidelidadeService = new FidelidadeService(500); 
        this.checkoutService = new CheckoutService(cupomService, freteService, fidelidadeService);

        this.clienteFicticio = new Cliente("Cliente Demonstração", "88000-000");
        this.carrinhoAtual = new Carrinho(clienteFicticio);
        this.cuponsMock = criarCuponsFicticios();

        spinnerQuantidade.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, 1));

        configurarMapeamentoTabelas();

        tabelaProdutos.setItems(criarProdutosFicticios());

        chkUtilizarPontos.selectedProperty().addListener((obs, antigo, novo) -> {
            txtPontosResgate.setDisable(!novo);
            if (!novo) txtPontosResgate.clear();
        });
    }

    @FXML
    void adicionarAoCarrinho() {
        Produto produtoSelecionado = tabelaProdutos.getSelectionModel().getSelectedItem();
        if (produtoSelecionado == null) {
            exibirAlerta("Aviso", "Selecione um produto do catálogo primeiro.", Alert.AlertType.WARNING);
            return;
        }

        int quantidade = spinnerQuantidade.getValue();
        carrinhoAtual.adicionarItem(produtoSelecionado, quantidade);
        
        tabelaCarrinho.setItems(FXCollections.observableArrayList(carrinhoAtual.getItens()));
        tabelaCarrinho.refresh();
    }

    @FXML
    void removerDoCarrinho() {
        ItemCarrinho itemSelecionado = tabelaCarrinho.getSelectionModel().getSelectedItem();
        
        if (itemSelecionado == null) {
            exibirAlerta("Aviso", "Por favor, selecione um item no carrinho para remover.", Alert.AlertType.WARNING);
            return;
        }

        carrinhoAtual.getItens().remove(itemSelecionado);
        
        tabelaCarrinho.setItems(FXCollections.observableArrayList(carrinhoAtual.getItens()));
        tabelaCarrinho.refresh();
        
        lblSubtotal.setText("R$ 0,00");
        lblDescontoCupom.setText("R$ 0,00");
        lblFrete.setText("R$ 0,00");
        lblDescontoPontos.setText("R$ 0,00");
        lblTotalFinal.setText("R$ 0,00");
        lblPontosGanhos.setText("0");
    }

    @FXML
    void finalizarCheckout() {
        try {
            String cepDigitado = txtCep.getText();
            if (cepDigitado == null || cepDigitado.trim().isEmpty()) {
                throw new IllegalArgumentException("O CEP de entrega é obrigatório.");
            }
            clienteFicticio.setCep(cepDigitado);

            String codigoCupom = txtCupom.getText();
            BigDecimal descontoCupomCalculado = BigDecimal.ZERO;

            if (codigoCupom != null && !codigoCupom.trim().isEmpty()) {
                Cupom cupomEncontrado = cuponsMock.stream()
                        .filter(c -> c.getCodigo().equalsIgnoreCase(codigoCupom.trim()))
                        .findFirst()
                        .orElseThrow(() -> new CupomInvalidoException("Cupom não existe na base simulada."));
                carrinhoAtual.setCupomAplicado(cupomEncontrado);
            } else {
                carrinhoAtual.setCupomAplicado(null);
            }

            ResumoPedido resumo;
            BigDecimal descontoPontosCalculado = BigDecimal.ZERO;

            if (chkUtilizarPontos.isSelected()) {
                int pontosParaResgatar = Integer.parseInt(txtPontosResgate.getText().trim());
                resumo = checkoutService.finalizarPedidoComPontos(carrinhoAtual, pontosParaResgatar);
                
                descontoPontosCalculado = BigDecimal.valueOf(pontosParaResgatar).multiply(new BigDecimal("0.10"));
                descontoCupomCalculado = resumo.getValorDescontos().subtract(descontoPontosCalculado);
                
                if (descontoCupomCalculado.compareTo(BigDecimal.ZERO) < 0) {
                    descontoCupomCalculado = BigDecimal.ZERO;
                }
            } else {
                resumo = checkoutService.finalizarPedido(carrinhoAtual);
                descontoCupomCalculado = resumo.getValorDescontos();
            }

            lblSubtotal.setText(String.format("R$ %.2f", resumo.getSubtotal()));
            lblDescontoCupom.setText(String.format("-R$ %.2f", descontoCupomCalculado));
            lblFrete.setText(String.format("R$ %.2f", resumo.getValorFrete()));
            lblDescontoPontos.setText(String.format("-R$ %.2f", descontoPontosCalculado));
            lblTotalFinal.setText(String.format("R$ %.2f", resumo.getTotalFinal()));
            lblPontosGanhos.setText(String.valueOf(resumo.getPontosGanhos()));

        } catch (NumberFormatException nfe) {
            exibirAlerta("Erro de Digitação", "A quantidade de pontos deve ser um número inteiro.", Alert.AlertType.ERROR);
        } catch (CarrinhoVazioException | CupomInvalidoException | PontosInsuficientesException | IllegalArgumentException ex) {
            exibirAlerta("Regra de Negócio Violada", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void configurarMapeamentoTabelas() {
        colProdutoNome.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNome()));
        colProdutoPreco.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getPreco()));
        colProdutoPeso.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getPesoKg()));

        colCarrinhoNome.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getProduto().getNome()));
        colCarrinhoPreco.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getProduto().getPreco()));
        colCarrinhoQtd.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getQuantidade()).asObject());
        colCarrinhoTotal.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getSubtotal()));
    }

    private ObservableList<Produto> criarProdutosFicticios() {
        return FXCollections.observableArrayList(
                new Produto(1L, "Item 1", new BigDecimal("25.50"), new BigDecimal("0.500")),
                new Produto(2L, "Item 2", new BigDecimal("120.00"), new BigDecimal("2.300")),
                new Produto(3L, "Item 3", new BigDecimal("15.50"), new BigDecimal("0.150")),
                new Produto(4L, "Item 4", new BigDecimal("250.00"), new BigDecimal("5.000"))
        );
    }

    private List<Cupom> criarCuponsFicticios() {
        List<Cupom> cupons = new ArrayList<>();
        cupons.add(new Cupom(1L, "CUPOM10", new BigDecimal("10.00"), BigDecimal.ZERO, TipoCupom.PERCENTUAL, true));
        cupons.add(new Cupom(2L, "FIXO30", new BigDecimal("30.00"), new BigDecimal("100.00"), TipoCupom.FIXO, true));
        return cupons;
    }

    private void exibirAlerta(String titulo, String msg, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}