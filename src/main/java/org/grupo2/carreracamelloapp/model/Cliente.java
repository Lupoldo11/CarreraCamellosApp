package org.grupo2.carreracamelloapp.model;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.grupo2.carreracamelloapp.StartApplication;
import org.grupo2.carreracamelloapp.controller.CarreraCamellosController;
import org.grupo2.carreracamelloapp.model.mensajes.*;

import java.io.*;
import java.net.*;
import java.util.Collection;

public class Cliente extends Componente implements Runnable,Serializable{
    /******************************* Atributos Static *********************************************/
    //Conexion TCP
    public static int puertoTCP;
    public static String conexionTCP; //cambiar por IP del ordenador en la red WEDU o localhost
    //192.168.113.100 -> pc central

    /******************************* Atributos Clase *********************************************/
    private MulticastSocket ms;
    private InetAddress grupo;
    private String nombreCliente; //nombreCliente == camello1
    private AsignacionGrupo datosGrupo;
    private CarreraCamellosController controller;
    private StartApplication application;
    private NetworkInterface networkInterface;

    private Cliente camello2;
    private Cliente camello3;

    private int distancia;

    /**************************************** Constructor ***************************************/
    public Cliente (String nombre, int distancia){
        this.nombreCliente= nombre;
        this.distancia = distancia;
    }

    public Cliente(String nombreCliente, AsignacionGrupo datosGrupo) {
        this.nombreCliente = nombreCliente;
        this.datosGrupo = datosGrupo;
    }

    /**************************************** Métodos *******************************************/
    public Cliente getCamello2() { return camello2; }

    public int getDistancia() { return distancia; }

    public void movimiento(int movimiento){ this.distancia += movimiento;}

    public CarreraCamellosController getController(){
        return controller;
    }

    public void setController(CarreraCamellosController controller){
        this.controller=controller;
    }

    public void setApplication(StartApplication application){
        this.application=application;
    }

    public StartApplication getApplication(){
        return application;
    }

