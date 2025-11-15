package org.grupo2.carreracamelloapp.model.mensajes;

import java.io.Serializable;

public class InicioCarrera implements Mensaje, Serializable {
    private String[] camellos;
    private String inicio="";
    @Override
    public String getData() {
        return inicio;
    }
    @Override
    public void setData(String mensaje) {
        this.inicio=mensaje;
    }
    public String[] getCamellos(){return camellos;}
    public void setCamellos(String[] camellos){this.camellos=camellos;}
}
