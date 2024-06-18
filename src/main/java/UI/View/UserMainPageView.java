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

import java.util.List;

import com.vaadin.flow.component.html.Span;

import UI.Presenter.UserMainPagePresenter;
import UI.Presenter.UserShopsPagePresenter;

@CssImport("./styles/shared-styles.css")
@PageTitle("User Main Page")
@Route(value = "user")
public class UserMainPageView extends BaseView {

    private UserMainPagePresenter presenter;
    private UserShopsPagePresenter shopsPresenter;
    private String _username;
    private Button _openShopButton;

    // Make shopsLayout a member variable
    private VerticalLayout shopsLayout;

    public UserMainPageView() {
        // Retrieve the username from the session
        _username = (String) VaadinSession.getCurrent().getAttribute("username");

        // Create welcome message
        H1 welcomeMessage = new H1("Welcome, " + _username + "!");

        // Create the header component
        Header header = new LoggedInHeader("8080");

        // Create tabs with icons
        Tab profileTab = new Tab(VaadinIcon.USER.create(), new Span("My Profile"));
        Tab shopsTab = new Tab(VaadinIcon.SHOP.create(), new Span("View My Shops"));

        // Apply icon on top theme to tabs
        profileTab.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);
        shopsTab.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);

        // Create Tabs component and apply custom style
        Tabs tabs = new Tabs(profileTab, shopsTab);
        // tabs.addThemeVariants(TabsVariant.LUMO_CENTERED);
        tabs.addClassName("custom-tabs"); // Apply custom CSS class

        // Create layouts for tabs content
        VerticalLayout profileLayout = new VerticalLayout();
        shopsLayout = new VerticalLayout(); // Initialize shopsLayout

        // Profile tab content
        // Add your profile tab content here

        // Shops tab content
        shopsPresenter = new UserShopsPagePresenter(this);
        shopsPresenter.fetchShops(_username);
        shopsLayout.add(new H1("My Shops"));

        // Attach content to the tabs
        tabs.addSelectedChangeListener(event -> {
            profileLayout.setVisible(false);
            shopsLayout.setVisible(false);
            if (event.getSelectedTab() == profileTab) {
                profileLayout.setVisible(true);
            } else if (event.getSelectedTab() == shopsTab) {
                shopsLayout.setVisible(true);
            }
        });

        // Initialize the tab content
        tabs.setSelectedTab(profileTab);
        profileLayout.setVisible(true);
        shopsLayout.setVisible(false);

        // Create a horizontal layout for the title to center it
        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setWidthFull(); // Make the layout take full width
        titleLayout.setJustifyContentMode(JustifyContentMode.CENTER); // Center the content
        titleLayout.add(welcomeMessage);

        // Create a layout for the tabs and center it
        HorizontalLayout tabsLayout = new HorizontalLayout(tabs);
        tabsLayout.setWidthFull(); // Make the layout take full width
        tabsLayout.setJustifyContentMode(JustifyContentMode.CENTER); // Center the tabs

        // Create the main layout and add components
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        mainLayout.add(titleLayout, tabsLayout, profileLayout, shopsLayout);

        // Add components to the vertical layout
        add(header, mainLayout);

        // Initialize presenter
        presenter = new UserMainPagePresenter(this);
    }

    public void createShopButtons(List<Integer> shops, List<String> shopNames) {
        VerticalLayout gridLayout = new VerticalLayout();
        if (shops.isEmpty()) {
            gridLayout.add(new Paragraph("No shops found"));
        } else {
            int maxButtonsPerRow = 3;
            HorizontalLayout rowLayout = new HorizontalLayout();

            for (int i = 0; i < shops.size(); i++) {
                Integer shopId = shops.get(i);
                Button shopButton = new Button("" + shopNames.get(i), e -> navigateToManageShop(shopId));
                shopButton.addClassName("same-size-button");
                rowLayout.add(shopButton);

                if ((i + 1) % maxButtonsPerRow == 0 || i == shops.size() - 1) {
                    gridLayout.add(rowLayout);
                    rowLayout = new HorizontalLayout();
                }
            }
        }
        shopsLayout.add(gridLayout); // Add grid layout to shopsLayout
    }

    private void navigateToShops() {
        getUI().ifPresent(ui -> ui.navigate("user_shops"));
    }

    private void navigateToProfile() {
        getUI().ifPresent(ui -> ui.navigate("profile"));
    }

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
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        VerticalLayout dialogLayout = new VerticalLayout(headline, formLayout, buttonLayout);
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        dialog.add(dialogLayout);

        return dialog;
    }

    public void navigateToManageShop(Integer shopId) {
        getUI().ifPresent(ui -> ui.navigate("user_shops/" + shopId));
    }
}
