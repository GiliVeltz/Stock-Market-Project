package Server.notifications;

import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import ch.qos.logback.classic.Logger;

/**
 * Configures WebSocket communication within the application.
 * This class is annotated with @Configuration to indicate that it is a source of bean definitions.
 * The @EnableWebSocket annotation declares that WebSocket support should be enabled.
 * It implements WebSocketConfigurer, allowing for custom configuration of WebSocket connections.
 * 
 * The primary role of this class is to set up the WebSocket infrastructure for the application.
 * It defines how WebSocket connections are handled, specifying the endpoint and configuring cross-origin requests.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    // The WebSocketServer instance that will handle WebSocket communication.
    private final WebSocketServer webSocketServer; 


    /**
     * Constructs a new WebSocketConfig with a specified WebSocketServer.
     * This constructor injects a WebSocketServer instance, which is used to handle WebSocket messages.
     *
     * @param webSocketServer the WebSocketServer to be used for handling WebSocket messages.
     */
    public WebSocketConfig(WebSocketServer webSocketServer) {
        this.webSocketServer = webSocketServer;
    }

    /**
     * Registers WebSocket handlers to manage WebSocket connections.
     * This method configures the WebSocket endpoint and sets the allowed origins to "*",
     * which means it accepts connections from any domain.
     * This is crucial for enabling the client-side application to communicate with the server via WebSockets.
     * 
     * @param registry the WebSocketHandlerRegistry to register handlers with.
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketServer, "/websocket").setAllowedOrigins("*");
    }

      /**
     * Configures CORS settings for WebSocket connections.
     * This method is used to configure Cross-Origin Resource Sharing (CORS) for WebSocket connections.
     *
     * @param registry the CorsRegistry used to configure CORS settings
     */
    public void addCorsMappingsForWebsocket(CorsRegistry registry) {
        registry.addMapping("/ws/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

     /**
     * Configures message broker settings.
     * This method is used to configure a message broker for handling WebSocket messages.
     *
     * @param config the MessageBrokerRegistry used to configure the message broker
     */
    
    public void configureMessageBrokerForASocket(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

     /**
     * Registers STOMP endpoints.
     * This method is used to register STOMP endpoints for WebSocket connections.
     *
     * @param registry the StompEndpointRegistry used to register STOMP endpoints
     */
    
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp").setAllowedOrigins("*").withSockJS();
    }

    



   
}
