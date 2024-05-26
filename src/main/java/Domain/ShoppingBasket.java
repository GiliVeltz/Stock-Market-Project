package Domain;

import java.util.ArrayList;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import Exceptions.ProdcutPolicyException;
import Exceptions.ProductOutOfStockExepction;
import Exceptions.ShopPolicyException;

// This class represents a shopping basket that contains a list of products.
// The shopping basket can belongs to one and only shop and one user.

public class ShoppingBasket implements Cloneable {
    private Shop _shop;
    private List<Integer> _productIdList;
    private double _basketTotalAmount;
    private static final Logger logger = Logger.getLogger(ShoppingBasket.class.getName());

    private Map<Integer, SortedMap<Double, Integer>> _productToPriceToAmount;

    // Constructor
    public ShoppingBasket(Shop shop) {
        _shop = shop;
        _productIdList = new ArrayList<>();
        _basketTotalAmount = 0.0;
        _productToPriceToAmount = new HashMap<>();
    }

    /**
     * Adds a product to the shopping basket after validating the user doesn't violate the product policy.
     * @param user the user that adds the product to the basket
     * @param productId the product id to add to the basket
     * @throws ProdcutPolicyException 
     */
    public void addProductToShoppingBasket(User user, Integer productId) throws ProdcutPolicyException {
        logger.log(Level.FINE,
                "ShoppingBasket - addProductToShoppingBasket - Check if "+user.getUserName()+" can add product with id "+productId+" to basket of shop with id " + _shop.getShopId());
        _shop.ValidateProdcutPolicy(user, _shop.getProductById(productId));
        _productIdList.add(productId);
        logger.log(Level.FINE,
                "ShoppingBasket - addProductToShoppingBasket - User "+user.getUserName()+" validated successfuly for product with id "+productId+" to basket of shop with id " + _shop.getShopId());
    }

    public void removeProductFromShoppingBasket(Integer productId) {
        _productIdList.remove(productId);
    }


    // Calculate and return the total price of all products in the basket
    public double calculateShoppingBasketPrice() {
        resetProductToPriceToAmount();
        _shop.applyDiscounts(this);
        _basketTotalAmount = 0.0;

        // case where there are no discounts on the basket
        if (_productToPriceToAmount.size() == 0) {
            for (Integer product : _productIdList) {
                _basketTotalAmount += _shop.getProductPriceById(product);
            }
        } else {
            // iterate over the productt to price to amount map and calculate the total
            // price
            for (Map.Entry<Integer, SortedMap<Double, Integer>> entry : _productToPriceToAmount.entrySet()) {
                for (Map.Entry<Double, Integer> priceToAmount : entry.getValue().entrySet()) {
                    _basketTotalAmount += priceToAmount.getKey() * priceToAmount.getValue();
                }
            }
        }
        return _basketTotalAmount;
    }

    public int getShopId() {
        return _shop.getShopId();
    }

    public Shop getShop(){
        return _shop;
    }

    public double getShoppingBasketPrice() {
        if (_basketTotalAmount == 0.0)
            return calculateShoppingBasketPrice();
        return _basketTotalAmount;
    }

    public List<Integer> getProductIdList() {
        return _productIdList;
    }

    public List<Product> getProductsList() {
        List<Product> products = new ArrayList<>();
        for (Integer productId : _productIdList) {
            products.add(_shop.getProductById(productId));
        }
        return products;
    }

