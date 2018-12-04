package com.byonchat.android.AdvRecy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MainDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "TabMain.db";

    public static final String TABLE_ITEM = "tabItem";

    public static final String TAB_ID = "tabId";
    public static final String TAB_ROOM_ID = "tabRoomId";
    public static final String TAB_POSITION = "tabPosition";

    private String CREATE_TAB_ITEM_TABLE = "CREATE TABLE " + TABLE_ITEM + " (" + TAB_ID + " INTEGER PRIMARY KEY, " +
    TAB_ROOM_ID + " INTEGER, " + TAB_POSITION + " TEXT" + ")" ;

    private String  DROP_TAB_ITEM_TABLE = "DROP TABLE IF EXISTS " + TABLE_ITEM;

    public MainDbHelper (Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TAB_ITEM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TAB_ITEM_TABLE);
        onCreate(db);
    }
}
