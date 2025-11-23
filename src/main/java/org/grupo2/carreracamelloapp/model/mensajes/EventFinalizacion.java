package org.grupo2.carreracamelloapp.model.mensajes;

import org.grupo2.carreracamelloapp.model.Cliente;

public class EventFinalizacion extends Mensaje{
    private Cliente[] podio;

    public EventFinalizacion(Cliente[] podio){
        this.podio=podio;
    }

    public Cliente[] getPodio() {
        return podio;
    }

    //Implementada
    public static EventFinalizacion parseEventFinalizacion(Mensaje mensaje){
        return (EventFinalizacion) mensaje;
    }
}
