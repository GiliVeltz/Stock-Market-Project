package Domain.ExternalServices.PaymentService;
import java.lang.reflect.Proxy;
import java.util.logging.Logger;
import Domain.ExternalServices.ExternalService;

// TODO: Implement the Adapter pattern
public class AdapterPayment implements ExternalService {

    private ProxyPayment _paymentService;
    private static final Logger logger = Logger.getLogger(AdapterPayment.class.getName());

    public AdapterPayment() {
        _paymentService = new ProxyPayment();
    }

    @Override
    public boolean ConnectToService() {
        // Connect to the payment service
        //TODO : forward the request to the Payment service
        logger.info("Connecting to the payment service");
        return true;
    }

    public void pay(String cardNumber) {
        logger.info("Paying for the cart");
        _paymentService.pay(cardNumber);
    }
    
}
