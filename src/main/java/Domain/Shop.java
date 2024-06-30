package Domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import Domain.Alerts.Alert;
import Domain.Alerts.CloseShopAlert;
import Domain.Alerts.CredentialsModifyAlert;
import Domain.Alerts.PurchaseFromShopAlert;
import Domain.Alerts.ReOpenShopAlert;
import Domain.Discounts.Discount;
import Domain.Policies.ShopPolicy;
import Domain.Rules.Rule;
import Domain.Rules.RuleFactory;
import Dtos.DiscountDto;
import Dtos.ShopDto;
import Dtos.ShoppingBasketRuleDto;
import Exceptions.DiscountExpiredException;
import Exceptions.PermissionException;
import Exceptions.ProdcutPolicyException;
import Exceptions.ProductAlreadyExistsException;
import Exceptions.ProductDoesNotExistsException;
import Exceptions.RoleException;
import Exceptions.ShopException;
import Exceptions.ShopPolicyException;
import Exceptions.StockMarketException;
import Server.notifications.NotificationHandler;
import enums.Category;
import enums.Permission;

///

//TODO: ADD ALERT SYSTEM WHEN APPOINTING MANAGER/OWNER

public class Shop {
    private int _shopId;
    private String _shopName;
    private String _shopFounder; // Shop founder username
    private Map<Integer, Product> _productMap; // <ProductId, Product>
    private List<ShopOrder> _orderHistory;
    private Map<String, Role> _userToRole; // <userName, Role>
    private static final Logger logger = Logger.getLogger(Shop.class.getName());
    private Map<Integer, Discount> _discounts;
    private String _bankDetails;
    private String _shopAddress;
    private Double _shopRating;
    private Integer _shopRatersCounter;
    private ShopPolicy _shopPolicy;
    private int _nextDiscountId;
    private boolean _isClosed;
    private NotificationHandler _notificationHandler;
    

