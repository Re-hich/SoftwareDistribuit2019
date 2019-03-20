package client;//import com.oracle.jrockit.jfr.ContentType;
//import static com.oracle.jrockit.jfr.ContentType.None;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ComUtils {

    /* Mida d'una cadena de caracters */
    private final int STRSIZE = 40;
    /* Objectes per escriure i llegir dades */
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;


    public ComUtils(InputStream inputStream, OutputStream outputStream) throws IOException {
        dataInputStream = new DataInputStream(inputStream);
        dataOutputStream = new DataOutputStream(outputStream);
    }


    public ComUtils(Socket socket) throws IOException{
        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
    }


    public ComUtils(File file) throws IOException{
        dataInputStream = new DataInputStream(new FileInputStream(file));
        dataOutputStream = new DataOutputStream(new FileOutputStream(file));
    }



    public static void main(String[] args) {
        File file = new File("test.txt");
        try {
            file.createNewFile();
            ComUtils cmUtils = new ComUtils(file);
            cmUtils.writeTest();
            System.out.println(cmUtils.readTest());
        }
        catch(IOException e)
        {
            System.out.println("Error Found during Operation:" + e.getMessage());
            e.printStackTrace();
        }
    }



    public String readTest() throws IOException {
        String str = read_string();
        return str;
    }

    public void writeTest() throws IOException {
        String str = "H";
        write_string(str);
    }






    /* Llegir un enter de 32 bits */
    public int read_int32() throws IOException{
        byte bytes[] = read_bytes(4);

        return bytesToInt32(bytes,Endianness.BIG_ENNDIAN);
    }

    /* Escriure un enter de 32 bits */
    public void write_int32(int number) throws IOException{
        byte bytes[] = int32ToBytes(number, Endianness.BIG_ENNDIAN);

        dataOutputStream.write(bytes, 0, 4);
    }

    /* Llegir un string de mida STRSIZE */
    public String read_string() throws IOException{
        String result;
        byte[] bStr = new byte[STRSIZE];
        char[] cStr = new char[STRSIZE];

        bStr = read_bytes(STRSIZE);

        for(int i = 0; i < STRSIZE;i++)
            cStr[i]= (char) bStr[i];

        result = String.valueOf(cStr);

        return result.trim();
    }

    /* Escriure un string */
    public void write_string(String str) throws IOException{
        int numBytes, lenStr;
        byte bStr[] = new byte[STRSIZE];

        lenStr = str.length();

        if (lenStr > STRSIZE)
            numBytes = STRSIZE;
        else
            numBytes = lenStr;

        for(int i = 0; i < numBytes; i++)
            bStr[i] = (byte) str.charAt(i);

        for(int i = numBytes; i < STRSIZE; i++)
            bStr[i] = (byte) ' ';

        dataOutputStream.write(bStr, 0,STRSIZE);
    }

    /* Passar d'enters a bytes */
    private byte[] int32ToBytes(int number, Endianness endianness) {
        byte[] bytes = new byte[4];

        if(Endianness.BIG_ENNDIAN == endianness) {
            bytes[0] = (byte)((number >> 24) & 0xFF);
            bytes[1] = (byte)((number >> 16) & 0xFF);
            bytes[2] = (byte)((number >> 8) & 0xFF);
            bytes[3] = (byte)(number & 0xFF);
        }
        else {
            bytes[0] = (byte)(number & 0xFF);
            bytes[1] = (byte)((number >> 8) & 0xFF);
            bytes[2] = (byte)((number >> 16) & 0xFF);
            bytes[3] = (byte)((number >> 24) & 0xFF);
        }
        return bytes;
    }

    /* Passar de bytes a enters */
    private int bytesToInt32(byte bytes[], Endianness endianness) {
        int number;

        if(Endianness.BIG_ENNDIAN == endianness) {
            number=((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) |
                    ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
        }
        else {
            number=(bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8) |
                    ((bytes[2] & 0xFF) << 16) | ((bytes[3] & 0xFF) << 24);
        }
        return number;
    }

    //llegir bytes.
    private byte[] read_bytes(int numBytes) throws IOException {
        int len = 0;
        byte bStr[] = new byte[numBytes];
        int bytesread = 0;
        do {
            bytesread = dataInputStream.read(bStr, len, numBytes-len);
            if (bytesread == -1)
                throw new IOException("Broken Pipe");
            len += bytesread;
        } while (len < numBytes);
        return bStr;
    }

    /* Llegir un string  mida variable size = nombre de bytes especifica la longitud*/
    public  String read_string_variable(int size) throws IOException {
        byte bHeader[] = new byte[size];
        char cHeader[] = new char[size];
        int numBytes = 0;

        // Llegim els bytes que indiquen la mida de l'string
        bHeader = read_bytes(size);
        // La mida de l'string ve en format text, per tant creem un string i el parsejem
        for(int i=0;i<size;i++){
            cHeader[i]=(char)bHeader[i]; }
        numBytes=Integer.parseInt(new String(cHeader));

        // Llegim l'string
        byte bStr[]=new byte[numBytes];
        char cStr[]=new char[numBytes];
        bStr = read_bytes(numBytes);
        for(int i=0;i<numBytes;i++)
            cStr[i]=(char)bStr[i];
        return String.valueOf(cStr);
    }


    /* Escriure un string mida variable, size = nombre de bytes especifica la longitud  */
    /* String str = string a escriure.*/
    public  void write_string_variable(int size,String str) throws IOException{
        // Creem una seqüència amb la mida
        byte bHeader[]=new byte[size];
        String strHeader;
        int numBytes=0;

        // Creem la capçalera amb el nombre de bytes que codifiquen la mida
        numBytes=str.length();

        strHeader=String.valueOf(numBytes);
        int len;
        if ((len=strHeader.length()) < size)
            for (int i =len; i< size;i++){
                strHeader= "0"+strHeader;}
        for(int i=0;i<size;i++)
            bHeader[i]=(byte)strHeader.charAt(i);
        // Enviem la capçalera
        dataOutputStream.write(bHeader, 0, size);
        // Enviem l'string writeBytes de DataOutputStrem no envia el byte més alt dels chars.
        dataOutputStream.writeBytes(str);
    }

    public void write_char(String s) throws IOException{
        byte bStr[] = new byte[2];/*1 char = 16 bits!!!!!!!!!*/
        String str = s;

        bStr[0] = (byte) str.charAt(0);

        dataOutputStream.write(bStr, 0, 1);
    }

    public String read_char() throws IOException {
        byte bStr[];
        char cStr[] = new char[1];
        bStr = read_bytes(1);
        cStr[0] = (char) bStr[0];
        return String.valueOf(cStr);
    }


    public byte read_byte() throws IOException{
        byte bytes[] = read_bytes(4);

        return (bytes[0]);
    }

    public void write_byte(byte a) throws IOException{
        byte bytes[] = new byte[STRSIZE];

        bytes[0] = a;

        dataOutputStream.write(bytes, 0, 4);
    }

    //------------- FI PRACTICA 0 ------------


    /*

    La llogica del joc NO esta a client.ComUtils

    client.ComUtils functions:
        public void writeSpace():
        public char readSpace(char ch);

        public void writeCommand(String command);
        public String readCommand();

        public void writeCommandOneParametre();
        public void readCommandOneParametre(String command, String parametre);

        public void writeCommandTwoParametre();
        public void readCommandTwoParametre(String command, String parametre1, String parametre2);

        public int string2int(String str);
        public String int2String(int int);



    */


    public void write_space() throws IOException {
        byte bStr[] = new byte[1];
        String str = " ";

        bStr[0] = (byte) str.charAt(0);

        dataOutputStream.write(bStr, 0, 1);
    }


    public String read_space() throws IOException {
        byte bStr[];
        char cStr[] = new char[1];
        bStr = read_bytes(1);
        cStr[0] = (char) bStr[0];
        return String.valueOf(cStr);
    }




    // Escribim una comanda de 3 lletres
    public void writeCommand3(String str) throws IOException{
        int numBytes, lenStr;
        byte bStr[] = new byte[4];

        lenStr = str.length();

        if (lenStr > 4)
            numBytes = 4;
        else
            numBytes = lenStr;

        for(int i = 0; i < numBytes; i++)
            bStr[i] = (byte) str.charAt(i);

        for(int i = numBytes; i < 4; i++)
            bStr[i] = (byte) ' ';

        dataOutputStream.write(bStr, 0,4);
    }

    public String readCommand3() throws IOException{
        String str;
        byte bStr[] = new byte[4];
        char cStr[] = new char[4];

        bStr = read_bytes(4);

        for(int i = 0; i < 4;i++)
            cStr[i]= (char) bStr[i];

        str = String.valueOf(cStr);

        return str.trim();
    }


    // Write i Read de la comanda PLY
    /*public void writePLY() throws IOException{
        writeCommand3("PLY");
    }
    public String readPLY() throws IOException{
        return readCommand3();
    }*/


    // Write i Read de la comanda STP
    public void writeSTP() throws IOException{
        writeCommand3("STP");
    }
    public String readSTP() throws IOException{
        return readCommand3();
    }


    public void writeCommand(String command) throws IOException{
        switch(command) {
            /* Cas en el que enviem la comanda Play */
            case "STRT":
                //System.out.println("prova a comutils");
                write_string("STRT");
                break;

            // Cas en el que enviem la comanda Stop
            case "EXIT":
                write_string("EXIT");
                break;

            // Cas en el que enviem la comanda Bet
            case "CASH":
                write_string("CASH");
                break;

            // Cas en el que enviem la comanda Call
            case "HITT":
                write_string("HITT");
                break;

            // Cas en el que enviem la comanda Fold
            case "SHOW":
                write_string("SHOW");
                break;

            // Cas en el que enviem la comanda Check
            case "BETT":
                write_string("BETT");
                break;
				
			case "CHK":
                write_string("SRND");
                break;

			case "RPLY":
                write_string("RPLY");
                break;
                
              /* Cas en el que enviem la comanda Play **/
            case "INIT":
                write_string("INIT");
                break;

            // Cas en el que enviem la comanda Stop
            case "IDCK":
                write_string("IDCK");
                break;

            // Cas en el que enviem la comanda Bet
            case "CARD":
                write_string("CARD");
                break;
                
            // Cas en el que enviem la comanda Fold
            case "WINS":
                write_string("WINS");
                break;

        }
    }


    public String readCommand() throws IOException{
        String value = read_string();
        return value;
    }





    public int string2int(String myString){
        int i = Integer.parseInt(myString);
        return i;
    }

    public String int2String(int myInt){
        String str = Integer.toString(myInt);
        return str;
    }


    public String char2String(char myChar){
        String str = Character.toString(myChar);
        return str;
    }

    /*
    public char string2char(String myString){
        char ch = myString.chartAt(0);
        return ch;
    }
    */


    public void writeSTRT(int a) throws IOException{
        writeCommand("STRT");
        write_space();
        write_int32(a);
    }

    public String readSTRT() {
        String str = "";

        return str;
    }

    public void writeINIT(int a) throws IOException{
        writeCommand("INIT");
        write_space();
        write_int32(a);
    }

    public String readINIT() throws IOException {
        String temp = readCommand();
        String temp2 = read_space();
        int bet = read_int32();

        return int2String(bet);
    }

    public void writeEXIT() throws IOException{
        writeCommand("EXIT");
    }

    public void writeCASH(int a) throws IOException{
        writeCommand("CASH");
        write_space();
        write_int32(a);
    }

    public String readCASH() throws IOException {
        String temp = readCommand();
        String temp2 = read_space();
        int max = read_int32();

        return int2String(max);
    }

    public void writeHITT() throws IOException{
        writeCommand("HITT");
    }

    public void writeBETT() throws IOException{
        writeCommand("BETT");
    }

    public void writeSRND() throws IOException{
        writeCommand("SRND");
    }

    public void writeRPLY() throws IOException{
        writeCommand("RPLY");
    }

    public void writeIDCK(char rank1, byte suit1, char rank2, byte suit2) throws IOException{
        writeCommand("IDCK");
        write_space();
        write_char(char2String(rank1));
        write_byte(suit1);
        write_space();
        write_char(char2String(rank2));
        write_byte(suit2);
    }

    public ArrayList readIDCK() throws IOException {
        ArrayList initialHand = new ArrayList();

        String temp = readCommand();
        String temp2 = read_space();
        initialHand.add(read_char());
        initialHand.add(read_byte());
        temp2 = read_space();
        initialHand.add(read_char());
        initialHand.add(read_byte());

        return initialHand;
    }

    public void writeCARD(String rank, String suit) throws IOException{
        writeCommand("CARD");
        write_space();
        write_char(rank);
        write_string(suit);
    }

    public ArrayList readCARD() throws IOException {
        ArrayList card = new ArrayList();

        String temp = readCommand();
        String temp2 = read_space();
        card.add(read_char());
        card.add(read_byte());

        return card;
    }

    public void writeSHOW(int len, ArrayList cardList) throws IOException{
        writeCommand("SHOW");
        write_space();
        write_int32(len);
        for (int i =  0; i < len; i = i + 2) {
            write_space();
            write_char((String) cardList.get(i));
            write_byte((Byte) cardList.get(i+1));
        }
    }

    public ArrayList readSHOW() throws IOException {
        ArrayList hand = new ArrayList();

        //String temp = readCommand();
        String temp2 = read_space();
        int len = read_int32();

        for (int i = 0; i < len; i = i + 2) {
            temp2 = read_space();
            hand.add(read_char());
            hand.add(read_byte());

        }

        return hand;
    }

    public void writeWINS(String winner, int chips) throws IOException{
        writeCommand("WINS");
        write_space();
        write_char(winner);
        write_space();
        write_int32(chips);
    }

    public ArrayList readWINS() throws IOException {
        ArrayList win = new ArrayList();

        String temp = readCommand();
        String temp2 = read_space();
        String winner = read_char();
        temp2 = read_space();
        int bet = read_int32();

        win.add(winner);
        win.add(bet);

        return win;
    }





    public static String byte2string(byte[] b){

        //System.out.println(b);

        String s = new String(b);

        //System.out.println(s);

        return s;

    }

    public static byte[] string2byte(String s){

        //System.out.println(s);

        byte[] bytes2 = s.getBytes();

        //System.out.println(bytes2);

        return bytes2;

    }




    public enum Endianness {
        BIG_ENNDIAN,
        LITTLE_ENDIAN
    }






}
