package Domain.ExternalServices;

import java.util.ArrayList;
import java.util.List;

public class ExternalServiceHandler {
    //fields of list of external services
    private List<ExternalService> externalServices;

    public ExternalServiceHandler() {
        this.externalServices = new ArrayList<ExternalService>();
    }

    //connect to all external services
    public boolean ConnectToServices() {
        for (ExternalService externalService : externalServices) {
            if (!externalService.ConnectToService()) {
                return false;
            }
        }
        return true;
    }
    
}
