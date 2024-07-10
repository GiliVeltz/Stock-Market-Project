package UI.View;

import java.util.Map;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import UI.Model.BasicDiscountDto;
import UI.Model.ProductDto;
import UI.Model.ProductGetterDto;
import UI.Presenter.ProductPresenter;

@PageTitle("product_page")
@Route(value = "product_page")
public class ProductView extends BaseView implements HasUrlParameter<String>{

    private ProductPresenter presenter;
    private String productName;
    private Integer productId;
    private Integer shopId;

    
    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        if (parameter != null) {
            String[] parts = parameter.split("_");
            
            if (parts.length == 2) {
                try {
                    int shopId = Integer.parseInt(parts[0]);
                    int productId = Integer.parseInt(parts[1]);
                    
                    System.out.println("Shop ID: " + shopId);
                    System.out.println("Product ID: " + productId);
                    
                    VaadinSession.getCurrent().setAttribute("shopId", shopId);
                    VaadinSession.getCurrent().setAttribute("productId", productId);
                    
                } catch (NumberFormatException e) {
                    // Handle parsing errors if necessary
                    e.printStackTrace();
                }
            } else {
                // Handle incorrect parameter format (should be shopId/productId)
                System.err.println("Invalid parameter format: " + parameter);
            }
        } else {
            // Handle null parameter case if needed
            System.err.println("Parameter is null");
        }
    }


    public ProductView(){

        // Initialize presenter
        presenter = new ProductPresenter(this, this.getServerPort());

        shopId = (Integer) VaadinSession.getCurrent().getAttribute("shopId");
        productId = (Integer) VaadinSession.getCurrent().getAttribute("productId");
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
            presenter.getDetailedProduct(shopId, productId);
        } else {
            showErrorMessage("Shop ID or Product ID is missing.");
        }

    }

    public void displayAllProductDetails(ProductGetterDto product) {
        removeAll(); // Clear previous content

        // Product Name
    H2 productName = new H2("Product Name: " + product.getProductName());

    // Product Price
    H3 productPrice = new H3("Price: $" + product.getPrice());

    // Product Quantity
    H3 productQuantity = new H3("Available Quantity: " + product.getProductQuantity());

    // Product Keywords
    H3 productKeywords = new H3("Keywords: " + String.join(", ", product.getKeywords()));

    // Product Rating
    H3 productRating = new H3("Rating: " + product.getProductRating() + " (" + product.getProductRatersCounter() + " raters)");

    // Product Category
    H3 productCategory = new H3("Category: " + product.getCategory().toString());

    // Product Policy
    H3 productPolicyHeader = new H3("Product Policies:");
    VerticalLayout productPolicyLayout = new VerticalLayout(productPolicyHeader);
    // Placeholder for product policy rules

    // Product Discounts in a Grid
    H3 productDiscountHeader = new H3("Discounts:");
    Grid<BasicDiscountDto> discountGrid = new Grid<>(BasicDiscountDto.class, false);
    discountGrid.addColumn(BasicDiscountDto::getId).setHeader("ID");
    discountGrid.addColumn(discount -> discount.isPrecentage() ? "Percentage" : "Amount").setHeader("Type");
    discountGrid.addColumn(BasicDiscountDto::getDiscountAmount).setHeader("Discount");
    discountGrid.addColumn(discount -> discount.getExpirationDate().toString()).setHeader("Expiration Date");
    discountGrid.setItems(product.getProductDiscounts());
    VerticalLayout productDiscountLayout = new VerticalLayout(productDiscountHeader, discountGrid);
    

    // Product Reviews
    H3 productReviewsHeader = new H3("Reviews:");
    VerticalLayout productReviewsLayout = new VerticalLayout(productReviewsHeader);
    for (Map.Entry<String, String> review : product.getReviews().entrySet()) {
        productReviewsLayout.add(new Paragraph(review.getKey() + ": " + review.getValue()));
    }

    // Add all components to the main layout
    add(
        productName,
        productPrice,
        productQuantity,
        productKeywords,
        productRating,
        productCategory,
        productPolicyLayout,
        productDiscountLayout,
        productReviewsLayout
    );
    }

    public void displayProductStringInfo(ProductDto product) {
        removeAll(); // Clear previous content

        // // Display product details
        // add(new H1("Product Details"));
        // add(new Span("Product Name: " + product.getProductName()));
        // add(new Span("Category: " + product.getCategory()));
        // add(new Span("Price: $" + product.getPrice()));
        // add(new Span("Quantity: " + product.getQuantity()));


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

