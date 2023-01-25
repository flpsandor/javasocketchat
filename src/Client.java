import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String username;

    public Client(Socket socket, String username) {
        try{
            this.socket = socket;
            this.username = username;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        }catch (IOException e){
            shutDown(socket, bufferedWriter, bufferedReader);
        }
    }

    public void sendMessage(){
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while(socket.isConnected()){
                String message = scanner.nextLine();
                bufferedWriter.write(username +": "+message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            shutDown(socket, bufferedWriter, bufferedReader);
        }
    }

    public void listen(){
        new Thread(() -> {
            String message;
            while(socket.isConnected()){
                try {
                    message = bufferedReader.readLine();
                    System.out.println(message);
                }
                catch (IOException e){
                    shutDown(socket, bufferedWriter, bufferedReader);
                }
            }
        }).start();
    }

    public void shutDown(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader){
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

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();

        Socket socket = new Socket("localhost", 5000);
        Client client = new Client(socket, username);
        client.listen();
        client.sendMessage();
    }
}
