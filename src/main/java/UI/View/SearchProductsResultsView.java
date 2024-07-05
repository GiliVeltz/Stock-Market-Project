package UI.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import UI.Model.ProductDto;
import UI.Model.ShopDto;
import UI.Presenter.SearchProductsPresenter;

@PageTitle("Search Products Results Page")
@Route(value = "products_search_results")
public class SearchProductsResultsView extends BaseView {
    //private SearchProductsPresenter presenter;
    Dialog resultsDialog;
    private final List<VerticalLayout> shopLayoutsList;

    // Filter fields
    private TextField minPriceField;
    private TextField maxPriceField;
    private CheckboxGroup<String> categoryCheckboxGroup;
    private TextField minProductRatingField;
    private TextField maxProductRatingField;
    private TextField minShopRatingField;
    private TextField maxShopRatingField;

    public SearchProductsResultsView(SearchProductsPresenter presenter) {
        // Initialize presenter
        //this.presenter = presenter;
        this.shopLayoutsList = new ArrayList<>();
        resultsDialog = new Dialog();

        // Create filter sidebar
        VerticalLayout sidebar = createFilterSidebar();
        add(sidebar);
    }

    private VerticalLayout createFilterSidebar() {
        VerticalLayout sidebar = new VerticalLayout();

        // Price Range Filter
        H5 priceRangeLabel = new H5("Price Range");
        minPriceField = new TextField("Min Price");
        maxPriceField = new TextField("Max Price");

        // Product Category Filter
        H5 categoryLabel = new H5("Product Category");
        categoryCheckboxGroup = new CheckboxGroup<>();
        categoryCheckboxGroup.setItems("Electronics", "Clothing", "Books", "Home & Kitchen"); // Add your categories

        // Product Rating Filter
        H5 productRatingLabel = new H5("Product Rating");
        minProductRatingField = new TextField("Min Rating");
        maxProductRatingField = new TextField("Max Rating");

        // Shop Rating Filter
        H5 shopRatingLabel = new H5("Shop Rating");
        minShopRatingField = new TextField("Min Rating");
        maxShopRatingField = new TextField("Max Rating");

        // Apply Filters Button
        Button applyFiltersButton = new Button("Apply Filters");
        applyFiltersButton.addClickListener(event -> applyFilters());

        // Add components to the sidebar
        sidebar.add(priceRangeLabel, minPriceField, maxPriceField, categoryLabel, categoryCheckboxGroup,
                productRatingLabel, minProductRatingField, maxProductRatingField, shopRatingLabel, minShopRatingField,
                maxShopRatingField, applyFiltersButton);
        sidebar.setPadding(true);
        sidebar.setSpacing(true);
        sidebar.setAlignItems(FlexComponent.Alignment.START);

        return sidebar;
    }

    private void applyFilters() {
        try {
            double minPrice = Double.parseDouble(minPriceField.getValue());
            double maxPrice = Double.parseDouble(maxPriceField.getValue());
            double minProductRating = Double.parseDouble(minProductRatingField.getValue());
            double maxProductRating = Double.parseDouble(maxProductRatingField.getValue());
            double minShopRating = Double.parseDouble(minShopRatingField.getValue());
            double maxShopRating = Double.parseDouble(maxShopRatingField.getValue());

            List<String> categories = new ArrayList<>(categoryCheckboxGroup.getSelectedItems());

            // Implement the logic to filter products based on the criteria
            // For demonstration, we'll filter using static data.
            // You should implement the actual filtering logic here.
            Notification.show("Filters applied. Implement the filter logic here.");
        } catch (NumberFormatException e) {
            Notification.show("Please enter valid numbers for filters.");
        }
    }
    public void displayResponseShopNotFound (String shopName) {
        clearSearchResults();  // Clear previous search results

        // create vertical Layout for the search results
        VerticalLayout dialogContent = new VerticalLayout();

        // Add "Search Results" title
        H2 headline = new H2("Search Results");
        headline.getStyle().set("margin", "0");
        dialogContent.add(headline);

        createNoResultsLayout(false, shopName);

        // Add the shop layouts to the main layout
        for (VerticalLayout shopLayout : shopLayoutsList) {
            dialogContent.add(shopLayout);
        }
         // Add close button
         Button closeButton = new Button("Close");
         closeButton.addClickListener(event -> {
             // Handle close button click
             clearSearchResults();
             resultsDialog.close();
         });
         closeButton.addClassName("pointer-cursor");
         dialogContent.add(closeButton);

         dialogContent.setAlignItems(FlexComponent.Alignment.CENTER);
         resultsDialog.add(dialogContent);
         resultsDialog.open();
    }

