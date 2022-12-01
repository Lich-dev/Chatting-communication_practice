package Models.chatClients;

import Models.Message;
import Models.gui.MainFrame;
import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class csvChatClient implements ChatClient{
    private String loggedUser;
    private List<String> loggedUsers;
    private List<Message> messages;
    private List<ChatClient> otherClients;
    private MainFrame GUI;

    public csvChatClient(MainFrame gui) {
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
            CSVReader reader = new CSVReader(new FileReader("messages.csv"));
            List<String[]> loaded = reader.readAll();
            CSVWriter writer = new CSVWriter(new FileWriter("messages.csv"));
            writer.writeAll(loaded);
            writer.writeNext(new String[]{loggedUser,message,time.toString()});
            writer.close();
        } catch (IOException | CsvException e) {
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
        if(new File("messages.csv").exists()){
            try {
                CSVReader reader = new CSVReader(new FileReader("messages.csv"));
                List<String[]> loaded = reader.readAll();
                for (String[] stringMessage :
                        loaded) {
                    Message msg = new Message(stringMessage[0],stringMessage[1],LocalDateTime.parse(stringMessage[2]));
                    messages.add(msg);
                    GUI.sendMessage(msg);
                }
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                new File("messages.csv").createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        /*String raw ="";
        try {
            raw = new String(Files.readAllBytes(Paths.get(String.valueOf(new File("messages.json")))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONArray obj = new JSONArray(raw);
        for (int i = 0; i<obj.length();i++){
            JSONObject json = obj.getJSONObject(i);
            Message msg = new Message(json.get("name").toString(),json.get("message").toString(),LocalDateTime.parse(json.get("time").toString()));
            messages.add(msg);
            GUI.sendMessage(msg);
        }*/

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
