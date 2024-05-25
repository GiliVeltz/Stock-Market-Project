package AcceptanceTests.ProjectTests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Disabled;

import AcceptanceTests.Implementor.BridgeInterface;
import AcceptanceTests.Implementor.RealBridge;

@ExtendWith(RealBridge.class)
public class ShoppingUserAcceptanceTests{

    // Fields.
    private BridgeInterface _bridge;

    // constructor.
    public ShoppingUserAcceptanceTests(RealBridge bridge) {
        _bridge = bridge;
    }

    @BeforeEach
    public void setUp() {
        _bridge.init(); // Ensure mocks are initialized
    }
    
    // Test get information about a shop as a User in the system.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testGetShopInfoAsUser() {
        assertTrue(_bridge.testGetShopInfoAsUser("shopId1") ); // success
        assertFalse(_bridge.testGetShopInfoAsUser("shopId2") ); // fail
    }
    
    // Test get information about a product as a User in the system.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testGetProductInfoAsUser() {
        assertTrue(_bridge.testGetProductInfoAsUser("productId1") ); // success - exist product
        assertFalse(_bridge.testGetProductInfoAsUser("productId2") ); // fail - non exist product
    }
    
    // Test search product information according to product name as a User in the system.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testGetProductInfoUsingProductNameAsUser() {
        assertTrue(_bridge.testGetProductInfoUsingProductNameAsUser("productId1") ); // success - exist product
        assertFalse(_bridge.testGetProductInfoUsingProductNameAsUser("productId2") ); // fail - non exist product
    }
    
    // Test search product information according to product category as a User in the system.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testGetProductInfoUsingProductCategoryAsUser() {
        assertTrue(_bridge.testGetProductInfoUsingProductCategoryAsUser("caterogy1") ); // success - exist category
        assertFalse(_bridge.testGetProductInfoUsingProductCategoryAsUser("caterogy2") ); // fail - non exist category
    }
    
    // Test search product information according to key words as a User in the system.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testGetProductInfoUsingKeyWordsAsUser() {
        assertTrue(_bridge.testGetProductInfoUsingKeyWordsAsUser("word1") ); // success - exist key word
        assertTrue(_bridge.testGetProductInfoUsingKeyWordsAsUser("word1", "word2") ); // success - one key word exist and one not
        assertFalse(_bridge.testGetProductInfoUsingKeyWordsAsUser("word2") ); // fail - non exist key word
    }
    
    // TODO: VERSION 2: add tests for filter out products by there price range, rating, category, and store rating.
    
    // Test search product information in a specific shop, according to product name as a User in the system.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testGetProductInfoUsingProductNameInShopAsUser() {
        assertTrue(_bridge.testGetProductInfoUsingProductNameInShopAsUser("productId1", "shopId1") ); // success - exist product and exist shop
        assertFalse(_bridge.testGetProductInfoUsingProductNameInShopAsUser("productId2", "shopId1") ); // fail - non exist product but exist shop
        assertFalse(_bridge.testGetProductInfoUsingProductNameInShopAsUser("productId1", "shopId2") ); // fail - exist product but non exist shop
        assertFalse(_bridge.testGetProductInfoUsingProductNameInShopAsUser("productId2", "shopId2") ); // fail - non exist product and non exist shop
    }
    
    // Test search product information in a specific shop, according to product category as a User in the system.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testGetProductInfoUsingProductCategoryInShopAsUser() {
        assertTrue(_bridge.testGetProductInfoUsingProductCategoryInShopAsUser("caterogy1", "shopId1") ); // success - exist category and exist shop
        assertFalse(_bridge.testGetProductInfoUsingProductCategoryInShopAsUser("caterogy2", "shopId1") ); // fail - non exist category but exist shop
        assertFalse(_bridge.testGetProductInfoUsingProductCategoryInShopAsUser("caterogy1", "shopId2") ); // fail - exist category but non exist shop
        assertFalse(_bridge.testGetProductInfoUsingProductCategoryInShopAsUser("caterogy2", "shopId2") ); // fail - non exist category and non exist shop
    }
    
    // Test search product information in a specific shop, according to key words as a User in the system.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testGetProductInfoUsingKeyWordsInShopAsUser() {
        assertTrue(_bridge.testGetProductInfoUsingKeyWordsInShopAsUser("keyword1", "shopId1") ); // success - exist keyword and exist shop
        assertFalse(_bridge.testGetProductInfoUsingKeyWordsInShopAsUser("keyword2", "shopId1") ); // fail - non exist keyword but exist shop
        assertFalse(_bridge.testGetProductInfoUsingKeyWordsInShopAsUser("keyword1", "shopId2") ); // fail - exist keyword but non exist shop
        assertFalse(_bridge.testGetProductInfoUsingKeyWordsInShopAsUser("keyword2", "shopId2") ); // fail - non exist keyword and non exist shop
    }
    
    // Test when add product to shopping cart- it stays there as a User in the system.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testAddProductToShoppingCartAsUser() {
        assertTrue(_bridge.testAddProductToShoppingCartAsUser("productId1") ); // success
        assertFalse(_bridge.testAddProductToShoppingCartAsUser("productId2") ); // fail
    }
    
    // Test a User can watch his items in the shopping cart as a User in the system.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testCheckAndViewItemsInShoppingCartAsUser() {
        assertTrue(_bridge.testCheckAndViewItemsInShoppingCartAsUser() ); // success
        assertFalse(_bridge.testCheckAndViewItemsInShoppingCartAsUser() ); // fail
    }
    
    // Test the buying senerio of a shopping cart (all or nothing) as a User in the system.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testBuyingShoppingCartAsUser() {
        assertTrue(_bridge.testCheckBuyingShoppingCartUser("bob","1 4 5 6 7","Visa","Israel") ); // success - all products are available to buy them
        assertFalse(_bridge.testCheckBuyingShoppingCartUser("Tomer","1 8 9","Cal","Israel") ); // fail - one of the pruducts (or more) is not available
        assertTrue(_bridge.testBuyingShoppingCartPoliciesUser("bob","bobspassword") ); // success - all shop policies are valid
        assertFalse(_bridge.testBuyingShoppingCartPoliciesUser("bob","bobspassword") ); // fail - one of the shop policies (or more) is not available
    }

    // Test if the user can logout from the system.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void TestUserLogout() {
        assertTrue(_bridge.testLogoutToTheSystem("Bob") ); // success
        assertFalse(_bridge.testLogoutToTheSystem("notUsername")); // not a user in the system
    }
    
    // Test if the user logouts from the system - his shopping cart we saved in the system.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void TestWhenUserLogoutThenHisCartSaved() {
        assertTrue(_bridge.TestWhenUserLogoutThenHisCartSaved("username") ); // success - his shopping cart saved
        assertFalse(_bridge.TestWhenUserLogoutThenHisCartSaved("username") ); // fail - his shopping cart not saved
    }
    
    // Test if the user logouts from the system - he become a guest in the system.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void TestWhenUserLogoutThenHeBecomeGuest() {
        assertTrue(_bridge.TestWhenUserLogoutThenHeBecomeGuest("username") ); // success - user logged out and become guest
        assertFalse(_bridge.TestWhenUserLogoutThenHeBecomeGuest("username") ); // fail
    }
    
    // Test that a user can open a shop and be the founder of the shop.
    // @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void TestUserOpenAShop() {
        assertTrue(_bridge.TestUserOpenAShop("Bob","bobspassword", "5555", "Vias", "Israel") ); // success - user open a shop
        assertFalse(_bridge.TestUserOpenAShop("Tom","bobspassword", "879", "MasterCard", "USA") ); // fail - user is a guest
        assertFalse(_bridge.TestUserOpenAShop("Ron","bobspassword", "879", "Cal", "Spain") ); // fail - the shop name is already exist
    }
    
    // Test that a user can open write a review about the product he purchased.
    @Disabled("FOR VERSOIN 2 ~ This test is disabled cuase needs to implement in real bridge")
    @Test
    public void TestUserWriteReviewOnPurchasedProduct() {
        assertTrue(_bridge.TestUserWriteReviewOnPurchasedProduct("bob","bobspassword", "product1") ); // success - the user secceeded to write a review
        assertFalse(_bridge.TestUserWriteReviewOnPurchasedProduct("bob","bobspassword", "product2") ); // fail - the user did not porchased this product
    }
    
    // Test that a user can rate a product he purchased.
    @Disabled("FOR VERSOIN 2 ~ This test is disabled cuase needs to implement in real bridge")
    @Test
    public void TestUserRatingPurchasedProduct() {
        assertTrue(_bridge.TestUserRatingPurchasedProduct("bob","bobspassword", "product1", "score1") ); // success - the user secceeded to rate the product
        assertFalse(_bridge.TestUserRatingPurchasedProduct("bob","bobspassword", "product1", "score2") ); // fail - the score in invalid
    }
    
    // Test that a user can rate a shop he purchased from.
    @Disabled("FOR VERSOIN 2 ~ This test is disabled cuase needs to implement in real bridge")
    @Test
    public void TestUserRatingShopHePurchasedFrom() {
        assertTrue(_bridge.TestUserRatingShopHePurchasedFrom("bob","bobspassword", "shop1", "score1") ); // success - the user secceeded to rate the shop
        assertFalse(_bridge.TestUserRatingShopHePurchasedFrom("bob","bobspassword", "shop1", "score2") ); // fail - the score in invalid
    }
    
    // Test that a user can send messages to the shop the purchased from about his orders.
    @Disabled("FOR VERSOIN 2 ~ This test is disabled cuase needs to implement in real bridge")
    @Test
    public void TestUserMessagingShopHePurchasedFrom() {
        assertTrue(_bridge.TestUserMessagingShopHePurchasedFrom("bob","bobspassword", "shop1", "message1") ); // success - the user secceeded to send the message
        assertFalse(_bridge.TestUserMessagingShopHePurchasedFrom("bob","bobspassword", "shop1", "message1") ); // fail - the score in invalid
        assertFalse(_bridge.TestUserMessagingShopHePurchasedFrom("bob","bobspassword", "shop2", "message1") ); // fail - the user didnot purchased from this shop
    }
    
    // Test that a user can report the system manager in case of breaking the integrity rules.
    @Disabled("FOR VERSOIN 2 ~ This test is disabled cuase needs to implement in real bridge")
    @Test
    public void TestUserReportSystemManagerOnBreakingIntegrityRules() {
        assertTrue(_bridge.TestUserReportSystemManagerOnBreakingIntegrityRules("bob","bobspassword", "message1") ); // success - the user secceeded to send the message
        assertFalse(_bridge.TestUserReportSystemManagerOnBreakingIntegrityRules("bob","bobspassword", "message1") ); // fail - the message in invalid
    }
    
    // Test that a user can see his own history shopping orders.
    @Disabled("FOR VERSOIN 2 ~ This test is disabled cuase needs to implement in real bridge")
    @Test
    public void TestUserViewHistoryPurchaseList() {
        assertTrue(_bridge.TestUserViewHistoryPurchaseList("bob","bobspassword") ); // success - the user secceeded to see his history purchased list
        assertTrue(_bridge.TestUserViewHistoryPurchaseListWhenProductRemovedFromSystem("bob","bobspassword", "product1") ); // success - the product exsist in the history purchased list
        assertTrue(_bridge.TestUserViewHistoryPurchaseListWhenShopRemovedFromSystem("bob","bobspassword", "shop1") ); // success - the shop products exsist in the history purchased list
    }
    
    // Test that a user can see his own private details.
    @Disabled("FOR VERSOIN 2 ~ This test is disabled cuase needs to implement in real bridge")
    @Test
    public void TestUserViewPrivateDetails() {
        assertTrue(_bridge.TestUserViewPrivateDetails("bob","bobspassword") ); // success - the user secceeded to see his private details
        assertFalse(_bridge.TestUserViewPrivateDetails("dad","dadspassword") ); // fail - the user did not exsist in the system
    }
    
    
    // Test that a user can edit his own private details.
    @Disabled("FOR VERSOIN 2 ~ This test is disabled cuase needs to implement in real bridge")
    @Test
    public void TestUserEditPrivateDetails() {
        assertTrue(_bridge.TestUserEditEmail("bob","bobspassword", "newEmail") ); // success - the user secceeded to edit his email
        assertTrue(_bridge.TestUserEditPassword("bob","newPassword", "email") ); // success - the user secceeded to edit his password
        assertFalse(_bridge.TestUserEditUsername("newName","bobspassword", "email") ); // fail - the user can not change his user name in the system
    }
}
