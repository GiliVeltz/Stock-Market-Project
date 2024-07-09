package UI.Model;

import java.util.List;

public class PurchaseCartDetailsDto {
    public PaymentInfoDto paymentInfo;
    public SupplyInfoDto supplyInfo;
    public List<Integer> basketsToBuy;

    public PurchaseCartDetailsDto(PaymentInfoDto paymentInfo, SupplyInfoDto supplyInfo, List<Integer> basketsToBuy) {
        this.paymentInfo = paymentInfo;
        this.supplyInfo = supplyInfo;
        this.basketsToBuy = basketsToBuy;
    }

    public List<Integer> getBasketsToBuy() {
        return basketsToBuy;
    }

    public void setBasketsToBuy(List<Integer> basketsToBuy) {
        this.basketsToBuy = basketsToBuy;
    }

    public PaymentInfoDto getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(PaymentInfoDto paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    public SupplyInfoDto getSupplyInfo() {
        return supplyInfo;
    }

    public void setSupplyInfo(SupplyInfoDto supplyInfo) {
        this.supplyInfo = supplyInfo;
    }
}
