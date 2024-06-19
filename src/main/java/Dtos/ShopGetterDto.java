package Dtos;

import java.util.Map;

import Domain.Shop;
import Domain.Discounts.Discount;
import Domain.Policies.ShopPolicy;

public class ShopGetterDto {
    public int shopId;
    public String shopName;
    private String _shopFounder;
    private Map<Integer, DiscountDto> _discounts;
    public String bankDetails;
    public String shopAddress;
    private Double _shopRating;
    private Integer _shopRatersCounter;
    private ShopPolicyDto _shopPolicy;
    private boolean _isClosed;

    public ShopGetterDto(int shopId, String shopName, String _shopFounder, String bankDetails,
     Map<Integer, DiscountDto> _discounts, String shopAddress, Double _shopRating, 
     Integer _shopRatersCounter, ShopPolicyDto _shopPolicy, boolean _isClosed) {
        this.shopId = shopId;
        this.shopName = shopName;
        this._shopFounder = _shopFounder;
        this._discounts = _discounts;
        this.bankDetails = bankDetails;
        this.shopAddress = shopAddress;
        this._shopRating = _shopRating;
        this._shopRatersCounter = _shopRatersCounter;
        this._shopPolicy = _shopPolicy;
        this._isClosed = _isClosed;
    }

    public ShopGetterDto (Shop shop) {
        this.shopId = shop.getShopId();
        this.shopName = shop.getShopName();
        this._shopFounder = shop.getFounderName();
        this._discounts = shop.getDiscountDtos();
        this.bankDetails = shop.getBankDetails();
        this.shopAddress = shop.getShopAddress();
        this._shopRating = shop.getShopRating();
        this._shopRatersCounter = shop.getShopRatersCounter();
        this._shopPolicy = new ShopPolicyDto(shop.getShopPolicy());
        this._isClosed = shop.isShopClosed();
    }
}
