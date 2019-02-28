package com.byonchat.android.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.byonchat.android.FragmentDinamicRoom.ItemRoomDetail;
import com.byonchat.android.FragmentDinamicRoom.ModelFormChild;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class BotListDB extends SQLiteOpenHelper {
    public static final String BOT_ID = "id";
    public static final String BOT_NAME = "name";
    public static final String BOT_DESC = "desc";
    public static final String BOT_LINK = "link";
    public static final String BOT_REALNAME = "realname";
    public static final String ROOMS_ID = "id";
    public static final String ROOMS_NAME = "name";
    public static final String ROOMS_REALNAME = "realname";

    public static final String ROOM_ID = "id";
    public static final String ROOM_USERNAME = "username";
    public static final String ROOM_REALNAME = "realname";
    public static final String ROOM_CONTENT = "content";
    public static final String ROOM_COLOR = "color";
    public static final String ROOM_BACKDROP = "backdrop";
    public static final String ROOM_LASTUPDATE = "lastupdate";
    public static final String ROOM_ICON = "icon";
    public static final String ROOM_FIRST_TAB = "first_tab";
    public static final String ROOM_COLOR_TEXT = "color_text";

    public static final String ROOM_DETAIL_ID = "id";
    public static final String ROOM_DETAIL_ID_TAB = "idtab";
    public static final String ROOM_DETAIL_ID_ROOM = "idroom";
    public static final String ROOM_DETAIL_CONTENT = "content";
    public static final String ROOM_DETAIL_FLAG_CONTENT = "fcontent";
    public static final String ROOM_DETAIL_FLAG_TAB = "ftab";
    public static final String ROOM_DETAIL_FLAG_ROOM = "flagroom";
    private Context context;
    private static BotListDB instance;
    private SQLiteDatabase database;

    private static final String DATABASE_NAME = "BotList.db";
    private static final int DATABASE_VERSION = 7;//8

    private static final String BOT_TABLE = "BotList";
    private static final String ROOMNAME_TABLE = "RoomName";
    private static final String ROOM_TABLE = "RoomsParent";
    private static final String ROOM_DETAIl_TABLE = "RoomsDetail";

    private static final String CREATE_BOT_TABLE = "create table "
            + BOT_TABLE + " (" + BOT_ID
            + " integer primary key autoincrement, "
            + BOT_NAME + " text not null unique ," + BOT_DESC + " text," + BOT_LINK + " text," + BOT_REALNAME + " text" + ");";

    private static final String CREATE_ROOMNAME_TABLE = "create table "
            + ROOMNAME_TABLE + " (" + ROOMS_ID
            + " integer primary key autoincrement, "
            + ROOMS_NAME + " text not null unique," + ROOMS_REALNAME + " text);";

    private static final String CREATE_ROOM_TABLE = "create table "
            + ROOM_TABLE + " (" + ROOM_ID
            + " integer primary key autoincrement, "
            + ROOM_USERNAME + " text not null unique,"
            + ROOM_REALNAME + " text,"
            + ROOM_BACKDROP + " text,"
            + ROOM_CONTENT + " text,"
            + ROOM_COLOR + " text,"
            + ROOM_LASTUPDATE + " text,"
            + ROOM_FIRST_TAB + " text,"
            + ROOM_COLOR_TEXT + " text,"
            + ROOM_ICON + " text);";

    private static final String CREATE_ROOM_DETAIL_TABLE = "create table "
            + ROOM_DETAIl_TABLE + " (" + ROOM_ID
            + " text, "
            + ROOM_DETAIL_ID_TAB + " text,"
            + ROOM_DETAIL_ID_ROOM + " text,"
            + ROOM_DETAIL_CONTENT + " text,"
            + ROOM_DETAIL_FLAG_CONTENT + " text,"
            + ROOM_DETAIL_FLAG_TAB + " text,"
            + ROOM_DETAIL_FLAG_ROOM + " text);";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BOT_TABLE);
        db.execSQL(CREATE_ROOMNAME_TABLE);
        db.execSQL(CREATE_ROOM_TABLE);
        db.execSQL(CREATE_ROOM_DETAIL_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + BOT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ROOMNAME_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ROOM_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ROOM_DETAIl_TABLE);
        onCreate(db);
    }

    private BotListDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    private SQLiteDatabase getDatabase() {
        if (database == null) {
            database = getWritableDatabase();
        }
        return database;
    }

    public synchronized static BotListDB getInstance(
            Context context) {
        if (instance == null) {
            instance = new BotListDB(
                    context);
        }
        return instance;
    }

    public void insertScrDetails(ContactBot contactBot) {
        ContentValues cv = new ContentValues();
        cv.put(BOT_NAME, contactBot.getName());
        cv.put(BOT_DESC, contactBot.getDesc());
        cv.put(BOT_LINK, contactBot.getLink());
        cv.put(BOT_REALNAME, contactBot.getRealname());
        getDatabase().insert(BOT_TABLE, null, cv);
    }

    public void insertRooms(Rooms rooms) {
        ContentValues cv = new ContentValues();
        cv.put(ROOM_USERNAME, rooms.getUsername());
        cv.put(ROOM_REALNAME, rooms.getRealname());
        cv.put(ROOM_CONTENT, rooms.getContent());
        cv.put(ROOM_ICON, rooms.getIcon());
        cv.put(ROOM_BACKDROP, rooms.getBackdrop());
        cv.put(ROOM_COLOR, rooms.getColor());
        cv.put(ROOM_LASTUPDATE, rooms.getLastupdate());
        cv.put(ROOM_FIRST_TAB, rooms.getFirst());
        cv.put(ROOM_COLOR_TEXT, rooms.getColorText());
        getDatabase().insert(ROOM_TABLE, null, cv);
    }

    public void insertRoomsDetail(RoomsDetail roomsDetail) {
        ContentValues cv = new ContentValues();
        cv.put(ROOM_DETAIL_ID, roomsDetail.getId());
        cv.put(ROOM_DETAIL_ID_ROOM, roomsDetail.getParent_room());
        cv.put(ROOM_DETAIL_ID_TAB, roomsDetail.getParent_tab());
        cv.put(ROOM_DETAIL_CONTENT, roomsDetail.getContent());
        cv.put(ROOM_DETAIL_FLAG_CONTENT, roomsDetail.getFlag_content());
        cv.put(ROOM_DETAIL_FLAG_ROOM, roomsDetail.getFlag_room());
        cv.put(ROOM_DETAIL_FLAG_TAB, roomsDetail.getFlag_tab());
        getDatabase().insert(ROOM_DETAIl_TABLE, null, cv);
        if (roomsDetail.getFlag_room().equalsIgnoreCase("cild") && roomsDetail.getId().split("\\|").length < 2) {
            insertListTitleTask(roomsDetail, "insert");
        }
    }

    public boolean deleteDetailRoomById(String detail_id, String tab_id) {
        String where = ROOM_DETAIL_ID_ROOM + " = ? AND " + ROOM_DETAIL_ID_TAB + " = ? AND " + ROOM_DETAIL_FLAG_ROOM + " = ? ";
        String[] whereArgs = {detail_id, tab_id, "form"};

        boolean updateSuccessful = getDatabase().delete(ROOM_DETAIl_TABLE, where, whereArgs) > 0;

        return updateSuccessful;
    }


    public void insertListTitleTask(RoomsDetail roomsDetail, String action) {
        if (jsonResultType(roomsDetail.getFlag_content(), "c").equalsIgnoreCase("0") || jsonResultType(roomsDetail.getFlag_content(), "c").equalsIgnoreCase("1")) {
            String content = roomsDetail.getContent();
            if (jsonResultType(roomsDetail.getFlag_content(), "b").equalsIgnoreCase("rear_camera") || jsonResultType(roomsDetail.getFlag_content(), "b").equalsIgnoreCase("front_camera")) {
                Random random = new SecureRandom();
                char[] result = new char[6];
                char[] CHARSET_AZ_09 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
                for (int i = 0; i < result.length; i++) {
                    int randomCharIndex = random.nextInt(CHARSET_AZ_09.length);
                    result[i] = CHARSET_AZ_09[randomCharIndex];
                }
                content = "IMG_" + new String(result);
            } else if (jsonResultType(roomsDetail.getFlag_content(), "b").equalsIgnoreCase("map")) {
                String[] latlong = content.split(
                        Message.LOCATION_DELIMITER);
                if (latlong.length > 4) {
                    String text = "<u><b>" + (String) latlong[2] + "</b></u><br/>";
                    content = text + latlong[3];
                }
            } else if (jsonResultType(roomsDetail.getFlag_content(), "b").equalsIgnoreCase("dropdown_form")) {
                content = roomsDetail.getParent_tab() + roomsDetail.getFlag_content();
            } else if (jsonResultType(roomsDetail.getFlag_content(), "b").equalsIgnoreCase("form_child")) {
                content = "";
            } else if (jsonResultType(roomsDetail.getFlag_content(), "b").equalsIgnoreCase("input_kodepos")) {
                content = jsonResultType(content, "a");
            } else if (jsonResultType(roomsDetail.getFlag_content(), "b").equalsIgnoreCase("dropdown_wilayah")) {
                content = jsonResultType(content, "b") + " , " + jsonResultType(content, "c") + " , " + jsonResultType(content, "d") + " , " + jsonResultType(content, "e") + " , " + jsonResultType(content, "a");
            } else if (jsonResultType(roomsDetail.getFlag_content(), "b").equalsIgnoreCase("checkbox")) {
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
            } else if (jsonResultType(roomsDetail.getFlag_content(), "b").equalsIgnoreCase("image_load")) {
                content = "image load";
            } else if (jsonResultType(roomsDetail.getFlag_content(), "b").equalsIgnoreCase("ocr")) {
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
            } else if (jsonResultType(roomsDetail.getFlag_content(), "b").equalsIgnoreCase("dropdown_dinamis") || jsonResultType(roomsDetail.getFlag_content(), "b").equalsIgnoreCase("new_dropdown_dinamis")) {
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
            } else if (jsonResultType(roomsDetail.getFlag_content(), "b").equalsIgnoreCase("upload_document")) {
                content = jsonResultType(content, "a");
            } else if (jsonResultType(roomsDetail.getFlag_content(), "b").equalsIgnoreCase("signature")) {
                content = "signature";
            } else if (jsonResultType(roomsDetail.getFlag_content(), "b").equalsIgnoreCase("distance_estimation")) {
                content = jsonResultType(content, "d");
            } else if (jsonResultType(roomsDetail.getFlag_content(), "b").equalsIgnoreCase("rate")) {

            }

            RoomsDetail orderModel = new RoomsDetail(roomsDetail.getId(), roomsDetail.getParent_tab(), roomsDetail.getParent_room(), content, "1", roomsDetail.getFlag_tab(), "list");
            if (jsonResultType(roomsDetail.getFlag_content(), "c").equalsIgnoreCase("1")) {
                orderModel = new RoomsDetail(roomsDetail.getId(), roomsDetail.getParent_tab(), roomsDetail.getParent_room(), content, "2", roomsDetail.getFlag_tab(), "list");
            }
            if (action.equalsIgnoreCase("insert")) {
                insertRoomsDetail(orderModel);
            } else if (action.equalsIgnoreCase("update")) {
                updateDetailRoomWithFlagContent(orderModel);
            } else if (action.equalsIgnoreCase("delete")) {
                deleteDetailRoomWithFlagContent(orderModel);
            }
        }
    }

    public boolean deleteDetailRoomWithFlagContentNew(RoomsDetail roomsDetail) {
        String where = ROOM_DETAIL_ID + " = ? AND " + ROOM_DETAIL_ID_ROOM + " = ? AND " + ROOM_DETAIL_ID_TAB + " = ? AND " + ROOM_DETAIL_FLAG_ROOM + " = ? ";
        String[] whereArgs = {roomsDetail.getId(), roomsDetail.getParent_room(), roomsDetail.getParent_tab(), roomsDetail.getFlag_room()};

        boolean updateSuccessful = getDatabase().delete(ROOM_DETAIl_TABLE, where, whereArgs) > 0;

        return updateSuccessful;
    }


    public boolean updateDetailRoomWithFlagContentNew(RoomsDetail roomsDetail) {
        ContentValues cv = new ContentValues();
        cv.put(ROOM_DETAIL_ID_ROOM, roomsDetail.getParent_room());
        cv.put(ROOM_DETAIL_ID_TAB, roomsDetail.getParent_tab());
        cv.put(ROOM_DETAIL_CONTENT, roomsDetail.getContent());
        cv.put(ROOM_DETAIL_FLAG_CONTENT, roomsDetail.getFlag_content());
        cv.put(ROOM_DETAIL_FLAG_ROOM, roomsDetail.getFlag_room());
        cv.put(ROOM_DETAIL_FLAG_TAB, roomsDetail.getFlag_tab());

        String where = ROOM_DETAIL_ID + " = ? AND " + ROOM_DETAIL_ID_ROOM + " = ? AND " + ROOM_DETAIL_ID_TAB + " = ? AND " + ROOM_DETAIL_FLAG_ROOM + " = ? ";

        String[] whereArgs = {roomsDetail.getId(), roomsDetail.getParent_room(), roomsDetail.getParent_tab(), roomsDetail.getFlag_room()};

        boolean updateSuccessful = getDatabase().update(ROOM_DETAIl_TABLE, cv, where, whereArgs) > 0;

        return updateSuccessful;
    }


    public boolean updateDetailRoomWithFlagContent(RoomsDetail roomsDetail) {

        ContentValues cv = new ContentValues();
        cv.put(ROOM_DETAIL_ID_ROOM, roomsDetail.getParent_room());
        cv.put(ROOM_DETAIL_ID_TAB, roomsDetail.getParent_tab());
        cv.put(ROOM_DETAIL_CONTENT, roomsDetail.getContent());
        cv.put(ROOM_DETAIL_FLAG_CONTENT, roomsDetail.getFlag_content());
        cv.put(ROOM_DETAIL_FLAG_ROOM, roomsDetail.getFlag_room());
        cv.put(ROOM_DETAIL_FLAG_TAB, roomsDetail.getFlag_tab());

        String where = ROOM_DETAIL_ID + " = ? AND " + ROOM_DETAIL_FLAG_CONTENT + " = ? AND " + ROOM_DETAIL_ID_TAB + " = ? AND " + ROOM_DETAIL_ID_ROOM + " = ? ";
        String[] whereArgs = {roomsDetail.getId(), roomsDetail.getFlag_content(), roomsDetail.getParent_tab(), roomsDetail.getParent_room()};


        boolean updateSuccessful = getDatabase().update(ROOM_DETAIl_TABLE, cv, where, whereArgs) > 0;

        if (roomsDetail.getFlag_room().equalsIgnoreCase("cild") && roomsDetail.getId().split("\\|").length < 2) {
            insertListTitleTask(roomsDetail, "update");
        }
        return updateSuccessful;
    }

    public boolean updateDetailRoomWithFlagContentParent(RoomsDetail roomsDetail) {
        ContentValues cv = new ContentValues();
        if (roomsDetail.getParent_room() != null) {
            cv.put(ROOM_DETAIL_ID_ROOM, roomsDetail.getParent_room());
        }
        if (roomsDetail.getParent_tab() != null) {
            cv.put(ROOM_DETAIL_ID_TAB, roomsDetail.getParent_tab());
        }
        if (roomsDetail.getContent() != null) {
            cv.put(ROOM_DETAIL_CONTENT, roomsDetail.getContent());
        }
        if (roomsDetail.getFlag_content() != null) {
            cv.put(ROOM_DETAIL_FLAG_CONTENT, roomsDetail.getFlag_content());
        }
        if (roomsDetail.getFlag_room() != null) {
            cv.put(ROOM_DETAIL_FLAG_ROOM, roomsDetail.getFlag_room());
        }
        if (roomsDetail.getFlag_tab() != null) {
            cv.put(ROOM_DETAIL_FLAG_TAB, roomsDetail.getFlag_tab());
        }

        String where = ROOM_DETAIL_ID + " = ? AND " + ROOM_DETAIL_ID_TAB + " = ? AND " + ROOM_DETAIL_ID_ROOM + " = ? AND " + ROOM_DETAIL_FLAG_ROOM + " = ? ";
        String[] whereArgs = {roomsDetail.getId(), roomsDetail.getParent_tab(), roomsDetail.getParent_room(), roomsDetail.getFlag_room()};
        boolean updateSuccessful = getDatabase().update(ROOM_DETAIl_TABLE, cv, where, whereArgs) > 0;

        return updateSuccessful;
    }

    public boolean deleteDetailRoomWithFlagContent(RoomsDetail roomsDetail) {
        String where = ROOM_DETAIL_ID + " = ? AND " + ROOM_DETAIL_FLAG_CONTENT + " = ? AND " + ROOM_DETAIL_ID_TAB + " = ? AND " + ROOM_DETAIL_ID_ROOM + " = ? ";
        String[] whereArgs = {roomsDetail.getId(), roomsDetail.getFlag_content(), roomsDetail.getParent_tab(), roomsDetail.getParent_room()};
        boolean updateSuccessful = getDatabase().delete(ROOM_DETAIl_TABLE, where, whereArgs) > 0;
        if (roomsDetail.getFlag_room().equalsIgnoreCase("cild") && roomsDetail.getId().split("\\|").length < 2) {
            insertListTitleTask(roomsDetail, "delete");
        }
        return updateSuccessful;
    }

    public boolean delete(String jaberId) {
        return getDatabase().delete(BOT_TABLE, BOT_NAME + "= '" + jaberId + "'", null) > 0;
    }

    public boolean deletebyId(String Id) {
        return getDatabase().delete(BOT_TABLE, BOT_ID + "= " + Id, null) > 0;
    }

    public boolean delete() {
        return getDatabase().delete(BOT_TABLE, null, null) > 0;
    }

    // To get list of employee details
    public ArrayList<ContactBot> retriveallList() throws SQLException {
        ArrayList<ContactBot> botsList = new ArrayList<ContactBot>();
        Cursor cur = getDatabase().query(true, BOT_TABLE, new String[]{BOT_ID,
                BOT_NAME, BOT_DESC, BOT_LINK, BOT_REALNAME}, null, null, null, null, BOT_NAME, null);
        if (cur.moveToFirst()) {
            do {
                String realname = cur.getString(cur.getColumnIndex(BOT_REALNAME));
                String name = cur.getString(cur.getColumnIndex(BOT_NAME));
                String desc = cur.getString(cur.getColumnIndex(BOT_DESC));
                String link = cur.getString(cur.getColumnIndex(BOT_LINK));
                String id = cur.getString(cur.getColumnIndex(BOT_ID));
                botsList.add(new ContactBot(id, name, desc, realname, link));
            } while (cur.moveToNext());
        }
        return botsList;
    }

    // To get list of employee details
    public ArrayList<Rooms> getListRooms() throws SQLException {
        ArrayList<Rooms> roomsArrayList = new ArrayList<Rooms>();
        Cursor cur = getDatabase().query(true, ROOM_TABLE, new String[]{ROOM_ID,
                ROOM_USERNAME, ROOM_REALNAME, ROOM_CONTENT, ROOM_ICON, ROOM_COLOR, ROOM_BACKDROP, ROOM_LASTUPDATE, ROOM_FIRST_TAB, ROOM_COLOR_TEXT}, null, null, null, null, null, null);
        if (cur.moveToFirst()) {
            do {
                String id = cur.getString(cur.getColumnIndex(ROOM_ID));
                String usr = cur.getString(cur.getColumnIndex(ROOM_USERNAME));
                String real = cur.getString(cur.getColumnIndex(ROOM_REALNAME));
                String cntn = cur.getString(cur.getColumnIndex(ROOM_CONTENT));
                String icon = cur.getString(cur.getColumnIndex(ROOM_ICON));
                String color = cur.getString(cur.getColumnIndex(ROOM_COLOR));
                String back = cur.getString(cur.getColumnIndex(ROOM_BACKDROP));
                String update = cur.getString(cur.getColumnIndex(ROOM_LASTUPDATE));
                String ftab = cur.getString(cur.getColumnIndex(ROOM_FIRST_TAB));
                String ctex = cur.getString(cur.getColumnIndex(ROOM_COLOR_TEXT));
                roomsArrayList.add(new Rooms(id, usr, real, cntn, color, back, update, icon, ftab, ctex));
            } while (cur.moveToNext());
        }
        return roomsArrayList;
    }

    // To get list of employee details
    public ArrayList<RoomsDetail> getListRoomsDetail() throws SQLException {
        ArrayList<RoomsDetail> roomsArrayList = new ArrayList<RoomsDetail>();
        Cursor cur = getDatabase().query(true, ROOM_DETAIl_TABLE, new String[]{
                ROOM_DETAIL_ID, ROOM_DETAIL_ID_TAB,
                ROOM_DETAIL_ID_ROOM, ROOM_DETAIL_CONTENT,
                ROOM_DETAIL_FLAG_CONTENT, ROOM_DETAIL_FLAG_TAB,
                ROOM_DETAIL_FLAG_ROOM
        }, null, null, null, null, null, null);
        if (cur.moveToFirst()) {
            do {
                String id = cur.getString(cur.getColumnIndex(ROOM_DETAIL_ID));
                String idTab = cur.getString(cur.getColumnIndex(ROOM_DETAIL_ID_TAB));
                String idRoom = cur.getString(cur.getColumnIndex(ROOM_DETAIL_ID_ROOM));
                String cntn = cur.getString(cur.getColumnIndex(ROOM_DETAIL_CONTENT));
                String flagContent = cur.getString(cur.getColumnIndex(ROOM_DETAIL_FLAG_CONTENT));
                String flagTab = cur.getString(cur.getColumnIndex(ROOM_DETAIL_FLAG_TAB));
                String flagRoom = cur.getString(cur.getColumnIndex(ROOM_DETAIL_FLAG_ROOM));
                roomsArrayList.add(new RoomsDetail(id, idTab, idRoom, cntn, flagContent, flagTab, flagRoom));
            } while (cur.moveToNext());
        }
        return roomsArrayList;
    }

    public ArrayList<ItemRoomDetail> getListRoomsDetailPOS(String status, String roomId) throws SQLException {
        ArrayList<ItemRoomDetail> roomsArrayList = new ArrayList<ItemRoomDetail>();
        Cursor cur = getDatabase().query(true, ROOM_DETAIl_TABLE, new String[]{
                ROOM_DETAIL_ID, ROOM_DETAIL_ID_TAB,
                ROOM_DETAIL_ID_ROOM, ROOM_DETAIL_CONTENT,
                ROOM_DETAIL_FLAG_CONTENT, ROOM_DETAIL_FLAG_TAB,
                ROOM_DETAIL_FLAG_ROOM
        }, ROOM_DETAIL_FLAG_ROOM + "= '" + status + "'" + " AND " + ROOM_DETAIL_ID_ROOM + "= '" + roomId + "'", null, null, null, null, null);
        if (cur.moveToFirst()) {
            do {
                String id = cur.getString(cur.getColumnIndex(ROOM_DETAIL_ID));
                String cntn = cur.getString(cur.getColumnIndex(ROOM_DETAIL_CONTENT));
                roomsArrayList.add(new ItemRoomDetail(Integer.valueOf(id), jsonToName(cntn), jsonToPrice(cntn), Integer.valueOf(jsonToQty(cntn)), jsonToSKU(cntn)));
            } while (cur.moveToNext());
        }
        return roomsArrayList;
    }

    public ArrayList<ItemRoomDetail> getListRoomsDetailPOSSKU(String roomId) throws SQLException {
        ArrayList<ItemRoomDetail> roomsArrayList = new ArrayList<ItemRoomDetail>();
        Cursor cur = getDatabase().query(true, ROOM_DETAIl_TABLE, new String[]{
                ROOM_DETAIL_ID, ROOM_DETAIL_ID_TAB,
                ROOM_DETAIL_ID_ROOM, ROOM_DETAIL_CONTENT,
                ROOM_DETAIL_FLAG_CONTENT, ROOM_DETAIL_FLAG_TAB,
                ROOM_DETAIL_FLAG_ROOM
        }, ROOM_DETAIL_ID_ROOM + "= '" + roomId + "'", null, null, null, null, null);
        if (cur.moveToFirst()) {
            do {
                String id = cur.getString(cur.getColumnIndex(ROOM_DETAIL_ID));
                String cntn = cur.getString(cur.getColumnIndex(ROOM_DETAIL_CONTENT));
                roomsArrayList.add(new ItemRoomDetail(Integer.valueOf(id), jsonToName(cntn), jsonToPrice(cntn), Integer.valueOf(jsonToQty(cntn)), jsonToSKU(cntn)));
            } while (cur.moveToNext());
        }
        return roomsArrayList;
    }

    public String jsonToQty(String object) {
        String jumlah = "";
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(object);
            jumlah = jObject.getString("qty");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jumlah;
    }

    public String jsonToName(String object) {
        String name = "";
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(object);
            name = jObject.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return name;
    }

    public String jsonToSKU(String object) {
        String name = "";
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(object);
            name = jObject.getString("sku");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return name;
    }

    public String jsonToPrice(String object) {
        String price = "";
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(object);
            price = jObject.getString("price");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return price;
    }

    public Cursor getSingle(String jaberId) {
        Cursor cursor = getDatabase().query(BOT_TABLE, new String[]
                {
                        BOT_NAME, BOT_DESC, BOT_LINK, BOT_REALNAME
                }, BOT_NAME + "= '" + jaberId + "'", null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }

    public Cursor getSingleRoom(String usrname) {
        Cursor cursor = getDatabase().query(ROOM_TABLE, new String[]
                {
                        ROOM_ID, ROOM_USERNAME, ROOM_REALNAME, ROOM_CONTENT, ROOM_ICON, ROOM_COLOR, ROOM_BACKDROP, ROOM_LASTUPDATE, ROOM_FIRST_TAB, ROOM_COLOR_TEXT
                }, ROOM_USERNAME + "= '" + usrname + "'", null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }

    public Cursor getSingleRoomDetail(String id) {
        Cursor cursor = getDatabase().query(ROOM_DETAIl_TABLE, new String[]
                {
                        ROOM_DETAIL_ID, ROOM_DETAIL_ID_TAB, ROOM_DETAIL_ID_ROOM, ROOM_DETAIL_CONTENT, ROOM_DETAIL_FLAG_CONTENT, ROOM_DETAIL_FLAG_TAB, ROOM_DETAIL_FLAG_ROOM
                }, ROOM_DETAIL_ID + "= '" + id + "'", null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }

    public Cursor getSingleRoomDetailStatus(String id, String roomId, String status) {
        Cursor cursor = getDatabase().query(ROOM_DETAIl_TABLE, new String[]
                {
                        ROOM_DETAIL_ID, ROOM_DETAIL_ID_TAB, ROOM_DETAIL_ID_ROOM, ROOM_DETAIL_CONTENT, ROOM_DETAIL_FLAG_CONTENT, ROOM_DETAIL_FLAG_TAB, ROOM_DETAIL_FLAG_ROOM
                }, ROOM_DETAIL_ID + "= '" + id + "' AND " + ROOM_DETAIL_FLAG_ROOM + "= '" + status + "'" + " AND " + ROOM_DETAIL_ID_ROOM + "= '" + roomId + "'", null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }

    public Cursor getSingleRoomDetailStatusSKU(String id) {
        Cursor cursor = getDatabase().query(ROOM_DETAIl_TABLE, new String[]
                {
                        ROOM_DETAIL_ID, ROOM_DETAIL_ID_TAB, ROOM_DETAIL_ID_ROOM, ROOM_DETAIL_CONTENT, ROOM_DETAIL_FLAG_CONTENT, ROOM_DETAIL_FLAG_TAB, ROOM_DETAIL_FLAG_ROOM
                }, ROOM_DETAIL_FLAG_ROOM + "= '" + id + "'", null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }


    public Cursor getSingleRoomDetailForm(String username, String tab_id) {
        Log.w("ggggg", "getSingleRoomDetailForm " + username + " -- " + tab_id);
        Cursor cursor = getDatabase().query(ROOM_DETAIl_TABLE, new String[]
                {
                        ROOM_DETAIL_ID, ROOM_DETAIL_ID_TAB, ROOM_DETAIL_ID_ROOM, ROOM_DETAIL_CONTENT, ROOM_DETAIL_FLAG_CONTENT, ROOM_DETAIL_FLAG_TAB, ROOM_DETAIL_FLAG_ROOM
                }, ROOM_DETAIL_ID_ROOM + "= '" + username + "' AND " + ROOM_DETAIL_FLAG_ROOM + "= 'form'" + " AND " + ROOM_DETAIL_ID_TAB + "= '" + tab_id + "'", null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }

    public Cursor getSingleRoomDetailFormIsi(String username, String tab_id) {
        Cursor cursor = getDatabase().query(ROOM_DETAIl_TABLE, new String[]
                {
                        ROOM_DETAIL_ID, ROOM_DETAIL_ID_TAB, ROOM_DETAIL_ID_ROOM, ROOM_DETAIL_CONTENT, ROOM_DETAIL_FLAG_CONTENT, ROOM_DETAIL_FLAG_TAB, ROOM_DETAIL_FLAG_ROOM
                }, ROOM_DETAIL_ID_ROOM + "= '" + username + "' AND " + ROOM_DETAIL_FLAG_ROOM + "= 'form_isi'" + " AND " + ROOM_DETAIL_ID_TAB + "= '" + tab_id + "'", null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }


    public Cursor getSingleRoomDetailFormWithFlag(String id, String usernameRoom, String tab_id, String Status) {
        Cursor cursor = getDatabase().query(ROOM_DETAIl_TABLE, new String[]
                {
                        ROOM_DETAIL_ID, ROOM_DETAIL_ID_TAB, ROOM_DETAIL_ID_ROOM, ROOM_DETAIL_CONTENT, ROOM_DETAIL_FLAG_CONTENT, ROOM_DETAIL_FLAG_TAB, ROOM_DETAIL_FLAG_ROOM
                }, ROOM_DETAIL_ID + "= '" + id + "' AND " + ROOM_DETAIL_ID_ROOM + "='" + usernameRoom + "' AND " + ROOM_DETAIL_ID_TAB + "= '" + tab_id + "'" + " AND " + ROOM_DETAIL_FLAG_ROOM + "= '" + Status + "'", null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }

    // To get list of employee details
    public ArrayList<RoomsDetail> allRoomDetailFormWithFlag(String id, String usernameRoom, String tab_id, String Status) throws SQLException {
        ArrayList<RoomsDetail> list = new ArrayList<RoomsDetail>();
        ArrayList<String> ll = new ArrayList<String>();
        String where = ROOM_DETAIL_ID_ROOM + "='" + usernameRoom + "' AND " + ROOM_DETAIL_ID_TAB + "= '" + tab_id + "'" + " AND " + ROOM_DETAIL_FLAG_ROOM + "= '" + Status + "'";
        if (Status.equalsIgnoreCase("cild") || Status.equalsIgnoreCase("list")) {
            where = ROOM_DETAIL_ID + "= '" + id + "' AND " + ROOM_DETAIL_ID_ROOM + "='" + usernameRoom + "' AND " + ROOM_DETAIL_ID_TAB + "= '" + tab_id + "'" + " AND " + ROOM_DETAIL_FLAG_ROOM + "= '" + Status + "'";
        } else if (Status.equalsIgnoreCase("title")) {
            Cursor cursor = getSingleRoomDetailForm(usernameRoom, tab_id);
            if (cursor.getCount() > 0) {
                String content = cursor.getString(cursor.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT));
                JSONArray jsonArray = null;

                try {
                    jsonArray = new JSONArray(content);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        if (i < 2) {
                            final String idListTask = jsonArray.getJSONObject(i).getString("id_list_task").toString();
                            ll.add(idListTask);
                        } else {
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                where = ROOM_DETAIL_ID + "= '" + id + "' AND " + ROOM_DETAIL_ID_ROOM + "='" + usernameRoom + "' AND " + ROOM_DETAIL_ID_TAB + "= '" + tab_id + "'" + " AND " + ROOM_DETAIL_FLAG_ROOM + "= 'cild' "/*AND "+ROOM_DETAIL_FLAG_CONTENT+aa*/;
            } else {
                return list;
            }
        }

        Cursor cursor;
        if (Status.equalsIgnoreCase("title")) {
            cursor = getDatabase().query(ROOM_DETAIl_TABLE, new String[]
                    {
                            ROOM_DETAIL_ID, ROOM_DETAIL_ID_TAB, ROOM_DETAIL_ID_ROOM, ROOM_DETAIL_CONTENT, ROOM_DETAIL_FLAG_CONTENT, ROOM_DETAIL_FLAG_TAB, ROOM_DETAIL_FLAG_ROOM
                    }, where, null, null, null, null, "2");
        } else {
            cursor = getDatabase().query(ROOM_DETAIl_TABLE, new String[]
                    {
                            ROOM_DETAIL_ID, ROOM_DETAIL_ID_TAB, ROOM_DETAIL_ID_ROOM, ROOM_DETAIL_CONTENT, ROOM_DETAIL_FLAG_CONTENT, ROOM_DETAIL_FLAG_TAB, ROOM_DETAIL_FLAG_ROOM
                    }, where, null, null, null, null);
        }

        if (cursor.moveToFirst()) {
            do {
                String a = cursor.getString(cursor.getColumnIndex(ROOM_DETAIL_ID));
                String b = cursor.getString(cursor.getColumnIndex(ROOM_DETAIL_ID_TAB));
                String c = cursor.getString(cursor.getColumnIndex(ROOM_DETAIL_ID_ROOM));
                String d = cursor.getString(cursor.getColumnIndex(ROOM_DETAIL_CONTENT));
                String e = cursor.getString(cursor.getColumnIndex(ROOM_DETAIL_FLAG_CONTENT));
                String f = cursor.getString(cursor.getColumnIndex(ROOM_DETAIL_FLAG_TAB));
                String g = cursor.getString(cursor.getColumnIndex(ROOM_DETAIL_FLAG_ROOM));

                if (Status.equalsIgnoreCase("title")) {
                    for (String aa : ll) {
                        if (aa.equalsIgnoreCase(jsonResultType(e, "a"))) {
                            list.add(new RoomsDetail(a, b, c, d, jsonResultType(e, "a"), f, g));
                        }
                    }
                } else {
                    list.add(new RoomsDetail(a, b, c, d, e, f, g));
                }
            } while (cursor.moveToNext());
        }
        return list;
    }

    // To get list of employee details
    public ArrayList<RoomsDetail> allRoomDetailFormWithFlagChild(String id, String usernameRoom, String tab_id, String Status) throws SQLException {
        ArrayList<RoomsDetail> list = new ArrayList<RoomsDetail>();
        ArrayList<String> ll = new ArrayList<String>();
        String where = ROOM_DETAIL_ID + "= '" + id + "' AND " + ROOM_DETAIL_ID_ROOM + "='" + usernameRoom + "' AND " + ROOM_DETAIL_ID_TAB + "= '" + tab_id + "'" + " AND " + ROOM_DETAIL_FLAG_ROOM + "= '" + Status + "'";

        Cursor cursor = getDatabase().query(ROOM_DETAIl_TABLE, new String[]
                {
                        ROOM_DETAIL_ID, ROOM_DETAIL_ID_TAB, ROOM_DETAIL_ID_ROOM, ROOM_DETAIL_CONTENT, ROOM_DETAIL_FLAG_CONTENT, ROOM_DETAIL_FLAG_TAB, ROOM_DETAIL_FLAG_ROOM
                }, where, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                String a = cursor.getString(cursor.getColumnIndex(ROOM_DETAIL_ID));
                String b = cursor.getString(cursor.getColumnIndex(ROOM_DETAIL_ID_TAB));
                String c = cursor.getString(cursor.getColumnIndex(ROOM_DETAIL_ID_ROOM));
                String d = cursor.getString(cursor.getColumnIndex(ROOM_DETAIL_CONTENT));
                String e = cursor.getString(cursor.getColumnIndex(ROOM_DETAIL_FLAG_CONTENT));
                String f = cursor.getString(cursor.getColumnIndex(ROOM_DETAIL_FLAG_TAB));
                String g = cursor.getString(cursor.getColumnIndex(ROOM_DETAIL_FLAG_ROOM));

                if (Status.equalsIgnoreCase("title")) {
                    for (String aa : ll) {
                        if (aa.equalsIgnoreCase(jsonResultType(e, "a"))) {
                            list.add(new RoomsDetail(a, b, c, d, jsonResultType(e, "a"), f, g));
                        }
                    }
                } else {
                    list.add(new RoomsDetail(a, b, c, d, e, f, g));
                }
            } while (cursor.moveToNext());
        }
        return list;
    }


    public ArrayList<RoomsDetail> allRoomDetailFormWithFlagqew(String id, String usernameRoom, String tab_id, String Status) throws SQLException {
        ArrayList<RoomsDetail> list = new ArrayList<RoomsDetail>();
        ArrayList<String> ll = new ArrayList<String>();
        String where = ROOM_DETAIL_ID_ROOM + "='" + usernameRoom + "' AND " + ROOM_DETAIL_ID_TAB + "= '" + tab_id + "'" + " AND " + ROOM_DETAIL_FLAG_ROOM + "= '" + Status + "'";
        if (Status.equalsIgnoreCase("cild")) {
            where = ROOM_DETAIL_ID + "= '" + id + "' AND " + ROOM_DETAIL_ID_ROOM + "='" + usernameRoom + "' AND " + ROOM_DETAIL_ID_TAB + "= '" + tab_id + "'" + " AND " + ROOM_DETAIL_FLAG_ROOM + "= '" + Status + "'";
        } else if (Status.equalsIgnoreCase("title")) {
            Cursor cursor = getSingleRoomDetailForm(usernameRoom, tab_id);
            if (cursor.getCount() > 0) {
                String content = cursor.getString(cursor.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT));
                JSONArray jsonArray = null;

                try {
                    jsonArray = new JSONArray(content);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        if (i < 2) {
                            final String idListTask = jsonArray.getJSONObject(i).getString("id_list_task").toString();
                            ll.add(idListTask);
                        } else {
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                where = ROOM_DETAIL_ID + "= '" + id + "' AND " + ROOM_DETAIL_ID_ROOM + "='" + usernameRoom + "' AND " + ROOM_DETAIL_ID_TAB + "= '" + tab_id + "'" + " AND " + ROOM_DETAIL_FLAG_ROOM + "= 'cild' "/*AND "+ROOM_DETAIL_FLAG_CONTENT+aa*/;
            } else {
                return list;
            }
        }
        Cursor cursor;
        if (Status.equalsIgnoreCase("title")) {
            cursor = getDatabase().query(ROOM_DETAIl_TABLE, new String[]
                    {
                            ROOM_DETAIL_ID, ROOM_DETAIL_ID_TAB, ROOM_DETAIL_ID_ROOM, ROOM_DETAIL_CONTENT, ROOM_DETAIL_FLAG_CONTENT, ROOM_DETAIL_FLAG_TAB, ROOM_DETAIL_FLAG_ROOM
                    }, where, null, null, null, null, "2");
        } else {
            cursor = getDatabase().query(ROOM_DETAIl_TABLE, new String[]
                    {
                            ROOM_DETAIL_ID, ROOM_DETAIL_ID_TAB, ROOM_DETAIL_ID_ROOM, ROOM_DETAIL_CONTENT, ROOM_DETAIL_FLAG_CONTENT, ROOM_DETAIL_FLAG_TAB, ROOM_DETAIL_FLAG_ROOM
                    }, where, null, null, null, null);
        }

        if (cursor.moveToFirst()) {
            do {
                String a = cursor.getString(cursor.getColumnIndex(ROOM_DETAIL_ID));
                String b = cursor.getString(cursor.getColumnIndex(ROOM_DETAIL_ID_TAB));
                String c = cursor.getString(cursor.getColumnIndex(ROOM_DETAIL_ID_ROOM));
                String d = cursor.getString(cursor.getColumnIndex(ROOM_DETAIL_CONTENT));
                String e = cursor.getString(cursor.getColumnIndex(ROOM_DETAIL_FLAG_CONTENT));
                String f = cursor.getString(cursor.getColumnIndex(ROOM_DETAIL_FLAG_TAB));
                String g = cursor.getString(cursor.getColumnIndex(ROOM_DETAIL_FLAG_ROOM));

                if (Status.equalsIgnoreCase("title")) {
                    for (String aa : ll) {
                        if (aa.equalsIgnoreCase(jsonResultType(e, "a"))) {
                            list.add(new RoomsDetail(a, b, c, d, jsonResultType(e, "a"), f, g));
                        }
                    }
                } else {
                    list.add(new RoomsDetail(a, b, c, d, e, f, g));
                }
            } while (cursor.moveToNext());
        }
        return list;
    }

    public String jsonResultType(String json, String type) {
        String hasil = "";
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
            hasil = "";
        }
        if (jObject != null) {
            try {
                hasil = jObject.getString(type);
            } catch (JSONException e) {
                e.printStackTrace();
                hasil = "";
            }
        }

        return hasil;
    }

    public Cursor getSingleRoomDetailFormWithFlagContent(String id, String usernameRoom, String tab_id, String Status, String fCo) {
        Cursor cursor = getDatabase().query(ROOM_DETAIl_TABLE, new String[]
                {
                        ROOM_DETAIL_ID, ROOM_DETAIL_ID_TAB, ROOM_DETAIL_ID_ROOM, ROOM_DETAIL_CONTENT, ROOM_DETAIL_FLAG_CONTENT, ROOM_DETAIL_FLAG_TAB, ROOM_DETAIL_FLAG_ROOM
                }, ROOM_DETAIL_ID + "= '" + id + "' AND " + ROOM_DETAIL_ID_ROOM + "='" + usernameRoom + "' AND " + ROOM_DETAIL_ID_TAB + "= '" + tab_id + "'" + " AND " + ROOM_DETAIL_FLAG_ROOM + "= '" + Status + "'" + " AND " + ROOM_DETAIL_FLAG_CONTENT + "= '" + fCo + "'", null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }

    public Cursor getSingleRoomDetailFormWithFlagContentChild(String id, String idUsrNTb, String idDtlTskOld, String Status, String fCo) {
        Log.w("jabwa", id + "<==>" + idUsrNTb + "<==>" + idDtlTskOld + "<==>" + Status + "<==>" + fCo);
        Cursor cursor = getDatabase().query(ROOM_DETAIl_TABLE, new String[]
                        {
                                ROOM_DETAIL_ID, ROOM_DETAIL_ID_TAB, ROOM_DETAIL_ID_ROOM, ROOM_DETAIL_CONTENT, ROOM_DETAIL_FLAG_CONTENT, ROOM_DETAIL_FLAG_TAB, ROOM_DETAIL_FLAG_ROOM
                        }, ROOM_DETAIL_ID + "= '" + id + "' AND " + ROOM_DETAIL_ID_ROOM + "='" + idDtlTskOld + "' AND " + ROOM_DETAIL_ID_TAB + "= '" + idUsrNTb + "'" + " AND " + ROOM_DETAIL_FLAG_ROOM + "= '" + Status + "'" + " AND " + ROOM_DETAIL_FLAG_CONTENT + "= '" + fCo + "'"
                , null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }


    // To get list of employee details
    public ArrayList<RoomsDetail> getAllRoomDetailFormWithFlagContent(String id, String idUsrNTb, String idDtlTskOld, String Status) throws SQLException {
        ArrayList<RoomsDetail> list = new ArrayList<RoomsDetail>();
        Cursor cursor = getDatabase().query(true, ROOM_DETAIl_TABLE, new String[]{ROOM_DETAIL_ID, ROOM_DETAIL_ID_TAB, ROOM_DETAIL_ID_ROOM, ROOM_DETAIL_CONTENT, ROOM_DETAIL_FLAG_CONTENT, ROOM_DETAIL_FLAG_TAB, ROOM_DETAIL_FLAG_ROOM
                }, ROOM_DETAIL_ID + "= '" + id + "' AND " + ROOM_DETAIL_ID_ROOM + "='" + idDtlTskOld + "' AND " + ROOM_DETAIL_ID_TAB + "= '" + idUsrNTb + "'" + " AND " + ROOM_DETAIL_FLAG_ROOM + "= '" + Status + "'"
                , null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {

                String a = cursor.getString(cursor.getColumnIndex(ROOM_DETAIL_ID));
                String b = cursor.getString(cursor.getColumnIndex(ROOM_DETAIL_ID_TAB));
                String c = cursor.getString(cursor.getColumnIndex(ROOM_DETAIL_ID_ROOM));
                String d = cursor.getString(cursor.getColumnIndex(ROOM_DETAIL_CONTENT));
                String e = cursor.getString(cursor.getColumnIndex(ROOM_DETAIL_FLAG_CONTENT));
                String f = cursor.getString(cursor.getColumnIndex(ROOM_DETAIL_FLAG_TAB));
                String g = cursor.getString(cursor.getColumnIndex(ROOM_DETAIL_FLAG_ROOM));

                list.add(new RoomsDetail(a, b, c, d, e, f, g));
            } while (cursor.moveToNext());
        }
        return list;
    }

    public ArrayList<String> getAllRoomDetailFormWithFlagContentWithOutId(String idUsrNTb, String idDtlTskOld, String Status) throws SQLException {
        ArrayList<String> list = new ArrayList<String>();
        Cursor cursor = getDatabase().query(true, ROOM_DETAIl_TABLE, new String[]{ROOM_DETAIL_ID/*,ROOM_DETAIL_ID_TAB,ROOM_DETAIL_ID_ROOM,ROOM_DETAIL_CONTENT,ROOM_DETAIL_FLAG_CONTENT,ROOM_DETAIL_FLAG_TAB,ROOM_DETAIL_FLAG_ROOM*/
                }, ROOM_DETAIL_ID_ROOM + "='" + idDtlTskOld + "' AND " + ROOM_DETAIL_ID_TAB + "= '" + idUsrNTb + "'" + " AND " + ROOM_DETAIL_FLAG_ROOM + "= '" + Status + "'"
                , null, ROOM_DETAIL_ID, null, null, null);
        if (cursor.moveToFirst()) {
            Log.w("ada", "kabar");
            do {

                String a = cursor.getString(cursor.getColumnIndex(ROOM_DETAIL_ID));
                list.add(a);
            } while (cursor.moveToNext());
        }


        return list;
    }

    public boolean removeRoomDetailFormWithFlagContentWithOutId(String idUsrNTb, String idDtlTskOld, String Status) {
        return getDatabase().delete(ROOM_DETAIl_TABLE, ROOM_DETAIL_ID_ROOM + "='" + idDtlTskOld + "' AND " + ROOM_DETAIL_ID_TAB + "= '" + idUsrNTb + "'" + " AND " + ROOM_DETAIL_FLAG_ROOM + "= '" + Status + "'", null) > 0;
    }


    // To get list of employee details
    public ArrayList<ModelFormChild> getAllChildFormChild(String id, String usernameRoom, String tab_id, String Status, String fCo) throws SQLException {
        ArrayList<ModelFormChild> list = new ArrayList<ModelFormChild>();
        Cursor cur = getDatabase().query(true, ROOM_DETAIl_TABLE, new String[]{ROOM_DETAIL_ID, ROOM_DETAIL_ID_TAB, ROOM_DETAIL_ID_ROOM, ROOM_DETAIL_CONTENT, ROOM_DETAIL_FLAG_CONTENT, ROOM_DETAIL_FLAG_TAB, ROOM_DETAIL_FLAG_ROOM
        }, ROOM_DETAIL_ID + "= '" + id + "' AND " + ROOM_DETAIL_ID_ROOM + "='" + usernameRoom + "' AND " + ROOM_DETAIL_ID_TAB + "= '" + tab_id + "'" + " AND " + ROOM_DETAIL_FLAG_ROOM + "= '" + Status + "'" + " AND " + ROOM_DETAIL_FLAG_CONTENT + "= '" + fCo + "'", null, null, null, null, null);
        if (cur.moveToFirst()) {
            do {

                String idd = jsonResultType(cur.getString(cur.getColumnIndex(ROOM_DETAIL_CONTENT)), "a");
                String name = jsonResultType(cur.getString(cur.getColumnIndex(ROOM_DETAIL_CONTENT)), "b");
                String desc = jsonResultType(cur.getString(cur.getColumnIndex(ROOM_DETAIL_CONTENT)), "c");
                String price = jsonResultType(cur.getString(cur.getColumnIndex(ROOM_DETAIL_CONTENT)), "d");

                list.add(new ModelFormChild(idd, name, desc, price));
            } while (cur.moveToNext());
        }
        return list;
    }


    public Cursor getSingleById(String id) {
        Cursor cursor = getDatabase().query(BOT_TABLE, new String[]
                {
                        BOT_NAME, BOT_DESC, BOT_LINK, BOT_REALNAME
                }, BOT_ID + "= " + id + "", null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }

    public void insertRoomsName(RealNameRoom realNameRoom) {
        ContentValues cv = new ContentValues();
        cv.put(ROOMS_NAME, realNameRoom.getName());
        cv.put(ROOMS_REALNAME, realNameRoom.getRealName());
        getDatabase().insert(ROOMNAME_TABLE, null, cv);
    }

    // To get list of employee details
    public ArrayList<RealNameRoom> allRealNameRooms() throws SQLException {
        ArrayList<RealNameRoom> list = new ArrayList<RealNameRoom>();
        Cursor cur = getDatabase().query(true, ROOMNAME_TABLE, new String[]{ROOMS_ID,
                ROOMS_NAME, ROOMS_REALNAME}, null, null, null, null, ROOMS_NAME, null);
        if (cur.moveToFirst()) {
            do {
                String id = cur.getString(cur.getColumnIndex(ROOMS_ID));
                String name = cur.getString(cur.getColumnIndex(ROOMS_NAME));
                String desc = cur.getString(cur.getColumnIndex(ROOMS_REALNAME));
                list.add(new RealNameRoom(id, name, desc));
            } while (cur.moveToNext());
        }
        return list;
    }

    public Cursor getRealNameByName(String name) {
        Cursor cursor = getDatabase().query(ROOMNAME_TABLE, new String[]
                {
                        ROOMS_NAME, ROOMS_REALNAME
                }, ROOMS_NAME + " like '" + name + "'", null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }

    public boolean deleteRoomsName(String name) {
        return getDatabase().delete(ROOMNAME_TABLE, ROOMS_NAME + "= '" + name + "'", null) > 0;
    }

    public boolean deleteRoomsDetailbyTAB(String idTab) {
        return getDatabase().delete(ROOM_DETAIl_TABLE, ROOM_DETAIL_ID_TAB + "= '" + idTab + "'", null) > 0;
    }

    public boolean deleteRoomsbyTAB(String username) {
        return getDatabase().delete(ROOM_TABLE, ROOM_USERNAME + "= '" + username + "'", null) > 0;
    }

    public boolean deleteRoomsDetailByItem(String id, String roomId, String status) {
        return getDatabase().delete(ROOM_DETAIl_TABLE, ROOM_DETAIL_ID + "= '" + id + "' AND " + ROOM_DETAIL_FLAG_ROOM + "= '" + status + "'" + " AND " + ROOM_DETAIL_ID_ROOM + "= '" + roomId + "'", null) > 0;
    }

    public boolean deleteRoomsDetailByItemSKU(String id) {
        return getDatabase().delete(ROOM_DETAIl_TABLE, ROOM_DETAIL_FLAG_ROOM + "= '" + id + "'", null) > 0;
    }

    public boolean deleteRoomsDetailAllItem(String roomId, String status) {
        return getDatabase().delete(ROOM_DETAIl_TABLE, ROOM_DETAIL_FLAG_ROOM + "= '" + status + "'" + " AND " + ROOM_DETAIL_ID_ROOM + "= '" + roomId + "'", null) > 0;
    }

    public boolean deleteRoomsDetailAllItemSku(String roomId) {
        return getDatabase().delete(ROOM_DETAIl_TABLE, ROOM_DETAIL_ID_ROOM + "= '" + roomId + "'", null) > 0;
    }

    public boolean deleteRoomsDetailPtabPRoom(String tab, String room) {
        return getDatabase().delete(ROOM_DETAIl_TABLE, ROOM_DETAIL_ID_TAB + "= '" + tab + "'" + " AND " + ROOM_DETAIL_ID_ROOM + "= '" + room + "'", null) > 0;
    }

    public boolean deleteRoomsDetailPtabPRoomNotValue(String tab, String room, String from) {
        Boolean delete = false;
        if (from.equalsIgnoreCase("show")) {
            delete = getDatabase().delete(ROOM_DETAIl_TABLE, ROOM_DETAIL_ID_TAB + "= '" + tab + "'" + " AND " + ROOM_DETAIL_ID_ROOM + "= '" + room + "'", null) > 0;
        } else {
            delete = getDatabase().delete(ROOM_DETAIl_TABLE, ROOM_DETAIL_ID_TAB + "= '" + tab + "'" + " AND " + ROOM_DETAIL_ID_ROOM + "= '" + room + "' AND " + ROOM_DETAIL_FLAG_ROOM + " NOT IN ('value','list','parent')", null) > 0;
        }

        return delete;
    }

    public boolean deleteRoomsDetailbyRoomId(String roomId) {
        return getDatabase().delete(ROOM_DETAIl_TABLE, ROOM_DETAIL_ID_ROOM + "= '" + roomId + "'", null) > 0;
    }

    public boolean deleteRoomsDetailbyId(String Id, String tab, String room) {
        return getDatabase().delete(ROOM_DETAIl_TABLE, ROOM_DETAIL_ID + "= '" + Id + "' AND " + ROOM_DETAIL_ID_TAB + "= '" + tab + "'" + " AND " + ROOM_DETAIL_ID_ROOM + "= '" + room + "'", null) > 0;
    }


    public boolean deleteRoomsNameAll() {
        return getDatabase().delete(ROOMNAME_TABLE, null, null) > 0;
    }


    public boolean deleteFormDetail(String username, String tab_id) {
        return getDatabase().delete(ROOM_DETAIl_TABLE, ROOM_DETAIL_ID_ROOM + "= '" + username + "' AND " + ROOM_DETAIL_FLAG_ROOM + "= 'form'" + " AND " + ROOM_DETAIL_ID_TAB + "= '" + tab_id + "'", null) > 0;
    }


}
