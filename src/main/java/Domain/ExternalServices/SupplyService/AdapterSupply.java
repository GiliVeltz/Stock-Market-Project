package Domain.ExternalServices.SupplyService;

import Domain.ExternalServices.ExternalService;

public class AdapterSupply implements ExternalService{

    public AdapterSupply() {
    
    }

    @Override
    public boolean ConnectToService() {
        // Connect to the supply service
        return true;
    }
    
}
