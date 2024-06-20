package Server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RestController
@ComponentScan({ "Server", "ServiceLayer" })
// Purpose: This class is building the web server, that will handle the requests
// from the client.
public class Server {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Server.class);
        app.run(args);
    }

    @GetMapping
    public String homepage() {
        return "This is Stock Market homepage";
    }
}
