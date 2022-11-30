import Models.chatClients.ChatClient;
import Models.chatClients.InMemoryChatClient;
import Models.gui.MainFrame;

public class Main {
    public static void main(String[] args) {
        MainFrame adam = new MainFrame(800,600);
        MainFrame eve = new MainFrame(800,600);//todo swagger API

        adam.addChatClient(eve.getChatClient());
        eve.addChatClient(adam.getChatClient());

        adam.setLocation(100,200);
        eve.setLocation(1000,200);
        /*todo
           "merge" with myDB,
           "merge" with liveServer,
           add HTTP requests GET AND POST (networking),
           add csv,
           add json*/
    }
}