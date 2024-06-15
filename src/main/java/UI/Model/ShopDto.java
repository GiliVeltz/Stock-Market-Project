package UI.Model;

import javax.validation.constraints.NotBlank;

public class ShopDto {
    @NotBlank(message = "Shop name is required")
    private String shopName;

    @NotBlank(message = "Bank details is required")
    private String bankDetails;

    @NotBlank(message = "Shop address is required")
    private String shopAddress;
    

    // Constructors, getters, and setters

    public ShopDto() {
    }

    public ShopDto(String shopName, String bankDetails, String shopAddress) {
        this.shopName = shopName;
        this.bankDetails = bankDetails;
        this.shopAddress = shopAddress;
    }

    // Getters and setters

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
}
