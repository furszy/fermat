package com.bitdubai.fermat_bnk_core;

import com.bitdubai.fermat_api.layer.all_definition.common.system.abstract_classes.AbstractPlatform;
import com.bitdubai.fermat_api.layer.all_definition.common.system.exceptions.CantRegisterLayerException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.exceptions.CantStartPlatformException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.utils.PlatformReference;
import com.bitdubai.fermat_api.layer.all_definition.enums.Platforms;
import com.bitdubai.fermat_bnk_core.layer.bank_money_transaction.BankMoneyTransactionLayer;
import com.bitdubai.fermat_bnk_core.layer.wallet.WalletLayer;

/**
 * Created by memo on 25/11/15.
 */
public class BNKPlatform extends AbstractPlatform {

    public BNKPlatform() {
        super(new PlatformReference(Platforms.BANKING_PLATFORM));
    }

    @Override
    public void start() throws CantStartPlatformException {
        try {
            registerLayer(new BankMoneyTransactionLayer());
            registerLayer(new WalletLayer());
        } catch (CantRegisterLayerException e) {

            throw new CantStartPlatformException(
                    e,
                    "",
                    "Problem trying to register a layer."
            );
        }
    }
}
