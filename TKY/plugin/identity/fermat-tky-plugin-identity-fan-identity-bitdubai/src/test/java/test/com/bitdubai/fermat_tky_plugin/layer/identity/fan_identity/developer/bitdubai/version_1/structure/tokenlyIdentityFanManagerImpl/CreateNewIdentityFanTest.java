package test.com.bitdubai.fermat_tky_plugin.layer.identity.fan_identity.developer.bitdubai.version_1.structure.tokenlyIdentityFanManagerImpl;

import com.bitdubai.fermat_tky_api.all_definitions.enums.ExternalPlatform;
import com.bitdubai.fermat_tky_api.all_definitions.interfaces.User;
import com.bitdubai.fermat_tky_api.layer.identity.fan.exceptions.CantCreateFanIdentityException;
import com.bitdubai.fermat_tky_api.layer.identity.fan.interfaces.Fan;
import com.bitdubai.fermat_tky_plugin.layer.identity.fan_identity.developer.bitdubai.version_1.structure.TokenlyIdentityFanManagerImpl;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

/**
 * Created by gianco on 06/05/16.
 */
public class CreateNewIdentityFanTest {

    @Mock
    Fan fan;

    @Mock
    User user;

    @Mock
    String password;

    @Mock
    byte[] profileImage;

    @Mock
    ExternalPlatform externalPlatform;

    @Test
    public void createNewIdentityFanTest() throws CantCreateFanIdentityException {
        TokenlyIdentityFanManagerImpl tokenlyIdentityFanManager = Mockito.mock(TokenlyIdentityFanManagerImpl.class);

        when(tokenlyIdentityFanManager.createNewIdentityFan(user,
                                                            password,
                                                            profileImage,
                                                            externalPlatform)).thenReturn(fan);
    }
}
