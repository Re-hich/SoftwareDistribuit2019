package servidor;


import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class Servidor {

    private static ComUtils comUtils;
    private static EstatJoc estatJoc;


    public static void main(String[] args) {
        ServerSocket serverSocket=null;
        Scanner sc = new Scanner(System.in);


        Socket socket;

        int portServidor = 5000;
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


        //Object card = estatJoc.getRandCard();
        //System.out.println(((EstatJoc.Card) card).rank);
        //System.out.println(((EstatJoc.Card) card).suit);



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
                str = comUtils.readCommand();
                System.out.println("Em llegit la comanda: "+str);

                switch(str) {
                    case "STRT":
                        System.out.println("Game started");
                        String sp = comUtils.read_space();
                        int id = comUtils.read_int32();
                        System.out.println("ID: "+id);

                        estatJoc = new EstatJoc(id);

                        menuSTRT(sc);

                        break;

                    case "EXIT":
                        System.out.println("Exiting...");
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


    private static void menuSTRT(Scanner sc) throws IOException {
        int serverInitBet = 100;

        // Write INIT
        comUtils.writeINIT(serverInitBet);
        estatJoc.setInitBet(serverInitBet);

        // Read CASH
        String max = comUtils.readCASH();
        estatJoc.setMaxBet(comUtils.string2int(max));
        System.out.println("Max bet: "+max);

        // Write IDCK
        Object card1 = estatJoc.getRandCard();
        Object card2 = estatJoc.getRandCard();
        estatJoc.addClientCard((EstatJoc.Card) card1);
        estatJoc.addClientCard((EstatJoc.Card) card2);

        comUtils.writeIDCK(((EstatJoc.Card) card1).rank, ((EstatJoc.Card) card1).suit, ((EstatJoc.Card) card2).rank, ((EstatJoc.Card) card2).suit);

        // Write SHOW
        Object card3 = estatJoc.getRandCard();
        Object card4 = estatJoc.getRandCard();
        estatJoc.addServerCard((EstatJoc.Card) card3);
        estatJoc.addServerCard((EstatJoc.Card) card4);

        ArrayList cardListTest =  new ArrayList();
        cardListTest.add(((EstatJoc.Card) card3).rank);
        cardListTest.add(((EstatJoc.Card) card3).suit);

        comUtils.writeSHOW(cardListTest.size(), cardListTest);

        menuPlay(sc);
    }



    private static void menuPlay(Scanner sc) throws IOException {
        /* Esperem una comanda */
        String str = comUtils.readCommand();
        System.out.println("Em llegit la comanda: "+str);

        switch(str) {
            case "HITT":
                Object card = estatJoc.getRandCard();
                estatJoc.addClientCard((EstatJoc.Card) card);
                comUtils.writeCARD(((EstatJoc.Card) card).rank, ((EstatJoc.Card) card).suit);
                if (estatJoc.getWinner() == '1'){
                    endMenu(sc);
                } else {
                    menuPlay(sc);
                }
                break;

            case "BETT":
                estatJoc.doubleBet();
                menuBet(sc);
                break;

            case "SHOW":
                ArrayList clientHand = comUtils.readSHOW();
                endMenu(sc);
                break;

            case "SRND":
                estatJoc.setWinner('1');
                endMenu(sc);
                break;
        }
    }

    private static void menuBet(Scanner sc) throws IOException {
        /* Esperem una comanda */
        String str = comUtils.readCommand();
        System.out.println("Em llegit la comanda: "+str);

        switch(str) {
            case "HITT":
                Object card = estatJoc.getRandCard();
                estatJoc.addClientCard((EstatJoc.Card) card);
                comUtils.writeCARD(((EstatJoc.Card) card).rank, ((EstatJoc.Card) card).suit);
                if (estatJoc.getWinner() == '1'){
                    endMenu(sc);
                } else {
                    menuBet(sc);
                }
                break;

            case "SHOW":
                comUtils.readSHOW();
                endMenu(sc);
                break;

        }
    }


    private static void endMenu(Scanner sc) throws IOException {
        EstatJoc.Card card;
        char winner = estatJoc.getWinner();

        if (winner == '0' || winner == '1') {
            comUtils.writeWINS(comUtils.char2String(winner), estatJoc.getBet());
        } else {
            // Server plays... and then a winner is decided
            while( estatJoc.getServerNum() < 17) {
                card = estatJoc.getRandCard();
                estatJoc.addServerCard(card);
            }

            ArrayList cardListSimple =  new ArrayList();
            ArrayList serverHand =  estatJoc.getServerHand();

            for (int i = 0; i < serverHand.size(); i++) {
                card = (EstatJoc.Card) serverHand.get(i);
                cardListSimple.add(card.rank);
                cardListSimple.add(card.suit);
            }

            System.out.println(cardListSimple);
            System.out.println(estatJoc.getServerNum());

            if (estatJoc.getServerNum() > 21) {
                // Server lost
                System.out.println("Client wins");
                comUtils.writeWINS("0", estatJoc.getBet());
            }

            else if (estatJoc.getServerNum() > estatJoc.getClientNum()) {
                // Server wins
                System.out.println("Server wins");
                comUtils.writeWINS("1", estatJoc.getBet());
            }
            else if (estatJoc.getServerNum() == estatJoc.getClientNum()) {
                System.out.println("It's a tie");

                comUtils.writeWINS("2", estatJoc.getBet());
            } else {
                System.out.println("Client wins");
                comUtils.writeWINS("0", estatJoc.getBet());

            }
        }


    }


}
