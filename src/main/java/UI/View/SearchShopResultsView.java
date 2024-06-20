package UI.View;

import java.util.List;
import java.util.Map;
import java.util.Map;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import UI.Model.ShopDto;
import UI.Model.ProductDto;

import UI.Presenter.searchShopsPresenter;

@PageTitle("Search Shops Page")
@Route(value = "search shops")
public class SearchShopResultsView extends BaseView{

    private searchShopsPresenter presenter;

    private String shopName;
    private Integer shopId;

    // UI Components for displaying shop details and products
    private Div shopDetailsDiv;
    private VerticalLayout productsLayout;
    private Div productDetailsDiv;

    public SearchShopResultsView() {
        shopName = (String) VaadinSession.getCurrent().getAttribute("searchShopName");
        shopId = (Integer) VaadinSession.getCurrent().getAttribute("searchShopId");

        // Create the header component
        Header header = new BrowsePagesHeader("8080");
        add(header);

        presenter = new searchShopsPresenter(this);
        
        // Create a title for the page
        H1 headlineShops = new H1("Shops Results");

        // Initialize the UI components
        shopDetailsDiv = new Div();
        productsLayout = new VerticalLayout();
        productDetailsDiv = new Div();

        // Add components to the view
        add(headlineShops, shopDetailsDiv, productsLayout, productDetailsDiv);

        if(shopId != -1){presenter.searchShopByID();}
        else if(shopName != "") {presenter.searchShopByName();}
        else {showErrorMessage("No shop ID or name provided");}

    }

//    public void displaySearchResults(Map<ShopDto, List<Dtos.ProductDto>> shopMap) {
//         // Clear previous results
//         shopDetailsDiv.removeAll();
//         productsLayout.removeAll();

//         // Display shop details and products
//         for (Map.Entry<ShopDto, List<ProductDto>> entry : shopMap.entrySet()) {
//             ShopDto shop = entry.getKey();
//             List<ProductDto> products = entry.getValue();

//             // Display shop details
//             shopDetailsDiv.add(new Paragraph("Shop Name: " + shop.getShopName()));
//             shopDetailsDiv.add(new Paragraph("Shop Address: " + shop.getShopAddress()));
//             shopDetailsDiv.add(new Paragraph("Shop Bank: " + shop.getBankDetails()));
//             shopDetailsDiv.add(new Paragraph("Shop Rating: " + (shop.getShopRating() == -1 ? "None" : shop.getShopRating())));
//             shopDetailsDiv.add(new Paragraph("Raters Count: " + shop.getShopRatersCounter()));
//             shopDetailsDiv.add(new Paragraph("Shop Status: " + (shop.getIsShopClosed() ? "Closed" : "Open")));

//             // Display products with buttons
//             for (ProductDto product : products) {
//                 Button productButton = new Button(product.getProductName(), event -> displayProductDetails(product));
//                 productsLayout.add(productButton);
//             }
//         }
//     }

    private void displayProductDetails(ProductDto product) {
        // Clear previous product details
        productDetailsDiv.removeAll();

        // Display selected product details
        productDetailsDiv.add(new Paragraph("Product Name: " + product.getProductName()));
        productDetailsDiv.add(new Paragraph("Category: " + product.getCategory()));
        productDetailsDiv.add(new Paragraph("Price: $" + product.getPrice()));
        // productDetailsDiv.add(new Paragraph("Quantity: " + product.getProductQuantity()));

        // Price and quantity controls
        Div priceAndControls = new Div();
        priceAndControls.add(new H3("Price: $" + product.getPrice()));

        // Plus and minus buttons for quantity
        Button plusButton = new Button("+");
        Button minusButton = new Button("-");
        Div quantityControls = new Div(plusButton, minusButton);
        priceAndControls.add(quantityControls);
    }


}
