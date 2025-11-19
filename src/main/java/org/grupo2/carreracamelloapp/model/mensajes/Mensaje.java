package org.grupo2.carreracamelloapp.model.mensajes;

import java.io.Serializable;

public class Mensaje implements Serializable {
    private String msg; //Que mensaje es (Inicio, Posicion o Victoria)
    private String camello; //Quien es el que envia el mensaje
    private String[] camellos; //participantes


    public String getData(){
        return msg;
    };
    public void setData(String mensaje){
        this.msg = mensaje;
    };
    public void setCamello(String camello){
        this.camello = camello;
    }
    public String getCamello(){
        return camello;
    }
    public String[] getCamellos(){
        return camellos;
    }
    public void setCamellos(String[] camellos){
        this.camellos=camellos;
    }
}
