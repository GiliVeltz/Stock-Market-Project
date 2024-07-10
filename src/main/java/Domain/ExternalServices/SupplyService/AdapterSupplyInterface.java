package Domain.ExternalServices.SupplyService;

import Dtos.SupplyInfoDto;

public interface AdapterSupplyInterface {

    /**
     * This action type is used for check the availability of the external systems.
     * action_type = handshake
     * Additional Parameters: none
     * Output: “OK” message to signify that the handshake has been successful
     */
    boolean handshake();

    /**
     * This action type is used for dispatching a delivery to a costumer.
     * action_type = supply
     * Additional Parameters: name , address, city, country, zip
     * Output: transaction id - an integer in the range [10000, 100000] which indicates a
     * transaction number if the transaction succeeds or -1 if the transaction has failed.
     */
    int supply(SupplyInfoDto supplyInfo);


    /**
     * This action type is used for cancelling a supply transaction.
     * action_type = cancel_supply
     * Additional Parameters: transaction_id - the id of the transaction id of the
     * transaction to be canceled.
     * Output: 1 if the cancelation has been successful or -1 if the cancelation has failed
     */
    int cancel_supply(int transaction_id);
} 
