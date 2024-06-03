package UI.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;

@PageTitle("list")
@Route(value = "/example")
@RouteAlias(value = "/example")
public class ListView extends VerticalLayout {

    public ListView() {
        // setSpacing(false);

        // Image img = new Image("images/empty-plant.png", "placeholder plant");
        // img.setWidth("200px");
        // add(img);

        // H2 header = new H2("This place intentionally left empty");
        // header.addClassNames(Margin.Top.XLARGE, Margin.Bottom.MEDIUM);
        // add(header);
        // add(new Paragraph("It’s a place where you can grow your own UI 🤗"));

        // setSizeFull();
        // setJustifyContentMode(JustifyContentMode.CENTER);
        // setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        // getStyle().set("text-align", "center");
        
        H1 h = new H1("Hello, World!");
        add();
        Button button = new Button("Click me", event -> {
            add(new Span("Clicked!"));
        });
        add(button);

        HorizontalLayout hl = new HorizontalLayout(h, button);
        hl.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        add(hl);
    }

}
