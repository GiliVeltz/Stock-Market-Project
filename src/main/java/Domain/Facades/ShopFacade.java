package Domain.Facades;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import Domain.Discounts.BaseDiscount;
import Domain.Discounts.ConditionalDiscount;
import Domain.Discounts.FixedDiscount;
import Domain.Discounts.PrecentageDiscount;
import Domain.Product;
import Domain.Role;
import Domain.Repositories.MemoryShopRepository;
import Domain.Repositories.ShopRepositoryInterface;
import Domain.Shop;
import Domain.Alerts.Alert;
import Domain.Alerts.AppointedManagerAlert;
import Domain.Alerts.AppointedOwnerAlert;
import Domain.Alerts.FireManagerAlert;
import Domain.ShopOrder;
import Dtos.BasicDiscountDto;
import Dtos.ConditionalDiscountDto;
import Dtos.ProductDto;
import Dtos.ShopDto;
import Dtos.ShopManagerDto;
import Dtos.ShopGetterDto;
import Dtos.ShoppingBasketRuleDto;
import Exceptions.PermissionException;
import Exceptions.ShopException;
import Exceptions.StockMarketException;
import enums.Category;
import enums.Permission;

public class ShopFacade {
    private static ShopFacade _shopFacade;

    private UserFacade _userFacade;
    private ShopRepositoryInterface _shopRepository;

    public ShopFacade() {
        _shopRepository = new MemoryShopRepository(new ArrayList<>());
        _userFacade = UserFacade.getUserFacade();

        //For testing UI
        try {
            initUI();
        }
        catch (StockMarketException e) {
            e.printStackTrace();
        }
    }

    public ShopFacade(List<Shop> shopsList) { // ForTests
        _shopRepository = new MemoryShopRepository(shopsList);
        _userFacade = UserFacade.getUserFacade();
    }

    // Public method to provide access to the _shopFacade
    public static synchronized ShopFacade getShopFacade() {
        if (_shopFacade == null) {
            _shopFacade = new ShopFacade();
        }
        return _shopFacade;
    }

    public Shop getShopByShopId(int shopId) {
        return _shopRepository.getShopByID(shopId);
    }

    /**
     * Checks if a shop ID exists.
     * 
     * @param shopId The ID of the shop to check.
     * @return True if the shop ID exists, false otherwise.
     */
    public Boolean isShopIdExist(int shopId) {
        return _shopRepository.doesShopExist(shopId);
    }

    public Integer openNewShop(String userName, ShopDto shopDto) throws StockMarketException {
        // check if the shop name already exists in the system, should be unique
        for (Shop shop : getAllShops()) {
            if (shop.getShopName().equals(shopDto.shopName)) {
                throw new StockMarketException(String.format("Shop name: %s already exists in the system.",
                        shopDto.shopName));
            }
        }
        
        // check and validate the shop details
        if (shopDto.shopName == null || shopDto.shopName.isEmpty()) {
            throw new StockMarketException("Shop name is null or empty.");
        }
        if (shopDto.bankDetails == null || shopDto.bankDetails.isEmpty()) {
            throw new StockMarketException("Bank details is null or empty.");
        }
        if (shopDto.shopAddress == null || shopDto.shopAddress.isEmpty()) {
            throw new StockMarketException("Shop address is null or empty.");
        }

        int shopId = _shopRepository.getUniqueShopID();
        _shopRepository.addShop(new Shop(shopId, shopDto.shopName, userName, shopDto.bankDetails, shopDto.shopAddress));
        getShopByShopId(shopId).notifyReOpenShop(userName);
        return shopId;
    }

    // close shop only if the user is the founder of the shop
    public void closeShop(Integer shopId, String userName) throws StockMarketException {
        try {
            if (!isShopIdExist(shopId))
                throw new StockMarketException(String.format("Shop ID: %d does not exist.", shopId));
            else {
                Shop shopToClose = getShopByShopId(shopId);
                if (shopToClose.checkPermission(userName, Permission.FOUNDER) || _userFacade.isAdmin(userName)) {
                    shopToClose.closeShop();
                    getShopByShopId(shopId).notifyCloseShop(userName);
                } else {
                    throw new StockMarketException(String.format(
                            "User %s can't cloase the Shop: %d. Only the fonder has the permission", userName, shopId));
                }
            }
        } catch (StockMarketException e) {
            throw new StockMarketException(e.getMessage());
        }

    }

