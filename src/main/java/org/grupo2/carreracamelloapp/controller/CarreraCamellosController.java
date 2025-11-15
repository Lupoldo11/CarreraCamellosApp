package org.grupo2.carreracamelloapp.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import org.grupo2.carreracamelloapp.model.Cliente;
import org.grupo2.carreracamelloapp.model.mensajes.Mensaje;
import org.grupo2.carreracamelloapp.model.mensajes.PosicionCamello;
import org.grupo2.carreracamelloapp.model.mensajes.Victoria;

public class CarreraCamellosController {

    private static Cliente camello;
    public static void setCliente(Cliente cliente) {
        camello = cliente;
    }
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

    @FXML
    protected void onIniciarCarreraClick(ActionEvent event) {
        PosicionCamello moviento = new PosicionCamello();
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

    protected void escuchaMoverCamellos(Mensaje msg){
        PosicionCamello moviento = (PosicionCamello) msg;
        if (moviento.getCamello().equals(camello.getCamello2())){
            camello2.setLayoutX(camello2.getLayoutX() + Integer.parseInt(moviento.getData()));
        } else if (moviento.getCamello().equals(camello.getCamello3())){
            camello3.setLayoutX(camello3.getLayoutX() + Integer.parseInt(moviento.getData()));
        }
    }

    public void victoria(Mensaje msg){
        Victoria winner = (Victoria) msg;
        mostrarWinner(winner.getData());
    }

    public void escuchaMovimientoMulticast(Mensaje msg){
        escuchaMoverCamellos(msg);
    }

    public void butonON(){
        iniciarCarreraButton.setDisable(false);
    }

    //Metodo lanza mensaje victoria
}
