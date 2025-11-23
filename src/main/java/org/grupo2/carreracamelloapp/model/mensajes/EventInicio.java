package org.grupo2.carreracamelloapp.model.mensajes;

import org.grupo2.carreracamelloapp.model.Cliente;

public class EventInicio extends Mensaje{
    private Cliente[] participantes;
    //private boolean activarBoton = true;

    public EventInicio(Cliente[] participantes){
        this.participantes = participantes;
    }

    public Cliente[] getParticipantes() {
        return participantes;
    }

    //Implementada
    public static EventInicio parseEventInicio(Mensaje mensaje){
        return (EventInicio) mensaje;
    }
}
