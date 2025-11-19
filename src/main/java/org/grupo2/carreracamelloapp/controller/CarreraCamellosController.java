package org.grupo2.carreracamelloapp.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import org.grupo2.carreracamelloapp.model.Cliente;
import org.grupo2.carreracamelloapp.model.mensajes.Mensaje;

public class CarreraCamellosController {

    /******************************* Guardado Cliente *********************************************/
    private static Cliente camello;
    public static void setCliente(Cliente cliente) {
        camello = cliente;
    }

    /******************************* Atributos del UI *********************************************/
    @FXML
    private Button iniciarCarreraButton;

    @FXML
    private Text winnerLabel;

    @FXML
    private ImageView camello1;

    @FXML
    private ImageView camello2;

    @FXML
    private ImageView camello3;

    /******************************* Metodos UI (Controller) *********************************************/
    @FXML
    protected void onIniciarCarreraClick(ActionEvent event) {
        Mensaje moviento = new Mensaje();
        moviento.setCamello(camello.getNombreCliente());
        int numMovimiento = camello.movimientoRandom();
        moviento.setData(String.valueOf(numMovimiento));
        camello1.setLayoutX(camello1.getLayoutX() + numMovimiento);
        camello.envioPaqueteUDP(moviento, camello.getMS(), camello.getInetAddress());
    }

    @FXML
    protected void mostrarWinner(String ganador) {
        winnerLabel.setText("El ganador de la carrera es: " + ganador);
    }

    //Mover camellos
    protected void escuchaMoverCamellos(Mensaje msg){
        Mensaje moviento = msg;
        if (moviento.getCamello().equals(camello.getCamello2())){
            camello2.setLayoutX(camello2.getLayoutX() + Integer.parseInt(moviento.getData()));
        } else if (moviento.getCamello().equals(camello.getCamello3())){
            camello3.setLayoutX(camello3.getLayoutX() + Integer.parseInt(moviento.getData()));
        }
    }

    /******************************* Metodos Acceso Exterior *********************************************/
    //Mensaje Victoria
    public void victoria(Mensaje msg){
        Mensaje winner =  msg;
        mostrarWinner(winner.getData());
    }

    //Para enviar el movimiento desde fuera
    public void escuchaMovimientoMulticast(Mensaje msg){
        escuchaMoverCamellos(msg);
    }

    //Habilitar el boton
    public void butonON(){
        iniciarCarreraButton.setDisable(false);
    }
}
