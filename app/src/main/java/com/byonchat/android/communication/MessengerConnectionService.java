package com.byonchat.android.communication;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.byonchat.android.ConversationGroupActivity;
import com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
import com.byonchat.android.R;
import com.byonchat.android.application.Application;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.list.ItemListMemberCard;
import com.byonchat.android.provider.BlockListDB;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.DataBaseDropDown;
import com.byonchat.android.provider.FilesURL;
import com.byonchat.android.provider.FilesURLDatabaseHelper;
import com.byonchat.android.provider.Group;
import com.byonchat.android.provider.Interval;
import com.byonchat.android.provider.IntervalDB;
import com.byonchat.android.provider.MembersDB;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.OffersDB;
import com.byonchat.android.provider.OffersModel;
import com.byonchat.android.provider.Rooms;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.provider.Skin;
import com.byonchat.android.provider.TimeLine;
import com.byonchat.android.provider.TimeLineDB;
import com.byonchat.android.smsSolders.WelcomeActivitySMS;
import com.byonchat.android.ui.activity.MainActivityNew;
import com.byonchat.android.ui.view.DialogAct;
import com.byonchat.android.utils.AllAboutUploadTask;
import com.byonchat.android.utils.GPSTracker;
import com.byonchat.android.utils.GetRealNameRoom;
import com.byonchat.android.utils.HttpHelper;
import com.byonchat.android.utils.ImageLoadingUtils;
import com.byonchat.android.utils.MediaProcessingUtil;
import com.byonchat.android.utils.PermanentLoggerUtil;
import com.byonchat.android.utils.PicassoOwnCache;
import com.byonchat.android.utils.RefreshContactService;
import com.byonchat.android.utils.RequestKeyTask;
import com.byonchat.android.utils.RequestUploadSite;
import com.byonchat.android.utils.TaskCompleted;
import com.byonchat.android.utils.UploadProfileService;
import com.byonchat.android.utils.UploadService;
import com.byonchat.android.utils.Utility;
import com.byonchat.android.utils.Validations;
import com.byonchat.android.utils.ValidationsKey;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterLoadedListener;
import org.jivesoftware.smack.roster.rosterstore.DirectoryRosterStore;
import org.jivesoftware.smack.roster.rosterstore.RosterStore;
import org.jivesoftware.smack.sasl.provided.SASLPlainMechanism;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.address.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.commands.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.delay.packet.DelayInformation;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.disco.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.iqlast.LastActivityManager;
import org.jivesoftware.smackx.iqlast.packet.LastActivity;
import org.jivesoftware.smackx.iqprivate.PrivateDataManager;
import org.jivesoftware.smackx.iqversion.provider.VersionProvider;
import org.jivesoftware.smackx.jiveproperties.packet.JivePropertiesExtension;
import org.jivesoftware.smackx.muc.Affiliate;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.ParticipantStatusListener;
import org.jivesoftware.smackx.muc.SubjectUpdatedListener;
import org.jivesoftware.smackx.muc.packet.GroupChatInvitation;
import org.jivesoftware.smackx.muc.packet.MUCUser;
import org.jivesoftware.smackx.muc.provider.MUCAdminProvider;
import org.jivesoftware.smackx.muc.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.muc.provider.MUCUserProvider;
import org.jivesoftware.smackx.offline.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.offline.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.ping.provider.PingProvider;
import org.jivesoftware.smackx.privacy.provider.PrivacyProvider;
import org.jivesoftware.smackx.receipts.DeliveryReceipt;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.sharedgroups.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.si.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.time.provider.TimeProvider;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.vcardtemp.provider.VCardProvider;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jivesoftware.smackx.xdata.provider.DataFormProvider;
import org.jivesoftware.smackx.xevent.provider.MessageEventProvider;
import org.jivesoftware.smackx.xhtmlim.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.xroster.provider.RosterExchangeProvider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.byonchat.android.utils.PicassoOwnCache.cacheDir;

public class MessengerConnectionService extends Service implements AllAboutUploadTask.OnTaskCompleted {
    private ScheduledExecutorService scheduleTaskExecutor;
    private NotificationReceiver notifReceive;
    public static final String CHAT_SERVER = "ss.byonchat.com";
    public static final String UTIL_SERVER = "uu.byonchat.com";
    public static final String HTTP_SERVER = "bb.byonchat.com";
    public static final String HTTP_SERVER2 = "bb.byonchat.com"; //hanya digunakan di Personal Room yang tadinya bbb
    public static final String FILE_SERVER = "pp.byonchat.com";
    public static final String F_SERVER = "f.byonchat.com";
    public static final int SERVER_PORT = 5222;
    public static final String SERVER_HOST = CHAT_SERVER;
    public static final String SERVER_NAME = CHAT_SERVER;
    private static final String CONFERENCE_SERVER = "conference." + CHAT_SERVER;
    public static final String SERVER_PORT_MN = "33021";//*//*"33011"*/;
    public static final String DOWNLOAD_LINK_THUMB = "/media/thumbnail/";
    public static final String GROUP_SERVER = "http://139.162.13.81:8080/InConnect/controll/open/";
    private static final String TAG = MessengerConnectionService.class.getSimpleName();
    public static Uri CONTACT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    public static final String ACTION_CONNECTED = MessengerConnectionService.class
            .getName() + ".connected";
    public static final String ACTION_DISCONNECTED = MessengerConnectionService.class
            .getName() + ".disconnected";
    public static final String ACTION_STATUS_CHANGED = MessengerConnectionService.class
            .getName() + ".statusChanged";
    public static final String ACTION_STATUS_CHANGED_CONTACT = MessengerConnectionService.class
            .getName() + ".statusChangedContact";
    public static final String ACTION_STATUS_CHANGED_CONVERSATION = MessengerConnectionService.class
            .getName() + ".statusChangedConversation";
    public static final String ACTION_MESSAGE_RECEIVED = MessengerConnectionService.class
            .getName() + ".messageReceived";
    public static final String ACTION_REFRESH_NOTIF_FORM = MessengerConnectionService.class
            .getName() + ".refreshNotifForm";
    public static final String ACTION_INVITE_GROUP = MessengerConnectionService.class
            .getName() + ".inviteGroup";
    public static final String ACTION_ADD_CARD = MessengerConnectionService.class
            .getName() + ".addCard";
    public static final String ACTION_REFRESH_ROOM = MessengerConnectionService.class
            .getName() + ".refreshRoom";
    public static final String ACTION_REQGPS = MessengerConnectionService.class
            .getName() + ".reqGps";
    public static final String ACTION_CHAT_OFF = MessengerConnectionService.class
            .getName() + ".chatOff";
    public static final String ACTION_REFRESH_CHAT_HISTORY = MessengerConnectionService.class
            .getName() + ".refreshChatHistory";
    public static final String ACTION_MESSAGE_SENT = MessengerConnectionService.class
            .getName() + ".messageSent";
    public static final String ACTION_MESSAGE_DELIVERED = MessengerConnectionService.class
            .getName() + ".messageDelivered";
    public static final String ACTION_MESSAGE_READ = MessengerConnectionService.class
            .getName() + ".messageRead";
    public static final String ACTION_MESSAGE_FAILED = MessengerConnectionService.class
            .getName() + ".messageFailed";
    public static final String ACTION_REFRESH_OFFERS = MessengerConnectionService.class
            .getName() + ".refreshOffers";
    public static final String ACTION_REFRESH_VOUCHERS = MessengerConnectionService.class
            .getName() + ".refreshVouchers";
    public static final String KEY_MESSAGE_OBJECT = MessengerConnectionService.class
            .getName() + ".messageObject";
    public static final String KEY_CONTACT_NAME = MessengerConnectionService.class
            .getName() + ".contactName";
    public static final String KEY_LOC_REQ = MessengerConnectionService.class
            .getName() + ".requestLocation";
    public static final String KEY_TASK_DONE = MessengerConnectionService.class
            .getName() + ".taskDone";
    public static final String KEY_DEST_ID = MessengerConnectionService.class
            .getName() + ".destID";
    public static final String KEY_HOST = MessengerConnectionService.class
            .getName() + ".host";
    public static final String KEY_PORT = MessengerConnectionService.class
            .getName() + ".port";
    public static final String KEY_SERVER_NAME = MessengerConnectionService.class
            .getName() + ".serverName";
    public static final String KEY_USERNAME = MessengerConnectionService.class
            .getName() + ".username";
    public static final String KEY_PASSWORD = MessengerConnectionService.class
            .getName() + ".password";
    public static boolean started = false;
    private static final String SQL_SELECT_CONTACTS = "SELECT * FROM "
            + Contact.TABLE_NAME + " WHERE _id > 1 order by lower(" + Contact.NAME + ")";
    public final static String URL_REMOVE_ROOM = "https://" + MessengerConnectionService.HTTP_SERVER + "/room/";
    public final static String URL_CEK_APPLY = "https://" + MessengerConnectionService.HTTP_SERVER + "/themes/boleh.php";
    public final static String URL_GET_MEMBERS = "https://" + MessengerConnectionService.HTTP_SERVER + "/memberships/daftar_kartu.php";
    public final static String URL_GET_OFFERS = "https://" + MessengerConnectionService.HTTP_SERVER + "/offers/";
    private static final String LOCATION_MESSAGE_PARAM = "com.byonchat.android.location";
    private static final String FILE_MESSAGE_PARAM = "com.byonchat.android.file";
    private static final String BROADCAST_MESSAGE_PARAM = "com.byonchat.android.broadcast";
    private static final String READSTATUS_MESSAGE_PARAM = "com.byonchat.android.readStatus";
    private static final String TARIKPESAN_MESSAGE_PARAM = "com.byonchat.android.tarikPesan";
    private static final String XMLNS_MUCUSER = "http://jabber.org/protocol/muc#user";
    private static final String XMLNS_DELIVERY = "urn:xmpp:receipts";
    private AbstractXMPPConnection xmppConnection;
    private HashMap<String, Chat> registeredChats = new HashMap<String, Chat>();
    private HashMap<String, GroupChat> registeredGroupChats = new HashMap<String, MessengerConnectionService.GroupChat>();
    private ChatMessageListener chatMsgListener = new MyChatMessageListener();
    private MessengerConnectionListener connListener = new MessengerConnectionListener();
    private Contact curContact;
    private boolean connectionHelperStarted = false;
    private MessengerDatabaseHelper databaseHelper;
    private StanzaListener presenceListener;
    private StanzaListener pingListener;
    private StanzaListener smAckListener;
    private InvitationListener mucInvitationListener;
    private SubjectUpdatedListener mucSubjectUpdatedListener;
    private GroupParticipantStatusListener mucParticipantStatusListener;
    private GroupMessageListener mucMessageListener = new GroupMessageListener();
    ;
    private BlockListDB blockListDB;
    private BotListDB botListDB;
    private Timer timer = new Timer();
    private Timer timerDua = new Timer();
    public int counter = 0;
    private static String SQL_REMOVE_MESSAGES_STATUS = "DELETE FROM " + Message.TABLE_NAME + " WHERE " + Message.PACKET_ID + " =?;";
    private static String SQL_UPDATE_MESSAGES = "UPDATE " + Message.TABLE_NAME + " SET status = " + Message.STATUS_READ + " WHERE " + Message.PACKET_ID + " =?;";
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String SMS_DELIVERED = "android.byonChat.SMS_DELIVERED";
    private static final String SMS_SENT = "android.byonChat.SMS_SENT";
    private static final String LOCATION_RECEIVED = "android.location.PROVIDERS_CHANGED";
    private String fileName, directory;
    private ImageLoadingUtils utils;
    private static final String PICASSO_CACHE = "byonchat-cache";
    PicassoOwnCache cache;
    String tanggal, flag;
    NotificationManager mNotificationManager;

    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 0;
    private static final float LOCATION_DISTANCE = 0f;
    Context c = this;
    private static Location locSpyChange = null;
    public static boolean onLoc = false;
    LocationListener[] mLocationListeners;


    static {
        SmackConfiguration.DEBUG = true;
        // TODO This is not really needed, but for some reason the static initializer block of
        // LastActivityManager is not run. This could be a problem caused by aSmack together with
        // dalvik, as the initializer is run on Smack's test cases.
        LastActivityManager.setEnabledPerDefault(true);

        SmackConfiguration.setDefaultPacketReplyTimeout(2 * 60000);

        SmackConfiguration.addDisabledSmackClass("org.jivesoftware.smackx.hoxt.HOXTManager");
        SmackConfiguration.addDisabledSmackClass("org.jivesoftware.smack.ReconnectionManager");
        SmackConfiguration.addDisabledSmackClass("org.jivesoftware.smackx.json");
        SmackConfiguration.addDisabledSmackClass("org.jivesoftware.smackx.gcm");
        SmackConfiguration.addDisabledSmackClass("org.jivesoftware.smackx.xdata.XDataManager");
        SmackConfiguration.addDisabledSmackClass("org.jivesoftware.smackx.xdatalayout.XDataLayoutManager");
        SmackConfiguration.addDisabledSmackClass("org.jivesoftware.smackx.xdatavalidation.XDataValidationManager");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
//        startTimer();

        Log.d("HIDUP", "onStartCommand MessengerConnectionService");

        mLocationListeners = new LocationListener[]{
                new LocationListener(LocationManager.GPS_PROVIDER),
                new LocationListener(LocationManager.NETWORK_PROVIDER)
        };

        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }

        if (new Validations().getInstance(getApplicationContext()).getShareLocOnOff(27) == true) {
            requestLocationUpdates(true);
        } else {
            requestLocationUpdates(false);
        }

