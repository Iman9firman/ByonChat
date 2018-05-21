package com.byonchat.android.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.byonchat.android.list.contact;

import java.util.ArrayList;

/**
 * Created by Lukmanpryg on 6/29/2016.
 */

public class VouchersDB {
    public static final String VOUCHERS_ID = "id";
    public static final String VOUCHERS_JUDUL = "judul";
    public static final String VOUCHERS_VALUE = "value";
    public static final String VOUCHERS_TYPE = "type";
    public static final String VOUCHERS_SUB = "sub";
    public static final String VOUCHERS_DETAIL = "detail";
    public static final String VOUCHERS_ICON = "icon";
    public static final String VOUCHERS_CATEGORY = "category";
    public static final String VOUCHERS_COLOR = "color";
    public static final String VOUCHERS_TEXTCOLOR = "textcolor";
    public static final String VOUCHERS_SERIALNUMBER = "serial";
    public static final String VOUCHERS_PEMILIK = "pemilik";
    public static final String VOUCHERS_TGLVALID = "tglvalid";
    public static final String VOUCHERS_BACKGROUND = "background";
    public static final String AMID = "amid";
    public static final String CONTACT_ID = "cid";
    public static final String CONTACT_NAME = "cname";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "VOUCHERS.db";
    private static final int DATABASE_VERSION = 2;

    private static final String VOUCHERS_TABLE = "vouchers";
    private static final String VOUCHERS_DETAIL_TABLE = "vouchers_detail";
    private static final String CONTACT_TABLE = "choose_contact";

    private static final String CREATE_TABLE_CONTACT = "create table "
            + CONTACT_TABLE + " (" + AMID
            + " integer primary key, "
            + CONTACT_ID + " text, "
            + CONTACT_NAME + " text)";

    private static final String CREATE_TABLE_VOUCHERS = "create table "
            + VOUCHERS_TABLE + " (" + VOUCHERS_ID
            + " integer primary key , "
            + VOUCHERS_JUDUL + " text, "
            + VOUCHERS_VALUE + " text, "
            + VOUCHERS_TYPE + " text, "
            + VOUCHERS_SUB + " text, "
            + VOUCHERS_DETAIL + " text, "
            + VOUCHERS_ICON + " text, "
            + VOUCHERS_CATEGORY + " text, "
            + VOUCHERS_COLOR + " text, "
            + VOUCHERS_TEXTCOLOR + " text)";

