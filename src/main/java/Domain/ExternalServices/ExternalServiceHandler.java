package Domain.ExternalServices;

import java.util.HashMap;
import java.util.Map;

import Dtos.ExternalServiceDto;

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
    public boolean addExternalService(ExternalServiceDto externalServiceDto) {
        // check validation of the arguments
        if(externalServiceDto.getServiceName() == null || externalServiceDto.getServiceName().length() == 0 || externalServiceDto.getInformationPersonName() == null
        || externalServiceDto.getInformationPersonName().length() == 0 || externalServiceDto.getInformationPersonPhone() == null || externalServiceDto.getInformationPersonPhone().length() == 0){
                      throw new IllegalArgumentException("One or more of the arguments are null");
        }

        // check if service name already exists
        boolean serviceExists = false;
        for (ExternalService service : _externalServices.values()) {
            if (service.getServiceName().equals(externalServiceDto.getServiceName())) {
                serviceExists = true;
                break;
            }
        }
        if (serviceExists) {
            throw new IllegalArgumentException("Service name already exists. Service name: " + externalServiceDto.getServiceName() + ".");
        }

        ExternalService newService = new ExternalService(_idCounter, externalServiceDto.getServiceName(), externalServiceDto.getInformationPersonName(), externalServiceDto.getInformationPersonPhone());
        _externalServices.putIfAbsent(_idCounter, newService);
        _idCounter++;
        return true;
    }

    // This function changes the external service name when given service id and new service name.
    public boolean changeExternalServiceName(int serviceId, String newServiceName) {
        // check validation of the arguments
        if(newServiceName == null || newServiceName.length() == 0){
            throw new IllegalArgumentException("One or more of the arguments are null");
        }

        // check if service id exists
        if (!_externalServices.containsKey(serviceId)) {
            throw new IllegalArgumentException("Service id not found. Service id: " + serviceId + ".");
        }

        // check if service name already exists
        boolean serviceExists = false;
        for (ExternalService service : _externalServices.values()) {
            if (service.getServiceName().equals(newServiceName)) {
                serviceExists = true;
                break;
            }
        }
        if (serviceExists) {
            throw new IllegalArgumentException("Service name already exists. Service name: " + newServiceName + ".");
        }

        ExternalService service = _externalServices.get(serviceId);
        service.setServiceName(newServiceName);
        return true;
    }

    // This function changes the external service information person name when given service id and new information person name.
    public boolean changeExternalServiceInformationPersonName(int serviceId, String newInformationPersonName) {
        // check validation of the arguments
        if(newInformationPersonName == null || newInformationPersonName.length() == 0){
            throw new IllegalArgumentException("One or more of the arguments are null");
        }

        // check if service id exists
        if (!_externalServices.containsKey(serviceId)) {
            throw new IllegalArgumentException("Service id not found. Service id: " + serviceId + ".");
        }

        ExternalService service = _externalServices.get(serviceId);
        service.setInformationPersonName(newInformationPersonName);
        return true;
    }

    // This function changes the external service information person phone when given service id and new information person phone.
    public boolean changeExternalServiceInformationPersonPhone(int serviceId, String newInformationPersonPhone) {
        // check validation of the arguments
        if(newInformationPersonPhone == null || newInformationPersonPhone.length() == 0){
            throw new IllegalArgumentException("One or more of the arguments are null");
        }

        // check if service id exists
        if (!_externalServices.containsKey(serviceId)) {
            throw new IllegalArgumentException("Service id not found. Service id: " + serviceId + ".");
        }

        ExternalService service = _externalServices.get(serviceId);
        service.setInformationPersonPhone(newInformationPersonPhone);
        return true;
    }

    public int getServiceIdByName(String serviceName){
        for (ExternalService service : _externalServices.values()) {
            if (service.getServiceName().equals(serviceName)) {
                return service.getServiceId();
            }
        }
        throw new IllegalArgumentException("Service id not found. Service name: " + serviceName + ".");
    }

    // get external service by id
    public ExternalService getExternalServiceById(int serviceId) {
        if (!_externalServices.containsKey(serviceId)) {
            throw new IllegalArgumentException("Service id not found. Service id: " + serviceId + ".");
        }
        return _externalServices.get(serviceId);
    }

    // get external service by name
    public ExternalService getExternalServiceByName(String serviceName) {
        for (ExternalService service : _externalServices.values()) {
            if (service.getServiceName().equals(serviceName)) {
                return service;
            }
        }
        throw new IllegalArgumentException("Service name not found. Service name: " + serviceName + ".");
    }

    // is service exists by id
    public boolean isServiceExistsById(int serviceId) {
        return _externalServices.containsKey(serviceId);
    }

    // is service exists by name
    public boolean isServiceExistsByName(String serviceName) {
        for (ExternalService service : _externalServices.values()) {
            if (service.getServiceName().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }
}
