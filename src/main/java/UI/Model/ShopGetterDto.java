package UI.Model;

import java.util.Map;

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

    // Getters and setters

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getBankDetails() {
        return bankDetails;
    }

    public void setBankDetails(String bankDetails) {
        this.bankDetails = bankDetails;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public Double getShopRating() {
        return _shopRating;
    }

    public void setShopRating(Double _shopRating) {
        this._shopRating = _shopRating;
    }

    public Integer getShopRatersCounter() {
        return _shopRatersCounter;
    }

    public void setShopRatersCounter(Integer _shopRatersCounter) {
        this._shopRatersCounter = _shopRatersCounter;
    }

    public ShopPolicyDto getShopPolicy() {
        return _shopPolicy;
    }

    public void setShopPolicy(ShopPolicyDto _shopPolicy) {
        this._shopPolicy = _shopPolicy;
    }

    public boolean isClosed() {
        return _isClosed;
    }

    public void setClosed(boolean _isClosed) {
        this._isClosed = _isClosed;
    }

    public String getShopFounder() {
        return _shopFounder;
    }

    public void setShopFounder(String _shopFounder) {
        this._shopFounder = _shopFounder;
    }

    public Map<Integer, DiscountDto> getDiscounts() {
        return _discounts;
    }

    public void setDiscounts(Map<Integer, DiscountDto> _discounts) {
        this._discounts = _discounts;
    }
}