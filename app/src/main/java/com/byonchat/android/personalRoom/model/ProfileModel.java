package com.byonchat.android.personalRoom.model;

/**
 * Created by lukma on 3/22/2016.
 */
public class ProfileModel {
    public String userid, deskripsi, hashtag;

    public ProfileModel(){

    }

    public ProfileModel(String userid, String deskripsi, String hashtag){
        super();
        this.userid = userid;
        this.deskripsi = deskripsi;
        this.hashtag = hashtag;
    }

    public String getUserid(){
        return userid;
    }

    public void setUserid(String userid){
        this.userid = userid;
    }

    public String getDeskripsi(){
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi){
        this.deskripsi = deskripsi;
    }

    public String getHashtag(){
        return hashtag;
    }

    public void setHashtag(String hashtag){
        this.hashtag = hashtag;
    }
}
