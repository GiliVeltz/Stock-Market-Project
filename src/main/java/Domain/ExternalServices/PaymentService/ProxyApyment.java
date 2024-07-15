package Domain.ExternalServices.PaymentService;

import Dtos.PaymentInfoDto;

public class ProxyApyment implements AdapterPaymentInterface{
    private static ProxyApyment _adapterPayment;

    public ProxyApyment() {
        _adapterPayment = this;
    }

    public static ProxyApyment getProxyApymentPayment() {
        if (_adapterPayment == null)
            _adapterPayment = new ProxyApyment();
        return _adapterPayment;
    }

    @Override
    public boolean handshake() {
        return true;
    }

    @Override
    public int payment(PaymentInfoDto paymentInfo, double price) {
        return (int) price;
    }

    @Override
    public int cancel_pay(int transactionId) {
        return -1;
    }

    @Override
    public boolean ConnectToService() {
        return true;
    }
}
