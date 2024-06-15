package UI.View;
import UI.Presenter.LandingPagePresenter;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@PageTitle("Landing Page")
@Route(value = "")
@RouteAlias(value = "")
public class LandingPageView extends BaseView implements ViewPageI {

    
    public LandingPageView() {
        // Create the header component
        Header header = new Header("8080");

        // Create the title
        H1 title = new H1("Welcome to Stock Market!!");

        // Create a horizontal layout for the title to center it
        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setWidthFull(); // Make the layout take full width
        titleLayout.setJustifyContentMode(JustifyContentMode.CENTER); // Center the content
        titleLayout.add(title);

        // Add components to the vertical layout
        add(header, titleLayout);
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


