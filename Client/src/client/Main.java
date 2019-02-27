package client;

import java.io.IOException;


public class Main{

    public static void main(String[] args) throws IOException{
        try{
            int port = 1234;
            MenuClient menu = new MenuClient();
            menu.iniMenu("localhost",port);
        }

        catch (IOException e){
            System.out.println("Els errors han de ser tractats correctament en el vostre programa.");
        }


    }
}