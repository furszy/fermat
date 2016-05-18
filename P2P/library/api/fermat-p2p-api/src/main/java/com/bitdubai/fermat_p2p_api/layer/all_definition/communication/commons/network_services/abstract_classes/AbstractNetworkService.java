package com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.abstract_classes;

import com.bitdubai.fermat_api.CantStartPluginException;
import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.abstract_classes.AbstractPlugin;
import com.bitdubai.fermat_api.layer.all_definition.common.system.annotations.NeededAddonReference;
import com.bitdubai.fermat_api.layer.all_definition.common.system.annotations.NeededPluginReference;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.ErrorManager;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.error_manager.enums.UnexpectedPluginExceptionSeverity;
import com.bitdubai.fermat_api.layer.all_definition.common.system.utils.PluginVersionReference;
import com.bitdubai.fermat_api.layer.all_definition.crypto.asymmetric.AsymmetricCryptography;
import com.bitdubai.fermat_api.layer.all_definition.crypto.asymmetric.ECCKeyPair;
import com.bitdubai.fermat_api.layer.all_definition.enums.Addons;
import com.bitdubai.fermat_api.layer.all_definition.enums.Layers;
import com.bitdubai.fermat_api.layer.all_definition.enums.Platforms;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.events.EventSource;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEventListener;
import com.bitdubai.fermat_api.layer.all_definition.network_service.enums.NetworkServiceType;
import com.bitdubai.fermat_api.layer.osa_android.database_system.Database;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantCreateDatabaseException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantOpenDatabaseException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.DatabaseNotFoundException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.FileLifeSpan;
import com.bitdubai.fermat_api.layer.osa_android.file_system.FilePrivacy;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginTextFile;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.CantCreateFileException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.FileNotFoundException;
import com.bitdubai.fermat_api.layer.osa_android.location_system.Location;
import com.bitdubai.fermat_api.layer.osa_android.location_system.LocationManager;
import com.bitdubai.fermat_api.layer.osa_android.location_system.exceptions.CantGetDeviceLocationException;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.interfaces.NetworkClientConnection;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.interfaces.NetworkClientManager;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.agents.NetworkServicePendingMessagesSupervisorAgent;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.agents.NetworkServiceRegistrationProcessAgent;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.database.constants.NetworkServiceDatabaseConstants;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.database.entities.NetworkServiceMessage;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.database.exceptions.CantInitializeNetworkServiceDatabaseException;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.database.factories.NetworkServiceDatabaseFactory;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.event_handlers.NetworkClientActorFoundEventHandler;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.event_handlers.NetworkClientConnectionClosedEventHandler;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.event_handlers.NetworkClientConnectionLostEventHandler;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.event_handlers.NetworkClientConnectionSuccessEventHandler;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.event_handlers.NetworkClientNetworkServiceRegisteredEventHandler;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.event_handlers.NetworkClientNewMessageTransmitEventHandler;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.event_handlers.NetworkClientRegisteredEventHandler;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.event_handlers.NetworkClientSentMessageDeliveredEventHandler;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.exceptions.CantInitializeIdentityException;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.exceptions.CantInitializeNetworkServiceProfileException;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.exceptions.CantSendMessageException;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.factories.NetworkServiceMessageFactory;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.structure.NetworkServiceConnectionManager;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.profiles.ActorProfile;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.profiles.NetworkServiceProfile;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.enums.MessageContentType;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.enums.P2pEventType;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.network_services.interfaces.NetworkService;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.CommunicationChannels;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.commons.enums.FermatMessagesStatus;
import com.bitdubai.fermat_pip_api.layer.platform_service.event_manager.interfaces.EventManager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The class <code>com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.abstract_classes.AbstractNetworkService</code>
 * implements the basic functionality of a network service component and define its behavior.<p/>
 *
 * Created by Leon Acosta - (laion.cj91@gmail.com) on 02/05/2016.
 *
 * @author  lnacosta
 * @version 1.0
 * @since   Java JDK 1.7
 */
