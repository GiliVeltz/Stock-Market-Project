package Domain.ExternalServices.PaymentService;

import java.lang.reflect.Proxy;
import java.util.logging.Logger;

import Domain.ExternalServices.ExternalService;
import Exceptions.PaymentFailedException;

// TODO: Implement the Adapter pattern
public class AdapterPayment implements ExternalService {

    // private fields
    private int _id;
    private String _serviceName;
    private String _informationPersonName;
    private String _informationPersonPhone;
    private static AdapterPayment _adapterPayment;
    private ProxyPayment _paymentService;
    private static final Logger logger = Logger.getLogger(AdapterPayment.class.getName());

    private AdapterPayment() {
        _paymentService = new ProxyPayment();
    }

    private AdapterPayment(int id, String newSerivceName, String informationPersonName, String informationPersonPhone) {
        _id = id;
        _serviceName = newSerivceName;
        _informationPersonName = informationPersonName;
        _informationPersonPhone = informationPersonPhone;
        _adapterPayment = this;
        _paymentService = new ProxyPayment();
    }

    public static AdapterPayment getAdapterPayment() {
        if(_adapterPayment == null)
            _adapterPayment = new AdapterPayment();
        return _adapterPayment;
    }

    @Override
    public boolean ConnectToService() {
        // Connect to the payment service
        // TODO : forward the request to the Payment service
        logger.info("Connecting to the payment service");
        return true;
    }

    public void checkIfPaymentOk(String cardNumber, String shopBankDetails, double amountToPay)
            throws PaymentFailedException {
        logger.info("Checking if the card is valid");
        if (!_paymentService.checkIfPaymentOk(cardNumber, shopBankDetails, amountToPay))
            throw new PaymentFailedException("Payment failed");
    }

    public void pay(String cardNumber, String shopBankDetails, double amountToPay) {
        logger.info("Paying for the cart");
        _paymentService.pay(cardNumber, shopBankDetails, amountToPay);
    }

    public void refound(String cardNumber) {
        logger.info("Refounding the cart");
        _paymentService.refound(cardNumber);
    }

    // getters and seeters

    public String getServiceName() {
        return _serviceName;
    }

    public void setServiceName(String serviceName) {
        this._serviceName = serviceName;
    }

    public String getInformationPersonName() {
        return _informationPersonName;
    }

    public void setInformationPersonName(String informationPersonName) {
        this._informationPersonName = informationPersonName;
    }

    public String getInformationPersonPhone() {
        return _informationPersonPhone;
    }

    public void setInformationPersonPhone(String informationPersonPhone) {
        this._informationPersonPhone = informationPersonPhone;
    }
}
