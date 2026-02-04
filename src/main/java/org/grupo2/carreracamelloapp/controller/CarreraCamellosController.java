package org.grupo2.carreracamelloapp.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.grupo2.carreracamelloapp.StartApplication;
import org.grupo2.carreracamelloapp.model.Cliente;
import org.grupo2.carreracamelloapp.model.mensajes.EventPosicion;

import java.io.IOException;

public class CarreraCamellosController {

    /******************************* Guardado Cliente *********************************************/
    private static Cliente camello;
    private static Cliente[] listCamellos;
    public static void setCliente(Cliente cliente) {
        camello = cliente;
    }
    public static void setListCamellos(Cliente[] camellos){listCamellos=camellos;}

    /******************************* Atributos del UI *********************************************/
    @FXML
    private Button iniciarCarreraButton;

    @FXML
    private ImageView camello1;

    @FXML
    private ImageView camello2;

    @FXML
    private ImageView camello3;

    /******************************* Metodos UI (Controller) *********************************************/
    @FXML
    protected void onIniciarCarreraClick(ActionEvent event) {
        //Escucha del boton UI
        EventPosicion moviento = new EventPosicion(camello.getNombreCliente(), camello.movimientoRandom());
        camello.envioPaqueteUDP(moviento, camello.getMS(), camello.getInetAddress());
    }

    /**
     * Mueve el camello después de comprobar de quien es
     * */
    protected void escuchaMoverCamellos(EventPosicion eventPosicion){
        if (eventPosicion.getPropietario().equals(listCamellos[0].getNombreCliente())){
            //mover el camello de lugar UI
            camello1.setLayoutX(camello1.getLayoutX() + eventPosicion.getMovimiento());
            System.out.println("[Controller] "+eventPosicion.getPropietario()+" se movió "+eventPosicion.getMovimiento());
        } else if (eventPosicion.getPropietario().equals(listCamellos[1].getNombreCliente())){
            //mover el camello de lugar UI
            camello2.setLayoutX(camello2.getLayoutX() + eventPosicion.getMovimiento());
            System.out.println("[Controller] "+eventPosicion.getPropietario()+" se movió "+eventPosicion.getMovimiento());
        } else if (eventPosicion.getPropietario().equals(listCamellos[2].getNombreCliente())){
            //mover el camello de lugar UI
            camello3.setLayoutX(camello3.getLayoutX() + eventPosicion.getMovimiento());
            System.out.println("[Controller] "+eventPosicion.getPropietario()+" se movió "+eventPosicion.getMovimiento());
        } else {
            System.out.println("[Warning] No conoce camello");
        }
    }

    /******************************* Metodos Acceso Exterior *********************************************/
    //Para enviar el movimiento desde fuera
    public void escuchaMovimientoMulticast(EventPosicion eventPosicion){
        Platform.runLater(() -> {
            escuchaMoverCamellos(eventPosicion);
        });
    }

    //Habilitar el boton
    public void butonON(){
        Platform.runLater(() -> {
            //Activar el boton  UI
            iniciarCarreraButton.setDisable(false);
            System.out.println("[Controller] Cambiado de estado el boton a activado");
        });
    }

    //Desactivar boton
    public void butonOFF(){
        Platform.runLater(() -> {
            iniciarCarreraButton.setDisable(true);
            System.out.println("[Controller] Cambiado de estado el boton a desactivado");
        });
    }

    /**
     * Lanzador de la UI de podio y su configuracion
     * */
    public void podio(Cliente[] podio) throws IOException {
        Platform.runLater(()->{
            FXMLLoader fxmlLoader = new FXMLLoader(PodioController.class.getResource("/org/grupo2/carreracamelloapp/pantallas/podioUI.fxml"));
            Parent root = null;
            try {
                root = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Scene scene = new Scene(root, 600, 400);

            //Nuevo
            Stage primaryStage = StartApplication.getInstance().getFirtsStage();
            primaryStage.setScene(scene);
            primaryStage.setTitle("Podio");
            primaryStage.setResizable(false);

            PodioController control = fxmlLoader.getController();
            control.setPodio(podio);
            primaryStage.show();
        });
    }
}
