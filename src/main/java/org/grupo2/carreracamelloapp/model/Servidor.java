package org.grupo2.carreracamelloapp.model;

import org.grupo2.carreracamelloapp.model.mensajes.*;

import java.io.*;
import java.net.*;
import java.util.Arrays;

public class Servidor extends Componente implements Runnable{
    /************************ Atributos Configuración-generales *******************************/
    public static int puertoTCP;
    public static int puertoUDP;
    public static String ipUDP;
    public static final int numCliente = 3; //camellos de cada carrera
    public static int contador = 0; //indicador de vueltas del bucle

    /******************************** Atributos Instancia ***************************************/
    private Thread hiloSala;
    private MulticastSocket ms;
    private InetAddress grupo;
    private AsignacionGrupo datosGrupo;

    private final int posicionMeta = 780;
    private Cliente[] camellos;

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
            ms = new MulticastSocket(datosGrupo.getPuertoUDP()); // -> atributo
            grupo = InetAddress.getByName(datosGrupo.getIpV4Multicast()); // -> atributo

            SocketAddress sa = new InetSocketAddress(grupo, datosGrupo.getPuertoUDP());
            NetworkInterface ni = NetworkInterface.getByInetAddress(ProtocoloInternetv4.getIPv4Network());
            ms.joinGroup(sa, ni); // Unirse al Multicast
        } catch (IOException e) {
            System.out.println("[Error] Error al hacer el multicast");
        }
    }

    public void leaveMulticast(){
        try {
            SocketAddress sa = new InetSocketAddress(grupo, puertoUDP);
            NetworkInterface ni = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()); //mirar esto 
            ms.leaveGroup(sa, ni); //Salirse del Multicast
            System.out.println("[Carrera"+contador+"] Desconectado del Multicast");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Thread getHilo() { return hiloSala; }

    /**************************************** Ejecutables ***************************************/
    public static void ejecutable(String puertoHost, String ipMulticast, String puertoMulticast){
        //Configuración Servidor
        puertoTCP = Integer.parseInt(puertoHost);
        puertoUDP = Integer.parseInt(puertoMulticast);
        ipUDP = ipMulticast;
        System.out.println("[Servidor] Configuración terminada.");

        //Conexión TCP
        try {
            System.out.println("[Servidor] Servidor disponible...");
            ServerSocket servidor = new ServerSocket(puertoTCP,4); //Crea el servidor de Clientes

            while(true){ //Espera infinita a jugadores
                System.out.println("[Servidor] Esperando conexión...");

                //determina la IP de este grupo (cada vuelta = cada grupo)
                AsignacionGrupo mensaje = new AsignacionGrupo(puertoUDP, ipUDP);

                Cliente[] listCamellos = new Cliente[3];

                for(int i = 0; i< numCliente; i++){
                    Socket cliente = servidor.accept();
                    initStream(cliente);

                    enviarPaqueteTCP(mensaje); //Envio de Objeto SendIPMulticast -> Client

                    ListoJoinMulticast ready = (ListoJoinMulticast) recibirPaqueteTCP();
                    System.out.println("[Servidor] Cliente-> "+ready.getData()+" aceptado");
                    listCamellos[i] = new Cliente(ready.getData(),0); //Se crea una Instancia por cada jugador

                    closeStream();
                    cliente.close(); //cerrarse los clientes ya que se ha mandado la IP
                }
                System.out.println("[Servidor] Participantes asignados");

                //Inicia Configuración de la carrera
                Servidor server = new Servidor(contador, mensaje ,listCamellos);
                server.joinMulticast();
                System.out.println("[Servidor] Iniciando Carrera...");
                server.getHilo().start();
                contador++;
                ipUDP = ProtocoloInternetv4.generarIPMulticast(ipUDP);
            }
        } catch (IOException e) {
            System.out.println("[Error] Servidor 503");
        }
    }

    /**************************************** Hilos *********************************************/
    public boolean carrera(EventPosicion eventPosicion){
        boolean salida = false;
        System.out.println("[Carrera"+contador+"] "+eventPosicion.getPropietario()+" se mueve "+eventPosicion.getMovimiento()); //Log del movimiento
        if (camellos[0].getDistancia() < posicionMeta && camellos[1].getDistancia() < posicionMeta
                && camellos[2].getDistancia() < posicionMeta){
            if (eventPosicion.getPropietario().equals(camellos[0].getNombreCliente())){
                camellos[0].movimiento(eventPosicion.getMovimiento());
                System.out.println("[Carrera"+contador+"] "+eventPosicion.getPropietario()+" está en: "+camellos[0].getDistancia());
            } else if (eventPosicion.getPropietario().equals(camellos[1].getNombreCliente())){
                camellos[1].movimiento(eventPosicion.getMovimiento());
                System.out.println("[Carrera"+contador+"] "+eventPosicion.getPropietario()+" está en: "+camellos[1].getDistancia());
            } else if (eventPosicion.getPropietario().equals(camellos[2].getNombreCliente())){
                camellos[2].movimiento(eventPosicion.getMovimiento());
                System.out.println("[Carrera"+contador+"] "+eventPosicion.getPropietario()+" está en: "+camellos[2].getDistancia());
            } else {
                System.out.println("[Warning] Camello no encontrado");
            }
        } else {
            salida = true;
            posicionesMeta();
        }
        return salida;
    }

    public void posicionesMeta(){
        Cliente[] podio; //Camellos A B C
        if (camellos[0].getDistancia()>camellos[1].getDistancia()){
            if (camellos[0].getDistancia()<camellos[2].getDistancia()){ // CAB
                podio = configuracionPodio(camellos[2],camellos[0],camellos[1]);
            } else {
                if (camellos[1].getDistancia()<camellos[2].getDistancia()){ //ACB
                    podio =configuracionPodio(camellos[0],camellos[2],camellos[1]);
                } else { //ABC
                    podio =configuracionPodio(camellos[0],camellos[1],camellos[2]);
                }
            }
        } else {
            if (camellos[1].getDistancia()>camellos[2].getDistancia()){
                if (camellos[2].getDistancia()<camellos[0].getDistancia()){ //BAC
                    podio =configuracionPodio(camellos[1],camellos[0],camellos[2]);
                } else{ //BCA
                    podio =configuracionPodio(camellos[1],camellos[2],camellos[0]);
                }
            } else { //CBA
                podio =configuracionPodio(camellos[2],camellos[1],camellos[0]);
            }
        }
        envioEventFinalizacion(podio);
    }

    public void envioEventFinalizacion(Cliente[] podio){
        EventFinalizacion finalizacion = new EventFinalizacion(podio);
        envioPaqueteUDP(finalizacion,ms,grupo); //Envio mensaje al Multicast
        System.out.println("[Carrera"+contador+"] Carrera finalizada");
        System.out.println("**Podio**\n\t1º) "+podio[0].getNombreCliente()+"\n\t2º) "+podio[1].getNombreCliente()+
                "\n\t3º) "+podio[2].getNombreCliente());
    }

    public Cliente[] configuracionPodio(Cliente primero, Cliente segundo, Cliente tercero){
        return new Cliente[]{primero,segundo,tercero};
    }

    @Override
    public void run() {
        //Enviar Inicio Carrera -> Clientes
        EventInicio inicio = new EventInicio(camellos);
        envioPaqueteUDP(inicio, ms, grupo);
        System.out.println("[Carrera"+contador+"] Carrera Iniciada");

        Mensaje mensaje;
        boolean salida = false;
        while(!salida){
            try {
                mensaje = recibirPaqueteUDP(ms);
                if(mensaje instanceof EventInicio){
                    //Nada, porque sería eco de su propio envio
                } else if (mensaje instanceof EventPosicion){
                    salida = carrera(EventPosicion.parseEventPosicion(mensaje));
                } else {
                    System.out.println("[Warning] Mensaje no identificado");
                }
            } catch (IOException e) {
                System.out.println("[Error] Error en Streams");
            } catch (ClassNotFoundException e) {
                System.out.println("[Error] Error en el cash");
            }
        }

        try { //Esperar un poco a terminar
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println("[Error] Error sleep carrera"+contador);
        }
        System.out.println("[Carrera"+contador+"] Protocolo de cierre...");
        leaveMulticast();
    }
}
