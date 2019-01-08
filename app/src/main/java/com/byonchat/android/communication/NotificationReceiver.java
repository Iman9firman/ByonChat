package com.byonchat.android.communication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.LogWriter;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.Html;
import android.util.Log;

import com.byonchat.android.ByonChatMainRoomActivity;
import com.byonchat.android.ConversationActivity;
import com.byonchat.android.ConversationGroupActivity;
import com.byonchat.android.MainActivity;
import com.byonchat.android.MemberDetailActivity;
import com.byonchat.android.R;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.utils.GPSTracker;
import com.byonchat.android.utils.UploadService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.Date;

import me.leolin.shortcutbadger.ShortcutBadgeException;
import me.leolin.shortcutbadger.ShortcutBadger;

public class NotificationReceiver extends BroadcastReceiver {
    public static final int NOTIFY_ID = 2001;
    public static final int NOTIFY_ID_CONV = 2111;
    public static final int NOTIFY_ID_CARD = 2002;
    public static final int NOTIFY_GPS_ON = 2003;
    public static final int NOTIFY_TASK = 2001;
    MessengerDatabaseHelper messengerHelper;
    private static String SQL_SELECT_TOTAL_MESSAGES_UNREAD_ALL = "SELECT count(*) total FROM "
            + Message.TABLE_NAME
            + " WHERE status = ?";
    String realname;
    Context ctx;