    // reopen a shop only if the user is the founder of the shop
    public void reOpenShop(Integer shopId, String userName) throws Exception {
        try {
            if (!isShopIdExist(shopId))
                throw new Exception(String.format("Shop ID: %d does not exist.", shopId));
            else {
                Shop shopToReOpen = getShopByShopId(shopId);
                if (shopToReOpen.checkPermission(userName, Permission.FOUNDER) || _userFacade.isAdmin(userName)) {
                    getShopByShopId(shopId).notifyReOpenShop(userName);
                    shopToReOpen.reopenShop();
                } else {
                    throw new Exception(String.format(
                            "User %s can't reopen the Shop: %d. Only the fonder has the permission", userName, shopId));
                }
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    /*
     * Add a product to a shop by its ID.
     * 
     * @param shopId The ID of the shop.
     * 
     * @param productDto The product DTO.
     * 
     * @param userName The username of the user adding the product.
     */
    public void addProductToShop(Integer shopId, ProductDto productDto, String userName) throws StockMarketException {
        // If the shop ID does not exist, raise an error
        if (!isShopIdExist(shopId))
            throw new StockMarketException(String.format("Shop ID: %d does not exist.", shopId));
        // If the product name exists in the shop, raise an error
        if (getShopByShopId(shopId).isProductNameExist(productDto.productName))
            throw new StockMarketException(String.format("Product name: %s already exists in shop: %d.",
                    productDto.productName, shopId));
        int productId = _shopRepository.getUniqueProductID();
        Product newProduct = new Product(productId, productDto.productName, productDto.category, productDto.price);
        newProduct.updateProductQuantity(productDto.productQuantity);
        getShopByShopId(shopId).addProductToShop(userName, newProduct);
    }

    /*
     * Remove a product from a shop by its ID.
     * 
     * @param shopId The ID of the shop.
     * 
     * @param productDto The product DTO.
     * 
     * @param userName The username of the user removing the product.
     */
    public synchronized void removeProductFromShop(Integer shopId, ProductDto productDto, String userName)
            throws StockMarketException {
        // If the shop ID does not exist, raise an error
        if (!isShopIdExist(shopId))
            throw new StockMarketException(String.format("Shop ID: %d does not exist.", shopId));
        // If one of the inputs in productDto is null, raise an error
        if (productDto == null || productDto.productName == null || productDto.productName.isEmpty())
            throw new StockMarketException("Product name is null.");
        // If the product name does not exists in the shop, raise an error
        if (!getShopByShopId(shopId).isProductNameExist(productDto.productName))
            throw new StockMarketException(String.format("Product name: %s is not exists in shop: %d.",
                    productDto.productName, shopId));
        getShopByShopId(shopId).removeProductFromShop(userName, productDto.productName);
    }

    /*
     * Edit a product in a shop by its ID.
     * 
     * @param shopId The ID of the shop.
     * 
     * @param productDtoOld The product to be edit in the shop - the old vars of the
     * product.
     * 
     * @param productDtoNew The product to be edit in the shop - the new vars of the
     * product.
     * 
     * @param userName The username of the user editing the product.
     */
    public synchronized void editProductInShop(Integer shopId, ProductDto productDtoOld, ProductDto productDtoNew,
            String userName) throws StockMarketException {
        // If the shop ID does not exist, raise an error
        if (!isShopIdExist(shopId))
            throw new StockMarketException(String.format("Shop ID: %d does not exist.", shopId));

        // If one of the inputs in productDto is null, raise an error
        if (productDtoOld == null || productDtoOld.productName == null || productDtoOld.productName.isEmpty())
            throw new StockMarketException("Old product name is null.");
        if (productDtoNew == null || productDtoNew.productName == null || productDtoNew.productName.isEmpty())
            throw new StockMarketException("New product name is null.");
        if (productDtoOld == null || productDtoOld.category == null)
            throw new StockMarketException("Old product category is null.");
        if (productDtoNew == null || productDtoNew.category == null)
            throw new StockMarketException("New product category is null.");
        if (productDtoOld == null || productDtoOld.price == 0.0)
            throw new StockMarketException("Old product price can not be 0.");
        if (productDtoNew == null || productDtoNew.price == 0.0)
            throw new StockMarketException("New product price can not be 0..");

        // If the product name does not exists in the shop, raise an error
        if (!getShopByShopId(shopId).isProductNameExist(productDtoOld.productName))
            throw new StockMarketException(String.format("Product name: %s is not exists in shop: %d.",
                    productDtoOld.productName, shopId));

        // If the new product name already exists in the shop, raise an error
        if (getShopByShopId(shopId).isProductNameExist(productDtoNew.productName))
            throw new StockMarketException(String.format("Product name: %s already exists in shop: %d.",
                    productDtoNew.productName, shopId));

        getShopByShopId(shopId).editProductInShop(userName, productDtoOld.productName, productDtoNew.productName,
                productDtoNew.category, productDtoNew.price);
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
     * @throws Exceptions.ShopException
     * 
     */
    public Boolean isShopOwner(Integer shopId, String userId) throws StockMarketException {
        Shop shop = getShopByShopId(shopId);
        if (shop != null) {
            return shop.isOwnerOrFounderOwner(userId);
        }
        return false;
    }

    /**
     * Adds a basic discount to the shop.
     *
     * @param shopId      the ID of the shop
     * @param username    the username of the user adding the discount
     * @param discountDto the discount DTO
     * @throws PermissionException if the user does not have permission to add a
     *                             discount to the shop
     * @throws ShopException       if there is an error adding the discount to the
     *                             shop
     */
    public int addBasicDiscountToShop(int shopId, String username, BasicDiscountDto discountDto)
            throws StockMarketException {

        Shop shop = getShopByShopId(shopId);
        if (!shop.checkPermission(username, Permission.ADD_DISCOUNT_POLICY))
            throw new PermissionException("User " + username + " has no permission to add discount to shop " + shopId);
        BaseDiscount discount;
        if (discountDto.isPrecentage)
            discount = new PrecentageDiscount(discountDto);
        else
            discount = new FixedDiscount(discountDto);
        return shop.addDiscount(discount);
    }

    /**
     * Adds a conditional discount to a shop.
     *
     * @param shopId      the ID of the shop
     * @param username    the username of the user adding the discount
     * @param discountDto the discount DTO
     * @return the ID of the newly added discount
     * @throws PermissionException if the user does not have permission to add a
     *                             discount to the shop
     * @throws ShopException       if the shop does not exist or an error occurs
     *                             while adding the discount
     */
    public int addConditionalDiscountToShop(int shopId, String username, ConditionalDiscountDto discountDto)
            throws StockMarketException {

        Shop shop = getShopByShopId(shopId);
        if (!shop.checkPermission(username, Permission.ADD_DISCOUNT_POLICY))
            throw new PermissionException("User " + username + " has no permission to add discount to shop " + shopId);

        ConditionalDiscount discount = new ConditionalDiscount(discountDto);
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
    public void removeDiscountFromShop(int shopId, int discountId, String username) throws StockMarketException {
        Shop shop = getShopByShopId(shopId);
        if (!shop.checkPermission(username, Permission.REMOVE_DISCOUNT_METHOD))
            throw new PermissionException(
                    "User " + username + " has no permission to remove discount from shop " + shopId);
        shop.removeDiscount(discountId);
    }

    // this function is responsible searching a product in a shop by its name for
    // all type of users
    // by checking if all inputs are valid and then calling the function in shop
    public Map<Integer, List<Product>> getProductInShopByName(Integer shopId, String productName)
            throws StockMarketException {
        Map<Integer, List<Product>> productsByShop = new HashMap<>();
        // If productName is null, raise an error
        if (productName == null) {
            throw new StockMarketException("Product name is null.");
        }
        // If shopId is null, search in all shops
        if (shopId == null) {
            for (Shop shop : getAllShops()) {
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
                throw new StockMarketException(String.format("Shop ID: %d doesn't exist.", shopId));
            }
        }
        return productsByShop;
    }

    public List<Shop> getAllShops() {
        return _shopRepository.getAllShops();
    }

    public List<ShopDto> getAllShopsDto() {
        List<ShopDto> shops = new ArrayList<>();
        for(Shop shop : getAllShops()){
            ShopDto shopDto = new ShopDto(shop);
            shops.add(shopDto);
        }
        return shops;
    }

    public Map<Integer, List<Product>> getProductInShopByCategory(Integer shopId, Category productCategory)
            throws StockMarketException {
        Map<Integer, List<Product>> productsByShop = new HashMap<>();
        // If category is null, raise an error
        if (productCategory == Category.DEFAULT_VAL) {
            throw new StockMarketException("Product category is null.");
        }
        // If shopId is null, search in all shops
        if (shopId == null) {
            for (Shop shop : getAllShops()) {
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
                throw new StockMarketException(String.format("Shop ID: %d doesn't exist.", shopId));
            }
        }
        return productsByShop;
    }

    public Map<Integer, List<Product>> getProductsInShopByKeywords(Integer shopId, List<String> keywords)
            throws StockMarketException {
        // If keywords is null, raise an error
        if (keywords == null || keywords.isEmpty()) {
            throw new StockMarketException("Product keywords is null or empty.");
        }
        Map<Integer, List<Product>> productsByShop = new HashMap<>();
        // If shopId is null, search in all shops
        if (shopId == null) {
            for (Shop shop : getAllShops()) {
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
                if (!products.isEmpty()) {
                    productsByShop.put(shop.getShopId(), products);
                }
            } else {
                throw new StockMarketException(String.format("Shop ID: %d doesn't exist.", shopId));
            }
        }
        return productsByShop;
    }

    public Map<Integer, List<Product>> getProductsInShopByPriceRange(Integer shopId, Double minPrice, Double maxPrice)
            throws StockMarketException {
        Map<Integer, List<Product>> productsByShop = new HashMap<>();
        // If shopId is null, search in all shops
        if (shopId == null) {
            for (Shop shop : getAllShops()) {
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
                if (!products.isEmpty()) {
                    productsByShop.put(shop.getShopId(), products);
                }
            } else {
                throw new StockMarketException(String.format("Shop ID: %d doesn't exist.", shopId));
            }
        }
        return productsByShop;
    }

    public void updateProductQuantity(String userName, Integer shopId, Integer productId, Integer productAmount)
            throws StockMarketException {
        Shop shop = getShopByShopId(shopId);
        if (shop == null)
            throw new StockMarketException(String.format("Shop ID: %d doesn't exist.", shopId));

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
     * 
     * @param username      the username of the user adding the owner
     * @param shopId        the ID of the shop
     * @param ownerUsername the username of the new owner
     * @throws StockMarketException
     */
    public void addShopOwner(String username, Integer shopId, String ownerUsername) throws StockMarketException {
        Shop shop = getShopByShopId(shopId);
        if (shop == null) {
            throw new StockMarketException(String.format("Shop ID: %d doesn't exist.", shopId));
        }
        shop.AppointOwner(username, ownerUsername);
        notifyAppointOwner(username, ownerUsername, shopId);
    }

    // notify the owner that he was appointed
    private void notifyAppointOwner(String username, String targetUser, int shopId) {
        Alert alert = new AppointedOwnerAlert(username, targetUser, shopId);
        _userFacade.notifyUser(targetUser, alert);
    }

    /**
     * Adds a new manager to a shop.
     * 
     * @param username        the username of the user adding the manager
     * @param shopId          the ID of the shop
     * @param managerUsername the username of the new manager
     * @param permissions     the permissions to assign to the manager
     * @throws StockMarketException
     */
    public void addShopManager(String username, Integer shopId, String managerUsername, Set<String> permissions)
            throws Exception {
        Shop shop = getShopByShopId(shopId);
        if (shop == null) {
            throw new Exception(String.format("Shop ID: %d doesn't exist.", shopId));
        }
        // Here we create a set of permissions from the strings.
        Set<Permission> permissionsSet = permissions.stream()
                .map(permissionString -> Permission.valueOf(permissionString.toUpperCase()))
                .collect(Collectors.toSet());
        shop.AppointManager(username, managerUsername, permissionsSet);
        notifyAppointManager(username, managerUsername, permissions,shopId);
    }

    //notify the manager that he was appointed
    private void notifyAppointManager(String username, String targetUser, Set<String> permissions, Integer shopId) {
        Alert alert = new AppointedManagerAlert(username, targetUser, permissions, shopId);
        _userFacade.notifyUser(targetUser, alert);
    }

    /**
     * Removes a manager from a shop.
     * 
     * @param username        the username of the user removing the manager
     * @param shopId          the ID of the shop
     * @param managerUsername the username of the manager to remove
     * @return the usernames of the managers that were removed
     * @throws StockMarketException
     */
    public Set<String> fireShopManager(String username, Integer shopId, String managerUsername)
            throws StockMarketException {
        Shop shop = getShopByShopId(shopId);
        Set<String> result = new HashSet<String>();
        if (shop == null) {
            throw new StockMarketException(String.format("Shop ID: %d doesn't exist.", shopId));
        }      
        result = shop.fireRole(username, managerUsername);
        notifyFireUser(username,managerUsername, shopId);

        return result;
        
    }
    //notify the manager that he was fired
    public void notifyFireUser(String targetUser, String manager, int shopId) {
        Alert alert = new FireManagerAlert(manager, targetUser, shopId);
        _userFacade.notifyUser(targetUser, alert);
    }

    /**
     * Resign a role from the shop.
     * 
     * @param username the username of the user resigning
     * @param shopId   the ID of the shope
     * @return the usernames of the roles that were resigned
     * @throws StockMarketException
     */
    public Set<String> resignFromRole(String username, Integer shopId) throws StockMarketException {
        Shop shop = getShopByShopId(shopId);
        if (shop == null) {
            return null;
        }
        return shop.resign(username);
    }

    /**
     * Modify the permissions of a manager in a shop.
     * 
     * @param username        the username of the user modifying the permissions
     * @param shopId          the ID of the shop
     * @param managerUsername the username of the manager to modify
     * @param permissions     the permissions to assign to the manager
     * @throws StockMarketException
     */
    public void modifyManagerPermissions(String username, Integer shopId, String managerUsername,
            Set<String> permissions) throws StockMarketException {
        Shop shop = getShopByShopId(shopId);
        if (shop == null) {
            throw new StockMarketException(String.format("Shop ID: %d doesn't exist.", shopId));
        }
        // Here we create a set of permissions from the strings.
        Set<Permission> permissionsSet = permissions.stream()
                .map(permissionString -> Permission.valueOf(permissionString.toUpperCase()))
                .collect(Collectors.toSet());
        shop.modifyPermissions(username, managerUsername, permissionsSet);
        
    }

    public String getShopPolicyInfo(Integer shopId) throws StockMarketException {
        if (isShopIdExist(shopId)) {
            Shop shop = getShopByShopId(shopId);
            return shop.getShopPolicyInfo();
        } else {
            throw new StockMarketException(String.format("Shop ID: %d doesn't exist.", shopId));
        }
    }

    public String getProductPolicyInfo(Integer shopId, Integer productId) throws StockMarketException {
        if (isShopIdExist(shopId)) {
            Shop shop = getShopByShopId(shopId);
            return shop.getProductPolicyInfo(productId);
        } else {
            throw new StockMarketException(String.format("Shop ID: %d doesn't exist.", shopId));
        }
    }

    public String getShopDiscountsInfo(Integer shopId) throws StockMarketException {
        if (isShopIdExist(shopId)) {
            Shop shop = getShopByShopId(shopId);
            return shop.getShopDiscountsInfo();
        } else {
            throw new StockMarketException(String.format("Shop ID: %d doesn't exist.", shopId));
        }
    }

    public String getProductDiscountsInfo(Integer shopId, Integer productId) throws StockMarketException {
        if (isShopIdExist(shopId)) {
            Shop shop = getShopByShopId(shopId);
            return shop.getProductDiscountsInfo(productId);
        } else {
            throw new StockMarketException(String.format("Shop ID: %d doesn't exist.", shopId));
        }
    }

    public String getShopGeneralInfo(Integer shopId) throws StockMarketException {
        if (isShopIdExist(shopId)) {
            Shop shop = getShopByShopId(shopId);
            return shop.getShopGeneralInfo();
        } else {
            throw new StockMarketException(String.format("Shop ID: %d doesn't exist.", shopId));
        }
    }

    public String getProductGeneralInfo(Integer shopId, Integer productId) throws Exception {
        if (isShopIdExist(shopId)) {
            Shop shop = getShopByShopId(shopId);
            return shop.getProductGeneralInfo(productId);
        } else {
            throw new Exception(String.format("Shop ID: %d doesn't exist.", shopId));
        }
    }

    public void addProductRating(Integer shopId, Integer productId, Integer rating) throws StockMarketException {
        if (!isShopIdExist(shopId))
            throw new StockMarketException(String.format("Shop ID: %d doesn't exist.", shopId));

        Shop shop = getShopByShopId(shopId);
        shop.addProductRating(productId, rating);
    }

    public void addShopRating(Integer shopId, Integer rating) throws StockMarketException {
        if (!isShopIdExist(shopId))
            throw new StockMarketException(String.format("Shop ID: %d doesn't exist.", shopId));

        Shop shop = getShopByShopId(shopId);
        shop.addShopRating(rating);
    }

    /**
     * Returns the shop name if exists, else returns null.
     * 
     * @param shopId
     * @return
     */
    public String getShopName(Integer shopId) {
        Shop shop = getShopByShopId(shopId);
        if (shop != null) {
            return shop.getShopName();
        }
        return null;
    }

    /**
     * Returns the shop bank details if exists, else returns null.
     * 
     * @param shopId
     * @return
     */
    public String getShopBankDetails(Integer shopId) {
        Shop shop = getShopByShopId(shopId);
        if (shop != null) {
            return shop.getBankDetails();
        }
        return null;
    }

    /**
     * Returns the shop address if exists, else returns null.
     * 
     * @param shopId
     * @return
     */
    public String getShopAddress(Integer shopId) {
        Shop shop = getShopByShopId(shopId);
        if (shop != null) {
            return shop.getShopAddress();
        }
        return null;
    }

    /**
     * Returns all the products in a shop by its ID.
     * 
     * @param shopId
     * @return
     */
    public List<Product> getAllProductsInShopByID(Integer shopId) {
        Shop shop = getShopByShopId(shopId);
        if (shop != null) {
            return shop.getAllProductsList();
        }
        return null;
    }

     /**
     * Returns all the products in a shop by its ID as DTO.
     * 
     * @param shopId
     * @return
     */
    public List<ProductDto> getAllProductsDtoInShopByID(Integer shopId) {
        List<ProductDto> productDtos = new ArrayList<>();

        List<Product> products = getAllProductsInShopByID(shopId);
        for(Product product : products)
        {
            productDtos.add(new ProductDto(product));
        }
        return productDtos;
    }


    /**
     * Returns all shopIds of shops with the input name.
     * 
     * @param shopName The name of the shop to search for.
     * @return A list of the matching shopIds
     */
    public List<Integer> getShopIdsByName(String shopName) {
        List<Integer> shopIds = new ArrayList<>();
        for (Shop shop : getAllShops()) {
            if (shop.getShopName().equals(shopName)) {
                shopIds.add(shop.getShopId());
            }
        }
        return shopIds;
    }

    /**
     * Get all the shops that the user has a role in
     * 
     * @param username the user's username
     * @return the list of shops that the user has a role in
     * @throws StockMarketException
     */
    public List<Integer> getUserShops(String username) throws StockMarketException {
        List<Integer> shops = new ArrayList<>();
        for (Shop shop : getAllShops()) {
            if (shop.checkIfHasRole(username)) {
                shops.add(shop.getShopId());
            }
        }
        return shops;
    }

    /**
     * Get all the shops that the user has a role in
     * 
     * @param username the user's username
     * @return the list of shops that the user has a role in
     * @throws StockMarketException
     */
    public void changeShopPolicy(String username, int shopId, List<ShoppingBasketRuleDto> shopRules)
            throws StockMarketException {
        Shop shop = getShopByShopId(shopId);
        if (shop == null)
            throw new StockMarketException(String.format("Shop ID: %d doesn't exist.", shopId));
        if (shop.isShopClosed())
            throw new StockMarketException(String.format("Shop ID: %d is closed.", shopId));
        shop.changeShopPolicy(username, shopRules);
    }

    // This function is responsible for getting all the shops in the system
    public List<ShopGetterDto> getShopsEntities() {
        List<Shop> shops = getAllShops();
        List<ShopGetterDto> shopsDto = new ArrayList<>();
        for (Shop shop : shops) {
            shopsDto.add(new ShopGetterDto(shop));
        }
        return shopsDto;
    }
    
    // This function is responsible for getting all the information about a shop
    public ShopDto getShopInfo(Integer shopId) {
        Shop shop = getShopByShopId(shopId);
        if (shop != null) {
            return new ShopDto(shop);
        }
        return null;
    }
      
    /**
     * Get the permissions of a user in a shop
     * @param username the user's username
     * @param shopId the shop's ID
     * @return the list of permissions of the user in the shop
     * @throws StockMarketException
     */
    public List<String> getShopManagerPermissions(String username, int shopId) throws StockMarketException{
        Shop shop = getShopByShopId(shopId);
        if (shop == null) {
            throw new StockMarketException(String.format("Shop ID: %d doesn't exist.", shopId));
        }
        Set<Permission> permissions = shop.getRole(username).getPermissions();
        List<String> permissionsList = permissions.stream().map(permission -> permission.toString()).collect(Collectors.toList());
        return permissionsList;
    }


    /**
     * Get all the shops names that the user has a role in
     * 
     * @param username the user's username
     * @return the list of shops names that the user has a role in
     * @throws StockMarketException
     */
    public List<String> getUserShopsNames(String username) throws StockMarketException {
        List<String> shops = new ArrayList<>();
        for (Shop shop : getAllShops()) {
            if (shop.checkIfHasRole(username)) {
                shops.add(shop.getShopName());
            }
        }
        return shops;
    }

    /**
     * Adds keywords to a product in a shop
     * @param username
     * @param shopId
     * @param productId
     * @param keywords
     * @throws StockMarketException
     */
    public void addKeywordsToProductInShop (String username, Integer shopId, Integer productId, List<String> keywords) throws StockMarketException {
        Shop shop = getShopByShopId(shopId);
        if (shop == null) {
            throw new StockMarketException(String.format("Shop ID: %d doesn't exist.", shopId));
        }
        shop.addKeywordsToProduct(username, productId, keywords);
    }

    // function to initilaize data for UI testing
    public void initUI() throws StockMarketException {
        // Shop shop = new Shop(10, "shopUITest", "Tal", "bankUITest", "addressUITest");
        // _shopRepository.addShop(shop);
        // Product product = new Product(10, "productUITest", Category.ELECTRONICS, 100.0);
        // product.updateProductQuantity(10);
        // shop.addProductToShop("Tal", product);

        openNewShop("tal", new ShopDto("shopUITest", "bankUITest", "addressUITest"));
        openNewShop("tal", new ShopDto("shopUITest2", "bankUITest2", "addressUITest2"));
        addProductToShop(0, new ProductDto("productUITest", Category.ELECTRONICS, 100.0, 10), "tal");
        addProductToShop(1, new ProductDto("productUITest2", Category.ELECTRONICS, 207.5, 10), "tal");
        addProductToShop(1, new ProductDto("productUITest3", Category.ELECTRONICS, 100.0, 10), "tal");
    }

    public List<ShopManagerDto> getShopManagers(String username, int shopId) throws StockMarketException{
        Shop shop = getShopByShopId(shopId);
        if (shop == null) {
            return null;
        }
        Map<String, Role> roles = shop.getUserToRoleMap(username);
        List<ShopManagerDto> managers = new ArrayList<>();
        for (Map.Entry<String, Role> entry : roles.entrySet()) {
            Set<Permission> permissions = entry.getValue().getPermissions();
            String role;
            if(permissions.contains(Permission.FOUNDER)){
                role = "Founder";
            }else if(permissions.contains(Permission.OWNER)){
                role = "Owner";
            }else{
                role = "Manager";
            }
            ShopManagerDto manager = new ShopManagerDto(entry.getKey(), role , permissions);
            managers.add(manager);
        }
        return managers;
    }
}
