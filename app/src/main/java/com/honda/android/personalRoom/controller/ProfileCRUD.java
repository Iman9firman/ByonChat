package com.honda.android.personalRoom.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.honda.android.personalRoom.database.PRDatabaseHelper;
import com.honda.android.personalRoom.database.PRDatabaseObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by lukma on 4/7/2016.
 */
public class ProfileCRUD extends PRDatabaseObject {
    private static final String WHERE_ID_EQUALS = PRDatabaseHelper.KOLOM_ID + " =?";

    public ProfileCRUD(Context context){
        super(context);
    }

//    public long save(Profile profile){
//        ContentValues values = new ContentValues();
//        values.put(PRDatabaseHelper.KOLOM_USERID, profile.getUserid());
//        Log.d("dob", profile.getName() + "");
//        values.put(PRDatabaseHelper.KOLOM_NAME, profile.getName());
//        values.put(PRDatabaseHelper.KOLOM_HASHTAG, profile.getHashtag());
//
//        return database.insert(PRDatabaseHelper.TABEL_PROFILE, null, values);
//    }
    public long save(Profile profile) {
        ContentValues values = new ContentValues();
        values.put(PRDatabaseHelper.KOLOM_USERID, profile.getUserid());
        Log.d("dob", profile.getName() + "");
        values.put(PRDatabaseHelper.KOLOM_NAME, profile.getName());
        values.put(PRDatabaseHelper.KOLOM_STATUS, profile.getStatus());
        values.put(PRDatabaseHelper.KOLOM_HASHTAG, profile.getHashtag());

        return database.insert(PRDatabaseHelper.TABEL_PROFILE, null, values);
    }
    public long update(Profile profile){
        ContentValues values = new ContentValues();
        values.put(PRDatabaseHelper.KOLOM_USERID, profile.getUserid());
        values.put(PRDatabaseHelper.KOLOM_NAME, profile.getName());
        values.put(PRDatabaseHelper.KOLOM_STATUS, profile.getStatus());
        values.put(PRDatabaseHelper.KOLOM_HASHTAG, profile.getHashtag());

        long result = database.update(PRDatabaseHelper.TABEL_PROFILE, values, WHERE_ID_EQUALS, new String[] { String.valueOf(profile.getId()) });
        Log.d("Update Result:", "=" + result);
        return result;
    }

    public int delete(Profile profile){
        return database.delete(PRDatabaseHelper.TABEL_PROFILE, WHERE_ID_EQUALS, new String[] { profile.getId() + ""});
    }

    public ArrayList<Profile> getProfile(){
        ArrayList<Profile> profiles = new ArrayList<>();

        Cursor cursor = database.query(PRDatabaseHelper.TABEL_PROFILE,
                new String[] { PRDatabaseHelper.KOLOM_ID,
                PRDatabaseHelper.KOLOM_USERID,
                PRDatabaseHelper.KOLOM_NAME,
                PRDatabaseHelper.KOLOM_STATUS,
                PRDatabaseHelper.KOLOM_HASHTAG }, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            Profile profile = new Profile();
            profile.setId(cursor.getInt(0));
            profile.setUserid(cursor.getString(1));
            profile.setName(cursor.getString(2));
            profile.setStatus(cursor.getString(3));
            profile.setHashtag(cursor.getString(4));
            profiles.add(profile);
        }
        return profiles;
    }

    public Profile getProfile(long id){
        Profile profile = null;

        String sql = "SELECT * FROM " + PRDatabaseHelper.TABEL_PROFILE + " WHERE " + PRDatabaseHelper.KOLOM_ID + " = ?";

        Cursor cursor = database.rawQuery(sql, new String[] { id + ""});

        if(cursor.moveToNext()){
            profile = new Profile();
            profile.setId(cursor.getInt(0));
            profile.setUserid(cursor.getString(1));
            profile.setName(cursor.getString(2));
            profile.setStatus(cursor.getString(3));
            profile.setHashtag(cursor.getString(4));
        }
        return profile;
    }
}
