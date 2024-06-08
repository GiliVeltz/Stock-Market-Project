package UI.views;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

import UI.dto.UserDto;

public class HeaderComponent extends HorizontalLayout {

    private Button loginButton;
    private final String _serverPort;

    public HeaderComponent(String serverPort) {
        //set the server port
        _serverPort = serverPort;
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
        loginButton.addClickListener(event -> {
            if (loginButton.getText().equals("Login")) {
                loginDialog.open();
            } else {
                // Open the logout confirmation dialog
                logoutConfirmationDialog.open();
            }
        });

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

            // Here you can add your logic to handle the registration
            System.out.println("Username: " + username);
            System.out.println("Email: " + email);
            System.out.println("Password: " + password);

            handleRegistration(username, email, password);

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

            // Here you can add your logic to handle the login
            System.out.println("Username: " + username);
            System.out.println("Password: " + password);


            // Simulate a successful login
            handleLogin(username, password);

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
            handleLogout();

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

    private void handleLogin(String username, String password) {
        RestTemplate restTemplate = new RestTemplate();
        UserDto userDto = new UserDto(username, null, password);

        // Assuming you have a method to get the JWT token
        String token = getTokenFromLocalStorage(); 

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        Map<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        params.put("password", password);
        HttpEntity<Map<String,String>> requestEntity = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:"+_serverPort+"/api/user/login",
                HttpMethod.POST,
                requestEntity,
                String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Notification.show("Login successful");
            // Change the login button text to "Logout"
            loginButton.setText("Logout");
            System.out.println(response.getBody());
        } else {
            Notification.show("Login failed");
        }
    }

    private void handleRegistration(String username, String email, String password){
        RestTemplate restTemplate = new RestTemplate();
        UserDto userDto = new UserDto(username, email, password);

        // Assuming you have a method to get the JWT token
        String token = getTokenFromLocalStorage(); 

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<UserDto> requestEntity = new HttpEntity<>(userDto, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:"+_serverPort+"/api/user/register",
                HttpMethod.POST,
                requestEntity,
                String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Notification.show("Registration successful");
            System.out.println(response.getBody());
        } else {
            Notification.show("Registration failed");
        }
    }

    private void handleLogout() {
        // Change the login button text back to "Login"
        loginButton.setText("Logout");
        System.out.println("User logged out");
    }

    private String getTokenFromLocalStorage() {
        // Example method to retrieve JWT token from local storage
        return UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');").toString();
    }
}
