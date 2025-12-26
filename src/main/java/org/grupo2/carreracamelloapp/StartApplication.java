package org.grupo2.carreracamelloapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.grupo2.carreracamelloapp.controller.CarreraCamellosController;
import org.grupo2.carreracamelloapp.model.Cliente;

import java.io.IOException;

public class StartApplication extends Application {
    private Thread hiloControlCarrera;
    private static Cliente camello;

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                StartApplication.class.getResource("pantallas/carreraCamellosUI.fxml")
        );
        Parent root = fxmlLoader.load();

        CarreraCamellosController controller = fxmlLoader.getController();

        // Vincular cliente ↔ controller
        camello.setController(controller);
        CarreraCamellosController.setCliente(camello);
        // Si tu controller tiene setCliente, mejor también:
        // controller.setCliente(camello);

        // Unirse al multicast AHORA que ya hay controller
        camello.joinMulticast();

        // Lanzar el hilo que ejecuta cicloCarrera()
        hiloControlCarrera = new Thread(camello);
        hiloControlCarrera.start();
        System.out.println("[Cliente] ✅ Hilo UDP iniciado");

        Scene scene = new Scene(root, 967, 606);
        primaryStage.setTitle("Carrera de camellos!");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void stop() {
        if (hiloControlCarrera != null && hiloControlCarrera.isAlive()) {
            hiloControlCarrera.interrupt();
        }
    }

    public static void getCliente(Cliente cliente) {
        camello = cliente;
    }
}