public abstract class AbstractNetworkService extends AbstractPlugin implements NetworkService {

    @NeededAddonReference(platform = Platforms.PLUG_INS_PLATFORM   , layer = Layers.PLATFORM_SERVICE, addon = Addons.ERROR_MANAGER)
    protected ErrorManager errorManager;

    @NeededAddonReference(platform = Platforms.PLUG_INS_PLATFORM   , layer = Layers.PLATFORM_SERVICE, addon = Addons.EVENT_MANAGER)
    protected EventManager eventManager;

    @NeededAddonReference(platform = Platforms.OPERATIVE_SYSTEM_API, layer = Layers.SYSTEM          , addon = Addons.DEVICE_LOCATION)
    protected LocationManager locationManager;

    @NeededAddonReference(platform = Platforms.OPERATIVE_SYSTEM_API, layer = Layers.SYSTEM          , addon = Addons.PLUGIN_DATABASE_SYSTEM)
    protected PluginDatabaseSystem pluginDatabaseSystem;

    @NeededAddonReference(platform = Platforms.OPERATIVE_SYSTEM_API, layer = Layers.SYSTEM          , addon = Addons.PLUGIN_FILE_SYSTEM)
    protected PluginFileSystem pluginFileSystem;

    @NeededPluginReference(platform = Platforms.COMMUNICATION_PLATFORM, layer = Layers.COMMUNICATION, plugin = Plugins.NETWORK_CLIENT)
    protected NetworkClientManager networkClientManager;

    /**
     * Represents the EVENT_SOURCE
     */
    public EventSource eventSource;

    /**
     * Represents the identity
     */
    private ECCKeyPair identity;

    /**
     * Represents the network Service Type
     */
    private NetworkServiceType networkServiceType;

    /**
     * Represents the network service profile.
     */
    private NetworkServiceProfile profile;

    /**
     * Represents the dataBase
     */
    private Database networkServiceDatabase;

    /**
     * Represents the registered
     */
    private boolean registered;

    /**
     * Holds the listeners references
     */
    protected List<FermatEventListener> listenersAdded;

    /*
     * Represents the listActorConnectIntoNode
     */
    protected Map<String, String> listActorConnectIntoNode;

    /*
     * Represents the listActorProfileConnectedInNode
     */
    protected Map<String, List<ActorProfile>> listActorProfileConnectedInNode;


    /**
     * Represents the networkServiceConnectionManager
     */
    private NetworkServiceConnectionManager networkServiceConnectionManager;

    /**
     * AGENTS DEFINITION ----->
     */
    /**
     * Represents the networkServiceRegistrationProcessAgent
     */
    private NetworkServiceRegistrationProcessAgent networkServiceRegistrationProcessAgent;

    /**
     * Represents the NetworkServicePendingMessagesSupervisorAgent
     */
    private NetworkServicePendingMessagesSupervisorAgent networkServicePendingMessagesSupervisorAgent;

    protected ActorProfile actorProfile;

    /**
     * Constructor with parameters
     *
     * @param pluginVersionReference
     * @param eventSource
     * @param networkServiceType
     */
    public AbstractNetworkService(final PluginVersionReference pluginVersionReference,
                                  final EventSource            eventSource           ,
                                  final NetworkServiceType     networkServiceType    ) {

        super(pluginVersionReference);

        this.eventSource           = eventSource;
        this.networkServiceType    = networkServiceType;

        this.registered            = Boolean.FALSE;
        this.listenersAdded        = new CopyOnWriteArrayList<>();
        this.listActorConnectIntoNode = new HashMap<>();
        this.listActorProfileConnectedInNode = new HashMap<>();
    }

