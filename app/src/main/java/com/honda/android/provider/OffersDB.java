package com.honda.android.provider;

/**
 * Created by Iman Firmansyah on 1/9/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.honda.android.list.ItemListOffers;
import com.honda.android.list.ItemListVoucher;

import java.util.ArrayList;

public class OffersDB {
    public static final String OFFERS_ID = "id";
    public static final String OFFERS_JUDUL = "judul";
    public static final String OFFERS_VALUE = "value";
    public static final String OFFERS_TYPE = "type";
    public static final String OFFERS_SUB = "sub";
    public static final String OFFERS_DETAIL = "detail";
    public static final String OFFERS_ICON = "icon";
    public static final String OFFERS_CATEGORY = "category";
    public static final String OFFERS_COLOR = "color";


    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "OFFERS.db";
    private static final int DATABASE_VERSION = 2;

    private static final String OFFERS_TABLE = "offers";

    private static final String CREATE_TABLE_DETAIL = "create table "
            + OFFERS_TABLE + " (" + OFFERS_ID
            + " integer primary key , "
            +OFFERS_JUDUL+ " text, "
            +OFFERS_VALUE+ " text, "
            +OFFERS_TYPE+ " text, "
            +OFFERS_SUB+ " text, "
            +OFFERS_DETAIL+ " text, "
            +OFFERS_ICON+ " text, "
            +OFFERS_CATEGORY+ " text, "
            +OFFERS_COLOR+ " text );";

    private final Context mCtx;



    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_DETAIL);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (newVersion > oldVersion) {
                db.execSQL("ALTER TABLE offers ADD COLUMN icon text");
                db.execSQL("ALTER TABLE offers ADD COLUMN category text");
                db.execSQL("ALTER TABLE offers ADD COLUMN color text");
            }

            db.execSQL("DROP TABLE IF EXISTS " + OFFERS_TABLE);
            onCreate(db);
        }
    }

    public void Reset() {
        mDbHelper.onUpgrade(this.mDb, 1, 1);
    }

    public OffersDB(Context ctx) {
        mCtx = ctx;
        mDbHelper = new DatabaseHelper(mCtx);
    }

    public OffersDB open() throws SQLException {
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public void insertOffers(OffersModel offersModel) {
        ContentValues cv = new ContentValues();
        cv.put(OFFERS_ID, offersModel.getId());
        cv.put(OFFERS_JUDUL, offersModel.getJudul());
        cv.put(OFFERS_VALUE, offersModel.getValue());
        cv.put(OFFERS_TYPE, offersModel.getType());
        cv.put(OFFERS_SUB, offersModel.getSub());
        cv.put(OFFERS_DETAIL, offersModel.getDetail());
        cv.put(OFFERS_ICON, offersModel.getIcon());
        cv.put(OFFERS_CATEGORY, offersModel.getCategory());
        cv.put(OFFERS_COLOR, offersModel.getColor());
        mDb.insert(OFFERS_TABLE, null, cv);
    }

    public boolean deletebyId(String id)
    {
        return mDb.delete(OFFERS_TABLE, OFFERS_ID + "= " + id+"", null) > 0;
    }
    public boolean deletebyName(String judul)
    {
        return mDb.delete(OFFERS_TABLE, OFFERS_JUDUL + "= '" + judul+"'", null) > 0;
    }

    public boolean deleteOffers()
    {
        return mDb.delete(OFFERS_TABLE, OFFERS_CATEGORY + "= '1'", null) > 0;
    }

    public boolean deleteVouchers()
    {
        return mDb.delete(OFFERS_TABLE, OFFERS_CATEGORY + "= '2'", null) > 0;
    }

  /*  public Cursor getOffers()
    {
        Cursor cursor = mDb.query(OFFERS_TABLE, new String[]
                {OFFERS_ID, OFFERS_JUDUL,OFFERS_VALUE,OFFERS_TYPE,OFFERS_SUB,OFFERS_DETAIL},
                null, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }*/

    public ArrayList<ItemListOffers> retriveOffers() throws SQLException {
        ArrayList<ItemListOffers> listMemberCards = new ArrayList<ItemListOffers>();
        Cursor cur = mDb.query(true, OFFERS_TABLE, new String[] {
                OFFERS_ID, OFFERS_JUDUL,OFFERS_VALUE,OFFERS_TYPE,OFFERS_SUB,OFFERS_DETAIL
                ,OFFERS_ICON,OFFERS_CATEGORY,OFFERS_COLOR}
                , OFFERS_CATEGORY + "= '1'", null, null, null, null, null);
        if (cur.moveToFirst()) {
            do {

                String id = cur.getString(cur.getColumnIndex(OFFERS_ID));
                String jud = cur.getString(cur.getColumnIndex(OFFERS_JUDUL));
                String val = cur.getString(cur.getColumnIndex(OFFERS_VALUE));
                String typ = cur.getString(cur.getColumnIndex(OFFERS_TYPE));
                String sub = cur.getString(cur.getColumnIndex(OFFERS_SUB));
                String det = cur.getString(cur.getColumnIndex(OFFERS_DETAIL));
                String ic = cur.getString(cur.getColumnIndex(OFFERS_ICON));
                String cat = cur.getString(cur.getColumnIndex(OFFERS_CATEGORY));
                String col = cur.getString(cur.getColumnIndex(OFFERS_COLOR));
                listMemberCards.add(new ItemListOffers(id,jud,val,typ,sub,det,ic,cat,col));

            } while (cur.moveToNext());
        }
        return listMemberCards;
    }

    public ArrayList<ItemListVoucher> retriveVoucher() throws SQLException {
        ArrayList<ItemListVoucher> listMemberCards = new ArrayList<ItemListVoucher>();
        Cursor cur = mDb.query(true, OFFERS_TABLE, new String[] {
                        OFFERS_ID, OFFERS_JUDUL,OFFERS_VALUE,OFFERS_TYPE,OFFERS_SUB,OFFERS_DETAIL
                        ,OFFERS_ICON,OFFERS_CATEGORY,OFFERS_COLOR}
                , OFFERS_CATEGORY + "= '2'", null, null, null, null, null);
        if (cur.moveToFirst()) {
            do {

                String id = cur.getString(cur.getColumnIndex(OFFERS_ID));
                String jud = cur.getString(cur.getColumnIndex(OFFERS_JUDUL));
                String val = cur.getString(cur.getColumnIndex(OFFERS_VALUE));
                String typ = cur.getString(cur.getColumnIndex(OFFERS_TYPE));
                String sub = cur.getString(cur.getColumnIndex(OFFERS_SUB));
                String det = cur.getString(cur.getColumnIndex(OFFERS_DETAIL));
                String ic = cur.getString(cur.getColumnIndex(OFFERS_ICON));
                String cat = cur.getString(cur.getColumnIndex(OFFERS_CATEGORY));
                String col = cur.getString(cur.getColumnIndex(OFFERS_COLOR));
                listMemberCards.add(new ItemListVoucher(id,jud,val,typ,sub,det,ic,cat,col));

            } while (cur.moveToNext());
        }
        return listMemberCards;
    }


}
