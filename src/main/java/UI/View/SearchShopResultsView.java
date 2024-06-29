package UI.View;

import java.util.List;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import UI.Model.ShopDto;
import UI.Presenter.SearchShopPresenter;

@PageTitle("Search Shops Results Page")
@Route(value = "shops_search_results")
public class SearchShopResultsView extends BaseView{
    Dialog resultsDialog;
    private SearchShopPresenter presenter;
    // private String shopName;
    // private String shopId;

    public SearchShopResultsView() {
        // shopName = (String) VaadinSession.getCurrent().getAttribute("searchShopName");
        // shopId = (String) VaadinSession.getCurrent().getAttribute("searchShopId");
        resultsDialog = new Dialog();
        // Create the header component
        // Header header = new BrowsePagesHeader("8080");
        // add(header);

        //presenter = new searchShopsPresenter(this);
        
        // Create a title for the page
        // H1 headlineShops = new H1("Shops Results");

        // // Add components to the view
        // add(headlineShops);
    }

    // @Override
    // public void beforeEnter(BeforeEnterEvent event) {
    //     beforeEnter(event);
    //     shopName = (String) VaadinSession.getCurrent().getAttribute("searchShopName");
    //     shopId = (String) VaadinSession.getCurrent().getAttribute("searchShopId");

    //     presenter.searchShop(shopName, shopId);
    // }

    public void displayResponseShopNotFound (Integer shopId, String shopName) {
        clearSearchResults();  // Clear previous search results

        // create vertical Layout for the search results
        VerticalLayout dialogContent = new VerticalLayout();

        // Add "Search Results" title
        H2 headline = new H2("Search Results");
        headline.getStyle().set("margin", "0");
        dialogContent.add(headline);

        dialogContent = createNoResultsLayout(dialogContent, shopId, shopName);

         // Add close button
         Button closeButton = new Button("Close");
         closeButton.addClickListener(event -> {
             // Handle close button click
             clearSearchResults();
             resultsDialog.close();
         });
         closeButton.addClassName("pointer-cursor");
         dialogContent.add(closeButton);

         dialogContent.setAlignItems(FlexComponent.Alignment.CENTER);
         resultsDialog.add(dialogContent);
         resultsDialog.open();
    }


    public void displaySearchResults(List<ShopDto> shops) {
        removeAll(); // Clear previous results

        if (shops.isEmpty()) {
            add(new Span("No shops found matching the criteria."));
            return;
        }

        for (ShopDto shop : shops) {
            VerticalLayout shopLayout = new VerticalLayout();
            shopLayout.add(
                new Span("Shop Name: " + shop.getShopName()),
                new Span("Bank Details: " + shop.getBankDetails()),
                new Span("Address: " + shop.getShopAddress())
            );
            add(shopLayout);
        }
    }

    public void displayResponseShops (List<ShopDto> shopDtosList) {
        // Clear previous search results
        clearSearchResults();  

        // create vertical Layout for the search results
        VerticalLayout dialogContent = new VerticalLayout();

        // Add "Search Results" title
        H2 headline = new H2("Search Results");
        headline.getStyle().set("margin", "0");
        dialogContent.add(headline);

        ///////

        // Set a maximum of 3 buttons per row
        int maxButtonsPerRow = 3;
        HorizontalLayout rowLayout = new HorizontalLayout();
        int count = 0;

        for (ShopDto shop : shopDtosList) {
            Button shopButton = createShopButton(shop);
            shopButton.addClassName("product-button");
            shopButton.addClassName("pointer-cursor");
            rowLayout.add(shopButton);
            if ((count + 1) % maxButtonsPerRow == 0 || count == shopDtosList.size() - 1) {
                dialogContent.add(rowLayout);
                rowLayout = new HorizontalLayout();
            }

            count++;
        }

        ///////

        // Add close button
        Button closeButton = new Button("Close");
        closeButton.addClickListener(event -> {
            // Handle close button click
            clearSearchResults();
            resultsDialog.close();
        });
        closeButton.addClassName("pointer-cursor");
        dialogContent.add(closeButton);

        dialogContent.setAlignItems(FlexComponent.Alignment.CENTER);
        resultsDialog.add(dialogContent);
        resultsDialog.open();

    }

    private Button createShopButton(ShopDto shop) {
        Button shopButton = new Button(shop.getShopName());
        // Increase the button size and add tooltip data attribute
        shopButton.getElement().getStyle().set("width", "200px").set("height", "100px");
        // Add click listener to show popup dialog
        shopButton.addClickListener(e -> showShopDetailsDialog(shop));
        return shopButton;
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
        // dialogContent.add(new Paragraph("Rating: " + (shop.getShopRating() == -1 ? "None" : shop.getShopRating())));
        // dialogContent.add(new Paragraph("Raters Count: " + shop.getShopRatersCounter()));
        // dialogContent.add(new Paragraph("Shop Status: " + (shop.getIsShopClosed() ? "Closed" : "Open")));

        Button closeButton = new Button("Close", event -> dialog.close());
        Button shopPageButton = new Button("Shop Page", event -> {
            dialog.close();
            navigateToShopPage(shopId, shop.getShopName());
        });

        dialogContent.add(shopPageButton,closeButton);
        dialogContent.setAlignItems(FlexComponent.Alignment.CENTER);
        dialog.add(dialogContent);
        dialog.open();
    }

    public void navigateToShopPage(Integer shopId, String shopName) {
        UI.getCurrent().getSession().setAttribute("shopId", shopId);
        UI.getCurrent().getSession().setAttribute("shopName", shopName);
        UI.getCurrent().navigate("shop_page/" + shopId);
        resultsDialog.close();
    }

    private VerticalLayout createNoResultsLayout(VerticalLayout dialogContent, Integer shopId, String shopName) {
        // Create a vertical layout for the grid
        VerticalLayout gridLayout = new VerticalLayout();
        gridLayout.setAlignItems(Alignment.START);
        gridLayout.addClassName("light-component-container");

        H3 noResultsLabel = new H3("No Results");
        noResultsLabel.addClassName("shop-name-label");
        gridLayout.add(noResultsLabel);

        H5 comment = new H5();
        if (shopId != null) {
            comment.add("Shop with the ID " + shopId + " does not exist, please try again.");
        } else {
            comment.add("Shop with the name " + shopName + " does not exist, please try again.");
        }
        gridLayout.add(comment);

        // Add the grid layout to the main layout
        gridLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        dialogContent.add(gridLayout);
        return dialogContent;
    }

    public void clearSearchResults() {
        resultsDialog.removeAll();
        removeAll();  // Clear all components from the view
    }

}
