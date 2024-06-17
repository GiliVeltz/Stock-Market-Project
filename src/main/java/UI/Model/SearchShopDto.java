package UI.Model;

import javax.validation.constraints.NotBlank;

public class SearchShopDto {
    private String shopName;
    private Integer shopId;

    // Constructors, getters, and setters
    public SearchShopDto() {
    }

    public SearchShopDto(String shopName, Integer shopId) {
        this.shopName = shopName;
        this.shopId = shopId;
    }

    // Getters and setters
    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }
}
