package UI.View;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;

import UI.Model.OrderDto;
import UI.Model.ShoppingBasketDto;
import UI.Presenter.UserOrderHistoryPresenter;

public class UserOrderHistoryView extends BaseView {
    private UserOrderHistoryPresenter presenter;
    private final Div ordersDiv;
    private Grid<OrderDto> orderGrid;
    private static final String FILLED_STAR = "⭐";
    private static final String EMPTY_STAR = "☆";
    private HorizontalLayout starLayout;
    private int currentRating = 0;

    public UserOrderHistoryView() {
        presenter = new UserOrderHistoryPresenter(this);

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
            detailsLayout.add(createDetailSpan("Shop ID: ", entry.getKey().toString()));
            detailsLayout.add(createDetailSpan("Shop Name: ", basketDto.getShop().getShopName()));
            detailsLayout.add(createDetailSpan("Basket Total Amount: ", String.valueOf(basketDto.getBasketTotalAmount())));

            // Format the product IDs with quantities
            String formattedProductIds = basketDto.getProductIdList().stream()
                .collect(Collectors.groupingBy(id -> id, Collectors.counting()))
                .entrySet().stream()
                .map(e -> e.getKey() + " (" + e.getValue() + ")")
                .collect(Collectors.joining(", "));

            detailsLayout.add(createDetailSpan("Product IDs: ", formattedProductIds));
        }

        // Create a dialog to display the details
        Dialog dialog = new Dialog();
        dialog.add(detailsLayout);
        dialog.setWidth("400px");
        dialog.setHeight("300px");

        HorizontalLayout buttonsLayout = new HorizontalLayout();

        // Add a close button to the dialog
        Button closeButton = new Button("Close", e -> dialog.close());
        detailsLayout.add(closeButton);

        Button reviewOrderButton = new Button("Review Order", e -> {
            dialog.close();
            // Call the method to create the review order dialog
            createReviewOrderDialog(orderDto);
        });
        detailsLayout.add(reviewOrderButton);

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
        IntegerField shopIdField = new IntegerField("Enter Shop ID");
        shopIdField.setReadOnly(false);
        Button enterShopIdButton = new Button("Enter");
        enterShopIdButton.setEnabled(true);

        Button resetButton = new Button("Reset");
        resetButton.setVisible(false);
        resetButton.addClassName("pointer-cursor");
        Button closeButton = new Button("Close");
        closeButton.addClassName("pointer-cursor");
        Button submitButton = new Button("Submit");
        submitButton.setVisible(false);
        submitButton.addClassName("pointer-cursor");

        resetButton.addClickListener(e -> {
            cleanAndHideStarsLayout(resetButton, submitButton);
            shopIdField.setReadOnly(false);
            shopIdField.clear();                    
            enterShopIdButton.setEnabled(true);

        });

        closeButton.addClickListener(e -> {
            cleanAndHideStarsLayout(resetButton, submitButton);
            shopIdField.setReadOnly(false);
            shopIdField.clear();
            dialog.close();
        });

        submitButton.addClickListener(e -> {
            shopIdRatingValue[1] = getCurrentRating();
            if (shopIdRatingValue[1] == 0) {
                showErrorMessage("No rating was chosen.");
            } else {
                //presenter.addShopRating(shopIdRatingValue[0], shopIdRatingValue[1]);
                showSuccessMessage("Shop was rated successfully");
                cleanAndHideStarsLayout(resetButton, submitButton);
            }
        });

        enterShopIdButton.addClickListener(event -> {
            shopIdRatingValue[0] = shopIdField.getValue();
            if (shopIdRatingValue[0] != null) {
                if (orderDto.getShoppingBasketMap().containsKey(shopIdRatingValue[0])) {
                    cleanAndDisplayStarsLayout(resetButton, submitButton);
                    shopIdField.setReadOnly(true);
                    enterShopIdButton.setEnabled(false);
                } else {
                    shopIdRatingValue[0] = null;
                    showErrorMessage("Shop ID not found in the order.");
                    shopIdField.setReadOnly(false);
                    shopIdField.clear();
                    enterShopIdButton.setEnabled(true);
                }
            }
        });

