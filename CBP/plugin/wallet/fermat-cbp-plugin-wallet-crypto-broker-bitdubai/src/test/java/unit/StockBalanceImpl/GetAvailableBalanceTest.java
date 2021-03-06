package unit.StockBalanceImpl;

import com.bitdubai.fermat_api.layer.all_definition.enums.FiatCurrency;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.exceptions.CantGetAvailableBalanceCryptoBrokerWalletException;
import com.bitdubai.fermat_cbp_plugin.layer.wallet.crypto_broker.developer.bitdubai.version_1.structure.util.StockBalanceImpl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by José Vilchez on 22/01/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class GetAvailableBalanceTest {

    @Test
    public void getAvailableBalance() throws CantGetAvailableBalanceCryptoBrokerWalletException {
        StockBalanceImpl stockBalance = mock(StockBalanceImpl.class);
        when(stockBalance.getAvailableBalance(FiatCurrency.ARGENTINE_PESO)).thenReturn(1f);
        assertThat(stockBalance.getAvailableBalance(FiatCurrency.ARGENTINE_PESO)).isNotNull();
    }

}
