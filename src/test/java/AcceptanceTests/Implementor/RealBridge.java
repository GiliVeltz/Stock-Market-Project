package AcceptanceTests.Implementor;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import Domain.Authenticators.PasswordEncoderUtil;
import Domain.ExternalServices.ExternalServiceHandler;
import Domain.Facades.*;
import Dtos.ExternalServiceDto;
import Dtos.ProductDto;
import Dtos.PurchaseCartDetailsDto;
import Dtos.ShopDto;
import Dtos.UserDto;
import Exceptions.StockMarketException;
import ServiceLayer.*;
import enums.Category;

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
    private TokenService _tokenService;
    private ExternalServiceHandler _externalServiceHandler;

    // mocks
    @Mock
    private PasswordEncoderUtil _passwordEncoderMock;
    @Mock
    private TokenService _tokenServiceMock;
    @Mock
    ShopFacade _shopFacadeMock;
    @Mock
    ShoppingBasket _shoppingBasketMock;
    @Mock
    ShoppingCartFacade _shoppingCartFacadeMock;

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
        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(new User("Bob", "bobspassword", "email@example.com", new Date()));
            }
        }, new ArrayList<>(), _passwordEncoderMock);
        _externalServiceHandler = new ExternalServiceHandler();
        _passwordEncoder = new PasswordEncoderUtil();
        _tokenService = new TokenService();

        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);
        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userFacade);
        _systemServiceUnderTest = new SystemService(_userServiceUnderTest, _externalServiceHandler, _tokenServiceMock,
                _userFacade, _shoppingCartFacade);
    }

    @AfterEach
    public void tearDown() {
    }

    // SYSTEM TESTS
    // --------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Test
    public boolean testOpenMarketSystem(String username) {
        // Arrange
        MockitoAnnotations.openMocks(this);
        _shopFacade = ShopFacade.getShopFacade();
        _shoppingCartFacade = ShoppingCartFacade.getShoppingCartFacade();
        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(new User("systemAdmin", "systemAdminPassword", "email@example.com", new Date()));
            }
        }, new ArrayList<>(), _passwordEncoderMock);
        _externalServiceHandler = new ExternalServiceHandler();
        _passwordEncoder = new PasswordEncoderUtil();

        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);
        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userFacade);
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
    public boolean testPayment(String senario) {
        // Dummy test
        return !senario.equals("error");
    }

    @Test
    public boolean testShipping(String senario) {
        // Dummy test
        return !senario.equals("error");
    }

    @Test
    public boolean testAddExternalService(String newSerivceName, String informationPersonName, String informationPersonPhone, Integer securityIdForService){
        // Arrange
        MockitoAnnotations.openMocks(this);

        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        when(_tokenServiceMock.extractUsername(token)).thenReturn("manager");
        when(_tokenServiceMock.isUserAndLoggedIn(token)).thenReturn(true);
        when(_tokenServiceMock.extractGuestId(token)).thenReturn("manager");

        _externalServiceHandler = new ExternalServiceHandler();
        _passwordEncoder = new PasswordEncoderUtil();

        _shopFacade = new ShopFacade();
        _shoppingCartFacade = new ShoppingCartFacade();
        _userFacade = new UserFacade(new ArrayList<User>(){
            {
                add(new User("manager", _passwordEncoder.encodePassword("managerPassword"), "email@gmail.com", new Date()));
            }
        }
        , new ArrayList<>(), _passwordEncoder);

        _shoppingCartFacade.addCartForGuest("manager");
        
        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);
        _systemServiceUnderTest = new SystemService(_userServiceUnderTest, _externalServiceHandler, _tokenServiceMock,
                _userFacade, _shoppingCartFacade);

        try {
            _userFacade.getUserByUsername("manager").setIsSystemAdmin(true);
        } catch (Exception e) {
            logger.info("testAddExternalService Error message: " + e.getMessage());
            return false;
        }
        ExternalServiceDto externalServiceDto = new ExternalServiceDto(-1, "existSerivce", "name", "111");

        _externalServiceHandler.addExternalService(externalServiceDto);

        Response res1 = _userServiceUnderTest.logIn(token, "manager", "managerPassword");
        if(res1.getErrorMessage() != null){
            logger.info("testAddExternalService Error message: " + res1.getErrorMessage());
            return false;
        }

        Response res2 = _systemServiceUnderTest.openSystem(token);
        if(res2.getErrorMessage() != null){
            logger.info("testAddExternalService Error message: " + res2.getErrorMessage());
            return false;
        }
        ExternalServiceDto externalServiceDto2 = new ExternalServiceDto(-1, newSerivceName, informationPersonName, informationPersonPhone);

        // Act
        Response res = _systemServiceUnderTest.addExternalService(token, externalServiceDto2);

        // Assert
        logger.info("testAddExternalService Error message: " + res.getErrorMessage());
        return res.getErrorMessage() == null;
    }

    @Test
    public boolean testChangeExternalService(Integer oldServiceSystemId, String newSerivceName, String newInformationPersonName, String newInformationPersonPhone){
        // Arrange
        MockitoAnnotations.openMocks(this);

        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        when(_tokenServiceMock.extractUsername(token)).thenReturn("manager");
        when(_tokenServiceMock.isUserAndLoggedIn(token)).thenReturn(true);
        when(_tokenServiceMock.extractGuestId(token)).thenReturn("manager");

        _externalServiceHandler = new ExternalServiceHandler();
        _passwordEncoder = new PasswordEncoderUtil();

        _shopFacade = new ShopFacade();
        _shoppingCartFacade = new ShoppingCartFacade();
        _userFacade = new UserFacade(new ArrayList<User>(){
            {
                add(new User("manager", _passwordEncoder.encodePassword("managerPassword"), "email@gmail.com", new Date()));
            }
        }
        , new ArrayList<>(), _passwordEncoder);

        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);
        _systemServiceUnderTest = new SystemService(_userServiceUnderTest, _externalServiceHandler, _tokenServiceMock,
                _userFacade, _shoppingCartFacade);
                
        _shoppingCartFacade.addCartForGuest("manager");

        try {
            _userFacade.getUserByUsername("manager").setIsSystemAdmin(true);
        } catch (Exception e) {
            logger.info("testChangeExternalService Error message: " + e.getMessage());
            return false;
        }
        ExternalServiceDto externalServiceDto = new ExternalServiceDto(0, "existSerivce", "name", "111");

        _externalServiceHandler.addExternalService(externalServiceDto);

        Response res1 = _userServiceUnderTest.logIn(token, "manager", "managerPassword");
        if(res1.getErrorMessage() != null){
            logger.info("testChangeExternalService Error message: " + res1.getErrorMessage());
            return false;
        }

        Response res2 = _systemServiceUnderTest.openSystem(token);
        if(res2.getErrorMessage() != null){
            logger.info("testChangeExternalService Error message: " + res2.getErrorMessage());
            return false;
        }
        ExternalServiceDto externalServiceDto2 = new ExternalServiceDto(oldServiceSystemId, newSerivceName, "name", "111");

        // Act
        Response res3 = _systemServiceUnderTest.changeExternalServiceName(token, externalServiceDto, newSerivceName);
        Response res4 = _systemServiceUnderTest.changeExternalServiceInformationPersonName(token, externalServiceDto2, newInformationPersonName);
        Response res5 = _systemServiceUnderTest.changeExternalServiceInformationPersonPhone(token, externalServiceDto2, newInformationPersonPhone);

        // Assert
        if(res3.getErrorMessage() != null)
            logger.info("changeExternalServiceName Error message: " + res3.getErrorMessage());
        if(res4.getErrorMessage() != null)
            logger.info("changeExternalServiceInformationPersonName Error message: " + res4.getErrorMessage());
        if(res5.getErrorMessage() != null)
        logger.info("changeExternalServiceInformationPersonPhone Error message: " + res5.getErrorMessage());
        
        return res3.getErrorMessage() == null && res4.getErrorMessage() == null && res5.getErrorMessage() == null;
    }

    // GUEST TESTS
    // --------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Test
    public boolean TestGuestEnterTheSystem(String shouldSeccess) {
        // Arrange
        MockitoAnnotations.openMocks(this);
        _shopFacade = ShopFacade.getShopFacade();
        _shoppingCartFacade = ShoppingCartFacade.getShoppingCartFacade();
        _userFacade = new UserFacade(new ArrayList<User>(), new ArrayList<>(), _passwordEncoderMock);
        _externalServiceHandler = new ExternalServiceHandler();
        _passwordEncoder = new PasswordEncoderUtil();

        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);
        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userFacade);
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
    public boolean TestGuestRegisterToTheSystem(String username, String password, String email) {
        // Arrange
        MockitoAnnotations.openMocks(this);
        _externalServiceHandler = new ExternalServiceHandler();
        _passwordEncoder = new PasswordEncoderUtil();
        _shopFacade = ShopFacade.getShopFacade();
        _shoppingCartFacade = ShoppingCartFacade.getShoppingCartFacade();
        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(new User("Bobi", _passwordEncoder.encodePassword("encodePassword"), "email@example.com",
                        new Date()));
            }
        }, new ArrayList<>(), _passwordEncoderMock);

        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);
        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userFacade);
        _systemServiceUnderTest = new SystemService(_userServiceUnderTest, _externalServiceHandler, _tokenServiceMock,
                _userFacade, _shoppingCartFacade);

        when(_tokenServiceMock.validateToken(token)).thenReturn(true);

        // Act
        UserDto userDto = new UserDto(username, password, email, new Date());
        Response res = _userServiceUnderTest.register(token, userDto);

        // Assert
        logger.info("TestGuestRegisterToTheSystem Error message: " + res.getErrorMessage());
        return res.getErrorMessage() == null;
    }

    @Test
    public boolean TestUserEnterTheSystem(String SystemStatus) {
        throw new UnsupportedOperationException("Unimplemented method 'TestUserEnterTheSystem'");
    }

    @Test
    public boolean testLoginToTheSystem(String username, String password) {
        // Arrange
        MockitoAnnotations.openMocks(this);
        _externalServiceHandler = new ExternalServiceHandler();
        _passwordEncoder = new PasswordEncoderUtil();
        _shopFacade = ShopFacade.getShopFacade();
        _shoppingCartFacade = ShoppingCartFacade.getShoppingCartFacade();
        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(new User("Bob", _passwordEncoder.encodePassword("bobspassword"), "email@example.com", new Date()));
            }
        }, new ArrayList<>(), _passwordEncoder);

        _shoppingCartFacade.addCartForGuest(username);

        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);
        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userFacade);
        _systemServiceUnderTest = new SystemService(_userServiceUnderTest, _externalServiceHandler, _tokenServiceMock,
                _userFacade, _shoppingCartFacade);

        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        when(_tokenServiceMock.extractGuestId(token)).thenReturn(username);

        // Act
        Response res = _userServiceUnderTest.logIn(token, username, password);

        // Assert
        logger.info("testLoginToTheSystem Error message: " + res.getErrorMessage());
        return res.getErrorMessage() == null;
    }

    // SYSTEM ADMIN TESTS --------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Test
    public boolean testSystemManagerViewHistoryPurcaseInUsers(String managerName, String userName){
        // Arrange
        MockitoAnnotations.openMocks(this);
        _passwordEncoder = new PasswordEncoderUtil();
        
        User manager = new User(managerName, _passwordEncoder.encodePassword("managersPassword"), "email@email.com",
                new Date());
        manager.setIsSystemAdmin(true);
        User guest = new User("guest", _passwordEncoder.encodePassword("guest"), "email@email.com", new Date());
        User user = new User("userName", _passwordEncoder.encodePassword("userName"), "email@email.com", new Date());

        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(manager);
                add(guest);
                add(user);
            }
        }, new ArrayList<>(), _passwordEncoder);

        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);

        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        when(_tokenServiceMock.extractUsername(token)).thenReturn(managerName);
        when(_tokenServiceMock.isUserAndLoggedIn(token)).thenReturn(true);

        // Act
        Response res = _userServiceUnderTest.getUserPurchaseHistory(token, userName);

        // Assert
        logger.info("testSystemManagerViewHistoryPurcaseInUsers Error message: " + res.getErrorMessage());
        return res.getErrorMessage() == null;
    }

    @Test
    public boolean testSystemManagerViewHistoryPurcaseInShops(String namanger, Integer shopId){
        // Arrange
        MockitoAnnotations.openMocks(this);
        _passwordEncoder = new PasswordEncoderUtil();
        
        User manager = new User("manager", _passwordEncoder.encodePassword("managersPassword"), "email@email.com",
                new Date());
        manager.setIsSystemAdmin(true);

        ShopDto shopDto = new ShopDto("shopName" ,"bankDetails", "address");

        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(manager);
            }
        }, new ArrayList<>(), _passwordEncoder);

        _shopFacade = new ShopFacade();
        
        try {
            _shopFacade.openNewShop(namanger, shopDto);
        } catch (StockMarketException e) {
            e.printStackTrace();
            logger.warning("testSystemManagerViewHistoryPurcaseInShops Error message: " + e.getMessage());
            return false;
        }

        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);
        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userFacade);

        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        when(_tokenServiceMock.extractUsername(token)).thenReturn(namanger);
        when(_tokenServiceMock.isUserAndLoggedIn(token)).thenReturn(true);

        // Act
        Response res = _shopServiceUnderTest.getShopPurchaseHistory(token, shopId);

        // Assert
        logger.info("testSystemManagerViewHistoryPurcaseInShops Error message: " + res.getErrorMessage());
        return res.getErrorMessage() == null;
    }

    // STORE MANAGER TESTS --------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Test
    public boolean testPermissionForShopManager(String username, Integer shopId, String permission) {
        // Arrange
        MockitoAnnotations.openMocks(this);

        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        when(_tokenServiceMock.extractUsername(token)).thenReturn("founder");
        when(_tokenServiceMock.isUserAndLoggedIn(token)).thenReturn(true);
        when(_tokenServiceMock.isUserAndLoggedIn(username)).thenReturn(true);
        when(_tokenServiceMock.isUserAndLoggedIn("founder")).thenReturn(true);

        _shopFacade = ShopFacade.getShopFacade();
        _shoppingCartFacade = ShoppingCartFacade.getShoppingCartFacade();
        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(new User("shopManager", "shopManagerPassword", "email@email.com", new Date()));
                add(new User("founder", "founderPassword", "email@email.com", new Date()));
            }
        }, new ArrayList<>(), _passwordEncoderMock);
        _externalServiceHandler = new ExternalServiceHandler();
        _passwordEncoder = new PasswordEncoderUtil();
        
        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);
        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userFacade);
        _systemServiceUnderTest = new SystemService(_userServiceUnderTest, _externalServiceHandler, _tokenServiceMock,
                _userFacade, _shoppingCartFacade);

        ShopDto shopDto = new ShopDto("shopName", "bankDetails", "address");
        _shopServiceUnderTest.openNewShop(token, shopDto);
        
        Set<String> permissions = new HashSet<>();
        if(permission.equals("possiblePermission")){
            permissions.add("ADD_PRODUCT");
        }
        
        _shopServiceUnderTest.addShopManager(token, shopId, username, permissions);

        // Act
        Response res = _shopServiceUnderTest.addProductToShop(token, shopId, new ProductDto("productName", Category.CLOTHING, 100));

        // Assert
        logger.info("testPermissionForShopManager Error message: " + res.getErrorMessage());
        return res.getErrorMessage() == null;
    }

    // SHOP OWNER TESTS --------------------------------------------------------------------------------------------------------------------------------------------------------------
    
    @Test
    public boolean testShopOwnerAddProductToShop(String username, String shopId, String productName, String productAmount){
        // Arrange
        MockitoAnnotations.openMocks(this);

        String tokenShopOwner = "shopOwner";
        String tokenShopFounder = "shopFounder";

        when(_tokenServiceMock.validateToken(tokenShopOwner)).thenReturn(true);
        when(_tokenServiceMock.extractUsername(tokenShopOwner)).thenReturn(username);
        when(_tokenServiceMock.isUserAndLoggedIn(tokenShopOwner)).thenReturn(true);

        when(_tokenServiceMock.validateToken(tokenShopFounder)).thenReturn(true);
        when(_tokenServiceMock.extractUsername(tokenShopFounder)).thenReturn("Founder");
        when(_tokenServiceMock.isUserAndLoggedIn(tokenShopFounder)).thenReturn(true);
        
        _passwordEncoder = new PasswordEncoderUtil();
        
        User shopFounder = new User("Founder", _passwordEncoder.encodePassword("shopFounderPassword"), "email@email.com", new Date());
        User shopOwner = new User("shopOwner", _passwordEncoder.encodePassword("shopOwnerPassword"), "email@email.com", new Date());
        ShopDto shopDto = new ShopDto("shopName", "bankDetails", "address");
        ProductDto productDto = new ProductDto(productName, Category.CLOTHING, Integer.parseInt(productAmount));
        ProductDto productExistDto = new ProductDto("ExistProductName", Category.CLOTHING, Integer.parseInt(productAmount));

        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(shopFounder);
                add(shopOwner);
            }
        }, new ArrayList<>(), _passwordEncoder);

        _shopFacade = new ShopFacade();

        try {
            _shopFacade.openNewShop("Founder", shopDto);
        } catch (StockMarketException e) {
            e.printStackTrace();
            logger.warning("testShopOwnerAddProductToShop Error message: " + e.getMessage());
            return false;
        }

        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);
        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userFacade);

        // Act
        Response res1 = _shopServiceUnderTest.addShopOwner(tokenShopFounder, Integer.parseInt(shopId), "shopOwner");
        Response res2 = _shopServiceUnderTest.addProductToShop(tokenShopOwner, Integer.parseInt(shopId), productExistDto);
        Response res3 = _shopServiceUnderTest.addProductToShop(tokenShopOwner, Integer.parseInt(shopId), productDto);


        // Assert
        if (res1.getErrorMessage() != null){
            logger.info("testShopOwnerAddProductToShop Error message: " + res1.getErrorMessage());
            return false;
        }
        if (res2.getErrorMessage() != null){
            logger.info("testShopOwnerAddProductToShop Error message: " + res2.getErrorMessage());
            return false;
        }
        if (res3.getErrorMessage() != null){
            logger.info("testShopOwnerAddProductToShop Error message: " + res3.getErrorMessage());
            return false;
        }
        return res3.getErrorMessage() == null;
    }
    
    @Test
    public boolean testShopOwnerRemoveProductFromShop(String username, String shopId, String productName){
        // Arrange
        MockitoAnnotations.openMocks(this);

        String tokenShopOwner = "shopOwner";
        String tokenShopFounder = "shopFounder";
        String tokenNotShopOwnerUserName = "NotShopOwnerUserName";

        when(_tokenServiceMock.validateToken(tokenShopOwner)).thenReturn(true);
        when(_tokenServiceMock.extractUsername(tokenShopOwner)).thenReturn(username);
        when(_tokenServiceMock.isUserAndLoggedIn(tokenShopOwner)).thenReturn(true);

        when(_tokenServiceMock.validateToken(tokenShopFounder)).thenReturn(true);
        when(_tokenServiceMock.extractUsername(tokenShopFounder)).thenReturn("Founder");
        when(_tokenServiceMock.isUserAndLoggedIn(tokenShopFounder)).thenReturn(true);
        when(_tokenServiceMock.isUserAndLoggedIn(tokenShopOwner)).thenReturn(true);

        when(_tokenServiceMock.validateToken(tokenNotShopOwnerUserName)).thenReturn(true);
        when(_tokenServiceMock.extractUsername(tokenNotShopOwnerUserName)).thenReturn("NotShopOwnerUserName");
        when(_tokenServiceMock.isUserAndLoggedIn(tokenNotShopOwnerUserName)).thenReturn(true);
        
        _passwordEncoder = new PasswordEncoderUtil();
        
        User shopFounder = new User("Founder", _passwordEncoder.encodePassword("shopFounderPassword"), "email@email.com", new Date());
        User shopOwner = new User("shopOwner", _passwordEncoder.encodePassword("shopOwnerPassword"), "email@email.com", new Date());
        User NotShopOwnerUserName = new User("NotShopOwnerUserName", _passwordEncoder.encodePassword("NotShopOwnerUserNamePassword"), "email@email.com", new Date());
        ShopDto shopDto = new ShopDto("shopName", "bankDetails", "address");
        ProductDto productDto = new ProductDto(productName, Category.CLOTHING, 10);

        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(shopFounder);
                add(shopOwner);
                add(NotShopOwnerUserName);
            }
        }, new ArrayList<>(), _passwordEncoder);

        _shopFacade = new ShopFacade();

        try {
            _shopFacade.openNewShop("Founder", shopDto);
        } catch (StockMarketException e) {
            e.printStackTrace();
            logger.warning("testShopOwnerAddProductToShop Error message: " + e.getMessage());
            return false;
        }

        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);
        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userFacade);

        // Act
        Response res1 = _shopServiceUnderTest.addShopOwner(tokenShopFounder, Integer.parseInt(shopId), "shopOwner");
        Response res3 = _shopServiceUnderTest.addProductToShop(tokenShopOwner, Integer.parseInt(shopId), productDto);
        Response res4 = _shopServiceUnderTest.removeProductFromShop(tokenShopOwner, Integer.parseInt(shopId), productDto);

        // Assert
        if (res1.getErrorMessage() != null){
            logger.info("testShopOwnerRemoveProductFromShop Error message: " + res1.getErrorMessage());
            return false;
        }
        if (res3.getErrorMessage() != null){
            logger.info("testShopOwnerRemoveProductFromShop Error message: " + res3.getErrorMessage());
            return false;
        }
        if (res4.getErrorMessage() != null){
            logger.info("testShopOwnerRemoveProductFromShop Error message: " + res4.getErrorMessage());
            return false;
        }
        return res4.getErrorMessage() == null;
    }
    
    @Test
    public boolean testShopOwnerEditProductInShop(String username, String shopId, String productName, String productNameNew, String productAmount, String productAmountNew){
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testShopOwnerEditProductInShop'");
    }
    
    @Test
    public boolean testShopOwnerChangeShopPolicies(String username, String shopId, String newPolicy){return false;}

    @Test
    public boolean testShopOwnerAppointAnotherShopOwner(String username, String shopId, String newOwnerUsername){
        // Arrange
        MockitoAnnotations.openMocks(this);

        String tokenShopFounder = "shopOwnerUserName";

        when(_tokenServiceMock.validateToken(tokenShopFounder)).thenReturn(true);
        when(_tokenServiceMock.extractUsername(tokenShopFounder)).thenReturn(username);
        when(_tokenServiceMock.isUserAndLoggedIn(tokenShopFounder)).thenReturn(true);

        _passwordEncoder = new PasswordEncoderUtil();

        User shopFounder = new User("shopOwnerUserName", _passwordEncoder.encodePassword("shopFounderPassword"), "email@email.com", new Date());
        User existOwner = new User("existOwner", _passwordEncoder.encodePassword("existOwnerPassword"), "email@email.com", new Date());
        User newOwner = new User("newOwner", _passwordEncoder.encodePassword("newOwnerPassword"), "email@email.com", new Date());
        ShopDto shopDto = new ShopDto("shopName", "bankDetails", "address");

        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(shopFounder);
                add(existOwner);
                add(newOwner);
            }
        }, new ArrayList<>(), _passwordEncoder);

        _shopFacade = new ShopFacade();
        
        try {
            _shopFacade.openNewShop("shopOwnerUserName", shopDto);
        } catch (StockMarketException e) {
            e.printStackTrace();
            logger.warning("testShopOwnerAppointAnotherShopOwner Error message: " + e.getMessage());
            return false;
        }

        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);
        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userFacade);

        // Act
        Response res1 = _shopServiceUnderTest.addShopOwner(tokenShopFounder, Integer.parseInt(shopId), "existOwner");
        Response res2 = _shopServiceUnderTest.addShopOwner(tokenShopFounder, Integer.parseInt(shopId), newOwnerUsername);

        // Assert
        if (res1.getErrorMessage() != null){
            logger.info("testShopOwnerAppointAnotherShopOwner Error message: " + res1.getErrorMessage());
            return false;
        }
        if (res2.getErrorMessage() != null){
            logger.info("testShopOwnerAppointAnotherShopOwner Error message: " + res2.getErrorMessage());
            return false;
        }
        return res2.getErrorMessage() == null;
    }
    
    @Test
    public boolean testShopOwnerAppointAnotherShopManager(String username, String shopId, String newManagerUsername){
        // Arrange
        MockitoAnnotations.openMocks(this);

        String tokenShopFounder = "shopOwnerUserName";

        when(_tokenServiceMock.validateToken(tokenShopFounder)).thenReturn(true);
        when(_tokenServiceMock.extractUsername(tokenShopFounder)).thenReturn(username);
        when(_tokenServiceMock.isUserAndLoggedIn(tokenShopFounder)).thenReturn(true);

        _passwordEncoder = new PasswordEncoderUtil();

        User shopFounder = new User("shopOwnerUserName", _passwordEncoder.encodePassword("shopFounderPassword"), "email@email.com", new Date());
        User existManager = new User("existManager", _passwordEncoder.encodePassword("existManagerPassword"), "email@email.com", new Date());
        User newManager = new User("newManager", _passwordEncoder.encodePassword("newManagerPassword"), "email@email.com", new Date());
        ShopDto shopDto = new ShopDto("shopName", "bankDetails", "address");

        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(shopFounder);
                add(existManager);
                add(newManager);
            }
        }, new ArrayList<>(), _passwordEncoder);

        _shopFacade = new ShopFacade();

        try {
            _shopFacade.openNewShop("shopOwnerUserName", shopDto);
        } catch (StockMarketException e) {
            e.printStackTrace();
            logger.warning("testShopOwnerAppointAnotherShopManager Error message: " + e.getMessage());
            return false;
        }

        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userFacade);

        Set<String> permissions = new HashSet<>();
        permissions.add("ADD_PRODUCT");

        // Act
        Response res1 = _shopServiceUnderTest.addShopManager(tokenShopFounder, Integer.parseInt(shopId), "existManager", permissions);
        Response res2 = _shopServiceUnderTest.addShopManager(tokenShopFounder, Integer.parseInt(shopId), newManagerUsername, permissions);

        // Assert
        if (res1.getErrorMessage() != null){
            logger.info("testShopOwnerAppointAnotherShopManager Error message: " + res1.getErrorMessage());
            return false;
        }
        if (res2.getErrorMessage() != null){
            logger.info("testShopOwnerAppointAnotherShopManager Error message: " + res2.getErrorMessage());
            return false;
        }
        return res2.getErrorMessage() == null;
    }
    
    @Test
    public boolean testShopOwnerAddShopManagerPermission(String username, String shopId, String managerUsername, String permission){
        // Arrange
        MockitoAnnotations.openMocks(this);

        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        when(_tokenServiceMock.extractUsername(token)).thenReturn(username);
        when(_tokenServiceMock.isUserAndLoggedIn(token)).thenReturn(true);

        _passwordEncoder = new PasswordEncoderUtil();

        User shopOwner = new User("shopOwner", _passwordEncoder.encodePassword("shopOwnerPassword"), "email@email.com", new Date());
        User shopManager = new User("managerUserName", _passwordEncoder.encodePassword("shopManagerPassword"), "email@EMAIL.COM", new Date());
        ShopDto shopDto = new ShopDto("shopName", "bankDetails", "address");

        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(shopOwner);
                add(shopManager);
            }
        }, new ArrayList<>(), _passwordEncoder);

        _shopFacade = new ShopFacade();

        try {
            _shopFacade.openNewShop("shopOwner", shopDto);
        } catch (StockMarketException e) {
            e.printStackTrace();
            logger.warning("testShopOwnerGetShopManagersPermissions Error message: " + e.getMessage());
            return false;
        }

        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userFacade);

        Set<String> permissions = new HashSet<>();
        permissions.add("ADD_PRODUCT");

        Set<String> permissionsToRemove = new HashSet<>();
        if(permission.equals("newPermission")){
            permissionsToRemove.add("ADD_PRODUCT");
        }
        if (permission.equals("invalidPermission")){
            permissionsToRemove.add("NON_EXIST_PERMISSION");
        }
        if (permission.equals("nonexistPermission")){
            permissionsToRemove.add("EDIT_PRODUCT");
        }
        
        // Act
        Response res1 = _shopServiceUnderTest.addShopManager(token, Integer.parseInt(shopId), "managerUserName", permissions);
        Response res2 = _shopServiceUnderTest.modifyManagerPermissions(token, Integer.parseInt(shopId), "managerUserName", permissionsToRemove);
        
        // Assert
        if (res1.getErrorMessage() != null){
            logger.info("testShopOwnerRemoveShopManagerPermission Error message: " + res1.getErrorMessage());
            return false;            
        }
        if (res2.getErrorMessage() != null){
            logger.info("testShopOwnerRemoveShopManagerPermission Error message: " + res2.getErrorMessage());
            return false;            
        }
        return res2.getErrorMessage() == null;
    }
    
    @Test
    public boolean testShopOwnerRemoveShopManagerPermission(String username, String shopId, String managerUsername, String permission){
        // Arrange
        MockitoAnnotations.openMocks(this);

        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        when(_tokenServiceMock.extractUsername(token)).thenReturn(username);
        when(_tokenServiceMock.isUserAndLoggedIn(token)).thenReturn(true);

        _passwordEncoder = new PasswordEncoderUtil();

        User shopOwner = new User("shopOwner", _passwordEncoder.encodePassword("shopOwnerPassword"), "email@email.com", new Date());
        User shopManager = new User("managerUserName", _passwordEncoder.encodePassword("shopManagerPassword"), "email@EMAIL.COM", new Date());
        ShopDto shopDto = new ShopDto("shopName", "bankDetails", "address");

        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(shopOwner);
                add(shopManager);
            }
        }, new ArrayList<>(), _passwordEncoder);

        _shopFacade = new ShopFacade();

        try {
            _shopFacade.openNewShop("shopOwner", shopDto);
        } catch (StockMarketException e) {
            e.printStackTrace();
            logger.warning("testShopOwnerGetShopManagersPermissions Error message: " + e.getMessage());
            return false;
        }

        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userFacade);

        Set<String> permissions = new HashSet<>();
        permissions.add("ADD_PRODUCT");

        Set<String> permissionsToRemove = new HashSet<>();
        if(permission.equals("existPermission")){
            permissionsToRemove.add("ADD_PRODUCT");
        }
        if (permission.equals("invalidPermission")){
            permissionsToRemove.add("NON_EXIST_PERMISSION");
        }
        if (permission.equals("nonexistPermission")){
            permissionsToRemove.add("EDIT_PRODUCT");
        }
        
        // Act
        Response res1 = _shopServiceUnderTest.addShopManager(token, Integer.parseInt(shopId), "managerUserName", permissions);
        Response res2 = _shopServiceUnderTest.modifyManagerPermissions(token, Integer.parseInt(shopId), "managerUserName", permissionsToRemove);
        
        // Assert
        if (res1.getErrorMessage() != null){
            logger.info("testShopOwnerRemoveShopManagerPermission Error message: " + res1.getErrorMessage());
            return false;            
        }
        if (res2.getErrorMessage() != null){
            logger.info("testShopOwnerRemoveShopManagerPermission Error message: " + res2.getErrorMessage());
            return false;            
        }
        return res2.getErrorMessage() == null;
    }
    
    @Test
    public boolean testShopOwnerCloseShop(String username, String shopId){
        // Arrange
        MockitoAnnotations.openMocks(this);

        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        when(_tokenServiceMock.extractUsername(token)).thenReturn(username);
        when(_tokenServiceMock.isUserAndLoggedIn(token)).thenReturn(true);

        _passwordEncoder = new PasswordEncoderUtil();

        User shopOwner = new User("Founder", _passwordEncoder.encodePassword("shopOwnerPassword"), "email@email.com", new Date());
        User userName = new User("userName", _passwordEncoder.encodePassword("userNamePassword"), "email@email.com", new Date());
        ShopDto shopDto = new ShopDto("shopName", "bankDetails", "address");

        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(shopOwner);
                add(userName);
            }
        }, new ArrayList<>(), _passwordEncoder);

        _shopFacade = new ShopFacade();

        try {
            _shopFacade.openNewShop("Founder", shopDto);
        } catch (StockMarketException e) {
            e.printStackTrace();
            logger.warning("testShopOwnerCloseShop Error message: " + e.getMessage());
            return false;
        }

        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userFacade);

        // Act
        Response res = _shopServiceUnderTest.closeShop(token, Integer.parseInt(shopId));

        // Assert
        logger.info("testShopOwnerCloseShop Error message: " + res.getErrorMessage());
        return res.getErrorMessage() == null;
    }
    
    @Test
    public boolean testShopOwnerGetShopInfo(String username, String shopId){
        MockitoAnnotations.openMocks(this);

        String tokenShopOwner = "shopOwner";

        when(_tokenServiceMock.validateToken(tokenShopOwner)).thenReturn(true);
        when(_tokenServiceMock.extractUsername(tokenShopOwner)).thenReturn(username);
        when(_tokenServiceMock.isUserAndLoggedIn(tokenShopOwner)).thenReturn(true);

        _passwordEncoder = new PasswordEncoderUtil();

        User shopOwner = new User("shopOwner", _passwordEncoder.encodePassword("shopOwnerPassword"), "email@email.com", new Date());
        ShopDto shopDto = new ShopDto("shopName", "bankDetails", "address");

        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(shopOwner);
            }
        }, new ArrayList<>(), _passwordEncoder);

        _shopFacade = new ShopFacade();

        try {
            _shopFacade.openNewShop("shopOwner", shopDto);
        } catch (StockMarketException e) {
            e.printStackTrace();
            logger.warning("testShopOwnerGetShopInfo Error message: " + e.getMessage());
            return false;
        }

        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userFacade);

        // Act
        Response res1 = _shopServiceUnderTest.displayShopGeneralInfo(tokenShopOwner, Integer.parseInt(shopId));

        // Assert
        logger.info("testShopOwnerGetShopInfo Error message: " + res1.getErrorMessage());
        return res1.getErrorMessage() == null;
    }
    
    @Test
    public boolean testShopOwnerGetShopManagersPermissions(String username, String shopId){ return false; }
    
    @Test
    public boolean testShopOwnerViewHistoryPurcaseInShop(String username, String shopId){
        // Arrange
        MockitoAnnotations.openMocks(this);
        _passwordEncoder = new PasswordEncoderUtil();
        
        User shopOwner = new User("shopOwner", _passwordEncoder.encodePassword("shopOwnerPassword"), "email@email.com",
                new Date());
        ShopDto shopDto = new ShopDto("shopName", "bankDetails", "address");

        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(shopOwner);
            }
        }, new ArrayList<>(), _passwordEncoder);

        _shopFacade = new ShopFacade();

        try {
            _shopFacade.openNewShop("shopOwner", shopDto);
        } catch (StockMarketException e) {
            e.printStackTrace();
            logger.warning("testShopOwnerViewHistoryPurcaseInShop Error message: " + e.getMessage());
            return false;
        }

        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);
        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userFacade);

        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        when(_tokenServiceMock.extractUsername(token)).thenReturn(username);
        when(_tokenServiceMock.isUserAndLoggedIn(token)).thenReturn(true);

        // Act
        Response res = _shopServiceUnderTest.getShopPurchaseHistory(token, Integer.parseInt(shopId));

        // Assert
        logger.info("testShopOwnerViewHistoryPurcaseInShop Error message: " + res.getErrorMessage());
        return res.getErrorMessage() == null;
    }
    
    //  --------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean testGetShopInfoAsGuest(String shopId) {
        // TODO Auto-generated method stub
        // #416
        throw new UnsupportedOperationException("Unimplemented method 'testGetShopInfoAsGuest'");
    }

    @Override
    public boolean testGetProductInfoAsGuest(String productId) {
        // TODO Auto-generated method stub
        // #416
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoAsGuest'");
    }

    @Override
    public boolean testGetProductInfoUsingProductNameAsGuest(String productId) {
        // TODO Auto-generated method stub
        // #416
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingProductNameAsGuest'");
    }

    @Override
    public boolean testGetProductInfoUsingProductCategoryAsGuest(String category) {
        // TODO Auto-generated method stub
        // #416
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingProductCategoryAsGuest'");
    }

    @Override
    public boolean testGetProductInfoUsingKeyWordsAsGuest(String kewWord) {
        // TODO Auto-generated method stub
        // #416
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingKeyWordsAsGuest'");
    }

    @Override
    public boolean testGetProductInfoUsingKeyWordsAsGuest(String kewWord1, String kewWord2) {
        // TODO Auto-generated method stub
        // #416
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingKeyWordsAsGuest'");
    }

    @Override
    public boolean testGetProductInfoUsingProductNameInShopAsGuest(String productId, String shopId) {
        // TODO Auto-generated method stub
        // #416
        throw new UnsupportedOperationException(
                "Unimplemented method 'testGetProductInfoUsingProductNameInShopAsGuest'");
    }

    @Override
    public boolean testGetProductInfoUsingProductCategoryInShopAsGuest(String category, String shopId) {
        // TODO Auto-generated method stub
        // #416
        throw new UnsupportedOperationException(
                "Unimplemented method 'testGetProductInfoUsingProductCategoryInShopAsGuest'");
    }

    @Override
    public boolean testGetProductInfoUsingKeyWordsInShopAsGuest(String kewWord, String shopId) {
        // TODO Auto-generated method stub
        // #416
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
        // TODO Gili getShopGeneralInfo - #416
        throw new UnsupportedOperationException("Unimplemented method 'testGetShopInfoAsUser'");
    }

    @Override
    public boolean testGetProductInfoAsUser(String productId) {
        // TODO Auto-generated method stub
        // TODO Gili getProductGeneralInfo - #416
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoAsUser'");
    }

    @Override
    public boolean testGetProductInfoUsingProductNameAsUser(String productId) {
        // TODO Auto-generated method stub
        // TODO Gili getProductGeneralInfo #416
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingProductNameAsUser'");
    }

    @Override
    public boolean testGetProductInfoUsingProductCategoryAsUser(String category) {
        // TODO Auto-generated method stub
        // TODO Gili #416
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingProductCategoryAsUser'");
    }

    @Override
    public boolean testGetProductInfoUsingKeyWordsAsUser(String keyWord) {
        // #416
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingKeyWordsAsUser'");
    }

    @Override
    public boolean testGetProductInfoUsingKeyWordsAsUser(String keyWord1, String keyWord2) {
        // #416
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingKeyWordsAsUser'");
    }

    @Override
    public boolean testGetProductInfoUsingProductNameInShop(String productId, String shopId) {
        // #416
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testGetProductInfoUsingProductNameInShop'");
    }

    @Override
    public boolean testGetProductInfoUsingProductNameInShopAsUser(String productId, String shopId) {
        // #416
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException(
                "Unimplemented method 'testGetProductInfoUsingProductNameInShopAsUser'");
    }

    @Override
    public boolean testGetProductInfoUsingProductCategoryInShopAsUser(String category, String shopId) {
        // #416
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException(
                "Unimplemented method 'testGetProductInfoUsingProductCategoryInShopAsUser'");
    }

    @Override
    public boolean testGetProductInfoUsingKeyWordsInShopAsUser(String keyWord1, String shopId) {
        // #416
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
        // try {
        // int number = Integer.parseInt(s);
        // busketsToBuyList.add(number);
        // } catch (NumberFormatException e) {
        // // Handle the case where the string cannot be parsed to an integer
        // System.err.println("Invalid number format: " + s);
        // }
        // }

        // try {
        // when(_tokenServiceMock.isGuest(token)).thenReturn(false);
        // when(_tokenServiceMock.extractUsername(token)).thenReturn(username);
        // // TODO: not sure how to handle the payment method and supply in
        // shopppingCart
        // _userServiceMock.purchaseCart(token, busketsToBuyList, cardNumber, address);

        // // Verify interactions
        // verify(_userServiceMock, times(1)).purchaseCart(token, busketsToBuyList,
        // cardNumber, address);
        // verify(_tokenServiceMock, times(1)).validateToken(token);
        // verify(_tokenServiceMock, times(1)).isGuest(token);

        // return true;
        // } catch (Exception e) {
        // return false;
        // }
    }

    @Override
    public boolean testBuyingShoppingCartPoliciesUser(String username, String password) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testBuyingShoppingCartPoliciesUser'");
    }

    @Override
    public boolean testLogoutToTheSystem(String username) {
        // Arrange
        MockitoAnnotations.openMocks(this);
        
        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        when(_tokenServiceMock.extractUsername(token)).thenReturn(username);
        when(_tokenServiceMock.generateGuestToken()).thenReturn(_tokenService.generateGuestToken());
        when(_passwordEncoderMock.decodePassword("password")).thenReturn("password");
        when(_passwordEncoderMock.encodePassword("password")).thenReturn("password");
        
        // create a user in the system
        User user = new User("Bob", "password", "email@email.com", new Date());
        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(user);
            }
        }, new ArrayList<>(), _passwordEncoderMock);

        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);

        // login the user
        _userServiceUnderTest.logIn(token, username, "password");

        // Act
        Response res = _userServiceUnderTest.logOut(token);

        // Assert
        logger.info("testLogoutToTheSystem Error message: " + res.getErrorMessage());
        return res.getErrorMessage() == null;
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
    public boolean TestUserOpenAShop(String username, String password, String shopName, String bankDetails,
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
        // when(_shopFacadeMock.isShopIdExist(Integer.valueOf("879"))).thenAnswer(invocation
        // -> {
        // throw new IllegalArgumentException();
        // });

        // } catch (Exception e) {
        // return false;
        // }

        // Response response = _shopServiceMock.openNewShop(token,
        // Integer.valueOf(shopId), username, bankDetails,
        // shopAddress);

        // // Verify interactions
        // verify(_shopServiceMock, times(1)).openNewShop(token,
        // Integer.valueOf(shopId), username, bankDetails,
        // shopAddress);
        // return response.getErrorMessage() == null;

    }

    @Override
    public boolean TestUserWriteReviewOnPurchasedProduct(String username, String password, String productId) {
        
        // assertTrue(_bridge.TestUserWriteReviewOnPurchasedProduct("bob","bobspassword", "product1") ); // success - the user secceeded to write a review
        // assertFalse(_bridge.TestUserWriteReviewOnPurchasedProduct("bob","bobspassword", "product2") ); // fail - the user did not porchased this product

        // Arrange
        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        when(_tokenServiceMock.extractUsername(token)).thenReturn(username);
        when(_tokenServiceMock.isUserAndLoggedIn(token)).thenReturn(true);

        // initiate a user object
        User user = new User(username, password, "email@email.com", new Date());
        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(user);
            }
        }, new ArrayList<>(), _passwordEncoder);

        // initiate userServiceUnderTest
        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);

        // create a shopingcart for the username
        _shoppingCartFacade.addCartForGuest(username);
        _shoppingCartFacade.addCartForUser(username, user);
        
        // this user opens a shop using ShopSerivce
        ShopDto shopDto = new ShopDto("shopName", "bankDetails", "address");
        Response res1 = _shopServiceUnderTest.openNewShop(token, shopDto);

        // this user adds a product to the shop using ShopSerivce
        ProductDto productDto = new ProductDto(productId, Category.CLOTHING, 100);
        Response res2 = _shopServiceUnderTest.addProductToShop(token, 0, productDto);

        // this user adds a product to the shopping cart using UserService
        Response res3 = _userServiceUnderTest.addProductToShoppingCart(token, 0, 0);

        // this user buys the product using UserService
        List<Integer> shoppingBackets = new ArrayList<>();
        shoppingBackets.add(0);
        PurchaseCartDetailsDto purchaseCartDetailsDto = new PurchaseCartDetailsDto(shoppingBackets, "123456789", "address");
        Response res4 = _userServiceUnderTest.purchaseCart(token, purchaseCartDetailsDto);

        // Act
        Response res5 = _userServiceUnderTest.writeReview(token, Integer.parseInt(productId), 0, "review");

        // Assert
        if(res1.getErrorMessage() != null)
            logger.info("TestUserWriteReviewOnPurchasedProduct Error message: " + res1.getErrorMessage());
        if(res2.getErrorMessage() != null)
            logger.info("TestUserWriteReviewOnPurchasedProduct Error message: " + res2.getErrorMessage());
        if(res3.getErrorMessage() != null)
            logger.info("TestUserWriteReviewOnPurchasedProduct Error message: " + res3.getErrorMessage());
        if(res4.getErrorMessage() != null)
            logger.info("TestUserWriteReviewOnPurchasedProduct Error message: " + res4.getErrorMessage());
        logger.info("TestUserWriteReviewOnPurchasedProduct Error message: " + res5.getErrorMessage());
        return res5.getErrorMessage() == null;
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
    public boolean testAddProductToShoppingCartUser(String username, String productId, String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testAddProductToShoppingCartUser'");
    }

    @Override
    public boolean testAddProductToShoppingCartGuest(String username, String productId, String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testAddProductToShoppingCartGuest'");
    }
}
