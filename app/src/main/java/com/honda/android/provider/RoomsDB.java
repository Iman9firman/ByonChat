package com.honda.android.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.honda.android.R;
import com.honda.android.list.ItemListTrending;

import java.util.ArrayList;

/**
 * Created by Lukmanpryg on 6/29/2016.
 */

public class RoomsDB extends SQLiteOpenHelper {

    public static final String ROOMS_ID = "id";
    public static final String ROOMS_NAME = "name";
    public static final String ROOMS_DESC = "description";
    public static final String ROOMS_REALNAME = "realname";
    public static final String ROOMS_LINKICON = "link";
    public static final String ROOMS_TYPE = "type";
    public static final String ROOMS_TARGET_URL = "target_url";
    public static final String ROOMS_ISACTIVE = "is_active";
    public static final String TRENDING_ID = "id";
    public static final String TRENDING_NAME = "trending_name";
    public static final String STRING_NAME = "string_name";

    private SQLiteDatabase mDb;
    private static RoomsDB instance;

    private static final String DATABASE_NAME = "ROOMS.db";
    private static final int DATABASE_VERSION = 7;
    private static final String ROOMS_TABLE = "rooms";
    private static final String TRENDING_TABLE = "trendings";
    private static final String SAVE_STRING = "strings";

    private static final String CREATE_TABLE_ROOMS = "create table "
            + ROOMS_TABLE + " (" + ROOMS_ID
            + " integer primary key , "
            + ROOMS_NAME + " text, "
            + ROOMS_DESC + " text, "
            + ROOMS_REALNAME + " text, "
            + ROOMS_LINKICON + " text, "
            + ROOMS_TYPE + " text, "
            + ROOMS_ISACTIVE + " text)";

    private static final String CREATE_TABLE_TRENDING = "create table "
            + TRENDING_TABLE + " (" + TRENDING_ID
            + " integer primary key , "
            + TRENDING_NAME + " text, "
            + ROOMS_TYPE + " text)";

    private static final String CREATE_TABLE_SAVE_STRING = "create table "
            + SAVE_STRING + " (" + TRENDING_ID
            + " integer primary key , "
            + STRING_NAME + " text)";

    private Context mCtx;

