package Models;

import java.time.LocalDateTime;

public class Message {
    private String author;
    private String text;
    private LocalDateTime time;

    public Message(String author, String text, LocalDateTime time) {
        this.author = author;
        this.text = text;
        this.time = time;
    }

    public String getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getTime() {
        return time;
    }
}
