package com.bitdubai.fermat_cbp_plugin.layer.actor_network_service.crypto_broker.developer.bitdubai.version_1.messages;

import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.enums.ConnectionRequestAction;
import com.bitdubai.fermat_cbp_plugin.layer.actor_network_service.crypto_broker.developer.bitdubai.version_1.enums.MessageTypes;
import com.bitdubai.fermat_ccp_api.layer.network_service.crypto_payment_request.enums.RequestAction;

import java.util.UUID;

/**
 * The class <code>com.bitdubai.fermat_cbp_plugin.layer.actor_network_service.crypto_broker.developer.bitdubai.version_1.messages.InformationMessage</code>
 * contains the structure of a Information message for this plugin.
 * <p>
 * Created by Leon Acosta - (laion.cj91@gmail.com) on 23/11/2015.
 */
public class InformationMessage extends NetworkServiceMessage {

    private final UUID                    requestId;
    private final ConnectionRequestAction action   ;

    public InformationMessage(final UUID                    requestId,
                              final ConnectionRequestAction action   ) {

        super(MessageTypes.INFORMATION);

        this.requestId = requestId;
        this.action    = action   ;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public ConnectionRequestAction getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "InformationMessage{" +
                "requestId=" + requestId +
                ", action=" + action +
                '}';
    }
}
