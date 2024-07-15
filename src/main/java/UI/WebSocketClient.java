package UI;

import org.springframework.stereotype.Component;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The WebSocketClient class is a WebSocket client endpoint that connects to a WebSocket server.
 * It uses annotations from the javax.websocket package to handle WebSocket events such as 
 * opening a connection, receiving messages, and closing a connection. The class is also annotated 
 * with @Component to indicate that it is a Spring-managed bean.
 *
 * Key Components:
 * 1. Annotations:
 *    - @Component: Marks the class as a Spring component.
 *    - @ClientEndpoint: Indicates that this class is a WebSocket client endpoint.
 *
 * 2. Fields:
 *    - private static Session session: Holds the WebSocket session once the connection is established.
 *    - private static ConcurrentHashMap<String, List<Message>> userMessages: A thread-safe map to store messages for each user.
 *
 * 3. Methods:
 *    - @OnOpen public void onOpen(Session session): This method is called when a WebSocket connection is established. 
 *      It sets the session and prints connection status messages.
 */

@Component
@ClientEndpoint
public class WebSocketClient {

    private static Session session;
    // private static final List<Message> messages = new ArrayList<>();
    private static ConcurrentHashMap<String, List<Message>> userMessages = new ConcurrentHashMap<>();

    // private static List<MessageListener> listeners = new ArrayList<>();

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected to server start");
        WebSocketClient.session = session;
        System.out.println("Connected to server end");

    }

    /**
     * Sends an alert message to a specified user via the WebSocket server.
     * Converts the Alert object to a string message before sending.
     *
     * @param targetUsername The username of the recipient.
     * @param alert          The Alert object containing the message to be sent.
     */
    @OnClose
    public void onClose() {
        System.out.println("Disconnected from server");
        WebSocketClient.session = null;
    }

    /**
     * Handles incoming messages from the server.
     * This method is called when a message is received from the server.
     * handle a case when the user is enter the system afte previously leave the system
     * and then it retrive all the past messages he already recevied from the server and 
     * 
     * 
     * @param message The message received from the server.
     */
    @OnMessage
    public void onMessage(String message) {
        synchronized (userMessages) {
            Message newMessage = new Message(message);
            String targetUser = newMessage.getTargetUser();
            List<Message> messages = userMessages.get(targetUser);
            if (messages == null) {
                messages = new ArrayList<>();
                messages.add(newMessage);
                userMessages.put(targetUser, messages);
            } else {
                // userMessages.get(targetUser).add(0, newMessage);
                boolean messageExists = false;
                for (Message msg : messages) {
                    if (msg.getMessage().equals(newMessage.getMessage())) {
                        messageExists = true;
                        break;
                    }
                }
                if (!messageExists) {
                    messages.add(0, newMessage);
                }
            }
        }
        // Optionally, notify the UI to update if you have a direct reference or a way
        // notifyListeners(message);
        System.out.println("Received from server: " + message);
    }

    public static List<Message> getMessages(String targetUser) {
        List<Message> messages = new ArrayList<>();
        if (userMessages.get(targetUser) != null) {
            messages = userMessages.get(targetUser);
        }
        return messages;
    }

    /**
     * Sends a message to the server.
     * This method sends a message to the server if the session is open.
     *
     * @param message The message to be sent to the server.
     */
    public static void sendMessage(String message) {
        if (session != null && session.isOpen()) {
            session.getAsyncRemote().sendText(message);
        }
    }

    /**
     * closes the session
     * 
     */
    public void closeSession() {
        if (session != null && session.isOpen()) {
            try {
                session.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends a message to a specific client through the server.
     * This method formats the message with the target client's ID before sending.
     *
     * @param targetClientId The ID of the target client.
     * @param message        The message to be sent.
     */
    public void sendMessage(String targetClientId, String message) {
        if (session != null && session.isOpen()) {
            session.getAsyncRemote().sendText(targetClientId + ":" + message);
        }
    }

    /**
     * Initiates a connection to the WebSocket server with a specified token.
     * This method attempts to connect to the server using the provided token for
     * authentication.
     *
     * @param token The authentication token to use for the connection.
     */
    public void connect(String token) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            URI uri = new URI("ws://localhost:8080/websocket?token=" + token); // Ensure the server is running on this
                                                                               // endpoint
            container.connectToServer(this, uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateMessageStsatus(Message message) {
        String targetUser = message.getTargetUser();
        List<Message> messages = userMessages.get(targetUser);
        if (messages != null) {
            for (Message m : messages) {
                if (m.equals(message)) {
                    m.setRead(message.isRead());
                    break;
                }
            }
        }
    }

    // public void addMessageListener(MessageListener listener) {
    // listeners.add(listener);
    // }

    // public void removeMessageListener(MessageListener listener) {
    // listeners.remove(listener);
    // }

    // private void notifyListeners(String message) {
    // for (MessageListener listener : listeners) {
    // listener.onMessageReceived(message);
    // }
    // }

}
