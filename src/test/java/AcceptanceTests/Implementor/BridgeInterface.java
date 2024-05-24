package AcceptanceTests.Implementor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public interface BridgeInterface {
    
    // SYSTEM TESTS --------------------------------------------------------------------------------------------------------------------------------------------------------------
    
    @Test
    boolean testOpenMarketSystem(String username);

    @Test
    boolean testPayment(String senario);

    @Test
    boolean testShipping(String senario);

    // GUEST TESTS --------------------------------------------------------------------------------------------------------------------------------------------------------------
    
    @Test
    boolean TestGuestEnterTheSystem(String shouldSeccess);
    
    @Test
    boolean TestGuestRegisterToTheSystem(String username, String password, String email);
    
    @Test
    boolean TestUserEnterTheSystem(String SystemStatus);
    
    @Test
    boolean testLoginToTheSystem(String username, String password);

    // SHOPPING GUEST TESTS --------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Test
    boolean testGetShopInfoAsGuest(String shopId);
    
    @Test
    boolean testGetProductInfoAsGuest(String productId);

    @Test
    boolean testGetProductInfoUsingProductNameAsGuest(String productId);
    
    @Test
    boolean testGetProductInfoUsingProductCategoryAsGuest(String category);
    
    @Test
    boolean testGetProductInfoUsingKeyWordsAsGuest(String kewWord);
    
    @Test
    boolean testGetProductInfoUsingKeyWordsAsGuest(String kewWord1, String kewWord2);

    @Test
    boolean testGetProductInfoUsingProductNameInShopAsGuest(String productId, String shopId);
    
    @Test
    boolean testGetProductInfoUsingProductCategoryInShopAsGuest(String category, String shopId);
    
    @Test
    boolean testGetProductInfoUsingKeyWordsInShopAsGuest(String kewWord, String shopId);

    @Test
    boolean testAddProductToShoppingCartAsGuest(String productId);
    
    @Test
    boolean testCheckAndViewItemsInShoppingCartAsGuest();

    @Test
    boolean testCheckAllOrNothingBuyingShoppingCartGuest();
    
    @Test
    boolean testBuyingShoppingCartPoliciesGuest();

    // SHOPPING USER TESTS --------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Test
    boolean testGetShopInfoAsUser(String shopId);
    
    @Test
    boolean testGetProductInfoAsUser(String productId);
    
    @Test
    boolean testGetProductInfoUsingProductNameAsUser(String productId);

    @Test
    boolean testGetProductInfoUsingProductCategoryAsUser(String category);
    
    @Test
    boolean testGetProductInfoUsingKeyWordsAsUser(String keyWord);
    
    @Test
    boolean testGetProductInfoUsingKeyWordsAsUser(String keyWord1, String keyWord2);

    @Test
    boolean testGetProductInfoUsingProductNameInShop(String productId, String shopId);
    
    @Test
    boolean testGetProductInfoUsingProductNameInShopAsUser(String productId, String shopId);
    
    @Test
    boolean testGetProductInfoUsingProductCategoryInShopAsUser(String category, String shopId);
    
    @Test
    boolean testGetProductInfoUsingKeyWordsInShopAsUser(String keyWord1, String shopId);

    @Test
    boolean testAddProductToShoppingCartAsUser(String productId);
    
    @Test
    boolean testCheckAndViewItemsInShoppingCartAsUser();
    
    @Test
    boolean testCheckAllOrNothingBuyingShoppingCartUser(String username, String password);
    
    @Test
    boolean testBuyingShoppingCartPoliciesUser(String username, String password);

    @Test
    boolean testLogoutToTheSystem(String username);
    
    @Test
    boolean TestWhenUserLogoutThenHisCartSaved(String username);
    
    @Test
    boolean TestWhenUserLogoutThenHeBecomeGuest(String username);
    
    @Test
    boolean TestUserOpenAShop(String username, String password, String shopName);
    
    @Test
    boolean TestUserIsFounderOfTheShop(String username, String password, String shopName);

    @Test
    boolean TestUserWriteReviewOnPurchasedProduct(String username, String password, String productId);

    @Test
    boolean TestUserRatingPurchasedProduct(String username, String password, String productId, String score);
    
    @Test
    boolean TestUserRatingShopHePurchasedFrom(String username, String password, String shopId, String score);
    
    @Test
    boolean TestUserMessagingShopHePurchasedFrom(String username, String password, String Id, String message);

    @Test
    boolean TestUserReportSystemManagerOnBreakingIntegrityRules(String username, String password, String message);
    
    @Test
    boolean TestUserViewHistoryPurchaseList(String username, String password);
    
    @Test
    boolean TestUserViewHistoryPurchaseListWhenProductRemovedFromSystem(String username, String password, String productId);

    @Test
    boolean TestUserViewHistoryPurchaseListWhenShopRemovedFromSystem(String username, String password, String shopId);
    
    @Test
    boolean TestUserViewPrivateDetails(String username, String password);
    
    @Test
    boolean TestUserEditEmail(String username, String password, String newEmail);

    @Test
    boolean TestUserEditPassword(String username, String newPassword, String email);
    
    @Test
    boolean TestUserEditUsername(String newName, String newPassword, String email);

    // STORE OWNER TESTS --------------------------------------------------------------------------------------------------------------------------------------------------------------
    
    @Test
    boolean testShopOwnerAddProductToShop(String username, String shopId, String productName, String productAmount);
    
    @Test
    boolean testShopOwnerRemoveProductFromShop(String username, String shopId, String productName);
    
    @Test
    boolean testShopOwnerEditProductInShop(String username, String shopId, String productName, String productNameNew, String productAmount, String productAmountNew);
    
    @Test
    boolean testShopOwnerChangeShopPolicies(String username, String shopId, String newPolicy);
    
    @Test
    boolean testShopOwnerAppointAnotherShopOwner(String username, String shopId, String newOwnerUsername);
    
    @Test
    boolean testShopOwnerAppointAnotherShopManager(String username, String shopId, String newManagerUsername);
    
    @Test
    boolean testShopOwnerAddShopManagerPermission(String username, String shopId, String managerUsername, String permission);
    
    @Test
    boolean testShopOwnerRemoveShopManagerPermission(String username, String shopId, String managerUsername, String permission);
    
    @Test
    boolean testShopOwnerCloseShop(String username, String shopId);
    
    @Test
    boolean testShopOwnerGetShopInfo(String username, String shopId);
    
    @Test
    boolean testShopOwnerGetShopManagersPermissions(String username, String shopId);
    
    @Test
    boolean testShopOwnerViewHistoryPurcaseInShop(String username, String shopId);
    
    // STORE MANAGER TESTS --------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Test
    boolean testOpenMarketSystem(String username, String shopId, String permission);

    // SYSTEM ADMIN TESTS --------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Test
    boolean testSystemManagerViewHistoryPurcaseInUsers(String namanger, String shopId);

    @Test
    boolean testSystemManagerViewHistoryPurcaseInShops(String namanger, String shopId);

    // SHOPPING CART TESTS --------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Test
    boolean testAddProductToShoppingCartUser(String username, String productId, String shopId);

    @Test
    boolean testAddProductToShoppingCartGuest(String username, String productId, String shopId);
}