package Domain.ExternalServices.PaymentService;

import Dtos.PaymentInfoDto;

public interface AdapterPaymentInterface {

    /**
     * This action type is used for check the availability of the external systems.
     * action_type = handshake
     * Additional Parameters: none
     * Output: “OK” message to signify that the handshake has been successful
     */
    boolean handshake();

    /**
     * This action type is used for charging a payment for purchases.
     *  action_type = pay
     *  Additional Parameters: amount, currency, card_number, month, year, holder, ccv, id
     *  Output: transaction id - an integer in the range [10000, 100000] which indicates a
     *  transaction number if the transaction succeeds or -1 if the transaction has failed.
     */
    int payment(PaymentInfoDto paymentInfo, double price);

    /**
     * This action type is used for cancelling a payment transaction.
     *  action_type = cancel_pay
     *  Additional Parameters: transaction_id - the id of the transaction id of the
     *  transaction to be canceled.
     *  Output: 1 if the cancelation has been successful or -1 if the cancelation has failed.
     */
    int cancel_pay(int transactionId);
}
