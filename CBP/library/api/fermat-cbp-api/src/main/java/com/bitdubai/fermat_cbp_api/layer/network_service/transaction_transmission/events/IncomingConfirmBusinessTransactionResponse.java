package com.bitdubai.fermat_cbp_api.layer.network_service.transaction_transmission.events;


import com.bitdubai.fermat_cbp_api.all_definition.events.enums.EventType;

/**
 * Created by Manuel Perez (darkpriestrelative@gmail.com) on 26/11/15.
 */
public class IncomingConfirmBusinessTransactionResponse extends AbstractBusinessTransactionEvent {

    public IncomingConfirmBusinessTransactionResponse(EventType eventType) {

        super(eventType);

    }


}
