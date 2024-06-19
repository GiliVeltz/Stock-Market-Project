package UI.View;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.catalina.Manager;
import org.springframework.web.bind.annotation.PathVariable;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import UI.Model.Permission;
import UI.Model.PermissionMapper;
import UI.Model.ShopManagerDto;
import UI.Presenter.ShopManagerPresenter;


@Route(value = "user_shops")
public class ShopManagerView extends BaseView implements HasUrlParameter<Integer>{

    private ShopManagerPresenter presenter;
    private String _username;
    private Set<Permission> _permissions;
    private H1 _title;
    private int _shopId;
    private Dialog _appointManagerDialog;
    private Dialog _appointOwnerDialog;
    private Dialog _viewRolesDialog;
    private List<ShopManagerDto> _managers;
    private Grid<ShopManagerDto> _viewRolesGrid;
    
    public ShopManagerView(){

        // Retrieve the username from the session
        _username = (String) VaadinSession.getCurrent().getAttribute("username");

        // Create the header component
        Header header = new BrowsePagesHeader("8080");
        add(header);
        
        // Initialize presenter
        presenter = new ShopManagerPresenter(this);
        presenter.fetchManagerPermissions(_username);

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
        Button addProductsbtn = new Button("Add Product", e -> presenter.viewProducts());
        Button addDiscountsBtn = new Button("Add Discount", e -> presenter.addDiscounts());
        Button changeProductPolicyBtn = new Button("Change Product Policy", e -> presenter.changeProductPolicy());
        Button changeShopPolicyBtn = new Button("Change Shop Policy", e -> presenter.changeProductPolicy());
        Button appointManagerBtn = new Button("Appoint Manager", e -> _appointManagerDialog.open());
        Button appointOwnerBtn = new Button("Appoint Owner", e -> _appointOwnerDialog.open());
        Button viewSubordinateBtn = new Button("View Subordinates", e -> presenter.viewSubordinate());
        Button viewShopRolesBtn = new Button("View Shop Roles", e -> {
            presenter.fetchShopManagers(managers -> {
                setManagers(managers);
                _viewRolesDialog = createViewRolesDialog();
                _viewRolesDialog.open();
            
            });
        });
        Button viewPurchasesBtn = new Button("View Purchases", e -> presenter.viewPurchases());
        Button viewProductsbtn = new Button("View Products", e -> presenter.viewProducts());

        // Here we create a set of permissions from the strings.
        _permissions = permissions.stream()
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

            if(!_permissions.contains(Permission.ADD_PRODUCT)){
                addProductsbtn.setEnabled(false);
            }
            if(!_permissions.contains(Permission.ADD_DISCOUNT_POLICY)){
                addDiscountsBtn.setEnabled(false);
            }
            if(!_permissions.contains(Permission.CHANGE_PRODUCT_POLICY)){
                changeProductPolicyBtn.setEnabled(false);
            }
            if(!_permissions.contains(Permission.CHANGE_SHOP_POLICY)){
                changeShopPolicyBtn.setEnabled(false);
            }
            if(!_permissions.contains(Permission.APPOINT_MANAGER)){
                appointManagerBtn.setEnabled(false);
            }
            if(!_permissions.contains(Permission.GET_ROLES_INFO)){
                viewShopRolesBtn.setEnabled(false);
            }
            if(!_permissions.contains(Permission.GET_PURCHASE_HISTORY)){
                viewPurchasesBtn.setEnabled(false);
            }
  
        }
        buttonsLayout.add(appointOwnerBtn, appointManagerBtn, viewSubordinateBtn, viewShopRolesBtn, addProductsbtn, viewProductsbtn, viewPurchasesBtn, addDiscountsBtn, changeProductPolicyBtn, changeShopPolicyBtn);
        add(_title, buttonsLayout);

