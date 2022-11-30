package Models.chatClients;

import Models.Message;
import Models.gui.MainFrame;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileChatClient implements ChatClient{
    private String loggedUser;
    private List<String> loggedUsers;
    private List<Message> messages;
    private List<ChatClient> otherClients;
    private MainFrame GUI;
    protected File file = new File("messages.txt");

    public FileChatClient(MainFrame gui) {
        GUI = gui;
        loggedUsers = new ArrayList<>();
        messages = new ArrayList<>();
        otherClients = new ArrayList<>();
    }

    @Override
    public void sendMessage(String message) {
        LocalDateTime time = LocalDateTime.now();
        messages.add(new Message(loggedUser,message, time));
        try(FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(loggedUser+';'+message+';'+time);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (ChatClient cc:
                otherClients) {
            if (!cc.isAuthenticated()){return;}
            cc.sendMessage(loggedUser,message,time);
        }
        //System.out.println(loggedUser+": "+message);
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
        try {
            if (!file.exists()){file.createNewFile();}
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()){
                String text = reader.nextLine();
                if (text==null){continue;}
                String[] txtsplit = text.split(";");
                Message msg = new Message(txtsplit[0],txtsplit[1],LocalDateTime.parse(txtsplit[2]));
                messages.add(msg);
                GUI.sendMessage(msg);
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        boolean auth = (loggedUser!=null);
        return auth;
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
