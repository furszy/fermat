package test.com.bitdubai.fermat_tky_plugin.layer.identity.fan_identity.developer.bitdubai.version_1.database.tokenlyFanIdentityDeveloperDatabaseFactory;

import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_tky_plugin.layer.identity.fan_identity.developer.bitdubai.version_1.database.TokenlyFanIdentityDeveloperDatabaseFactory;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.doCallRealMethod;

/**
 * Created by gianco on 06/05/16.
 */
public class SetPluginDatabaseSystemTest {

    @Mock
    PluginDatabaseSystem pluginDatabaseSystem;

    @Test
    public void setPluginDatabaseSystemTest(){
        TokenlyFanIdentityDeveloperDatabaseFactory tokenlyFanIdentityDeveloperDatabaseFactory = Mockito.mock(TokenlyFanIdentityDeveloperDatabaseFactory.class);

        doCallRealMethod().when(tokenlyFanIdentityDeveloperDatabaseFactory).setPluginDatabaseSystem(pluginDatabaseSystem);

    }
}
