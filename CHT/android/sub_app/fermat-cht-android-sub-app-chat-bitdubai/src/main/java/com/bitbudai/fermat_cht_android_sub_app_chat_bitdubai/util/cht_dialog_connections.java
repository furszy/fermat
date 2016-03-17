package com.bitbudai.fermat_cht_android_sub_app_chat_bitdubai.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.bitbudai.fermat_cht_android_sub_app_chat_bitdubai.adapters.ConnectionListAdapter;
import com.bitbudai.fermat_cht_android_sub_app_chat_bitdubai.adapters.ContactListAdapter;
import com.bitbudai.fermat_cht_android_sub_app_chat_bitdubai.adapters.DialogConnectionListAdapter;
import com.bitbudai.fermat_cht_android_sub_app_chat_bitdubai.fragments.ContactsListFragment;
import com.bitbudai.fermat_cht_android_sub_app_chat_bitdubai.models.ContactList;
import com.bitbudai.fermat_cht_android_sub_app_chat_bitdubai.sessions.ChatSession;
import com.bitbudai.fermat_cht_android_sub_app_chat_bitdubai.settings.ChatSettings;
import com.bitdubai.fermat_android_api.layer.definition.wallet.interfaces.FermatSession;
import com.bitdubai.fermat_android_api.layer.definition.wallet.views.FermatButton;
import com.bitdubai.fermat_android_api.layer.definition.wallet.views.FermatCheckBox;
import com.bitdubai.fermat_android_api.layer.definition.wallet.views.FermatTextView;
import com.bitdubai.fermat_android_api.ui.dialogs.FermatDialog;
import com.bitdubai.fermat_android_api.ui.interfaces.FermatWorkerCallBack;
import com.bitdubai.fermat_android_api.ui.util.FermatWorker;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.enums.Activities;
import com.bitdubai.fermat_api.layer.all_definition.settings.structure.SettingsManager;
import com.bitdubai.fermat_api.layer.dmp_engine.sub_app_runtime.enums.SubApps;
import com.bitdubai.fermat_cht_api.all_definition.exceptions.CantDeleteContactException;
import com.bitdubai.fermat_cht_api.all_definition.exceptions.CantGetChatException;
import com.bitdubai.fermat_cht_api.all_definition.exceptions.CantGetContactConnectionException;
import com.bitdubai.fermat_cht_api.all_definition.exceptions.CantGetContactException;
import com.bitdubai.fermat_cht_api.all_definition.exceptions.CantSaveChatException;
import com.bitdubai.fermat_cht_api.all_definition.exceptions.CantSaveContactException;
import com.bitdubai.fermat_cht_api.layer.middleware.interfaces.Chat;
import com.bitdubai.fermat_cht_api.layer.middleware.interfaces.Contact;
import com.bitdubai.fermat_cht_api.layer.middleware.interfaces.ContactConnection;
import com.bitdubai.fermat_cht_api.layer.middleware.utils.ContactImpl;
import com.bitdubai.fermat_cht_api.layer.sup_app_module.interfaces.ChatManager;
import com.bitdubai.fermat_cht_api.layer.sup_app_module.interfaces.ChatModuleManager;
import com.bitdubai.fermat_pip_api.layer.network_service.subapp_resources.SubAppResourcesProviderManager;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.enums.UnexpectedSubAppExceptionSeverity;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import com.bitdubai.fermat_cht_android_sub_app_chat_bitdubai.R;
import java.util.UUID;


/**
 * Created by Lozadaa on 05/03/16.
 */
public class cht_dialog_connections extends FermatDialog<FermatSession, SubAppResourcesProviderManager> implements View.OnClickListener {

    private final Activity activity;
    private static final String TAG = "cht_dialog_connections";
    private boolean mIsSearchResultView = false;
    private ChatManager chatManager;
    private ChatModuleManager moduleManager;
    private ErrorManager errorManager;
    private SettingsManager<ChatSettings> settingsManager;
    private ChatSession chatSession;
    public List<ContactConnection> contacts;
    ArrayList<String> contactname=new ArrayList<String>();
    ArrayList<Bitmap> contacticon=new ArrayList<>();
    ArrayList<UUID> contactid=new ArrayList<UUID>();
    private ArrayList<ContactConnection> contactConnectionList;
    ListView list;
    private AdapterCallbackContacts mAdapterCallback;
    FermatTextView txt_title,txt_body;
    TextView text;
    FermatButton btn_yes,btn_no;
    Button btn_add, btn_cancel;
    DialogConnectionListAdapter adapter;
    public cht_dialog_connections(Activity activity, FermatSession fermatSession, SubAppResourcesProviderManager resources,
                                  ChatManager chatManager, AdapterCallbackContacts mAdapterCallback) {
        super(activity, fermatSession, null);
        this.activity = activity;
        this.chatManager = chatManager;
        this.mAdapterCallback = mAdapterCallback;

    }

