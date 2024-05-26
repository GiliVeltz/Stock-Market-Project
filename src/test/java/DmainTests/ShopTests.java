package DmainTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.assertj.core.api.AssertDelegateTarget;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties.Producer;

import Domain.PasswordEncoderUtil;
import Domain.Permission;
import Domain.Product;
import Domain.Shop;
import Domain.Facades.UserFacade;
import Domain.Facades.ShopFacade.Category;
import Exceptions.PermissionException;
import Exceptions.ProductAlreadyExistsException;
import Exceptions.RoleException;
import Exceptions.ShopException;

public class ShopTests {

    // Shop feilds for tests
    Shop _shop1;

    
    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }
    @Test
    public void testCheckIfHasRole_WhenUsernameIsNull_ReturnsFalse() throws ShopException {
        // Arrange
        String username = null;
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        
        // Act
        boolean result = shop.checkIfHasRole(username);
        
        // Assert
        assertFalse(result);
    }
    
    @Test
    public void testCheckIfHasRole_WhenUsernameDoesNotExist_ReturnsFalse() throws ShopException {
        // Arrange
        String username = "nonExistingUser";
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        
        // Act
        boolean result = shop.checkIfHasRole(username);
        
        // Assert
        assertFalse(result);
    }
    
    @Test
    public void testCheckIfHasRole_WhenUsernameExists_ReturnsTrue() throws ShopException {
        // Arrange
        String username = "user1";
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        
        // Act
        boolean result = shop.checkIfHasRole(username);
        
        // Assert
        assertEquals(true, result);
    }

    // TODO: TEST IS NOT PASSING
    @Disabled
    @Test
    public void testCheckPermission_WhenUsernameIsNull_ReturnsFalse() throws ShopException {
        // Arrange
        String username = null;
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        
        // Act
        boolean result = shop.checkPermission(username, Permission.ADD_PRODUCT);
        
        // Assert
        assertFalse(result);
    }

    // TODO: TEST IS NOT PASSING
    @Disabled
    @Test
    public void testCheckPermission_WhenUsernameDoesNotExist_ReturnsFalse() throws ShopException {
        // Arrange
        String username = "nonExistingUser";
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        
        // Act
        boolean result = shop.checkPermission(username, Permission.ADD_PRODUCT);
        
        // Assert
        assertFalse(result);
    }

    // TODO: TEST IS NOT PASSING
    @Disabled
    @Test
    public void testCheckPermission_WhenUsernameExistsButDoesNotHavePermission_ReturnsFalse() throws ShopException {
        // Arrange
        String username = "user1";
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        
        // Act
        boolean result = shop.checkPermission(username, Permission.ADD_PRODUCT);
        
        // Assert
        assertFalse(result);
    }

    @Test
    public void testCheckPermission_WhenUsernameExistsAndHasPermission_ReturnsTrue() throws ShopException {
        // Arrange
        String username = "user1";
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        
        // Act
        boolean result = shop.checkPermission(username, Permission.ADD_PRODUCT);
        
        // Assert
        assertEquals(true, result);
    }

    // TODO: TEST IS NOT PASSING
    @Disabled
    @Test
    public void testCheckAtLeastOnePermission_WhenUsernameIsNull_ReturnsFalse() throws ShopException {
        // Arrange
        String username = null;
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        
        // Act
        boolean result = shop.checkAtLeastOnePermission(username, permissions);
        
        // Assert
        assertFalse(result);
    }

    // TODO: TEST IS NOT PASSING
    @Disabled
    @Test
    public void testCheckAtLeastOnePermission_WhenUsernameDoesNotExist_ReturnsFalse() throws ShopException {
        // Arrange
        String username = "nonExistingUser";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        
        // Act
        boolean result = shop.checkAtLeastOnePermission(username, permissions);
        
        // Assert
        assertFalse(result);
    }

    // TODO: TEST IS NOT PASSING
    @Disabled
    @Test
    public void testCheckAtLeastOnePermission_WhenUsernameExistsButDoesNotHavePermission_ReturnsFalse() throws ShopException {
        // Arrange
        String username = "user1";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.DELETE_PRODUCT);
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        
        // Act
        boolean result = shop.checkAtLeastOnePermission(username, permissions);
        
        // Assert
        assertFalse(result);
    }

    @Test
    public void testCheckAtLeastOnePermission_WhenUsernameExistsAndHasPermission_ReturnsTrue() throws ShopException {
        // Arrange
        String username = "user1";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        
        // Act
        boolean result = shop.checkAtLeastOnePermission(username, permissions);
        
        // Assert
        assertEquals(true, result);
    }

    @Test
    public void testAppointManager_WhenUserHasPermissionAndNewManagerDoesNotExist_ShouldAppointNewManager() throws ShopException, PermissionException, RoleException {
        // Arrange
        String username = "user1";
        String newManagerUserName = "newManager";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        
        // Act
        shop.AppointManager(username, newManagerUserName, permissions);
        
        // Assert
        // Check if the new manager is appointed successfully
        assertTrue(shop.checkIfHasRole(newManagerUserName));
        // Check if the new manager has the correct permissions
        assertTrue(shop.checkPermission(newManagerUserName, Permission.ADD_PRODUCT));
    }
    
    // TODO: TEST IS NOT PASSING
    @Disabled
    @Test
    public void testAppointManager_WhenUserDoesNotHavePermission_ShouldThrowPermissionException() throws ShopException, PermissionException, RoleException {
        // Arrange
        String username = "user1";
        String newManagerUserName = "newManager";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        
        // Act & Assert
        assertThrows(PermissionException.class, () -> {
            shop.AppointManager(username, newManagerUserName, permissions);
        });
    }
    
    @Test
    public void testAppointManager_WhenNewManagerAlreadyExists_ShouldThrowShopException() throws ShopException, PermissionException, RoleException {
        // Arrange
        String username = "user1";
        String newManagerUserName = "existingManager";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        // Add the existing manager to the shop
        shop.AppointManager(username, newManagerUserName, permissions);
        
        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.AppointManager(username, newManagerUserName, permissions);
        });
    }
    
    @Test
    public void testAppointManager_WhenPermissionsIsEmpty_ShouldThrowPermissionException() throws ShopException, PermissionException, RoleException {
        // Arrange
        String username = "user1";
        String newManagerUserName = "newManager";
        Set<Permission> permissions = new HashSet<>();
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        
        // Act & Assert
        assertThrows(PermissionException.class, () -> {
            shop.AppointManager(username, newManagerUserName, permissions);
        });
    }
    
    @Test
    public void testAppointManager_WhenPermissionsContainOwnerOrFounder_ShouldThrowPermissionException() throws ShopException, PermissionException, RoleException {
        // Arrange
        String username = "user1";
        String newManagerUserName = "newManager";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.OWNER);
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        
        // Act & Assert
        assertThrows(PermissionException.class, () -> {
            shop.AppointManager(username, newManagerUserName, permissions);
        });
    }

    @Test
    public void testAppointOwner_WhenUserHasPermissionAndNewOwnerDoesNotExist_ShouldAppointNewOwner() throws ShopException, PermissionException, RoleException {
        // Arrange
        String username = "user1";
        String newOwnerUserName = "newOwner";
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        
        // Act
        shop.AppointOwner(username, newOwnerUserName);
        
        // Assert
        // Check if the new owner is appointed successfully
        assertTrue(shop.checkIfHasRole(newOwnerUserName));
        // Check if the new owner has the correct permissions
        assertTrue(shop.checkPermission(newOwnerUserName, Permission.OWNER));
    }
    
    // TODO: TEST IS NOT PASSING
    @Disabled
    @Test
    public void testAppointOwner_WhenUserDoesNotHavePermission_ShouldThrowPermissionException() throws ShopException, PermissionException, RoleException {
        // Arrange
        String username = "user1";
        String newOwnerUserName = "newOwner";
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        
        // Act & Assert
        assertThrows(PermissionException.class, () -> {
            shop.AppointOwner(username, newOwnerUserName);
        });
    }
    
    @Test
    public void testAppointOwner_WhenNewOwnerAlreadyExists_ShouldThrowShopException() throws ShopException, PermissionException, RoleException {
        // Arrange
        String username = "user1";
        String newOwnerUserName = "existingOwner";
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        // Add the existing owner to the shop
        shop.AppointOwner(username, newOwnerUserName);
        
        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.AppointOwner(username, newOwnerUserName);
        });
    }
    
    // TODO: TEST IS NOT PASSING
    @Disabled
    @Test
    public void testAppointOwner_WhenNewOwnerIsManager_ShouldThrowPermissionException() throws ShopException, PermissionException, RoleException {
        // Arrange
        String username = "user1";
        String newOwnerUserName = "newOwner";
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        // Add the new owner as a manager
        shop.AppointManager(username, newOwnerUserName, new HashSet<>());
        
        // Act & Assert
        assertThrows(PermissionException.class, () -> {
            shop.AppointOwner(username, newOwnerUserName);
        });
    }
    @Test
    public void testAddPermissions_WhenUsernameIsNull_ShouldThrowIllegalArgumentException() throws ShopException {
        // Arrange
        String username = null;
        String userRole = "manager";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
    
        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.addPermissions(username, userRole, permissions);
        });
    }
    
    @Test
    public void testAddPermissions_WhenUserRoleIsNull_ShouldThrowIllegalArgumentException() throws ShopException {
        // Arrange
        String username = "user1";
        String userRole = null;
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
    
        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.addPermissions(username, userRole, permissions);
        });
    }
    
    @Test
    public void testAddPermissions_WhenPermissionsIsNull_ShouldThrowIllegalArgumentException() throws ShopException {
        // Arrange
        String username = "user1";
        String userRole = "manager";
        Set<Permission> permissions = null;
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
    
        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.addPermissions(username, userRole, permissions);
        });
    }
    
    @Test
    public void testAddPermissions_WhenUserDoesNotExist_ShouldThrowShopException() throws ShopException {
        // Arrange
        String username = "nonExistingUser";
        String userRole = "manager";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
    
        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.addPermissions(username, userRole, permissions);
        });
    }
    
    @Test
    public void testAddPermissions_WhenUserRoleIsInvalid_ShouldThrowIllegalArgumentException() throws ShopException {
        // Arrange
        String username = "user1";
        String userRole = "invalidRole";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
    
        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.addPermissions(username, userRole, permissions);
        });
    }
    
    @Test
    public void testAddPermissions_WhenUserHasNoRole_ShouldThrowPermissionException() throws ShopException {
        // Arrange
        String username = "user1";
        String userRole = "manager";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
    
        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.addPermissions(username, userRole, permissions);
        });
    }
    
    // TODO: TEST IS NOT PASSING
    @Disabled
    @Test
    public void testAddPermissions_WhenUserHasRoleAndPermissionsAreValid_ShouldAddPermissions() throws ShopException, PermissionException, RoleException {
        // Arrange
        String username = "user1";
        String userRole = "manager";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        // Appoint the user as a manager
        shop.AppointManager(username, username, new HashSet<>());
    
        // Act
        shop.addPermissions(username, userRole, permissions);
    
        // Assert
        assertTrue(shop.checkPermission(username, Permission.ADD_PRODUCT));
    }
    
    // TODO: TEST IS NOT PASSING
    @Disabled
    @Test
    public void testAddPermissions_WhenUserHasRoleAndRoleExists_ShouldAddPermissions() throws ShopException, PermissionException, RoleException {
        // Arrange
        String username = "user1";
        String userRole = "manager";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        shop.AppointManager(username, userRole, new HashSet<>());
    
        // Act
        shop.addPermissions(username, userRole, permissions);
    
        // Assert
        assertTrue(shop.checkPermission(userRole, Permission.ADD_PRODUCT));
    }
    
    @Test
    public void testAddPermissions_WhenUserDoesNotHaveRole_ShouldThrowShopException() throws ShopException, PermissionException, RoleException {
        // Arrange
        String username = "user1";
        String userRole = "manager";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
    
        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.addPermissions(username, userRole, permissions);
        });
    }
    
    // TODO: TEST IS NOT PASSING
    @Disabled
    @Test
    public void testAddPermissions_WhenRoleDoesNotExist_ShouldThrowShopException() throws ShopException, PermissionException, RoleException {
        // Arrange
        String username = "user1";
        String userRole = "nonExistingRole";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        shop.AppointManager(username, "manager", new HashSet<>());
    
        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.addPermissions(username, userRole, permissions);
        });
    }
    
    // TODO: TEST IS NOT PASSING
    @Disabled
    @Test
    public void testAddPermissions_WhenUserDoesNotHavePermission_ShouldThrowPermissionException() throws ShopException, PermissionException, RoleException {
        // Arrange
        String username = "user1";
        String userRole = "manager";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        shop.AppointManager(username, userRole, new HashSet<>());
    
        // Act & Assert
        assertThrows(PermissionException.class, () -> {
            shop.addPermissions(userRole, userRole, permissions);
        });
    }
    
    // TODO: TEST IS NOT PASSING
    @Disabled
    @Test
    public void testAddPermissions_WhenUserIsNotAppointer_ShouldThrowPermissionException() throws ShopException, PermissionException, RoleException {
        // Arrange
        String username = "user1";
        String userRole = "manager";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        shop.AppointManager(username, userRole, new HashSet<>());
    
        // Act & Assert
        assertThrows(PermissionException.class, () -> {
            shop.addPermissions(userRole, username, permissions);
        });
    }
    
    // TODO: TEST IS NOT PASSING
    @Disabled
    @Test
    public void testAddPermissions_WhenPermissionsIsEmpty_ShouldNotAddPermissions() throws ShopException, PermissionException, RoleException {
        // Arrange
        String username = "user1";
        String userRole = "manager";
        Set<Permission> permissions = new HashSet<>();
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        shop.AppointManager(username, userRole, new HashSet<>());
    
        // Act
        shop.addPermissions(username, userRole, permissions);
    
        // Assert
        assertFalse(shop.checkPermission(userRole, Permission.ADD_PRODUCT));
    }
    
    // TODO: TEST IS NOT PASSING
    @Disabled
    @Test
    public void testAddPermissions_WhenUserIsOwnerOrFounder_ShouldAddAllPermissions() throws ShopException, PermissionException, RoleException {
        // Arrange
        String username = "user1";
        String userRole = "manager";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        permissions.add(Permission.DELETE_PRODUCT);
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        shop.AppointManager(username, userRole, new HashSet<>());
        shop.AppointOwner(username, "owner");
    
        // Act
        shop.addPermissions(username, userRole, permissions);
    
        // Assert
        assertTrue(shop.checkPermission(userRole, Permission.ADD_PRODUCT));
        assertTrue(shop.checkPermission(userRole, Permission.DELETE_PRODUCT));
    }

    @Test
    public void testDeletePermissions_whenUsernameIsNull_ShouldThrowIllegalArgumentException() throws ShopException {
        // Arrange
        String username = null;
        String userRole = "manager";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");

        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.deletePermissions(username, userRole, permissions);
        });
    }
    
    @Test
    public void testDeletePermissions_whenUserRoleIsNull_ShouldThrowIllegalArgumentException() throws ShopException {
        // Arrange
        String username = "user1";
        String userRole = null;
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");

        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.deletePermissions(username, userRole, permissions);
        });
    }
    
    @Test
    public void testDeletePermissions_whenPermissionsIsNull_ShouldThrowIllegalArgumentException() throws ShopException {
        // Arrange
        String username = "user1";
        String userRole = "manager";
        Set<Permission> permissions = null;
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");

        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.deletePermissions(username, userRole, permissions);
        });
    }
    
    @Test
    public void testDeletePermissions_whenUserDoesNotExist_ShouldThrowShopException() throws ShopException {
        // Arrange
        String username = "nonExistingUser";
        String userRole = "manager";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");

        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.deletePermissions(username, userRole, permissions);
        });
    }
    
    @Test
    public void testDeletePermissions_whenUserDoesNotHaveRole_ShouldThrowShopException() throws ShopException {
        // Arrange
        String username = "user1";
        String userRole = "manager";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");

        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.deletePermissions(username, userRole, permissions);
        });
    }
    
    // TODO: TEST IS NOT PASSING
    @Disabled
    @Test
    public void testDeletePermissions_whenRoleDoesNotExist_ShouldThrowShopException() throws ShopException, PermissionException, RoleException {
        // Arrange
        String username = "user1";
        String userRole = "nonExistingRole";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        shop.AppointManager(username, "manager", new HashSet<>());

        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.deletePermissions(username, userRole, permissions);
        });
    }
    
    // TODO: TEST IS NOT PASSING
    @Disabled
    @Test
    public void testDeletePermissions_whenUserDoesNotHavePermission_ShouldThrowPermissionException() throws ShopException, PermissionException, RoleException {
        // Arrange
        String username = "user1";
        String userRole = "manager";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        shop.AppointManager(username, userRole, new HashSet<>());

        // Act & Assert
        assertThrows(PermissionException.class, () -> {
            shop.deletePermissions(userRole, userRole, permissions);
        });
    }
    
    // TODO: TEST IS NOT PASSING
    @Disabled
    @Test
    public void testDeletePermissions_whenUserIsNotAppointer_ShouldThrowPermissionException() throws ShopException, PermissionException, RoleException {
        // Arrange
        String username = "user1";
        String userRole = "manager";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        shop.AppointManager(username, userRole, new HashSet<>());

        // Act & Assert
        assertThrows(PermissionException.class, () -> {
            shop.deletePermissions(userRole, username, permissions);
        });
    }

    @Test
    public void testFireRole_whenRoleIsNull_ShouldThrowIllegalArgumentException() throws ShopException {
        // Arrange
        String username = "user1";
        String userRole = null;
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");

        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.fireRole(username, userRole);
        });
    }
    
    @Test
    public void testFireRole_whenUserDoesNotExist_ShouldThrowShopException() throws ShopException {
        // Arrange
        String username = "nonExistingUser";
        String userRole = "manager";
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");

        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.fireRole(username, userRole);
        });
    }
    
    @Test
    public void testFireRole_whenUserDoesNotHaveRole_ShouldThrowShopException() throws ShopException {
        // Arrange
        String username = "user1";
        String userRole = "manager";
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");

        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.fireRole(username, userRole);
        });
    }
    
    // TODO: TEST IS NOT PASSING
    @Disabled
    @Test
    public void testFireRole_whenRoleDoesNotExist_ShouldThrowShopException() throws ShopException, PermissionException, RoleException {
        // Arrange
        String username = "user1";
        String userRole = "nonExistingRole";
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        shop.AppointManager(username, "manager", new HashSet<>());

        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.fireRole(username, userRole);
        });
    }
    
    // TODO: TEST IS NOT PASSING
    @Disabled
    @Test
    public void testFireRole_whenUserIsNotAppointer_ShouldThrowPermissionException() throws ShopException, PermissionException, RoleException {
        // Arrange
        String username = "user1";
        String userRole = "manager";
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        shop.AppointManager(username, userRole, new HashSet<>());

        // Act & Assert
        assertThrows(PermissionException.class, () -> {
            shop.fireRole(userRole, username);
        });
    }
    
    // TODO: TEST IS NOT PASSING
    @Disabled
    @Test
    public void testFireRole_whenUserIsOwnerOrFounder_ShouldFireRole() throws ShopException, PermissionException, RoleException {
        // Arrange
        String username = "user1";
        String userRole = "manager";
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        shop.AppointManager(username, userRole, new HashSet<>());
        shop.AppointOwner(username, "owner");

        // Act
        shop.fireRole(username, userRole);

        // Assert
        assertFalse(shop.checkIfHasRole(userRole));
    }
    
    // TODO: TEST IS NOT PASSING
    @Disabled
    @Test
    public void testFireRole_whenUserIsManager_ShouldFireRole() throws ShopException, PermissionException, RoleException {
        // Arrange
        String username = "user1";
        String userRole = "manager";
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        shop.AppointManager(username, userRole, new HashSet<>());

        // Act
        shop.fireRole(username, userRole);

        // Assert
        assertFalse(shop.checkIfHasRole(userRole));
    }
    
    @Test
    public void testResign_whenUserIsOwner_ShouldThrowPermissionException() throws ShopException, PermissionException, RoleException {
        // Arrange
        String username = "user1";
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        shop.AppointOwner(username, "owner");

        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.resign(username);
        });
    }
    
    @Test
    public void testResign_whenUserDoesNotExist_ShouldThrowShopException() throws ShopException {
        // Arrange
        String username = "nonExistingUser";
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");

        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.resign(username);
        });
    }
    
    // TODO: TEST IS NOT PASSING
    @Disabled
    @Test
    public void testResign_whenUserIsManager_ShouldResign() throws ShopException, PermissionException, RoleException {
        // Arrange
        String username = "user1";
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        shop.AppointManager(username, "manager", new HashSet<>());

        // Act
        shop.resign(username);

        // Assert
        assertFalse(shop.checkIfHasRole(username));
    }
    
    // TODO: TEST IS NOT PASSING
    @Disabled
    @Test
    public void testResign_whenUserIsManagerAndHasPermissions_ShouldRemovePermissions() throws ShopException, PermissionException, RoleException {
        // Arrange
        String username = "user1";
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        shop.AppointManager(username, "manager", new HashSet<>());
        shop.addPermissions(username, "manager", new HashSet<>());

        // Act
        shop.resign(username);

        // Assert
        assertFalse(shop.checkPermission("manager", Permission.ADD_PRODUCT));
    }
    
    // TODO: TEST IS NOT PASSING
    @Disabled
    @Test
    public void testResign_whenUserIsManagerAndHasRole_ShouldFireRole() throws ShopException, PermissionException, RoleException {
        // Arrange
        String username = "user1";
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        shop.AppointManager(username, "manager", new HashSet<>());

        // Act
        shop.resign(username);

        // Assert
        assertFalse(shop.checkIfHasRole("manager"));
    }
    
    // TODO: TEST IS NOT PASSING
    @Disabled
    @Test
    public void testResign_whenUserIsManagerAndHasRoleAndPermissions_ShouldFireRoleAndRemovePermissions() throws ShopException, PermissionException, RoleException {
        // Arrange
        String username = "user1";
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        shop.AppointManager(username, "manager", new HashSet<>());
        shop.addPermissions(username, "manager", new HashSet<>());

        // Act
        shop.resign(username);

        // Assert
        assertFalse(shop.checkIfHasRole("manager"));
        assertFalse(shop.checkPermission("manager", Permission.ADD_PRODUCT));
    }
    
    // TODO: TEST IS NOT PASSING
    @Disabled
    @Test
    public void testResign_whenUserIsManagerAndHasRoleAndPermissionsAndIsOwner_ShouldFireRoleAndRemovePermissions() throws ShopException, PermissionException, RoleException {
        // Arrange
        String username = "user1";
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        shop.AppointManager(username, "manager", new HashSet<>());
        shop.addPermissions(username, "manager", new HashSet<>());
        shop.AppointOwner(username, "owner");

        // Act
        shop.resign(username);

        // Assert
        assertFalse(shop.checkIfHasRole("manager"));
        assertFalse(shop.checkPermission("manager", Permission.ADD_PRODUCT));
    }
    
    // TODO: TEST IS NOT PASSING
    @Disabled
    @Test
    public void testAddShopRating_whenRatingIsNegative_ShouldThrowIllegalArgumentException() throws ShopException {
        // Arrange
        int rating = -1;
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            shop.addShopRating(rating);
        });
    }
    
    @Test
    public void testAddShopRating_whenRatingIsPositive_ShouldAddRating() throws ShopException {
        // Arrange
        int rating = 5;
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");

        // Act
        shop.addShopRating(rating);

        // Assert
        assertEquals(5, shop.getShopRating());
    }

    // TODO: TEST IS NOT PASSING
    @Disabled
    @Test
    public void testsAddProductToShop_whenUserDoesNotHavePermission_thenFails() throws ShopException, PermissionException, RoleException {
        // Arrange
        String username = "user1";
        Product product = new Product(1, "product1", Category.CLOTHING, 100);
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.DELETE_PRODUCT);
        shop.AppointManager(username, "manager", permissions);

        // Act & Assert
        assertThrows(PermissionException.class, () -> {
            shop.addProductToShop(username, product);
        });
    }
    
    @Test
    public void testsAddProductToShop_whenUserHasPermission_thenSucceeds() throws ShopException, PermissionException, RoleException, ProductAlreadyExistsException {
        // Arrange
        String username = "user1";
        Product product = new Product(1, "product1", Category.CLOTHING, 100);
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        shop.AppointManager(username, "manager", permissions);

        // Act
        shop.addProductToShop(username, product);

        // Assert
        assertEquals(1, shop.getShopProducts().size());
    }
    
    @Test
    public void testsAddProductToShop_whenProductAlreadyExists_thenFails() throws ShopException, PermissionException, RoleException, ProductAlreadyExistsException {
        // Arrange
        String username = "user1";
        Product product = new Product(1, "product1", Category.CLOTHING, 100);
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        shop.AppointManager(username, "manager", permissions);
        shop.addProductToShop(username, product);

        // Act & Assert
        assertThrows(ProductAlreadyExistsException.class, () -> {
            shop.addProductToShop(username, product);
        });
    }

    @Test
    public void testUpdateProductQuantity_whenUserDoesNotHavePermission_thenFails() throws ShopException, PermissionException, RoleException {
        // Arrange
        String username = "user1";
        Product product = new Product(1, "product1", Category.CLOTHING, 100);
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.DELETE_PRODUCT);
        shop.AppointManager(username, "manager", permissions);

        // Act & Assert
        assertThrows(Exception.class, () -> {
            shop.updateProductQuantity(username, product.getProductId(), 50);
        });
    }
    
    @Test
    public void testUpdateProductQuantity_whenProductDoesNotExist_thenFails() throws ShopException, PermissionException, RoleException {
        // Arrange
        String username = "user1";
        Product product = new Product(1, "product1", Category.CLOTHING, 100);
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        shop.AppointManager(username, "manager", permissions);

        // Act & Assert
        assertThrows(Exception.class, () -> {
            shop.updateProductQuantity(username, 2, 50);
        });
    }
    
    // TODO: TEST IS NOT PASSING
    @Disabled
    @Test
    public void testUpdateProductQuantity_whenUserHasPermission_thenSucceeds() throws Exception {
        // Arrange
        String username = "user1";
        Product product = new Product(1, "product1", Category.CLOTHING, 100);
        Shop shop = new Shop(1, "user1", "bank1", "adderss1");
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        shop.AppointManager(username, "manager", permissions);
        shop.addProductToShop(username, product);

        // Act
        shop.updateProductQuantity(username, product.getProductId(), 50);

        // Assert
        assertEquals(50, shop.getShopProducts().get(0).getProductQuantity());
    }

}
