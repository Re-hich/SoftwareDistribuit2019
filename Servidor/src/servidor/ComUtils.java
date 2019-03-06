package servidor;


//import com.oracle.jrockit.jfr.ContentType;
//import static com.oracle.jrockit.jfr.ContentType.None;
//import static com.sun.org.apache.xml.internal.utils.XMLCharacterRecognizer.isWhiteSpace;
import java.net.*;
import java.io.*;
import java.util.Locale;

public class ComUtils{

    /* Mida d'una cadena de caracters */
    private final int STRSIZE = 40;
    /* Objectes per escriure i llegir dades */
    private DataInputStream dis;
    private DataOutputStream dos;



    public ComUtils(Socket socket) throws IOException{
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
    }


    public ComUtils(File file) throws IOException{
        dis = new DataInputStream(new FileInputStream(file));
        dos = new DataOutputStream(new FileOutputStream(file));
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
        byte bytes[] = new byte[4];
        bytes  = read_bytes(4);

        return bytesToInt32(bytes,"be");
    }

    /* Escriure un enter de 32 bits */
    public void write_int32(int number) throws IOException{
        byte bytes[]=new byte[4];

        int32ToBytes(number,bytes,"be");
        dos.write(bytes, 0, 4);
    }

    /* Llegir un string de mida STRSIZE */
    public String read_string() throws IOException{
        String str;
        byte bStr[] = new byte[STRSIZE];
        char cStr[] = new char[STRSIZE];

        bStr = read_bytes(STRSIZE);

        for(int i = 0; i < STRSIZE;i++)
            cStr[i]= (char) bStr[i];

        str = String.valueOf(cStr);

        return str.trim();
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

        dos.write(bStr, 0,STRSIZE);
    }

    /* Passar d'enters a bytes */
    private int int32ToBytes(int number,byte bytes[], String endianess){
        if("be".equals(endianess.toLowerCase())){
            bytes[0] = (byte)((number >> 24) & 0xFF);
            bytes[1] = (byte)((number >> 16) & 0xFF);
            bytes[2] = (byte)((number >> 8) & 0xFF);
            bytes[3] = (byte)(number & 0xFF);
        }
        else{
            bytes[0] = (byte)(number & 0xFF);
            bytes[1] = (byte)((number >> 8) & 0xFF);
            bytes[2] = (byte)((number >> 16) & 0xFF);
            bytes[3] = (byte)((number >> 24) & 0xFF);
        }
        return 4;
    }

    /* Passar de bytes a enters */
    private int bytesToInt32(byte bytes[], String endianess){
        int number;

        if("be".equals(endianess.toLowerCase())){
            number=((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) |
                    ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
        }
        else{
            number=(bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8) |
                    ((bytes[2] & 0xFF) << 16) | ((bytes[3] & 0xFF) << 24);
        }
        return number;
    }

    //llegir bytes.
    private byte[] read_bytes(int numBytes) throws IOException{
        int len=0 ;
        byte bStr[] = new byte[numBytes];
        int bytesread=0;
        do {
            bytesread= dis.read(bStr, len, numBytes-len);
            if (bytesread == -1)
                throw new IOException("Broken Pipe");
            len += bytesread;
        } while (len < numBytes);
        return bStr;
    }

    /* Llegir un string  mida variable size = nombre de bytes especifica la longitud*/
    public  String read_string_variable(int size) throws IOException{
        byte bHeader[]=new byte[size];
        char cHeader[]=new char[size];
        int numBytes=0;

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
        dos.write(bHeader, 0, size);
        // Enviem l'string writeBytes de DataOutputStrem no envia el byte més alt dels chars.
        dos.writeBytes(str);
    }

    public void writeChar(String s) throws IOException{
        byte bStr[] = new byte[2];/*1 char = 16 bits!!!!!!!!!*/
        String str = s;

        bStr[0] = (byte) str.charAt(0);

        dos.write(bStr, 0, 1);
    }

    public String readChar() throws IOException {
        byte bStr[];
        char cStr[] = new char[1];
        bStr = read_bytes(1);
        cStr[0] = (char) bStr[0];
        return String.valueOf(cStr);
    }



    //------------- FI PRACTICA 0 ------------


    /*

    La llogica del joc NO esta a ComUtils

    ComUtils functions:
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

        dos.write(bStr, 0, 1);
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
        byte bStr[] = new byte[3];

        lenStr = str.length();

        if (lenStr > 3)
            numBytes = 3;
        else
            numBytes = lenStr;

        for(int i = 0; i < numBytes; i++)
            bStr[i] = (byte) str.charAt(i);

        for(int i = numBytes; i < 3; i++)
            bStr[i] = (byte) ' ';

        dos.write(bStr, 0,3);
    }

    public String readCommand3() throws IOException{
        String str;
        byte bStr[] = new byte[3];
        char cStr[] = new char[3];

        bStr = read_bytes(3);

        for(int i = 0; i < 3;i++)
            cStr[i]= (char) bStr[i];

        str = String.valueOf(cStr);

        return str.trim();
    }


    // Write i Read de la comanda PLY
    public void writePLY() throws IOException{
        writeCommand3("PLY");
    }
    public String readPLY() throws IOException{
        return readCommand3();
    }


    // Write i Read de la comanda STP
    public void writeSTP() throws IOException{
        writeCommand3("STP");
    }
    public String readSTP() throws IOException{
        return readCommand3();
    }

/*
    public void writeCommand(String command) throws IOException{
        switch(command) {
            /* Cas en el que enviem la comanda Play *
            case "PLY":
                //System.out.println("prova a comutils");
                write_string("PLY");
                break;

            // Cas en el que enviem la comanda Stop
            case "STP":
                write_string("STP");
                break;

            // Cas en el que enviem la comanda Bet
            case "BET":
                write_string("BET");
                break;

            // Cas en el que enviem la comanda Call
            case "CAL":
                write_string("CAL");
                break;

            // Cas en el que enviem la comanda Fold
            case "FLD":
                write_string("FLD");
                break;

            // Cas en el que enviem la comanda Check
            case "CHK":
                write_string("CHK");
                break;

        }
    }

    */

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


    void writeCommandTwoParametre(String stk, String int2String, String int2String0) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void writeCommandOneParametre(String trn, String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }







}