    /**
     * (non-javadoc)
     * @see AbstractPlugin#start()
     */
    @Override
    public final void start() throws CantStartPluginException {

        /*
         * Validate required resources
         */
        validateInjectedResources();

        try {

            /*
             * Initialize the identity
             */
            initializeIdentity();

            /*
             * Initialize the profile
             */
            initializeProfile();

            /*
             * Initialize the data base
             */
            initializeDataBase();

            /*
             * Initialize listeners
             */
            initializeNetworkServiceListeners();

            this.networkServiceConnectionManager = new NetworkServiceConnectionManager(this, errorManager);

            /*
             * Initialize the agents and start
             */
            this.networkServiceRegistrationProcessAgent = new NetworkServiceRegistrationProcessAgent(this);
            this.networkServiceRegistrationProcessAgent.start();

            this.networkServicePendingMessagesSupervisorAgent = new NetworkServicePendingMessagesSupervisorAgent(this);
            this.networkServicePendingMessagesSupervisorAgent.start();

            onNetworkServiceStart();

        } catch (Exception exception) {

            System.out.println(exception.toString());

            String context = "Plugin ID: " + pluginId + CantStartPluginException.CONTEXT_CONTENT_SEPARATOR
                    + "Database Name: " + NetworkServiceDatabaseConstants.DATABASE_NAME
                    + "NS Name: " + "COMPLETE"; // TODO COMPLETE WITH NETWORK SERVICE NAME

            String possibleCause = "The Template triggered an unexpected problem that wasn't able to solve by itself - ";
            possibleCause += exception.getMessage();
            CantStartPluginException pluginStartException = new CantStartPluginException(CantStartPluginException.DEFAULT_MESSAGE, exception, context, possibleCause);

            errorManager.reportUnexpectedPluginException(this.getPluginVersionReference(), UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, pluginStartException);
            throw pluginStartException;

        }
    }

    /**
     * This method validates if there are injected all the required resources
     * in the network service root.
     */
    private void validateInjectedResources() throws CantStartPluginException {

         /*
         * Ask if the resources are injected.
         */
        if (networkClientManager == null ||
                pluginDatabaseSystem == null ||
                locationManager == null ||
                errorManager == null ||
                eventManager == null) {

            String context =
                    "Plugin ID: " + pluginId
                    + CantStartPluginException.CONTEXT_CONTENT_SEPARATOR
                    + "networkClientManager: " + networkClientManager
                    + CantStartPluginException.CONTEXT_CONTENT_SEPARATOR
                    + "pluginDatabaseSystem: " + pluginDatabaseSystem
                    + CantStartPluginException.CONTEXT_CONTENT_SEPARATOR
                    + "locationManager: " + locationManager
                    + CantStartPluginException.CONTEXT_CONTENT_SEPARATOR
                    + "errorManager: " + errorManager
                    + CantStartPluginException.CONTEXT_CONTENT_SEPARATOR
                    + "eventManager: " + eventManager;

            String possibleCause = "No all required resource are injected";
            CantStartPluginException pluginStartException = new CantStartPluginException(CantStartPluginException.DEFAULT_MESSAGE, null, context, possibleCause);

            errorManager.reportUnexpectedPluginException(this.getPluginVersionReference(), UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, pluginStartException);
            throw pluginStartException;
        }

    }

    private static final String IDENTITY_FILE_DIRECTORY = "private"   ;
    private static final String IDENTITY_FILE_NAME      = "nsIdentity";

