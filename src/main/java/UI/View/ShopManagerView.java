package UI.View;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.user.UserRegistryMessageHandler;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import UI.Model.Permission;
import UI.Model.PermissionMapper;
import UI.Model.ProductDto;
import UI.Model.ShopDiscountDto;
import UI.Model.ShopManagerDto;
import UI.Model.ProductPolicy.MinAgeRuleDto;
import UI.Model.ProductPolicy.UserRuleDto;
import UI.Model.ShopPolicy.MinBasketPriceRuleDto;
import UI.Model.ShopPolicy.MinProductAmountRuleDto;
import UI.Model.ShopPolicy.ShoppingBasketRuleDto;
import UI.Presenter.ShopManagerPresenter;
import UI.Model.Category;


@Route(value = "user_shops")
public class ShopManagerView extends BaseView implements HasUrlParameter<Integer>{

    private ShopManagerPresenter presenter;
    private String _username;
    private Set<Permission> _permissions;
    private ProductDto _currentProductDto;
    private List<ShoppingBasketRuleDto> _shopRules;
    private List<UserRuleDto> _productRules;
    private H1 _title;
    private int _shopId;
    private Dialog _appointManagerDialog;
    private Dialog _appointOwnerDialog;
    private Dialog _viewRolesDialog;
    private Dialog _viewSubordinatesDialog;
    private Dialog _viewDiscountsDialog;
    private Dialog _addDiscountDialog;
    private Dialog _managePermissionsDialog;
    private Dialog _changeShopPolicyDialog;
    private Dialog _changeProductPolicyDialog;
    private Dialog _chooseProductToChangePolicyDialog;
    private Dialog _addShopRuleDialog;
    private Dialog _addProductRuleDialog;
    private List<ShopManagerDto> _managers;
    private List<ShopManagerDto> _subordinates;
    private List<ShopDiscountDto> _discounts;
    private Grid<ShopManagerDto> _viewRolesGrid;
    private Grid<ShopManagerDto> _viewSubordinatesGrid;
    private Grid<ShopDiscountDto> _viewDiscountsGrid;
    private Grid<ShoppingBasketRuleDto> _changeShopPolicyGrid;
    private Grid<UserRuleDto> _changeProductPolicyGrid;
    
    public ShopManagerView(){

        // Retrieve the username from the session
        _username = (String) VaadinSession.getCurrent().getAttribute("username");

        // Create the header component
        Header header = new BrowsePagesHeader("8080");
        add(header);
        
        
        // Initialize presenter
        presenter = new ShopManagerPresenter(this);
        presenter.fetchManagerPermissions(_username);

        // // Initialize the shop rules map
        // _shopRules = new HashMap<>();
        // _shopRules.put("MinBasketPrice", new ArrayList<>());
        // _shopRules.put("MinProductAmount", new ArrayList<>());

    }

     // Button creation helper method
    Button createButtonWithIcon(String text, VaadinIcon icon, ComponentEventListener<ClickEvent<Button>> listener) {
        Button button = new Button(text, new Icon(icon));
        button.addClickListener(listener);
        button.setWidth("300px");
        button.setHeight("60px");
        button.getElement().getStyle().set("font-size", "20px");
        return button;
    }

