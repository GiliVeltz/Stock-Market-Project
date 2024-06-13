package Server.notifications;

import Domain.Alerts.Alert;

public class NotificationHandler {
    private static NotificationHandler instance;
    private static WebSocketServer wServer;

    private NotificationHandler() {
        // Initialization code
        wServer = WebSocketServer.getInstance();
    }

    public static synchronized NotificationHandler getInstance() {
        if (instance == null) {
            instance = new NotificationHandler();
        }
        return instance;
    }

    //send message via web socket server 
    public void sendMessage(String targetUsername, String message) {
        wServer.sendMessage(targetUsername, message);
    }
    
    //send message via web socket server 
    public void sendMessage(String targetUsername, Alert alert) {
        String message = alert.getMessage();
        wServer.sendMessage(targetUsername, message);
    }


}
