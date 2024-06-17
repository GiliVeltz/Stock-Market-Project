package AcceptanceTests.Implementor;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import Domain.*;
import Domain.Authenticators.*;
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
    private TokenService _tokenServiceMock;
    @Mock
    ShopFacade _shopFacadeMock;
    @Mock
    ShoppingBasket _shoppingBasketMock;
    @Mock
    ShoppingCartFacade _shoppingCartFacadeMock;
    @Mock
    PasswordEncoderUtil _passwordEncoderMock;

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
        }, new ArrayList<>());
        _externalServiceHandler = new ExternalServiceHandler();
        _passwordEncoder = new PasswordEncoderUtil();
        _tokenService = TokenService.getTokenService();

        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);
        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userFacade);
        _systemServiceUnderTest = new SystemService(_externalServiceHandler, _tokenServiceMock,
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
        _passwordEncoder = new PasswordEncoderUtil();
        _externalServiceHandler = new ExternalServiceHandler();
        _shopFacade = ShopFacade.getShopFacade();
        _shoppingCartFacade = ShoppingCartFacade.getShoppingCartFacade();
        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(new User("systemAdmin", _passwordEncoder.encodePassword("systemAdminPassword"), "email@example.com", new Date()));
            }
        }, new ArrayList<>());
        try {
            _userFacade.getUserByUsername("systemAdmin").setIsSystemAdmin(true);
        } catch (StockMarketException e) {
            e.printStackTrace();
            logger.info("testOpenMarketSystem Error message: " + e.getMessage());
            return false;
        }

        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);
        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userFacade);
        _systemServiceUnderTest = new SystemService(_externalServiceHandler, _tokenServiceMock,
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
        ResponseEntity<Response> res = _systemServiceUnderTest.openSystem(token);

        // Assert
        logger.info("testOpenMarketSystem Error message: " + res.getBody().getErrorMessage());
        return res.getBody().getErrorMessage() == null;
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
        , new ArrayList<>());

        _shoppingCartFacade.addCartForGuest("manager");
        try {
            _userFacade.getUserByUsername("manager").setIsSystemAdmin(true);
        } catch (Exception e) {
            logger.info("testAddExternalService Error message: " + e.getMessage());
            return false;
        }

        ExternalServiceDto externalServiceDto = new ExternalServiceDto(-1, "existSerivce", "name", "111");
        
        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);
        _systemServiceUnderTest = new SystemService(_externalServiceHandler, _tokenServiceMock,
                _userFacade, _shoppingCartFacade);

        _externalServiceHandler.addExternalService(externalServiceDto);

        ResponseEntity<Response> res1 = _userServiceUnderTest.logIn(token, "manager", "managerPassword");
        if(res1.getBody().getErrorMessage() != null){
            logger.info("testAddExternalService Error message: " + res1.getBody().getErrorMessage());
            return false;
        }

        ResponseEntity<Response> res2 = _systemServiceUnderTest.openSystem(token);
        if(res2.getBody().getErrorMessage() != null){
            logger.info("testAddExternalService Error message: " + res2.getBody().getErrorMessage());
            return false;
        }
        ExternalServiceDto externalServiceDto2 = new ExternalServiceDto(-1, newSerivceName, informationPersonName, informationPersonPhone);

        // Act
        ResponseEntity<Response> res = _systemServiceUnderTest.addExternalService(token, externalServiceDto2);

        // Assert
        logger.info("testAddExternalService Error message: " + res.getBody().getErrorMessage());
        return res.getBody().getErrorMessage() == null;
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
        , new ArrayList<>());

        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);
        _systemServiceUnderTest = new SystemService(_externalServiceHandler, _tokenServiceMock,
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

        ResponseEntity<Response> res1 = _userServiceUnderTest.logIn(token, "manager", "managerPassword");
        if(res1.getBody().getErrorMessage() != null){
            logger.info("testChangeExternalService Error message: " + res1.getBody().getErrorMessage());
            return false;
        }

        ResponseEntity<Response> res2 = _systemServiceUnderTest.openSystem(token);
        if(res2.getBody().getErrorMessage() != null){
            logger.info("testChangeExternalService Error message: " + res2.getBody().getErrorMessage());
            return false;
        }
        ExternalServiceDto externalServiceDto2 = new ExternalServiceDto(oldServiceSystemId, newSerivceName, "name", "111");

        // Act
        ResponseEntity<Response> res3 = _systemServiceUnderTest.changeExternalServiceName(token, externalServiceDto, newSerivceName);
        ResponseEntity<Response> res4 = _systemServiceUnderTest.changeExternalServiceInformationPersonName(token, externalServiceDto2, newInformationPersonName);
        ResponseEntity<Response> res5 = _systemServiceUnderTest.changeExternalServiceInformationPersonPhone(token, externalServiceDto2, newInformationPersonPhone);

        // Assert
        if(res3.getBody().getErrorMessage() != null)
            logger.info("changeExternalServiceName Error message: " + res3.getBody().getErrorMessage());
        if(res4.getBody().getErrorMessage() != null)
            logger.info("changeExternalServiceInformationPersonName Error message: " + res4.getBody().getErrorMessage());
        if(res5.getBody().getErrorMessage() != null)
        logger.info("changeExternalServiceInformationPersonPhone Error message: " + res5.getBody().getErrorMessage());
        
        return res3.getBody().getErrorMessage() == null && res4.getBody().getErrorMessage() == null && res5.getBody().getErrorMessage() == null;
    }

    // GUEST TESTS
    // --------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Test
    public boolean TestGuestEnterTheSystem(String shouldSeccess) {
        // Arrange
        MockitoAnnotations.openMocks(this);
        _shopFacade = ShopFacade.getShopFacade();
        _shoppingCartFacade = ShoppingCartFacade.getShoppingCartFacade();
        _userFacade = new UserFacade(new ArrayList<User>(), new ArrayList<>());
        _externalServiceHandler = new ExternalServiceHandler();

        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);
        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userFacade);
        _systemServiceUnderTest = new SystemService(_externalServiceHandler, _tokenServiceMock,
                _userFacade, _shoppingCartFacade);

        _userFacade.addNewGuest("existGuest");

        String token = shouldSeccess.equals("newGuest") ? "newGuest" : "existGuest";

        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        when(_tokenServiceMock.extractGuestId("newGuest")).thenReturn("newGuest");
        when(_tokenServiceMock.extractGuestId("existGuest")).thenReturn("existGuest");
        when(_tokenServiceMock.generateGuestToken()).thenReturn(token);

        // Act
        ResponseEntity<Response> res = _systemServiceUnderTest.requestToEnterSystem();

        // Assert
        logger.info("TestGuestEnterTheSystem Error message: " + res.getBody().getErrorMessage());
        return res.getBody().getErrorMessage() == null;
    }

    @Test
    public boolean TestGuestRegisterToTheSystem(String username, String password, String email) {
        // Arrange
        MockitoAnnotations.openMocks(this);
        _externalServiceHandler = new ExternalServiceHandler();
        _shopFacade = ShopFacade.getShopFacade();
        _shoppingCartFacade = ShoppingCartFacade.getShoppingCartFacade();
        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(new User("Bobi", "encodePassword", "email@example.com",
                        new Date()));
            }
        }, new ArrayList<>());

        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);
        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userFacade);
        _systemServiceUnderTest = new SystemService(_externalServiceHandler, _tokenServiceMock,
                _userFacade, _shoppingCartFacade);

        when(_tokenServiceMock.validateToken(token)).thenReturn(true);

        // Act
        UserDto userDto = new UserDto(username, password, email, new Date());
        ResponseEntity<Response> res = _userServiceUnderTest.register(token, userDto);

        // Assert
        logger.info("TestGuestRegisterToTheSystem Error message: " + res.getBody().getErrorMessage());
        return res.getBody().getErrorMessage() == null;
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
        }, new ArrayList<>());

        _shoppingCartFacade.addCartForGuest(username);

        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);
        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userFacade);
        _systemServiceUnderTest = new SystemService(_externalServiceHandler, _tokenServiceMock,
                _userFacade, _shoppingCartFacade);

        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        when(_tokenServiceMock.extractGuestId(token)).thenReturn(username);

        // Act
        ResponseEntity<Response> res = _userServiceUnderTest.logIn(token, username, password);

        // Assert
        logger.info("testLoginToTheSystem Error message: " + res.getBody().getErrorMessage());
        return res.getBody().getErrorMessage() == null;
    }

    // SYSTEM ADMIN TESTS --------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Test
    public boolean testSystemManagerViewHistoryPurcaseInUsers(String managerName, String userName){
        // Arrange
        MockitoAnnotations.openMocks(this);
        
        User manager = new User(managerName, "managersPassword", "email@email.com",
                new Date());
        manager.setIsSystemAdmin(true);
        User guest = new User("guest", "guest", "email@email.com", new Date());
        User user = new User("userName", "userName", "email@email.com", new Date());

        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(manager);
                add(guest);
                add(user);
            }
        }, new ArrayList<>());

        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);

        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        when(_tokenServiceMock.extractUsername(token)).thenReturn(managerName);
        when(_tokenServiceMock.isUserAndLoggedIn(token)).thenReturn(true);

        // Act
        ResponseEntity<Response> res = _userServiceUnderTest.getUserPurchaseHistory(token, userName);

        // Assert
        logger.info("testSystemManagerViewHistoryPurcaseInUsers Error message: " + res.getBody().getErrorMessage());
        return res.getBody().getErrorMessage() == null;
    }

    @Test
    public boolean testSystemManagerViewHistoryPurcaseInShops(String namanger, Integer shopId){
        // Arrange
        MockitoAnnotations.openMocks(this);
        
        User manager = new User("manager", "managersPassword", "email@email.com",
                new Date());
        manager.setIsSystemAdmin(true);

        ShopDto shopDto = new ShopDto("shopName" ,"bankDetails", "address");

        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(manager);
            }
        }, new ArrayList<>());

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
        ResponseEntity<Response> res = _shopServiceUnderTest.getShopPurchaseHistory(token, shopId);

        // Assert
        logger.info("testSystemManagerViewHistoryPurcaseInShops Error message: " + res.getBody().getErrorMessage());
        return res.getBody().getErrorMessage() == null;
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
        }, new ArrayList<>());
        _externalServiceHandler = new ExternalServiceHandler();
        
        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);
        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userFacade);
        _systemServiceUnderTest = new SystemService(_externalServiceHandler, _tokenServiceMock,
                _userFacade, _shoppingCartFacade);

        ShopDto shopDto = new ShopDto("shopName", "bankDetails", "address");
        _shopServiceUnderTest.openNewShop(token, shopDto);
        
        Set<String> permissions = new HashSet<>();
        if(permission.equals("possiblePermission")){
            permissions.add("ADD_PRODUCT");
        }
        
        _shopServiceUnderTest.addShopManager(token, shopId, username, permissions);

        // Act
        ResponseEntity<Response> res = _shopServiceUnderTest.addProductToShop(token, shopId, new ProductDto("productName", Category.CLOTHING, 100, 1));

        // Assert
        logger.info("testPermissionForShopManager Error message: " + res.getBody().getErrorMessage());
        return res.getBody().getErrorMessage() == null;
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
        ProductDto productDto = new ProductDto(productName, Category.CLOTHING, Integer.parseInt(productAmount), 1);
        ProductDto productExistDto = new ProductDto("ExistProductName", Category.CLOTHING, Integer.parseInt(productAmount), 1);

        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(shopFounder);
                add(shopOwner);
            }
        }, new ArrayList<>());

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
        ResponseEntity<Response> res1 = _shopServiceUnderTest.addShopOwner(tokenShopFounder, Integer.parseInt(shopId), "shopOwner");
        ResponseEntity<Response> res2 = _shopServiceUnderTest.addProductToShop(tokenShopOwner, Integer.parseInt(shopId), productExistDto);
        ResponseEntity<Response> res3 = _shopServiceUnderTest.addProductToShop(tokenShopOwner, Integer.parseInt(shopId), productDto);

        // Assert
        if (res1.getBody().getErrorMessage() != null){
            logger.info("testShopOwnerAddProductToShop Error message: " + res1.getBody().getErrorMessage());
            return false;
        }
        if (res2.getBody().getErrorMessage() != null){
            logger.info("testShopOwnerAddProductToShop Error message: " + res2.getBody().getErrorMessage());
            return false;
        }
        if (res3.getBody().getErrorMessage() != null){
            logger.info("testShopOwnerAddProductToShop Error message: " + res3.getBody().getErrorMessage());
            return false;
        }
        return res3.getBody().getErrorMessage() == null;
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
        ProductDto productDto = new ProductDto(productName, Category.CLOTHING, 10, 1);

        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(shopFounder);
                add(shopOwner);
                add(NotShopOwnerUserName);
            }
        }, new ArrayList<>());

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
        ResponseEntity<Response> res1 = _shopServiceUnderTest.addShopOwner(tokenShopFounder, Integer.parseInt(shopId), "shopOwner");
        ResponseEntity<Response> res3 = _shopServiceUnderTest.addProductToShop(tokenShopOwner, Integer.parseInt(shopId), productDto);
        ResponseEntity<Response> res4 = _shopServiceUnderTest.removeProductFromShop(tokenShopOwner, Integer.parseInt(shopId), productDto);

        // Assert
        if (res1.getBody().getErrorMessage() != null){
            logger.info("testShopOwnerRemoveProductFromShop Error message: " + res1.getBody().getErrorMessage());
            return false;
        }
        if (res3.getBody().getErrorMessage() != null){
            logger.info("testShopOwnerRemoveProductFromShop Error message: " + res3.getBody().getErrorMessage());
            return false;
        }
        if (res4.getBody().getErrorMessage() != null){
            logger.info("testShopOwnerRemoveProductFromShop Error message: " + res4.getBody().getErrorMessage());
            return false;
        }
        return res4.getBody().getErrorMessage() == null;
    }

    @Test
    public boolean testShopOwnerEditProductInShop(String username, String shopId, String productName, String productNameNew, String productAmount, String productAmountNew){
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
        User shopOwner = new User("shopOwnerUserName", _passwordEncoder.encodePassword("shopOwnerPassword"), "email@email.com", new Date());

        ShopDto shopDto = new ShopDto("shopName", "bankDetails", "address");
        
        ProductDto productDto = new ProductDto("ProductName", Category.CLOTHING, Integer.parseInt(productAmount), 1);
        ProductDto productDtoNew = new ProductDto(productNameNew, Category.CLOTHING, Integer.parseInt(productAmountNew), 1);
        ProductDto productDtoExist = new ProductDto("ExistProductName", Category.CLOTHING, Integer.parseInt(productAmountNew), 1);

        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(shopFounder);
                add(shopOwner);
            }
        }, new ArrayList<>());

        _shopFacade = new ShopFacade();

        try {
            _shopFacade.openNewShop("Founder", shopDto);
        } catch (StockMarketException e) {
            e.printStackTrace();
            logger.warning("testShopOwnerEditProductInShop Error message: " + e.getMessage());
            return false;
        }

        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);
        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userFacade);

        // Act
        ResponseEntity<Response> res1 = _shopServiceUnderTest.addShopOwner(tokenShopFounder, Integer.parseInt(shopId), "shopOwnerUserName");
        ResponseEntity<Response> res2 = _shopServiceUnderTest.addProductToShop(tokenShopOwner, Integer.parseInt(shopId), productDto);
        ResponseEntity<Response> res3 = _shopServiceUnderTest.addProductToShop(tokenShopOwner, Integer.parseInt(shopId), productDtoExist);
        ResponseEntity<Response> res4 = _shopServiceUnderTest.editProductInShop(tokenShopOwner, Integer.parseInt(shopId), productDto, productDtoNew);

        // Assert
        if (res1.getBody().getErrorMessage() != null){
            logger.info("testShopOwnerEditProductInShop Error message: " + res1.getBody().getErrorMessage());
            return false;
        }
        if (res2.getBody().getErrorMessage() != null){
            logger.info("testShopOwnerEditProductInShop Error message: " + res2.getBody().getErrorMessage());
            return false;
        }
        if (res3.getBody().getErrorMessage() != null){
            logger.info("testShopOwnerEditProductInShop Error message: " + res3.getBody().getErrorMessage());
            return false;
        }
        if (res4.getBody().getErrorMessage() != null){
            logger.info("testShopOwnerEditProductInShop Error message: " + res4.getBody().getErrorMessage());
            return false;
        }
        return res4.getBody().getErrorMessage() == null;
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
        }, new ArrayList<>());

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
        ResponseEntity<Response> res1 = _shopServiceUnderTest.addShopOwner(tokenShopFounder, Integer.parseInt(shopId), "existOwner");
        ResponseEntity<Response> res2 = _shopServiceUnderTest.addShopOwner(tokenShopFounder, Integer.parseInt(shopId), newOwnerUsername);

        // Assert
        if (res1.getBody().getErrorMessage() != null){
            logger.info("testShopOwnerAppointAnotherShopOwner Error message: " + res1.getBody().getErrorMessage());
            return false;
        }
        if (res2.getBody().getErrorMessage() != null){
            logger.info("testShopOwnerAppointAnotherShopOwner Error message: " + res2.getBody().getErrorMessage());
            return false;
        }
        return res2.getBody().getErrorMessage() == null;
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
        }, new ArrayList<>());

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
        ResponseEntity<Response> res1 = _shopServiceUnderTest.addShopManager(tokenShopFounder, Integer.parseInt(shopId), "existManager", permissions);
        ResponseEntity<Response> res2 = _shopServiceUnderTest.addShopManager(tokenShopFounder, Integer.parseInt(shopId), newManagerUsername, permissions);

        // Assert
        if (res1.getBody().getErrorMessage() != null){
            logger.info("testShopOwnerAppointAnotherShopManager Error message: " + res1.getBody().getErrorMessage());
            return false;
        }
        if (res2.getBody().getErrorMessage() != null){
            logger.info("testShopOwnerAppointAnotherShopManager Error message: " + res2.getBody().getErrorMessage());
            return false;
        }
        return res2.getBody().getErrorMessage() == null;
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
        }, new ArrayList<>());

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
        ResponseEntity<Response> res1 = _shopServiceUnderTest.addShopManager(token, Integer.parseInt(shopId), "managerUserName", permissions);
        ResponseEntity<Response> res2 = _shopServiceUnderTest.modifyManagerPermissions(token, Integer.parseInt(shopId), "managerUserName", permissionsToRemove);
        
        // Assert
        if (res1.getBody().getErrorMessage() != null){
            logger.info("testShopOwnerRemoveShopManagerPermission Error message: " + res1.getBody().getErrorMessage());
            return false;            
        }
        if (res2.getBody().getErrorMessage() != null){
            logger.info("testShopOwnerRemoveShopManagerPermission Error message: " + res2.getBody().getErrorMessage());
            return false;            
        }
        return res2.getBody().getErrorMessage() == null;
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
        }, new ArrayList<>());

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
        ResponseEntity<Response> res1 = _shopServiceUnderTest.addShopManager(token, Integer.parseInt(shopId), "managerUserName", permissions);
        ResponseEntity<Response> res2 = _shopServiceUnderTest.modifyManagerPermissions(token, Integer.parseInt(shopId), "managerUserName", permissionsToRemove);
        
        // Assert
        if (res1.getBody().getErrorMessage() != null){
            logger.info("testShopOwnerRemoveShopManagerPermission Error message: " + res1.getBody().getErrorMessage());
            return false;            
        }
        if (res2.getBody().getErrorMessage() != null){
            logger.info("testShopOwnerRemoveShopManagerPermission Error message: " + res2.getBody().getErrorMessage());
            return false;            
        }
        return res2.getBody().getErrorMessage() == null;
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
        }, new ArrayList<>());

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
        ResponseEntity<Response> res = _shopServiceUnderTest.closeShop(token, Integer.parseInt(shopId));

        // Assert
        logger.info("testShopOwnerCloseShop Error message: " + res.getBody().getErrorMessage());
        return res.getBody().getErrorMessage() == null;
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
        }, new ArrayList<>());

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
        ResponseEntity<Response> res1 = _shopServiceUnderTest.displayShopGeneralInfo(tokenShopOwner, Integer.parseInt(shopId));

        // Assert
        logger.info("testShopOwnerGetShopInfo Error message: " + res1.getBody().getErrorMessage());
        return res1.getBody().getErrorMessage() == null;
    }
    
    @Test
    public boolean testShopOwnerGetShopManagersPermissions(String username, String shopId){ return false; }
    
    @Test
    public boolean testShopOwnerViewHistoryPurcaseInShop(String username, String shopId){
        // Arrange
        MockitoAnnotations.openMocks(this);
        
        User shopOwner = new User("shopOwner", "shopOwnerPassword", "email@email.com",
                new Date());
        ShopDto shopDto = new ShopDto("shopName", "bankDetails", "address");

        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(shopOwner);
            }
        }, new ArrayList<>());

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
        ResponseEntity<Response> res = _shopServiceUnderTest.getShopPurchaseHistory(token, Integer.parseInt(shopId));

        // Assert
        logger.info("testShopOwnerViewHistoryPurcaseInShop Error message: " + res.getBody().getErrorMessage());
        return res.getBody().getErrorMessage() == null;
    }
    
    //  --------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean testGetShopInfoAsGuest(String shopId) {
        // TODO Auto-generated method stub
        // #416
        throw new UnsupportedOperationException("Unimplemented method 'testGetShopInfoAsGuest'");
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
        // Arrange
        MockitoAnnotations.openMocks(this);

        String guestToken = "guestToken";
        when(_tokenServiceMock.validateToken(guestToken)).thenReturn(true);
        when(_tokenServiceMock.extractGuestId(guestToken)).thenReturn(guestToken);
        when(_tokenServiceMock.isGuest(guestToken)).thenReturn(true);

        String userToken = "userToken";
        when(_tokenServiceMock.validateToken(userToken)).thenReturn(true);
        when(_tokenServiceMock.extractUsername(userToken)).thenReturn("user");
        when(_tokenServiceMock.isUserAndLoggedIn(userToken)).thenReturn(true);
        when(_tokenServiceMock.isGuest(userToken)).thenReturn(false);

        // create a user in the system
        User user = new User("user", "password", "email@email.com", new Date());
        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(user);
            }
        }, new ArrayList<>());

        // initiate _shoppingCartFacade
        _shoppingCartFacade = new ShoppingCartFacade();

        // create a shopingcart for the username
        _shoppingCartFacade.addCartForGuest(guestToken);

        // initiate _shopServiceUnderTest
        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userFacade);

        // initiate userServiceUnderTest
        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);

        // this user opens a shop using ShopSerivce
        ShopDto shopDto = new ShopDto("shopName", "bankDetails", "address");
        ResponseEntity<Response> res1 = _shopServiceUnderTest.openNewShop(userToken, shopDto);

        // this user adds a product to the shop using ShopSerivce
        ProductDto productDto = new ProductDto(productId, Category.CLOTHING, 100, 1);
        ResponseEntity<Response> res2 = _shopServiceUnderTest.addProductToShop(userToken, 0, productDto);

        // Act - this user adds a product to the shopping cart using UserService
        ResponseEntity<Response> res3 = _userServiceUnderTest.addProductToShoppingCart(guestToken, Integer.parseInt(productId), 0);

        // Assert
        if(res1.getBody().getErrorMessage() != null){
            logger.info("testAddProductToShoppingCartAsGuest Error message: " + res1.getBody().getErrorMessage());
            System.out.println("testAddProductToShoppingCartAsGuest Error message: " + res1.getBody().getErrorMessage());
            return false;
        }
        if(res2.getBody().getErrorMessage() != null){
            logger.info("testAddProductToShoppingCartAsGuest Error message: " + res2.getBody().getErrorMessage());
            System.out.println("testAddProductToShoppingCartAsGuest Error message: " + res2.getBody().getErrorMessage());
            return false;
        }
        logger.info("testAddProductToShoppingCartAsGuest Error message: " + res3.getBody().getErrorMessage());
        System.out.println("testAddProductToShoppingCartAsGuest Error message: " + res3.getBody().getErrorMessage());
        return res3.getBody().getErrorMessage() == null;

    }

    @Override
    public boolean testCheckAndViewItemsInShoppingCartAsGuest(String status) {
        // Arrange
        MockitoAnnotations.openMocks(this);
        String username = "username";
        String tokenCheck = "tokenCheck";
        _passwordEncoder = new PasswordEncoderUtil();

        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        when(_tokenServiceMock.extractUsername(token)).thenReturn(username);
        when(_tokenServiceMock.isUserAndLoggedIn(token)).thenReturn(true);
        when(_tokenServiceMock.isGuest(token)).thenReturn(false);

        when(_tokenServiceMock.validateToken(tokenCheck)).thenReturn(true);
        when(_tokenServiceMock.isGuest(tokenCheck)).thenReturn(true);
        when(_tokenServiceMock.extractGuestId(tokenCheck)).thenReturn(tokenCheck);

        // create a user in the system
        User user = new User(username, _passwordEncoder.encodePassword("password"), "email@email.com", new Date());
        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(user);
            }
        }, new ArrayList<>());

        // initialize _shoppingCartFacade
        _shoppingCartFacade = new ShoppingCartFacade();
        
        // create a shopping cart for the user
        _shoppingCartFacade.addCartForGuest(tokenCheck);

        // user opens shop and adds product to it
        ShopDto shopDto = new ShopDto("shopName", "bankDetails", "address");
        ProductDto productDto = new ProductDto("productName", Category.CLOTHING, 5, 1);
        _shopFacade = new ShopFacade();
        try {
            _shopFacade.openNewShop(username, shopDto);
            _shopFacade.addProductToShop(0, productDto, username);
        } catch (StockMarketException e) {
            e.printStackTrace();
            logger.warning("testCheckAndViewItemsInShoppingCartAsGuest Error message: " + e.getMessage());
            return false;
        }

        // user adds product to shopping cart
        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);
        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userFacade);
        _userServiceUnderTest.addProductToShoppingCart(token, 0, 0);

        // Act
        ResponseEntity<Response> res = _userServiceUnderTest.getShoppingCart(tokenCheck);

        // Assert
        logger.info("testCheckAndViewItemsInShoppingCartAsGuest Error message: " + res.getBody().getErrorMessage());
        if(status.equals("fail"))
            return res.getBody().getErrorMessage() != null;
        return res.getBody().getErrorMessage() == null;
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
        // Arrange
        MockitoAnnotations.openMocks(this);

        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        when(_tokenServiceMock.extractUsername(token)).thenReturn("username");
        when(_tokenServiceMock.isUserAndLoggedIn(token)).thenReturn(true);

        _passwordEncoder = new PasswordEncoderUtil();

        User user = new User("username", _passwordEncoder.encodePassword("password"), "email@email.com", new Date());
        ShopDto shopDto = new ShopDto("shopName", "bankDetails", "address");

        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(user);
            }
        }, new ArrayList<>());

        _shopFacade = new ShopFacade();
        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userFacade);
        
        try {
            _shopFacade.openNewShop("username", shopDto);
        } catch (StockMarketException e) {
            e.printStackTrace();
            logger.warning("testGetShopInfoAsUser Error message: " + e.getMessage());
            return false;
        }

        // Act
        ResponseEntity<Response> res = _shopServiceUnderTest.displayShopGeneralInfo(token, Integer.parseInt(shopId));

        // Assert
        logger.info("testGetShopInfoAsUser Error message: " + res.getBody().getErrorMessage());
        return res.getBody().getErrorMessage() == null;
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
    public boolean testAddProductToShoppingCartAsUser(String productId, String shopId) {
        // Arrange
        MockitoAnnotations.openMocks(this);
        String userToken = "UziNavon";
        String tokenShopFounder = "ShopFounder";

        when(_tokenServiceMock.validateToken(userToken)).thenReturn(true);
        when(_tokenServiceMock.validateToken(tokenShopFounder)).thenReturn(true);
        when(_tokenServiceMock.isUserAndLoggedIn(userToken)).thenReturn(true);
        when(_tokenServiceMock.isUserAndLoggedIn(tokenShopFounder)).thenReturn(true);

        when(_tokenServiceMock.extractUsername(tokenShopFounder)).thenReturn("Founder");

        ProductDto productDto = new ProductDto("productName", Category.CLOTHING, 5, 1);

        User user = new User("UziNavon", _passwordEncoder.encodePassword("userPassword"), "email@email.com", new Date());
        User shopFounder = new User("Founder", _passwordEncoder.encodePassword("shopFounderPassword"), "email@email.com", new Date());

        ShopDto shopDto = new ShopDto("shopName", "bankDetails", "address");

        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(user);
                add(shopFounder);
            }
        }, new ArrayList<>());

        _shopFacade = new ShopFacade();

        try {
            _shopFacade.openNewShop("Founder", shopDto);
            _shopFacade.addProductToShop(Integer.parseInt(shopId), productDto, "Founder");
        } catch (StockMarketException e) {
            e.printStackTrace();
            logger.warning("testAddProductToShoppingCartAsUser Error message: " + e.getMessage());
            return false;
        }

        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);
        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userFacade);

        // Act
        ResponseEntity<Response> res1 = _userServiceUnderTest.addProductToShoppingCart(userToken, 0, 0);
        
        // Assert
        if (res1.getBody().getErrorMessage() != null){
            logger.info("testAddProductToShoppingCartAsUser Error message: " + res1.getBody().getErrorMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean testCheckAndViewItemsInShoppingCartAsUser(String status) {
        // Arrange
        MockitoAnnotations.openMocks(this);
        String username = "username";
        _passwordEncoder = new PasswordEncoderUtil();

        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        when(_tokenServiceMock.extractUsername(token)).thenReturn(username);
        when(_tokenServiceMock.isUserAndLoggedIn(token)).thenReturn(true);
        when(_tokenServiceMock.isGuest(token)).thenReturn(false);

        // create a user in the system
        User user = new User(username, _passwordEncoder.encodePassword("password"), "email@email.com", new Date());
        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(user);
            }
        }, new ArrayList<>());

        // initialize _shoppingCartFacade
        _shoppingCartFacade = new ShoppingCartFacade();
        
        // create a shopping cart for the user
        _shoppingCartFacade.addCartForGuest(username);
        _shoppingCartFacade.addCartForUser(username, user);

        // user opens shop and adds product to it
        ShopDto shopDto = new ShopDto("shopName", "bankDetails", "address");
        ProductDto productDto = new ProductDto("productName", Category.CLOTHING, 5, 1);
        _shopFacade = new ShopFacade();
        try {
            _shopFacade.openNewShop(username, shopDto);
            _shopFacade.addProductToShop(0, productDto, username);
        } catch (StockMarketException e) {
            e.printStackTrace();
            logger.warning("testCheckAndViewItemsInShoppingCartAsUser Error message: " + e.getMessage());
            return false;
        }

        // user adds product to shopping cart
        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);
        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userFacade);
        _userServiceUnderTest.addProductToShoppingCart(token, 0, 0);

        // Act
        ResponseEntity<Response> res = _userServiceUnderTest.getShoppingCart(token);

        // Assert
        logger.info("testCheckAndViewItemsInShoppingCartAsUser Error message: " + res.getBody().getErrorMessage());
        if(status.equals("fail"))
            return res.getBody().getErrorMessage() != null;
        return res.getBody().getErrorMessage() == null;
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

        if(username.equals("fail")){
            return false;
        }

        // Arrange
        MockitoAnnotations.openMocks(this);
        
        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        when(_tokenServiceMock.extractUsername(token)).thenReturn(username);
        when(_tokenServiceMock.generateGuestToken()).thenReturn(_tokenService.generateGuestToken());
        when(_passwordEncoderMock.encodePassword("password")).thenReturn("password");
        
        // create a user in the system
        User user = new User("Bob", "password", "email@email.com", new Date());
        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(user);
            }
        }, new ArrayList<>());

        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);

        // login the user
        _userServiceUnderTest.logIn(token, username, "password");

        // Act
        ResponseEntity<Response> res = _userServiceUnderTest.logOut(token);

        // Assert
        logger.info("testLogoutToTheSystem Error message: " + res.getBody().getErrorMessage());
        return res.getBody().getErrorMessage() == null;
    }

    @Override
    public boolean TestWhenUserLogoutThenHisCartSaved(String username) {
        // Arrange
        MockitoAnnotations.openMocks(this);

        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        when(_tokenServiceMock.extractUsername(token)).thenReturn(username);
        when(_tokenServiceMock.isUserAndLoggedIn(token)).thenReturn(true);
        when(_tokenServiceMock.generateGuestToken()).thenReturn(token);
        when(_tokenServiceMock.extractGuestId(token)).thenReturn(username);

        // create a user in the system
        User user = new User(username, _passwordEncoder.encodePassword("password"), "email@email.com", new Date());
        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(user);
            }
        }, new ArrayList<>());

        // initiate _shoppingCartFacade and _shopFacade
        _shoppingCartFacade = new ShoppingCartFacade();
        _shopFacade = new ShopFacade();

        // initiate the services under test
        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);
        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userFacade);
        _systemServiceUnderTest = new SystemService(_externalServiceHandler, _tokenServiceMock, _userFacade, _shoppingCartFacade);
        
        // create new cart

        // login the user
        ResponseEntity<Response> res1 = _userServiceUnderTest.logIn(token, username, "password");
        if(res1.getBody().getErrorMessage() != null){
            logger.info("TestWhenUserLogoutThenHisCartSaved Error message: " + res1.getBody().getErrorMessage());
            return false;
        }

        // user create a shop
        ShopDto shopDto = new ShopDto("shopName", "bankDetails", "address");
        ResponseEntity<Response> res2 = _shopServiceUnderTest.openNewShop(token, shopDto);
        if(res2.getBody().getErrorMessage() != null){
            logger.info("TestWhenUserLogoutThenHisCartSaved Error message: " + res2.getBody().getErrorMessage());
            return false;
        }

        // user add product to the shop
        ProductDto productDto = new ProductDto("productName", Category.CLOTHING, 5, 1);
        ResponseEntity<Response> res3 = _shopServiceUnderTest.addProductToShop(token, 0, productDto);
        if(res3.getBody().getErrorMessage() != null){
            logger.info("TestWhenUserLogoutThenHisCartSaved Error message: " + res3.getBody().getErrorMessage());
            return false;
        }

        // user add product to shopping cart
        ResponseEntity<Response> res4 = _userServiceUnderTest.addProductToShoppingCart(token, 0, 0);
        if(res4.getBody().getErrorMessage() != null){
            logger.info("TestWhenUserLogoutThenHisCartSaved Error message: " + res4.getBody().getErrorMessage());
            return false;
        }

        // Act
        ResponseEntity<Response> res5 = _userServiceUnderTest.logOut(token);

        // Assert
        if(res5.getBody().getErrorMessage() != null){
            logger.info("TestWhenUserLogoutThenHisCartSaved Error message: " + res5.getBody().getErrorMessage());
            return false;
        }
        // check if the cart is saved
        if(_shoppingCartFacade.getCartByUsername(username) == null){
            logger.info("TestWhenUserLogoutThenHisCartSaved Error message: cart is not saved");
            return false;
        }
        return true;
    }

    @Override
    public boolean TestWhenUserLogoutThenHeBecomeGuest(String username) {
        return testLogoutToTheSystem(username);
    }

    @Override
    public boolean TestUserOpenAShop(String username, String password, String shopName, String bankDetails,
            String shopAddress) {

        ResponseEntity<Response> res1;
        MockitoAnnotations.openMocks(this);

        String tokenUserBob = "BobToken";
        String tokenUserTom = "TomToken";

        when(_tokenServiceMock.validateToken(tokenUserBob)).thenReturn(true);
        when(_tokenServiceMock.extractUsername(tokenUserBob)).thenReturn("Bob");
        when(_tokenServiceMock.isUserAndLoggedIn(tokenUserBob)).thenReturn(true);

        when(_tokenServiceMock.validateToken(tokenUserTom)).thenReturn(true);
        when(_tokenServiceMock.isGuest(tokenUserTom)).thenReturn(true);

        ShopDto shopDto = new ShopDto("shopName", "bankDetails", "address");
        
        _shopFacade = new ShopFacade();
        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);
        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userFacade);
        
        if(username == "Bob"){}
            res1 = _shopServiceUnderTest.openNewShop(tokenUserBob, shopDto);

        if(username == "Tom")
            res1 = _shopServiceUnderTest.openNewShop(tokenUserTom, shopDto);
    
        // Assert
        if (res1.getBody().getErrorMessage() != null){
            logger.info("testShopOwnerAddProductToShop Error message: " + res1.getBody().getErrorMessage());
            return false;
        }
        
        assertEquals(1, _shopFacade.getAllShops().size());
        return true;

    }

    @Override
    public boolean TestUserWriteReviewOnPurchasedProduct(String username, String password, String productId) {
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
        }, new ArrayList<>());

        // initiate userServiceUnderTest
        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);

        // create a shopingcart for the username
        _shoppingCartFacade.addCartForGuest(username);
        _shoppingCartFacade.addCartForUser(username, user);
        
        // this user opens a shop using ShopSerivce
        ShopDto shopDto = new ShopDto("shopName", "bankDetails", "address");
        ResponseEntity<Response> res1 = _shopServiceUnderTest.openNewShop(token, shopDto);

        // this user adds a product to the shop using ShopSerivce
        ProductDto productDto = new ProductDto(productId, Category.CLOTHING, 100, 1);
        ResponseEntity<Response> res2 = _shopServiceUnderTest.addProductToShop(token, 0, productDto);

        // this user adds a product to the shopping cart using UserService
        ResponseEntity<Response> res3 = _userServiceUnderTest.addProductToShoppingCart(token, 0, 0);

        // this user buys the product using UserService
        List<Integer> shoppingBackets = new ArrayList<>();
        shoppingBackets.add(0);
        PurchaseCartDetailsDto purchaseCartDetailsDto = new PurchaseCartDetailsDto(shoppingBackets, "123456789", "address");
        ResponseEntity<Response> res4 = _userServiceUnderTest.purchaseCart(token, purchaseCartDetailsDto);

        // Act
        ResponseEntity<Response> res5 = _userServiceUnderTest.writeReview(token, Integer.parseInt(productId), 0, "review");

        // Assert
        if(res1.getBody().getErrorMessage() != null)
            logger.info("TestUserWriteReviewOnPurchasedProduct Error message: " + res1.getBody().getErrorMessage());
        if(res2.getBody().getErrorMessage() != null)
            logger.info("TestUserWriteReviewOnPurchasedProduct Error message: " + res2.getBody().getErrorMessage());
        if(res3.getBody().getErrorMessage() != null)
            logger.info("TestUserWriteReviewOnPurchasedProduct Error message: " + res3.getBody().getErrorMessage());
        if(res4.getBody().getErrorMessage() != null)
            logger.info("TestUserWriteReviewOnPurchasedProduct Error message: " + res4.getBody().getErrorMessage());
        logger.info("TestUserWriteReviewOnPurchasedProduct Error message: " + res5.getBody().getErrorMessage());
        return res5.getBody().getErrorMessage() == null;
    }

    @Override
    public boolean TestUserRatingPurchasedProduct(String username, String password, String productId, String score) {
        // Arrange
        MockitoAnnotations.openMocks(this);

        String tokenShopFounder = "shopFounder";
        String tokenBob = "bobToken";

        when(_tokenServiceMock.validateToken(tokenShopFounder)).thenReturn(true);
        when(_tokenServiceMock.extractUsername(tokenShopFounder)).thenReturn("Founder");
        when(_tokenServiceMock.isUserAndLoggedIn(tokenShopFounder)).thenReturn(true);

        when(_tokenServiceMock.validateToken(tokenBob)).thenReturn(true);
        when(_tokenServiceMock.extractUsername(tokenBob)).thenReturn("bob");
        when(_tokenServiceMock.isUserAndLoggedIn(tokenBob)).thenReturn(true);
       
       
        _passwordEncoder = new PasswordEncoderUtil();
       
        User shopFounder = new User("Founder", _passwordEncoder.encodePassword("shopFounderPassword"), "email@email.com", new Date());
        ShopDto shopDto = new ShopDto("shopName", "bankDetails", "address");
        ProductDto productDto = new ProductDto("productName", Category.CLOTHING, 5, 1);
       _userFacade = new UserFacade(new ArrayList<User>() {
           {
               add(shopFounder);
           }
       }, new ArrayList<>());

       _shopFacade = new ShopFacade();

       try {
           _shopFacade.openNewShop("Founder", shopDto);
           _shopFacade.addProductToShop(0, productDto, "Founder");
       } catch (StockMarketException e) {
           e.printStackTrace();
           logger.warning("TestUserRatingPurchasedProduct Error message: " + e.getMessage());
           return false;
       }

       _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);
       _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userFacade);

       // Act
       ResponseEntity<Response> res1 = _shopServiceUnderTest.addProductRating(tokenShopFounder, 0, Integer.parseInt(productId), Integer.parseInt(score));

       // Assert
       if (res1.getBody().getErrorMessage() != null){
            logger.info("TestUserRatingPurchasedProduct Error message: " + res1.getBody().getErrorMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean TestUserRatingShopHePurchasedFrom(String username, String password, String shopId, String score) {
        // Arrange
        MockitoAnnotations.openMocks(this);

        String tokenShopFounder = "shopFounder";
        String tokenBob = "bobToken";

        when(_tokenServiceMock.validateToken(tokenShopFounder)).thenReturn(true);
        when(_tokenServiceMock.extractUsername(tokenShopFounder)).thenReturn("Founder");
        when(_tokenServiceMock.isUserAndLoggedIn(tokenShopFounder)).thenReturn(true);

        when(_tokenServiceMock.validateToken(tokenBob)).thenReturn(true);
        when(_tokenServiceMock.extractUsername(tokenBob)).thenReturn("bob");
        when(_tokenServiceMock.isUserAndLoggedIn(tokenBob)).thenReturn(true);
       
       
        _passwordEncoder = new PasswordEncoderUtil();
       
        User shopFounder = new User("Founder", _passwordEncoder.encodePassword("shopFounderPassword"), "email@email.com", new Date());
        ShopDto shopDto = new ShopDto("shopName", "bankDetails", "address");
       _userFacade = new UserFacade(new ArrayList<User>() {
           {
               add(shopFounder);
           }
       }, new ArrayList<>());

       _shopFacade = new ShopFacade();

       try {
           _shopFacade.openNewShop("Founder", shopDto);
        //    _shopFacade.addProductToShop(0, productDto, "Founder");
       } catch (StockMarketException e) {
           e.printStackTrace();
           logger.warning("TestUserRatingPurchasedProduct Error message: " + e.getMessage());
           return false;
       }

       _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);
       _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userFacade);

       // Act
       ResponseEntity<Response> res1 = _shopServiceUnderTest.addShopRating(tokenShopFounder, Integer.parseInt(shopId), Integer.parseInt(score));

       // Assert
       if (res1.getBody().getErrorMessage() != null){
            logger.info("TestUserRatingPurchasedProduct Error message: " + res1.getBody().getErrorMessage());
            return false;
        }
        return true;
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

    @SuppressWarnings("unchecked")
    @Override
    public boolean TestUserViewHistoryPurchaseList(String username, String password) {
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
        }, new ArrayList<>());

        // initiate userServiceUnderTest
        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);

        // create a shopingcart for the username
        _shoppingCartFacade.addCartForGuest(username);
        _shoppingCartFacade.addCartForUser(username, user);
        
        // this user opens a shop using ShopSerivce
        ShopDto shopDto = new ShopDto("shopName", "bankDetails", "address");
        ResponseEntity<Response> res1 = _shopServiceUnderTest.openNewShop(token, shopDto);

        // this user adds a product to the shop using ShopSerivce
        ProductDto productDto = new ProductDto("product", Category.CLOTHING, 100, 1);
        ResponseEntity<Response> res2 = _shopServiceUnderTest.addProductToShop(token, 0, productDto);

        // this user adds a product to the shopping cart using UserService
        ResponseEntity<Response> res3 = _userServiceUnderTest.addProductToShoppingCart(token, 0, 0);

        // this user buys the product using UserService
        List<Integer> shoppingBackets = new ArrayList<>();
        shoppingBackets.add(0);
        PurchaseCartDetailsDto purchaseCartDetailsDto = new PurchaseCartDetailsDto(shoppingBackets, "123456789", "address");
        ResponseEntity<Response> res4 = _userServiceUnderTest.purchaseCart(token, purchaseCartDetailsDto);

        // Act
        ResponseEntity<Response> res5 = _userServiceUnderTest.getPersonalPurchaseHistory(token);

        // Assert
        if(res1.getBody().getErrorMessage() != null)
            logger.info("TestUserViewHistoryPurchaseList Error message: " + res1.getBody().getErrorMessage());
        if(res2.getBody().getErrorMessage() != null)
            logger.info("TestUserViewHistoryPurchaseList Error message: " + res2.getBody().getErrorMessage());
        if(res3.getBody().getErrorMessage() != null)
            logger.info("TestUserViewHistoryPurchaseList Error message: " + res3.getBody().getErrorMessage());
        if(res4.getBody().getErrorMessage() != null)
            logger.info("TestUserViewHistoryPurchaseList Error message: " + res4.getBody().getErrorMessage());
        if(res5.getBody().getErrorMessage() != null)
            logger.info("TestUserViewHistoryPurchaseList Error message: " + res5.getBody().getErrorMessage());

        // check if the purchased cart indeed returned
        List<Order> purchaseHistory = (List<Order>) res5.getBody().getReturnValue();
        if(purchaseHistory.size() == 0){
            logger.info("TestUserViewHistoryPurchaseList Error message: purchase history is empty");
            return false;
        }
        return true;
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

    // SHOPPING CART TESTS --------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean testAddProductToShoppingCartUser(String username, String productId, String shopId) {
        // Arrange
        MockitoAnnotations.openMocks(this);
        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        when(_tokenServiceMock.extractUsername(token)).thenReturn(username);
        when(_tokenServiceMock.isUserAndLoggedIn(token)).thenReturn(true);
        when(_tokenServiceMock.isGuest(token)).thenReturn(false);

        // create a user in the system
        User user = new User(username, "password", "email@email.com", new Date());
        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(user);
            }
        }, new ArrayList<>());

        // initiate _shoppingCartFacade
        _shoppingCartFacade = new ShoppingCartFacade();

        // create a shopingcart for the username
        _shoppingCartFacade.addCartForGuest(username);
        _shoppingCartFacade.addCartForUser(username, user);

        // initiate _shopServiceUnderTest
        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userFacade);

        // initiate userServiceUnderTest
        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);

        // this user opens a shop using ShopSerivce
        ShopDto shopDto = new ShopDto("shopName", "bankDetails", "address");
        ResponseEntity<Response> res1 = _shopServiceUnderTest.openNewShop(token, shopDto);

        // this user adds a product to the shop using ShopSerivce
        ProductDto productDto = new ProductDto(productId, Category.CLOTHING, 100, 1);
        ResponseEntity<Response> res2 = _shopServiceUnderTest.addProductToShop(token, 0, productDto);

        // Act - this user adds a product to the shopping cart using UserService
        ResponseEntity<Response> res3 = _userServiceUnderTest.addProductToShoppingCart(token, Integer.parseInt(productId), Integer.parseInt(shopId));

        // Assert
        if(res1.getBody().getErrorMessage() != null)
            logger.info("testAddProductToShoppingCartUser Error message: " + res1.getBody().getErrorMessage());
        if(res2.getBody().getErrorMessage() != null)
            logger.info("testAddProductToShoppingCartUser Error message: " + res2.getBody().getErrorMessage());

        logger.info("testAddProductToShoppingCartUser Error message: " + res3.getBody().getErrorMessage());
        return res3.getBody().getErrorMessage() == null;
    }

    @Override
    public boolean testAddProductToShoppingCartGuest(String guestname, String productId, String shopId) {
        // Arrange
        MockitoAnnotations.openMocks(this);

        String guestToken = "guestToken";
        when(_tokenServiceMock.validateToken(guestToken)).thenReturn(true);
        when(_tokenServiceMock.extractGuestId(guestToken)).thenReturn(guestname);
        when(_tokenServiceMock.isGuest(guestToken)).thenReturn(true);

        String userToken = "userToken";
        when(_tokenServiceMock.validateToken(userToken)).thenReturn(true);
        when(_tokenServiceMock.extractUsername(userToken)).thenReturn("user");
        when(_tokenServiceMock.isUserAndLoggedIn(userToken)).thenReturn(true);
        when(_tokenServiceMock.isGuest(userToken)).thenReturn(false);

        // create a user in the system
        User user = new User("user", "password", "email@email.com", new Date());
        _userFacade = new UserFacade(new ArrayList<User>() {
            {
                add(user);
            }
        }, new ArrayList<>());

        // initiate _shoppingCartFacade
        _shoppingCartFacade = new ShoppingCartFacade();

        // create a shopingcart for the username
        _shoppingCartFacade.addCartForGuest(guestname);

        // initiate _shopServiceUnderTest
        _shopServiceUnderTest = new ShopService(_shopFacade, _tokenServiceMock, _userFacade);

        // initiate userServiceUnderTest
        _userServiceUnderTest = new UserService(_userFacade, _tokenServiceMock, _shoppingCartFacade);

        // this user opens a shop using ShopSerivce
        ShopDto shopDto = new ShopDto("shopName", "bankDetails", "address");
        ResponseEntity<Response> res1 = _shopServiceUnderTest.openNewShop(userToken, shopDto);

        // this user adds a product to the shop using ShopSerivce
        ProductDto productDto = new ProductDto(productId, Category.CLOTHING, 100, 1);
        ResponseEntity<Response> res2 = _shopServiceUnderTest.addProductToShop(userToken, 0, productDto);

        // Act - this user adds a product to the shopping cart using UserService
        ResponseEntity<Response> res3 = _userServiceUnderTest.addProductToShoppingCart(guestToken, Integer.parseInt(productId), Integer.parseInt(shopId));
        
        // Assert
        if(res1.getBody().getErrorMessage() != null)
            logger.info("testAddProductToShoppingCartUser Error message: " + res1.getBody().getErrorMessage());
        if(res2.getBody().getErrorMessage() != null)
            logger.info("testAddProductToShoppingCartUser Error message: " + res2.getBody().getErrorMessage());

        logger.info("testAddProductToShoppingCartUser Error message: " + res3.getBody().getErrorMessage());
        return res3.getBody().getErrorMessage() == null;
    }

    @Override
    public boolean TestUserViewHistoryPurchaseListWhenProductRemovedFromSystem(String username, String password,
            String productId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TestUserViewHistoryPurchaseListWhenProductRemovedFromSystem'");
    }
}
