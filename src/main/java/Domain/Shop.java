package Domain;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import Domain.ShopFacade.Category;
import Domain.Discounts.Discount;
import Exceptions.*;

public class Shop {
    private Integer _shopId;
    private String _shopFounder; // Shop founder username
    private Map<Integer, Product> _productMap; // <ProductId, Product>
    private List<ShopOrder> _orderHistory;
    private Map<String, Role> _userToRole; // <userName, Role>
    private static final Logger logger = Logger.getLogger(Shop.class.getName());
    private List<Discount> _discounts;
    private String _bankDetails;
    private String _shopAddress;
    private Double _shopRating;
    private Integer _shopRatersCounter;

    // Constructor
    public Shop(Integer shopId, String shopFounderUserName, String bankDetails, String shopAddress)
            throws ShopException {
        try {
            logger.log(Level.INFO, "Shop - constructor: Creating a new shop with id " + shopId
                    + ". The Founder of the shop is: " + shopFounderUserName);
            _shopId = shopId;
            _shopFounder = shopFounderUserName;
            _productMap = new HashMap<>(); // Initialize the product map
            _orderHistory = new ArrayList<>();
            _userToRole = new HashMap<>();
            _bankDetails = bankDetails;
            _shopAddress = shopAddress;
            _discounts = new ArrayList<>();
            this._shopRating = -1.0;
            this._shopRatersCounter = 0;
            Role founder = new Role(shopFounderUserName, shopId, null, EnumSet.of(Permission.FOUNDER));
            _userToRole.putIfAbsent(shopFounderUserName, founder);
            logger.log(Level.FINE, "Shop - constructor: Successfully created a new shop with id " + shopId
                    + ". The Founder of the shop is: " + shopFounderUserName);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Shop - constructor: Error while creating a new shop with id " + shopId
                    + ". The Founder of the shop is: " + shopFounderUserName);
            throw new ShopException("Error while creating shop.");
        }
    }

    /**
     * Check if a username has a role in shop.
     * 
     * @param username the username to check.
     * @return True - if has role. False - if doesn't have.
     * @throws ShopException
     */
    public boolean checkIfHasRole(String username) throws ShopException {
        logger.log(Level.FINE,
                "Shop - checkIfHasRole: Checking if user " + username + " has a role in shop with id: " + _shopId);
        if (username == null) {
            return false;
        }
        return _userToRole.containsKey(username);
    }

    // get role of the user in the shop
    public Role getRole(String username) throws ShopException {
        if (!checkIfHasRole(username)) {
            throw new ShopException("User " + username + " doesn't have a role in this shop with id " + _shopId);
        }
        return _userToRole.get(username);
    }

    /**
     * Check if a user has a specific permission to do an action.
     * 
     * @param username the username of the user that does the action.
     * @param p        the permission needed.
     * @return true if has permission. false if hasn't.
     * @throws ShopException
     */
    public boolean checkPermission(String username, Permission p) throws ShopException {
        logger.log(Level.FINE, "Shop - checkPermission: Checking if user " + username + " has permission: " + p);
        if (!checkIfHasRole(username)) {
            logger.log(Level.SEVERE,
                    "Shop - checkPermission: user " + username + " doesn't have a role in the shop with id " + _shopId);
            throw new ShopException("User " + username + " doesn't have a role in this shop with id " + _shopId);
        }
        Role role = _userToRole.get(username);
        if (!isOwnerOrFounder(role) && !role.hasPermission(p)) {
            return false;
        }
        return true;
    }

    /**
     * Check if a user has a at least one permission of the given set.
     * 
     * @param username    the username of the user that does the action.
     * @param permissions the permissions set.
     * @return true if has permission. false if hasn't.
     * @throws ShopException
     */
    public boolean checkAtLeastOnePermission(String username, Set<Permission> permissions) throws ShopException {
        logger.log(Level.FINE, "Shop - checkAtLeastOnePermission: Checking if user " + username
                + " has at least one permission from the set: " + permissions);
        if (!checkIfHasRole(username)) {
            logger.log(Level.SEVERE, "Shop - checkAtLeastOnePermission: user " + username
                    + " doesn't have a role in the shop with id " + _shopId);
            throw new ShopException("User " + username + " doesn't have a role in this shop with id " + _shopId);
        }
        Role role = _userToRole.get(username);
        if (!isOwnerOrFounder(role) && !role.hasAtLeastOnePermission(permissions)) {
            return false;
        }
        return true;
    }

    /**
     * Check if a user has all permissions of the given set.
     * 
     * @param username    the username of the user that does the action.
     * @param permissions the permissions needed.
     * @return true if has permission. false if hasn't.
     * @throws ShopException
     */
    public boolean checkAllPermission(String username, Set<Permission> permissions) throws ShopException {
        logger.log(Level.FINE, "Shop - checkAllPermission: Checking if user " + username
                + " has all permissions from the set: " + permissions);
        if (!checkIfHasRole(username)) {
            logger.log(Level.SEVERE, "Shop - checkAllPermission: user " + username
                    + " doesn't have a role in the shop with id " + _shopId);
            throw new ShopException("User " + username + " doesn't have a role in this shop with id " + _shopId);
        }
        Role role = _userToRole.get(username);
        if (!isOwnerOrFounder(role) && !role.hasAllPermissions(permissions)) {
            return false;
        }
        return true;
    }

    public double getProductPriceById(Integer product) {
        return _productMap.get(product).getPrice();

    }

    public boolean isOwnerOrFounder(Role role) {
        return role.isFounder() || role.isOwner();
    }

    /**
     * 
     * @param username           the user that does the appointment.
     * @param newManagerUserName the user that is appointed.
     * @param permissions        the permissions we give this new manager.
     * @return True - if success. False - if failed.
     * @throws PermissionException
     * @throws ShopException
     * @throws RoleException
     */
    public void AppointManager(String username, String newManagerUserName, Set<Permission> permissions)
            throws ShopException, PermissionException, RoleException {
        logger.log(Level.INFO, "Shop - AppointManager: " + username + " trying to appoint " + newManagerUserName
                + " as a new manager with permissions: " + permissions);
        if (!checkAtLeastOnePermission(username,
                EnumSet.of(Permission.FOUNDER, Permission.OWNER, Permission.APPOINT_MANAGER))) {
            logger.log(Level.SEVERE, "Shop - AppointManager: user " + username
                    + " doesn't have permission to add new manager to shop with id " + _shopId);
            throw new PermissionException(
                    "User " + username + " doesn't have permission to add new manager to shop with id " + _shopId);
        }
        if (checkIfHasRole(newManagerUserName)) {
            logger.log(Level.SEVERE, "Shop - AppointManager: user " + username + " already in shop with id " + _shopId);
            throw new ShopException("User " + username + " already in shop with id " + _shopId);
        }
        if (permissions.isEmpty()) {
            logger.log(Level.SEVERE, "Shop - AppointManager: Error while appointing a new manager with 0 permissions.");
            throw new PermissionException("Cannot create a manager with 0 permissions.");
        }
        if (permissions.contains(Permission.OWNER) || permissions.contains(Permission.FOUNDER)) {
            logger.log(Level.SEVERE,
                    "Shop - AppointManager: Error while appointing a new manager with founder of owner permissions.");
            throw new PermissionException("Cannot appoint manager with owner or founder permissions.");
        }
        // All constraints checked
        Role appointer = _userToRole.get(username);
        // Here we make sure that a manager doesn't give permissions that he doesn't
        // have to his assignee.
        if (!isOwnerOrFounder(appointer)) {
            permissions.retainAll(appointer.getPermissions());
        }
        Role manager = new Role(newManagerUserName, _shopId, username, permissions);

        _userToRole.putIfAbsent(newManagerUserName, manager);
        logger.log(Level.INFO, "Shop - AppointManager: " + username + " successfully appointed " + newManagerUserName
                + " as a new manager with permissions: " + permissions + "in the shop with id " + _shopId);
    }

    /**
     * Appoint new owner
     * 
     * @param username           the user that does the appointment.
     * @param newManagerUserName the user that is appointed.
     * @return True - if success. False - if failed.
     * @throws PermissionException
     * @throws ShopException
     * @throws RoleException
     */
    public void AppointOwner(String username, String newOwnerUserName)
            throws ShopException, PermissionException, RoleException {
        logger.log(Level.INFO,
                "Shop - AppointOwner: " + username + " trying to appoint " + newOwnerUserName + " as a new owner.");
        if (!checkAtLeastOnePermission(username, EnumSet.of(Permission.FOUNDER, Permission.OWNER))) {
            logger.log(Level.SEVERE, "Shop - AppointOwner: user " + username
                    + " doesn't have permission to add new owner to shop with id " + _shopId);
            throw new PermissionException(
                    "User " + username + " doesn't have permission to add new owner to shop with id " + _shopId);
        }
        if (checkIfHasRole(newOwnerUserName)) {
            logger.log(Level.SEVERE, "Shop - AppointOwner: user " + username + " already in shop with id " + _shopId);
            throw new ShopException("User " + username + " already in shop with id " + _shopId);
        }
        // All constraints checked
        Role owner = new Role(newOwnerUserName, _shopId, username, EnumSet.of(Permission.OWNER));
        _userToRole.putIfAbsent(newOwnerUserName, owner);
        logger.log(Level.INFO, "Shop - AppointOwner: " + username + " successfully appointed " + newOwnerUserName
                + " as a new owner in the shop with id " + _shopId);
    }

    /**
     * Add new permissions to a manager in the shop.
     * 
     * @param username    the username that wants to add the permissions.
     * @param userRole    the username to add the permissions to.
     * @param permissions the set of permissions to add.
     * @implNote if some of the permissions already exist, they are ignored.
     * @throws ShopException
     * @throws PermissionException
     * @throws RoleException
     */
    public void addPermissions(String username, String userRole, Set<Permission> permissions)
            throws ShopException, PermissionException, RoleException {
        logger.log(Level.INFO, "Shop - addPermissions: " + username + " trying to add permissions " + permissions
                + " to user " + userRole + " in the shop with id " + _shopId);
        if (!checkIfHasRole(username)) {
            logger.log(Level.SEVERE,
                    "Shop - addPermissions: user " + username + " doesn't have a role in shop with id " + _shopId);
            throw new ShopException("User " + username + " doesn't have a role in this shop with id " + _shopId);
        }
        if (!checkIfHasRole(userRole)) {
            logger.log(Level.SEVERE,
                    "Shop - addPermissions: user " + userRole + " doesn't have a role in shop with id " + _shopId);
            throw new ShopException("User " + userRole + " doesn't have a role in this shop with id " + _shopId);
        }
        if (!checkAtLeastOnePermission(username,
                EnumSet.of(Permission.FOUNDER, Permission.OWNER, Permission.ADD_PERMISSION))) {
            logger.log(Level.SEVERE, "Shop - addPermissions: user " + username
                    + " doesn't have permission to add permissions to other roles in shop with id " + _shopId);
            throw new PermissionException("User " + username
                    + " doesn't have permission to change permissions in the shop with id " + _shopId);
        }
        Role appointer = _userToRole.get(username);
        // Here we make sure that a manager doesn't give permissions that he doesn't
        // have to his assignee.
        if (!isOwnerOrFounder(appointer)) {
            permissions.retainAll(appointer.getPermissions());
        }
        Role manager = _userToRole.get(userRole);
        if (manager.getAppointedBy() != username) {
            logger.log(Level.SEVERE, "Shop - addPermissions: User " + username + " didn't appoint manager " + userRole
                    + ". Can't change his permissions.");
            throw new PermissionException(
                    "User " + username + " didn't appoint manager " + userRole + ". Can't change his permissions.");
        }
        // All constraints checked
        manager.addPermissions(username, permissions);
        logger.log(Level.INFO, "Shop - addPermissions: " + username + " successfuly added permissions " + permissions
                + " to user " + userRole + " in the shop with id " + _shopId);
    }

    /**
     * Delete permissions from manager in the shop.
     * 
     * @param username    the username that wants to delete the permissions.
     * @param userRole    the username to delete the permissions from.
     * @param permissions the set of permissions to add.
     * @implNote if some of the permissions already exist, they are ignored.
     * @throws ShopException
     * @throws PermissionException
     * @throws RoleException
     */
    public void deletePermissions(String username, String userRole, Set<Permission> permissions)
            throws ShopException, PermissionException, RoleException {
        logger.log(Level.INFO, "Shop - deletePermissions: " + username + " trying to delete permissions " + permissions
                + " from user " + userRole + " in the shop with id " + _shopId);
        if (!checkIfHasRole(username)) {
            logger.log(Level.SEVERE,
                    "Shop - deletePermissions: user " + username + " doesn't have a role in shop with id " + _shopId);
            throw new ShopException("User " + username + " doesn't have a role in this shop with id " + _shopId);
        }
        if (!checkIfHasRole(userRole)) {
            logger.log(Level.SEVERE,
                    "Shop - deletePermissions: user " + userRole + " doesn't have a role in shop with id " + _shopId);
            throw new ShopException("User " + userRole + " doesn't have a role in this shop with id " + _shopId);
        }
        if (!checkAtLeastOnePermission(username,
                EnumSet.of(Permission.FOUNDER, Permission.OWNER, Permission.REMOVE_PERMISSION))) {
            logger.log(Level.SEVERE, "Shop - deletePermissions: user " + username
                    + " doesn't have permission to delete permissions to other roles in shop with id " + _shopId);
            throw new PermissionException("User " + username
                    + " doesn't have permission to change permissions in the shop with id " + _shopId);
        }
        Role manager = _userToRole.get(userRole);
        if (manager.getAppointedBy() != username) {
            logger.log(Level.SEVERE, "Shop - deletePermissions: User " + username + " didn't appoint manager "
                    + userRole + ". Can't change his permissions.");
            throw new PermissionException(
                    "User " + username + " didn't appoint manager " + userRole + ". Can't change his permissions.");
        }
        // All constraints checked
        manager.deletePermissions(username, permissions);
        if (manager.getPermissions().isEmpty()) {
            // TODO: Maybe he is fired? Can ask if he is sure he wants to delete him.
        }
        logger.log(Level.INFO, "Shop - deletePermissions: " + username + " successfuly deleted permissions "
                + permissions + " to user " + userRole + " in the shop with id " + _shopId);
    }

    /**
     * Function to fire a manager/owner. All people he assigned fired too.
     * 
     * @param username        the username that initiates the firing.
     * @param managerUserName the username to be fired.
     * @implNote Founder can fire anyone.
     * @throws ShopException
     * @throws PermissionException
     */
    public void fireRole(String username, String managerUserName) throws ShopException, PermissionException {
        logger.log(Level.INFO, "Shop - fireRole: " + username + " trying to fire user " + managerUserName
                + " from the shop with id " + _shopId);
        if (!checkIfHasRole(username)) {
            logger.log(Level.SEVERE,
                    "Shop - fireRole: user " + username + " doesn't have a role in shop with id " + _shopId);
            throw new ShopException("User " + username + " doesn't have a role in this shop with id " + _shopId);
        }
        if (!checkIfHasRole(managerUserName)) {
            logger.log(Level.SEVERE,
                    "Shop - fireRole: user " + managerUserName + " doesn't have a role in shop with id " + _shopId);
            throw new ShopException("User " + managerUserName + " doesn't have a role in this shop with id " + _shopId);
        }
        if (!checkAtLeastOnePermission(username, EnumSet.of(Permission.FOUNDER, Permission.OWNER))) {
            logger.log(Level.SEVERE, "Shop - fireRole: user " + username
                    + " doesn't have permission to fire users from shop with id " + _shopId);
            throw new PermissionException(
                    "User " + username + " doesn't have permission to fire people in the shop with id " + _shopId);
        }
        Role manager = _userToRole.get(managerUserName);
        if (manager.getAppointedBy() != username) {
            logger.log(Level.SEVERE, "Shop - fireRole: User " + username + " didn't appoint manager " + managerUserName
                    + ". Can't fire him.");
            throw new PermissionException(
                    "User " + username + " didn't appoint role " + managerUserName + ". Can't fire him.");
        }
        // All constraints checked
        // TODO: maybe when firing need to add some special logic?
        Set<String> appointed = getAllAppointed(managerUserName);
        for (String user : appointed) {
            _userToRole.remove(user);
        }
        logger.log(Level.INFO, "Shop - fireRole: " + username + " successfuly fired " + managerUserName
                + " and all the users he appointed:" + appointed.remove(username) + "from the shop with id " + _shopId);
    }

    /**
     * Deletes the role from the shop and all the roles he assigned recursivly.
     * 
     * @param username the root user to resign.
     * @throws ShopException
     */
    public void resign(String username) throws ShopException {
        logger.log(Level.INFO, "Shop - resign: " + username + " trying to resign from the shop with id " + _shopId);
        if (!checkIfHasRole(username)) {
            logger.log(Level.SEVERE,
                    "Shop - resign: user " + username + " doesn't have a role in shop with id " + _shopId);
            throw new ShopException("User " + username + " doesn't have a role in this shop with id " + _shopId);
        }
        if (username.equals(_shopFounder)) {
            logger.log(Level.SEVERE, "Shop - resign: user " + username
                    + " is the founder and cannot resign from his shop with id " + _shopId);
            throw new ShopException("Founder cannot resign from his shop.");
        }
        Set<String> appointed = getAllAppointed(username);
        for (String user : appointed) {
            _userToRole.remove(user);
        }
        logger.log(Level.INFO, "Shop - resign: " + username + " successfuly resigned with all the users he appointed:"
                + appointed.remove(username) + "from the shop with id " + _shopId);
    }

    /**
     * Helper function to retrieve all the roles that we assigned from root role.
     * 
     * @param username the root role username.
     * @return a set of all usernames that were appointed from the root username.
     * @throws ShopException
     */
    private Set<String> getAllAppointed(String username) throws ShopException {
        logger.log(Level.FINE, "ShoppingCart - getAllAppointed: Getting all the appointed users by " + username);
        Set<String> appointed = new HashSet<>();
        collectAppointedUsers(username, appointed);
        return appointed;
    }

    /**
     * Helper function to retrieve all the roles that we assigned from root role.
     * 
     * @param username  the current username we collect.
     * @param appointed the collected set of users to some point.
     * @throws ShopException
     */
    private void collectAppointedUsers(String username, Set<String> appointed) throws ShopException {
        if (!checkIfHasRole(username)) {
            logger.log(Level.SEVERE, "Shop - collectAppointedUsers: user " + username
                    + " doesn't have a role in shop with id " + _shopId);
            throw new ShopException("User " + username + " doesn't have a role in this shop with id " + _shopId);
        }
        if (!appointed.add(username)) {
            // If username is already present in appointed, avoid processing it again to
            // prevent infinite recursion.
            return;
        }
        Role role = _userToRole.get(username);
        for (String user : role.getAppointments()) {
            collectAppointedUsers(user, appointed);
        }
    }

    public String getRolesInfo(String username) throws PermissionException, ShopException {
        logger.log(Level.INFO,
                "Shop - getRolesInfo: " + username + " trying get all roles info from the shop with id " + _shopId);
        if (!checkPermission(username, Permission.GET_ROLES_INFO)) {
            logger.log(Level.SEVERE, "Shop - getRolesInfo: user " + username
                    + " doesn't have permission to get roles info in shop with id " + _shopId);
            throw new PermissionException(
                    "User " + username + " doesn't have permission to get roles info in shop with id " + _shopId);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("SHOP " + _shopId + " ROLES:\n");
        for (Map.Entry<String, Role> entry : _userToRole.entrySet()) {
            sb.append(
                    "Username: " + entry.getKey() + " | ROLES:" + entry.getValue().getPermissions().toString() + "\n");
        }
        logger.log(Level.INFO, "Shop - getRolesInfo: " + username
                + " successfuly got all roles info from the shop with id " + _shopId);
        return sb.toString();
    }

    public Double getShopRating() {
        return _shopRating;
    }

    public void addShopRating(Integer rating) {
        //TODO: limit the rating to 1-5 
        Double newRating = Double.valueOf(rating);
        if (_shopRating == -1.0) {
            _shopRating = newRating;
        } else {
            _shopRating = ((_shopRating * _shopRatersCounter) + newRating) / (_shopRatersCounter + 1);
        }
        _shopRatersCounter++;
    }

    /**
     * Add new product to the shop.
     * 
     * @param username the username of the function activator
     * @param product  the new product we want to add
     * @throws ProductAlreadyExistsException
     * @throws ShopException
     * @throws PermissionException
     */
    public void addProductToShop(String username, Product product)
            throws ProductAlreadyExistsException, ShopException, PermissionException {
        logger.log(Level.INFO, "Shop - addProductToShop: " + username + " trying get add product "
                + product.getProductName() + " in the shop with id " + _shopId);
        if (!checkPermission(username, Permission.ADD_PRODUCT)) {
            logger.log(Level.SEVERE, "Shop - addProductToShop: user " + username
                    + " doesn't have permission to add products in shop with id " + _shopId);
            throw new PermissionException(
                    "User " + username + " doesn't have permission to add product in shop with id " + _shopId);
        }

        if (_productMap.containsKey(product.getProductId())) {
            logger.log(Level.SEVERE, "Shop - addProductToShop: Error while trying to add product with id: "
                    + product.getProductId() + " to shop with id " + _shopId);
            throw new ProductAlreadyExistsException("Product with ID " +
                    product.getProductId() + " already exists.");
        }
        _productMap.put(product.getProductId(), product); // Add product to the map
        logger.log(Level.INFO, "Shop - addProductToShop: " + username + " successfully added product "
                + product.getProductName() + " in the shop with id " + _shopId);
    }

    public Integer getShopId() {
        return _shopId;
    }

    public Product getProductById(Integer productId) {
        return _productMap.get(productId); // Get product by ID from the map
    }

    public Map<Integer, Product> getShopProducts() {
        return _productMap;
    }

    public List<ShopOrder> getShopOrderHistory() {
        return _orderHistory;
    }

    public void addDiscount(Discount discount) {
        _discounts.add(discount);
    }

    public void applyDiscounts(ShoppingBasket basket) {
        basket.resetProductToPriceToAmount();
        for (Discount discount : _discounts) {
            discount.applyDiscount(basket);
        }
    }

    public void addOrderToOrderHistory(ShopOrder order) {
        _orderHistory.add(order); // Add order to the history
    }

    @Override
    public String toString() {
        return "Shop{" +
                "Shop ID=" + _shopId +
                ", Shop Founder=" + _shopFounder +
                ", Shop address=" + _shopAddress +
                ", Shop rating=" + _shopRating +
                ", Products= \n" + _productMap +
                ", Order History= \n " + _orderHistory +
                '}';
    }

    public List<Product> getProductsByName(String productName) {
        List<Product> products = new ArrayList<>();
        for (Product product : _productMap.values()) {
            if (product.getProductName().equals(productName)) {
                products.add(product);
            }
        }
        return products;
    }

    public List<Product> getProductsByCategory(Category productCategory) {
        List<Product> products = new ArrayList<>();
        for (Product product : _productMap.values()) {
            if (product.getCategory() == productCategory) {
                products.add(product);
            }
        }
        return products;
    }

    public List<Product> getProductsByKeywords(List<String> keywords) {
        List<Product> products = new ArrayList<>();
        for (Product product : _productMap.values()) 
        {
            if (product.isKeywordListExist(keywords)) 
            {
                products.add(product);
            }
        }
        return products;
    }
    

    public List<Product> getProductsByPriceRange(Double minPrice, Double maxPrice) {
        List<Product> products = new ArrayList<>();
        for (Product product : _productMap.values()) 
        {
            if (product.isPriceInRange(minPrice, maxPrice)) 
            {
                products.add(product);
            }
        }
        return products;
    }
        

    public List<ShopOrder> getPurchaseHistory() {
        return this._orderHistory;
    }

    public Boolean isOwnerOrFounderOwner(String userId) throws ShopException {
        Role role = getRole(userId);
        return isOwnerOrFounder(role);
    }

    // before removing the shop send notificstion to all relevasnt users
    public void notifyRemoveShop() {
        for (Map.Entry<String, Role> entry : _userToRole.entrySet()) {
            String userName = entry.getKey();
            // TODO: StoreClosedAlert();
        }
    }

    public String getBankDetails() {
        return _bankDetails;
    }

    public String getShopAddress() {
        return _shopAddress;
    }

    public Double addProductRating(Integer productId, Integer rating) {
        //TODO: limit the rating to 1-5
        Product product = _productMap.get(productId);
        product.addProductRating(rating);
        return product.getProductRating();
    }


}
