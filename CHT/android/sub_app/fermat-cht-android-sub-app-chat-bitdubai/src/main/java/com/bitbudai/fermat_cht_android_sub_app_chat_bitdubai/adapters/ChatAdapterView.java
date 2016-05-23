package com.bitbudai.fermat_cht_android_sub_app_chat_bitdubai.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.bitbudai.fermat_cht_android_sub_app_chat_bitdubai.models.ChatMessage;
import com.bitbudai.fermat_cht_android_sub_app_chat_bitdubai.sessions.ChatSession;
import com.bitbudai.fermat_cht_android_sub_app_chat_bitdubai.util.ConstantSubtitle;
import com.bitbudai.fermat_cht_android_sub_app_chat_bitdubai.util.Utils;
import com.bitdubai.fermat_android_api.layer.definition.wallet.interfaces.FermatSession;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.ErrorManager;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.error_manager.enums.UnexpectedSubAppExceptionSeverity;
import com.bitdubai.fermat_android_api.ui.util.FermatWorker;
import com.bitdubai.fermat_api.layer.all_definition.components.enums.PlatformComponentType;
import com.bitdubai.fermat_api.layer.all_definition.util.Validate;
import com.bitdubai.fermat_api.layer.dmp_engine.sub_app_runtime.enums.SubApps;
import com.bitdubai.fermat_cht_android_sub_app_chat_bitdubai.R;
import com.bitdubai.fermat_cht_api.all_definition.enums.ChatStatus;
import com.bitdubai.fermat_cht_api.all_definition.enums.MessageStatus;
import com.bitdubai.fermat_cht_api.all_definition.enums.TypeChat;
import com.bitdubai.fermat_cht_api.all_definition.enums.TypeMessage;
import com.bitdubai.fermat_cht_api.all_definition.exceptions.CHTException;
import com.bitdubai.fermat_cht_api.all_definition.exceptions.CantGetChatException;
import com.bitdubai.fermat_cht_api.all_definition.exceptions.CantGetMessageException;
import com.bitdubai.fermat_cht_api.all_definition.exceptions.CantGetNetworkServicePublicKeyException;
import com.bitdubai.fermat_cht_api.all_definition.exceptions.CantGetOnlineStatus;
import com.bitdubai.fermat_cht_api.all_definition.exceptions.CantGetWritingStatus;
import com.bitdubai.fermat_cht_api.all_definition.exceptions.CantSaveChatException;
import com.bitdubai.fermat_cht_api.all_definition.exceptions.CantSaveMessageException;
import com.bitdubai.fermat_cht_api.layer.middleware.interfaces.Chat;
import com.bitdubai.fermat_cht_api.layer.middleware.interfaces.Contact;
import com.bitdubai.fermat_cht_api.layer.middleware.interfaces.Message;
import com.bitdubai.fermat_cht_api.layer.middleware.utils.ChatImpl;
import com.bitdubai.fermat_cht_api.layer.middleware.utils.MessageImpl;
import com.bitdubai.fermat_cht_api.layer.sup_app_module.interfaces.ChatManager;
import com.bitdubai.fermat_cht_api.layer.sup_app_module.interfaces.ChatModuleManager;
import com.bitdubai.fermat_cht_api.layer.sup_app_module.interfaces.ChatPreferenceSettings;

import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.error_manager.enums.UnexpectedSubAppExceptionSeverity;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.ErrorManager;

import com.bitdubai.fermat_api.layer.all_definition.util.Validate;

import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Created by miguel on 22/01/16.
 * Updated by Jose Cardozo josejcb (josejcb89@gmail.com) on 09/01/16.
 */

public class ChatAdapterView extends LinearLayout {

