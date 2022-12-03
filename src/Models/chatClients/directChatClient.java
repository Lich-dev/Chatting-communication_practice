package Models.chatClients;

import Models.Message;
import Models.gui.MainFrame;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class directChatClient implements ChatClient{
    private String loggedUser;
    private List<String> loggedUsers;
    private List<Message> messages;
    private List<ChatClient> otherClients;
    private MainFrame GUI;
    Socket socket;

    public directChatClient(MainFrame gui) {
        GUI = gui;
        loggedUsers = new ArrayList<>();
        messages = new ArrayList<>();
        otherClients = new ArrayList<>();
    }

    @Override
    public void sendMessage(String message) {
        LocalDateTime time = LocalDateTime.now();
        messages.add(new Message(loggedUser,message, time));

        try {
            socket = new Socket("localhost",5001);
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            out.print(loggedUser+";"+message+";"+time);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (ChatClient cc:
                otherClients) {
            if (!cc.isAuthenticated()){return;}
            cc.sendMessage(loggedUser,message,time);
        }
    }
    @Override
    public void sendMessage(String user,String message,LocalDateTime time) {
        messages.add(new Message(user,message, time));
        if(isAuthenticated()) GUI.sendMessage(new Message(user,message, time));
    }
    @Override
    public void notifyLogin(String username){
        if(isAuthenticated())
            GUI.notifyLogin(username);
    }
    @Override
    public void notifyLogout(String username){
        if(isAuthenticated())
            GUI.notifyLogout(username);
    }
    @Override
    public void login(String username) {
        //new Thread(()->{
        try {
            socket = new Socket("localhost",5001);
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            out.print("request");
            out.flush();
            socket.shutdownOutput();
            //System.out.println(socket.isClosed());
            //while (true){

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );
            do{
                String str = in.readLine();
                if (str == null){continue;}
                String[] txtSplit = str.split(";");
                Message msg = new Message(txtSplit[0],txtSplit[1], LocalDateTime.parse(txtSplit[2]));
                messages.add(msg);
                GUI.sendMessage(msg);
            }while (in.ready());
            in.close();
            //break;
            //}
        } catch (IOException e) {
            e.printStackTrace();
        }
        //}).start();

        loggedUsers.add(new String(username));
        for (ChatClient cc:
                otherClients) {
            loggedUsers.add(cc.getLoggedUser());
            cc.notifyLogin(username);
            if (cc.isAuthenticated()) {
                GUI.loadChatter(cc.getLoggedUser());
            }
        }
        loggedUser = username;
        GUI.notifyLogin(username);
        //System.out.println("logged in as "+username);
    }

    @Override
    public void logout() {
        loggedUsers.clear();
        messages.clear();
        //System.out.println("User "+loggedUser+" has logged out");
        for (ChatClient cc :
                otherClients) {
            cc.notifyLogout(loggedUser);
        }
        loggedUser = null;
    }

    @Override
    public boolean isAuthenticated() {
        return (loggedUser!=null);
    }

    @Override
    public List<Message> getMessages() {
        return messages;
    }
    public String getLoggedUser(){return loggedUser;}

    @Override
    public void addClient(ChatClient cc) {
        otherClients.add(cc);
    }
}
