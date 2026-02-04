package org.grupo2.carreracamelloapp.model.mensajes;


public class ListoJoinMulticast extends Mensaje{
    private String nombre;

    public String getData() {
        return nombre;
    }

    public void setData(String mensaje) {
        this.nombre=mensaje;
    }
}
