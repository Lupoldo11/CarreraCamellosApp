package org.grupo2.carreracamelloapp.model.mensajes;

public class EventPosicion extends Mensaje{
    private String propietario;
    private int movimiento;

    public EventPosicion(String propietario, int movimiento){
        this.propietario = propietario;
        this.movimiento = movimiento;
    }

    public String getPropietario() {
        return propietario;
    }

    public int getMovimiento() {
        return movimiento;
    }

    //Implementada
    public static EventPosicion parseEventPosicion(Mensaje mensaje){
        return (EventPosicion) mensaje;
    }
}
