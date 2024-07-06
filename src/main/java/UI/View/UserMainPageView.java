package UI.View;

import java.time.ZoneId;
import java.util.Date;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import com.vaadin.flow.component.html.Span;

import UI.Presenter.UserMainPagePresenter;
import UI.Model.UserDto;

@CssImport("./styles/shared-styles.css")
@PageTitle("User Main Page")
@Route(value = "user")
public class UserMainPageView extends BaseView {

    private UserMainPagePresenter presenter;
    private String _username;
    private String _userRole; 
    private Button _openShopButton;
    private VerticalLayout shopsLayout;
    private VerticalLayout messagesLayout;
    private VerticalLayout orderLayout;
    private VerticalLayout adminLayout;
    private Button saveButton; // Moved saveButton declaration to class level
    private Button editButton;
    public TextField usernameField = new TextField();
    public TextField emailField = new TextField();
    public DatePicker birthDateField = new DatePicker();
    Tab systemAdminTab;

    public UserMainPageView() {
        _username = (String) VaadinSession.getCurrent().getAttribute("username");
        _userRole = (String) VaadinSession.getCurrent().getAttribute("role");

        H2 welcomeMessage = new H2("Welcome " + _username + "!");
        Header header = new LoggedInHeader("8080");

        Tab profileTab = new Tab(VaadinIcon.USER.create(), new Span("My Profile"));
        Tab shopsTab = new Tab(VaadinIcon.SHOP.create(), new Span("My Shops"));
        Tab messagesTab = new Tab(VaadinIcon.COMMENT.create(), new Span("My Messages"));
        Tab orderHistoryTab = new Tab(VaadinIcon.CART.create(), new Span("Order History"));

        profileTab.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);
        shopsTab.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);
        messagesTab.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);
        orderHistoryTab.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);

        Tabs tabs = new Tabs(profileTab, shopsTab, messagesTab, orderHistoryTab);
        tabs.addClassName("custom-tabs");

        if ("admin".equals(_userRole)) {
            systemAdminTab = new Tab(VaadinIcon.COG.create(), new Span("System Admin"));
            systemAdminTab.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);
            tabs.add(systemAdminTab);
        }


        VerticalLayout profileLayout = new VerticalLayout();

        shopsLayout = new VerticalLayout(); // Use class-level variable
        UserShopsPageView userShopsPageView = new UserShopsPageView();
        shopsLayout.add(userShopsPageView);

        messagesLayout = new VerticalLayout();
        UserMessagesPageView userMessagesPageView = new UserMessagesPageView();
        messagesLayout.add(userMessagesPageView);

        orderLayout = new VerticalLayout();
        UserOrderHistoryView userOrderHistoryPageView = new UserOrderHistoryView();
        orderLayout.add(userOrderHistoryPageView);

        adminLayout = new VerticalLayout();
        SystemAdminPageView systemAdminPageView = new SystemAdminPageView();
        adminLayout.add(systemAdminPageView);

        FormLayout userInfoLayout = new FormLayout();

        // Fetch user information from presenter
        //UserDto userDto = new UserDto();
        presenter = new UserMainPagePresenter(this);
        presenter.getUserInfo(); // Blocking call to get the result
        // Handle the retrieved UserDto here
        // Display user information in non-editable fields
        usernameField.setReadOnly(true);
        userInfoLayout.addFormItem(usernameField, "Username");

        emailField.setReadOnly(true);
        userInfoLayout.addFormItem(emailField, "Email");

        birthDateField.setReadOnly(true);
        userInfoLayout.addFormItem(birthDateField, "Birth Date");
        // Initialize the cart image
        Image cartImage = new Image("https://raw.githubusercontent.com/inbarbc/StockMarket_Project/main/shoppingCart.jpg", "Cart");
        cartImage.setWidth("400px");

        // Create a horizontal layout for the cart image to center it
        HorizontalLayout cartImageLayout = new HorizontalLayout();
        cartImageLayout.setWidthFull(); // Make the layout take full width
        cartImageLayout.setJustifyContentMode(JustifyContentMode.CENTER); // Center the content
        cartImageLayout.add(cartImage);

        // Initialize edit and save buttons

        editButton = new Button("Edit Details", event -> {
            // Switch to edit mode
            emailField.setReadOnly(false);
            birthDateField.setReadOnly(false);

            saveButton.setVisible(true);
            editButton.setVisible(false);
        });

        saveButton = new Button("Save", event -> {
            // Save changes to presenter or backend
            Date date = Date.from(birthDateField.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            presenter.updateUserInfo(
                    new UserDto(usernameField.getValue(), emailField.getValue(), "", date));

            // Notify user of successful save
            Notification.show("Details saved successfully", 3000, Notification.Position.TOP_CENTER);

            // Switch back to view mode
            usernameField.setReadOnly(true);
            emailField.setReadOnly(true);
            birthDateField.setReadOnly(true);

            // Hide the save button
            saveButton.setVisible(false);
            editButton.setVisible(true);
        });
        saveButton.setVisible(false); // Initially hide save button

        HorizontalLayout editSaveButtonLayout = new HorizontalLayout(editButton, saveButton);
        editSaveButtonLayout.setWidthFull();
        editSaveButtonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        VerticalLayout dialogLayout = new VerticalLayout(userInfoLayout, editSaveButtonLayout);
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        profileLayout.add(dialogLayout);

        tabs.addSelectedChangeListener(event -> {
            boolean isProfileTabSelected = event.getSelectedTab() == profileTab;
            boolean isShopsTabSelected = event.getSelectedTab() == shopsTab;
            boolean isMessagesTabSelected = event.getSelectedTab() == messagesTab;
            boolean isOrderHistoryTabSelected = event.getSelectedTab() == orderHistoryTab;
            boolean isSystemAdminTabSelected = event.getSelectedTab() == systemAdminTab;

            profileLayout.setVisible(isProfileTabSelected);

            if (isShopsTabSelected) {
                shopsLayout.removeAll();
                UserShopsPageView UpdateduserShopsPageView = new UserShopsPageView();
                shopsLayout.add(UpdateduserShopsPageView);
            }
            shopsLayout.setVisible(isShopsTabSelected);

            if (isMessagesTabSelected) {
                messagesLayout.removeAll();
                UserMessagesPageView UpdateduserMessagesPageView = constructMessagesContent();
                messagesLayout.add(UpdateduserMessagesPageView);
            }
            messagesLayout.setVisible(isMessagesTabSelected);

            if (isOrderHistoryTabSelected) {
                orderLayout.removeAll();
                UserOrderHistoryView UpdateduserOrderHistoryPageView = new UserOrderHistoryView();
                orderLayout.add(UpdateduserOrderHistoryPageView);
            }
            orderLayout.setVisible(isOrderHistoryTabSelected);

            if (isSystemAdminTabSelected) {
                adminLayout.removeAll();
                SystemAdminPageView updatedAdminPageView = new SystemAdminPageView();
                adminLayout.add(updatedAdminPageView);
            }
            adminLayout.setVisible(isSystemAdminTabSelected);


            _openShopButton.setVisible(!(isMessagesTabSelected || isProfileTabSelected || isOrderHistoryTabSelected || isSystemAdminTabSelected));

        });

        tabs.setSelectedTab(profileTab);
        profileLayout.setVisible(true);
        shopsLayout.setVisible(false);
        messagesLayout.setVisible(false);
        orderLayout.setVisible(false);
        adminLayout.setVisible(false);

        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setWidthFull();
        titleLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        titleLayout.add(welcomeMessage);

        HorizontalLayout tabsLayout = new HorizontalLayout(tabs);
        tabsLayout.setWidthFull();
        tabsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        mainLayout.add(titleLayout, tabsLayout);

        _openShopButton = new Button("Open Shop", e -> createOpenNewShopDialog().open());
        _openShopButton.setWidth("120px");

        // _myMessagesButton = new Button("My Messages", e -> {
        // getUI().ifPresent(ui -> ui.navigate("user_messages"));
        // });
        // Add a click listener to the messagesTab
        // Setup for messagesTab with a selected change listener

        HorizontalLayout openShopButtonLayout = new HorizontalLayout(_openShopButton);
        openShopButtonLayout.setWidthFull();
        openShopButtonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        openShopButtonLayout.setAlignItems(FlexComponent.Alignment.END);

        add(header, mainLayout, shopsLayout, profileLayout, messagesLayout, orderLayout, openShopButtonLayout);

        if ("admin".equals(_userRole)) {
            add(adminLayout);
        }
    }

    // Method to construct or reload the messages content
    private UserMessagesPageView constructMessagesContent() { 
        UserMessagesPageView userMessagesPageView = new UserMessagesPageView();
        // Example: Add components to layout, such as messages
        return userMessagesPageView;
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
            shopsLayout.removeAll();
            UserShopsPageView UpdateduserShopsPageView = new UserShopsPageView();
            shopsLayout.add(UpdateduserShopsPageView);

            // Close the dialog after submission
            dialog.close();
        });

        submitButton.addClassName("pointer-cursor");

        Button cancelButton = new Button("Cancel", event -> dialog.close());

        cancelButton.addClassName("pointer-cursor");

        // Create button layout
        HorizontalLayout buttonLayout = new HorizontalLayout(submitButton, cancelButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER); // Center the buttons

        // Add form layout and button layout to the dialog
        VerticalLayout dialogLayout = new VerticalLayout(headline, formLayout, buttonLayout);
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        dialog.add(dialogLayout);

        return dialog;
    }
}
