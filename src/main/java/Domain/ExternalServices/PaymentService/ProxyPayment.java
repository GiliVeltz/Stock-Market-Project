package Domain.ExternalServices.PaymentService;

import java.util.Map;

public class ProxyPayment {

    public ProxyPayment() {
        // TODO: TAL: add constructor detalils
    }
    
    public boolean checkIfPaymentOk(String cardNumber) {
        return true;
    }

    public boolean pay(String cardNumber, double amountToPay) {
        // TODO Auto-generated method stub
        return true;
    }

    public boolean payShops(Map<Double, String> priceToShopDetails) {
        // TODO Auto-generated method stub
        return true;
    }

    public boolean refound(String cardNumber, double amountToRefound) {
        // TODO Auto-generated method stub
        return true;
    }
    
}
