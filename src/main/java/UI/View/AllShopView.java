package UI.View;
import java.util.List;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import UI.Model.ShopDto;
import UI.Presenter.AllShopPresenter;

@PageTitle("Shop List")
@Route(value = "all_shops")
public class AllShopView extends BaseView{

    private AllShopPresenter presenter;
    //private List<ShopDto> shops; 
    private boolean isGuest;
    private Grid<ShopDto> shopGrid;

    private AllShopView()
    {
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
        
        // Initialize the grid
        shopGrid = new Grid<>();
        
        // Define columns explicitly and set headers
        shopGrid.addColumn(ShopDto::getShopName).setHeader("Shop Name");
        shopGrid.addColumn(ShopDto::getBankDetails).setHeader("Bank Details");
        shopGrid.addColumn(ShopDto::getShopAddress).setHeader("Shop Address");
        shopGrid.addColumn(shopDto -> shopDto.getShopRating() == -1 ? "None" : String.valueOf(shopDto.getShopRating()))
                .setHeader("Rating");
        shopGrid.addColumn(ShopDto::getShopRatersCounter).setHeader("Raters Count");
        shopGrid.addColumn(shopDto -> shopDto.getIsShopClosed() ? "Closed" : "Open").setHeader("Shop Status");

        // Add the grid to the view
        add(shopGrid);

        presenter.getAllShops();
    }

    public boolean isGuest() {
        String username = (String) UI.getCurrent().getSession().getAttribute("username");
        return username == null;
    }

    public void showShops(List<ShopDto> shopList) {
        // Update the grid with shop data
        shopGrid.setItems(shopList);
    }



}
