package Models.Servers;
import Models.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

public class HTTPServer {
    public static final int PORT = 5001;
    ServerSocket ss = null;
    List<Message> messages = new ArrayList<>();

    public HTTPServer() throws IOException {
        ss = new ServerSocket(PORT);
    }
    public void start(){
        messages.add(new Message("Larry","This is a message",LocalDateTime.now()));
        messages.add(new Message("Anon","This is another message",LocalDateTime.now()));
        messages.add(new Message("Yu","What is going on",LocalDateTime.now()));
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
        out.println("HTTP/1.1 200 OK");
        out.println("Content-type: text/html");
        String ts ="<html>" +
                    "<body>";
        for (Message msg :
                messages) {
            ts += "<H3><b style=\"text-transform: uppercase;\">"+msg.getAuthor()+"</b>: "+msg.getText()+" ("+msg.getTime()+")"+"</H3>";
        }
        ts+=        "</body>" +
                "</html>";
        out.println();
        out.println(ts);
        /*for (Message msg :
                messages) {
            out.println(msg.getAuthor() + ";" + msg.getText() + ";" + msg.getTime());
        }*/
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
        boolean reply = false;
        do{
            String str = in.readLine();
            System.out.println(str);
            if (str == null){continue;}
            if(str.startsWith("GET")&&str.contains("history")){
                return true;
            }else if(str.startsWith("POST")&&str.contains("msg")){
                reply = true;
                break;
            }
            //String[] txtSplit = str.split(";");
            //Message msg = new Message(txtSplit[0],txtSplit[1], LocalDateTime.parse(txtSplit[2]));
            //messages.add(msg);
            //GUI.sendMessage(msg);
        }while (in.ready());
        if (reply){
            System.out.println("sending reply");
            PrintWriter out = new PrintWriter(s.getOutputStream());
            out.println("HTTP/1.1 200 OK");
            out.flush();
            out.close();
        }
        /*
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
        in.close();*/
        return false;
    }
}
