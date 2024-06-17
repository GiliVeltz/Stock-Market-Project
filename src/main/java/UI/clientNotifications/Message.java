package UI.clientNotifications;

import java.time.LocalDateTime;

public class Message {
   
    String message;
    LocalDateTime timestamp;

    public Message(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now(); // Automatically sets the current time and date
    }
    //add getters and setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