    private static final String CREATE_TABLE_VOUCHERS_DETAIL = "create table "
            + VOUCHERS_DETAIL_TABLE + " (" + VOUCHERS_ID
            + " integer primary key , "
            + VOUCHERS_SERIALNUMBER + " text, "
            + VOUCHERS_PEMILIK + " text, "
            + VOUCHERS_JUDUL + " text, "
            + VOUCHERS_VALUE + " text, "
            + VOUCHERS_TGLVALID + " text, "
            + VOUCHERS_ICON + " text, "
            + VOUCHERS_CATEGORY + " text, "
            + VOUCHERS_COLOR + " text, "
            + VOUCHERS_TEXTCOLOR + " text, "
            + VOUCHERS_BACKGROUND + " text)";

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_VOUCHERS);
            db.execSQL(CREATE_TABLE_VOUCHERS_DETAIL);
            db.execSQL(CREATE_TABLE_CONTACT);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (newVersion > oldVersion) {
                db.execSQL("ALTER TABLE vouchers ADD COLUMN icon text");
                db.execSQL("ALTER TABLE vouchers ADD COLUMN category text");
                db.execSQL("ALTER TABLE vouchers ADD COLUMN color text");
                db.execSQL("ALTER TABLE vouchers ADD COLUMN textcolor text");

                db.execSQL("ALTER TABLE vouchers_detail ADD COLUMN icon text");
                db.execSQL("ALTER TABLE vouchers_detail ADD COLUMN category text");
                db.execSQL("ALTER TABLE vouchers_detail ADD COLUMN color text");
                db.execSQL("ALTER TABLE vouchers_detail ADD COLUMN textcolor text");
                db.execSQL("ALTER TABLE vouchers_detail ADD COLUMN background text");
            }

            db.execSQL("DROP TABLE IF EXISTS " + VOUCHERS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + VOUCHERS_DETAIL_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + CONTACT_TABLE);
            onCreate(db);
        }
    }

    public void Reset() {
        mDbHelper.onUpgrade(this.mDb, 1, 1);
    }

    public VouchersDB(Context ctx) {
        mCtx = ctx;
        mDbHelper = new DatabaseHelper(mCtx);
    }

    public VouchersDB open() throws SQLException {
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public void insertContact(contact cntct) {
        ContentValues cv = new ContentValues();
        cv.put(AMID, cntct.getId());
        cv.put(CONTACT_ID, cntct.getCid());
        cv.put(CONTACT_NAME, cntct.getCname());
        mDb.insert(CONTACT_TABLE, null, cv);
    }

    public void insertVouchers(VouchersModel vouchersModel) {
        ContentValues cv = new ContentValues();
        cv.put(VOUCHERS_ID, vouchersModel.getId());
        cv.put(VOUCHERS_JUDUL, vouchersModel.getJudul());
        cv.put(VOUCHERS_VALUE, vouchersModel.getValue());
        cv.put(VOUCHERS_TYPE, vouchersModel.getType());
        cv.put(VOUCHERS_SUB, vouchersModel.getSub());
        cv.put(VOUCHERS_DETAIL, vouchersModel.getDetail());
        cv.put(VOUCHERS_ICON, vouchersModel.getIcon());
        cv.put(VOUCHERS_CATEGORY, vouchersModel.getCategory());
        cv.put(VOUCHERS_COLOR, vouchersModel.getColor());
        cv.put(VOUCHERS_TEXTCOLOR, vouchersModel.getTextcolor());
        mDb.insert(VOUCHERS_TABLE, null, cv);
    }

    public void insertVouchersDetail(VouchersDetailModel vouchersDetailModel) {

        ContentValues cv = new ContentValues();
        cv.put(VOUCHERS_ID, vouchersDetailModel.getId());
        cv.put(VOUCHERS_SERIALNUMBER, vouchersDetailModel.getSerial_number());
        cv.put(VOUCHERS_PEMILIK, vouchersDetailModel.getPemilik());
        cv.put(VOUCHERS_JUDUL, vouchersDetailModel.getJudul());
        cv.put(VOUCHERS_VALUE, vouchersDetailModel.getValue());
        cv.put(VOUCHERS_TGLVALID, vouchersDetailModel.getTgl_valid());
        cv.put(VOUCHERS_ICON, vouchersDetailModel.getIcon());
        cv.put(VOUCHERS_CATEGORY, vouchersDetailModel.getCategory());
        cv.put(VOUCHERS_COLOR, vouchersDetailModel.getColor());
        cv.put(VOUCHERS_TEXTCOLOR, vouchersDetailModel.getTextcolor());
        cv.put(VOUCHERS_BACKGROUND, vouchersDetailModel.getBackground());
        mDb.insert(VOUCHERS_DETAIL_TABLE, null, cv);
    }

    public boolean deleteContact() {
        return mDb.delete(CONTACT_TABLE, null, null) > 0;
    }

    public boolean deletebyId(String id) {
        return mDb.delete(VOUCHERS_TABLE, VOUCHERS_ID + "= " + id + "", null) > 0;
    }

    public boolean deletebyName(String judul) {
        return mDb.delete(VOUCHERS_TABLE, VOUCHERS_JUDUL + "= '" + judul + "'", null) > 0;
    }

    public boolean deleteVouchers() {
        return mDb.delete(VOUCHERS_TABLE, VOUCHERS_CATEGORY + "= '2'", null) > 0;
    }


    public boolean deleteOffers() {
        return mDb.delete(VOUCHERS_TABLE, VOUCHERS_CATEGORY + "= '1'", null) > 0;
    }

    public boolean deletebyIdOffers(String id) {
        return mDb.delete(VOUCHERS_TABLE, VOUCHERS_ID + "= " + id + "", null) > 0;
    }

    public boolean deletebyIdDetail(String id) {
        return mDb.delete(VOUCHERS_DETAIL_TABLE, VOUCHERS_ID + "= " + id + "", null) > 0;
    }

    public boolean deletebyNameDetail(String judul) {
        return mDb.delete(VOUCHERS_DETAIL_TABLE, VOUCHERS_JUDUL + "= '" + judul + "'", null) > 0;
    }

    public boolean deleteVouchersDetail() {
        return mDb.delete(VOUCHERS_DETAIL_TABLE, VOUCHERS_CATEGORY + "= '2'", null) > 0;
    }

    //    public boolean deleteVouchers()
//    {
//        return mDb.delete(OFFERS_TABLE, OFFERS_CATEGORY + "= '2'", null) > 0;
//    }

    public ArrayList<contact> retrieveContact() throws SQLException {
        ArrayList<contact> listMemberCards = new ArrayList<contact>();
        Cursor cur = mDb.query(true, CONTACT_TABLE, new String[]{
                        CONTACT_ID, CONTACT_NAME}
                , AMID + "= '1'", null, null, null, null, null);
//        String selectQuery = "SELECT * FROM VOUCHERS_TABLE";
//        Cursor cur = mDb.rawQuery(selectQuery, null);
        if (cur.moveToFirst()) {
            do {
                long id = cur.getLong(cur.getColumnIndex(AMID));
                String cid = cur.getString(cur.getColumnIndex(CONTACT_ID));
                String name = cur.getString(cur.getColumnIndex(CONTACT_NAME));
                listMemberCards.add(new contact(id, cid, name));

            } while (cur.moveToNext());
        }
        return listMemberCards;
    }

    public ArrayList<VouchersModel> retriveVouchers() throws SQLException {
        ArrayList<VouchersModel> listMemberCards = new ArrayList<VouchersModel>();
        Cursor cur = mDb.query(true, VOUCHERS_TABLE, new String[]{
                        VOUCHERS_ID, VOUCHERS_JUDUL, VOUCHERS_VALUE, VOUCHERS_TYPE, VOUCHERS_SUB, VOUCHERS_DETAIL
                        , VOUCHERS_ICON, VOUCHERS_CATEGORY, VOUCHERS_COLOR, VOUCHERS_TEXTCOLOR}
                , VOUCHERS_CATEGORY + "= '2'", null, null, null, null, null);
//        String selectQuery = "SELECT * FROM VOUCHERS_TABLE";
//        Cursor cur = mDb.rawQuery(selectQuery, null);
        if (cur.moveToFirst()) {
            do {
                String id = cur.getString(cur.getColumnIndex(VOUCHERS_ID));
                String jud = cur.getString(cur.getColumnIndex(VOUCHERS_JUDUL));
                String val = cur.getString(cur.getColumnIndex(VOUCHERS_VALUE));
                String typ = cur.getString(cur.getColumnIndex(VOUCHERS_TYPE));
                String sub = cur.getString(cur.getColumnIndex(VOUCHERS_SUB));
                String det = cur.getString(cur.getColumnIndex(VOUCHERS_DETAIL));
                String ic = cur.getString(cur.getColumnIndex(VOUCHERS_ICON));
                String cat = cur.getString(cur.getColumnIndex(VOUCHERS_CATEGORY));
                String col = cur.getString(cur.getColumnIndex(VOUCHERS_COLOR));
                String txtcol = cur.getString(cur.getColumnIndex(VOUCHERS_TEXTCOLOR));
                listMemberCards.add(new VouchersModel(id, jud, val, typ, sub, det, ic, cat, col, txtcol));

            } while (cur.moveToNext());
        }
        return listMemberCards;
    }

    public ArrayList<VouchersDetailModel> retriveVouchersDetail() throws SQLException {
        ArrayList<VouchersDetailModel> listMemberCards = new ArrayList<VouchersDetailModel>();
        Cursor cur = mDb.query(true, VOUCHERS_DETAIL_TABLE, new String[]{
                        VOUCHERS_ID, VOUCHERS_SERIALNUMBER, VOUCHERS_PEMILIK, VOUCHERS_JUDUL, VOUCHERS_VALUE, VOUCHERS_TGLVALID
                        , VOUCHERS_ICON, VOUCHERS_CATEGORY, VOUCHERS_COLOR, VOUCHERS_TEXTCOLOR, VOUCHERS_BACKGROUND}
                , VOUCHERS_CATEGORY + "= '2'", null, null, null, null, null);
//        String selectQuery = "SELECT * FROM VOUCHERS_TABLE";
//        Cursor cur = mDb.rawQuery(selectQuery, null);
        if (cur.moveToFirst()) {
            do {
                String id = cur.getString(cur.getColumnIndex(VOUCHERS_ID));
                String sn = cur.getString(cur.getColumnIndex(VOUCHERS_SERIALNUMBER));
                String pem = cur.getString(cur.getColumnIndex(VOUCHERS_PEMILIK));
                String jud = cur.getString(cur.getColumnIndex(VOUCHERS_JUDUL));
                String val = cur.getString(cur.getColumnIndex(VOUCHERS_VALUE));
                String tgl = cur.getString(cur.getColumnIndex(VOUCHERS_TGLVALID));
                String ic = cur.getString(cur.getColumnIndex(VOUCHERS_ICON));
                String cat = cur.getString(cur.getColumnIndex(VOUCHERS_CATEGORY));
                String col = cur.getString(cur.getColumnIndex(VOUCHERS_COLOR));
                String txtcol = cur.getString(cur.getColumnIndex(VOUCHERS_TEXTCOLOR));
                String backgr = cur.getString(cur.getColumnIndex(VOUCHERS_BACKGROUND));

                listMemberCards.add(new VouchersDetailModel(id, sn, pem, jud, val, tgl, ic, cat, col, txtcol, backgr));

            } while (cur.moveToNext());
        }
        return listMemberCards;
    }

    public ArrayList<VouchersModel> retriveOffers() throws SQLException {
        ArrayList<VouchersModel> listMemberCards = new ArrayList<VouchersModel>();
        Cursor cur = mDb.query(true, VOUCHERS_TABLE, new String[]{
                        VOUCHERS_ID, VOUCHERS_JUDUL, VOUCHERS_VALUE, VOUCHERS_TYPE, VOUCHERS_SUB, VOUCHERS_DETAIL
                        , VOUCHERS_ICON, VOUCHERS_CATEGORY, VOUCHERS_COLOR, VOUCHERS_TEXTCOLOR}
                , VOUCHERS_CATEGORY + "= '1'", null, null, null, null, null);
        if (cur.moveToFirst()) {
            do {
                String id = cur.getString(cur.getColumnIndex(VOUCHERS_ID));
                String jud = cur.getString(cur.getColumnIndex(VOUCHERS_JUDUL));
                String val = cur.getString(cur.getColumnIndex(VOUCHERS_VALUE));
                String typ = cur.getString(cur.getColumnIndex(VOUCHERS_TYPE));
                String sub = cur.getString(cur.getColumnIndex(VOUCHERS_SUB));
                String det = cur.getString(cur.getColumnIndex(VOUCHERS_DETAIL));
                String ic = cur.getString(cur.getColumnIndex(VOUCHERS_ICON));
                String cat = cur.getString(cur.getColumnIndex(VOUCHERS_CATEGORY));
                String col = cur.getString(cur.getColumnIndex(VOUCHERS_COLOR));
                String txtcol = cur.getString(cur.getColumnIndex(VOUCHERS_TEXTCOLOR));

                listMemberCards.add(new VouchersModel(id, jud, val, typ, sub, det, ic, cat, col, txtcol));

            } while (cur.moveToNext());
        }
        return listMemberCards;
    }

}