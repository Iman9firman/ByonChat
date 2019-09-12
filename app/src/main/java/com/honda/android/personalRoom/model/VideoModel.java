package com.honda.android.personalRoom.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lukma on 3/17/2016.
 */
public class VideoModel implements Parcelable {

    String myuserid, userid, id_video, title, description, url, thumbnail, tgl_upload, duration;

    public VideoModel() {

    }

    protected VideoModel(Parcel in) {
        myuserid = in.readString();
        userid = in.readString();
        id_video = in.readString();
        title = in.readString();
        description = in.readString();
        url = in.readString();
        thumbnail = in.readString();
        tgl_upload = in.readString();
        duration = in.readString();
    }

    public static final Creator<VideoModel> CREATOR = new Creator<VideoModel>() {
        @Override
        public VideoModel createFromParcel(Parcel in) {
            return new VideoModel(in);
        }

        @Override
        public VideoModel[] newArray(int size) {
            return new VideoModel[size];
        }
    };


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

    public String getId_video(){
        return id_video;
    }

    public void setId_video(String id_video){
        this.id_video = id_video;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnail(){
        return thumbnail;
    }

    public void setThumbnail(String thumbnail){
        this.thumbnail = thumbnail;
    }

    public String getTgl_upload(){
        return tgl_upload;
    }

    public void setTgl_upload(String tgl_upload){
        this.tgl_upload = tgl_upload;
    }

    public String getDuration(){
        return duration;
    }

    public void setDuration(String duration){
        this.duration = duration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(myuserid);
        dest.writeString(userid);
        dest.writeString(id_video);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(thumbnail);
        dest.writeString(url);
        dest.writeString(tgl_upload);
        dest.writeString(duration);
    }
}
