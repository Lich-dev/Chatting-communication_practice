package Models.chatClients;

import Models.Message;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatClient {
    void sendMessage(String message);
    void sendMessage(String user, String message, LocalDateTime time);
    void login(String username);
    void logout();
    boolean isAuthenticated();

    String getLoggedUser();
    void notifyLogin(String username);
    void notifyLogout(String username);
    List<Message>getMessages();
    void addClient(ChatClient cc);
}