    // Constructor
    public Shop(Integer shopId, String shopName, String shopFounderUserName, String bankDetails, String shopAddress)
            throws ShopException {
        try {
            logger.log(Level.INFO, "Shop - constructor: Creating a new shop with id " + shopId
                    + " named " + shopName + ". The Founder of the shop is: " + shopFounderUserName);
            _shopId = shopId;
            _shopName = shopName;
            _shopFounder = shopFounderUserName;
            _productMap = new HashMap<>(); // Initialize the product map
            _orderHistory = new ArrayList<>();
            _userToRole = new HashMap<>();
            _bankDetails = bankDetails;
            _shopAddress = shopAddress;
            _discounts = new HashMap<>();
            this._shopRating = -1.0;
            this._shopRatersCounter = 0;
            _shopPolicy = new ShopPolicy();
            Role founder = new Role(shopFounderUserName, shopId, null, EnumSet.of(Permission.FOUNDER));
            _userToRole.putIfAbsent(shopFounderUserName, founder);
            _nextDiscountId = 0;
            _isClosed = false;
            _notificationHandler = NotificationHandler.getInstance();

            
            logger.log(Level.FINE, "Shop - constructor: Successfully created a new shop with id " + shopId
                    + ". The Founder of the shop is: " + shopFounderUserName);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Shop - constructor: Error while creating a new shop with id " + shopId
                    + ". The Founder of the shop is: " + shopFounderUserName);
            throw new ShopException("Error while creating shop.");
        }
    }

    public Shop(int shopId, String shopName, String founderUsername, ShopDto shopDto) throws StockMarketException {
        this(shopId, shopName, founderUsername, shopDto.bankDetails, shopDto.shopAddress);
    }

    public void closeShop() {
        _isClosed = true;
    }

    public boolean isShopClosed() {
        return _isClosed;
    }


    public void setProductPrice(int productId, double price) {
        _productMap.get(productId).setPrice(price);
    }

    public void reopenShop() {
        _isClosed = false;
    }

    public Map<Integer, Product> getAllProducts() {
        return _productMap;
    }

    /**
     * Check if a username has a role in shop.
     * 
     * @param username the username to check.
     * @return True - if has role. False - if doesn't have.
     * @throws StockMarketException
     */
    public boolean checkIfHasRole(String username) throws StockMarketException {
        logger.log(Level.FINE,
                "Shop - checkIfHasRole: Checking if user " + username + " has a role in shop with id: " + _shopId);
        if (username == null) {
            return false;
        }
        return _userToRole.containsKey(username);
    }

    // get role of the user in the shop
    public Role getRole(String username) throws StockMarketException {
        if (!checkIfHasRole(username)) {
            throw new ShopException("User " + username + " doesn't have a role in this shop with id " + _shopId);
        }
        return _userToRole.get(username);
    }

    /**
     * Get all the roles in the shop.
     * @param username the username of the user that does the action.
     * @return a map of all the roles in the shop.
     * @throws StockMarketException
     */
    public Map<String, Role> getUserToRoleMap(String username) throws StockMarketException {
        logger.log(Level.INFO,
                "Shop - getUserToRoleMap: " + username + " trying get all roles info from the shop with id " + _shopId);
        if (!checkPermission(username, Permission.GET_ROLES_INFO)) {
            logger.log(Level.SEVERE, "Shop - getUserToRoleMap: user " + username
                    + " doesn't have permission to get roles info in shop with id " + _shopId);
            throw new PermissionException(
                    "User " + username + " doesn't have permission to get roles info in shop with id " + _shopId);
        }
        logger.log(Level.INFO, "Shop - getUserToRoleMap: " + username
                + " successfuly got all roles info from the shop with id " + _shopId);
        return _userToRole;
    }

    /**
     * Check if a user has a specific permission to do an action.
     * 
     * @param username the username of the user that does the action.
     * @param p        the permission needed.
     * @return true if has permission. false if hasn't.
     * @throws StockMarketException if the user doesn't have a role in the shop.
     */
    public boolean checkPermission(String username, Permission p) throws StockMarketException {
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

    public Map<Integer, Discount> getDiscountsOfProduct(Integer productId) throws StockMarketException {
        // check if the product exists
        if (!_productMap.containsKey(productId)) {
            logger.log(Level.SEVERE,
                    "Shop - getDiscountsOfProduct: Error while trying to get discounts of product with id: "
                            + productId + " from shop with id " + _shopId);
            throw new ProductDoesNotExistsException("Product with ID " + productId + " does not exist.");
        }

        Map<Integer, Discount> productDiscounts = new HashMap<>();
        for (Map.Entry<Integer, Discount> entry : _discounts.entrySet()) {
            if (new Date().after(entry.getValue().getExpirationDate())) {
                removeDiscount(entry.getKey());
            } else{
                int participating_product_id = entry.getValue().getParticipatingProduct();
                Product product = _productMap.get(productId);
                if (productId == participating_product_id || (participating_product_id == -1 && entry.getValue().specialPredicate(product))) {
                    productDiscounts.put(entry.getKey(), entry.getValue());
                }
            } 
        }
        return productDiscounts;
    }

    /**
     * Check if a user has a at least one permission of the given set.
     * 
     * @param username    the username of the user that does the action.
     * @param permissions the permissions set.
     * @return true if has permission. false if hasn't.
     * @throws StockMarketException
     */
    public boolean checkAtLeastOnePermission(String username, Set<Permission> permissions) throws StockMarketException {
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
     * @throws StockMarketException
     */
    public boolean checkAllPermission(String username, Set<Permission> permissions) throws StockMarketException {
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
            throws StockMarketException {
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

        if (isShopClosed())
            throw new StockMarketException("Shop is closed, cannot appoint new manager.");
        // All constraints checked
        Role appointer = _userToRole.get(username);
        // Here we make sure that a manager doesn't give permissions that he doesn't
        // have to his assignee.
        if (!isOwnerOrFounder(appointer)) {
            permissions.retainAll(appointer.getPermissions());
        }
        Role manager = new Role(newManagerUserName, _shopId, username, permissions);

        _userToRole.putIfAbsent(newManagerUserName, manager);
        appointer.addAppointment(newManagerUserName);
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
            throws ShopException, PermissionException, RoleException, StockMarketException {
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

        if (isShopClosed())
            throw new StockMarketException("Shop is closed, cannot appoint new owner.");
        // All constraints checked
        Role appointer = _userToRole.get(username);
        Role owner = new Role(newOwnerUserName, _shopId, username, EnumSet.of(Permission.OWNER));
        _userToRole.putIfAbsent(newOwnerUserName, owner);
        appointer.addAppointment(newOwnerUserName);
        logger.log(Level.INFO, "Shop - AppointOwner: " + username + " successfully appointed " + newOwnerUserName
                + " as a new owner in the shop with id " + _shopId);
    }

    /**
     * Modify permissions of a manager in the shop.
     * 
     * @param username    the username that wants to add the permissions.
     * @param userRole    the username to add the permissions to.
     * @param permissions the set of new permissions.
     * @throws StockMarketException
     * @implNote cannot grant permissions that the appointer doesn't have.
     * @Constraint at least one permission in the set.
     */
    public void modifyPermissions(String username, String userRole, Set<Permission> permissions)
            throws StockMarketException {
        logger.log(Level.INFO, "Shop - modifyPermissions: " + username + " trying to add permissions " + permissions
                + " to user " + userRole + " in the shop with id " + _shopId);
        if (isShopClosed())
            throw new StockMarketException("Shop is closed, cannot add permissions.");
        if (!checkIfHasRole(username)) {
            logger.log(Level.SEVERE,
                    "Shop - modifyPermissions: user " + username + " doesn't have a role in shop with id " + _shopId);
            throw new ShopException("User " + username + " doesn't have a role in this shop with id " + _shopId);
        }
        if (!checkIfHasRole(userRole)) {
            logger.log(Level.SEVERE,
                    "Shop - modifyPermissions: user " + userRole + " doesn't have a role in shop with id " + _shopId);
            throw new ShopException("User " + userRole + " doesn't have a role in this shop with id " + _shopId);
        }
        if (!checkAtLeastOnePermission(username,
                EnumSet.of(Permission.FOUNDER, Permission.OWNER, Permission.CHANGE_PERMISSION))) {
            logger.log(Level.SEVERE, "Shop - modifyPermissions: user " + username
                    + " doesn't have permission to modify permissions to other roles in shop with id " + _shopId);
            throw new PermissionException("User " + username
                    + " doesn't have permission to change permissions in the shop with id " + _shopId);
        }
        if (permissions.isEmpty()) {
            logger.log(Level.SEVERE, "Shop - modifyPermissions: user " + username
                    + " cannot remove all permission from " + userRole + " in shop with id " + _shopId);
            throw new PermissionException("User " + username +
                    " cannot remove all permission from " + userRole + " in shop with id " + _shopId);
        }
        Role appointer = _userToRole.get(username);
        // Here we make sure that a manager doesn't give permissions that he doesn't
        // have to his assignee.
        if (!isOwnerOrFounder(appointer)) {
            permissions.retainAll(appointer.getPermissions());
        }
        Role manager = _userToRole.get(userRole);
        if (!manager.getAppointedBy().equals(username)) {
            logger.log(Level.SEVERE,
                    "Shop - modifyPermissions: User " + username + " didn't appoint manager " + userRole
                            + ". Can't change his permissions.");
            throw new PermissionException(
                    "User " + username + " didn't appoint manager " + userRole + ". Can't change his permissions.");
        }
        // All constraints checked
        manager.modifyPermissions(username, permissions);
        notifyModifiedPermissions(username, userRole, permissions,getShopId());
        logger.log(Level.INFO,
                "Shop - modifyPermissions: " + username + " successfuly modified permissions. Now the permission are: "
                        + permissions
                        + " to user " + userRole + " in the shop with id " + _shopId);
    }


    /**
     * Function to fire a manager/owner. All people he assigned fired too.
     * 
     * @param username        the username that initiates the firing.
     * @param managerUserName the username to be fired.
     * @throws StockMarketException
     * @implNote Founder can fire anyone.
     */
    public Set<String> fireRole(String username, String managerUserName) throws StockMarketException {
        logger.log(Level.INFO, "Shop - fireRole: " + username + " trying to fire user " + managerUserName
                + " from the shop with id " + _shopId);
        if (isShopClosed())
            throw new StockMarketException("Shop is closed, cannot fire roles.");
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
        if (!manager.getAppointedBy().equals(username)) {
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
        return appointed;
    }

    /**
     * Deletes the role from the shop and all the roles he assigned recursivly.
     * 
     * @param username the root user to resign.
     * @throws StockMarketException
     */
    public Set<String> resign(String username) throws StockMarketException {
        logger.log(Level.INFO, "Shop - resign: " + username + " trying to resign from the shop with id " + _shopId);
        if (isShopClosed())
            throw new StockMarketException("Shop is closed, cannot resign.");
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
        return appointed;
    }

    /**
     * Helper function to retrieve all the roles that we assigned from root role.
     * 
     * @param username the root role username.
     * @return a set of all usernames that were appointed from the root username.
     * @throws StockMarketException
     */
    private Set<String> getAllAppointed(String username) throws StockMarketException {
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
     * @throws StockMarketException
     */
    private void collectAppointedUsers(String username, Set<String> appointed) throws StockMarketException {
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

    public String getRolesInfo(String username) throws StockMarketException {
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

    public void addShopRating(Integer rating) throws StockMarketException {
        // limit the rating to 1-5
        if (rating < 1 || rating > 5) {
            throw new StockMarketException("Rating must be between 1-5.");
        }
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
     * @throws StockMarketException
     */
    public void addProductToShop(String username, Product product) throws StockMarketException {
        // print logs to inform about the action
        logger.log(Level.INFO, "Shop - addProductToShop: " + username + " trying get add product "
                + product.getProductName() + " in the shop with id " + _shopId);

        // check if shop is closed
        if (isShopClosed())
            throw new StockMarketException("Shop is closed, cannot add product.");

        // check if user has permission to add product
        if (!checkPermission(username, Permission.ADD_PRODUCT)) {
            logger.log(Level.SEVERE, "Shop - addProductToShop: user " + username
                    + " doesn't have permission to add products in shop with id " + _shopId);
            throw new PermissionException(
                    "User " + username + " doesn't have permission to add product in shop with id " + _shopId);
        }

        // check if product already exists
        if (_productMap.containsKey(product.getProductId())) {
            logger.log(Level.SEVERE, "Shop - addProductToShop: Error while trying to add product with id: "
                    + product.getProductId() + " to shop with id " + _shopId);
            throw new ProductAlreadyExistsException("Product with ID " +
                    product.getProductId() + " already exists.");
        }

        // All constraints checked - add product to the shop
        _productMap.put(product.getProductId(), product);

        // print logs to inform about the action
        logger.log(Level.INFO, "Shop - addProductToShop: " + username + " successfully added product "
                + product.getProductName() + " in the shop with id " + _shopId);
    }

    /**
     * Remove product from the shop.
     * 
     * @param username     the username of the function activator
     * @param _productName the product name we want to remove
     * @throws StockMarketException
     */
    public synchronized void removeProductFromShop(String userName, String _productName) throws StockMarketException {
        // print logs to inform about the action
        logger.log(Level.INFO, "Shop - removeProductFromShop: " + userName + " trying get remove product "
                + _productName + " in the shop with id " + _shopId);

        // check if shop is closed
        if (isShopClosed())
            throw new StockMarketException("Shop is closed, cannot remove product.");

        // check if user has permission to remove product, or the user is the founder or
        // the owner (dont need specific permission to remove products)
        if (!checkPermission(userName, Permission.DELETE_PRODUCT) && !checkPermission(userName, Permission.FOUNDER)
                && !checkPermission(userName, Permission.OWNER)) {
            logger.log(Level.SEVERE, "Shop - removeProductFromShop: user " + userName
                    + " doesn't have permission to remove products in shop with id " + _shopId);
            throw new PermissionException(
                    "User " + userName + " doesn't have permission to remove product in shop with id " + _shopId);
        }

        // check if product exists
        Product product = null;
        for (Product p : _productMap.values()) {
            if (p.getProductName().equals(_productName)) {
                product = p;
                break;
            }
        }
        if (product == null) {
            logger.log(Level.SEVERE, "Shop - removeProductFromShop: Error while trying to remove product with name: "
                    + _productName + " from shop with id " + _shopId);
            throw new ProductDoesNotExistsException("Product with name " + _productName + " does not exist.");
        }

        // All constraints checked - remove product from the shop
        _productMap.remove(product.getProductId());

        // print logs to inform about the action
        logger.log(Level.INFO, "Shop - removeProductFromShop: " + userName + " successfully removed product "
                + _productName + " in the shop with id " + _shopId);
    }

    /**
     * Edit product from the shop.
     * 
     * @param username           the username of the function activator
     * @param productNameOld     the product name we want to edit
     * @param productNameNew     thenew product name
     * @param productCategoryNew the new product category
     * @param productPriceNew    the new product price
     * @throws StockMarketException
     */
    public synchronized void editProductInShop(String userName, String productNameOld, String productNameNew,
            Category productCategoryNew, double productPriceNew) throws StockMarketException {
        // print logs to inform about the action
        logger.log(Level.INFO, "Shop - editProductInShop: " + userName + " trying get edit product " + productNameOld
                + " in the shop with id " + _shopId);

        // check if shop is closed
        if (isShopClosed())
            throw new StockMarketException("Shop is closed, cannot remove product.");

        // check if user has permission to edit product, or the user is the founder or
        // the owner (dont need specific permission to remove products)
        if (!checkPermission(userName, Permission.EDIT_PRODUCT) && !checkPermission(userName, Permission.FOUNDER)
                && !checkPermission(userName, Permission.OWNER)) {
            logger.log(Level.SEVERE, "Shop - editProductInShop: user " + userName
                    + " doesn't have permission to edit products in shop with id " + _shopId);
            throw new PermissionException(
                    "User " + userName + " doesn't have permission to edit product in shop with id " + _shopId);
        }

        // check if product exists
        Product product = null;
        for (Product p : _productMap.values()) {
            if (p.getProductName().equals(productNameOld)) {
                product = p;
                break;
            }
        }
        if (product == null) {
            logger.log(Level.SEVERE, "Shop - editProductInShop: Error while trying to remove product with name: "
                    + productNameOld + " from shop with id " + _shopId);
            throw new ProductDoesNotExistsException("Product with name " + productNameOld + " does not exist.");
        }

        // All constraints checked - edit product in the shop
        product.setProductName(productNameNew);
        product.setCategory(productCategoryNew);
        product.setPrice(productPriceNew);

        // print logs to inform about the action
        logger.log(Level.INFO, "Shop - removeProductFromShop: " + userName + " successfully edit product "
                + productNameNew + " in the shop with id " + _shopId);
    }

    // Get product by ID
    public Product getProductById(Integer productId) throws ProductDoesNotExistsException {
        // check if product exists
        if (!_productMap.containsKey(productId)) {
            logger.log(Level.SEVERE, "Shop - getProductById: Error while trying to get product with id: " + productId
                    + " from shop with id " + _shopId);
            throw new ProductDoesNotExistsException("Product with ID " + productId + " does not exist.");
        }
        return _productMap.get(productId); // Get product by ID from the map
    }

    /**
     * Adds a discount to the shop.
     * 
     * @param discount the discount to be added
     * @return the ID of the added discount
     */
    public int addDiscount(Discount discount) throws StockMarketException {
        // check if shop is closed
        if (isShopClosed())
            throw new StockMarketException("Shop is closed, cannot add discount.");
        // check if discount is expired
        if (new Date().after(discount.getExpirationDate())) {
            throw new StockMarketException("Discount is expired, cannot add discount.");
        }
        // check if discount already exists
        for (Discount d : _discounts.values()) {
            if (d.equals(discount)) {
                throw new StockMarketException("Discount already exists, cannot add discount.");
            }
        }

        int discountId = _nextDiscountId++;
        _discounts.put(discountId, discount);
        discount.setId(discountId);
        return discountId;
    }

    public void removeDiscount(int discountId) throws StockMarketException {
        // check if shop is closed
        if (isShopClosed())
            throw new StockMarketException("Shop is closed, cannot remove discount.");
        // check if discount exists
        if (!_discounts.containsKey(discountId)) {
            throw new StockMarketException("Discount does not exist, cannot remove discount.");
        }

        _discounts.remove(discountId);
    }

    public void applyDiscounts(ShoppingBasket basket) throws StockMarketException {
        if (isShopClosed())
            throw new StockMarketException("Shop is closed, cannot apply discounts.");
        List<Integer> expiredDiscounts = new ArrayList<>();
        basket.resetProductToPriceToAmount();
        for (int discountId : _discounts.keySet()) {
            Discount discount = _discounts.get(discountId);
            try {
                discount.applyDiscount(basket);
            } catch (DiscountExpiredException e) {
                logger.info("Shop - applyDiscounts: discount: " + discountId + " has expired, removing it.");
                expiredDiscounts.add(discountId);
            }
        }
        for (Integer discountId : expiredDiscounts) {
            _discounts.remove(discountId);
        }
    }

    public void addOrderToOrderHistory(ShopOrder order) throws StockMarketException {
        if (isShopClosed())
            throw new StockMarketException("Shop is closed, cannot add order.");
        _orderHistory.add(order); // Add order to the history
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
        for (Product product : _productMap.values()) {
            if (product.isKeywordListExist(keywords)) {
                products.add(product);
            }
        }
        return products;
    }

    public List<Product> getProductsByPriceRange(Double minPrice, Double maxPrice) {
        List<Product> products = new ArrayList<>();
        for (Product product : _productMap.values()) {
            if (product.isPriceInRange(minPrice, maxPrice)) {
                products.add(product);
            }
        }
        return products;
    }

    public Boolean isOwnerOrFounderOwner(String userId) throws StockMarketException {
        Role role = getRole(userId);
        return isOwnerOrFounder(role);
    }

    public void addProductRating(Integer productId, Integer rating) throws StockMarketException {
        if (!isProductExist(productId))
            throw new StockMarketException(String.format("Product ID: %d doesn't exist.", productId));

        Product product = _productMap.get(productId);
        product.addProductRating(rating);
    }

    public Double getProductRating(Integer productId) {
        Product product = _productMap.get(productId);
        return product.getProductRating();
    }

    public Boolean isProductExist(Integer productId) throws StockMarketException {
        if (!_productMap.containsKey(productId)) {
            logger.log(Level.SEVERE, String.format(
                    "Shop : Error while trying to find product with id: %d in shopId: %d. Product does not exist",
                    productId, _shopId));
            throw new ProductDoesNotExistsException(String.format("Product: %d does not exist", productId));
        }
        return true;
    }

    public Boolean isProductNameExist(String productName) {
        for (Product product : _productMap.values()) {
            if (product.getProductName().equals(productName)) {
                return true;
            }
        }
        return false;
    }

    public void updateProductQuantity(String username, Integer productId, Integer productAmoutn)
            throws StockMarketException {
        try {
            if (!checkPermission(username, Permission.ADD_PRODUCT)) {
                logger.log(Level.SEVERE, String.format(
                        "Shop - updateProductQuantity: Error while trying to update product with id: %d to shopId: %d. User: %s does not have permissions",
                        productId, _shopId, username));
                throw new PermissionException(
                        String.format("User: %s does not have permission to Update product: %d", username, productId));
            }

            if (isShopClosed()) {
                logger.log(Level.SEVERE,
                        String.format("Shop: %d is close, product: %d can't be updated", _shopId, productId));
                throw new ShopException(
                        String.format("Shop: %d is close, product: %d can't be updated", _shopId, productId));
            }

            isProductExist(productId);
            getProductById(productId).updateProductQuantity(productAmoutn);
        } catch (StockMarketException e) {
            throw new StockMarketException(e.getMessage());
        }
    }

    /**
     * Checks if a basket is meeting the shop Policy.
     * 
     * @param sb the basket to check
     * @throws StockMarketException
     */
    public void ValidateBasketMeetsShopPolicy(ShoppingBasket sb) throws StockMarketException {
        logger.log(Level.FINE,
                "Shop - ValidateBasketMeetsShopPolicy: Starting validation of basket for shop with id: " + _shopId);
        if (!_shopPolicy.evaluate(sb)) {
            logger.log(Level.SEVERE,
                    "Shop - ValidateBasketMeetsShopPolicy: Basket violates the shop policy of shop with id: "
                            + _shopId);
            throw new ShopPolicyException("Basket violates the shop policy of shop with id: " + _shopId);
        }
    }

    /**
     * Checks if a user is meeting the product policy.
     * 
     * @param u The user that tries to add the product to basket.
     * @param p The product which policy is being checked.
     * @throws StockMarketException
     */
    public void ValidateProdcutPolicy(User u, Product p) throws StockMarketException {
        logger.log(Level.FINE,
                "Shop - ValidateProdcutPolicy: Starting validation of product in shop with id: " + _shopId);

        // if the user recived is null, means its a guest user in the system, so we need
        // to check if the product policy allows guest users (have any policy)
        if (u == null) {
            if (p.getProductPolicy().getRules().size() > 0) {
                logger.log(Level.SEVERE, "Shop - ValidateProdcutPolicy: the product " + p.getProductName()
                        + " in shop with id: " + _shopId + " doesn't allow guest users");
                throw new ProdcutPolicyException("Guest user violates the shop policy of shop with id: " + _shopId);
            }
            return;
        }

        if (!p.getProductPolicy().evaluate(u)) {
            logger.log(Level.SEVERE, "Shop - ValidateProdcutPolicy: User " + u.getUserName()
                    + " violates the product policy of product " + p.getProductName() + " in shop with id: " + _shopId);
            throw new ProdcutPolicyException(
                    "User " + u.getUserName() + " violates the shop policy of shop with id: " + _shopId);
        }
    }

    /**
     * Adds a rule to the shop policy.
     * 
     * @username The username of the user that tries to add the rule.
     * @param rule The rule to add.
     * @throws StockMarketException
     */
    public void addRuleToShopPolicy(String username, Rule<ShoppingBasket> rule) throws StockMarketException {
        logger.log(Level.INFO, "Shop - addRuleToShopPolicy: User " + username
                + " trying to add rule to shop policy of shop with id: " + _shopId);
        if (checkPermission(username, Permission.CHANGE_SHOP_POLICY))
            _shopPolicy.addRule(rule);
        logger.log(Level.FINE, "Shop - addRuleToShopPolicy: User " + username
                + " successfuly added a rule to shop policy of shop with id: " + _shopId);
    }

    /**
     * Removes a rule from the shop policy.
     * 
     * @username The username of the user that tries to remove the rule.
     * @param rule The rule to remove.
     * @throws StockMarketException
     */
    public void removeRuleFromShopPolicy(String username, Rule<ShoppingBasket> rule) throws StockMarketException {
        logger.log(Level.INFO, "Shop - removeRuleFromShopPolicy: User " + username
                + " trying to remove rule from shop policy of shop with id: " + _shopId);
        if (checkPermission(username, Permission.CHANGE_SHOP_POLICY))
            _shopPolicy.deleteRule(rule);
        logger.log(Level.FINE, "Shop - removeRuleFromShopPolicy: User " + username
                + " successfuly removed a rule from shop policy of shop with id: " + _shopId);
    }

    /**
     * Adds a rule to the product policy of a product.
     * 
     * @param username  The username of the user that tries to add the rule.
     * @param rule      The rule to add.
     * @param productId The id of the product to add the rule to.
     * @throws StockMarketException
     */
    public void addRuleToProductPolicy(String username, Rule<User> rule, int productId) throws StockMarketException {
        logger.log(Level.INFO, "Shop - addRuleToProductPolicy: User " + username
                + " trying to add rule to product policy of shop with id: " + _shopId);
        if (checkPermission(username, Permission.CHANGE_PRODUCT_POLICY)) {
            _productMap.get(productId).getProductPolicy().addRule(rule);
        }
        logger.log(Level.FINE, "Shop - addRuleToProductPolicy: User " + username
                + " successfuly added a rule to product policy of shop with id: " + _shopId);
    }

    /**
     * Removes a rule from the product policy of a product.
     * 
     * @param username  The username of the user that tries to remove the rule.
     * @param rule      The rule to remove.
     * @param productId The id of the product to remove the rule from.
     * @throws StockMarketException
     */
    public void removeRuleFromProductPolicy(String username, Rule<User> rule, int productId)
            throws StockMarketException {
        logger.log(Level.INFO, "Shop - removeRuleFromProductPolicy: User " + username
                + " trying to remove rule from product policy of shop with id: " + _shopId);
        if (checkPermission(username, Permission.CHANGE_PRODUCT_POLICY)) {
            _productMap.get(productId).getProductPolicy().deleteRule(rule);
        }
        logger.log(Level.FINE, "Shop - removeRuleFromProductPolicy: User " + username
                + " successfuly removed a rule from product policy of shop with id: " + _shopId);
    }

    public String getProductPolicyInfo(Integer productId) throws StockMarketException {
        if (isProductExist(productId)) {
            return _productMap.get(productId).getProductPolicyInfo();
        } else {
            return null;
        }
    }

    public String getShopDiscountsInfo() {
        StringBuilder discountsBuilder = new StringBuilder();
        for (Map.Entry<Integer, Discount> entry : _discounts.entrySet()) {
            discountsBuilder.append("Discount ID: ").append(entry.getKey()).append(" | Discount: ")
                    .append(entry.getValue().toString()).append("\n");
        }
        return discountsBuilder.toString();
    }

    public String getProductDiscountsInfo(Integer productId) throws StockMarketException {
        // TODO: implement after getDiscountsByProduct is implemented
        if (isProductExist(productId)) {
            StringBuilder discountsBuilder = new StringBuilder();
            for (Map.Entry<Integer, Discount> entry : getDiscountsOfProduct(productId).entrySet()) {
                discountsBuilder.append("Discount ID: ").append(entry.getKey()).append(" | Discount: ")
                        .append(entry.getValue().toString()).append("\n");
            }
            return discountsBuilder.toString();
        } else {
            return null;
        }
    }

    public String getProductGeneralInfo(Integer productId) throws StockMarketException {
        if (isProductExist(productId)) {
            return _productMap.get(productId).getProductGeneralInfo();
        } else {
            return null;
        }
    }

    /**
     * Notify owner of the shop that a purchase has been made.
     * @param buyingUser the buying user.   
     * @param productIdList the product id list.
     */
    public void notfyPurchaseFromShop(String buyingUser, List<Integer> productIdList) {
        for (Map.Entry<String, Role> entry : _userToRole.entrySet()) {
            String owner = entry.getKey();
            Alert alert = new PurchaseFromShopAlert(owner,buyingUser, productIdList, _shopId);
            _notificationHandler.sendMessage(owner, alert);
        }
    }

    /**
     * Notify the users that the shop has been closed.
     * @param username the user that closed the shop.
     */
    public void notifyCloseShop(String username) {
        for (Map.Entry<String, Role> entry : _userToRole.entrySet()) {
            String owner = entry.getKey();
            Alert alert = new CloseShopAlert(owner, username, _shopId);
            _notificationHandler.sendMessage(owner, alert);
        }
    }

    /**
     * Notify the users that the shop has been re-opened.
     * @param username the  user that re-opened the shop.
     */
    public void notifyReOpenShop(String username) {
        for (Map.Entry<String, Role> entry : _userToRole.entrySet()) {
            String owner = entry.getKey();
            Alert alert = new ReOpenShopAlert(owner, username, _shopId);
            _notificationHandler.sendMessage(owner, alert);
        }
    }
    /**
     * Notify the user that his permissions have been modified.
     * @param fromUser the user that modified the permissions.
     * @param targetUser the user that the permissions have been modified.
     * @param permissions the new permissions.
     * @param shopId the shop id.
     */
    private void notifyModifiedPermissions(String fromUser, String targetUser, Set<Permission> permissions, int shopId) {
        List <String> newPermissions = new ArrayList<String>();
        for (Permission p : permissions) {
            newPermissions.add(p.toString());
        }
        Alert alert = new CredentialsModifyAlert(fromUser, targetUser, newPermissions, shopId);
        _notificationHandler.sendMessage(targetUser, alert);
    }

    // this function adds a new review to the product in the shop
    public void addReview(String username, int productID, String review) {
        Product product = _productMap.get(productID);
        product.addReview(username, review);
    }

    // this function changes the shop policy
    public void changeShopPolicy(String username, List<ShoppingBasketRuleDto> shopRules) throws StockMarketException {
        if (checkPermission(username, Permission.CHANGE_SHOP_POLICY)) {
            _shopPolicy = new ShopPolicy();
            for (ShoppingBasketRuleDto rule : shopRules) {
                Rule<ShoppingBasket> newRule = RuleFactory.createShoppingBasketRule(rule);
                _shopPolicy.addRule(newRule);
            }
        }
    }

      
    public synchronized void addKeywordsToProduct(String userName, Integer productId, List<String> keywords) throws StockMarketException {
        // print logs to inform about the action
        logger.log(Level.INFO, "Shop - addKeywordsToProduct: " + userName + " trying add key words to product " + productId
                + " in the shop with id " + _shopId);

        // check if shop is closed
        if (isShopClosed())
            throw new StockMarketException("Shop is closed, cannot edit product.");

        // check if user has permission to edit product, or the user is the founder or
        // the owner (dont need specific permission to remove products)
        if (!checkPermission(userName, Permission.EDIT_PRODUCT) && !checkPermission(userName, Permission.FOUNDER)
                && !checkPermission(userName, Permission.OWNER)) {
            logger.log(Level.SEVERE, "Shop - addKeywordsToProduct: user " + userName
                    + " doesn't have permission to edit products in shop with id " + _shopId);
            throw new PermissionException(
                    "User " + userName + " doesn't have permission to edit product in shop with id " + _shopId);
        }

        // check if product exists
        if (!_productMap.containsKey(productId)) {
            logger.log(Level.SEVERE, "Shop - addKeywordsToProduct: Error while trying to get product with id: " + productId
                    + " from shop with id " + _shopId);
            throw new ProductDoesNotExistsException("Product with ID " + productId + " does not exist.");
        }
        

        // All constraints checked - edit product in the shop
        Product product = _productMap.get(productId);
        for (String keyword : keywords) {
            product.addKeyword(keyword);
        }

        // print logs to inform about the action
        logger.log(Level.INFO, "Shop - addKeywordsToProduct: " + userName + " successfully added keywords to product "
                + productId + " in the shop with id " + _shopId);
    }
    
    /**
     * Get all the products in the shop.
     */
    public List<Product> getAllProductsList() {
        return new ArrayList<>(_productMap.values());
    }

    public String getShopGeneralInfo() {
        return "Shop ID: " + _shopId + " | Shop Founder: " + _shopFounder + " | Shop Address: " + _shopAddress
                + " | Shop Rating: " + _shopRating;
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

    public int getShopId() {
        return _shopId;
    }

    public String getShopName() {
        return _shopName;
    }

    public void setShopName(String shopName) {
        _shopName = shopName;
    }

    public String getFounderName() {
        return _shopFounder;
    }

    public void setShopFounder(String shopFounder) {
        _shopFounder = shopFounder;
    }

    public Map<Integer, Product> getShopProducts() {
        return _productMap;
    }

    public List<ShopOrder> getPurchaseHistory() {
        return _orderHistory;
    }

    public String getBankDetails() {
        return _bankDetails;
    }

    // return the anoumt of product
    public Integer getAmoutOfProductInShop() {
        return _productMap.size();
    }

    // get all discount in the shop
    public Map<Integer, Discount> getDiscounts() {
        return _discounts;
    }

    // get all discount in the shop in DiscountDto
    public Map<Integer, DiscountDto> getDiscountDtos() {
        Map<Integer, DiscountDto> discountDtos = new HashMap<>();
        for (Map.Entry<Integer, Discount> entry : _discounts.entrySet()) {
            discountDtos.put(entry.getKey(), new DiscountDto(entry.getValue()));
        }
        return discountDtos;
    }

    public void setBankDetails(String bankDetails) {
        _bankDetails = bankDetails;
    }

    public String getShopAddress() {
        return _shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        _shopAddress = shopAddress;
    }

    public Double getShopRating() {
        return _shopRating;
    }

    public Integer getShopRatersCounter() {
        return _shopRatersCounter;
    }
    
    public Integer getNextDiscountId() {
        return _nextDiscountId;
    }

    public String getShopPolicyInfo() {
        return _shopPolicy.toString();
    }

    public ShopPolicy getShopPolicy() {
        return _shopPolicy;
    }

    public void setShopPolicy(ShopPolicy shopPolicy) {
        _shopPolicy = shopPolicy;
    }
}
