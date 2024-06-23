package UI.View;

import java.util.List;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import UI.Model.ProductDto;
import UI.Presenter.SearchProductsPresenter;

@PageTitle("Search Results Page")
@Route(value = "products_search_results")
public class SearchResultsView extends BaseView {
    private SearchProductsPresenter presenter;
    private boolean resultsVisible = false;  // Track if search results are visible

    public SearchResultsView(SearchProductsPresenter presenter) {
        // Initialize presenter
        this.presenter = presenter;

        // Add "Search Results" title
        H1 title = new H1("Search Results");
        add(title);

        // Add close button
        Button closeButton = new Button("Close");
        closeButton.addClickListener(event -> {
            // Handle close button click
            clearSearchResults();
        });
        add(closeButton);
        closeButton.setVisible(false);  // Initially hide the close button
    }

    public void createProductsInShopButtons(String shopName, List<ProductDto> products) {
        if (!resultsVisible) {
            resultsVisible = true;
        }

        // Check if there are no products for this shop
        if (products.isEmpty()) {
            add(new Paragraph("No products found for " + shopName));
            return;
        }

        // Create a vertical layout for the grid
        VerticalLayout gridLayout = new VerticalLayout();
        gridLayout.setAlignItems(Alignment.START);

        // Set a maximum of 3 buttons per row
        int maxButtonsPerRow = 3;
        HorizontalLayout rowLayout = new HorizontalLayout();

        // Iterate through each product
        for (int i = 0; i < products.size(); i++) {
            ProductDto product = products.get(i);

            // Create a button for the product with essential data
            Button productButton = new Button(product.getProductName());  // Display product name
            productButton.addClassName("product-button");

            // Set tooltip with product details (e.g., category and price)
            productButton.getElement().setAttribute("title", "Category: " + product.getCategory() + ", Price: $" + product.getPrice());

            productButton.addClickListener(event -> {
                // Handle click event (e.g., navigate to product details)
                navigateToProductDetails(product.getProductName());  // Assuming getId() retrieves the product ID
            });

            // Add the button to the row layout
            rowLayout.add(productButton);

            // Check if we reached the maximum buttons per row or it's the last product
            if ((i + 1) % maxButtonsPerRow == 0 || i == products.size() - 1) {
                gridLayout.add(rowLayout);  // Add the completed row to the grid layout
                rowLayout = new HorizontalLayout();  // Reset row layout for the next row
            }
        }

        // Add a label for the shop name above the grid layout
        H5 shopNameLabel = new H5(shopName);
        shopNameLabel.addClassName("shop-name-label");
        add(shopNameLabel);
        add(gridLayout);  // Add the grid layout to the main layout

        // Show close button when results are visible
        if (resultsVisible) {
            getComponentAt(2).setVisible(true);  // Assuming close button is at index 2
        }
    }

    public void clearSearchResults() {
        removeAll();  // Clear all components from the view
        resultsVisible = false;  // Reset results visibility
        H1 title = new H1("Search Results");
        add(title);  // Add "Search Results" title back
    }

    public void navigateToProductDetails(String productName) {
        getUI().ifPresent(ui -> ui.navigate("product" + productName));
    }
}

