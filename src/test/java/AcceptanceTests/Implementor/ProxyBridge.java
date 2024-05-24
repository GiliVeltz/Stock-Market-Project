package AcceptanceTests.Implementor;

// Proxy is a structural design pattern that lets you provide a substitute or placeholder for another object.
// A proxy controls access to the original object, allowing you to perform something either before or after the request gets through to the original object.
public class ProxyBridge implements BridgeInterface{
    
    // IMPORTANT: all the functions will return false because the real implementation is in the RealBridge class

    @Override
    public boolean testOpenMarketSystem(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testOpenMarketSystem' in ProxyBridge class");
    }

    @Override
    public boolean testPayment(String senario) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testPayment' in ProxyBridge class");
    }

    @Override
    public boolean testShipping(String senario) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testShipping' in ProxyBridge class");
    }

    @Override
    public boolean TestGuestEnterTheSystem(String shouldSeccess) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestGuestEnterTheSystem' in ProxyBridge class"); 
    }

    @Override
    public boolean TestGuestRegisterToTheSystem(String username, String password, String email) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestGuestRegisterToTheSystem' in ProxyBridge class");
    }

    @Override
    public boolean TestUserEnterTheSystem(String SystemStatus) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestUserEnterTheSystem' in ProxyBridge class");
    }

    @Override
    public boolean testLoginToTheSystem(String username, String password) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testLoginToTheSystem' in ProxyBridge class");
    }

    @Override
    public boolean testGetShopInfoAsGuest(String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetShopInfoAsGuest' in ProxyBridge class");
    }

    @Override
    public boolean testGetProductInfoAsGuest(String productId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoAsGuest' in ProxyBridge class");
    }

    @Override
    public boolean testGetProductInfoUsingProductNameAsGuest(String productId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingProductNameAsGuest' in ProxyBridge class");
    }

    @Override
    public boolean testGetProductInfoUsingProductCategoryAsGuest(String category) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingProductCategoryAsGuest' in ProxyBridge class");
    }

    @Override
    public boolean testGetProductInfoUsingKeyWordsAsGuest(String kewWord) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingKeyWordsAsGuest' in ProxyBridge class");
    }

    @Override
    public boolean testGetProductInfoUsingKeyWordsAsGuest(String kewWord1, String kewWord2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingKeyWordsAsGuest' in ProxyBridge class");
    }

    @Override
    public boolean testGetProductInfoUsingProductNameInShopAsGuest(String productId, String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingProductNameInShopAsGuest' in ProxyBridge class");
    }

    @Override
    public boolean testGetProductInfoUsingProductCategoryInShopAsGuest(String category, String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingProductCategoryInShopAsGuest' in ProxyBridge class");
    }

    @Override
    public boolean testGetProductInfoUsingKeyWordsInShopAsGuest(String kewWord, String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingKeyWordsInShopAsGuest' in ProxyBridge class");
    }

    @Override
    public boolean testAddProductToShoppingCartAsGuest(String productId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testAddProductToShoppingCartAsGuest' in ProxyBridge class");
    }

    @Override
    public boolean testCheckAndViewItemsInShoppingCartAsGuest() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testCheckAndViewItemsInShoppingCartAsGuest' in ProxyBridge class");
    }

    @Override
    public boolean testCheckAllOrNothingBuyingShoppingCartGuest() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testCheckAllOrNothingBuyingShoppingCartGuest' in ProxyBridge class");
    }

    @Override
    public boolean testBuyingShoppingCartPoliciesGuest() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testBuyingShoppingCartPoliciesGuest' in ProxyBridge class");
    }

    @Override
    public boolean testGetShopInfoAsUser(String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetShopInfoAsUser' in ProxyBridge class");
    }

    @Override
    public boolean testGetProductInfoAsUser(String productId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoAsUser' in ProxyBridge class");
    }

    @Override
    public boolean testGetProductInfoUsingProductNameAsUser(String productId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingProductNameAsUser' in ProxyBridge class");
    }

    @Override
    public boolean testGetProductInfoUsingProductCategoryAsUser(String category) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingProductCategoryAsUser' in ProxyBridge class");
    }

    @Override
    public boolean testGetProductInfoUsingKeyWordsAsUser(String keyWord) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingKeyWordsAsUser' in ProxyBridge class");
    }

    @Override
    public boolean testGetProductInfoUsingKeyWordsAsUser(String keyWord1, String keyWord2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingKeyWordsAsUser' in ProxyBridge class");
    }

    @Override
    public boolean testGetProductInfoUsingProductNameInShop(String productId, String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingProductNameInShop' in ProxyBridge class");
    }

    @Override
    public boolean testGetProductInfoUsingProductNameInShopAsUser(String productId, String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingProductNameInShopAsUser' in ProxyBridge class");
    }

    @Override
    public boolean testGetProductInfoUsingProductCategoryInShopAsUser(String category, String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingProductCategoryInShopAsUser' in ProxyBridge class");
    }

    @Override
    public boolean testGetProductInfoUsingKeyWordsInShopAsUser(String keyWord1, String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingKeyWordsInShopAsUser' in ProxyBridge class");
    }

    @Override
    public boolean testAddProductToShoppingCartAsUser(String productId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testAddProductToShoppingCartAsUser' in ProxyBridge class");
    }

    @Override
    public boolean testCheckAndViewItemsInShoppingCartAsUser() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testCheckAndViewItemsInShoppingCartAsUser' in ProxyBridge class");
    }

    @Override
    public boolean testCheckBuyingShoppingCartUser(String username, String busketsToBuy, String cardNumber, String address) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testCheckAllOrNothingBuyingShoppingCartUser' in ProxyBridge class");
    }

    @Override
    public boolean testBuyingShoppingCartPoliciesUser(String username, String password) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testBuyingShoppingCartPoliciesUser' in ProxyBridge class");
    }

    @Override
    public boolean testLogoutToTheSystem(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testLogoutToTheSystem' in ProxyBridge class");
    }

    @Override
    public boolean TestWhenUserLogoutThenHisCartSaved(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestWhenUserLogoutThenHisCartSaved' in ProxyBridge class");
    }

    @Override
    public boolean TestWhenUserLogoutThenHeBecomeGuest(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestWhenUserLogoutThenHeBecomeGuest' in ProxyBridge class");
    }

    @Override
    public boolean TestUserOpenAShop(String username, String password, String shopName, String bankDetails, String shopAddress) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestUserOpenAShop' in ProxyBridge class");
    }

    @Override
    public boolean TestUserWriteReviewOnPurchasedProduct(String username, String password, String productId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestUserWriteReviewOnPurchasedProduct' in ProxyBridge class");
    }

    @Override
    public boolean TestUserRatingPurchasedProduct(String username, String password, String productId, String score) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestUserRatingPurchasedProduct' in ProxyBridge class");
    }

    @Override
    public boolean TestUserRatingShopHePurchasedFrom(String username, String password, String shopId, String score) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestUserRatingShopHePurchasedFrom' in ProxyBridge class");
    }

    @Override
    public boolean TestUserMessagingShopHePurchasedFrom(String username, String password, String Id, String message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestUserMessagingShopHePurchasedFrom' in ProxyBridge class");
    }

    @Override
    public boolean TestUserReportSystemManagerOnBreakingIntegrityRules(String username, String password,
            String message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestUserReportSystemManagerOnBreakingIntegrityRules' in ProxyBridge class");
    }

    @Override
    public boolean TestUserViewHistoryPurchaseList(String username, String password) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestUserViewHistoryPurchaseList' in ProxyBridge class");
    }

    @Override
    public boolean TestUserViewHistoryPurchaseListWhenProductRemovedFromSystem(String username, String password,
            String productId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestUserViewHistoryPurchaseListWhenProductRemovedFromSystem' in ProxyBridge class");
    }

    @Override
    public boolean TestUserViewHistoryPurchaseListWhenShopRemovedFromSystem(String username, String password,
            String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestUserViewHistoryPurchaseListWhenShopRemovedFromSystem' in ProxyBridge class");
    }

    @Override
    public boolean TestUserViewPrivateDetails(String username, String password) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestUserViewPrivateDetails' in ProxyBridge class");
    }

    @Override
    public boolean TestUserEditEmail(String username, String password, String newEmail) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestUserEditEmail' in ProxyBridge class");
    }

    @Override
    public boolean TestUserEditPassword(String username, String newPassword, String email) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestUserEditPassword' in ProxyBridge class");
    }

    @Override
    public boolean TestUserEditUsername(String newName, String newPassword, String email) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestUserEditUsername' in ProxyBridge class");
    }

    @Override
    public boolean testShopOwnerAddProductToShop(String username, String shopId, String productName,
            String productAmount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testShopOwnerAddProductToShop' in ProxyBridge class");
    }

    @Override
    public boolean testShopOwnerRemoveProductFromShop(String username, String shopId, String productName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testShopOwnerRemoveProductFromShop' in ProxyBridge class");
    }

    @Override
    public boolean testShopOwnerEditProductInShop(String username, String shopId, String productName,
            String productNameNew, String productAmount, String productAmountNew) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testShopOwnerEditProductInShop' in ProxyBridge class");
    }

    @Override
    public boolean testOpenMarketSystem(String username, String shopId, String permission) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testOpenMarketSystem' in ProxyBridge class");
    }

    @Override
    public boolean testSystemManagerViewHistoryPurcaseInUsers(String namanger, String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testSystemManagerViewHistoryPurcaseInUsers' in ProxyBridge class");
    }

    @Override
    public boolean testSystemManagerViewHistoryPurcaseInShops(String namanger, String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testSystemManagerViewHistoryPurcaseInShops' in ProxyBridge class");
    }

    @Override
    public boolean testAddProductToShoppingCartUser(String username, String productId, String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testAddProductToShoppingCartUser' in ProxyBridge class");
    }

    @Override
    public boolean testAddProductToShoppingCartGuest(String username, String productId, String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testAddProductToShoppingCartGuest' in ProxyBridge class");
    }

    @Override
    public boolean testShopOwnerChangeShopPolicies(String username, String shopId, String newPolicy) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testShopOwnerChangeShopPolicies' in ProxyBridge class");
    }

    @Override
    public boolean testShopOwnerAppointAnotherShopOwner(String username, String shopId, String newOwnerUsername) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testShopOwnerAppointAnotherShopOwner' in ProxyBridge class");
    }

    @Override
    public boolean testShopOwnerAppointAnotherShopManager(String username, String shopId, String newManagerUsername) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testShopOwnerAppointAnotherShopManager' in ProxyBridge class");
    }

    @Override
    public boolean testShopOwnerAddShopManagerPermission(String username, String shopId, String managerUsername,
            String permission) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testShopOwnerAddShopManagerPermission' in ProxyBridge class");
    }

    @Override
    public boolean testShopOwnerRemoveShopManagerPermission(String username, String shopId, String managerUsername,
            String permission) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testShopOwnerRemoveShopManagerPermission' in ProxyBridge class");
    }

    @Override
    public boolean testShopOwnerCloseShop(String username, String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testShopOwnerCloseShop' in ProxyBridge class");
    }

    @Override
    public boolean testShopOwnerGetShopInfo(String username, String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testShopOwnerGetShopInfo' in ProxyBridge class");
    }

    @Override
    public boolean testShopOwnerGetShopManagersPermissions(String username, String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testShopOwnerGetShopManagersPermissions' in ProxyBridge class");
    }

    @Override
    public boolean testShopOwnerViewHistoryPurcaseInShop(String username, String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testShopOwnerViewHistoryPurcaseInShop' in ProxyBridge class");
    }
}
