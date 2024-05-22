package Domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopController {
    private static ShopController _shopController;
    private List<Shop> _shopsList;

    private ShopController() {
        this._shopsList = new ArrayList<>();
    }

    // Public method to provide access to the _shopController
    public static synchronized ShopController getShopController() {
        if (_shopController == null) {
            _shopController = new ShopController();
        }
        return _shopController;
    }

    public Shop getShopByShopId(Integer shopId){
        for (Shop shop : this._shopsList) {
            if (shop.getShopId().equals(shopId)) {
                return shop;
            }
        }
        return null;
    }

    public Boolean isShopIdExist(Integer shopId)
    {
        for (Shop shop : this._shopsList) {
            if (shop.getShopId().equals(shopId)) {
                return true;
            }
        }
        return false;
    }

    public void OpenNewShop(Integer shopId, String userName) throws Exception 
    {
        if(isShopIdExist(shopId))
            throw new Exception(String.format("Shop ID: %d is already exist.", shopId));
        else
            _shopsList.add(new Shop(shopId, userName));
    }

    public void addProductToShop(Integer shopId, Product product, String userName )
    {
        //TODO: need to implement  (check if shop exist)

    }

    public Map<Integer, List<Product>> getProductInShopByName(Integer shopId, String productName) throws Exception
    {
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
