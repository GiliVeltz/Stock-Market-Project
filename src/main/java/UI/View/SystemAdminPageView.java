package UI.View;

import java.util.List;
import java.util.Map;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import UI.Model.ShopOrderDto;
import UI.Model.OrderDto;
import UI.Model.ShoppingBasketDto;
import UI.Presenter.SystemAdminPresenter;

@Route("admin")
public class SystemAdminPageView extends BaseView {

    private SystemAdminPresenter presenter;
    private Grid<OrderDto> userOrderGrid;
    private final Div ordersDiv;

    public SystemAdminPageView() {
        presenter = new SystemAdminPresenter(this);
        initializeLayout();

        ordersDiv = new Div();
        ordersDiv.setId("orders-div");
    }

    private void initializeLayout() {
        // Create the image buttons
        Button closeShopButton = createImageButton("https://github.com/inbarbc/StockMarket_Project/blob/main/closeShop.png?raw=true", "Close Shop", this::onCloseShopClick);
        Button unsubscribeUserButton = createImageButton("https://github.com/inbarbc/StockMarket_Project/blob/main/UnsubscribeUser.png?raw=true", "Unsubscribe User", this::onUnsubscribeUserClick);
        Button respondingComplaintsButton = createImageButton("https://github.com/inbarbc/StockMarket_Project/blob/main/RespondingComplaints.png?raw=true", "Responding to Complaints", this::onRespondingComplaintsClick);
        Button sendingMessagesButton = createImageButton("https://github.com/inbarbc/StockMarket_Project/blob/main/SendingMessages.png?raw=true", "Sending Messages", this::onSendingMessagesClick);
        Button purchaseHistoryButton = createImageButton("https://github.com/inbarbc/StockMarket_Project/blob/main/PurchaseHistory.png?raw=true", "Purchase History", this::onPurchaseHistoryClick);
        Button systemInformationButton = createImageButton("https://github.com/inbarbc/StockMarket_Project/blob/main/SystemInformation.png?raw=true", "System Information", this::onSystemInformationClick);
        Button externalServiceButton = createImageButton("https://github.com/inbarbc/StockMarket_Project/blob/main/ExternalService.png?raw=true", "External Service", this::externalServiceClick);
    
        // Create layouts for buttons
        HorizontalLayout firstRow = new HorizontalLayout(closeShopButton, unsubscribeUserButton, respondingComplaintsButton, externalServiceButton);
        firstRow.setSpacing(true); // Add spacing between buttons
        firstRow.setJustifyContentMode(HorizontalLayout.JustifyContentMode.CENTER); // Center buttons in the row
        firstRow.getStyle().set("margin-bottom", "150px");
    
        HorizontalLayout secondRow = new HorizontalLayout(sendingMessagesButton, purchaseHistoryButton, systemInformationButton);
        secondRow.setSpacing(true); // Add spacing between buttons
        secondRow.setJustifyContentMode(HorizontalLayout.JustifyContentMode.CENTER); // Center buttons in the row
        secondRow.getStyle().set("margin-bottom", "150px");
    
        VerticalLayout layout = new VerticalLayout(firstRow, secondRow);
        layout.setSpacing(true); // Add spacing between rows
        layout.setPadding(true); // Add padding around the layout
        layout.setDefaultHorizontalComponentAlignment(VerticalLayout.Alignment.CENTER); // Center rows in the layout
        layout.setSizeFull(); // Make the layout take the full size of the container
        layout.setJustifyContentMode(VerticalLayout.JustifyContentMode.CENTER); // Center the layout vertically
    
        add(layout);
    }

    private Button createImageButton(String imagePath, String tooltip, Runnable clickListener) {
        // Create an image component
        Image image = new Image(imagePath, tooltip);
        image.setWidth("180px");
        image.setHeight("180px");
    
        // Create a button and set the image as its icon
        Button button = new Button();
        button.setIcon(image);
        button.addClickListener(event -> clickListener.run());
        button.addClassName("pointer-cursor");
        button.getElement().setAttribute("title", tooltip); // Set tooltip

        button.getStyle().set("margin-right", "150px");

        return button;
    }    

    private void onCloseShopClick() {
         // Create and configure the dialog
        Dialog dialog = new Dialog();
        
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        H4 headline = new H4("Choose the shop ID you want to close");
        dialog.add(headline);

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setSpacing(true);
        dialogLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER); 
        dialogLayout.setAlignItems(Alignment.CENTER); 


