package UI.View;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import UI.Model.BasketDto;
import UI.Presenter.ShoppingCartPagePresentor;

@PageTitle("Shopping Cart Page")
@Route(value = "user_cart")
public class ShoppingCartPageView extends BaseView {
    private ShoppingCartPagePresentor presenter;
    private H1 _title;
    private Grid<BasketDto> grid = new Grid<>(BasketDto.class);

    public ShoppingCartPageView() {
        // Initialize presenter
        presenter = new ShoppingCartPagePresentor(this);
        presenter.viewCart();

        // Create the header component
        Header guestHeader = new BrowsePagesHeaderGuest("8080");
        Header userHeader = new BrowsePagesHeader("8080");

        if (isGuest()) {
            add(guestHeader);
        } else {
            add(userHeader);
        }

    }

    public void showBaskets(List<BasketDto> baskets) {
        // Create a grid bound to the Product class

        grid.setItems(baskets);

        // Configure the grid to show specific properties of the Product class
        configureGrid();

        // Add the grid to the layout
        add(grid);
    }

    private void configureGrid() {
        grid.setColumns(); // Clear any default columns

        // Shop ID column
        grid.addColumn(BasketDto::getShopID)
            .setHeader("Shop ID")
            .setKey("_shopID");

        // Product IDs column
        grid.addColumn(createProductIDsRenderer())
            .setHeader("Product IDs")
            .setKey("_productIDs");

        // Total Price column
        grid.addColumn(BasketDto::getTotalPrice)
            .setHeader("Total Price")
            .setKey("_totalPrice");
    }

    private Renderer<BasketDto> createProductIDsRenderer() {
        return new TextRenderer<>(basketDto -> 
            basketDto.getProductIDs().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", ")));
    }

    public boolean isGuest() {
        String username = (String) UI.getCurrent().getSession().getAttribute("username");
        if (username == null) 
            return true;
        return false;
    }
}
