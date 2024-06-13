package Domain.ExternalServices.PaymentService;

import java.util.Map;
import java.util.logging.Logger;

import Domain.ExternalServices.ExternalService;
import Exceptions.PaymentFailedException;

// TODO: Implement the Adapter pattern
public class AdapterPayment extends ExternalService {

    // private fields
    private static AdapterPayment _adapterPayment;
    private ProxyPayment _paymentService;
    private static final Logger logger = Logger.getLogger(AdapterPayment.class.getName());

    public AdapterPayment(int id, String newSerivceName, String informationPersonName, String informationPersonPhone) {
        super(id, newSerivceName, informationPersonName, informationPersonPhone); // Explicitly invoke the constructor of the superclass
        _adapterPayment = this;
        _paymentService = new ProxyPayment();
    }

    public static AdapterPayment getAdapterPayment() {
        if (_adapterPayment == null)
            _adapterPayment = new AdapterPayment(-1, "PaymentService", "Tal", "123456789");
        return _adapterPayment;
    }

    @Override
    public boolean ConnectToService() {
        // Connect to the payment service
        // TODO : forward the request to the Payment service
        logger.info("Connecting to the payment service");
        return true;
    }

    public void checkIfPaymentOk(String cardNumber)
            throws PaymentFailedException {
        logger.info("Checking if the card is valid");
        if (!_paymentService.checkIfPaymentOk(cardNumber))
            throw new PaymentFailedException("Payment failed");
    }

    public void pay(String cardNumber, Map<Double, String> priceToShopDetails, double overallPrice) throws PaymentFailedException{
        logger.info("Paying for the cart");
        if (!_paymentService.pay(cardNumber, overallPrice) || !_paymentService.payShops(priceToShopDetails))
            throw new PaymentFailedException("Payment failed");
    }

    public void refound(String cardNumber, double amountToRefound) throws PaymentFailedException{
        logger.info("Refounding the cart");
        if (!_paymentService.refound(cardNumber, amountToRefound))
            throw new PaymentFailedException("Refound failed");
    }
}
