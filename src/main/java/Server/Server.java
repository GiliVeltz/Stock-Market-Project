package Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.GetMapping;
import Server.Configuration.LoggerConfig;

@SpringBootApplication
@ComponentScan({ "Server", "Server.notification", "ServiceLayer", "Domain.Facades", "Domain.Repositories", "Domain.ExternalServices", "Domain.Authenticators"})
@EnableJpaRepositories("Domain.Repositories")
@EntityScan("Domain.Entities")
public class Server {

    public static void main(String[] args) {
        new LoggerConfig();
        SpringApplication.run(Server.class, args);
    }

    @GetMapping
    public String homepage() {
        return "This is Stock Market homepage";
    }

}
