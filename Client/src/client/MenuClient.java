package client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class MenuClient {

    private Scanner sc = new Scanner(System.in);
    private Controller ctr = new Controller();
    public ArrayList clientHand;
    public int clientMoney = 10000;

    static private enum OpcionsMenu {OPCIO1, OPCIO2};
    static private String [] descOpcionsMenu = {"Play", "Sortir "};


    public MenuClient(){

    }

    public void iniMenu(String ms, int port) throws IOException{
        Scanner sc = new Scanner(System.in);

        MenuClient m = new MenuClient();
        m.showMenu(sc,ms,port);
    }


    private void showMenu(Scanner sc, String ms, int port) throws IOException{
       this.ctr.iniClient(ms, port);


        System.out.println("\nComanda a enviar: STRT/EXIT");
        String c = sc.next();


        switch(c){
            // Client starts with STRT  command and his ID
            case "STRT":
                System.out.println("\nIntrodueix numero de usuari");
                String id = sc.next();
                int a = this.ctr.getClient().getComUtils().string2int(id);
                this.ctr.getClient().getComUtils().writeSTRT(a);


                playMenu(sc);
                break;

            case "EXIT":
                this.ctr.getClient().getComUtils().writeEXIT();
                break;
            
        }



    }


    private void playMenu(Scanner sc) throws IOException {
        //String str = this.ctr.getClient().getComUtils().readCommand();
        //String sp = this.ctr.getClient().getComUtils().read_space();
        //int bet = this.ctr.getClient().getComUtils().read_int32();

        // Read INIT
        String bet = this.ctr.getClient().getComUtils().readINIT();
        System.out.println("Initial bet: "+bet);

        if (this.ctr.getClient().getComUtils().string2int(bet) > clientMoney) {
            return;
        } else {
            clientMoney = clientMoney - this.ctr.getClient().getComUtils().string2int(bet);
            System.out.println("Betting...");
        }

        // Write CASH
        System.out.println("\nIntrodueix aposta maxima");
        String maxBet = sc.next();
        while(this.ctr.getClient().getComUtils().string2int(maxBet) > clientMoney){
            System.out.println("\nAposta maxima massa alta, prova un numero mes petit");
            maxBet = sc.next();
        }
        this.ctr.getClient().getComUtils().writeCASH(this.ctr.getClient().getComUtils().string2int(maxBet));

        // Read IDCK
        System.out.println("\nLa teva ma es: ");
        ArrayList idck = this.ctr.getClient().getComUtils().readIDCK();
        clientHand = idck;
        showHand(idck);
        System.out.print("   Suma actual es: ");
        System.out.println(getSum(idck));

        // Read SHOW
        System.out.println("\nla banca mostra: ");
        String temp = this.ctr.getClient().getComUtils().readCommand();
        ArrayList show = this.ctr.getClient().getComUtils().readSHOW();
        showHand(show);

        baseMenu(sc);
    }


    private void baseMenu(Scanner sc) throws IOException {
        System.out.println("\nComanda a enviar: HITT / BETT / SHOW / SRND");
        String c = sc.next();


        switch(c) {
            case "HITT":
                this.ctr.getClient().getComUtils().writeHITT();
                ArrayList card = this.ctr.getClient().getComUtils().readCARD();
                clientHand.add(card.get(0));
                clientHand.add(card.get(1));
                System.out.println("\nLa teva ma es: ");
                showHand(clientHand);
                int sum = getSum(clientHand);
                System.out.print("   Suma actual es: "+sum);

                if (sum > 21) {
                    // At this point client lost
                    endMenu(sc);
                } else {
                    baseMenu(sc);
                }

                break;

            case "BETT":
                this.ctr.getClient().getComUtils().writeBETT();
                betMenu(sc);
                break;

            case "SHOW":
                System.out.println("Client final hand: ");
                showHand(clientHand);
                System.out.println("Client final number: ");
                int sum2 = getSum(clientHand);
                System.out.println("   Suma actual es: "+sum2);
                this.ctr.getClient().getComUtils().writeSHOW(clientHand.size(), clientHand);
                showMenu(sc);
                break;

            case "SRND":
                this.ctr.getClient().getComUtils().writeSRND();
                endMenu(sc);
                break;
        }
    }

    private void endMenu(Scanner sc) throws IOException {
        System.out.println("\n\nTEST");
        ArrayList wins = this.ctr.getClient().getComUtils().readWINS();
        System.out.println("\n\nTEST");
        int winner = this.ctr.getClient().getComUtils().string2int((String) wins.get(0));

        if ( winner == 0) {
            System.out.println("\n\nCLIENT WINS");
            System.out.println("You win: "+wins.get(1));

        } else  if ( winner == 1) {
            System.out.println("\n\nSERVER WINS");
            System.out.println("You lose: "+wins.get(1));

        } else  if ( winner == 2) {
            System.out.println("\n\nTIE");
            System.out.println("You can have your bet");

        }


    }

    private void betMenu(Scanner sc) throws IOException {
        System.out.println("\nComanda a enviar: HITT / SHOW ");
        String c = sc.next();


        switch(c) {
            case "HITT":
                this.ctr.getClient().getComUtils().writeHITT();
                ArrayList card = this.ctr.getClient().getComUtils().readCARD();
                clientHand.add(card.get(0));
                clientHand.add(card.get(1));
                System.out.print("\nLa carta rebuda es: ");
                showHand(card);
                int sum = getSum(clientHand);
                System.out.println(sum);

                if (sum > 21) {
                    // At this point client lost
                    endMenu(sc);
                } else {
                    betMenu(sc);
                }

                break;

            case "SHOW":
                System.out.println("Client hand: ");
                showHand(clientHand);
                this.ctr.getClient().getComUtils().writeSHOW(clientHand.size(), clientHand);
                endMenu(sc);
                break;

        }
    }

    private void showMenu(Scanner sc) throws IOException {
        System.out.println("\nla banca mostra: ");
        String temp = this.ctr.getClient().getComUtils().readCommand();
        System.out.println("\nla banca mostra: ");
        ArrayList show = this.ctr.getClient().getComUtils().readSHOW();
        showHand(show);
        System.out.println("Server final number: ");
        int sum2 = getSum(show);
        System.out.println("   Suma actual es: "+sum2);
        endMenu(sc);
    }


    private void showHand(ArrayList hand) {
        for (int i = 0; i < hand.size(); i = i +2) {
            System.out.print("   "+hand.get(i));
            System.out.println(hand.get(i+1));
        }
    }

    private int getSum(ArrayList hand) {
        String card;
        int sum = 0;
        int a = 0;

        for (int i = 0; i < hand.size(); i = i +2) {
            card = (String) hand.get(i);
            switch(card) {
                case "A":
                    a++;
                    sum = sum + 11;
                    break;

                case "K":
                    sum = sum + 10;
                    break;

                case "Q":
                    sum = sum + 10;
                    break;

                case "J":
                    sum = sum + 10;
                    break;

                case "X":
                    sum = sum + 10;
                    break;

                case "9":
                    sum = sum + 9;
                    break;

                case "8":
                    sum = sum + 8;
                    break;

                case "7":
                    sum = sum + 7;
                    break;

                case "6":
                    sum = sum + 6;
                    break;

                case "5":
                    sum = sum + 5;
                    break;

                case "4":
                    sum = sum + 4;
                    break;

                case "3":
                    sum = sum + 3;
                    break;

                case "2":
                    sum = sum + 2;
                    break;

            }
        }

        if (a == 1 && sum > 21) {
            sum = sum - 10;
            if (a == 2 && sum > 21) {
                sum = sum - 10;
                if (a == 3 && sum > 21) {
                    sum = sum - 10;
                    if (a == 4 && sum > 21) {
                        sum = sum - 10;
                    }
                }
            }
        }
        return sum;
    }


}
