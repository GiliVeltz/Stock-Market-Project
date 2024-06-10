package UI.View;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

import UI.Presenter.HeaderPresenter;

public class Header extends HorizontalLayout implements ViewPageI{

    private Button loginButton;
    private final HeaderPresenter presenter;

    public Header(String serverPort) {

        // Initialize the presenter
        presenter = new HeaderPresenter(this, serverPort);

        // Create the buttons
        Button registerButton = new Button("Register");
        loginButton = new Button("Login");
        Button searchProductsButton = new Button("Search Products");
        Button searchShopsButton = new Button("Search Shops");
        Button profileButton = new Button("My Profile");
        Button shoppingCartButton = new Button("Shopping Cart");

        // Add cursor styling
        registerButton.addClassName("pointer-cursor");
        loginButton.addClassName("pointer-cursor");
        searchProductsButton.addClassName("pointer-cursor");
        searchShopsButton.addClassName("pointer-cursor");
        profileButton.addClassName("pointer-cursor");
        shoppingCartButton.addClassName("pointer-cursor");

        // Create horizontal layout for left buttons
        HorizontalLayout leftButtonLayout = new HorizontalLayout();
        leftButtonLayout.add(registerButton, loginButton);

        // Spacer to separate left and right buttons
        Span spacer = new Span();
        spacer.getStyle().set("flex-grow", "1"); // This will make the spacer flexible and push the buttons apart

        // Create horizontal layout for right buttons
        HorizontalLayout rightButtonLayout = new HorizontalLayout();
        rightButtonLayout.add(searchProductsButton, searchShopsButton, profileButton, shoppingCartButton);

        // Add left buttons, spacer, and right buttons to the main layout
        add(leftButtonLayout, spacer, rightButtonLayout);

        // Adjust button spacing if needed
        setWidthFull(); // Make the layout take full width
        setAlignItems(Alignment.CENTER); // Center the buttons vertically

        // Create registration dialog
        Dialog registrationDialog = createRegistrationDialog();

        // Create login dialog
        Dialog loginDialog = createLoginDialog();

        // Create logout confirmation dialog
        Dialog logoutConfirmationDialog = createLogoutConfirmationDialog();

        // Add click listener to the login button
        loginButton.addClickListener(event -> loginDialog.open());

        // Add click listener to the register button
        registerButton.addClickListener(event -> registrationDialog.open());
    }

    private Dialog createRegistrationDialog() {
        Dialog dialog = new Dialog();

        // Create form layout
        FormLayout formLayout = new FormLayout();

        // Create form fields
        TextField usernameField = new TextField("Username");
        TextField emailField = new TextField("Email");
        PasswordField passwordField = new PasswordField("Password");

        // Add fields to the form layout
        formLayout.add(usernameField, emailField, passwordField);

        // Create buttons
        Button submitButton = new Button("Submit", event -> {
            // Handle form submission
            String username = usernameField.getValue();
            String email = emailField.getValue();
            String password = passwordField.getValue();

            presenter.registerUser(username, email, password);

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
        VerticalLayout dialogLayout = new VerticalLayout(formLayout, buttonLayout);
        dialog.add(dialogLayout);

        return dialog;
    }

    private Dialog createLoginDialog() {
        Dialog dialog = new Dialog();

        // Create form layout
        FormLayout formLayout = new FormLayout();

        // Create form fields
        TextField usernameField = new TextField("Username");
        PasswordField passwordField = new PasswordField("Password");

        // Add fields to the form layout
        formLayout.add(usernameField, passwordField);

        // Create buttons
        Button submitButton = new Button("Submit", event -> {
            // Handle form submission
            String username = usernameField.getValue();
            String password = passwordField.getValue();

            presenter.loginUser(username, password);

            // Close the dialog after submission
            dialog.close();
        });

        Button cancelButton = new Button("Cancel", event -> dialog.close());

        // Create button layout
        HorizontalLayout buttonLayout = new HorizontalLayout(submitButton, cancelButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER); // Center the buttons

        // Add form layout and button layout to the dialog
        VerticalLayout dialogLayout = new VerticalLayout(formLayout, buttonLayout);
        dialog.add(dialogLayout);

        return dialog;
    }

    private Dialog createLogoutConfirmationDialog() {
        Dialog dialog = new Dialog();

        // Create confirmation message
        Span message = new Span("Are you sure you want to logout?");

        // Create buttons
        Button confirmButton = new Button("Yes", event -> {
            // Handle logout confirmation
            presenter.logoutUser();

            // Close the dialog after confirmation
            dialog.close();
        });

        confirmButton.addClassName("pointer-cursor");

        Button cancelButton = new Button("No", event -> dialog.close());

        cancelButton.addClassName("pointer-cursor");

        // Create button layout
        HorizontalLayout buttonLayout = new HorizontalLayout(confirmButton, cancelButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER); // Center the buttons

        // Add confirmation message and button layout to the dialog
        VerticalLayout dialogLayout = new VerticalLayout(message, buttonLayout);
        dialog.add(dialogLayout);

        return dialog;
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
