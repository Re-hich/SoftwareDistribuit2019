package client;

import java.io.IOException;
import java.util.Scanner;


public class MenuClient {

    private Scanner sc = new Scanner(System.in);
    private Controller ctr = new Controller();

    static private enum OpcionsMenu {OPCIO1, OPCIO2};
    static private String [] descOpcionsMenu = {"Play", "Sortir "};


    public MenuClient(){

    }

    public void iniMenu(String ms, int port) throws IOException{
        Scanner sc = new Scanner(System.in);
        int IDU;

        System.out.println("Introduce tu numero de Usuario: ");
        IDU = sc.nextInt();

        MenuClient m = new MenuClient();
        m.showMenu(sc,IDU,ms,port);
    }


    private void showMenu(Scanner sc,int IDU, String ms, int port) throws IOException{
       this.ctr.iniClient(IDU, ms, port);


        System.out.println("\nComanda a enviar: STRT/EXIT");
        String c = sc.next();


        switch(c){
            case "STRT":
                System.out.println("\nIntrodueix numero de usuari");
                String id = sc.next();
                int a = this.ctr.getClient().getComUtils().string2int(id);
                this.ctr.getClient().getComUtils().writeSTRT(a);


                menuPlay(sc);
                break;

            case "EXIT":
                this.ctr.getClient().getComUtils().writeEXIT();
                break;
                
            case "CASH":
                System.out.println("\nQuantes fitxes vols canviar?");
                id = sc.next();
                a = this.ctr.getClient().getComUtils().string2int(id);
                //this.ctr.getClient().getComUtils().writeCASH(id);
                break;
                
            case "HITT":
                //this.ctr.getClient().getComUtils().writeHITT();
                break;
            
            case "BETT":
                //this.ctr.getClient().getComUtils().writeBETT();
                break;
            case "SRND":
                //this.ctr.getClient().getComUtils().writeSRND();
                break;
            case "RPLY":
                //this.ctr.getClient().getComUtils().writeRPLY();
                break;
            
        }



    }


    private void menuPlay(Scanner sc) throws IOException {
        String str = this.ctr.getClient().getComUtils().readCommand();
        String sp = this.ctr.getClient().getComUtils().read_space();
        int bet = this.ctr.getClient().getComUtils().read_int32();

        System.out.println("Em llegit la comanda: "+str);
        System.out.println("Bet: "+bet);


        System.out.println("\nIntrodueix aposta maxima");
        String id = sc.next();
        int a = this.ctr.getClient().getComUtils().string2int(id);
        this.ctr.getClient().getComUtils().writeCASH(a);

    }


}
