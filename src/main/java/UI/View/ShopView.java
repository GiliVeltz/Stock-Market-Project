package UI.View;

import java.util.List;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import UI.Model.ProductDto;
import UI.Presenter.ShopViewPresenter;

@Route(value = "shop_page")
public class ShopView extends BaseView implements HasUrlParameter<Integer> {

    private ShopViewPresenter _presenter;
    private List<ProductDto> _products;
    private String _shopName;
    private Integer _shopId;
    private boolean _isGuest;

    @Override
    public void setParameter(BeforeEvent event, Integer parameter) {
        if (parameter != null) {
            _shopId = parameter;
            VaadinSession.getCurrent().setAttribute("shopId", _shopId);
        }
    }

    public ShopView() {

        // Initialize presenter
        _presenter = new ShopViewPresenter(this);

        // Create the header component
        Header guestHeader = new BrowsePagesHeaderGuest("8080");
        Header userHeader = new BrowsePagesHeader("8080");

        _shopId = (Integer) VaadinSession.getCurrent().getAttribute("shopId");
        _shopName = (String) VaadinSession.getCurrent().getAttribute("shopName");

        _isGuest = isGuest();

        if (_isGuest) {
            add(guestHeader);
        } else {
            add(userHeader);
        }

        if (_shopId != null) {
            // Fetch and display products only if shopId is set
            H2 shopHeadline = new H2(_shopName);
            add(shopHeadline);

            _presenter.getShopProducts();
        } else {
            Notification.show("Shop ID is not set.");
        }

        // Add the complain button
        Button complainButton = new Button("Complain");
        complainButton.addClickListener(e -> openComplainDialog());
        add(complainButton);

    }

    public void displayAllProducts(List<ProductDto> products) {
        this._products = products;

        VerticalLayout productsLayout = new VerticalLayout();
        productsLayout.setWidth("100%");

        for (ProductDto product : products) {
            // Create a product panel with details
            VerticalLayout productPanel = createProductPanel(product);
            productsLayout.add(productPanel);
        }

        add(productsLayout);
    }

    private VerticalLayout createProductPanel(ProductDto product) {
        VerticalLayout productPanel = new VerticalLayout();
        productPanel.setWidth("100%");
        productPanel.setPadding(true);
        productPanel.setSpacing(true);

        // Product name
        H3 productNameLabel = new H3(product.getProductName());
        productPanel.add(productNameLabel);

        // Product category (if needed)
        // H4 categoryLabel = new H4(product.getCategory().toString());
        // productPanel.add(categoryLabel);

        // Price and quantity controls
        Div priceAndControls = new Div();
        priceAndControls.add(new H3("Price: $" + product.getPrice()));

        // Plus and minus buttons for quantity
        Button plusButton = new Button("+");
        Button minusButton = new Button("-");
        Div quantityControls = new Div(plusButton, minusButton);
        priceAndControls.add(quantityControls);

        // Add to cart button
        Button addToCartButton = new Button("Add to Cart", event -> addToCart(product));
        productPanel.add(priceAndControls, addToCartButton);

        // Enable dragging of the product panel
        productPanel.getElement().setAttribute("draggable", "true");

        return productPanel;
    }

    private void addToCart(ProductDto product) {
        // Implement logic to add the product to the cart (not shown here)
        // Example: presenter.addToCart(product);
        Notification.show(product.getProductName() + " added to cart");
    }

    private void openComplainDialog() {
        Dialog complainDialog = new Dialog();
        complainDialog.setWidth("500px"); // Increased width
        complainDialog.setHeight("400px"); // Increased height
    
        FormLayout formLayout = new FormLayout();
    
        // Create the dropdown for complaint reasons
        Select<String> reasonSelect = new Select<>();
        reasonSelect.setLabel("Select the reason from the list below");
        reasonSelect.setItems("No longer needed", "Item doesn't match the description",
                "Item defective or doesn't work", "Damaged", "Items are missing", "Expiry date issues","Other...");
        reasonSelect.setPlaceholder("Please select");
        reasonSelect.setWidthFull(); // Make the select component full width
    
        TextArea complaintField = new TextArea("Complaint details");
        complaintField.setWidthFull(); // Make the text area full width
        complaintField.setHeight("150px"); // Set height for the text area
    
        Button submitButton = new Button("Submit", event -> {
            // Implement logic to handle the complaint submission (e.g., send to the server)
            String selectedReason = reasonSelect.getValue();
            String complaintDetails = complaintField.getValue();
            // Add your logic here to handle the complaint
            Notification.show("Complaint submitted: " + selectedReason);
            complainDialog.close();
            String username = (String) UI.getCurrent().getSession().getAttribute("username");
            // String message = "Complaint submitted for shop " + _shopId +", from user: "+ username + ".\n" + "The reason: " + selectedReason + ".\n" +"details:" + complaintDetails; 
            String message = "Complaint submitted for shop " + _shopId +", from user: "+ username + ".\n" + "The reason: " + selectedReason + ".\n" +"details:" + complaintDetails; 
            _presenter.openComplain(message);
        });
    
        formLayout.addFormItem(reasonSelect, "Reason");
        formLayout.addFormItem(complaintField, "Complaint details");
        formLayout.add(submitButton);
    
        // Make the form layout full width to fit the dialog
        formLayout.setWidthFull();
    
        complainDialog.add(formLayout);
        complainDialog.open();
    }
}
