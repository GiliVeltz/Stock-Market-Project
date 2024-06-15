package Server.notifications;

import Domain.Alerts.Alert;

/**
 * Handles notifications by sending messages through a WebSocket server.
 */

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
    
     /**
     * Sends an alert message to a specified user via the WebSocket server.
     * Converts the Alert object to a string message before sending.
     *
     * @param targetUsername The username of the recipient.
     * @param alert The Alert object containing the message to be sent.
     */
    public void sendMessage(String targetUsername, Alert alert) {
        String message = alert.getMessage();
        wServer.sendMessage(targetUsername, message);
    }


}