        //After we have the permissions, we can create the dialog
        _appointManagerDialog = createAppointManagerDialog();
        _appointOwnerDialog = createAppointOwnerDialog();
    }

    public int getShopId() {
        return _shopId;
    }

    // Retrieve the shop ID from the URL
    @Override
    public void setParameter(BeforeEvent event, Integer parameter) {
        if(parameter != null){
            _shopId = parameter;
        }
    }

    public Dialog createAppointManagerDialog() {
        Dialog dialog = new Dialog();

        // Create form layout
        FormLayout formLayout = new FormLayout();

        // Create the TextField for the new manager's username
        TextField usernameField = new TextField("New Manager Username");
        formLayout.add(usernameField);

        // Create a set to store selected permissions
        Set<Permission> selectedPermissions = new HashSet<>();

        // If Founder or Owner can select all permissions
        if(_permissions.contains(Permission.FOUNDER) || _permissions.contains(Permission.OWNER)){
            for (Permission p: Permission.values()){
                if(p != Permission.FOUNDER && p != Permission.OWNER){
                    Checkbox permissionCheckbox = new Checkbox(PermissionMapper.getPermissionName(p));
                    permissionCheckbox.addValueChangeListener(event -> {
                        if (event.getValue()) {
                            selectedPermissions.add(p);
                        } else {
                            selectedPermissions.remove(p);
                        }
                    });
                    formLayout.add(permissionCheckbox);
                }
            }
        }else{
            // Add checkboxes for each permission
            for (Permission permission : _permissions) {
                if (permission == Permission.FOUNDER || permission == Permission.OWNER) {
                    continue;
                }
                Checkbox permissionCheckbox = new Checkbox(PermissionMapper.getPermissionName(permission));
                permissionCheckbox.addValueChangeListener(event -> {
                    if (event.getValue()) {
                        selectedPermissions.add(permission);
                    } else {
                        selectedPermissions.remove(permission);
                    }
                });
                formLayout.add(permissionCheckbox);
            }
        }
        
        

        // Add a submit button
        Button submitButton = new Button("Appoint", event -> {
            String username = usernameField.getValue();
            if (username.isEmpty()) {
                Notification.show("Please enter a username");
            } else if (selectedPermissions.isEmpty()) {
                Notification.show("Please select at least one permission");
            } else {
                // Handle the appointment logic here
                presenter.appointManager(username, selectedPermissions);
                dialog.close();
            }
        });

        // Add a cancel button
        Button cancelButton = new Button("Cancel", event -> dialog.close());

        // Add components to the dialog
        dialog.add(formLayout, submitButton, cancelButton);

        return dialog;
    }


    public Dialog createAppointOwnerDialog() {
        Dialog dialog = new Dialog();

        // Create form layout
        FormLayout formLayout = new FormLayout();

        // Create the TextField for the new manager's username
        TextField usernameField = new TextField("New Owner Username");
        formLayout.add(usernameField);

        // Add a submit button
        Button submitButton = new Button("Appoint", event -> {
            String username = usernameField.getValue();
            if (username.isEmpty()) {
                Notification.show("Please enter a username"); 
            } else {
                // Handle the appointment logic here
                presenter.appointOwner(username);
                dialog.close();
            }
        });

        // Add a cancel button
        Button cancelButton = new Button("Cancel", event -> dialog.close());

        // Add components to the dialog
        dialog.add(formLayout, submitButton, cancelButton);

        return dialog;
    }

    // public Dialog createViewRolesDialog() {
    //     // Create a dialog
    //     Dialog dialog = new Dialog();

    //      // Create a grid
    //     _viewRolesGrid = new Grid<>(ShopManagerDto.class, false);
    //     _viewRolesGrid.addColumn(ShopManagerDto::getUsername).setHeader("Username");
    //     _viewRolesGrid.addColumn(ShopManagerDto::getRole).setHeader("Role");
    //     _viewRolesGrid.addColumn(manager -> String.join(", ", 
    //             manager.getPermissions().stream()
    //                 .map(p -> PermissionMapper.getPermissionName(p))
    //                 .toArray(String[]::new)
    //     )).setHeader("Permissions");


    //     if(_managers != null){
    //         // Set items to the grid
    //         _viewRolesGrid.setItems(_managers);
    //     }
        
    //     dialog.add(_viewRolesGrid);
    //     dialog.setWidth("800px"); // Set the desired width
    //     dialog.setHeight("400px"); // Set the desired height
    //     return dialog;
    // }

    public Dialog createViewRolesDialog() {
    // Create a dialog
    Dialog dialog = new Dialog();

    // Create a grid
    _viewRolesGrid = new Grid<>(ShopManagerDto.class, false);
    _viewRolesGrid.addColumn(ShopManagerDto::getUsername).setHeader("Username");
    _viewRolesGrid.addColumn(ShopManagerDto::getRole).setHeader("Role");

    // Add a column for expand/collapse toggle
    _viewRolesGrid.addComponentColumn(shopManager -> {
        Button toggleButton = new Button("Details");
        toggleButton.addClickListener(e -> {
            if (_viewRolesGrid.isDetailsVisible(shopManager)) {
                _viewRolesGrid.setDetailsVisible(shopManager, false);
                toggleButton.setText("Details");
            } else {
                _viewRolesGrid.setDetailsVisible(shopManager, true);
                toggleButton.setText("Hide");
            }
        });
        return toggleButton;
    }).setHeader("Permissions");

    // Set the details generator for expandable rows
    _viewRolesGrid.setItemDetailsRenderer(new ComponentRenderer<>(shopManager -> {
        VerticalLayout detailsLayout = new VerticalLayout();
        shopManager.getPermissions().forEach(permission -> {
            Span permissionSpan = new Span(PermissionMapper.getPermissionName(permission));
            detailsLayout.add(permissionSpan);
        });
        return detailsLayout;
    }));

    // Set items to the grid if available
    if (_managers != null) {
        _viewRolesGrid.setItems(_managers);
    }

    dialog.add(_viewRolesGrid);
    dialog.setWidth("900px"); // Set the desired width of the dialog
    dialog.setHeight("500px"); // Set the desired height of the dialog

    return dialog;
}


    public void openViewRolesDialog() {
        _viewRolesDialog.open();
    }

    public void setManagers(List<ShopManagerDto> managers) {
        _managers = managers;
    }
    
}
