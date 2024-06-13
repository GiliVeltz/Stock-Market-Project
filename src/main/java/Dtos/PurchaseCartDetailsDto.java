package Dtos;

import java.util.List;

public class PurchaseCartDetailsDto {
    public List<Integer> basketsToBuy;
    public String cardNumber;
    public String address;

    public PurchaseCartDetailsDto(List<Integer> basketsToBuy, String cardNumber, String address) {
        this.basketsToBuy = basketsToBuy;
        this.cardNumber = cardNumber;
        this.address = address;
    }

    public List<Integer> getBasketsToBuy() {
        return basketsToBuy;
    }
}
