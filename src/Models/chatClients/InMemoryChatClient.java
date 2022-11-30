package Models.chatClients;

import Models.Message;
import Models.gui.MainFrame;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InMemoryChatClient implements ChatClient{
    private String loggedUser;
    private List<String> loggedUsers;
    private List<Message> messages;
    private List<ChatClient> otherClients;
    private MainFrame GUI;

    public InMemoryChatClient(MainFrame gui) {
        GUI = gui;
        loggedUsers = new ArrayList<>();
        messages = new ArrayList<>();
        otherClients = new ArrayList<>();
    }

    @Override
    public void sendMessage(String message) {
        messages.add(new Message(loggedUser,message, LocalDateTime.now()));
        for (ChatClient cc:
             otherClients) {
            //if (!cc.isAuthenticated()){return;}
             cc.sendMessage(loggedUser,message,LocalDateTime.now());
        }
        //System.out.println(loggedUser+": "+message);
    }
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
