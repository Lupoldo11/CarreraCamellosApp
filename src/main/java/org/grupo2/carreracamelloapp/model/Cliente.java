package org.grupo2.carreracamelloapp.model;

import javafx.application.Application;
import org.grupo2.carreracamelloapp.StartApplication;
import org.grupo2.carreracamelloapp.model.mensajes.*;

import java.io.*;
import java.net.*;

public class Cliente extends Componente implements Runnable{
    /******************************* Atributos Static *********************************************/
    //Conexión TCP
    public static int puertoTCP = 12345;
    public static String conexionTCP = "localhost"; //cambiar por IP del ordenador en la red WEDU

    /******************************* Atributos Clase *********************************************/
    private String ipMulti;
    private MulticastSocket ms;
    private InetAddress grupo;
    private int puertoUDP;
    private Thread hiloConexión;

    private int posicionCamello1;
    private int posicionCamello2;
    private int posicionCamello3;
    private String camello1;
    private String camello2;
    private String camello3;

    /**************************************** Constructor ***************************************/
    public Cliente(SendIPMulticast ip){
        hiloConexión = new Thread(this, "conexion");
        String[] config = ip.getData().split(",");
        this.ipMulti = config[0].trim();
        this.puertoUDP= Integer.parseInt(config[1].trim());
    }

    /**************************************** Métodos *******************************************/
    public Thread getHilo(){
        return hiloConexión;
    }

    public void joinMulticast(){
        try {
            //Inicio de conexión multicast UDP
            ms = new MulticastSocket(puertoUDP); //averiguar como sacarlo
            grupo = InetAddress.getByName(ipMulti); //nombreIPMulticast

            SocketAddress sa = new InetSocketAddress(grupo, puertoUDP); //Prueba conectar
            NetworkInterface ni = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            ms.joinGroup(sa, ni); //Se uniría
        } catch (IOException e) {
            System.out.println("Error al Conectarse con el multicast");
            e.printStackTrace();
        }
    }

    /**************************************** Ejecutables ***************************************/
    public static void main(String[] args){
        try {
            //Conectando con el Servidor (TCP)
            Socket cliente = new Socket(conexionTCP,puertoTCP);
            System.out.println("Conectandose...");

            initStream(cliente);

            //Recibe la IP Multicast
            SendIPMulticast IPMulti = (SendIPMulticast) recibirPaqueteTCP();
            System.out.println(IPMulti.getData());

            //Mandar OK
            ListoJoinMulticast ready = new ListoJoinMulticast();
            ready.setData("200");
            enviarPaqueteTCP(ready);

            closeStream();
            cliente.close(); //Cierra la TCP

            //A partir de aquí hay que administrar la carrera
            Cliente camello = new Cliente(IPMulti);
            camello.joinMulticast();
            camello.getHilo().start();
            Application.launch(StartApplication.class, args); //Metodo que lanza el JavaFX
        } catch (IOException e) {
            System.out.println("Servidor Cerrado (Esto es cliente)"); //El cliente no se puede conectar
        }
    }

    /**************************************** Hilo ***************************************/
    @Override
    public void run() {
        // Esperar el listo del servidor con el boton en "no pulsable"
        // El server envia un mensaje de cambio el boton a pulsable

        try {
            InicioCarrera ready = (InicioCarrera) recibirPaqueteUDP(ms); //Esta recepcion tiene que cambiar el boton a activo
            System.out.println(ready.getData());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Paquete inicio carrera no recibido");
        }

        boolean salida = false;
        while (!salida){
            /*try {
                PosicionCamello movimiento = (PosicionCamello) recibirPaqueteUDP(ms);
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

                //SI DAN CLICK AL BOTON ENVIAR
                PosicionCamello move = new PosicionCamello();
                move.setCamello("pito");
                move.setData("2");
                envioPaqueteUDP(move, ms, grupo);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }*/
        }
    }
}
