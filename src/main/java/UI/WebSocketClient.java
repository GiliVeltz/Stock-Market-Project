package UI;

import org.glassfish.tyrus.client.ClientManager;
import org.springframework.stereotype.Component;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import java.io.IOException;
import java.net.URI;
@Component
@ClientEndpoint
public class WebSocketClient {

    private Session session;

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected to server start");
        this.session = session;
        try {
            session.getBasicRemote().sendText("Hello Server");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Connected to server end");
    }
    
    @OnClose
    public void onClose() {
        System.out.println("Disconnected from server");
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Received from server: " + message);
    }

    public void sendMessage(String message) {
        if (session != null && session.isOpen()) {
            session.getAsyncRemote().sendText(message);
        }
    }

      public void connect() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            URI uri = new URI("ws://localhost:8080/websocket"); // Ensure the server is running on this endpoint
            container.connectToServer(this, uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
