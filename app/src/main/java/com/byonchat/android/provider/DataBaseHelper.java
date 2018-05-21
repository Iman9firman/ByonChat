package com.byonchat.android.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "assign.db";
    public static final int DATABASE_VERSION = 3;

    private static DataBaseHelper instance;
    private Context context;
    private SQLiteDatabase database;

    private DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public synchronized static DataBaseHelper getInstance(
            Context context) {
        if (instance == null) {
            instance = new DataBaseHelper(
                    context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqliteDatabase) {
        if (database == null) {
            database = sqliteDatabase;
        }

        String CREATE_TABLE_NEW_USER = "CREATE TABLE IF NOT EXISTS room (idRoom TEXT,lokasi TEXT,jabatan TEXT, divisi TEXT,nik TEXT,no_tlpn TEXT,nama TEXT,idTab TEXT)";
        database.execSQL(CREATE_TABLE_NEW_USER);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {

            String CREATE_TABLE_NEW_USER = "CREATE TABLE IF NOT EXISTS room (idRoom TEXT,lokasi TEXT,jabatan TEXT, divisi TEXT,nik TEXT,no_tlpn TEXT,nama TEXT,idTab TEXT)";
            database.execSQL(CREATE_TABLE_NEW_USER);

            db.execSQL("ALTER TABLE room ADD COLUMN idTab text");

            //String CREATE_TABLE_NEW_USER = "CREATE TABLE IF NOT EXISTS room (idRoom TEXT,lokasi TEXT,jabatan TEXT, divisi TEXT,nik TEXT,no_tlpn TEXT,nama TEXT)";
            //db.execSQL(CREATE_TABLE_NEW_USER);
        }
    }

    private SQLiteDatabase getDatabase() {
        if (database == null) {
            database = getWritableDatabase();
        }
        return database;
    }


    public void deleteAllrow(String tableName,String idRoom,String idTab) {
        getDatabase().execSQL("delete from " + tableName +" where idRoom = '"+idRoom+"' and idTab = '"+idTab+"'");
    }

    public Cursor selectAll(String tableName,String idRoom,String idTab) {
        return getDatabase().rawQuery("Select * FROM " + tableName+" where idRoom = '"+idRoom+"'"+" and idTab = '"+idTab+"'", null);
    }

    public Boolean checkTable(String tableName) {
        boolean hasil = true;
        Cursor cursor = getDatabase().rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
        if (cursor != null) {
            if (cursor.getCount() == 0) {
                hasil = false;
            }
        }

        cursor.close();
        return hasil;
    }

    public void createUserTable(String tabName, String value,String username,String idTab) {

        Cursor cursor = getDatabase().rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tabName + "'", null);
        if (cursor != null) {
            if (cursor.getCount() == 0) {
                String CREATE_TABLE_NEW_USER = "CREATE TABLE IF NOT EXISTS " + tabName + " (idRoom TEXT,lokasi TEXT,jabatan TEXT, divisi TEXT,nik TEXT,no_tlpn TEXT,nama TEXT,idTab TEXT)";
                getDatabase().execSQL(CREATE_TABLE_NEW_USER);
            }
        }

        cursor.close();

        Cursor cc = selectAll(tabName,username,idTab);

        if (cc.getCount() > 0) {
            deleteAllrow(tabName,username,idTab);
        }


        try {
            JSONObject sajojo = new JSONObject(value);
            JSONArray alah = sajojo.getJSONArray("data");

            for (int u = 0; u < alah.length(); u++) {
                String id = alah.getJSONObject(u).getString("id");
                String name = alah.getJSONObject(u).getString("name");
                String nik = alah.getJSONObject(u).getString("nik");
                String telp_number = alah.getJSONObject(u).getString("telp_number");
                String divisi = alah.getJSONObject(u).getString("divisi");
                String jabatan = alah.getJSONObject(u).getString("jabatan");
                String lokasi = alah.getJSONObject(u).getString("lokasi");

                ContentValues values = new ContentValues();
                values.put("idTab", idTab);
                values.put("idRoom", username);
                values.put("jabatan", jabatan);
                values.put("divisi", divisi);
                values.put("nik", nik);
                values.put("no_tlpn", telp_number);
                values.put("lokasi", lokasi);
                values.put("nama", name);

                getDatabase().insert(tabName, null, values);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


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


    public void destroy() {
        instance.close();
        instance = null;
    }
}
