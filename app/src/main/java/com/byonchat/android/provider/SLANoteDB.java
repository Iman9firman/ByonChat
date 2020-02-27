package com.byonchat.android.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SLANoteDB extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "sla_note_db";


    public SLANoteDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static final String TABLE_NAME = "table_id";

    public static final String ID = "id";
    public static final String COLUMN_ID_DETAIL = "id_detail";
    public static final String COLUMN_ID = "id_item";
    public static final String COLUMN_COMMENT = "comment";
    public static final String COLUMN_FILEUPLOAD = "fileupload";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_ID_DETAIL + " STRING ,"
                    + COLUMN_ID + " INTEGER ,"
                    + COLUMN_COMMENT + " TEXT ,"
                    + COLUMN_FILEUPLOAD + " TEXT "
                    + ")";

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        if (newVersion > oldVersion) {
            db.execSQL("ALTER TABLE table_id ADD COLUMN fileupload text");
        }

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}

