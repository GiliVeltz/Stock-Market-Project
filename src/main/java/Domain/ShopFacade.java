package Domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Exceptions.PermissionException;
import Exceptions.ShopException;
import org.springframework.web.bind.annotation.RestController;

import Domain.Discounts.Discount;
import Domain.Discounts.PrecentageDiscount;

@RestController
public class ShopFacade {
    private static ShopFacade _shopFacade;
    private List<Shop> _shopsList;

    private ShopFacade() {
        this._shopsList = new ArrayList<>();
    }

    // Public method to provide access to the _shopFacade
    public static synchronized ShopFacade getShopFacade() {
        if (_shopFacade == null) {
            _shopFacade = new ShopFacade();
        }
        return _shopFacade;
    }

    public Shop getShopByShopId(Integer shopId) {
        for (Shop shop : this._shopsList) {
            if (shop.getShopId().equals(shopId)) {
                return shop;
            }
        }
        return null;
    }

    /**
     * Checks if a shop ID exists.
     * 
     * @param shopId The ID of the shop to check.
     * @return True if the shop ID exists, false otherwise.
     */
    public Boolean isShopIdExist(Integer shopId) {
        for (Shop shop : this._shopsList) {
            if (shop.getShopId().equals(shopId)) {
                return true;
            }
        }
        return false;
    }

    public void openNewShop(Integer shopId, String userName, String bankDetails, String shopAddress) throws Exception {
        if (isShopIdExist(shopId))
            throw new Exception(String.format("Shop ID: %d is already exist.", shopId));
        else
            _shopsList.add(new Shop(shopId, userName, bankDetails, shopAddress));
    }

    // close shop only if the user is the founder of the shop
    public void closeShop(Integer shopId, String userName) throws Exception {
        try {
            if (!isShopIdExist(shopId))
                throw new Exception(String.format("Shop ID: %d does not exist.", shopId));
            else {
                Shop shopToClose = getShopByShopId(shopId);
                if (shopToClose.checkPermission(userName, Permission.FOUNDER)) {
                    getShopByShopId(shopId).notifyRemoveShop();
                    _shopsList.remove(shopToClose);
                } else {
                    throw new Exception(String.format(
                            "User %s can't cloase the Shop: %d. Only the fonder has the permission", userName, shopId));
                }
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    public void addProductToShop(Integer shopId, Product product, String userName) throws Exception {
        if (!isShopIdExist(shopId))
            throw new Exception(String.format("Shop ID: %d does not exist.", shopId));
        else
            getShopByShopId(shopId).addProductToShop(userName, product);
    }

    /**
     * Retrieves the purchase history for a shop by its ID.
     *
     * @param shopId The ID of the shop.
     * @return A list of ShopOrder objects representing the shop's purchase history.
     */
    public List<ShopOrder> getPurchaseHistory(Integer shopId) {
        List<ShopOrder> purchaseHistory = new ArrayList<>();
        Shop shop = getShopByShopId(shopId);
        if (shop != null) {
            purchaseHistory = shop.getPurchaseHistory();
        }
        return purchaseHistory;
    }

    /**
     * Checks if a user is the owner of a shop.
     *
     * @param shopId The ID of the shop.
     * @param userId The ID of the user.
     * @return A boolean indicating whether the user is the owner of the shop.
     * 
     */
    public Boolean isShopOwner(Integer shopId, String userId) throws ShopException {
        Shop shop = getShopByShopId(shopId);
        if (shop != null) {
            return shop.isOwnerOrFounderOwner(userId);
        }
        return false;
    }

    /**
     * Adds a basic discount to the shop.
     *
     * @param shopId         the ID of the shop
     * @param username       the username of the user adding the discount
     * @param isPercentage   a flag indicating whether the discount amount is a
     *                       percentage or a fixed value
     * @param discountAmount the amount of the discount
     * @param expirationDate the expiration date of the discount
     * @throws PermissionException if the user does not have permission to add a
     *                             discount to the shop
     * @throws ShopException       if there is an error adding the discount to the
     *                             shop
     */
    public void addBasicDiscountToShop(int shopId, String username, boolean isPrecentage, double discountAmount,
            Date expirationDate) throws PermissionException, ShopException {

        Shop shop = getShopByShopId(shopId);
        if (!shop.checkPermission(username, Permission.ADD_DISCOUNT_POLICY))
            throw new PermissionException("User " + username + " has no permission to add discount to shop " + shopId);
        Discount discount;
        if (isPrecentage)
            discount = new PrecentageDiscount(expirationDate, discountAmount, shopId);
        else
            discount = new PrecentageDiscount(expirationDate, discountAmount, shopId);
        shop.addDiscount(discount);
    }
}