    public Cliente getCamello3() {
        return camello3;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public InetAddress getInetAddress() {
        return grupo;
    }

    public MulticastSocket getMS() {
        return ms;
    }

    public void asignarCamellos(EventInicio datosInicio) {
        Cliente[] camellos = datosInicio.getParticipantes();
        CarreraCamellosController.setListCamellos(camellos);
    }

    public void joinMulticast() {
        try {
            //Inicio de conexión multicast UDP
            ms = new MulticastSocket(datosGrupo.getPuertoUDP()); //averiguar como sacarlo
            grupo = InetAddress.getByName(datosGrupo.getIpV4Multicast()); //nombreIPMulticast
            networkInterface = ProtocoloInternetv4.getIPv4Network();

            SocketAddress sa = new InetSocketAddress(grupo, datosGrupo.getPuertoUDP()); //Prueba conectar
            ms.setReuseAddress(true); //esto es una prueba

            ms.joinGroup(sa, networkInterface); //Se uniría
            System.out.println("[Cliente] Uniendose de la conexión:" + datosGrupo.getIpV4Multicast() + " y puerto:" + datosGrupo.getPuertoUDP());

        } catch (IOException e) {
            System.out.println("[Error] No ha sido posible conectar al multicast");
        }
    }

    public void leaveMulticast() {
        try {
            SocketAddress sa = new InetSocketAddress(grupo, datosGrupo.getPuertoUDP());
            ms.leaveGroup(sa, networkInterface); //Salirse del Multicast
            System.out.println("[Cliente] Desconectando de la conexión:" + datosGrupo.getIpV4Multicast() + " y puerto:" + datosGrupo.getPuertoUDP());
        } catch (IOException e) {
            System.out.println("[Error] No ha sido posible desconectase del multicast");
        }
    }

    public static CarreraCamellosController generateController() { //Obtener una instancia del controlador
        FXMLLoader loader = new FXMLLoader(CarreraCamellosController.class.getResource("/org/grupo2/carreracamelloapp/pantallas/carreraCamellosUI.fxml"));
        try {
            Parent root = loader.load();
        } catch (IOException e) {
            System.out.println("[Error] No ha cargado el fichero .fxml");
        }
        return loader.getController();
    }

    /**************************************** Ejecutables ***************************************/
    public static void ejecutable(String ipHost, String puertoHost, String nombreCliente) {
        //Configurar Cliente
        puertoTCP = Integer.parseInt(puertoHost);
        conexionTCP = ipHost;
        System.out.println("[Cliente] Configuración terminada.");

        try {
            //Conexión TCP (Entera)
            AsignacionGrupo datosGrupo = conexionTCP(nombreCliente);

            //Generamos la instancia de cliente
            System.out.println("[Cliente] Generando usuario...");
            Cliente camello = new Cliente(nombreCliente, datosGrupo);

            StartApplication.getCliente(camello);

            Application.launch(StartApplication.class, nombreCliente);

        } catch (IOException e) {
            System.out.println("[Error] Servidor Cerrado (Esto es cliente)");
        }
    }

    /**************************************** Static ***************************************/
    public static AsignacionGrupo conexionTCP(String nombreCliente) throws IOException {
        System.out.println("[TCP] Conectandose al servidor: " + conexionTCP);
        Socket cliente = new Socket(conexionTCP, puertoTCP);

        initStream(cliente); //Abrir Streams

        //Recibe la IP Multicast
        AsignacionGrupo datosGrupo = (AsignacionGrupo) recibirPaqueteTCP();
        System.out.println("[TCP] IP Multicast recibida: " + datosGrupo.getIpV4Multicast());
        System.out.println("[TCP] Puerto Multicast: " + datosGrupo.getPuertoUDP());

        //Mandar OK
        ListoJoinMulticast ready = new ListoJoinMulticast();
        ready.setData(nombreCliente);
        enviarPaqueteTCP(ready);

        closeStream(); //Cerrar Streams
        cliente.close(); //Cierra la TCP
        System.out.println("[TCP] Cerrando conexión al servidor:" + conexionTCP);

        return datosGrupo;
    }

    public void cicloCarrera() {
        boolean salida = true;
        Mensaje mensaje;
        while (salida) {
            try {
                mensaje = recibirPaqueteUDP(ms);
                if (mensaje instanceof EventInicio) {
                    if (this.controller == null){
                        System.out.println("[Controller] Controlador no cargado, está en null");
                    } else {
                        this.controller.butonON();
                    }
                    System.out.println("[Carrera] La carrera da comienzo YA!!");
                    asignarCamellos(EventInicio.parseEventInicio(mensaje));
                } else if (mensaje instanceof EventPosicion) {
                    System.out.println("[Carrera] Movimiento!!");
                    this.controller.escuchaMovimientoMulticast(EventPosicion.parseEventPosicion(mensaje));
                } else if (mensaje instanceof EventFinalizacion) {
                    System.out.println("[Carrera] Fin!!");
                    this.controller.butonOFF();
                    salida = victoria(EventFinalizacion.parseEventFinalizacion(mensaje));
                    //aqui hacer que el controller saque un podio
                } else {
                    System.out.println("[Warning] Mensaje no identificado");
                }
            } catch (IOException e) {
                System.out.println("[Error] Error de lectura");
            } catch (ClassNotFoundException e) {
                System.out.println("[Error] Error con el cash");
            }
        }

        leaveMulticast();
        System.out.println("[Cliente] Final de la Carrera y Programa");
    }

    public static boolean victoria(EventFinalizacion eventFinalizacion) {
        Cliente[] podio = eventFinalizacion.getPodio();
        for (int i = 0; i < podio.length; i++) {
            System.out.println("[Carrera] Posiciones " + (i + 1) + ": " + podio[i].getNombreCliente());
        }
        //Lanzar la UI Podio
        return false;
    }

    @Override
    public void run() {
        //lanza el bucle
        cicloCarrera();
    }
}