    @Override
    public void onReceive(Context context, Intent intent) {
        ctx = context;
        Message vo = intent
                .getParcelableExtra(MessengerConnectionService.KEY_MESSAGE_OBJECT);
        String name = intent
                .getStringExtra(MessengerConnectionService.KEY_CONTACT_NAME);
        String loc = intent
                .getStringExtra(MessengerConnectionService.KEY_LOC_REQ);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context);
        Uri url = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.whistle);
        builder.setSound(url);
        builder.setLights(Color.GREEN, 500, 500);
        long[] pattern = {500, 500};
        builder.setVibrate(pattern);

        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(context);
        }

        if (vo != null) {
            Log.w("sudah", "1");
            Log.w("sudah1", vo.getMessage());
            bc:
//1_277091610admin//2595//8;List Pengiriman Mobil
            if (vo.getMessage().startsWith("bc://")) {

                Log.w("sudah1", vo.getMessage());

                builder.setSmallIcon(R.drawable.ic_notif);
                builder.setLargeIcon(BitmapFactory.decodeResource(Resources.getSystem(), R.mipmap.ic_launcher));
                builder.setContentTitle(name);
                String content = Message.parsedMessageBodyHtmlCode(vo, context);
                builder.setContentText(content);
                builder.setTicker("New message from " + name);

                String asal[] = vo.getMessage().split("//");

                Intent destIntent = new Intent(context, ByonChatMainRoomActivity.class);
                BotListDB botListDB = BotListDB.getInstance(context);

                Cursor cursorIS = botListDB.getSingleRoom(asal[1]);

                if (cursorIS.getCount() > 0) {
                    String a = cursorIS.getString(cursorIS.getColumnIndexOrThrow(BotListDB.ROOM_ID));
                    String b = cursorIS.getString(cursorIS.getColumnIndexOrThrow(BotListDB.ROOM_USERNAME));
                    String c = cursorIS.getString(cursorIS.getColumnIndexOrThrow(BotListDB.ROOM_REALNAME));
                    String d = cursorIS.getString(cursorIS.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT));
                    String e = cursorIS.getString(cursorIS.getColumnIndexOrThrow(BotListDB.ROOM_COLOR));
                    String h = cursorIS.getString(cursorIS.getColumnIndexOrThrow(BotListDB.ROOM_ICON));

                    try {
                        Log.w("lala2", d);
                        JSONArray jsonArray = new JSONArray(d);
                        for (int iss = 0; iss < jsonArray.length(); iss++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(iss);
                            Log.w("kasuk :: ", jsonObject.toString());
                            //   Log.w("kasuk :: " + iss, jsonArray.getJSONObject(iss).getString("urutan"));

                            if (jsonObject.getString("id_rooms_tab").equalsIgnoreCase(asal[2])) {

                                String aaa = b;
                                String bbb = (jsonObject.getString("include_latlong"));
                                String ccc = (jsonObject.getString("url_tembak"));
                                String ddd = (jsonObject.getString("category_tab"));
                                String ddds = (jsonObject.getString("id_rooms_tab"));
                                String eee = (jsonObject.getString("include_pull"));
                                String fff = (jsonObject.getString("tab_name"));
                                String hhh = (jsonObject.getString("status"));
                                String iii = h;

                                JSONObject jsonObject1 = new JSONObject(e);
                                String jjj = jsonObject1.getString("a");
                                String kkk = jsonObject1.getString("b");
                                String lll = jsonObject1.getString("e");
                                String mmm = c;

                                destIntent.putExtra(ConversationActivity.KEY_JABBER_ID, aaa);
                                destIntent.putExtra(ByonChatMainRoomActivity.EXTRA_POSITION, iss);
                                destIntent.putExtra(ByonChatMainRoomActivity.EXTRA_COLOR, jjj);
                                destIntent.putExtra(ByonChatMainRoomActivity.EXTRA_COLORTEXT, kkk);
                                destIntent.putExtra(ByonChatMainRoomActivity.EXTRA_TARGETURL, lll);
                                destIntent.putExtra(ByonChatMainRoomActivity.EXTRA_CATEGORY, ddd);
                                destIntent.putExtra(ByonChatMainRoomActivity.EXTRA_TITLE, fff);
                                destIntent.putExtra(ByonChatMainRoomActivity.EXTRA_URL_TEMBAK, ccc);
                                destIntent.putExtra(ByonChatMainRoomActivity.EXTRA_ID_ROOMS_TAB, ddds);
                                destIntent.putExtra(ByonChatMainRoomActivity.EXTRA_INCLUDE_PULL, eee);
                                destIntent.putExtra(ByonChatMainRoomActivity.EXTRA_INCLUDE_LATLONG, bbb);
                                destIntent.putExtra(ByonChatMainRoomActivity.EXTRA_STATUS, hhh);
                                destIntent.putExtra(ByonChatMainRoomActivity.EXTRA_NAME, mmm);
                                destIntent.putExtra(ByonChatMainRoomActivity.EXTRA_ICON, iii);

                            }
                        }

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                        Log.w("lala33", e1.getMessage());
                    }

                }


                destIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                destIntent.putExtra(ConversationActivity.KEY_JABBER_ID, vo.getSource());
                builder.setContentIntent(PendingIntent.getActivity(context, 0,
                        destIntent, PendingIntent.FLAG_UPDATE_CURRENT));

                NotificationManager mgr = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel notificationChannel = new NotificationChannel("Message", "ByonChat", importance);
                    notificationChannel.enableLights(true);
                    notificationChannel.setLightColor(Color.RED);
                    notificationChannel.enableVibration(true);
                    notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    if (mgr != null) {
                        builder.setChannelId("Message");
                        mgr.createNotificationChannel(notificationChannel);
                    }
                }
                if (mgr != null) {
                    mgr.notify(NOTIFY_ID, builder.build());
                }

                int badgeCount = 0;
                Cursor cursor = messengerHelper.query(
                        SQL_SELECT_TOTAL_MESSAGES_UNREAD_ALL,
                        new String[]{String.valueOf(Message.STATUS_UNREAD)});
                int indexTotal = cursor.getColumnIndex("total");
                while (cursor.moveToNext()) {
                    badgeCount = cursor.getInt(indexTotal);
                }
                cursor.close();
                ShortcutBadger.applyCount(context, badgeCount);

            } else {
                builder.setSmallIcon(R.drawable.ic_notif);
                builder.setLargeIcon(BitmapFactory.decodeResource(Resources.getSystem(), R.mipmap.ic_launcher));
                builder.setContentTitle(name);
                String content = Message.parsedMessageBodyHtmlCode(vo, context);
                builder.setContentText(content);
                builder.setTicker("New message from " + name);
                Intent destIntent = new Intent(context, ConversationActivity.class);
                destIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                destIntent.putExtra(ConversationActivity.KEY_JABBER_ID, vo.getSource());

                if (vo.isGroupChat()) {
                    String nameAdd = vo.getSourceInfo();
                    Contact contact = messengerHelper.getContact(vo.getSourceInfo());

                    if (contact != null) {
                        nameAdd = contact.getName();
                    }

                    if (nameAdd.equals(messengerHelper.getMyContact().getName())) nameAdd = "";
                    String contentGroup = Message.parsedMessageBodyHtmlCode(vo, context);
                    builder.setContentText(nameAdd + " : " + contentGroup);
                    destIntent = new Intent(context, ConversationGroupActivity.class);
                    destIntent.putExtra(ConversationGroupActivity.EXTRA_KEY_NEW_PERSON, "0");
                    destIntent.putExtra(ConversationGroupActivity.EXTRA_KEY_STICKY, "0");
                    destIntent.putExtra(ConversationActivity.KEY_JABBER_ID, vo.getSource());
                }
                builder.setContentIntent(PendingIntent.getActivity(context, 0,
                        destIntent, PendingIntent.FLAG_UPDATE_CURRENT));

                NotificationManager mgr = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel notificationChannel = new NotificationChannel("Message", "ByonChat", importance);
                    notificationChannel.enableLights(true);
                    notificationChannel.setLightColor(Color.RED);
                    notificationChannel.enableVibration(true);
                    notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    if (mgr != null) {
                        builder.setChannelId("Message");
                        mgr.createNotificationChannel(notificationChannel);
                    }
                }
                if (mgr != null) {
                    mgr.notify(NOTIFY_ID, builder.build());
                }

                int badgeCount = 0;
                Cursor cursor = messengerHelper.query(
                        SQL_SELECT_TOTAL_MESSAGES_UNREAD_ALL,
                        new String[]{String.valueOf(Message.STATUS_UNREAD)});
                int indexTotal = cursor.getColumnIndex("total");
                while (cursor.moveToNext()) {
                    badgeCount = cursor.getInt(indexTotal);
                }
                cursor.close();
                ShortcutBadger.applyCount(context, badgeCount);
            }

        } else if (name != null) {
            //add members
            Log.w("sudah", "2");
            builder.setSmallIcon(R.drawable.ic_notif);
            builder.setLargeIcon(BitmapFactory.decodeResource(Resources.getSystem(), R.mipmap.ic_launcher));
            String isinya[] = name.split(";");
            if (isinya.length > 2) {
                builder.setContentTitle(isinya[1]);
                String content = isinya[1] + " add you";
                builder.setContentText(content);
                builder.setTicker("New Memberships");

                Intent destIntent = new Intent(context, MemberDetailActivity.class);
                destIntent.putExtra(MemberDetailActivity.KEY_MEMBERS_ID, isinya[0]);
                destIntent.putExtra(MemberDetailActivity.KEY_MEMBERS_NAME, isinya[1]);
                destIntent.putExtra(MemberDetailActivity.KEY_MEMBERS_COLOR, isinya[2]);
                destIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                builder.setContentIntent(PendingIntent.getActivity(context, 0,
                        destIntent, PendingIntent.FLAG_UPDATE_CURRENT));
                NotificationManager mgr = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel notificationChannel = new NotificationChannel("Message", "ByonChat", importance);
                    notificationChannel.enableLights(true);
                    notificationChannel.setLightColor(Color.RED);
                    notificationChannel.enableVibration(true);
                    notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    if (mgr != null) {
                        builder.setChannelId("Message");
                        mgr.createNotificationChannel(notificationChannel);
                    }
                }
                if (mgr != null) {
                    mgr.notify(NOTIFY_ID_CARD, builder.build());
                }

            } else if (isinya.length == 1) {
                Log.w("sudah", "3");
                JSONObject jObject = null;
                try {
                    jObject = new JSONObject(isinya[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (jObject != null) {
                    Log.w("sudah", "4");
                    try {
                        String username = jObject.getString("a");
                        String realname = jObject.getString("b");
                        if (username.equalsIgnoreCase("DATABASE")) {
                            Log.w("sudah", "5");
                            builder.setContentTitle("Success");
                            builder.setContentText("DATABASE has been updated");

                            Intent destIntent = new Intent(context, ByonChatMainRoomActivity.class);
                            destIntent.putExtra(ConversationActivity.KEY_JABBER_ID, realname);
                            destIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            builder.setContentIntent(PendingIntent.getActivity(context, 0,
                                    destIntent, PendingIntent.FLAG_UPDATE_CURRENT));
                            NotificationManager mgr = (NotificationManager) context
                                    .getSystemService(Context.NOTIFICATION_SERVICE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                int importance = NotificationManager.IMPORTANCE_HIGH;
                                NotificationChannel notificationChannel = new NotificationChannel("Message", "ByonChat", importance);
                                notificationChannel.enableLights(true);
                                notificationChannel.setLightColor(Color.RED);
                                notificationChannel.enableVibration(true);
                                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                                if (mgr != null) {
                                    builder.setChannelId("Message");
                                    mgr.createNotificationChannel(notificationChannel);
                                }
                            }
                            if (mgr != null) {
                                mgr.notify(NOTIFY_TASK, builder.build());
                            }

                        } else {
                            Log.w("sudah", "6");
                            builder.setContentTitle("Success");
                            builder.setContentText("Refresh Room " + realname);

                            Intent destIntent = new Intent(context, ByonChatMainRoomActivity.class);
                            destIntent.putExtra(ConversationActivity.KEY_JABBER_ID, username);
                            destIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            builder.setContentIntent(PendingIntent.getActivity(context, 0,
                                    destIntent, PendingIntent.FLAG_UPDATE_CURRENT));
                            NotificationManager mgr = (NotificationManager) context
                                    .getSystemService(Context.NOTIFICATION_SERVICE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                int importance = NotificationManager.IMPORTANCE_HIGH;
                                NotificationChannel notificationChannel = new NotificationChannel("Message", "ByonChat", importance);
                                notificationChannel.enableLights(true);
                                notificationChannel.setLightColor(Color.RED);
                                notificationChannel.enableVibration(true);
                                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                                if (mgr != null) {
                                    builder.setChannelId("Message");
                                    mgr.createNotificationChannel(notificationChannel);
                                }
                            }
                            if (mgr != null) {
                                mgr.notify(NOTIFY_TASK, builder.build());
                            }
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.w("sudah", "7");
                    builder.setContentTitle("Success");
                    builder.setContentText(name);
                    builder.setContentIntent(PendingIntent.getActivity(context, 0,
                            null, PendingIntent.FLAG_UPDATE_CURRENT));
                    NotificationManager mgr = (NotificationManager) context
                            .getSystemService(Context.NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        int importance = NotificationManager.IMPORTANCE_HIGH;
                        NotificationChannel notificationChannel = new NotificationChannel("Message", "ByonChat", importance);
                        notificationChannel.enableLights(true);
                        notificationChannel.setLightColor(Color.RED);
                        notificationChannel.enableVibration(true);
                        notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                        if (mgr != null) {
                            builder.setChannelId("Message");
                            mgr.createNotificationChannel(notificationChannel);
                        }
                    }
                    if (mgr != null) {
                        mgr.notify(NOTIFY_TASK, builder.build());
                    }
                }

            } else {
                Log.w("sudah", "8");
                builder.setContentTitle("Success");
                builder.setContentText("success upload task");
                builder.setTicker("TASK");

                Intent destIntent = new Intent(context, ByonChatMainRoomActivity.class);
                destIntent.putExtra(ConversationActivity.KEY_JABBER_ID, isinya[1]);
                destIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                builder.setContentIntent(PendingIntent.getActivity(context, 0,
                        destIntent, PendingIntent.FLAG_UPDATE_CURRENT));
                NotificationManager mgr = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel notificationChannel = new NotificationChannel("Message", "ByonChat", importance);
                    notificationChannel.enableLights(true);
                    notificationChannel.setLightColor(Color.RED);
                    notificationChannel.enableVibration(true);
                    notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    if (mgr != null) {
                        builder.setChannelId("Message");
                        mgr.createNotificationChannel(notificationChannel);
                    }
                }
                if (mgr != null) {
                    mgr.notify(NOTIFY_TASK, builder.build());
                }
            }

        }
        if (loc != null) {
            Log.w("sudah", "9");
            String pesan[] = loc.split(";");
            GPSTracker gps = new GPSTracker(context);
            if (gps.canGetLocation()) {
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();
                //  if (latitude == 0.0 && longitude == 0.0) {
                String planText = null;
                planText = "location;" + pesan[1] + ";" + latitude + "," + longitude + ";" + pesan[2] + ";" + simInfo();
                if (NetworkInternetConnectionStatus.getInstance(context).isOnline(context)) {
                    Message report = new Message(messengerHelper.getMyContact().getJabberId(), "x_byonchatbackground", planText);
                    report.setType("text");
                    report.setSendDate(new Date());
                    report.setStatus(Message.STATUS_INPROGRESS);
                    report.generatePacketId();

                    Intent i = new Intent();
                    i.setAction(UploadService.FILE_SEND_INTENT);
                    i.putExtra(MessengerConnectionService.KEY_MESSAGE_OBJECT, report);
                    context.sendBroadcast(i);
                } else {
                    sendSMSMessage(planText);
                }
            } else {
                Log.w("ate", "sss1");
             /*   Intent i = new Intent();
                i.setClassName("com.byonchat.android", "com.byonchat.android.DialogPopUpActivity");
                intent.putExtra("pesan", loc);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);*/
                String planText = null;

                planText = "location;" + pesan[1] + ";" + "" + "," + "" + ";" + pesan[2] + ";" + simInfo();
                if (NetworkInternetConnectionStatus.getInstance(context).isOnline(context)) {
                    Message report = new Message(messengerHelper.getMyContact().getJabberId(), "x_byonchatbackground", planText);
                    report.setType("text");
                    report.setSendDate(new Date());
                    report.setStatus(Message.STATUS_INPROGRESS);
                    report.generatePacketId();

                    Intent ia = new Intent();
                    ia.setAction(UploadService.FILE_SEND_INTENT);
                    ia.putExtra(MessengerConnectionService.KEY_MESSAGE_OBJECT, report);
                    context.sendBroadcast(ia);
                } else {
                    sendSMSMessage(planText);
                }

            }
        }

    }

    protected void sendSMSMessage(String content) {
        String phoneNo = "+6288977669999";
        // String phoneNo = "+628158888248";
        String message = content;

        try {
         /*   SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);*/
            SmsManager sms = SmsManager.getDefault();
            String sent = "android.telephony.SmsManager.STATUS_ON_ICC_SENT";
            PendingIntent piSent = PendingIntent.getBroadcast(ctx, 0, new Intent(sent), 0);

            sms.sendTextMessage(phoneNo, null, message, piSent, null);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("MissingPermission")
    public String simInfo() {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
            GsmCellLocation cellLocation = (GsmCellLocation) telephonyManager.getCellLocation();
            if (cellLocation != null) {
                int cid = cellLocation.getCid();
                int lac = cellLocation.getLac();
                return "|" + lac + "|" + cid + "|" + telephonyManager.getNetworkOperator().toString().substring(0, 3) + "|" + telephonyManager.getNetworkOperator().toString().substring(3, telephonyManager.getNetworkOperator().toString().length()) + "|" + manufacturer + "|" + model;

            }
        } catch (Exception e) {
            return "|no simcard|";
        }

        return "|no simcard|";
    }

}