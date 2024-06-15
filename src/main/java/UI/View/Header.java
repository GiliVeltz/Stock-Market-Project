package UI.View;

import java.time.LocalDate;
import java.util.Date;

import com.vaadin.flow.component.datepicker.DatePicker; 

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.VaadinSession;

import UI.Presenter.HeaderPresenter;

public class Header extends HorizontalLayout implements ViewPageI {

    private Button loginButton;
    private Button _registerButton;
    private Button openShopButton;
    private final HeaderPresenter presenter;
    private HorizontalLayout _leftButtonLayout;
    private Dialog logoutConfirmationDialog;

    public Header(String serverPort) {

        // Initialize the presenter
        presenter = new HeaderPresenter(this, serverPort);

        // Create the buttons
        _registerButton = new Button("Register");
        loginButton = new Button("Login");
        Button searchProductsButton = new Button("Search Products");
        Button searchShopsButton = new Button("Search Shops");
        Button profileButton = new Button("My Profile");
        Button shoppingCartButton = new Button("Shopping Cart");

        // New button for opening a shop
        openShopButton = new Button("Open Shop");
        openShopButton.setVisible(false); // Hide initially
        openShopButton.addClassName("pointer-cursor");
        openShopButton.addClickListener(event -> createOpenNewShopDialog().open());

        // Add cursor styling
        _registerButton.addClassName("pointer-cursor");
        loginButton.addClassName("pointer-cursor");
        searchProductsButton.addClassName("pointer-cursor");
        searchShopsButton.addClassName("pointer-cursor");
        profileButton.addClassName("pointer-cursor");
        shoppingCartButton.addClassName("pointer-cursor");

        // Create horizontal layout for left buttons
        _leftButtonLayout = new HorizontalLayout();
        _leftButtonLayout.add(_registerButton, loginButton);

        // Spacer to separate left and right buttons
        Span spacer = new Span();
        spacer.getStyle().set("flex-grow", "1"); // This will make the spacer flexible and push the buttons apart

        // Create horizontal layout for right buttons
        HorizontalLayout rightButtonLayout = new HorizontalLayout();
        rightButtonLayout.add(openShopButton, searchProductsButton, searchShopsButton, profileButton, shoppingCartButton);

        // Add left buttons, spacer, and right buttons to the main layout
        add(_leftButtonLayout, spacer, rightButtonLayout);

        // Adjust button spacing if needed
        setWidthFull(); // Make the layout take full width
        setAlignItems(Alignment.CENTER); // Center the buttons vertically

        // Create registration dialog
        Dialog registrationDialog = createRegistrationDialog();

        // Create login dialog
        Dialog loginDialog = createLoginDialog();

        // Create logout confirmation dialog
        logoutConfirmationDialog = createLogoutConfirmationDialog();

        // Add click listener to the login button
        loginButton.addClickListener(event -> {
            if (!loginButton.getText().equals("Logout"))
                loginDialog.open();
            else
                logoutConfirmationDialog.open();
        });

        // Add click listener to the register button
        _registerButton.addClickListener(event -> registrationDialog.open());
    }

    private Dialog createRegistrationDialog() {
        Dialog dialog = new Dialog();

        // Create form layout
        FormLayout formLayout = new FormLayout();

        // Create a headline
        H2 headline = new H2("Registeration");
        headline.getStyle().set("margin", "0");

        // Create form fields
        TextField usernameField = new TextField("Username");
        TextField emailField = new TextField("Email");
        PasswordField passwordField = new PasswordField("Password");
        DatePicker birthdayPicker = new DatePicker("Birthday");

        // Add fields to the form layout
        formLayout.add(usernameField, emailField, passwordField, birthdayPicker);

        // Create buttons
        Button submitButton = new Button("Submit", event -> {
            // Handle form submission
            String username = usernameField.getValue();
            String email = emailField.getValue();
            String password = passwordField.getValue();
            Date birthday = convertToDate(birthdayPicker.getValue()); // Convert LocalDate to Date

            // Validate fields (add your validation logic here)

            presenter.registerUser(username, email, password, birthday);

            // Close the dialog after submission
            dialog.close();
        });

        submitButton.addClassName("pointer-cursor");

        Button cancelButton = new Button("Cancel", event -> {
            // Close the dialog
            dialog.close();
        });

        cancelButton.addClassName("pointer-cursor");

        // Create button layout
        HorizontalLayout buttonLayout = new HorizontalLayout(submitButton, cancelButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER); // Center the buttons

        // Add form layout and button layout to the dialog
        VerticalLayout dialogLayout = new VerticalLayout(headline, formLayout, buttonLayout);
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER); // Center the layout content
        dialog.add(dialogLayout);

        // Add listener to clear fields when dialog is opened
        dialog.addOpenedChangeListener(event -> {
            if (dialog.isOpened()) {
                usernameField.clear();
                emailField.clear();
                passwordField.clear();
                birthdayPicker.clear();
            }
        });

        return dialog;
    }

    // Method to convert LocalDate to Date
    private Date convertToDate(LocalDate localDate) {
        return java.sql.Date.valueOf(localDate);
    }

    private Dialog createLoginDialog() {
        Dialog dialog = new Dialog();

        // Create form layout
        FormLayout formLayout = new FormLayout();

        // Create a headline
        H2 headline = new H2("Log In");
        headline.getStyle().set("margin", "0");

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

        Button cancelButton = new Button("Cancel", event -> {
            // Close the dialog
            dialog.close();
        });

        // Create button layout
        HorizontalLayout buttonLayout = new HorizontalLayout(submitButton, cancelButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER); // Center the buttons

        // Add form layout and button layout to the dialog
        VerticalLayout dialogLayout = new VerticalLayout(headline, formLayout, buttonLayout);
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER); // Center the layout content
        dialog.add(dialogLayout);

        // Add listener to clear fields when dialog is opened
        dialog.addOpenedChangeListener(event -> {
            if (dialog.isOpened()) {
                usernameField.clear();
                passwordField.clear();
            }
        });

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


    public void hideRegisterButton(){
        _registerButton.setVisible(false);
    }

    public void createBackToMainButton() {
        Button backToMainButton = new Button("Back to Main Page", event -> {
            VaadinSession.getCurrent().setAttribute("username", "User");
            getUI().ifPresent(ui -> ui.navigate("user"));
        });

        backToMainButton.addClassName("pointer-cursor");
        _leftButtonLayout.remove(_registerButton);
        _leftButtonLayout.add(backToMainButton);
    }

    public void createLogoutButton() {
        Button logoutButton = new Button("Logout", event -> logoutConfirmationDialog.open());

        logoutButton.addClassName("pointer-cursor");
        _leftButtonLayout.remove(loginButton);
        _leftButtonLayout.add(logoutButton);
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

    @Override
    public void showSuccessMessage(String message) {
        Notification.show(message);

        // Show the open shop button on successful login
        if ("Login successful".equals(message)) {
            openShopButton.setVisible(true);
        }
    }

    @Override
    public void showErrorMessage(String message) {
        Notification.show(message);
    }

    public void switchToLogout() {
        loginButton.setText("Logout");
    }

    public void switchToLogin() {
        loginButton.setText("Login");
    }
}
