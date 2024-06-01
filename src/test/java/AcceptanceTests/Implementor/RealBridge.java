package AcceptanceTests.Implementor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import Domain.*;
import Domain.ExternalServices.ExternalServiceHandler;
import Domain.Facades.*;
import ServiceLayer.*;

// A real conection to the system.
// The code is tested on the real information on te system.
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RealBridge implements BridgeInterface, ParameterResolver {

    // real services under test
    private ShopService _shopServiceUnderTest;
    private SystemService _systemServiceUnderTest;
    private UserService _userServiceUnderTest;

    // real facades to use in tests
    private ShopFacade _shopFacade = ShopFacade.getShopFacade();
    private ShoppingCartFacade _shoppingCartFacade;
    private UserFacade _userFacade;
    private PasswordEncoderUtil _passwordEncoder;
    private ExternalServiceHandler _externalServiceHandler;

    // mocks
    @Mock
    private PasswordEncoderUtil _passwordEncoderMock;
    @Mock
    private TokenService _tokenServiceMock;

    // other private fields
    private static String token = "token";
    private Logger logger = Logger.getLogger(RealBridge.class.getName());

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == RealBridge.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return new RealBridge();
    }

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        
        _shopFacade = ShopFacade.getShopFacade();
        _shoppingCartFacade = ShoppingCartFacade.getShoppingCartFacade();
        _userFacade = UserFacade.getUserFacade(new ArrayList<User>() {
            {
                add(new User("Bob", "bobspassword", "email", new Date()));
            }
        }, new ArrayList<>(), _passwordEncoderMock);
        _externalServiceHandler = new ExternalServiceHandler();
        _passwordEncoder = new PasswordEncoderUtil();

        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);
        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userServiceUnderTest);
        _systemServiceUnderTest = new SystemService(_userServiceUnderTest, _externalServiceHandler, _tokenServiceMock,
                _userFacade, _shoppingCartFacade);
    }

    @AfterEach
    public void tearDown() {
    }

    // SYSTEM TESTS --------------------------------------------------------------------------------------------------------------------------------------------------------------
    
    @Test
    public boolean testOpenMarketSystem(String username){
        // Arrange
        MockitoAnnotations.openMocks(this);
        _shopFacade = ShopFacade.getShopFacade();
        _shoppingCartFacade = ShoppingCartFacade.getShoppingCartFacade();
        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(new User("systemAdmin", "systemAdminPassword", "email", new Date()));
            }
        }, new ArrayList<>(), _passwordEncoderMock);
        _externalServiceHandler = new ExternalServiceHandler();
        _passwordEncoder = new PasswordEncoderUtil();

        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);
        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userServiceUnderTest);
        _systemServiceUnderTest = new SystemService(_userServiceUnderTest, _externalServiceHandler, _tokenServiceMock,
                _userFacade, _shoppingCartFacade);

        _userServiceUnderTest.logIn(token, "systemAdmin", "systemAdminPassword");

        String token = username.equals("systemAdmin") ? "systemAdmin" : "guest";

        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        when(_tokenServiceMock.extractUsername(token)).thenReturn(username);
        when(_tokenServiceMock.isUserAndLoggedIn("systemAdmin")).thenReturn(true);
        when(_tokenServiceMock.isUserAndLoggedIn("guest")).thenReturn(false);
        when(_tokenServiceMock.isGuest("systemAdmin")).thenReturn(false);
        when(_tokenServiceMock.isGuest("guest")).thenReturn(true);

        // Act
        Response res = _systemServiceUnderTest.openSystem(token);

        // Assert
        logger.info("testOpenMarketSystem Error message: " + res.getErrorMessage());
        return res.getErrorMessage() == null;
    }

    @Test
    public boolean testPayment(String senario){
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testPayment'");
    }

    @Test
    public boolean testShipping(String senario){
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testShipping'");
    }

    @Test
    public boolean testAddExternalService(String newSerivceName, String informationPersonName, String informationPersonPhone, Integer securityIdForService){
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testAddExternalService'");
    }

    @Test
    public boolean testChangeExternalService(Integer oldServiceSystemId, String newSerivceName, String newInformationPersonName, String newInformationPersonPhone){
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testChangeExternalService'");
    }

    // GUEST TESTS --------------------------------------------------------------------------------------------------------------------------------------------------------------
    
    @Test
    public boolean TestGuestEnterTheSystem(String shouldSeccess){
        // Arrange
        MockitoAnnotations.openMocks(this);
        _shopFacade = ShopFacade.getShopFacade();
        _shoppingCartFacade = ShoppingCartFacade.getShoppingCartFacade();
        _userFacade = new UserFacade(new ArrayList<User>(), new ArrayList<>(), _passwordEncoderMock);
        _externalServiceHandler = new ExternalServiceHandler();
        _passwordEncoder = new PasswordEncoderUtil();

        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);
        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userServiceUnderTest);
        _systemServiceUnderTest = new SystemService(_userServiceUnderTest, _externalServiceHandler, _tokenServiceMock,
                _userFacade, _shoppingCartFacade);

        _userFacade.addNewGuest("existGuest");

        String token = shouldSeccess.equals("newGuest") ? "newGuest" : "existGuest";

        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        when(_tokenServiceMock.extractGuestId("newGuest")).thenReturn("newGuest");
        when(_tokenServiceMock.extractGuestId("existGuest")).thenReturn("existGuest");
        when(_tokenServiceMock.generateGuestToken()).thenReturn(token);

        // Act
        Response res = _systemServiceUnderTest.requestToEnterSystem();

        // Assert
        logger.info("TestGuestEnterTheSystem Error message: " + res.getErrorMessage());
        return res.getErrorMessage() == null;
    }
    
    @Test
    public boolean TestGuestRegisterToTheSystem(String username, String password, String email){
        // Arrange
        MockitoAnnotations.openMocks(this);
        _externalServiceHandler = new ExternalServiceHandler();
        _passwordEncoder = new PasswordEncoderUtil();
        _shopFacade = ShopFacade.getShopFacade();
        _shoppingCartFacade = ShoppingCartFacade.getShoppingCartFacade();
        _userFacade = UserFacade.getUserFacade(new ArrayList<User>() {
            {
                add(new User("Bobi", _passwordEncoder.encodePassword("encodePassword"), "email", new Date()));
            }
        }, new ArrayList<>(), _passwordEncoderMock);

        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);
        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userServiceUnderTest);
        _systemServiceUnderTest = new SystemService(_userServiceUnderTest, _externalServiceHandler, _tokenServiceMock,
                _userFacade, _shoppingCartFacade);

        when(_tokenServiceMock.validateToken(token)).thenReturn(true);

        // Act
        Response res = _userServiceUnderTest.register(token, username, password, email, "2000", "1", "1");

        // Assert
        logger.info("TestGuestRegisterToTheSystem Error message: " + res.getErrorMessage());
        return res.getErrorMessage() == null;
    }
    
    @Test
    public boolean TestUserEnterTheSystem(String SystemStatus){
        throw new UnsupportedOperationException("Unimplemented method 'testLoginToTheSystem'");}
    
    @Test
    public boolean testLoginToTheSystem(String username, String password){
        // Arrange
        MockitoAnnotations.openMocks(this);
        _externalServiceHandler = new ExternalServiceHandler();
        _passwordEncoder = new PasswordEncoderUtil();
        _shopFacade = ShopFacade.getShopFacade();
        _shoppingCartFacade = ShoppingCartFacade.getShoppingCartFacade();
        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(new User("Bob", _passwordEncoder.encodePassword("bobspassword"), "email", new Date()));
            }
        }, new ArrayList<>(), _passwordEncoder);

        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);
        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userServiceUnderTest);
        _systemServiceUnderTest = new SystemService(_userServiceUnderTest, _externalServiceHandler, _tokenServiceMock,
                _userFacade, _shoppingCartFacade);

        when(_tokenServiceMock.validateToken(token)).thenReturn(true);

        // Act
        Response res = _userServiceUnderTest.logIn(token, username, password);

        // Assert
        logger.info("testLoginToTheSystem Error message: " + res.getErrorMessage());
        return res.getErrorMessage() == null;
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
        throw new UnsupportedOperationException(
                "Unimplemented method 'testGetProductInfoUsingProductNameInShopAsGuest'");
    }

    @Override
    public boolean testGetProductInfoUsingProductCategoryInShopAsGuest(String category, String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException(
                "Unimplemented method 'testGetProductInfoUsingProductCategoryInShopAsGuest'");
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
        throw new UnsupportedOperationException(
                "Unimplemented method 'testGetProductInfoUsingProductNameInShopAsUser'");
    }

    @Override
    public boolean testGetProductInfoUsingProductCategoryInShopAsUser(String category, String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException(
                "Unimplemented method 'testGetProductInfoUsingProductCategoryInShopAsUser'");
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
    public boolean testCheckBuyingShoppingCartUser(String username, String busketsToBuy, String cardNumber,
            String address) {
                // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testCheckBuyingShoppingCartUser'");
        
        // // Split the input string by spaces Convert the array to a list of Integer
        // when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        // String[] stringArray = busketsToBuy.split("\\s+");
        // List<Integer> busketsToBuyList = new ArrayList<>();

        // for (String s : stringArray) {
        //     try {
        //         int number = Integer.parseInt(s);
        //         busketsToBuyList.add(number);
        //     } catch (NumberFormatException e) {
        //         // Handle the case where the string cannot be parsed to an integer
        //         System.err.println("Invalid number format: " + s);
        //     }
        // }

        // try {
        //     when(_tokenServiceMock.isGuest(token)).thenReturn(false);
        //     when(_tokenServiceMock.extractUsername(token)).thenReturn(username);
        //     // TODO: not sure how to handle the payment method and supply in shopppingCart
        //     _userServiceMock.purchaseCart(token, busketsToBuyList, cardNumber, address);

        //     // Verify interactions
        //     verify(_userServiceMock, times(1)).purchaseCart(token, busketsToBuyList, cardNumber, address);
        //     verify(_tokenServiceMock, times(1)).validateToken(token);
        //     verify(_tokenServiceMock, times(1)).isGuest(token);

        //     return true;
        // } catch (Exception e) {
        //     return false;
        // }
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

        // when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        // when(_tokenServiceMock.extractUsername(token)).thenReturn(username);
        // when(_userFacadeMock.doesUserExist("Bob")).thenReturn(true);
        // when(_userFacadeMock.doesUserExist("notUsername")).thenReturn(false);

        // try {
        //     _userServiceMock.logOut(token);

        //     // Verify interactions
        //     verify(_userFacadeMock, times(1)).doesUserExist(username);
        //     verify(_tokenServiceMock, times(1)).extractUsername(token);

        //     return true;
        // } catch (Exception e) {
        //     return false;
        // }
    }

    @Override
    public boolean TestWhenUserLogoutThenHisCartSaved(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestWhenUserLogoutThenHisCartSaved'");
    }

    // Not sure if neccery or how to test it >> maybe its enough
    // testLogoutToTheSystem
    @Override
    public boolean TestWhenUserLogoutThenHeBecomeGuest(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestWhenUserLogoutThenHeBecomeGuest'");
    }

    @Override
    public boolean TestUserOpenAShop(String username, String password, String shopId, String bankDetails,
            String shopAddress) {
                // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestUserOpenAShop'");

        // when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        // when(_shopFacadeMock.isShopIdExist(Integer.valueOf("5555"))).thenReturn(false);

        // when(_tokenServiceMock.isUserAndLoggedIn("Bob")).thenReturn(true);
        // when(_tokenServiceMock.isUserAndLoggedIn("Ron")).thenReturn(true);
        // when(_tokenServiceMock.isUserAndLoggedIn("Tom")).thenReturn(false);
        // when(_tokenServiceMock.isGuest("Tom")).thenReturn(true);

        // try {
        //     when(_shopFacadeMock.isShopIdExist(Integer.valueOf("879"))).thenAnswer(invocation -> {
        //         throw new IllegalArgumentException();
        //     });

        // } catch (Exception e) {
        //     return false;
        // }

        // Response response = _shopServiceMock.openNewShop(token, Integer.valueOf(shopId), username, bankDetails,
        //         shopAddress);

        // // Verify interactions
        // verify(_shopServiceMock, times(1)).openNewShop(token, Integer.valueOf(shopId), username, bankDetails,
        //         shopAddress);
        // return response.getErrorMessage() == null;

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
        throw new UnsupportedOperationException(
                "Unimplemented method 'TestUserReportSystemManagerOnBreakingIntegrityRules'");
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
        throw new UnsupportedOperationException(
                "Unimplemented method 'TestUserViewHistoryPurchaseListWhenProductRemovedFromSystem'");
    }

    @Override
    public boolean TestUserViewHistoryPurchaseListWhenShopRemovedFromSystem(String username, String password,
            String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException(
                "Unimplemented method 'TestUserViewHistoryPurchaseListWhenShopRemovedFromSystem'");
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

        // MockitoAnnotations.openMocks(this);
        // when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        // _userServiceMock = Mockito.spy(new UserService(_userFacadeMock, _tokenServiceMock, _shoppingCartFacadeMock));
        // _shopServiceMock = Mockito.spy(new ShopService(_shopFacadeMock, _tokenServiceMock, _userServiceMock));
        // try {
        //     when(_tokenServiceMock.isUserAndLoggedIn("Nirvana")).thenReturn(true);
        //     when(_tokenServiceMock.isUserAndLoggedIn("whoAmI")).thenReturn(true);
        //     when(_shopFacadeMock.isShopIdExist(Integer.valueOf("56321"))).thenReturn(true);
        //     when(_shopFacadeMock.getShopByShopId(Integer.valueOf("56321"))).thenReturn(_shopMock);
        //     when(_shopMock.checkPermission("Nirvana", Permission.ADD_PRODUCT)).thenReturn(true);
        //     when(_shopMock.checkPermission("whoAmI", Permission.ADD_PRODUCT)).thenReturn(false);

        //     when(_shopMock.checkPermission("whoAmI", Permission.ADD_PRODUCT)).thenAnswer(invocation -> {
        //         throw new IllegalArgumentException();
        //     });

        //     Response response = _shopServiceMock.addProductToShop(token, Integer.valueOf(shopId), username,
        //             _productMock);

        //     // Verify interactions
        //     verify(_shopServiceMock, times(1)).addProductToShop(token, Integer.valueOf(shopId), username, _productMock);
        //     verify(_shopFacadeMock, times(1)).addProductToShop(Integer.valueOf(shopId), _productMock, username);

        //     return response.getErrorMessage() == null;
        // } catch (Exception e) {
        //     return false;
        // }
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

        // MockitoAnnotations.openMocks(this);

        // when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        // _userServiceMock = Mockito.spy(new UserService(_userFacadeMock, _tokenServiceMock, _shoppingCartFacadeMock));
        // _shopServiceMock = Mockito.spy(new ShopService(_shopFacadeMock, _tokenServiceMock, _userServiceMock));
        // try {
        //     when(_shopFacadeMock.isShopIdExist(Integer.valueOf("12345"))).thenReturn(true);
        //     when(_tokenServiceMock.isUserAndLoggedIn("Bob")).thenReturn(true);
        //     when(_shopMock.checkPermission("Bob", Permission.FOUNDER)).thenReturn(true);

        //     when(_shopFacadeMock.isShopIdExist(Integer.valueOf("67890"))).thenReturn(true);
        //     when(_tokenServiceMock.isUserAndLoggedIn("Tom")).thenReturn(true);
        //     when(_shopMock.checkPermission("Tom", Permission.FOUNDER)).thenReturn(false);

        //     when(_shopFacadeMock.isShopIdExist(Integer.valueOf("33333"))).thenReturn(false);

        //     Response response = _shopServiceMock.closeShop(token, Integer.valueOf(shopId), username);
        //     // Verify interactions
        //     verify(_shopServiceMock, times(1)).closeShop(token, Integer.valueOf(shopId), username);

        //     return response.getErrorMessage() == null;
        // } catch (Exception e) {
        //     logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
        //     return false;
        // }
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
