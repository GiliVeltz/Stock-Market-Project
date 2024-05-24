package AcceptanceTests.Implementor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import Domain.*;
import ServiceLayer.*;
import java.util.List;

// A real conection to the system.
// The code is tested on the real information on te system.
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RealBridge implements BridgeInterface, ParameterResolver{

    // mocks services
    @Mock
    private ShopService _shopServiceMock;
    
    @Mock
    private SystemService _systemServiceMock;
    
    @Mock
    private TokenService _tokenServiceMock;
    @Mock
    private UserService _userServiceMock;

    // mocks facades
    @Mock
    private ShopFacade _shopFacadeMock;
   
    @Mock
    private ShoppingCartFacade _shoppingCartFacadeMock;

    @Mock
    private User _userMock;

    @Mock
    private Shop _shopMock;

    @Mock
    private UserFacade _userFacadeMock;

    @Mock
    private Product _productMock;

    // more mocks
    @Mock
    private PasswordEncoderUtil _passwordEncoderMock;

    // private fields
    private String token = "token";
    
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
        _shopServiceMock = mock(ShopService.class);
        _systemServiceMock = mock(SystemService.class);
        _tokenServiceMock = mock(TokenService.class);
        _userServiceMock = mock(UserService.class);
        _userFacadeMock = mock(UserFacade.class);
        _shopFacadeMock = mock(ShopFacade.class);
        _shoppingCartFacadeMock = mock(ShoppingCartFacade.class);
        _userFacadeMock = mock(UserFacade.class);
        _passwordEncoderMock = mock(PasswordEncoderUtil.class);
        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
    }

    @AfterEach
    public void tearDown() {
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
    public boolean TestGuestRegisterToTheSystem(String username, String password, String email) 
    {

        when(_userFacadeMock.isUserNameExists("Bob")).thenReturn(false);
        when(_userFacadeMock.isUserNameExists("Bobi")).thenReturn(true);
        when(_userFacadeMock.isUserNameExists("Mom")).thenReturn(false);
        try
        {
            _userServiceMock.register(token, username, password, email);
            
            // Verify interactions
            verify(_userFacadeMock, times(1)).isUserNameExists(username);
            verify(_tokenServiceMock, times(1)).validateToken(token);
   
            return true;
        }
        catch(Exception e)
        {
            System.out.println("TestGuestRegisterToTheSystem Error message: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean TestUserEnterTheSystem(String SystemStatus) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestUserEnterTheSystem'");
    }

    @Override
    public boolean testLoginToTheSystem(String username, String password) {
        when(_userFacadeMock.AreCredentialsCorrect("Bob","bobspassword")).thenReturn(true);
        when(_userFacadeMock.AreCredentialsCorrect("Mom","momspassword")).thenReturn(false);

        try
        {
            _userServiceMock.logIn(token, username, password);
            
            // Verify interactions
            verify(_userFacadeMock, times(1)).AreCredentialsCorrect(username,password);
            verify(_tokenServiceMock, times(1)).validateToken(token);
   
            return true;
        }
        catch(Exception e)
        {
            return false;
        }

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
        when(_tokenServiceMock.isGuest(token)).thenReturn(true);
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
    public boolean testCheckBuyingShoppingCartUser(String username, String busketsToBuy, String cardNumber, String address) {

        // Split the input string by spaces Convert the array to a list of Integer
        String[] stringArray = busketsToBuy.split("\\s+");
        List<Integer> busketsToBuyList = new ArrayList<>();
        for (String s : stringArray) {
        try {
            int number = Integer.parseInt(s);
            busketsToBuyList.add(number);
        } catch (NumberFormatException e) {
            // Handle the case where the string cannot be parsed to an integer
            System.err.println("Invalid number format: " + s);
        }
        }

        try
        {
            when(_tokenServiceMock.isGuest(token)).thenReturn(false);
            when(_tokenServiceMock.extractUsername(token)).thenReturn(username);
            //TODO: not sure how to handle the payment method and supply in shopppingCart
            _userServiceMock.purchaseCart(token,busketsToBuyList,cardNumber,address);
            
            // Verify interactions
            verify(_userServiceMock, times(1)).purchaseCart(token,busketsToBuyList,cardNumber,address);
            verify(_tokenServiceMock, times(1)).validateToken(token);
            verify(_tokenServiceMock, times(1)).isGuest(token);
   
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }

    @Override
    public boolean testBuyingShoppingCartPoliciesUser(String username, String password) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testBuyingShoppingCartPoliciesUser'");
    }

    @Override
    public boolean testLogoutToTheSystem(String username) {
        when(_tokenServiceMock.extractUsername(token)).thenReturn(username);
        when(_userFacadeMock.isUserNameExists("Bob")).thenReturn(true);
        when(_userFacadeMock.isUserNameExists("notUsername")).thenReturn(false);

        try
        {
            _userServiceMock.logOut(token);
            
            // Verify interactions
            verify(_userFacadeMock, times(1)).isUserNameExists(username);
            verify(_tokenServiceMock, times(1)).extractUsername(token);
   
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }

    @Override
    public boolean TestWhenUserLogoutThenHisCartSaved(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestWhenUserLogoutThenHisCartSaved'");
    }

    //Not sure if neccery or how to test it >> maybe its enough testLogoutToTheSystem
    @Override
    public boolean TestWhenUserLogoutThenHeBecomeGuest(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestWhenUserLogoutThenHeBecomeGuest'");
    }

    @Override
    public boolean TestUserOpenAShop(String username, String password, String shopId, String bankDetails, String shopAddress) {
        when(_shopFacadeMock.isShopIdExist(Integer.valueOf("5555"))).thenReturn(false);
        when(_tokenServiceMock.isUserAndLoggedIn("Bob")).thenReturn(true);
        when(_tokenServiceMock.isGuest("Tom")).thenReturn(true);
        when(_shopFacadeMock.isShopIdExist(Integer.valueOf("879"))).thenReturn(true);
        try
        {
            // Verify interactions
            verify(_shopFacadeMock, times(1)).openNewShop(Integer.valueOf(shopId), username, bankDetails, shopAddress);
   
            assertDoesNotThrow(() -> _shopServiceMock.openNewShop(token, Integer.valueOf(shopId), username, bankDetails, shopAddress));
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
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
    String productAmount)
    {
        try
        {
            when(_tokenServiceMock.isUserAndLoggedIn("Nirvana")).thenReturn(true);
            when(_tokenServiceMock.isUserAndLoggedIn("whoAmI")).thenReturn(true);
            when(_shopFacadeMock.isShopIdExist(Integer.valueOf("56321"))).thenReturn(true);
            when(_shopFacadeMock.getShopByShopId(Integer.valueOf("56321"))).thenReturn(_shopMock);
            when(_shopMock.checkPermission("Nirvana",Permission.ADD_PRODUCT)).thenReturn(true);
            when(_shopMock.checkPermission("whoAmI",Permission.ADD_PRODUCT)).thenReturn(false);
            
            //TODO: verify how to check if product mock exists>> maybe split it to 2 different functions 
            // _productMap.containsKey(product.getProductId())

            // Verify interactions
            verify(_shopFacadeMock, times(1)).addProductToShop(Integer.valueOf(shopId), _productMock, username);
            verify(_shopFacadeMock, times(1)).addProductToShop(Integer.valueOf(shopId), _productMock, username);

   
            _shopServiceMock.addProductToShop(token, Integer.valueOf(shopId), username, _productMock);
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
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
    
        try
        {
            when(_shopFacadeMock.isShopIdExist(Integer.valueOf("12345"))).thenReturn(true);
            when(_tokenServiceMock.isUserAndLoggedIn("Bob")).thenReturn(true);
            when(_shopMock.checkPermission("Bob", Permission.FOUNDER)).thenReturn(true);

            when(_shopFacadeMock.isShopIdExist(Integer.valueOf("67890"))).thenReturn(true);
            when(_tokenServiceMock.isUserAndLoggedIn("Tom")).thenReturn(true);
            when(_shopMock.checkPermission("Tom", Permission.FOUNDER)).thenReturn(false);

            when(_shopFacadeMock.isShopIdExist(Integer.valueOf("33333"))).thenReturn(false);

            _shopServiceMock.closeShop(token, Integer.valueOf(shopId), username);
            // Verify interactions
            verify(_shopFacadeMock, times(1)).closeShop(Integer.valueOf(shopId), username);
   
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
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
