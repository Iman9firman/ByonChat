package com.honda.android.personalRoom.controller;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lukma on 4/7/2016.
 */
public class Profile implements Parcelable{
    private int id;
    private String userid;
    private String name;
    private String status;
    private String hashtag;

    public Profile(){
        super();
    }

    private Profile(Parcel in) {
        super();
        this.id = in.readInt();
        this.userid = in.readString();
        this.name = in.readString();
        this.status = in.readString();
        this.hashtag = in.readString();
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getUserid(){
        return userid;
    }

    public void setUserid(String userid){
        this.userid = userid;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String getHashtag(){
        return hashtag;
    }

    public void setHashtag(String hashtag){
        this.hashtag = hashtag;
    }

    @Override
    public String toString() {
        return "Profile [id=" +id + ", userid=" + userid + ", name=" + name + ", status=" + status + ", hashtag=" + hashtag + "]";
    }

    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Profile other = (Profile) obj;
        if (id != other.id)
            return false;
        return true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(getId());
        parcel.writeString(getUserid());
        parcel.writeString(getName());
        parcel.writeString(getStatus());
        parcel.writeString(getHashtag());
    }

    public static final Creator<Profile> CREATOR = new Creator<Profile>() {
        public Profile createFromParcel(Parcel in) {
            return new Profile(in);
        }

        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };
}