    public static interface AdapterCallbackContacts extends cht_dialog_yes_no.AdapterCallbackContacts {
        void onMethodCallbackContacts();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            chatSession=((ChatSession) getSession());
            moduleManager= chatSession.getModuleManager();
            chatManager=moduleManager.getChatManager();
            errorManager=getSession().getErrorManager();

        }catch (Exception e)
        {
            if(errorManager!=null)
                errorManager.reportUnexpectedSubAppException(SubApps.CHT_CHAT,UnexpectedSubAppExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT,e);
        }
        text=(TextView) findViewById(R.id.text);

        btn_add = (Button) findViewById(R.id.btn_add);
         setUpListeners();

        try {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
            FermatWorker worker = new FermatWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    return getMoreData();
                }
            };
            worker.setContext(getActivity());
            worker.setCallBack(new FermatWorkerCallBack() {
                @SuppressWarnings("unchecked")
                @Override
                public void onPostExecute(Object... result) {
                    if (result != null &&
                            result.length > 0) {
                        progressDialog.dismiss();
                        if (getActivity() != null && adapter != null) {
                            contactConnectionList = (ArrayList<ContactConnection>) result[0];
                            for (ContactConnection con : contactConnectionList) {
                                if(!con.getAlias().isEmpty() &&
                                        !con.getContactId().equals("") &&
                                        !con.getProfileImage().equals("")) {
                                    try {
                                        ByteArrayInputStream bytes = new ByteArrayInputStream(con.getProfileImage());
                                        BitmapDrawable bmd = new BitmapDrawable(bytes);
                                        if (bmd.getBitmap().getWidth() != 0) {
                                            contactname.add(con.getAlias());
                                            contactid.add(con.getContactId());
                                            contacticon.add(bmd.getBitmap());
                                        }
                                    }catch(Exception e){
                                        //errorManager.reportUnexpectedSubAppException(SubApps.CHT_CHAT, UnexpectedSubAppExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT, e);
                                        Log.i("CHT add contacts", "se ha ignorado contacto mal creado.");
                                    }
                                }
                            }

                            adapter=new DialogConnectionListAdapter(getActivity(), contactname, contacticon, contactid, errorManager);
                            if (contactConnectionList.isEmpty()) {
                                showEmpty(true, text);
                            } else {
                                showEmpty(false, text);
                            }
                        }
                    } else {
                        showEmpty(true, text);
                    }
                }

                @Override
                public void onErrorOccurred(Exception ex) {
                    progressDialog.dismiss();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
                    ex.printStackTrace();
                }
            });
            worker.execute();


