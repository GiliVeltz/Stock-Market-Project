package ServiceLayer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Server.notifications.WebSocketServer;



@Service
public class AlertService {
    @Autowired
    private final WebSocketServer webSocketServer;

    @Autowired
    public AlertService(WebSocketServer webSocketServer) {
        this.webSocketServer = webSocketServer;

        
    }

    public void sendBroadcastAlert(String message) {
        webSocketServer.broadcastMessage(message);
    }

    public void sendAlertToUser(String username, String message) {
        webSocketServer.sendMessage(username, message);
    }


    
    
    
}
