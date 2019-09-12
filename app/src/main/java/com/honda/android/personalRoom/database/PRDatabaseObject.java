package com.honda.android.personalRoom.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by lukma on 4/7/2016.
 */
public class PRDatabaseObject {
    protected SQLiteDatabase database;
    private PRDatabaseHelper dbHelper;
    private Context mContext;

    public PRDatabaseObject(Context context){
        this.mContext = context;
        dbHelper = PRDatabaseHelper.getHelper(mContext);
        open();
    }

    public void open() throws SQLException {
        if(dbHelper == null)
            dbHelper = PRDatabaseHelper.getHelper(mContext);
        database = dbHelper.getWritableDatabase();
    }
}
