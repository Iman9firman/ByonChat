package com.honda.android.personalRoom.model;

/**
 * Created by lukma on 3/7/2016.
 */
public class NoteFeedItem {
    private int level;
    private String myuserid, userid,idRoomTab,colorHeader;
    private String id, parentID, name, status, image, profilePic, timeStamp, url, jumlahLove, jumlahNix, jumlahComment, userLike, userDislike, name2, comment2;
    private Boolean flag;

    public NoteFeedItem() {
    }

    public NoteFeedItem(int level, String myuserid, String userid, String id, String name, String image, String status,
                        String profilePic, String timeStamp, String url, String jumlahLove, String jumlahNix, String jumlahComment, String userLike, String userDislike, String name2, String comment2, String parentID,String idRoomTab,String colorHeader,Boolean flag) {
        super();
        this.level = level;
        this.myuserid = myuserid;
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
        this.userLike = userLike;
        this.userDislike = userDislike;
        this.name2 = name2;
        this.comment2 = comment2;
        this.parentID = parentID;
        this.idRoomTab = idRoomTab;
        this.colorHeader = colorHeader;
        this.flag = flag;
    }

    public String getColorHeader() {
        return colorHeader;
    }

    public void setColorHeader(String colorHeader) {
        this.colorHeader = colorHeader;
    }

    public String getIdRoomTab() {
        return idRoomTab;
    }

    public void setIdRoomTab(String idRoomTab) {
        this.idRoomTab = idRoomTab;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMyuserid() {
        return myuserid;
    }

    public void setMyuserid(String myuserid) {
        this.myuserid = myuserid;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getParentID() {
        return parentID;
    }

    public void setParentID(String parentID) {
        this.parentID = parentID;
    }

    public Boolean getFlag(){
        return flag;
    }

    public void setFlag(Boolean flag){
        this.flag = flag;
    }

}
