package servidor;


import java.io.*;
import java.net.*;



public class Servidor {

    private static ComUtils comUtils;


    public static void main(String[] args) {
        ServerSocket serverSocket=null;


        Socket socket;

        int portServidor = 1234;
        int value;
        String str;
        //EstatJoc prova;

        if (args.length > 1)
        {
            System.out.println("Us: java Servidor [<numPort>]");
            System.exit(1);
        }

        if (args.length == 1)
            portServidor = Integer.parseInt(args[0]);

        try {
            /* Creem el servidor */
            serverSocket = new ServerSocket(portServidor);
            System.out.println("Servidor socket preparat en el port " + portServidor);

            while (true) {
                System.out.println("Esperant una connexió d'un client.");

                /* Esperem a que un client es connecti amb el servidor */
                socket = serverSocket.accept();
                System.out.println("Connexió acceptada d'un client.");

                /* Associem un flux d'entrada/sortida amb el client */
                comUtils = new ComUtils(socket);

                /* Esperem una comanda */
                str = comUtils.readCommand3();
                System.out.println("Em llegit la comanda: "+str);



                switch(str){
                    case "PLY":
                        /*
                        System.out.println("Anem a començar el joc");
                        prova = new EstatJoc(10,10);
                        prova.setPlayers();

                        char turn = prova.getTurn();
                        comUtils.writeChar(comUtils.char2String(turn));
                        if (turn == 'C'){
                            System.out.println("    El servidor es player 2 i agafa segon la carta");
                        }
                        if (turn == 'S'){
                            System.out.println("    El servidor es player 1 i comença");
                        }

                        prova.setCardsCS(turn);

                        char card = prova.getCardClient();
                        comUtils.writeChar(comUtils.char2String(card));

                        char cardServidor = prova.getCardServidor();
                        System.out.println("    La carta del servidor es: "+cardServidor);

                        */

                        break;

                    case "STP":

                        break;
                }




            } // fi del while infinit
        } // fi del try
        catch (IOException ex) {
            System.out.println("Els errors han de ser tractats correctament pel vostre programa");
        } // fi del catch
        finally
        {
            /* Tanquem la comunicacio amb el client */
            try {
                if(serverSocket != null) serverSocket.close();
            }
            catch (IOException ex) {
                System.out.println("Els errors han de ser tractats correctament pel vostre programa");
            } // fi del catch
        }

    } // fi del main


}