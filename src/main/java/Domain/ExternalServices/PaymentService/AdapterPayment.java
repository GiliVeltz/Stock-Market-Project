package Domain.ExternalServices.PaymentService;
import java.util.logging.Logger;
import Domain.ExternalServices.ExternalService;

// TODO: Implement the Adapter pattern
public class AdapterPayment implements ExternalService {

    private static final Logger logger = Logger.getLogger(AdapterPayment.class.getName());

    public AdapterPayment() {

    
    }

    @Override
    public boolean ConnectToService() {
        // Connect to the payment service
        //TODO : forward the request to the Payment service
        logger.info("Connecting to the payment service");
        return true;
    }
    
}
