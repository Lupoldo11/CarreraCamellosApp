package org.grupo2.carreracamelloapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StartApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("pantallas/carreraCamellosUI.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 967, 606);
        primaryStage.setTitle("Carrera de camellos!");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

}
