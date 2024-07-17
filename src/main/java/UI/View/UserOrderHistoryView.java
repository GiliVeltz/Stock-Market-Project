package UI.View;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;

import UI.Model.OrderDto;
import UI.Model.ShoppingBasketDto;
import UI.Presenter.ReviewOrderPresenter;
import UI.Presenter.UserOrderHistoryPresenter;

public class UserOrderHistoryView extends BaseView {
    private UserOrderHistoryPresenter presenter;
    private ReviewOrderPresenter reviewsPresenter;
    private final Div ordersDiv;
    private Grid<OrderDto> orderGrid;
    private static final String FILLED_STAR = "⭐";
    private HorizontalLayout starLayout;
    private Span[] stars = new Span[5];
    private int currentRating = 0;

    public UserOrderHistoryView() {
        presenter = new UserOrderHistoryPresenter(this);
        reviewsPresenter = new ReviewOrderPresenter(this);

        // Initialize starLayout
        starLayout = createStarRatingLayout();
        starLayout.setVisible(false);

        // Initialize the grid
        orderGrid = new Grid<>(OrderDto.class, false);
        orderGrid.addColumn(OrderDto::getOrderId).setHeader("Order ID");

        // Add a button to expand each order and show details
        orderGrid.addComponentColumn(orderDto -> {
            Button detailsButton = new Button("Show Details");
            detailsButton.addClickListener(e -> showOrderDetails(orderDto));
            return detailsButton;
        }).setHeader("Actions");

        ordersDiv = new Div();
        ordersDiv.setId("orders-div");

        // Add components to the layout
        add(orderGrid); // Add the grid directly to the layout
        add(ordersDiv);

        // Load order history
        presenter.viewOrderHistory();
    }

    public void showOrders(List<OrderDto> orders) {
        orderGrid.setItems(orders);
    }

    private void showOrderDetails(OrderDto orderDto) {
        // Create a layout to hold the order details
        VerticalLayout detailsLayout = new VerticalLayout();

        // Add details with bold labels
        detailsLayout.add(createDetailSpan("Order ID: ", orderDto.getOrderId().toString()));
        detailsLayout.add(createDetailSpan("Total Order Amount: ", String.valueOf(orderDto.getTotalOrderAmount())));

        // Add the shopping baskets for this order
        for (Map.Entry<Integer, ShoppingBasketDto> entry : orderDto.getShoppingBasketMap().entrySet()) {
            ShoppingBasketDto basketDto = entry.getValue();
            
            // Create a bordered div for each basket
            Div basketDiv = new Div();
            basketDiv.getStyle().set("border", "1px solid black");
            basketDiv.getStyle().set("padding", "10px");
            basketDiv.getStyle().set("margin", "10px 0");

            // Create a vertical layout for basket details
            VerticalLayout basketDetailsLayout = new VerticalLayout();
            basketDetailsLayout.add(createDetailSpan("Shop ID: ", entry.getKey().toString()));
            basketDetailsLayout.add(createDetailSpan("Shop Name: ", basketDto.getShop().getShopName()));
            basketDetailsLayout.add(createDetailSpan("Basket Total Amount: ", String.valueOf(basketDto.getBasketTotalAmount())));

            // Format the product IDs with quantities
            String formattedProductIds = basketDto.getProductIdList().stream()
                .collect(Collectors.groupingBy(id -> id, Collectors.counting()))
                .entrySet().stream()
                .map(e -> e.getKey() + " (" + e.getValue() + ")")
                .collect(Collectors.joining(", "));

            basketDetailsLayout.add(createDetailSpan("Product IDs: ", formattedProductIds));

            basketDiv.add(basketDetailsLayout);
            detailsLayout.add(basketDiv);
        }

        // Create a dialog to display the details
        Dialog dialog = new Dialog();
        dialog.add(detailsLayout);
        dialog.setWidth("600px"); // Increase the width of the dialog
        dialog.setHeight("500px"); // Increase the height of the dialog

        HorizontalLayout buttonsLayout = new HorizontalLayout();

        // Add a close button to the dialog
        Button closeButton = new Button("Close", e -> dialog.close());
        closeButton.addClassName("pointer-cursor");
        Button reviewOrderButton = new Button("Review Order", e -> {
            dialog.close();
            // Call the method to create the review order dialog
            createReviewOrderDialog(orderDto);
        });
        reviewOrderButton.addClassName("pointer-cursor");

        buttonsLayout.add(closeButton, reviewOrderButton);
        buttonsLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        buttonsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        detailsLayout.add(buttonsLayout);

        // Open the dialog
        dialog.open();
    }

