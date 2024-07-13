package UI.View;

import java.util.List;
import java.util.Map;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import UI.Model.BasicDiscountDto;
import UI.Model.ProductDto;
import UI.Model.ProductGetterDto;
import UI.Presenter.ProductPresenter;

@SuppressWarnings("unused")
@PageTitle("product_page")
@Route(value = "product_page")
public class ProductView extends BaseView implements HasUrlParameter<String>{

    private ProductPresenter presenter;
    private String productName;
    private Integer productId;
    private Integer shopId;
    private boolean _isGuest;
    private Dialog viewDiscountsDialog;
    private Dialog viewPolicyDialog;
    private Dialog viewReviewsDialog;




    public ProductView(){

        // Initialize presenter
        presenter = new ProductPresenter(this, this.getServerPort());

        // Create the header component
        Header guestHeader = new BrowsePagesHeaderGuest("8080");
        Header userHeader = new BrowsePagesHeader("8080");

        shopId = (Integer) VaadinSession.getCurrent().getAttribute("shopId");
        productId = (Integer) VaadinSession.getCurrent().getAttribute("productId");
        productName = (String) VaadinSession.getCurrent().getAttribute("productName");

        _isGuest = isGuest();

        if (_isGuest) {
            add(guestHeader);
        } else {
            add(userHeader);
        }

        
        // Create welcome message
        H2 headline = new H2("Product Information");

         // Create a horizontal layout for the title to center it
        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setWidthFull(); // Make the layout take full width
        titleLayout.setJustifyContentMode(JustifyContentMode.CENTER); // Center the content
        titleLayout.add(headline);

        // Add components to the vertical layout
        add(titleLayout);

        if (shopId != null && productId != null) {
            presenter.getDetailedProduct(shopId, productId);
        } else {
            showErrorMessage("Shop ID or Product ID is missing.");
        }

    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        if (parameter != null ) {
            String[] parts = parameter.split("_");
            if (parts.length == 2) {
                int shopId = Integer.parseInt(parts[0]);
                int productId = Integer.parseInt(parts[1]);
                
                System.out.println("Shop ID: " + shopId);
                System.out.println("Product ID: " + productId);
                
                VaadinSession.getCurrent().setAttribute("shopId", shopId);
                VaadinSession.getCurrent().setAttribute("productId", productId);
            }
            else {
                showErrorMessage("Invalid parameter format");
            }
        } else {
            showErrorMessage("Parameter is null");
        }
    }

    public void displayAllProductDetails(ProductGetterDto product) {

        VerticalLayout prodcutLayout = new VerticalLayout();
        prodcutLayout.setWidth("100%"); 
        prodcutLayout.setPadding(true);
        prodcutLayout.setSpacing(true);
        prodcutLayout.setAlignItems(Alignment.CENTER);
        
        VerticalLayout productHeaderLayout = new VerticalLayout();
        VerticalLayout productPictureLayout = new VerticalLayout();
        VerticalLayout productPurchaseInfoLayout = new VerticalLayout();
        HorizontalLayout productKeywordsLayout = new HorizontalLayout();
        VerticalLayout productRatingLayout = new VerticalLayout();
        HorizontalLayout productMoreInfoButtonsLayout = new HorizontalLayout();

        // Product Name
        H3 productNameLabel = new H3("Product Name: " + product.getProductName());
        HorizontalLayout categoryAndPriceRow = new HorizontalLayout();

        // Product Category
        H5 productCategory = new H5("Category: " + product.getCategory().getCategoryName());
        productCategory.setWidth("50%");
        productCategory.getStyle().set("text-align", "center");
        // Product Price
        H5 productPrice = new H5("Price: $" + product.getPrice());
        productPrice.setWidth("50%");
        productPrice.getStyle().set("text-align", "center");

        // Set the headline component
        categoryAndPriceRow.add(productCategory, productPrice);
        categoryAndPriceRow.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        categoryAndPriceRow.setAlignItems(FlexComponent.Alignment.CENTER);
        categoryAndPriceRow.setWidthFull();
        productHeaderLayout.add(productNameLabel, categoryAndPriceRow);
        productHeaderLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, productNameLabel,categoryAndPriceRow);
        productHeaderLayout.addClassName("product-display-component");