    public void createPermissionButtons(List<String> permissions) {
        if(permissions.isEmpty()){
            add(new Paragraph("No permissions found"));
            return;
        }

        // Create a vertical layout
        VerticalLayout buttonsLayout = new VerticalLayout();
        buttonsLayout.setAlignItems(Alignment.CENTER);

        // Create buttons
        Button addProductsBtn = createButtonWithIcon("Add Product", VaadinIcon.PLUS, event -> createaddProductDialog().open());
        Button addDiscountsBtn = createButtonWithIcon("View Discounts", VaadinIcon.GIFT, event -> {
            presenter.fetchShopDiscounts(discounts -> {
                _discounts = discounts;
                _viewDiscountsDialog = createViewDiscountsDialog();
                _viewDiscountsDialog.open();
            });
        });
        Button changeProductPolicyBtn = createButtonWithIcon("Change Product Policy", VaadinIcon.EDIT, event -> {
            presenter.fetchShopProducts(products -> {
                _chooseProductToChangePolicyDialog = createChangeProductsPolicyDialog(products);
                _chooseProductToChangePolicyDialog.open();
            });
        });
        Button changeShopPolicyBtn = createButtonWithIcon("Change Shop Policy", VaadinIcon.COGS, event -> {
            presenter.fetchShopPolicy(rules -> {
                _shopRules = rules;
                _changeShopPolicyDialog = createChangeShopPolicyDialog();
                _changeShopPolicyDialog.open();
            });
        });
        Button appointManagerBtn = createButtonWithIcon("Appoint Manager", VaadinIcon.USER_STAR, event -> _appointManagerDialog.open());
        Button appointOwnerBtn = createButtonWithIcon("Appoint Owner", VaadinIcon.USER_CHECK, event -> _appointOwnerDialog.open());
        Button viewSubordinateBtn = createButtonWithIcon("View Subordinates", VaadinIcon.USERS, event -> {
            presenter.fetchMySubordinates(managers -> {
                _subordinates = managers;
                _viewSubordinatesDialog = createViewSubordinatesDialog();
                _viewSubordinatesDialog.open();
            });
        });
        Button viewShopRolesBtn = createButtonWithIcon("View Shop Roles", VaadinIcon.USER_CHECK, event -> {
            presenter.fetchShopManagers(managers -> {
                setManagers(managers);
                _viewRolesDialog = createViewRolesDialog();
                _viewRolesDialog.open();
            });
        });
        Button viewPurchasesBtn = createButtonWithIcon("View Purchases", VaadinIcon.CART_O, event -> presenter.viewPurchases());
        Button viewProductsBtn = createButtonWithIcon("View Products", VaadinIcon.PACKAGE, event -> presenter.viewProducts());
        Button closeShopBtn = createButtonWithIcon("Close Shop", VaadinIcon.CLOSE, event -> {
        Dialog closeDialog = new Dialog();
        closeDialog.add(new Paragraph("Are you sure you want to close the shop?"));

        Button yesButton = new Button("Yes", yesEvent -> {
            // Logic to close the shop
            presenter.closeShop(String.valueOf(getShopId()));
            closeDialog.close();
        });
        yesButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button noButton = new Button("No", noEvent -> closeDialog.close());

        HorizontalLayout dialogButtons = new HorizontalLayout(yesButton, noButton);
        closeDialog.add(dialogButtons);
        closeDialog.open();
    });
    
    Button reopenShopBtn = createButtonWithIcon("Reopen Shop", VaadinIcon.SHOP, event -> {
        Dialog reopenDialog = new Dialog();
        reopenDialog.add(new Paragraph("Are you sure you want to reopen the shop?"));

        Button yesButton = new Button("Yes", yesEvent -> {
            // Logic to reopen the shop
            presenter.reopenShop(String.valueOf(getShopId()));
            reopenDialog.close();
        });
        yesButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        Button noButton = new Button("No", noEvent -> reopenDialog.close());

        HorizontalLayout dialogButtons = new HorizontalLayout(yesButton, noButton);
        reopenDialog.add(dialogButtons);
        reopenDialog.open();
    });

     // Add Edit Product button
     Button editProductBtn = createButtonWithIcon("Edit Product", VaadinIcon.EDIT, event -> createEditProductDialog().open());



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
                addProductsBtn.setEnabled(false);
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

        HorizontalLayout row = new HorizontalLayout();
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        int count = 0;
        for (Button button : Arrays.asList(
                appointOwnerBtn, appointManagerBtn, viewSubordinateBtn, viewShopRolesBtn,
                addProductsBtn, viewProductsBtn, viewPurchasesBtn, addDiscountsBtn,
                changeProductPolicyBtn, changeShopPolicyBtn, closeShopBtn, reopenShopBtn, editProductBtn)) {
            row.add(button);
            count++;
            if (count % 4 == 0) {
                row.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
                buttonsLayout.add(row);
                row = new HorizontalLayout();
            }
        }
        // Add any remaining buttons
        if (count % 4 != 0) {
            row.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            buttonsLayout.add(row);
        }

        add(_title, buttonsLayout);
    
        //After we have the permissions, we can create the dialog
        _appointManagerDialog = createAppointManagerDialog();
        _appointOwnerDialog = createAppointOwnerDialog();
    }

    private Dialog createEditProductDialog() {
        Dialog editProductDialog = new Dialog();
        TextField productIdField = new TextField("Enter Product ID");

        Button fetchProductBtn = new Button("Fetch Product", event -> {
            String productId = productIdField.getValue();
            // Logic to fetch product details using the product ID

            // Create buttons for editing product details
            Button updateQuantityBtn = new Button("Update Quantity", e -> createUpdateQuantityDialog(productId).open());
            Button updatePriceBtn = new Button("Update Price", e -> createUpdatePriceDialog(productId).open());
            Button updateNameBtn = new Button("Update Name", e -> createUpdateNameDialog(productId).open());
            Button updateCategoryBtn = new Button("Update Category", e -> createUpdateCategoryDialog(productId).open());

            VerticalLayout editOptionsLayout = new VerticalLayout(updateQuantityBtn, updatePriceBtn, updateNameBtn, updateCategoryBtn);
            editProductDialog.add(editOptionsLayout);
        });

        editProductDialog.add(productIdField, fetchProductBtn);
        return editProductDialog;
    }

