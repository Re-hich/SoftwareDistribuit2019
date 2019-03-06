package client;

import java.io.*;
import java.net.*;


public class Client {
    int UID;
    String maquinaServidora;
    int port;
    int stack;
    String trun;
    ComUtilsClientv2 comUtils;

    public Client(int UID,String ms, int port) {
        this.UID = UID;
        this.maquinaServidora = ms;
        this.port = port;
    }

    public void conectarServidor(){

        InetAddress ms;
        Socket socket = null;

        try{
            ms = InetAddress.getByName(maquinaServidora);
            socket = new Socket(ms,port);
            /* Obrim un flux d'entrada/sortida amb el servidor */
            comUtils = new ComUtilsClientv2(socket);
            System.out.println("Se ha establecido conexion");
        }
        catch(IOException e){
            System.out.println("No se ha establecido conexion");
            System.exit(1);
        }

    }


    public ComUtilsClientv2 getComUtils() {
        return comUtils;
    }

    public String getTrun() {
        return trun;
    }

    public void setTrun(String trun) {
        this.trun = trun;
    }
} // fi de la classe