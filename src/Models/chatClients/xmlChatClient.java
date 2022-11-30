package Models.chatClients;

import Models.Message;
import Models.gui.MainFrame;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.*;
import org.w3c.dom.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class xmlChatClient implements ChatClient{
    private String loggedUser;
    private List<String> loggedUsers;
    private List<Message> messages;
    private List<ChatClient> otherClients;
    private MainFrame GUI;
    protected Document dom;

    public xmlChatClient(MainFrame gui) {
        GUI = gui;
        loggedUsers = new ArrayList<>();
        messages = new ArrayList<>();
        otherClients = new ArrayList<>();
    }

    @Override
    public void sendMessage(String message) {
        LocalDateTime time = LocalDateTime.now();
        messages.add(new Message(loggedUser,message, time));

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse("messages.xml");
            Node rootEl = dom.getFirstChild();
            Element msgEl = dom.createElement("message");
            msgEl.setTextContent(loggedUser+';'+message+';'+time);
            rootEl.appendChild(msgEl);
            try {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "no");//spams whitelines otherwise
                tr.setOutputProperty(OutputKeys.METHOD, "xml");
                tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                tr.transform(new DOMSource(dom),
                        new StreamResult(new FileOutputStream("messages.xml")));

            } catch (TransformerException | IOException te) {
                System.out.println(te.getMessage());
            }

        } catch (ParserConfigurationException | IOException | SAXException e) {
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
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            dom = db.parse("messages.xml");
            if (dom == null){
                dom = db.newDocument();
            }else {
                NodeList msgNodes = dom.getDocumentElement().getChildNodes();
                for (int i = 0; i < msgNodes.getLength();i++){
                    String[] txtsplit = msgNodes.item(i).getTextContent().split(";");
                    Message msg = new Message(txtsplit[0],txtsplit[1],LocalDateTime.parse(txtsplit[2]));
                    messages.add(msg);
                    GUI.sendMessage(msg);
                }
            }
        }catch (IOException | ParserConfigurationException | SAXException e) {
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
