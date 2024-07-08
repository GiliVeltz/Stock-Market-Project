package Server.notifications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import Domain.Entities.Alerts.Alert;

/**
 * Handles notifications by sending messages through a WebSocket server.
 */
@Component
public class NotificationHandler {

    private static NotificationHandler instance;
    @Autowired
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

    // Using this method for testing purposes
    public static void setInstance(NotificationHandler _notificationHandlerMock) {
        instance = _notificationHandlerMock;
    }


}
