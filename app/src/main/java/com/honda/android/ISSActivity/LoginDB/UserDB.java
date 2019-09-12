package com.honda.android.ISSActivity.LoginDB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.honda.android.provider.BotListDB;

public class UserDB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Userlogin.db";
    public static final String TABLE_ISS = "user";

    private SQLiteDatabase database;
    private static UserDB instance;
    public static final String URI_TOKEN = "URI_TOKEN";
    public static final String STATUS = "STATUS";
    public static final String USERNAME = "USERNAME";
    public static final String EMPLOYEE_PHOTOS = "EMPLOYEE_PHOTOS";
    public static final String EMPLOYEE_NIK = "EMPLOYEE_NIK";
    public static final String EMPLOYEE_JT = "EMPLOYEE_JT";
    public static final String EMPLOYEE_NAME = "EMPLOYEE_NAME";
    public static final String EMPLOYEE_EMAIL = "EMPLOYEE_EMAIL";
    public static final String EMPLOYEE_PHONE = "EMPLOYEE_PHONE";
    public static final String EMPLOYEE_MULTICOST = "EMPLOYEE_MULTICOST";
    public static final String ATASAN_1_JT = "ATASAN_1_JT";
    public static final String ATASAN_1_EMAIL = "ATASAN_1_EMAIL";
    public static final String ATASAN_1_NAMA = "ATASAN_1_NAMA";
    public static final String ATASAN_1_NIK = "ATASAN_1_NIK";
    public static final String ATASAN_1_PHONE = "ATASAN_1_PHONE";
    public static final String ATASAN_1_USERNAME = "ATASAN_1_USERNAME";
    public static final String DIVISION_CODE = "DIVISION_CODE";
    public static final String DIVISION_NAME = "DIVISION_NAME";
    public static final String DEPARTEMEN_CODE = "DEPARTEMEN_CODE";
    public static final String DEPARTEMEN_NAME = "DEPARTEMEN_NAME";
    public static final String ATASAN_2_JT = "ATASAN_2_JT";
    public static final String ATASAN_2_EMAIL = "ATASAN_2_EMAIL";
    public static final String ATASAN_2_NAMA = "ATASAN_2_NAMA";
    public static final String ATASAN_2_NIK = "ATASAN_2_NIK";
    public static final String ATASAN_2_PHONE = "ATASAN_2_PHONE";
    public static final String ATASAN_2_USERNAME = "ATASAN_2_USERNAME";
    public static final String LIST_REQ_ROLE = "LIST_REQ_ROLE";
    public static final String LIST_APPROVE_ROLE1 = "LIST_APPROVE_ROLE1";
    public static final String LIST_APPROVE_ROLE2 = "LIST_APPROVE_ROLE2";
    public static final String MY_ROLE = "MY_ROLE";

    private String CREATE_EVENT_TABLE = "CREATE TABLE " + TABLE_ISS + " (" +
            URI_TOKEN + " TEXT, " +
            STATUS + " TEXT, " +
            USERNAME + " TEXT, " +
            EMPLOYEE_NAME + " TEXT, " +
            EMPLOYEE_EMAIL + " TEXT, " +
            EMPLOYEE_NIK + " TEXT, " +
            EMPLOYEE_JT + " TEXT, " +
            EMPLOYEE_MULTICOST + " TEXT, " +
            EMPLOYEE_PHONE + " TEXT, " +
            EMPLOYEE_PHOTOS + " TEXT, " +
            ATASAN_1_USERNAME + " TEXT, " +
            ATASAN_1_EMAIL + " TEXT, " +
            ATASAN_1_NIK + " TEXT, " +
            ATASAN_1_JT + " TEXT, " +
            ATASAN_1_NAMA + " TEXT, " +
            ATASAN_1_PHONE + " TEXT, " +
            DIVISION_CODE + " TEXT, " +
            DIVISION_NAME + " TEXT, " +
            DEPARTEMEN_CODE + " TEXT, " +
            DEPARTEMEN_NAME + " TEXT, " +
            ATASAN_2_USERNAME + " TEXT, " +
            ATASAN_2_EMAIL + " TEXT, " +
            ATASAN_2_NIK + " TEXT, " +
            ATASAN_2_JT + " TEXT, " +
            ATASAN_2_NAMA + " TEXT, " +
            ATASAN_2_PHONE + " TEXT, " +
            LIST_APPROVE_ROLE1 + " TEXT, " +
            LIST_APPROVE_ROLE2 + " TEXT, " +
            LIST_REQ_ROLE + " TEXT, " +
            MY_ROLE + " TEXT" +
            ")";

    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_ISS;

    public UserDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_EVENT_TABLE);
        database = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_USER_TABLE);
        onCreate(db);
    }


    private SQLiteDatabase getDatabase() {
        if (database == null) {
            database = getWritableDatabase();
        }
        return database;
    }

    public synchronized static UserDB getInstance(
            Context context) {
        if (instance == null) {
            instance = new UserDB(
                    context);
        }
        return instance;
    }


    public Cursor query(String rawQuery, String[] args) {
        return getDatabase().rawQuery(rawQuery, args);
    }

    public void execSql(String sql, String[] args) {
        if (args == null) {
            getDatabase().execSQL(sql);
        } else {
            getDatabase().execSQL(sql, args);
        }
    }

    public Cursor getSingle() {
        Cursor cursor = getDatabase().query(TABLE_ISS, new String[]
                {
                        EMPLOYEE_MULTICOST,
                }, null, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }

    public String getColValue(String keyword){
        Cursor cursor = getDatabase().query(TABLE_ISS, new String[]
                {
                        keyword,
                }, null, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();
        String name = cursor.getString(cursor.getColumnIndexOrThrow(keyword));
        return name;
    }

}
