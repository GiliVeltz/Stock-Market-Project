package Server;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws/server")
public class WebSocketServer {
    private static Map<String, Session> clients = new ConcurrentHashMap<>();
    private static Map<String, List<String>> messageStore = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        String clientId = session.getId();
        clients.put(clientId, session);
        System.out.println("Connected: " + clientId);

        // Send stored messages if any
        if (messageStore.containsKey(clientId)) {
            List<String> messages = messageStore.get(clientId);
            for (String message : messages) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            messageStore.remove(clientId);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Received: " + message);
        try {
            session.getBasicRemote().sendText("Server received: " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session) {
        String clientId = session.getId();
        clients.remove(clientId);
        System.out.println("Session closed: " + clientId);
    }

    public void sendMessageToClient(String clientId, String message) {
        Session session = clients.get(clientId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Store the message if the client is not connected
            messageStore.computeIfAbsent(clientId, k -> new ArrayList<>()).add(message);
        }
    }
}
