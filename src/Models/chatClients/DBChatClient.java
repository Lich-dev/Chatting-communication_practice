package Models.chatClients;

import Models.Message;
import Models.gui.MainFrame;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DBChatClient implements ChatClient{
    private String loggedUser;
    private List<String> loggedUsers;
    private List<Message> messages;
    private List<ChatClient> otherClients;
    private MainFrame GUI;

    public DBChatClient(MainFrame gui) {
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
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection("jdbc:derby:chatDB;create=true");
            Statement st = con.createStatement();
            //st.addBatch("drop table peeps");
            //st.addBatch("create table messages (id int not null generated always as identity constraint messages_PK primary key, name varchar(20),message varchar(50),time varchar(100))");
            st.addBatch("insert into messages (name,message,time) values ('"+loggedUser+"','"+message+"','"+time+"')");
            st.executeBatch();
            /*ResultSet res = st.executeQuery("select * from messages");
            while(res.next()){
                System.out.println(res.getString("name")+res.getString("message")+res.getString("time"));
            }*/
        } catch (SQLException | ClassNotFoundException e) {
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
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection("jdbc:derby:chatDB;create=true");
            Statement st = con.createStatement();
            //st.addBatch("drop table peeps");
            //st.addBatch("create table messages (id int not null generated always as identity constraint messages_PK primary key, name varchar(20),message varchar(50),time varchar(100))");
            //st.addBatch("insert into messages (name,message,time) values ('"+loggedUser+"','"+message+"','"+time+"')");
            //st.executeBatch();
            ResultSet res = st.executeQuery("select * from messages");
            while(res.next()){
                Message msg = new Message(res.getString("name"),res.getString("message"),LocalDateTime.parse(res.getString("time")));
                messages.add(msg);
                GUI.sendMessage(msg);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
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
