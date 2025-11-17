package org.grupo2.carreracamelloapp.model.mensajes;

import java.io.Serializable;

public class ListoJoinMulticast extends Mensaje{
    private String nombre;

    @Override
    public String getData() {
        return nombre;
    }
    @Override
    public void setData(String mensaje) {
        this.nombre=mensaje;
    }
}
