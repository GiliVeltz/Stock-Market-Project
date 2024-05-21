package Domain.ExternalServices.PaymentService;

import Domain.ExternalServices.ExternalService;

public class AdapterPayment implements ExternalService {
    
    public AdapterPayment() {
    
    }

    @Override
    public boolean ConnectToService() {
        // Connect to the payment service
        return true;
    }
    
}
