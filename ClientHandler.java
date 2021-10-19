import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();//para mentener pista detodo los clientes, para cuando sea que se mande el mensaje se mande a todos los otros. es static porque queremos que pertenezca a cada objeto de las clases
    private Socket socket;//hilo para establecer conexion entre el cliente y servidor
    private BufferedReader bufferedReader;//para leer data mensajes que han enviado los clientes.
    private BufferedWriter bufferedWriter;//mandar mensajes broadcasting a clientes que por ejemplo mandan otros clientes al cliente
    private String clientUsername;

    public ClientHandler(Socket socket){
        //propiedades del constructor
        try{
            this.socket = socket;//este es objeto de esta clase y pondra el hilo en lo que va en la clase
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));//character stream se pone con writer, porque queremos mandar characteres
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //el sig es importante que se haga en el Client.java la parte correspondiente que esperara
            this.clientUsername = bufferedReader.readLine(); //para mandar el primer el mensaje de clientes a poner el mensajito de por favor introduzca su nombre de usuario -con readLine al press enter key se mandara el mensaje
            clientHandlers.add(this);//para agregar al cliente en el array list para que sea parte delos mensajes do los otros clientes ---this representa clienthANDLER object
            broadcastMessage("SERVER: " + clientUsername + " has entered the chat!");//para mandarle a los otros en el chat que alguien nuevo a entrado
        }catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    //necesitamos sobreescribir el metodo del runnable
    @Override
    public void run(){//listening for messages y usar muchos hilos multiplethreads y no tener que esperar a alguien mandar un mensaje antes de que yo pueda mandar un mensaje
        String messageFromClient;

        while (socket.isConnected()){//para escuchar mensajes de los clientes mientras el hilo esta conectado
            try{
                messageFromClient = bufferedReader.readLine();//espera el programa hasta recibir un mensaje del cliente, por eso necesitamos que esta operacion que bloquea le pongamos atencion
                broadcastMessage(messageFromClient);
            }catch (IOException e){
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;//cuando el cliente se disconecta pues sigue corriendo el metodo
            }
        }
    }

    public void broadcastMessage(String messageToSend){
        for(ClientHandler clientHandler : clientHandlers){//por cada clientHandler va representar a cada uno en el array
            try{
                if(!clientHandler.clientUsername.equals(clientUsername)){//para que no se mande el mensaje al que esta escribiendo y no se vea doble, pues esto
                    clientHandler.bufferedWriter.write(messageToSend);//pa mandar mensajestodo lo que pase al metodod
                    clientHandler.bufferedWriter.newLine();//enterkey
                    clientHandler.bufferedWriter.flush();//overwhelming. antes de que el buffer se llene pues flush.
                }
            }catch (IOException e){
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    //para que al salirse del chat a todos los otros le salga que ya se salio esa persona del chat
    public void removeClientHandler(){
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + clientUsername + " has left the chat!");
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        removeClientHandler();
        try{
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if (bufferedWriter !=null){
                bufferedWriter.close();
            }
            if (socket != null){
                socket.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}