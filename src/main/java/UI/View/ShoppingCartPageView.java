package UI.View;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import UI.Model.BasketDto;
import UI.Model.PaymentInfoDto;
import UI.Model.SupplyInfoDto;
import UI.Presenter.ShoppingCartPagePresentor;

@PageTitle("Shopping Cart Page")
@Route(value = "user_cart")
public class ShoppingCartPageView extends BaseView {
    private ShoppingCartPagePresentor presenter;
    //private H1 _title;
    private Grid<BasketDto> grid = new Grid<>(BasketDto.class);
    private Map<BasketDto, Boolean> selectedItems = new HashMap<>();
    private boolean isGuest;
    private List<BasketDto> basketList;

    public ShoppingCartPageView() {
        // Initialize presenter
        presenter = new ShoppingCartPagePresentor(this);

        // Create the header component
        Header guestHeader = new BrowsePagesHeaderGuest("8080");
        Header userHeader = new BrowsePagesHeader("8080");

        isGuest = isGuest();

        if (isGuest) {
            add(guestHeader);
        } else {
            add(userHeader);
        }

        presenter.viewCart();
        Button openDialogButton = new Button("Checkout", e -> openCheckoutDialog());
        add(grid, openDialogButton);
    }

    public void showBaskets(List<BasketDto> baskets) {
        // Store the basket list for index retrieval
        basketList = baskets;

        // Create a grid bound to the Product class
        grid.setItems(baskets);

        // Configure the grid to show specific properties of the Product class
        configureGrid();
    }

    private void configureGrid() {
        grid.setColumns(); // Clear any default columns

        // Add a checkbox column
        grid.addColumn(new ComponentRenderer<>(basketDto -> {
            Checkbox checkbox = new Checkbox();
            checkbox.setValue(isGuest); // Automatically check for guests
            checkbox.setEnabled(!isGuest); // Disable checkbox for guests
            checkbox.addValueChangeListener(event -> {
                selectedItems.put(basketDto, event.getValue());
            });
            if (isGuest) {
                selectedItems.put(basketDto, true); // Mark as selected for guests
            }
            return checkbox;
        })).setHeader("Select");

        // Shop ID column
        grid.addColumn(BasketDto::getShopID)
                .setHeader("Shop ID")
                .setKey("_shopID");

        // Product IDs column
        grid.addColumn(createProductIDsRenderer())
                .setHeader("Product IDs")
                .setKey("_productIDs");

        // Total Price column
        grid.addColumn(BasketDto::getTotalPrice)
            .setHeader("Total Price")
            .setKey("_totalPrice");
    }

    private Renderer<BasketDto> createProductIDsRenderer() {
        return new TextRenderer<>(basketDto -> 
            basketDto.getProductIDs().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", ")));
    }

    private void openRemoveItemDialog(BasketDto basketDto) {
        Dialog dialog = new Dialog();
        FormLayout formLayout = new FormLayout();

        H3 removeItemHeader = new H3("Remove Item from Basket:");

        // ComboBox for Product ID
        ComboBox<Integer> productIDComboBox = new ComboBox<>("Product ID");
        productIDComboBox.setItems(basketDto.getProductIDsCount().keySet());

        // Number field for quantity
        NumberField quantityField = new NumberField("Quantity");
        quantityField.setMin(1);

        // Set max based on selected product ID
        productIDComboBox.addValueChangeListener(event -> {
            Integer selectedProductID = event.getValue();
            if (selectedProductID != null) {
                long maxQuantity = basketDto.getProductIDsCount().getOrDefault(selectedProductID, 1L);
                quantityField.setMax(maxQuantity);
                quantityField.setValue(1.0); // Set default value
            }
        });

        formLayout.add(removeItemHeader, productIDComboBox, quantityField);

        Button submitButton = new Button("Submit", event -> {
            // Validate and get input values
            Integer productID = productIDComboBox.getValue();
            Double quantityValue = quantityField.getValue();
        
            if (productID != null && quantityValue != null) {
                long maxQuantity = basketDto.getProductIDsCount().getOrDefault(productID, 1L);
                int quantity = Math.min(quantityValue.intValue(), (int) maxQuantity);
        
                // Adjust the quantity field if it exceeds the maximum allowed
                if (quantity != quantityValue.intValue()) {
                    quantityField.setValue((double) quantity);
                }
        
                // Call the presenter to remove the item
                presenter.removeItemFromCart(basketDto.getShopID(), productID, quantity);
        
                // Close the dialog
                dialog.close();
            } else {
                // Handle invalid input
                if (productID == null) {
                    productIDComboBox.setInvalid(true);
                }
                if (quantityValue == null) {
                    quantityField.setInvalid(true);
                }
            }
        });

        formLayout.add(submitButton);
        dialog.add(formLayout);
        dialog.open();
    }

    public boolean isGuest() {
        String username = (String) UI.getCurrent().getSession().getAttribute("username");
        return username == null;
    }

    private void openCheckoutDialog() {
        Dialog dialog = new Dialog();

        FormLayout formLayout = new FormLayout();

        H3 paymentInfoHeader = new H3("Payment Information:");
        H3 supplyInfoHeader = new H3("Supply Information:");

        // Payment fields
        TextField currencyField = new TextField("Currency");
        TextField cardNumberField = new TextField("Card Number");
        TextField monthField = new TextField("Month");
        TextField yearField = new TextField("Year");
        TextField holderField = new TextField("Card Holder Name");
        TextField cvvField = new TextField("CVV");
        TextField idField = new TextField("ID");

        // Supply fields
        TextField nameField = new TextField("Name");
        TextField addressField = new TextField("Address");
        TextField cityField = new TextField("City");
        TextField countryField = new TextField("Country");
        TextField zipField = new TextField("ZIP Code");

        formLayout.add(paymentInfoHeader);
        formLayout.add(currencyField, cardNumberField, monthField, yearField, holderField, cvvField, idField);

        formLayout.add(supplyInfoHeader);
        formLayout.add(nameField, addressField, cityField, countryField, zipField);

        Button submitButton = new Button("Submit", event -> {
            // Handle form submission for payment or supply
            String currency = currencyField.getValue();
            String cardNumber = cardNumberField.getValue();
            String month = monthField.getValue();
            String year = yearField.getValue();
            String holder = holderField.getValue();
            String cvv = cvvField.getValue();
            String id = idField.getValue();

            String name = nameField.getValue();
            String address = addressField.getValue();
            String city = cityField.getValue();
            String country = countryField.getValue();
            String zip = zipField.getValue();

            List<Integer> selectedIndexes = getSelectedIndexes();

            PaymentInfoDto paymentInfo = new PaymentInfoDto(currency, cardNumber, month, year, holder, cvv, id);
            SupplyInfoDto supplyInfo = new SupplyInfoDto(name, address, city, country, zip);

            presenter.purchaseCart(paymentInfo, supplyInfo, selectedIndexes);
            dialog.close();
        });

        formLayout.add(submitButton);

        dialog.add(formLayout);
        dialog.open();
    }

    public List<Integer> getSelectedIndexes() {
        return selectedItems.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(entry -> basketList.indexOf(entry.getKey()))
                .collect(Collectors.toList());
    }
}
