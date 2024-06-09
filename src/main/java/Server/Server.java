package Server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import Server.notifications.WebSocketHandler;

@SpringBootApplication
@RestController
@ComponentScan({ "Server", "ServiceLayer" })
// Purpose: This class is building the web server, that will handle the requests
// from the client.
public class Server {

    private final WebSocketHandler webSocketHandler;
    
    public Server(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
    }

    @GetMapping
    public String homepage() {
        return "This is Stock Market homepage";
    }
}