        Contact contact = databaseHelper.getMyContact();
        if (contact != null) {
            xmppOpen();
            doSomethingRepeatedly();
            this.getApplicationContext().getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, contentObserver);
            started = true;
        }


        return START_STICKY;
    }

    public void requestLocationUpdates(boolean request) {
        if (request) {

            try {
                mLocationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                        mLocationListeners[1]);
            } catch (java.lang.SecurityException ex) {
                Log.i(TAG, "fail to request location update, ignore", ex);
            } catch (IllegalArgumentException ex) {
                Log.d(TAG, "network provider does not exist, " + ex.getMessage());
            }

            try {
                mLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                        mLocationListeners[0]);
            } catch (java.lang.SecurityException ex) {
                Log.i(TAG, "fail to request location update, ignore", ex);
            } catch (IllegalArgumentException ex) {
                Log.d(TAG, "gps provider does not exist " + ex.getMessage());
            }

        } else {
            mLocationManager.removeUpdates(mLocationListeners[0]);
            mLocationManager.removeUpdates(mLocationListeners[1]);
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId, String channelName) {
        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.createNotificationChannel(chan);
        return channelId;
    }

    private class MessengerConnectionTask implements Runnable {
        Context context;

        private MessengerConnectionTask(Context context) {
            this.context = context;
        }

        public void run() {
            Log.i("DEWA", "Received Start Foreground Intent ");
            Intent notificationIntent = new Intent(context, MainActivityNew.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    notificationIntent, 0);

            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    R.drawable.logo_byon);
            String channelId = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                channelId = createNotificationChannel("ByonChat", "Connected");

                mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                int notifyID = 101;
                NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context, channelId)
                        .setContentTitle("New Message")
                        .setSmallIcon(R.drawable.logo_byon)
                        .setContentText(NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext()) ? "Connected" : "No Connectivity");

                mNotificationManager.notify(
                        notifyID,
                        mNotifyBuilder.build());
            } else {
                mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                int notifyID = 101;
                NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context)
                        .setContentTitle("New Message")
                        .setSmallIcon(R.drawable.logo_byon)
                        .setContentText(NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext()) ? "Connected" : "No Connectivity");

                mNotificationManager.notify(
                        notifyID,
                        mNotifyBuilder.build());
            }
        }
    }


    private void doSomethingRepeatedly() {
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {


                    /* service
                    ArrayList<SubmitingModel> ss = SubmitingRoomDB.getInstance(getApplicationContext()).getAllSubmitingModel();

                    Log.w("alal1", ss.size() + "");
                    for (SubmitingModel sss : ss) {

                        Log.w("alalContent", sss.getContent() + "");
                        Log.w("alalStatus", sss.getStatus() + "");
                        Log.w("alalId", sss.getId() + "");


                        SubmitingRoomDB submitingRoomDB = SubmitingRoomDB.getInstance(getApplicationContext());
                        submitingRoomDB.updateContact(sss.getId(), "2");

                        Message message = new Message();
                        message.setMessage(sss.getContent());
                        message.setId(sss.getId());

                        Intent intent = new Intent(getApplicationContext(), UploadService.class);
                        intent.putExtra(UploadService.ACTION, "uploadTaskRoom");
                        intent.putExtra(UploadService.KEY_MESSAGE, message);
                        startService(intent);

                    }*/

                    if (new Validations().getInstance(getApplicationContext()).getShareLocOnOff(27) == true) {

                        if (locSpyChange != null) {
                            try {
                                String url = "https://bb.byonchat.com/luar/lapor_lokasi.php";
                                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                            }
                                        }
                                ) {
                                    @Override
                                    protected Map<String, String> getParams() {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("user", databaseHelper.getMyContact().getJabberId());
                                        params.put("lat", locSpyChange.getLatitude() + "");
                                        params.put("long", locSpyChange.getLongitude() + "");
                                        return params;
                                    }
                                };

                                Application.getInstance().addToRequestQueue(postRequest);

                            } catch (Exception e) {
                            }
                        }

                    } else {
                        requestLocationUpdates(false);
                        Log.w("laporPak", "no");
                    }

                    Contact contact = databaseHelper.getMyContact();

                    if (contact != null) {

                        String SQL_SELECT_MESSAGES = "SELECT *  FROM "
                                + Message.TABLE_NAME + " WHERE " + Message.SOURCE + "=? "
                                + " and status = 2 and is_retry = 0 order by packet_id desc";

                        Cursor cursor = databaseHelper.query(SQL_SELECT_MESSAGES, new String[]{
                                contact.getJabberId()});

                        ArrayList<Message> messages = new ArrayList<Message>();

                        while (cursor.moveToNext()) {
                            Message vo = new Message(cursor);
                            messages.add(0, vo);
                        }
                        cursor.close();
                        for (Iterator<Message> iterator = messages.iterator(); iterator
                                .hasNext(); ) {
                            Message vo = iterator.next();
                            if (!new Validations().getInstance(getApplicationContext()).cekRoom(vo.getDestination()) && !vo.isRetry() && !vo.isGroupChat()) {
                                SimpleDateFormat hourFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                                Date d = vo.getSendDate();
                                if (new Validations().getInstance(getApplicationContext()).getShowMinute(String.valueOf(hourFormat.format(d)), 1) == 1) {
                                    if (vo.getType().equals(Message.TYPE_VIDEO) || vo.getType().equals(Message.TYPE_IMAGE)) {
                                        try {
                                            sendFile(vo);
                                        } catch (SmackException e) {
                                            e.printStackTrace();
                                        }
                                    } else if (vo.getType().equalsIgnoreCase(Message.TYPE_LOC)) {
                                        try {
                                            sendLocation(vo);
                                        } catch (SmackException e) {
                                            e.printStackTrace();
                                        }
                                    } else if (vo.getType().equalsIgnoreCase(Message.TYPE_BROADCAST)) {
                                        sendBroadCast(vo);
                                    } else if (vo.getType().equalsIgnoreCase(Message.TYPE_TEXT)) {
                                        try {
                                            sendMessage(vo);
                                        } catch (SmackException e) {
                                            e.printStackTrace();
                                        }
                                    } else if (vo.getType().equalsIgnoreCase(Message.TYPE_TARIK)) {
                                        try {
                                            sendTarikPesan(vo);
                                        } catch (SmackException e) {
                                            e.printStackTrace();
                                        }
                                    } else if (vo.getType().equalsIgnoreCase(Message.TYPE_READSTATUS)) {
                                        try {
                                            sendReadStatus(vo);
                                        } catch (SmackException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }


                        }
                    }


                }

            }

            public void sendReadStatus(Message vo) throws SmackException {
                Map<String, String> props = new HashMap<String, String>();
                props.put(READSTATUS_MESSAGE_PARAM, "true");
                sendMessage(vo, props);
            }

            public void sendTarikPesan(Message vo) throws SmackException {
                Map<String, String> props = new HashMap<String, String>();
                props.put(TARIKPESAN_MESSAGE_PARAM, "true");
                sendMessage(vo, props);
            }

            public void sendBroadCast(Message vo) {
                Map<String, String> props = new HashMap<String, String>();
                props.put(BROADCAST_MESSAGE_PARAM, "true");
                try {
                    sendMessage(vo, props);
                } catch (SmackException e) {
                    e.printStackTrace();
                }
            }

            public void sendLocation(Message vo) throws SmackException {
                Map<String, String> props = new HashMap<String, String>();
                props.put(LOCATION_MESSAGE_PARAM, "true");
                sendMessage(vo, props);
            }

            public void sendFile(Message vo) throws SmackException {
                Map<String, String> props = new HashMap<String, String>();
                props.put(FILE_MESSAGE_PARAM, vo.getType());
                sendMessage(vo, props);
            }

            private void sendMessage(Message vo, Map<String, String> properties) throws SmackException {
                vo.setStatus(Message.STATUS_INPROGRESS);
                String action = "";
                vo.setSendDate(new Date());
                if (isConnectionAlive()) {
                    String messageToSend = vo.getMessage();
                    if (vo.getType().equals(Message.TYPE_VIDEO) || vo.getType().equals(Message.TYPE_IMAGE)) {
                        String urlFile = "";
                        String caption = "";

                        JSONObject jObject = null;
                        try {
                            jObject = new JSONObject(messageToSend);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (jObject != null) {
                            try {
                                urlFile = jObject.getString("u");
                                caption = jObject.getString("c");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        messageToSend = jsonMessage(caption, urlFile);

                        Log.i("message to send: ", messageToSend);
                    }
                    try {
                        if (vo.isGroupChat()) {

                        } else {
                            Chat xmppChat = createChat(vo.getDestination(),
                                    vo.isGroupChat());
                            org.jivesoftware.smack.packet.Message msgObj = new org.jivesoftware.smack.packet.Message();
                            msgObj.setBody(messageToSend);
                            msgObj.setStanzaId(vo.getPacketId());
                            //msgObj.addExtension(deliveryRequestExtension);
                            DeliveryReceiptRequest.addTo(msgObj);
                            addPropertyToMessage(msgObj, properties);
                            xmppChat.sendMessage(msgObj);
                        }

                        vo.setStatus(Message.STATUS_SENT);
                        vo.setIsRetry(true);
                        action = ACTION_MESSAGE_SENT;
                    } catch (SmackException e) {
                        Log.e(getClass().getSimpleName(),
                                "Error sending data to server. " + e.getMessage());
                        vo.setStatus(Message.STATUS_FAILED);
                        action = ACTION_MESSAGE_FAILED;
                    }
                } else {
                    vo.setStatus(Message.STATUS_FAILED);
                    action = ACTION_MESSAGE_FAILED;
                }
                onMessageProcessed(vo, action);
            }

            public void sendMessage(Message vo) throws SmackException {
                sendMessage(vo, null);
            }

            public Chat getChat(String userName) {
                return registeredChats.get(userName);
            }

            public Chat createChat(String userName, boolean groupChat) {
                Chat xmppChat = getChat(userName);
                if (xmppChat == null) {
                    String destAddr = getBareJabberId(userName);
                    if (groupChat) {
                        destAddr = userName + "@" + CONFERENCE_SERVER;
                    }
                    xmppChat = ChatManager.getInstanceFor(xmppConnection).createChat(destAddr, chatMsgListener);
                    registeredChats.put(userName, xmppChat);
                }
                return xmppChat;
            }

            private void addPropertyToMessage(
                    org.jivesoftware.smack.packet.Message msg,
                    Map<String, String> properties) {
                if (properties != null) {
                    for (Iterator<String> iterator = properties.keySet().iterator(); iterator
                            .hasNext(); ) {
                        String key = iterator.next();
                        String value = properties.get(key);
                        if (value != null) {
                            //msg.setProperty(key, value);
                            JivePropertiesExtension jpe = new JivePropertiesExtension();
                            jpe.setProperty(key, value);
                            msg.addExtension(jpe);
                        }
                    }
                }
            }

            public void postData(String valueIWantToSend, final String usr, final String idr, final String idDetail) {
                // Log.w("masuk","kenisn3");
                // Create a new HttpClient and Post Header
                SimpleDateFormat hourFormat = new SimpleDateFormat(
                        "HH:mm:ss dd/MM/yyyy", Locale.getDefault());
                try {
                    // Log.w("a1a2", valueIWantToSend + ".:." + usr);
                    HttpParams httpParameters = new BasicHttpParams();
                    HttpConnectionParams.setConnectionTimeout(httpParameters, 13000);
                    HttpConnectionParams.setSoTimeout(httpParameters, 15000);
                    HttpClient httpclient = new DefaultHttpClient(httpParameters);
                    HttpPost httppost = new HttpPost(valueIWantToSend);

                    // Add your data
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("username_room", usr));
                    nameValuePairs.add(new BasicNameValuePair("id_rooms_tab", idr));
                    nameValuePairs.add(new BasicNameValuePair("id_detail_tab", idDetail));
                    Cursor cursorParent = botListDB.getSingleRoomDetailFormWithFlag(idDetail, usr, idr, "parent");

                    if (cursorParent.getCount() > 0) {
                        if (!cursorParent.getString(cursorParent.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)).equalsIgnoreCase("")) {
                            nameValuePairs.add(new BasicNameValuePair("latlong_before", jsonResultType(cursorParent.getString(cursorParent.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "a")));
                            nameValuePairs.add(new BasicNameValuePair("latlong_after", jsonResultType(cursorParent.getString(cursorParent.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "b")));
                        } else {
                            nameValuePairs.add(new BasicNameValuePair("latlong_before", "null"));
                            nameValuePairs.add(new BasicNameValuePair("latlong_after", "null"));
                        }
                    } else {
                        nameValuePairs.add(new BasicNameValuePair("latlong_before", "null"));
                        nameValuePairs.add(new BasicNameValuePair("latlong_after", "null"));
                    }

                    if (valueIWantToSend.equalsIgnoreCase(new ValidationsKey().getInstance(getApplicationContext()).getTargetUrl(usr) + DinamicRoomTaskActivity.PULLDETAIL)) {
                        if (idDetail != null || !idDetail.equalsIgnoreCase("")) {
                            String[] ff = idDetail.split("\\|");
                            if (ff.length == 2) {
                                nameValuePairs.add(new BasicNameValuePair("parent_id", ff[1]));
                                nameValuePairs.add(new BasicNameValuePair("id_list_push", ff[0]));
                            }
                        }
                    }

                    MessengerDatabaseHelper messengerHelper = null;
                    if (messengerHelper == null) {
                        messengerHelper = MessengerDatabaseHelper.getInstance(getApplicationContext());
                    }

                    Contact contact = messengerHelper.getMyContact();
                    nameValuePairs.add(new BasicNameValuePair("bc_user", contact.getJabberId()));

                    ArrayList<RoomsDetail> list = botListDB.allRoomDetailFormWithFlag(idDetail, usr, idr, "cild");

                    for (int u = 0; u < list.size(); u++) {

                        JSONArray jsA = null;
                        String content = "";
                        String cc = list.get(u).getContent();
                        if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("input_kodepos") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown_wilayah")) {
                            cc = jsoncreateC(list.get(u).getContent());
                        } else if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("form_child")) {
                            Log.w("disiji", "cek");

                        }

                        try {
                            if (cc.startsWith("{")) {
                                if (!cc.startsWith("[")) {
                                    cc = "[" + cc + "]";
                                }
                                jsA = new JSONArray(cc);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (jsA != null) {
                            if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("distance_estimation") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown_dinamis") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("ocr") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("upload_document")) {
                                nameValuePairs.add(new BasicNameValuePair("key[]", list.get(u).getFlag_tab()));
                                nameValuePairs.add(new BasicNameValuePair("value[]", list.get(u).getContent()));
                                nameValuePairs.add(new BasicNameValuePair("type[]", jsonResultType(list.get(u).getFlag_content(), "b")));
                            } else {
                                try {
                                    for (int ic = 0; ic < jsA.length(); ic++) {
                                        final String icC = jsA.getJSONObject(ic).getString("c").toString();
                                        content += icC + "|";
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                nameValuePairs.add(new BasicNameValuePair("key[]", list.get(u).getFlag_tab()));
                                nameValuePairs.add(new BasicNameValuePair("value[]", content.substring(0, content.length() - 1)));
                                nameValuePairs.add(new BasicNameValuePair("type[]", jsonResultType(list.get(u).getFlag_content(), "b")));
                            }
                        } else {
                            nameValuePairs.add(new BasicNameValuePair("key[]", list.get(u).getFlag_tab()));
                            nameValuePairs.add(new BasicNameValuePair("value[]", list.get(u).getContent()));
                            nameValuePairs.add(new BasicNameValuePair("type[]", jsonResultType(list.get(u).getFlag_content(), "b")));
                        }

                    }

                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    // Execute HTTP Post Request
                    HttpResponse response = httpclient.execute(httppost);
                    int status = response.getStatusLine().getStatusCode();

                    if (status == 200) {
                        HttpEntity entity = response.getEntity();
                        String data = EntityUtils.toString(entity);

                        if (data.equalsIgnoreCase("1")) {
                            long date = System.currentTimeMillis();
                            String dateString = hourFormat.format(date);
                            RoomsDetail orderModel = new RoomsDetail(idDetail, idr, usr, dateString, "2", null, "parent");
                            botListDB.updateDetailRoomWithFlagContentParent(orderModel);

                            Intent intent = new Intent(KEY_TASK_DONE);
                            intent.putExtra(KEY_CONTACT_NAME, "success Upload task;" + usr);
                            sendOrderedBroadcast(intent, null);

                        } else {
                            long date = System.currentTimeMillis();
                            String dateString = hourFormat.format(date);
                            RoomsDetail orderModel = new RoomsDetail(idDetail, idr, usr, dateString, "3", null, "parent");
                            botListDB.updateDetailRoomWithFlagContentParent(orderModel);
                        }
                    } else {
                        // Log.w("boker", status + "");
                        long date = System.currentTimeMillis();
                        String dateString = hourFormat.format(date);
                        RoomsDetail orderModel = new RoomsDetail(idDetail, idr, usr, dateString, "3", null, "parent");
                        botListDB.updateDetailRoomWithFlagContentParent(orderModel);
                    }

                } catch (ConnectTimeoutException e) {
                    e.printStackTrace();
                    long date = System.currentTimeMillis();
                    String dateString = hourFormat.format(date);
                    RoomsDetail orderModel = new RoomsDetail(idDetail, idr, usr, dateString, "3", null, "parent");
                    botListDB.updateDetailRoomWithFlagContentParent(orderModel);
                } catch (ClientProtocolException e) {
                    long date = System.currentTimeMillis();
                    String dateString = hourFormat.format(date);
                    RoomsDetail orderModel = new RoomsDetail(idDetail, idr, usr, dateString, "3", null, "parent");
                    botListDB.updateDetailRoomWithFlagContentParent(orderModel);
                    // TODO Auto-generated catch block
                } catch (IOException e) {
                    long date = System.currentTimeMillis();
                    String dateString = hourFormat.format(date);
                    RoomsDetail orderModel = new RoomsDetail(idDetail, idr, usr, dateString, "3", null, "parent");
                    botListDB.updateDetailRoomWithFlagContentParent(orderModel);
                    // TODO Auto-generated catch block
                }
            }

            private String jsoncreateC(String content) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("c", content);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return obj.toString();
            }

            private String jsonResultType(String json, String type) {
                String hasil = "";
                JSONObject jObject = null;
                try {
                    jObject = new JSONObject(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (jObject != null) {
                    try {
                        hasil = jObject.getString(type);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                return hasil;
            }


        }, 0, (1 * 60) * 1000);
    }


    private void xmppOpen() {
        XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
        configBuilder.setHost(SERVER_HOST);
        configBuilder.setPort(SERVER_PORT);
        configBuilder.setServiceName(SERVER_NAME);
        configBuilder.setCompressionEnabled(false);
        configBuilder.setConnectTimeout(60000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            configBuilder.setKeystoreType("AndroidCAStore");
            configBuilder.setKeystorePath(null);
        } else {
            configBuilder.setKeystoreType("BKS");
            String path = System.getProperty("javax.net.ssl.trustStore");
            if (path == null)
                path = System.getProperty("java.home") + File.separator + "etc"
                        + File.separator + "security" + File.separator
                        + "cacerts.bks";
            configBuilder.setKeystorePath(path);
        }
        configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.required);

        xmppConnection = new XMPPTCPConnection(configBuilder.build());
        xmppConnection.addConnectionListener(connListener);
        final Roster roster = Roster.getInstanceFor(xmppConnection);

        // Setup the roster store
        File rosterStoreDirectory = getFileDir(getApplicationContext(), "rosterStore");
        RosterStore rosterStore = DirectoryRosterStore.init(rosterStoreDirectory);
        roster.setRosterStore(rosterStore);

        roster.addRosterLoadedListener(new RosterLoadedListener() {
            @Override
            public void onRosterLoaded(Roster roster) {
                //Log.d(TAG, "RosterLoadedListener.onRosterLoaded() invoked, roster has been loaded");
            }
        });

        new ConnectionHelper().start();
    }

    public static File getFileDir(Context context, String directoryName) {
        File result = new File(context.getFilesDir(), directoryName);
        if (!result.isDirectory()) {
            boolean success = result.mkdir();
            if (!success) {
                throw new IllegalStateException("Could not create directory: " + result);
            }
        }
        return result;
    }

    public void sendReadStatus(Message vo) throws SmackException {
        Map<String, String> props = new HashMap<String, String>();
        props.put(READSTATUS_MESSAGE_PARAM, "true");
        sendMessage(vo, props);
    }

    public void sendTarikPesan(Message vo) throws SmackException {
        Map<String, String> props = new HashMap<String, String>();
        props.put(TARIKPESAN_MESSAGE_PARAM, "true");
        sendMessage(vo, props);
    }

    public void sendBroadCast(Message vo) {
        Map<String, String> props = new HashMap<String, String>();
        props.put(BROADCAST_MESSAGE_PARAM, "true");
        try {
            sendMessage(vo, props);
        } catch (SmackException e) {
            e.printStackTrace();
        }
    }

    public void sendLocation(Message vo) throws SmackException {
        Map<String, String> props = new HashMap<String, String>();
        props.put(LOCATION_MESSAGE_PARAM, "true");
        sendMessage(vo, props);
    }

    public void sendFile(Message vo) throws SmackException {
        Map<String, String> props = new HashMap<String, String>();
        props.put(FILE_MESSAGE_PARAM, vo.getType());
        sendMessage(vo, props);
    }

    private void sendMessage(Message vo, Map<String, String> properties) throws SmackException {
        vo.setStatus(Message.STATUS_INPROGRESS);
        String action = "";
        vo.setSendDate(new Date());
        if (isConnectionAlive()) {
            String messageToSend = vo.getMessage();
            if (vo.getType().equals(Message.TYPE_VIDEO) || vo.getType().equals(Message.TYPE_IMAGE)) {
                String urlFile = "";
                String caption = "";

                JSONObject jObject = null;
                try {
                    jObject = new JSONObject(messageToSend);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (jObject != null) {
                    try {
                        urlFile = jObject.getString("u");
                        caption = jObject.getString("c");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                messageToSend = jsonMessage(caption, urlFile);

                Log.i("message to send: ", messageToSend);
            }
            try {
                if (vo.isGroupChat()) {

                } else {
                    Chat xmppChat = createChat(vo.getDestination(),
                            vo.isGroupChat());
                    org.jivesoftware.smack.packet.Message msgObj = new org.jivesoftware.smack.packet.Message();
                    msgObj.setBody(messageToSend);
                    msgObj.setStanzaId(vo.getPacketId());
                    //msgObj.addExtension(deliveryRequestExtension);
                    DeliveryReceiptRequest.addTo(msgObj);
                    addPropertyToMessage(msgObj, properties);
                    xmppChat.sendMessage(msgObj);
                }

                vo.setStatus(Message.STATUS_SENT);
                vo.setIsRetry(true);
                action = ACTION_MESSAGE_SENT;
            } catch (SmackException e) {
                Log.e(getClass().getSimpleName(),
                        "Error sending data to server. " + e.getMessage());
                vo.setStatus(Message.STATUS_FAILED);
                action = ACTION_MESSAGE_FAILED;
            }
        } else {
            vo.setStatus(Message.STATUS_FAILED);
            action = ACTION_MESSAGE_FAILED;
        }
        onMessageProcessed(vo, action);
    }

    public void sendMessage(Message vo) throws SmackException {
        sendMessage(vo, null);
    }

    public Chat getChat(String userName) {
        return registeredChats.get(userName);
    }

    public Chat createChat(String userName, boolean groupChat) {
        Chat xmppChat = getChat(userName);
        if (xmppChat == null) {
            String destAddr = getBareJabberId(userName);
            if (groupChat) {
                destAddr = userName + "@" + CONFERENCE_SERVER;
            }
            xmppChat = ChatManager.getInstanceFor(xmppConnection).createChat(destAddr, chatMsgListener);
            registeredChats.put(userName, xmppChat);
        }
        return xmppChat;
    }

    private void addPropertyToMessage(
            org.jivesoftware.smack.packet.Message msg,
            Map<String, String> properties) {
        if (properties != null) {
            for (Iterator<String> iterator = properties.keySet().iterator(); iterator
                    .hasNext(); ) {
                String key = iterator.next();
                String value = properties.get(key);
                if (value != null) {
                    //msg.setProperty(key, value);
                    JivePropertiesExtension jpe = new JivePropertiesExtension();
                    jpe.setProperty(key, value);
                    msg.addExtension(jpe);
                }
            }
        }
    }

    public void postData(String valueIWantToSend, final String usr, final String idr, final String idDetail) {
        // Log.w("masuk","kenisn3");
        // Create a new HttpClient and Post Header
        SimpleDateFormat hourFormat = new SimpleDateFormat(
                "HH:mm:ss dd/MM/yyyy", Locale.getDefault());
        try {
            // Log.w("a1a2", valueIWantToSend + ".:." + usr);
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 13000);
            HttpConnectionParams.setSoTimeout(httpParameters, 15000);
            HttpClient httpclient = new DefaultHttpClient(httpParameters);
            HttpPost httppost = new HttpPost(valueIWantToSend);

            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("username_room", usr));
            nameValuePairs.add(new BasicNameValuePair("id_rooms_tab", idr));
            nameValuePairs.add(new BasicNameValuePair("id_detail_tab", idDetail));
            Cursor cursorParent = botListDB.getSingleRoomDetailFormWithFlag(idDetail, usr, idr, "parent");

            if (cursorParent.getCount() > 0) {
                if (!cursorParent.getString(cursorParent.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)).equalsIgnoreCase("")) {
                    nameValuePairs.add(new BasicNameValuePair("latlong_before", jsonResultType(cursorParent.getString(cursorParent.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "a")));
                    nameValuePairs.add(new BasicNameValuePair("latlong_after", jsonResultType(cursorParent.getString(cursorParent.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "b")));
                } else {
                    nameValuePairs.add(new BasicNameValuePair("latlong_before", "null"));
                    nameValuePairs.add(new BasicNameValuePair("latlong_after", "null"));
                }
            } else {
                nameValuePairs.add(new BasicNameValuePair("latlong_before", "null"));
                nameValuePairs.add(new BasicNameValuePair("latlong_after", "null"));
            }

            if (valueIWantToSend.equalsIgnoreCase(new ValidationsKey().getInstance(getApplicationContext()).getTargetUrl(usr) + DinamicRoomTaskActivity.PULLDETAIL)) {
                if (idDetail != null || !idDetail.equalsIgnoreCase("")) {
                    String[] ff = idDetail.split("\\|");
                    if (ff.length == 2) {
                        nameValuePairs.add(new BasicNameValuePair("parent_id", ff[1]));
                        nameValuePairs.add(new BasicNameValuePair("id_list_push", ff[0]));
                    }
                }
            }

            MessengerDatabaseHelper messengerHelper = null;
            if (messengerHelper == null) {
                messengerHelper = MessengerDatabaseHelper.getInstance(getApplicationContext());
            }

            Contact contact = messengerHelper.getMyContact();
            nameValuePairs.add(new BasicNameValuePair("bc_user", contact.getJabberId()));

            ArrayList<RoomsDetail> list = botListDB.allRoomDetailFormWithFlag(idDetail, usr, idr, "cild");

            for (int u = 0; u < list.size(); u++) {

                JSONArray jsA = null;
                String content = "";
                String cc = list.get(u).getContent();
                if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("input_kodepos") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown_wilayah")) {
                    cc = jsoncreateC(list.get(u).getContent());
                }

                try {
                    if (cc.startsWith("{")) {
                        if (!cc.startsWith("[")) {
                            cc = "[" + cc + "]";
                        }
                        jsA = new JSONArray(cc);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (jsA != null) {
                    if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("distance_estimation") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown_dinamis") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("ocr") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("upload_document")) {
                        nameValuePairs.add(new BasicNameValuePair("key[]", list.get(u).getFlag_tab()));
                        nameValuePairs.add(new BasicNameValuePair("value[]", list.get(u).getContent()));
                        nameValuePairs.add(new BasicNameValuePair("type[]", jsonResultType(list.get(u).getFlag_content(), "b")));
                    } else {
                        try {
                            for (int ic = 0; ic < jsA.length(); ic++) {
                                final String icC = jsA.getJSONObject(ic).getString("c").toString();
                                content += icC + "|";
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        nameValuePairs.add(new BasicNameValuePair("key[]", list.get(u).getFlag_tab()));
                        nameValuePairs.add(new BasicNameValuePair("value[]", content.substring(0, content.length() - 1)));
                        nameValuePairs.add(new BasicNameValuePair("type[]", jsonResultType(list.get(u).getFlag_content(), "b")));
                    }
                } else {
                    nameValuePairs.add(new BasicNameValuePair("key[]", list.get(u).getFlag_tab()));
                    nameValuePairs.add(new BasicNameValuePair("value[]", list.get(u).getContent()));
                    nameValuePairs.add(new BasicNameValuePair("type[]", jsonResultType(list.get(u).getFlag_content(), "b")));
                }

            }

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            int status = response.getStatusLine().getStatusCode();

            if (status == 200) {
                HttpEntity entity = response.getEntity();
                String data = EntityUtils.toString(entity);

                if (data.equalsIgnoreCase("1")) {
                    long date = System.currentTimeMillis();
                    String dateString = hourFormat.format(date);
                    RoomsDetail orderModel = new RoomsDetail(idDetail, idr, usr, dateString, "2", null, "parent");
                    botListDB.updateDetailRoomWithFlagContentParent(orderModel);

                    Intent intent = new Intent(KEY_TASK_DONE);
                    intent.putExtra(KEY_CONTACT_NAME, "success Upload task;" + usr);
                    sendOrderedBroadcast(intent, null);

                } else {
                    long date = System.currentTimeMillis();
                    String dateString = hourFormat.format(date);
                    RoomsDetail orderModel = new RoomsDetail(idDetail, idr, usr, dateString, "3", null, "parent");
                    botListDB.updateDetailRoomWithFlagContentParent(orderModel);
                }
            } else {
                long date = System.currentTimeMillis();
                String dateString = hourFormat.format(date);
                RoomsDetail orderModel = new RoomsDetail(idDetail, idr, usr, dateString, "3", null, "parent");
                botListDB.updateDetailRoomWithFlagContentParent(orderModel);
            }

        } catch (ConnectTimeoutException e) {
            e.printStackTrace();
            long date = System.currentTimeMillis();
            String dateString = hourFormat.format(date);
            RoomsDetail orderModel = new RoomsDetail(idDetail, idr, usr, dateString, "3", null, "parent");
            botListDB.updateDetailRoomWithFlagContentParent(orderModel);
        } catch (ClientProtocolException e) {
            long date = System.currentTimeMillis();
            String dateString = hourFormat.format(date);
            RoomsDetail orderModel = new RoomsDetail(idDetail, idr, usr, dateString, "3", null, "parent");
            botListDB.updateDetailRoomWithFlagContentParent(orderModel);
            // TODO Auto-generated catch block
        } catch (IOException e) {
            long date = System.currentTimeMillis();
            String dateString = hourFormat.format(date);
            RoomsDetail orderModel = new RoomsDetail(idDetail, idr, usr, dateString, "3", null, "parent");
            botListDB.updateDetailRoomWithFlagContentParent(orderModel);
            // TODO Auto-generated catch block
        }
    }

    private String jsoncreateC(String content) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("c", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }

    private String jsonResultType(String json, String type) {
        String hasil = "";
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jObject != null) {
            try {
                hasil = jObject.getString(type);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return hasil;
    }


    public void appendLog(String text) {
        File logFile = new File("sdcard/log.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTaskProses(String response) {

    }

    @Override
    public void onTaskUpdate(int response, String message) {

    }

    @Override
    public void onTaskCompleted(int status, String response) {

    }

    private class MyContentObserver extends ContentObserver {

        public MyContentObserver() {
            super(null);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            String[] projection = new String[]{
                    ContactsContract.Contacts.LOOKUP_KEY,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER};
            Cursor cursor = getContentResolver().query(CONTACT_URI, projection,
                    null, null, null);
            String time_strDB = "0";
            IntervalDB intervalDB = new IntervalDB(getApplicationContext());
            intervalDB.open();
            Cursor cContact = intervalDB.getSingleContact(2);
            if (cContact.getCount() > 0) {
                time_strDB = cContact.getString(cContact.getColumnIndexOrThrow(IntervalDB.COL_TIME));
            }
            if (cursor.getCount() != Integer.valueOf(time_strDB)) {
                if (cContact.getCount() > 0) {
                    intervalDB.deleteContact(2);
                }
                Interval interval = new Interval();
                interval.setId(2);
                interval.setTime(String.valueOf(cursor.getCount()));
                intervalDB.createContact(interval);
                startService(new Intent(getBaseContext(), RefreshContactService.class));
            } else {
                Cursor cursorDB = databaseHelper.query(SQL_SELECT_CONTACTS, null);
                Contact contact;

                for (cursorDB.moveToFirst(); !cursorDB.isAfterLast(); cursorDB.moveToNext()) {
                    contact = new Contact(cursorDB);
                    Cursor cursorCek = getContentResolver().query(CONTACT_URI, projection,
                            null, null, null);
                    int indexName = cursorCek.getColumnIndex(projection[1]);
                    int indexMnumber = cursorCek.getColumnIndex(projection[2]);
                    HashMap<Long, Contact> osMap = new HashMap<Long, Contact>();
                    while (cursorCek.moveToNext()) {
                        String name = cursorCek.getString(indexName);
                        String mnumber = cursorCek.getString(indexMnumber).replace(" ", "")
                                .replace("-", "");
                        if (mnumber.startsWith("0")) {
                            mnumber = mnumber.replaceFirst("0", "62");
                        } else if (mnumber.startsWith("+")) {
                            mnumber = mnumber.replaceFirst("\\+", "");
                        }
                        if (cursorDB.getString(cursorDB.getColumnIndexOrThrow(Contact.JABBER_ID)).equalsIgnoreCase(mnumber)) {
                            contact.setName(name);
                            databaseHelper.updateData(contact);
                        }
                    }
                }
            }
            cursor.close();
            cContact.close();
            intervalDB.close();

        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

    }

    MyContentObserver contentObserver = new MyContentObserver();

    public void configure() {

        // Private Data Storage
        ProviderManager.addIQProvider("query", "jabber:iq:private",
                new PrivateDataManager.PrivateDataIQProvider());
        // Time
        ProviderManager.addIQProvider("query", "jabber:iq:time",
                new TimeProvider());

        // Roster Exchange
        ProviderManager.addExtensionProvider("x", "jabber:x:roster",
                new RosterExchangeProvider());

        // Message Events
        ProviderManager.addExtensionProvider("x", "jabber:x:event",
                new MessageEventProvider());

        // XHTML
        ProviderManager.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im",
                new XHTMLExtensionProvider());

        // Group Chat Invitations
        ProviderManager.addExtensionProvider("x", "jabber:x:conference",
                new GroupChatInvitation.Provider());

        // Service Discovery # Items
        ProviderManager.addIQProvider("query", "http://jabber.org/protocol/disco#items",
                new DiscoverItemsProvider());

        // Service Discovery # Info
        ProviderManager.addIQProvider("query", "http://jabber.org/protocol/disco#info",
                new DiscoverInfoProvider());

        // Data Forms
        ProviderManager.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());

        // MUC User
        ProviderManager.addExtensionProvider("x", XMLNS_MUCUSER, new MUCUserProvider());

        // MUC Admin
        ProviderManager.addIQProvider("query", "http://jabber.org/protocol/muc#admin",
                new MUCAdminProvider());

        // MUC Owner
        ProviderManager.addIQProvider("query", "http://jabber.org/protocol/muc#owner",
                new MUCOwnerProvider());

        // Delayed Delivery
/*
        ProviderManager.addExtensionProvider("x", "jabber:x:delay",
                new DelayInformationProvider());
*/
        ProviderManager.addExtensionProvider("x", "jabber:x:delay",
                new MyDelayInformationProvider());

        // Version
        ProviderManager.addIQProvider("query", "jabber:iq:version", new VersionProvider());

        // VCard
        ProviderManager.addIQProvider("vCard", "vcard-temp", new VCardProvider());

        // Offline Message Requests
        ProviderManager.addIQProvider("offline", "http://jabber.org/protocol/offline",
                new OfflineMessageRequest.Provider());

        // Offline Message Indicator
        ProviderManager.addExtensionProvider("offline",
                "http://jabber.org/protocol/offline",
                new OfflineMessageInfo.Provider());

        // Last Activity
        ProviderManager.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());

        // User Search
        ProviderManager.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());

        // SharedGroupsInfo
        ProviderManager.addIQProvider("sharedgroup",
                "http://www.jivesoftware.org/protocol/sharedgroup",
                new SharedGroupsInfo.Provider());

        // JEP-33: Extended Stanza Addressing
        ProviderManager.addExtensionProvider("addresses",
                "http://jabber.org/protocol/address",
                new MultipleAddressesProvider());

        // FileTransfer
        ProviderManager.addIQProvider("si", "http://jabber.org/protocol/si",
                new StreamInitiationProvider());
        ProviderManager.addIQProvider("query", "http://jabber.org/protocol/bytestreams",
                new BytestreamsProvider());

        // Privacy
        ProviderManager.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());

        ProviderManager.addIQProvider("command", "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider());
        ProviderManager.addExtensionProvider("malformed-action",
                "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider.MalformedActionError());
        ProviderManager.addExtensionProvider("bad-locale",
                "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider.BadLocaleError());
        ProviderManager.addExtensionProvider("bad-payload",
                "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider.BadPayloadError());
        ProviderManager.addExtensionProvider("bad-sessionid",
                "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider.BadSessionIDError());
        ProviderManager.addExtensionProvider("session-expired",
                "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider.SessionExpiredError());
        // Ping
        ProviderManager.addIQProvider("ping", "urn:xmpp:ping", new PingProvider());

        //Deliv receipt
        ProviderManager.addExtensionProvider(DeliveryReceipt.ELEMENT, DeliveryReceipt.NAMESPACE,
                new DeliveryReceipt.Provider());
        ProviderManager.addExtensionProvider(DeliveryReceiptRequest.ELEMENT, new DeliveryReceiptRequest().getNamespace(),
                new DeliveryReceiptRequest.Provider());
    }

    private void saveIncomingFileToGallery(File theFile) {
        MediaProcessingUtil.saveFileToGallery(this, Uri.fromFile(theFile));
    }

    @Override
    public void onCreate() {
        super.onCreate();

        databaseHelper = MessengerDatabaseHelper.getInstance(this);
        botListDB = BotListDB.getInstance(this);

        IntentFilter filter = new IntentFilter(
                UploadService.class.getName() + ".sendFile");
        filter.addAction(LOCATION_RECEIVED);
        filter.setPriority(1);
        registerReceiver(receiver, filter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notifReceive = new NotificationReceiver();
            IntentFilter nya = new IntentFilter();
            nya.addAction("com.byonchat.android.communication.MessengerConnectionService.messageReceived");
            nya.addAction("com.byonchat.android.communication.MessengerConnectionService.addCard");
            nya.addAction("com.byonchat.android.communication.MessengerConnectionService.inviteGroup");
            nya.addAction("com.byonchat.android.communication.MessengerConnectionService.reqGps");
            nya.addAction("com.byonchat.android.communication.MessengerConnectionService.taskDone");
            nya.addAction("com.byonchat.android.communication.MessengerConnectionService.refreshRoom");

            getBaseContext().registerReceiver(notifReceive, nya);
        }
    }


    //This will handle the broadcast
    public BroadcastReceiver receiver = new BroadcastReceiver() {
        //@Override
        public void onReceive(Context context, Intent intent) {
            /*
            di tutup karena ini fitur SMS SOLDIER
            if (intent.getAction().equals(SMS_RECEIVED)) {

                Message report = new Message(databaseHelper.getMyContact().getJabberId(), "x_byonchatbackground", "sms_gateway;" + intent.getStringExtra("id") + "|" + intent.getStringExtra("number") + "|" + "SMS RECEIVED" + "|" + intent.getFlags());
                report.setType("text");
                report.setSendDate(new Date());
                report.setStatus(Message.STATUS_INPROGRESS);
                report.generatePacketId();

                Intent i = new Intent();
                i.setAction(UploadService.FILE_SEND_INTENT);
                i.putExtra(KEY_MESSAGE_OBJECT, report);
                sendBroadcast(i);

                *//*
                bug kenapa
                Bundle bundle = intent.getExtras();
                SmsMessage[] msgs = null;
                if (bundle != null){
                    try{
                        Object[] pdus = (Object[]) bundle.get("pdus");
                        msgs = new SmsMessage[pdus.length];
                        for(int i=0; i<msgs.length; i++) {
                            msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                            String msgBody = msgs[i].getMessageBody();
                            String msgMsidn = msgs[i].getOriginatingAddress();
                            if (msgMsidn.equalsIgnoreCase("+6288977669999")){
                                    Intent intent2 = new Intent(ACTION_REQGPS);
                                    intent2.putExtra(KEY_LOC_REQ, msgBody);
                                    sendOrderedBroadcast(intent2, null);
                                    return;
                            }
                        }
                    }catch(Exception e){
//                            //Log.d("Exception caught",e.getMessage());
                    }
                }*//*

            } else if (intent.getAction().equals(SMS_DELIVERED)) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Message report = new Message(databaseHelper.getMyContact().getJabberId(), "x_byonchatbackground", "sms_gateway;" + intent.getStringExtra("id") + "|" + intent.getStringExtra("number") + "|" + "SMS Delivered" + "|" + intent.getFlags());
                        report.setType("text");
                        report.setSendDate(new Date());
                        report.setStatus(Message.STATUS_INPROGRESS);
                        report.generatePacketId();

                        Intent i = new Intent();
                        i.setAction(UploadService.FILE_SEND_INTENT);
                        i.putExtra(KEY_MESSAGE_OBJECT, report);
                        sendBroadcast(i);
                        break;
                    case Activity.RESULT_CANCELED:
                        report = new Message(databaseHelper.getMyContact().getJabberId(), "x_byonchatbackground", "sms_gateway;" + intent.getStringExtra("id") + "|" + intent.getStringExtra("number") + "|" + "SMS not delivered" + intent.getFlags());
                        report.setType("text");
                        report.setSendDate(new Date());
                        report.setStatus(Message.STATUS_INPROGRESS);
                        report.generatePacketId();

                        i = new Intent();
                        i.setAction(UploadService.FILE_SEND_INTENT);
                        i.putExtra(KEY_MESSAGE_OBJECT, report);
                        sendBroadcast(i);

                        break;
                }
            } else if (intent.getAction().equals(SMS_SENT)) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Message report = new Message(databaseHelper.getMyContact().getJabberId(), "x_byonchatbackground", "sms_gateway;" + intent.getStringExtra("id") + "|" + intent.getStringExtra("number") + "|" + "SMS has been sent|" + intent.getFlags());
                        report.setType("text");
                        report.setSendDate(new Date());
                        report.setStatus(Message.STATUS_INPROGRESS);
                        report.generatePacketId();

                        Intent i = new Intent();
                        i.setAction(UploadService.FILE_SEND_INTENT);
                        i.putExtra(KEY_MESSAGE_OBJECT, report);
                        sendBroadcast(i);

                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        report = new Message(databaseHelper.getMyContact().getJabberId(), "x_byonchatbackground", "sms_gateway;" + intent.getStringExtra("id") + "|" + intent.getStringExtra("number") + "|" + "Generic Failure|" + intent.getFlags());
                        report.setType("text");
                        report.setSendDate(new Date());
                        report.setStatus(Message.STATUS_INPROGRESS);
                        report.generatePacketId();

                        i = new Intent();
                        i.setAction(UploadService.FILE_SEND_INTENT);
                        i.putExtra(KEY_MESSAGE_OBJECT, report);
                        sendBroadcast(i);

                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        report = new Message(databaseHelper.getMyContact().getJabberId(), "x_byonchatbackground", "sms_gateway;" + intent.getStringExtra("id") + "|" + intent.getStringExtra("number") + "|" + "No Service|" + intent.getFlags());
                        report.setType("text");
                        report.setSendDate(new Date());
                        report.setStatus(Message.STATUS_INPROGRESS);
                        report.generatePacketId();

                        i = new Intent();
                        i.setAction(UploadService.FILE_SEND_INTENT);
                        i.putExtra(KEY_MESSAGE_OBJECT, report);
                        sendBroadcast(i);

                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        report = new Message(databaseHelper.getMyContact().getJabberId(), "x_byonchatbackground", "sms_gateway;" + intent.getStringExtra("id") + "|" + intent.getStringExtra("number") + "|" + "Null PDU|" + intent.getFlags());
                        report.setType("text");
                        report.setSendDate(new Date());
                        report.setStatus(Message.STATUS_INPROGRESS);
                        report.generatePacketId();

                        i = new Intent();
                        i.setAction(UploadService.FILE_SEND_INTENT);
                        i.putExtra(KEY_MESSAGE_OBJECT, report);
                        sendBroadcast(i);

                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        report = new Message(databaseHelper.getMyContact().getJabberId(), "x_byonchatbackground", "sms_gateway;" + intent.getStringExtra("id") + "|" + intent.getStringExtra("number") + "|" + "Radio Off|" + intent.getFlags());
                        report.setType("text");
                        report.setSendDate(new Date());
                        report.setStatus(Message.STATUS_INPROGRESS);
                        report.generatePacketId();

                        i = new Intent();
                        i.setAction(UploadService.FILE_SEND_INTENT);
                        i.putExtra(KEY_MESSAGE_OBJECT, report);
                        sendBroadcast(i);


                        break;
                    default:
                        break;
                }
            } else */
            if (intent.getAction().equals(LOCATION_RECEIVED)) {
                GPSTracker gps = new GPSTracker(getApplicationContext());
                if (gps.canGetLocation()) {
                    CountDownTimer cdt = new CountDownTimer(5000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            GPSTracker gps = new GPSTracker(getApplicationContext());
                            if (gps.canGetLocation()) {
                                IntervalDB db = new IntervalDB(getApplicationContext());
                                db.open();
                                Cursor cursorSelect = db.getSingleContact(20);
                                if (cursorSelect.getCount() > 0) {
                                    db.updateContact(20, gps.getLatitude() + "|" + gps.getLongitude());
                                } else {
                                    Interval interval = new Interval();
                                    interval.setId(20);
                                    interval.setTime(gps.getLatitude() + "|" + gps.getLongitude());
                                    db.createContact(interval);
                                }
                                db.close();
                            }
                        }
                    };
                    cdt.start();
                }
            } else {
                Message vo = intent.getParcelableExtra(MessengerConnectionService.KEY_MESSAGE_OBJECT);
                if (vo.getType().equalsIgnoreCase(Message.TYPE_TEXT)) {
                    if (isXmppConnected()) try {
                        sendMessage(vo, null);
                    } catch (SmackException e) {
                        e.printStackTrace();
                    }
                } else if (vo.getType().equalsIgnoreCase(Message.TYPE_LOC)) {
                    if (isXmppConnected()) {
                        try {
                            sendLocation(vo);
                        } catch (SmackException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (vo.getType().equalsIgnoreCase(Message.TYPE_VIDEO) || vo.getType().equalsIgnoreCase(Message.TYPE_IMAGE)) {
                    if (isXmppConnected()) {
                        try {
                            sendFile(vo);
                        } catch (SmackException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (vo.getType().equalsIgnoreCase(Message.TYPE_READSTATUS)) {
                    if (isXmppConnected()) {
                        try {
                            sendReadStatus(vo);
                        } catch (SmackException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (vo.getType().equalsIgnoreCase(Message.TYPE_TARIK)) {
                    if (isXmppConnected()) {
                        try {
                            tarikPesan(vo);
                        } catch (SmackException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        public void sendReadStatus(Message vo) throws SmackException {
            Map<String, String> props = new HashMap<String, String>();
            props.put(READSTATUS_MESSAGE_PARAM, "true");
            sendMessage(vo, props);
        }

        public void tarikPesan(Message vo) throws SmackException {
            Map<String, String> props = new HashMap<String, String>();
            props.put(TARIKPESAN_MESSAGE_PARAM, "true");
            sendMessage(vo, props);
        }

        public void sendLocation(Message vo) throws SmackException {
            Map<String, String> props = new HashMap<String, String>();
            props.put(LOCATION_MESSAGE_PARAM, "true");
            sendMessage(vo, props);
        }

        public void sendFile(Message vo) throws SmackException {
            Map<String, String> props = new HashMap<String, String>();
            props.put(FILE_MESSAGE_PARAM, vo.getType());
            sendMessage(vo, props);
        }


        private void sendMessage(Message vo, Map<String, String> properties) throws SmackException {
            Log.w("kirim pesan", vo.getMessage());
            vo.setStatus(Message.STATUS_INPROGRESS);
            String action = "";
            vo.setSendDate(new Date());
            if (isConnectionAlive()) {
                String messageToSend = vo.getMessage();
                if (vo.getType().equals(Message.TYPE_VIDEO) || vo.getType().equals(Message.TYPE_IMAGE)) {
                    String urlFile = "";
                    String caption = "";

                    JSONObject jObject = null;
                    try {
                        jObject = new JSONObject(messageToSend);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (jObject != null) {
                        try {
                            urlFile = jObject.getString("u");
                            caption = jObject.getString("c");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    messageToSend = jsonMessage(caption, urlFile);

                    Log.i("message to send: ", messageToSend);
                }
                try {
                    if (vo.isGroupChat()) {
                    } else {
                        Chat xmppChat = createChat(vo.getDestination(),
                                vo.isGroupChat());
                        org.jivesoftware.smack.packet.Message msgObj = new org.jivesoftware.smack.packet.Message();
                        msgObj.setBody(messageToSend);
                        msgObj.setStanzaId(vo.getPacketId());
                        //msgObj.addExtension(deliveryRequestExtension);
                        DeliveryReceiptRequest.addTo(msgObj);
                        addPropertyToMessage(msgObj, properties);
                        xmppChat.sendMessage(msgObj);
                    }

                    vo.setStatus(Message.STATUS_SENT);
                    action = ACTION_MESSAGE_SENT;
                } catch (SmackException e) {
                    Log.e(getClass().getSimpleName(),
                            "Error sending data to server. " + e.getMessage());
                    vo.setStatus(Message.STATUS_FAILED);
                    action = ACTION_MESSAGE_FAILED;
                }
            } else {
                vo.setStatus(Message.STATUS_FAILED);
                action = ACTION_MESSAGE_FAILED;
            }
            onMessageProcessed(vo, action);
        }

        public void sendMessage(Message vo) throws SmackException {
            sendMessage(vo, null);
        }

        public Chat getChat(String userName) {
            return registeredChats.get(userName);
        }

        public Chat createChat(String userName, boolean groupChat) {
            Chat xmppChat = getChat(userName);
            if (xmppChat == null) {
                String destAddr = getBareJabberId(userName);
                if (groupChat) {
                    destAddr = userName + "@" + CONFERENCE_SERVER;
                }
/*
                xmppChat = xmppConnection.getChatManager().createChat(destAddr,
                        chatMsgListener);
*/
                xmppChat = ChatManager.getInstanceFor(xmppConnection).createChat(destAddr, chatMsgListener);
                registeredChats.put(userName, xmppChat);
            }
            return xmppChat;
        }

        private void addPropertyToMessage(
                org.jivesoftware.smack.packet.Message msg,
                Map<String, String> properties) {
            if (properties != null) {
                for (Iterator<String> iterator = properties.keySet().iterator(); iterator
                        .hasNext(); ) {
                    String key = iterator.next();
                    String value = properties.get(key);
                    if (value != null) {
                        //msg.setProperty(key, value);
                        JivePropertiesExtension jpe = new JivePropertiesExtension();
                        jpe.setProperty(key, value);
                        msg.addExtension(jpe);
                    }
                }
            }
        }
    };


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("HIDUP", "OnTaskRemoved MessengerConnectionService");
        stopSelf();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Utility.scheduleJob(this);
        } else {
            MessengerConnectionService mUploadService = new MessengerConnectionService();
            Intent mServiceIntent = new Intent(this, mUploadService.getClass());
            if (!isMyServiceRunning(mUploadService.getClass())) {
                this.startService(mServiceIntent);
            }

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 898989,
                    mServiceIntent, 0);
            int alarmType = AlarmManager.ELAPSED_REALTIME;
            final int FIFTEEN_SEC_MILLIS = 15000;
            AlarmManager alarmManager = (AlarmManager)
                    this.getSystemService(this.ALARM_SERVICE);
            alarmManager.setRepeating(alarmType, SystemClock.elapsedRealtime() + FIFTEEN_SEC_MILLIS,
                    FIFTEEN_SEC_MILLIS, pendingIntent);

            ComponentName receiver = new ComponentName(this, MyBroadcastReceiver.class);
            PackageManager pm = this.getPackageManager();

            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }

    protected boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i(MainActivityNew.class.getName(), "isMyServiceRunning? " + true + "");
                return true;
            }
        }

        Log.i(MainActivityNew.class.getName(), "isMyServiceRunning? " + false + "");
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        appendLog("lah ko Fisa Diskonek " + new Date().toString());
        presenceListener = null;
        disconnect();
        xmppConnection = null;

        registeredChats.clear();
        registeredGroupChats.clear();
        mucSubjectUpdatedListener = null;
        mucMessageListener = null;
        mucInvitationListener = null;
        mucParticipantStatusListener = null;
        presenceListener = null;
        pingListener = null;
        started = false;
        unregisterReceiver(receiver);
        unregisterReceiver(notifReceive);

        Intent broadcastIntent = new Intent("com.byonchat.android.utils.ConnectionChangeReceiver");
        sendBroadcast(broadcastIntent);

    }


    public Contact getCurContact() {
        if (curContact == null) {
            curContact = databaseHelper.getMyContact();
        }
        return curContact;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MessengerConnectionBinder(intent);
    }

    private void sendBroadcast(String action) {
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void disconnect() {
        //Log.d(TAG, "disconnect()");
        if (xmppConnection != null)
            xmppConnection.disconnect();
    }

    public static void startService(Context context) {
        Intent iservice = new Intent(context, MessengerConnectionService.class);
        context.startService(iservice);
    }

    public static void stopService(Context context) {
        Intent iservice = new Intent(context, MessengerConnectionService.class);
        context.stopService(iservice);
    }

    public void onMessageProcessed(Message vo, String action) {

        if (Utility.roomType(vo.getDestination()).equalsIgnoreCase("U")) {
            long totalMessages = 0;
            Cursor cursor = databaseHelper.query(
                    "SELECT count(*) total FROM "
                            + vo.TABLE_NAME
                            + " WHERE "
                            + vo.DESTINATION
                            + "=? OR "
                            + vo.SOURCE + "=? ",
                    new String[]{vo.getDestination(),
                            vo.getDestination()});
            int indexTotal = cursor.getColumnIndex("total");
            while (cursor.moveToNext()) {
                totalMessages = cursor.getLong(indexTotal);
            }
            cursor.close();

            if (totalMessages > 0) {
                if (vo.getId() < 1) {
                    databaseHelper.insertData(vo);
                } else {
                    databaseHelper.updateData(vo);
                }

                Intent intent = new Intent(action);
                intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                sendBroadcast(intent);
            } else {
                databaseHelper.deleteData(vo);
            }

        } else if (Utility.roomType(vo.getDestination()).equalsIgnoreCase("X")) {
            //cek tembakan
            databaseHelper.deleteData(vo);
        } else {
            if (vo.getId() < 1) {
                databaseHelper.insertData(vo);
            } else {
                databaseHelper.updateData(vo);
            }

            Intent intent = new Intent(action);
            intent.putExtra(KEY_MESSAGE_OBJECT, vo);
            sendBroadcast(intent);
        }


    }

    public void onMessageReceived(final Message vo/*,String aaa*/) {
        Log.w("alhamdulillah", "supaya satu");

        ArrayList<String> listblock = new ArrayList<String>();

        blockListDB = new BlockListDB(this);
        blockListDB.open();
        listblock = blockListDB.getBlockList();
        blockListDB.close();

        String name = vo.getSource();
        boolean send = true;


        for (String a : listblock) {
            if (name.equalsIgnoreCase(a)) {
                send = false;
            }
        }


        String additionalInfo = "";
        if (vo.isGroupChat()) {
            name = vo.getSourceInfo();
            if (databaseHelper.getMyContact().getJabberId().equals(name)) {
                name = "";
            }

            String gname = databaseHelper.getGroup(vo.getSource()).getName();
            if ("".equals(name)) {
                additionalInfo = "Group Chat '" + gname + "'";
            } else {
                additionalInfo = " on " + gname;
            }
        }


        if (!"".equals(name)) {
            Log.w("supaya1", "satu");
            Contact contact = databaseHelper.getContact(name);
            String regex = "[0-9]+";
            if (!name.matches(regex)) {
                Log.w("supaya2", "satu");
                name = Utility.roomName(getApplicationContext(), name, true);
                if (Utility.roomType(vo.getSource()).equalsIgnoreCase("X") && Utility.roomName(getApplicationContext(), vo.getSource(), false).equalsIgnoreCase("BYONCHATBACKGROUND")) {
                    Log.w("supaya3", "satu");
                    send = false;
                    if (isJSONValid(vo.getMessage())) {
                        Log.w("supaya4", "satu");
                        JSONObject jObject = null;
                        try {
                            jObject = new JSONObject(vo.getMessage());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (jObject != null) {
                            if (jObject.length() == 3) {
                                String username_room = "";
                                String id_rooms_tab = "";
                                String action = "";
                                try {
                                    username_room = jObject.getString("username_room");
                                    id_rooms_tab = jObject.getString("id_rooms_tab");
                                    action = jObject.getString("action");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                if (action.contains("deleteDB")) {
                                    String dbDelete[] = action.split(";");
                                    if (dbDelete.length == 3) {
                                        File newDB = new File(DataBaseDropDown.getDatabaseFolder() + dbDelete[2]);
                                        if (newDB.exists()) {
                                            newDB.delete();
                                            new requestDeleteDB().execute(dbDelete[1], dbDelete[2], username_room);
                                        }
                                    }
                                } else {
                                    new requestRefreshRoom(getApplicationContext()).execute(username_room, id_rooms_tab, action);
                                }
                            } else {
                                String title = "";
                                String desc = "";
                                String logo = "";
                                String back = "";
                                String logoHeader = "";
                                String color_code = "";
                                try {
                                    title = jObject.getString("nama");
                                    desc = jObject.getString("deskripsi");
                                    logo = jObject.getString("logo");
                                    color_code = jObject.getString("color_code");
                                    back = jObject.getString("background");
                                    logoHeader = jObject.getString("logo_chat");

                                    ArrayList<String> skinArrayList = new ArrayList<String>();
                                    IntervalDB db = new IntervalDB(getApplicationContext());
                                    db.open();
                                    skinArrayList = db.retriveallSkinTitle();
                                    db.close();
                                    boolean downloadSkin = true;
                                    for (String a : skinArrayList) {
                                        if (a.equalsIgnoreCase(title)) {
                                            downloadSkin = false;
                                        }
                                    }
                                    if (downloadSkin) {
                                        new DownloadImages(getApplicationContext(),
                                                title,
                                                desc,
                                                color_code,
                                                "https://" + MessengerConnectionService.HTTP_SERVER + "/uploads/skins/" + logo,
                                                "https://" + MessengerConnectionService.HTTP_SERVER + "/uploads/skins/" + logoHeader,
                                                "https://" + MessengerConnectionService.HTTP_SERVER + "/uploads/skins/" + back)
                                                .execute();
                                    } else {
                                        if (NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                                            String key = new ValidationsKey().getInstance(getApplicationContext()).key(true);
                                            if (!key.equalsIgnoreCase("null")) {
                                                new cekApply(getApplicationContext()).execute(key, title);
                                            }
                                        }
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    } else {
                        Log.w("supaya10", "satu");
                        String pesan[] = vo.getMessage().split(";");
                        if (pesan.length == 2) {
                            Log.w("supaya20", pesan[0]);
                            if (pesan[0].equalsIgnoreCase("hapus")) {
                                new GetRealNameRoom().getInstance(getApplicationContext()).deleteRoomName(pesan[1]);
                                Cursor cur = botListDB.getSingle(pesan[1]);
                                String jbId = "";
                                if (cur.getCount() > 0) {
                                    jbId = cur.getString(cur.getColumnIndex(BotListDB.BOT_NAME));
                                    if (jbId.length() > 0) {
                                        botListDB.delete(jbId);

                                    }
                                }
                                databaseHelper
                                        .deleteRows(
                                                Message.TABLE_NAME,
                                                " destination=? OR source =? ",
                                                new String[]{pesan[1],
                                                        pesan[1]}
                                        );
                                cur.close();
                                Intent intent = new Intent(ACTION_REFRESH_CHAT_HISTORY);
                                sendOrderedBroadcast(intent, null);
                            }
                        } else if (pesan.length == 3) {
                            Log.w("supaya30", "susus");
                            if (pesan[0].equalsIgnoreCase("chat_off")) {
                                Log.w("supaya30", "off");
                                IntervalDB db = new IntervalDB(getApplicationContext());
                                db.open();
                                Cursor cursorSelect = db.getSingleContact(24);
                                if (cursorSelect.getCount() > 0) {

                                } else {
                                    Interval interval = new Interval();
                                    interval.setId(24);
                                    interval.setTime("off");
                                    db.createContact(interval);
                                }
                                Intent intent = new Intent(ACTION_CHAT_OFF);
                                sendOrderedBroadcast(intent, null);
                            } else if (pesan[0].equalsIgnoreCase("chat_on")) {
                                Log.w("supaya30", "on");
                                IntervalDB db = new IntervalDB(getApplicationContext());
                                db.open();
                                Cursor cursorSelect = db.getSingleContact(24);
                                if (cursorSelect.getCount() > 0) {
                                    db.deleteContact(24);
                                }
                                Intent intent = new Intent(ACTION_CHAT_OFF);
                                sendOrderedBroadcast(intent, null);
                            } else if (pesan[0].equalsIgnoreCase("give_me_location")) {
                                Log.w("supayaBisa", "on");
                                Intent intent = new Intent(ACTION_REQGPS);
                                intent.putExtra(KEY_LOC_REQ, vo.getMessage());
                                sendOrderedBroadcast(intent, null);
                            } else if (pesan[0].equalsIgnoreCase("give_me_req")) {
                                SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                                String regId = pref.getString("regId", null);

                                Message report = new Message(databaseHelper.getMyContact().getJabberId(), "x_byonchatbackground", "firebase_id : " + regId);
                                report.setType("text");
                                report.setSendDate(new Date());
                                report.setStatus(Message.STATUS_INPROGRESS);
                                report.generatePacketId();

                                Intent i = new Intent();
                                i.setAction(UploadService.FILE_SEND_INTENT);
                                i.putExtra(KEY_MESSAGE_OBJECT, report);
                                sendBroadcast(i);

                            } else if (pesan[0].trim().equalsIgnoreCase("send_sms")) {
                                try {
                                    JSONObject cek = new JSONObject(pesan[1]);
                                    String number = cek.getString("n");
                                    String message = cek.getString("m");

                                    SmsManager sms = SmsManager.getDefault();
                                    int requestID = (int) System.currentTimeMillis();
                                    PendingIntent piDelivered = PendingIntent.getBroadcast(this, requestID, new Intent(SMS_DELIVERED).putExtra("number", number).putExtra("id", pesan[2]), PendingIntent.FLAG_UPDATE_CURRENT);
                                    PendingIntent piSent = PendingIntent.getBroadcast(MessengerConnectionService.this, requestID, new Intent(SMS_SENT).putExtra("number", number).putExtra("id", pesan[2]), PendingIntent.FLAG_UPDATE_CURRENT);
                                    sms.sendTextMessage(number, null, message, piSent, piDelivered);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (pesan[0].trim().equalsIgnoreCase("hide_att")) {
                                Message report = new Message(databaseHelper.getMyContact().getJabberId(), "x_byonchatbackground", "hide");
                                report.setType("text");
                                report.setSendDate(new Date());
                                report.setStatus(Message.STATUS_INPROGRESS);
                                report.generatePacketId();

                                Intent i = new Intent();
                                i.setAction(UploadService.FILE_SEND_INTENT);
                                i.putExtra(KEY_MESSAGE_OBJECT, report);
                                sendBroadcast(i);
                            } else if (pesan[0].trim().equalsIgnoreCase("create_shortcut")) {
                                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_soldier);

                                Intent aa = new Intent(getApplicationContext(), WelcomeActivitySMS.class);
                                Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
                                shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "SMS Soldier");
                                shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON, MediaProcessingUtil.getRoundedCornerBitmap(largeIcon, 30));
                                shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, aa);
                                sendBroadcast(shortcutintent);

                            }
                        } else {
                            Log.w("supaya40", "quick");
                            if (vo.getMessage().equalsIgnoreCase("refresh_membership")) {
                                String key = new ValidationsKey().getInstance(getApplicationContext()).key(false);
                                if (key.equalsIgnoreCase("null")) {
                                } else {
                                    new requestMemberCard(getApplicationContext()).execute(key);
                                }
                            } else if (vo.getMessage().equalsIgnoreCase("refresh_offers")) {
                                String key = new ValidationsKey().getInstance(getApplicationContext()).key(false);
                                if (key.equalsIgnoreCase("null")) {
                                } else {
                                    new requestRefreshOffers(getApplicationContext()).execute(key);
                                }
                            }
                        }
                    }
                }
                if (vo.getMessage().equalsIgnoreCase("") || vo.getMessage() == null || vo.getMessage().equalsIgnoreCase("<br />") || vo.getMessage().equalsIgnoreCase("<br/>")) {
                    send = false;
                    Log.w("supaya50", "quick");
                } else {
                    Log.w("supaya60", "quick");
                    String pesanDariBot[] = vo.getMessage().split("8==D");

                    if (pesanDariBot.length == 2) {
                        Log.w("supaya70", "quick");
                        if (isJSONValid(pesanDariBot[0])) {
                            Log.w("supaya80", "quick");
                            JSONObject jObject = null;
                            try {
                                jObject = new JSONObject(pesanDariBot[0]);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (jObject != null) {
                                String u = "";
                                String c = "";
                                String t = "";
                                try {
                                    u = jObject.getString("u");
                                    c = jObject.getString("c");
                                    t = jObject.getString("t");
                                    if (t.equalsIgnoreCase("0")) {
                                        vo.setMessage(c);
                                        vo.setType(Message.TYPE_TEXT);
                                    } else if (t.equalsIgnoreCase("1")) {
                                        vo.setMessage(jsonMessage(c + pesanDariBot[1], u));
                                        vo.setType(Message.TYPE_IMAGE);
                                    } else if (t.equalsIgnoreCase("2")) {
                                        vo.setMessage(jsonMessage(c + pesanDariBot[1], u));
                                        vo.setType(Message.TYPE_VIDEO);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        Log.w("supaya80", "quick");
                    }
                }
            } else {
                Log.w("supaya90", "quick");
                name = Utility.roomName(getApplicationContext(), name, true);
            }
            if (contact != null) {
                name = contact.getName();
            }
        }
        Boolean insert = true;
        if (send) {
            if (vo.getType().equalsIgnoreCase(Message.TYPE_TEXT) && vo.getMessage().startsWith("bc://")) {
                String regex = "[0-9]+";
                if (!name.matches(regex)) {
                    String room[] = vo.getMessage().split("//");
                    if (room[1].equalsIgnoreCase("1_277091610admin")) {
                        insert = false;
                    }
                    if (room.length == 4) {
                        try {
                            JSONArray jsonArrays = new JSONArray(room[3]);
                            if (jsonArrays.length() == 2) {
                                JSONObject jsonObject = jsonArrays.getJSONObject(0);
                                String id = jsonObject.getString("id");
                                String parent_id = jsonObject.getString("parent_id");
                                String add_date = jsonObject.getString("add_date");

                                JSONObject jsonObjectA = jsonArrays.getJSONObject(1);
                                JSONObject caca = jsonObjectA.getJSONObject("value_detail");

                                String type = caca.getString("type");
                                String values = caca.getString("value");

                                Cursor cursorParent = botListDB.getSingleRoomDetailFormWithFlag(id + "|" + parent_id, room[1], room[2], "parent");

                                if (cursorParent.getCount() == 0) {
                                    RoomsDetail orderModel = new RoomsDetail(id + "|" + parent_id, room[2], room[1], add_date, "4", "", "parent");
                                    botListDB.insertRoomsDetail(orderModel);

                                    RoomsDetail orderModelTitle211 = new RoomsDetail(id + "|" + parent_id, room[2], room[1], values, "1", type, "list");
                                    RoomsDetail orderModelTitle2 = new RoomsDetail(id + "|" + parent_id, room[2], room[1], jsonDuaObject(va(orderModelTitle211), ""), "1", type, "list");
                                    botListDB.insertRoomsDetail(orderModelTitle2);

                                    JSONObject jsonObjectI = new JSONObject();
                                    try {
                                        jsonObjectI.put("idDetail", id + "|" + parent_id);
                                        jsonObjectI.put("username", room[1]);
                                        jsonObjectI.put("idTab", room[2]);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    Message message = new Message();
                                    message.setMessage(jsonObjectI.toString());
                                    message.setId(Integer.valueOf(room[2]));

                                    Intent intent = new Intent(getApplicationContext(), UploadService.class);
                                    intent.putExtra(UploadService.ACTION, "downloadValueForm");
                                    intent.putExtra(UploadService.KEY_MESSAGE, message);
                                    startService(intent);

                                }


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    if(room.length == 5){
                        if(room[4].equalsIgnoreCase("urgent")){
                            DialogAct.startDialog(getApplicationContext(),60000,room[1],room[2],room[3],0);
                        }

                        databaseHelper.execSql("INSERT INTO tab_menu_badge (id_tab, jid, message) VALUES (?,?,?)",
                                new String[]{room[2],databaseHelper.getMyContact().getJabberId(),room[3]});
                        vo.setMessage(room[3]);

                        Intent intent = new Intent(ACTION_REFRESH_NOTIF_FORM);
                        intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                        intent.putExtra("TYPE_XZ","yes");
                        intent.putExtra(KEY_CONTACT_NAME, name + additionalInfo);
                        sendOrderedBroadcast(intent, null);
                        return;
                    }


                    String SQL_UPDATE_MESSAGES = "Delete from " + Message.TABLE_NAME + " WHERE " + Message.MESSAGE + " = ?;";
                    String SQL_SELECT_MESSAGES = "SELECT *  FROM "
                            + Message.TABLE_NAME + " WHERE " + Message.MESSAGE + "=? ;";
                    Cursor cursor = databaseHelper.query(SQL_SELECT_MESSAGES, new String[]{vo.getMessage()});

                    Message voc = null;
                    while (cursor.moveToNext()) {
                        voc = new Message(cursor);
                        if (voc != null) {
                            databaseHelper.execSql(SQL_UPDATE_MESSAGES, new String[]{vo.getMessage()});
                            Intent intent = new Intent(ACTION_REFRESH_CHAT_HISTORY);
                            intent.putExtra(KEY_MESSAGE_OBJECT, voc);
                            sendOrderedBroadcast(intent, null);
                        }

                    }
                    cursor.close();
                }
            }

            if (insert)
                databaseHelper.insertData(vo);
            if (vo.getType().equalsIgnoreCase(Message.TYPE_TEXT) ||
                    vo.getType().equalsIgnoreCase(Message.TYPE_LOC) ||
                    vo.getType().equalsIgnoreCase(Message.TYPE_BROADCAST)) {
                if (vo.getSource().equals("askhonda_bot")) {
                    String source = null, isi_msg = null;
                    try {
                        JSONObject jO = new JSONObject(vo.getMessage());
                        source = jO.getString("bc_user");
                        isi_msg = jO.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Message nm = new Message(source, databaseHelper.getMyContact().getJabberId(), isi_msg);
                    nm.setDeliveredDate(vo.getDeliveredDate());
                    nm.setId(vo.getId());
                    nm.setSendDate(vo.getSendDate());
                    databaseHelper.updateData(nm);

                    String namo = source;
                    if (namo.matches("[0-9]+")) {
                        Contact contact = databaseHelper.getContact(namo);
                        namo = contact.getName();
                    }
                    Intent intent = new Intent(ACTION_MESSAGE_RECEIVED);
                    intent.putExtra(KEY_MESSAGE_OBJECT, nm);
                    intent.putExtra(KEY_CONTACT_NAME, namo + additionalInfo);
                    sendOrderedBroadcast(intent, null);
                } else if (vo.getSource().equals("server_get_location")) {
                    if (vo.getMessage().matches("latlong(.*)")) {
                        String[] separated = vo.getMessage().split(";");
                        Log.w("AHAHA", separated[1] + "   ----    " + separated[2]);
                        double lati = Double.parseDouble(separated[1]);
                        double longi = Double.parseDouble(separated[2]);
                        Message nm = new Message(databaseHelper.getMyContact().getJabberId(), vo.getSource(), getAddress(lati, longi));
                        nm.setDeliveredDate(vo.getDeliveredDate());
                        nm.setId(vo.getId());
                        nm.setSendDate(vo.getSendDate());
                        databaseHelper.updateData(nm);

                        Intent intent = new Intent(ACTION_MESSAGE_RECEIVED);
                        intent.putExtra(KEY_MESSAGE_OBJECT, nm);
                        intent.putExtra(KEY_CONTACT_NAME, name + additionalInfo);
                        sendOrderedBroadcast(intent, null);

                        try {
                            sendMessage(nm);
                        } catch (SmackException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Intent intent = new Intent(ACTION_MESSAGE_RECEIVED);
                    intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                    intent.putExtra(KEY_CONTACT_NAME, name + additionalInfo);
                    sendOrderedBroadcast(intent, null);
                }
            } else if (vo.getType().equalsIgnoreCase(Message.TYPE_IMAGE)) {
                String regex = "[0-9]+";
                if (!vo.getSource().matches(regex)) {
                    //rooms
                    Intent intent = new Intent(ACTION_MESSAGE_RECEIVED);
                    intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                    intent.putExtra(KEY_CONTACT_NAME, name + additionalInfo);
                    sendOrderedBroadcast(intent, null);
                } else {
                    JSONObject jObject = null;
                    String urlFile = "";
                    try {
                        jObject = new JSONObject(vo.getMessage());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (jObject != null) {
                        try {
                            urlFile = jObject.getString("u");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    final String finalUrlFile = urlFile;
                    final String finalName = name;
                    final String finalAdditionalInfo = additionalInfo;
                    RequestKeyTask testAsyncTask = new RequestKeyTask(new TaskCompleted() {
                        @Override
                        public void onTaskDone(String key) {
                            if (!key.equalsIgnoreCase("null")) {
                                RequestUploadSite testAsyncTask = new RequestUploadSite(new TaskCompleted() {
                                    @Override
                                    public void onTaskDone(String key) {
                                        if (key.contains("byonchat.com")) {
                                            String aa = key.replace("/v2/uploaded/thb/", MessengerConnectionService.DOWNLOAD_LINK_THUMB);
                                            Message bb = new Message("", "", aa);
                                            new FileDownloadHandlerImageThumbP2PSave().execute(new Message[]{vo, bb});

                                        }
                                    }
                                }, getApplicationContext(), key, finalUrlFile, RequestUploadSite.REQUEST_KEYS_URL_Thum);
                                testAsyncTask.execute();

                            } else {
                                Intent intent = new Intent(ACTION_MESSAGE_RECEIVED);
                                intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                                intent.putExtra(KEY_CONTACT_NAME, finalName + finalAdditionalInfo);
                                sendOrderedBroadcast(intent, null);
                            }
                        }
                    }, getApplicationContext());
                    testAsyncTask.execute();
                }
            } else if (vo.getType().equalsIgnoreCase(Message.TYPE_VIDEO)) {
                //lagi editing video
                String regex = "[0-9]+";
                if (!vo.getSource().matches(regex)) {

                    //rooms
                    JSONObject jObject = null;
                    String urlFile = "";
                    try {
                        jObject = new JSONObject(vo.getMessage());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (jObject != null) {
                        try {
                            urlFile = jObject.getString("u");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    String url = urlFile.substring(0, urlFile.lastIndexOf("."));
                    String finalFileServer = url + "_thumb.jpg";

                    Message bb = new Message("", "", finalFileServer);
                    new FileDownloadHandlerImageTumbP2PSave().execute(new Message[]{vo, bb});

                } else {
                    JSONObject jObject = null;
                    String urlFile = "";
                    try {
                        jObject = new JSONObject(vo.getMessage());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (jObject != null) {
                        try {
                            urlFile = jObject.getString("u");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    String[] url = urlFile.split("\\.");
                    final String finalFileServer = url[0] + ".jpg";
                    final String finalName1 = name;
                    final String finalAdditionalInfo1 = additionalInfo;
                    RequestKeyTask testAsyncTask = new RequestKeyTask(new TaskCompleted() {
                        @Override
                        public void onTaskDone(String key) {
                            if (!key.equalsIgnoreCase("null")) {
                                RequestUploadSite testAsyncTask = new RequestUploadSite(new TaskCompleted() {
                                    @Override
                                    public void onTaskDone(String key) {
                                        if (key.contains("byonchat.com")) {
                                            String aa = key.replace("/v2/uploaded/thb/", MessengerConnectionService.DOWNLOAD_LINK_THUMB);
                                            Message bb = new Message("", "", aa);
                                            new FileDownloadHandlerImageTumbP2PSave().execute(new Message[]{vo, bb});
                                        }
                                    }
                                }, getApplicationContext(), key, finalFileServer, RequestUploadSite.REQUEST_KEYS_URL_Thum);
                                testAsyncTask.execute();

                            } else {
                                Intent intent = new Intent(ACTION_MESSAGE_RECEIVED);
                                intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                                intent.putExtra(KEY_CONTACT_NAME, finalName1 + finalAdditionalInfo1);
                                sendOrderedBroadcast(intent, null);
                            }
                        }
                    }, getApplicationContext());
                    testAsyncTask.execute();
                }
            } else if (vo.getType().equalsIgnoreCase(Message.TYPE_READSTATUS)) {
                databaseHelper.execSql(SQL_UPDATE_MESSAGES, new String[]{vo.getMessage()});
                databaseHelper.execSql(SQL_REMOVE_MESSAGES_STATUS, new String[]{vo.getPacketId()});

                String SQL_SELECT_MESSAGES = "SELECT *  FROM "
                        + Message.TABLE_NAME + " WHERE " + Message.PACKET_ID + "=? ;";
                Cursor cursor = databaseHelper.query(SQL_SELECT_MESSAGES, new String[]{vo.getMessage()});

                Message voc = null;
                while (cursor.moveToNext()) {
                    voc = new Message(cursor);
                }
                cursor.close();

                if (voc != null) {
                    if (voc.getType().equals(Message.TYPE_REPORT_TARIK)) {
                        voc.setMessage("Your message has been recalled successfully and has disappreared in the recepient's device");
                        databaseHelper.updateData(voc);
                    }
                    Intent intent = new Intent(ACTION_MESSAGE_DELIVERED);
                    intent.putExtra(KEY_MESSAGE_OBJECT, voc);
                    sendBroadcast(intent);
                }
            } else if (vo.getType().equalsIgnoreCase(Message.TYPE_TARIK)) {
                databaseHelper.execSql(SQL_REMOVE_MESSAGES_STATUS, new String[]{vo.getPacketId()});
                Message report = new Message(databaseHelper.getMyContact().getJabberId(), vo.getSource(), vo.getMessage());
                report.setType(Message.TYPE_READSTATUS);
                report.setSendDate(new Date());
                report.setStatus(Message.STATUS_INPROGRESS);
                report.generatePacketId();

                Intent i = new Intent();
                i.setAction(UploadService.FILE_SEND_INTENT);
                i.putExtra(KEY_MESSAGE_OBJECT, report);
                sendBroadcast(i);


                String SQL_SELECT_MESSAGES = "SELECT *  FROM "
                        + Message.TABLE_NAME + " WHERE " + Message.PACKET_ID + "=? ;";
                Cursor cursor = databaseHelper.query(SQL_SELECT_MESSAGES, new String[]{vo.getMessage()});
                Message voc = null;
                while (cursor.moveToNext()) {
                    voc = new Message(cursor);
                }
                cursor.close();

                if (voc != null) {

                    databaseHelper.execSql(SQL_REMOVE_MESSAGES_STATUS, new String[]{vo.getMessage()});
                    Intent intent = new Intent(ACTION_REFRESH_CHAT_HISTORY);
                    intent.putExtra(KEY_MESSAGE_OBJECT, voc);
                    sendOrderedBroadcast(intent, null);
                }


            }

        } else {
            Log.w("supaya100", "quick");
        }
    }

    private String jsonDuaObject(String a, String b) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("aa", a);
            obj.put("bb", b);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }


    public String va(RoomsDetail roomsDetail) {
        String content = roomsDetail.getContent();

        if (roomsDetail.getFlag_tab().equalsIgnoreCase("rear_camera") || roomsDetail.getFlag_tab().equalsIgnoreCase("front_camera")) {
            Random random = new SecureRandom();
            char[] result = new char[6];
            char[] CHARSET_AZ_09 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
            for (int i = 0; i < result.length; i++) {
                int randomCharIndex = random.nextInt(CHARSET_AZ_09.length);
                result[i] = CHARSET_AZ_09[randomCharIndex];
            }
            content = "IMG_" + new String(result);
        } else if (roomsDetail.getFlag_tab().equalsIgnoreCase("map")) {
            String[] latlong = content.split(
                    Message.LOCATION_DELIMITER);
            if (latlong.length > 4) {
                String text = "<u><b>" + (String) latlong[2] + "</b></u><br/>";
                content = text + latlong[3];
            }
        } else if (roomsDetail.getFlag_tab().equalsIgnoreCase("input_kodepos")) {
            content = jsonResultType(content, "a");
        } else if (roomsDetail.getFlag_tab().equalsIgnoreCase("dropdown_wilayah")) {
            content = jsonResultType(content, "b") + " , " + jsonResultType(content, "c") + " , " + jsonResultType(content, "d") + " , " + jsonResultType(content, "e") + " , " + jsonResultType(content, "a");
        } else if (roomsDetail.getFlag_tab().equalsIgnoreCase("checkbox")) {
            if (!content.startsWith("[")) {
                content = "[" + content + "]";
            }
            JSONArray jsA = null;
            try {
                jsA = new JSONArray(content);
                if (jsA.length() > 0) {
                    content = jsA.getJSONObject(0).getString("c").toString();
                }
            } catch (JSONException e) {
                content = "";
                e.printStackTrace();
            }
        } else if (roomsDetail.getFlag_tab().equalsIgnoreCase("image_load")) {
            content = "image load";
        } else if (roomsDetail.getFlag_tab().equalsIgnoreCase("ocr")) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(content);
                Iterator<String> keys = jsonObject.keys();
                String aa = jsonObject.get(keys.next()).toString();
                content = aa;
            } catch (JSONException e) {
                e.printStackTrace();
                content = "ocr";
            }
        } else if (roomsDetail.getFlag_tab().equalsIgnoreCase("dropdown_dinamis") || jsonResultType(roomsDetail.getFlag_content(), "b").equalsIgnoreCase("new_dropdown_dinamis")) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(content);
                Iterator<String> keys = jsonObject.keys();
                String aa = jsonObject.get(keys.next()).toString();
                content = aa;
            } catch (JSONException e) {
                e.printStackTrace();
                content = "";
            }
        } else if (roomsDetail.getFlag_tab().equalsIgnoreCase("upload_document")) {
            content = jsonResultType(content, "a");
        } else if (roomsDetail.getFlag_tab().equalsIgnoreCase("signature")) {
            content = "signature";
        } else if (roomsDetail.getFlag_tab().equalsIgnoreCase("distance_estimation")) {
            content = jsonResultType(content, "d");
        } else if (roomsDetail.getFlag_tab().equalsIgnoreCase("rate")) {

        }


        return content;
    }


    public static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    class requestMemberCard extends AsyncTask<String, Void, String> {
        MembersDB membersDB;
        ItemListMemberCard itemListMemberCard;
        ArrayList<ItemListMemberCard> listMemberCards;
        private MessengerDatabaseHelper messengerHelper;

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();

        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content = null;
        private boolean error = false;
        private Context mContext;

        public requestMemberCard(Context context) {
            this.mContext = context;
            if (membersDB == null) {
                membersDB = new MembersDB(mContext);
            }
            if (messengerHelper == null) {
                messengerHelper = MessengerDatabaseHelper.getInstance(mContext);
            }
        }


        @Override
        protected void onPreExecute() {
        }

        protected String doInBackground(String... key) {
            try {

                HttpClient httpClient = HttpHelper
                        .createHttpClient(mContext);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                nameValuePairs.add(new BasicNameValuePair("username", messengerHelper.getMyContact().getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("key", key[0]));

                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(URL_GET_MEMBERS);
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                //Response from the Http Request
                response = httpclient.execute(post);
                StatusLine statusLine = response.getStatusLine();

                //Check the Http Request for success

                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    content = out.toString();
                    JSONObject result = new JSONObject(content);
                    JSONArray menuitemArray = result.getJSONArray("items");
                    membersDB.open();
                    listMemberCards = membersDB.retriveallMembers();
                    membersDB.delete();
                    // listMemberCards = new ArrayList<ItemListMemberCard>();

                    for (int i = 0; i < menuitemArray.length(); i++) {
                        String id = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("id").toString()));
                        String name = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("nama").toString()));
                        String color = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("warna").toString()));

                        boolean notif = true;
                        for (ItemListMemberCard cek : listMemberCards) {
                            if (cek.getName().equalsIgnoreCase(name)) {
                                notif = false;
                            }
                        }

                        Intent intent = new Intent(ACTION_ADD_CARD);
                        if (notif) {
                            intent.putExtra(KEY_CONTACT_NAME, id + ";" + name + ";#" + color);
                        }
                        sendOrderedBroadcast(intent, null);

                        itemListMemberCard = new ItemListMemberCard(id, name, color);
                        membersDB.insertMembers(itemListMemberCard);
                    }

                    membersDB.close();
                } else {
                    //Closes the connection.
                    error = true;
                    // Log.w("HTTP1:", statusLine.getReasonPhrase());
                    content = statusLine.getReasonPhrase();
                    response.getEntity().getContent().close();
                    throw new IOException(content);
                }

            } catch (ClientProtocolException e) {
                // Log.w("HTTP2:", e);
                content = e.getMessage();
                error = true;
            } catch (IOException e) {
                // Log.w("HTTP3:", e);
                content = e.getMessage();
                error = true;
            } catch (Exception e) {
                error = true;
            }

            return content;
        }

        protected void onCancelled() {
        }

        protected void onPostExecute(String content) {
            if (error) {
                if (content.contains("invalid_key")) {
                    if (NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)) {
                        String key = new ValidationsKey().getInstance(mContext).key(true);
                        if (key.equalsIgnoreCase("null")) {
                        } else {
                            new requestMemberCard(mContext).execute(key);
                        }
                    }
                }
            }
        }
    }

    class requestRefreshRoom extends AsyncTask<String, Void, String> {
        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();

        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content = null;
        private boolean error = false;
        private Context mContext;
        private MessengerDatabaseHelper messengerHelper;
        OffersDB offersDB;

        public requestRefreshRoom(Context context) {
            this.mContext = context;
            if (messengerHelper == null) {
                messengerHelper = MessengerDatabaseHelper.getInstance(mContext);
            }
            if (offersDB == null) {
                offersDB = new OffersDB(context);
            }
        }


        @Override
        protected void onPreExecute() {
        }

        protected String doInBackground(String... key) {
            try {
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
                HttpConnectionParams.setSoTimeout(httpParameters, 20000);
                HttpClient httpclient = new DefaultHttpClient(httpParameters);

                String uri = new ValidationsKey().getInstance(getApplicationContext()).getTargetUrl(key[0]);

                if (key[1].equalsIgnoreCase("null") || key[1] == null) {
                    Cursor cur = botListDB.getSingleRoom(key[0]);
                    if (cur.getCount() > 0) {
                        uri = jsonResultType(cur.getString(cur.getColumnIndex(BotListDB.ROOM_COLOR)), "e");
                    }
                } else {
                    uri = key[1];
                }

                HttpPost httppost = new HttpPost(uri + "/bc_voucher_client/webservice/get_tab_rooms.php");

                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("username_room", key[0]));
                MessengerDatabaseHelper messengerHelper = null;
                if (messengerHelper == null) {
                    messengerHelper = MessengerDatabaseHelper.getInstance(getApplicationContext());
                }

                Contact contact = messengerHelper.getMyContact();
                nameValuePairs.add(new BasicNameValuePair("bc_user", contact.getJabberId()));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();
                //Check the Http Request for success

                if (status == HttpStatus.SC_OK) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);
                    try {
                        JSONObject jsonRootObject = new JSONObject(data);
                        String username = jsonRootObject.getString("username_room");
                        String content = jsonRootObject.getString("tab_room");
                        String realname = jsonRootObject.getString("nama_display");
                        String icon = jsonRootObject.getString("icon");
                        String backdrop = jsonRootObject.getString("backdrop");
                        String color = jsonRootObject.getString("color");
                        String lastUpdate = jsonRootObject.getString("last_update");
                        String firstTab = jsonRootObject.getString("current_tab");
                        String textColor = jsonRootObject.getString("color_text");
                        String description = jsonRootObject.getString("description");
                        String officer = jsonRootObject.getString("officer");

                        botListDB.deleteRoomsbyTAB(username);
                        String lu = "";
                        Cursor cursor = botListDB.getSingleRoom(username);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Calendar cal = Calendar.getInstance();
                        String time_str = dateFormat.format(cal.getTime());
                        if (cursor.getCount() > 0) {
                            lu = cursor.getString(cursor.getColumnIndexOrThrow(BotListDB.ROOM_LASTUPDATE));
                            if (!lu.equalsIgnoreCase(lastUpdate)) {
                                botListDB.deleteRoomsbyTAB(username);
                                Rooms rooms = new Rooms(username, realname, content, jsonCreateType(color, textColor, description, officer, uri), backdrop, lastUpdate, icon, firstTab, time_str);
                                botListDB.insertRooms(rooms);
                            }
                        } else {
                            Rooms rooms = new Rooms(username, realname, content, jsonCreateType(color, textColor, description, officer, uri), backdrop, lastUpdate, icon, firstTab, time_str);
                            botListDB.insertRooms(rooms);
                        }
                        cursor.close();

                        JSONObject obj = new JSONObject();

                        try {
                            obj.put("a", username);
                            obj.put("b", realname);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent(ACTION_REFRESH_ROOM);
                        intent.putExtra(KEY_CONTACT_NAME, obj.toString());
                        sendOrderedBroadcast(intent, null);


                    } catch (JSONException e) {
                        e.printStackTrace();
                        // Log.w("HTTP3:", e);
                        content = e.getMessage();
                        error = true;
                    }
                } else {
                    //Closes the connection.
                    error = true;
                    response.getEntity().getContent().close();
                    throw new IOException(content);
                }

            } catch (ClientProtocolException e) {
                // Log.w("HTTP2:", e);
                content = e.getMessage();
                error = true;
            } catch (IOException e) {
                // Log.w("HTTP3:", e);
                content = e.getMessage();
                error = true;
            } catch (Exception e) {
                error = true;
            }

            return content;
        }

        protected void onCancelled() {
        }

        protected void onPostExecute(String content) {
            if (error) {

            } else {
                Intent intent = new Intent(ACTION_REFRESH_CHAT_HISTORY);
                sendOrderedBroadcast(intent, null);
            }
        }
    }

    class requestDeleteDB extends AsyncTask<String, Integer, Boolean> {

        String username = "";

        public requestDeleteDB() {
        }

        @Override
        protected void onPreExecute() {
        }

        protected Boolean doInBackground(String... param) {
            try {
                File dbDownloadPath = new File(DataBaseDropDown.getDatabaseFolder());
                if (!dbDownloadPath.exists()) {
                    dbDownloadPath.mkdirs();
                }
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
                HttpConnectionParams.setSoTimeout(httpParameters, 5000);
                DefaultHttpClient client = new DefaultHttpClient(httpParameters);
                HttpGet httpGet = new HttpGet(param[0] + param[1]);
                username = param[2];
                InputStream content = null;
                try {
                    HttpResponse execute = client.execute(httpGet);
                    if (execute.getStatusLine().getStatusCode() != 200) {
                        return null;
                    }
                    content = execute.getEntity().getContent();
                    FileOutputStream fos = new FileOutputStream(DataBaseDropDown.getDatabaseFolder() + param[1]);
                    byte[] buffer = new byte[256];
                    int read;
                    while ((read = content.read(buffer)) != -1) {
                        fos.write(buffer, 0, read);
                    }
                    fos.flush();
                    fos.close();
                    content.close();
                    return true;
                } catch (Exception e) {
                    if (content != null) {
                        try {
                            content.close();
                        } catch (IOException e1) {
                        }
                    }
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }

        protected void onCancelled() {
        }

        protected void onPostExecute(Boolean content) {
            if (content) {
                JSONObject obj = new JSONObject();

                try {
                    obj.put("a", "DATABASE");
                    obj.put("b", username);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Intent intent = new Intent(ACTION_REFRESH_ROOM);
                intent.putExtra(KEY_CONTACT_NAME, obj.toString());
                sendOrderedBroadcast(intent, null);

            }
        }
    }

    private String jsonCreateType(String idContent, String type, String desc, String of, String tatge) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("a", idContent);
            obj.put("b", type);
            obj.put("c", desc);
            obj.put("d", of);
            obj.put("e", tatge);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }


    class requestRefreshOffers extends AsyncTask<String, Void, String> {
        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();

        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content = null;
        private boolean error = false;
        private Context mContext;
        private MessengerDatabaseHelper messengerHelper;
        OffersDB offersDB;

        public requestRefreshOffers(Context context) {
            this.mContext = context;
            if (messengerHelper == null) {
                messengerHelper = MessengerDatabaseHelper.getInstance(mContext);
            }
            if (offersDB == null) {
                offersDB = new OffersDB(context);
            }
        }


        @Override
        protected void onPreExecute() {
        }

        protected String doInBackground(String... key) {
            try {

                HttpClient httpClient = HttpHelper
                        .createHttpClient(mContext);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                nameValuePairs.add(new BasicNameValuePair("username", messengerHelper.getMyContact().getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("key", key[0]));

                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(URL_GET_OFFERS);
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                //Response from the Http Request
                response = httpclient.execute(post);
                StatusLine statusLine = response.getStatusLine();

                //Check the Http Request for success

                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    content = out.toString();
                    JSONObject result = new JSONObject(content);
                    JSONArray menuitemArray = result.getJSONArray("offers");
                    offersDB.open();
                    offersDB.deleteOffers();
                    for (int i = 0; i < menuitemArray.length(); i++) {
                        String a = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("id").toString()));
                        String b = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("judul").toString()));
                        String c = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("value").toString()));
                        String d = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("type").toString()));
                        String e = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("sub").toString()));
                        String f = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("detail").toString()));
                        String[] myString;
                        Random rgenerator = new Random();
                        Resources res = getResources();
                        myString = res.getStringArray(R.array.colorOffers);
                        String q = myString[rgenerator.nextInt(myString.length)];
                        OffersModel model = new OffersModel(Long.parseLong(a), b, c, d, e, f, "", "1", q);
                        offersDB.insertOffers(model);
                    }
                    offersDB.close();
                } else {
                    //Closes the connection.
                    error = true;
                    // Log.w("HTTP1:", statusLine.getReasonPhrase());
                    content = statusLine.getReasonPhrase();
                    response.getEntity().getContent().close();
                    throw new IOException(content);
                }

            } catch (ClientProtocolException e) {
                // Log.w("HTTP2:", e);
                content = e.getMessage();
                error = true;
            } catch (IOException e) {
                // Log.w("HTTP3:", e);
                content = e.getMessage();
                error = true;
            } catch (Exception e) {
                error = true;
            }

            return content;
        }

        protected void onCancelled() {
        }

        protected void onPostExecute(String content) {
            if (error) {
                if (content.contains("invalid_key")) {
                    if (NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)) {
                        String key = new ValidationsKey().getInstance(mContext).key(true);
                        if (key.equalsIgnoreCase("null")) {
                        } else {
                            new requestRefreshOffers(mContext).execute(key);
                        }
                    }
                }
            } else {
                Intent intent = new Intent(ACTION_REFRESH_CHAT_HISTORY);
                sendOrderedBroadcast(intent, null);
            }
        }
    }


    public class DownloadImages extends AsyncTask {
        String[] URLs;
        private Context mContext;
        String title, descripsi, color;
        ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();

        public DownloadImages(Context context, String title, String descripsi, String color, String data1, String data2, String data3) {
            URLs = new String[]{data1, data2, data3};
            mContext = context;
            this.title = title;
            this.descripsi = descripsi;
            this.color = color;
        }

        @Override
        protected Object doInBackground(Object... objects) {
            for (int i = 0; i < URLs.length; i++) {
                try {
                    URL url = new URL(URLs[i]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    bitmapArray.add(BitmapFactory.decodeStream(input));
                    publishProgress(i);
                } catch (Exception e) {
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            if (bitmapArray.size() == 3) {
                IntervalDB db = new IntervalDB(getApplicationContext());
                db.open();
                Cursor cursor = db.getCountSkin();
                Skin skin = new Skin(title, descripsi, "#" + color, bitmapArray.get(0), bitmapArray.get(1), bitmapArray.get(2));
                cursor.close();
                db.createSkin(skin);
                db.close();

                if (NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                    String key = new ValidationsKey().getInstance(getApplicationContext()).key(true);
                    if (!key.equalsIgnoreCase("null")) {
                        new cekApply(getApplicationContext()).execute(key, title);
                    }
                }
            }
        }
    }


    /*public void load(String title,String descripsi,String color,String data1,String data2){
        try {
            Bitmap logos =  new MyAsyncTask(getApplicationContext()).execute("https://b.byonchat.com/uploads/skins/"+data1).get();
            Bitmap backs =  new MyAsyncTask(getApplicationContext()).execute("https://b.byonchat.com/uploads/skins/"+data2).get();
            Bitmap kosong = BitmapFactory.decodeResource(getResources(), R.drawable.bg_chat_baru);
            if (backs!=null&&logos!=null){
                IntervalDB  db = new IntervalDB(getApplicationContext());
                db.open();
                Cursor cursor =  db.getCountSkin();
                Skin skin = new Skin(title,descripsi,"#"+color,logos,kosong,backs);
                cursor.close();
                db.createSkin(skin);
                db.close();

                if(NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())){
                    String key = new ValidationsKey().getInstance(getApplicationContext()).key(true);
                    if (!key.equalsIgnoreCase("null")){
                        new cekApply(getApplicationContext()).execute(key,title);
                    }
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }*/

    class cekApply extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 3 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();

        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private JSONObject jObject;
        private Context mContext;
        private String content = null;
        private boolean error = false;
        String code2 = "400";
        String desc;
        String themesName;
        private MessengerDatabaseHelper messengerHelper;

        public cekApply(Context context) {
            this.mContext = context;

        }

        @Override
        protected void onPreExecute() {
        }

        InputStreamReader reader = null;

        protected String doInBackground(String... key) {

            try {
                themesName = key[1];
                HttpClient httpClient = HttpHelper
                        .createHttpClient(mContext);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                if (messengerHelper == null) {
                    messengerHelper = MessengerDatabaseHelper.getInstance(mContext);
                }

                nameValuePairs.add(new BasicNameValuePair("username", messengerHelper.getMyContact().getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("key", key[0]));
                nameValuePairs.add(new BasicNameValuePair("nama_theme", new Validations().getInstance(getApplicationContext()).getTitle()));
                nameValuePairs.add(new BasicNameValuePair("nama_theme_baru", key[1]));

                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(URL_CEK_APPLY);
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));


                //Response from the Http Request
                response = httpclient.execute(post);
                StatusLine statusLine = response.getStatusLine();

                //Check the Http Request for success


                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    content = out.toString();
                    jObject = new JSONObject(content);

                    if (content.contains("\"code\"")) {
                        code2 = jObject.getString("code");
                        desc = jObject.getString("description");
                        error = true;
                    } else {
                        if (content.contains("\"boleh_ganti\"")) {
                            code2 = jObject.getString("boleh_ganti");
                            desc = "Your Theme is locked until " + jObject.getString("tgl_habis");
                        }
                    }
                } else {
                    //Closes the connection.
                    error = true;
                    // Log.w("HTTP1:", statusLine.getReasonPhrase());
                    content = statusLine.getReasonPhrase();
                    response.getEntity().getContent().close();
                    throw new IOException(content);
                }

            } catch (ClientProtocolException e) {
                // Log.w("HTTP2:", e);
                content = e.getMessage();
                error = true;
            } catch (IOException e) {
                // Log.w("HTTP3:", e);
                content = e.getMessage();
                error = true;
            } catch (Exception e) {
                error = true;
            }

            return content;
        }

        protected void onCancelled() {

        }

        protected void onPostExecute(String content) {
            if (error) {
                if (content.contains("invalid_key")) {
                    if (NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)) {
                        String key = new ValidationsKey().getInstance(mContext).key(true);
                        if (!key.equalsIgnoreCase("null")) {
                            new cekApply(mContext).execute(key, themesName);
                        }
                    }
                }
            } else {
                if (code2.equalsIgnoreCase("true")) {
                    IntervalDB db = new IntervalDB(getApplicationContext());
                    db.open();
                    Cursor cursor2 = db.getSingleContact(4);
                    if (cursor2.getCount() > 0) {
                        db.deleteContact(4);
                    }
                    Interval interval = new Interval();
                    interval.setId(4);
                    interval.setTime(themesName);
                    db.createContact(interval);
                    cursor2.close();
                    db.close();
                }
            }
        }

    }

    class FileDownloadHandlerImageTumbP2PSave extends AsyncTask<Message, Message, Bitmap> {
        Message data;
        Message link;

        @Override
        protected Bitmap doInBackground(Message... params) {
            if (params.length == 0) {
                return null;
            }
            data = params[0];
            link = params[1];
            try {
                URL url = new URL(link.getMessage());
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.connect();
                connection.setConnectTimeout(120000);//set 2 minutes
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);

                return myBitmap;

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("getBmpFromUrl error: ", e.getMessage().toString());
                return null;
            }
        }


        @Override
        protected void onPostExecute(Bitmap result) {
            String name = data.getSource();
            String additionalInfo = "";

            if (data.isGroupChat()) {
                name = data.getSourceInfo();
                if (databaseHelper.getMyContact().getJabberId().equals(name)) {
                    name = "";
                }

                String gname = databaseHelper.getGroup(data.getSource()).getName();
                if ("".equals(name)) {
                    additionalInfo = "Group Chat '" + gname + "'";
                } else {
                    additionalInfo = " on " + gname;
                }
            }


            if (!"".equals(name)) {
                Contact contact = databaseHelper.getContact(name);
                String regex = "[0-9]+";
                if (!name.matches(regex)) {
                } else {
                    name = "+" + Utility.formatPhoneNumber(name);
                }
                if (contact != null) {
                    name = contact.getName();
                }
            }

            if (result != null) {

                FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(getApplicationContext());
                db.open();
                FilesURL files;
                if (result != null) {
                    String regex = "[0-9]+";
                    if (!data.getSource().matches(regex)) {
                        Bitmap b = MediaProcessingUtil.fastblur(getApplicationContext(), result, 10);
                        cache.getInstance();

                        Random generator = new Random();
                        int n = 10000;
                        n = generator.nextInt(n);
                        String iname = "bc-" + n + ".jpg";
                        saveImage(b, iname);
                        files = new FilesURL((int) data.getId(), "0", "tumb", link.getMessage(), iname);
                    } else {
                        Bitmap b = MediaProcessingUtil.fastblur(getApplicationContext(), result, 10);
                        cache.getInstance();

                        Random generator = new Random();
                        int n = 10000;
                        n = generator.nextInt(n);
                        String iname = "bc-" + n + ".jpg";
                        saveImage(b, iname);
                        files = new FilesURL((int) data.getId(), "0", "tumb", link.getMessage(), iname);
                    }
                } else {
                    files = new FilesURL((int) data.getId(), "0", "tumb", "");
                }

                db.insertFiles(files);
                db.close();
                result.recycle();

            }
            Intent intent = new Intent(ACTION_MESSAGE_RECEIVED);
            intent.putExtra(KEY_MESSAGE_OBJECT, data);
            intent.putExtra(KEY_CONTACT_NAME, name + additionalInfo);
            sendOrderedBroadcast(intent, null);
        }
    }

    class FileDownloadHandlerImageThumbP2PSave extends AsyncTask<Message, Message, Bitmap> {
        Message data;
        Message link;

        @Override
        protected Bitmap doInBackground(Message... params) {
            if (params.length == 0) {
                return null;
            }
            data = params[0];
            link = params[1];
            try {
                URL url = new URL(link.getMessage());
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.connect();
                connection.setConnectTimeout(120000);//set 2 minutes
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);

                return myBitmap;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }


        @Override
        protected void onPostExecute(Bitmap result) {
            String name = data.getSource();
            String additionalInfo = "";
            if (data.isGroupChat()) {
                name = data.getSourceInfo();
                if (databaseHelper.getMyContact().getJabberId().equals(name)) {
                    name = "";
                }
                String gname = databaseHelper.getGroup(data.getSource()).getName();
                if ("".equals(name)) {
                    additionalInfo = "Group Chat '" + gname + "'";
                } else {
                    additionalInfo = " on " + gname;
                }
            }

            if (!"".equals(name)) {
                Contact contact = databaseHelper.getContact(name);
                String regex = "[0-9]+";
                if (!name.matches(regex)) {
                } else {
                    name = "+" + Utility.formatPhoneNumber(name);
                }
                if (contact != null) {
                    name = contact.getName();
                }
            }


            if (result != null) {

                FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(getApplicationContext());
                db.open();
                FilesURL files;
                if (result != null) {
                    String regex = "[0-9]+";
                    if (!data.getSource().matches(regex)) {
                        Bitmap b = MediaProcessingUtil.fastblur(getApplicationContext(), result, 10);
                        cache.getInstance();

                        Random generator = new Random();
                        int n = 10000;
                        n = generator.nextInt(n);
                        String iname = "bc-" + n + ".jpg";
                        saveImage(b, iname);
                        files = new FilesURL((int) data.getId(), "0", "tumb", link.getMessage(), iname);
                    } else {
                        Bitmap b = MediaProcessingUtil.fastblur(getApplicationContext(), result, 10);
                        cache.getInstance();

                        Random generator = new Random();
                        int n = 10000;
                        n = generator.nextInt(n);
                        String iname = "bc-" + n + ".jpg";
                        saveImage(b, iname);
                        files = new FilesURL((int) data.getId(), "0", "tumb", link.getMessage(), iname);
                    }
                } else {
                    files = new FilesURL((int) data.getId(), "0", "tumb", "");
                }

                db.insertFiles(files);
                db.close();
                result.recycle();
            }

            Intent intent = new Intent(ACTION_MESSAGE_RECEIVED);
            intent.putExtra(KEY_MESSAGE_OBJECT, data);
            intent.putExtra(KEY_CONTACT_NAME, name + additionalInfo);
            sendOrderedBroadcast(intent, null);

        }
    }


    public boolean isXmppConnected() {
        Boolean rr = false;
        try {
            rr = xmppConnection.isConnected() && xmppConnection.isAuthenticated();
        } catch (Exception e) {
        }
        return rr;
    }

    public boolean isConnectionAlive() {
        int c = 0;
        while (!isXmppConnected()) {
            SystemClock.sleep(5000);
            c++;
            if (c > 5) {
                return false;
            }
        }
        return true;
    }

    private String generateGroupChatId() {
        Random r = new Random();
        return String.valueOf(Math.abs(r.nextInt()));
    }

    private String getBareJabberId(String jid) {
        return jid + "@" + SERVER_NAME;
    }

    private RosterEntry getRosterEntry(String jid) {
        //return xmppConnection.getRoster().getEntry(getBareJabberId(jid));
        return Roster.getInstanceFor(xmppConnection).getEntry(getBareJabberId(jid));
    }

    private Presence addRoster(String jabberId, String name)
            throws XMPPException, SmackException {
        //xmppConnection.getRoster().createEntry(getBareJabberId(jabberId), name,null);
        Roster.getInstanceFor(xmppConnection).createEntry(getBareJabberId(jabberId), name, null);
        return getPresence(jabberId);
    }

    private Presence getPresence(String jid) {
        //return xmppConnection.getRoster().getPresence(getBareJabberId(jid));
        return Roster.getInstanceFor(xmppConnection).getPresence(getBareJabberId(jid));
    }

    private void updateGroupChatSubject(Group group, String jabberId,
                                        String subject) {
        if (group == null)
            return;
        Message vo = createGroupInfoMessage(group.getJabberId(), jabberId,
                "Group name changed to \"" + subject + "\"");
        group.setName(subject);
        databaseHelper.updateData(group);
        onMessageReceived(vo);
    }

    private void registerMuc(MultiUserChat muc, String groupId,
                             String groupStatus) {

        // Log.w("testGroupKo",groupId);
        GroupChat gchat = registeredGroupChats.get(groupId);
        if (gchat == null) {
            // Log.w("testGroup","disnin1");
            Group g = databaseHelper.getGroup(groupId);
            if (g == null) {
                g = new Group("", groupId, groupStatus);
                databaseHelper.insertData(g);
            } else {
                g.setStatus(groupStatus);
                databaseHelper.updateData(g);
            }
            registeredGroupChats.put(groupId, new GroupChat(g, muc));
            muc.addSubjectUpdatedListener(mucSubjectUpdatedListener);
            muc.addParticipantStatusListener(mucParticipantStatusListener);
            muc.addMessageListener(mucMessageListener);
        } else {
            // Log.w("testGroup","disnin2");
            Group g = databaseHelper.getGroup(groupId);
            g.setStatus(groupStatus);
            databaseHelper.updateData(g);
        }
    }

    private void grantModeratorRole(String groupId, String jabberId)
            throws XMPPException {
        GroupChat groupChat = registeredGroupChats.get(groupId);
        if (groupChat != null) {
            try {
                groupChat.getMuc().grantModerator(jabberId);
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }


    private Message createGroupInfoMessage(String groupId, String userJabberId,
                                           String messageBody) {
        Message vo = new Message(groupId, getCurContact().getJabberId(),
                messageBody);
        vo.setSourceInfo(userJabberId);
        vo.setGroupChat(true);
        vo.setSendDate(new Date());
        vo.setDeliveredDate(new Date());
        vo.setStatus(Message.STATUS_DELIVERED);
        vo.setType(Message.TYPE_INFO);
        return vo;
    }

    private VCard loadProfile(String jabberId) throws XMPPException, SmackException {
        //VCard vcard = new VCard();
        //vcard.load(xmppConnection, getBareJabberId(jabberId));
        VCard vcard = VCardManager.getInstanceFor(xmppConnection).loadVCard(getBareJabberId(jabberId));
        return vcard;
    }

    private File getAvatar(Contact contact) throws XMPPException, SmackException {
        VCard vcard = loadProfile(contact.getJabberId());
        byte[] b = vcard.getAvatar();
        String fname = MediaProcessingUtil.getProfilePicName(contact);
        if (b != null && b.length > 0) {
            FileOutputStream fos = null;
            try {
                fos = openFileOutput(fname, Activity.MODE_PRIVATE);
                fos.write(b);
                fos.flush();
                String iconsStoragePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                        + "/ByonChat/Photo Profile/";
                File sdIconStorageDir = new File(iconsStoragePath);
                if (!sdIconStorageDir.exists()) {
                    sdIconStorageDir.mkdirs();
                }
                File yourFile = new File(sdIconStorageDir, contact.getJabberId() + ".jpg");
                if (yourFile.exists()) {
                    yourFile.delete();
                }
            } catch (IOException e) {
                Log.e(getClass().getSimpleName(),
                        "failed saving profile picture for "
                                + contact.getJabberId() + " :" + e.getMessage(),
                        e);
            } finally {
                try {
                    if (fos != null)
                        fos.close();
                } catch (IOException e) {
                }
            }
        } else {
            File photoFile = getApplicationContext().getFileStreamPath(fname);
            if (photoFile.exists()) {
                photoFile.delete();
            }
        }
        return getFileStreamPath(fname);
    }

    public class MessengerConnectionBinder extends Binder {

        public MessengerConnectionBinder(Intent intent) {
        }

        public void inviteUserToGroup(String groupId, String userJabberId) {
            GroupChat groupChat = registeredGroupChats.get(groupId);
            String addr = getPresenceByJabberId(userJabberId).getFrom();
            MultiUserChat muc = groupChat.getMuc();
            try {
                muc.invite(addr, "");
                muc.addSubjectUpdatedListener(mucSubjectUpdatedListener);
                muc.addParticipantStatusListener(mucParticipantStatusListener);
                muc.addMessageListener(mucMessageListener);
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }

        public void setUserAsGroupModerator(String groupId, String userJabberId)
                throws XMPPException {
            grantModeratorRole(groupId, userJabberId);
        }

        public void kickUserFromGroup(String groupId, String userJabberId)
                throws XMPPException {
            GroupChat groupChat = registeredGroupChats.get(groupId);
            if (groupChat != null) {
                String addr = getPresenceByJabberId(userJabberId).getFrom();
                try {
                    groupChat.getMuc().revokeMembership(addr);
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }
        }

        public Collection<Affiliate> getGroupOwners(String groupId)
                throws XMPPException {
            GroupChat groupChat = registeredGroupChats.get(groupId);
            if (groupChat != null) {
                try {
                    return groupChat.getMuc().getOwners();
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }
            return new ArrayList<Affiliate>();
        }

        public Presence getPresenceByJabberId(String jabberId) {
            return getPresence(jabberId);
        }

        public Presence addRosterEntry(String jabberId, String name)
                throws XMPPException, SmackException {
            return addRoster(jabberId, name);
        }

        public File loadAvatar(Contact contact) throws XMPPException, SmackException {
            return getAvatar(contact);
        }

        public void addNewContact(Contact contact) throws XMPPException, SmackException {
            Presence p = addRosterEntry(contact.getJabberId(),
                    contact.getName());
            if (p.getStatus() != null)
                contact.setStatus(p.getStatus());
            loadAvatar(contact);
            databaseHelper.insertData(contact);
        }

        public VCard getProfile(String jabberId) throws XMPPException {
            return getProfile(jabberId);
        }

        public void updateProfile(String name, File profilePic)
                throws XMPPException, SmackException {
            VCard vcard = new VCard();
            vcard.setFirstName(name);
            FileInputStream fis = null;
            try {
                String profilePicName = MediaProcessingUtil
                        .getProfilePicNameSmall(databaseHelper.getMyContact());
                FileOutputStream fos = openFileOutput(profilePicName,
                        MODE_PRIVATE);
                fos.close();

                File f = getFileStreamPath(profilePicName);
                MediaProcessingUtil.getResizedImage(profilePic,
                        f.getAbsolutePath(), 100, 100);
                byte[] pp = new byte[(int) f.length()];
                fis = new FileInputStream(f);
                fis.read(pp);
                vcard.setAvatar(pp);
            } catch (IOException ioe) {
                Log.e(getClass().getSimpleName(),
                        "Failed setting profile picture: " + ioe.getMessage(),
                        ioe);
            } finally {
                try {
                    fis.close();
                } catch (IOException e) {
                }
            }
            //vcard.save(xmppConnection);
            VCardManager.getInstanceFor(xmppConnection).saveVCard(vcard);
        }

      /*  public void deleteProfile(String name)
                throws XMPPException {
            VCard vcard = new VCard();
            vcard.setFirstName(name);
            vcard.removeAvatar();
            vcard.save(xmppConnection);
        }*/

        public void deleteRosterEntry(String jabberId) throws XMPPException, SmackException {
            RosterEntry en = getRosterEntry(jabberId);
            if (en != null) {
                //xmppConnection.getRoster().removeEntry(en);
                Roster.getInstanceFor(xmppConnection).removeEntry(en);
            }
        }

        public void updatePresence(Presence.Type type, String status) throws SmackException {
            Presence p = new Presence(type, status, 50, Presence.Mode.available);
            //xmppConnection.sendPacket(p);
            xmppConnection.sendStanza(p);

        }


        public Chat createChat(String userName, boolean groupChat) {
            Chat xmppChat = getChat(userName);
            if (xmppChat == null) {
                String destAddr = getBareJabberId(userName);
                if (groupChat) {
                    destAddr = userName + "@" + CONFERENCE_SERVER;
                }
                //xmppChat = xmppConnection.getChatManager().createChat(destAddr,chatMsgListener);
                xmppChat = ChatManager.getInstanceFor(xmppConnection).createChat(destAddr, chatMsgListener);
                registeredChats.put(userName, xmppChat);
            }
            return xmppChat;
        }


        public Chat getChat(String userName) {
            return registeredChats.get(userName);
        }

        public boolean isConnected() {
            return isXmppConnected();
        }


        public void sendBroadCast(Message vo) {
            Map<String, String> props = new HashMap<String, String>();
            props.put(BROADCAST_MESSAGE_PARAM, "true");
            sendMessage(vo, props);
        }


        public void sendLocation(Message vo) {
            Map<String, String> props = new HashMap<String, String>();
            props.put(LOCATION_MESSAGE_PARAM, "true");
            sendMessage(vo, props);
        }

        public void sendFile(Message vo) {
            Map<String, String> props = new HashMap<String, String>();
            props.put(FILE_MESSAGE_PARAM, vo.getType());
            sendMessage(vo, props);
        }

        private void addPropertyToMessage(
                org.jivesoftware.smack.packet.Message msg,
                Map<String, String> properties) {
            if (properties != null) {
                for (Iterator<String> iterator = properties.keySet().iterator(); iterator
                        .hasNext(); ) {
                    String key = iterator.next();
                    String value = properties.get(key);
                    if (value != null) {
                        //msg.setProperty(key, value);
                        JivePropertiesExtension jpe = new JivePropertiesExtension();
                        jpe.setProperty(key, value);
                        msg.addExtension(jpe);
                    }
                }
            }
        }

        private void sendMessage(Message vo, Map<String, String> properties) {
            vo.setStatus(Message.STATUS_INPROGRESS);
            String action = "";
            vo.setSendDate(new Date());
            if (isConnectionAlive()) {
                String messageToSend = vo.getMessage();
                if (vo.getType().equals(Message.TYPE_VIDEO) || vo.getType().equals(Message.TYPE_IMAGE)) {
                    String urlFile = "";
                    String caption = "";

                    JSONObject jObject = null;
                    try {
                        jObject = new JSONObject(messageToSend);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (jObject != null) {
                        try {
                            urlFile = jObject.getString("u");
                            caption = jObject.getString("c");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    messageToSend = jsonMessage(caption, urlFile);

                    Log.i("message to send: ", messageToSend);
                }
                try {
                    if (vo.isGroupChat()) {
                    } else {
                        Chat xmppChat = createChat(vo.getDestination(),
                                vo.isGroupChat());
                        org.jivesoftware.smack.packet.Message msgObj = new org.jivesoftware.smack.packet.Message();
                        msgObj.setBody(messageToSend);
                        msgObj.setStanzaId(vo.getPacketId());
                        //msgObj.addExtension(deliveryRequestExtension);
                        DeliveryReceiptRequest.addTo(msgObj);
                        addPropertyToMessage(msgObj, properties);
                        xmppChat.sendMessage(msgObj);
                    }

                    vo.setStatus(Message.STATUS_SENT);
                    action = ACTION_MESSAGE_SENT;

                } catch (SmackException e) {
                    Log.e(getClass().getSimpleName(),
                            "Error sending data to server. " + e.getMessage());
                    vo.setStatus(Message.STATUS_FAILED);
                    action = ACTION_MESSAGE_FAILED;
                }
            } else {
                vo.setStatus(Message.STATUS_FAILED);
                action = ACTION_MESSAGE_FAILED;
            }
            onMessageProcessed(vo, action);
        }

        public void sendMessage(Message vo) {
            sendMessage(vo, null);
        }

        public void leaveGroupChat(Group g) {
            GroupChat gchat = registeredGroupChats.remove(g.getJabberId());
            if (gchat != null) {
                try {
                    gchat.getMuc().leave();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }
            databaseHelper.deleteData(g);
        }

        public void changeGroupChatSubject(String groupId, String newSubject)
                throws XMPPException {
            GroupChat gchat = registeredGroupChats.get(groupId);
            if (gchat != null) {
                try {
                    gchat.getMuc().changeSubject(newSubject);
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }

        }

       /* public String createGroupChat(List<Contact> participants)
            throws XMPPException {
                if (!isConnectionAlive()) {
                    return null;
                }
            String randId = "group"
                    + databaseHelper.getMyContact().getJabberId()
                    + generateGroupChatId();
            String groupId = randId + "@" + CONFERENCE_SERVER;

            MultiUserChatManager mucMgr = MultiUserChatManager.getInstanceFor(xmppConnection);
            MultiUserChat muc = mucMgr.getMultiUserChat(groupId);
            Contact co = getCurContact();
            try {
Log.w("every",co.getJabberId());
                muc.create(co.getJabberId());
                setConfig(muc);
                for (Iterator<Contact> iterator = participants.iterator(); iterator
                        .hasNext();) {

                    Contact contact = iterator.next();
                    String addr = getPresenceByJabberId(contact.getJabberId())
                            .getFrom();
                    muc.invite(addr, "");
                }
                registerMuc(muc, randId, Group.STATUS_OWNER);
            } catch (SmackException e) {
                e.printStackTrace();
            }
            return randId;
        }
*/
       /* private void setConfig(MultiUserChat multiUserChat) {

            try {
                Form form = multiUserChat.getConfigurationForm();
                Form submitForm = form.createAnswerForm();

                for(FormField field : submitForm.getFields()){
                    if (!FormField.Type.hidden.equals(field.getType())
                            && field.getVariable() != null) {
                        submitForm.setDefaultAnswer(field.getVariable());
                    }
                }


              *//* // Log.w("suger777",submitForm.toString());
                List<String> owners = new ArrayList<String>();
                owners.add(databaseHelper.getMyContact().getJabberId());
               // Log.w(TAG, "list of owners=====" +owners.toString());*//*
                submitForm.setAnswer("muc#roomconfig_publicroom", true);
                submitForm.setAnswer("muc#roomconfig_persistentroom", true);
             //   submitForm.setAnswer("muc#roomconfig_roomowners", owners);
                multiUserChat.sendConfigurationForm(submitForm);

         //       muc.sendConfigurationForm(new Form(DataForm.Type.submit));

            } catch (Exception e) {
                e.printStackTrace();
            }

        }*/


        public String createGroupChatD(List<Contact> participants)
                throws XMPPException {
            if (!isConnectionAlive()) {
                return null;
            }
            String randId = "group"
                    + databaseHelper.getMyContact().getJabberId()
                    + generateGroupChatId();
            String groupId = randId + "@" + CONFERENCE_SERVER;
            // Get the MultiUserChatManager
            try {
                MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(xmppConnection);
                MultiUserChat muc = manager.getMultiUserChat(groupId);
                muc.create("testbot");
                muc.sendConfigurationForm(new Form(DataForm.Type.submit));
            } catch (SmackException e) {
                e.printStackTrace();
            }


            return randId;
        }

        public String createGroupChat(List<Contact> participants)
                throws XMPPException {
            if (!isConnectionAlive()) {
                return null;
            }
            String randId = "group"
                    + databaseHelper.getMyContact().getJabberId()
                    + generateGroupChatId();
            String groupId = randId + "@" + CONFERENCE_SERVER;
            try {
                Contact co = getCurContact();

                MultiUserChatManager mucMgr = MultiUserChatManager.getInstanceFor(xmppConnection);
                MultiUserChat muc = mucMgr.getMultiUserChat(groupId);
                muc.create(co.getJabberId()); // RoomName room name
                // To obtain the chat room configuration form
                Form form = muc.getConfigurationForm();
                // Create a new form to submit the original form according to the.
                Form submitForm = form.createAnswerForm();
                // To submit the form to add a default reply
                for (FormField field : submitForm.getFields()) {
                    if (!FormField.Type.hidden.equals(field.getType())
                            && field.getVariable() != null) {
                        submitForm.setDefaultAnswer(field.getVariable());
                    }
                }
                // Set the chat room of the new owner
                List<String> owners = new ArrayList<String>();
                owners.add(co.getJabberId());// The user JID
                for (Iterator<Contact> iterator = participants.iterator(); iterator
                        .hasNext(); ) {
                    Contact contact = iterator.next();
                    String addr = getPresenceByJabberId(contact.getJabberId())
                            .getFrom();
                    muc.invite(addr, "");
                }


                submitForm.setAnswer("muc#roomconfig_roomowners", owners);
                // Set the chat room is a long chat room, soon to be preserved
                submitForm.setAnswer("muc#roomconfig_persistentroom", false);
                // Only members of the open room
                submitForm.setAnswer("muc#roomconfig_membersonly", false);
                // Allows the possessor to invite others
                submitForm.setAnswer("muc#roomconfig_allowinvites", true);
                submitForm.setAnswer("muc#roomconfig_enablelogging", true);
                // Only allow registered nickname log
                submitForm.setAnswer("x-muc#roomconfig_reservednick", true);
                // Allows the user to modify the nickname
                submitForm.setAnswer("x-muc#roomconfig_canchangenick", false);
                // Allows the user to register the room
                submitForm.setAnswer("x-muc#roomconfig_registration", false);
                // Send the completed form (the default) to the server to configure the chat room
                submitForm.setAnswer("muc#roomconfig_passwordprotectedroom", true);
                // Send the completed form (the default) to the server to configure the chat room
                muc.sendConfigurationForm(submitForm);
                //  registerMuc(muc, randId, Group.STATUS_OWNER);

            } catch (SmackException e) {
                e.printStackTrace();
            }
            return randId;
        }


    }

    private String jsonMessage(String caption, String mUrlServer) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("s", "");
            obj.put("c", caption);
            obj.put("u", mUrlServer);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }


    class MyChatMessageListener implements ChatMessageListener {

        private Message processDeliveryReport(
                org.jivesoftware.smack.packet.Message msg) {

            Message vo = null;
            ExtensionElement extension = msg.getExtension(DeliveryReceipt.ELEMENT,
                    DeliveryReceipt.NAMESPACE);
            if (extension != null) {
                DeliveryReceipt resp = (DeliveryReceipt) extension;
                String packetId = resp.getId();

                Cursor c = databaseHelper.query(
                        getString(R.string.sql_message_by_packetid),
                        new String[]{packetId});

                if (c.moveToNext()) {
                    vo = new Message(c);
                }
                c.close();
            }

            return vo;
        }

        private void sendDeliveryReport(Chat chat,
                                        org.jivesoftware.smack.packet.Message msg) {
            org.jivesoftware.smack.packet.Message deliveryResponseObj = new org.jivesoftware.smack.packet.Message();
            deliveryResponseObj.setType(org.jivesoftware.smack.packet.Message.Type.chat);
            deliveryResponseObj.setTo(msg.getFrom());
            deliveryResponseObj.setFrom(msg.getTo());
            deliveryResponseObj.addExtension(new DeliveryExtension.Response(msg.getStanzaId()));
            DeliveryReceiptRequest.addTo(deliveryResponseObj);

            try {
                chat.sendMessage(deliveryResponseObj);

            } catch (SmackException.NotConnectedException e) {
                Log.e(getClass().getSimpleName(),
                        "Failed sending delivery: " + e.getMessage(), e);
            }

        }

        @Override
        public void processMessage(Chat chat,
                                   org.jivesoftware.smack.packet.Message msg) {

            if (chat.getParticipant().contains("@conference." + SERVER_NAME)) {
                String[] groupId = Group.parseGroupId(msg.getFrom());
                if (msg.getBody().contains("invites you to the room " + chat.getParticipant())) {
                    GroupChat gchat = registeredGroupChats.get(groupId[0]);
                    if (gchat == null) {
                        MultiUserChatManager mucm = MultiUserChatManager.getInstanceFor(xmppConnection);
                        try {
                            List<String> services = mucm.getServiceNames();
                            if (services.isEmpty()) {
                                throw new Exception("No MUC services found");
                            }
                            MultiUserChat muc = mucm.getMultiUserChat(chat.getParticipant());
                            muc.join(getCurContact().getJabberId());
                            registerMuc(muc, groupId[0],
                                    Group.STATUS_ACTIVE);
                        } catch (SmackException.NoResponseException e) {
                            e.printStackTrace();
                        } catch (XMPPException.XMPPErrorException e) {
                            e.printStackTrace();
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mucm.addInvitationListener(mucInvitationListener);
                    }
                }
                return;
            }

            Message vo = null;
            String to = getCurContact().getJabberId();

            ExtensionElement ex = msg.getExtension(XMLNS_MUCUSER);
            if (ex != null && ex instanceof MUCUser) {
                return;
            }

            String from = Contact.parseJabberId(msg.getFrom());

            if (msg.getBody() == null) {
                vo = processDeliveryReport(msg);
                if (vo == null)
                    return;
            } else {

                vo = new Message(from, to, msg.getBody());
                vo.setPacketId(msg.getStanzaId());

                JivePropertiesExtension jpe = (JivePropertiesExtension) msg.getExtension(JivePropertiesExtension.NAMESPACE);
                if (jpe != null) {
                    for (String pname : jpe.getPropertyNames()) {
                        if (LOCATION_MESSAGE_PARAM.equalsIgnoreCase(pname)) {
                            if (Boolean.parseBoolean((String) jpe.getProperty(pname))) {
                                vo.setType(Message.TYPE_LOC);
                            }
                        } else if (FILE_MESSAGE_PARAM.equalsIgnoreCase(pname)) {
                            String val = (String) jpe.getProperty(pname);
                            if (Message.TYPE_IMAGE.equalsIgnoreCase(val)) {
                                vo.setType(Message.TYPE_IMAGE);
                            } else if (Message.TYPE_VIDEO.equalsIgnoreCase(val)) {
                                vo.setType(Message.TYPE_VIDEO);
                            }
                        } else if (BROADCAST_MESSAGE_PARAM.equalsIgnoreCase(pname)) {
                            if (Boolean.parseBoolean((String) jpe.getProperty(pname))) {
                                vo.setType(Message.TYPE_BROADCAST);
                            }
                        } else if (READSTATUS_MESSAGE_PARAM.equalsIgnoreCase(pname)) {
                            if (Boolean.parseBoolean((String) jpe.getProperty(pname))) {
                                vo.setType(Message.TYPE_READSTATUS);
                            }
                        } else if (TARIKPESAN_MESSAGE_PARAM.equalsIgnoreCase(pname)) {
                            if (Boolean.parseBoolean((String) jpe.getProperty(pname))) {
                                vo.setType(Message.TYPE_TARIK);
                            }
                        }
                    }
                }
            }

            vo.setDeliveredDate(new Date());

            if (vo.getId() == 0) {
                long totalMessages = 0;
                Cursor c = databaseHelper.query(
                        "SELECT count(*) total FROM "
                                + vo.TABLE_NAME
                                + " WHERE "
                                + vo.PACKET_ID
                                + "=? ",
                        new String[]{vo.getPacketId()});
                int indexTotal = c.getColumnIndex("total");
                while (c.moveToNext()) {
                    totalMessages = c.getLong(indexTotal);
                }
                c.close();

                if (totalMessages == 0) {
                    vo.setStatus(Message.STATUS_UNREAD);
                    if (registeredChats.get(vo.getSource()) == null) {
                        registeredChats.put(vo.getSource(), chat);
                    }
                    DelayInformation inf = msg.getExtension(DelayInformation.ELEMENT, DelayInformation.NAMESPACE);
                    if (inf != null) {
                        Date date = inf.getStamp();
                        vo.setSendDate(date);
                    } else {
                        vo.setSendDate(vo.getDeliveredDate());
                    }
                    onMessageReceived(vo);
                    ex = msg.getExtension(XMLNS_DELIVERY);
                    if (ex != null) {
                        sendDeliveryReport(chat, msg);
                    }
                }
            } else {
                if (vo.getType().equalsIgnoreCase(Message.TYPE_READSTATUS)) {
                    databaseHelper.execSql(SQL_REMOVE_MESSAGES_STATUS, new String[]{vo.getPacketId()});
                } else if (vo.getType().equalsIgnoreCase(Message.TYPE_TARIK)) {
                    databaseHelper.execSql(SQL_REMOVE_MESSAGES_STATUS, new String[]{vo.getPacketId()});
                } else {
                    if (new Validations().getInstance(getApplicationContext()).getValidationPerHari("17") == 1) {
                        Message report = new Message(databaseHelper.getMyContact().getJabberId(), "x_280164515arlandi", "1");
                        report.setType("text");
                        report.setSendDate(new Date());
                        report.setStatus(Message.STATUS_INPROGRESS);
                        report.generatePacketId();

                        Intent i = new Intent();
                        i.setAction(UploadService.FILE_SEND_INTENT);
                        i.putExtra(KEY_MESSAGE_OBJECT, report);
                        sendBroadcast(i);
                    }

                    vo.setStatus(Message.STATUS_DELIVERED);
                    onMessageProcessed(vo, ACTION_MESSAGE_DELIVERED);
                }
            }
        }

    }

    class ConnectionHelper extends Thread {


        private void addPresenceListener() {
            if (presenceListener == null) {
                presenceListener = new StanzaListener() {

                    @Override
                    public void processPacket(Stanza packet) {

                        Presence presence = (Presence) packet;
                        if (presence.getType().equals(Presence.Type.subscribe)) {
                            String jabberId = Contact.parseJabberId(presence
                                    .getFrom());

                            RosterEntry entry = getRosterEntry(jabberId);
                            if (entry == null) {

                                String name = "+" + jabberId;
                                String regex = "[0-9]+";
                                if (!jabberId.matches(regex)) name = jabberId;

                                Contact c = databaseHelper.getContact(jabberId);
                                if (c != null)
                                    name = c.getName();
                                try {
                                    addRoster(jabberId, name);
                                } catch (Exception e) {
                                    Log.e(getClass().getSimpleName(),
                                            "Error adding roster: "
                                                    + e.getMessage(), e);
                                }
                            }
                        } else if (presence.getType().equals(Presence.Type.available)) {
                            if (!presence.getFrom().startsWith(databaseHelper.getMyContact().getJabberId())) {
                                if (presence.getStatus() != null) {
                                    String jabberId = Contact
                                            .parseJabberId(presence.getFrom());
                                    Contact c = databaseHelper.getContact(jabberId);
                                    c.setStatus(presence.getStatus());
                                    try {
                                        getAvatar(c);
                                    } catch (SmackException e) {
                                        Log.e(getClass().getSimpleName(),
                                                "Failed getting avatar: "
                                                        + e.getMessage(), e);
                                    } catch (XMPPException e) {
                                        Log.e(getClass().getSimpleName(),
                                                "Failed getting avatar: "
                                                        + e.getMessage(), e);
                                    }

                                    DelayInformation inf = presence.getExtension(DelayInformation.ELEMENT, DelayInformation.NAMESPACE);
                                    if (inf != null) {
                                        Date date = inf.getStamp();
                                    } else {
                                        c.setChangeProfile(1);
                                        String name;
                                        if (c.getJabberId().equalsIgnoreCase(databaseHelper.getMyContact().getJabberId())) {
                                            name = c.getRealname();
                                        } else {
                                            name = c.getName();
                                        }

                                        try {
                                            JSONObject json = new JSONObject(presence.getStatus());
                                            tanggal = json.getString("date");
                                            flag = json.getString("flag");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                        Date convertedDate = new Date();
                                        try {
                                            convertedDate = dateFormat.parse(tanggal);
                                            TimeLine timeLine = new TimeLine(c.getJabberId(), c.getStatus(), convertedDate, name, flag);
                                            TimeLineDB timeLineDB = TimeLineDB.getInstance(MessengerConnectionService.this);
                                            timeLineDB.insert(timeLine);

                                            long milliseconds = convertedDate.getTime();
                                            c.setFacebookid(String.valueOf(milliseconds));

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    databaseHelper.updateData(c);
                                } else {

                                }

                            }
                            sendBroadcast(ACTION_STATUS_CHANGED);
//                            sendBroadcast(ACTION_STATUS_CHANGED_CONTACT);

                        } else if (presence.getType().equals(Presence.Type.unavailable)) {

/*
  String SQL_SELECT_MESSAGES =  "SELECT *  FROM "
                                    + Message.TABLE_NAME + " WHERE (" + Message.DESTINATION + "=? OR "
                                    + Message.SOURCE + "=? ) and status = 2 order by packet_id desc";
*/

                      /*      String SQL_SELECT_MESSAGES =  "SELECT *  FROM "
                                    + Message.TABLE_NAME + " WHERE " + Message.SOURCE + "=? "
                                    + " and status = 2 order by packet_id desc";

                            Cursor  cursor = databaseHelper.query(SQL_SELECT_MESSAGES, new String[] {
                                    //Contact.parseJabberId(presence.getFrom()), Contact.parseJabberId(presence.getFrom())});
                                    Contact.parseJabberId(presence.getFrom())});

                            ArrayList<Message> messages = new ArrayList<Message>();

                            while (cursor.moveToNext()) {
                                Message vo = new Message(cursor);
                                messages.add(0, vo);
                            }
                            cursor.close();
                            for (Iterator<Message> iterator = messages.iterator(); iterator
                                    .hasNext();) {
                                Message vo = iterator.next();
                                if(vo.getType().equals(Message.TYPE_VIDEO) || vo.getType().equals(Message.TYPE_IMAGE)) {
                                    try {
                                        sendFile(vo);
                                    } catch (SmackException e) {
                                        e.printStackTrace();
                                    }
                                }else if (vo.getType().equalsIgnoreCase(Message.TYPE_LOC)){
                                    try {
                                        sendLocation(vo);
                                    } catch (SmackException e) {
                                        e.printStackTrace();
                                    }
                                }else if (vo.getType().equalsIgnoreCase(Message.TYPE_BROADCAST)){
                                    try {
                                        sendBroadCast(vo);
                                    } catch (SmackException e) {
                                        e.printStackTrace();
                                    }
                                }else if(vo.getType().equalsIgnoreCase(Message.TYPE_TEXT)){
                                    try {
                                        sendMessage(vo);
                                    } catch (SmackException e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    try {
                                        sendReadStatus(vo);
                                    } catch (SmackException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
*/
                            /*salah
                            MUCUser mucUser = (MUCUser) packet
                                    .getExtension(XMLNS_MUCUSER);
                            if (mucUser != null) {
                                String[] tmp = Group.parseGroupId(presence
                                        .getFrom());
                                String jabberId = Contact.parseJabberId(mucUser
                                        .getItem().getJid());

                                if (mucUser.getStatus().contains(MUCUser.Status.BANNED_301)) {
                                //if ("321".equals(mucUser.getStatus().getCode())) {
                                    Group g = databaseHelper.getGroup(tmp[0]);
                                    if (g != null) {
                                        Contact c = databaseHelper
                                                .getContact(tmp[1]);
                                        String participant = "You";
                                        if (c == null) {
                                            participant = "+" + tmp[1];
                                        } else {
                                            if (!getCurContact().getJabberId()
                                                    .equals(c.getJabberId())) {
                                                participant = c.getName();
                                            } else {
                                                g.setStatus(Group.STATUS_INACTIVE);
                                                databaseHelper.updateData(g);
                                                GroupChat gchat = registeredGroupChats
                                                        .remove(g.getJabberId());

                                                gchat.getMuc()
                                                        .removeMessageListener((MessageListener) mucMessageListener);
                                                gchat.getMuc()
                                                        .removeParticipantStatusListener(
                                                                mucParticipantStatusListener);
                                                gchat.getMuc()
                                                        .removeSubjectUpdatedListener(
                                                                mucSubjectUpdatedListener);
                                                gchat = null;
                                            }
                                        }

                                        if ("".equals(tmp[0]))
                                            return;
                                        String bodyMessage = participant
                                                + " is removed from this group";
                                        if ("You".equals(participant)) {
                                            bodyMessage = participant
                                                    + " are removed from this group";
                                        }
                                        Message vo = createGroupInfoMessage(
                                                tmp[0], tmp[1], bodyMessage);
                                        onMessageReceived(vo);
                                    }
                                }
                            }*/
                        }

                    }
                };
                xmppConnection.addAsyncStanzaListener(presenceListener,
                        new StanzaFilter() {
                            @Override
                            public boolean accept(Stanza packet) {
                                if (packet instanceof Presence) {
                                    return true;
                                }
                                return false;
                            }
                        });
            }
        }

        public void sendLocation(Message vo) throws SmackException {
            Map<String, String> props = new HashMap<String, String>();
            props.put(LOCATION_MESSAGE_PARAM, "true");
            sendMessage(vo, props);
        }

        public void sendFile(Message vo) throws SmackException {
            Map<String, String> props = new HashMap<String, String>();
            props.put(FILE_MESSAGE_PARAM, vo.getType());
            sendMessage(vo, props);
        }

        public void sendBroadCast(Message vo) throws SmackException {
            Map<String, String> props = new HashMap<String, String>();
            props.put(BROADCAST_MESSAGE_PARAM, "true");
            sendMessage(vo, props);
        }

        public void sendReadStatus(Message vo) throws SmackException {
            Map<String, String> props = new HashMap<String, String>();
            props.put(READSTATUS_MESSAGE_PARAM, "true");
            sendMessage(vo, props);
        }

        public void sendTarikPesan(Message vo) throws SmackException {
            Map<String, String> props = new HashMap<String, String>();
            props.put(TARIKPESAN_MESSAGE_PARAM, "true");
            sendMessage(vo, props);
        }

        private void sendMessage(Message vo, Map<String, String> properties) throws SmackException {
            vo.setStatus(Message.STATUS_INPROGRESS);
            String action = "";
            vo.setSendDate(new Date());
            if (isConnectionAlive()) {
                String messageToSend = vo.getMessage();
                if (vo.getType().equals(Message.TYPE_VIDEO) || vo.getType().equals(Message.TYPE_IMAGE)) {
                    String urlFile = "";
                    String caption = "";

                    JSONObject jObject = null;
                    try {
                        jObject = new JSONObject(messageToSend);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (jObject != null) {
                        try {
                            urlFile = jObject.getString("u");
                            caption = jObject.getString("c");

                        } catch (JSONException e) {
                            e.printStackTrace();
                            vo.setStatus(Message.STATUS_NOTSEND);
                            action = UploadService.KEY_UPDATE_UPLOAD_BAR;
                            onMessageProcessed(vo, action);
                            return;
                        }
                    }
                    messageToSend = jsonMessage(caption, urlFile);

                    Log.i("message to send: ", messageToSend);
                }
                try {
                    if (vo.isGroupChat()) {
                        GroupChat gchat = registeredGroupChats.get(vo
                                .getDestination());
                        if (gchat != null) {
                            org.jivesoftware.smack.packet.Message msgObj = gchat
                                    .getMuc().createMessage();
                            msgObj.setBody(messageToSend);
                            msgObj.setPacketID(vo.getPacketId());
                            addPropertyToMessage(msgObj, properties);
                            gchat.getMuc().sendMessage(msgObj);
                        }
                    } else {
                        Chat xmppChat = createChat(vo.getDestination(),
                                vo.isGroupChat());
                        org.jivesoftware.smack.packet.Message msgObj = new org.jivesoftware.smack.packet.Message();
                        msgObj.setBody(messageToSend);
                        msgObj.setStanzaId(vo.getPacketId());
                        //msgObj.addExtension(deliveryRequestExtension);
                        DeliveryReceiptRequest.addTo(msgObj);
                        addPropertyToMessage(msgObj, properties);
                        xmppChat.sendMessage(msgObj);
                    }

                    vo.setStatus(Message.STATUS_SENT);
                    action = ACTION_MESSAGE_SENT;
                } catch (SmackException e) {
                    Log.e(getClass().getSimpleName(),
                            "Error sending data to server. " + e.getMessage());
                    vo.setStatus(Message.STATUS_FAILED);
                    action = ACTION_MESSAGE_FAILED;
                }
            } else {
                vo.setStatus(Message.STATUS_FAILED);
                action = ACTION_MESSAGE_FAILED;
            }
            onMessageProcessed(vo, action);
        }

        public void sendMessage(Message vo) throws SmackException {
            sendMessage(vo, null);
        }

        private void addPropertyToMessage(
                org.jivesoftware.smack.packet.Message msg,
                Map<String, String> properties) {
            if (properties != null) {
                for (Iterator<String> iterator = properties.keySet().iterator(); iterator
                        .hasNext(); ) {
                    String key = iterator.next();
                    String value = properties.get(key);
                    if (value != null) {
                        //msg.setProperty(key, value);
                        JivePropertiesExtension jpe = new JivePropertiesExtension();
                        jpe.setProperty(key, value);
                        msg.addExtension(jpe);
                    }
                }
            }
        }

        public Chat createChat(String userName, boolean groupChat) {
            Chat xmppChat = getChat(userName);
            if (xmppChat == null) {
                String destAddr = getBareJabberId(userName);
                if (groupChat) {
                    destAddr = userName + "@" + CONFERENCE_SERVER;
                }

                xmppChat = ChatManager.getInstanceFor(xmppConnection).createChat(destAddr, chatMsgListener);
                registeredChats.put(userName, xmppChat);
            }
            return xmppChat;
        }

        public Chat getChat(String userName) {
            return registeredChats.get(userName);
        }

        private void startPingBoss() {
            PingManager pingBoss = PingManager.getInstanceFor(xmppConnection);
            pingBoss.setPingInterval(180);  //ping per 3 minute
            pingBoss.registerPingFailedListener(new PingFailedListener() {
                @Override
                public void pingFailed() {
                    // Log.w(TAG, "ping server FAILED !!!");
                    //restart connection ???
                    disconnect();
                }
            });
        }

        private void addMucInvitationListener() {
            if (mucInvitationListener == null) {
                mucInvitationListener = new InvitationListener() {
                    @Override
                    public void invitationReceived(XMPPConnection conn, MultiUserChat room, String inviter, String reason, String password, org.jivesoftware.smack.packet.Message message) {
                        String[] groupId = Group.parseGroupId(room.getRoom());
                        GroupChat gchat = registeredGroupChats.get(groupId[0]);
                        if (gchat == null) {
                            MultiUserChatManager mucm = MultiUserChatManager.getInstanceFor(conn);
                            try {
                                List<String> services = mucm.getServiceNames();
                                if (services.isEmpty()) {
                                    throw new Exception("No MUC services found");
                                }
                                String service = services.get(0);

                                MultiUserChat muc = mucm.getMultiUserChat(room + "@" + service);

                                muc.join(getCurContact().getJabberId());
                                registerMuc(muc, groupId[0],
                                        Group.STATUS_ACTIVE);
                            } catch (SmackException.NoResponseException e) {
                                e.printStackTrace();
                            } catch (XMPPException.XMPPErrorException e) {
                                e.printStackTrace();
                            } catch (SmackException.NotConnectedException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            mucm.addInvitationListener(mucInvitationListener);
                        }

                    }
                };

            }
        }

        public void run() {

            if (xmppConnection != null) {
                if (connectionHelperStarted)
                    return;
                connectionHelperStarted = true;

                try {
                    while (!xmppConnection.isAuthenticated()) {

                        while (!xmppConnection.isConnected()) {
                            try {
                                xmppConnection.connect();

                                configure();
                                ServiceDiscoveryManager sdm = ServiceDiscoveryManager
                                        .getInstanceFor(xmppConnection);

                                sdm.addFeature("http://jabber.org/protocol/disco#info");
                                sdm.addFeature("jabber:iq:privacy");

                                startPingBoss();
                                addStreamManagementAckListener();

                                //set our receipt manager
                                DeliveryReceiptManager.getInstanceFor(xmppConnection).addReceiptReceivedListener(new ReceiptReceivedListener() {
                                    @Override
                                    public void onReceiptReceived(String fromJid, String toJid, String receiptId, Stanza receipt) {
                                        //Log.d(TAG, "onReceiptReceived from:" + fromJid + " to:" + toJid + " receiptid:" + receiptId);
                                    }
                                });
                                //DeliveryReceiptManager.getInstanceFor(xmppConnection).dontAutoAddDeliveryReceiptRequests();
                                //DeliveryReceiptManager.getInstanceFor(xmppConnection).setAutoReceiptMode(DeliveryReceiptManager.AutoReceiptMode.disabled);
                                addMucInvitationListener();
                            } catch (Exception e) {
                                Log.e(getClass().getSimpleName(),
                                        "Error connecting to " + SERVER_HOST + ":"
                                                + SERVER_PORT + ": " + e.getMessage());
                                try {
                                    sleep(3000);
                                } catch (InterruptedException ie) {
                                }
                            }
                        }   //end while isconnected

                        try {
                            Contact myContact;
                            while ((myContact = getCurContact()) == null) {
                                try {
                                    sleep(10000);
                                } catch (InterruptedException ie) {
                                }
                            }

                            if (!xmppConnection.isAuthenticated()) {
                                SASLAuthentication.unregisterSASLMechanism("DIGEST-MD5");
                                //SASLAuthentication.supportSASLMechanism("PLAIN", 0);
                                SASLAuthentication.registerSASLMechanism(new SASLPlainMechanism());
                                xmppConnection.login(myContact.getJabberId(),
                                        myContact.getName());
                                Log.w("logindulu", myContact.getJabberId() + " -- " + myContact.getName());

                                ChatManager.getInstanceFor(xmppConnection).addChatListener(
                                        new ChatManagerListener() {
                                            @Override
                                            public void chatCreated(Chat chat,
                                                                    boolean createdLocally) {
                                                if (!createdLocally) {
                                                    chat.addMessageListener(chatMsgListener);
                                                }
                                            }
                                        });
                                sendBroadcast(ACTION_CONNECTED);
                            }

                            String SQL_SELECT_MESSAGES = "SELECT *  FROM "
                                    + Message.TABLE_NAME + " WHERE status = 1 order by packet_id desc";

                            Cursor cursor = databaseHelper.query(SQL_SELECT_MESSAGES, new String[]{});

                            ArrayList<Message> messages = new ArrayList<Message>();

                            while (cursor.moveToNext()) {
                                Message vo = new Message(cursor);
                                messages.add(0, vo);
                            }
                            cursor.close();
                            for (Iterator<Message> iterator = messages.iterator(); iterator
                                    .hasNext(); ) {
                                Message vo = iterator.next();
                                if (vo.isGroupChat()) {

                                    if (!ConversationGroupActivity.client.isConnected()) {
                                        ConversationGroupActivity.client.connect();
                                    }

                                    Intent intent = new Intent(getApplicationContext(), UploadService.class);
                                    intent.putExtra(UploadService.ACTION, "sendTextGroup");
                                    intent.putExtra(UploadService.KEY_MESSAGE, vo);
                                    startService(intent);
                                } else {
                                    if (vo.getType().equals(Message.TYPE_VIDEO) || vo.getType().equals(Message.TYPE_IMAGE)) {
                                        if (isXmppConnected()) sendFile(vo);
                                    } else if (vo.getType().equals(Message.TYPE_LOC)) {
                                        if (isXmppConnected()) sendLocation(vo);
                                    } else if (vo.getType().equals(Message.TYPE_TEXT)) {
                                        if (isXmppConnected()) sendMessage(vo);
                                    } else if (vo.getType().equals(Message.TYPE_TARIK)) {
                                        if (isXmppConnected()) sendTarikPesan(vo);
                                    } else if (vo.getType().equals(Message.TYPE_READSTATUS)) {
                                        if (isXmppConnected()) sendReadStatus(vo);
                                        ;
                                    }
                                }
                            }

                            IntervalDB db = new IntervalDB(getApplicationContext());
                            db.open();
                            Cursor c = db.getSingleContact(13);
                            if (c.getCount() > 0) {
                                Intent intent = new Intent(getApplicationContext(), UploadProfileService.class);
                                startService(intent);
                            }
                            db.close();


                        } catch (Exception e) {
                            Log.e(getClass().getSimpleName(),
                                    "Error login: " + e.getMessage());
                            try {
                                sleep(3000);
                            } catch (InterruptedException ie) {
                            }
                        }
                    }
                    //end if authenticated
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(),
                            "Error logout: " + e.getMessage());
                    try {
                        sleep(3000);
                    } catch (InterruptedException ie) {
                    }
                }

                if (xmppConnection != null) {
                    Roster.getInstanceFor(xmppConnection).setSubscriptionMode(
                            Roster.SubscriptionMode.accept_all
                    );
                    addPresenceListener();
//Log.w("bukan1","ke tartik");
                    if (mucSubjectUpdatedListener == null) {
                        // Log.w("bukan2","ke tartik");
                        mucSubjectUpdatedListener = new MucSubjectUpdatedListener();
                        mucParticipantStatusListener = new GroupParticipantStatusListener();

                        Group group = new Group();
                        Cursor cursor = databaseHelper.query(
                                "SELECT * FROM " + group.getTableName(), null);
                        if (!xmppConnection.isConnected()) {
                            SystemClock.sleep(100);
                        }
                        while (cursor.moveToNext()) {
                            group = new Group(cursor);
                            if (Group.STATUS_INACTIVE.equals(group.getStatus()))
                                continue;
                            String groupId = group.getJabberId() + "@"
                                    + CONFERENCE_SERVER;
                            MultiUserChatManager mucMgr = MultiUserChatManager.getInstanceFor(xmppConnection);
                            MultiUserChat muc = mucMgr.getMultiUserChat(groupId);
                            try {
                                //sudah sampi sini
                                DiscussionHistory history = new DiscussionHistory();
                                history.setMaxChars(0);

                                muc.join(getCurContact().getJabberId(), "", history,
                                        SmackConfiguration.getDefaultPacketReplyTimeout());

                                registerMuc(muc, group.getJabberId(),
                                        Group.STATUS_ACTIVE);
                            } catch (XMPPException e) {
                                Log.e(getClass().getSimpleName(), "Failed rejoining "
                                        + group.getJabberId(), e);
                            } catch (SmackException.NotConnectedException e) {
                                e.printStackTrace();
                            } catch (SmackException.NoResponseException e) {
                                e.printStackTrace();
                            }

                        }
                        cursor.close();
                    }
                }

                connectionHelperStarted = false;
            }

        }

        private void addStreamManagementAckListener() {

            ((XMPPTCPConnection) xmppConnection).removeAllStanzaIdAcknowledgedListeners();

            smAckListener = new StanzaListener() {
                @Override
                public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
                    //Log.d(TAG, "ACK -> " + packet.toString());
                }
            };

            ((XMPPTCPConnection) xmppConnection).addStanzaAcknowledgedListener(smAckListener);
        }

    }

    class MessengerConnectionListener implements ConnectionListener {

        @Override
        public void connected(XMPPConnection connection) {
            if (NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                new ConnectionHelper().start();
                List<Rooms> roomses = botListDB.getListRooms();
                for (Rooms aa : roomses) {
                    String content = aa.getContent().toString();
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(content);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            if (jsonArray.getJSONObject(i).getString("category_tab").toString().equalsIgnoreCase("4")) {
                                ArrayList<RoomsDetail> roomsDetails = botListDB.allRoomDetailFormWithFlag("", aa.getUsername(), jsonArray.getJSONObject(i).getString("id_rooms_tab").toString(), "parent");
                                for (RoomsDetail bb : roomsDetails) {
                                    if (bb.getFlag_content().equalsIgnoreCase("3")) {
                                        Log.w("ia", "wwowo2");

                                        SimpleDateFormat hourFormat = new SimpleDateFormat(
                                                "HH:mm:ss dd/MM/yyyy", Locale.getDefault());
                                        long date = System.currentTimeMillis();
                                        String dateString = hourFormat.format(date);
                                        RoomsDetail orderModel = new RoomsDetail(bb.getId(), bb.getParent_tab(), aa.getUsername(), dateString, "1", null, "parent");
                                        botListDB.updateDetailRoomWithFlagContentParent(orderModel);

                                        new AllAboutUploadTask().getInstance(getApplicationContext()).UploadTask(MessengerConnectionService.this, bb.getId(), aa.getUsername(), bb.getParent_tab(), "");


                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                Contact contact = databaseHelper.getMyContact();

                if (contact != null) {
                    String SQL_SELECT_MESSAGES = "SELECT *  FROM "
                            + Message.TABLE_NAME + " WHERE " + Message.SOURCE + "=? "
                            + " and status = 2 and is_retry = 0 order by packet_id desc";

                    Cursor cursor = databaseHelper.query(SQL_SELECT_MESSAGES, new String[]{
                            contact.getJabberId()});

                    ArrayList<Message> messages = new ArrayList<Message>();

                    while (cursor.moveToNext()) {
                        Message vo = new Message(cursor);
                        messages.add(0, vo);
                    }
                    cursor.close();
                    for (Iterator<Message> iterator = messages.iterator(); iterator
                            .hasNext(); ) {
                        Message vo = iterator.next();
                        if (!new Validations().getInstance(getApplicationContext()).cekRoom(vo.getDestination()) && !vo.isRetry() && !vo.isGroupChat()) {
                            SimpleDateFormat hourFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                            Date d = vo.getSendDate();
                            if (new Validations().getInstance(getApplicationContext()).getShowMinute(String.valueOf(hourFormat.format(d)), 1) == 1) {
                                if (vo.getType().equals(Message.TYPE_VIDEO) || vo.getType().equals(Message.TYPE_IMAGE)) {
                                    try {
                                        new MessengerConnectionService().sendFile(vo);
                                    } catch (SmackException e) {
                                        e.printStackTrace();
                                    }
                                } else if (vo.getType().equalsIgnoreCase(Message.TYPE_LOC)) {
                                    try {
                                        sendLocation(vo);
                                    } catch (SmackException e) {
                                        e.printStackTrace();
                                    }
                                } else if (vo.getType().equalsIgnoreCase(Message.TYPE_BROADCAST)) {
                                    sendBroadCast(vo);
                                } else if (vo.getType().equalsIgnoreCase(Message.TYPE_TEXT)) {
                                    try {
                                        sendMessage(vo);
                                    } catch (SmackException e) {
                                        e.printStackTrace();
                                    }
                                } else if (vo.getType().equalsIgnoreCase(Message.TYPE_TARIK)) {
                                    try {
                                        sendTarikPesan(vo);
                                    } catch (SmackException e) {
                                        e.printStackTrace();
                                    }
                                } else if (vo.getType().equalsIgnoreCase(Message.TYPE_READSTATUS)) {
                                    try {
                                        sendReadStatus(vo);
                                    } catch (SmackException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }


                    }
                }

            }
        }

        @Override
        public void authenticated(XMPPConnection connection, boolean resumed) {
            //Log.d(getClass().getSimpleName(), "connection authenticated");
        }

        @Override
        public void connectionClosed() {
            // Log.w(getClass().getSimpleName(), "connection closed. reconnecting...");
            sendBroadcast(ACTION_DISCONNECTED);
            new ConnectionHelper().start();
            /*xmppOpen();*/
        }

        @Override
        public void connectionClosedOnError(Exception e) {

            //jangan close connection kalau parse packet (misal: packet timestamp duh)```````
            if (e instanceof ParseException) {
               /* Log.w(getClass().getSimpleName(),
                        "Parse Exception " + e.getMessage(), e);*/

            } else {
               /* Log.w(getClass().getSimpleName(),
                        "Connection closed due to error: " + e.getMessage(), e);*/

                disconnect();
                connectionClosed();
            }
        }

        @Override
        public void reconnectingIn(int timeToReconnect) {
            // Log.w(getClass().getSimpleName(), "Connection closed. reconnecting in .." + String.valueOf(timeToReconnect));
        }

        @Override
        public void reconnectionFailed(Exception e) {
           /* Log.w(getClass().getSimpleName(),
                    "Reconnection fails due to: " + e.getMessage()
                            + ". Will try again in 5 seconds...", e);*/
            //SystemClock.sleep(5000);
            //new ConnectionHelper().start();
        }

        @Override
        public void reconnectionSuccessful() {
        }
    }

    class GroupParticipantStatusListener implements ParticipantStatusListener {

        private String[] parseParticipant(String participant) {
            // Log.w("like1","1");
            String[] result = new String[]{"", "", ""};

            if (participant == null)
                return result;
            String[] tmp = Group.parseGroupId(participant);

            // GroupId
            result[0] = tmp[0];

            if (tmp.length == 2) {
                // JabberId
                result[1] = tmp[1];

                // Contact Name
                Contact c = databaseHelper.getContact(result[1]);
                if (c == null) {
                    result[2] = "+" + result[1];
                } else {
                    if (getCurContact().getJabberId().equals(c.getJabberId())) {
                        result[2] = "You";
                    } else {
                        result[2] = c.getName();
                    }
                }
            }
            return result;

        }

        @Override
        public void adminGranted(String participant) {
        }

        @Override
        public void adminRevoked(String participant) {
        }

        @Override
        public void banned(String participant, String actor, String reason) {
        }

        @Override
        public void joined(String participant) {
            // Log.w("like2","2");
            String[] tmp = parseParticipant(participant);
            if ("".equals(tmp[0]))
                return;
            Message vo = createGroupInfoMessage(tmp[0], tmp[1], tmp[2]
                    + " joined the group");
            onMessageReceived(vo);
            Group g = databaseHelper.getGroup(tmp[0]);
            if (g != null) {
                if (Group.STATUS_OWNER.equals(g.getStatus())) {
                    try {
                        grantModeratorRole(tmp[0], tmp[1]);
                    } catch (XMPPException e) {
                        Log.e(getClass().getSimpleName(),
                                "Error granting moderator to: " + tmp[1], e);
                    }
                }
            }
        }

        @Override
        public void kicked(String participant, String actor, String reason) {
        }

        @Override
        public void left(String participant) {
            String[] tmp = parseParticipant(participant);
            if ("".equals(tmp[0]))
                return;
            Message vo = createGroupInfoMessage(tmp[0], tmp[1], tmp[2]
                    + " left the group");
            onMessageReceived(vo);
        }

        @Override
        public void membershipGranted(String participant) {
        }

        @Override
        public void membershipRevoked(String participant) {
        }

        @Override
        public void moderatorGranted(String participant) {
        }

        @Override
        public void moderatorRevoked(String participant) {
        }

        @Override
        public void nicknameChanged(String participant, String nick) {
        }

        @Override
        public void ownershipGranted(String participant) {
        }

        @Override
        public void ownershipRevoked(String participant) {
        }

        @Override
        public void voiceGranted(String participant) {
            // TODO Auto-generated method stub

        }

        @Override
        public void voiceRevoked(String participant) {
            // TODO Auto-generated method stub

        }

    }

    class MucSubjectUpdatedListener implements SubjectUpdatedListener {
        public void subjectUpdated(String subject, String from) {
            // Log.w("like3","3");
            String[] tmp = Group.parseGroupId(from);
            Group g = databaseHelper.getGroup(tmp[0]);
            if (g != null && g.getName().equals(subject)) {
                return;
            }
            updateGroupChatSubject(g, tmp[1], subject);
        }
    }

    class GroupMessageListener implements MessageListener {
        @Override
        public void processMessage(org.jivesoftware.smack.packet.Message msg) {
            // Log.w("like4",msg.toString());
        }
    }

    class GroupChat {
        private Group group;
        private MultiUserChat muc;

        public GroupChat(Group group, MultiUserChat muc) {
            this.group = group;
            this.muc = muc;
        }

        public MultiUserChat getMuc() {
            return muc;
        }

        public void setMuc(MultiUserChat muc) {
            this.muc = muc;
        }

        public Group getGroup() {
            return group;
        }

        public void setGroup(Group group) {
            this.group = group;
        }
    }

    public static void clearCache(Context context) {
        final File cache = new File(
                context.getApplicationContext().getCacheDir(),
                PICASSO_CACHE);
        if (cache.exists()) {
            deleteFolder(cache);
        }
    }

    private static void deleteFolder(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles())
                deleteFolder(child);
        }
        fileOrDirectory.delete();
    }

    private void saveImage(Bitmap finalBitmap, String iname) {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        System.out.println(root + " Root value in saveImage Function");
        File fullCacheDir = new File(Environment.getExternalStorageDirectory().toString(), cacheDir);
        if (!fullCacheDir.exists()) {
            fullCacheDir.mkdirs();
        }
        File file = new File(fullCacheDir, iname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        File[] files = fullCacheDir.listFiles();
        int numberOfImages = files.length;
        System.out.println("Total images in Folder " + numberOfImages);
    }

    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            return obj.getAddressLine(0).split(",")[0] + " | " + obj.getAddressLine(0).split(",")[1] + " | " + obj.getSubAdminArea();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    private class LocationListener implements android.location.LocationListener {

        Location mLastLocation;
        String lat = "";
        String lng = "";
        String acc = "";

        public LocationListener(String provider) {
            Log.e("zharfan", "LocationListener : " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            lat = "" + location.getLatitude();
            lng = "" + location.getLongitude();
            acc = "" + location.getAccuracy();
            locSpyChange = location;

            if (isMockLocationOn(location, getApplicationContext())) {
                //boobfan
            }

            Log.e(TAG, "onLocationChanged : " + location);
            mLastLocation.set(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged : " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled : " + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled : " + provider);
        }


    }


    public static boolean isMockLocationOn(Location location, Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return location.isFromMockProvider();
        } else {
            String mockLocation = "0";
            try {
                mockLocation = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return !mockLocation.equals("0");
        }
    }

}
