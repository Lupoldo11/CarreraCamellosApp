package org.grupo2.carreracamelloapp.model.Pruebas;

import org.grupo2.carreracamelloapp.Launcher;

public class LanzadorC2 {
    // CONFIGURAR AQUÍ la IP del servidor donde se ejecuta LanzadorS
    private static final String SERVIDOR_IP = "192.168.1.133"; // Cambiar por la IP real del servidor

    public static void main(String[] args){
        System.out.println("[LanzadorC2] Conectando al servidor en: " + SERVIDOR_IP);
        args = new String[]{SERVIDOR_IP, "50000", "cliente", "rober"};
        Launcher.main(args);
    }
}
