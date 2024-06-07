package DmainTests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import Domain.ExternalServices.ExternalServiceHandler;
import Dtos.ExternalServiceDto;

public class ExternalServicesTests {
    
    @Mock
    ExternalServiceHandler externalServiceHandlerMock;

    @BeforeEach
    public void setUp() {
        externalServiceHandlerMock = mock(ExternalServiceHandler.class);
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testAddExternalService_whenServiceNameIsEmpty_ReturnsFalse() {
        // Arrange
        ExternalServiceDto serviceDto = new ExternalServiceDto(-1, "", "name", "phone");
        when(externalServiceHandlerMock.addExternalService(serviceDto)).thenReturn(false);
        
        // Act
        boolean result = externalServiceHandlerMock.addExternalService(serviceDto);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testAddExternalService_whenInformationPersonNameIsEmpty_ReturnsFalse() {
        // Arrange
        ExternalServiceHandler externalServiceHandler = mock(ExternalServiceHandler.class);
        ExternalServiceDto serviceDto = new ExternalServiceDto(-1, "newSerivceName", "", "phone");

        when(externalServiceHandler.addExternalService(serviceDto)).thenReturn(false);
        // Act

        boolean result = externalServiceHandler.addExternalService(serviceDto);
        // Assert
        assertFalse(result);
    }

    @Test
    public void testAddExternalService_whenInformationPersonPhoneIsEmpty_ReturnsFalse() {
        // Arrange
        ExternalServiceDto serviceDto = new ExternalServiceDto(-1, "newSerivceName", "name", "");

        when(externalServiceHandlerMock.addExternalService(serviceDto)).thenReturn(false);
    
        // Act
        boolean result = externalServiceHandlerMock.addExternalService(serviceDto);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testAddExternalService_whenServiceNameAlreadyExists_ReturnsFalse() {
        // Arrange
        ExternalServiceDto serviceDto = new ExternalServiceDto(-1, "existSerivce", "name", "phone");
        
        when(externalServiceHandlerMock.addExternalService(serviceDto)).thenReturn(false);
        
        // Act
        boolean result = externalServiceHandlerMock.addExternalService(serviceDto);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testChangeExternalService_whenNewService_returnsTrue() {
        // Arrange
        ExternalServiceDto serviceDto = new ExternalServiceDto(-1, "newSerivceName", "name", "phone");
        when(externalServiceHandlerMock.addExternalService(serviceDto)).thenReturn(true);

        // Act
        boolean result = externalServiceHandlerMock.addExternalService(serviceDto);

        // Assert
        assertTrue(result);
        assertTrue(result);
    }

    @Test
    public void testAddPaymentService_whenNewService_returnsTrue() {
        // Arrange
        when(externalServiceHandlerMock.addPaymentService("newSerivceName", "name", "phone")).thenReturn(true);

        // Act
        boolean result = externalServiceHandlerMock.addPaymentService("newSerivceName", "name", "phone");

        // Assert
        assertTrue(result);
    }

    @Test
    public void testAddSupplyService_whenNewService_returnsTrue() {
        // Arrange
        when(externalServiceHandlerMock.addSupplyService("newSerivceName", "name", "phone")).thenReturn(true);

        // Act
        boolean result = externalServiceHandlerMock.addSupplyService("newSerivceName", "name", "phone");

        // Assert
        assertTrue(result);
    }

    @Test
    public void testConnectToServices_whenAllServicesConnected_returnsTrue() {
        // Arrange
        when(externalServiceHandlerMock.connectToServices()).thenReturn(true);

        // Act
        boolean result = externalServiceHandlerMock.connectToServices();

        // Assert
        assertTrue(result);
    }

    @Test
    public void testConnectToServices_whenNotAllServicesConnected_returnsFalse() {
        // Arrange
        when(externalServiceHandlerMock.connectToServices()).thenReturn(false);

        // Act
        boolean result = externalServiceHandlerMock.connectToServices();

        // Assert
        assertFalse(result);
    }

    @Test
    public void testChangeExternalServiceName_whenNewServiceName_returnsTrue() {
        // Arrange
        when(externalServiceHandlerMock.changeExternalServiceName(1, "newServiceName")).thenReturn(true);

        // Act
        boolean result = externalServiceHandlerMock.changeExternalServiceName(1, "newServiceName");

        // Assert
        assertTrue(result);
    }

    @Test
    public void testChangeExternalServiceName_whenNewServiceNameIsEmpty_returnsFalse() {
        // Arrange
        when(externalServiceHandlerMock.changeExternalServiceName(1, "")).thenReturn(false);

        // Act
        boolean result = externalServiceHandlerMock.changeExternalServiceName(1, "");

        // Assert
        assertFalse(result);
    }

    @Test
    public void testChangeExternalServiceName_whenServiceIdDoesNotExist_returnsFalse() {
        // Arrange
        when(externalServiceHandlerMock.changeExternalServiceName(1, "newServiceName")).thenReturn(false);

        // Act
        boolean result = externalServiceHandlerMock.changeExternalServiceName(1, "newServiceName");

        // Assert
        assertFalse(result);
    }

    @Test
    public void testChangeExternalServiceName_whenServiceIdIsNull_returnsFalse() {
        // Arrange
        when(externalServiceHandlerMock.changeExternalServiceName(1, null)).thenReturn(false);

        // Act
        boolean result = externalServiceHandlerMock.changeExternalServiceName(1, null);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testChangeExternalServiceInformationPersonName_whenNewInformationPersonName_returnsTrue() {
        // Arrange
        when(externalServiceHandlerMock.changeExternalServiceInformationPersonName(1, "newInformationPersonName")).thenReturn(true);

        // Act
        boolean result = externalServiceHandlerMock.changeExternalServiceInformationPersonName(1, "newInformationPersonName");

        // Assert
        assertTrue(result);
    }

    @Test
    public void testChangeExternalServiceInformationPersonName_whenNewInformationPersonNameIsEmpty_returnsFalse() {
        // Arrange
        when(externalServiceHandlerMock.changeExternalServiceInformationPersonName(1, "")).thenReturn(false);

        // Act
        boolean result = externalServiceHandlerMock.changeExternalServiceInformationPersonName(1, "");

        // Assert
        assertFalse(result);
    }

    @Test
    public void testChangeExternalServiceInformationPersonName_whenServiceIdDoesNotExist_returnsFalse() {
        // Arrange
        when(externalServiceHandlerMock.changeExternalServiceInformationPersonName(1, "newInformationPersonName")).thenReturn(false);

        // Act
        boolean result = externalServiceHandlerMock.changeExternalServiceInformationPersonName(1, "newInformationPersonName");

        // Assert
        assertFalse(result);
    }

    @Test
    public void testChangeExternalServiceInformationPersonName_whenServiceIdIsNull_returnsFalse() {
        // Arrange
        when(externalServiceHandlerMock.changeExternalServiceInformationPersonName(1, null)).thenReturn(false);

        // Act
        boolean result = externalServiceHandlerMock.changeExternalServiceInformationPersonName(1, null);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testChangeExternalServiceInformationPersonPhone_whenNewInformationPersonPhone_returnsTrue() {
        // Arrange
        when(externalServiceHandlerMock.changeExternalServiceInformationPersonPhone(1, "newInformationPersonPhone")).thenReturn(true);

        // Act
        boolean result = externalServiceHandlerMock.changeExternalServiceInformationPersonPhone(1, "newInformationPersonPhone");

        // Assert
        assertTrue(result);
    }

    @Test
    public void testChangeExternalServiceInformationPersonPhone_whenNewInformationPersonPhoneIsEmpty_returnsFalse() {
        // Arrange
        when(externalServiceHandlerMock.changeExternalServiceInformationPersonPhone(1, "")).thenReturn(false);

        // Act
        boolean result = externalServiceHandlerMock.changeExternalServiceInformationPersonPhone(1, "");

        // Assert
        assertFalse(result);
    }

    @Test
    public void testChangeExternalServiceInformationPersonPhone_whenServiceIdDoesNotExist_returnsFalse() {
        // Arrange
        when(externalServiceHandlerMock.changeExternalServiceInformationPersonPhone(1, "newInformationPersonPhone")).thenReturn(false);

        // Act
        boolean result = externalServiceHandlerMock.changeExternalServiceInformationPersonPhone(1, "newInformationPersonPhone");

        // Assert
        assertFalse(result);
    }

    @Test
    public void testChangeExternalServiceInformationPersonPhone_whenServiceIdIsNull_returnsFalse() {
        // Arrange
        when(externalServiceHandlerMock.changeExternalServiceInformationPersonPhone(1, null)).thenReturn(false);

        // Act
        boolean result = externalServiceHandlerMock.changeExternalServiceInformationPersonPhone(1, null);

        // Assert
        assertFalse(result);
    }
}
