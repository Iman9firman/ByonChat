package com.byonchat.android.provider;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.DecimalFormat;

public class Contact implements Data, ChatParty {
    public static final String TABLE_NAME = "contacts";
    public static final String NAME = "name";
    public static final String REALNAME = "realname";
    public static final String STATUS = "status";
    public static final String JABBER_ID = "jid";
    public static final String ADDRBOOK_ID = "addrbook_id";
    public static final String POINT = "point";
    public static final String GENDER = "gender";
    public static final String BIRTHDATE = "birthdate";
    public static final String EMAIL = "email";
    public static final String FACEBOOKID = "facebookId";//disini buat simpan signature pofile picture
    public static final String CITY = "city";
    public static final String CHANGE_PROFILE = "cp";

    // public static final String TYPE = "type";

    private static String[] fieldNames;

    static {
        fieldNames = new String[12];
        fieldNames[0] = NAME;
        fieldNames[1] = JABBER_ID;
        fieldNames[2] = STATUS;
        fieldNames[3] = ADDRBOOK_ID;
        fieldNames[4] = POINT;
        fieldNames[5] = REALNAME;
        fieldNames[6] = GENDER;
        fieldNames[7] = BIRTHDATE;
        fieldNames[8] = EMAIL;
        fieldNames[9] = FACEBOOKID;
        fieldNames[10] = CITY;
        fieldNames[11] = CHANGE_PROFILE;
    }

    private long id;
    private String name;
    private String realname;
    private String jabberId;
    private String status;
    private String addrbookId;
    private String point;
    private String formaterPoint;
    private String gender;
    private String birthdate;
    private String email;
    private String facebookid;
    private String city;
    private int changeProfile;



    public Contact() {
        id = 0;
    }

    public Contact(Parcel parcel) {
        id = parcel.readLong();
        name = parcel.readString();
        realname = parcel.readString();
        jabberId = parcel.readString();
        status = parcel.readString();
        addrbookId = parcel.readString();
        point = parcel.readString();
        gender = parcel.readString();
        birthdate = parcel.readString();
        email = parcel.readString();
        facebookid = parcel.readString();
        city = parcel.readString();
        changeProfile = parcel.readInt();

    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public Contact(Cursor cursor) {
        this.id = cursor.getLong(cursor.getColumnIndex(Contact.ID));
        this.name = cursor.getString(cursor.getColumnIndex(Contact.NAME));
        this.realname = cursor.getString(cursor.getColumnIndex(Contact.REALNAME));
        this.status = cursor.getString(cursor.getColumnIndex(Contact.STATUS));
        this.jabberId = cursor.getString(cursor
                .getColumnIndex(Contact.JABBER_ID));
        this.addrbookId = cursor.getString(cursor
                .getColumnIndex(Contact.ADDRBOOK_ID));
        this.point = cursor.getString(cursor.getColumnIndex(Contact.POINT));
        this.gender = cursor.getString(cursor.getColumnIndex(Contact.GENDER));
        this.birthdate = cursor.getString(cursor.getColumnIndex(Contact.BIRTHDATE));
        this.email = cursor.getString(cursor.getColumnIndex(Contact.EMAIL));
        this.facebookid = cursor.getString(cursor.getColumnIndex(Contact.FACEBOOKID));
        this.city = cursor.getString(cursor.getColumnIndex(Contact.CITY));
        this.changeProfile = cursor.getInt(cursor.getColumnIndex(Contact.CHANGE_PROFILE));

    }

    public Contact(String name, String jabberId, String status) {
        this.name = name;
        this.status = status;
        this.jabberId = jabberId;
    }

    public Contact(long id, String name, String jabberId, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.jabberId = jabberId;
    }

    public Contact(long id, String name, String jabberId, String status,String realname) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.jabberId = jabberId;
        this.realname = realname;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAddrbookId(String addrbookId) {
        this.addrbookId = addrbookId;
    }

    public String getAddrbookId() {
        return addrbookId;
    }

    public String getJabberId() {
        return jabberId;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public void setJabberId(String jabberId) {
        this.jabberId = jabberId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }



    public String getFormaterPoint() {
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        formaterPoint = "0";
        if(point!=null){
            if(!point.equalsIgnoreCase("")){
                formaterPoint= formatter.format(Integer.valueOf(point != null ? point : "0"));
            }
        }

        return formaterPoint;
    }

    public String getFormaterPointInt() {
        DecimalFormat formatter = new DecimalFormat("#########");
        formaterPoint = "0";
        if(point!=null){
            if(!point.equalsIgnoreCase("")){
                formaterPoint= formatter.format(Integer.valueOf(point != null ? point : "0"));
            }
        }

        return formaterPoint;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFacebookid() {
        return facebookid;
    }

    public void setFacebookid(String facebookid) {
        this.facebookid = facebookid;
    }

    public int getChangeProfile() {
        return changeProfile;
    }

    public void setChangeProfile(int cp) {
        this.changeProfile = cp;
    }

    // public int getType() {
    // return type;
    // }
    //
    // public void setType(int type) {
    // this.type = type;
    // }

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
        cv.put(REALNAME, getRealname());
        cv.put(STATUS, getStatus());
        cv.put(JABBER_ID, getJabberId());
        cv.put(ADDRBOOK_ID, getAddrbookId());
        cv.put(POINT, getPoint());
        cv.put(GENDER, getGender());
        cv.put(BIRTHDATE, getBirthdate());
        cv.put(EMAIL, getEmail());
        cv.put(FACEBOOKID, getFacebookid());
        cv.put(CITY, getCity());
        cv.put(CHANGE_PROFILE, getChangeProfile());
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
        parcel.writeString(getRealname());
        parcel.writeString(getJabberId());
        parcel.writeString(getStatus());
        parcel.writeString(getAddrbookId());
        parcel.writeString(getPoint());
        parcel.writeString(getGender());
        parcel.writeString(getBirthdate());
        parcel.writeString(getEmail());
        parcel.writeString(getFacebookid());
        parcel.writeString(getCity());
        parcel.writeInt(getChangeProfile());
    }

    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    public static String parseJabberId(String fullJabberId) {
        String[] tmp = fullJabberId.split("@");
        return tmp[0];
    }

}
