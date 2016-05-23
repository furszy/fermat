package test.com.bitdubai.fermat_art_plugin.layer.actor_network_service.fan.developer.bitdubai.version_1.structure.fanActorNetworkServiceManager;

import com.bitdubai.fermat_art_api.layer.actor_network_service.exceptions.CantCancelConnectionRequestException;
import com.bitdubai.fermat_art_api.layer.actor_network_service.exceptions.ConnectionRequestNotFoundException;
import com.bitdubai.fermat_art_plugin.layer.actor_network_service.fan.developer.bitdubai.version_1.structure.FanActorNetworkServiceManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.UUID;

import static org.powermock.api.mockito.PowerMockito.doCallRealMethod;

/**
 * Created by gianco on 27/04/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(FanActorNetworkServiceManager.class)
public class CancelConnectionTest {

    @Test
    public void cancelConnectionTest () throws ConnectionRequestNotFoundException, CantCancelConnectionRequestException {

        FanActorNetworkServiceManager fanActorNetworkServiceManager = PowerMockito.mock(FanActorNetworkServiceManager.class);
        final UUID requestId = new UUID(0,0);

        doCallRealMethod().when(fanActorNetworkServiceManager).cancelConnection(requestId);

    }
}