    /**
     * Initializes a key pair identity for this network service
     *
     * @throws CantInitializeIdentityException if something goes wrong.
     */
    private void initializeIdentity() throws CantInitializeIdentityException {

        try {

             /*
              * Load the file with the network service identity
              */
            PluginTextFile pluginTextFile = pluginFileSystem.getTextFile(pluginId, IDENTITY_FILE_DIRECTORY, IDENTITY_FILE_NAME, FilePrivacy.PRIVATE, FileLifeSpan.PERMANENT);
            String content = pluginTextFile.getContent();

            identity = new ECCKeyPair(content);

        } catch (FileNotFoundException e) {

            /*
             * The file does not exist, maybe it is the first time that the plugin had been run on this device,
             * We need to create the new network service identity
             */
            try {

                /*
                 * Create the new network service identity
                 */
                identity = new ECCKeyPair();

                /*
                 * save into the file
                 */
                PluginTextFile pluginTextFile = pluginFileSystem.createTextFile(pluginId, IDENTITY_FILE_DIRECTORY, IDENTITY_FILE_NAME, FilePrivacy.PRIVATE, FileLifeSpan.PERMANENT);
                pluginTextFile.setContent(identity.getPrivateKey());
                pluginTextFile.persistToMedia();

            } catch (Exception exception) {
                /*
                 * The file cannot be created. We can not handle this situation.
                 */
                errorManager.reportUnexpectedPluginException(this.getPluginVersionReference(), UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, exception);
                throw new CantInitializeIdentityException(exception, "", "Unhandled Exception");
            }


        } catch (CantCreateFileException cantCreateFileException) {

            /*
             * The file cannot be load. We can not handle this situation.
             */
            errorManager.reportUnexpectedPluginException(this.getPluginVersionReference(), UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, cantCreateFileException);
            throw new CantInitializeIdentityException(cantCreateFileException, "", "Error creating the identity file.");

        }

    }

    /**
     * Initializes the profile of this network service
     *
     * @throws CantInitializeNetworkServiceProfileException if something goes wrong.
     */
    private void initializeProfile() throws CantInitializeNetworkServiceProfileException {

        Location location;

        try {

            location = locationManager.getLastKnownLocation();

        } catch (CantGetDeviceLocationException exception) {

            location = null;
            // TODO MANAGE IN OTHER WAY...
            errorManager.reportUnexpectedPluginException(
                    this.getPluginVersionReference(),
                    UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN,
                    exception
            );
        }
        this.profile = new NetworkServiceProfile();

        this.profile.setIdentityPublicKey(this.identity.getPublicKey());
        this.profile.setNetworkServiceType(this.networkServiceType);
        this.profile.setLocation(location);

    }

    /**
     * This method initialize the database
     *
     * @throws CantInitializeNetworkServiceDatabaseException
     */
    private void initializeDataBase() throws CantInitializeNetworkServiceDatabaseException {

        try {
            /*
             * Open new database connection
             */
            this.networkServiceDatabase = this.pluginDatabaseSystem.openDatabase(pluginId, NetworkServiceDatabaseConstants.DATABASE_NAME);

        } catch (CantOpenDatabaseException cantOpenDatabaseException) {

            /*
             * The database exists but cannot be open. I can not handle this situation.
             */
            errorManager.reportUnexpectedPluginException(this.getPluginVersionReference(), UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, cantOpenDatabaseException);
            throw new CantInitializeNetworkServiceDatabaseException(cantOpenDatabaseException);

        } catch (DatabaseNotFoundException e) {

            /*
             * The database no exist may be the first time the plugin is running on this device,
             * We need to create the new database
             */
            NetworkServiceDatabaseFactory networkServiceDatabaseFactory = new NetworkServiceDatabaseFactory(pluginDatabaseSystem);

            try {

                /*
                 * We create the new database
                 */
                this.networkServiceDatabase = networkServiceDatabaseFactory.createDatabase(pluginId, NetworkServiceDatabaseConstants.DATABASE_NAME);

            } catch (CantCreateDatabaseException cantOpenDatabaseException) {

                /*
                 * The database cannot be created. I can not handle this situation.
                 */
                errorManager.reportUnexpectedPluginException(this.getPluginVersionReference(), UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, cantOpenDatabaseException);
                throw new CantInitializeNetworkServiceDatabaseException(cantOpenDatabaseException);

            }
        }

    }