        buttonsLayout.add(resetButton, closeButton, submitButton);
        buttonsLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        reviewLayout.add(reviewShopLabel, shopIdField, enterShopIdButton, starLayout, buttonsLayout);
        reviewLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        dialog.add(reviewLayout);
        dialog.open();
    }

    public void createReviewProductDialog(OrderDto orderDto) {
        showSuccessMessage("not implemented Yet");
    //     // Create a layout to hold the review order form
    //     Dialog dialog = new Dialog();
    //     VerticalLayout reviewLayout = new VerticalLayout();
    //     HorizontalLayout buttonsLayout = new HorizontalLayout();

    //     Integer[] shopIdRatingValue = {null, null}; //shopIdRatingValue[0] = shopId, shopIdRatingValue[1] = ratingValue

    //     IntegerField shopIdField = new IntegerField("Enter Shop ID");
    //     shopIdField.setReadOnly(false);

    //     Button enterShopIdButton = new Button("Enter");
    //     enterShopIdButton.setEnabled(true);

    //     Button resetButton = new Button("Reset");
    //     resetButton.setVisible(false);
    //     resetButton.addClickListener(e -> {
    //             cleanAndHideStarsLayout();
    //             shopIdField.setReadOnly(false);
    //             shopIdField.clear();                    
    //             enterShopIdButton.setEnabled(true);

    //     });
    //     resetButton.addClassName("pointer-cursor");

    //     Button closeButton = new Button("Close");
    //     closeButton.addClickListener(e -> {
    //         cleanAndHideStarsLayout();
    //         shopIdField.setReadOnly(false);
    //         shopIdField.clear();
    //         dialog.close();
    //     });

    //     Button submitButton = new Button("Submit");
    //     submitButton.setVisible(false);
    //     submitButton.addClickListener(e -> {
    //         shopIdRatingValue[1] = getCurrentRating();
    //         if (shopIdRatingValue[1] == 0) {
    //             showErrorMessage("No rating was chosen.");
    //         } else {
    //             presenter.addShopRating(shopIdRatingValue[0], shopIdRatingValue[1]);
    //             showSuccessMessage("Shop was rated successfully");
    //             cleanAndHideStarsLayout();
    //         }
    //     });
    //     submitButton.addClassName("pointer-cursor");

    //     enterShopIdButton.addClickListener(event -> {
    //         shopIdRatingValue[0] = shopIdField.getValue();
    //         if (shopIdRatingValue[0] != null) {
    //             if (orderDto.getShoppingBasketMap().containsKey(Integer.parseInt(shopId))) {
    //                 cleanAndDisplayStarsLayout(cleanButton, submitButton);
    //                 shopIdField.setReadOnly(true);
    //                 enterShopIdButton.setEnabled(false);
    //             } else {
    //                 shopIdRatingValue[0] = null;
    //                 showErrorMessage("Shop ID not found in the order.");
    //                 shopIdField.setReadOnly(false);
    //                 shopIdField.clear();
    //                 enterShopIdButton.setEnabled(true);
    //             }
    //         }
    //     });

    //     buttonsLayout.add(resetButton, closeButton, submitButton);
    //     buttonsLayout.setAlignItems(FlexComponent.Alignment.CENTER);

    //     reviewLayout.add(shopIdField, enterShopIdButton, buttonsLayout);
    //     reviewLayout.setAlignItems(FlexComponent.Alignment.CENTER);

    //     dialog.add(reviewLayout);
    //     dialog.open();

    //     // Add a text area for the review
    //     TextArea reviewTextArea = new TextArea();
    //     reviewTextArea.setLabel("Review");
    //     reviewTextArea.setPlaceholder("Enter your review here...");
    //     reviewLayout.add(reviewTextArea);

    //     // Add a rating field
    //     Rating rating = new Rating();
    //     rating.setLabel("Rating");
    //     reviewLayout.add(rating);

    //     // Add a submit button
    //     Button submitButton = new Button("Submit Review", e -> {
    //         // Get the review text and rating
    //         String reviewText = reviewTextArea.getValue();
    //         int ratingValue = rating.getValue();

    //         // Call the presenter method to submit the review
    //         presenter.submitOrderReview(orderDto.getOrderId(), reviewText, ratingValue);
    //     });

    //     reviewLayout.add(submitButton);

    //     // Create a dialog to display the review form
    //     Dialog reviewDialog = new Dialog();
    //     reviewDialog.add(reviewLayout);
    //     reviewDialog.setWidth("400px");
    //     reviewDialog.setHeight("300px");

    //     // Open the dialog
    //     reviewDialog.open();
    }

    private HorizontalLayout createStarRatingLayout() {
        HorizontalLayout starsRatingLayout = new HorizontalLayout();
        for (int i = 1; i <= 5; i++) {
            final int starIndex = i;
            Span star = new Span(EMPTY_STAR);
            star.addClassName("star");
            star.getElement().addEventListener("click", e -> updateRating(starIndex));
            starsRatingLayout.add(star);
        }
        return starsRatingLayout;
    }

    private void cleanAndDisplayStarsLayout(Button resetButton, Button submitButton) {
        starLayout.removeAll();
        for (int i = 1; i <= 5; i++) {
            final int starIndex = i;
            Span star = new Span(EMPTY_STAR);
            star.addClassName("star");
            star.getElement().addEventListener("click", e -> updateRating(starIndex));
            starLayout.add(star);
        }
        starLayout.setVisible(true);
        resetButton.setVisible(true);
        submitButton.setVisible(true);
    }

    private void cleanAndHideStarsLayout(Button resetButton, Button submitButton) {
        starLayout.removeAll();
        for (int i = 1; i <= 5; i++) {
            final int starIndex = i;
            Span star = new Span(EMPTY_STAR);
            star.addClassName("star");
            star.getElement().addEventListener("click", e -> updateRating(starIndex));
            starLayout.add(star);
        }
        starLayout.setVisible(false);
        resetButton.setVisible(false);
        submitButton.setVisible(false);
    }

    private void updateRating(int rating) {
        currentRating = rating;
    }

    public int getCurrentRating() {
        return currentRating;
    }

    private Span createDetailSpan(String label, String value) {
        Span span = new Span();
        span.getElement().setProperty("innerHTML", "<b>" + label + "</b>" + value);
        return span;
    }


}
