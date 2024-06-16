package UI.View;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import UI.Presenter.UserMainPagePresenter;

@PageTitle("User Main Page")
@Route(value = "user")
public class UserMainPageView extends VerticalLayout implements ViewPageI{

    private UserMainPagePresenter presenter;
    
    private String _username;

    public UserMainPageView(){
        // Retrieve the username from the session
        _username = (String) VaadinSession.getCurrent().getAttribute("username");

        // Create welcome message
        H1 welcomeMessage = new H1("Welcome, " + _username + "!");

        // Create the header component
        Header header = new LoggedInHeader("8080");

        // Create buttons
        Button profileButton = new Button("My Profile", e -> navigateToProfile());
        Button shopsButton = new Button("View My Shops", e -> navigateToShops());
        

        // Apply CSS class to buttons
        profileButton.addClassName("same-size-button");
        shopsButton.addClassName("same-size-button");

        // Create vertical layout for buttons
        VerticalLayout buttonLayout = new VerticalLayout(profileButton, shopsButton);
        buttonLayout.setAlignItems(Alignment.END);

        // Create a horizontal layout for the title to center it
        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setWidthFull(); // Make the layout take full width
        titleLayout.setJustifyContentMode(JustifyContentMode.CENTER); // Center the content
        titleLayout.add(welcomeMessage);

        // Add components to the vertical layout
        add(header, titleLayout, buttonLayout);

        // Initialize presenter
        presenter = new UserMainPagePresenter(this);
    }
    

    private void navigateToShops() {
        getUI().ifPresent(ui -> ui.navigate("user_shops"));
    }

    private void navigateToProfile() {
        getUI().ifPresent(ui -> ui.navigate("profile"));
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
