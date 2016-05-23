package test.com.bitdubai.fermat_art_plugin.layer.actor_network_service.fan.developer.bitdubai.version_1.database.fanActorNetworkServiceDeveloperDatabaseFactory;

import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperDatabase;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperDatabaseTable;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperObjectFactory;
import com.bitdubai.fermat_art_plugin.layer.actor_network_service.fan.developer.bitdubai.version_1.database.FanActorNetworkServiceDeveloperDatabaseFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

import static org.mockito.Mockito.when;

/**
 * Created by gianco on 02/05/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(FanActorNetworkServiceDeveloperDatabaseFactory.class)
public class GetDatabaseTableListTest {

    @Mock
    List<DeveloperDatabaseTable> developerDatabaseTableList;
    @Mock
    DeveloperObjectFactory developerObjectFactory;
    @Mock
    DeveloperDatabase developerDatabase;

    @Test
    public void getDatabaseTableListTest() {

        FanActorNetworkServiceDeveloperDatabaseFactory fanActorNetworkServiceDeveloperDatabaseFactory = PowerMockito.mock(FanActorNetworkServiceDeveloperDatabaseFactory.class);

        when(fanActorNetworkServiceDeveloperDatabaseFactory.getDatabaseTableList(developerObjectFactory, developerDatabase)).thenReturn(developerDatabaseTableList);
    }


}
