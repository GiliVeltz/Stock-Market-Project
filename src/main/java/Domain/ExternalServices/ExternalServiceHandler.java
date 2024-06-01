package Domain.ExternalServices;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents an external service handler.
 * It manages a list of external services.
 */
public class ExternalServiceHandler {

    private Map<Integer, ExternalService> _externalServices; // uniqe id for each external service
    int _idCounter = 0;

    public ExternalServiceHandler() {
        _externalServices = new HashMap<>();
    }

    /**
     * Connects to all external services.
     * @return true if all services are successfully connected, false otherwise.
     */
    public boolean connectToServices() {
        for (ExternalService externalService : _externalServices.values()) {
            if (!externalService.ConnectToService()) {
                return false;
            }
        }
        return true;
    }

    // add payment service when initializing the system
    public boolean addPaymentService(String newSerivceName, String informationPersonName, String informationPersonPhone) {
        ExternalService newService = new ExternalService(_idCounter, newSerivceName, informationPersonName, informationPersonPhone);
        _externalServices.putIfAbsent(_idCounter, newService);
        _idCounter++;
        return true;
    }

    // add supply service when initializing the system
    public boolean addSupplyService(String newSerivceName, String informationPersonName, String informationPersonPhone) {
        ExternalService newService = new ExternalService(_idCounter, newSerivceName, informationPersonName, informationPersonPhone);
        _externalServices.putIfAbsent(_idCounter, newService);
        _idCounter++;
        return true;
    }

    // Add external service. called from the system service.
    public boolean addService(String newSerivceName, String informationPersonName, String informationPersonPhone) {
        boolean serviceExists = false;
        for (ExternalService service : _externalServices.values()) {
            if (service.getServiceName().equals(newSerivceName)) {
                serviceExists = true;
                break;
            }
        }
        if (serviceExists) {
            throw new IllegalArgumentException("Service name already exists. Service name: " + newSerivceName + ".");
        }

        ExternalService newService = new ExternalService(_idCounter, newSerivceName, informationPersonName, informationPersonPhone);
        _externalServices.putIfAbsent(_idCounter, newService);
        _idCounter++;
        return true;
    }

    // TODO: add fuction for change external service information. called from the system service.
}
