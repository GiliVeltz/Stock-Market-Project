package Dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import Domain.Shop;
@JsonInclude
public class ShopDto {
    public String shopName;
    public Integer shopId;
    public String bankDetails;
    public String shopAddress;
    public Double shopRating;
    public Integer shopRatersCounter;
    public boolean isShopClosed;

    public ShopDto(String shopName, String bankDetails, String shopAddress) {
        this.shopName = shopName;
        this.bankDetails = bankDetails;
        this.shopAddress = shopAddress;
    }

    public ShopDto (Shop shop) {
        this.shopId = shop.getShopId();
        this.shopName = shop.getShopName();
        this.bankDetails = shop.getBankDetails();
        this.shopAddress = shop.getShopAddress();
        this.shopRating = shop.getShopRating();
        this.shopRatersCounter = shop.getShopRatersCounter();
        this.isShopClosed = shop.isShopClosed();
    }

}