    private Dialog createUpdateQuantityDialog(String productId) {
        Dialog dialog = new Dialog();
    
        // Use a TextField to allow numeric input and add a value change listener
        TextField quantityField = new TextField("New Quantity");
        quantityField.setPlaceholder("Enter quantity");
    
        // Add a value change listener to validate input
        quantityField.addValueChangeListener(event -> {
            String value = event.getValue();
            try {
                Integer.parseInt(value);
            } catch (NumberFormatException e) {
                quantityField.setInvalid(true);
                quantityField.setErrorMessage("Please enter a valid number");
            }
        });
    
        Button saveBtn = new Button("Save", event -> {
            String value = quantityField.getValue();
            try {
                Integer newQuantity = Integer.parseInt(value);
                // Logic to update the product quantity using the product ID and new quantity
                presenter.updateProductQuantity(getShopId(), Integer.parseInt(productId), newQuantity);
                dialog.close();
            } catch (NumberFormatException e) {
                Notification.show("Please enter a valid number");
            }
        });
    
        dialog.add(quantityField, saveBtn);
        return dialog;
    }

    private Dialog createUpdatePriceDialog(String productId) {
        Dialog dialog = new Dialog();
    
        // Use a TextField to allow numeric input and add a value change listener
        TextField priceField = new TextField("New Price");
        priceField.setPlaceholder("Enter price");
    
        // Add a value change listener to validate input
        priceField.addValueChangeListener(event -> {
            String value = event.getValue();
            try {
                Double.parseDouble(value);
                priceField.setInvalid(false);
                priceField.setErrorMessage(null);
            } catch (NumberFormatException e) {
                priceField.setInvalid(true);
                priceField.setErrorMessage("Please enter a valid price");
            }
        });
    
        Button saveBtn = new Button("Save", event -> {
            String value = priceField.getValue();
            try {
                Double newPrice = Double.parseDouble(value);
                // Logic to update the product price using the product ID and new price
                presenter.updateProductPrice(getShopId(), Integer.parseInt(productId), newPrice);
                dialog.close();
            } catch (NumberFormatException e) {
                Notification.show("Please enter a valid price");
            }
        });
    
        dialog.add(priceField, saveBtn);
        return dialog;
    }

    private Dialog createUpdateNameDialog(String productId) {
        Dialog dialog = new Dialog();
        TextField nameField = new TextField("New Name");
        nameField.setPlaceholder("Enter name");
    
        // Disable the save button initially
        Button saveBtn = new Button("Save");
        saveBtn.setEnabled(false);
    
        // Add a value change listener to validate input
        nameField.addValueChangeListener(event -> {
            String value = event.getValue();
            if (value == null || value.trim().isEmpty()) {
                nameField.setInvalid(true);
                nameField.setErrorMessage("Name cannot be empty");
                saveBtn.setEnabled(false);
            } else {
                nameField.setInvalid(false);
                nameField.setErrorMessage(null);
                saveBtn.setEnabled(true);
            }
        });
    
        // Add click listener to the save button
        saveBtn.addClickListener(event -> {
            String newName = nameField.getValue();
            if (newName != null && !newName.trim().isEmpty()) {
                presenter.updateProductName(getShopId(), Integer.parseInt(productId), newName);
                dialog.close();
                Notification.show("Name updated");
            } else {
                Notification.show("Please enter a valid name");
            }
        });
    
        dialog.add(nameField, saveBtn);
        return dialog;
    }

