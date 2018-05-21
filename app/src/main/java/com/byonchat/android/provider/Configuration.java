package com.byonchat.android.provider;

import android.database.Cursor;

public class Configuration {
    public final static String TABLE_NAME = "configurations";
    public final static String LAST_CONTACT_REFRESHED = "last_contact_refreshed";
    public final static String FIELD_NAME = "name";
    public final static String FIELD_VALUE = "value";
    private static final String SQL_GET_VALUE = "SELECT * FROM " + TABLE_NAME
            + " WHERE name=? LIMIT 1;";
    private static final String SQL_SET_VALUE = "UPDATE configurations set value=?;";

    public static String getValue(MessengerDatabaseHelper dbHelper,
            String configName) {
        Cursor cursor = dbHelper.query(SQL_GET_VALUE,
                new String[] { configName });

        int indexValue = cursor.getColumnIndex(FIELD_VALUE);
        String res = "";
        while (cursor.moveToNext()) {
            res = cursor.getString(indexValue);
            break;
        }
        cursor.close();
        return res;
    }

    public static void setValue(MessengerDatabaseHelper dbHelper,
            String configName, String value) {
        dbHelper.execSql(SQL_SET_VALUE, new String[] { value });

    }
}
