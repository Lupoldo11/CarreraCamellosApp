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
    public static String conexionTCP;
    public static Cliente[] listCamellos; // ✅ STATIC para TODOS los clientes

    /******************************* Atributos Clase *********************************************/

    private MulticastSocket ms;
    private InetAddress grupo;
    private String nombreCliente;
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
        listCamellos = datosInicio.getParticipantes();
        System.out.println("[Cliente] ✅ Camellos asignados: " + listCamellos.length);

        // ✅ DEBUG: Imprimir nombres de TODOS los camellos
        for (int i = 0; i < listCamellos.length; i++) {
            System.out.println("  [" + i + "] " + listCamellos[i].getNombreCliente());
        }

        if (this.controller != null) {
            CarreraCamellosController.setListCamellos(listCamellos);
            System.out.println("[Cliente] Controller actualizado");
        } else {
            System.out.println("[Cliente] Controller null, pero listCamellos STATIC OK");
        }
    }


    // ✅ GETTER para que controller acceda a listCamellos STATIC
    public static Cliente[] getListCamellos() {
        return listCamellos;
    }

    public void joinMulticast() {
        try {
            ms = new MulticastSocket(datosGrupo.getPuertoUDP());
            ms.setReuseAddress(true); // ✅ CRÍTICO

            grupo = InetAddress.getByName(datosGrupo.getIpV4Multicast());
            networkInterface = ProtocoloInternetv4.getIPv4Network();

            SocketAddress sa = new InetSocketAddress(grupo, datosGrupo.getPuertoUDP());
            ms.joinGroup(sa, networkInterface);

            // ✅ TAMBIÉN sin interfaz específica
            ms.joinGroup(grupo);

            System.out.println("[Cliente] Uniendose de la conexión:" + datosGrupo.getIpV4Multicast() +
                    " y puerto:" + datosGrupo.getPuertoUDP());
            System.out.println("[Cliente] Interfaz: " + networkInterface.getDisplayName());

        } catch (IOException e) {
            System.out.println("[Error] No ha sido posible conectar al multicast");
            e.printStackTrace();
        }
    }


    public void leaveMulticast() {
        try {
            SocketAddress sa = new InetSocketAddress(grupo, datosGrupo.getPuertoUDP());
            ms.leaveGroup(sa, networkInterface);
            System.out.println("[Cliente] Desconectando de la conexión:" + datosGrupo.getIpV4Multicast() +
                    " y puerto:" + datosGrupo.getPuertoUDP());
        } catch (IOException e) {
            System.out.println("[Error] No ha sido posible desconectase del multicast");
        }
    }

    public static CarreraCamellosController generateController() {
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
        puertoTCP = Integer.parseInt(puertoHost);
        conexionTCP = ipHost;
        System.out.println("[Cliente] Configuración terminada.");
        try {
            AsignacionGrupo datosGrupo = conexionTCP(nombreCliente);
            System.out.println("[Cliente] Generando usuario...");
            Cliente camello = new Cliente(nombreCliente, datosGrupo);

            // ✅✅✅ UNIRSE AL MULTICAST ANTES DE LANZAR JAVAFX
            camello.joinMulticast();
            Thread hiloUDP = new Thread(camello);
            hiloUDP.start();
            System.out.println("[Cliente] ✅ Conectado al multicast y escuchando");

            // ✅ Pequeña espera para asegurar que está escuchando
            Thread.sleep(500);

            StartApplication.getCliente(camello);
            Application.launch(StartApplication.class, nombreCliente);
        } catch (IOException e) {
            System.out.println("[Error] Servidor Cerrado (Esto es cliente)");
        } catch (InterruptedException e) {
            System.out.println("[Error] Interrupción en espera");
        }
    }

    /**************************************** Static ***************************************/

    public static AsignacionGrupo conexionTCP(String nombreCliente) throws IOException {
        System.out.println("[TCP] Conectandose al servidor: " + conexionTCP);
        Socket cliente = new Socket(conexionTCP, puertoTCP);
        initStream(cliente);
        AsignacionGrupo datosGrupo = (AsignacionGrupo) recibirPaqueteTCP();
        System.out.println("[TCP] IP Multicast recibida: " + datosGrupo.getIpV4Multicast());
        System.out.println("[TCP] Puerto Multicast: " + datosGrupo.getPuertoUDP());
        ListoJoinMulticast ready = new ListoJoinMulticast();
        ready.setData(nombreCliente);
        enviarPaqueteTCP(ready);
        closeStream();
        cliente.close();
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
                    System.out.println("[Carrera] La carrera da comienzo YA!!");
                    // ✅ SIEMPRE asignar camellos PRIMERO (STATIC)
                    asignarCamellos(EventInicio.parseEventInicio(mensaje));
                    // ✅ UI opcional DESPUÉS
                    if (this.controller != null) {
                        this.controller.butonON();
                    }
                } else if (mensaje instanceof EventPosicion) {
                    EventPosicion ep = EventPosicion.parseEventPosicion(mensaje);
                    System.out.println("[Carrera] Movimiento de: '" + ep.getPropietario() + "' -> " + ep.getMovimiento());

                    // ✅ DEBUG: Mostrar lista de camellos conocidos
                    if (Cliente.listCamellos != null) {
                        System.out.println("[DEBUG] Camellos conocidos:");
                        for (int i = 0; i < listCamellos.length; i++) {
                            System.out.println("  [" + i + "] '" + listCamellos[i].getNombreCliente() + "'");
                        }
                    }

                    if (Cliente.listCamellos != null && this.controller != null) {
                        this.controller.escuchaMovimientoMulticast(ep);
                    } else {
                        System.out.println("[Warning] listCamellos no inicializado, ignorando movimiento de " +
                                ep.getPropietario());
                    }
                } else if (mensaje instanceof EventFinalizacion) {
                    System.out.println("[Carrera] Fin!!");
                    if (this.controller != null) {
                        this.controller.butonOFF();
                    }
                    salida = victoria(EventFinalizacion.parseEventFinalizacion(mensaje));
                } else {
                    System.out.println("[Warning] Mensaje no identificado");
                }
            } catch (IOException e) {
                System.out.println("[Error] Error de lectura");
            } catch (ClassNotFoundException e) {
                System.out.println("[Error] Error con el cast");
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
        return false;
    }

    @Override
    public void run() {
        System.out.println("👂 CLIENTE ESCUCHA: " + grupo.getHostAddress() + ":" + datosGrupo.getPuertoUDP());
        cicloCarrera();
    }
}
