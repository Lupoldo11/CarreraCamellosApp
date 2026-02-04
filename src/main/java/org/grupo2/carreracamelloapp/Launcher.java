package org.grupo2.carreracamelloapp;

import org.grupo2.carreracamelloapp.model.Cliente;
import org.grupo2.carreracamelloapp.model.ProtocoloInternetv4;
import org.grupo2.carreracamelloapp.model.Servidor;

public class Launcher { //IP clase: 192.168.113.1

    public static void main(String[] args) {
        System.out.println("[Programa] Comenzando programa....");
        if(args[2].equals("servidor") && args.length == 5){
            inicio(args);
        } else if(args[2].equals("cliente") && args.length == 4){
            inicio(args);
        } else {
            System.out.println("[Error] El argumento opción es inválido o no es el rango indicado de argumentos");
        }
        System.out.println("[Programa] Cerrando el programa....");
    }

    /**
     * Diferencia entra Servidor y Cliente (comprueba argumentos)
     * */
    public static void inicio(String[] configuracion){

        System.out.println("[Programa] Configurando preferencias.....");
        switch (configuracion[2]){
            case "servidor": //IPHost PuertoTCP servidor IPMulti PuertoUDP
                if(ProtocoloInternetv4.checkMulticast(configuracion[3]) &&
                        ProtocoloInternetv4.checkRangoPuerto(Integer.parseInt(configuracion[4]))){
                    System.out.println("[Servidor] Bienvenido al lado del Servidor. IPHost:"+configuracion[0]+", Puerto:"+configuracion[1]);
                    Servidor.ejecutable(configuracion[1],configuracion[3],configuracion[4]);
                }
                break;
            case "cliente": //IPHost PuertoTCP cliente nombre
                if(ProtocoloInternetv4.checkRangoPuerto(Integer.parseInt(configuracion[1]))){
                    System.out.println("[Cliente] Bienvenido "+configuracion[3]+" al lado Cliente.");
                    Cliente.ejecutable(configuracion[0],configuracion[1],configuracion[3]); //Pasamos: IP, puerto, nombreCliente
                }
                break;
            default:
                System.out.println("[Error] Los argumentos de Inicio no son válidos");
                break;
        }
    }
}
