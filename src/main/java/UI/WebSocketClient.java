package UI;

import org.glassfish.tyrus.client.ClientManager;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.io.IOException;
import java.net.URI;

@ClientEndpoint
public class WebSocketClient {

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected to server");
        try {
            session.getBasicRemote().sendText("Hello Server");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Received from server: " + message);
    }

    public static void connect() {
        ClientManager client = ClientManager.createClient();
        try {
            client.connectToServer(WebSocketClient.class, new URI("ws://localhost:8081/ws/server"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
