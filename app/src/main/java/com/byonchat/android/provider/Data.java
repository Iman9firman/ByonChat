package com.byonchat.android.provider;

import android.content.ContentValues;
import android.os.Parcelable;

public interface Data extends Parcelable {
    public static final String ID = "_id";

    public long getId();

    public void setId(long id);

    public String getTableName();

    public String[] getFieldNames();

    public ContentValues getContentValues();
}
