package Server.notifications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import Domain.Repositories.DbUserRepository;
import Domain.Repositories.InterfaceUserRepository;
import ServiceLayer.TokenService;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is responsible for handling WebSocket communication within the
 * application.
 * It extends TextWebSocketHandler, which provides a simple way to handle text
 * messages
 * over WebSocket connections.
 * 
 * The WebSocketServer class provides methods for:
 * 
 * 1. Handling WebSocket connections:
 * - onOpen: Invoked when a new WebSocket connection is established.
 * - onClose: Invoked when an existing WebSocket connection is closed.
 * 
 * 2. Handling incoming messages:
 * - handleTextMessage: Invoked when a new text message is received from a
 * client.
 * 
 * 3. Sending messages to clients:
 * - sendMessage: Allows the server to send a text message to a specific client.
 * 
 * Usage:
 * - When a client connects to the WebSocket server, the onOpen method is
 * called, allowing
 * the server to perform any initialization or logging.
 * - When a client sends a message, the handleTextMessage method is called,
 * allowing the
 * server to process the message and respond if necessary.
 * - When a client disconnects, the onClose method is called, allowing the
 * server to clean
 * up any resources or perform logging.
 * - The sendMessage method can be used by the server to send messages to
 * clients at any
 * time, for example, to broadcast updates or notifications.
 */
// @Component
@Service
public class WebSocketServer extends TextWebSocketHandler {

    private TokenService tokenService;
    private InterfaceUserRepository _userRepository;

    // assumption messages as aformat of:"targetUsername:message"

    private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>(); // registered user ->
                                                                                             // <username,session>,
                                                                                             // guest -> <token,session>
    private static final Map<String, Queue<String>> messageQueues = new ConcurrentHashMap<>(); // <username,
                                                                                               // messageQueue>
    private static final Map<String, List<String>> allMessages = new ConcurrentHashMap<>(); // <username, list of all
                                                                                            // messages>
    private static Logger logger = Logger.getLogger(WebSocketServer.class.getName());

    @Autowired
    // Private constructor to prevent instantiation
    private WebSocketServer(TokenService tokenService, DbUserRepository dbUserRepository) {
        // Initialization code
        this.tokenService = tokenService;
        this._userRepository = dbUserRepository;
    }

    // set the repositories to be used test time
    public void setWebSocketServerFacadeRepositories(InterfaceUserRepository userRepository) {
        this._userRepository = userRepository;
    }

    public WebSocketServer() {
    }

