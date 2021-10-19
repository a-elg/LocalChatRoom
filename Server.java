import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server{
    private final ServerSocket serverSocket;//este objeto es resposable de escuchar las venideras conexiones de clientes para comunicarse
    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    } //el constructor
    public void startServer(){//para mantener el server corriendo
        try{
            //Listen for connections (clients to connect) on port 1234.
            while (!serverSocket.isClosed()){//mantenerlo corriendo mentras no esta cerrado, esperamos al client to connect
                //Will be closed in the Client Handler.
                Socket socket = serverSocket.accept();//metodo para que el socket se comunique con el client en el chat
                System.out.println("A new client has connected!");
                ClientHandler clientHandler = new ClientHandler(socket); //IMPORTANTE LUEGO
                Thread thread = new Thread(clientHandler);//PRIMER HILO PARA LUEGO IMPLEMENTAR EL RUNNABLE 
                //the start method empieza con la ejecusion del hilo
                //Cuando usamos start() el metodo que esta corriendo es llamado
                thread.start();//ejecutamos el hilo
            }
        }catch (IOException e){
            closeServerSocket();
        }
    }

    public void closeServerSocket(){
        try{
            if (serverSocket != null){
                serverSocket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);//servidor va escuchar a los clientes atraves del port number 1234
        Server server = new Server(serverSocket);
        server.startServer();//para mantenerlo corriendo
    }
}