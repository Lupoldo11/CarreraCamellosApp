package org.grupo2.carreracamelloapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.grupo2.carreracamelloapp.controller.CarreraCamellosController;
import org.grupo2.carreracamelloapp.model.Cliente;
import org.grupo2.carreracamelloapp.model.mensajes.AsignacionGrupo;
import org.grupo2.carreracamelloapp.model.mensajes.EventDeath;
import org.grupo2.carreracamelloapp.model.mensajes.EventPosicion;

import javax.swing.plaf.nimbus.State;
import java.io.IOException;

public class StartApplication extends Application {
    private Thread hiloControlCarrera;
    private static Cliente camello;

    //nuevo
    private Stage firtsStage;
    private static StartApplication windows;
    public StartApplication(){
        windows = this;
    }

    @Override
    public void start(Stage primaryStage) throws IOException { //Lanzar UI
        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("pantallas/carreraCamellosUI.fxml"));
        Parent root = fxmlLoader.load();

        CarreraCamellosController controller = fxmlLoader.getController();
        camello.setController(controller);

        Scene scene = new Scene(root, 967, 606);
        primaryStage.setTitle("Carrera de camellos!");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);

        //nuevo
        this.firtsStage=primaryStage;

        primaryStage.setOnCloseRequest(event -> {
            // método que quieres lanzar al cerrar
            EventDeath death = new EventDeath(camello.getNombreCliente());
            camello.envioPaqueteUDP(death,camello.getMS(), camello.getInetAddress());
            primaryStage.close();   // opcional, JavaFX ya cierra la ventana
            System.exit(0);  // si quieres forzar el cierre de la JVM
        });


        primaryStage.show();

        hiloControlCarrera = new Thread(camello);
        hiloControlCarrera.start();
    }


    @Override
    public void stop(){
        hiloControlCarrera.interrupt();
    }

    //nuevo
    public static StartApplication getInstance(){
        return windows;
    }
    public Stage getFirtsStage(){
        return firtsStage;
    }

    public static void getCliente(Cliente cliente){
        camello=cliente;
    }
}
