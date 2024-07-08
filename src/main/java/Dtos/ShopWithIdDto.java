package Dtos;

import Domain.Entities.Shop;

public class ShopWithIdDto {
    public int shopId;
    public String shopName;
    public String bankDetails;
    public String shopAddress;

    public ShopWithIdDto(int shopId, String shopName, String bankDetails, String shopAddress) {
        this.shopId = shopId;
        this.shopName = shopName;
        this.bankDetails = bankDetails;
        this.shopAddress = shopAddress;
    }

    public ShopWithIdDto (Shop shop) {
        this.shopId = shop.getShopId();
        this.shopName = shop.getShopName();
        this.bankDetails = shop.getBankDetails();
        this.shopAddress = shop.getShopAddress();
    }
}
