package UI.views;
 
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
 
@PageTitle("Home Page")
@Route(value = "")
@RouteAlias(value = "")
public class WelcomeView extends VerticalLayout {
 
    public WelcomeView() {
        // Create the buttons
        Button registerButton = new Button("Register");
        Button loginButton = new Button("Login");
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
 
        // Create horizontal layout for buttons
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(registerButton, loginButton);
        
        // Spacer to separate left and right buttons
        Span spacer = new Span();
        spacer.getStyle().set("flex-grow", "1"); // This will make the spacer flexible and push the buttons apart
 
        buttonLayout.add(spacer, searchProductsButton, searchShopsButton, profileButton, shoppingCartButton);
        
        // Adjust button spacing if needed
        buttonLayout.setSpacing(true);
        buttonLayout.setWidthFull(); // Make the layout take full width
        buttonLayout.setAlignItems(Alignment.CENTER); // Center the buttons vertically
 
        // Create the title
        H1 title = new H1("Welcome to Stock Market!!");
        
        // Create a horizontal layout for the title to center it
        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setWidthFull(); // Make the layout take full width
        titleLayout.setJustifyContentMode(JustifyContentMode.CENTER); // Center the content
        titleLayout.add(title);
 
        // Add components to the vertical layout
        add(buttonLayout, titleLayout);
 
        // Create registration dialog
        Dialog registrationDialog = createRegistrationDialog();

        // Create login dialog
        Dialog loginDialog = createLoginDialog();

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
 
            // Here you can add your logic to handle the registration
            System.out.println("Username: " + username);
            System.out.println("Email: " + email);
            System.out.println("Password: " + password);
 
            // Close the dialog after submission
            dialog.close();
        });
 
        Button cancelButton = new Button("Cancel", event -> dialog.close());
 
        // Create button layout
        HorizontalLayout buttonLayout = new HorizontalLayout(submitButton, cancelButton);
 
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

            // Close the dialog after submission
            dialog.close();
        });

        Button cancelButton = new Button("Cancel", event -> dialog.close());

        // Create button layout
        HorizontalLayout buttonLayout = new HorizontalLayout(submitButton, cancelButton);

        // Add form layout and button layout to the dialog
        VerticalLayout dialogLayout = new VerticalLayout(formLayout, buttonLayout);
        dialog.add(dialogLayout);

        return dialog;
    }

    
}
 