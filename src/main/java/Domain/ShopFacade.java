package Domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Exceptions.ShopException;

public class ShopFacade {
    private static ShopFacade _shopFacade;
    private List<Shop> _shopsList;

    private ShopFacade() {
        this._shopsList = new ArrayList<>();
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

    public void openNewShop(Integer shopId, String userName) throws Exception {
        if (isShopIdExist(shopId))
            throw new Exception(String.format("Shop ID: %d is already exist.", shopId));
        else
            _shopsList.add(new Shop(shopId, userName));
    }

    // close shop only if the user is the founder of the shop
    public void closeShop(Integer shopId, String userName) throws Exception {
        try {
            if (!isShopIdExist(shopId))
                throw new Exception(String.format("Shop ID: %d does not exist.", shopId));
            else {
                Shop shopToClose = getShopByShopId(shopId);
                if (shopToClose.checkPermission(userName, Permission.FOUNDER))
                    _shopsList.remove(shopToClose);
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

        public Map<Integer, List<Product>> getProductInShopByName(Integer shopId, String productName) throws Exception {
        Map<Integer, List<Product>> productsByShop = new HashMap<>();
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
            if(isShopIdExist(shopId)) {
                Shop shop = getShopByShopId(shopId);
                List<Product> products = shop.getProductsByName(productName);
                productsByShop.put(shop.getShopId(), products);
            }
            else {
                throw new Exception(String.format("Shop ID: %d doesn't exist.", shopId));
            }
        }
        return productsByShop;
    }

    public Map<Integer, List<Product>> getProductsInShopByKeywords(Integer shopId, List<String> keywords) throws Exception {
        //TODO: check if keywords is empty
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
            if(isShopIdExist(shopId)) {
                Shop shop = getShopByShopId(shopId);
                List<Product> products = shop.getProductsByKeywords(keywords);
                productsByShop.put(shop.getShopId(), products);
            }
            else {
                throw new Exception(String.format("Shop ID: %d doesn't exist.", shopId));
            }
        }
        return productsByShop;
    }

    public Map<Integer, List<Product>> getProductsInShopByPriceRange(Integer shopId, Double minPrice, Double maxPrice) throws Exception {
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
            if(isShopIdExist(shopId)) {
                Shop shop = getShopByShopId(shopId);
                List<Product> products = shop.getProductsByPriceRange(minPrice, maxPrice);
                productsByShop.put(shop.getShopId(), products);
            }
            else {
                throw new Exception(String.format("Shop ID: %d doesn't exist.", shopId));
            }
        }
        return productsByShop;
    }

}
