package com.byonchat.android.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.byonchat.android.utils.Utility;

import java.util.ArrayList;

public class IntervalDB {
//id 1 untuk interval
//id 2 untuk contact OS
//id 3 untuk key
//id 4 untuk skin
//id 5 untuk splash
//id 6 untuk loading screen
//id 7 untuk interval 1 day
//id 8 untuk show banner
//id 9 untuk playRadio
//id 10 untuk request key
//id 11 untuk cek change contact
//id 12 untuk register
//id 13 untuk upload profile
//id 14 untuk iklan date
//id 15 untuk cek update per hari
//id 16 save update
//id 17 kirim count mesage ke server per hari
//id 19 untuk refreshContact
//id 20 untuk latLong
//id 21 untuk squence Image
//id 22 untuk squence Video
//id 23 untuk squence cekDaftarSMS
//id 24 untuk squence chatOff
//id 25 untuk squence limitBukaRoom
//id 26 untuk squence limitBukaRoomISS
//id 27 untuk on/off ShareLocation (Reliever ISS)
//id 28 untuk string username / NIK ISS
//id 29 untuk string password ISS

	private static final String DB_NAME		= "interval_db";
	private static final int	DB_VER		= 3;
	public static final String TABLE_NAME	= "interval";
	public static final String TABLE_NAME_SKIN	= "skin";
	public static final String COL_ID		= "_id";
	public static final String COL_TIME	= "time";
	public static final String COL_TITLE	= "title";
	public static final String COL_DESC	= "desc";
	public static final String COL_COLOR	= "color";
	public static final String COL_LOGO	= "logo";
	public static final String COL_LOGO_HEADER	= "logo_header";
	public static final String COL_BACKGROUND	= "background";

	private static final String TAG			= "INTERVAL";
	private DatabaseHelper		dbHelper;
	private SQLiteDatabase db;

