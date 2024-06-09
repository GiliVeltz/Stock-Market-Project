package Dtos;

// TODO: change the DTO to match the real Shop constructor
public class ShopDto {
    public String bankDetails;
    public String shopAddress;

    public ShopDto(String bankDetails, String shopAddress) {
        this.bankDetails = bankDetails;
        this.shopAddress = shopAddress;
    }
}
