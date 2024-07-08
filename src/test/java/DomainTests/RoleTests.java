package DomainTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Domain.*;
import Domain.Entities.Role;
import Domain.Entities.Shop;
import Domain.Entities.User;
import Domain.Entities.enums.Permission;
import Exceptions.ShopException;
import Exceptions.StockMarketException;

public class RoleTests {

    // tests fields
    private User user;
    private Shop shop;

    @BeforeEach
    public void setUp() {
        user = new User("user", "password", "email@email.com", new Date());
        try {
            shop = new Shop(1, "shop name",  "founder name",  "shop bank",  "shop address");
        } catch (ShopException e) {
            e.printStackTrace();
            fail("Exception was thrown when it shouldn't have been: " + e.getMessage());
        }
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testHasPermission_whenUserHasPermission_thenReturnTrue() {
        // Arrange
        Set<Permission> permissions = new HashSet<Permission>();
        permissions.add(Permission.ADD_PRODUCT);

        Role role = null;
        try {
            role = new Role(user.getUserName(), shop.getShopId(), "appointedBy", permissions);
        } catch (StockMarketException e) {
            e.printStackTrace();
            fail("Exception was thrown when it shouldn't have been: " + e.getMessage());
        }

        // Act
        @SuppressWarnings("null")
        boolean hasPermission = role.hasPermission(Permission.ADD_PRODUCT);

        // Assert
        assertEquals(true, hasPermission);
    }

    @Test
    public void testHasPermission_whenUserDoesNotHavePermission_thenReturnFalse() {
        // Arrange
        Set<Permission> permissions = new HashSet<Permission>();
        permissions.add(Permission.ADD_PRODUCT);

        Role role = null;
        try {
            role = new Role(user.getUserName(), shop.getShopId(), "appointedBy", permissions);
        } catch (StockMarketException e) {
            e.printStackTrace();
            fail("Exception was thrown when it shouldn't have been: " + e.getMessage());
        }

        // Act
        @SuppressWarnings("null")
        boolean hasPermission = role.hasPermission(Permission.ADD_DISCOUNT_POLICY);

        // Assert
        assertEquals(false, hasPermission);
    }

    @Test
    public void testHasPermission_whenUserHasMultiplePermissions_thenReturnTrue() {
        // Arrange
        Set<Permission> permissions = new HashSet<Permission>();
        permissions.add(Permission.ADD_PRODUCT);
        permissions.add(Permission.ADD_DISCOUNT_POLICY);
        permissions.add(Permission.CHANGE_PERMISSION);

        Role role = null;
        try {
            role = new Role(user.getUserName(), shop.getShopId(), "appointedBy", permissions);
        } catch (StockMarketException e) {
            e.printStackTrace();
            fail("Exception was thrown when it shouldn't have been: " + e.getMessage());
        }

        // Act
        @SuppressWarnings("null")
        boolean hasPermission = role.hasPermission(Permission.ADD_DISCOUNT_POLICY);

        // Assert
        assertEquals(true, hasPermission);
    }

    @Test
    public void testHasPermission_whenUserHasNoPermissions_thenReturnFalse() {
        // Arrange
        Set<Permission> permissions = new HashSet<Permission>();
        permissions.add(Permission.ADD_PRODUCT);

        Role role = null;
        try {
            role = new Role(user.getUserName(), shop.getShopId(), "appointedBy", permissions);
        } catch (StockMarketException e) {
            e.printStackTrace();
            fail("Exception was thrown when it shouldn't have been: " + e.getMessage());
        }

        // Act
        @SuppressWarnings("null")
        boolean hasPermission = role.hasPermission(Permission.ADD_DISCOUNT_POLICY);

        // Assert
        assertEquals(false, hasPermission);
    }

    @Test
    public void testHasAtLeastOnePermission_whenUserHasOnePermission_thenReturnTrue() {
        // Arrange
        Set<Permission> permissions = new HashSet<Permission>();
        permissions.add(Permission.ADD_PRODUCT);

        Role role = null;
        try {
            role = new Role(user.getUserName(), shop.getShopId(), "appointedBy", permissions);
        } catch (StockMarketException e) {
            e.printStackTrace();
            fail("Exception was thrown when it shouldn't have been: " + e.getMessage());
        }

        Set<Permission> permissionsToCheck = new HashSet<Permission>();
        permissionsToCheck.add(Permission.ADD_PRODUCT);
        permissionsToCheck.add(Permission.ADD_DISCOUNT_POLICY);

        // Act
        @SuppressWarnings("null")
        boolean hasPermission = role.hasAtLeastOnePermission(permissionsToCheck);

        // Assert
        assertEquals(true, hasPermission);
    }

    @Test
    public void testHasAtLeastOnePermission_whenUserHasMultiplePermissions_thenReturnTrue() {
        // Arrange
        Set<Permission> permissions = new HashSet<Permission>();
        permissions.add(Permission.ADD_PRODUCT);
        permissions.add(Permission.ADD_DISCOUNT_POLICY);
        permissions.add(Permission.CHANGE_PERMISSION);

        Role role = null;
        try {
            role = new Role(user.getUserName(), shop.getShopId(), "appointedBy", permissions);
        } catch (StockMarketException e) {
            e.printStackTrace();
            fail("Exception was thrown when it shouldn't have been: " + e.getMessage());
        }

        Set<Permission> permissionsToCheck = new HashSet<Permission>();
        permissionsToCheck.add(Permission.ADD_PRODUCT);
        permissionsToCheck.add(Permission.ADD_DISCOUNT_POLICY);

        // Act
        @SuppressWarnings("null")
        boolean hasPermission = role.hasAtLeastOnePermission(permissionsToCheck);

        // Assert
        assertEquals(true, hasPermission);
    }

    @Test
    public void testHasAtLeastOnePermission_whenUserHasNoPermissions_thenReturnFalse() {
        // Arrange
        Set<Permission> permissions = new HashSet<Permission>();
        permissions.add(Permission.EDIT_PRODUCT);

        Role role = null;
        try {
            role = new Role(user.getUserName(), shop.getShopId(), "appointedBy", permissions);
        } catch (StockMarketException e) {
            e.printStackTrace();
            fail("Exception was thrown when it shouldn't have been: " + e.getMessage());
        }

        Set<Permission> permissionsToCheck = new HashSet<Permission>();
        permissionsToCheck.add(Permission.ADD_PRODUCT);
        permissionsToCheck.add(Permission.ADD_DISCOUNT_POLICY);

        // Act
        @SuppressWarnings("null")
        boolean hasPermission = role.hasAtLeastOnePermission(permissionsToCheck);

        // Assert
        assertEquals(false, hasPermission);
    }

    @Test
    public void testHasAllPermissions_whenUserHasAllPermissions_thenReturnTrue() {
        // Arrange
        Set<Permission> permissions = new HashSet<Permission>();
        permissions.add(Permission.ADD_PRODUCT);
        permissions.add(Permission.ADD_DISCOUNT_POLICY);

        Role role = null;
        try {
            role = new Role(user.getUserName(), shop.getShopId(), "appointedBy", permissions);
        } catch (StockMarketException e) {
            e.printStackTrace();
            fail("Exception was thrown when it shouldn't have been: " + e.getMessage());
        }

        Set<Permission> permissionsToCheck = new HashSet<Permission>();
        permissionsToCheck.add(Permission.ADD_PRODUCT);
        permissionsToCheck.add(Permission.ADD_DISCOUNT_POLICY);

        // Act
        @SuppressWarnings("null")
        boolean hasPermission = role.hasAllPermissions(permissionsToCheck);

        // Assert
        assertEquals(true, hasPermission);
    }

    @Test
    public void testHasAllPermissions_whenUserHasMultiplePermissions_thenReturnTrue() {
        // Arrange
        Set<Permission> permissions = new HashSet<Permission>();
        permissions.add(Permission.ADD_PRODUCT);
        permissions.add(Permission.ADD_DISCOUNT_POLICY);
        permissions.add(Permission.CHANGE_PERMISSION);

        Role role = null;
        try {
            role = new Role(user.getUserName(), shop.getShopId(), "appointedBy", permissions);
        } catch (StockMarketException e) {
            e.printStackTrace();
            fail("Exception was thrown when it shouldn't have been: " + e.getMessage());
        }

        Set<Permission> permissionsToCheck = new HashSet<Permission>();
        permissionsToCheck.add(Permission.ADD_PRODUCT);
        permissionsToCheck.add(Permission.ADD_DISCOUNT_POLICY);

        // Act
        @SuppressWarnings("null")
        boolean hasPermission = role.hasAllPermissions(permissionsToCheck);

        // Assert
        assertEquals(true, hasPermission);
    }

    @Test
    public void testHasAllPermissions_whenUserHasSomePermissions_thenReturnFalse() {
        // Arrange
        Set<Permission> permissions = new HashSet<Permission>();
        permissions.add(Permission.ADD_PRODUCT);
        permissions.add(Permission.CHANGE_PERMISSION);

        Role role = null;
        try {
            role = new Role(user.getUserName(), shop.getShopId(), "appointedBy", permissions);
        } catch (StockMarketException e) {
            e.printStackTrace();
            fail("Exception was thrown when it shouldn't have been: " + e.getMessage());
        }

        Set<Permission> permissionsToCheck = new HashSet<Permission>();
        permissionsToCheck.add(Permission.ADD_PRODUCT);
        permissionsToCheck.add(Permission.ADD_DISCOUNT_POLICY);

        // Act
        @SuppressWarnings("null")
        boolean hasPermission = role.hasAllPermissions(permissionsToCheck);

        // Assert
        assertEquals(false, hasPermission);
    }

    @SuppressWarnings("null")
    @Test
    public void testModifyPermissions_whenUserIsNotAppointedBy_thenReturnException() {
        // Arrange
        Set<Permission> permissions = new HashSet<Permission>();
        permissions.add(Permission.ADD_PRODUCT);
        permissions.add(Permission.ADD_DISCOUNT_POLICY);

        Role role = null;
        try {
            role = new Role(user.getUserName(), shop.getShopId(), "appointedBy", permissions);
        } catch (StockMarketException e) {
            e.printStackTrace();
            fail("Exception was thrown when it shouldn't have been: " + e.getMessage());
        }

        User user2 = new User("user2", "password", "email", new Date());

        // Act
        try {
            role.modifyPermissions(user2.getUserName(), permissions);
            fail("Exception was not thrown when it should have been.");
        } catch (StockMarketException e) {
            // Assert
            assertEquals("Only the user that appointed this user can modify the permissions.", e.getMessage());
        }
    }

    @SuppressWarnings("null")
    @Test
    public void testModifyPermissions_whenUserIsAppointedBy_thenReturnModifiedPermissions() {
        // Arrange
        Set<Permission> permissions = new HashSet<Permission>();
        permissions.add(Permission.ADD_PRODUCT);
        permissions.add(Permission.ADD_DISCOUNT_POLICY);

        Role role = null;
        try {
            role = new Role(user.getUserName(), shop.getShopId(), "appointedBy", permissions);
        } catch (StockMarketException e) {
            e.printStackTrace();
            fail("Exception was thrown when it shouldn't have been: " + e.getMessage());
        }

        Set<Permission> newPermissions = new HashSet<Permission>();
        newPermissions.add(Permission.ADD_PRODUCT);
        newPermissions.add(Permission.ADD_DISCOUNT_POLICY);
        newPermissions.add(Permission.CHANGE_PERMISSION);

        User user2 = new User("appointedBy", "password", "email", new Date());

        // Act
        try {
            role.modifyPermissions(user2.getUserName(), newPermissions);
        } catch (StockMarketException e) {
            e.printStackTrace();
            fail("Exception was thrown when it shouldn't have been: " + e.getMessage());
        }

        // Assert
        assertEquals(newPermissions, role.getPermissions());
    }
    
    @SuppressWarnings("null")
    @Test
    public void testAddAppointment_whenUserIsTryingToAppointHimself_thenReturnException() {
        // Arrange
        Set<Permission> permissions = new HashSet<Permission>();
        permissions.add(Permission.ADD_PRODUCT);
        permissions.add(Permission.ADD_DISCOUNT_POLICY);

        Role role = null;
        try {
            role = new Role(user.getUserName(), shop.getShopId(), "appointedBy", permissions);
        } catch (StockMarketException e) {
            e.printStackTrace();
            fail("Exception was thrown when it shouldn't have been: " + e.getMessage());
        }

        // Act
        try {
            role.addAppointment(user.getUserName());
            fail("Exception was not thrown when it should have been.");
        } catch (StockMarketException e) {
            // Assert
            assertEquals("Username user cannot appoint itself.", e.getMessage());
        }
    }

    @SuppressWarnings("null")
    @Test
    public void testAddAppointment_whenUserIsAppointedBy_thenReturnNewRole() {
        // Arrange
        Set<Permission> permissions = new HashSet<Permission>();
        permissions.add(Permission.ADD_PRODUCT);
        permissions.add(Permission.ADD_DISCOUNT_POLICY);

        Role role = null;
        try {
            role = new Role(user.getUserName(), shop.getShopId(), "appointedBy", permissions);
        } catch (StockMarketException e) {
            e.printStackTrace();
            fail("Exception was thrown when it shouldn't have been: " + e.getMessage());
        }

        User user2 = new User("user2", "password", "email", new Date());

        // Act
        try {
            role.addAppointment(user2.getUserName());
            // Assert - not eception throw
        } catch (StockMarketException e) {
            e.printStackTrace();
            fail("Exception was thrown when it shouldn't have been: " + e.getMessage());
        }
    }

    @SuppressWarnings("null")
    @Test
    public void testDeleteAppointment_whenUserIsNotAppointedBy_thenReturnException() {
        // Arrange
        Set<Permission> permissions = new HashSet<Permission>();
        permissions.add(Permission.ADD_PRODUCT);
        permissions.add(Permission.ADD_DISCOUNT_POLICY);

        Role role = null;
        try {
            role = new Role(user.getUserName(), shop.getShopId(), "appointedBy", permissions);
        } catch (StockMarketException e) {
            e.printStackTrace();
            fail("Exception was thrown when it shouldn't have been: " + e.getMessage());
        }

        User user2 = new User("user2", "password", "email", new Date());

        // Act
        try {
            role.deleteAppointment(user2.getUserName());
            fail("Exception was not thrown when it should have been.");
        } catch (StockMarketException e) {
            // Assert
            assertEquals("Username user2 is not appointed.", e.getMessage());
        }
    }

    @SuppressWarnings("null")
    @Test
    public void testDeleteAppointment_whenUserIsAppointedBy_thenReturnNewRole() {
        // Arrange
        Set<Permission> permissions = new HashSet<Permission>();
        permissions.add(Permission.ADD_PRODUCT);
        permissions.add(Permission.ADD_DISCOUNT_POLICY);

        Role role = null;
        try {
            role = new Role(user.getUserName(), shop.getShopId(), "appointedBy", permissions);
        } catch (StockMarketException e) {
            e.printStackTrace();
            fail("Exception was thrown when it shouldn't have been: " + e.getMessage());
        }

        User user2 = new User("user2", "password", "email", new Date());
        try {
            role.addAppointment(user2.getUserName());
        } catch (StockMarketException e) {
            e.printStackTrace();
            fail("Exception was thrown when it shouldn't have been: " + e.getMessage());
        }

        // Act
        try {
            role.deleteAppointment(user2.getUserName());
            // Assert - not exception thrown
        } catch (StockMarketException e) {
            e.printStackTrace();
            fail("Exception was thrown when it shouldn't have been: " + e.getMessage());
        }
    }
}