    private Dialog createUpdateCategoryDialog(String productId) {
        Dialog dialog = new Dialog();
        ComboBox<String> categoryField = new ComboBox<>("New Category");
        categoryField.setItems("Electronics", "Books", "Clothing", "Home", "Kitchen", "Sports", "Grocery", "Pharmacy");
        Button saveBtn = new Button("Save", event -> {
            Category category = parseCategory(categoryField.getValue());
                if (category == Category.DEFAULT_VAL) {
                    Notification.show("Invalid category");
                    return;
                }
            presenter.updateProductCategory(getShopId(), Integer.parseInt(productId), category);
            dialog.close();
        });
        dialog.add(categoryField, saveBtn);
        return dialog;
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
            Button managePermissionsBtn = new Button("Manage Permissions", e -> {
                createManagePermissionsDialog(shopManager).open();
            });
            if(!shopManager.getPermissions().contains(Permission.FOUNDER) && !shopManager.getPermissions().contains(Permission.OWNER)){
                detailsLayout.add(managePermissionsBtn);   
            }
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
        _viewDiscountsGrid.addColumn(ShopDiscountDto::getId).setHeader("ID");
        _viewDiscountsGrid.addColumn(ShopDiscountDto::getType).setHeader("Type");
        _viewDiscountsGrid.addColumn(ShopDiscountDto::getDiscount).setHeader("Discount");
        _viewDiscountsGrid.addColumn(ShopDiscountDto::getParticipants).setHeader("Participants");
        _viewDiscountsGrid.addColumn(ShopDiscountDto::getFormattedDate).setHeader("Expiration Date");
        
        
        _viewDiscountsGrid.addItemClickListener(event -> {
            ShopDiscountDto selectedItem = event.getItem();
            Dialog confirmationDialog = new Dialog();
            Span confirmationText = new Span("Are you sure you want to delete this discount?");
            
            // Create Yes and No buttons
            Button yesButton = new Button("Yes", e -> {
                // Handle the deletion here
                presenter.deleteDiscount(selectedItem, isSuccess ->{
                    if(isSuccess) {
                        presenter.fetchShopDiscounts(discounts -> {
                            _discounts = discounts;
                            _viewDiscountsDialog.close();
                            confirmationDialog.close();
                            _viewDiscountsDialog = createViewDiscountsDialog();
                            _viewDiscountsDialog.open();
                        });
                    }else{
                        confirmationDialog.close();
                    }
                });
            });
            
            Button noButton = new Button("No", e -> confirmationDialog.close());

            // Add the buttons to a HorizontalLayout
            Span spacer = new Span();
            HorizontalLayout buttonsLayout = new HorizontalLayout(yesButton, spacer, noButton);
            buttonsLayout.setWidthFull(); // Ensure the layout takes up the full width
            buttonsLayout.setFlexGrow(1, spacer); // Make yesButton take up available space, pushing noButton to the right
            
            // Create a layout for the dialog
            VerticalLayout dialogLayout = new VerticalLayout(confirmationText, buttonsLayout);
            confirmationDialog.add(dialogLayout);
            confirmationDialog.open();
        });

        // Set items to the grid if available
        if (_discounts != null) {
            _viewDiscountsGrid.setItems(_discounts);
        }

        content.add(_viewDiscountsGrid);
        
        Button addDiscountButton = new Button("Add Discount", e -> {
            _addDiscountDialog.open();
        });
        dialog.setWidth("900px"); // Set the desired width of the dialog
        dialog.setHeight("700px"); // Set the desired height of the dialog

        _addDiscountDialog = createAddDiscountDialog();
        content.add(addDiscountButton);
        dialog.add(content);
        
        return dialog;
    }

    public Dialog createAddDiscountDialog(){
        Dialog dialog = new Dialog();

        // Create form layout
        FormLayout formLayout = new FormLayout();

        // Create a headline
        H2 headline = new H2("Add New Discount");
        headline.getStyle().set("margin", "0");

        // Create form fields
        // Type choose
        Select<String> selectDiscountType = new Select<>();
        selectDiscountType.setLabel("Discount Type");
        selectDiscountType.setItems("Product Discount", "Shop Discount", "Category Discount");
        selectDiscountType.setValue("Product Discount");
        
        // Method choose
        Select<String> selectDiscountMethod = new Select<>();
        selectDiscountMethod.setLabel("Discount Method");
        selectDiscountMethod.setItems("Fixed", "Percentage");
        selectDiscountMethod.setValue("Fixed");
        
        NumberField discountValue = new NumberField("Discount Value");
        discountValue.setErrorMessage("Please enter a valid value for the discount");
        discountValue.setRequired(true);
        DatePicker expirationDate = new DatePicker("Expiration Date");
        expirationDate.setRequired(true);

        // Additional fields
        IntegerField productIdField = new IntegerField("Product ID");
        productIdField.setErrorMessage("Please enter a valid price");
        ComboBox<String> categoryField = new ComboBox<>("Category");
        List<String> categories = Arrays.stream(Category.values())
                                        .map(Enum::name)
                                        .collect(Collectors.toList());
        categoryField.setItems(categories); 

        // Add fields to the form layout
        formLayout.add(selectDiscountType, selectDiscountMethod, discountValue, expirationDate);

        // Listener to handle showing/hiding additional fields based on discount type
        selectDiscountType.addValueChangeListener(event -> {
            String selectedType = event.getValue();
            if ("Product Discount".equals(selectedType)) {
                if (!formLayout.getChildren().collect(Collectors.toList()).contains(productIdField)) {
                    productIdField.clear();
                    formLayout.add(productIdField);
                }
                formLayout.remove(categoryField);
            } else if ("Category Discount".equals(selectedType)) {
                if (!formLayout.getChildren().collect(Collectors.toList()).contains(categoryField)) {
                    categoryField.clear();
                    formLayout.add(categoryField);
                }
                formLayout.remove(productIdField);
            } else {
                formLayout.remove(productIdField, categoryField);
            }
        });

        // Create buttons
        Button submitButton = new Button("Submit", event -> {
            // Handle form submission
            String discountType = selectDiscountType.getValue();
            boolean isPercentage = selectDiscountMethod.getValue().equals("Percentage");
            Double value = discountValue.getValue();
            
            Date expiration = convertToDate(expirationDate.getValue()); // Convert LocalDate to Date

            // Validate fields (add your validation logic here)
            if(isPercentage && value < 0 || value > 100){
                Notification.show("Please enter a valid percentage value");
                return;
            }

            if(!isPercentage && value < 0){
                Notification.show("Please enter a valid discount value");
                return;
            }

            Category category = selectDiscountType.getValue().equals("Category Discount") ? Category.valueOf(categoryField.getValue()) : null;
            int product = selectDiscountType.getValue().equals("Product Discount") ? productIdField.getValue() : -1;

            presenter.addDiscount(discountType, isPercentage, value, expiration, product, category, isSuccess ->{
                if(isSuccess) {
                    presenter.fetchShopDiscounts(discounts -> {
                        _discounts = discounts;
                        _viewDiscountsDialog.close();
                        dialog.close();
                        _viewDiscountsDialog = createViewDiscountsDialog();
                        _viewDiscountsDialog.open();
                    });
                }else{
                    dialog.close();
                }
            }); 
        });

        submitButton.addClassName("pointer-cursor");

        Button cancelButton = new Button("Cancel", event -> {
            // Close the dialog
            dialog.close();
        });

        cancelButton.addClassName("pointer-cursor");

        // Create button layout
        HorizontalLayout buttonLayout = new HorizontalLayout(submitButton, cancelButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER); // Center the buttons

        // Add form layout and button layout to the dialog
        VerticalLayout dialogLayout = new VerticalLayout(headline, formLayout, buttonLayout);
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER); // Center the layout content
        dialog.add(dialogLayout);

