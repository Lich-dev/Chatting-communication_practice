import Models.chatClients.ChatClient;
import Models.chatClients.InMemoryChatClient;
import Models.gui.MainFrame;

import java.sql.*;

public class Main {
    public static void main(String[] args) {
        MainFrame adam = new MainFrame(800,600);
        MainFrame eve = new MainFrame(800,600);

        adam.addChatClient(eve.getChatClient());
        eve.addChatClient(adam.getChatClient());

        adam.setLocation(100,200);
        eve.setLocation(800,200);

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection con = DriverManager.getConnection("jdbc:derby:chatDB;create=true");
            Statement st = con.createStatement();
            //st.addBatch("create table messages (id int not null generated always as identity constraint messages_PK primary key, name varchar(20),message varchar(50),time varchar(100))");
            //st.addBatch("delete from messages");
            st.executeBatch();
            //ResultSet res = st.executeQuery("select * from peeps");
            /*while(res.next()){
                System.out.println(res.getString("jmeno"));
            }*/
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        /*todo
           "merge" with liveServer,
           add HTTP requests GET AND POST (networking)*/
    }
}