	private static final String DB_CREATE	= "create table interval (_id integer primary key, time text);";
	private static final String DB_CREATE_SKIN	= "create table skin (_id integer primary key autoincrement, title text not null unique,desc text,color text, logo  blob ,logo_header  blob ,background  blob);";

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
			db.execSQL(DB_CREATE_SKIN);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			// TODO Auto-generated method stub
			Log.d(TAG, "upgrade DB");
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_SKIN);
			onCreate(db);

		}

	}

	public IntervalDB(Context context)
	{
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	public IntervalDB open() throws SQLException
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

   /* public void alter(){
        db.execSQL("ALTER TABLE " + TABLE_NAME_SKIN + " ADD COLUMN desc text;");
    };
*/
	public void createContact(Interval interval)
	{
		ContentValues val = new ContentValues();
		val.put(COL_ID, interval.getId());
		val.put(COL_TIME, interval.getTime());
		db.insert(TABLE_NAME, null, val);
	}

	public boolean deleteContact(long id)
	{
		return db.delete(TABLE_NAME, COL_ID + "=" + id, null) > 0;
	}

    public void updateContact(long id,String text) {
        ContentValues cv = new ContentValues();
        cv.put(COL_TIME, text);
        db.update(TABLE_NAME, cv, COL_ID + "= " + String.valueOf(id),null);

    }

	public Cursor getSingleContact(long id)
	{
		Cursor cursor = db.query(TABLE_NAME, new String[]
		{
				COL_ID,COL_TIME
		},COL_ID + " = " + id, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		return cursor;
	}

    public void createSkin(Skin skin)
    {
        ContentValues cv = new ContentValues();
        cv.put(COL_TITLE, skin.getTitle());
        cv.put(COL_DESC,skin.getDesc());
        cv.put(COL_COLOR,skin.getColor());
        cv.put(COL_LOGO, Utility.getBytes(skin.getLogo()));
        cv.put(COL_LOGO_HEADER, Utility.getBytes(skin.getLogo_header()));
        cv.put(COL_BACKGROUND, Utility.getBytes(skin.getBackground()));
        db.insert(TABLE_NAME_SKIN, null, cv);
    }

    public void update(Skin skin)
    {
        ContentValues cv = new ContentValues();
        cv.put(COL_TITLE, skin.getTitle());
        cv.put(COL_DESC,skin.getDesc());
        cv.put(COL_COLOR,skin.getColor());
        cv.put(COL_LOGO, Utility.getBytes(skin.getLogo()));
        cv.put(COL_LOGO_HEADER, Utility.getBytes(skin.getLogo_header()));
        cv.put(COL_BACKGROUND, Utility.getBytes(skin.getBackground()));
        db.update(TABLE_NAME_SKIN, cv, COL_TITLE +"= '"+skin.getTitle()+"'", null);
    }

    public boolean deleteSkin(String title)
    {
        return db.delete(TABLE_NAME_SKIN, COL_TITLE + "='" + title+"'", null) > 0;
    }

    public Cursor getCountSkin()
    {
        Cursor cursor = db.query(TABLE_NAME_SKIN, new String[]
                {
                        COL_TITLE,COL_DESC,COL_COLOR,COL_LOGO,
                        COL_BACKGROUND
                },null, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }


    public Skin retriveSkinDetails(String skin) throws SQLException {
        Cursor cur = db.query(true, TABLE_NAME_SKIN, new String[] { COL_TITLE,COL_DESC,COL_COLOR,COL_LOGO,COL_LOGO_HEADER,
                COL_BACKGROUND }, COL_TITLE+ "='" + skin+"'", null, null, null, null, null);
        if (cur.moveToFirst()) {
            byte[] blobLogo = cur.getBlob(cur.getColumnIndex(COL_LOGO));
            byte[] blobLogoHeader = cur.getBlob(cur.getColumnIndex(COL_LOGO_HEADER));
            byte[] blobBack = cur.getBlob(cur.getColumnIndex(COL_BACKGROUND));
            String title = cur.getString(cur.getColumnIndex(COL_TITLE));
            String desc = cur.getString(cur.getColumnIndex(COL_DESC));
            String color = cur.getString(cur.getColumnIndex(COL_COLOR));
            cur.close();
            return new Skin(title,desc,color,Utility.getPhoto(blobLogo),Utility.getPhoto(blobLogoHeader),Utility.getPhoto(blobBack));
        }
        cur.close();
        return null;
    }


    // To get list of employee details
    public ArrayList<Skin> retriveallSkin() throws SQLException {
        ArrayList<Skin> skinArrayList = new ArrayList<Skin>();
        Cursor cur = db.query(true, TABLE_NAME_SKIN, new String[] { COL_TITLE,COL_DESC,COL_COLOR,COL_LOGO,COL_LOGO_HEADER,
                COL_BACKGROUND },null, null, null, null, null, null);
        if (cur.moveToFirst()) {
            do {
                byte[] blobLogo = cur.getBlob(cur.getColumnIndex(COL_LOGO));
                byte[] blobLogoHeader = cur.getBlob(cur.getColumnIndex(COL_LOGO_HEADER));
                byte[] blobBack = cur.getBlob(cur.getColumnIndex(COL_BACKGROUND));
                String title = cur.getString(cur.getColumnIndex(COL_TITLE));
                String color = cur.getString(cur.getColumnIndex(COL_COLOR));
                String desc = cur.getString(cur.getColumnIndex(COL_DESC));
                skinArrayList
                        .add(new Skin(title,desc,color,Utility.getPhoto(blobLogo),Utility.getPhoto(blobLogoHeader),Utility.getPhoto(blobBack)));
            } while (cur.moveToNext());
        }
        return skinArrayList;
    }
    // To get list of employee details
    public ArrayList<String> retriveallSkinTitle() throws SQLException {
        ArrayList<String> skinArrayList = new ArrayList<String>();
        Cursor cur = db.query(true, TABLE_NAME_SKIN, new String[] { COL_TITLE},null, null, null, null, null, null);
        if (cur.moveToFirst()) {
            do {
                String title = cur.getString(cur.getColumnIndex(COL_TITLE));
                skinArrayList.add(title);
            } while (cur.moveToNext());
        }
        return skinArrayList;
    }
}
