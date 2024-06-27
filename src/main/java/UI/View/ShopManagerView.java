package UI.View;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.model.Dial;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import UI.Model.Permission;
import UI.Model.PermissionMapper;
import UI.Model.ShopDiscountDto;
import UI.Model.ShopManagerDto;
import UI.Presenter.ShopManagerPresenter;
import enums.Category;


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
    private Dialog _viewSubordinatesDialog;
    private Dialog _viewDiscountsDialog;
    private List<ShopManagerDto> _managers;
    private List<ShopManagerDto> _subordinates;
    private List<ShopDiscountDto> _discounts;
    private Grid<ShopManagerDto> _viewRolesGrid;
    private Grid<ShopManagerDto> _viewSubordinatesGrid;
    private Grid<ShopDiscountDto> _viewDiscountsGrid;
    
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
        Button addProductsbtn = new Button("Add Product");
        Button addDiscountsBtn = new Button("View Discounts", e -> {
            presenter.fetchShopDiscounts(discounts -> {
            _discounts = discounts;
            _viewDiscountsDialog = createViewDiscountsDialog();
            _viewDiscountsDialog.open();
            });
        });
        Button changeProductPolicyBtn = new Button("Change Product Policy", e -> presenter.changeProductPolicy());
        Button changeShopPolicyBtn = new Button("Change Shop Policy", e -> presenter.changeProductPolicy());
        Button appointManagerBtn = new Button("Appoint Manager", e -> _appointManagerDialog.open());
        Button appointOwnerBtn = new Button("Appoint Owner", e -> _appointOwnerDialog.open());
        Button viewSubordinateBtn = new Button("View Subordinates", e -> {
            presenter.fetchMySubordinates(managers -> {
                _subordinates = managers;
                _viewSubordinatesDialog = createViewSubordinatesDialog();
                _viewSubordinatesDialog.open();
            
            });
        });
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

        // Create registration dialog
        Dialog addProductDialog = createaddProductDialog();
        addProductsbtn.addClickListener(event -> addProductDialog.open());

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


    public Dialog createViewRolesDialog() {
        // Create a dialog
        Dialog dialog = new Dialog();

         // Title for the dialog
         H3 title = new H3("Shop Roles");

         // Create a vertical layout to hold the title and the grid
         VerticalLayout content = new VerticalLayout();
         content.add(title);

        // Create a grid
        _viewRolesGrid = new Grid<>(ShopManagerDto.class, false);
        _viewRolesGrid.addColumn(ShopManagerDto::getUsername).setHeader("Username");
        _viewRolesGrid.addColumn(ShopManagerDto::getRole).setHeader("Role");

        // Add a column for expand/collapse toggle
        _viewRolesGrid.addComponentColumn(shopManager -> {
            Button toggleButton = new Button("Details");
            updateToggleButton(toggleButton, shopManager, _viewRolesGrid);
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

        content.add(_viewRolesGrid);
        dialog.add(content);
        dialog.setWidth("900px"); // Set the desired width of the dialog
        dialog.setHeight("500px"); // Set the desired height of the dialog

        return dialog;
    }

    public Dialog createViewSubordinatesDialog(){
        // Create a dialog
        Dialog dialog = new Dialog();

        // Title for the dialog
        H3 title = new H3("My Subordinates");

        // Create a vertical layout to hold the title and the grid
        VerticalLayout content = new VerticalLayout();
        content.add(title);

        // Create a grid
        _viewSubordinatesGrid = new Grid<>(ShopManagerDto.class, false);
        _viewSubordinatesGrid.addColumn(ShopManagerDto::getUsername).setHeader("Username");
        _viewSubordinatesGrid.addColumn(ShopManagerDto::getRole).setHeader("Role");

        // Add a column for expand/collapse toggle
        _viewSubordinatesGrid.addComponentColumn(shopManager -> {
            Button toggleButton = new Button("Details");
            updateToggleButton(toggleButton, shopManager, _viewSubordinatesGrid);
            toggleButton.addClickListener(e -> {
                if (_viewSubordinatesGrid.isDetailsVisible(shopManager)) {
                    _viewSubordinatesGrid.setDetailsVisible(shopManager, false);
                    toggleButton.setText("Details");
                } else {
                    _viewSubordinatesGrid.setDetailsVisible(shopManager, true);
                    toggleButton.setText("Hide");
                }
            });
            return toggleButton;
        }).setHeader("Permissions");

        // Set the details generator for expandable rows
        _viewSubordinatesGrid.setItemDetailsRenderer(new ComponentRenderer<>(shopManager -> {
            VerticalLayout detailsLayout = new VerticalLayout();
            shopManager.getPermissions().forEach(permission -> {
                Span permissionSpan = new Span(PermissionMapper.getPermissionName(permission));
                detailsLayout.add(permissionSpan);
            });
            return detailsLayout;
        }));

        // Disable row clicks to expand/collapse details
        _viewSubordinatesGrid.addItemClickListener(event -> {
            // Prevent automatic detail toggling on row click
            event.getItem();
        });

        // Set items to the grid if available
        if (_subordinates != null) {
            _viewSubordinatesGrid.setItems(_subordinates);
        }

        content.add(_viewSubordinatesGrid);
        dialog.add(content);
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

    private void updateToggleButton(Button toggleButton, ShopManagerDto shopManager, Grid<ShopManagerDto> grid) {
        if (grid.isDetailsVisible(shopManager)) {
            toggleButton.setText("Hide");
        } else {
            toggleButton.setText("Details");
        }
    }

    public Dialog createaddProductDialog()
    {
        Dialog dialog = new Dialog();

        // Create form layout
        FormLayout formLayout = new FormLayout();

        // Create a headline
        H2 headline = new H2("Add Product");
        headline.getStyle().set("margin", "0");

        // Create form fields
        TextField productNameField = new TextField("Product Name");
        ComboBox<String> categoryField = new ComboBox<>("Category");
        categoryField.setItems("Electronics", "Books", "Clothing", "Home", "Kitchen", "Sports", "Grocery", "Pharmacy");
        TextField priceField = new TextField("Price");
        priceField.setPattern("[0-9]+(\\.[0-9]{1,2})?"); // Allows whole numbers or decimals with up to 2 places
        priceField.setErrorMessage("Please enter a valid price");

        // Add form fields to form layout
        formLayout.add(productNameField, categoryField, priceField);

        // Create buttons
        Button addButton = new Button("Add", event -> {
            if (validateFields(productNameField, categoryField, priceField)) {
                // Convert category string to enum
                Category category = parseCategory(categoryField.getValue());
                if (category == Category.DEFAULT_VAL) {
                    Notification.show("Invalid category");
                    return;
                }

                // Process the form data (e.g., save product)
                presenter.addNewProduct(productNameField.getValue(), category, Double.parseDouble(priceField.getValue()));
                dialog.close();
            } else {
                Notification.show("Please fill in all fields correctly");
            }
        });

        Button cancelButton = new Button("Cancel", event -> dialog.close());

        Button refreshButton = new Button("Refresh", event -> {
            // Clear all form fields
            productNameField.clear();
            categoryField.clear();
            priceField.clear();
        });

        // Add buttons to form layout
        formLayout.add(addButton, refreshButton, cancelButton);

        // Add form layout to dialog content
        dialog.add(headline, formLayout);

        return dialog;

    }

    // Validate form fields
    private boolean validateFields(TextField productNameField, ComboBox<String> categoryField, TextField priceField) {
        return !productNameField.isEmpty() && !categoryField.isEmpty() && !priceField.isEmpty();
    }



    public static Category parseCategory(String categoryStr) {
        try {
            return Category.valueOf(categoryStr.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException ex) {
            // Handle if categoryStr is null or doesn't match any enum constant
            return Category.DEFAULT_VAL; // or throw exception or handle differently as needed
        }
    }

    public Dialog createViewDiscountsDialog(){
        // Create a dialog
        Dialog dialog = new Dialog();

        // Title for the dialog
        H3 title = new H3("Shop Discounts");

        // Create a vertical layout to hold the title and the grid
        VerticalLayout content = new VerticalLayout();
        content.add(title);

        // Create a grid
        _viewDiscountsGrid = new Grid<>(ShopDiscountDto.class, false);
        _viewDiscountsGrid.addColumn(ShopDiscountDto::getType).setHeader("Type");
        _viewDiscountsGrid.addColumn(ShopDiscountDto::getDiscount).setHeader("Discount");
        _viewDiscountsGrid.addColumn(ShopDiscountDto::getParticipants).setHeader("Participants");
        _viewDiscountsGrid.addColumn(ShopDiscountDto::getExpirationDate).setHeader("Expiration Date");


        // Set items to the grid if available
        if (_discounts != null) {
            _viewDiscountsGrid.setItems(_discounts);
        }

        content.add(_viewDiscountsGrid);
        dialog.add(content);
        dialog.setWidth("900px"); // Set the desired width of the dialog
        dialog.setHeight("500px"); // Set the desired height of the dialog

        return dialog;
    }
    

  
    
}
