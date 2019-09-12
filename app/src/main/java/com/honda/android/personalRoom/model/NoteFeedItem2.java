package com.honda.android.personalRoom.model;

/**
 * Created by lukma on 3/7/2016.
 */
public class NoteFeedItem2 {
    private String userid;
    private int parentID, level;
    private String id, id_comment, name, status, image, profilePic, timeStamp, url, jumlahLove, jumlahNix, jumlahComment, name2, comment2;

    public NoteFeedItem2() {
    }

    public NoteFeedItem2(int level, String userid, String id, String id_comment, String name, String image, String status,
                        String profilePic, String timeStamp, String url, String jumlahLove, String jumlahNix, String jumlahComment, String name2, String comment2, int parentID) {
        super();
        this.level = level;
        this.userid = userid;
        this.id = id;
        this.name = name;
        this.image = image;
        this.status = status;
        this.profilePic = profilePic;
        this.timeStamp = timeStamp;
        this.url = url;
        this.jumlahLove = jumlahLove;
        this.jumlahNix = jumlahNix;
        this.jumlahComment = jumlahComment;
        this.name2 = name2;
        this.id_comment = id_comment;
        this.comment2 = comment2;
        this.parentID = parentID;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_comment() {
        return id_comment;
    }

    public void setId_comment(String id_comment) {
        this.id_comment = id_comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImge() {
        return image;
    }

    public void setImge(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getJumlahLove() {
        return jumlahLove;
    }

    public void setJumlahLove(String jumlahLove) {
        this.jumlahLove = jumlahLove;
    }

    public String getJumlahNix() {
        return jumlahNix;
    }

    public void setJumlahNix(String jumlahNix) {
        this.jumlahNix = jumlahNix;
    }

    public String getJumlahComment() {
        return jumlahComment;
    }

    public void setJumlahComment(String jumlahComment) {
        this.jumlahComment = jumlahComment;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getComment2() {
        return comment2;
    }

    public void setComment2(String comment2) {
        this.comment2 = comment2;
    }

    public int getParentID() {
        return parentID;
    }

    public void setParentID(int parentID) {
        this.parentID = parentID;
    }
}
