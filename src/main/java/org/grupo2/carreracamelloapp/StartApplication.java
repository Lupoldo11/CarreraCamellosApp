package org.grupo2.carreracamelloapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.grupo2.carreracamelloapp.model.Cliente;

import java.io.IOException;

public class StartApplication extends Application {
    private Thread hiloControlCarrera;
    private static Cliente camello;

    @Override
    public void start(Stage primaryStage) throws IOException { //Lanzar UI
        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("pantallas/carreraCamellosUI.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 967, 606);
        primaryStage.setTitle("Carrera de camellos!");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        hiloControlCarrera = new Thread(camello);
        hiloControlCarrera.start();
    }

    @Override
    public void stop(){
        hiloControlCarrera.interrupt();
    }

    public static void getCliente(Cliente cliente){
        camello=cliente;
    }
}
