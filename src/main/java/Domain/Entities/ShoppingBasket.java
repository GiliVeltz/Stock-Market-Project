package Domain.Entities;

import java.util.ArrayList;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import Exceptions.ProductDoesNotExistsException;
import Exceptions.ProductOutOfStockExepction;
import Exceptions.StockMarketException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import Exceptions.ShopPolicyException;

// This class represents a shopping basket that contains a list of products.
// The shopping basket can belongs to one and only shop and one user.

@Entity
@Table(name = "[shopping_basket]")
public class ShoppingBasket implements Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer shoppingBasketId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "basket_product", 
        joinColumns = @JoinColumn(name = "basket_id"), 
        inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> productsList;

    @Column(name = "total_basket_amount", nullable = false)
    private double basketTotalAmount;

    @Transient
    private Map<Integer, SortedMap<Double, Integer>> _productToPriceToAmount;

    @Transient
    private static final Logger logger = Logger.getLogger(ShoppingBasket.class.getName());

    // Default constructor
    public ShoppingBasket() { }

    // Constructor
    public ShoppingBasket(Shop shop) {
        this.shop = shop;
        productsList = new ArrayList<>();
        basketTotalAmount = 0.0;
        _productToPriceToAmount = new HashMap<>();
    }

    // Adds a product to the shopping basket after validating the user doesn't violate the product policy.
    public void addProductToShoppingBasket(User user, int productId, int quantity) throws StockMarketException {
        if (user == null) {
            logger.log(Level.FINE,
                "ShoppingBasket - addProductToShoppingBasket - Check if a guest (null user in shopping basket) can add product with id "+productId+" to basket of shop with id " + shop.getShopId());
        }
        else{
            logger.log(Level.FINE,
                "ShoppingBasket - addProductToShoppingBasket - Check if "+user.getUserName()+" can add product with id "+productId+" to basket of shop with id " + shop.getShopId());
        }
        
        // check if the product is in the shop and validate the user doesn't violate the product policy
        // shop.ValidateProdcutPolicy(user, shop.getProductById(productId));
        // TODO VLADIN

        // add the product to the basket
        for (int i = 0; i < quantity; i++)
            productsList.add(shop.getProductById(productId));
        
        if (user == null) {
            logger.log(Level.FINE,
                "ShoppingBasket - addProductToShoppingBasket - guest (null user in shopping basket) validated successfuly for product with id "+productId+" to basket of shop with id " + shop.getShopId());
        }
        else{
            logger.log(Level.FINE,
                "ShoppingBasket - addProductToShoppingBasket - User "+user.getUserName()+" validated successfuly for product with id "+productId+" to basket of shop with id " + shop.getShopId());
        }
    }

    public void removeProductFromShoppingBasket(Product product, int quantity) throws StockMarketException {
        // check if the product is in the basket
        for (int i = 0; i < quantity; i++) {
            if (!getProductIdsList().contains(product.getProductId())) {
                logger.log(Level.SEVERE,
                        "ShoppingBasket - removeProductFromShoppingBasket - Product with id " + product.getProductId() + " is not in the basket of shop with id " + shop.getShopId());
                throw new ProductDoesNotExistsException("Product with id " + product.getProductId() + " is not in the basket");
            }
            productsList.remove(product);
        }
    }


    // Calculate and return the total price of all products in the basket
    public double calculateShoppingBasketPrice() throws StockMarketException {
        resetProductToPriceToAmount();
        shop.applyDiscounts(this);
        basketTotalAmount = 0.0;

        // // case where there are no discounts on the basket
        // if (_productToPriceToAmount.size() == 0) {
        //     for (Integer product : _productIdList) {
        //         _basketTotalAmount += _shop.getProductPriceById(product);
        //     }
        // } else {
            // iterate over the product to price to amount map and calculate the total price
            for (Map.Entry<Integer, SortedMap<Double, Integer>> entry : _productToPriceToAmount.entrySet()) {
                for (Map.Entry<Double, Integer> priceToAmount : entry.getValue().entrySet()) {
                    basketTotalAmount += priceToAmount.getKey() * priceToAmount.getValue();
                }
            }
        // }
        return basketTotalAmount;
    }
    
    // Return the total price of all products in the basket
    public double getShoppingBasketPrice() throws StockMarketException{
        if (basketTotalAmount == 0.0)
            return calculateShoppingBasketPrice();
        return basketTotalAmount;
    }

    // Return the list of products in the basket
    public List<Product> getProductsList() {
        return productsList;
    }

    // Return the list of product IDs in the basket
    public List<Integer> getProductIdsList() throws StockMarketException {
        List<Integer> products = new ArrayList<>();
        for (Product product : productsList) {
            products.add(product.getProductId());
        }
        return products;
    }

    /*
     * Go through the list of products in the basket and purchase them.
     * If an exception is thrown, cancel the purchase of all the products that were
     * bought. This function only updates the item's stock.
     */
    public boolean purchaseBasket(String username) throws StockMarketException {
        logger.log(Level.FINE,
                "ShoppingBasket - purchaseBasket - Start purchasing basket from shodId: " + shop.getShopId());
        List<Integer> boughtProductIdList = new ArrayList<>();

        //HERE WE CHECK IF THE SHOP Policy is met.
        try{
        logger.log(Level.FINE,
                "ShoppingBasket - purchaseBasket - Check if the shop policy is ok for shop: " + shop.getShopId());
        shop.ValidateBasketMeetsShopPolicy(this);
        }catch (ShopPolicyException e){
            logger.log(Level.FINE,
                "ShoppingBasket - purchaseBasket - Basket didn't meet the shop policy.");
            throw e;
        }
        
        for (int productId : getProductIdsList()) {
            try {
                shop.getProductById(productId).purchaseProduct();
                boughtProductIdList.add(productId);
            } catch (ProductOutOfStockExepction e) {
                logger.log(Level.SEVERE,
                        "ShoppingBasket - purchaseBasket - Product out of stock in basket from shopId: "
                                + shop.getShopId() + ". Exception: " + e.getMessage(),
                        e);
                logger.log(Level.FINE,
                        "ShoppingBasket - purchaseBasket - Canceling purchase of all products from basket from shopId: "
                                + shop.getShopId());
                for (int boughtProductId : boughtProductIdList) {
                    shop.getProductById(boughtProductId).cancelPurchase();
                }
                return false;
            }
        }
        notfyPurchaseFromShop(username, getProductIdsList(), shop);
        System.out.println("Finished method purchaseBasket - Returning true.");
        return true;
    }

    private void notfyPurchaseFromShop(String buyingUser, List<Integer> productIdList, Shop shop) {
        shop.notfyOwnerPurchaseFromShop(buyingUser,productIdList);
    }

    // Cancel the purchase of all products in the basket
    public void cancelPurchase() throws StockMarketException {
        logger.log(Level.FINE,
                "ShoppingBasket - cancelPurchase - Canceling purchase of all products from basket from shodId: "
                        + shop.getShopId());
        for (Product product : productsList) {
            product.cancelPurchase();
        }
    }

    // Return the number of times a product appears in the basket
    public int getProductCount(int productId) {
        int count = 0;

        for (Product product : productsList)
            if (product.getProductId() == productId)
                count++;

        return count;
    }

    /**
     * Resets the product to price to amount mapping in the shopping basket.
     * This method iterates through the product list and updates the mapping
     * based on the product ID, price, and quantity.
     * @throws StockMarketException 
     */
    public void resetProductToPriceToAmount() throws StockMarketException {
        _productToPriceToAmount = new HashMap<>();

        for (Product product : productsList) {
            double price = product.getPrice();
            if (!_productToPriceToAmount.containsKey(product.getProductId()))
                _productToPriceToAmount.put(product.getProductId(), new TreeMap<>());
            if (!_productToPriceToAmount.get(product.getProductId()).containsKey(price))
                _productToPriceToAmount.get(product.getProductId()).put(price, 0);

            int oldAmount = _productToPriceToAmount.get(product.getProductId()).get(price);
            _productToPriceToAmount.get(product.getProductId()).put(price, oldAmount + 1);
        }
    }

    // Clone the shopping basket, using for the clone method when finich order
    @Override
    public ShoppingBasket clone() {
        try {
            ShoppingBasket cloned = (ShoppingBasket) super.clone();
            cloned.shop = this.shop;
            cloned.productsList = new ArrayList<>(productsList);
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

    // Print all products in the basket
    public String printAllProducts() {
        StringBuilder sb = new StringBuilder();
        for (Product product : productsList) {
            try {
                sb.append(product.toString());
            } catch (Exception e) {
                return "Error while printAllProduct: " + e.getMessage();
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public SortedMap<Double, Integer> getProductPriceToAmount(Integer productId) {
        return _productToPriceToAmount.get(productId);
    }

    public void setProductPriceToAmount(SortedMap<Double, Integer> map, Integer productId) {
        _productToPriceToAmount.remove(productId);
        _productToPriceToAmount.put(productId, map);
    }

    @Override
    public String toString() {
        return "ShoppingBasket{" +
                "ShopId=" + shop.getShopId() +
                ", products=" + printAllProducts() +
                '}';
    }

    public boolean isEmpty() {
        return this.productsList.isEmpty(); 
    }
    
    // getters and setters
    
    public int getShopId() {
        return shop.getShopId();
    }

    public Shop getShop(){
        return shop;
    }

    public String getShopBankDetails() {
        return shop.getBankDetails();
    }

    public String getShopAddress() {
        return shop.getShopAddress();
    }

    public Integer getShoppingBasketId() {
        return shoppingBasketId;
    }

    public Object getId() {
        return shoppingBasketId;
    }

    public void setId(int id) {
        this.shoppingBasketId = id;
    }

    public void setProductsList(List<Product> productsList) {
        this.productsList = productsList;
    }
}
