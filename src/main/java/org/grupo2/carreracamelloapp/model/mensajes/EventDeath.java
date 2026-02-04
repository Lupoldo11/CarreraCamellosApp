package org.grupo2.carreracamelloapp.model.mensajes;

public class EventDeath extends Mensaje{
    private String propietario;

    public EventDeath(String propietario){
        this.propietario = propietario;
    }

    public String getPropietario() {
        return propietario;
    }

    //Implementada
    public static EventDeath parseEventPosicion(Mensaje mensaje){
        return (EventDeath) mensaje;
    }
}
