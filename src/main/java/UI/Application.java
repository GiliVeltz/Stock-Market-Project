package UI;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.theme.Theme;

import jakarta.annotation.PostConstruct;

import java.net.ServerSocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@Theme(value = "flowcrmtutorial")
@ComponentScan(basePackages = { "UI" })
public class Application extends SpringBootServletInitializer implements AppShellConfigurator {
    @Autowired
    private WebSocketClient webSocketClient;
    // private String token;

    // TODO: Maybe use the same port for both server and client
    public static void main(String[] args) {
        int port = findAvailablePort(); // dynamic allocation of port (enable multiple instances of the application to
                                        // run on the same machine without port conflicts)
        System.setProperty("server.port", String.valueOf(port));
        System.out.println("Server port: " + port);
        SpringApplication.run(Application.class, args);
    }

    private static int findAvailablePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (Exception e) {
            throw new RuntimeException("Failed to find an available port", e);
        }
    }

    @Bean
    public VaadinServiceInitListener vaadinServiceInitListener() {
        return new VaadinServiceInitListener() {
            @Override
            public void serviceInit(ServiceInitEvent event) {
                // event.getSource().addUIInitListener(uiEvent -> {
                //     UI ui = uiEvent.getUI();
                //     ui.access(() -> {
                //         ui.getPage().executeJs("return localStorage.getItem('authToken');")
                //                 .then(String.class, token -> {
                //                     if (token != null && !token.isEmpty()) {
                //                         webSocketClient.connect(token);
                //                         System.out.println("Token retrieved and passed to WebSocket client: " + token);
                //                     } else {
                //                         System.out.println("Token not found or empty");
                //                     }
                //                 });
                //     });
                // });
                webSocketClient.connect("GENERIC_TOKEN");
            }
        };
    }


    // // Method to set the token
    // public void setToken(String token) {
    //     this.token = token;
    // }

    // // Connect the WebSocket client
    // @PostConstruct
    // public void init() {
    // UI.getCurrent().getPage().executeJs("return
    // localStorage.getItem('authToken');")
    // .then(String.class, retrievedToken -> {
    // this.token = retrievedToken;
    // });
    // webSocketClient.connect(token);
    // }

    // @PostConstruct
    // public void init() {
    // UI.getCurrent().getPage().executeJs("return
    // localStorage.getItem('authToken');")
    // .then(String.class, retrievedToken -> {
    // if (retrievedToken != null && !retrievedToken.isEmpty()) {
    // webSocketClient.connect(retrievedToken);
    // } else {
    // System.out.println("Token not found or empty");
    // }
    // });
    // }
}