    private RecyclerView messagesContainer;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;
    private ChatManager chatManager;
    private ChatModuleManager moduleManager;
    private ErrorManager errorManager;
    private ChatSession chatSession;
    private ChatPreferenceSettings chatSettings;
    private FermatSession appSession;
    private Toolbar toolbar;
    private Button sendBtn;
    private EditText messageET;
    private ViewGroup rootView;
    private String leftName;
    private String rightName;
    private UUID chatId;
    private String contactId;
    private int background = -1;
    private String remotePk;
    private PlatformComponentType remotePCT;
    private Bitmap contactIcon;
    private BitmapDrawable contactIconCircular;
    private boolean loadDummyData = false;
    private boolean chatWasCreate = false;
    private Calendar today;
    UUID newChatId;
    int CounterText;
    Boolean isOnline = false;
    Boolean textNeverChange = false;
    static final int TIME_TO_REFRESH_TOOLBAR = 6000;
    public ChatAdapterView(Context context, ArrayList<ChatMessage> chatHistory,
                           ChatManager chatManager, ChatModuleManager moduleManager,
                           ErrorManager errorManager, ChatSession chatSession, FermatSession appSession, int background, Toolbar toolbar, ChatPreferenceSettings chatSettings) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        addView(inflater.inflate(R.layout.chat, (rootView != null) ? rootView : null));
        this.chatHistory = chatHistory;
        this.chatManager = chatManager;
        this.moduleManager = moduleManager;
        this.errorManager = errorManager;
        this.chatSession = chatSession;
        this.appSession = appSession;
        this.toolbar = toolbar;
        this.chatSettings = chatSettings;
        //this.background=background;
        initControls();
    }

    public ChatAdapterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChatAdapterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ChatAdapterView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    void findValues(Contact contact) { //With contact Id find chatId,pkremote,actortype
        try {
            if (contact != null) {
                remotePk = contact.getRemoteActorPublicKey();
                remotePCT = PlatformComponentType.ACTOR_CHAT;
                contactId = contact.getRemoteActorPublicKey();
                ByteArrayInputStream bytes = new ByteArrayInputStream(contact.getProfileImage());
                BitmapDrawable bmd = new BitmapDrawable(bytes);
                contactIcon = bmd.getBitmap();
                leftName = contact.getAlias();
                Chat cht = chatManager.getChatByRemotePublicKey(remotePk);
                try {
                    chatManager.activeOnlineStatus(remotePk);
                } catch (CantGetOnlineStatus cantGetOnlineStatus) {
                    cantGetOnlineStatus.printStackTrace();
                }
                if (cht != null){
                    chatId = cht.getChatId();
                    appSession.setData(ChatSession.CHAT_DATA, chatManager.getChatByChatId(chatId));
                }
                else chatId = null;
            }
        } catch (CantGetChatException e) {
            errorManager.reportUnexpectedSubAppException(SubApps.CHT_CHAT, UnexpectedSubAppExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT, e);
        } catch (Exception e) {
            errorManager.reportUnexpectedSubAppException(SubApps.CHT_CHAT, UnexpectedSubAppExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT, e);
        }
    }

    public void whatToDo() {
        try {
            //System.out.println("WHOCALME NOW:" + chatSession.getData("whocallme"));
            findValues(chatSession.getSelectedContact());
            if (chatSession.getData("whocallme").equals("chatlist")) {
                //if I choose a chat, this will retrieve the chatId
                chatWasCreate = true;
            } else if (chatSession.getData("whocallme").equals("contact")) {  //fragment contact call this fragment
                //if I choose a contact, this will search the chat previously created with this contact
                //Here it is define if we need to create a new chat or just add the message to chat created previously
                chatWasCreate = chatId != null;
            }
        } catch (Exception e) {
            errorManager.reportUnexpectedSubAppException(SubApps.CHT_CHAT, UnexpectedSubAppExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT, e);
        }
    }

    public void findMessage() {
        String message;
        String inorout;
        String estatus;
        ChatMessage msg;
        //Chat chat;
        try {
            setChatHistory(null);
            chatHistory = null;
//            if(chatId !=null){
//                chat=chatManager.getChatByChatId(chatId);
//            }else{
//                chat=chatSession.getSelectedChat();
//            }
//
//            if(chat!=null)
//                chatId =chat.getChatId();
            if (chatHistory == null) {
                chatHistory = new ArrayList<ChatMessage>();
            }

            if (chatId != null) {
                List<Message> messL = chatManager.getMessagesByChatId(chatId);
                if(messL!=null) {
                    MessageImpl messagei;
                    for (Message mess : messL) {
                        msg = new ChatMessage();
                        message = mess.getMessage();
                        inorout = mess.getType().toString();
                        estatus = mess.getStatus().toString();
                        msg.setId(mess.getMessageId());
                        if (inorout == TypeMessage.OUTGOING.toString()) msg.setMe(true);
                        else {
                            msg.setMe(false);
                            if (estatus != MessageStatus.READ.toString()) {
                                messagei = (MessageImpl) chatManager.getMessageByMessageId(msg.getId());
                                msg.setStatus(MessageStatus.READ.toString());
                                messagei.setStatus(MessageStatus.READ);
                                chatManager.saveMessage(messagei);
                                chatManager.sendReadMessageNotification(messagei);
                            }
                        }
                        msg.setStatus(mess.getStatus().toString());
                        long timemess = mess.getMessageDate().getTime();
                        long nanos = (mess.getMessageDate().getNanos() / 1000000);
                        long milliseconds = timemess + nanos;
                        Date dated = new java.util.Date(milliseconds);
                        DateFormat formatter = DateFormat.getDateTimeInstance();
                        if (android.text.format.DateFormat.is24HourFormat(getContext())) {
                            formatter= new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
                        } else {
                            formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                        }
                        if (Validate.isDateToday(dated)) {
                            if (android.text.format.DateFormat.is24HourFormat(getContext())) {
                                formatter = new SimpleDateFormat("HH:mm");
                            } else {
                                formatter = new SimpleDateFormat("hh:mm aa");
                            }
                        }
                        formatter.setTimeZone(TimeZone.getDefault());
                        msg.setDate(formatter.format(new java.util.Date(milliseconds)));
                        msg.setUserId(mess.getContactId());
                        msg.setMessage(message);
                        msg.setType(mess.getType().toString());
                        chatHistory.add(msg);
                    }
                }
                adapter = new ChatAdapter(this.getContext(), (chatHistory != null) ? chatHistory : new ArrayList<ChatMessage>());
                messagesContainer.setAdapter(adapter);
           }
        } catch (CantGetMessageException e) {
            errorManager.reportUnexpectedSubAppException(SubApps.CHT_CHAT, UnexpectedSubAppExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT, e);
        } catch (Exception e) {
            errorManager.reportUnexpectedSubAppException(SubApps.CHT_CHAT, UnexpectedSubAppExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT, e);
        }
    }
    public class BackgroundAsyncTaskWriting extends
            AsyncTask<Void, Integer, Void> {

        int myProgress;

        @Override
        protected void onPostExecute(Void result) {
            //this.cancel(true);
            return;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                try {
                    chatManager.sendWritingStatus(chatId);
                } catch (CHTException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class BackgroundAsyncTask extends
            AsyncTask<Message, Integer, Message> {

        int myProgress;

        @Override
        protected void onPostExecute(Message result) {
            //this.cancel(true);
            return;
        }

        @Override
        protected Message doInBackground(Message... params) {
            try {
                for (Message param : params) {
                    chatManager.sendMessage(param);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                onBackPressed();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onBackPressed() {
        final int actualHeight = getHeight();
        FrameLayout.LayoutParams layoutParams =
                (FrameLayout.LayoutParams) messagesContainer.getLayoutParams();
        layoutParams.height = 764;
        messagesContainer.setLayoutParams(layoutParams);
    }

    public void onAdjustKeyboard() {
        FrameLayout.LayoutParams layoutParams =
                (FrameLayout.LayoutParams) messagesContainer.getLayoutParams();
        layoutParams.height = 440;
        messagesContainer.setLayoutParams(layoutParams);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                onBackPressed();
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private boolean isKeyboardShown(View rootView) {
        final int SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD = 128;
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        int heightDiff = rootView.getBottom() - r.bottom;
        boolean isKeyboardShown = heightDiff > SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD * dm.density;
        return isKeyboardShown;
    }

    public String setFormatLastTime(String date){
        String fecha = date;
        SimpleDateFormat formatter;
        String formattedTime;
        if(android.text.format.DateFormat.is24HourFormat(getContext())) {
            formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        } else {
            formatter= new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
        }
        try {
            formatter.setTimeZone(TimeZone.getDefault());

             formattedTime = formatter.format(new java.util.Date(date));
            //String formattedTime = formatter.format(dater);
            if(date.length() > 16) {
                 formattedTime = formattedTime.substring(11, 19);
            }else {
                 formattedTime = formattedTime.substring(11, 16);
            }

            if(Validate.isDateToday(new java.util.Date(date))){
                    fecha = "today at "+formattedTime;
                }else{
                    Date today = new Date();
                    long dias = (today.getTime() - new java.util.Date(date).getTime()) / (1000 * 60 * 60 * 24);
                    if(dias == 1){
                        fecha = "yesterday at "+formattedTime;
                    }
                }
        }catch(Exception e){
            Log.e("ErrorOnSetFormatLastTim", e.getMessage(),e);
        }

        return fecha;
    }
    public void ChangeStatusOnTheSubtitleBar(int state, String date) {
        switch (state) {
            case ConstantSubtitle.IS_OFFLINE:
                if(date != null && date != "no record") {
                    toolbar.setSubtitle(Html.fromHtml("<small><small>Last time "+setFormatLastTime(date)+"</small></small>"));
                }else{
                    Log.i("159753**LastTimeOnChat", "No show");
                }
                break;
            case ConstantSubtitle.IS_ONLINE:
                toolbar.setSubtitle("Online");
                break;

            case ConstantSubtitle.IS_WRITING:
                // toolbar.setSubtitleTextColor(Color.parseColor("#fff"));
                toolbar.setSubtitle("Typing...");
                break;
        }
    }

    public void initControls() {
        messagesContainer = (RecyclerView) findViewById(R.id.messagesContainer);
        messagesContainer.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (Button) findViewById(R.id.chatSendButton);
        messageET.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                            if (!isKeyboardShown(messageET.getRootView())) {
                                onBackPressed();
                            } else onAdjustKeyboard();
                        }
                    }
                });

        messageET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (chatWasCreate) {
                    CounterText++;
                    if(CounterText == 5){
                        BackgroundAsyncTaskWriting batw = new BackgroundAsyncTaskWriting();
                        batw.execute();
                        CounterText = 0;
                    }
                    if (messageET.length() > 0 && textNeverChange == false) {
                        BackgroundAsyncTaskWriting batw = new BackgroundAsyncTaskWriting();
                        batw.execute();
                        textNeverChange = true;
                    } else if (messageET.length() == 0 && textNeverChange == true) {
                        textNeverChange = false;
                        BackgroundAsyncTaskWriting batw = new BackgroundAsyncTaskWriting();
                        batw.execute();

                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        //adapter = new ChatAdapter(getContext(), (chatHistory != null) ? chatHistory : new ArrayList<ChatMessage>());
        //messagesContainer.setAdapter(adapter);
        //messageET.setText("Type message");
        //TextView meLabel = (TextView) findViewById(R.id.meLbl);
        //TextView companionLabel = (TextView) findViewById(R.id.friendLabel);
        //ScrollView container = (ScrollView) findViewById(R.id.container);

        if (chatSession != null) {
            whatToDo();
            findMessage();
            scroll();
            checkStatus();
//        if (rightName != null) {
//            meLabel.setText(rightName);
//        } else {
//            meLabel.setText("");
//        }

            if (leftName != null) {
                toolbar.setTitle(leftName);
                contactIconCircular = new BitmapDrawable(getResources(), Utils.getRoundedShape(contactIcon, 100));
                toolbar.setLogo(contactIconCircular);
            }
        }
        //companionLabel.setText(leftName);
//        } else {
//            companionLabel.setText("Contacto");
//        }

        //if (background != -1) {
        //    container.setBackgroundColor(background);
        //}

        messageET.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //messageET.setText("");
            }
        });

//        messageET.setOnTouchListener(new OnTouchListener() {
//            public boolean onTouch(View view, MotionEvent event) {
//                // TODO Auto-generated method stub
//                if (view.getId() == R.id.messageEdit) {
//                    view.getParent().requestDisallowInterceptTouchEvent(true);
//                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
//                        case MotionEvent.ACTION_UP:
//                            view.getParent().requestDisallowInterceptTouchEvent(false);
//                            break;
//                    }
//                }
//                return false;
//            }
//        });

        sendBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BackgroundAsyncTask sendMessageAsync = new BackgroundAsyncTask();
                String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText) || messageText.trim().length()==0) {
                    return;
                }

                messageText = messageText.trim();

                try {
                    ChatImpl chat = new ChatImpl();
                    final MessageImpl message = new MessageImpl();
                    Long dv = System.currentTimeMillis();
                    String remotePublicKey;
                    if (chatWasCreate) {
                        chat = (ChatImpl) chatManager.getChatByChatId(chatId);
                        chat.setLastMessageDate(new Timestamp(dv));
                        remotePublicKey = chat.getRemoteActorPublicKey();
                        Chat chatPrevious = chatManager.getChatByRemotePublicKey(remotePublicKey);

                        if (chatPrevious.getChatId() != chatId) {
                            newChatId = chatPrevious.getChatId();
                        } else {
                            newChatId = chatId;
                        }
                        chat.setChatId(newChatId);
                        chatManager.saveChat(chat);

                        message.setChatId(newChatId);
                        message.setMessageId(UUID.randomUUID());
                        message.setMessage(messageText);
                        message.setMessageDate(new Timestamp(dv));
                        message.setStatus(MessageStatus.CREATED);
                        message.setType(TypeMessage.OUTGOING);
                        message.setContactId(UUID.randomUUID());
                        chatManager.saveMessage(message);
                        sendMessageAsync.execute(message);
                    } else {
                        Contact newContact = chatSession.getSelectedContact();
                        remotePublicKey = newContact.getRemoteActorPublicKey();
                        chat.setRemoteActorType(PlatformComponentType.ACTOR_CHAT);//chat.setRemoteActorType(remoteActorType);
                        chat.setRemoteActorPublicKey(remotePublicKey);
                        Chat chatPrevious = chatManager.getChatByRemotePublicKey(remotePublicKey);
                        if(newChatId == null) {
                            if (chatPrevious != null) {
                                newChatId = chatPrevious.getChatId();
                            } else {
                                newChatId = UUID.randomUUID();
                            }
                        }
                        chat.setChatId(newChatId);
                        chat.setObjectId(UUID.randomUUID());
                        chat.setStatus(ChatStatus.VISSIBLE);
                        chat.setChatName("Chat_" + newContact.getAlias());
                        chat.setDate(new Timestamp(dv));
                        chat.setLastMessageDate(new Timestamp(dv));
                        chat.setTypeChat(TypeChat.INDIVIDUAL);
                        /**
                         * Now we got the identities registered in the device.
                         * To avoid nulls, I'll put default data in chat object
                         *///
                        chat.setLocalActorPublicKey(chatManager.getNetworkServicePublicKey());
                        chat.setLocalActorType(PlatformComponentType.NETWORK_SERVICE);
                        //if (chatSettings.getLocalPublicKey() != null /*&& chatSettings.getLocalPlatformComponentType() != null*/) {
                        //Asigno pk del usuario y no uso la del NS
                        try {
                            String pKey = chatSettings.getLocalPublicKey();
                            if (pKey != null) {
                                chat.setLocalActorPublicKey(pKey);
                            } else {
                                chat.setLocalActorPublicKey(chatManager.getIdentityChatUsersFromCurrentDeviceUser().get(0).getPublicKey());
                            }
                            chat.setLocalActorType(PlatformComponentType.ACTOR_CHAT);
                        } catch (Exception e) {
                            chat.setLocalActorPublicKey(chatManager.getIdentityChatUsersFromCurrentDeviceUser().get(0).getPublicKey());
                            chat.setLocalActorType(PlatformComponentType.ACTOR_CHAT);
                        }
                        chatManager.saveChat(chat);

                        message.setChatId(newChatId);
                        message.setMessageId(UUID.randomUUID());
                        message.setMessage(messageText);
                        message.setMessageDate(new Timestamp(dv));
                        message.setStatus(MessageStatus.CREATED);
                        message.setType(TypeMessage.OUTGOING);
                        message.setContactId(UUID.randomUUID());//message.setContactId(contactId);
                        chatManager.saveMessage(message);
                        sendMessageAsync.execute(message);//
                        //If everything goes OK, we save the chat in the fragment session.
                        chatSession.setData("whocallme", "chatlist");
                        chatSession.setData(
                                "contactid",
                                newContact
                        );
                        /**
                         * This chat was created, so, I will put chatWasCreate as true to avoid
                         * the multiple chats from this contact. Also I will put the chatId as
                         * newChatId
                         */
                        chatWasCreate = true;
                        chatId = newChatId;
                    }

                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setId(UUID.randomUUID());//dummy
                    chatMessage.setMessage(messageText);
                    String S = new SimpleDateFormat("HH:mm").format(new Date());
                    chatMessage.setDate(S);
                    chatMessage.setMe(true);
                    messageET.setText("");
                    adapter = new ChatAdapter(getContext(), (chatHistory != null) ? chatHistory : new ArrayList<ChatMessage>());
                    messagesContainer.setAdapter(adapter);
                    displayMessage(chatMessage);
                    System.out.println("*** 12345 case 1:send msg in android layer" + new Timestamp(System.currentTimeMillis()));
                } catch (CantSaveMessageException e) {
                    errorManager.reportUnexpectedSubAppException(SubApps.CHT_CHAT, UnexpectedSubAppExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT, e);
                } catch (CantSaveChatException e) {
                    errorManager.reportUnexpectedSubAppException(SubApps.CHT_CHAT, UnexpectedSubAppExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT, e);
                } catch (CantGetNetworkServicePublicKeyException e) {
                    errorManager.reportUnexpectedSubAppException(SubApps.CHT_CHAT, UnexpectedSubAppExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT, e);
                } catch (Exception e) {
                    errorManager.reportUnexpectedSubAppException(SubApps.CHT_CHAT, UnexpectedSubAppExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT, e);
                }
            }
        });
