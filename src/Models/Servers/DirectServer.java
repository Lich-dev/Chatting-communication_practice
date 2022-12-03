package Models.Servers;
import Models.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.*;

public class DirectServer {
    public static final int PORT = 5001;
    ServerSocket ss = null;
    List<Message> messages = new ArrayList<>();

    public DirectServer() throws IOException {
        ss = new ServerSocket(PORT);
    }
    public void start(){
        while(true){
            new Thread(()->{
                try {
                    //System.out.println("listening");
                    Socket s =ss.accept();
                    //sendHistory(s);
                    //while (s.isConnected()){
                        //System.out.println("listening");
                    if (recieveMessage(s)){
                        sendHistory(s);}
                    //}
                    //System.out.println("closing");
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
    private boolean sendHistory(Socket s) throws IOException {
        PrintWriter out = new PrintWriter(s.getOutputStream());
        //System.out.println("history");
        for (Message msg :
                messages) {
            out.println(msg.getAuthor() + ";" + msg.getText() + ";" + msg.getTime());
        }
        out.flush();
        out.close();
        return true;
        //Uzavreme socket = konec spojeni
    }
    private boolean recieveMessage(Socket s) throws IOException{
        //System.out.println(s.isClosed());
        BufferedReader in = new BufferedReader(
                new InputStreamReader(s.getInputStream())
        );
        //System.out.println("|"+in.readLine()+"|");
        String str = in.readLine();
        //System.out.println(str);
        if (str == null){
            in.close();
            return false;}
        //System.out.println(str);
        if (!str.contains(";")){
            if (str.contains("request")){
                //System.out.println("requesting");
                return true;
            }
            in.close();
            return false;
        }
        String[] msg = str.split(";");
        messages.add(new Message(msg[0],msg[1], LocalDateTime.parse(msg[2])));
        in.close();
        return false;
    }
}
