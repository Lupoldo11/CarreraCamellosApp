package org.grupo2.carreracamelloapp.model.mensajes;

import java.io.Serializable;

public class SendIPMulticast implements Mensaje, Serializable {
    //Atributos
    private String IPMulti; //TIPO: 230.0.0.1,54321 (IP , puertoUDP)

    //Metodos
    @Override
    public String getData() {
        return IPMulti;
    }
    @Override
    public void setData(String mensaje) {
        this.IPMulti = mensaje;
    }
}
