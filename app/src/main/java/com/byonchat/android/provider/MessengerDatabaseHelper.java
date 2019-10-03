package com.byonchat.android.provider;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.byonchat.android.R;

import java.util.ArrayList;

public class MessengerDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "messenger.db";
    public static final int DATABASE_VERSION = 2;

    private static MessengerDatabaseHelper instance;
    private Context context;
    private SQLiteDatabase database;
    private Contact myContact;

    private MessengerDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public synchronized static MessengerDatabaseHelper getInstance(
            Context context) {
        if (instance == null) {
            instance = new MessengerDatabaseHelper(
                    context);
        }
        return instance;
    }

    public Group getGroup(String jabberId) {
        Group group = null;
        Cursor cursor = instance.query(
                context.getString(R.string.sql_group_by_jid),
                new String[] { jabberId });
        if (cursor.moveToNext()) {
            group = new Group(cursor);
        }
        cursor.close();
        return group;
    }

    public String[] getAllJabberId(){
        Cursor cursor = instance.query(context.getString(R.string.sql_all_jid),null);
        cursor.moveToFirst();
        ArrayList<String> jid = new ArrayList<String>();

        while(!cursor.isAfterLast()){
            jid.add(cursor.getString(cursor.getColumnIndex("jid")));
            cursor.moveToNext();
        }

        cursor.close();
        return jid.toArray(new String[jid.size()]);
    }

    public ArrayList<Contact> getContactCount() throws SQLException {
        ArrayList<Contact> contact = new ArrayList<Contact>();
        Cursor cursor = instance.query(context.getString(R.string.sql_all_jid), null);
        if (cursor.moveToFirst()) {
            do {
                String jid = cursor.getString(cursor.getColumnIndex(Contact.JABBER_ID));
                String nam = cursor.getString(cursor.getColumnIndex(Contact.NAME));
                String sta = cursor.getString(cursor.getColumnIndex(Contact.STATUS));
                contact.add(new Contact(nam,jid,sta));
            } while (cursor.moveToNext());
        }
        return contact;
    }

    public Contact getContact(String jabberId) {
        Contact contact = null;
        Cursor cursor = instance.query(
                context.getString(R.string.sql_contact_by_jid),
                new String[] { jabberId });
        if (cursor.moveToNext()) {
            contact = new Contact(cursor);
        }
        cursor.close();
        return contact;
    }

    public boolean getContactCount(String jabberId) {
        boolean contact = false;
        Cursor cursor = instance.query(
                context.getString(R.string.sql_contact_by_jid),
                new String[] { jabberId });
        if (cursor.getCount()>0) {
            contact = true;
        }
        cursor.close();
        return contact;
    }

    public Contact getMyContact() {
        if (myContact == null) {
            Cursor cursor = instance.query("SELECT * FROM "
                    + Contact.TABLE_NAME + " WHERE _id=1", null);
            if (cursor.moveToNext()) {
                myContact = new Contact(cursor);

            }
            cursor.close();

        }
        return myContact;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
     * .SQLiteDatabase)
     */
    @Override
    public void onCreate(SQLiteDatabase sqliteDatabase) {
        if (database == null) {
            database = sqliteDatabase;
        }

        createTable();
    }

    private void createTable() {
        getDatabase()
                .execSQL(context.getString(R.string.sql_createtable_group));
        getDatabase().execSQL(
                context.getString(R.string.sql_createtable_group_index));
        getDatabase().execSQL(
                context.getString(R.string.sql_createtable_contact));
        getDatabase().execSQL(
                context.getString(R.string.sql_createtable_contact_index));
        getDatabase().execSQL(
                context.getString(R.string.sql_createtable_conversation));
        getDatabase()
                .execSQL(
                        context.getString(R.string.sql_createtable_conversation_index_destination));
        getDatabase()
                .execSQL(
                        context.getString(R.string.sql_createtable_conversation_index_packetid));
        getDatabase()
                .execSQL(
                        context.getString(R.string.sql_createtable_conversation_index_source));
        getDatabase().execSQL(
                context.getString(R.string.sql_createtable_configurations));

        getDatabase().execSQL(
                context.getString(R.string.sql_insert_configuration),
                new String[] { Configuration.LAST_CONTACT_REFRESHED, "0" });
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
     * .SQLiteDatabase, int, int)
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < newVersion){
            db.execSQL(context.getString(R.string.sql_altertable_contact));
        }
    }

    private SQLiteDatabase getDatabase() {
        if (database == null) {
            database = getWritableDatabase();
        }
        return database;
    }

    public void insertData(Data data) {
        long id = getDatabase().insert(data.getTableName(),
                data.getFieldNames()[0], data.getContentValues());
        if (id != -1) {
            data.setId(id);
        }
    }

    public void updateData(Data data) {
        getDatabase().update(data.getTableName(), data.getContentValues(),
                "_id=?", new String[] { String.valueOf(data.getId()) });
    }

    public void deleteData(Data data) {
        if(data.getTableName().equalsIgnoreCase(Message.TABLE_NAME)){
            FilesURLDatabaseHelper fdb=  new FilesURLDatabaseHelper(context);
            fdb.open();
            fdb.deleteFile(String.valueOf(data.getId()));
            fdb.close();
        }
        getDatabase().delete(data.getTableName(), "_id=?",
                new String[] { String.valueOf(data.getId()) });
    }

    public int deleteRows(String tableName, String whereCond, String[] args) {
        if(tableName.equalsIgnoreCase(Message.TABLE_NAME)){
            String SQL_SELECT_MESSAGES = "SELECT _id FROM "
                    + Message.TABLE_NAME + " WHERE " + Message.DESTINATION + "=? OR "
                    + Message.SOURCE+ "=?" ;
            Cursor cursor = query(SQL_SELECT_MESSAGES, args);
            if(cursor.getCount()>0) {
                FilesURLDatabaseHelper fdb=  new FilesURLDatabaseHelper(context);
                fdb.open();
                while (cursor.moveToNext()) {
                    fdb.deleteFile(cursor.getString(cursor.getColumnIndex("_id")));
                }
                fdb.close();
            }
            cursor.close();
        }
        return getDatabase().delete(tableName, whereCond, args);
    }

    public Cursor selectAll(String tableName) {
        return getDatabase().rawQuery("Select * FROM " + tableName, null);
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