    /*
     * Go through the list of products in the basket and purchase them.
     * If an exception is thrown, cancel the purchase of all the products that were
     * bought. This function only updates the item's stock.
     */
    public boolean purchaseBasket() throws ShopPolicyException {
        logger.log(Level.FINE,
                "ShoppingBasket - purchaseBasket - Start purchasing basket from shodId: " + _shop.getShopId());
        List<Integer> boughtProductIdList = new ArrayList<>();

        //HERE WE CHECK IF THE SHOP Policy is met.
        try{
        logger.log(Level.FINE,
                "ShoppingBasket - purchaseBasket - Check if the shop policy is ok for shop: " + _shop.getShopId());
        _shop.ValidateBasketMeetsShopPolicy(this);
        }catch (ShopPolicyException e){
            logger.log(Level.FINE,
                "ShoppingBasket - purchaseBasket - Basket didn't meet the shop policy.");
            throw e;
        }
        
        // TODO: consider the discounts using productToPriceToAmount
        
        for (Integer productId : _productIdList) {
            try {
                _shop.getProductById(productId).purchaseProduct();
                boughtProductIdList.add(productId);
            } catch (ProductOutOfStockExepction e) {
                logger.log(Level.SEVERE,
                        "ShoppingBasket - purchaseBasket - Product out of stock in basket from shopId: "
                                + _shop.getShopId() + ". Exception: " + e.getMessage(),
                        e);
                logger.log(Level.FINE,
                        "ShoppingBasket - purchaseBasket - Canceling purchase of all products from basket from shopId: "
                                + _shop.getShopId());
                for (Integer boughtProductId : boughtProductIdList) {
                    _shop.getProductById(boughtProductId).cancelPurchase();
                }
                return false;
            }
        }
        return true;
    }

    public void cancelPurchase() {
        logger.log(Level.FINE,
                "ShoppingBasket - cancelPurchase - Canceling purchase of all products from basket from shodId: "
                        + _shop.getShopId());
        for (Integer productId : _productIdList) {
            _shop.getProductById(productId).cancelPurchase();
        }
    }

    public int getProductCount(Integer productId) {
        int count = 0;

        for (Integer product : _productIdList)
            if (product == productId)
                count++;

        return count;
    }

    /**
     * Resets the product to price to amount mapping in the shopping basket.
     * This method iterates through the product list and updates the mapping
     * based on the product ID, price, and quantity.
     */
    public void resetProductToPriceToAmount() {
        _productToPriceToAmount = new HashMap<>();

        for (Integer productId : _productIdList) {
            double price = _shop.getProductById(productId).getPrice();
            if (!_productToPriceToAmount.containsKey(productId))
                _productToPriceToAmount.put(productId, new TreeMap<>());
            if (!_productToPriceToAmount.get(productId).containsKey(price))
                _productToPriceToAmount.get(productId).put(price, 0);

            int oldAmount = _productToPriceToAmount.get(productId).get(price);
            _productToPriceToAmount.get(productId).put(price, oldAmount + 1);
        }
    }

    public String getShopBankDetails() {
        return _shop.getBankDetails();
    }

    public String getShopAddress() {
        return _shop.getShopAddress();
    }

    @Override
    public ShoppingBasket clone() {
        try {
            ShoppingBasket cloned = (ShoppingBasket) super.clone();
            cloned._shop = this._shop;
            cloned._productIdList = new ArrayList<>(_productIdList);
            cloned._productToPriceToAmount = cloneProductToPriceToAmount();
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // should not happen as we implement Cloneable
        }
    }

    // clone the map
    public Map<Integer, SortedMap<Double, Integer>> cloneProductToPriceToAmount() {
        Map<Integer, SortedMap<Double, Integer>> clonedMap = new HashMap<>();
        for (Map.Entry<Integer, SortedMap<Double, Integer>> entry : _productToPriceToAmount.entrySet()) {
            SortedMap<Double, Integer> clonedInnerMap = new TreeMap<>(entry.getValue());
            clonedMap.put(entry.getKey(), clonedInnerMap);
        }
        return clonedMap;
    }

    public String printAllProducts() {
        StringBuilder sb = new StringBuilder();
        for (Integer product : _productIdList) {
            sb.append(_shop.getProductById(product).toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    public SortedMap<Double, Integer> getProductPriceToAmount(Integer productId) {
        return _productToPriceToAmount.get(productId);
    }

    @Override
    public String toString() {
        return "ShoppingBasket{" +
                "ShopId=" + _shop.getShopId() +
                ", products=" + printAllProducts() +
                '}';
    }

    public boolean isEmpty() {
        return this._productIdList.isEmpty(); 
    }
    
}
