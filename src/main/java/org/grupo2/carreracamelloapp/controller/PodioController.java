package org.grupo2.carreracamelloapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.grupo2.carreracamelloapp.model.Cliente;

public class PodioController{

    @FXML
    private Label gold;

    @FXML
    private Label silver;

    @FXML
    private Label bronze;

    public void setPodio(Cliente[] podio) {
        gold.setText(podio[0].getNombreCliente());
        silver.setText(podio[1].getNombreCliente());
        bronze.setText(podio[2].getNombreCliente());
    }

}
