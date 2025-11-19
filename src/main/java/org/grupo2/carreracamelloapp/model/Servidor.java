package org.grupo2.carreracamelloapp.model;

import org.grupo2.carreracamelloapp.model.mensajes.*;

import java.io.*;
import java.net.*;

public class Servidor extends Componente implements Runnable{
    /************************ Atributos Configuración-generales *******************************/
    public static int puertoTCP = 12345;
    public static int puertoUDP = 54321;
    public static final int numCliente = 3; //camellos de cada carrera
    public static int contador = 0; //indicador de vueltas del bucle

    /******************************** Atributos Instancia ***************************************/
    private String grupoMulticast = "";
    private Thread hiloSala;
    private MulticastSocket ms;
    private InetAddress grupo;
    private int puertoMulti;

    private final int posicionMeta = 755;
    private int posicionCamello1 = 0;
    private int posicionCamello2 = 0;
    private int posicionCamello3 = 0;
    private String[] camellos;

    /**************************************** Constructor ***************************************/
    public Servidor(int contador, SendIPMulticast ip, String[] camellos){
        this.camellos = camellos;
        String[] config = ip.getData().split(",");
        this.puertoMulti = Integer.parseInt(config[1]);
        this.grupoMulticast= config[0];
        this.hiloSala = new Thread(this, "Sala" + contador);
        System.out.println("Hilo"+contador+" creado");
    }

    /**************************************** Métodos ***************************************/
    public void joinMulticast(){
        try {
            ms = new MulticastSocket(puertoMulti); // -> atributo
            grupo = InetAddress.getByName(grupoMulticast); // -> atributo

            SocketAddress sa = new InetSocketAddress(grupo, puertoMulti);
            NetworkInterface ni = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            ms.joinGroup(sa, ni); // Unirse al Multicast
        } catch (IOException e) {
            System.out.println("Error al hacer el multicast");
        }
    }

    public void leaveMulticast(){
        try {
            SocketAddress sa = new InetSocketAddress(grupo, puertoUDP);
            NetworkInterface ni = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            ms.leaveGroup(sa, ni); //Salirse del Multicast
            System.out.println("Desconectado del Multicast");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Thread getHilo() { return hiloSala; }

    /**************************************** Métodos Static ***************************************/
    public static String getGrupo(){
        if(contador >=255 || contador==0 ){
            contador = 1;
        }
        return ("230.0.0."+contador+","+puertoUDP).trim();
        //Metodo que saca la IP de clase D que tiene que ir generando
        //*Puede mejorarse para que genere desde el menor rango hasta el mayor (limitar la generación de IPs)
    }

    /**************************************** Ejecutables ***************************************/
    public static void main(String[] args){
        //Conexión TCP
        try {
            InetAddress ipTCP = InetAddress.getByName("10.102.189.254");
            //IP clase: 192.168.13.1
            ServerSocket servidor = new ServerSocket(puertoTCP,4); //Crea el servidor de Clientes

            System.out.println("Esperando conexión...");
            while(true){ //Espera infinita a jugadores

                //determina la IP de este grupo (cada vuelta = cada grupo)
                SendIPMulticast mensajeMulti = new SendIPMulticast();
                mensajeMulti.setData(getGrupo());

                String[] camellos = new String[3];

                for(int i = 0; i< numCliente; i++){
                    Socket cliente = servidor.accept();

                    initStream(cliente);

                    enviarPaqueteTCP(mensajeMulti); //Envio de Objeto SendIPMulticast -> Client
                    ListoJoinMulticast ready = (ListoJoinMulticast) recibirPaqueteTCP();
                    System.out.println("Cliente "+i+" -> "+ready.getData());
                    camellos[i]=ready.getData();
                    closeStream();
                    cliente.close(); //cerrarse los clientes ya que se ha mandado la IP
                }

                //Inicia Configuración de la carrera
                Servidor server = new Servidor(contador, mensajeMulti,camellos);
                server.joinMulticast();
                server.getHilo().start();
                contador++;
            }
        } catch (IOException e) {
            System.out.println("Servidor 503");
        }
    }

    /**************************************** Hilos *********************************************/
    @Override
    public void run() {
        //Enviar Inicio Carrera -> Clientes
        Mensaje inicio = new Mensaje();
        inicio.setCamellos(camellos);
        inicio.setData("inicio");
        envioPaqueteUDP(inicio, ms, grupo);


        try {
            recibirPaqueteUDP(ms);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error recibir inicio");
        }
        //Aquí se administra toda la carrera
        Mensaje posicion;
        String ganador = "";
        boolean salida = false;
        while (!salida){
            try {
                posicion = recibirPaqueteUDP(ms);
                if (posicionCamello1 <= posicionMeta && posicionCamello2 <= posicionMeta
                        && posicionCamello3 <= posicionMeta){
                    if (posicion.getCamello().equals(camellos[0])){
                        posicionCamello1+=Integer.parseInt(posicion.getData());
                    } else if (posicion.getCamello().equals(camellos[1])){
                        posicionCamello2+=Integer.parseInt(posicion.getData());
                    } else if (posicion.getCamello().equals(camellos[2])){
                        posicionCamello3+=Integer.parseInt(posicion.getData());
                    }
                } else {
                    salida = true;
                    if (posicionCamello1 >= posicionMeta) {
                        ganador = camellos[0];
                    } else if (posicionCamello2 >= posicionMeta) {
                        ganador = camellos[1];
                    } else {
                        ganador = camellos[2];
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println();
            }
        }

        Mensaje victoria = new Mensaje();
        victoria.setData(ganador);
        envioPaqueteUDP(victoria, ms, grupo);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println("Error hilo");
        }
        leaveMulticast();
    }
}
