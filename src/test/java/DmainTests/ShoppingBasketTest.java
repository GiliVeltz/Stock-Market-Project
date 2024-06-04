package DmainTests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Domain.Product;
import Domain.Shop;
import Domain.ShoppingBasket;
import Domain.User;
import Exceptions.PermissionException;
import Exceptions.ProdcutPolicyException;
import Exceptions.ProductAlreadyExistsException;
import Exceptions.ShopPolicyException;
import Exceptions.StockMarketException;
import enums.Category;

public class ShoppingBasketTest {
    
    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testPurchaseBasket_whenBasketMeetsShopPolicyAndEverythingInStock_thenReturnedTrue() throws ProdcutPolicyException,
     ShopPolicyException, ProductAlreadyExistsException, PermissionException, StockMarketException {
        // Arrange
        Date date = new Date();
        date.setTime(0);
        User buyer = new User("username1", "password1", "email1", date);
        Shop shop = new Shop(1, "ownerUsername", "bank1", "address1");
        ShoppingBasket shoppingBasket = new ShoppingBasket(shop);
        Product product = new Product("product1", Category.ELECTRONICS, 100.0);
        product.updateProductQuantity(3);
        shop.addProductToShop("ownerUsername", product);
        Product product2 = new Product("product2", Category.ELECTRONICS, 100.0);
        product2.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product2);
        shoppingBasket.addProductToShoppingBasket(buyer, product.getProductId());
        shoppingBasket.addProductToShoppingBasket(buyer, product.getProductId());
        shoppingBasket.addProductToShoppingBasket(buyer, product.getProductId());
        shoppingBasket.addProductToShoppingBasket(buyer, product2.getProductId());

        // Act
        boolean result = shoppingBasket.purchaseBasket();
        
        // Assert
        assertEquals(result, true);
        assertEquals(product.getProductQuantity(), 0);
        assertEquals(product2.getProductQuantity(), 9);
    }

    @Test
    public void testPurchaseBasket_whenBasketMeetsShopPolicyAndFirstProductInStockSecondCompletlyNotInStock_thenReturnedFalseAndRestockFirstProduct() throws ProdcutPolicyException,
     ShopPolicyException, ProductAlreadyExistsException, PermissionException, StockMarketException {
        // Arrange
        Date date = new Date();
        date.setTime(0);
        User buyer = new User("username1", "password1", "email1", date);
        Shop shop = new Shop(1, "ownerUsername", "bank1", "address1");
        ShoppingBasket shoppingBasket = new ShoppingBasket(shop);
        Product product = new Product("product1", Category.ELECTRONICS, 100.0);
        product.updateProductQuantity(3);
        shop.addProductToShop("ownerUsername", product);
        Product product2 = new Product("product2", Category.ELECTRONICS, 100.0);
        product2.updateProductQuantity(0);
        shop.addProductToShop("ownerUsername", product2);
        shoppingBasket.addProductToShoppingBasket(buyer, product.getProductId());
        shoppingBasket.addProductToShoppingBasket(buyer, product.getProductId());
        shoppingBasket.addProductToShoppingBasket(buyer, product.getProductId());
        shoppingBasket.addProductToShoppingBasket(buyer, product2.getProductId());

        // Act
        boolean result = shoppingBasket.purchaseBasket();
        
        // Assert
        assertEquals(result, false);
        assertEquals(product.getProductQuantity(), 3);
        assertEquals(product2.getProductQuantity(), 0);
    }

    @Test
    public void testPurchaseBasket_whenBasketMeetsShopPolicyAndFirstProductInStockSecondSomeInStock_thenReturnedFalseAndRestockProducts() throws ProdcutPolicyException,
     ShopPolicyException, ProductAlreadyExistsException, PermissionException, StockMarketException {
        // Arrange
        Date date = new Date();
        date.setTime(0);
        User buyer = new User("username1", "password1", "email1", date);
        Shop shop = new Shop(1, "ownerUsername", "bank1", "address1");
        ShoppingBasket shoppingBasket = new ShoppingBasket(shop);
        Product product = new Product("product1", Category.ELECTRONICS, 100.0);
        product.updateProductQuantity(3);
        shop.addProductToShop("ownerUsername", product);
        Product product2 = new Product("product2", Category.ELECTRONICS, 100.0);
        product2.updateProductQuantity(1);
        shop.addProductToShop("ownerUsername", product2);
        shoppingBasket.addProductToShoppingBasket(buyer, product.getProductId());
        shoppingBasket.addProductToShoppingBasket(buyer, product.getProductId());
        shoppingBasket.addProductToShoppingBasket(buyer, product.getProductId());
        shoppingBasket.addProductToShoppingBasket(buyer, product2.getProductId());
        shoppingBasket.addProductToShoppingBasket(buyer, product2.getProductId());
        shoppingBasket.addProductToShoppingBasket(buyer, product2.getProductId());

        // Act
        boolean result = shoppingBasket.purchaseBasket();
        
        // Assert
        assertEquals(result, false);
        assertEquals(product.getProductQuantity(), 3);
        assertEquals(product2.getProductQuantity(), 1);
    }
    
}
