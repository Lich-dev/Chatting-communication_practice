package Models.gui;

import Models.Message;
import Models.chatClients.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Objects;

public class MainFrame extends JFrame {
    //InMemoryChatClient client = new InMemoryChatClient(this);
    //FileChatClient client = new FileChatClient(this);
    //xmlChatClient client = new xmlChatClient(this);
    //jsonChatClient client = new jsonChatClient(this);
    csvChatClient client = new csvChatClient(this);
    JTextArea txtChatArea;
    JTextField msgField;
    JTable userTable;
    JScrollPane userTablePane;
    public MainFrame(int width, int height){
        super("Chatting App");
        setSize(width,height);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initGui(width,height);

        pack();

        setVisible(true);
    }
    private void initGui(int width, int height){
        JPanel panelMain = new JPanel(new BorderLayout());

        panelMain.setSize(width,height);
        panelMain.add(initLoginPanel(),BorderLayout.NORTH);
        panelMain.add(initChatPanel(),BorderLayout.CENTER);
        panelMain.add(initMessagePanel(),BorderLayout.SOUTH);

        panelMain.add(initUserListPanel(width/5, height*2/3),BorderLayout.EAST);

        add(panelMain);
    }
    private JPanel initUserListPanel(int width,int height){//TODO if not logged in, dont recieve message, send all when logging in
        JPanel panel = new JPanel();
        FlowLayout layout = new FlowLayout(FlowLayout.RIGHT);
        panel.setLayout(layout);
        panel.setSize(width, height);//TODO use panel, make fileChatClient i guess
        layout.preferredLayoutSize(panel);

        DefaultTableModel tdm = new DefaultTableModel();
        tdm.addColumn("Chatters");
        userTable = new JTable();
        userTable.setModel(tdm);

        JScrollPane userTablePane = new JScrollPane(userTable);
        userTablePane.setPreferredSize(new Dimension(width,height));
        userTablePane.revalidate();

        panel.add(userTablePane);
        return panel;
    }
    private JPanel initLoginPanel(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        panel.add(new JLabel("Username: "));
        JTextField textField = new JTextField("",30);
        panel.add(textField);

        JButton btnLogin = new JButton("Login");
        JButton btnLogout = new JButton("Logout");
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (Objects.equals(textField.getText(), "")){
                    String errorMessage = "Please input your username!";
                    JOptionPane.showMessageDialog(panel,errorMessage,"Error",0);
                    return;
                }
                //load all messages in chat
                List<Message> messages = client.getMessages();
                for (int i = 0; i<messages.stream().count();i++) {
                    txtChatArea.append(messages.get(i).getAuthor()+": "+messages.get(i).getText()+'\n');
                }

                client.login(textField.getText());
                msgField.setText("");
                btnLogout.setVisible(true);
                btnLogin.setVisible(false);
            }
        });
        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                client.logout();
                msgField.setText("");
                txtChatArea.setText("");
                btnLogout.setVisible(false);
                DefaultTableModel dtm = new DefaultTableModel();
                dtm.addColumn("Chatters");
                userTable.setModel(dtm);
                btnLogin.setVisible(true);
            }
        });
        panel.add(btnLogin);
        panel.add(btnLogout);
        btnLogout.setVisible(false);

        return panel;
    }
    private JPanel initChatPanel(){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));

        txtChatArea = new JTextArea();
        txtChatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtChatArea);
        panel.add(scrollPane);

        /*for (int i = 0; i < 50; i++){
            txtChatArea.append("Message: "+i+'\n');
        }*/
        return panel;
    }
    private JPanel initMessagePanel(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnSend = new JButton("Send");
        msgField = new JTextField("",50);

        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(client.isAuthenticated())
                {
                    if (Objects.equals(msgField.getText(), "")){
                        String errorMessage = "Can't send an empty message!";
                        JOptionPane.showMessageDialog(panel,errorMessage,"Error",0);
                        return;
                    }
                    client.sendMessage(msgField.getText());
                    txtChatArea.append(client.getLoggedUser()+": "+msgField.getText()+'\n');
                    msgField.setText("");
                }else{
                    String errorMessage = "User not logged in!";
                    JOptionPane.showMessageDialog(panel,errorMessage,"Error",0);
                    msgField.setText("");
                }
            }
        });

        panel.add(msgField);
        panel.add(btnSend);

        return panel;
    }
    public void sendMessage(Message message){
        txtChatArea.append(message.getAuthor()+": "+message.getText()+'\n');
    }
    public ChatClient getChatClient(){
        return client;
    }
    public void addChatClient(ChatClient cc){
        client.addClient(cc);
    }
    public void notifyLogin(String username){
        txtChatArea.append("user "+username+" has logged into the chat, say hi!"+'\n');
        DefaultTableModel dtm = (DefaultTableModel) userTable.getModel();
        dtm.addRow(new String[]{username});
    }
    public void notifyLogout(String username){
        txtChatArea.append("user "+username+" has left the chat"+'\n');
        DefaultTableModel dtm = (DefaultTableModel) userTable.getModel();
        for (int i = 0; i < userTable.getRowCount(); i++) {
            if(dtm.getValueAt(i,0) == username){
                dtm.removeRow(i);
                break;
            }
        }
    }
    public void loadChatter(String username){
        DefaultTableModel dtm = (DefaultTableModel) userTable.getModel();
        dtm.addRow(new String[]{username});
    }
}
