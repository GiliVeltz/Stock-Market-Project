package Domain.ExternalServices.SupplyService;

import Dtos.SupplyInfoDto;

public class ProxySupply implements AdapterSupplyInterface{
    private static ProxySupply _adapterSupply;

    public ProxySupply() {
        _adapterSupply = this;
    }

    public static ProxySupply getProxySupply() {
        if (_adapterSupply == null)
            _adapterSupply = new ProxySupply();
        return _adapterSupply;
    }

    @Override
    public boolean handshake() {
        return true;
    }

    @Override
    public int supply(SupplyInfoDto supplyInfo) {
        return 1;
    }

    @Override
    public int cancel_supply(int transaction_id) {
        return -1;
    }

    @Override
    public boolean ConnectToService() {
        return true;
    }
}
