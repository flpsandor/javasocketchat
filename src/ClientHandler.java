import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {

    public static List<ClientHandler> clientHandlerList = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    public ClientHandler(Socket socket) throws IOException {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            clientHandlerList.add(this);
            broadcastMessage("SERVER: " + clientUsername + " is online");
        } catch (IOException e) {
            shutDown(socket, bufferedWriter, bufferedReader);
        }
    }

    public void broadcastMessage(String message){
        for(var client: clientHandlerList){
            try {
                if(!client.clientUsername.equals(clientUsername)){
                    client.bufferedWriter.write(message);
                    client.bufferedWriter.newLine();
                    client.bufferedWriter.flush();
                }
            }
            catch (IOException e){
                shutDown(socket, bufferedWriter, bufferedReader);
            }
        }
    }

    public void removeClient(){
        clientHandlerList.remove(this);
        broadcastMessage("SERVER: "+clientUsername+" is offline");
    }

    public void shutDown(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader){
        removeClient();
        try{
            if(bufferedWriter!=null){
                bufferedWriter.close();
            }
            if(bufferedReader!=null){
                bufferedReader.close();
            }
            if(socket != null){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String clientMessage;
        while (socket.isConnected()) {
            try {
                clientMessage = bufferedReader.readLine();
                broadcastMessage(clientMessage);
            } catch (IOException e) {
                shutDown(socket, bufferedWriter, bufferedReader);
                break;
            }
        }
    }
}
