package com.unisul.ecommerce;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carrega o arquivo FXML que está no mesmo nível do pacote
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unisul/ecommerce/view/interface.fxml"));
        Parent root = loader.load();

        // Configura e exibe a janela principal
        primaryStage.setTitle("Motor de Checkout - Demonstração");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}