    public void createReviewOrderDialog(OrderDto orderDto) {
        // Create a layout to hold the review order form
        Dialog dialog = new Dialog();
        VerticalLayout reviewLayout = new VerticalLayout();
        HorizontalLayout reviewButtonsLayout = new HorizontalLayout();

        H3 reviewOrderLabel = new H3("Review Order");
        Button reviewShopButton = new Button ("Review Shop", e -> createReviewShopDialog(orderDto));
        reviewShopButton.addClassName("pointer-cursor");
        Button reviewProductButton = new Button ("Review Product", e -> createReviewProductDialog(orderDto));
        reviewProductButton.addClassName("pointer-cursor");
        reviewButtonsLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        reviewButtonsLayout.add(reviewShopButton, reviewProductButton);

        Button closeButton = new Button ("Close", e -> dialog.close());
        closeButton.addClassName("pointer-cursor");

        reviewLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        reviewLayout.add(reviewOrderLabel, reviewButtonsLayout, closeButton);
        
        dialog.add(reviewLayout);
        dialog.open();

    }


    public void createReviewShopDialog(OrderDto orderDto) {
        // Create a layout to hold the review order form
        Dialog dialog = new Dialog();
        VerticalLayout reviewLayout = new VerticalLayout();
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        Integer[] shopIdRatingValue = {null, null}; //shopIdRatingValue[0] = shopId, shopIdRatingValue[1] = ratingValue

        H3 reviewShopLabel = new H3("Review Shop");
        ComboBox<Integer> shopIdComboBox = new ComboBox<>("Select Shop ID");
        shopIdComboBox.setItems(orderDto.getShoppingBasketMap().keySet());
        shopIdComboBox.setReadOnly(false);
        Button enterShopIdButton = new Button("Enter");
        enterShopIdButton.setEnabled(true);

        Button resetButton = new Button("Reset");
        resetButton.addClassName("pointer-cursor");
        Button closeButton = new Button("Close");
        closeButton.addClassName("pointer-cursor");
        Button submitButton = new Button("Submit");
        submitButton.setVisible(false);
        submitButton.addClassName("pointer-cursor");

        resetButton.addClickListener(e -> {
            cleanAndHideStarsLayout(submitButton);
            shopIdComboBox.setReadOnly(false);
            shopIdComboBox.clear();                    
            enterShopIdButton.setEnabled(true);
            shopIdRatingValue[0] = null;
            shopIdRatingValue[1] = null;
        });

        closeButton.addClickListener(e -> {
            cleanAndHideStarsLayout(submitButton);
            shopIdComboBox.setReadOnly(false);
            shopIdComboBox.clear();
            shopIdRatingValue[0] = null;
            shopIdRatingValue[1] = null;
            dialog.close();
        });

        submitButton.addClickListener(e -> {
            shopIdRatingValue[1] = getCurrentRating();
            if (shopIdRatingValue[1] == 0) {
                showErrorMessage("No rating was chosen.");
            } else {
                reviewsPresenter.addShopRating(shopIdRatingValue[0], shopIdRatingValue[1]);
                showSuccessMessage("Shop was rated successfully");
                cleanAndHideStarsLayout(submitButton);
            }
        });

        enterShopIdButton.addClickListener(event -> {
            shopIdRatingValue[0] = shopIdComboBox.getValue();
            if (shopIdRatingValue[0] != null) {
                if (orderDto.getShoppingBasketMap().containsKey(shopIdRatingValue[0])) {
                    cleanAndDisplayStarsLayout(submitButton);
                    shopIdComboBox.setReadOnly(true);
                    enterShopIdButton.setEnabled(false);
                } else {
                    shopIdRatingValue[0] = null;
                    showErrorMessage("Shop ID not found in the order.");
                    shopIdComboBox.setReadOnly(false);
                    shopIdComboBox.clear();
                    enterShopIdButton.setEnabled(true);
                }
            }
        });

        buttonsLayout.add(resetButton, submitButton, closeButton);
        buttonsLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        reviewLayout.add(reviewShopLabel, shopIdComboBox, enterShopIdButton, starLayout, buttonsLayout);
        reviewLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        dialog.add(reviewLayout);
        dialog.open();
    }




