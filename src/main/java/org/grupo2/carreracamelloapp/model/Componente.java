package org.grupo2.carreracamelloapp.model;

import org.grupo2.carreracamelloapp.model.mensajes.*;
import java.io.*;
import java.net.*;

public class Componente {

    /************************************* DatagramPacket -> envio ***************************************/

    public void envioPaqueteUDP(Mensaje objeto, MulticastSocket ms, InetAddress grupo, int puerto){
        try {
            // ✅ Asegurar TTL alto para cross-machine
            if(ms.getTimeToLive() < 255) {
                ms.setTimeToLive(255);
            }

            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bs);
            out.writeObject(objeto);
            out.close();
            byte[] mensaje = bs.toByteArray();

            DatagramPacket paqueteEnvio = new DatagramPacket(mensaje, mensaje.length, grupo, puerto);
            ms.send(paqueteEnvio);

        } catch (IOException e) {
            System.out.println("[Error] Envio paquete no realizado");
            e.printStackTrace();
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
        return msg;
    }

    /************************************* Mensajería TCP ***********************************/

    public static ObjectInputStream in;
    public static ObjectOutputStream out;

    public static void initStream(Socket cliente){
        try {
            // 🔥🔥🔥 CRÍTICO: OutputStream PRIMERO + FLUSH
            out = new ObjectOutputStream(cliente.getOutputStream());
            out.flush(); // ✅✅✅ ESTO PREVIENE EL DEADLOCK
            in = new ObjectInputStream(cliente.getInputStream());
        } catch (IOException e) {
            System.out.println("[Error] No se han podido crear los Streams");
            e.printStackTrace();
        }
    }

    public static void closeStream(){
        try {
            if(out != null) out.close();
            if(in != null) in.close();
        } catch (IOException e) {
            System.out.println("[Error] No se han podido cerrar los Streams");
        }
    }

    /************************************* TCP -> enviar ***********************************/

    public static void enviarPaqueteTCP(Mensaje mensaje) throws IOException {
        out.flush();
        out.writeObject(mensaje);
        out.flush();
    }

    /************************************* TCP -> recibir ***********************************/

    public static Mensaje recibirPaqueteTCP(){
        Mensaje msg = null;
        try {
            msg = (Mensaje) in.readObject();
            return msg;
        } catch (IOException e) {
            System.out.println("[Error] No ha sido posible recibir paquete");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("[Error] El cast no ha podido realizarse");
            e.printStackTrace();
        }
        return msg;
    }

    /************************************* Movimiento Camellos ***********************************/

    public int max = 10;
    public int min = 5;

    public int movimientoRandom(){
        return (int) (Math.random() * (max - min + 1)) + min;
    }
}
