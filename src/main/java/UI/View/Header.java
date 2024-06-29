package UI.View;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

import UI.Presenter.HeaderPresenter;
import UI.Presenter.SearchProductsPresenter;
import UI.Presenter.SearchShopPresenter;

public class Header extends HorizontalLayout {

    private Button loginButton;
    private Button _registerButton;
    private final HeaderPresenter presenter;
    private final SearchProductsPresenter searchProductsPresenter;
    private final SearchShopPresenter searchShopPresenter;
    private HorizontalLayout _leftButtonLayout;
    private VerticalLayout searchResultsLayout;
    private Dialog logoutConfirmationDialog;

    public Header(String serverPort) {

        // Initialize the presenters
        presenter = new HeaderPresenter(this, serverPort);
        searchProductsPresenter = new SearchProductsPresenter(this, serverPort);
        searchShopPresenter = new SearchShopPresenter(this, serverPort);

        // Create an Image component
        Image image = new Image("https://raw.githubusercontent.com/inbarbc/StockMarket_Project/main/shoppingCartSmallIcon.jpg", "Shopping Cart");

        // Set the size of the image
        image.setWidth("75px");
        image.setHeight("75px");

        // Create the buttons
        _registerButton = new Button("Register");
        loginButton = new Button("Login");
        Button searchProductsButton = new Button("Search Products");
        Button searchShopsButton = new Button("Search Shops");
        Button shoppingCartButton = new Button(image);
        // Button messagesButton = new Button("My Messages");
        Button messagesButton = new Button("My Messages", e -> navigateToMessages());
        Button allShopsButton = new Button("All Shops");

        SearchProductsResultsView searchProductsResultsView = new SearchProductsResultsView(searchProductsPresenter);
        searchProductsPresenter.setSearchProductsResultsView(searchProductsResultsView);

        SearchShopResultsView searchShopResultsView = new SearchShopResultsView();
        searchShopPresenter.setSearchShopsResultsView(searchShopResultsView);

        // Add cursor styling
        _registerButton.addClassName("pointer-cursor");
        loginButton.addClassName("pointer-cursor");
        searchProductsButton.addClassName("pointer-cursor");
        searchShopsButton.addClassName("pointer-cursor");
        shoppingCartButton.addClassName("pointer-cursor");
        messagesButton.addClassName("pointer-cursor");
        allShopsButton.addClassName("pointer-cursor");

        // Create horizontal layout for left buttons
        _leftButtonLayout = new HorizontalLayout();
        _leftButtonLayout.add(_registerButton, loginButton);

        // Spacer to separate left and right buttons
        Span spacer = new Span();
        spacer.getStyle().set("flex-grow", "1"); // This will make the spacer flexible and push the buttons apart


        // Create horizontal layout for right buttons
        HorizontalLayout rightButtonLayout = new HorizontalLayout();
        rightButtonLayout.add(searchProductsButton, searchShopsButton, allShopsButton, shoppingCartButton);

        // Add left buttons, spacer, and right buttons to the main layout
        add(_leftButtonLayout, spacer, rightButtonLayout);

        // Adjust button spacing if needed
        setWidthFull(); // Make the layout take full width
        setAlignItems(Alignment.CENTER); // Center the buttons vertically

        // Create registration dialog
        Dialog registrationDialog = createRegistrationDialog();

        // Create login dialog
        Dialog loginDialog = createLoginDialog();

        // Create search products dialog
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

        allShopsButton.addClickListener(event -> {
            getUI().ifPresent(ui -> ui.navigate("all_shops"));
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
        H2 headline = new H2("Search Products");
        headline.getStyle().set("margin", "0");

        // Initial shop search method selection
        H5 shopComment = new H5("Please choose your desired search method");

        Button searchInASpecificShopButton = new Button("A specific Shop");
        searchInASpecificShopButton.setWidth("200px");
        searchInASpecificShopButton.addClassName("pointer-cursor");

        Button searchInAllShopsButton = new Button("All Shops");
        searchInAllShopsButton.setWidth("200px");
        searchInAllShopsButton.addClassName("pointer-cursor");

        HorizontalLayout initialButtonsLayout = new HorizontalLayout(searchInAllShopsButton, searchInASpecificShopButton);
        VerticalLayout initialLayout = new VerticalLayout(shopComment);
        initialLayout.add(initialButtonsLayout);
        initialLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        initialLayout.setWidthFull();

        // Create form layout for shop name entry
        
        FormLayout shopNameLayout = new FormLayout();
        TextField shopNameField = new TextField("Shop Name:");
        Button enterShopNameButton = new Button("Enter");
        enterShopNameButton.setEnabled(false);
        shopNameField.addValueChangeListener(listener -> {
            if (shopNameField.getValue().isEmpty()) {
                enterShopNameButton.setEnabled(false);
            } else {
                enterShopNameButton.setEnabled(true);
                enterShopNameButton.addClassName("pointer-cursor");
            }
        });
        shopNameLayout.setWidthFull();


        shopNameLayout.add(shopNameField, enterShopNameButton);
        shopNameLayout.setVisible(false); // Initially hidden

        // Create a single-element array to store the shopName variable
        final String[] shopName = {null};

        // Create form layout for product search
        VerticalLayout searchFormWrapperLayout = new VerticalLayout();
        FormLayout searchFormLayout = new FormLayout();

        ComboBox<String> categoryField = new ComboBox<>("Search By Category");
        categoryField.setItems("Electronics", "Books", "Clothing", "Home", "Kitchen", "Sports", "Grocery", "Pharmacy");

        MultiSelectComboBox<String> keywordsField = new MultiSelectComboBox<>();
        keywordsField.setHeight("100px");
        keywordsField.setWidth("150px");

        HorizontalLayout keywordRow = new HorizontalLayout();
        TextField keywordInputField = new TextField("Search By Keywords");
        Set<String> allKeywords = new HashSet<>();
        Set<String> selectedKeywords = new HashSet<>();
        Button addKeywordButton = new Button("Add Keyword", event -> {
            String keyword = keywordInputField.getValue().trim();
            if (!keyword.isEmpty() && !allKeywords.contains(keyword)) {
                allKeywords.add(keyword);
                selectedKeywords.clear();
                selectedKeywords.addAll(keywordsField.getSelectedItems());
                keywordsField.setItems(allKeywords);
                keywordsField.updateSelection(selectedKeywords, Collections.emptySet());
                keywordInputField.clear();
            }
        });
        addKeywordButton.addClassName("pointer-cursor");
        keywordsField.getElement().setAttribute("style", "padding-bottom: 10px;");
        keywordRow.add(keywordInputField, addKeywordButton);
        //keywordRow.setWidth("500px");
        keywordRow.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        keywordRow.setVerticalComponentAlignment(FlexComponent.Alignment.END, keywordInputField, addKeywordButton);        


        TextField productNameField = new TextField("Search By Product Name");

        searchFormLayout.add(productNameField, categoryField, keywordRow, keywordsField);
        //searchFormLayout.getElement().getStyle().set("align-items", "center");
        searchFormLayout.getElement().getStyle().set("margin", "auto");
        searchFormLayout.setMaxWidth("500px");
        searchFormWrapperLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        searchFormWrapperLayout.add(searchFormLayout);
        searchFormWrapperLayout.setVisible(false); // Initially hidden

        // Create Buttons layout
        HorizontalLayout buttonLayout = new HorizontalLayout();

        // Create buttons
        Button searchButton = new Button("Search");
        searchButton.addClickListener(event -> {
            String category = categoryField.getValue();
            List<String> keywords = new ArrayList<>(keywordsField.getSelectedItems());
            String productName = productNameField.getValue();
            if (category != null && category.isEmpty()) {
                category = null;
            }
            if (keywords != null && keywords.isEmpty()) {
                keywords = null;
            }
            if (productName != null && productName.isEmpty()) {
                productName = null;
            }
            searchProductsPresenter.searchProducts(shopName[0], productName, category, keywords);
            resetSearchProductsFields(categoryField, keywordsField, keywordInputField, addKeywordButton, productNameField, searchButton);
            resetSearchProductsShopsFields(searchInAllShopsButton, searchInASpecificShopButton, shopNameField, enterShopNameButton);
            resetSearchProductFormLayout(initialLayout, shopNameLayout, searchFormWrapperLayout, buttonLayout);
            dialog.close();
        });
        searchButton.addClassName("pointer-cursor");
        searchButton.setEnabled(false);

        ValueChangeListener<ValueChangeEvent<?>> listener = event -> updateFieldStates(categoryField, keywordsField, keywordInputField, addKeywordButton, productNameField, searchButton);

        categoryField.addValueChangeListener(listener);
        keywordsField.addValueChangeListener(listener);
        productNameField.addValueChangeListener(listener);

        Button cancelButton = new Button("Cancel", event -> {
            resetSearchProductsFields(categoryField, keywordsField, keywordInputField, addKeywordButton, productNameField, searchButton);
            resetSearchProductsShopsFields(searchInAllShopsButton, searchInASpecificShopButton, shopNameField, enterShopNameButton);
            resetSearchProductFormLayout(initialLayout, shopNameLayout, searchFormWrapperLayout, buttonLayout);
            dialog.close();
        });
        cancelButton.addClassName("pointer-cursor");

        Button refreshButton = new Button("Refresh", event -> resetSearchProductsFields(categoryField, keywordsField, keywordInputField, addKeywordButton, productNameField, searchButton));
        refreshButton.addClassName("pointer-cursor");


        buttonLayout.add(cancelButton, searchButton, refreshButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        buttonLayout.setVisible(false); // Initially hidden

        VerticalLayout dialogLayout = new VerticalLayout(headline, initialLayout, shopNameLayout, searchFormWrapperLayout, buttonLayout);
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        dialogLayout.setMaxWidth("500px");

        dialog.add(dialogLayout);

        
        // Add button click listeners
        searchInASpecificShopButton.addClickListener(event -> {
            searchInASpecificShopButton.addClassName("selected");
            searchInAllShopsButton.removeClassName("selected");
            shopName[0] = null;
            shopNameLayout.setVisible(true);
            shopNameField.setReadOnly(false);
            enterShopNameButton.setEnabled(true);
            enterShopNameButton.addClassName("pointer-cursor");
            enterShopNameButton.setEnabled(true);
        });

        searchInAllShopsButton.addClickListener(event -> {
            shopName[0] = null;
            searchInAllShopsButton.addClassName("selected");
            searchInASpecificShopButton.removeClassName("selected");
            shopNameLayout.setVisible(false);
            searchFormWrapperLayout.setVisible(true);
            buttonLayout.setVisible(true);
        });

        enterShopNameButton.addClickListener(event -> {
            shopName[0] = shopNameField.getValue().trim();
            if (!shopName[0].isEmpty()) {
                enterShopNameButton.setEnabled(false);
                shopNameField.setReadOnly(true);
                searchFormWrapperLayout.setVisible(true);
                buttonLayout.setVisible(true);
            } else {
                Notification.show("Please enter a shop name");
            }
        });
        return dialog;
    }


    private void updateFieldStates(ComboBox<String> categoryField, MultiSelectComboBox<String> keyWordField, TextField keywordInputField, Button addKeywordButton, TextField productNameField, Button searchButton) {
        boolean anyFieldFilled = !categoryField.isEmpty() || !productNameField.isEmpty() || !keyWordField.isEmpty();
        boolean keywordsFieldFilled = !keyWordField.isEmpty() || !keywordInputField.isEmpty();

        categoryField.setEnabled(!anyFieldFilled || !categoryField.isEmpty());
        keyWordField.setEnabled(!anyFieldFilled || !keyWordField.isEmpty());
        keywordInputField.setEnabled(!anyFieldFilled || keywordsFieldFilled);
        addKeywordButton.setEnabled(!anyFieldFilled || keywordsFieldFilled);
        productNameField.setEnabled(!anyFieldFilled || !productNameField.isEmpty());
        searchButton.setEnabled(anyFieldFilled);
    }

    private void resetSearchProductsFields(ComboBox<String> categoryField, MultiSelectComboBox<String> keyWordField, TextField keywordInputField, Button addKeywordButton, TextField productNameField, Button searchButton) {
        categoryField.clear();
        keyWordField.clear();
        keywordInputField.clear();
        productNameField.clear();
        updateFieldStates(categoryField, keyWordField, keywordInputField, addKeywordButton, productNameField, searchButton);
    }

    public Dialog createSearchShopsDialog(){
        Dialog dialog = new Dialog();

        // Create form layout
        FormLayout formLayout = new FormLayout();

        // Create a headline
        H2 headline = new H2("Search Shops");
        headline.getStyle().set("margin", "0");

        H5 comment = new H5("Please choose your desired search method");

        // Create form fields
        TextField shopNameField = new TextField("Shop Name");
        TextField shopIdField = new TextField("Shop ID");
        shopIdField.setPattern("[0-9]+");
        shopIdField.setErrorMessage("Please enter a valid shop ID");

        // Add fields to the form layout
        formLayout.add(shopNameField, shopIdField);

        // Create Buttons layout
        HorizontalLayout buttonLayout = new HorizontalLayout();

        // Create buttons
        Button searchButton = new Button("Search");
        searchButton.addClickListener(event -> {
            // Handle form submission
            String shopName = shopNameField.getValue();
            String shopId = shopIdField.getValue();

            // Convert empty values to null
            if (shopName.isEmpty()) {
                searchShopPresenter.searchShop(Integer.parseInt(shopId), null);
            }
            else if (shopId.isEmpty()) {
                searchShopPresenter.searchShop(null, shopName);
            }

            // // Store search criteria in session
            // VaadinSession.getCurrent().setAttribute("searchShopName", shopName);
            // VaadinSession.getCurrent().setAttribute("searchShopId", shopId);

            // searchShopPresenter.searchShop(Integer.parseInt(shopId), shopName);
            resetSearchShopFields(shopNameField, shopIdField, searchButton);
            dialog.close();
        });

        searchButton.addClassName("pointer-cursor");
        searchButton.setEnabled(false);

        // Add value change listeners to ensure only one filter is active
        shopNameField.addValueChangeListener(event -> updateFieldsStates(shopNameField, shopIdField, searchButton));
        shopIdField.addValueChangeListener(event -> updateFieldsStates(shopNameField, shopIdField, searchButton));

        Button cancelButton = new Button("Cancel", event -> {
            resetSearchShopFields(shopNameField, shopIdField, searchButton);
            dialog.close();
        });
        cancelButton.addClassName("pointer-cursor");

        Button refreshButton = new Button("Refresh", event -> resetSearchShopFields(shopNameField, shopIdField, searchButton));
        refreshButton.addClassName("pointer-cursor");


        buttonLayout.add(cancelButton, searchButton, refreshButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        // Add form layout and button layout to the dialog
        VerticalLayout dialogLayout = new VerticalLayout(headline, comment, formLayout, buttonLayout);
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        dialog.add(dialogLayout);

        return dialog;
    }

    private void updateFieldsStates(TextField shopNameField, TextField shopIdField, Button searchButton) {
        boolean isShopNameFilled = !shopNameField.isEmpty();
        boolean isShopIdFilled = !shopIdField.isEmpty();
        shopNameField.setEnabled(!isShopIdFilled);
        shopIdField.setEnabled(!isShopNameFilled);
        searchButton.setEnabled(isShopNameFilled || isShopIdFilled);
    }

    private void resetSearchShopFields(TextField shopNameField, TextField shopIdField, Button searchButton) {
        shopNameField.clear();
        shopIdField.clear();
        searchButton.setEnabled(false);
        updateFieldsStates(shopNameField, shopIdField, searchButton);
    }

    public void setSearchResultsVisible(boolean visible) {
        //add(searchResultsLayout); 
        searchResultsLayout.setVisible(visible);
    }

    private void resetSearchProductsShopsFields(Button searchInAllShopsButton, Button searchInASpecificShopButton, TextField shopName, Button enterShopNameButton) {
        searchInAllShopsButton.removeClassName("selected");
        searchInASpecificShopButton.removeClassName("selected");
        shopName.clear();
        shopName.setReadOnly(false);
        enterShopNameButton.setEnabled(true);       
    }

    private void resetSearchProductFormLayout(VerticalLayout initialLayout, FormLayout shopNameLayout, VerticalLayout searchFormWrapperLayout, HorizontalLayout buttonLayout) {
        initialLayout.setVisible(true);
        shopNameLayout.setVisible(false);
        searchFormWrapperLayout.setVisible(false);
        buttonLayout.setVisible(false);

    }




    
}