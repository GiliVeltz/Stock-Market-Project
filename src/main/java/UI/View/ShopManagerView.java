package UI.View;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.PathVariable;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import UI.Presenter.ShopManagerPresenter;
import enums.Permission;


@Route("user_shops/:shopId")
public class ShopManagerView extends BaseView{

    private ShopManagerPresenter presenter;
    private String _username;
    private Set<Permission> _permissions;
    private H1 _title;
    private int _shopId;
    
    public ShopManagerView(@OptionalParameter String shopId){

        // Retrieve the username from the session
        _username = (String) VaadinSession.getCurrent().getAttribute("username");
        //TODO: FIX THE SHOP ID
        // Retrieve the shopId from the URL
        Optional<String> shopId = getRouteParameters().get("shopId");
        _shopId = Integer.parseInt(shopId);

        // Initialize presenter
        presenter = new ShopManagerPresenter(this);
        presenter.fetchManagerPermissions(_username);

        // Create the header component
        Header header = new BrowsePagesHeader("8080");
        add(header);

    }

    public void createPermissionButtons(List<String> permissions) {
        if(permissions.isEmpty()){
            add(new Paragraph("No permissions found"));
            return;
        }

        // Create a vertical layout
        VerticalLayout buttonsLayout = new VerticalLayout();
        buttonsLayout.setAlignItems(Alignment.END);

        // Create buttons
        Button viewProductsbtn = new Button("Add Product", e -> presenter.viewProducts());
        Button viewDiscountsBtn = new Button("Add Discount", e -> presenter.addDiscounts());
        Button changeProductPolicyBtn = new Button("Change Product Policy", e -> presenter.changeProductPolicy());
        Button changeShopPolicyBtn = new Button("Change Shop Policy", e -> presenter.changeProductPolicy());
        Button appointManagerBtn = new Button("Appoint Manager", e -> presenter.appointManager());
        Button appointOwnerBtn = new Button("Appoint Owner", e -> presenter.appointManager());
        Button viewSubordinateBtn = new Button("View Subordinates", e -> presenter.viewSubordinate());
        Button viewShopRolesBtn = new Button("View Shop Roles", e -> presenter.viewShopRoles());
        Button viewPurchasesBtn = new Button("View Purchases", e -> presenter.viewPurchases());

        // Here we create a set of permissions from the strings.
        Set<Permission> _permissions = permissions.stream()
                .map(permissionString -> Permission.valueOf(permissionString.toUpperCase()))
                .collect(Collectors.toSet());
        
        if(_permissions.contains(Permission.FOUNDER)){
            _title = new H1("Shop Management: Founder");
        }else if(_permissions.contains(Permission.OWNER)){
            _title = new H1("Shop Management: Owner");
        }else{
            _title = new H1("Shop Management: Manager");

            // Regular manager cannot appoint owner
            appointOwnerBtn.setEnabled(false);

            if(_permissions.contains(Permission.CHANGE_PRODUCT_POLICY)){
                changeProductPolicyBtn.setEnabled(false);
            }
            if(_permissions.contains(Permission.CHANGE_SHOP_POLICY)){
                changeShopPolicyBtn.setEnabled(false);
            }
            if(_permissions.contains(Permission.APPOINT_MANAGER)){
                appointManagerBtn.setEnabled(false);
            }
            
        }
    }

    public int getShopId() {
        return _shopId;
    }
    
}
