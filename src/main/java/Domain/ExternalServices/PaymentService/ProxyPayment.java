package Domain.ExternalServices.PaymentService;

import Dtos.PaymentInfoDto;

public class ProxyPayment implements AdapterPaymentInterface{
    private static ProxyPayment _adapterPayment;

    public ProxyPayment() {
        _adapterPayment = this;
    }

    public static ProxyPayment getProxyAdapterPayment() {
        if (_adapterPayment == null)
            _adapterPayment = new ProxyPayment();
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
