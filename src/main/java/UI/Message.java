package UI;

import java.time.LocalDateTime;

public class Message {

    String message;
    LocalDateTime timestamp;

    public Message(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now(); // Automatically sets the current time and date
    }

    // add getters and setters
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

    public String getTargetUser() {
        String targetUser = message;
        if (!message.contains(":")) {
            return ""; // or return null; depending on how you want to handle messages without a target
                       // user
        }
        // Split the message at the first occurrence of ":" and take the part after it
        String afterColon = targetUser.substring(targetUser.indexOf(":") + 1).trim();
        // Split by spaces to get all words after the ":"
        String[] words = afterColon.split("\\s+");
        // Check if there are any words after the ":"
        if (words.length > 0) {
            return words[0]; // Return the first word as the target user
        } else {
            return ""; // or return null; if there's nothing after the ":"
        }
    }
}
