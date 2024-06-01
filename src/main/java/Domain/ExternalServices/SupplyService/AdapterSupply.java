package Domain.ExternalServices.SupplyService;

import java.util.logging.Logger;

import Domain.ExternalServices.ExternalService;
import Exceptions.ShippingFailedException;

public class AdapterSupply implements ExternalService {

    // private fields
    private int _id;
    private String _serviceName;
    private String _informationPersonName;
    private String _informationPersonPhone;
    private static AdapterSupply _AdapterSupply;
    private ProxySupply _supplyService;
    private static final Logger logger = Logger.getLogger(AdapterSupply.class.getName());

    public AdapterSupply() {
        _supplyService = new ProxySupply();
    }

    private AdapterSupply(int id, String newSerivceName, String informationPersonName, String informationPersonPhone) {
        _id = id;
        _serviceName = newSerivceName;
        _informationPersonName = informationPersonName;
        _informationPersonPhone = informationPersonPhone;
        _AdapterSupply = this;
        _supplyService = new ProxySupply();
    }

    public static AdapterSupply getAdapterPayment() {
        if(_AdapterSupply == null)
        _AdapterSupply = new AdapterSupply();
        return _AdapterSupply;
    }

    @Override
    public boolean ConnectToService() {
        // Connect to the supply service
        // TODO : forward the request to the Supply service
        logger.info("Connecting to the Supply service");

        return true;
    }

    public void checkIfDeliverOk(String address, String shopAddress) throws ShippingFailedException {
        logger.info("Checking if the delivery is valid");
        if (!_supplyService.checkIfDeliverOk(address, shopAddress))
            throw new ShippingFailedException("Shipping failed");
    }

    public void deliver(String clientAddress, String shopAddress) {
        logger.info("Delivering the cart");
        _supplyService.deliver(clientAddress, shopAddress);
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
