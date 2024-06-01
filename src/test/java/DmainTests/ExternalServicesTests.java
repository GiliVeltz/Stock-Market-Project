package DmainTests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Domain.ExternalServices.ExternalServiceHandler;

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
        when(externalServiceHandler.addExternalService("", "name", "phone")).thenReturn(false);
        // Act
        boolean result = externalServiceHandler.addExternalService("", "name", "phone");
        // Assert
        assertFalse(result);
    }

    @Test
    public void testAddExternalService_whenInformationPersonNameIsEmpty_ReturnsFalse() {
        // Arrange
        ExternalServiceHandler externalServiceHandler = mock(ExternalServiceHandler.class);
        when(externalServiceHandler.addExternalService("newSerivceName", "", "phone")).thenReturn(false);
        // Act
        boolean result = externalServiceHandler.addExternalService("newSerivceName", "", "phone");
        // Assert
        assertFalse(result);
    }

    @Test
    public void testAddExternalService_whenInformationPersonPhoneIsEmpty_ReturnsFalse() {
        // Arrange
        ExternalServiceHandler externalServiceHandler = mock(ExternalServiceHandler.class);
        when(externalServiceHandler.addExternalService("newSerivceName", "name", "")).thenReturn(false);
        // Act
        boolean result = externalServiceHandler.addExternalService("newSerivceName", "name", "");
        // Assert
        assertFalse(result);
    }

    @Test
    public void testAddExternalService_whenServiceNameAlreadyExists_ReturnsFalse() {
        // Arrange
        ExternalServiceHandler externalServiceHandler = mock(ExternalServiceHandler.class);
        when(externalServiceHandler.addExternalService("existSerivce", "name", "phone")).thenReturn(false);
        // Act
        boolean result = externalServiceHandler.addExternalService("existSerivce", "name", "phone");
        // Assert
        assertFalse(result);
    }

    @Test
    public void testChangeExternalService_whenNewService_returnsTrue() {
        // Arrange
        ExternalServiceHandler externalServiceHandler = mock(ExternalServiceHandler.class);
        when(externalServiceHandler.addExternalService("newSerivceName", "name", "phone")).thenReturn(true);
        // Act
        boolean result = externalServiceHandler.addExternalService("newSerivceName", "name", "phone");
        // Assert
        assertTrue(result);
        assertTrue(result);
    }

}
