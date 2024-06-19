package UI.Model;

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

    public void setBasketsToBuy(List<Integer> basketsToBuy) {
        this.basketsToBuy = basketsToBuy;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
