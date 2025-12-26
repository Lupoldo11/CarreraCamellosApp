package org.grupo2.carreracamelloapp.model;

import org.grupo2.carreracamelloapp.model.mensajes.*;

import java.io.*;
import java.net.*;

public class Componente {

    /************************************* DatagramPacket -> envio ***************************************/
    public void envioPaqueteUDP(Mensaje objeto, MulticastSocket ms, InetAddress grupo){
        try {
            //Generar byte del objeto
            ByteArrayOutputStream bs= new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream (bs);
            out.writeObject(objeto); //escribir objeto Persona en el stream
            out.close(); //cerrar stream
            byte[] mensaje= bs.toByteArray();

            //Enviar objeto
            int puerto = 54321; // o mejor, pásalo como parámetro en vez de depender de ms
            DatagramPacket paqueteEnvio = new DatagramPacket(mensaje, mensaje.length, grupo, puerto);
            ms.send(paqueteEnvio);
        } catch (IOException e) {
            System.out.println("[Error] Envio paquete no realizado");
        }
    }

    /************************************* DatagramPacket -> recibir ***********************************/
    public Mensaje recibirPaqueteUDP(MulticastSocket ms) throws IOException, ClassNotFoundException {
        byte[] recibido = new byte[4096];
        DatagramPacket recibo = new DatagramPacket(recibido, recibido.length);
        ms.receive(recibo);

        ByteArrayInputStream in = new ByteArrayInputStream(recibido);
        ObjectInputStream ois = new ObjectInputStream(in);

        Mensaje msg=null;
        while (msg == null){
            msg = (Mensaje) ois.readObject();
        }
        ois.close();
        in.close();

        return msg; //envia objeto que luego hay que cashetear
    }

    /************************************* Mensajería TCP ***********************************/
    public static ObjectInputStream in;
    public static ObjectOutputStream out;

    public static void initStream(Socket cliente){
        try {
            out = new ObjectOutputStream (cliente.getOutputStream());
            in = new ObjectInputStream(cliente.getInputStream());
        } catch (IOException e) {
            System.out.println("[Error] No se han podido crear los Streams");
        }
    }

    public static void closeStream(){
        try {
            out.close();
            in.close();
        } catch (IOException e) {
            System.out.println("[Error] No se han podido cerrar los Streams");
        }
    }

    /************************************* TCP -> enviar ***********************************/
    public static void enviarPaqueteTCP(Mensaje mensaje) throws IOException {
        out.flush();
        out.writeObject(mensaje); //envia el objeto al cliente ->
        out.flush();
        /*Metodo Static: Envia el objeto por la conexión TCP
         * Argumentos: (Obj) la IP Multicast , (Socket) la conexión del cliente */
    }

    /************************************* TCP -> recibir ***********************************/
    public static Mensaje recibirPaqueteTCP(){
        //Aqui recibir el objeto con la IP Multicast
        Mensaje msg = null;
        try {

            msg = (Mensaje) in.readObject();
            return msg;

        } catch (IOException e) {
            System.out.println("[Error] No ha sido posible recibir paquete");
        } catch (ClassNotFoundException e) {
            System.out.println("[Error] El cash no ha podido realizarse");
        }

        return msg;
    }

    /************************************* Movimiento Camellos ***********************************/
    public int max = 10; //movimiento maximo
    public int min = 5; //movimiento mínimo
    public int movimientoRandom(){
        return (int) (Math.random() * (max - min + 1)) + min;
    }

}
