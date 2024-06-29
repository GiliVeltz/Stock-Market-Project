package UI.View;

import java.util.List;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import UI.Model.ShopDto;
import UI.Presenter.searchShopsPresenter;

@PageTitle("Search Shops Page")
@Route(value = "search shops")
public class SearchShopResultsView extends BaseView{

    private searchShopsPresenter presenter;

    private String shopName;
    private String shopId;

    public SearchShopResultsView() {
        shopName = (String) VaadinSession.getCurrent().getAttribute("searchShopName");
        shopId = (String) VaadinSession.getCurrent().getAttribute("searchShopId");

        // Create the header component
        Header header = new BrowsePagesHeader("8080");
        add(header);

        presenter = new searchShopsPresenter(this);
        
        // Create a title for the page
        H1 headlineShops = new H1("Shops Results");

        // Add components to the view
        add(headlineShops);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        beforeEnter(event);
        shopName = (String) VaadinSession.getCurrent().getAttribute("searchShopName");
        shopId = (String) VaadinSession.getCurrent().getAttribute("searchShopId");

        presenter.searchShop(shopName, shopId);
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

}
