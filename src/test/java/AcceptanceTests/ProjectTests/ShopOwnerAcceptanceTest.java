package AcceptanceTests.ProjectTests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import AcceptanceTests.Implementor.BridgeInterface;
import AcceptanceTests.Implementor.RealBridge;
import net.bytebuddy.implementation.bind.annotation.IgnoreForBinding;

@ExtendWith(RealBridge.class)

public class ShopOwnerAcceptanceTest {

    // Fields.
    private BridgeInterface _bridge;

    // constructor.
    public ShopOwnerAcceptanceTest(RealBridge bridge) {
        _bridge = bridge;
    }
    
    // Test that shop owner can add products to the shop.
    @IgnoreForBinding
    @Test
    public void testShopOwnerAddProductToShop() {
        assertTrue(_bridge.testShopOwnerAddProductToShop("shopOwnerUserName", "shopId1", "ProductName", "ProductAmount") ); // success
        assertFalse(_bridge.testShopOwnerAddProductToShop("shopOwnerUserName", "shopId1", "ProductName", "inValidAmount") ); // fail - invalid amount
        assertFalse(_bridge.testShopOwnerAddProductToShop("shopOwnerUserName", "shopId1", "ExistProductName", "ProductAmount") ); // fail - already exist product name
        assertFalse(_bridge.testShopOwnerAddProductToShop("shopOwnerUserName", "shopId2", "ExistProductName", "ProductAmount") ); // fail - the user is not the shop onwer
    }
    
    // Test that shop owner can remove products from the shop.
    @IgnoreForBinding
    @Test
    public void testShopOwnerRemoveProductFromShop() {
        assertTrue(_bridge.testShopOwnerRemoveProductFromShop("shopOwnerUserName", "shopId1", "ProductName") ); // success
        assertFalse(_bridge.testShopOwnerRemoveProductFromShop("shopOwnerUserName", "shopId1", "ProductName") ); // fail - invalid productname
        assertFalse(_bridge.testShopOwnerRemoveProductFromShop("shopOwnerUserName", "shopId2", "ExistProductName") ); // fail - the user is not the shop onwer
    }
    
    // Test that shop owner can edit products details in the shop.
    @IgnoreForBinding
    @Test
    public void testShopOwnerEditProductInShop() {
        assertTrue(_bridge.testShopOwnerEditProductInShop("shopOwnerUserName", "shopId1", "ProductName", "newProductName", "ProductAmount", "newProductAmount") ); // success
        assertFalse(_bridge.testShopOwnerEditProductInShop("shopOwnerUserName", "shopId1", "ProductName", "newProductName", "inValidAmount", "newProductAmount") ); // fail - invalid amount
        assertFalse(_bridge.testShopOwnerEditProductInShop("shopOwnerUserName", "shopId1", "ExistProductName", "newProductName", "ProductAmount", "newProductAmount") ); // fail - already exist product name
        assertFalse(_bridge.testShopOwnerEditProductInShop("shopOwnerUserName", "shopId1", "NonProductName", "newProductName", "ProductAmount", "newProductAmount") ); // fail - there is no product with such name in the store
        assertFalse(_bridge.testShopOwnerEditProductInShop("shopOwnerUserName", "shopId2", "ExistProductName", "newProductName", "ProductAmount", "newProductAmount") ); // fail - the user is not the shop onwer
    }
    
    // TODO: NOT FOR VERSION 1
    // Test that shop owner can edit the shop policies.
    @IgnoreForBinding
    @Test
    public void testShopOwnerChangeShopPolicies() {
        // assertTrue(_bridge.testShopOwnerEditProductInShop("shopOwnerUserName", "shopId1", "new shop policy") ); // success
        // assertFalse(_bridge.testShopOwnerEditProductInShop("shopOwnerUserName", "shopId1", "new shop policy") ); // fail - invalid new shop policy
        // assertFalse(_bridge.testShopOwnerEditProductInShop("shopOwnerUserName", "shopId2", "new shop policy") ); // fail - the user is not the shop onwer
    }
}
