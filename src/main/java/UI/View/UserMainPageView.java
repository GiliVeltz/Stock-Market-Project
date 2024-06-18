package UI.View;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@CssImport("./styles/shared-styles.css")
@PageTitle("User Main Page")
@Route(value = "user")
public class UserMainPageView extends BaseView {

    private String _username;
    private Button _openShopButton;
    private VerticalLayout shopsLayout;

    public UserMainPageView() {
        _username = (String) VaadinSession.getCurrent().getAttribute("username");

        H2 welcomeMessage = new H2("Welcome " + _username + "!");
        Header header = new LoggedInHeader("8080");

        Tab profileTab = new Tab(VaadinIcon.USER.create(), new Span("My Profile"));
        Tab shopsTab = new Tab(VaadinIcon.SHOP.create(), new Span("My Shops"));

        profileTab.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);
        shopsTab.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);

        Tabs tabs = new Tabs(profileTab, shopsTab);
        tabs.addClassName("custom-tabs");

        VerticalLayout profileLayout = new VerticalLayout();
        shopsLayout = new VerticalLayout(); // Use class-level variable

        UserShopsPageView userShopsPageView = new UserShopsPageView();
        shopsLayout.add(userShopsPageView);

        tabs.addSelectedChangeListener(event -> {
            profileLayout.setVisible(event.getSelectedTab() == profileTab);
            shopsLayout.setVisible(event.getSelectedTab() == shopsTab);
        });

        tabs.setSelectedTab(profileTab);
        profileLayout.setVisible(true);
        shopsLayout.setVisible(false);

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

        HorizontalLayout buttonLayout = new HorizontalLayout(_openShopButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setAlignItems(FlexComponent.Alignment.END);

        add(header, mainLayout, shopsLayout, profileLayout, buttonLayout);
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

            // Handle submission logic here

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
