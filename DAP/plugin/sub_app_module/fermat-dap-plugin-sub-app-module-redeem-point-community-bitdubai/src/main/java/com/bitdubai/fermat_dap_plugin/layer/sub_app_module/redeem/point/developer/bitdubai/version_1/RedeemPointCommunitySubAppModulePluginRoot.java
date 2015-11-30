package com.bitdubai.fermat_dap_plugin.layer.sub_app_module.redeem.point.developer.bitdubai.version_1;

import com.bitdubai.fermat_api.layer.all_definition.common.system.abstract_classes.AbstractPlugin;
import com.bitdubai.fermat_api.layer.all_definition.common.system.annotations.NeededAddonReference;
import com.bitdubai.fermat_api.layer.all_definition.common.system.annotations.NeededPluginReference;
import com.bitdubai.fermat_api.layer.all_definition.common.system.utils.PluginVersionReference;
import com.bitdubai.fermat_api.layer.all_definition.enums.Addons;
import com.bitdubai.fermat_api.layer.all_definition.enums.Layers;
import com.bitdubai.fermat_api.layer.all_definition.enums.Platforms;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.util.Version;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.exceptions.CantGetAssetUserActorsException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.interfaces.ActorAssetUser;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.interfaces.ActorAssetUserManager;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.exceptions.CantConnectToActorAssetUserException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.exceptions.CantConnectToActorAssetRedeemPointException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.exceptions.CantGetAssetRedeemPointActorsException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.interfaces.ActorAssetRedeemPoint;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.interfaces.ActorAssetRedeemPointManager;
import com.bitdubai.fermat_dap_api.layer.dap_sub_app_module.redeem_point_community.interfaces.RedeemPointCommunitySubAppModuleManager;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.ErrorManager;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.UnexpectedPluginExceptionSeverity;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO explain here the main functionality of the plug-in.
 *
 * Created by Nerio on 13/10/15.
 */
public class RedeemPointCommunitySubAppModulePluginRoot extends AbstractPlugin implements
        RedeemPointCommunitySubAppModuleManager {

    @NeededPluginReference(platform = Platforms.DIGITAL_ASSET_PLATFORM, layer = Layers.ACTOR, plugin = Plugins.ASSET_USER)
    ActorAssetUserManager actorAssetUserManager;

    @NeededPluginReference(platform = Platforms.DIGITAL_ASSET_PLATFORM, layer = Layers.ACTOR, plugin = Plugins.REDEEM_POINT)
    ActorAssetRedeemPointManager actorAssetRedeemPointManager;

    @NeededAddonReference(platform = Platforms.PLUG_INS_PLATFORM, layer = Layers.PLATFORM_SERVICE, addon = Addons.ERROR_MANAGER)
    ErrorManager errorManager;

    public RedeemPointCommunitySubAppModulePluginRoot() {
        super(new PluginVersionReference(new Version()));
    }

    @Override
    public List<ActorAssetRedeemPoint> getAllActorAssetRedeemPointRegistered() throws CantGetAssetRedeemPointActorsException {

        try {
            return actorAssetRedeemPointManager.getAllAssetRedeemPointActorInTableRegistered();
        } catch (CantGetAssetRedeemPointActorsException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_REDEEM_POINT_COMMUNITY_SUB_APP_MODULE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public void connectToActorAssetRedeemPoint(ActorAssetUser requester, ActorAssetRedeemPoint actorAssetRedeemPoint) throws CantConnectToActorAssetUserException {

        ActorAssetUser actorAssetUser;
        //TODO Actor Asset User de BD Local
        try {
            actorAssetUser = actorAssetUserManager.getActorAssetUser();

            actorAssetUserManager.connectToActorAssetRedeemPoint(actorAssetUser, actorAssetRedeemPoint);

        } catch (CantGetAssetUserActorsException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_REDEEM_POINT_COMMUNITY_SUB_APP_MODULE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantConnectToActorAssetUserException(CantConnectToActorAssetUserException.DEFAULT_MESSAGE, e, "THERE WAS AN ERROR GET ACTOR ASSET USER.", null);
        } catch (CantConnectToActorAssetRedeemPointException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_REDEEM_POINT_COMMUNITY_SUB_APP_MODULE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantConnectToActorAssetUserException(CantConnectToActorAssetUserException.DEFAULT_MESSAGE, e, "THERE WAS AN ERROR CONNECTING TO REDEEM POINT.", null);
        }
    }
}
