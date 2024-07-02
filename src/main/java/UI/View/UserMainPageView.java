package UI.View;

import java.time.ZoneId;
import java.util.Date;

import com.vaadin.flow.component.UI;
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
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
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
    private Button _openShopButton;
    private Button _reportButton;
    private VerticalLayout shopsLayout;
    private VerticalLayout messagesLayout;
    private VerticalLayout orderLayout;
    private Button saveButton; // Moved saveButton declaration to class level
    private Button editButton;
    public TextField usernameField = new TextField();
    public TextField emailField = new TextField();
    public DatePicker birthDateField = new DatePicker();

    public UserMainPageView() {
        _username = (String) VaadinSession.getCurrent().getAttribute("username");

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

        FormLayout userInfoLayout = new FormLayout();

        presenter = new UserMainPagePresenter(this);
        presenter.getUserInfo(); // Blocking call to get the result

        usernameField.setReadOnly(true);
        userInfoLayout.addFormItem(usernameField, "Username");

        emailField.setReadOnly(true);
        userInfoLayout.addFormItem(emailField, "Email");

        birthDateField.setReadOnly(true);
        userInfoLayout.addFormItem(birthDateField, "Birth Date");

        Image cartImage = new Image(
                "https://raw.githubusercontent.com/inbarbc/StockMarket_Project/main/shoppingCart.jpg", "Cart");
        cartImage.setWidth("400px");

        HorizontalLayout cartImageLayout = new HorizontalLayout();
        cartImageLayout.setWidthFull();
        cartImageLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        cartImageLayout.add(cartImage);

        editButton = new Button("Edit Details", event -> {
            emailField.setReadOnly(false);
            birthDateField.setReadOnly(false);

            saveButton.setVisible(true);
            editButton.setVisible(false);
        });

        saveButton = new Button("Save", event -> {
            Date date = Date.from(birthDateField.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            presenter.updateUserInfo(new UserDto(usernameField.getValue(), emailField.getValue(), "", date));

            Notification.show("Details saved successfully", 3000, Notification.Position.TOP_CENTER);

            usernameField.setReadOnly(true);
            emailField.setReadOnly(true);
            birthDateField.setReadOnly(true);

            saveButton.setVisible(false);
            editButton.setVisible(true);
        });
        saveButton.setVisible(false);

        HorizontalLayout editSaveButtonLayout = new HorizontalLayout(editButton, saveButton);
        editSaveButtonLayout.setWidthFull();
        editSaveButtonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        VerticalLayout dialogLayout = new VerticalLayout(userInfoLayout, editSaveButtonLayout);
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        profileLayout.add(dialogLayout);

        // Initialize the Report button
        _reportButton = new Button("Report", e -> {
            Dialog reportDialog = new Dialog();
            reportDialog.setWidth("500px"); // Increased width
            reportDialog.setHeight("400px"); // Increased height

            FormLayout formLayout = new FormLayout();
            Select<String> reasonSelect = new Select<>();
            reasonSelect.setLabel("Select the reason from the list below");
            reasonSelect.setItems("No longer needed", "Item doesn't match the description",
                    "Item defective or doesn't work", "Damaged", "Items are missing", "Expiry date issues", "Other...");
            reasonSelect.setPlaceholder("Please select");
            reasonSelect.setWidthFull(); // Make the select component full width

            TextArea complaintField = new TextArea("Report details");
            complaintField.setWidthFull(); // Make the text area full width
            complaintField.setHeight("150px"); // Set height for the text area

            Button submitButton = new Button("Submit", event -> {
                // Implement logic to handle the complaint submission (e.g., send to the server)
                String selectedReason = reasonSelect.getValue();
                String complaintDetails = complaintField.getValue();
                // Add your logic here to handle the complaint
                Notification.show("Complaint submitted: " + selectedReason);
                reportDialog.close();
                String username = (String) UI.getCurrent().getSession().getAttribute("username");
                // String message = "Complaint submitted for shop " + _shopId +", from user: "+
                // username + ".\n" + "The reason: " + selectedReason + ".\n" +"details:" +
                // complaintDetails;
                String message = "report submitted for shop " + ", from user: " + username + ".\n"
                        + "The reason: " + selectedReason + ".\n" + "details:" + complaintDetails;
                presenter.openComplain(message);
            });

            formLayout.addFormItem(reasonSelect, "Reason");
            formLayout.addFormItem(complaintField, "Complaint details");
            formLayout.add(submitButton);

            // Make the form layout full width to fit the dialog
            formLayout.setWidthFull();

            reportDialog.add(formLayout);
            reportDialog.open();
        });

        _reportButton.setWidth("120px");
        _reportButton.setVisible(true); // Initially hidden

        tabs.addSelectedChangeListener(event -> {
            boolean isProfileTabSelected = event.getSelectedTab() == profileTab;
            boolean isShopsTabSelected = event.getSelectedTab() == shopsTab;
            boolean isMessagesTabSelected = event.getSelectedTab() == messagesTab;
            boolean isOrderHistoryTabSelected = event.getSelectedTab() == orderHistoryTab;

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

            _openShopButton.setVisible(isShopsTabSelected);
            _reportButton.setVisible(isProfileTabSelected);
        });

        tabs.setSelectedTab(profileTab);
        profileLayout.setVisible(true);
        shopsLayout.setVisible(false);
        messagesLayout.setVisible(false);
        orderLayout.setVisible(false);

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
        _openShopButton.setVisible(false); // Initially hidden

        HorizontalLayout openShopButtonLayout = new HorizontalLayout(_openShopButton, _reportButton);
        openShopButtonLayout.setWidthFull();
        openShopButtonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        openShopButtonLayout.setAlignItems(FlexComponent.Alignment.END);

        add(header, mainLayout, shopsLayout, profileLayout, messagesLayout, orderLayout, openShopButtonLayout);
    }

    private UserMessagesPageView constructMessagesContent() {
        UserMessagesPageView userMessagesPageView = new UserMessagesPageView();
        return userMessagesPageView;
    }

    private Dialog createOpenNewShopDialog() {
        Dialog dialog = new Dialog();

        H2 headline = new H2("Open New Shop");

        FormLayout formLayout = new FormLayout();

        TextField shopNameField = new TextField("Shop Name");
        TextField bankDetailsField = new

        TextField("Bank Details");
        TextField shopAddressField = new TextField("Address");

        formLayout.add(shopNameField, bankDetailsField, shopAddressField);

        Button submitButton = new Button("Submit", event -> {
            String shopName = shopNameField.getValue();
            String bankDetails = bankDetailsField.getValue();
            String shopAddress = shopAddressField.getValue();

            presenter.openNewShop(shopName, bankDetails, shopAddress);
            shopsLayout.removeAll();
            UserShopsPageView UpdateduserShopsPageView = new UserShopsPageView();
            shopsLayout.add(UpdateduserShopsPageView);

            dialog.close();
        });

        submitButton.addClassName("pointer-cursor");

        Button cancelButton = new Button("Cancel", event -> dialog.close());

        cancelButton.addClassName("pointer-cursor");

        HorizontalLayout buttonLayout = new HorizontalLayout(submitButton, cancelButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        VerticalLayout dialogLayout = new VerticalLayout(headline, formLayout, buttonLayout);
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        dialog.add(dialogLayout);

        return dialog;
    }
}