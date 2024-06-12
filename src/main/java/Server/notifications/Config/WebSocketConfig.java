package Server.notifications.Config;

import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


import Server.notifications.WebSocketServer;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    // private final WebSocketHandler webSocketServer;
    // private final SimpMessagingTemplate messagingTemplate;

    // public WebSocketConfig(WebSocketHandler webSocketServer, SimpMessagingTemplate messagingTemplate) {
    //     this.webSocketServer = webSocketServer;
    //     this.messagingTemplate = messagingTemplate;
    // }

    // @Override
    // public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    //     registry.addHandler(webSocketServer, "/ws/server").setAllowedOrigins("*");
    // }

    private final WebSocketServer webSocketServer;

    public WebSocketConfig(WebSocketServer webSocketServer) {
        this.webSocketServer = webSocketServer;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketServer, "/websocket").setAllowedOrigins("*");
    }
}
