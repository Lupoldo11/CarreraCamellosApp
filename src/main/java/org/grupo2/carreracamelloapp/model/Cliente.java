package org.grupo2.carreracamelloapp.model;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import org.grupo2.carreracamelloapp.StartApplication;
import org.grupo2.carreracamelloapp.controller.CarreraCamellosController;
import org.grupo2.carreracamelloapp.model.mensajes.*;

import java.io.*;
import java.net.*;

public class Cliente extends Componente implements Runnable{
    /******************************* Atributos Static *********************************************/
    //Conexion TCP
    public static int puertoTCP = 12345;
    public static String conexionTCP = "192.168.113.1"; //cambiar por IP del ordenador en la red WEDU o localhost

    /******************************* Atributos Clase *********************************************/
    private String ipMulti;
    private MulticastSocket ms;
    private InetAddress grupo;
    private int puertoUDP;
    private Thread hiloConexión;
    private String nombreCliente; //nombreCliente == camello1

    private String camello2;
    private String camello3;

    /**************************************** Constructor ***************************************/
    public Cliente(SendIPMulticast ip, String nombreCliente){
        this.nombreCliente = nombreCliente;
        hiloConexión = new Thread(this, "conexion");
        String[] config = ip.getData().split(",");
        this.ipMulti = config[0].trim();
        this.puertoUDP= Integer.parseInt(config[1].trim());
    }

    /**************************************** Métodos *******************************************/
    public String getCamello2(){return camello2;}
    public String getCamello3(){return camello3;}
    public String getNombreCliente(){return nombreCliente;}
    public InetAddress getInetAddress(){ return grupo;}
    public MulticastSocket getMS(){return ms;}
    public Thread getHilo(){return hiloConexión;}

    public void asignarCamellos(String[] camellos){
        if (camellos[0].equals(nombreCliente)){
            camello2 = camellos[1];
            camello3 = camellos[2];
        } else if (camellos[1].equals(nombreCliente)){
            camello2 = camellos[0];
            camello3 = camellos[2];
        } else {
            camello2 = camellos[0];
            camello3 = camellos[1];
        }
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
    /**************************************** Ejecutables ***************************************/
    public static void main(String args){
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
            ready.setData(args);
            enviarPaqueteTCP(ready);

            closeStream();
            cliente.close(); //Cierra la TCP

            //A partir de aquí hay que administrar la carrera
            Cliente camello = new Cliente(IPMulti, args);
            camello.joinMulticast();
            camello.getHilo().start();
            CarreraCamellosController.setCliente(camello);
            Application.launch(StartApplication.class, args); //Metodo que lanza el JavaFX
        } catch (IOException e) {
            System.out.println("Servidor Cerrado (Esto es cliente)");
        }
    }

    /**************************************** Hilo ***************************************/
    @Override
    public void run() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("pantallas/carreraCamellosUI.fxml"));
        CarreraCamellosController controller = fxmlLoader.getController();
        Mensaje msg;

        try {
            Mensaje ready = recibirPaqueteUDP(ms);
            System.out.println(ready.getData());

            asignarCamellos(ready.getCamellos());

            controller.butonON(); //Pone el boton en OK
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Paquete inicio carrera no recibido");
        }

        boolean salida = false;
        while (!salida){
            try {
                msg = recibirPaqueteUDP(ms);
                if (msg.getData().equals("victoria")){
                    controller.victoria(msg);
                    salida = true;
                    Thread.sleep(4000);
                } else  {
                    controller.escuchaMovimientoMulticast(msg);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println();
            } catch (InterruptedException e) {
                System.out.println("Error en el sleep hilo");
            }
        }

        leaveMulticast();
        System.out.println("Final de la Carrera y Programa");
    }
}
