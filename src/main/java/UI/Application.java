package UI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.UIInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.theme.Theme;

import jakarta.annotation.PostConstruct;

import java.net.ServerSocket;
import java.util.prefs.Preferences;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
public class Application implements AppShellConfigurator, WebMvcConfigurer {

    @Autowired
    private WebSocketClient webSocketClient;

    private static int port;
    // Static variable to store the token
    public static String token;

    public static void main(String[] args) {
        port = findAvailablePort();
        System.setProperty("server.port", String.valueOf(port));
        // System.out.println("Server port: " + port);
        SpringApplication.run(Application.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {

        String token = fetchAndStoreToken("8080");
        System.out.println("Token:" + token);
        webSocketClient.connect(token);
    }

    private static String fetchAndStoreToken(String port) {
        RestTemplate restTemplate = new RestTemplate();
        String serverUrl = "http://localhost:" + port + "/api/system/enterSystem";

        ResponseEntity<String> response = restTemplate.getForEntity(serverUrl, String.class);

        String responseBody = response.getBody();
        // Parse the response to extract the token
        ObjectMapper objectMapper = new ObjectMapper();
        String token = null;
        try {
            JsonNode responseJson = objectMapper.readTree(responseBody);
            token = responseJson.get("returnValue").asText();

            // Store the token in local storage
            Preferences prefs = Preferences.userNodeForPackage(Application.class);
            prefs.put("token", token);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }

    private static int findAvailablePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (Exception e) {
            throw new RuntimeException("Failed to find an available port", e);
        }
    }

    // @Bean
    // public VaadinServiceInitListener vaadinServiceInitListener() {
    // // Your existing code
    // }

    // @SpringBootApplication
    // @Theme(value = "flowcrmtutorial")
    // @ComponentScan(basePackages = { "UI" })
    // public class Application extends SpringBootServletInitializer implements
    // AppShellConfigurator {
    // @Autowired
    // private WebSocketClient webSocketClient;
    // // private String token;

    // // TODO: Maybe use the same port for both server and client
    // public static void main(String[] args) {
    // int port = findAvailablePort(); // dynamic allocation of port (enable
    // multiple instances of the application to
    // // run on the same machine without port conflicts)
    // System.setProperty("server.port", String.valueOf(port));
    // System.out.println("Server port: " + port);
    // SpringApplication.run(Application.class, args);

    // }

    // private static int findAvailablePort() {
    // try (ServerSocket socket = new ServerSocket(0)) {
    // return socket.getLocalPort();
    // } catch (Exception e) {
    // throw new RuntimeException("Failed to find an available port", e);
    // }
    // }

    // @Bean
    // public VaadinServiceInitListener vaadinServiceInitListener() {

    // // return new VaadinServiceInitListener() {
    // // @Override
    // // public void serviceInit(ServiceInitEvent event) {
    // // // event.getSource().addUIInitListener(uiEvent -> {
    // // // UI ui = uiEvent.getUI();
    // // // ui.access(() -> {
    // // // ui.getPage().executeJs("return localStorage.getItem('authToken');")
    // // // .then(String.class, token -> {
    // // // if (token != null && !token.isEmpty()) {
    // // // webSocketClient.connect(token);
    // // // System.out.println("Token retrieved and passed to WebSocket client: " +
    // token);
    // // // } else {
    // // // System.out.println("Token not found or empty");
    // // // }
    // // // });
    // // // });
    // // // });
    // // webSocketClient.connect("GENERIC_TOKEN");
    // // }
    // // };

    // }

}
