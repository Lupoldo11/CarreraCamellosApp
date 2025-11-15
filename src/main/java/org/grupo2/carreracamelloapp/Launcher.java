package org.grupo2.carreracamelloapp;

import javafx.application.Application;
import org.grupo2.carreracamelloapp.model.Cliente;
import org.grupo2.carreracamelloapp.model.Servidor;

import java.net.Socket;
import java.util.Scanner;

public class Launcher {
    public static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Opción: 1)Servidor 2)Cliente");
        int entrada;
        if((entrada = Integer.parseInt(sc.nextLine())) == 1){
            Servidor.main(null);
        } else {
            System.out.println("Dime tu nombre de Usuario: ");
            String nombre = sc.nextLine();
            Cliente.main(nombre);
        }

        //Application.launch(StartApplication.class, args); //Metodo que lanza el JavaFX
    }
}