    /**
     * Initializes all event listener and configure
     */
    private void initializeNetworkServiceListeners() {

        /*
         * 1. Listen and handle Network Client Registered Event
         */
        FermatEventListener networkClientRegistered = eventManager.getNewListener(P2pEventType.NETWORK_CLIENT_REGISTERED);
        networkClientRegistered.setEventHandler(new NetworkClientRegisteredEventHandler(this));
        eventManager.addListener(networkClientRegistered);
        listenersAdded.add(networkClientRegistered);

        /*
         * 2. Listen and handle Network Client Network Service Registered Event
         */
        FermatEventListener networkServiceProfileRegisteredListener = eventManager.getNewListener(P2pEventType.NETWORK_CLIENT_NETWORK_SERVICE_PROFILE_REGISTERED);
        networkServiceProfileRegisteredListener.setEventHandler(new NetworkClientNetworkServiceRegisteredEventHandler(this));
        eventManager.addListener(networkServiceProfileRegisteredListener);
        listenersAdded.add(networkServiceProfileRegisteredListener);

        /*
         * 3. Listen and handle Network Client Connection Closed Event
         */
        FermatEventListener connectionClosedListener = eventManager.getNewListener(P2pEventType.NETWORK_CLIENT_CONNECTION_CLOSED);
        connectionClosedListener.setEventHandler(new NetworkClientConnectionClosedEventHandler(this));
        eventManager.addListener(connectionClosedListener);
        listenersAdded.add(connectionClosedListener);

        /*
         * 4. Listen and handle Network Client Connection Lost Event
         */
        FermatEventListener connectionLostListener = eventManager.getNewListener(P2pEventType.NETWORK_CLIENT_CONNECTION_LOST);
        connectionLostListener.setEventHandler(new NetworkClientConnectionLostEventHandler(this));
        eventManager.addListener(connectionLostListener);
        listenersAdded.add(connectionLostListener);

        /*
         * 5. Listen and handle Actor Found Event
         */
        FermatEventListener actorFoundListener = eventManager.getNewListener(P2pEventType.NETWORK_CLIENT_ACTOR_FOUND);
        actorFoundListener.setEventHandler(new NetworkClientActorFoundEventHandler(this));
        eventManager.addListener(actorFoundListener);
        listenersAdded.add(actorFoundListener);

        /*
         * 6. Listen and handle Network Client Connection Success Event
         */
        FermatEventListener connectionSuccessListener = eventManager.getNewListener(P2pEventType.NETWORK_CLIENT_CONNECTION_SUCCESS);
        connectionSuccessListener.setEventHandler(new NetworkClientConnectionSuccessEventHandler(this));
        eventManager.addListener(connectionSuccessListener);
        listenersAdded.add(connectionSuccessListener);

        /*
         * 7. Listen and handle Network Client New Message Transmit Event
         */
        FermatEventListener newMessageTransmitListener = eventManager.getNewListener(P2pEventType.NETWORK_CLIENT_NEW_MESSAGE_TRANSMIT);
        newMessageTransmitListener.setEventHandler(new NetworkClientNewMessageTransmitEventHandler(this));
        eventManager.addListener(newMessageTransmitListener);
        listenersAdded.add(newMessageTransmitListener);

        /*
         * 8. Listen and handle Network Client Sent Message Delivered Event
         */
        FermatEventListener sentMessageDeliveredListener = eventManager.getNewListener(P2pEventType.NETWORK_CLIENT_SENT_MESSAGE_DELIVERED);
        sentMessageDeliveredListener.setEventHandler(new NetworkClientSentMessageDeliveredEventHandler(this));
        eventManager.addListener(sentMessageDeliveredListener);
        listenersAdded.add(sentMessageDeliveredListener);


    }

    public final void handleNetworkClientRegisteredEvent(final CommunicationChannels communicationChannel) throws FermatException {

        if(networkServiceRegistrationProcessAgent != null && networkServiceRegistrationProcessAgent.getActive()) {
            networkServiceRegistrationProcessAgent.stop();
            networkServiceRegistrationProcessAgent = null;
        }

        if (this.getConnection().isConnected() && this.getConnection().isRegistered())
            this.getConnection().registerProfile(this.getProfile());
        else {
            this.networkServiceRegistrationProcessAgent = new NetworkServiceRegistrationProcessAgent(this);
            this.networkServiceRegistrationProcessAgent.start();
        }

    }

