package com.byonchat.android.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MkgServices implements Parcelable {
    public String id;
    public int position;
    public int header_id;
    public String header_name;
    public String child_name;
    public String child_distance;
    public String child_status;
    public String child_contact;
    public String child_location;
    public String child_rating;
    public boolean isApprove;
    public boolean isChecked;

    public MkgServices() {

    }

    public boolean isChecked() {
        return isChecked;
    }

    protected MkgServices(Parcel in) {
        id = in.readString();
        position = in.readInt();
        header_id = in.readInt();
        header_name = in.readString();
        child_name = in.readString();
        child_distance = in.readString();
        child_status = in.readString();
        child_contact = in.readString();
        child_location = in.readString();
        child_rating = in.readString();
        isApprove = in.readByte() != 0;
        isChecked = in.readByte() != 0;
    }

    public static final Creator<MkgServices> CREATOR = new Creator<MkgServices>() {
        @Override
        public MkgServices createFromParcel(Parcel in) {
            return new MkgServices(in);
        }

        @Override
        public MkgServices[] newArray(int size) {
            return new MkgServices[size];
        }
    };

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(position);
        dest.writeInt(header_id);
        dest.writeString(header_name);
        dest.writeString(child_name);
        dest.writeString(child_distance);
        dest.writeString(child_status);
        dest.writeString(child_contact);
        dest.writeString(child_location);
        dest.writeString(child_rating);
        dest.writeByte((byte) (isApprove ? 1 : 0));
        dest.writeByte((byte) (isChecked ? 1 : 0));
    }
}