package com.byonchat.android.personalRoom.database;

/**
 * Created by Lukmanpryg on 12/1/2016.
 */

public class Profile {
    String userid, name, status, cover, hashtag, hashtag1, hashtag2, hashtag3, hashtag4, hashtag5, hashtag6, hashtag7, hashtag8;

    public Profile(String userid, String name, String status, String cover, String hashtag){
        this.userid = userid;
        this.name = name;
        this.status = status;
        this.cover = cover;
        this.hashtag = hashtag;
    }

    public Profile(String userid, String hashtag1, String hashtag2, String hashtag3, String hashtag4, String hashtag5, String hashtag6, String hashtag7, String hashtag8){
        this.userid = userid;
        this.hashtag1 = hashtag1;
        this.hashtag2 = hashtag2;
        this.hashtag3 = hashtag3;
        this.hashtag4 = hashtag4;
        this.hashtag5 = hashtag5;
        this.hashtag6 = hashtag6;
        this.hashtag7 = hashtag7;
        this.hashtag8 = hashtag8;
    }

    public void setUserid(String userid){
        this.userid = userid;
    }

    public String getUserid(){
        return userid;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String getStatus(){
        return status;
    }

    public String getCover(){
        return cover;
    }

    public void setCover(String cover){
        this.cover = cover;
    }

    public void setHashtag(String hashtag){
        this.hashtag = hashtag;
    }

    public String getHashtag(){
        return hashtag;
    }
    public void setHashtag1(String hashtag1){
        this.hashtag1 = hashtag1;
    }

    public String getHashtag1(){
        return hashtag1;
    }
    public void setHashtag2(String hashtag2){
        this.hashtag2 = hashtag2;
    }

    public String getHashtag2(){
        return hashtag2;
    }
    public void setHashtag3(String hashtag3){
        this.hashtag3 = hashtag3;
    }

    public String getHashtag3(){
        return hashtag3;
    }
    public void setHashtag4(String hashtag4){
        this.hashtag4 = hashtag4;
    }

    public String getHashtag4(){
        return hashtag4;
    }
    public void setHashtag5(String hashtag5){
        this.hashtag5 = hashtag5;
    }

    public String getHashtag5(){
        return hashtag5;
    }
    public void setHashtag6(String hashtag6){
        this.hashtag6 = hashtag6;
    }

    public String getHashtag6(){
        return hashtag6;
    }
    public void setHashtag7(String hashtag7){
        this.hashtag7 = hashtag7;
    }

    public String getHashtag7(){
        return hashtag7;
    }
    public void setHashtag8(String hashtag8){
        this.hashtag8 = hashtag8;
    }

    public String getHashtag8(){
        return hashtag8;
    }

}