        // Set the Picture component
        Icon packageIcon = VaadinIcon.PACKAGE.create();
        productPictureLayout.add(packageIcon);
        packageIcon.setSize("200px");
        productPictureLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, packageIcon);
        productPictureLayout.setPadding(true);
        productPictureLayout.setSpacing(true);
        productPictureLayout.addClassName("product-picture-component");


        // Set the Purchase Product component
        // Product Quantity
        H5 productQuantity = new H5("Available Quantity: " + product.getProductQuantity());
        productPurchaseInfoLayout.setPadding(true);
        productPurchaseInfoLayout.setSpacing(true);
        productPurchaseInfoLayout.add(productQuantity);
        Div priceAndControls = new Div();
        // Number field for quantity
        NumberField quantityField = new NumberField();
        quantityField.setValue(1.0);
        quantityField.setStep(1); // Adds increment and decrement buttons
        quantityField.setMin(1); // Minimum value
        quantityField.setWidth("100px"); // Set a fixed width for better alignment
        // Add value change listener to ensure quantity is not less than 1
        quantityField.addValueChangeListener(event -> {
            if (event.getValue() < 1) {
                quantityField.setValue(1.0);
            }
        });
        // Plus and minus buttons for quantity
        Button plusButton = new Button("+", event -> {
            quantityField.setValue(quantityField.getValue() + 1);
        });
        Button minusButton = new Button("-", event -> {
            if (quantityField.getValue() > 1) {
                quantityField.setValue(quantityField.getValue() - 1);
            }
        });
        // Div for quantity controls including the number field
        Div quantityControls = new Div(minusButton, quantityField, plusButton);
        priceAndControls.add(quantityControls);
        // Add to cart button
        Button addToCartButton = new Button("Add to Cart", event -> addToCart(product, quantityField.getValue().intValue()));
        productPurchaseInfoLayout.add(priceAndControls, addToCartButton);
        // Enable dragging of the product panel
        productPurchaseInfoLayout.getElement().setAttribute("draggable", "true");
        productHeaderLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, productQuantity,priceAndControls, addToCartButton);
        productPurchaseInfoLayout.addClassName("product-display-component");


        // Set the Product Keywords component
        H5 productKeywords = new H5("Keywords:" );
        productKeywordsLayout.add(productKeywords);
        for (String keyword : product.getKeywords()) {
            Span keywordTag = new Span("#" + keyword);
            keywordTag.getElement().getThemeList().add("badge");
            productKeywordsLayout.add(keywordTag);
        }
        productKeywordsLayout.setWidthFull();
        productKeywordsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        productKeywordsLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        productKeywordsLayout.addClassName("product-display-component");


        // Set the Product rating component
        double rating = product.getProductRating();
        H5 productRating = new H5 ();
        H6 ratingComment = new H6 ();
        if (rating <= 0) {
            productRating.setText("Rating: ");
            ratingComment.setText("Product has no rating yet.");
        } else {
            int ratingInt = (int) rating;
            String ratingStars = "";
            for (int i = 0; i < ratingInt; i++) {
                ratingStars += "⭐";
            }
            productRating.setText("Rating: " + ratingStars);
            ratingComment.setText("(" + rating + ", " + product.getProductRatersCounter() + " raters)");
        }
        // Product Rating
        productRatingLayout.add(productRating, ratingComment);
        productRatingLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, productRating, ratingComment);
        productRatingLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        productRatingLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        productRatingLayout.addClassName("product-display-component");

        // Create buttons
        Button productPolicyButton = new Button("Product Policy");
        productPolicyButton.addClickListener(event -> {
            // Implement logic to show product policy (not shown here)
            Notification.show("Product Policy - to be implemented");
        });
        productPolicyButton.addClassName("pointer-cursor");

        Button ProductReviewsButton = new Button("Product Reviews", event -> {
            viewReviewsDialog = createViewReviewsDialog(product.getReviews());
            viewReviewsDialog.open();
        });
        ProductReviewsButton.addClassName("pointer-cursor");

        // If need to fetch from presenter:
        // Button ProductDiscountsButton = new Button("Product Discounts", event -> {
        //     presenter.fetchProductDiscounts(discounts -> {
        //         viewDiscountsDialog = createViewDiscountsDialog(discounts);
        //         viewDiscountsDialog.open();
        //     Notification.show("Product Discounts - to be implemented");
        // });});
        
        //if productGetterDto has discounts
        Button ProductDiscountsButton = new Button("Product Discounts", event -> {
            viewDiscountsDialog = createViewDiscountsDialog(product.getProductDiscounts());
            viewDiscountsDialog.open();
        });
        ProductDiscountsButton.addClassName("pointer-cursor");

        productMoreInfoButtonsLayout.add(ProductDiscountsButton, ProductReviewsButton, productPolicyButton);
        productMoreInfoButtonsLayout.setWidthFull();
        productMoreInfoButtonsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        productMoreInfoButtonsLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        //productMoreInfoButtonsLayout.addClassName("product-display-component");

        // Add all components to the main layout
        prodcutLayout.add(
            productHeaderLayout,
            productPictureLayout,
            productPurchaseInfoLayout,
            productKeywordsLayout,
            productRatingLayout,
            productMoreInfoButtonsLayout
        );

        add(prodcutLayout);
    }

    public void displayProductStringInfo(ProductDto product) {
        removeAll(); // Clear previous content
    }


    private Dialog createViewDiscountsDialog(List<BasicDiscountDto> discounts) {
        // Create a dialog
        Dialog dialog = new Dialog();

        // Title for the dialog
        H3 title = new H3("Product Discounts");

        // Create a vertical layout to hold the title and the grid
        VerticalLayout content = new VerticalLayout();
        content.add(title);

        Grid<BasicDiscountDto> discountGrid = new Grid<>(BasicDiscountDto.class, false);
        discountGrid.addColumn(BasicDiscountDto::getId).setHeader("ID");
        discountGrid.addColumn(discount -> discount.isPrecentage() ? "Percentage" : "Amount").setHeader("Type");
        discountGrid.addColumn(BasicDiscountDto::getDiscountAmount).setHeader("Discount");
        discountGrid.addColumn(discount -> discount.getExpirationDate().toString()).setHeader("Expiration Date");
        discountGrid.setItems(discounts);

        content.add(discountGrid);

        Button closeButton = new Button("Close", event -> dialog.close());
        content.add(closeButton);

        content.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, closeButton);
        dialog.add(content);
        dialog.setWidth("800px");
        return dialog;
    }

    private Dialog createViewReviewsDialog(Map<String, String> reviews) {
        // Create a dialog
        Dialog dialog = new Dialog();

        // Title for the dialog
        H3 title = new H3("Product Reviews");

        // Create a vertical layout to hold the title and the grid
        VerticalLayout content = new VerticalLayout();
        content.add(title);

        for (Map.Entry<String, String> review : reviews.entrySet()) {
            HorizontalLayout reviewLayout = new HorizontalLayout();
            H5 reviewHeader = new H5(review.getKey());
            reviewHeader.getStyle().set("margin-right", "10px");
            reviewHeader.setWidth("30%");
            Span reviewText = new Span(review.getValue());
            reviewText.setWidth("70%");
            reviewLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
            reviewLayout.add(reviewHeader, reviewText);
            content.add(reviewLayout);
        }

        Button closeButton = new Button("Close", event -> dialog.close());
        content.add(closeButton);

        content.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, closeButton);
        dialog.add(content);
        return dialog;
    }

    private void addToCart(ProductGetterDto product, int quantity) {
        // Implement logic to add the product to the cart (not shown here)
        // Example: presenter.addToCart(product);
        presenter.addProductToCart(shopId, product.getProductId(), quantity);
        Notification.show(product.getProductName() + " added to cart successfully");
    }

}

