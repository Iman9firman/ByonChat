package com.byonchat.android.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import com.byonchat.android.utils.Validations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.text.Normalizer;
import java.util.Date;
import java.util.regex.Pattern;

public class Message implements Data {
    public static final String LOCATION_DELIMITER = ";";
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_VIDEO = "video";
    public static final String TYPE_TEXT = "text";
    public static final String TYPE_LOC = "location";
    public static final String TYPE_STICKY = "sticky";
    public static final String TYPE_VOTING = "voting";
    public static final String TYPE_INFO = "info";
    public static final String TYPE_BROADCAST = "broadcast";
    public static final String TYPE_READSTATUS = "readStatus";
    public static final String TYPE_TARIK = "tarikPesan";
    public static final String TYPE_REPORT_TARIK = "reportPesan";
    public static final String TYPE_DELIVERSTATUS = "deliverStatus";

    public static final String STATUS_TYPE_RECEIVE = "receive";
    public static final String STATUS_TYPE_DELIVER = "deliver";
    public static final String STATUS_TYPE_SEND = "send";
    public static final String STATUS_TYPE_FAILED = "failed";
    public static final String STATUS_TYPE_INPROSES = "inproses";
    public static final String STATUS_TYPE_READ = "read";


    public static final String TABLE_NAME = "messages";
    public static final String _ID = "_id";
    public static final String DELIVERED_DATE = "delivered_date";
    public static final String SEND_DATE = "send_date";
    public static final String PACKET_ID = "packet_id";
    public static final String SOURCE = "source";
    public static final String SOURCE_INFO = "source_info";
    public static final String DESTINATION = "destination";
    public static final String MESSAGE = "message";
    public static final String NUMBER = "number";
    public static final String STATUS = "status";
    public static final String TYPE = "type";
    public static final String IS_GROUP_CHAT = "is_group_chat";
    public static final String IS_RETRY = "is_retry";

    public static final int STATUS_INPROGRESS = 1;
    public static final int STATUS_SENT = 2;
    public static final int STATUS_DELIVERED = 3;
    public static final int STATUS_FAILED = 9;
    public static final int STATUS_UNREAD = 12;
    public static final int STATUS_NOTSEND = 13;
    public static final int STATUS_READ = 14;

    private static String[] fieldNames;

    static {
        fieldNames = new String[11];
        fieldNames[0] = SOURCE;
        fieldNames[1] = DESTINATION;
        fieldNames[2] = SEND_DATE;
        fieldNames[3] = DELIVERED_DATE;
        fieldNames[4] = MESSAGE;
        fieldNames[5] = STATUS;
        fieldNames[6] = PACKET_ID;
        fieldNames[7] = TYPE;
        fieldNames[8] = SOURCE_INFO;
        fieldNames[9] = IS_GROUP_CHAT;
        fieldNames[10] = IS_RETRY;
    }

    private long id;
    private Date deliveredDate;
    private Date sendDate;
    private String source;
    private String sourceInfo;
    private String destination;
    private String message;
    private String packetId;
    private String type = "text";
    private int status;
    private boolean isGroupChat;
    private boolean isRetry;

    public Message() {
        id = 0;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public Message(Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex(ID));
        source = cursor.getString(cursor.getColumnIndex(SOURCE));
        destination = cursor.getString(cursor.getColumnIndex(DESTINATION));
        long tmp = cursor.getLong(cursor.getColumnIndex(SEND_DATE));
        if (tmp != 0) {
            sendDate = new Date(tmp * 1000);
        }
        tmp = cursor.getLong(cursor.getColumnIndex(DELIVERED_DATE));
        if (tmp != 0) {
            deliveredDate = new Date(tmp * 1000);
        }
        message = cursor.getString(cursor.getColumnIndex(MESSAGE));
        status = cursor.getInt(cursor.getColumnIndex(STATUS));
        packetId = cursor.getString(cursor.getColumnIndex(PACKET_ID));
        type = cursor.getString(cursor.getColumnIndex(TYPE));
        sourceInfo = cursor.getString(cursor.getColumnIndex(SOURCE_INFO));
        isGroupChat = parseGroupChatInfo(cursor.getInt(cursor
                .getColumnIndex(IS_GROUP_CHAT)));
        isRetry = parseRetryInfo(cursor.getInt(cursor
                .getColumnIndex(IS_RETRY)));
    }

    private boolean parseGroupChatInfo(int gchat) {
        return (gchat == 0) ? false : true;
    }

    private boolean parseRetryInfo(int retry) {
        return (retry == 0) ? false : true;
    }

    private int parseGroupChatInfo(boolean gchat) {
        return (gchat) ? 1 : 0;
    }

    private int parseRetryInfo(boolean retry) {
        return (retry) ? 1 : 0;
    }

