package UI.View;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;

import UI.Presenter.LandingPagePresenter;
import UI.Presenter.UserMainPagePresenter;
import UI.Presenter.UserShopsPagePresenter;

@PageTitle("User Shops Page")
@Route(value = "user_shops")
public class UserShopsPageView extends VerticalLayout implements ViewPageI{

    private UserShopsPagePresenter presenter;
    
    
    private String _username;

    public UserShopsPageView(){
        // Retrieve the username from the session
        _username = (String) VaadinSession.getCurrent().getAttribute("username");

        // Create welcome message
        H1 welcomeMessage = new H1("Welcome, " + _username + "!");

        // Create the header component
        Header header = new Header("8080");
        header.hideRegisterButton();

        

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
        presenter = new UserShopsPagePresenter(this);
    }
    

    private void navigateToShops() {
        getUI().ifPresent(ui -> ui.navigate("myshops"));
    }

    private void navigateToProfile() {
        getUI().ifPresent(ui -> ui.navigate("profile"));
    }

    @Override
    public void showSuccessMessage(String message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'showSuccessMessage'");
    }

    @Override
    public void showErrorMessage(String message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'showErrorMessage'");
    }

}