//
//            List<ContactConnection> con = chatManager.getContactConnections();
//
//            int size = con.size();
//            if (size > 0) {
//                for (int i = 0; i < size; i++) {
//                    if(!con.get(i).getAlias().isEmpty() && !con.get(i).getContactId().equals("") && !con.get(i).getProfileImage().equals("")) {
//                        try {
//                            ByteArrayInputStream bytes = new ByteArrayInputStream(con.get(i).getProfileImage());
//                            BitmapDrawable bmd = new BitmapDrawable(bytes);
//                            if (bmd.getBitmap().getWidth() != 0) {
//                                contactname.add(con.get(i).getAlias());
//                                contactid.add(con.get(i).getContactId());
//                                contacticon.add(bmd.getBitmap());
//                            }
//                        }catch(Exception e){
//                            //errorManager.reportUnexpectedSubAppException(SubApps.CHT_CHAT, UnexpectedSubAppExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT, e);
//                            Log.i("CHT add contacts", "se ha ignorado contacto mal creado.");
//                        }
//                    }
//                }
//                text.setVisibility(View.GONE);
//            } else {
//                text.setVisibility(View.VISIBLE);
//                text.setText("No Connections");
//            }

        }catch (Exception e){
            if (errorManager != null)
                errorManager.reportUnexpectedSubAppException(SubApps.CHT_CHAT, UnexpectedSubAppExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT, e);
        }


        list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Contact contactexist = chatSession.getSelectedContactToUpdate();
                    if (contactexist != null) {
                        if (contactexist.getRemoteActorPublicKey().equals("CONTACTTOUPDATE_DATA")) {
                            UUID contactidnew = contactexist.getContactId();
                            contactexist = chatManager.getContactByContactId(contactid.get(position));
                            Chat chat = chatManager.getChatByChatId((UUID)  getSession().getData("chatid"));
                            chat.setRemoteActorPublicKey(contactexist.getRemoteActorPublicKey());
                            chatManager.saveChat(chat);
                            Contact contactnew = new ContactImpl();
                            contactnew = chatManager.getContactByContactId(contactidnew);
                            contactnew.setRemoteActorPublicKey(contactexist.getRemoteActorPublicKey());
                            contactnew.setAlias(contactexist.getAlias());
                            contactnew.setRemoteName(contactexist.getRemoteName());
                            contactnew.setRemoteActorType(contactexist.getRemoteActorType());
                            chatManager.saveContact(contactnew);
                            Contact deleteContact;
                            for (int i = 0; i < chatManager.getContacts().size(); i++) {
                                deleteContact = chatManager.getContacts().get(i);
                                if (deleteContact.getRemoteName().equals("Not registered contact")) {
                                    if (deleteContact.getContactId().equals(contactidnew)) {
                                        chatManager.deleteContact(deleteContact);
                                    }
                                }

                            }
                            chatManager.deleteContact(contactexist);
                            getSession().setData(ChatSession.CONTACTTOUPDATE_DATA, null);
                            getSession().setData("whocallme", "contact");
                            getSession().setData(ChatSession.CONTACT_DATA, chatManager.getContactByContactId(contactidnew));
                            Toast.makeText(getActivity(), "Connection added as Contact", Toast.LENGTH_SHORT).show();
                          //  changeActivity(Activities.CHT_CHAT_OPEN_MESSAGE_LIST, getSession().getAppPublicKey());
                            dismiss();

                        }
                    } else {
                        final int pos = position;
                        final ContactConnection contactConn = chatManager.getContactConnectionByContactId(contactid.get(pos));

                        if (contactConn.getRemoteName() != null) {
                            cht_dialog_yes_no customAlert = new cht_dialog_yes_no(getActivity(),getSession(),null,contactConn, mAdapterCallback);
                            customAlert.setTextBody("Do you want to add " + contactConn.getRemoteName() + " to your Contact List?");
                           customAlert.setTextTitle("Add connections");
                            customAlert.setType("add-connections");
                            customAlert.show();
                        } else {
                            //changeActivity(Activities.CHT_CHAT_OPEN_CONTACTLIST, appSession.getAppPublicKey());
                            dismiss();
                        }
                    }
                } catch (CantSaveChatException e) {
                    errorManager.reportUnexpectedSubAppException(SubApps.CHT_CHAT, UnexpectedSubAppExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT, e);
                } catch (CantDeleteContactException e) {
                    errorManager.reportUnexpectedSubAppException(SubApps.CHT_CHAT, UnexpectedSubAppExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT, e);
                } catch (CantSaveContactException e) {
                    errorManager.reportUnexpectedSubAppException(SubApps.CHT_CHAT, UnexpectedSubAppExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT, e);
                } catch (CantGetContactException e) {
                    errorManager.reportUnexpectedSubAppException(SubApps.CHT_CHAT, UnexpectedSubAppExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT, e);
                } catch (Exception e) {
                    errorManager.reportUnexpectedSubAppException(SubApps.CHT_CHAT, UnexpectedSubAppExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT, e);
                }
            }
        });


    }


    protected int setLayoutId() {
            return R.layout.cht_dialog_connections;
    }

    private void setUpListeners() {
        btn_add.setOnClickListener(this);
        }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_add) {
            dismiss();
        }
    }
    @Override
    protected int setWindowFeature() {
        return Window.FEATURE_NO_TITLE;
    }

    private synchronized List<ContactConnection> getMoreData() {
        List<ContactConnection> dataSet = new ArrayList<>();

        try {
            List<ContactConnection> result = chatManager.discoverActorsRegistered();//moduleManager.listWorldCryptoBrokers(moduleManager.getSelectedActorIdentity(), MAX, offset);
            dataSet.addAll(result);
            //offset = dataSet.size();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataSet;
    }

    public void showEmpty(boolean show, TextView text) {
        if (show &&
                (text.getVisibility() == View.GONE || text.getVisibility() == View.INVISIBLE)) {
            text.setVisibility(View.VISIBLE);
            text.setText("No Connections");
            if (adapter != null)
                adapter.refreshEvents(null, null, null);
        } else if (!show && text.getVisibility() == View.VISIBLE) {
            text.setVisibility(View.GONE);
        }
    }
//
//    public void showEmpty(boolean show, View emptyView) {
//        Animation anim = AnimationUtils.loadAnimation(getActivity(),
//                show ? android.R.anim.fade_in : android.R.anim.fade_out);
//        if (show &&
//                (emptyView.getVisibility() == View.GONE || emptyView.getVisibility() == View.INVISIBLE)) {
//            emptyView.setAnimation(anim);
//            emptyView.setVisibility(View.VISIBLE);
//            if (adapter != null)
//                adapter.refreshEvents(null, null, null);
//        } else if (!show && emptyView.getVisibility() == View.VISIBLE) {
//            emptyView.setAnimation(anim);
//            emptyView.setVisibility(View.GONE);
//        }
//    }

}
