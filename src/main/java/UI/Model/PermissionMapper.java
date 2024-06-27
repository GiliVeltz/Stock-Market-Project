package UI.Model;
import java.util.HashMap;
import java.util.Map;

public class PermissionMapper {
    private static final Map<Permission, String> permissionNames = new HashMap<>();

    static {
        permissionNames.put(Permission.FOUNDER, "Founder");
        permissionNames.put(Permission.OWNER, "Owner");
        permissionNames.put(Permission.ADD_PRODUCT, "Add Product");
        permissionNames.put(Permission.DELETE_PRODUCT, "Delete Product");
        permissionNames.put(Permission.PUBLISH_PRODUCT_FOR_SALE, "Publish Product for Sale");
        permissionNames.put(Permission.REMOVE_PRODUCT_FROM_SALE, "Remove Product from Sale");
        permissionNames.put(Permission.EDIT_PRODUCT, "Edit Product");
        permissionNames.put(Permission.CHANGE_PURCHASE_POLICY, "Change Purchase Policy");
        permissionNames.put(Permission.CHANGE_DISCOUNT_POLICY, "Change Discount Policy");
        permissionNames.put(Permission.ADD_PURCHASE_METHOD, "Add Purchase Method");
        permissionNames.put(Permission.REMOVE_PURCHASE_METHOD, "Remove Purchase Method");
        permissionNames.put(Permission.ADD_DISCOUNT_POLICY, "Add Discount Policy");
        permissionNames.put(Permission.REMOVE_DISCOUNT_METHOD, "Remove Discount Method");
        permissionNames.put(Permission.CHANGE_PRODUCT_POLICY, "Change Product Policy");
        permissionNames.put(Permission.CHANGE_SHOP_POLICY, "Change Shop Policy");
        permissionNames.put(Permission.APPOINT_MANAGER, "Appoint Manager");
        permissionNames.put(Permission.REMOVE_OWNER, "Remove Owner");
        permissionNames.put(Permission.REMOVE_MANAGER, "Remove Manager");
        permissionNames.put(Permission.CHANGE_PERMISSION, "Change Permission");
        permissionNames.put(Permission.GET_ROLES_INFO, "Get Roles Info");
        permissionNames.put(Permission.GET_CLIENT_REQUEST, "Get Client Request");
        permissionNames.put(Permission.RESPONSE_TO_CLIENT_REQUEST, "Response to Client Request");
        permissionNames.put(Permission.GET_PURCHASE_HISTORY, "Get Purchase History");
    }

    public static String getPermissionName(Permission permission) {
        return permissionNames.get(permission);
    }
}