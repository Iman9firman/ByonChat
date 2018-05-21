package com.byonchat.android.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;

import com.byonchat.android.provider.ChatParty;
import com.byonchat.android.provider.Data;

public class Group implements Data, ChatParty {
    public static final String STATUS_OWNER = "owner";
    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_INACTIVE = "inactive";

    public static final String TABLE_NAME = "groups";
    public static final String NAME = "name";
    public static final String STATUS = "status";
    public static final String JABBER_ID = "jid";

    private static String[] fieldNames;

    static {
        fieldNames = new String[4];
        fieldNames[0] = NAME;
        fieldNames[1] = JABBER_ID;
        fieldNames[2] = STATUS;
    }

    private long id;
    private String name;
    private String jabberId;
    private String status;

    public Group() {
        id = 0;
    }

    public Group(Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex(ID));
        name = cursor.getString(cursor.getColumnIndex(NAME));
        jabberId = cursor.getString(cursor.getColumnIndex(JABBER_ID));
        status = cursor.getString(cursor.getColumnIndex(STATUS));
    }

    public Group(Parcel parcel) {
        id = parcel.readLong();
        name = parcel.readString();
        jabberId = parcel.readString();
        status = parcel.readString();
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public Group(String name, String jabberId, String status) {
        this.name = name;
        this.status = status;
        this.jabberId = jabberId;
    }

    public Group(long id, String name, String jabberId, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.jabberId = jabberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getJabberId() {
        return jabberId;
    }

    public void setJabberId(String jabberId) {
        this.jabberId = jabberId;
    }

    @Override
    public String[] getFieldNames() {
        return fieldNames;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(NAME, getName());
        cv.put(STATUS, getStatus());
        cv.put(JABBER_ID, getJabberId());
        if (id > 0) {
            cv.put(ID, getId());
        }
        return cv;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(getId());
        parcel.writeString(getName());
        parcel.writeString(getJabberId());
        parcel.writeString(getStatus());

    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

}
