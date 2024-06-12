package UI;

import com.vaadin.flow.component.page.AppShellConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import UI.Presenter.LandingPagePresenter;
import UI.View.LandingPageView;

@SpringBootApplication
@ComponentScan(basePackages = { "UI" })
public class Application extends SpringBootServletInitializer implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public LandingPageView landingPageView() {
        return new LandingPageView();
    }

    @Bean
    public LandingPagePresenter landingPagePresenter(LandingPageView landingPageView, WebSocketClient webSocketClient) {
        return new LandingPagePresenter(landingPageView, webSocketClient);
    }
}
