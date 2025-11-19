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
            DatagramPacket paqueteEnvio = new DatagramPacket(mensaje, mensaje.length, grupo, ms.getLocalPort());
            ms.send(paqueteEnvio);
        } catch (IOException e) {
            System.out.println("Error al enviar paquete");
        }
    }

    /************************************* DatagramPacket -> recibir ***********************************/
    public Mensaje recibirPaqueteUDP(MulticastSocket ms) throws IOException, ClassNotFoundException {
        byte[] recibido = new byte[4096];
        DatagramPacket recibo = new DatagramPacket(recibido, recibido.length);
        ms.receive(recibo);

        ByteArrayInputStream in = new ByteArrayInputStream(recibido); // 0, getLength() [probar]
        ObjectInputStream ois = new ObjectInputStream(in);
        System.out.println("Antes del cash");
        //Rompe aquí porque creo que revienta

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
            throw new RuntimeException(e);
        }
    }

    public static void closeStream(){
        try {
            out.close();
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
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
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return msg;
    }

    /************************************* Movimiento Camellos ***********************************/
    public int max =3; //movimiento maximo
    public int min =1; //movimiento mínimo
    public int movimientoRandom(){
        return (int) (Math.random() * (max - min + 1)) + min;
    }

}
