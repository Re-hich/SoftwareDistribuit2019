package client;

public class Controller {

    private Client client;


    public Controller() {

    }

    public void iniClient( String ms, int port){
        this.client = new Client(ms, port);
        this.client.conectarServidor();
    }


    public Client getClient() {
        return client;
    }


    /*

    public void enviarComandes...
    */

}