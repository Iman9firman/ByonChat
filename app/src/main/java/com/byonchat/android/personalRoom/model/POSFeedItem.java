package com.byonchat.android.personalRoom.model;

/**
 * Created by lukma on 3/7/2016.
 */

public class POSFeedItem {
    private int level;
    private String myuserid, userid,idRoomTab,colorHeader;
    private String id, parentID,title,name, status, image, profilePic, timeStamp, url;

    public POSFeedItem() {
    }


    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
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

    public String getIdRoomTab() {
        return idRoomTab;
    }

    public void setIdRoomTab(String idRoomTab) {
        this.idRoomTab = idRoomTab;
    }

    public String getColorHeader() {
        return colorHeader;
    }

    public void setColorHeader(String colorHeader) {
        this.colorHeader = colorHeader;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentID() {
        return parentID;
    }

    public void setParentID(String parentID) {
        this.parentID = parentID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
}
