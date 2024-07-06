package DomainTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Domain.Product;
import Domain.Shop;
import Domain.Discounts.Discount;
import Domain.Discounts.ProductPercentageDiscount;
import Dtos.Rules.AllItemsRuleDto;
import Dtos.Rules.ShoppingBasketRuleDto;
import Exceptions.PermissionException;
import Exceptions.ProductAlreadyExistsException;
import Exceptions.ShopException;
import Exceptions.StockMarketException;
import enums.Category;
import enums.Permission;

public class ShopTests {

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testCheckIfHasRole_WhenUsernameIsNull_ReturnsFalse() throws StockMarketException {
        // Arrange
        String username = null;
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        // Act
        boolean result = shop.checkIfHasRole(username);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testCheckIfHasRole_WhenUsernameDoesNotExist_ReturnsFalse() throws StockMarketException {
        // Arrange
        String username = "nonExistingUser";
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        // Act
        boolean result = shop.checkIfHasRole(username);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testCheckIfHasRole_WhenUsernameExists_ReturnsTrue() throws StockMarketException {
        // Arrange
        String username = "user1";
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        // Act
        boolean result = shop.checkIfHasRole(username);

        // Assert
        assertEquals(true, result);
    }

    @Test
    public void testCheckPermission_WhenUsernameIsNull_ReturnsFalse() throws StockMarketException {
        // Arrange
        boolean result = true;
        String username = null;
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        try {
            // Act
            result = shop.checkPermission(username, Permission.ADD_PRODUCT);
        } catch (Exception e) {
            result = false;
        }

        // Assert
        assertFalse(result);
    }

    @Test
    public void testCheckPermission_WhenUsernameDoesNotExist_ReturnsFalse() throws StockMarketException {
        // Arrange
        boolean result = true;
        String username = "nonExistingUser";
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        try {
            // Act
            result = shop.checkPermission(username, Permission.ADD_PRODUCT);
        } catch (Exception e) {
            result = false;
        }
        // Assert
        assertFalse(result);
    }

    @Test
    public void testCheckPermission_WhenUsernameExistsButDoesNotHavePermission_ReturnsFalse()
            throws StockMarketException {
        // Arrange
        boolean result = true;
        String username = "user1";
        Shop shop = new Shop(1, "shopName1", "founder", "bank1", "adderss1");
        try {
            // Act
            result = shop.checkPermission(username, Permission.ADD_PRODUCT);
        } catch (Exception e) {
            result = false;
        }

        // Assert
        assertFalse(result);
    }

    @Test
    public void testCheckPermission_WhenUsernameExistsInShopRoleButDoesNotHavePermission_ReturnsFalse()
            throws StockMarketException {
        // Arrange
        boolean result = true;
        String username = "user1";
        Shop shop = new Shop(1, "shopName1", "founder", "bank1", "adderss1");
        try {
            // Act
            result = shop.checkPermission(username, Permission.ADD_PRODUCT);
        } catch (Exception e) {
            result = false;
        }

        // Assert
        assertFalse(result);
    }

    @Test
    public void testCheckPermission_WhenUsernameExistsAndHasPermission_ReturnsTrue() throws StockMarketException {
        // Arrange
        String username = "user1";
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        // Act
        boolean result = shop.checkPermission(username, Permission.ADD_PRODUCT);

        // Assert
        assertEquals(true, result);
    }

    @Test
    public void testCheckAtLeastOnePermission_WhenUsernameIsNull_ReturnsFalse() throws StockMarketException {
        // Arrange
        boolean result = true;
        String username = null;
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        try {
            // Act
            shop.checkAtLeastOnePermission(username, permissions);
            fail("null username should raise an error");
        } catch (Exception e) {
            result = false;
        }

        // Assert
        assertFalse(result);
    }

    @Test
    public void testCheckAtLeastOnePermission_WhenUsernameDoesNotExist_ReturnsFalse() throws StockMarketException {
        // Arrange
        boolean result = true;
        String username = "nonExistingUser";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        try {
            // Act
            shop.checkAtLeastOnePermission(username, permissions);
            fail("nonExistingUser username should raise an error");
        } catch (Exception e) {
            result = false;
        }

        // Assert
        assertFalse(result);
    }

    @Test
    public void testCheckAtLeastOnePermission_WhenUsernameExistsButDoesNotHavePermission_ReturnsFalse()
            throws StockMarketException {
        // Arrange
        boolean result = true;
        String username = "user1";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.DELETE_PRODUCT);
        Shop shop = new Shop(1, "shopName1", "founder", "bank1", "adderss1");

        try {
            // Act
            shop.checkAtLeastOnePermission(username, permissions);
            fail("Username without role should raise an error");
        } catch (Exception e) {
            result = false;
        }

        // Assert
        assertFalse(result);
    }

    @Test
    public void testCheckAtLeastOnePermission_WhenUsernameExistsAndHasPermission_ReturnsTrue()
            throws StockMarketException {
        // Arrange
        String username = "user1";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        // Act
        boolean result = shop.checkAtLeastOnePermission(username, permissions);

        // Assert
        assertEquals(true, result);
    }

    @Test
    public void testAppointManager_WhenUserHasPermissionAndNewManagerDoesNotExist_ShouldAppointNewManager()
            throws StockMarketException {
        // Arrange
        String username = "user1";
        String newManagerUserName = "newManager";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        // Act
        shop.AppointManager(username, newManagerUserName, permissions);

        // Assert
        // Check if the new manager is appointed successfully
        assertTrue(shop.checkIfHasRole(newManagerUserName));
        // Check if the new manager has the correct permissions
        assertTrue(shop.checkPermission(newManagerUserName, Permission.ADD_PRODUCT));
    }

    @Test
    public void testAppointManager_WhenUserDoesNotHavePermission_ShouldThrowPermissionException()
            throws StockMarketException {
        // Arrange
        String username = "user1";
        String newManagerUserName = "newManager";
        String testManagerUserName = "testManagerUserName";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        shop.AppointManager(username, newManagerUserName, permissions);

        // Act & Assert
        assertThrows(PermissionException.class, () -> {
            shop.AppointManager(newManagerUserName, testManagerUserName, permissions);
        });
    }

    @Test
    public void testAppointManager_WhenNewManagerAlreadyExists_ShouldThrowShopException() throws StockMarketException {
        // Arrange
        String username = "user1";
        String newManagerUserName = "existingManager";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        // Add the existing manager to the shop
        shop.AppointManager(username, newManagerUserName, permissions);

        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.AppointManager(username, newManagerUserName, permissions);
        });
    }

    @Test
    public void testAppointManager_WhenPermissionsIsEmpty_ShouldThrowPermissionException() throws StockMarketException {
        // Arrange
        String username = "user1";
        String newManagerUserName = "newManager";
        Set<Permission> permissions = new HashSet<>();
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        // Act & Assert
        assertThrows(PermissionException.class, () -> {
            shop.AppointManager(username, newManagerUserName, permissions);
        });
    }

    @Test
    public void testAppointManager_WhenPermissionsContainOwnerOrFounder_ShouldThrowPermissionException()
            throws StockMarketException {
        // Arrange
        String username = "user1";
        String newManagerUserName = "newManager";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.OWNER);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        // Act & Assert
        assertThrows(PermissionException.class, () -> {
            shop.AppointManager(username, newManagerUserName, permissions);
        });
    }

    @Test
    public void testAppointOwner_WhenUserHasPermissionAndNewOwnerDoesNotExist_ShouldAppointNewOwner()
            throws StockMarketException {
        // Arrange
        String username = "user1";
        String newOwnerUserName = "newOwner";
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        // Act
        shop.AppointOwner(username, newOwnerUserName);

        // Assert
        // Check if the new owner is appointed successfully
        assertTrue(shop.checkIfHasRole(newOwnerUserName));
        // Check if the new owner has the correct permissions
        assertTrue(shop.checkPermission(newOwnerUserName, Permission.OWNER));
    }

    @Test
    public void testAppointOwner_WhenNewOwnerAlreadyExists_ShouldThrowShopException() throws StockMarketException {
        // Arrange
        String username = "user1";
        String newOwnerUserName = "existingOwner";
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        // Add the existing owner to the shop
        shop.AppointOwner(username, newOwnerUserName);

        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.AppointOwner(username, newOwnerUserName);
        });
    }

    @Test
    public void testmodifyPermissions_WhenUsernameIsNull_ShouldThrowIllegalArgumentException()
            throws StockMarketException {
        // Arrange
        String username = null;
        String userRole = "manager";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.modifyPermissions(username, userRole, permissions);
        });
    }

    @Test
    public void testmodifyPermissions_WhenUserRoleIsNull_ShouldThrowIllegalArgumentException()
            throws StockMarketException {
        // Arrange
        String username = "user1";
        String userRole = null;
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.modifyPermissions(username, userRole, permissions);
        });
    }

    @Test
    public void testmodifyPermissions_WhenPermissionsIsNull_ShouldThrowIllegalArgumentException()
            throws StockMarketException {
        // Arrange
        String username = "user1";
        String userRole = "manager";
        Set<Permission> permissions = null;
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.modifyPermissions(username, userRole, permissions);
        });
    }

    @Test
    public void testmodifyPermissions_WhenUserDoesNotExist_ShouldThrowShopException() throws StockMarketException {
        // Arrange
        String username = "nonExistingUser";
        String userRole = "manager";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.modifyPermissions(username, userRole, permissions);
        });
    }

    @Test
    public void testmodifyPermissions_WhenUserRoleIsInvalid_ShouldThrowIllegalArgumentException()
            throws StockMarketException {
        // Arrange
        String username = "user1";
        String userRole = "invalidRole";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.modifyPermissions(username, userRole, permissions);
        });
    }

    @Test
    public void testmodifyPermissions_WhenUserHasNoRole_ShouldThrowPermissionException() throws StockMarketException {
        // Arrange
        String username = "user1";
        String userRole = "manager";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.modifyPermissions(username, userRole, permissions);
        });
    }

    @Test
    public void testmodifyPermissions_WhenUserHasRoleAndPermissionsAreValid_ShouldmodifyPermissions()
            throws StockMarketException {
        // Arrange
        String username = "user1";
        String userRole = "manager";
        Set<Permission> permissions = new HashSet<>();
        Set<Permission> permissionsNew = new HashSet<>();
        permissions.add(Permission.EDIT_PRODUCT);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        // Appoint the user as a manager
        shop.AppointManager(username, userRole, permissions);
        permissionsNew.add(Permission.ADD_PRODUCT);

        // Act
        shop.modifyPermissions(username, userRole, permissionsNew);

        // Assert
        assertTrue(shop.checkPermission(userRole, Permission.ADD_PRODUCT));
    }

    @Test
    public void testmodifyPermissions_WhenUserHasRoleAndRoleExists_ShouldmodifyPermissions()
            throws StockMarketException {
        // Arrange
        String username = "user1";
        String userRole = "manager";
        Set<Permission> permissions = new HashSet<>();
        Set<Permission> permissionsNew = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        permissionsNew.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        shop.AppointManager(username, userRole, permissions);

        // Act
        shop.modifyPermissions(username, userRole, permissionsNew);

        // Assert
        assertTrue(shop.checkPermission(userRole, Permission.ADD_PRODUCT));
    }

    @Test
    public void testmodifyPermissions_WhenUserDoesNotHaveRole_ShouldThrowShopException() throws StockMarketException {
        // Arrange
        String username = "user1";
        String userRole = "manager";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.modifyPermissions(username, userRole, permissions);
        });
    }

    @Test
    public void testmodifyPermissions_WhenUserIsNotAppointer_ShouldThrowPermissionException()
            throws StockMarketException {
        // Arrange
        String username = "user1";
        String userRole = "manager";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        // shop.AppointManager(username, userRole, new HashSet<>());

        // Act & Assert
        assertThrows(StockMarketException.class, () -> {
            shop.modifyPermissions(userRole, username, permissions);
        });
    }

    @Test
    public void testmodifyPermissions_WhenPermissionsIsEmpty_ShouldNotmodifyPermissions() throws StockMarketException {
        // Arrange
        String username = "user1";
        String userRole = "manager";
        Set<Permission> permissions = new HashSet<>();
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        // Act & Assert
        assertThrows(StockMarketException.class, () -> {
            shop.AppointManager(username, userRole, new HashSet<>());
            shop.modifyPermissions(userRole, username, permissions);
        });
    }

    @Test
    public void testmodifyPermissions_WhenUserIsOwnerOrFounder_ShouldAddAllPermissions() throws StockMarketException {
        // Arrange
        String username = "user1";
        String userRole = "manager";
        Set<Permission> permissions = new HashSet<>();
        Set<Permission> permissionsNew = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        permissionsNew.add(Permission.ADD_PRODUCT);
        permissionsNew.add(Permission.DELETE_PRODUCT);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        shop.AppointManager(username, userRole, permissions);
        shop.AppointOwner(username, "owner");

        // Act
        shop.modifyPermissions(username, userRole, permissionsNew);

        // Assert
        assertTrue(shop.checkPermission(userRole, Permission.ADD_PRODUCT));
        assertTrue(shop.checkPermission(userRole, Permission.DELETE_PRODUCT));
    }

    @Test
    public void testmodifyPermissions_whenUsernameIsNull_ShouldThrowIllegalArgumentException()
            throws StockMarketException {
        // Arrange
        String username = null;
        String userRole = "manager";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.modifyPermissions(username, userRole, permissions);
        });
    }

    @Test
    public void testmodifyPermissions_whenUserRoleIsNull_ShouldThrowIllegalArgumentException()
            throws StockMarketException {
        // Arrange
        String username = "user1";
        String userRole = null;
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.modifyPermissions(username, userRole, permissions);
        });
    }

    @Test
    public void testmodifyPermissions_whenPermissionsIsNull_ShouldThrowIllegalArgumentException()
            throws StockMarketException {
        // Arrange
        String username = "user1";
        String userRole = "manager";
        Set<Permission> permissions = null;
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.modifyPermissions(username, userRole, permissions);
        });
    }

    @Test
    public void testmodifyPermissions_whenUserDoesNotExist_ShouldThrowShopException() throws StockMarketException {
        // Arrange
        String username = "nonExistingUser";
        String userRole = "manager";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.modifyPermissions(username, userRole, permissions);
        });
    }

    @Test
    public void testmodifyPermissions_whenUserDoesNotHaveRole_ShouldThrowShopException() throws StockMarketException {
        // Arrange
        String username = "user1";
        String userRole = "manager";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.modifyPermissions(username, userRole, permissions);
        });
    }

    @Test
    public void testFireRole_whenRoleIsNull_ShouldThrowIllegalArgumentException() throws StockMarketException {
        // Arrange
        String username = "user1";
        String userRole = null;
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.fireRole(username, userRole);
        });
    }

    @Test
    public void testFireRole_whenUserDoesNotExist_ShouldThrowShopException() throws StockMarketException {
        // Arrange
        String username = "nonExistingUser";
        String userRole = "manager";
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.fireRole(username, userRole);
        });
    }

    @Test
    public void testFireRole_whenUserDoesNotHaveRole_ShouldThrowShopException() throws StockMarketException {
        // Arrange
        String username = "user1";
        String userRole = "manager";
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.fireRole(username, userRole);
        });
    }

    @Test
    public void testFireRole_whenRoleDoesNotExist_ShouldThrowShopException() throws StockMarketException {
        // Arrange
        String username = "user1";
        String userRole = "nonExistingRole";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        shop.AppointManager(username, "manager", permissions);

        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.fireRole(username, userRole);
        });
    }

    @Test
    public void testFireRole_whenUserIsNotAppointer_ShouldThrowPermissionException() throws StockMarketException {
        // Arrange
        String username = "user1";
        String userRole = "manager";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        shop.AppointManager(username, userRole, permissions);

        // Act & Assert
        assertThrows(PermissionException.class, () -> {
            shop.fireRole(userRole, username);
        });
    }

    @Test
    public void testFireRole_whenUserIsOwnerOrFounder_ShouldFireRole() throws StockMarketException {
        // Arrange
        String username = "user1";
        String userRole = "manager";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        shop.AppointManager(username, userRole, permissions);
        shop.AppointOwner(username, "owner");

        // Act
        shop.fireRole(username, userRole);

        // Assert
        assertFalse(shop.checkIfHasRole(userRole));
    }

    @Test
    public void testFireRole_whenUserIsManager_ShouldFireRole() throws StockMarketException {
        // Arrange
        String username = "user1";
        String userRole = "manager";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        shop.AppointManager(username, userRole, permissions);

        // Act
        shop.fireRole(username, userRole);

        // Assert
        assertFalse(shop.checkIfHasRole(userRole));
    }

    @Test
    public void testResign_whenUserIsOwner_ShouldThrowPermissionException() throws StockMarketException {
        // Arrange
        String username = "user1";
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        shop.AppointOwner(username, "owner");

        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.resign(username);
        });
    }

    @Test
    public void testResign_whenUserDoesNotExist_ShouldThrowShopException() throws StockMarketException {
        // Arrange
        String username = "nonExistingUser";
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.resign(username);
        });
    }

    @Test
    public void testResign_whenUserIsManager_ShouldResign() throws StockMarketException {
        // Arrange
        String username = "user1";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        shop.AppointManager(username, "manager", permissions);

        // Act
        shop.resign("manager");

        // Assert
        assertFalse(shop.checkIfHasRole("manager"));
    }

    @Test
    public void testResign_whenUserIsDounder_ShouldThrowException() throws StockMarketException {
        // Arrange
        String username = "user1";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        shop.AppointManager(username, "manager", permissions);

        // Act & Assert
        assertThrows(ShopException.class, () -> {
            shop.resign(username);
        });
    }

    @Test
    public void testResign_whenUserIsManagerAndHasRole_ShouldFireRole() throws StockMarketException {
        // Arrange
        String username = "user1";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        shop.AppointManager(username, "manager", permissions);

        // Act
        shop.resign("manager");

        // Assert
        assertFalse(shop.checkIfHasRole("manager"));
    }

    @Test
    public void testResign_whenUserIsManagerAndHasRoleAndPermissions_ShouldFireRoleAndRemovePermissions()
            throws StockMarketException {
        // Arrange
        String username = "user1";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        shop.AppointManager(username, "manager", permissions);
        shop.modifyPermissions(username, "manager", permissions);

        // Act
        shop.resign("manager");

        // Assert
        assertFalse(shop.checkIfHasRole("manager"));

        // Act & Assert
        assertFalse( shop.checkPermission("manager", Permission.ADD_PRODUCT));

    }

    @Test
    public void testResign_whenUserIsManagerAndHasRoleAndPermissionsAndIsOwner_ShouldFireRoleAndRemovePermissions()
            throws StockMarketException {
        // Arrange
        String username = "user1";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        shop.AppointManager(username, "manager", permissions);
        shop.modifyPermissions(username, "manager", permissions);
        shop.AppointOwner(username, "owner");

        // Act
        shop.resign("owner");

        // Assert
        assertFalse(shop.checkIfHasRole("owner"));
        // Act & Assert
        assertFalse(shop.checkPermission("owner", Permission.ADD_PRODUCT));

    }

    @Test
    public void testAddShopRating_whenRatingIsNegative_ShouldThrowIllegalArgumentException()
            throws StockMarketException {
        // Arrange
        int rating = -1;
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        // Act & Assert
        assertThrows(StockMarketException.class, () -> {
            shop.addShopRating(rating);
        });
    }

    @Test
    public void testAddShopRating_whenRatingIsPositive_ShouldAddRating() throws StockMarketException {
        // Arrange
        int rating = 5;
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        // Act
        shop.addShopRating(rating);

        // Assert
        assertEquals(5, shop.getShopRating());
    }

    @Test
    public void testAddShopRating_whenRatingIsAboveFive_ShouldThrowIllegalArgumentException()
            throws StockMarketException {
        // Arrange
        int rating = 6;
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        // Act & Assert
        assertThrows(StockMarketException.class, () -> {
            shop.addShopRating(rating);
        });
    }

    @Test
    public void testsAddProductToShop_whenUserHasPermission_thenSucceeds() throws StockMarketException {
        // Arrange
        String username = "user1";
        Product product = new Product(1, "product1", Category.CLOTHING, 100);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        Set<Permission> permissions = new HashSet<Permission>();
        permissions.add(Permission.ADD_PRODUCT);
        shop.AppointManager(username, "manager", permissions);

        // Act
        shop.addProductToShop(username, product);

        // Assert
        assertEquals(1, shop.getShopProducts().size());
    }

    @Test
    public void testsAddProductToShop_whenProductAlreadyExists_thenFails() throws StockMarketException {
        // Arrange
        String username = "user1";
        Product product = new Product(1, "product1", Category.CLOTHING, 100);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
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
    public void testRemoveProductFromShop_whenUserDoesNotHavePermission_thenFails() throws StockMarketException {
        // Arrange
        String username = "user1";
        Product product = new Product(1, "product1", Category.CLOTHING, 100);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        // Act & Assert
        assertThrows(Exception.class, () -> {
            shop.removeProductFromShop(username, product.getProductName());
        });
    }

    @Test
    public void testRemoveProductFromShop_whenProductDoesNotExist_thenFails() throws StockMarketException {
        // Arrange
        String username = "user1";
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.DELETE_PRODUCT);
        shop.AppointManager(username, "manager", permissions);

        // Act & Assert
        assertThrows(Exception.class, () -> {
            shop.removeProductFromShop(username, "product1");
        });
    }

    @Test
    public void testRemoveProductFromShop_whenUserHasPermission_thenSucceeds() throws StockMarketException {
        // Arrange
        String username = "user1";
        Product product = new Product(1, "product1", Category.CLOTHING, 100);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.DELETE_PRODUCT);
        shop.AppointManager(username, "manager", permissions);
        shop.addProductToShop(username, product);

        // Act
        shop.removeProductFromShop(username, product.getProductName());

        // Assert
        assertEquals(0, shop.getShopProducts().size());
    }

    @Test
    public void testRemoveProductFromShop_whenUserIsTheFounder_thenSucceeds() throws StockMarketException {
        // Arrange
        String username = "user1";
        Product product = new Product(1, "product1", Category.CLOTHING, 100);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        shop.addProductToShop(username, product);

        // Act
        shop.removeProductFromShop(username, product.getProductName());

        // Assert
        assertEquals(0, shop.getShopProducts().size());
    }

    @Test
    public void testRemoveProductFromShop_whenUserIsTheOwner_thenSucceeds() throws StockMarketException {
        // Arrange
        String username = "user1";
        Product product = new Product(1, "product1", Category.CLOTHING, 100);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        shop.AppointOwner(username, "owner");
        shop.addProductToShop(username, product);

        // Act
        shop.removeProductFromShop(username, product.getProductName());

        // Assert
        assertEquals(0, shop.getShopProducts().size());
    }

    @Test
    public void testEditProductInShop_whenUserDoesNotHavePermission_thenFails() throws StockMarketException {
        // Arrange
        String username = "user1";
        Product product = new Product(1, "product1", Category.CLOTHING, 100);
        Product newProduct = new Product(1, "product2", Category.CLOTHING, 100);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        // Act & Assert
        assertThrows(Exception.class, () -> {
            shop.editProductInShop(username, product.getProductName(), newProduct.getProductName(),
                    newProduct.getCategory(), newProduct.getPrice());
        });
    }

    @Test
    public void testEditProductInShop_whenProductDoesNotExist_thenFails() throws StockMarketException {
        // Arrange
        String username = "user1";
        Product newProduct = new Product(1, "product2", Category.CLOTHING, 100);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.EDIT_PRODUCT);
        shop.AppointManager(username, "manager", permissions);

        // Act & Assert
        assertThrows(Exception.class, () -> {
            shop.editProductInShop(username, newProduct.getProductName(), newProduct.getProductName(),
                    newProduct.getCategory(), newProduct.getPrice());
        });
    }

    @Test
    public void testEditProductInShop_whenUserHasPermission_thenSucceeds() throws StockMarketException {
        // Arrange
        String username = "user1";
        Product product = new Product(1, "product1", Category.CLOTHING, 100);
        Product newProduct = new Product(1, "product2", Category.CLOTHING, 100);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.EDIT_PRODUCT);
        shop.AppointManager(username, "manager", permissions);
        shop.addProductToShop(username, product);

        // Act
        shop.editProductInShop(username, product.getProductName(), newProduct.getProductName(),
                newProduct.getCategory(), newProduct.getPrice());

        // Assert
        assertEquals(newProduct.getProductName(), shop.getShopProducts().get(1).getProductName());
    }

    @Test
    public void testEditProductInShop_whenUserIsTheFounder_thenSucceeds() throws StockMarketException {
        // Arrange
        String username = "user1";
        Product product = new Product(1, "product1", Category.CLOTHING, 100);
        Product newProduct = new Product(1, "product2", Category.CLOTHING, 100);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        shop.addProductToShop(username, product);

        // Act
        shop.editProductInShop(username, product.getProductName(), newProduct.getProductName(),
                newProduct.getCategory(), newProduct.getPrice());

        // Assert
        assertEquals(newProduct.getProductName(), shop.getShopProducts().get(1).getProductName());
    }

    @Test
    public void testEditProductInShop_whenUserIsTheOwner_thenSucceeds() throws StockMarketException {
        // Arrange
        String username = "user1";
        Product product = new Product(1, "product1", Category.CLOTHING, 100);
        Product newProduct = new Product(1, "product2", Category.CLOTHING, 100);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        shop.AppointOwner(username, "owner");
        shop.addProductToShop(username, product);

        // Act
        shop.editProductInShop(username, product.getProductName(), newProduct.getProductName(),
                newProduct.getCategory(), newProduct.getPrice());

        // Assert
        assertEquals(newProduct.getProductName(), shop.getShopProducts().get(1).getProductName());
    }

    @Test
    public void testUpdateProductQuantity_whenUserDoesNotHavePermission_thenFails() throws StockMarketException {
        // Arrange
        String username = "user1";
        Product product = new Product(1, "product1", Category.CLOTHING, 100);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.DELETE_PRODUCT);
        shop.AppointManager(username, "manager", permissions);

        // Act & Assert
        assertThrows(Exception.class, () -> {
            shop.updateProductQuantity(username, product.getProductId(), 50);
        });
    }

    @Test
    public void testUpdateProductQuantity_whenProductDoesNotExist_thenFails() throws StockMarketException {
        // Arrange
        String username = "user1";
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        shop.AppointManager(username, "manager", permissions);

        // Act & Assert
        assertThrows(Exception.class, () -> {
            shop.updateProductQuantity(username, 2, 50);
        });
    }

    @Test
    public void testUpdateProductQuantity_whenUserHasPermission_thenSucceeds() throws StockMarketException {
        // Arrange
        String username = "user1";
        Product product = new Product(1, "product1", Category.CLOTHING, 100);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        shop.AppointManager(username, "manager", permissions);
        shop.addProductToShop(username, product);

        // Act
        shop.updateProductQuantity(username, product.getProductId(), 50);

        // Assert
        assertEquals(50, shop.getShopProducts().get(1).getProductQuantity());
    }

    @Test
    public void testGetDiscountsOfProduct_whenProductDoesNotExist_thenFails() throws StockMarketException {
        // Arrange
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        // Act & Assert
        assertThrows(Exception.class, () -> {
            shop.getDiscountsOfProduct(1);
        });
    }

    @Test
    public void testGetDiscountsOfProduct_whenProductExists_thenSucceeds() throws StockMarketException {
        // Arrange
        String username = "user1";
        Product product = new Product(1, "product1", Category.CLOTHING, 100);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        shop.AppointManager(username, "manager", permissions);
        shop.addProductToShop(username, product);

        // Act
        Map<Integer, Discount> discounts = shop.getDiscountsOfProduct(product.getProductId());

        // Assert
        assertEquals(0, discounts.size());
    }

    @Test
    public void testGetDiscountsOfProduct_whenDiscountsExist_thenSucceeds() throws StockMarketException {
        // Arrange
        String username = "user1";
        Product product = new Product(1, "product1", Category.CLOTHING, 100);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        shop.AppointManager(username, "manager", permissions);
        shop.addProductToShop(username, product);
        Calendar calendar = Calendar.getInstance();
        calendar.set(2025, Calendar.OCTOBER, 10);
        Date date = calendar.getTime();
        ProductPercentageDiscount discount = new ProductPercentageDiscount(date, 0.2, 1, 1);
        shop.addDiscount(discount);

        // Act
        Map<Integer, Discount> discounts = shop.getDiscountsOfProduct(product.getProductId());

        // Assert
        assertEquals(1, discounts.size());
    }

    @Test
    public void testCheckAllPermission_whenUserHasPermission_thenSucceeds() throws StockMarketException {
        // Arrange
        String username = "user1";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        // Act & Assert
        assertTrue(shop.checkAllPermission(username, permissions));
    }

    @Test
    public void testCheckAllPermission_whenUserHasSomePermissions_thenFails() throws StockMarketException {
        // Arrange
        String username = "user1";
        String usernameFounder = "usernameFounder";
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        permissions.add(Permission.DELETE_PRODUCT);
        Shop shop = new Shop(1, "shopName1", "usernameFounder", "bank1", "adderss1");
        shop.AppointManager(usernameFounder, username, new HashSet<Permission>() {
            {
                add(Permission.ADD_PRODUCT);
            }
        });

        // Act & Assert
        assertFalse(shop.checkAllPermission(username, permissions));
    }

    @Test
    public void testAddDiscount_whenUserHasPermission_thenSucceeds() throws StockMarketException {
        // Arrange
        String username = "user1";
        @SuppressWarnings("deprecation")
        ProductPercentageDiscount discount = new ProductPercentageDiscount(new Date(2025, 10, 10), 0.2, 1, 1);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_DISCOUNT_POLICY);
        shop.AppointManager(username, "manager", permissions);

        // Act
        shop.addDiscount(discount);

        // Assert
        assertEquals(1, shop.getDiscounts().size());
    }

    @Test
    public void testAddDiscount_whenDiscountAlreadyExists_thenFails() throws StockMarketException {
        // Arrange
        String username = "user1";
        @SuppressWarnings("deprecation")
        ProductPercentageDiscount discount = new ProductPercentageDiscount(new Date(2025, 10, 10), 0.2, 1, 1);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_DISCOUNT_POLICY);
        shop.AppointManager(username, "manager", permissions);
        shop.addDiscount(discount);

        // Act & Assert
        assertThrows(StockMarketException.class, () -> {
            shop.addDiscount(discount);
        });
    }

    @Test
    public void testRemoveDiscount_whenDiscountDoesNotExist_thenFails() throws StockMarketException {
        // Arrange
        String username = "user1";
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.REMOVE_DISCOUNT_METHOD);
        shop.AppointManager(username, "manager", permissions);

        // Act & Assert
        assertThrows(StockMarketException.class, () -> {
            shop.removeDiscount(0);
        });
    }

    @Test
    public void testRemoveDiscount_whenUserHasPermission_thenSucceeds() throws StockMarketException {
        // Arrange
        String username = "user1";
        @SuppressWarnings("deprecation")
        ProductPercentageDiscount discount = new ProductPercentageDiscount(new Date(2025, 10, 10), 0.2, 1, 1);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.REMOVE_DISCOUNT_METHOD);
        shop.AppointManager(username, "manager", permissions);
        shop.addDiscount(discount);

        // Act
        shop.removeDiscount(0);

        // Assert
        assertEquals(0, shop.getDiscounts().size());
    }

    @Test
    public void testRemoveDiscount_whenUserHasPermissionAndDiscountExists_thenSucceeds() throws StockMarketException {
        // Arrange
        String username = "user1";
        @SuppressWarnings("deprecation")
        ProductPercentageDiscount discount = new ProductPercentageDiscount(new Date(2025, 10, 10), 0.2, 1, 1);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.REMOVE_DISCOUNT_METHOD);
        shop.AppointManager(username, "manager", permissions);
        shop.addDiscount(discount);

        // Act
        shop.removeDiscount(0);

        // Assert
        assertEquals(0, shop.getDiscounts().size());
    }

    @Test
    public void testGetProductsByName_whenProductDoesNotExist_thenFails() throws StockMarketException {
        // Arrange
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");

        // Act
        List<Product> products = shop.getProductsByName("product1");

        // Assert
        assertEquals(0, products.size());
    }

    @Test
    public void testGetProductsByName_whenProductExists_thenSucceeds() throws StockMarketException {
        // Arrange
        String username = "user1";
        Product product = new Product(1, "product1", Category.CLOTHING, 100);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);
        shop.AppointManager(username, "manager", permissions);
        shop.addProductToShop(username, product);

        // Act
        List<Product> products = shop.getProductsByName("product1");

        // Assert
        assertEquals(1, products.size());
    }

    @Test
    public void testAddReview_whenReviewAdded_thenCorrectReviewAdded() throws StockMarketException {
        // Arrange
        String username = "user1";
        String review = "review1";
        Product product = new Product(1, "product1", Category.CLOTHING, 100);
        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        shop.addProductToShop(username, product);

        // Act
        shop.addReview(username, product.getProductId(), review);

        // Assert
        assertEquals(1, product.getReviews().size());
    }

    @Test
    public void testChangeShopPolicy_whenManagerHasPermission_thenSucceeds() throws StockMarketException {
        // Arrange
        String username = "user1";

        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        Product product1 = new Product(1, "product1", Category.CLOTHING, 100);
        Product product2 = new Product(2, "product2", Category.GROCERY, 50);
        Product product3 = new Product(3, "product3", Category.ELECTRONICS, 200);
        shop.addProductToShop("user1", product1);
        shop.addProductToShop("user1", product2);
        shop.addProductToShop("user1", product3);

        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.CHANGE_SHOP_POLICY);

        shop.AppointManager(username, "manager", permissions);
        List<ShoppingBasketRuleDto> rules = new ArrayList<>();
        rules.add(new AllItemsRuleDto(List.of(1, 3)));

        // Act
        shop.changeShopPolicy("manager", rules);

        // Assert
        assertEquals(1, shop.getShopPolicy().getRules().size());
    }

    @Test
    public void testChangeShopPolicy_whenManagerDoesNotHavePermission_thenFails() throws StockMarketException {
        // Arrange
        String username = "user1";

        Shop shop = new Shop(1, "shopName1", "user1", "bank1", "adderss1");
        Product product1 = new Product(1, "product1", Category.CLOTHING, 100);
        Product product2 = new Product(2, "product2", Category.GROCERY, 50);
        Product product3 = new Product(3, "product3", Category.ELECTRONICS, 200);
        shop.addProductToShop("user1", product1);
        shop.addProductToShop("user1", product2);
        shop.addProductToShop("user1", product3);

        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.ADD_PRODUCT);

        shop.AppointManager(username, "manager", permissions);
        List<ShoppingBasketRuleDto> rules = new ArrayList<>();
        rules.add(new AllItemsRuleDto(List.of(1, 3)));

        // Act
        shop.changeShopPolicy("manager", rules);

        // Assert
        assertEquals(0, shop.getShopPolicy().getRules().size());
    }
}
