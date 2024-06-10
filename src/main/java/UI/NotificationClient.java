package UI;
import java.net.URI;

import javax.websocket.*;

@ClientEndpoint
public class NotificationClient {

    private Session session;
    private MessageHandler messageHandler;

    public NotificationClient(URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected to server");
        this.session = session;
        
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Received message from server: " + message);

        if (this.messageHandler != null) {
            this.messageHandler.handleMessage(message);
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("Disconnected from server");

        this.session = null;
    }

    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    public interface MessageHandler {
        void handleMessage(String message);
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
