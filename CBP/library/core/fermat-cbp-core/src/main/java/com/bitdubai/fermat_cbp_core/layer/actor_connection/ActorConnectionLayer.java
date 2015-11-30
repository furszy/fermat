package com.bitdubai.fermat_cbp_core.layer.actor_connection;

import com.bitdubai.fermat_api.layer.all_definition.common.system.abstract_classes.AbstractLayer;
import com.bitdubai.fermat_api.layer.all_definition.common.system.exceptions.CantRegisterPluginException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.exceptions.CantStartLayerException;
import com.bitdubai.fermat_api.layer.all_definition.enums.Layers;
import com.bitdubai.fermat_cbp_core.layer.actor_connection.crypto_broker.CryptoBrokerPluginSubsystem;
import com.bitdubai.fermat_cbp_core.layer.actor_connection.crypto_customer.CryptoCustomerPluginSubsystem;

/**
 * Created by Leon Acosta - (laion.cj91@gmail.com) on 21/11/2015.
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
public class ActorConnectionLayer extends AbstractLayer {

    public ActorConnectionLayer() {
        super(Layers.ACTOR_CONNECTION);
    }

    public void start() throws CantStartLayerException {

        try {

            registerPlugin(new CryptoBrokerPluginSubsystem());
            registerPlugin(new CryptoCustomerPluginSubsystem());

        } catch(CantRegisterPluginException e) {

            throw new CantStartLayerException(
                    e,
                    "",
                    "Problem trying to register a plugin."
            );
        }
    }

}
