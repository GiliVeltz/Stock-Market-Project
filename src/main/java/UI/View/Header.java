package UI.View;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.VaadinSession;

import Dtos.ProductDto;
import UI.Model.ShopDto;
import UI.Presenter.HeaderPresenter;

public class Header extends HorizontalLayout {

    private Button loginButton;
    private Button _registerButton;
    private final HeaderPresenter presenter;
    private HorizontalLayout _leftButtonLayout;
    private Dialog logoutConfirmationDialog;

    public Header(String serverPort) {

        // Initialize the presenter
        presenter = new HeaderPresenter(this, serverPort);

        // Create an Image component
        Image image = new Image("https://raw.githubusercontent.com/inbarbc/StockMarket_Project/main/shoppingCartSmallIcon.jpg", "Shopping Cart");

        // Set the size of the image
        image.setWidth("100px");
        image.setHeight("100px");

        // Create the buttons
        _registerButton = new Button("Register");
        loginButton = new Button("Login");
        Button searchProductsButton = new Button("Search Products");
        Button searchShopsButton = new Button("Search Shops");
        Button shoppingCartButton = new Button(image);
        // Button messagesButton = new Button("My Messages");
        Button messagesButton = new Button("My Messages", e -> navigateToMessages());



        // Add cursor styling
        _registerButton.addClassName("pointer-cursor");
        loginButton.addClassName("pointer-cursor");
        searchProductsButton.addClassName("pointer-cursor");
        searchShopsButton.addClassName("pointer-cursor");
        shoppingCartButton.addClassName("pointer-cursor");
        messagesButton.addClassName("pointer-cursor");

        // Create horizontal layout for left buttons
        _leftButtonLayout = new HorizontalLayout();
        _leftButtonLayout.add(_registerButton, loginButton);

        // Spacer to separate left and right buttons
        Span spacer = new Span();
        spacer.getStyle().set("flex-grow", "1"); // This will make the spacer flexible and push the buttons apart


        // Create horizontal layout for right buttons
        HorizontalLayout rightButtonLayout = new HorizontalLayout();
        rightButtonLayout.add(searchProductsButton, searchShopsButton, shoppingCartButton);

        // Add left buttons, spacer, and right buttons to the main layout
        add(_leftButtonLayout, spacer, rightButtonLayout);

        // Adjust button spacing if needed
        setWidthFull(); // Make the layout take full width
        setAlignItems(Alignment.CENTER); // Center the buttons vertically

        // Create registration dialog
        Dialog registrationDialog = createRegistrationDialog();

        // Create login dialog
        Dialog loginDialog = createLoginDialog();

        Dialog searchProductsDialog = createSearchProductsDialog();

        // Create login dialog
        Dialog searchShopsDialog = createSearchShopsDialog();
    
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

        searchProductsButton.addClickListener(event -> searchProductsDialog.open());
        searchShopsButton.addClickListener(event -> searchShopsDialog.open());

        shoppingCartButton.addClickListener(event -> {
            getUI().ifPresent(ui -> ui.navigate("user_cart"));
        });
    }


    private void navigateToMessages() {
        getUI().ifPresent(ui -> ui.navigate("user_messages"));
    }

