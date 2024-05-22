package Domain;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Exceptions.*;


public class Shop {
    private Integer _shopId;
    private String _shopFounder; // Shop founder username
    private Map<Integer, Product> _productMap; // <ProductId, Product>
    private List<ShoppingBasket> _orderHistory;
    private Map<String, Role> _userToRole; //<userName, Role>
    // TODO:: List<Discount>

    // Constructor
    public Shop(Integer shopId, String shopFounderUserName) throws ShopException {
        try{
            _shopId = shopId;
            _shopFounder = shopFounderUserName;
            _productMap = new HashMap<>(); // Initialize the product map
            _orderHistory = new ArrayList<>();
            _userToRole = new HashMap<>();
            Role founder = new Role(shopFounderUserName, shopId, null, EnumSet.of(Permission.FOUNDER));
            _userToRole.putIfAbsent(shopFounderUserName, founder);
        }catch(Exception e){
            throw new ShopException("Error while creating shop.");
        }
    }

    public boolean checkIfHasRole(String username) throws ShopException{
        if(username == null){
            return false;
        }
        return _userToRole.containsKey(username);  
    }

    /**
     * Check if a user has a specific permission to do an action.
     * @param username the username of the user that does the action.
     * @param p the permission needed.
     * @return true if has permission. false if hasn't.
     * @throws ShopException
     */
    public boolean checkPermission(String username, Permission p) throws ShopException{
        if(!checkIfHasRole(username)){
            throw new ShopException("User "+username+ " doesn't have a role in this shop with id "+_shopId);
        }
        Role role = _userToRole.get(username);
        if(!isOwnerOrFounder(role) && !role.hasPermission(p)){
            return false;
        }
        return true;
    }

    /**
     * Check if a user has a at least one permission of the given set.
     * @param username the username of the user that does the action.
     * @param permissions the permissions set.
     * @return true if has permission. false if hasn't.
     * @throws ShopException
     */
    public boolean checkAtLeastOnePermission(String username, Set<Permission> permissions) throws ShopException{
        if(!checkIfHasRole(username)){
            throw new ShopException("User "+username+ " doesn't have a role in this shop with id "+_shopId);
        }
        Role role = _userToRole.get(username);
        if(!isOwnerOrFounder(role) && !role.hasAtLeastOnePermission(permissions)){
            return false;
        }
        return true;
    }

    /**
     * Check if a user has all permissions of the given set.
     * @param username the username of the user that does the action.
     * @param permissions the permissions needed.
     * @return true if has permission. false if hasn't.
     * @throws ShopException
     */
    public boolean checkAllPermission(String username, Set<Permission> permissions) throws ShopException{
        if(!checkIfHasRole(username)){
            throw new ShopException("User "+username+ " doesn't have a role in this shop with id "+_shopId);
        }
        Role role = _userToRole.get(username);
        if(!isOwnerOrFounder(role) && !role.hasAllPermissions(permissions)){
            return false;
        }
        return true;
    }

    public boolean isOwnerOrFounder(Role role){
        return role.isFounder() || role.isOwner();
    }

    /**
     * 
     * @param username the user that does the appointment.
     * @param newManagerUserName the user that is appointed.
     * @param permissions the permissions we give this new manager.
     * @return True - if success. False - if failed.
     * @throws PermissionException 
     * @throws ShopException 
     * @throws RoleException 
     */
    public void AppointManager(String username, String newManagerUserName, Set<Permission> permissions) throws ShopException, PermissionException, RoleException{
        if(!checkAtLeastOnePermission(username, EnumSet.of(Permission.FOUNDER, Permission.OWNER, Permission.APPOINT_MANAGER))){
            throw new PermissionException("User "+username+ " doesn't have permission to add new manager to shop with id "+_shopId);
        }
        if(checkIfHasRole(newManagerUserName)){
            throw new ShopException("User "+username+ " already in shop with id "+_shopId);
        }
        if(permissions.isEmpty()){
            throw new PermissionException("Cannot create a manager with 0 permissions.");
        }
        if(permissions.contains(Permission.OWNER) || permissions.contains(Permission.FOUNDER)){
            throw new PermissionException("Cannot appoint manager with owner or founder permissions.");
        }
        //All constraints checked
        Role appointer = _userToRole.get(username);
        //Here we make sure that a manager doesn't give permissions that he doesn't have to his assignee.
        if(!isOwnerOrFounder(appointer)){
            permissions.retainAll(appointer.getPermissions());
        }
        Role manager = new Role(newManagerUserName, _shopId, username, permissions);
       
        _userToRole.putIfAbsent(newManagerUserName, manager);
    }

