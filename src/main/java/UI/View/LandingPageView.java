package UI.View;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;

@PageTitle("Landing Page")
@Route(value = "")
@RouteAlias(value = "")
public class LandingPageView extends BaseView{

    
    public LandingPageView() {
        System.setProperty("server.port", "8080");

        // Create the header component
        Header header = new Header("8080");

        // Create the title
        H1 title = new H1("Welcome to Stock Market!!");

        // Create a horizontal layout for the title to center it
        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setWidthFull(); // Make the layout take full width
        titleLayout.setJustifyContentMode(JustifyContentMode.CENTER); // Center the content
        titleLayout.add(title);

        Button gotToUser = new Button("Go to User Page", e -> navigateToUserMainPage());
        // Add components to the vertical layout
        add(header, titleLayout, gotToUser);
    }

    private void navigateToUserMainPage() {
        VaadinSession.getCurrent().setAttribute("username", "User");
        getUI().ifPresent(ui -> ui.navigate("user"));
    }
}


