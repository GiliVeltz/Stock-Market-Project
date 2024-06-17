package UI.View;

import java.util.List;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.Route;

import UI.Model.BasketDto;
import UI.Presenter.ShoppingCartPagePresentor;

@Route(value = "user_cart")
public class ShoppingCartPageView extends BaseView {
    private ShoppingCartPagePresentor presenter;
    private H1 _title;

    public ShoppingCartPageView() {
        // Initialize presenter
        presenter = new ShoppingCartPagePresentor(this);
        presenter.viewCart();

        // Create the header component
        Header header = new BrowsePagesHeader("8080");
        add(header);


    }

    public void showBaskets(List<BasketDto> baskets) {
        // Create a grid bound to the Product class
        Grid<BasketDto> grid = new Grid<>(BasketDto.class);

        grid.setItems(baskets);

        // Configure the grid to show specific properties of the Product class
        grid.setColumns("Shop ID", "Product ID", "Price"); // Display only the name and price columns

        // Add the grid to the layout
        add(grid);
    }
}