    public void displayResponseProducts (Map<ShopDto, List<ProductDto>> shopNameToProducts) {
        clearSearchResults();  // Clear previous search results

        // create vertical Layout for the search results
        VerticalLayout dialogContent = new VerticalLayout();

        // Add "Search Results" title
        H2 headline = new H2("Search Results");
        headline.getStyle().set("margin", "0");
        dialogContent.add(headline);

        if (shopNameToProducts.isEmpty()) {
            createNoResultsLayout(true, "All Shops");
        }

        for (Map.Entry<ShopDto, List<ProductDto>> entry : shopNameToProducts.entrySet()) {
            if (entry.getValue().isEmpty()) {
                createNoResultsLayout(true, entry.getKey().getShopName());
            } else {
                createShopLayout(entry.getKey(), entry.getValue());
            }
        }
        // Add the shop layouts to the main layout
        for (VerticalLayout shopLayout : shopLayoutsList) {
            dialogContent.add(shopLayout);
        }
         // Add close button
         Button closeButton = new Button("Close");
         closeButton.addClickListener(event -> {
             // Handle close button click
             clearSearchResults();
             resultsDialog.close();
         });
         closeButton.addClassName("pointer-cursor");
         dialogContent.add(closeButton);

         dialogContent.setAlignItems(FlexComponent.Alignment.CENTER);
         resultsDialog.add(dialogContent);
         resultsDialog.open();


    }

    private void createShopLayout(ShopDto shopDto, List<ProductDto> productsList) {
        // Create a vertical layout for the grid
        VerticalLayout gridLayout = new VerticalLayout();
        gridLayout.setAlignItems(Alignment.START);
        gridLayout.addClassName("light-component-container");

        H3 shopNameLabel = new H3(shopDto.getShopName());
        shopNameLabel.addClassName("shop-name-label");
        H5 shopRatingLabel = new H5("Rating: " + shopDto.getShopRating());
        gridLayout.add(shopNameLabel, shopRatingLabel);
        
        // Set a maximum of 3 buttons per row
        int maxButtonsPerRow = 3;
        HorizontalLayout rowLayout = new HorizontalLayout();
        int count = 0;

        for (ProductDto product : productsList) {
            Button productButton = createProductInShopButton(product);
            productButton.addClassName("product-button");
            productButton.addClassName("pointer-cursor");
            rowLayout.add(productButton);
            if ((count + 1) % maxButtonsPerRow == 0 || count == productsList.size() - 1) {
                gridLayout.add(rowLayout);
                rowLayout = new HorizontalLayout();
            }

            count++;
        }

        // Add the grid layout to the main layout
        gridLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        shopLayoutsList.add(gridLayout);
    }

    private void createNoResultsLayout(Boolean isExist, String shopName) {
        // Create a vertical layout for the grid
        VerticalLayout gridLayout = new VerticalLayout();
        gridLayout.setAlignItems(Alignment.START);
        gridLayout.addClassName("light-component-container");

        H3 shopNameLabel = new H3(shopName);
        shopNameLabel.addClassName("shop-name-label");
        gridLayout.add(shopNameLabel);

        H5 noResultsLabel = new H5();
        if (isExist) {
            noResultsLabel.add("No results were found.");
        } else {
            noResultsLabel.add("Shop with the given name does not exist, please try again.");
        }
        gridLayout.add(noResultsLabel);

        // Add the grid layout to the main layout
        gridLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        shopLayoutsList.add(gridLayout);
    }


    private Button createProductInShopButton(ProductDto product) {

        Button productButton = new Button(product.getProductName());  // Display product name
        productButton.addClassName("product-button");

        productButton.addClickListener(event -> {
            showProductDialog(product);  // Open a dialog with product details
        });

        return productButton;
    }

    public void showProductDialog(ProductDto product) {
        // Create a dialog with shop details
        Dialog dialog = new Dialog();
        dialog.setModal(true); // Make the dialog modal (blocks interaction with other UI elements)
        // Set the dialog content
        VerticalLayout dialogContent = new VerticalLayout();
        String productName = product.getProductName().substring(0, 1).toUpperCase() + product.getProductName().substring(1);
        dialogContent.add(new H3(productName));
        dialogContent.add(new Div());
        Span categorySpan = new Span("Category: " + product.getCategory());
        Span priceSpan = new Span("Price: " + product.getPrice());
        Span ratingSpan = new Span("Rating: " + product.getProductRating());
        dialogContent.add(categorySpan, priceSpan, ratingSpan);
        dialogContent.add(new Div());


        Button addToCartButton = new Button("Add To Cart", event -> addToCart(product));
        addToCartButton.addClassName("pointer-cursor");
        addToCartButton.setWidth("150px");

        Button produtPageButton = new Button("Product Page", event -> {
            dialog.close();
            navigateToProductDetails(product);
        });
        produtPageButton.addClassName("pointer-cursor");
        produtPageButton.setWidth("150px");

        HorizontalLayout productButtonsLayout = new HorizontalLayout(produtPageButton, addToCartButton);
        dialogContent.add(productButtonsLayout);

        Button closeButton = new Button("Close", event -> dialog.close());
        closeButton.addClassName("pointer-cursor");
        dialogContent.add(closeButton);
        
        dialogContent.setAlignItems(FlexComponent.Alignment.CENTER);
        dialog.add(dialogContent);
        dialog.open();
    }

    private void addToCart(ProductDto product) {
        // Implement logic to add the product to the cart (not shown here)
        // Example: presenter.addToCart(product);
        Notification.show(product.getProductName() + " Add To Cart is not Implemented yet");
    }

    public void navigateToProductDetails(ProductDto product) {
        // getUI().ifPresent(ui -> ui.navigate("product" + productName));
        Notification.show(product.getProductName() + " Nevigating to Product Page is not Implemented yet");
    }


    public void clearSearchResults() {
        shopLayoutsList.clear();
        resultsDialog.removeAll();
        removeAll();  // Clear all components from the view
    }
}

