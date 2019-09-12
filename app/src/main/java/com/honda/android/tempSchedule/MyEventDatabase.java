package com.honda.android.tempSchedule;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyEventDatabase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "MyEvent.db";
    public static final String TABLE_EVENT = "event";

    public static final String EVENT_ID = "id_event";
    public static final String EVENT_ID_DETAIL = "id_detail_event";
    public static final String EVENT_ID_TAB = "id_tab_event";
    public static final String EVENT_START_DATE = "startDate_event";
    public static final String EVENT_STATUS = "status_event";
    public static final String EVENT_VALUE = "value_event";
    public static final String EVENT_WARNA = "warna_event";
    public static final String EVENT_ISSUBMIT = "issubmit_event";
    public static final String EVENT_KETERANGAN = "keterangan_event";
//    public static final String EVENT_END_DATE = "endDate_event";
//    public static final String EVENT_START_TIME = "startTime_event";
//    public static final String EVENT_END_TIME = "endTime_event";
//    public static final String EVENT_CREATED_BY = "createdBy_event";
//    public static final String EVENT_UPDATED_BY = "updatedBy_event";
//    public static final String EVENT_CREATED_DATE = "createdDate_event";
//    public static final String EVENT_UPDATED_DATE = "updatedDate_event";


    private String CREATE_EVENT_TABLE = "CREATE TABLE " + TABLE_EVENT + " (" +
            EVENT_ID + " INTEGER PRIMARY KEY, " +
            EVENT_ID_DETAIL + " TEXT, " +
            EVENT_ID_TAB + " TEXT, " +
            EVENT_START_DATE + " TEXT, " +
            EVENT_STATUS + " TEXT, " +
            EVENT_VALUE + " TEXT, " +
            EVENT_WARNA + " TEXT, " +
            EVENT_ISSUBMIT + " INTEGER DEFAULT 0," +
            EVENT_KETERANGAN + " TEXT " +
            /*EVENT_END_DATE + " TEXT, " +
            EVENT_START_TIME + " TEXT, " +
            EVENT_END_TIME + " TEXT, " +
            EVENT_CREATED_BY + " TEXT, " +
            EVENT_UPDATED_BY + " TEXT, " +
            EVENT_CREATED_DATE + " TEXT, " +
            EVENT_UPDATED_DATE + " TEXT" +*/
            ")";

    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_EVENT;

    public MyEventDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_EVENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        if (newVer > oldVer) {
            db.execSQL("ALTER TABLE " + TABLE_EVENT + " ADD COLUMN " + EVENT_KETERANGAN + " TEXT");
        }
        db.execSQL(DROP_USER_TABLE);
        onCreate(db);
    }

    public void resetDatabase() {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(DROP_USER_TABLE);
        database.execSQL(CREATE_EVENT_TABLE);
        database.close();
    }
}
