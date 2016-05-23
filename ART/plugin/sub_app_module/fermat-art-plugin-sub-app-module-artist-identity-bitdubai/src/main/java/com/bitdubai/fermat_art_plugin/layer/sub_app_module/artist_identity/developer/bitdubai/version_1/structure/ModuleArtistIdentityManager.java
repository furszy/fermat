package com.bitdubai.fermat_art_plugin.layer.sub_app_module.artist_identity.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_api.layer.modules.ModuleManagerImpl;
import com.bitdubai.fermat_api.layer.modules.common_classes.ActiveActorIdentityInformation;
import com.bitdubai.fermat_api.layer.modules.exceptions.ActorIdentityNotSelectedException;
import com.bitdubai.fermat_api.layer.modules.exceptions.CantGetSelectedActorIdentityException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_art_api.all_definition.enums.ArtExternalPlatform;
import com.bitdubai.fermat_art_api.all_definition.enums.ArtistAcceptConnectionsType;
import com.bitdubai.fermat_art_api.all_definition.enums.ExposureLevel;
import com.bitdubai.fermat_art_api.all_definition.exceptions.CantHideIdentityException;
import com.bitdubai.fermat_art_api.all_definition.exceptions.CantPublishIdentityException;
import com.bitdubai.fermat_art_api.all_definition.exceptions.IdentityNotFoundException;
import com.bitdubai.fermat_art_api.all_definition.interfaces.ArtIdentity;
import com.bitdubai.fermat_art_api.layer.identity.artist.exceptions.ArtistIdentityAlreadyExistsException;
import com.bitdubai.fermat_art_api.layer.identity.artist.exceptions.CantCreateArtistIdentityException;
import com.bitdubai.fermat_art_api.layer.identity.artist.exceptions.CantGetArtistIdentityException;
import com.bitdubai.fermat_art_api.layer.identity.artist.exceptions.CantListArtistIdentitiesException;
import com.bitdubai.fermat_art_api.layer.identity.artist.exceptions.CantUpdateArtistIdentityException;
import com.bitdubai.fermat_art_api.layer.identity.artist.interfaces.Artist;
import com.bitdubai.fermat_art_api.layer.identity.artist.interfaces.ArtistIdentityManager;
import com.bitdubai.fermat_art_api.layer.sub_app_module.identity.Artist.ArtistIdentityManagerModule;
import com.bitdubai.fermat_art_api.layer.sub_app_module.identity.Artist.ArtistIdentitySettings;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by alexander on 3/15/16.
 */
public class ModuleArtistIdentityManager
        extends ModuleManagerImpl<ArtistIdentitySettings>
        implements ArtistIdentityManagerModule,Serializable {

    private final ArtistIdentityManager artistIdentityManager;

    public ModuleArtistIdentityManager(
                                       ArtistIdentityManager artistIdentityManager,
                                       PluginFileSystem pluginFileSystem,
                                       UUID pluginId) {
        super(pluginFileSystem, pluginId);
        this.artistIdentityManager = artistIdentityManager;

    }

    @Override
    public List<Artist> listIdentitiesFromCurrentDeviceUser() throws CantListArtistIdentitiesException {
        return artistIdentityManager.listIdentitiesFromCurrentDeviceUser();
    }

    @Override
    public HashMap<ArtExternalPlatform, HashMap<UUID, String>> listExternalIdentitiesFromCurrentDeviceUser() throws CantListArtistIdentitiesException {
        return artistIdentityManager.listExternalIdentitiesFromCurrentDeviceUser();
    }

    @Override
    public ArtIdentity getLinkedIdentity(String publicKey) {
        return artistIdentityManager.getLinkedIdentity(publicKey);
    }

    @Override
    public Artist createArtistIdentity(
            final String alias,
            final byte[] imageBytes,
            final String externalUsername,
            ExposureLevel exposureLevel,
            ArtistAcceptConnectionsType acceptConnectionsType, final UUID externalIdentityID,
            final ArtExternalPlatform artExternalPlatform) throws CantCreateArtistIdentityException, ArtistIdentityAlreadyExistsException {
        return artistIdentityManager.createArtistIdentity(
                alias,
                imageBytes,
                externalUsername,
                exposureLevel,
                acceptConnectionsType,
                externalIdentityID,
                artExternalPlatform);
    }

    @Override
    public void updateArtistIdentity(
            String alias,
            String publicKey,
            byte[] profileImage,
            ExposureLevel exposureLevel,
            ArtistAcceptConnectionsType acceptConnectionsType,
            UUID externalIdentityID,
            ArtExternalPlatform artExternalPlatform,
            String externalUserName) throws CantUpdateArtistIdentityException {
        artistIdentityManager.updateArtistIdentity(
                alias,
                publicKey,
                profileImage,
                exposureLevel,
                acceptConnectionsType,
                externalIdentityID,
                artExternalPlatform,
                externalUserName);

    }

    @Override
    public Artist getArtistIdentity(String publicKey) throws CantGetArtistIdentityException, IdentityNotFoundException {
        return artistIdentityManager.getArtistIdentity(publicKey);
    }

    @Override
    public void publishIdentity(String publicKey) throws CantPublishIdentityException, IdentityNotFoundException {
        artistIdentityManager.publishIdentity(publicKey);
    }

    @Override
    public void hideIdentity(String publicKey) throws CantHideIdentityException, IdentityNotFoundException {
        artistIdentityManager.hideIdentity(publicKey);
    }

    /*@Override
    public SettingsManager<ArtistIdentitySettings> getSettingsManager() {
        if (this.settingsManager != null)
            return this.settingsManager;

        this.settingsManager = new SettingsManager<>(
                pluginFileSystem,
                pluginId
        );

        return this.settingsManager;
    }*/

    @Override
    public ActiveActorIdentityInformation getSelectedActorIdentity()
            throws CantGetSelectedActorIdentityException, ActorIdentityNotSelectedException {
        try{
            List<Artist> artistList = artistIdentityManager.listIdentitiesFromCurrentDeviceUser();
            ActiveActorIdentityInformation activeActorIdentityInformation;
            Artist fanatic;
            if(artistList!=null||!artistList.isEmpty()){
                fanatic = artistList.get(0);
                activeActorIdentityInformation = new ActiveActorIdentityInformationRecord(fanatic);
                return activeActorIdentityInformation;
            } else {
                //If there's no Identity created, in this version, I'll return an empty activeActorIdentityInformation
                activeActorIdentityInformation = new ActiveActorIdentityInformationRecord(null);
                return activeActorIdentityInformation;
            }
        } catch (CantListArtistIdentitiesException e) {
            throw new CantGetSelectedActorIdentityException(
                    e,
                    "Getting the ActiveActorIdentityInformation",
                    "Cannot get the selected identity");
        }
    }

    @Override
    public void createIdentity(String name, String phrase, byte[] profile_img) throws Exception {

    }

    @Override
    public void setAppPublicKey(String publicKey) {

    }

    @Override
    public int[] getMenuNotifications() {
        return new int[0];
    }
}
