package com.byonchat.android.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by byonc on 5/4/2017.
 */

public class ModelPicture implements Parcelable {

    String myuserid, userid, id_photo, title, description, url,url_thumb, tgl_upload, duration, flag,color;
    Integer drawable;
    Boolean selected;

    public ModelPicture() {

    }

    protected ModelPicture(Parcel in) {
        myuserid = in.readString();
        userid = in.readString();
        id_photo = in.readString();
        title = in.readString();
        description = in.readString();
        url = in.readString();
        url_thumb = in.readString();
        tgl_upload = in.readString();
        duration = in.readString();
        drawable = in.readInt();
        flag = in.readString();
        color = in.readString();
    }

    public static final Creator<ModelPicture> CREATOR = new Creator<ModelPicture>() {
        @Override
        public ModelPicture createFromParcel(Parcel in) {
            return new ModelPicture(in);
        }

        @Override
        public ModelPicture[] newArray(int size) {
            return new ModelPicture[size];
        }
    };

    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getUrl_thumb() {
        return url_thumb;
    }

    public void setUrl_thumb(String url_thumb) {
        this.url_thumb = url_thumb;
    }


    public String getMyuserid() {
        return myuserid;
    }

    public void setMyuserid(String myuserid) {
        this.myuserid = myuserid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }


    public String getId_photo() {
        return id_photo;
    }

    public void setId_photo(String id_photo) {
        this.id_photo = id_photo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTgl_upload() {
        return tgl_upload;
    }

    public void setTgl_upload(String tgl_upload) {
        this.tgl_upload = tgl_upload;
    }

    public String getDuration(){
        return duration;
    }

    public void setDuration(String duration){
        this.duration = duration;
    }

    public Integer getDrawable(){
        return drawable;
    }

    public void setDrawable(Integer drawable){
        this.drawable = drawable;
    }

    public String getFlag(){
        return flag;
    }

    public void setFlag(String flag){
        this.flag = flag;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(myuserid);
        dest.writeString(userid);
        dest.writeString(id_photo);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(url);
        dest.writeString(url_thumb);
        dest.writeString(tgl_upload);
        dest.writeString(duration);
        dest.writeInt(drawable);
        dest.writeString(flag);
        dest.writeString(color);
    }
}
