package org.grupo2.carreracamelloapp.model;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collection;
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
    /*********************************************** Comprobar rango Puertos *****************************************************/

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

    private static NetworkInterface networkInterface;
    public static NetworkInterface getNetwork(){
        return networkInterface;
    }
    
    /****** Método para obtener la IP local IPv4 dinámicamente *******/
    public static String getLocalIPv4Address(){
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while(interfaces.hasMoreElements()){
                NetworkInterface iface = interfaces.nextElement();
                if(iface.isUp() && !iface.isLoopback() && !iface.isVirtual()){
                    Enumeration<InetAddress> addresses = iface.getInetAddresses();
                    while(addresses.hasMoreElements()){
                        InetAddress address = addresses.nextElement();
                        if(address instanceof Inet4Address && !address.isLoopbackAddress()){
                            return address.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            System.out.println("[Error] No se ha podido obtener la dirección IPv4 local");
        }
        return "127.0.0.1"; //fallback a localhost
    }
    
    public static NetworkInterface getIPv4Network(){
        try {
            Enumeration<NetworkInterface> listNetworks = NetworkInterface.getNetworkInterfaces();
            while(listNetworks.hasMoreElements()){
                NetworkInterface prueba = listNetworks.nextElement();
                // 🔥 PRIORIDAD: WiFi/Ethernet REAL (nombre contiene "wlan", "WiFi", "Ethernet")
                if(prueba.isUp() && !prueba.isLoopback() && !prueba.isVirtual() &&
                        (prueba.getDisplayName().toLowerCase().contains("wlan") ||
                                prueba.getDisplayName().toLowerCase().contains("wifi") ||
                                prueba.getDisplayName().toLowerCase().contains("ethernet"))){

                    System.out.println("🔍 INTERFAZ ELEGIDA: " + prueba.getDisplayName());
                    Enumeration<InetAddress> ips = prueba.getInetAddresses();
                    while(ips.hasMoreElements()){
                        InetAddress ip = ips.nextElement();
                        if(ip instanceof Inet4Address && !ip.isLoopbackAddress()){
                            System.out.println("   → IP LAN: " + ip.getHostAddress());
                        }
                    }
                    networkInterface = prueba;
                    return prueba;
                }
            }
        } catch (SocketException e) {
            System.out.println("[Error] No se ha podido encontrar interfaz LAN");
        }
        return null;  // Falla si no encuentra WiFi/Ethernet
    }
}
