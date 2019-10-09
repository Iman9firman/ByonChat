package com.byonchat.android.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UpdateListDB extends SQLiteOpenHelper {

    public static final String UPD_ID = "id";
    public static final String UPD_NAME = "type_name";
    public static final String UPD_USER = "username_room";
    public static final String UPD_IDTAB = "id_tab";
    public static final String UPD_PARENT_ID = "parent_id";
    public static final String UPD_ID_LIST_PUSH = "id_list_push";
    public static final String UPD_FROM = "fromlist";
    public static final String UPD_STATUS = "status";
    public static final String UPD_TARGET_URL = "target_url";
    public static final String UPD_APP_VERSION = "app_version";
    public static final String UPD_APP_COMPANY = "app_company";
    public static final String UPD_ISACTIVE = "is_active";

    public static final String UPD_REQ = "update_req";
    public static final String UPD_DATE = "date_received";
    public static final String UPD_DATE_EXP = "date_expired";

    private SQLiteDatabase mDb;
    private static UpdateListDB instance;

    private static final String DATABASE_NAME = "UpdateList.db";
    private static final int DATABASE_VERSION = 2;
    private static final String UPD_TABLE = "updatelist";
    private static final String SAVE_STRING = "strings";

    private static final String CREATE_TABLE_ROOMS1 = "CREATE TABLE IF NOT EXISTS "
            + UPD_TABLE + " (" + UPD_ID
            + " integer primary key , "
            + UPD_NAME + " text, "
            + UPD_USER + " text, "
            + UPD_IDTAB + " text, "
            + UPD_PARENT_ID + " text, "
            + UPD_ID_LIST_PUSH + " text, "
            + UPD_TARGET_URL + " text, "
            + UPD_FROM + " text, "
            + UPD_APP_VERSION + " text, "
            + UPD_APP_COMPANY + " text, "
            + UPD_REQ + " text, "
            + UPD_DATE + " text, "
            + UPD_DATE_EXP + " text, "
            + UPD_STATUS + " text)";

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
            if (newVersion == 2) {
                db.execSQL("DROP TABLE IF EXISTS " + UPD_TABLE);
            }

            db.execSQL(CREATE_TABLE_ROOMS1);
            /*try {
//                db.execSQL("ALTER TABLE " + UPD_TABLE + " ADD COLUMN " + UPD_ISACTIVE + " text ");
//                db.execSQL("ALTER TABLE " + UPD_TABLE + " ADD COLUMN " + UPD_TARGET_URL + " text ");
            } catch (SQLiteException e) {
                //ignored when column exist
                Log.e("IGNORE IT.", e.toString());
            }*/
        }
    }

    private SQLiteDatabase getDatabase() {
        if (mDb == null) {
            mDb = getWritableDatabase();
        }
        return mDb;
    }

    private void createTable() {
        getDatabase().execSQL(CREATE_TABLE_ROOMS1);
    }

    public UpdateListDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mCtx = context;
    }

    public synchronized static UpdateListDB getInstance(
            Context context) {
        if (instance == null) {
            instance = new UpdateListDB(
                    context.getApplicationContext());
        }
        return instance;
    }

    public UpdateListDB open() throws SQLException {
        mDb = getWritableDatabase();
        return this;
    }

    public void close() {
        mDb.close();
    }

    public void insertRooms(UpdateList contactBot) {
        ContentValues cv = new ContentValues();
        if(contactBot.gettype_name().equalsIgnoreCase("refresh_tab")) {
            //refresh_tab
            cv.put(UPD_NAME, contactBot.gettype_name());
            cv.put(UPD_USER, contactBot.getusername());
            cv.put(UPD_STATUS, contactBot.getstatus());
        }else if(contactBot.gettype_name().equalsIgnoreCase("refresh_form")) {
            //refresh_form
            cv.put(UPD_NAME, contactBot.gettype_name());
            cv.put(UPD_USER, contactBot.getusername());
            cv.put(UPD_IDTAB, contactBot.getid_tab());
            cv.put(UPD_ID_LIST_PUSH, contactBot.getid_list_push());
            cv.put(UPD_TARGET_URL, contactBot.getlink_tembak());
            cv.put(UPD_FROM, contactBot.getfromlist());
            cv.put(UPD_STATUS, contactBot.getstatus());
        } else if(contactBot.gettype_name().equalsIgnoreCase("refresh_list")) {
            //refresh_list
            cv.put(UPD_NAME, contactBot.gettype_name());
            cv.put(UPD_USER, contactBot.getusername());
            cv.put(UPD_IDTAB, contactBot.getid_tab());
            cv.put(UPD_ID_LIST_PUSH, contactBot.getid_list_push());
            cv.put(UPD_TARGET_URL, contactBot.getlink_tembak());
            cv.put(UPD_FROM, contactBot.getfromlist());
            cv.put(UPD_STATUS, contactBot.getstatus());
        } else {
            //refresh_version
            cv.put(UPD_NAME, contactBot.gettype_name());
            cv.put(UPD_APP_VERSION, contactBot.getVersion());
            cv.put(UPD_APP_COMPANY, contactBot.getCompany());
            cv.put(UPD_STATUS, contactBot.getstatus());
            cv.put(UPD_TARGET_URL, contactBot.getlink_tembak());
            cv.put(UPD_DATE, contactBot.getDate_received());
            cv.put(UPD_DATE_EXP, contactBot.getDate_expired());
        }
        mDb.insert(UPD_TABLE, null, cv);
    }

    public void updateRoomsByUsername(String username, String type_name){
        ContentValues cv = new ContentValues();
        cv.put(UPD_STATUS, "1");
        mDb.update(UPD_TABLE,  cv,  UPD_USER + " = ? AND " + UPD_NAME + " = ?", new String[]{username, type_name});
    }

    public void updateTabsByUsernAndIdtab(String username, String idtab, String type_name){
        ContentValues cv = new ContentValues();
        cv.put(UPD_STATUS, "1");
        mDb.update(UPD_TABLE,  cv,  UPD_USER + " = ? AND " + UPD_IDTAB + " = ? AND " + UPD_NAME + " = ?", new String[]{username, idtab, type_name});
    }

    public void updateVersionByCompany(String company, String version, String type_name){
        ContentValues cv = new ContentValues();
        cv.put(UPD_STATUS, "1");
        mDb.update(UPD_TABLE,  cv,  UPD_APP_COMPANY + " = ? AND " + UPD_APP_VERSION + " = ? AND " + UPD_NAME + " = ?", new String[]{company, version, type_name});
    }

    public Cursor getUnrefreshedData(String status)
    {
        Cursor cursor = mDb.query(UPD_TABLE, new String[]
                {
                        UPD_NAME, UPD_USER, UPD_IDTAB, UPD_PARENT_ID, UPD_ID_LIST_PUSH, UPD_TARGET_URL, UPD_DATE_EXP, UPD_APP_VERSION, UPD_APP_COMPANY
                },UPD_STATUS + " = " + status, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }

    public Cursor getUnrefreshedVersion(String status){
        String rrefr = "refresh_version";
        Cursor cursor = mDb.query(UPD_TABLE, new String[]
                {
                        UPD_NAME, UPD_USER, UPD_IDTAB, UPD_PARENT_ID, UPD_ID_LIST_PUSH, UPD_TARGET_URL, UPD_DATE_EXP, UPD_APP_VERSION, UPD_APP_COMPANY
                },UPD_STATUS + " = ? AND " + UPD_NAME + " = ?", new String[]{status, rrefr}, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }

    public boolean deleteRooms() {
        return mDb.delete(UPD_TABLE, null, null) > 0;
    }

    public boolean deletebyId(String id) {
        return mDb.delete(UPD_TABLE, UPD_ID + "= " + id + "", null) > 0;
    }

    public boolean deletebyName(String judul) {
        return mDb.delete(UPD_TABLE, UPD_NAME + "= '" + judul + "'", null) > 0;
    }

    public boolean deleteRoomsStatusDone() {
        return mDb.delete(UPD_TABLE, UPD_STATUS + "= '1'", null) > 0;
    }

}