        // Add listener to clear fields when dialog is opened
        dialog.addOpenedChangeListener(event -> {
            if (dialog.isOpened()) {
                selectDiscountType.clear();;
                selectDiscountMethod.clear();
                discountValue.clear();
                productIdField.clear();
                categoryField.clear();
                expirationDate.clear();
            }
        });

        return dialog;  
    }
    
            // Method to convert LocalDate to Date
    private Date convertToDate(LocalDate localDate) {
        return java.sql.Date.valueOf(localDate);
    }

    public Dialog createManagePermissionsDialog(ShopManagerDto manager) {
        Dialog dialog = new Dialog();

        // Create form layout
        FormLayout formLayout = new FormLayout();

        // Create a headline
        H2 headline = new H2("Manage Permissions");
        headline.getStyle().set("margin", "0");
        formLayout.add(headline);

        // Create a set to store selected permissions
        Set<Permission> selectedPermissions = new HashSet<>(manager.getPermissions());

        // Create CheckboxGroup for permissions
        CheckboxGroup<Permission> checkboxGroup = new CheckboxGroup<>();
        checkboxGroup.setLabel("Permissions");
        Set<Permission> allPermissions = new HashSet<>(Arrays.asList(Permission.values()));
        allPermissions.remove(Permission.FOUNDER); // Founder cannot be assigned
        allPermissions.remove(Permission.OWNER); // Owner cannot be assigned
        checkboxGroup.setItems(allPermissions);

        // If Founder or Owner, can select all permissions
        if (_permissions.contains(Permission.FOUNDER) || _permissions.contains(Permission.OWNER)) {
            checkboxGroup.setItemEnabledProvider(permission -> permission != Permission.FOUNDER && permission != Permission.OWNER);
        } else {
            checkboxGroup.setItemEnabledProvider(_permissions::contains);
        }

        checkboxGroup.setItemLabelGenerator(PermissionMapper::getPermissionName);
        checkboxGroup.setValue(selectedPermissions);

        checkboxGroup.addValueChangeListener(event -> {
            selectedPermissions.clear();
            selectedPermissions.addAll(event.getValue());
        });

        formLayout.add(checkboxGroup);

        // Add form layout to dialog
        dialog.add(formLayout);

        Button saveButton = new Button("Save", e -> {
            // Handle save logic here
            if(selectedPermissions.isEmpty()){
                Notification.show("Please select at least one permission");
                return;
            }
            if(selectedPermissions.equals(manager.getPermissions())){
                Notification.show("No changes made");
                return;
            }
            presenter.updatePermissions(manager.getUsername(), selectedPermissions, isSuccess -> {
                if(isSuccess){
                    Notification.show("Permissions updated successfully");
                    manager.setPermissions(selectedPermissions);
                    _viewSubordinatesDialog.close();
                    presenter.fetchMySubordinates(managers -> {
                        _subordinates = managers;
                        _viewSubordinatesDialog = createViewSubordinatesDialog();
                        _viewSubordinatesDialog.open();
                    });
                }else{
                    Notification.show("Failed to update permissions");
                }
            });
            dialog.close();
        });
        Button cancelButton = new Button("Cancel", e -> dialog.close());
        HorizontalLayout buttonsLayout = new HorizontalLayout(saveButton, cancelButton);
        formLayout.add(buttonsLayout);

        return dialog;
    }

    

    public Dialog createChangeShopPolicyDialog(){
        // Create a dialog
        Dialog dialog = new Dialog();

        // Title for the dialog
        H3 title = new H3("Shop Policy");

        // Create a vertical layout to hold the title and the grid
        VerticalLayout content = new VerticalLayout();
        content.add(title);

        // Create the new rules data structure
        List<ShoppingBasketRuleDto> newRules = new ArrayList<>();
        if(_shopRules != null){
            for (ShoppingBasketRuleDto rule : _shopRules) {
                newRules.add(rule.createCopy(rule));
            }
        }
        // Create a grid
        _changeShopPolicyGrid = new Grid<>(ShoppingBasketRuleDto.class, false);
        _changeShopPolicyGrid.addColumn(ShoppingBasketRuleDto::getRuleString).setHeader("Rules");
        
        
        _changeShopPolicyGrid.addItemClickListener(event -> {
            ShoppingBasketRuleDto rule = event.getItem();
            Dialog confirmationDialog = new Dialog();
            Span confirmationText = new Span("Are you sure you want to delete this rule?");
            
            // Create Yes and No buttons
            Button yesButton = new Button("Yes", e -> {
                // Remove the rule from the data source
                ListDataProvider<ShoppingBasketRuleDto> dataProvider = (ListDataProvider<ShoppingBasketRuleDto>) _changeShopPolicyGrid.getDataProvider();
                dataProvider.getItems().remove(rule);
                dataProvider.refreshAll();
                newRules.remove(rule);
                confirmationDialog.close();
            });
            
            Button noButton = new Button("No", e -> confirmationDialog.close());

            // Add the buttons to a HorizontalLayout
            Span spacer = new Span();
            HorizontalLayout buttonsLayout = new HorizontalLayout(yesButton, spacer, noButton);
            buttonsLayout.setWidthFull(); // Ensure the layout takes up the full width
            buttonsLayout.setFlexGrow(1, spacer); // Make yesButton take up available space, pushing noButton to the right
            
            // Create a layout for the dialog
            VerticalLayout dialogLayout = new VerticalLayout(confirmationText, buttonsLayout);
            confirmationDialog.add(dialogLayout);
            confirmationDialog.open();
        });

        _changeShopPolicyGrid.setItems(newRules);

        content.add(_changeShopPolicyGrid);

        //Save button
        Button saveChanges = new Button("Save", e-> {
            // Handle save logic here
            _shopRules = newRules;
            presenter.updateShopPolicy(_shopRules, isSuccess -> {
                if(isSuccess){
                    Notification.show("Shop policy updated successfully");
                }else{
                    Notification.show("Failed to update shop policy");
                }
            });
            dialog.close();
        
        });
        Button addShopRule = new Button("Add Rule", e -> {
            _addShopRuleDialog.open();
        });
        dialog.setWidth("900px"); // Set the desired width of the dialog
        dialog.setHeight("700px"); // Set the desired height of the dialog

        _addShopRuleDialog = createAddShopRuleDialog(newRules);
        HorizontalLayout buttonsLayout = new HorizontalLayout(addShopRule, saveChanges);
        content.add(buttonsLayout);
        dialog.add(content);
        
        return dialog;
    }

    public Dialog createAddShopRuleDialog(List<ShoppingBasketRuleDto> newRules){
        Dialog dialog = new Dialog();

        // Create form layout
        FormLayout formLayout = new FormLayout();

        // Create a headline
        H2 headline = new H2("Add New Shop Rule");
        headline.getStyle().set("margin", "0");

        // Create form fields
        // Type choose
        Select<String> selectRuleType = new Select<>();
        selectRuleType.setLabel("Rule Type");
        selectRuleType.setItems("Min Busket Price", "Min Product Amount");
        selectRuleType.setValue("Min Busket Price");
        
        //for min busket price
        NumberField minBasketPrice = new NumberField("Value");
        minBasketPrice.setErrorMessage("Please enter a valid value for the price");
        minBasketPrice.setRequired(true);

        //for min product amount
        IntegerField productIdField = new IntegerField("Product ID");
        productIdField.setErrorMessage("Please enter a valid id");
        productIdField.setRequired(true);
        IntegerField minProductAmount = new IntegerField("Value");
        minProductAmount.setErrorMessage("Please enter a valid value for the amount");
        minProductAmount.setRequired(true);


        // Add fields to the form layout
        formLayout.add(selectRuleType, minBasketPrice);

        // Listener to handle showing/hiding additional fields based on rule type
        selectRuleType.addValueChangeListener(event -> {
            String selectedType = event.getValue();
            if ("Min Busket Price".equals(selectedType)) {
                minBasketPrice.clear();
                formLayout.remove(productIdField);
                formLayout.remove(minProductAmount);
                formLayout.add(minBasketPrice);
            } else if ("Min Product Amount".equals(selectedType)) {
                minProductAmount.clear();
                formLayout.remove(minBasketPrice);
                formLayout.add(productIdField);
                formLayout.add(minProductAmount);
            } else {
                //do nothing for now
            }
        });

        // Create buttons
        Button submitButton = new Button("Add", event -> {
            // Handle form submission
            String ruleType = selectRuleType.getValue();
            ShoppingBasketRuleDto rule = null;
            if("Min Busket Price".equals(ruleType)){
                if(minBasketPrice.getValue() < 0){
                    Notification.show("Please enter a valid price");
                    return;
                }
                rule = new MinBasketPriceRuleDto(minBasketPrice.getValue());
                newRules.add(rule);
            }else if("Min Product Amount".equals(ruleType)){
                if(minProductAmount.getValue() < 0){
                    Notification.show("Please enter a valid amount");
                    return;
                }
                if(productIdField.getValue() < 0){
                    Notification.show("Please enter a valid product id");
                    return;
                }
                rule = new MinProductAmountRuleDto(productIdField.getValue(), minProductAmount.getValue());
                newRules.add(rule);
            }else{
                //do nothing for now
            }
            
            ListDataProvider<ShoppingBasketRuleDto> dataProvider = (ListDataProvider<ShoppingBasketRuleDto>) _changeShopPolicyGrid.getDataProvider();
            dataProvider.refreshAll();
            dialog.close();
        });

        submitButton.addClassName("pointer-cursor");

        Button cancelButton = new Button("Cancel", event -> {
            // Close the dialog
            dialog.close();
        });

        cancelButton.addClassName("pointer-cursor");

        // Create button layout
        HorizontalLayout buttonLayout = new HorizontalLayout(submitButton, cancelButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER); // Center the buttons

        // Add form layout and button layout to the dialog
        VerticalLayout dialogLayout = new VerticalLayout(headline, formLayout, buttonLayout);
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER); // Center the layout content
        dialog.add(dialogLayout);

        // Add listener to clear fields when dialog is opened
        dialog.addOpenedChangeListener(event -> {
            if (dialog.isOpened()) {
                selectRuleType.clear();
                minBasketPrice.clear();
                minProductAmount.clear();
                productIdField.clear();
            }
        });

        return dialog;  
    }

    public Dialog createChangeProductsPolicyDialog(List<ProductDto> products){
        // Create a dialog
        Dialog dialog = new Dialog();

        // Title for the dialog
        H3 title = new H3("Products Policy");

        // Create a vertical layout to hold the title and the grid
        VerticalLayout content = new VerticalLayout();
        content.add(title);

        // Create List of names of products
        List<String> productNames = new ArrayList<>();
        for (ProductDto product : products) {
            productNames.add(product.getProductName()+" - "+product.getProductId());
        }
        // Type choose
        Select<String> selectProduct = new Select<>();
        selectProduct.setLabel("Product");
        selectProduct.setItems(productNames);

        content.add(selectProduct);

        Button viewPolicyBtn = new Button("View Policy", e -> {
            String selectedProduct = selectProduct.getValue();
            String[] parts = selectedProduct.split(" - ");
            int productId = Integer.parseInt(parts[1]);
            ProductDto selectedProductDto = null;
            for (ProductDto product : products) {
                if(product.getProductId() == productId){
                    selectedProductDto = product;
                    break;
                }
            }
            if(selectedProductDto != null){
                _currentProductDto = selectedProductDto;
                presenter.fetchProductPolicy(selectedProductDto, rules -> {
                    _productRules = rules;
                    _changeProductPolicyDialog = createViewAndChangeProdcutPolicyDialog(_currentProductDto);
                    _changeProductPolicyDialog.open();
                });
            }
        });

        content.add(viewPolicyBtn);
        dialog.add(content);
        return dialog;
    }
    public Dialog createViewAndChangeProdcutPolicyDialog(ProductDto product){
        // Create a dialog
        Dialog dialog = new Dialog();

        // Title for the dialog
        H3 title = new H3("Product "+product.getProductName()+" Policy");

        // Create a vertical layout to hold the title and the grid
        VerticalLayout content = new VerticalLayout();
        content.add(title);

        // Create the new rules data structure
        List<UserRuleDto> newRules = new ArrayList<>();
        if(_shopRules != null){
            for (UserRuleDto rule : _productRules) {
                newRules.add(rule.createCopy(rule));
            }
        }
        // Create a grid
        _changeProductPolicyGrid = new Grid<>(UserRuleDto.class, false);
        _changeProductPolicyGrid.addColumn(UserRuleDto::getRuleString).setHeader("Rules");
        
        
        _changeProductPolicyGrid.addItemClickListener(event -> {
            UserRuleDto rule = event.getItem();
            Dialog confirmationDialog = new Dialog();
            Span confirmationText = new Span("Are you sure you want to delete this rule?");
            
            // Create Yes and No buttons
            Button yesButton = new Button("Yes", e -> {
                // Remove the rule from the data source
                ListDataProvider<UserRuleDto> dataProvider = (ListDataProvider<UserRuleDto>) _changeProductPolicyGrid.getDataProvider();
                dataProvider.getItems().remove(rule);
                dataProvider.refreshAll();
                newRules.remove(rule);
                confirmationDialog.close();
            });
            
            Button noButton = new Button("No", e -> confirmationDialog.close());

            // Add the buttons to a HorizontalLayout
            Span spacer = new Span();
            HorizontalLayout buttonsLayout = new HorizontalLayout(yesButton, spacer, noButton);
            buttonsLayout.setWidthFull(); // Ensure the layout takes up the full width
            buttonsLayout.setFlexGrow(1, spacer); // Make yesButton take up available space, pushing noButton to the right
            
            // Create a layout for the dialog
            VerticalLayout dialogLayout = new VerticalLayout(confirmationText, buttonsLayout);
            confirmationDialog.add(dialogLayout);
            confirmationDialog.open();
        });

        _changeProductPolicyGrid.setItems(newRules);

        content.add(_changeProductPolicyGrid);

        //Save button
        Button saveChanges = new Button("Save", e-> {
            // Handle save logic here
            _productRules = newRules;
            presenter.updateProductPolicy(_productRules, product, isSuccess -> {
                if(isSuccess){
                    Notification.show("Product policy updated successfully");
                }else{
                    Notification.show("Failed to update product policy");
                }
            });
            dialog.close();
        
        });
        Button addShopRule = new Button("Add Rule", e -> {
            _addShopRuleDialog.open();
        });
        dialog.setWidth("900px"); // Set the desired width of the dialog
        dialog.setHeight("700px"); // Set the desired height of the dialog

        _addShopRuleDialog = createAddProductRuleDialog(newRules);
        HorizontalLayout buttonsLayout = new HorizontalLayout(addShopRule, saveChanges);
        content.add(buttonsLayout);
        dialog.add(content);
        
        return dialog;
    }

    public Dialog createAddProductRuleDialog(List<UserRuleDto> newRules){
        Dialog dialog = new Dialog();

        // Create form layout
        FormLayout formLayout = new FormLayout();

        // Create a headline
        H2 headline = new H2("Add New Product Rule");
        headline.getStyle().set("margin", "0");

        // Create form fields
        // Type choose
        Select<String> selectRuleType = new Select<>();
        selectRuleType.setLabel("Rule Type");
        selectRuleType.setItems("Min Age Rule");
        selectRuleType.setValue("Min Age Rule");
        
        //for min age rule
        IntegerField minAge = new IntegerField("Value");
        minAge.setErrorMessage("Please enter a valid value for the age");
        minAge.setRequired(true);


        // Add fields to the form layout
        formLayout.add(selectRuleType, minAge);

        // Listener to handle showing/hiding additional fields based on rule type
        selectRuleType.addValueChangeListener(event -> {
            String selectedType = event.getValue();
            if ("Min Age Rule".equals(selectedType)) {
                minAge.clear();
                formLayout.add(minAge);
            } else {
                //do nothing for now
            }
        });

        // Create buttons
        Button submitButton = new Button("Add", event -> {
            // Handle form submission
            String ruleType = selectRuleType.getValue();
            UserRuleDto rule = null;
            if("Min Age Rule".equals(ruleType)){
                if(minAge.getValue() <= 0){
                    Notification.show("Please enter a valid age");
                    return;
                }
                rule = new MinAgeRuleDto(minAge.getValue());
                newRules.add(rule);
            }else{
                //do nothing for now
            }
            
            ListDataProvider<UserRuleDto> dataProvider = (ListDataProvider<UserRuleDto>) _changeProductPolicyGrid.getDataProvider();
            dataProvider.refreshAll();
            dialog.close();
        });

        submitButton.addClassName("pointer-cursor");

        Button cancelButton = new Button("Cancel", event -> {
            // Close the dialog
            dialog.close();
        });

        cancelButton.addClassName("pointer-cursor");

        // Create button layout
        HorizontalLayout buttonLayout = new HorizontalLayout(submitButton, cancelButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER); // Center the buttons

        // Add form layout and button layout to the dialog
        VerticalLayout dialogLayout = new VerticalLayout(headline, formLayout, buttonLayout);
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER); // Center the layout content
        dialog.add(dialogLayout);

        // Add listener to clear fields when dialog is opened
        dialog.addOpenedChangeListener(event -> {
            if (dialog.isOpened()) {
                selectRuleType.clear();
                minAge.clear();
            }
        });

        return dialog;  
    }

    
}
