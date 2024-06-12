package Server.notifications;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class WebSocketHandlerBU extends TextWebSocketHandler {

    private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Extract token from query parameters
        URI uri = session.getUri();
        String query = uri.getQuery();
        String token = null;

        if (query != null && query.contains("token=")) {
            token = query.split("token=")[1];
        }

        if (token == null || !validateToken(token)) {
            session.close(CloseStatus.BAD_DATA);
            System.out.println("Invalid token, connection closed");
            return;
        }

        // Add session to the map with a unique client identifier
        String clientToken = session.getId(); // In a real application, use a proper client ID based on the token or user
        sessions.put(clientToken, session);
        System.out.println("Connected: " + clientToken);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Remove session from the map
        String clientToken = session.getId();
        sessions.remove(clientToken);
        System.out.println("Disconnected: " + clientToken);
    }

    // @Override
    // protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    //     System.out.println("Received message from client " + session.getId() + ": " + message.getPayload());
    //     // Echo the message back to the client
    //     session.sendMessage(new TextMessage("Server received: " + message.getPayload()));
    // }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Assuming the message format is "targetClientId:message"
        String[] parts = message.getPayload().split(":", 2);
        if (parts.length == 2) {
            String targetClientId = parts[0];
            String msg = parts[1];

            // Send message to the target client
            WebSocketSession targetSession = sessions.get(targetClientId);
            if (targetSession != null && targetSession.isOpen()) {
                targetSession.sendMessage(new TextMessage(msg));
            } else {
                System.out.println("Client not found or not open: " + targetClientId);
            }
        } else {
            System.out.println("Invalid message format");
        }
    }



    // @Override
    // public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status)
    //         throws Exception {
    //     sessions.remove(session);
    //     System.out.println("WebSocket connection closed: " + session.getId());
    // }

    // public static void broadcastMessage(String message) {
    //     for (WebSocketSession session : sessions) {
    //         try {
    //             session.sendMessage(new TextMessage(message));
    //         } catch (IOException e) {
    //             e.printStackTrace();
    //         }
    //     }
    // }

      // Method to broadcast message to all clients (optional)
      private void broadcastMessage(String message) {
        for (Map.Entry<String, WebSocketSession> entry : sessions.entrySet()) {
            WebSocketSession session = entry.getValue();
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean validateToken(String token) {
        // Implement your token validation logic here
        // For example, verify the token with a JWT library or check against a database
        return true; // Replace with actual validation
    }


    // private static final ConcurrentHashMap<String, WebSocketSession> clients =
    // new ConcurrentHashMap<>();
    // private static final ConcurrentHashMap<String, List<String>> messageStore =
    // new ConcurrentHashMap<>();

    // @Override
    // public void afterConnectionEstablished(WebSocketSession session) throws
    // Exception {
    // String clientToken = session.getId();
    // clients.put(clientToken, session);
    // System.out.println("Connected: " + clientToken);

    // // Send stored messages if any
    // if (messageStore.containsKey(clientToken)) {
    // List<String> messages = messageStore.get(clientToken);
    // for (String message : messages) {
    // try {
    // session.sendMessage(new TextMessage(message));
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }
    // messageStore.remove(clientToken);
    // }
    // }

    // @Override
    // protected void handleTextMessage(WebSocketSession session, TextMessage
    // message) throws Exception {
    // System.out.println("Received: " + message.getPayload());
    // session.sendMessage(new TextMessage("Server received: " +
    // message.getPayload()));
    // }

    // @Override
    // public void afterConnectionClosed(WebSocketSession session, CloseStatus
    // status) throws Exception {
    // String clientToken = session.getId();
    // clients.remove(clientToken);
    // System.out.println("Session closed: " + clientToken);
    // }

    // public void sendMessageToClient(String clientToken, String message) {
    // WebSocketSession session = clients.get(clientToken);
    // if (session != null && session.isOpen()) {
    // try {
    // session.sendMessage(new TextMessage(message));
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // } else {
    // // Store the message if the client is not connected
    // messageStore.computeIfAbsent(clientToken, k -> new ArrayList<>()).add(message);
    // }
    // }
}
 
