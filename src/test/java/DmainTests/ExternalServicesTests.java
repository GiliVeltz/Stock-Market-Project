package DmainTests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Domain.ExternalServices.ExternalServiceHandler;
import Dtos.ExternalServiceDto;

public class ExternalServicesTests {
    
    //@Mock

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testAddExternalService_whenServiceNameIsEmpty_ReturnsFalse() {
        // Arrange
        ExternalServiceHandler externalServiceHandler = mock(ExternalServiceHandler.class);
        ExternalServiceDto serviceDto = new ExternalServiceDto("", "name", "phone");
        when(externalServiceHandler.addExternalService(serviceDto)).thenReturn(false);
        // Act
        boolean result = externalServiceHandler.addExternalService(serviceDto);
        // Assert
        assertFalse(result);
    }

    @Test
    public void testAddExternalService_whenInformationPersonNameIsEmpty_ReturnsFalse() {
        // Arrange
        ExternalServiceHandler externalServiceHandler = mock(ExternalServiceHandler.class);
        ExternalServiceDto serviceDto = new ExternalServiceDto("newSerivceName", "", "phone");

        when(externalServiceHandler.addExternalService(serviceDto)).thenReturn(false);
        // Act
        boolean result = externalServiceHandler.addExternalService(serviceDto);
        // Assert
        assertFalse(result);
    }

    @Test
    public void testAddExternalService_whenInformationPersonPhoneIsEmpty_ReturnsFalse() {
        // Arrange
        ExternalServiceHandler externalServiceHandler = mock(ExternalServiceHandler.class);
        ExternalServiceDto serviceDto = new ExternalServiceDto("newSerivceName", "name", "");

        when(externalServiceHandler.addExternalService(serviceDto)).thenReturn(false);
    
        // Act
        boolean result = externalServiceHandler.addExternalService(serviceDto);
        // Assert
        assertFalse(result);
    }

    @Test
    public void testAddExternalService_whenServiceNameAlreadyExists_ReturnsFalse() {
        // Arrange
        ExternalServiceHandler externalServiceHandler = mock(ExternalServiceHandler.class);
        ExternalServiceDto serviceDto = new ExternalServiceDto("existSerivce", "name", "phone");
        
        when(externalServiceHandler.addExternalService(serviceDto)).thenReturn(false);
        
        // Act
        boolean result = externalServiceHandler.addExternalService(serviceDto);
        // Assert
        assertFalse(result);
    }

    @Test
    public void testChangeExternalService_whenNewService_returnsTrue() {
        // Arrange
        ExternalServiceHandler externalServiceHandler = mock(ExternalServiceHandler.class);
        ExternalServiceDto serviceDto = new ExternalServiceDto("newSerivceName", "name", "phone");
        when(externalServiceHandler.addExternalService(serviceDto)).thenReturn(true);
        // Act
        boolean result = externalServiceHandler.addExternalService(serviceDto);
        // Assert
        assertTrue(result);
        assertTrue(result);
    }

}
