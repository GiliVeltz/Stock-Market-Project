package Server;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.GetMapping;

import Domain.Entities.User;
import Domain.Repositories.DbUserRepository;

@SpringBootApplication
@ComponentScan({ "Server", "ServiceLayer", "Domain.Facades", "Domain.Repositories", "Domain.ExternalServices"})
@EnableJpaRepositories("Domain.Repositories")
@EntityScan("Domain.Entities")
public class Server {

    @Autowired
    private DbUserRepository repository;

    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
    }

    @GetMapping
    public String homepage() {
        return "This is Stock Market homepage";
    }

    @Bean
    public CommandLineRunner demo() {
    return (args) -> {
      // save a few customers
      // repository.save(new User("Or", "Aa123456!", "or@gmail.com", new Date()));
    };
  }

}