    @Override
    public void onCreate(SQLiteDatabase sqliteDatabase) {
        if (mDb == null) {
            mDb = sqliteDatabase;
        }

        createTable();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            if (newVersion == 7) {
                db.execSQL("DROP TABLE IF EXISTS " + ROOMS_TABLE);
            }

            db.execSQL(mCtx.getString(R.string.sql_createtable_rooms));
            db.execSQL(mCtx.getString(R.string.sql_createtable_rooms_trending));
            db.execSQL(mCtx.getString(R.string.sql_createtable_rooms_string));
            try {
                db.execSQL("ALTER TABLE " + ROOMS_TABLE + " ADD COLUMN " + ROOMS_ISACTIVE + " text ");
                db.execSQL("ALTER TABLE " + ROOMS_TABLE + " ADD COLUMN " + ROOMS_TARGET_URL + " text ");
            } catch (SQLiteException e) {
                //ignored when column exist
                Log.e("IGNORE IT.", e.toString());
            }
        }
    }

    private SQLiteDatabase getDatabase() {
        if (mDb == null) {
            mDb = getWritableDatabase();
        }
        return mDb;
    }

    private void createTable() {
        getDatabase().execSQL(mCtx.getString(R.string.sql_createtable_rooms));
        getDatabase().execSQL(mCtx.getString(R.string.sql_createtable_rooms_trending));
        getDatabase().execSQL(mCtx.getString(R.string.sql_createtable_rooms_string));
    }

    public RoomsDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mCtx = context;
    }

    public synchronized static RoomsDB getInstance(
            Context context) {
        if (instance == null) {
            instance = new RoomsDB(
                    context.getApplicationContext());
        }
        return instance;
    }

    public RoomsDB open() throws SQLException {
        mDb = getWritableDatabase();
        return this;
    }

    public void close() {
        mDb.close();
    }

    public void insertRooms(ContactBot contactBot) {
        ContentValues vc = new ContentValues();
        vc.put(ROOMS_ISACTIVE, "0");
        getDatabase().update(ROOMS_TABLE, vc, null, null);

        ContentValues cv = new ContentValues();
        cv.put(ROOMS_NAME, contactBot.getName());
        cv.put(ROOMS_DESC, contactBot.getDesc());
        cv.put(ROOMS_REALNAME, contactBot.getRealname());
        cv.put(ROOMS_LINKICON, contactBot.getLink());
        cv.put(ROOMS_TYPE, contactBot.getType());
        cv.put(ROOMS_ISACTIVE, contactBot.isActive ? "1" : "0");
        cv.put(ROOMS_TARGET_URL, contactBot.getTargetUrl());
        mDb.insert(ROOMS_TABLE, null, cv);
    }


    public void insertTrending(ItemListTrending itemListTrending) {
        ContentValues cv = new ContentValues();
        cv.put(TRENDING_NAME, itemListTrending.getName());
        cv.put(ROOMS_TYPE, itemListTrending.getType());
        mDb.insert(TRENDING_TABLE, null, cv);
    }

    public void insertSaveString(String value) {
        ContentValues cv = new ContentValues();
        cv.put(STRING_NAME, value);
        mDb.insert(SAVE_STRING, null, cv);
        Log.w("apa isii",value);
//        mDb.execSQL("INSERT INTO strings ("+STRING_NAME+") VALUES ("+value+")");
    }

    public boolean deleteTrending(String type) {
        return mDb.delete(TRENDING_TABLE, ROOMS_TYPE + "= '" + type + "'", null) > 0;
    }

    public boolean deleteRooms() {
        return mDb.delete(ROOMS_TABLE, null, null) > 0;
    }

    public boolean deleteStrings() {
        return mDb.delete(SAVE_STRING, null, null) > 0;
    }

    public boolean deletebyId(String id) {
        return mDb.delete(ROOMS_TABLE, ROOMS_ID + "= " + id + "", null) > 0;
    }

    public boolean deletebyName(String judul) {
        return mDb.delete(ROOMS_TABLE, ROOMS_NAME + "= '" + judul + "'", null) > 0;
    }

    public boolean deleteStringbyValue(String judul) {
        return mDb.delete(SAVE_STRING, STRING_NAME + "= '" + judul + "'", null) > 0;
    }

    public boolean deleteRoomsSelected() {
        return mDb.delete(ROOMS_TABLE, ROOMS_TYPE + "= '2'", null) > 0;
    }

    public boolean deleteRoomsTrending() {
        return mDb.delete(ROOMS_TABLE, ROOMS_TYPE + "= '1'", null) > 0;
    }

    public ArrayList<ItemListTrending> retrieveTrending(String type) throws SQLException {
        ArrayList<ItemListTrending> listMemberCards = new ArrayList<ItemListTrending>();
        Cursor cur = mDb.query(true, TRENDING_TABLE, new String[]{
                        TRENDING_ID, TRENDING_NAME, ROOMS_TYPE}
                , ROOMS_TYPE + "= '" + type + "'", null, null, null, null, null);
        if (cur.moveToFirst()) {
            do {
                String id = cur.getString(cur.getColumnIndex(TRENDING_ID));
                String nam = cur.getString(cur.getColumnIndex(TRENDING_NAME));
                String typ = cur.getString(cur.getColumnIndex(ROOMS_TYPE));
                listMemberCards.add(new ItemListTrending(id, "#" + nam, typ));
            } while (cur.moveToNext());
        }
        return listMemberCards;
    }

    public ArrayList<ContactBot> retrieveRooms(String type) throws SQLException {
        ArrayList<ContactBot> listMemberCards = new ArrayList<ContactBot>();
        Cursor cur = mDb.query(true, ROOMS_TABLE, new String[]{
                        ROOMS_ID, ROOMS_NAME, ROOMS_DESC, ROOMS_REALNAME, ROOMS_LINKICON, ROOMS_TYPE}
                , ROOMS_TYPE + "= '" + type + "'", null, null, null, null, null);
        if (cur.moveToFirst()) {
            do {
                String id = cur.getString(cur.getColumnIndex(ROOMS_ID));
                String nam = cur.getString(cur.getColumnIndex(ROOMS_NAME));
                String des = cur.getString(cur.getColumnIndex(ROOMS_DESC));
                String real = cur.getString(cur.getColumnIndex(ROOMS_REALNAME));
                String ic = cur.getString(cur.getColumnIndex(ROOMS_LINKICON));
                String typ = cur.getString(cur.getColumnIndex(ROOMS_TYPE));
                listMemberCards.add(new ContactBot(id, nam, des, real, ic, typ));
            } while (cur.moveToNext());
        }
        return listMemberCards;
    }

    public void updateActiveRooms(ContactBot contactBot) {
        try {
            ContentValues vc = new ContentValues();
            vc.put(ROOMS_ISACTIVE, "0");
            getDatabase().update(ROOMS_TABLE, vc, null, null);

            ContentValues cv = new ContentValues();
            cv.put(ROOMS_ISACTIVE, contactBot.isActive ? "0" : "1");
            getDatabase().update(ROOMS_TABLE, cv, ROOMS_NAME + "=?", new String[]{contactBot.getName()});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void updateActiveRoomsManual(ContactBot contactBot) {
        try {
            ContentValues vc = new ContentValues();
            vc.put(ROOMS_ISACTIVE, "0");
            getDatabase().update(ROOMS_TABLE, vc, null, null);

            ContentValues cv = new ContentValues();
            cv.put(ROOMS_ISACTIVE, "1");
            getDatabase().update(ROOMS_TABLE, cv, ROOMS_NAME + "=?", new String[]{contactBot.getName()});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public ArrayList<ContactBot> retrieveRooms(String type, boolean isActive) throws SQLException {
        String isActived = "0";
        if (isActive)
            isActived = "1";
        ArrayList<ContactBot> listMemberCards = new ArrayList<ContactBot>();
        Cursor cur = mDb.query(true, ROOMS_TABLE, new String[]{
                        ROOMS_ID, ROOMS_NAME, ROOMS_DESC, ROOMS_REALNAME, ROOMS_LINKICON, ROOMS_TYPE, ROOMS_ISACTIVE, ROOMS_TARGET_URL}
                , ROOMS_TYPE + "= '" + type + "' AND " + ROOMS_ISACTIVE + "= '" + isActived + "'", null, null, null, null, null);
        if (cur.moveToFirst()) {
            do {
                String id = cur.getString(cur.getColumnIndex(ROOMS_ID));
                String nam = cur.getString(cur.getColumnIndex(ROOMS_NAME));
                String des = cur.getString(cur.getColumnIndex(ROOMS_DESC));
                String real = cur.getString(cur.getColumnIndex(ROOMS_REALNAME));
                String ic = cur.getString(cur.getColumnIndex(ROOMS_LINKICON));
                String typ = cur.getString(cur.getColumnIndex(ROOMS_TYPE));
                String active = cur.getString(cur.getColumnIndex(ROOMS_ISACTIVE));
                String targeturl = cur.getString(cur.getColumnIndex(ROOMS_TARGET_URL));
                boolean isAct = active.equalsIgnoreCase("1");
                listMemberCards.add(new ContactBot(id, nam, des, real, ic, typ, isAct, targeturl));
            } while (cur.moveToNext());
        }
        return listMemberCards;
    }

    public ArrayList<ContactBot> retrieveRoomsByName(String name, String tipe) throws SQLException {
        ArrayList<ContactBot> listMemberCards = new ArrayList<ContactBot>();
        Cursor cur = mDb.query(true, ROOMS_TABLE, new String[]{
                        ROOMS_ID, ROOMS_NAME, ROOMS_DESC, ROOMS_REALNAME, ROOMS_LINKICON, ROOMS_TYPE}
                , ROOMS_NAME + "= '" + name + "' AND " + ROOMS_TYPE + "= '" + tipe + "'", null, null, null, null, null);
        if (cur.moveToFirst()) {
            do {
                String id = cur.getString(cur.getColumnIndex(ROOMS_ID));
                String nam = cur.getString(cur.getColumnIndex(ROOMS_NAME));
                String des = cur.getString(cur.getColumnIndex(ROOMS_DESC));
                String real = cur.getString(cur.getColumnIndex(ROOMS_REALNAME));
                String ic = cur.getString(cur.getColumnIndex(ROOMS_LINKICON));
                String typ = cur.getString(cur.getColumnIndex(ROOMS_TYPE));
                listMemberCards.add(new ContactBot(id, nam, des, real, ic, typ));

            } while (cur.moveToNext());
        }
        return listMemberCards;
    }

    public ArrayList<ContactBot> retrieveRoomsByRealName(String name, String tipe) throws SQLException {
        ArrayList<ContactBot> listMemberCards = new ArrayList<ContactBot>();
        Cursor cur = mDb.query(true, ROOMS_TABLE, new String[]{
                        ROOMS_ID, ROOMS_NAME, ROOMS_DESC, ROOMS_REALNAME, ROOMS_LINKICON, ROOMS_TYPE}
                , ROOMS_REALNAME + "= '" + name + "' AND " + ROOMS_TYPE + "= '" + tipe + "'", null, null, null, null, null);
        if (cur.moveToFirst()) {
            do {
                String id = cur.getString(cur.getColumnIndex(ROOMS_ID));
                String nam = cur.getString(cur.getColumnIndex(ROOMS_NAME));
                String des = cur.getString(cur.getColumnIndex(ROOMS_DESC));
                String real = cur.getString(cur.getColumnIndex(ROOMS_REALNAME));
                String ic = cur.getString(cur.getColumnIndex(ROOMS_LINKICON));
                String typ = cur.getString(cur.getColumnIndex(ROOMS_TYPE));
                listMemberCards.add(new ContactBot(id, nam, des, real, ic, typ));

            } while (cur.moveToNext());
        }
        return listMemberCards;
    }

    public ArrayList<String> retrieveSaveString(){
        ArrayList<String> value = new ArrayList<>();
        Cursor cursor = getDatabase().query(SAVE_STRING, new String[]
                {
                        STRING_NAME,
                }, null, null, null, null, null);

        if (cursor != null)
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(STRING_NAME));
            value.add(name);
        }
        return value;
    }

}