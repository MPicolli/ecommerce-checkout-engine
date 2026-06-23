package com.unisul.ecommerce.controller;

import com.unisul.ecommerce.model.*;
import com.unisul.ecommerce.service.*;
import com.unisul.ecommerce.repository.*;
import com.unisul.ecommerce.exception.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
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

    @FXML private TableView<ResumoPedido> tabelaHistorico;
    @FXML private TableColumn<ResumoPedido, String> colHistPedido;
    @FXML private TableColumn<ResumoPedido, BigDecimal> colHistTotal;
    @FXML private TableColumn<ResumoPedido, Integer> colHistPontos;

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

    @FXML private Label lblNomeCliente;
    @FXML private Label lblCpfCliente;
    @FXML private Label lblSaldoPontos;
    
    private int saldoPontosAtual = 500;

    private CheckoutService checkoutService;
    private List<Cupom> cuponsMock;
    private Carrinho carrinhoAtual;
    private Cliente clienteFicticio;
    
    private ProdutoRepository produtoRepository; 
    private CupomRepository cupomRepository;
    private ClienteRepository clienteRepository;

    private ObservableList<ResumoPedido> listaHistorico = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        CupomService cupomService = new CupomService();
        FreteService freteService = new FreteService();
        FidelidadeService fidelidadeService = new FidelidadeService(saldoPontosAtual); 
        this.checkoutService = new CheckoutService(cupomService, freteService, fidelidadeService);

        this.produtoRepository = new ProdutoRepository(); 
        this.cupomRepository = new CupomRepository();
        this.clienteRepository = new ClienteRepository();

        this.cuponsMock = cupomRepository.buscarTodos();
        this.clienteFicticio = clienteRepository.buscarTodos().get(0); 
        
        this.carrinhoAtual = new Carrinho(clienteFicticio);

        spinnerQuantidade.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, 1));

        configurarMapeamentoTabelas();

        tabelaProdutos.setItems(FXCollections.observableArrayList(produtoRepository.buscarTodos()));
        tabelaHistorico.setItems(listaHistorico);

        chkUtilizarPontos.selectedProperty().addListener((obs, antigo, novo) -> {
            txtPontosResgate.setDisable(!novo);
            if (!novo) txtPontosResgate.clear();
        });

        txtCep.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 9) {
                txtCep.setText(oldValue);
            }
        });
        
        if (lblNomeCliente != null) {
            lblNomeCliente.setText(clienteFicticio.getNome());
        }
        
        if (lblCpfCliente != null) {
            lblCpfCliente.setText("📄 CPF: 123.456.789-00");
        }

        if (lblSaldoPontos != null) {
            lblSaldoPontos.setText(saldoPontosAtual + " pts");
        }
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
        
        spinnerQuantidade.getValueFactory().setValue(1);
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
        limparResumo();
    }

    @FXML
    void finalizarCheckout() {
        try {
            String cepDigitado = txtCep.getText();
            if (cepDigitado == null || cepDigitado.trim().isEmpty()) {
                throw new IllegalArgumentException("O CEP de entrega é obrigatório.");
            }
            if (!cepDigitado.matches("[0-9\\-]+")) {
                throw new IllegalArgumentException("CEP inválido. Use apenas números e traço.");
            }
            if (cepDigitado.replace("-", "").length() != 8) {
                throw new IllegalArgumentException("O CEP deve conter exatamente 8 dígitos.");
            }
            
            clienteFicticio.setCep(cepDigitado);

            String codigoCupom = txtCupom.getText();
            BigDecimal descontoCupomCalculado = BigDecimal.ZERO;

            if (codigoCupom != null && !codigoCupom.trim().isEmpty()) {
                Cupom cupomEncontrado = cuponsMock.stream()
                        .filter(c -> c.getCodigo().equalsIgnoreCase(codigoCupom.trim()))
                        .findFirst()
                        .orElseThrow(() -> new CupomInvalidoException("Cupom não existe no banco de dados."));
                carrinhoAtual.setCupomAplicado(cupomEncontrado);
            } else {
                carrinhoAtual.setCupomAplicado(null);
            }

            ResumoPedido resumo;
            BigDecimal descontoPontosCalculado = BigDecimal.ZERO;
            int pontosParaResgatar = 0;

            if (chkUtilizarPontos.isSelected()) {
                pontosParaResgatar = Integer.parseInt(txtPontosResgate.getText().trim());
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

            listaHistorico.add(resumo);

            saldoPontosAtual = saldoPontosAtual - pontosParaResgatar + resumo.getPontosGanhos();
            if (lblSaldoPontos != null) {
                lblSaldoPontos.setText(saldoPontosAtual + " pts");
            }

            exibirAlerta("Sucesso", "Pedido adicionado ao Histórico!", Alert.AlertType.INFORMATION);
            esvaziarCarrinho();

        } catch (NumberFormatException nfe) {
            limparResumo(); 
            exibirAlerta("Erro de Digitação", "A quantidade de pontos deve ser um número inteiro.", Alert.AlertType.ERROR);
        } catch (CarrinhoVazioException | CupomInvalidoException | PontosInsuficientesException | IllegalArgumentException ex) {
            carrinhoAtual.setCupomAplicado(null); 
            limparResumo(); 
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

        colHistPedido.setCellValueFactory(cell -> new SimpleStringProperty("#" + (listaHistorico.indexOf(cell.getValue()) + 1)));
        colHistTotal.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getTotalFinal()));
        colHistPontos.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getPontosGanhos()).asObject());
    }

    private void limparResumo() {
        lblSubtotal.setText("R$ 0,00");
        lblDescontoCupom.setText("R$ 0,00");
        lblFrete.setText("R$ 0,00");
        lblDescontoPontos.setText("R$ 0,00");
        lblTotalFinal.setText("R$ 0,00");
        lblPontosGanhos.setText("0");
    }

    private void esvaziarCarrinho() {
        carrinhoAtual.getItens().clear();
        tabelaCarrinho.setItems(FXCollections.observableArrayList(carrinhoAtual.getItens()));
        tabelaCarrinho.refresh();
        limparResumo();
        txtCep.clear();
        txtCupom.clear();
        chkUtilizarPontos.setSelected(false);
        txtPontosResgate.clear();
        spinnerQuantidade.getValueFactory().setValue(1);
    }

    private void exibirAlerta(String titulo, String msg, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}