    /**
     * Appoint new owner
     * @param username the user that does the appointment.
     * @param newManagerUserName the user that is appointed.
     * @return True - if success. False - if failed.
     * @throws PermissionException 
     * @throws ShopException 
     * @throws RoleException 
     */
    public void AppointOwner(String username, String newOwnerUserName) throws ShopException, PermissionException, RoleException{
        if(!checkAtLeastOnePermission(username, EnumSet.of(Permission.FOUNDER, Permission.OWNER))){
            throw new PermissionException("User "+username+ " doesn't have permission to add new owner to shop with id "+_shopId);
        }
        if(checkIfHasRole(newOwnerUserName)){
            throw new ShopException("User "+username+ " already in shop with id "+_shopId);
        }
        //All constraints checked
        Role owner = new Role(newOwnerUserName, _shopId, username, EnumSet.of(Permission.OWNER));
        _userToRole.putIfAbsent(newOwnerUserName, owner);
    }

    /**
     * Add new permissions to a manager in the shop.
     * @param username the username that wants to add the permissions.
     * @param userRole the username to add the permissions to.
     * @param permissions the set of permissions to add.
     * @implNote if some of the permissions already exist, they are ignored.
     * @throws ShopException
     * @throws PermissionException
     * @throws RoleException
     */
    public void addPermissions(String username, String userRole, Set<Permission> permissions) throws ShopException, PermissionException, RoleException{
        if(!checkIfHasRole(username)){
            throw new ShopException("User "+username+ " doesn't have a role in this shop with id "+_shopId);
        }
        if(!checkIfHasRole(userRole)){
            throw new ShopException("User "+userRole+ " doesn't have a role in this shop with id "+_shopId);
        }
        if(!checkAtLeastOnePermission(username, EnumSet.of(Permission.FOUNDER, Permission.OWNER, Permission.ADD_PERMISSION))){
            throw new PermissionException("User "+username+ " doesn't have permission to change permissions in the shop with id "+_shopId);
        }
        Role appointer = _userToRole.get(username);
        //Here we make sure that a manager doesn't give permissions that he doesn't have to his assignee.
        if(!isOwnerOrFounder(appointer)){
            permissions.retainAll(appointer.getPermissions());
        }
        Role manager = _userToRole.get(userRole);
        if(manager.getAppointedBy() != username){
            throw new PermissionException("User "+username+ " didn't appoint manager "+userRole+". Can't change his permissions.");
        }
        //All constraints checked
        manager.addPermissions(username, permissions);
    }

    /**
     * Delete permissions from manager in the shop.
     * @param username the username that wants to delete the permissions.
     * @param userRole the username to delete the permissions from.
     * @param permissions the set of permissions to add.
     * @implNote if some of the permissions already exist, they are ignored.
     * @throws ShopException
     * @throws PermissionException
     * @throws RoleException
     */
    public void deletePermissions(String username, String userRole, Set<Permission> permissions) throws ShopException, PermissionException, RoleException{
        if(!checkIfHasRole(username)){
            throw new ShopException("User "+username+ " doesn't have a role in this shop with id "+_shopId);
        }
        if(!checkIfHasRole(userRole)){
            throw new ShopException("User "+userRole+ " doesn't have a role in this shop with id "+_shopId);
        }
        if(!checkAtLeastOnePermission(username, EnumSet.of(Permission.FOUNDER, Permission.OWNER, Permission.REMOVE_PERMISSION))){
            throw new PermissionException("User "+username+ " doesn't have permission to change permissions in the shop with id "+_shopId);
        }
        Role manager = _userToRole.get(userRole);
        if(manager.getAppointedBy() != username){
            throw new PermissionException("User "+username+ " didn't appoint manager "+userRole+". Can't change his permissions.");
        }
        //All constraints checked
        manager.deletePermissions(username, permissions);
        if(manager.getPermissions().isEmpty()){
            //TODO: Maybe he is fired? Can ask if he is sure he wants to delete him.
        }
    }

