package UI.View;

import java.util.List;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.model.Tooltip;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import UI.Model.ShopDto;
import UI.Presenter.AllShopPresenter;

@PageTitle("Shop List")
@Route(value = "all_shops")
public class AllShopView extends BaseView {

    private AllShopPresenter presenter;
    private boolean isGuest;

    public AllShopView() {
        // Initialize presenter
        presenter = new AllShopPresenter(this);

        // Create the header component
        Header guestHeader = new BrowsePagesHeaderGuest("8080");
        Header userHeader = new BrowsePagesHeader("8080");

        isGuest = isGuest();

        if (isGuest) {
            add(guestHeader);
        } else {
            add(userHeader);
        }

        // Create a title for the page
        H1 headlineShops = new H1("Stock Market Shops");
        add(headlineShops);

        presenter.getAllShops();
    }

    public boolean isGuest() {
        String username = (String) UI.getCurrent().getSession().getAttribute("username");
        return username == null;
    }

    public void showShops(List<ShopDto> shopList) {
        createShopButtons(shopList);
    }

    public void createShopButtons(List<ShopDto> shopList) {

        if (shopList.isEmpty()) {
            add(new Paragraph("No shops found"));
            return;
        }

        // Create a vertical layout for the grid
        VerticalLayout gridLayout = new VerticalLayout();
        gridLayout.setAlignItems(Alignment.START);

        // Set a maximum of 3 buttons per row
        int maxButtonsPerRow = 3;
        HorizontalLayout rowLayout = new HorizontalLayout();
        int count = 0;

        for (ShopDto shop : shopList) {
            String shopName = shop.getShopName();
            Button shopButton = new Button(shopName);
            // Button shopButton = new Button(shopName, e -> navigateToShopPage(shopId));

           // Increase the button size and add tooltip data attribute
           shopButton.getElement().getStyle().set("width", "200px").set("height", "100px");

            // Add click listener to show popup dialog
            shopButton.addClickListener(e -> showShopDetailsDialog(shop));
            rowLayout.add(shopButton);

            if ((count + 1) % maxButtonsPerRow == 0 || count == shopList.size() - 1) {
                gridLayout.add(rowLayout);
                rowLayout = new HorizontalLayout();
            }

            count++;
        }

        // Add the grid layout to the main layout
        add(gridLayout);
    }
     public void showShopDetailsDialog(ShopDto shop) {
        // Create a dialog with shop details
        Dialog dialog = new Dialog();
        dialog.setModal(true); // Make the dialog modal (blocks interaction with other UI elements)
        Integer shopId = shop.getShopId();
        // Set the dialog content
        VerticalLayout dialogContent = new VerticalLayout();
        dialogContent.add(new Paragraph("Shop Name: " + shop.getShopName()));
        dialogContent.add(new Paragraph("Address: " + shop.getShopAddress()));
        dialogContent.add(new Paragraph("Rating: " + (shop.getShopRating() == -1 ? "None" : shop.getShopRating())));
        dialogContent.add(new Paragraph("Raters Count: " + shop.getShopRatersCounter()));
        dialogContent.add(new Paragraph("Shop Status: " + (shop.getIsShopClosed() ? "Closed" : "Open")));

        VerticalLayout dialogButtons = new VerticalLayout();
        Button closeButton = new Button("Close", event -> dialog.close());
        Button shopPageButton = new Button("Shop Page", event -> {
            dialog.close();
            navigateToShopPage(shopId, shop.getShopName());
        });

        dialogButtons.add(closeButton, shopPageButton);
        dialogButtons.setAlignItems(Alignment.CENTER);

        dialogContent.add(dialogButtons);

        dialog.add(dialogContent);
        dialog.open();
    }

    public void navigateToShopPage(Integer shopId, String shopName) {
        UI.getCurrent().getSession().setAttribute("shopId", shopId);
        UI.getCurrent().getSession().setAttribute("shopName", shopName);
        getUI().ifPresent(ui -> ui.navigate("shop_page/" + shopId));
    }
}
