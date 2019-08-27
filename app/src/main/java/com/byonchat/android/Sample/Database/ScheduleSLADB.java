package com.byonchat.android.Sample.Database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ScheduleSLADB extends SQLiteOpenHelper {
    public static final String SCH_ID = "id";
    public static final String SCH_USER = "user";
    public static final String SCH_JJT = "jjt";
    public static final String SCH_FREQUENCY = "frequency";
    public static final String SCH_FLOOR = "floor";
    public static final String SCH_PERIOD = "periode";
    public static final String SCH_START = "start_date";
    public static final String SCH_FINISH = "finish_date";
    public static final String SCH_DETAIL_AREA = "detail_area";

    public static final String SCH_DATA_ID = "id";
    public static final String SCH_DATA_ID_AREA = "id_detailarea";
    public static final String SCH_DATA_USER = "user";
    public static final String SCH_DATA_JJT = "jjt";
    public static final String SCH_DATA_START_PIC = "start_pic";
    public static final String SCH_DATA_PROSES_PIC = "proses_pic";
    public static final String SCH_DATA_DONE_PIC = "done_pic";

    private Context context;
    private static ScheduleSLADB instance;
    private SQLiteDatabase database;

    private static final String DATABASE_NAME = "Schedule_SLA.db";
    private static final int DATABASE_VERSION = 2;

    private static final String SCH_TABLE = "Schedule";
    private static final String SCH_DATA_TABLE = "Data_pic";

    private static final String CREATE_TABLE_SCHEDULE = "create table "
            + SCH_TABLE + " (" + SCH_ID
            + " integer primary key autoincrement, "
            + SCH_USER + " text not null unique,"
            + SCH_JJT + " text,"
            + SCH_FREQUENCY + " text,"
            + SCH_FLOOR + " text,"
            + SCH_PERIOD + " text,"
            + SCH_START + " text,"
            + SCH_FINISH + " text,"
            + SCH_DETAIL_AREA + " text);";

    private static final String CREATE_TABLE_DATA = "create table "
            + SCH_DATA_TABLE + " (" + SCH_DATA_ID
            + " integer primary key autoincrement, "
            + SCH_DATA_ID_AREA + " text,"
            + SCH_DATA_USER + " text,"
            + SCH_DATA_JJT + " text,"
            + SCH_DATA_START_PIC + " text,"
            + SCH_DATA_PROSES_PIC + " text,"
            + SCH_DATA_DONE_PIC + " text);";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SCHEDULE);
        db.execSQL(CREATE_TABLE_DATA);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + SCH_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + SCH_DATA_TABLE);
            onCreate(db);
        }
    }

    private ScheduleSLADB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    private SQLiteDatabase getDatabase() {
        if (database == null) {
            database = getWritableDatabase();
        }
        return database;
    }

    public synchronized static ScheduleSLADB getInstance(
            Context context) {
        if (instance == null) {
            instance = new ScheduleSLADB(
                    context);
        }
        return instance;
    }

    public void insertSchedule(ScheduleSLA scheduleSLA) {
        ContentValues cv = new ContentValues();
        cv.put(SCH_USER, scheduleSLA.getUser());
        cv.put(SCH_JJT, scheduleSLA.getJjt());
        cv.put(SCH_FREQUENCY, scheduleSLA.getFrequency());
        cv.put(SCH_FLOOR, scheduleSLA.getFloor());
        cv.put(SCH_START, scheduleSLA.getStartdate());
        cv.put(SCH_FINISH, scheduleSLA.getFinishdate());
        cv.put(SCH_DETAIL_AREA, scheduleSLA.getDetail_area());
        getDatabase().insert(SCH_TABLE, null, cv);
    }

    public void insertDataSchedule(ScheduleSLA scheduleSLA) {
        ContentValues cv = new ContentValues();
        cv.put(SCH_DATA_ID_AREA,scheduleSLA.getIddata());
        cv.put(SCH_DATA_JJT, scheduleSLA.getJjt());
        cv.put(SCH_DATA_USER, scheduleSLA.getUser());
        cv.put(SCH_DATA_START_PIC, scheduleSLA.getStartpic());
        cv.put(SCH_DATA_PROSES_PIC, scheduleSLA.getProgresspic());
        cv.put(SCH_DATA_DONE_PIC, scheduleSLA.getFinishpic());
        getDatabase().insert(SCH_DATA_TABLE, null, cv);
    }

    public Cursor getDataPicByID(String Id) {
        Cursor cursor = getDatabase().query(SCH_DATA_TABLE, new String[]
                {SCH_DATA_ID_AREA, SCH_DATA_JJT, SCH_DATA_USER, SCH_DATA_START_PIC, SCH_DATA_PROSES_PIC, SCH_DATA_DONE_PIC
                }, SCH_DATA_ID_AREA + "= '" + Id + "'", null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }

    public Cursor getAllImgSaved() {
        Cursor cursor = getDatabase().query(SCH_DATA_TABLE, new String[]
                {SCH_DATA_ID_AREA, SCH_DATA_JJT, SCH_DATA_USER, SCH_DATA_START_PIC, SCH_DATA_PROSES_PIC, SCH_DATA_DONE_PIC
                }, null, null, null, null, null);
        return cursor;
    }

    public boolean updateImgNow(ScheduleSLA scheduleSLA, String pos) {
        ContentValues cv = new ContentValues();
        cv.put(SCH_DATA_ID_AREA,scheduleSLA.getIddata());
        cv.put(SCH_DATA_JJT, scheduleSLA.getJjt());
        cv.put(SCH_DATA_USER, scheduleSLA.getUser());
        if (pos.equalsIgnoreCase("start")) {
            cv.put(SCH_DATA_START_PIC, scheduleSLA.getStartpic());
        } else if (pos.equalsIgnoreCase("proses")) {
            cv.put(SCH_DATA_PROSES_PIC, scheduleSLA.getProgresspic());
        } else {
            cv.put(SCH_DATA_DONE_PIC, scheduleSLA.getFinishpic());
        }
        String where = SCH_DATA_ID_AREA + " = ? ";

        String[] whereArgs = {scheduleSLA.getIddata()};

        boolean updateSuccessful = getDatabase().update(SCH_DATA_TABLE, cv, where, whereArgs) > 0;

        return updateSuccessful;
    }

    public boolean updateImgAll(ScheduleSLA scheduleSLA) {
        ContentValues cv = new ContentValues();
        cv.put(SCH_DATA_ID_AREA,scheduleSLA.getIddata());
        cv.put(SCH_DATA_JJT, scheduleSLA.getJjt());
        cv.put(SCH_DATA_USER, scheduleSLA.getUser());
        cv.put(SCH_DATA_START_PIC, scheduleSLA.getStartpic());
        cv.put(SCH_DATA_PROSES_PIC, scheduleSLA.getProgresspic());
        cv.put(SCH_DATA_DONE_PIC, scheduleSLA.getFinishpic());
        String where = SCH_DATA_ID_AREA + " = ? ";

        String[] whereArgs = {scheduleSLA.getIddata()};

        boolean updateSuccessful = getDatabase().update(SCH_DATA_TABLE, cv, where, whereArgs) > 0;

        return updateSuccessful;
    }


    public boolean deleteByIdDAandJJT(String id_da, String id_jjt) {
        String where = SCH_DATA_ID_AREA + " = ? AND " + SCH_DATA_JJT +  " = ? ";
        String[] whereArgs = {id_da, id_jjt};

        boolean updateSuccessful = getDatabase().delete(SCH_DATA_TABLE, where, whereArgs) > 0;

        return updateSuccessful;
    }

}