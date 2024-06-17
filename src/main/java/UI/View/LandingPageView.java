package UI.View;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamResource;

@PageTitle("Landing Page")
@Route(value = "")
@RouteAlias(value = "")
public class LandingPageView extends BaseView{

    
    public LandingPageView() {
        System.setProperty("server.port", "8080");

        // Create the header component
        Header header = new Header("8080");

        // Create the title
        H1 title = new H1("Welcome to Stock Market!!");

        // Create a horizontal layout for the title to center it
        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setWidthFull(); // Make the layout take full width
        titleLayout.setJustifyContentMode(JustifyContentMode.CENTER); // Center the content
        titleLayout.add(title);

        // Create image component for the cart
        Image cartImage = new Image("https://raw.githubusercontent.com/inbarbc/StockMarket_Project/main/shoppingCart.jpg", "Cart");
        cartImage.setWidth("400px");// Adjust size as needed

       // Create a horizontal layout for the cart image to center it
       HorizontalLayout cartImageLayout = new HorizontalLayout();
       cartImageLayout.setWidthFull(); // Make the layout take full width
       cartImageLayout.setJustifyContentMode(JustifyContentMode.CENTER); // Center the content
       cartImageLayout.add(cartImage);

        // Add components to the vertical layout
        add(header, titleLayout, cartImageLayout);
    }


    @Override
    public void showSuccessMessage(String message) {
        Notification.show(message);
    }

    @Override
    public void showErrorMessage(String message) {
        Notification.show(message);
    }

    

}


