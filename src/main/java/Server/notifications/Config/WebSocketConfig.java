package Server.notifications.Config;

import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


import Server.notifications.WebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    // private final WebSocketHandler webSocketHandler;
    // private final SimpMessagingTemplate messagingTemplate;

    // public WebSocketConfig(WebSocketHandler webSocketHandler, SimpMessagingTemplate messagingTemplate) {
    //     this.webSocketHandler = webSocketHandler;
    //     this.messagingTemplate = messagingTemplate;
    // }

    // @Override
    // public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    //     registry.addHandler(webSocketHandler, "/ws/server").setAllowedOrigins("*");
    // }

    private final WebSocketHandler webSocketHandler;

    public WebSocketConfig(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, "/websocket").setAllowedOrigins("*");
    }
}
