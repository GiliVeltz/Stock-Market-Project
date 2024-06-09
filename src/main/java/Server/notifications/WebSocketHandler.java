package Server.notifications;


import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

     private static final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("WebSocket connection established: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("Received message from client " + session.getId() + ": " + message.getPayload());
        // Echo the message back to the client
        session.sendMessage(new TextMessage("Server received: " + message.getPayload()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("WebSocket connection closed: " + session.getId());
    }

    public static void broadcastMessage(String message) {
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    // private static final ConcurrentHashMap<String, WebSocketSession> clients = new ConcurrentHashMap<>();
    // private static final ConcurrentHashMap<String, List<String>> messageStore = new ConcurrentHashMap<>();

    // @Override
    // public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    //     String clientId = session.getId();
    //     clients.put(clientId, session);
    //     System.out.println("Connected: " + clientId);

    //     // Send stored messages if any
    //     if (messageStore.containsKey(clientId)) {
    //         List<String> messages = messageStore.get(clientId);
    //         for (String message : messages) {
    //             try {
    //                 session.sendMessage(new TextMessage(message));
    //             } catch (IOException e) {
    //                 e.printStackTrace();
    //             }
    //         }
    //         messageStore.remove(clientId);
    //     }
    // }

    // @Override
    // protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    //     System.out.println("Received: " + message.getPayload());
    //     session.sendMessage(new TextMessage("Server received: " + message.getPayload()));
    // }

    // @Override
    // public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    //     String clientId = session.getId();
    //     clients.remove(clientId);
    //     System.out.println("Session closed: " + clientId);
    // }

    // public void sendMessageToClient(String clientId, String message) {
    //     WebSocketSession session = clients.get(clientId);
    //     if (session != null && session.isOpen()) {
    //         try {
    //             session.sendMessage(new TextMessage(message));
    //         } catch (IOException e) {
    //             e.printStackTrace();
    //         }
    //     } else {
    //         // Store the message if the client is not connected
    //         messageStore.computeIfAbsent(clientId, k -> new ArrayList<>()).add(message);
    //     }
    // }
}