    /**
     * Handles a new WebSocket connection after it has been established.
     * This method is called after the connection is opened.
     *
     * @param session The WebSocket session for the newly opened connection.
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        URI uri = session.getUri();
        String query = uri.getQuery();
        String token = null;

        // Log the URI and query parameters
        logger.info("Connection established with URI: " + uri);
        logger.info("Query parameters: " + query);

        if (query != null) {
            for (String param : query.split("&")) {
                if (param.startsWith("token=")) {
                    token = param.split("token=")[1];
                    // Log the extracted token
                    logger.info("Extracted token: " + token);
                }
            }
        }
        if (token == null || !validateToken(token)) {
            session.close(CloseStatus.BAD_DATA);
            System.out.println("Invalid token, connection closed");
            logger.warning("Invalid token, connection closed");
            return;
        }

        String username = tokenService.extractUsername(token);
        String clientKey = (username != null) ? username : "guest-" + token;
        // String clientKey = "guest-" + token;

        // Log the extracted username or guest key
        logger.info("Extracted username: " + username);
        logger.info("Client key: " + clientKey);

        if (username != null && tokenService.isUserAndLoggedIn(token)) {
            // User is logged in
            sessions.put(clientKey, session);
            System.out.println("Connected: " + clientKey);
            logger.info("Connected: " + clientKey);
        } else {
            // User is a guest
            sessions.put(clientKey, session);
            System.out.println("Connected: " + clientKey);
            logger.info("Connected: " + clientKey);
        }
    }

    /**
     * Checks for any queued messages for the specified user and sends
     *  them to the client if they exist.
     * also check that if the current all msessages is empty
     * then it might be because it is a new initialization
     * of the server so need to retive all user messages from the DB
     * 
     * @param username the username of the client to check for queued messages
     */    @Transactional
    public void checkForQueuedMessages(String username) {
        WebSocketSession session = sessions.get(username);
        if (session != null && session.isOpen()) {
            List<String> lst = allMessages.getOrDefault(username, new ArrayList<>());
            if (lst.isEmpty()) {
                lst = _userRepository.findMessagesByUsername(username);
            }
            // if (!lst.isEmpty()) {
            Queue<String> queue = new LinkedList<>(lst);
            while (!queue.isEmpty()) {
                String message = queue.poll();
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // messageQueues.remove(username);
        }
        // }
    }

    /**
     * Handles the closing of a WebSocket connection.
     * This method is called when the connection with a client is closed.
     *
     * @param session The WebSocket session that was closed.
     * @param status  The status code for the closure.
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.values().remove(session);
        System.out.println("Disconnected: " + session.getId());
        // Log the connection closure
        logger.info("Connection closed with status: " + status);
    }

    /**
     * Handles a text message received from a client.
     * This method is called when a new text message arrives.
     *
     * @param session The WebSocket session from which the message was received.
     * @param message The text message received.
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Log the received message
        logger.info("Received message: " + message.getPayload());

        // Assuming the message format is "targetUsername:message"
        String[] parts = message.getPayload().split(":", 2);
        if (parts.length == 2) {
            String targetUsername = parts[0];
            String msg = "New notification :" + targetUsername + parts[1];

            // Add message to the user's all messages list
            allMessages.computeIfAbsent(targetUsername, k -> new ArrayList<>()).add(msg);

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

    /**
     * Broadcasts a text message to all connected users.
     *
     * @param message The message to be broadcasted.
     * @throws IOException If an I/O error occurs while sending the message.
     */
    public void broadcastMessage(String message) {
        // Log the message being sent to all connected users
        logger.info("Sending broadcast message: " + message);
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

    /**
     * Sends a text message to a specific user.
     * This method looks up the session for the username and sends the message if
     * the session exists.
     * if it is a registered user but not logged in than the message will be queued.
     * also check that if the current all msessages is empty
     * then it might be because it is a new initialization
     * of the server so need to retive all user messages from the DB
     * 
     * @param username The username of the recipient.
     * @param message  The message to be sent.
     * @throws IOException If an I/O error occurs while sending the message.
     */
    @Transactional
    public void sendMessage(String targetUser, String message) {
        WebSocketSession session = sessions.get(targetUser);
        if (!allMessages.containsKey(targetUser)) {
            // might be because first start of the server- need to check in DB id a user is
         
            List<String> previousMessages = _userRepository.findMessagesByUsername(targetUser);
            allMessages.put(targetUser, new ArrayList<>(previousMessages));

        }
        allMessages.computeIfAbsent(targetUser, k -> new ArrayList<>()).add(message);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
                System.out.println("Sent message to: " + targetUser);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Queue message for later delivery
            messageQueues.computeIfAbsent(targetUser, k -> new ConcurrentLinkedQueue<>()).add(message);
            System.out.println("Client not found or not open, message queued for : " + targetUser);
        }
    }


    /**
     * Replaces a guest user's token with a logged-in user's new token.
     * This method is called when a guest user logs in and their token is replaced
     * with a new token.
     * 
     * @param oldToken The old token of the guest user.
     * @param newToken The new token for the logged-in user.
     * @param username The username of the logged-in user.
     */
    public void replaceGuestTokenToUserToken(String oldToken, String newToken, String username) {
        if (username != null && oldToken != null) {
            String guestKey = "guest-" + oldToken;
            if (sessions.containsKey(guestKey)) {
                WebSocketSession session = sessions.get(guestKey);
                sessions.remove(guestKey);
                if (session != null) {
                    sessions.put(username, session);
                }
            }
        }
        checkForQueuedMessages(username);
    }

    public void changeLoggedInSession(String userName, String guestToken) {
        WebSocketSession session = sessions.get(userName);
        if (session != null) {
            try {
                sessions.remove(userName);
                String guestKey = "guest-" + guestToken;
                sessions.put(guestKey, session);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * function to retrieve the previous messages for a exist user who is logged
     * after leaving the system.
     * 
     * @param targetUser
     */
    public void retrivePreviousMessages(String targetUser) {
        WebSocketSession session = sessions.get(targetUser);
        if (session != null && session.isOpen()) {
            List<String> lst = allMessages.getOrDefault(targetUser, new ArrayList<>());
            Queue<String> queue = new LinkedList<>(lst);
            while (!queue.isEmpty()) {
                String message = queue.poll();
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void addGuestSession(String guestToken, WebSocketSession session) throws IOException {
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
        // String clientKey = "guest-" + token;

        if (username != null && tokenService.isUserAndLoggedIn(token)) {
            // User is logged in
            sessions.put(clientKey, session);
            System.out.println("Connected: " + clientKey);
        } else {
            // User is a guest
            sessions.put(clientKey, session);
            System.out.println("Connected: " + clientKey);
        }
    }

     /**
     * Adds a guest session to the WebSocket server.
     *
     * @param guestToken the guest token
     * @param session the WebSocket session
     * @throws IOException if an I/O error occurs
     */
    public void addGuestSessionfromToken(String guestToken, WebSocketSession session) throws IOException {
        String token = extractTokenFromQuery(session.getUri().getQuery());

        if (token == null || !validateToken(token)) {
            closeSession(session, CloseStatus.BAD_DATA, "Invalid token, connection closed");
            return;
        }

        String username = tokenService.extractUsername(token);
        String clientKey = (username != null) ? username : "guest-" + token;

        sessions.put(clientKey, session);
        logConnection(clientKey);
    }

    /**
     * Extracts the token from the query string.
     *
     * @param query the query string
     * @return the extracted token, or null if not found
     */
    private String extractTokenFromQuery(String query) {
        if (query != null) {
            for (String param : query.split("&")) {
                if (param.startsWith("token=")) {
                    return param.split("token=")[1];
                }
            }
        }
        return null;
    }

    /**
     * Validates the token.
     *
     * @param token the token to validate
     * @return true if the token is valid, false otherwise
     */
    private boolean validateToken(String token) {
        // Implement your token validation logic here
        return tokenService.validateToken(token);
    }

    /**
     * Closes the session with a specific status and logs the reason.
     *
     * @param session the WebSocket session
     * @param status the close status
     * @param reason the reason for closing the session
     * @throws IOException if an I/O error occurs
     */
    private void closeSession(WebSocketSession session, CloseStatus status, String reason) throws IOException {
        session.close(status);
        logger.info(reason);
    }

    /**
     * Logs the connection details.
     *
     * @param clientKey the client key
     */
    private void logConnection(String clientKey) {
        logger.info("Connected:" + clientKey);
    }

}
