package AcceptanceTests.ProjectTests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Disabled;

import AcceptanceTests.Implementor.BridgeInterface;
import AcceptanceTests.Implementor.RealBridge;

@ExtendWith(RealBridge.class)

public class ShopOwnerAcceptanceTests {

    // Fields.
    private BridgeInterface _bridge;

    // constructor.
    public ShopOwnerAcceptanceTests(RealBridge bridge) {
        _bridge = bridge;
    }
    
    // Test that shop owner can add products to the shop.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testShopOwnerAddProductToShop() {
        assertTrue(_bridge.testShopOwnerAddProductToShop("shopOwnerUserName", "shopId1", "ProductName", "ProductAmount") ); // success
        assertFalse(_bridge.testShopOwnerAddProductToShop("shopOwnerUserName", "shopId1", "ProductName", "inValidAmount") ); // fail - invalid amount
        assertFalse(_bridge.testShopOwnerAddProductToShop("shopOwnerUserName", "shopId1", "ExistProductName", "ProductAmount") ); // fail - already exist product name
        assertFalse(_bridge.testShopOwnerAddProductToShop("shopOwnerUserName", "shopId2", "ExistProductName", "ProductAmount") ); // fail - the user is not the shop onwer
    }
    
    // Test that shop owner can remove products from the shop.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testShopOwnerRemoveProductFromShop() {
        assertTrue(_bridge.testShopOwnerRemoveProductFromShop("shopOwnerUserName", "shopId1", "ProductName") ); // success
        assertFalse(_bridge.testShopOwnerRemoveProductFromShop("shopOwnerUserName", "shopId1", "ProductName") ); // fail - invalid productname
        assertFalse(_bridge.testShopOwnerRemoveProductFromShop("shopOwnerUserName", "shopId2", "ExistProductName") ); // fail - the user is not the shop onwer
    }
    
    // Test that shop owner can edit products details in the shop.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testShopOwnerEditProductInShop() {
        assertTrue(_bridge.testShopOwnerEditProductInShop("shopOwnerUserName", "shopId1", "ProductName", "newProductName", "ProductAmount", "newProductAmount") ); // success
        assertFalse(_bridge.testShopOwnerEditProductInShop("shopOwnerUserName", "shopId1", "ProductName", "newProductName", "inValidAmount", "newProductAmount") ); // fail - invalid amount
        assertFalse(_bridge.testShopOwnerEditProductInShop("shopOwnerUserName", "shopId1", "ExistProductName", "newProductName", "ProductAmount", "newProductAmount") ); // fail - already exist product name
        assertFalse(_bridge.testShopOwnerEditProductInShop("shopOwnerUserName", "shopId1", "NonProductName", "newProductName", "ProductAmount", "newProductAmount") ); // fail - there is no product with such name in the store
        assertFalse(_bridge.testShopOwnerEditProductInShop("shopOwnerUserName", "shopId2", "ExistProductName", "newProductName", "ProductAmount", "newProductAmount") ); // fail - the user is not the shop onwer
    }
    
    // Test that shop owner can edit the shop policies.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testShopOwnerChangeShopPolicies() {
        assertTrue(_bridge.testShopOwnerChangeShopPolicies("shopOwnerUserName", "shopId1", "new shop policy") ); // success
        assertFalse(_bridge.testShopOwnerChangeShopPolicies("shopOwnerUserName", "shopId1", "new shop policy") ); // fail - invalid new shop policy
        assertFalse(_bridge.testShopOwnerChangeShopPolicies("shopOwnerUserName", "shopId2", "new shop policy") ); // fail - the user is not the shop onwer
    }
    
    // Test that shop owner can add another shop owner to his shop.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testShopOwnerAppointAnotherShopOwner() {
        assertTrue(_bridge.testShopOwnerAppointAnotherShopOwner("shopOwnerUserName", "shopId1", "newOwner") ); // success
        assertFalse(_bridge.testShopOwnerAppointAnotherShopOwner("shopOwnerUserName", "shopId1", "newOwnerInvalidUserName") ); // fail - invalid new owner name
        assertFalse(_bridge.testShopOwnerAppointAnotherShopOwner("shopOwnerUserName", "shopId2", "existOwner") ); // fail - the new user is already a shop onwer
    }
    
    // Test that shop owner can add another shop manager to his shop.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testShopOwnerAppointAnotherShopManager() {
        assertTrue(_bridge.testShopOwnerAppointAnotherShopManager("shopOwnerUserName", "shopId1", "newManager") ); // success
        assertFalse(_bridge.testShopOwnerAppointAnotherShopManager("shopOwnerUserName", "shopId1", "newManagerInvalidUserName") ); // fail - invalid new manager name
        assertFalse(_bridge.testShopOwnerAppointAnotherShopManager("shopOwnerUserName", "shopId2", "existManager") ); // fail - the new user is already a shop manager
    }

    // Test that the shop owner can add a permission of a shop manager.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testShopOwnerAddShopManagerPermission(){
        assertTrue(_bridge.testShopOwnerAddShopManagerPermission("userName", "shopId", "managerUserName", "newPermission")); // success
        assertFalse(_bridge.testShopOwnerAddShopManagerPermission("userName", "shopId", "managerUserName", "existPermission")); // fail - alreadey exist permission
        assertFalse(_bridge.testShopOwnerAddShopManagerPermission("userName", "shopId", "managerUserName", "invalidPermission")); // fail - invalid permission
    }
    
    // Test that the shop owner can remove a permission of a shop manager.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testShopOwnerRemoveShopManagerPermission(){
        assertTrue(_bridge.testShopOwnerRemoveShopManagerPermission("userName", "shopId", "managerUserName", "existPermission")); // success
        assertFalse(_bridge.testShopOwnerRemoveShopManagerPermission("userName", "shopId", "managerUserName", "nonexistPermission")); // fail - non exist permission in shop manager
        assertFalse(_bridge.testShopOwnerRemoveShopManagerPermission("userName", "shopId", "managerUserName", "invalidPermission")); // fail - invalid permission
    }
    
    // Test that the shop owner can close his shop in the system.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testShopOwnerCloseShop(){
        assertTrue(_bridge.testShopOwnerCloseShop("userName", "shopId1")); // success
        assertFalse(_bridge.testShopOwnerCloseShop("userName", "shopId2")); // fail - exist shop but he is not the owner
        assertFalse(_bridge.testShopOwnerCloseShop("userName", "shopId3")); // fail - non exist shop id
    }
    
    // Test that the shop owner can get the information about the shop.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testShopOwnerGetShopInfo(){
        assertTrue(_bridge.testShopOwnerGetShopInfo("userName", "shopId1")); // success
        assertFalse(_bridge.testShopOwnerGetShopInfo("userName", "shopId2")); // fail - exist shop but he is not the owner
        assertFalse(_bridge.testShopOwnerGetShopInfo("userName", "shopId3")); // fail - non exist shop id
    }
    
    // Test that the shop owner can get the permissions of the shop managers.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testShopOwnerGetShopManagersPermissions(){
        assertTrue(_bridge.testShopOwnerGetShopManagersPermissions("userName", "shopId1")); // success
        assertFalse(_bridge.testShopOwnerGetShopManagersPermissions("userName", "shopId2")); // fail - exist shop but he is not the owner
        assertFalse(_bridge.testShopOwnerGetShopManagersPermissions("userName", "shopId3")); // fail - non exist shop id
    }
    
    // Test that the shop owner can see the history purchases of the shop.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testShopOwnerViewHistoryPurcaseInShop() {
        assertTrue(_bridge.testShopOwnerViewHistoryPurcaseInShop("manager", "shopId1") ); // success
        assertFalse(_bridge.testShopOwnerViewHistoryPurcaseInShop("userName", "shopId2")); // fail - exist shop but he is not the owner
        assertFalse(_bridge.testShopOwnerViewHistoryPurcaseInShop("userName", "shopId3")); // fail - non exist shop id
    }
}