        // Create components for the dialog
        TextField shopIdField = new TextField("Enter Shop ID");
        Button submitButton = new Button("Submit", event -> {
            String shopId = shopIdField.getValue();
            presenter.closeShop(shopId); // Call presenter method with shop ID
            dialog.close();
        });

        Button cancelButton = new Button("Cancel", event -> dialog.close());

        // Add components to the dialog layout
        dialogLayout.add(shopIdField, new HorizontalLayout(submitButton, cancelButton));

        dialog.add(dialogLayout);
        dialog.open();

    }

    private void onUnsubscribeUserClick() {
        Notification.show("Unsubscribe User clicked");
        // Handle the unsubscribe user action
    }

    private void onRespondingComplaintsClick() {
        Notification.show("Responding to Complaints clicked");
        // Handle the responding to complaints action
    }

    private void onSendingMessagesClick() {
    Dialog dialog = new Dialog();

    dialog.setCloseOnEsc(true);
    dialog.setCloseOnOutsideClick(true);

    VerticalLayout dialogLayout = new VerticalLayout();
    dialogLayout.setSpacing(true);
    dialogLayout.setPadding(true);
    dialogLayout.setAlignItems(Alignment.CENTER);

    H4 headline = new H4("Send Alert Notification");
    dialogLayout.add(headline);

    TextField targetUserField = new TextField("Enter Target Username");
    TextArea messageField = new TextArea("Enter Message");

    Button submitButton = new Button("Submit", event -> {
        String targetUser = targetUserField.getValue();
        String message = messageField.getValue();
        presenter.sendAlertNotification(targetUser, message); // Call presenter method with targetUser and message
        dialog.close();
    });

    Button cancelButton = new Button("Cancel", event -> dialog.close());

    dialogLayout.add(targetUserField, messageField, new HorizontalLayout(submitButton, cancelButton));
    dialog.add(dialogLayout);
    dialog.open();
}

    private void onSystemInformationClick() {
        Notification.show("System Information clicked");
        // Handle the system information action
    }

    private void externalServiceClick() {
        Notification.show("External Service clicked");
        // Handle the system information action
    }

    private void onPurchaseHistoryClick() {
        Dialog purchaseHistoryDialog = new Dialog();
        purchaseHistoryDialog.setCloseOnEsc(true);
        purchaseHistoryDialog.setCloseOnOutsideClick(true);
    
        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setSpacing(true);
        dialogLayout.setPadding(true);
        dialogLayout.setAlignItems(Alignment.CENTER);
    
        H4 headline = new H4("Choose Purchase History Type");
        dialogLayout.add(headline);
    
        Button userPurchaseHistoryButton = new Button("User Purchase History", event -> {
            purchaseHistoryDialog.close();
            openUserPurchaseHistoryDialog();
        });
    
        Button shopPurchaseHistoryButton = new Button("Shop Purchase History", event -> {
            purchaseHistoryDialog.close();
            openShopPurchaseHistoryDialog();
        });
    
        dialogLayout.add(userPurchaseHistoryButton, shopPurchaseHistoryButton);
        purchaseHistoryDialog.add(dialogLayout);
        purchaseHistoryDialog.open();
    }
    private void openUserPurchaseHistoryDialog() {
        Dialog userDialog = new Dialog();
        userDialog.setCloseOnEsc(true);
        userDialog.setCloseOnOutsideClick(true);

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setSpacing(true);
        dialogLayout.setPadding(true);
        dialogLayout.setAlignItems(Alignment.CENTER);
        
        H4 headline = new H4("Enter Username for User Purchase History");
        dialogLayout.add(headline);

        TextField usernameField = new TextField("Enter Username");
        Button submitButton = new Button("Submit", event -> {
            String username = usernameField.getValue();
            presenter.getUserPurchaseHistory(username); // Call presenter method with username
            userDialog.close();
        });

        Button cancelButton = new Button("Cancel", event -> userDialog.close());

        dialogLayout.add(usernameField, new HorizontalLayout(submitButton, cancelButton));
        userDialog.add(dialogLayout);
        userDialog.open();
    }


    public void showUserOrders(List<OrderDto> orders) {
        removeAll();
        // Initialize the grid
        userOrderGrid = new Grid<>(OrderDto.class, false);
        userOrderGrid.addColumn(OrderDto::getOrderId).setHeader("Order ID");

        // Add a button to expand each order and show details
        userOrderGrid.addComponentColumn(orderDto -> {
            Button detailsButton = new Button("Show Details");
            detailsButton.addClickListener(e -> showOrderDetails(orderDto));
            return detailsButton;
        }).setHeader("Actions");

        // Add components to the layout
        add(userOrderGrid); // Add the grid directly to the layout
        add(ordersDiv);

        // Load order history
        userOrderGrid.setItems(orders);

        createBackButton();
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

    private void createBackButton() {
        Button backButton = new Button("Back");
        backButton.addClickListener(event -> restoreInitialState());
        add(backButton);
    }

    private void restoreInitialState() {
        removeAll(); // Clear the current layout
        ordersDiv.removeAll(); // Clear the ordersDiv
        initializeLayout(); // Re-add the initial layout with buttons
    }

    public void showShopOrders(List<ShopOrderDto> orders) {
        Dialog shopOrderDialog = new Dialog();
        shopOrderDialog.setWidth("80%");
        shopOrderDialog.setHeight("80%");
    
        VerticalLayout dialogLayout = new VerticalLayout();
        shopOrderDialog.add(dialogLayout);
    
        Grid<ShopOrderDto> shopOrderGrid = new Grid<>(ShopOrderDto.class, false);
        shopOrderGrid.addColumn(ShopOrderDto::getOrderId).setHeader("Order ID");
        shopOrderGrid.addColumn(ShopOrderDto::getTotalOrderAmount).setHeader("Total Amount");
    
        // Add a button to expand each order and show details
        shopOrderGrid.addComponentColumn(orderDto -> {
            Button detailsButton = new Button("Show Details");
            detailsButton.addClickListener(e -> openShopOrderDetailsDialog(orderDto));
            return detailsButton;
        }).setHeader("Actions");
    
        dialogLayout.add(shopOrderGrid);
        shopOrderGrid.setItems(orders);
    
        Button closeButton = new Button("Close", e -> shopOrderDialog.close());
        dialogLayout.add(closeButton);
    
        shopOrderDialog.open();
    }

    private void openShopPurchaseHistoryDialog() {
        Dialog shopDialog = new Dialog();
        shopDialog.setCloseOnEsc(true);
        shopDialog.setCloseOnOutsideClick(true);
    
        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setSpacing(true);
        dialogLayout.setPadding(true);
        dialogLayout.setAlignItems(Alignment.CENTER);
    
        H4 headline = new H4("Enter Shop ID for Shop Purchase History");
        dialogLayout.add(headline);
    
        TextField shopIdField = new TextField("Enter Shop ID");
        Button submitButton = new Button("Submit", event -> {
            String shopId = shopIdField.getValue();
            presenter.getShopPurchaseHistory(shopId); // Call presenter method with shop ID
            shopDialog.close();
        });
    
        Button cancelButton = new Button("Cancel", event -> shopDialog.close());
    
        dialogLayout.add(shopIdField, new HorizontalLayout(submitButton, cancelButton));
        shopDialog.add(dialogLayout);
        shopDialog.open();
    }
    
   private void openShopOrderDetailsDialog(ShopOrderDto orderDto) {
    Dialog dialog = new Dialog();
    dialog.setWidth("400px");
    dialog.setHeight("300px");

    VerticalLayout dialogLayout = new VerticalLayout();
    dialog.add(dialogLayout);

    dialogLayout.add(new Text("Order ID: " + orderDto.getOrderId()));
    dialogLayout.add(new Text("Total Amount: " + orderDto.getTotalOrderAmount()));

    Button viewProductsButton = new Button("View Products", e -> showProductIds(orderDto.getShoppingBasketDto().getProductIdList()));
    dialogLayout.add(viewProductsButton);

    Button closeButton = new Button("Close", e -> dialog.close());
    dialogLayout.add(closeButton);

    dialog.open();
}

private void showProductIds(List<Integer> productIds) {
    Dialog productDialog = new Dialog();
    productDialog.setWidth("300px");
    productDialog.setHeight("200px");

    VerticalLayout dialogLayout = new VerticalLayout();
    productDialog.add(dialogLayout);

    dialogLayout.add(new Text("Product IDs:"));

    for (Integer productId : productIds) {
        dialogLayout.add(new Text(productId.toString()));
    }

    Button closeButton = new Button("Close", e -> productDialog.close());
    dialogLayout.add(closeButton);

    productDialog.open();
}
    
}