    public void createReviewProductDialog(OrderDto orderDto) {
        // Create a layout to hold the review order form
        Dialog dialog = new Dialog();
        VerticalLayout reviewLayout = new VerticalLayout();
        HorizontalLayout shopSelectionLayout = new HorizontalLayout();
        HorizontalLayout ProductSelectionLayout = new HorizontalLayout();
        VerticalLayout productRateAndReviewLayout = new VerticalLayout();
        HorizontalLayout buttonsLayout = new HorizontalLayout();

        Set<Integer> productIdList = new HashSet<>();
        Integer[] shopIdProductIdRatingValue = {null, null, null}; //shopIdProductIdRatingValue[0] = shopId, shopIdProductIdRatingValue[1] = productId, shopIdProductIdRatingValue[2] = ratingValue
        H3 reviewProductLabel = new H3("Review Product");

        /////////////////////////////////////

        ComboBox<Integer> shopIdComboBox = new ComboBox<>("Select Shop ID");
        shopIdComboBox.setItems(orderDto.getShoppingBasketMap().keySet());
        Button enterShopIdButton = new Button("Enter");
        enterShopIdButton.setEnabled(false);
        enterShopIdButton.addClassName("pointer-cursor");

        shopIdComboBox.addValueChangeListener(e -> {
            enterShopIdButton.setEnabled(true);
        });

        shopSelectionLayout.add(shopIdComboBox, enterShopIdButton);
        shopSelectionLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        shopSelectionLayout.setVerticalComponentAlignment(FlexComponent.Alignment.END, shopIdComboBox, enterShopIdButton);

        //////////////////////////////////

        ComboBox<Integer> productIdComboBox = new ComboBox<>("Select Product ID");
        productIdComboBox.setItems(productIdList);
        Button enterProductIdButton = new Button("Enter");
        enterProductIdButton.setEnabled(false);
        enterProductIdButton.addClassName("pointer-cursor");

        productIdComboBox.addValueChangeListener(e -> {
            enterProductIdButton.setEnabled(true);
        });

        ProductSelectionLayout.add(productIdComboBox, enterProductIdButton);
        ProductSelectionLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        ProductSelectionLayout.setVerticalComponentAlignment(FlexComponent.Alignment.END, productIdComboBox, enterProductIdButton);


        //////////////////////////////////////

        //productRateAndReviewLayout
        //Add a text area for the review
        TextArea reviewTextArea = new TextArea();
        reviewTextArea.setLabel("Review: ");
        reviewTextArea.setPlaceholder("Enter your review here...");
        reviewTextArea.setWidthFull();

        productRateAndReviewLayout.add(starLayout, reviewTextArea);
        productRateAndReviewLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        productRateAndReviewLayout.setVisible(false);

        //////////////////////////////////////
        

        Button resetButton = new Button("Reset");
        resetButton.addClassName("pointer-cursor");
        Button closeButton = new Button("Close");
        closeButton.addClassName("pointer-cursor");
        Button submitButton = new Button("Submit");
        submitButton.setVisible(false);
        submitButton.addClassName("pointer-cursor");

        buttonsLayout.add(resetButton, submitButton, closeButton);
        buttonsLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        ////////////////////////////////////////////

        resetButton.addClickListener(e -> {
            resetIdsSelectionForm(shopIdComboBox, enterShopIdButton);
            resetIdsSelectionForm(productIdComboBox, enterProductIdButton);
            cleanAndHideStarsLayout(submitButton);
            reviewTextArea.clear();
            ProductSelectionLayout.setVisible(false);
            productRateAndReviewLayout.setVisible(false);
            shopIdProductIdRatingValue[0] = null;
            shopIdProductIdRatingValue[1] = null;
            shopIdProductIdRatingValue[2] = null;
        });

        closeButton.addClickListener(e -> {
            resetButton.click();
            dialog.close();
        });


        //new.....
        enterShopIdButton.addClickListener(event -> {
            shopIdProductIdRatingValue[0] = shopIdComboBox.getValue();
            if (shopIdProductIdRatingValue[0] != null) {
                shopIdComboBox.setReadOnly(true);
                enterShopIdButton.setEnabled(false);
                productIdList.clear();
                productIdList.addAll(orderDto.getShoppingBasketMap().get(shopIdProductIdRatingValue[0]).getProductIdList());
                productIdComboBox.setItems(productIdList);
                ProductSelectionLayout.setVisible(true);
            }
            else {
                showErrorMessage("Shop ID not found in the order.");
                shopIdComboBox.setReadOnly(false);
                shopIdComboBox.clear();
                enterShopIdButton.setEnabled(true);
            }
        });

        // new.........
        enterProductIdButton.addClickListener(event -> {
            shopIdProductIdRatingValue[1] = shopIdComboBox.getValue();
            if (shopIdProductIdRatingValue[1] != null) {
                productIdComboBox.setReadOnly(true);
                enterProductIdButton.setEnabled(false);
                productRateAndReviewLayout.setVisible(true);
                cleanAndDisplayStarsLayout(submitButton);
                submitButton.setVisible(true);
            } else {
                showErrorMessage("Product ID not found in the order.");
                productIdComboBox.setReadOnly(false);
                productIdComboBox.clear();
                enterProductIdButton.setEnabled(true);
            }
        });

        submitButton.addClickListener(e -> {
            shopIdProductIdRatingValue[2] = getCurrentRating();
            if (shopIdProductIdRatingValue[2] == 0) {
                showErrorMessage("No rating was chosen.");
            } else if (reviewTextArea == null || reviewTextArea.isEmpty()) {
                showErrorMessage("No review was entered.");
            } else {
                reviewsPresenter.addProductRatingAndReview(shopIdProductIdRatingValue[0], shopIdProductIdRatingValue[1], shopIdProductIdRatingValue[2], reviewTextArea.getValue());
                showSuccessMessage("Product was rated and reviewed successfully");
                cleanAndHideStarsLayout(submitButton);
                closeButton.click();
            }
        });

        ////////////////////////////////////////////


        reviewLayout.add(reviewProductLabel, shopSelectionLayout, ProductSelectionLayout, productRateAndReviewLayout, buttonsLayout);
        reviewLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        dialog.add(reviewLayout);
        dialog.open();
    }

