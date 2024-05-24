package AcceptanceTests.Implementor;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import Domain.*;
import ServiceLayer.*;

// A real conection to the system.
// The code is tested on the real information on te system.
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RealBridge implements BridgeInterface, ParameterResolver{


    private UserService _userServiceUnderTest;
    @Mock
    private UserFacade _userControllerMock;
    @Mock
    private TokenService _tokenServiceMock;
    @Mock
    private ShoppingCartFacade _shoppingCartFacadeMock;
    
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == RealBridge.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return new RealBridge();
    }

    @BeforeEach
    public void setUp() {
        _userControllerMock = mock(UserFacade.class);
        _tokenServiceMock = mock(TokenService.class);
        _shoppingCartFacadeMock = mock(ShoppingCartFacade.class);
    }

    @Test
    public boolean testRegisterToTheSystem(String username, String password, String email) {
        // Arrange
        String token = "";
        _tokenServiceMock = mock(TokenService.class); // Initialize the mock object
        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        _userServiceUnderTest = new UserService(_userControllerMock, _tokenServiceMock, _shoppingCartFacadeMock);

        // Act
        Response res = _userServiceUnderTest.register(token, username, password, email);

        // Assert
        return res.getErrorMessage() == null;
    }

    @Override
    public boolean testOpenMarketSystem(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testOpenMarketSystem'");
    }

    @Override
    public boolean testPayment(String senario) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testPayment'");
    }

    @Override
    public boolean testShipping(String senario) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testShipping'");
    }

    @Override
    public boolean TestGuestEnterTheSystem(String shouldSeccess) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestGuestEnterTheSystem'");
    }

    @Override
    public boolean TestGuestRegisterToTheSystem(String username, String password, String email) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestGuestRegisterToTheSystem'");
    }

    @Override
    public boolean TestUserEnterTheSystem(String SystemStatus) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestUserEnterTheSystem'");
    }

    @Override
    public boolean testLoginToTheSystem(String username, String password, String email) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testLoginToTheSystem'");
    }

    @Override
    public boolean testGetShopInfoAsGuest(String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetShopInfoAsGuest'");
    }

    @Override
    public boolean testGetProductInfoAsGuest(String productId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoAsGuest'");
    }

    @Override
    public boolean testGetProductInfoUsingProductNameAsGuest(String productId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingProductNameAsGuest'");
    }

    @Override
    public boolean testGetProductInfoUsingProductCategoryAsGuest(String category) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingProductCategoryAsGuest'");
    }

    @Override
    public boolean testGetProductInfoUsingKeyWordsAsGuest(String kewWord) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingKeyWordsAsGuest'");
    }

    @Override
    public boolean testGetProductInfoUsingKeyWordsAsGuest(String kewWord1, String kewWord2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingKeyWordsAsGuest'");
    }

    @Override
    public boolean testGetProductInfoUsingProductNameInShopAsGuest(String productId, String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingProductNameInShopAsGuest'");
    }

    @Override
    public boolean testGetProductInfoUsingProductCategoryInShopAsGuest(String category, String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingProductCategoryInShopAsGuest'");
    }

    @Override
    public boolean testGetProductInfoUsingKeyWordsInShopAsGuest(String kewWord, String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingKeyWordsInShopAsGuest'");
    }

    @Override
    public boolean testAddProductToShoppingCartAsGuest(String productId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testAddProductToShoppingCartAsGuest'");
    }

    @Override
    public boolean testCheckAndViewItemsInShoppingCartAsGuest() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testCheckAndViewItemsInShoppingCartAsGuest'");
    }

    @Override
    public boolean testCheckAllOrNothingBuyingShoppingCartGuest() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testCheckAllOrNothingBuyingShoppingCartGuest'");
    }

    @Override
    public boolean testBuyingShoppingCartPoliciesGuest() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testBuyingShoppingCartPoliciesGuest'");
    }

    @Override
    public boolean testGetShopInfoAsUser(String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetShopInfoAsUser'");
    }

    @Override
    public boolean testGetProductInfoAsUser(String productId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoAsUser'");
    }

    @Override
    public boolean testGetProductInfoUsingProductNameAsUser(String productId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingProductNameAsUser'");
    }

    @Override
    public boolean testGetProductInfoUsingProductCategoryAsUser(String category) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingProductCategoryAsUser'");
    }

    @Override
    public boolean testGetProductInfoUsingKeyWordsAsUser(String keyWord) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingKeyWordsAsUser'");
    }

    @Override
    public boolean testGetProductInfoUsingKeyWordsAsUser(String keyWord1, String keyWord2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingKeyWordsAsUser'");
    }

    @Override
    public boolean testGetProductInfoUsingProductNameInShop(String productId, String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingProductNameInShop'");
    }

    @Override
    public boolean testGetProductInfoUsingProductNameInShopAsUser(String productId, String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingProductNameInShopAsUser'");
    }

    @Override
    public boolean testGetProductInfoUsingProductCategoryInShopAsUser(String category, String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingProductCategoryInShopAsUser'");
    }

    @Override
    public boolean testGetProductInfoUsingKeyWordsInShopAsUser(String keyWord1, String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingKeyWordsInShopAsUser'");
    }

    @Override
    public boolean testAddProductToShoppingCartAsUser(String productId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testAddProductToShoppingCartAsUser'");
    }

    @Override
    public boolean testCheckAndViewItemsInShoppingCartAsUser() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testCheckAndViewItemsInShoppingCartAsUser'");
    }

    @Override
    public boolean testCheckAllOrNothingBuyingShoppingCartUser(String username, String password) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testCheckAllOrNothingBuyingShoppingCartUser'");
    }

    @Override
    public boolean testBuyingShoppingCartPoliciesUser(String username, String password) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testBuyingShoppingCartPoliciesUser'");
    }

    @Override
    public boolean testLogoutToTheSystem(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testLogoutToTheSystem'");
    }

    @Override
    public boolean TestWhenUserLogoutThenHisCartSaved(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestWhenUserLogoutThenHisCartSaved'");
    }

    @Override
    public boolean TestWhenUserLogoutThenHeBecomeGuest(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestWhenUserLogoutThenHeBecomeGuest'");
    }

    @Override
    public boolean TestUserOpenAShop(String username, String password, String shopName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestUserOpenAShop'");
    }

    @Override
    public boolean TestUserIsFounderOfTheShop(String username, String password, String shopName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestUserIsFounderOfTheShop'");
    }

    @Override
    public boolean TestUserWriteReviewOnPurchasedProduct(String username, String password, String productId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestUserWriteReviewOnPurchasedProduct'");
    }

    @Override
    public boolean TestUserRatingPurchasedProduct(String username, String password, String productId, String score) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestUserRatingPurchasedProduct'");
    }

    @Override
    public boolean TestUserRatingShopHePurchasedFrom(String username, String password, String shopId, String score) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestUserRatingShopHePurchasedFrom'");
    }

    @Override
    public boolean TestUserMessagingShopHePurchasedFrom(String username, String password, String Id, String message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestUserMessagingShopHePurchasedFrom'");
    }

    @Override
    public boolean TestUserReportSystemManagerOnBreakingIntegrityRules(String username, String password,
            String message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestUserReportSystemManagerOnBreakingIntegrityRules'");
    }

    @Override
    public boolean TestUserViewHistoryPurchaseList(String username, String password) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestUserViewHistoryPurchaseList'");
    }

    @Override
    public boolean TestUserViewHistoryPurchaseListWhenProductRemovedFromSystem(String username, String password,
            String productId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestUserViewHistoryPurchaseListWhenProductRemovedFromSystem'");
    }

    @Override
    public boolean TestUserViewHistoryPurchaseListWhenShopRemovedFromSystem(String username, String password,
            String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestUserViewHistoryPurchaseListWhenShopRemovedFromSystem'");
    }

    @Override
    public boolean TestUserViewPrivateDetails(String username, String password) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestUserViewPrivateDetails'");
    }

    @Override
    public boolean TestUserEditEmail(String username, String password, String newEmail) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestUserEditEmail'");
    }

    @Override
    public boolean TestUserEditPassword(String username, String newPassword, String email) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestUserEditPassword'");
    }

    @Override
    public boolean TestUserEditUsername(String newName, String newPassword, String email) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestUserEditUsername'");
    }

    @Override
    public boolean testShopOwnerAddProductToShop(String username, String shopId, String productName,
            String productAmount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testShopOwnerAddProductToShop'");
    }

    @Override
    public boolean testShopOwnerRemoveProductFromShop(String username, String shopId, String productName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testShopOwnerRemoveProductFromShop'");
    }

    @Override
    public boolean testShopOwnerEditProductInShop(String username, String shopId, String productName,
            String productNameNew, String productAmount, String productAmountNew) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testShopOwnerEditProductInShop'");
    }

    @Override
    public boolean testOpenMarketSystem(String username, String shopId, String permission) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testOpenMarketSystem'");
    }

    @Override
    public boolean testSystemManagerViewHistoryPurcaseInUsers(String namanger, String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testSystemManagerViewHistoryPurcaseInUsers'");
    }

    @Override
    public boolean testSystemManagerViewHistoryPurcaseInShops(String namanger, String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testSystemManagerViewHistoryPurcaseInShops'");
    }

    @Override
    public boolean testAddProductToShoppingCartUser(String username, String productId, String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testAddProductToShoppingCartUser'");
    }

    @Override
    public boolean testAddProductToShoppingCartGuest(String username, String productId, String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testAddProductToShoppingCartGuest'");
    }

    @Override
    public boolean testShopOwnerChangeShopPolicies(String username, String shopId, String newPolicy) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testShopOwnerChangeShopPolicies'");
    }

    @Override
    public boolean testShopOwnerAppointAnotherShopOwner(String username, String shopId, String newOwnerUsername) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testShopOwnerAppointAnotherShopOwner'");
    }

    @Override
    public boolean testShopOwnerAppointAnotherShopManager(String username, String shopId, String newManagerUsername) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testShopOwnerAppointAnotherShopManager'");
    }

    @Override
    public boolean testShopOwnerAddShopManagerPermission(String username, String shopId, String managerUsername,
            String permission) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testShopOwnerAddShopManagerPermission'");
    }

    @Override
    public boolean testShopOwnerRemoveShopManagerPermission(String username, String shopId, String managerUsername,
            String permission) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testShopOwnerRemoveShopManagerPermission'");
    }

    @Override
    public boolean testShopOwnerCloseShop(String username, String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testShopOwnerCloseShop'");
    }

    @Override
    public boolean testShopOwnerGetShopInfo(String username, String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testShopOwnerGetShopInfo'");
    }

    @Override
    public boolean testShopOwnerGetShopManagersPermissions(String username, String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testShopOwnerGetShopManagersPermissions'");
    }

    @Override
    public boolean testShopOwnerViewHistoryPurcaseInShop(String username, String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testShopOwnerViewHistoryPurcaseInShop'");
    }
}
