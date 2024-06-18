package UI.View;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
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

@CssImport("./styles/shared-styles.css")
@PageTitle("User Main Page")
@Route(value = "user")
public class UserMainPageView extends BaseView {

    private UserMainPagePresenter presenter;
    private String _username;
    private Button _openShopButton;

    // Make shopsLayout a member variable
    private VerticalLayout shopsLayout;

    // Reference to UserShopsPageView
    private UserShopsPageView userShopsPageView;

    public UserMainPageView() {
        // Retrieve the username from the session
        _username = (String) VaadinSession.getCurrent().getAttribute("username");

        // Create welcome message
        H2 welcomeMessage = new H2("Welcome " + _username + "!");

        // Create the header component
        Header header = new LoggedInHeader("8080");

        // Create tabs with icons
        Tab profileTab = new Tab(VaadinIcon.USER.create(), new Span("My Profile"));
        Tab shopsTab = new Tab(VaadinIcon.SHOP.create(), new Span("My Shops"));

        // Apply icon on top theme to tabs
        profileTab.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);
        shopsTab.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);

        // Create Tabs component and apply custom style
        Tabs tabs = new Tabs(profileTab, shopsTab);
        tabs.addClassName("custom-tabs"); // Apply custom CSS class

        // Create layouts for tabs content
        VerticalLayout profileLayout = new VerticalLayout();
        shopsLayout = new VerticalLayout(); // Initialize shopsLayout

        // Profile tab content
        // Add your profile tab content here

        // Initialize UserShopsPageView
        userShopsPageView = new UserShopsPageView();

        // Attach content to the tabs
        tabs.addSelectedChangeListener(event -> {
            profileLayout.setVisible(false);
            shopsLayout.setVisible(false);
            if (event.getSelectedTab() == profileTab) {
                profileLayout.setVisible(true);
            } else if (event.getSelectedTab() == shopsTab) {
                shopsLayout.setVisible(true);
                userShopsPageView.setVisible(true); // Show UserShopsPageView content
            } else {
                userShopsPageView.setVisible(false); // Hide UserShopsPageView content for other tabs
            }
        });

        // Initialize the tab content
        tabs.setSelectedTab(profileTab);
        profileLayout.setVisible(true);
        shopsLayout.setVisible(false);
        userShopsPageView.setVisible(false); // Initially hide UserShopsPageView content

        // Create a horizontal layout for the title to center it
        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setWidthFull(); // Make the layout take full width
        titleLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER); // Center the content
        titleLayout.add(welcomeMessage);

        // Create a layout for the tabs and center it
        HorizontalLayout tabsLayout = new HorizontalLayout(tabs);
        tabsLayout.setWidthFull(); // Make the layout take full width
        tabsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER); // Center the tabs

        // Create the main layout and add components
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        mainLayout.add(titleLayout, tabsLayout, profileLayout, shopsLayout, userShopsPageView);

        // Initialize presenter
        presenter = new UserMainPagePresenter(this);

        // Create the "Open Shop" button with small width
        _openShopButton = new Button("Open Shop", e -> createOpenNewShopDialog().open());
        _openShopButton.setWidth("120px"); // Set a fixed width for the button

        // Create a layout for the button and align it to the bottom-right
        HorizontalLayout buttonLayout = new HorizontalLayout(_openShopButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setAlignItems(FlexComponent.Alignment.END);

        // Add components to the vertical layout
        add(header, mainLayout, buttonLayout);

    }

    // Optionally remove the createShopButtons method from UserMainPageView
    // as it's now handled by UserShopsPageView directly

    private Dialog createOpenNewShopDialog() {
        Dialog dialog = new Dialog();

        H2 headline = new H2("Open New Shop");
        FormLayout formLayout = new FormLayout();

        TextField shopNameField = new TextField("Shop Name");
        TextField bankDetailsField = new TextField("Bank Details");
        TextField shopAddressField = new TextField("Address");

        formLayout.add(shopNameField, bankDetailsField, shopAddressField);

        Button submitButton = new Button("Submit", event -> {
            String shopName = shopNameField.getValue();
            String bankDetails = bankDetailsField.getValue();
            String shopAddress = shopAddressField.getValue();

            presenter.openNewShop(shopName, bankDetails, shopAddress);

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
