package org.grupo2.carreracamelloapp.model;

import org.grupo2.carreracamelloapp.model.mensajes.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Servidor extends Componente implements Runnable{

    /************************ Atributos Configuración-generales *******************************/

    public static int puertoTCP;
    public static int puertoUDP;
    public static String ipUDP;
    public static final int numCliente = 3;
    public static int contador = 0;

    /******************************** Atributos Instancia ***************************************/

    private Thread hiloSala;
    private MulticastSocket ms;
    private InetAddress grupo;
    private AsignacionGrupo datosGrupo;
    private final int posicionMeta = 780;
    private Cliente[] camellos;
    private NetworkInterface networkInterface;

    /**************************************** Constructor ***************************************/

    public Servidor(int contador, AsignacionGrupo datosGrupo, Cliente[] camellos){
        this.camellos = camellos;
        this.datosGrupo = datosGrupo;
        this.hiloSala = new Thread(this, "Sala" + contador);
        System.out.println("[Carrera"+contador+"] Generado hilo de la carrera");
    }

    /**************************************** Métodos ***************************************/

    public void joinMulticast(){
        try {
            ms = new MulticastSocket(datosGrupo.getPuertoUDP());
            grupo = InetAddress.getByName(datosGrupo.getIpV4Multicast());
            SocketAddress sa = new InetSocketAddress(grupo, datosGrupo.getPuertoUDP());
            networkInterface = ProtocoloInternetv4.getIPv4Network();
            ms.joinGroup(sa, networkInterface);
            System.out.println("[Servidor] Unido a multicast " + grupo.getHostAddress() +
                    ":" + datosGrupo.getPuertoUDP());
        } catch (IOException e) {
            System.out.println("[Error] Error al hacer el multicast");
            e.printStackTrace();
        }
    }

    public void leaveMulticast(){
        try {
            SocketAddress sa = new InetSocketAddress(grupo, datosGrupo.getPuertoUDP());
            ms.leaveGroup(sa, networkInterface);
            ms.close();
            System.out.println("[Carrera"+contador+"] Desconectado del Multicast");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Thread getHilo() { return hiloSala; }

    /**************************************** Ejecutables ***************************************/

    public static void ejecutable(String puertoHost, String ipMulticast, String puertoMulticast){
        puertoTCP = Integer.parseInt(puertoHost);
        puertoUDP = Integer.parseInt(puertoMulticast);
        ipUDP = ipMulticast;
        System.out.println("[Servidor] Configuración terminada.");

        try {
            System.out.println("[Servidor] Servidor disponible en puerto " + puertoTCP);
            ServerSocket servidor = new ServerSocket(puertoTCP, 50, InetAddress.getByName("0.0.0.0"));

            System.out.println("\n========== ESPERANDO " + numCliente + " CLIENTES ==========");

            AsignacionGrupo mensaje = new AsignacionGrupo(puertoUDP, ipUDP);
            Cliente[] listCamellos = new Cliente[numCliente];

            for(int i = 0; i < numCliente; i++){
                System.out.println("[Servidor] Esperando cliente " + (i+1) + "/" + numCliente + "...");

                Socket cliente = servidor.accept();
                System.out.println("[Servidor] ✅ Cliente " + (i+1) + " conectado: " + cliente.getInetAddress());

                initStream(cliente);
                enviarPaqueteTCP(mensaje);

                ListoJoinMulticast ready = (ListoJoinMulticast) recibirPaqueteTCP();
                System.out.println("[Servidor] ✅ Confirmado: '" + ready.getData() + "'");

                listCamellos[i] = new Cliente(ready.getData(), 0);

                closeStream();
                cliente.close();
            }

            System.out.println("\n========== TODOS CONECTADOS ==========");
            for(int i = 0; i < listCamellos.length; i++){
                System.out.println("  [" + i + "] " + listCamellos[i].getNombreCliente());
            }

            Servidor server = new Servidor(contador, mensaje, listCamellos);
            server.joinMulticast();
            System.out.println("\n[Servidor] INICIANDO CARRERA...\n");
            server.getHilo().start();

        } catch (IOException e) {
            System.out.println("[Error] Servidor falló");
            e.printStackTrace();
        }
    }

    /**************************************** Hilos *********************************************/

    public boolean carrera(EventPosicion eventPosicion){
        boolean salida = false;

        if (camellos[0].getDistancia() < posicionMeta &&
                camellos[1].getDistancia() < posicionMeta &&
                camellos[2].getDistancia() < posicionMeta){

            if (eventPosicion.getPropietario().equals(camellos[0].getNombreCliente())){
                camellos[0].movimiento(eventPosicion.getMovimiento());
                System.out.println("[Servidor] " + camellos[0].getNombreCliente() + " -> " + camellos[0].getDistancia());
            } else if (eventPosicion.getPropietario().equals(camellos[1].getNombreCliente())){
                camellos[1].movimiento(eventPosicion.getMovimiento());
                System.out.println("[Servidor] " + camellos[1].getNombreCliente() + " -> " + camellos[1].getDistancia());
            } else if (eventPosicion.getPropietario().equals(camellos[2].getNombreCliente())){
                camellos[2].movimiento(eventPosicion.getMovimiento());
                System.out.println("[Servidor] " + camellos[2].getNombreCliente() + " -> " + camellos[2].getDistancia());
            }
        } else {
            salida = true;
            posicionesMeta();
        }
        return salida;
    }

    public void posicionesMeta(){
        Cliente[] podio;
        if (camellos[0].getDistancia()>camellos[1].getDistancia()){
            if (camellos[0].getDistancia()>camellos[2].getDistancia()){
                if (camellos[2].getDistancia()>camellos[1].getDistancia()){
                    podio = new Cliente[]{camellos[0],camellos[2],camellos[1]};
                } else {
                    podio = new Cliente[]{camellos[0],camellos[1],camellos[2]};
                }
            } else {
                podio = new Cliente[]{camellos[2],camellos[0],camellos[1]};
            }
        } else {
            if (camellos[1].getDistancia()>camellos[2].getDistancia()){
                if (camellos[2].getDistancia()>camellos[0].getDistancia()){
                    podio = new Cliente[]{camellos[1],camellos[2],camellos[0]};
                } else {
                    podio = new Cliente[]{camellos[1],camellos[0],camellos[2]};
                }
            } else {
                podio = new Cliente[]{camellos[2],camellos[1],camellos[0]};
            }
        }
        EventFinalizacion end = new EventFinalizacion(podio);
        envioPaqueteUDP(end, ms, grupo, datosGrupo.getPuertoUDP());
        System.out.println("[Servidor] CARRERA FINALIZADA");
    }

    @Override
    public void run() {
        EventInicio inicio = new EventInicio(camellos);

        // ✅ ESPERAR 2 segundos a que todos se unan al multicast
        System.out.println("[Servidor] Esperando a que todos se unan al multicast...");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // ✅ ENVIAR EventInicio 3 VECES para asegurar que todos lo reciben
        System.out.println("[Servidor] Enviando EventInicio...");
        for(int i = 0; i < 3; i++) {
            envioPaqueteUDP(inicio, ms, grupo, datosGrupo.getPuertoUDP());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("[Servidor] EventInicio enviado (x3)");

        boolean salida = false;
        while(!salida){
            try {
                Mensaje mensaje = recibirPaqueteUDP(ms);
                if(mensaje instanceof EventInicio){
                    // eco ignorado
                } else if (mensaje instanceof EventPosicion){
                    salida = carrera(EventPosicion.parseEventPosicion(mensaje));
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("[Error] " + e.getMessage());
            }
        }

        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        leaveMulticast();
        System.out.println("[Servidor] FIN");
    }

}
