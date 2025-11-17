package org.grupo2.carreracamelloapp.model.mensajes;

import java.io.Serializable;

public class Victoria extends Mensaje{
    private String finalizacion="";
    @Override
    public String getData() {
        return finalizacion;
    }
    @Override
    public void setData(String mensaje) {
        this.finalizacion= mensaje;
    }
}
