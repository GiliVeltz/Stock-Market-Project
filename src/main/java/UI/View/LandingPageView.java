package UI.View;
import UI.Presenter.HeaderPresenter;
import UI.Presenter.LandingPagePresenter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import io.jsonwebtoken.io.IOException;

@PageTitle("Landing Page")
@Route(value = "")
@RouteAlias(value = "")
public class LandingPageView extends VerticalLayout implements ViewPageI {

    
    private LandingPagePresenter presenter;

    public LandingPageView() {
        // Create the header component
        Header header = new Header("8081");

        // Create the title
        H1 title = new H1("Welcome to Stock Market!!");

        // Create a horizontal layout for the title to center it
        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setWidthFull(); // Make the layout take full width
        titleLayout.setJustifyContentMode(JustifyContentMode.CENTER); // Center the content
        titleLayout.add(title);

        // Add components to the vertical layout
        add(header, titleLayout);

        // Initialize presenter
        presenter = new LandingPagePresenter(this);
        
        // Send the enterSystem request
        presenter.sendEnterSystemRequest();
    }


    @Override
    public void showSuccessMessage(String message) {
        Notification.show(message);
    }

    @Override
    public void showErrorMessage(String message) {
        Notification.show(message);
    }
}


