package UI;

import java.time.LocalDateTime;

public class Message {

    String message;
    String targetUser;
    boolean isRead;
    LocalDateTime timestamp;

    public Message(String message) {
        this.message = message;
        this.targetUser = "";
        this.timestamp = LocalDateTime.now(); // Automatically sets the current time and date
        this.isRead = false;
        setMessage(message);
    }

    // add getters and setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String newMessage) {
        String targetUser = getTarget(); // Reuse the existing method to get the target user
        this.targetUser = targetUser;
        if (targetUser.isEmpty()) {
            this.message = newMessage; // Return the original message if no target user is found
        }
        int targetUserIndex = newMessage.indexOf(targetUser);
        if (targetUserIndex != -1) {
            // Find the end index of the target user's name and add 1 to start after it
            int startIndex = targetUserIndex + targetUser.length();
            if (startIndex < newMessage.length()) {
                // Return the substring starting after the target user's name
                this.message =  newMessage.substring(startIndex).trim();
            }
        }
        
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    private String getTarget() {
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
    public String getTargetUser(){  
        return targetUser;
    }

    public void markAsRead(){
        this.isRead = true;
    }

    public void markAsNotRead(){
        this.isRead = false;
    }

    public boolean isRead(){ 
        return isRead;
    }

    public void setRead(boolean b) {
        this.isRead = b;
    }
}
