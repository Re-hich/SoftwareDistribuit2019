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


        System.out.println("\nComanda a enviar: PLY/STP");
        String c = sc.next();

        switch(c){
            case "PLY":
                //this.ctr.getClient().getComUtils().writePLY();
                //menuPlay(sc);
                break;

            case "STP":
                //this.ctr.getClient().getComUtils().writeSTP();
                break;
        }



    }


}
