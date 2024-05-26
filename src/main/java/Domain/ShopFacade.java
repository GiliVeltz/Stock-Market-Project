package Domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import Exceptions.PermissionException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import Exceptions.ShopException;
import Exceptions.StockMarketException;

import org.springframework.web.bind.annotation.RestController;

import Domain.Discounts.BaseDiscount;
import Domain.Discounts.ConditionalDiscount;
import Domain.Discounts.Discount;
import Domain.Discounts.PrecentageDiscount;

@RestController
public class ShopFacade {
    private static ShopFacade _shopFacade;
    private List<Shop> _shopsList;

    public enum Category {
        GROCERY,
        CLOTHING,
        ELECTRONICS,
        PHARMACY,
        // Add more categories as needed
    }

    private ShopFacade() {
        this._shopsList = new ArrayList<>();
    }

    public ShopFacade(List<Shop> shopsList) { // ForTests
        _shopsList = shopsList;
    }

    // Public method to provide access to the _shopFacade
    public static synchronized ShopFacade getShopFacade() {
        if (_shopFacade == null) {
            _shopFacade = new ShopFacade();
        }
        return _shopFacade;
    }

    public Shop getShopByShopId(Integer shopId) {
        for (Shop shop : this._shopsList) {
            if (shop.getShopId().equals(shopId)) {
                return shop;
            }
        }
        return null;
    }

    /**
     * Checks if a shop ID exists.
     * 
     * @param shopId The ID of the shop to check.
     * @return True if the shop ID exists, false otherwise.
     */
    public Boolean isShopIdExist(Integer shopId) {
        for (Shop shop : this._shopsList) {
            if (shop.getShopId().equals(shopId)) {
                return true;
            }
        }
        return false;
    }

    public void openNewShop(Integer shopId, String userName, String bankDetails, String shopAddress) throws Exception {
        if (isShopIdExist(shopId))
            throw new Exception(String.format("Shop ID: %d is already exist.", shopId));
        else
            _shopsList.add(new Shop(shopId, userName, bankDetails, shopAddress));
    }

