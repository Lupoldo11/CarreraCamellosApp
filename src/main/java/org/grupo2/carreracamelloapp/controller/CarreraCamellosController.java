package org.grupo2.carreracamelloapp.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import org.grupo2.carreracamelloapp.model.Cliente;
import org.grupo2.carreracamelloapp.model.mensajes.EventFinalizacion;
import org.grupo2.carreracamelloapp.model.mensajes.EventPosicion;

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

    //Mover camellos
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
        escuchaMoverCamellos(eventPosicion);
    }

    //Habilitar el boton
    public void butonON(){
        //Activar el boton  UI
        iniciarCarreraButton.setVisible(true);
        System.out.println("[Controller] Cambiado de estado el boton a visible..");
    }
}
