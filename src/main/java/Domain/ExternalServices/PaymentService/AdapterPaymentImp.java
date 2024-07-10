package Domain.ExternalServices.PaymentService;

import java.util.logging.Logger;

import Domain.ExternalServices.ExternalService;
import Dtos.PaymentInfoDto;

public class AdapterPaymentImp extends ExternalService implements AdapterPaymentInterface{

    // private fields
    private static AdapterPaymentImp _adapterPayment;
    private ProxyPayment _paymentService;
    private static final Logger logger = Logger.getLogger(AdapterPaymentImp.class.getName());

    public AdapterPaymentImp(int id, String newSerivceName, String informationPersonName, String informationPersonPhone) {
        super(id, newSerivceName, informationPersonName, informationPersonPhone); // Explicitly invoke the constructor of the superclass
        _adapterPayment = this;
        _paymentService = new ProxyPayment();
    }

    public static AdapterPaymentImp getAdapterPayment() {
        if (_adapterPayment == null)
            _adapterPayment = new AdapterPaymentImp(-1, "PaymentService", "Tal", "123456789");
        return _adapterPayment;
    }

    @Override
    public boolean ConnectToService() {
        // Connect to the payment service
        logger.info("Connecting to the payment service");
        return true;
    }

    @Override
    public boolean handshake() {
        logger.info("Starting handshake with the payment service");
        return _paymentService.handshake();
    }

    @Override
    public int payment(PaymentInfoDto paymentInfo, double price) {
        logger.info("Paying for the cart");
        return _paymentService.payment(paymentInfo, price);
    }

    @Override
    public int cancel_pay(int transaction_id) {
        logger.info("Canceling the payment");
        return _paymentService.cancel_pay(transaction_id);
    }
}