/*
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT).show();
                        try {
                            findMessage();
                        } catch (Exception e) {
                            errorManager.reportUnexpectedSubAppException(SubApps.CHT_CHAT, UnexpectedSubAppExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT, e);
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 2500);
            }
        });*/
    }

    public void getFilter(String s) {
        adapter.getFilter().filter(s);
    }

    public void displayMessage(ChatMessage message) {
        adapter.addItem(message);
        adapter.notifyDataSetChanged();
        scroll();
    }

    public void refreshEvents() {
        //whatToDo();
        findValues(chatSession.getSelectedContact());
        findMessage();
        checkStatus();
        scroll();
    }

    public void checkStatus(){
        try {
           if(chatSession.getData("whocallme").equals("chatlist")) {
               if (chatManager.checkWritingStatus(chatId)) {
                   ChangeStatusOnTheSubtitleBar(ConstantSubtitle.IS_WRITING, null);
               } else if(chatManager.checkOnlineStatus(remotePk)){
                   ChangeStatusOnTheSubtitleBar(ConstantSubtitle.IS_ONLINE, null);
               }else{
                   String date = chatManager.checkLastConnection(remotePk);
                       ChangeStatusOnTheSubtitleBar(ConstantSubtitle.IS_OFFLINE, date);
               }
           }else {
               if(chatManager.checkOnlineStatus(remotePk)){
                   ChangeStatusOnTheSubtitleBar(ConstantSubtitle.IS_ONLINE, null);
               }else{
                   String date = chatManager.checkLastConnection(remotePk);
                       ChangeStatusOnTheSubtitleBar(ConstantSubtitle.IS_OFFLINE, date);
               }
           }
        } catch (CantGetWritingStatus cantGetWritingStatus) {
            cantGetWritingStatus.printStackTrace();
        } catch (CantGetOnlineStatus cantGetOnlineStatus) {
            cantGetOnlineStatus.printStackTrace();
        }
    }

    private void scroll() {
        if (adapter != null && adapter.getItemCount() != 0 && adapter.getItemCount() > 0)
            messagesContainer.scrollToPosition(adapter.getItemCount() - 1);
    }

    public void setRootView(ViewGroup rootView) {
        this.rootView = rootView;
    }

    public void addLeftName(String leftName) {
        this.leftName = leftName;
    }

    public void addRightName(String rightName) {
        this.rightName = rightName;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    private void setChatHistory(ArrayList<ChatMessage> chatHistory) {
        this.chatHistory = chatHistory;
    }

    private void setChatManager(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    private void setModuleManager(ChatModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    private void setErrorManager(ErrorManager errorManager) {
        this.errorManager = errorManager;
    }

    private void setChatSession(ChatSession chatSession) {
        this.chatSession = chatSession;
    }

    private void setChatSettings(ChatPreferenceSettings chatSettings) {
        this.chatSettings = chatSettings;
    }

    private void setAppSession(FermatSession appSession) {
        this.appSession = appSession;
    }

    private void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    private void loadDummyHistory(boolean loadDummyData) {
        this.loadDummyData = loadDummyData;
    }

    public static class Builder {

        private Context context;
        private ViewGroup rootView;
        private ArrayList<ChatMessage> chatHistory;
        private ChatManager chatManager;
        private ChatModuleManager moduleManager;
        private ErrorManager errorManager;
        private ChatSession chatSession;
        private ChatPreferenceSettings chatSettings;
        private FermatSession appSession;
        private Toolbar toolbar;
        private boolean loadDummyData = false;
        private int background = -1;
        private float chatTextSize;
        private int chatTextColor;
        private float dateTextSize;
        private int dateTextColor;
        String rightName;
        String leftName;
        Button customButtom;
        EditText editText;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder insertInto(ViewGroup rootView) {
            this.rootView = rootView;
            return this;
        }

        public Builder addRightName(String rightName) {
            this.rightName = rightName;
            return this;
        }

        public Builder addLeftName(String leftName) {
            this.leftName = leftName;
            return this;
        }

        public void addCustomSendButtom(Button customButtom) {
            this.customButtom = customButtom;
        }

        public void addCustomEditText(EditText customEditText) {
            this.editText = customEditText;
        }

        public Builder setBackground(int background) {
            this.background = background;
            return this;
        }

        public Builder addChatHistory(ArrayList<ChatMessage> chatHistory) {
            this.chatHistory = chatHistory;
            return this;
        }

        public Builder addChatManager(ChatManager chatManager) {
            this.chatManager = chatManager;
            return this;
        }

        public Builder addModuleManager(ChatModuleManager moduleManager) {
            this.moduleManager = moduleManager;
            return this;
        }

        public Builder addErrorManager(ErrorManager errorManager) {
            this.errorManager = errorManager;
            return this;
        }

        public Builder addChatSession(ChatSession chatSession) {
            this.chatSession = chatSession;
            return this;
        }

        public Builder addChatSettings(ChatPreferenceSettings chatSettings) {
            this.chatSettings = chatSettings;
            return this;
        }

        public Builder addToolbar(Toolbar toolbar) {
            this.toolbar = toolbar;
            return this;
        }

        public Builder addAppSession(FermatSession appSession) {
            this.appSession = appSession;
            return this;
        }

        public void loadDummyData(boolean loadDummyData) {
            this.loadDummyData = loadDummyData;
        }

        public ChatAdapterView build() {
            ChatAdapterView chatView = new ChatAdapterView(context, chatHistory,
                    chatManager, moduleManager, errorManager, chatSession, appSession, background, toolbar, chatSettings);
            if (rootView != null) {
                chatView.setRootView(rootView);
            }
            if (chatHistory != null) {
                chatView.setChatHistory(chatHistory);
            }
            if (chatManager != null) {
                chatView.setChatManager(chatManager);
            }
            if (moduleManager != null) {
                chatView.setModuleManager(moduleManager);
            }
            if (errorManager != null) {
                chatView.setErrorManager(errorManager);
            }
            if (chatSettings != null) {
                chatView.setChatSettings(chatSettings);
            }
            if (chatSession != null) {
                chatView.setChatSession(chatSession);
            }
            if (appSession != null) {
                chatView.setAppSession(appSession);
            }
            if (leftName != null) {
                chatView.addLeftName(leftName);
            }
            if (rightName != null) {
                chatView.addRightName(rightName);
            }
            if (toolbar != null) {
                chatView.setToolbar(toolbar);
            }
            if (background != -1) {
                chatView.setBackground(background);
            }
            chatView.loadDummyHistory(loadDummyData);
            return chatView;
        }
    }
}