    // close shop only if the user is the founder of the shop
    public void closeShop(Integer shopId, String userName) throws Exception {
        try {
            if (!isShopIdExist(shopId))
                throw new Exception(String.format("Shop ID: %d does not exist.", shopId));
            else {
                Shop shopToClose = getShopByShopId(shopId);
                if (shopToClose.checkPermission(userName, Permission.FOUNDER)) {
                    getShopByShopId(shopId).notifyRemoveShop();
                    _shopsList.remove(shopToClose);
                } else {
                    throw new Exception(String.format(
                            "User %s can't cloase the Shop: %d. Only the fonder has the permission", userName, shopId));
                }
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    public void addProductToShop(Integer shopId, Product product, String userName) throws Exception {
        if (!isShopIdExist(shopId))
            throw new Exception(String.format("Shop ID: %d does not exist.", shopId));
        else
            getShopByShopId(shopId).addProductToShop(userName, product);
    }

    /**
     * Retrieves the purchase history for a shop by its ID.
     *
     * @param shopId The ID of the shop.
     * @return A list of ShopOrder objects representing the shop's purchase history.
     */
    public List<ShopOrder> getPurchaseHistory(Integer shopId) {
        List<ShopOrder> purchaseHistory = new ArrayList<>();
        Shop shop = getShopByShopId(shopId);
        if (shop != null) {
            purchaseHistory = shop.getPurchaseHistory();
        }
        return purchaseHistory;
    }

    /**
     * Checks if a user is the owner of a shop.
     *
     * @param shopId The ID of the shop.
     * @param userId The ID of the user.
     * @return A boolean indicating whether the user is the owner of the shop.
     * 
     */
    public Boolean isShopOwner(Integer shopId, String userId) throws ShopException {
        Shop shop = getShopByShopId(shopId);
        if (shop != null) {
            return shop.isOwnerOrFounderOwner(userId);
        }
        return false;
    }

    /**
     * Adds a basic discount to the shop.
     *
     * @param shopId         the ID of the shop
     * @param productId      the ID of the product to discount
     * @param username       the username of the user adding the discount
     * @param isPercentage   a flag indicating whether the discount amount is a
     *                       percentage or a fixed value
     * @param discountAmount the amount of the discount
     * @param expirationDate the expiration date of the discount
     * @return the ID of the newly added discount
     * @throws PermissionException if the user does not have permission to add a
     *                             discount to the shop
     * @throws ShopException       if there is an error adding the discount to the
     *                             shop
     */
    public int addBasicDiscountToShop(int shopId, int productId, String username, boolean isPrecentage,
            double discountAmount,
            Date expirationDate) throws PermissionException, ShopException {

        Shop shop = getShopByShopId(shopId);
        if (!shop.checkPermission(username, Permission.ADD_DISCOUNT_POLICY))
            throw new PermissionException("User " + username + " has no permission to add discount to shop " + shopId);
        BaseDiscount discount;
        if (isPrecentage)
            discount = new PrecentageDiscount(expirationDate, discountAmount, productId);
        else
            discount = new PrecentageDiscount(expirationDate, discountAmount, productId);
        return shop.addDiscount(discount);
    }

    /**
     * Adds a conditional discount to a shop.
     *
     * @param shopId           the ID of the shop
     * @param productId        the ID of the product to discount if the condition is
     *                         met
     * @param username         the username of the user adding the discount
     * @param mustHaveProducts a list of product IDs that must be present in the
     *                         cart for the discount to apply
     * @param isPercentage     a flag indicating whether the discount amount is a
     *                         percentage or a fixed amount
     * @param discountAmount   the amount of the discount
     * @param expirationDate   the expiration date of the discount
     * @return the ID of the newly added discount
     * @throws PermissionException if the user does not have permission to add a
     *                             discount to the shop
     * @throws ShopException       if the shop does not exist or an error occurs
     *                             while adding the discount
     */
    public int addConditionalDiscountToShop(int shopId, int productId, String username, List<Integer> mustHaveProducts,
            boolean isPrecentage, double discountAmount, Date expirationDate)
            throws PermissionException, ShopException {

        Shop shop = getShopByShopId(shopId);
        if (!shop.checkPermission(username, Permission.ADD_DISCOUNT_POLICY))
            throw new PermissionException("User " + username + " has no permission to add discount to shop " + shopId);
        BaseDiscount baseDiscount;
        if (isPrecentage)
            baseDiscount = new PrecentageDiscount(expirationDate, discountAmount, productId);
        else
            baseDiscount = new PrecentageDiscount(expirationDate, discountAmount, productId);

        ConditionalDiscount discount = new ConditionalDiscount(mustHaveProducts, baseDiscount);
        return shop.addDiscount(discount);
    }

    /**
     * Removes a discount from a shop.
     *
     * @param shopId     the ID of the shop
     * @param discountId the ID of the discount to remove
     * @param username   the username of the user removing the discount
     * @throws PermissionException if the user does not have permission to remove a
     *                             discount from the shop
     * @throws ShopException       if the shop does not exist or an error occurs
     *                             while removing the discount
     */
    public void removeDiscountFromShop(int shopId, int discountId, String username)
            throws PermissionException, ShopException {
        Shop shop = getShopByShopId(shopId);
        if (!shop.checkPermission(username, Permission.REMOVE_DISCOUNT_METHOD))
            throw new PermissionException(
                    "User " + username + " has no permission to remove discount from shop " + shopId);
        shop.removeDiscount(discountId);
    }

    // this function is responsible searching a product in a shop by its name for all type of users
    // by checking if all inputs are valid and then calling the function in shop
    public Map<Integer, List<Product>> getProductInShopByName(Integer shopId, String productName) throws Exception {
        Map<Integer, List<Product>> productsByShop = new HashMap<>();
        // If productName is null, raise an error
        if (productName == null) {
            throw new Exception("Product name is null.");
        }
        // If shopId is null, search in all shops
        if (shopId == null) {
            for (Shop shop : this._shopsList) {
                List<Product> products = shop.getProductsByName(productName);
                if (!products.isEmpty()) {
                    productsByShop.put(shop.getShopId(), products);
                }
            }
        }
        // Search in a specific shop
        else {
            if (isShopIdExist(shopId)) {
                Shop shop = getShopByShopId(shopId);
                List<Product> products = shop.getProductsByName(productName);
                productsByShop.put(shop.getShopId(), products);
            } else {
                throw new Exception(String.format("Shop ID: %d doesn't exist.", shopId));
            }
        }
        return productsByShop;
    }

    public Map<Integer, List<Product>> getProductInShopByCategory(Integer shopId, Category productCategory)
            throws Exception {
        Map<Integer, List<Product>> productsByShop = new HashMap<>();
        // If category is null, raise an error
        if (productCategory == null) {
            throw new Exception("Product category is null.");
        }
        // If shopId is null, search in all shops
        if (shopId == null) {
            for (Shop shop : this._shopsList) {
                List<Product> products = shop.getProductsByCategory(productCategory);
                if (!products.isEmpty()) {
                    productsByShop.put(shop.getShopId(), products);
                }
            }
        }
        // Search in a specific shop
        else {
            if (isShopIdExist(shopId)) {
                Shop shop = getShopByShopId(shopId);
                List<Product> products = shop.getProductsByCategory(productCategory);
                // If the shop has products in the requested category, add them to the map
                if (!products.isEmpty()) {
                    productsByShop.put(shop.getShopId(), products);
                }
            } else {
                throw new Exception(String.format("Shop ID: %d doesn't exist.", shopId));
            }
        }
        return productsByShop;
    }

    public Map<Integer, List<Product>> getProductsInShopByKeywords(Integer shopId, List<String> keywords)
            throws Exception {
        // If keywords is null, raise an error
        if (keywords == null || keywords.isEmpty()) {
            throw new Exception("Product keywords is null.");
        }
        Map<Integer, List<Product>> productsByShop = new HashMap<>();
        // If shopId is null, search in all shops
        if (shopId == null) {
            for (Shop shop : this._shopsList) {
                List<Product> products = shop.getProductsByKeywords(keywords);
                if (!products.isEmpty()) {
                    productsByShop.put(shop.getShopId(), products);
                }
            }
        }
        // Search in a specific shop
        else {
            if (isShopIdExist(shopId)) {
                Shop shop = getShopByShopId(shopId);
                List<Product> products = shop.getProductsByKeywords(keywords);
                productsByShop.put(shop.getShopId(), products);
            } else {
                throw new Exception(String.format("Shop ID: %d doesn't exist.", shopId));
            }
        }
        return productsByShop;
    }

    public Map<Integer, List<Product>> getProductsInShopByPriceRange(Integer shopId, Double minPrice, Double maxPrice)
            throws Exception {
        Map<Integer, List<Product>> productsByShop = new HashMap<>();
        // If shopId is null, search in all shops
        if (shopId == null) {
            for (Shop shop : this._shopsList) {
                List<Product> products = shop.getProductsByPriceRange(minPrice, maxPrice);
                if (!products.isEmpty()) {
                    productsByShop.put(shop.getShopId(), products);
                }
            }
        }
        // Search in a specific shop
        else {
            if (isShopIdExist(shopId)) {
                Shop shop = getShopByShopId(shopId);
                List<Product> products = shop.getProductsByPriceRange(minPrice, maxPrice);
                productsByShop.put(shop.getShopId(), products);
            } else {
                throw new Exception(String.format("Shop ID: %d doesn't exist.", shopId));
            }
        }
        return productsByShop;
    }

    public void updateProductQuantity(String userName, Integer shopId, Integer productId, Integer productAmount)
            throws Exception {
        Shop shop = getShopByShopId(shopId);
        if (shop == null)
            throw new Exception(String.format("Shop ID: %d doesn't exist.", shopId));

        shop.updateProductQuantity(userName, productId, productAmount);
    }

    public String getShopFounderUsername(Integer shopId) {
        Shop shop = getShopByShopId(shopId);
        if (shop != null) {
            return shop.getFounderName();
        }
        return null;
    }

    /**
     * Adds a new owner to a shop.
     * @param username the username of the user adding the owner
     * @param shopId the ID of the shop
     * @param ownerUsername the username of the new owner
     * @throws Exception
     */
    public void addShopOwner(String username, Integer shopId, String ownerUsername) throws Exception {
        Shop shop = getShopByShopId(shopId);
        if (shop == null) {
            throw new Exception(String.format("Shop ID: %d doesn't exist.", shopId));
        }
        shop.AppointOwner(username, ownerUsername);
    }

    /**
     * Adds a new manager to a shop.
     * @param username the username of the user adding the manager
     * @param shopId the ID of the shop
     * @param managerUsername the username of the new manager
     * @param permissions the permissions to assign to the manager
     * @throws Exception
     */
    public void addShopManager(String username, Integer shopId, String managerUsername, Set<String> permissions) throws Exception {
        Shop shop = getShopByShopId(shopId);
        if (shop == null) {
            throw new Exception(String.format("Shop ID: %d doesn't exist.", shopId));
        }
        //Here we create a set of permissions from the strings.
        Set<Permission> permissionsSet = permissions.stream().map(permissionString -> Permission.valueOf(permissionString.toUpperCase())).collect(Collectors.toSet());
        shop.AppointManager(username, managerUsername, permissionsSet);
    }

    /**
     * Removes a manager from a shop.
     * @param username the username of the user removing the manager
     * @param shopId the ID of the shop
     * @param managerUsername the username of the manager to remove
     * @return the usernames of the managers that were removed
     * @throws Exception
     */
    public Set<String> fireShopManager (String username, Integer shopId, String managerUsername) throws Exception {
        Shop shop = getShopByShopId(shopId);
        if (shop == null) {
            throw new Exception(String.format("Shop ID: %d doesn't exist.", shopId));
        }
        return shop.fireRole(username, managerUsername);
    }

    /**
     * Resign a role from the shop.
     * @param username the username of the user resigning
     * @param shopId the ID of the shope
     * @return the usernames of the roles that were resigned
     * @throws Exception
     */
    public Set<String> resignFromRole(String username, Integer shopId) throws ShopException{
        Shop shop = getShopByShopId(shopId);
        if (shop == null) {
            return null;
        }
        return shop.resign(username);
    }

    /**
     * Modify the permissions of a manager in a shop.
     * @param username the username of the user modifying the permissions
     * @param shopId the ID of the shop
     * @param managerUsername the username of the manager to modify
     * @param permissions the permissions to assign to the manager
     * @throws Exception
     */
    public void modifyManagerPermissions(String username, Integer shopId, String managerUsername, Set<String> permissions) throws Exception {
        Shop shop = getShopByShopId(shopId);
        if (shop == null) {
            throw new Exception(String.format("Shop ID: %d doesn't exist.", shopId));
        }
        //Here we create a set of permissions from the strings.
        Set<Permission> permissionsSet = permissions.stream().map(permissionString -> Permission.valueOf(permissionString.toUpperCase())).collect(Collectors.toSet());
        shop.modifyPermissions(username, managerUsername, permissionsSet);
    }
}
