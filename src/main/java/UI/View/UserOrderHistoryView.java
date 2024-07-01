package UI.View;

import java.util.List;
import java.util.Map; // Add this import statement

import com.vaadin.flow.component.button.Button; // Add this import statement
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import UI.Model.OrderDto;
import UI.Model.ShoppingBasketDto; // Add this import statement
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
        detailsLayout.add("Order ID: " + orderDto.getOrderId());
        detailsLayout.add("Total Order Amount: " + orderDto.getTotalOrderAmount());

        // Add the shopping baskets for this order
        for (Map.Entry<Integer, ShoppingBasketDto> entry : orderDto.getShoppingBasketMap().entrySet()) {
            ShoppingBasketDto basketDto = entry.getValue();
            detailsLayout.add("Shop ID: " + entry.getKey());
            detailsLayout.add("Shop Name: " + basketDto.getShop().getShopName());
            detailsLayout.add("Basket Total Amount: " + basketDto.getBasketTotalAmount());
            detailsLayout.add("Product IDs: " + basketDto.getProductIdList().toString());
        }

        // Display the order details in the ordersDiv
        ordersDiv.removeAll();
        ordersDiv.add(detailsLayout);
    }
}
