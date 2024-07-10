package Domain.ExternalServices.SupplyService;

import java.util.logging.Logger;

import Domain.ExternalServices.ExternalService;
import Dtos.SupplyInfoDto;

public class AdapterSupplyImp extends ExternalService implements AdapterSupplyInterface{

    // private fields
    private static AdapterSupplyImp _AdapterSupply;
    private ProxySupply _supplyService;
    private static final Logger logger = Logger.getLogger(AdapterSupplyImp.class.getName());

    private AdapterSupplyImp(int id, String newSerivceName, String informationPersonName, String informationPersonPhone) {
        super(id, newSerivceName, informationPersonName, informationPersonPhone);
        _AdapterSupply = this;
        _supplyService = new ProxySupply();
    }

    public static AdapterSupplyImp getAdapterSupply() {
        if(_AdapterSupply == null)
        _AdapterSupply = new AdapterSupplyImp(-1, "SupplyService", "Tal", "123456789");
        return _AdapterSupply;
    }

    @Override
    public boolean ConnectToService() {
        // Connect to the supply service
        logger.info("Connecting to the Supply service");

        return true;
    }

    @Override
    public boolean handshake() {
        logger.info("Starting handshake with the supply service");
        return _supplyService.handshake();
    }

    @Override
    public int supply(SupplyInfoDto supplyInfo) {
        logger.info("Supplying the cart");
        return _supplyService.supply(supplyInfo);
    }

    @Override
    public int cancel_supply(int transaction_id) {
        logger.info("Canceling the supply");
        return _supplyService.cancel_supply(transaction_id);
    }
}
