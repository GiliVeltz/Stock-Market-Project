package Server.notifications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import Domain.Entities.Alerts.Alert;

/**
 * Handles notifications by sending messages through a WebSocket server.
 */
@Component
public class NotificationHandler {

    private WebSocketServer wServer;

    @Autowired
    private NotificationHandler(WebSocketServer wServer) {
        this.wServer = wServer;
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
