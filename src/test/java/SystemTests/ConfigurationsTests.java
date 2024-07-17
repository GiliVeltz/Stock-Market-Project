package SystemTests;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import Domain.Facades.ShopFacade;
import Domain.Facades.ShoppingCartFacade;
import Domain.Facades.UserFacade;
import Exceptions.StockMarketException;
import Server.MarketSystem;
import Server.notifications.NotificationHandler;
import Server.notifications.WebSocketServer;

// write a test class to check the configurations belongs to "MarketSystem.java" class
public class ConfigurationsTests {

    private MarketSystem marketSystemUnderTest;

    public String external_system_url = "https://damp-lynna-wsep-1984852e.koyeb.app/";
    public String instructions_config_path = "src/main/java/Server/Configuration/instructions_config.txt";
    public String real_system_config_path = "src/main/java/Server/Configuration/system_config.txt";

    @Mock
    ShopFacade shopFacadeMock;
    @Mock
    UserFacade userFacadeMock;
    @Mock
    ShoppingCartFacade shoppingCartFacadeMock;
    @Mock
    NotificationHandler notificationHandlerMock;
    @Mock
    WebSocketServer webSocketServerMock;
    
    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testSetConfigurations_whenNewConfigurationsNotContainsExternalService_thenConfigurationsNotSetAndThrowException() {
        // Arrange - Create a new url for the configurations
        String url = "src/test/java/SystemTests/configurations_config/configurations_no_external_service.txt";

        // Act - try to set a new configurations
        marketSystemUnderTest = new MarketSystem(shopFacadeMock, userFacadeMock, shoppingCartFacadeMock, notificationHandlerMock, webSocketServerMock, external_system_url, instructions_config_path, real_system_config_path);
        marketSystemUnderTest.setReal_system_config_path(url);

        // Assert - Verify that the configurations was not set successfully - throw exception from init function in MarketSystem class
        assertThrows(StockMarketException.class, () -> marketSystemUnderTest.set_external_services(url), "The method should throw an exception");
    }

    @Test
    public void testSetConfigurations_whenNewConfigurationsNotContainsDatabase_thenConfigurationsNotSetAndThrowException() {
        // Arrange - Create a new url for the configurations
        String url = "src/test/java/SystemTests/configurations_config/configurations_no_database.txt";

        // Act - try to set a new configurations
        marketSystemUnderTest = new MarketSystem(shopFacadeMock, userFacadeMock, shoppingCartFacadeMock, notificationHandlerMock, webSocketServerMock, external_system_url, instructions_config_path, real_system_config_path);
        marketSystemUnderTest.setReal_system_config_path(url);

        // Assert - Verify that the configurations was not set successfully - throw exception from init function in MarketSystem class
        assertThrows(StockMarketException.class, () -> marketSystemUnderTest.set_database(url), "The method should throw an exception");
    }

    @Test
    public void testSetConfigurations_whenNewConfigurationsExistPath_thenConfigurationsSet() {
        // Arrange - Create a new url for the configurations
        String url = "src/main/java/Server/Configuration/test_config.txt";

        // Act - try to set a new configurations
        marketSystemUnderTest = new MarketSystem(shopFacadeMock, userFacadeMock, shoppingCartFacadeMock, notificationHandlerMock, webSocketServerMock, external_system_url, instructions_config_path, real_system_config_path);
        marketSystemUnderTest.setReal_system_config_path(url);

        // Assert - Verify that the configurations was set successfully - no throw exception from init function in MarketSystem class
        assertDoesNotThrow(() -> marketSystemUnderTest.read_config_file(url), "The method should not throw any exception");
    }

    @Test
    public void testSetConfigurations_whenNewConfigurationsNotExistPath_thenConfigurationsNotSet() {
        // Arrange - Create a new url for the configurations
        String url = "src/main/java/Server/Configuration/test_config_not_found_path";


        // Act - try to set a new configurations
        marketSystemUnderTest = new MarketSystem(shopFacadeMock, userFacadeMock, shoppingCartFacadeMock, notificationHandlerMock, webSocketServerMock, external_system_url, instructions_config_path, real_system_config_path);
        marketSystemUnderTest.setReal_system_config_path(url);

        // Assert - Verify that the configurations was not set successfully - throw exception from init function in MarketSystem class
        assertThrows(StockMarketException.class, () -> marketSystemUnderTest.read_config_file(url), "The method should throw an exception");
    }

