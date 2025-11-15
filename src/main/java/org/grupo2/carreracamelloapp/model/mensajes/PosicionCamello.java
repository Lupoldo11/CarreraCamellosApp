package org.grupo2.carreracamelloapp.model.mensajes;

import java.io.Serializable;

public class PosicionCamello implements Mensaje, Serializable {
    private String movimiento;
    private String camello;

    //Metodos
    @Override
    public String getData() {
        return movimiento;
    }
    @Override
    public void setData(String mensaje) {
        this.movimiento=mensaje;
    }
    public String getCamello (){return camello;}
    public void setCamello (String camello){this.camello=camello;}
}
