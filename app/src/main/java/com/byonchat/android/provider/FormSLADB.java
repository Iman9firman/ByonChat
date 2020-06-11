package com.byonchat.android.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;
import android.util.Log;

import com.byonchat.android.R;
import com.byonchat.android.list.ItemListTrending;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Lukmanpryg on 6/29/2016.
 */

public class FormSLADB extends SQLiteOpenHelper {

    public static final String JJT = "JJT";
    public static final String JSON = "JSON";

    private SQLiteDatabase mDb;
    private static FormSLADB instance;

    private static final String DATABASE_NAME = "FormSLADB.db";
    private static final int DATABASE_VERSION = 1;
    private static final String ROOMS_TABLE = "form";


    private static final String CREATE_TABLE_SAVE_STRING = "create table "
            + ROOMS_TABLE + " (" + JJT
            + " text , "
            + JSON + " text)";

    private Context mCtx;

    @Override
    public void onCreate(SQLiteDatabase sqliteDatabase) {
        if (mDb == null) {
            mDb = sqliteDatabase;
        }

        createTable();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL(CREATE_TABLE_SAVE_STRING);
        }
    }

    private SQLiteDatabase getDatabase() {
        if (mDb == null) {
            mDb = getWritableDatabase();
        }
        return mDb;
    }

    private void createTable() {
        getDatabase().execSQL(CREATE_TABLE_SAVE_STRING);
    }

    public FormSLADB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mCtx = context;
    }

    public synchronized static FormSLADB getInstance(
            Context context) {
        if (instance == null) {
            instance = new FormSLADB(
                    context.getApplicationContext());
        }
        return instance;
    }

    public FormSLADB open() throws SQLException {
        mDb = getWritableDatabase();
        return this;
    }

    public void close() {
        mDb.close();
    }

    public void insertRooms(String _JJT, String _JSON) {
        ContentValues cv = new ContentValues();
        cv.put(JJT, _JJT);
        cv.put(JSON, _JSON);
        long inse = mDb.insert(ROOMS_TABLE, null, cv);
        Log.w("INSE", inse + "");
    }

    public boolean deleteFormSLA(String _JJT) {
        return mDb.delete(ROOMS_TABLE, JJT + "= '" + _JJT + "'", null) > 0;
    }

    public boolean deleteFormSLALL() {
        return mDb.delete(ROOMS_TABLE, null, null) > 0;
    }

    public ArrayList<String> getJJtJSONALL() throws SQLException {
        ArrayList<String> listMemberCards = new ArrayList<String>();
        Cursor cur = mDb.query(true, ROOMS_TABLE, new String[]{
                        JJT, JSON}
                , null, null, null, null, null, null);
        if (cur.moveToFirst()) {
            do {
                byte[] decodedBytes = Base64.decode(cur.getString(cur.getColumnIndex(JSON)), 0);
                String rr = "null";
                try {
                    rr = decompress(decodedBytes);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.w("INSE", e.getMessage() + "");
                }
                listMemberCards.add(rr);

            } while (cur.moveToNext());
        }
        return listMemberCards;
    }

    public ArrayList<String> getJJtJSON(String jjt) throws SQLException {
        ArrayList<String> listMemberCards = new ArrayList<String>();
        Cursor cur = mDb.query(true, ROOMS_TABLE, new String[]{
                        JJT, JSON}
                , JJT + "= '" + jjt + "'", null, null, null, null, null);
        if (cur.moveToFirst()) {
            do {
                byte[] decodedBytes = Base64.decode(cur.getString(cur.getColumnIndex(JSON)), 0);
                String rr = "null";
                try {
                    rr = decompress(decodedBytes);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.w("INSE", e.getMessage() + "");
                }
                listMemberCards.add(rr);

            } while (cur.moveToNext());
        }
        return listMemberCards;
    }

    public void insser(String jjs) {
        try {
            byte[] decodedBytes = Base64.decode(jjs, 0);
            String rr = decompress(decodedBytes);
            JSONArray jsonArray = new JSONArray(rr);
            for (int i = 0; i < jsonArray.length(); i++) {
                String kode = jsonArray.getJSONObject(i).getString("kode_jjt");
                JSONArray data = jsonArray.getJSONObject(i).getJSONArray("data");
                JSONObject form = new JSONObject();
                form.put("data", data);
                String contentForm = compress(form.toString());
                insertRooms(kode, contentForm);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.w("STAU1", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.w("STAU2", e.getMessage());
        }
    }

    public static String compress(String string) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream(string.length());
        GZIPOutputStream gos = new GZIPOutputStream(os);
        gos.write(string.getBytes());
        gos.close();
        byte[] compressed = os.toByteArray();
        os.close();
        String value = Base64.encodeToString(compressed, Base64.DEFAULT);
        return value;
    }


    public static String decompress(byte[] compressed) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
        GZIPInputStream gis = new GZIPInputStream(bis);
        BufferedReader br = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        gis.close();
        bis.close();
        return sb.toString();
    }


}