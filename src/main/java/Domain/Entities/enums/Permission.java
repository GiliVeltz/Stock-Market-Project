package Domain.Entities.enums;

import jakarta.persistence.Entity;

/**
 * This enum represents the permissions that a user with a role in a specific shop has. 
 */
//TODO: VLADI: remove numbers when done.
public enum Permission {

    //CAN DO ALL
    FOUNDER,

    //FOUNDER ACTIONS //TODO: Maybe not needed and we can use just FOUNDER.
    // CLOSE_SHOP, //9
    // OPEN_CLOSED_SHOP, //10

    //CAN DO ALL EXCEPT FOUNDER STAFF (OWNER PERMISSION)
    OWNER,

    //PRODUCTS MANAGEMENT
    ADD_PRODUCT, //1
    DELETE_PRODUCT, //1
    PUBLISH_PRODUCT_FOR_SALE, //1
    REMOVE_PRODUCT_FROM_SALE, //1
    EDIT_PRODUCT, //1

    //POLICY MANAGEMENT (NOT FOR THIS VERSION)
    // TODO: MAYBE ADD MORE LATER
    CHANGE_PURCHASE_POLICY, //2
    CHANGE_DISCOUNT_POLICY, //2
    ADD_PURCHASE_METHOD, //2
    REMOVE_PURCHASE_METHOD, //2
    ADD_DISCOUNT_POLICY, //2
    REMOVE_DISCOUNT_METHOD, //2
    CHANGE_PRODUCT_POLICY,
    CHANGE_SHOP_POLICY,

    //APPOINT MANAGEMENT
    //APPOINT_OWNER, //3 We don't want managers to have this permission.
    APPOINT_MANAGER, //6
    REMOVE_OWNER, //4
    REMOVE_MANAGER, //8

    //PERMISSION MANAGEMENT //7
    CHANGE_PERMISSION, //7

    //GENERAL
    GET_ROLES_INFO, //11
    GET_CLIENT_REQUEST, //12
    RESPONSE_TO_CLIENT_REQUEST, //12
    GET_PURCHASE_HISTORY, //13
}
