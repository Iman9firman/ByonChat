package com.byonchat.android.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class BlockListDB {

    private static final String DB_NAME		= "block_list_db";
    private static final int	DB_VER		= 1;
    public static final String TABLE_NAME	= "block_list";
    public static final String COL_ID		= "_id";
    public static final String COL_NAME	= "name";

    private static final String TAG			= "BLOCKLIST";
    private DatabaseHelper		dbHelper;
    private SQLiteDatabase db;

    private static final String DB_CREATE	= "create table block_list (_id integer primary key, name text not null unique);";

    private final Context context;

    private static class DatabaseHelper extends SQLiteOpenHelper
    {

        public DatabaseHelper(Context context)
        {
            // TODO Auto-generated constructor stub
            super(context, DB_NAME, null, DB_VER);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            // TODO Auto-generated method stub
            db.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            // TODO Auto-generated method stub
            Log.d(TAG, "upgrade DB");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);

        }

    }

    public BlockListDB(Context context)
    {
        this.context = context;
        // TODO Auto-generated constructor stub
    }

    public BlockListDB open() throws SQLException
    {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close()
    {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    public void insertListBlock(String name)
    {
        ContentValues val = new ContentValues();

        val.put(COL_NAME, name);
        db.insert(TABLE_NAME, null, val);
    }

    public boolean deleteListBlock(String name)
    {
        return db.delete(TABLE_NAME, COL_NAME + "=" + name, null) > 0;
    }

    public ArrayList<String> getBlockList() throws SQLException {
        ArrayList<String> stickersList = new ArrayList<String>();
        Cursor cur = db.query(true, TABLE_NAME, new String[] {COL_NAME },null , null, null, null, null, null);
        if (cur.moveToFirst()) {
            do {
                String name = cur.getString(cur.getColumnIndex(COL_NAME));
                stickersList.add(name);
            } while (cur.moveToNext());
        }
        return stickersList;
    }

}
