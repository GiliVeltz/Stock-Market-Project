package Domain.ExternalServices.PaymentService;

public class ProxyPayment {

    public ProxyPayment() {
        // TODO: TAL: add constructor detalils
    }
    
    public boolean checkIfPaymentOk(String cardNumber, String shopBankDetails, double amountToPay) {
        return true;
    }

    public void pay(String cardNumber, String shopBankDetails, double amountToPay) {
        return;
    }

    public void refound(String cardNumber) {
        return;
    }
    
}
