package org.grupo2.carreracamelloapp.model.Pruebas;

import org.grupo2.carreracamelloapp.Launcher;
import org.grupo2.carreracamelloapp.model.ProtocoloInternetv4;

public class LanzadorS {
    public static void main(String[] args){
        // Obtener IP local dinámicamente en lugar de hardcodearla
        String ipLocal = ProtocoloInternetv4.getLocalIPv4Address();
        System.out.println("[LanzadorS] IP local detectada: " + ipLocal);
        args = new String[]{ipLocal, "50000", "servidor", "230.0.0.0", "54321"};
        Launcher.main(args);
    }
}