    private Dialog createRegistrationDialog() {
        Dialog dialog = new Dialog();

        // Create form layout
        FormLayout formLayout = new FormLayout();

        // Create a headline
        H2 headline = new H2("Registration");
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

            // Switch the button text back to "Login"
            switchToLogin();

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

    public void hideRegisterButton() {
        _registerButton.setVisible(false);
    }

    public void createBackToMainButton() {
        Button backToMainButton = new Button("Back to Main Page", event -> {
            getUI().ifPresent(ui -> ui.navigate("user"));
        });

        backToMainButton.addClassName("pointer-cursor");
        _leftButtonLayout.remove(_registerButton);
        _leftButtonLayout.add(backToMainButton);
    }

    public void createBackToMainButtonGuest() {
        Button backToMainButton = new Button("Back to Main Page", event -> {
            getUI().ifPresent(ui -> ui.navigate(""));
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


    public void showSuccessMessage(String message) {
        Notification.show(message);
    }

    public void showErrorMessage(String message) {
        Notification.show(message);
    }

    public void switchToLogout() {
        loginButton.setText("Logout");
    }

    public void switchToLogin() {
        loginButton.setText("Login");
    }

    public void navigateToUserMainPage() {
        getUI().ifPresent(ui -> ui.navigate("user"));
    }

    public void navigateToLandingPage() {
        getUI().ifPresent(ui -> ui.navigate(""));
    }

    private Dialog createSearchProductsDialog() {
        Dialog dialog = new Dialog();

        // Create a headline
        H2 headline = new H2("Search Product");
        headline.getStyle().set("margin", "0");

        H5 comment = new H5("An empty search will return all products");

        // Create form layout
        FormLayout formLayout = new FormLayout();

        // Create form fields
        ComboBox<String> categoryField = new ComboBox<>("By Category");
        categoryField.setItems("Electronics", "Books", "Clothing", "Home", "Kitchen", "Sports", "Grocery","Pharmacy");

        // MultiSelectListBox to store and display keywords
        MultiSelectListBox<String> keyWordField = new MultiSelectListBox<>();
        keyWordField.setHeight("100px");
        keyWordField.setWidth("150px");

        TextField keywordInputField = new TextField("Key Words");
        Button addKeywordButton = new Button("Add Keyword", event -> {
            String keyword = keywordInputField.getValue().trim();
            if (!keyword.isEmpty() && !keyWordField.getSelectedItems().contains(keyword)) {
                Set<String> currentKeywords = new LinkedHashSet<>(keyWordField.getSelectedItems());
                currentKeywords.add(keyword);
                keyWordField.setItems(currentKeywords);
                keywordInputField.clear();
            }
        });

        keyWordField.getElement().setAttribute("style", "padding-bottom: 10px;");

        // Create form fields
        TextField minPriceField = new TextField("Minimum Price");
        minPriceField.setPattern("[0-9]+");
        minPriceField.setErrorMessage("Please enter a valid minimum price");

        TextField maxPriceField = new TextField("Maximum Price");
        maxPriceField.setPattern("[0-9]+");
        maxPriceField.setErrorMessage("Please enter a valid maximum price");


        TextField productNameField = new TextField("By Product Name");

        // Add value change listeners to update field states
         ValueChangeListener<ValueChangeEvent<?>> listener = event -> updateFieldStates(categoryField, keyWordField, keywordInputField, addKeywordButton, minPriceField, maxPriceField, productNameField);

        categoryField.addValueChangeListener(listener);
        keyWordField.addValueChangeListener(listener);
        minPriceField.addValueChangeListener(listener);
        maxPriceField.addValueChangeListener(listener);
        productNameField.addValueChangeListener(listener);


        // Add fields to the form layout
        formLayout.add(minPriceField, maxPriceField, categoryField, productNameField, keywordInputField, keyWordField, addKeywordButton);

        // Create buttons
        Button searchButton = new Button("Search", event -> {
            // Handle form submission
            String category = categoryField.getValue();
            Set<String> keyWords = new HashSet<>(keyWordField.getSelectedItems()); // Get selected keywords
            String minPrice = minPriceField.getValue();
            String maxPrice = maxPriceField.getValue();
            String productName = productNameField.getValue();

            // Convert empty values to null
            if (category.isEmpty()) {
                category = null;
            }
            if (keyWords.isEmpty()) {
                keyWords = null;
            }
            if (minPrice.isEmpty()) {
                minPrice = null;
            }
            if (maxPrice.isEmpty()) {
                maxPrice = null;
            }
            if (productName.isEmpty()) {
                productName = null;
            }

            presenter.SearchProducts(category, keyWords, minPrice, maxPrice, productName);

            // Close the dialog after submission
            dialog.close();
        });

        searchButton.addClassName("pointer-cursor");

        Button cancelButton = new Button("Cancel", event -> dialog.close());
        cancelButton.addClassName("pointer-cursor");

         // Create refresh button
        Button refreshButton = new Button("Refresh", event -> resetFields(categoryField, keyWordField, keywordInputField, minPriceField, maxPriceField, productNameField));
        refreshButton.addClassName("pointer-cursor");

        // Create button layout
        HorizontalLayout buttonLayout = new HorizontalLayout(searchButton, cancelButton, refreshButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER); // Center the buttons

        // Add form layout and button layout to the dialog
        VerticalLayout dialogLayout = new VerticalLayout(headline, comment, formLayout, buttonLayout);
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        dialog.add(dialogLayout);

        return dialog;
    }


    private void updateFieldStates(ComboBox<String> categoryField, MultiSelectListBox<String> keyWordField, TextField keywordInputField, Button addKeywordButton, TextField minPriceField, TextField maxPriceField, TextField productNameField) {
        boolean anyFieldFilled = !categoryField.isEmpty() || !keyWordField.isEmpty() || !minPriceField.isEmpty() || !maxPriceField.isEmpty() || !productNameField.isEmpty();
        boolean priceFieldFilled = !minPriceField.isEmpty() || !maxPriceField.isEmpty();

        categoryField.setEnabled(!anyFieldFilled || !categoryField.isEmpty());
        keyWordField.setEnabled(!anyFieldFilled || !keyWordField.isEmpty());
        keywordInputField.setEnabled(!anyFieldFilled || !keywordInputField.isEmpty());
        addKeywordButton.setEnabled(!anyFieldFilled || !keywordInputField.isEmpty());
        minPriceField.setEnabled(!anyFieldFilled || priceFieldFilled);
        maxPriceField.setEnabled(!anyFieldFilled || priceFieldFilled);
        productNameField.setEnabled(!anyFieldFilled || !productNameField.isEmpty());
    }

    private void resetFields(ComboBox<String> categoryField, MultiSelectListBox<String> keyWordField, TextField keywordInputField, TextField minPriceField, TextField maxPriceField, TextField productNameField) {
        categoryField.clear();
        keyWordField.clear();
        keywordInputField.clear();
        minPriceField.clear();
        maxPriceField.clear();
        productNameField.clear();
        updateFieldStates(categoryField, keyWordField, keywordInputField, null, minPriceField, maxPriceField, productNameField);
    }

    public Dialog createSearchShopsDialog(){
        Dialog dialog = new Dialog();

        // Create form layout
        FormLayout formLayout = new FormLayout();

        // Create a headline
        H2 headline = new H2("Search Shops");
        headline.getStyle().set("margin", "0");
        H5 comment = new H5("An empty search will return all shops");

        // Create form fields
        TextField shopNameField = new TextField("Shop Name");
        TextField shopIdField = new TextField("Shop ID");
        shopIdField.setPattern("[0-9]+");
        shopIdField.setErrorMessage("Please enter a valid shop ID");

        // Add value change listeners to ensure only one filter is active
        shopNameField.addValueChangeListener(event -> updateFieldStates(shopNameField, shopIdField));
        shopIdField.addValueChangeListener(event -> updateFieldStates(shopNameField, shopIdField));

        // Add fields to the form layout
        formLayout.add(shopNameField, shopIdField);

        // Create buttons
        Button searchButton = new Button("Search", event -> {
            // Handle form submission
            String shopName = shopNameField.getValue();
            String shopId = shopIdField.getValue();

            // Convert empty values to null
            if (shopName.isEmpty()) {
                shopName = null;
            }
            if (shopId.isEmpty()) {
                shopId = null;
            }

            // Store search criteria in session
            VaadinSession.getCurrent().setAttribute("searchShopName", shopName);
            VaadinSession.getCurrent().setAttribute("searchShopId", shopId);

            // Navigate to search results view
            getUI().ifPresent(ui -> ui.navigate("search shops"));
            dialog.close();
        });

        searchButton.addClassName("pointer-cursor");

        Button cancelButton = new Button("Cancel", event -> dialog.close());
        cancelButton.addClassName("pointer-cursor");

        // Create refresh button
        Button refreshButton = new Button("Refresh", event -> resetFields(shopNameField, shopIdField));
        refreshButton.addClassName("pointer-cursor");

        // Create button layout
        HorizontalLayout buttonLayout = new HorizontalLayout(searchButton, cancelButton, refreshButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER); // Center the buttons

        // Add form layout and button layout to the dialog
        VerticalLayout dialogLayout = new VerticalLayout(headline, comment, formLayout, buttonLayout);
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        dialog.add(dialogLayout);

        return dialog;
    }

    private void updateFieldStates(TextField shopNameField, TextField shopIdField) {
        boolean isShopNameFilled = !shopNameField.isEmpty();
        boolean isShopIdFilled = !shopIdField.isEmpty();

        shopNameField.setEnabled(!isShopIdFilled);
        shopIdField.setEnabled(!isShopNameFilled);
    }

    private void resetFields(TextField shopNameField, TextField shopIdField) {
        shopNameField.clear();
        shopIdField.clear();
        updateFieldStates(shopNameField, shopIdField);
    }
}
