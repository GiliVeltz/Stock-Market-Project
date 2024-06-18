package UI.View;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import UI.Model.ProductDto;
import UI.Presenter.ProductPresenter;

@PageTitle("Product Page")
@Route(value = "product")
public class ProductView extends BaseView{

    private ProductPresenter presenter;
    private String productName = "";
    private String productId;
    private String shopId;


    public ProductView(){

        // Initialize presenter
        presenter = new ProductPresenter(this);

        shopId = (String) VaadinSession.getCurrent().getAttribute("shopId");
        productId = (String) VaadinSession.getCurrent().getAttribute("productId");
        productName = (String) VaadinSession.getCurrent().getAttribute("productName");
        // Create welcome message
        H1 headline = new H1(productName + " Information");

        // Create the header component
        Header header = new LoggedInHeader("8080");

        // Create buttons
        Button addProductToCartButton = new Button();
        Button removeProductFromCartButton = new Button();

        addProductToCartButton.addClassName("pointer-cursor");
        removeProductFromCartButton.addClassName("pointer-cursor");
        addProductToCartButton.addClassName("same-size-button");
        removeProductFromCartButton.addClassName("same-size-button");

        // Create vertical layout for buttons
        VerticalLayout buttonLayout = new VerticalLayout(addProductToCartButton, removeProductFromCartButton);
        buttonLayout.setAlignItems(Alignment.END);

         // Create a horizontal layout for the title to center it
        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setWidthFull(); // Make the layout take full width
        titleLayout.setJustifyContentMode(JustifyContentMode.CENTER); // Center the content
        titleLayout.add(headline);

        // Add components to the vertical layout
        add(header, titleLayout, buttonLayout);

        if (shopId != null && productId != null) {
            presenter.productInfo(shopId, productId);
        } else {
            showErrorMessage("Shop ID or Product ID is missing.");
        }

    }

    public void displayProductInfo(ProductDto product) {
        removeAll(); // Clear previous content

        // Display product details
        add(new H1("Product Details"));
        add(new Span("Product Name: " + product.getProductName()));
        add(new Span("Category: " + product.getCategory()));
        add(new Span("Price: $" + product.getPrice()));
        add(new Span("Quantity: " + product.getQuantity()));


        //TODO: add review and rating

        // // Add reviews and ratings if they exist
        // if (product.getReviews() != null && !product.getReviews().isEmpty()) {
        //     add(new H1("Reviews"));
        //     for (String review : product.getReviews()) {
        //         add(new Span(review));
        //     }
        // } else {
        //     add(new Span("No reviews available."));
        // }

        // if (product.getRating() != null) {
        //     add(new H1("Rating"));
        //     add(new Span("Rating: " + product.getRating()));
        // } else {
        //     add(new Span("No rating available."));
        // }
    }

}