    @Test
    public void testInitDataToMarket_whenDataInitiateIsCorrect_thenDataInit() {
        // Arrange - Create a new url for the data
        String url = "src/test/java/SystemTests/instructions_config/instructions_true.txt";
        
        // Act - try to set a new configurations
        marketSystemUnderTest = new MarketSystem(shopFacadeMock, userFacadeMock, shoppingCartFacadeMock, notificationHandlerMock, webSocketServerMock, external_system_url, instructions_config_path, real_system_config_path);
        marketSystemUnderTest.setInstructions_config_path(url);

        // Act and verify that the data was set successfully
        assertDoesNotThrow(() -> marketSystemUnderTest.init_data_to_market(url), "The method should not throw any exception");
    }

    @Test
    public void testInitDataToMarket_whenDataInitiateWrongPath_thenDataNotInitAndThrowException() {
        // Arrange - Create a new url for the data
        String url = "src/test/java/SystemTests/instructions_config/instructions_not_found_path";
        
        // Act - try to set a new configurations
        marketSystemUnderTest = new MarketSystem(shopFacadeMock, userFacadeMock, shoppingCartFacadeMock, notificationHandlerMock, webSocketServerMock, external_system_url, instructions_config_path, real_system_config_path);
        marketSystemUnderTest.setInstructions_config_path(url);
        
        // Act and verify that the data was not set successfully
        assertThrows(StockMarketException.class, () -> marketSystemUnderTest.init_data_to_market(url), "The method should throw an exception");
    }

    @Test
    public void testInitDataToMarket_whenDataInitiateWrongInstruction_thenDataNotInitAndThrowException() {
        // Arrange - Create a new url for the data
        String url = "src/test/java/SystemTests/instructions_config/instructions_fail.txt";
        
        // Act - try to set a new configurations
        marketSystemUnderTest = new MarketSystem(shopFacadeMock, userFacadeMock, shoppingCartFacadeMock, notificationHandlerMock, webSocketServerMock, external_system_url, instructions_config_path, real_system_config_path);
        marketSystemUnderTest.setInstructions_config_path(url);

        // Act and verify that the data was not set successfully
        assertThrows(StockMarketException.class, () -> marketSystemUnderTest.init_data_to_market(url), "The method should throw an exception");
    }

    @Test
    public void testInitDataToMarket_whenDataInitiateWrongNumberOfArgsForInstruction_thenNotThrowExceptionButPrintLog() {
        // Arrange - Create a new url for the data
        String url = "src/test/java/SystemTests/instructions_config/instructions_not_enough_args.txt";
        
        // Act - try to set a new configurations
        marketSystemUnderTest = new MarketSystem(shopFacadeMock, userFacadeMock, shoppingCartFacadeMock, notificationHandlerMock, webSocketServerMock, external_system_url, instructions_config_path, real_system_config_path);
        marketSystemUnderTest.setInstructions_config_path(url);
        
        // Act and verify that the data was not set successfully
        assertDoesNotThrow(() -> marketSystemUnderTest.init_data_to_market(url), "The method should not throw an exception");
    }

    @Test
    public void testInitDataToMarket_whenDataInitiateEmptyFile_thenNotThrowException() {
        // Arrange - Create a new url for the data
        String url = "src/test/java/SystemTests/instructions_config/instructions_empty.txt";
        
        // Act - try to set a new configurations
        marketSystemUnderTest = new MarketSystem(shopFacadeMock, userFacadeMock, shoppingCartFacadeMock, notificationHandlerMock, webSocketServerMock, external_system_url, instructions_config_path, real_system_config_path);
        marketSystemUnderTest.setInstructions_config_path(url);
        
        // Act and verify that the data was set successfully
        assertDoesNotThrow(() -> marketSystemUnderTest.init_data_to_market(url), "The method should not throw any exception");
    }

    @Test
    public void testInitDataToMarket_whenDataInitiateCorrectAndReal_ThenAddedToDb() {
        // Arrange - Create a new url for the data
        String url = "src/test/java/SystemTests/instructions_config/instructions_true_to_db.txt";
        
        // Act - try to set a new configurations
        marketSystemUnderTest = new MarketSystem(shopFacadeMock, userFacadeMock, shoppingCartFacadeMock, notificationHandlerMock, webSocketServerMock, external_system_url, instructions_config_path, real_system_config_path);
        marketSystemUnderTest.setInstructions_config_path(url);
        
        // Act and verify that the data was set successfully
        assertDoesNotThrow(() -> marketSystemUnderTest.init_data_to_market(url), "The method should not throw any exception");
    }
}
