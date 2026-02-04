package org.grupo2.carreracamelloapp.model;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class ProtocoloInternetv4 {
    /************************************ Comprobacación de IPv4 Clase D (Listo) *********************************************/
    public static boolean checkMulticast(String ipV4){ //min:224.0.0.1 - max:239.255.255.255
        boolean salida = false;
        String[] fragmentoIPv4 = ipV4.split("\\.");
        if (fragmentoIPv4.length==4){
            int[] fragmentoIPv4Integer = new int[fragmentoIPv4.length];
            for (int i = 0; i < fragmentoIPv4.length; i++) {
                fragmentoIPv4Integer[i] = Integer.parseInt(fragmentoIPv4[i]);
            }

            //Comprobar Validez de IPv4
            if (checkRangoClaseD(fragmentoIPv4Integer[0])){ //Fuera de Clase D
                if (checkRangoIPv4(fragmentoIPv4Integer[1])){
                    if (checkRangoIPv4(fragmentoIPv4Integer[2])) {
                        if(checkRangoIPv4(fragmentoIPv4Integer[3])){
                            if(ipV4.equals("224.0.0.0")){
                                System.out.println("[Error] La IP: 224.0.0.0, está reservada");
                            } else {
                                salida = true;
                            }
                        }
                    }
                }
            } else {
                System.out.println("[Error] La IP:"+ipV4+", no es de clase D");
            }
        }
        if(salida == false){
            System.out.println("[Error] La IP no es correcta");
        }
        return salida;
    }

    /************************************ Sumador más 1 a la IP **************************************/
    public static String generarIPMulticast(String ipV4){
        String[] fragmentoIPv4 = ipV4.split("\\."); //Fracciono la IP
        int[] fragmentoIPv4Integer = new int[fragmentoIPv4.length];
        for (int i = 0; i < fragmentoIPv4.length; i++) { //genero en int
            fragmentoIPv4Integer[i] = Integer.parseInt(fragmentoIPv4[i]);
        }

        if(!checkRangoIPv4(fragmentoIPv4Integer[3] + 1)){
            fragmentoIPv4Integer[3] = 0;
            if(!checkRangoIPv4(fragmentoIPv4Integer[2] + 1)){
                fragmentoIPv4Integer[2] = 0;
                if (!checkRangoIPv4(fragmentoIPv4Integer[1] + 1)){
                    fragmentoIPv4Integer[1] = 0;
                    if (!checkRangoClaseD(fragmentoIPv4Integer[0] + 1)){
                        fragmentoIPv4Integer[0] = 224;
                        fragmentoIPv4Integer[1] = 0;
                        fragmentoIPv4Integer[2] = 0;
                        fragmentoIPv4Integer[3] = 1;
                    } else {
                        fragmentoIPv4Integer[0] += 1;
                    }
                } else {
                    fragmentoIPv4Integer[1] += 1;
                }
            } else {
                fragmentoIPv4Integer[2] += 1;
            }
        } else {
            fragmentoIPv4Integer[3] += 1;
        }

        for(int i = 0; i<fragmentoIPv4Integer.length; i++){
            ipV4= fragmentoIPv4Integer[0] + "." + fragmentoIPv4Integer[1] + "." + fragmentoIPv4Integer[2] + "." + fragmentoIPv4Integer[3];
        }
        return ipV4;
    }

    /************************************** Metodos adicionales a los superiores *****************************/
    public static boolean checkRangoClaseD(int fragmentoIP){
        boolean salida = false;
        if( fragmentoIP <= 239 && fragmentoIP >= 224){
            salida = true;
        } else {
            salida = false;
        }
        return salida;
    }

    public static boolean checkRangoIPv4(int fragmentoIP){
        boolean salida = false;
        if( fragmentoIP <= 255 && fragmentoIP >= 0){
            salida = true;
        } else {
            salida = false;
        }
        return salida;
    }

    public static boolean checkRangoPuerto(int puerto){
        boolean salida = false;
        if( puerto <= 65535 && puerto >= 49152){
            salida = true;
        } else {
            System.out.println("[Error] El puerto no entra en rango");
            salida = false;
        }
        return salida;
    }

    //[Deprecated]
    private static NetworkInterface networkInterface;
    public static NetworkInterface getNetwork(){
        return networkInterface;
    }
    public static NetworkInterface getIPv4Network(){
        NetworkInterface network = null;
        boolean salida = true;

        try {
            Enumeration<NetworkInterface> listNetworks = NetworkInterface.getNetworkInterfaces();
            while(listNetworks.hasMoreElements() && salida){
                NetworkInterface prueba = listNetworks.nextElement();
                if(prueba.isUp() && !prueba.isVirtual() && prueba.supportsMulticast()){
                    network = prueba;
                }
            }

        } catch (SocketException e) {
            System.out.println("[Error] No se ha podido encontrar la IPv4 de eth0");
        }
        networkInterface= network;
        return network;
    }
}
