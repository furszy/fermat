package com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.channels.processors.clients;

import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.exceptions.CantInsertRecordDataBaseException;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.exceptions.CantReadRecordDataBaseException;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.Package;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.request.CheckInProfileMsgRequest;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.respond.CheckInProfileMsjRespond;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.profiles.ClientProfile;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.enums.HeadersAttName;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.enums.MessageContentType;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.enums.PackageType;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.channels.endpoinsts.FermatWebSocketChannelEndpoint;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.channels.processors.PackageProcessor;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.entities.CheckedInClient;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.entities.ClientsRegistrationHistory;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.enums.RegistrationResult;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.enums.RegistrationType;

import org.apache.commons.lang.ClassUtils;
import org.jboss.logging.Logger;

import java.io.IOException;

import javax.websocket.EncodeException;
import javax.websocket.Session;

/**
 * The Class <code>com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.channels.processors.clients.CheckInClientRequestProcessor</code>
 * process all packages received the type <code>PackageType.CHECK_IN_CLIENT_REQUEST</code><p/>
 * <p/>
 * Created by Roberto Requena - (rart3001@gmail.com) on 06/12/15.
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
public class CheckInClientRequestProcessor extends PackageProcessor {

    /**
     * Represent the LOG
     */
    private final Logger LOG = Logger.getLogger(ClassUtils.getShortClassName(CheckInClientRequestProcessor.class));

    /**
     * Constructor whit parameter
     *
     * @param fermatWebSocketChannelEndpoint register
     */
    public CheckInClientRequestProcessor(FermatWebSocketChannelEndpoint fermatWebSocketChannelEndpoint) {
        super(fermatWebSocketChannelEndpoint, PackageType.CHECK_IN_CLIENT_REQUEST);
    }

    /**
     * (non-javadoc)
     *
     * @see PackageProcessor#processingPackage(Session, Package)
     */
    @Override
    public void processingPackage(Session session, Package packageReceived) {

        System.out.println("Processing new package received CHECK IN CLIENT REQUEST");

        LOG.info("Processing new package received");

        String channelIdentityPrivateKey = getChannel().getChannelIdentity().getPrivateKey();
        String destinationIdentityPublicKey = (String) session.getUserProperties().get(HeadersAttName.CPKI_ATT_HEADER_NAME);
        ClientProfile clientProfile = null;

        try {

            CheckInProfileMsgRequest messageContent = CheckInProfileMsgRequest.parseContent(packageReceived.getContent());

            /*
             * Create the method call history
             */
            methodCallsHistory(getGson().toJson(messageContent.getProfileToRegister()), destinationIdentityPublicKey);

            /*
             * Validate if content type is the correct
             */
            if (messageContent.getMessageContentType() == MessageContentType.JSON) {

                /*
                 * Obtain the profile of the client
                 */
                clientProfile = (ClientProfile) messageContent.getProfileToRegister();

                /*
                 * CheckedInClient into data base
                 */
                checkInClient(clientProfile);

                /*
                 * If all ok, respond whit success message
                 */
                CheckInProfileMsjRespond respondProfileCheckInMsj = new CheckInProfileMsjRespond(CheckInProfileMsjRespond.STATUS.SUCCESS, CheckInProfileMsjRespond.STATUS.SUCCESS.toString(), clientProfile.getIdentityPublicKey());
                Package packageRespond = Package.createInstance(respondProfileCheckInMsj.toJson(), packageReceived.getNetworkServiceTypeSource(), PackageType.CHECK_IN_CLIENT_RESPOND, channelIdentityPrivateKey, destinationIdentityPublicKey);

                /*
                 * Send the respond
                 */
                session.getBasicRemote().sendObject(packageRespond);

            }

        } catch (Exception exception) {
            exception.printStackTrace();

            try {

                LOG.error(exception.getMessage());

                /*
                 * Respond whit fail message
                 */
                CheckInProfileMsjRespond respondProfileCheckInMsj = new CheckInProfileMsjRespond(
                        CheckInProfileMsjRespond.STATUS.FAIL,
                        exception.getLocalizedMessage(),
                        null
                );
                Package packageRespond = Package.createInstance(
                        respondProfileCheckInMsj.toJson(),
                        packageReceived.getNetworkServiceTypeSource(),
                        PackageType.CHECK_IN_CLIENT_RESPOND,
                        channelIdentityPrivateKey,
                        destinationIdentityPublicKey
                );

                /*
                 * Send the respond
                 */
                session.getBasicRemote().sendObject(packageRespond);

            } catch (IOException | EncodeException iOException) {
                LOG.error(iOException.getMessage());
            }
        }
    }

    /**
     * Create a new row into the data base
     *
     * @param profile of the client
     *
     * @throws CantInsertRecordDataBaseException if something goes wrong.
     */
    private void checkInClient(final ClientProfile profile) throws CantInsertRecordDataBaseException, CantReadRecordDataBaseException {

        if (!getDaoFactory().getCheckedInClientDao().exists(profile.getIdentityPublicKey())) {
            /*
             * Create the CheckedInClient
             */
            CheckedInClient checkedInClient = new CheckedInClient();
            checkedInClient.setIdentityPublicKey(profile.getIdentityPublicKey());
            checkedInClient.setDeviceType(profile.getDeviceType());

            //Validate if location are available
            if (profile.getLocation() != null) {
                checkedInClient.setLatitude(profile.getLocation().getLatitude());
                checkedInClient.setLongitude(profile.getLocation().getLongitude());
            }else{
                checkedInClient.setLatitude(0.0);
                checkedInClient.setLongitude(0.0);
            }

            /*
             * Save into the data base
             */
            getDaoFactory().getCheckedInClientDao().create(checkedInClient);

            /*
             * ClientsRegistrationHistory into data base
             */
            insertClientsRegistrationHistory(profile, RegistrationResult.SUCCESS, null);
        } else {
            insertClientsRegistrationHistory(profile, RegistrationResult.IGNORED, "The client was already checked-in.");
        }
    }

    /**
     * Create a new row into the data base
     *
     * @param profile of the client.
     * @param result  of the registration.
     * @param detail  of the registration.
     *
     * @throws CantInsertRecordDataBaseException if something goes wrong.
     */
    private void insertClientsRegistrationHistory(final ClientProfile      profile,
                                                  final RegistrationResult result ,
                                                  final String             detail ) throws CantInsertRecordDataBaseException {

        /*
         * Create the ClientsRegistrationHistory
         */
        ClientsRegistrationHistory clientsRegistrationHistory = new ClientsRegistrationHistory();
        clientsRegistrationHistory.setIdentityPublicKey(profile.getIdentityPublicKey());
        clientsRegistrationHistory.setDeviceType(profile.getDeviceType());
        clientsRegistrationHistory.setType(RegistrationType.CHECK_IN);
        clientsRegistrationHistory.setResult(result);
        clientsRegistrationHistory.setDetail(detail);

        //Validate if location are available
        if (profile.getLocation() != null) {
            clientsRegistrationHistory.setLastLatitude(profile.getLocation().getLatitude());
            clientsRegistrationHistory.setLastLongitude(profile.getLocation().getLongitude());
        }else{
            clientsRegistrationHistory.setLastLatitude(0.0);
            clientsRegistrationHistory.setLastLongitude(0.0);
        }

        /*
         * Save into the data base
         */
        getDaoFactory().getClientsRegistrationHistoryDao().create(clientsRegistrationHistory);
    }
}
