package org.grupo2.carreracamelloapp.model.mensajes;

import java.io.Serializable;

public class AsignacionGrupo extends Mensaje implements Serializable{
    private int puertoUDP;
    private String ipV4Multicast;

    public AsignacionGrupo(int puerto, String ip){
        this.ipV4Multicast=ip;
        this.puertoUDP=puerto;
    }

    public int getPuertoUDP(){
        return puertoUDP;
    }
    public String getIpV4Multicast(){
        return ipV4Multicast;
    }
}
