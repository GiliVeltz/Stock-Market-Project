package Domain.ExternalServices.SupplyService;
import java.util.logging.Logger;

import Domain.ExternalServices.ExternalService;

public class AdapterSupply implements ExternalService{

    private static final Logger logger = Logger.getLogger(AdapterSupply.class.getName());
    public AdapterSupply() {
    
    }

    @Override
    public boolean ConnectToService() {
        // Connect to the supply service
         //TODO : forward the request to the Supply service
         logger.info("Connecting to the Supply service");

        return true;
    }
    
}