    public Message(String source, String destination, String message) {
        this.source = source;
        this.destination = destination;
        this.message = message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Message(Parcel parcel) {
        id = parcel.readLong();
        source = parcel.readString();
        destination = parcel.readString();
        sendDate = new Date(parcel.readLong());
        deliveredDate = new Date(parcel.readLong());
        message = parcel.readString();
        status = parcel.readInt();
        packetId = parcel.readString();
        type = parcel.readString();
        sourceInfo = parcel.readString();
        isGroupChat = parseGroupChatInfo(parcel.readInt());
        isRetry = parseRetryInfo(parcel.readInt());
    }

    public String generatePacketId() {
        String pid = source + destination;
        int hash = pid.hashCode();
        packetId = Integer.toHexString(hash) + "-"
                + Long.toHexString(System.currentTimeMillis());
        return packetId;
    }

    public String generatePacketId(String durasi) {
        String pid = source + destination;
        int hash = pid.hashCode();
        packetId = Integer.toHexString(hash) + ";" + durasi + ";"
                + Long.toHexString(System.currentTimeMillis());
        return packetId;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String[] getFieldNames() {
        return fieldNames;
    }

    public Date getDeliveredDate() {
        return deliveredDate;
    }

    public void setDeliveredDate(Date deliveredDate) {
        this.deliveredDate = deliveredDate;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sentDate) {
        this.sendDate = sentDate;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPacketId() {
        return packetId;
    }

    public void setPacketId(String packetId) {
        this.packetId = packetId;
    }

    public void setSourceInfo(String sourceInfo) {
        this.sourceInfo = sourceInfo;
    }

    public String getSourceInfo() {
        return sourceInfo;
    }

    public boolean isGroupChat() {
        return isGroupChat;
    }

    public void setGroupChat(boolean isGroupChat) {
        this.isGroupChat = isGroupChat;
    }

    public boolean isRetry() {
        return isRetry;
    }

    public void setIsRetry(boolean isRetry) {
        this.isRetry = isRetry;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        long tmp = 0;
        if (getDeliveredDate() != null) {
            tmp = getDeliveredDate().getTime() / 1000;
        }
        cv.put(DELIVERED_DATE, tmp);
        tmp = 0;
        if (getSendDate() != null) {
            tmp = getSendDate().getTime() / 1000;
        }
        cv.put(SEND_DATE, tmp);
        cv.put(SOURCE, getSource());
        cv.put(STATUS, getStatus());
        cv.put(DESTINATION, getDestination());
        cv.put(MESSAGE, getMessage());
        cv.put(PACKET_ID, getPacketId());
        cv.put(TYPE, getType());
        cv.put(SOURCE_INFO, getSourceInfo());
        cv.put(IS_GROUP_CHAT, parseGroupChatInfo(isGroupChat()));
        cv.put(IS_RETRY, parseRetryInfo(isGroupChat()));
        if (id != 0) {
            cv.put(ID, getId());
        }
        return cv;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Message) {
            return (getPacketId().equals(((Message) o).getPacketId()));
        }
        return false;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(getId());
        parcel.writeString(getSource());
        parcel.writeString(getDestination());
        long tmp = 0;
        if (getSendDate() != null) {
            tmp = getSendDate().getTime();
        }
        parcel.writeLong(tmp);
        tmp = 0;
        if (getDeliveredDate() != null) {
            tmp = getDeliveredDate().getTime();
        }
        parcel.writeLong(tmp);
        parcel.writeString(getMessage());
        parcel.writeInt(getStatus());
        parcel.writeString(getPacketId());
        parcel.writeString(getType());
        parcel.writeString(getSourceInfo());
        parcel.writeInt(parseGroupChatInfo(isGroupChat()));
        parcel.writeInt(parseRetryInfo(isRetry()));
    }

    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public static String parsedMessageBody(Message vo, Context context) {
        return parsedMessageBody(vo, 20, context);
    }

    public static String parsedMessageBody(Message vo, int maxLen, Context context) {
        String message;
        if (vo.getStatus() == Message.STATUS_FAILED) {
            message = "Message fail to deliver";
        } else if (Message.TYPE_IMAGE.equals(vo.getType())) {
            message = "Image";
        } else if (Message.TYPE_VIDEO.equals(vo.getType())) {
            message = "Video";
        } else if (Message.TYPE_LOC.equals(vo.getType())) {
            message = "Location";
        } else {
            message = vo.getMessage().replaceAll("\n|\r,", " ");

            if (message.length() > maxLen) {
                message = message.substring(0, maxLen) + " ...";
            }

            String text = Html.fromHtml(message).toString();
            Pattern htmlPattern = Pattern.compile(".*\\<[^>]+>.*", Pattern.DOTALL);
            boolean isHTML = htmlPattern.matcher(message).matches();
            if (isHTML) {
                if (text.contains("<")) {
                    text = Html.fromHtml(Html.fromHtml(message).toString()).toString();
                }
            } else {
                text = vo.getMessage();
            }

            if (text.length() > 20) {
                message = text.substring(0, 20) + " ...";
            } else {
                message = text;
            }

            if (new Validations().getInstance(context).cekRoom(vo.getSource())) {
                JSONObject jObject = null;
                try {
                    jObject = new JSONObject(vo.getMessage());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (jObject != null) {
                    try {
                        String buka = jObject.getString("buka");
                        message = Html.fromHtml(URLDecoder.decode(buka)).toString();

                        if (message.contains("<")) {
                            message = Html.fromHtml(Html.fromHtml(buka).toString()).toString();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return message;
    }

    public static String parsedMessageText(String text) {
        String message;
        message = text.replaceAll("\n|\r,", " ");
        if (message.length() > 20) {
            message = message.substring(0, 20) + " ...";
        }
        return message;
    }

    public static String parsedMessageBodyHtmlCode(Message vo, Context context) {
        String message;
        if (vo.getStatus() == Message.STATUS_FAILED) {
            message = "Message fail to deliver";
        } else if (Message.TYPE_IMAGE.equals(vo.getType())) {
            message = "Image";
        } else if (Message.TYPE_VIDEO.equals(vo.getType())) {
            message = "Video";
        } else if (Message.TYPE_LOC.equals(vo.getType())) {
            message = "Location";
        } else {

            message = vo.getMessage().replaceAll("\n|\r,", " ");
            if (message.equalsIgnoreCase("bc://1_340113808admin;Work Schedule") || message.equalsIgnoreCase("bc://u_341114250arlandi;Work Schedule")) {
                return "Work Schedule";
            } else if (message.startsWith("bc://")) {
                String r[] = message.split(";");
                if (r.length >= 2) {
                    message = r[1];
                }

            } else {
                String text = Html.fromHtml(message).toString();
                Pattern htmlPattern = Pattern.compile(".*\\<[^>]+>.*", Pattern.DOTALL);
                boolean isHTML = htmlPattern.matcher(message).matches();
                if (isHTML) {
                    if (text.contains("<")) {
                        text = Html.fromHtml(Html.fromHtml(message).toString()).toString();
                    }
                } else {
                    text = vo.getMessage();
                    String a = Html.fromHtml(vo.getMessage()).toString();
                    if (a.contains("<")) {
                        text = Html.fromHtml(a).toString();
                    }
                }

                if (text.length() > 20) {
                    message = text.substring(0, 20) + " ...";
                } else {
                    message = text;
                }

                if (new Validations().getInstance(context).cekRoom(vo.getSource())) {
                    if (isJSONValid(vo.getMessage())) {
                        JSONObject jObject = null;
                        try {
                            jObject = new JSONObject(vo.getMessage());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (jObject != null) {
                            try {
                                String buka = jObject.getString("buka");
                                message = Html.fromHtml(URLDecoder.decode(buka)).toString();
                                if (message.contains("<")) {
                                    message = Html.fromHtml(Html.fromHtml(buka).toString()).toString();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

            }
        }
        return message;

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

    public static String getStatusMessage(Message vo, String myContact) {
        String type;
        if (!vo.getType().equals(Message.TYPE_TEXT)) {
            type = vo.getType();
        } else {
            if (vo.getDestination().equals(myContact)) {
                type = STATUS_TYPE_RECEIVE;
            } else {
                if (vo.getStatus() == Message.STATUS_DELIVERED) {
                    type = STATUS_TYPE_DELIVER;
                } else if (vo.getStatus() == Message.STATUS_NOTSEND) {
                    type = STATUS_TYPE_FAILED;
                } else if (vo.getStatus() == Message.STATUS_UNREAD) {
                    type = STATUS_TYPE_RECEIVE;
                } else if (vo.getStatus() == Message.STATUS_FAILED) {
                    type = STATUS_TYPE_FAILED;
                } else if (vo.getStatus() == Message.STATUS_SENT) {
                    type = STATUS_TYPE_SEND;
                } else if (vo.getStatus() == Message.STATUS_INPROGRESS) {
                    type = STATUS_TYPE_INPROSES;
                } else if (vo.getStatus() == Message.STATUS_READ) {
                    type = STATUS_TYPE_READ;
                } else {
                    type = STATUS_TYPE_RECEIVE;
                }
            }
        }
        return type;
    }

    public static CharSequence highlightText(String search, String originalText) {
        if (search != null) {
            if (search != null && !search.equalsIgnoreCase("")) {
                String normalizedText = Normalizer.normalize(originalText, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
                int start = normalizedText.indexOf(search);
                if (start < 0) {
                    return originalText;
                } else {
                    Spannable highlighted = new SpannableString(originalText);
                    while (start >= 0) {
                        int spanStart = Math.min(start, originalText.length());
                        int spanEnd = Math.min(start + search.length(), originalText.length());
                        highlighted.setSpan(new ForegroundColorSpan(Color.parseColor("#004a6d")), spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        start = normalizedText.indexOf(search, spanEnd);
                    }
                    return highlighted;
                }
            }
        }
        return originalText;
    }


}
