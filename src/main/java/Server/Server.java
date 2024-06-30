package Server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;

import Domain.Repositories.InterfaceShoppingCartRepository;
import Domain.Repositories.MemoryShoppingCartRepository;

@SpringBootApplication
@ComponentScan({ "Server", "ServiceLayer", "Domain.Facades", "Domain.Repositories", "Domain.ExternalServices" })
// Purpose: This class is building the web server, that will handle the requests
// from the client.
public class Server {

    public static void main(String[] args) {
        InterfaceShoppingCartRepository shoppingCartRepository = new MemoryShoppingCartRepository();
        SpringApplication.run(Server.class, args);
    }

    @GetMapping
    public String homepage() {
        return "This is Stock Market homepage";
    }
}
