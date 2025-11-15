package org.grupo2.carreracamelloapp.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class CarreraCamellosController {

    @FXML
    private ImageView camello1;

    @FXML
    private ImageView camello2;

    @FXML
    private ImageView camello3;

    @FXML
    private Text winnerLabel;

    @FXML
    protected void onIniciarCarreraClick(ActionEvent event) {
        double moveDistance = 30; // píxeles a mover por clic
        camello1.setLayoutX(camello1.getLayoutX() + moveDistance);
        camello2.setLayoutX(camello2.getLayoutX() + moveDistance);
        camello3.setLayoutX(camello3.getLayoutX() + moveDistance);
    }

    @FXML
    protected void mostrarWinner(String ganador) {
        winnerLabel.setText("El ganador de la carrera es: " + ganador);
    }

}
