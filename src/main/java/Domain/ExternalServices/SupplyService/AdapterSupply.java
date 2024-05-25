package Domain.ExternalServices.SupplyService;

import java.util.logging.Logger;

import Domain.ExternalServices.ExternalService;
import Exceptions.ShippingFailedException;

public class AdapterSupply implements ExternalService {

    private ProxySupply _supplyService;
    private static final Logger logger = Logger.getLogger(AdapterSupply.class.getName());

    public AdapterSupply() {
        _supplyService = new ProxySupply();
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

}
