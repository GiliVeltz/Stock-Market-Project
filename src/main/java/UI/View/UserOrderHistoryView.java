package UI.View;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import UI.Model.OrderDto;
import UI.Model.ShoppingBasketDto;
import UI.Presenter.UserOrderHistoryPresenter;

public class UserOrderHistoryView extends BaseView {
    private UserOrderHistoryPresenter presenter;
    private final Div ordersDiv;
    private Grid<OrderDto> orderGrid;

    public UserOrderHistoryView() {
        presenter = new UserOrderHistoryPresenter(this);

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

        // Add a close button to the dialog
        Button closeButton = new Button("Close", e -> dialog.close());
        detailsLayout.add(closeButton);

        // Open the dialog
        dialog.open();
    }

    private Span createDetailSpan(String label, String value) {
        Span span = new Span();
        span.getElement().setProperty("innerHTML", "<b>" + label + "</b>" + value);
        return span;
    }
}