    public final void handleActorFoundEvent(String uriToNode, ActorProfile actorProfile){

        listActorConnectIntoNode.put(actorProfile.getIdentityPublicKey(), uriToNode);

        if (listActorProfileConnectedInNode.get(uriToNode) != null)
            listActorProfileConnectedInNode.get(uriToNode).add(actorProfile);
        else {
            List<ActorProfile> actorList = new ArrayList<>();
            actorList.add(actorProfile);
            listActorProfileConnectedInNode.put(uriToNode, actorList);
        }

        // request connection to the Node external in the clientsConnectionsManager
        networkClientManager.requestConnectionToExternalNode(
                actorProfile.getIdentityPublicKey(),
                uriToNode
        );

    }

    /*
     * with this uriToNode we can get the NetworkClientCommunicationConnection
     * from the lit of the ClientsConnectionsManager to that connection specific
     */
    public final void handleNetworkClientConnectionSuccessEvent(String uriToNode){

        if(listActorProfileConnectedInNode.containsKey(uriToNode)) {

            getNetworkServiceConnectionManager().handleEstablishedRequestedNetworkServiceConnection(
                    listActorProfileConnectedInNode.get(uriToNode), uriToNode
            );
        }

    }

    /**
     * Notify the client when a incoming message is receive by the incomingTemplateNetworkServiceMessage
     * ant fire a new event
     *
     * @param incomingMessage received
     */
    public final void onMessageReceived(String incomingMessage) {

        try {

            NetworkServiceMessage networkServiceMessage = NetworkServiceMessage.parseContent(incomingMessage);

            networkServiceMessage.setContent(AsymmetricCryptography.decryptMessagePrivateKey(networkServiceMessage.getContent(), this.identity.getPrivateKey()));
            /*
             * process the new message receive
             */
            networkServiceConnectionManager.getNetworkServiceRoot().onNewMessageReceived(networkServiceMessage);

            networkServiceMessage.setFermatMessagesStatus(FermatMessagesStatus.READ);
            networkServiceConnectionManager.getIncomingMessagesDao().create(networkServiceMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final void handleNetworkServiceRegisteredEvent() {

        System.out.println("********** THIS NETWORK SERVICE HAS BEEN REGISTERED: " + this.getProfile());
        this.registered = Boolean.TRUE;
        onNetworkServiceRegistered();
    }

    protected void onNetworkServiceRegistered() {

    }

    protected void onNetworkServiceStart() throws CantStartPluginException {

    }

    /**
     * Handle the event NetworkClientConnectionLostEvent
     * @param communicationChannel
     */
    public final void handleNetworkClientConnectionLostEvent(final CommunicationChannels communicationChannel) {

        try {

            if(!networkClientManager.getConnection().isRegistered()) {

                if (networkServiceConnectionManager != null) {
                    networkServiceConnectionManager.stop();
                }


                this.registered = Boolean.FALSE;
/*
                reprocessMessages();
*/
                onNetworkClientConnectionLost();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * This method is automatically called when the client connection was lost
     */
    protected void onNetworkClientConnectionLost() {

    }

    /**
     * Handle the event NetworkClientConnectionClosedEvent
     * @param communicationChannel
     */
    public final void handleNetworkClientConnectionClosedEvent(final CommunicationChannels communicationChannel) {

        try {

            if(!networkClientManager.getConnection().isRegistered()) {

                if (networkServiceConnectionManager != null) {
                    networkServiceConnectionManager.closeAllConnection();
                    networkServiceConnectionManager.stop();
                }

                this.registered = Boolean.FALSE;

                networkServicePendingMessagesSupervisorAgent.removeAllConnectionWaitingForResponse();

                onNetworkClientConnectionClosed();

            }

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Method tha send a new Message
     */
    public void sendNewMessage(NetworkServiceProfile destination, String messageContent) throws CantSendMessageException {

        try {

            System.out.println("*** 12345 case 6:send msg in NS P2P layer not active connection" + new Timestamp(System.currentTimeMillis()));
            /*
             * Created the message
             */
            NetworkServiceMessage networkServiceMessage = NetworkServiceMessageFactory.buildNetworkServiceMessage(
                    this.getProfile(),
                    destination      ,
                    messageContent,
                    MessageContentType.TEXT
            );

            /*
             * Save to the data base table
             */
            networkServiceConnectionManager.getOutgoingMessagesDao().create(networkServiceMessage);

            /*
             * Ask the client to connect
             */
            networkServiceConnectionManager.connectTo(destination);

        }catch (Exception e){

            System.out.println("Error sending message: " + e.getMessage());
            throw new CantSendMessageException(e, "destination: "+destination+" - message: "+messageContent, "Unhandled error trying to send a message.");
        }
    }

    /**
     * Method tha send a new Message
     */
    public void sendNewMessage(ActorProfile sender, ActorProfile destination, String messageContent) throws CantSendMessageException {

        try {

            System.out.println("*** 12345 case 6:send msg in NS P2P layer not active connection" + new Timestamp(System.currentTimeMillis()));
            /*
             * Created the message
             */
            NetworkServiceMessage networkServiceMessage = NetworkServiceMessageFactory.buildNetworkServiceMessage(
                    sender           ,
                    destination      ,
                    this.getProfile(),
                    messageContent   ,
                    MessageContentType.TEXT
            );

            /*
             * Save to the data base table
             */
            networkServiceConnectionManager.getOutgoingMessagesDao().create(networkServiceMessage);

            /*
             * Ask the client to connect
             */
            networkServiceConnectionManager.connectTo(destination);

        }catch (Exception e){

            System.out.println("Error sending message: " + e.getMessage());
            throw new CantSendMessageException(e, "destination: "+destination+" - message: "+messageContent, "Unhandled error trying to send a message.");
        }
    }

    /**
     * This method is automatically called when the network service receive
     * a new message
     *
     * @param messageReceived
     */
    public synchronized void onNewMessageReceived(NetworkServiceMessage messageReceived) {

        System.out.println("Me llego un nuevo mensaje"+ messageReceived);
    }

    public synchronized void onSentMessage(NetworkServiceMessage networkServiceMessage) {

        System.out.println("Mensaje Delivered " + networkServiceMessage);

        //networkServiceMessage.setContent(AsymmetricCryptography.decryptMessagePrivateKey(networkServiceMessage.getContent(), this.identity.getPrivateKey()));

        networkServiceMessage.setFermatMessagesStatus(FermatMessagesStatus.DELIVERED);

        try {
            networkServiceConnectionManager.getOutgoingMessagesDao().update(networkServiceMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Get the database instance
     * @return Database
     */
    public Database getDataBase() {
        return this.networkServiceDatabase;
    }

    /**
     * This method is automatically called when the client connection was closed
     */
    protected void onNetworkClientConnectionClosed() {

    }

    public NetworkServiceConnectionManager getNetworkServiceConnectionManager() {
        return networkServiceConnectionManager;
    }

    /**
     * Get registered value
     *
     * @return boolean
     */
    public final boolean isRegistered() {
        return registered;
    }

    public final String getPublicKey() {

        return this.identity.getPublicKey();
    }

    public ECCKeyPair getIdentity() {

        return identity;
    }


    public final NetworkServiceProfile getProfile() {

        return profile;
    }

    public final NetworkClientConnection getConnection() {

        return networkClientManager.getConnection();
    }

    public final NetworkClientConnection getConnection(String uriToNode) {

        return networkClientManager.getConnection(uriToNode);
    }
}