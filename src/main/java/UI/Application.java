package UI;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.theme.Theme;

import jakarta.annotation.PostConstruct;

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
    private String token;

    // TODO: Maybe use the same port for both server and client
    public static void main(String[] args) {
        System.setProperty("server.port", "8081");
        SpringApplication.run(Application.class, args);

    }

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
    //     UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
    //             .then(String.class, retrievedToken -> {
    //                 if (retrievedToken != null && !retrievedToken.isEmpty()) {
    //                     webSocketClient.connect(retrievedToken);
    //                 } else {
    //                     System.out.println("Token not found or empty");
    //                 }
    //             });
    // }
     @Bean
    public VaadinServiceInitListener vaadinServiceInitListener() {
        return new VaadinServiceInitListener() {
            @Override
            public void serviceInit(ServiceInitEvent event) {
                event.getSource().addUIInitListener(uiEvent -> {
                    UI ui = uiEvent.getUI();
                    ui.access(() -> {
                        ui.getPage().executeJs("return localStorage.getItem('authToken');")
                                .then(String.class, token -> {
                                    if (token != null && !token.isEmpty()) {
                                        webSocketClient.connect(token);
                                    } else {
                                        System.out.println("Token not found or empty");
                                    }
                                });
                    });
                });
            }
        };
    }

}
