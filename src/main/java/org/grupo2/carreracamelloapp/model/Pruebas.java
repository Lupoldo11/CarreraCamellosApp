package org.grupo2.carreracamelloapp.model;

import java.io.*;
import java.net.*;

public class Pruebas implements Serializable{
    String mundo ="hola";


    public String getMundo(){
        return mundo;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        FileOutputStream bs= new FileOutputStream(new File("hola.dat"));
        ObjectOutputStream out = new ObjectOutputStream (bs);
        out.writeObject(new Pruebas()); //escribir objeto Persona en el stream
        out.close(); //cerrar stream


        FileInputStream in = new FileInputStream("hola.dat");
        ObjectInputStream ois = new ObjectInputStream(in);
        Object sv = (Object) ois.readObject();

        Pruebas pruebas = (Pruebas) sv;
        System.out.println(pruebas.getMundo());
    }
}
