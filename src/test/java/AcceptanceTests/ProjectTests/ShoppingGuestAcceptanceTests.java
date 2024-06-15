package AcceptanceTests.ProjectTests;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

import AcceptanceTests.Implementor.BridgeInterface;
import org.junit.jupiter.api.Disabled;
import AcceptanceTests.Implementor.RealBridge;

@ExtendWith(RealBridge.class)

public class ShoppingGuestAcceptanceTests {

    // Fields.
    private BridgeInterface _bridge;

    // constructor.
    public ShoppingGuestAcceptanceTests(RealBridge bridge) {
        _bridge = bridge;
    }
    
    // Test get information about a shop as a guest in the system.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testGetShopInfoAsGuest() {
        assertTrue(_bridge.testGetShopInfoAsGuest("shopId1") ); // success
        assertFalse(_bridge.testGetShopInfoAsGuest("shopId2") ); // fail
    }
    
    // Test search product information according to product name as a guest in the system.
    // TODO: GILI
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testGetProductInfoUsingProductNameAsGuest() {
        assertTrue(_bridge.testGetProductInfoUsingProductNameAsGuest("productId1") ); // success - exist product
        assertFalse(_bridge.testGetProductInfoUsingProductNameAsGuest("productId2") ); // fail - non exist product
    }
    
    // Test search product information according to product category as a guest in the system.
    // TODO: GILI
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testGetProductInfoUsingProductCategoryAsGuest() {
        assertTrue(_bridge.testGetProductInfoUsingProductCategoryAsGuest("caterogy1") ); // success - exist category
        assertFalse(_bridge.testGetProductInfoUsingProductCategoryAsGuest("caterogy2") ); // fail - non exist category
    }
    
    // Test search product information according to key words as a guest in the system.
    // TODO: GILI
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testGetProductInfoUsingKeyWordsAsGuest() {
        assertTrue(_bridge.testGetProductInfoUsingKeyWordsAsGuest("word1") ); // success - exist key word
        assertTrue(_bridge.testGetProductInfoUsingKeyWordsAsGuest("word1", "word2") ); // success - one key word exist and one not
        assertFalse(_bridge.testGetProductInfoUsingKeyWordsAsGuest("word2") ); // fail - non exist key word
    }
    
    // TODO: VERSION 2: add tests for filter out products by there price range, rating, category, and store rating.
    // TODO: GILI
    
    // Test search product information in a specific shop, according to product name as a guest in the system.
    // TODO: GILI
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testGetProductInfoUsingProductNameInShopAsGuest() {
        assertTrue(_bridge.testGetProductInfoUsingProductNameInShopAsGuest("productId1", "shopId1") ); // success - exist product and exist shop
        assertFalse(_bridge.testGetProductInfoUsingProductNameInShopAsGuest("productId2", "shopId1") ); // fail - non exist product but exist shop
        assertFalse(_bridge.testGetProductInfoUsingProductNameInShopAsGuest("productId1", "shopId2") ); // fail - exist product but non exist shop
        assertFalse(_bridge.testGetProductInfoUsingProductNameInShopAsGuest("productId2", "shopId2") ); // fail - non exist product and non exist shop
    }
    
    // Test search product information in a specific shop, according to product category as a guest in the system.
    // TODO: GILI
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testGetProductInfoUsingProductCategoryInShopAsGuest() {
        assertTrue(_bridge.testGetProductInfoUsingProductCategoryInShopAsGuest("caterogy1", "shopId1") ); // success - exist category and exist shop
        assertFalse(_bridge.testGetProductInfoUsingProductCategoryInShopAsGuest("caterogy2", "shopId1") ); // fail - non exist category but exist shop
        assertFalse(_bridge.testGetProductInfoUsingProductCategoryInShopAsGuest("caterogy1", "shopId2") ); // fail - exist category but non exist shop
        assertFalse(_bridge.testGetProductInfoUsingProductCategoryInShopAsGuest("caterogy2", "shopId2") ); // fail - non exist category and non exist shop
    }
    
    // Test search product information in a specific shop, according to key words as a guest in the system.
    // TODO: GILI
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testGetProductInfoUsingKeyWordsInShopAsGuest() {
        assertTrue(_bridge.testGetProductInfoUsingKeyWordsInShopAsGuest("keyword1", "shopId1") ); // success - exist keyword and exist shop
        assertFalse(_bridge.testGetProductInfoUsingKeyWordsInShopAsGuest("keyword2", "shopId1") ); // fail - non exist keyword but exist shop
        assertFalse(_bridge.testGetProductInfoUsingKeyWordsInShopAsGuest("keyword1", "shopId2") ); // fail - exist keyword but non exist shop
        assertFalse(_bridge.testGetProductInfoUsingKeyWordsInShopAsGuest("keyword2", "shopId2") ); // fail - non exist keyword and non exist shop
    }
    
    // Test a guest can watch his items in the shopping cart.
    @Test
    public void testCheckAndViewItemsInShoppingCartAsGuest() {
        assertTrue(_bridge.testCheckAndViewItemsInShoppingCartAsGuest("seccess") ); // success
        assertFalse(_bridge.testCheckAndViewItemsInShoppingCartAsGuest("fail") ); // fail
    }
    
    // Test the buying senerio of a shopping cart (all or nothing).
    // TODO: TAL
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testBuyingShoppingCartAsGuest() {
        assertTrue(_bridge.testCheckAllOrNothingBuyingShoppingCartGuest() ); // success - all products a re available to buy them
        assertFalse(_bridge.testCheckAllOrNothingBuyingShoppingCartGuest() ); // fail - one of the pruducts (or more) is not available
        assertTrue(_bridge.testBuyingShoppingCartPoliciesGuest() ); // success - all shop policies are valid
        assertFalse(_bridge.testBuyingShoppingCartPoliciesGuest() ); // fail - one of the shop policies (or more) is not available
    }
}
