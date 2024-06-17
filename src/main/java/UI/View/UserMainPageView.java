package UI.View;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import UI.Presenter.UserMainPagePresenter;

@PageTitle("User Main Page")
@Route(value = "user")
public class UserMainPageView extends BaseView{

    private UserMainPagePresenter presenter;
    
    private String _username;
    private Button _openShopButton;

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
        
        // New button for opening a shop
        _openShopButton = new Button("Open Shop", e -> createOpenNewShopDialog().open());
        _openShopButton.addClassName("pointer-cursor");

        // Apply CSS class to buttons
        profileButton.addClassName("same-size-button");
        shopsButton.addClassName("same-size-button");

        // Create vertical layout for buttons
        VerticalLayout buttonLayout = new VerticalLayout(profileButton, shopsButton, _openShopButton);
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

    private Dialog createOpenNewShopDialog() {
        Dialog dialog = new Dialog();

        // Create a headline
        H2 headline = new H2("Open New Shop");

        // Create form layout
        FormLayout formLayout = new FormLayout();

        // Create form fields
        TextField shopNameField = new TextField("Shop Name");
        TextField bankDetailsField = new TextField("Bank Details");
        TextField shopAddressField = new TextField("Address");

        // Add fields to the form layout
        formLayout.add(shopNameField, bankDetailsField, shopAddressField);

        // Create buttons
        Button submitButton = new Button("Submit", event -> {
            // Handle form submission
            String shopName = shopNameField.getValue();
            String bankDetails = bankDetailsField.getValue();
            String shopAddress = shopAddressField.getValue();

            presenter.openNewShop(shopName, bankDetails, shopAddress);

            // Close the dialog after submission
            dialog.close();
        });

        submitButton.addClassName("pointer-cursor");

        Button cancelButton = new Button("Cancel", event -> dialog.close());

        cancelButton.addClassName("pointer-cursor");

        // Create button layout
        HorizontalLayout buttonLayout = new HorizontalLayout(submitButton, cancelButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER); // Center the buttons

        // Add form layout and button layout to the dialog
        VerticalLayout dialogLayout = new VerticalLayout(headline, formLayout, buttonLayout);
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        dialog.add(dialogLayout);

        return dialog;
    }
    

    private void navigateToShops() {
        getUI().ifPresent(ui -> ui.navigate("user_shops"));
    }

    private void navigateToProfile() {
        getUI().ifPresent(ui -> ui.navigate("profile"));
    }

}
