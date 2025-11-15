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

    private final int posicionMeta = 200;
    private int posicionCamello1 = 0;
    private int posicionCamello2 = 0;
    private int posicionCamello3 = 0;
    private String camello1="mauel";
    private String camello2="ada";
    private String camello3="eda";

    /**************************************** Constructor ***************************************/
    public Servidor(int contador, SendIPMulticast ip){
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
            ServerSocket servidor = new ServerSocket(puertoTCP); //Crea el servidor de Clientes

            System.out.println("Esperando conexión...");
            while(true){ //Espera infinita a jugadores

                //determina la IP de este grupo (cada vuelta = cada grupo)
                SendIPMulticast mensajeMulti = new SendIPMulticast();
                mensajeMulti.setData(getGrupo());

                for(int i = 0; i< numCliente; i++){
                    Socket cliente = servidor.accept();

                    initStream(cliente);

                    enviarPaqueteTCP(mensajeMulti); //Envio de Objeto SendIPMulticast -> Client
                    ListoJoinMulticast ready = (ListoJoinMulticast) recibirPaqueteTCP();
                    //
                    System.out.println("Cliente "+i+" -> "+ready.getData());

                    closeStream();
                    cliente.close(); //cerrarse los clientes ya que se ha mandado la IP
                }

                //Inicia Configuración de la carrera
                Servidor server = new Servidor(contador, mensajeMulti);
                server.joinMulticast();
                server.getHilo().start();
                contador++;
            }
        } catch (IOException e) {
            System.out.println("Servidor 503"); //Servidor no operativo
        }
    }

    /**************************************** Hilos *********************************************/
    @Override
    public void run() {
        //Enviar Inicio Carrera -> Clientes
        InicioCarrera ready = new InicioCarrera();
        ready.setData("200");
        envioPaqueteUDP(ready, ms, grupo);

        //Aquí se administra toda la carrera
        boolean salida = false;
        while (!salida){
            /*if (posicionCamello1 < 755 || posicionCamello2 < 755 || posicionCamello3 < 755){
                try {
                    mensajes.PosicionCamello movimiento = (PosicionCamello) recibirPaqueteUDP(ms);
                    if (movimiento.getCamello().equals(camello1)){
                        posicionCamello1+=Integer.parseInt(movimiento.getData());
                        //modificar en la UI
                    } else if(movimiento.getCamello().equals(camello2)){
                        posicionCamello2+=Integer.parseInt(movimiento.getData());
                        //modificar en la UI
                    } else if(movimiento.getCamello().equals(camello3)){
                        posicionCamello3+=Integer.parseInt(movimiento.getData());
                        //modificar en la UI
                    }
                } catch (IOException e) {} catch (ClassNotFoundException e) {}
            } else {
                salida = true;
            }*/
        }
    }
}
