package com.byonchat.android.data.local;


import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.byonchat.android.data.model.Video;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class ByonchatVideoTubeOpenHelper extends SQLiteOpenHelper {

    private Context context;

    ByonchatVideoTubeOpenHelper(Context context) {
        super(context, Video.DATABASE_NAME, null, Video.DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL(Video.VideoTubeTable.CREATE);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Before version 14, we just clear old data
        if (oldVersion < newVersion) {
            clearOldData(db);
            onCreate(db);
            return;
        }

        /* Upgrade DB using SQL scripts place at assets directory
         * format filename : Solo.db_from_{oldVersion}_to_{newVersion}.sql
         * example : Solo.db_from_14_to_15.sql
         */
        try {
            for (int i = oldVersion; i < newVersion; i++) {
                String migrationName = String.format("ByonchatVideoTube.db_from_%d_to_%d.sql", i, (i + 1));
                readAndExecSQL(db, context, migrationName);
            }

        } catch (Exception e) {
        }
    }

    private void readAndExecSQL(SQLiteDatabase db, Context context, String migrationName) {
        if (TextUtils.isEmpty(migrationName)) {
            return;
        }

        AssetManager assets = context.getAssets();
        BufferedReader reader = null;

        try {
            InputStream inputStream = assets.open(migrationName);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);
            execSQLScript(db, reader);
        } catch (IOException e) {
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private void execSQLScript(SQLiteDatabase db, BufferedReader reader) throws IOException {
        db.beginTransaction();
        try {
            String line;
            StringBuilder statement = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                statement.append(line);
                statement.append(System.getProperty("line.separator"));
                if (line.endsWith(";")) {
                    db.execSQL(statement.toString());
                    statement = new StringBuilder();
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void clearOldData(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL("DROP TABLE IF EXISTS " + Video.VideoTubeTable.TABLE_NAME);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}

