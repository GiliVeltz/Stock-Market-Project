package Server;

import java.util.Date;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.GetMapping;

import Domain.Customer;
import Domain.User;
import Domain.Repositories.DbUserRepository;

@SpringBootApplication
@ComponentScan({ "Server", "ServiceLayer", "Domain.Facades", "Domain.Repositories", "Domain.ExternalServices"})
@EnableJpaRepositories("Domain.Repositories")
@EntityScan("Domain")
public class Server {

    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
    }

    @GetMapping
    public String homepage() {
        return "This is Stock Market homepage";
    }

    @Bean
    public CommandLineRunner demo(DbUserRepository repository) {
    return (args) -> {
      // save a few customers
      repository.save(new Customer("Amit", "Lewinz"));
    };
  }

}
