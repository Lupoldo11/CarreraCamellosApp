package org.grupo2.carreracamelloapp.model.mensajes;

import java.io.Serializable;

public class ListoJoinMulticast extends Mensaje{
    private String nombre;

    public String getData() {
        return nombre;
    }

    public void setData(String mensaje) {
        this.nombre=mensaje;
    }
}
