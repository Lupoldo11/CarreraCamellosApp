package org.grupo2.carreracamelloapp.model.mensajes;

import java.io.Serializable;

public class InicioCarrera implements Mensaje, Serializable {
    private String inicio="";
    @Override
    public String getData() {
        return inicio;
    }
    @Override
    public void setData(String mensaje) {
        this.inicio=mensaje;
    }
}
