package Server.notifications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import ServiceLayer.TokenService;
import ServiceLayer.TokenServiceCopy;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class WebSocketServer extends TextWebSocketHandler {
    @Autowired
    private TokenServiceCopy tokenService;
     // Singleton instance
     private static WebSocketServer instance;
    // assumption messages as aformat of:"targetUsername:message"

    private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>(); // registered user ->
                                                                                             // <username,session>,
                                                                                             // guest -> <token,session>
    private static final Map<String, Queue<String>> messageQueues = new ConcurrentHashMap<>(); // <username,
                                                                                               // messageQueue>

                  // Private constructor to prevent instantiation
    private WebSocketServer() {
        // Initialization code
    }

    // Method to get singleton instance
    public static synchronized WebSocketServer getInstance() {
        if (instance == null) {
            instance = new WebSocketServer();
        }
        return instance;
    }                                                                               

    /*
     * This method is called after the connection is established. It validates the
     * token and adds the session to the sessions map.
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        URI uri = session.getUri();
        String query = uri.getQuery();
        String token = null;

        if (query != null) {
            for (String param : query.split("&")) {
                if (param.startsWith("token=")) {
                    token = param.split("token=")[1];
                }
            }
        }

        if (token == null || !validateToken(token)) {
            session.close(CloseStatus.BAD_DATA);
            System.out.println("Invalid token, connection closed");
            return;
        }

        String username = tokenService.extractUsername(token);
        String clientKey = (username != null) ? username : "guest-" + token;

        if (username != null && tokenService.isUserAndLoggedIn(token)) {
            // User is logged in
            sessions.put(clientKey, session);
            System.out.println("Connected: " + clientKey);

            // Send any queued messages sent while user was loggedOut
            Queue<String> queue = messageQueues.getOrDefault(username, new ConcurrentLinkedQueue<>());
            while (!queue.isEmpty()) {
                String message = queue.poll();
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                }
            }
            messageQueues.remove(username);
        } else {
            // User is a guest
            sessions.put(clientKey, session);
            System.out.println("Connected: " + clientKey);
        }
        if (sessions.size() > 1) {
            broadcastMessage("Hello all clients!");
            
        }
    }

   
    
    /**
     * This method is called after the connection is closed. It removes the session
     * from the sessions map.
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.values().remove(session);
        System.out.println("Disconnected: " + session.getId());
    }

    /*
     * This method is called when a message is received from the client. The message
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Assuming the message format is "targetUsername:message"
        String[] parts = message.getPayload().split(":", 2);
        if (parts.length == 2) {
            String targetUsername = parts[0];
            String msg = parts[1];

            // Send message to the target client
            WebSocketSession targetSession = sessions.get(targetUsername);
            if (targetSession != null && targetSession.isOpen()) {
                targetSession.sendMessage(new TextMessage(msg));
            } else {
                // Queue message for later delivery
                messageQueues.computeIfAbsent(targetUsername, k -> new ConcurrentLinkedQueue<>()).add(msg);
                System.out.println("Client not found or not open, message queued for : " + targetUsername);
            }
        } else {
            System.out.println("Invalid message format");
        }
    }

    // Method to broadcast message to all clients 
    public void broadcastMessage(String message) {
        for (Map.Entry<String, WebSocketSession> entry : sessions.entrySet()) {
            WebSocketSession session = entry.getValue();
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                    System.out.println("Broadcasted message to: " + entry.getKey());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //method to send message to a specific client if a registered user and not logged in that add to its queue
    public void sendMessage(String username, String message) {
        WebSocketSession session = sessions.get(username);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
                System.out.println("Sent message to: " + username);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Queue message for later delivery
            messageQueues.computeIfAbsent(username, k -> new ConcurrentLinkedQueue<>()).add(message);
            System.out.println("Client not found or not open, message queued for : " + username);
        }
    }
    
    private boolean validateToken(String token) {
    //    return tokenService.validateToken(token);
    return true;
    }
}
