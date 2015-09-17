/*
 * @#ServerHandshakeRespondPacketProcessor.java - 2015
 * Copyright bitDubai.com., All rights reserved.
 * You may not modify, use, reproduce or distribute this software.
 * BITDUBAI/CONFIDENTIAL
 */
package com.bitdubai.fermat_p2p_plugin.layer.ws.communications.cloud.client.developer.bitdubai.version_1.structure.processors;

import com.bitdubai.fermat_api.layer.all_definition.crypto.asymmetric.AsymmectricCryptography;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.components.PlatformComponentProfileCommunication;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.contents.FermatPacketCommunicationFactory;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.contents.FermatPacketEncoder;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.commons.components.PlatformComponentProfile;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.commons.contents.FermatPacket;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.commons.enums.AttNamesConstants;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.commons.enums.FermatPacketType;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.commons.enums.NetworkServiceType;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.commons.enums.PlatformComponentType;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * The Class <code>com.bitdubai.fermat_p2p_plugin.layer.ws.communications.cloud.client.developer.bitdubai.version_1.structure.processors.ServerHandshakeRespondPacketProcessor</code> this
 * class process the FermatPacket of type <code>com.bitdubai.fermat_p2p_api.layer.p2p_communication.commons.enums.FermatPacketType.SERVER_HANDSHAKE_RESPOND</code>
 * <p/>
 * Created by Roberto Requena - (rart3001@gmail.com) on 03/09/15.
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
public class ServerHandshakeRespondPacketProcessor extends FermatPacketProcessor {

    /**
     * (no-javadoc)
     * @see FermatPacketProcessor#processingPackage(FermatPacket)
     */
    @Override
    public void processingPackage(final FermatPacket receiveFermatPacket) {

        System.out.println(" --------------------------------------------------------------------- ");
        System.out.println("ServerHandshakeRespondPacketProcessor - processingPackage");

        /* -----------------------------------------------------------------------------------------
         * IMPORTANT: This Message Content of this packet come encrypted with the temporal identity public key
         * and contain the server identity whit the communications cloud client that
         * have to use to talk with the server.
         * -----------------------------------------------------------------------------------------
         */

        /*
         * Decrypt the message content
         */
        String jsonRepresentation = AsymmectricCryptography.decryptMessagePrivateKey(receiveFermatPacket.getMessageContent(), getWsCommunicationsCloudClientChannel().getTemporalIdentity().getPrivateKey());

        /*
         * Construct the json object
         */
        JsonParser parser = new JsonParser();
        JsonObject serverIdentity = parser.parse(jsonRepresentation).getAsJsonObject();

        /*
         * Get the server identity and set into the communication cloud client
         */
        getWsCommunicationsCloudClientChannel().setServerIdentity(serverIdentity.get(AttNamesConstants.JSON_ATT_NAME_SERVER_IDENTITY).getAsString());


        System.out.println("ServerHandshakeRespondPacketProcessor - ServerIdentity = "+ getWsCommunicationsCloudClientChannel().getServerIdentity());

        /*
         * Construct a Communications Cloud Client Profile for this component and send and fermat packet type FermatPacketType.COMPONENT_REGISTRATION_REQUEST
         */
        PlatformComponentProfile communicationsCloudClientProfile = new PlatformComponentProfileCommunication("WsCommunicationsCloudClientChannel", getWsCommunicationsCloudClientChannel().getClientIdentity().getPublicKey(), getWsCommunicationsCloudClientChannel().getClientIdentity().getPublicKey(), new Double(0), new Double(0), "Web Socket Communications Cloud Client", NetworkServiceType.UNDEFINED, PlatformComponentType.COMMUNICATION_CLOUD_CLIENT_COMPONENT);
        getWsCommunicationsCloudClientChannel().setPlatformComponentProfile(communicationsCloudClientProfile);

        /* ------------------------------------
         * IMPORTANT: At this moment the server only
         * know the temporal identity of the client
         * the packet has construct with this identity
         * --------------------------------------
         */

        /*
         * Construct a fermat packet whit the server identity
         */
        FermatPacket fermatPacketRespond = FermatPacketCommunicationFactory.constructFermatPacketEncryptedAndSinged(getWsCommunicationsCloudClientChannel().getServerIdentity(),                    //Destination
                                                                                                                    getWsCommunicationsCloudClientChannel().getTemporalIdentity().getPublicKey(),   //Sender
                                                                                                                    communicationsCloudClientProfile.toJson(),                                      //Message Content
                                                                                                                    FermatPacketType.COMPONENT_REGISTRATION_REQUEST,                                //Packet type
                                                                                                                    getWsCommunicationsCloudClientChannel().getTemporalIdentity().getPrivateKey()); //Sender private key


        /*
         * Send the encode packet to the server
         */
        getWsCommunicationsCloudClientChannel().send(FermatPacketEncoder.encode(fermatPacketRespond));

    }

    /**
     * (no-javadoc)
     * @see FermatPacketProcessor#getFermatPacketType()
     */
    @Override
    public FermatPacketType getFermatPacketType() {
        return FermatPacketType.SERVER_HANDSHAKE_RESPOND;
    }
}
