package com.byonchat.android.provider;

/**
 * Created by Iman Firmansyah on 1/9/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.byonchat.android.list.ItemListMemberCard;

import java.util.ArrayList;

public class MembersDB {
    public static final String MEMBERS_ID = "id";
    public static final String MEMBERS_NAME = "name";
    public static final String MEMBERS_COLOR = "color";

    public static final String MEMBERS_DETAIL_ID = "id";
    public static final String MEMBERS_DETAIL_TIME = "time";
    public static final String MEMBERS_DETAIL_BARCODE = "barcode";
    public static final String MEMBERS_DETAIL_PROMO = "promo";
    public static final String MEMBERS_DETAIL_J_PROMO = "judulPromo";
    public static final String MEMBERS_DETAIL_BONUS = "bonus";
    public static final String MEMBERS_DETAIL_J_BONUS = "judulBonus";
    public static final String MEMBERS_DETAIL_ROOM = "room";
    public static final String MEMBERS_DETAIL_FOTO = "foto";
    public static final String MEMBERS_DETAIL_DESC = "encodedDesc";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "Members.db";
    private static final int DATABASE_VERSION = 5;

    private static final String MEMBERS_TABLE = "members";
    private static final String MEMBERS_DETAIL_TABLE = "members_detail";

    private static final String CREATE_TABLE = "create table "
            + MEMBERS_TABLE + " (" + MEMBERS_ID
            + " integer primary key , " + MEMBERS_NAME+ " text not null unique, "
            +MEMBERS_COLOR+ " text );";

    private static final String CREATE_TABLE_DETAIL = "create table "
            + MEMBERS_DETAIL_TABLE + " (" + MEMBERS_DETAIL_ID
            + " integer primary key , "
            +MEMBERS_DETAIL_TIME+ " text, "
            +MEMBERS_DETAIL_BARCODE+ " text, "
            +MEMBERS_DETAIL_PROMO+ " text, "
            +MEMBERS_DETAIL_J_PROMO+ " text, "
            +MEMBERS_DETAIL_BONUS+ " text, "
            +MEMBERS_DETAIL_J_BONUS+ " text, "
            +MEMBERS_DETAIL_ROOM+ " text, "
            +MEMBERS_DETAIL_FOTO+ " text, "
            +MEMBERS_DETAIL_DESC+ " text );";

    private final Context mCtx;



    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
            db.execSQL(CREATE_TABLE_DETAIL);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + MEMBERS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + MEMBERS_DETAIL_TABLE);
            onCreate(db);
        }
    }

    public void Reset() {
        mDbHelper.onUpgrade(this.mDb, 1, 1);
    }

    public MembersDB(Context ctx) {
        mCtx = ctx;
        mDbHelper = new DatabaseHelper(mCtx);
    }

    public MembersDB open() throws SQLException {
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public void insertMembers(ItemListMemberCard itemListMemberCard) {
        ContentValues cv = new ContentValues();
        cv.put(MEMBERS_ID, itemListMemberCard.getId_card());
        cv.put(MEMBERS_NAME, itemListMemberCard.getName());
        cv.put(MEMBERS_COLOR, itemListMemberCard.getColor_code());
        mDb.insert(MEMBERS_TABLE, null, cv);

    }

    public void insertMembersDetail(MembersDetail membersDetail) {
        ContentValues cv = new ContentValues();
        cv.put(MEMBERS_DETAIL_ID, membersDetail.getId());
        cv.put(MEMBERS_DETAIL_TIME, membersDetail.getTime());
        cv.put(MEMBERS_DETAIL_FOTO, membersDetail.getFoto());
        cv.put(MEMBERS_DETAIL_BARCODE, membersDetail.getBarcode());
        cv.put(MEMBERS_DETAIL_PROMO, membersDetail.getPromo());
        cv.put(MEMBERS_DETAIL_J_PROMO, membersDetail.getJudulPromo());
        cv.put(MEMBERS_DETAIL_BONUS, membersDetail.getBonus());
        cv.put(MEMBERS_DETAIL_J_BONUS, membersDetail.getJudulBonus());
        cv.put(MEMBERS_DETAIL_ROOM, membersDetail.getRoom());
        cv.put(MEMBERS_DETAIL_DESC, membersDetail.getEncodedDesc());
        mDb.insert(MEMBERS_DETAIL_TABLE, null, cv);
    }

    public boolean deletebyName(String name)
    {
        return mDb.delete(MEMBERS_TABLE, MEMBERS_NAME + "= '" + name+"'", null) > 0;
    }

    public boolean delete()
    {
        return mDb.delete(MEMBERS_TABLE, null, null) > 0;
    }

    public Cursor getMembersCard(String name)
    {
        Cursor cursor = mDb.query(MEMBERS_TABLE, new String[]
                {MEMBERS_ID, MEMBERS_NAME,
                        MEMBERS_COLOR
                }, MEMBERS_NAME + "= '" +name+"'", null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }


    public ArrayList<ItemListMemberCard> retriveallMembers() throws SQLException {
        ArrayList<ItemListMemberCard> listMemberCards = new ArrayList<ItemListMemberCard>();
        Cursor cur = mDb.query(true, MEMBERS_TABLE, new String[] {MEMBERS_ID, MEMBERS_NAME,
                MEMBERS_COLOR},null
                , null, null, null, null, null);
        if (cur.moveToFirst()) {
            do {

                String id = cur.getString(cur.getColumnIndex(MEMBERS_ID));
                String name = cur.getString(cur.getColumnIndex(MEMBERS_NAME));
                String color = cur.getString(cur.getColumnIndex(MEMBERS_COLOR));
                listMemberCards.add(new ItemListMemberCard(id,name,color));

            } while (cur.moveToNext());
        }
        return listMemberCards;
    }

    public Cursor getDetailMembers(String id)
    {
        Cursor cursor = mDb.query(MEMBERS_DETAIL_TABLE, new String[]
                {
                        MEMBERS_DETAIL_ID,MEMBERS_DETAIL_TIME,MEMBERS_DETAIL_BARCODE,
                        MEMBERS_DETAIL_PROMO,MEMBERS_DETAIL_J_PROMO, MEMBERS_DETAIL_BONUS,
                        MEMBERS_DETAIL_J_BONUS,MEMBERS_DETAIL_ROOM,MEMBERS_DETAIL_DESC, MEMBERS_DETAIL_FOTO
                }, MEMBERS_DETAIL_ID + "= '" +id+"'", null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }

    public boolean deleteDetailMembers(String id)
    {
        return mDb.delete(MEMBERS_DETAIL_TABLE, MEMBERS_DETAIL_ID + "= '" +id+"'", null) > 0;
    }

}
