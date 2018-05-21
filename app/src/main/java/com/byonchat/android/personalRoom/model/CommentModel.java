package com.byonchat.android.personalRoom.model;

/**
 * Created by Lukmanpryg on 5/12/2016.
 */
public class CommentModel {
    private String idRoomTab,id_note,headerColor, id_comment, myuserid, userid, profileName, profile_photo, jumlahLove, jumlahNix, jumlahComment, content_comment, timeStamp, parent_id, userLike, userDislike, name2, comment2;
    private int level;
    private Boolean flag;

    public CommentModel() {
    }

    public CommentModel(String id_note, String id_comment, String myuserid, String userid, String profileName, String profile_photo, String jumlahLove, String jumlahNix, String jumlahComment, String content_comment, String timeStamp, String parent_id, String userLike, String userDislike , String name2, String comment2, int level,String idRoomTab,String headerColor,Boolean flag) {
        super();
        this.id_note = id_note;
        this.id_comment = id_comment;
        this.myuserid = myuserid;
        this.userid = userid;
        this.profileName = profileName;
        this.profile_photo = profile_photo;
        this.jumlahLove = jumlahLove;
        this.jumlahNix = jumlahNix;
        this.jumlahComment = jumlahComment;
        this.content_comment = content_comment;
        this.timeStamp = timeStamp;
        this.parent_id = parent_id;
        this.userLike =
        this.name2 = name2;
        this.comment2 = comment2;
        this.level = level;
        this.idRoomTab = idRoomTab;
        this.headerColor = headerColor;
        this.flag = flag;
    }

    public String getHeaderColor() {
        return headerColor;
    }

    public void setHeaderColor(String headerColor) {
        this.headerColor = headerColor;
    }

    public String getIdRoomTab() {
        return idRoomTab;
    }

    public void setIdRoomTab(String idRoomTab) {
        this.idRoomTab = idRoomTab;
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

    public String getId_note() {
        return id_note;
    }

    public void setId_note(String id_note) {
        this.id_note = id_note;
    }

    public String getId_comment() {
        return id_comment;
    }

    public void setId_comment(String id_comment) {
        this.id_comment = id_comment;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
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

    public String getContent_comment() {
        return content_comment;
    }

    public void setContent_comment(String content_comment) {
        this.content_comment = content_comment;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getUserLike() {
        return userLike;
    }

    public void setUserLike(String userLike) {
        this.userLike = userLike;
    }

    public String getUserDislike() {
        return userDislike;
    }

    public void setUserDislike(String userDislike) {
        this.userDislike = userDislike;
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Boolean getFlag(){
        return flag;
    }

    public void setFlag(Boolean flag){
        this.flag = flag;
    }
}