    private HorizontalLayout createStarRatingLayout() {
        HorizontalLayout starsRatingLayout = new HorizontalLayout();
        for (int i = 0; i < 5; i++) {
            final int starIndex = i;
            stars[i] = new Span(FILLED_STAR);
            stars[i].addClassName("star");
            stars[i].getElement().addEventListener("click", e -> {
                updateRating(starIndex + 1);
                // Fix display of a toggled star and updateRating
            });
            starsRatingLayout.add(stars[i]);
        }
        return starsRatingLayout;
    }

    private void cleanAndDisplayStarsLayout(Button submitButton) {
        for (int i = 0; i < 5; i++) {
            stars[i].removeClassName("selected-star");
        }
        currentRating = 0;
        starLayout.setVisible(true);
        submitButton.setVisible(true);
    }

    private void cleanAndHideStarsLayout(Button submitButton) {
        for (int i = 0; i < 5; i++) {
            stars[i].removeClassName("selected-star");
        }
        currentRating = 0;
        starLayout.setVisible(false);
        submitButton.setVisible(false);
    }

    private void updateRating(int rating) {
        for (int i = 0; i < rating; i++) {
            stars[i].addClassName("selected-star");
        }
        for(int i = rating; i < 5; i++) {
            stars[i].removeClassName("selected-star");
        }
        currentRating = rating;
    }

    public int getCurrentRating() {
        return currentRating;
    }

    public void resetIdsSelectionForm(ComboBox<Integer> comboBox, Button enterButton) {
        comboBox.setReadOnly(false);
        comboBox.clear();
        enterButton.setEnabled(false);
    }

    private Span createDetailSpan(String label, String value) {
        Span span = new Span();
        span.getElement().setProperty("innerHTML", "<b>" + label + "</b>" + value);
        return span;
    }


}
