package com.bitdubai.fermat_cbp_api.layer.wallet_module.common.interfaces;

/**
 * Summarized information about a index
 */
public interface IndexInfoSummary {
    /**
     * @return String with formatted like this: Currency / Reference Currency. <br> Example: BTC/USD
     */
    String getCurrencyAndReferenceCurrency();

    /**
     * @return String formatted like this: Currency SalePrice. <br> Example: USD 325,81
     */
    String getSalePriceAndCurrency();

    /**
     * @return String formatted like this: Currency PurchasePrice. <br> Example: USD 314,56
     */
    String getPurchasePriceAndCurrency();
}