    /**
     * Function to fire a manager/owner. All people he assigned fired too.
     * @param username the username that initiates the firing.
     * @param managerUserName the username to be fired.
     * @implNote Founder can fire anyone.
     * @throws ShopException
     * @throws PermissionException
     */
    public void fireRole(String username, String managerUserName) throws ShopException, PermissionException{
        if(!checkIfHasRole(username)){
            throw new ShopException("User "+username+ " doesn't have a role in this shop with id "+_shopId);
        }
        if(!checkIfHasRole(managerUserName)){
            throw new ShopException("User "+managerUserName+ " doesn't have a role in this shop with id "+_shopId);
        }
        if(!checkAtLeastOnePermission(username, EnumSet.of(Permission.FOUNDER, Permission.OWNER))){
            throw new PermissionException("User "+username+ " doesn't have permission to fire people in the shop with id "+_shopId);
        }
        Role manager = _userToRole.get(managerUserName);
        if(manager.getAppointedBy() != username){
            throw new PermissionException("User "+username+ " didn't appoint role "+managerUserName+". Can't fire him.");
        }
        //All constraints checked
        //TODO: maybe when firing need to add some special logic?
        Set<String> appointed = getAllAppointed(managerUserName);
        for(String user: appointed){
            _userToRole.remove(user);
        }
    }

    /**
     * Deletes the role from the shop and all the roles he assigned recursivly.
     * @param username the root user to resign.
     * @throws ShopException
     */
    public void resign(String username) throws ShopException{
        if(!checkIfHasRole(username)){
            throw new ShopException("User "+username+ " doesn't have a role in this shop with id "+_shopId);
        }
        if(username.equals(_shopFounder)){
            throw new ShopException("Founder cannot resign from his shop.");
        }
        Set<String> appointed = getAllAppointed(username);
        for(String user: appointed){
            _userToRole.remove(user);
        }
    }


    /**
     * Helper function to retrieve all the roles that we assigned from root role.
     * @param username the root role username.
     * @return a set of all usernames that were appointed from the root username.
     * @throws ShopException
     */
    private Set<String> getAllAppointed(String username) throws ShopException {
        Set<String> appointed = new HashSet<>();
        collectAppointedUsers(username, appointed);
        return appointed;
    }
    
    /**
     * Helper function to retrieve all the roles that we assigned from root role.
     * @param username the current username we collect.
     * @param appointed the collected set of users to some point.
     * @throws ShopException
     */
    private void collectAppointedUsers(String username, Set<String> appointed) throws ShopException {
        if (!checkIfHasRole(username)) {
            throw new ShopException("User " + username + " doesn't have a role in this shop with id " + _shopId);
        }
        if (!appointed.add(username)) {
            // If username is already present in appointed, avoid processing it again to prevent infinite recursion.
            return;
        }
        Role role = _userToRole.get(username);
        for (String user : role.getAppointments()) {
            collectAppointedUsers(user, appointed);
        }
    }

    public String getRolesInfo(String username) throws PermissionException, ShopException{
        if(!checkPermission(username, Permission.GET_ROLES_INFO)){
            throw new PermissionException("User "+username+ " doesn't have permission to get roles info in shop with id "+_shopId);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("SHOP "+_shopId+" ROLES:\n");
        for (Map.Entry<String,Role> entry : _userToRole.entrySet()){
            sb.append("Username: "+entry.getKey()+" | ROLES:"+entry.getValue().getPermissions().toString()+"\n");
        }
        return sb.toString();
    }


    /**
     * Add new product to the shop.
     * @param username the username of the function activator
     * @param product the new product we want to add
     * @throws ProductAlreadyExistsException
     * @throws ShopException 
     * @throws PermissionException 
     */
    public void addProductToShop(String username, Product product) throws ProductAlreadyExistsException, ShopException, PermissionException {
        if(!checkPermission(username, Permission.ADD_PRODUCT)){
            throw new PermissionException("User "+username+ " doesn't have permission to add product in shop with id "+_shopId);
        }
        
        if (_productMap.containsKey(product.getProductId())) {
            throw new ProductAlreadyExistsException("Product with ID " +
            product.getProductId() + " already exists.");
        }
        _productMap.put(product.getProductId(), product); // Add product to the map
    }

    public Integer getShopId() { return _shopId;}

    public Product getProductById(Integer productId) {
        return _productMap.get(productId); // Get product by ID from the map
    }

    public Map<Integer, Product> getShopProducts() {
        return _productMap;
    }

    public List<ShoppingBasket> getShopOrderHistory() {
        return _orderHistory;
    }

    public void addOrderToOrderHistory(ShoppingBasket order) {
        _orderHistory.add(order); // Add order to the history
    }

    @Override
    public String toString() {
        return "Shop{" +
                "Shop ID=" + _shopId +
                ", Shop Founder=" + _shopFounder +
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
        
}
