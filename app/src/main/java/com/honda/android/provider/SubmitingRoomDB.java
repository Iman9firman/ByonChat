package com.honda.android.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.honda.android.utils.Utility;

import java.util.ArrayList;

public class SubmitingRoomDB extends SQLiteOpenHelper {

    private static final String DB_NAME = "submiting_room";
    private static final int DB_VER = 1;
    public static final String TABLE_NAME = "submiting_room";

    public static final String COL_ID = "id";
    public static final String COL_CONTENT = "content";
    public static final String COL_STATUS = "status";

    private static final String TAG = "submiting";

    private Context context;
    private static SubmitingRoomDB instance;
    private SQLiteDatabase database;

    private static final String DB_CREATE = "create table submiting_room (id integer primary key AUTOINCREMENT, content text, status text);";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    private SubmitingRoomDB(Context context) {
        super(context, DB_NAME, null, DB_VER);
        this.context = context;
    }

    private SQLiteDatabase getDatabase() {
        if (database == null) {
            database = getWritableDatabase();
        }
        return database;
    }

    public synchronized static SubmitingRoomDB getInstance(
            Context context) {
        if (instance == null) {
            instance = new SubmitingRoomDB(
                    context);
        }
        return instance;
    }


    public long createContact(SubmitingModel submitingModel) {
        ContentValues val = new ContentValues();
        val.put(COL_CONTENT, submitingModel.getContent());
        val.put(COL_STATUS, submitingModel.getStatus());
        return getDatabase().insert(TABLE_NAME, null, val);
    }

    public boolean deleteContact(long id) {
        return getDatabase().delete(TABLE_NAME, COL_ID + "=" + id, null) > 0;
    }

    public void updateContact(long id, String text) {
        ContentValues cv = new ContentValues();
        cv.put(COL_STATUS, text);
        getDatabase().update(TABLE_NAME, cv, COL_ID + "= " + String.valueOf(id), null);

    }

    public Cursor getSingleContact(long id) {
        Cursor cursor = getDatabase().query(TABLE_NAME, new String[]
                {
                        COL_ID, COL_CONTENT, COL_STATUS
                }, COL_ID + " = " + id, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }

    public Cursor getSingleContactByContent(String COL_CONTEN) {
        Cursor cursor = getDatabase().query(TABLE_NAME, new String[]
                {
                        COL_ID, COL_CONTENT, COL_STATUS
                }, COL_CONTENT + " = '" + COL_CONTEN + "'", null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }


    public ArrayList<SubmitingModel> getAllSubmitingModel() throws SQLException {
        ArrayList<SubmitingModel> skinArrayList = new ArrayList<SubmitingModel>();
        Cursor cur = getDatabase().query(true, TABLE_NAME, new String[]{COL_ID, COL_CONTENT, COL_STATUS}, COL_STATUS + "= '1'", null, null, null, null, null);
        if (cur.moveToFirst()) {
            do {
                SubmitingModel submitingModel = new SubmitingModel();

                long id = cur.getLong(cur.getColumnIndex(COL_ID));
                String content = cur.getString(cur.getColumnIndex(COL_CONTENT));
                String status = cur.getString(cur.getColumnIndex(COL_STATUS));
                submitingModel.setId(id);
                submitingModel.setContent(content);
                submitingModel.setStatus(status);

                skinArrayList.add(submitingModel);

            } while (cur.moveToNext());
        }
        return skinArrayList;
    }
}
