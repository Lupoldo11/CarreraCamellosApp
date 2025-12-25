package org.grupo2.carreracamelloapp.model.mensajes;

public class ListNetworkInterfaces {
    //un main
        //enumeratico de network interfaces = .getNetwrokInterfaces()

        //bucle MIENTRAS enumeration.hasmoreelement()
            //guardar en variable la interfaz.hasElement()
            //name = .getName()
            //index = .getIndex()

            //muestra nombre, indice, vitual
            //funcion getMac(mandar la interfaz)
            //list de la interfaz.getInterfaceAddresses()


            //bucle for mientras .size de la list
                //guarda la interfaz adress por .get(i)
                //mostrar la direccion ip -> .getAddress()
                //mostrar la dirección broadcast -> getBroadcast
                //mostrar la CIDR -> .getNetwrokPrefixLength()
            //--------

    //funcion getMac( NetworkInterfces e)
        //variable de byte[] del argumento y pasa .getHardwareAddress()

        //condicion si es null
            //string builder de la mac
            //bucle for (.legth de byte[])
                // macAddress.append()
}
