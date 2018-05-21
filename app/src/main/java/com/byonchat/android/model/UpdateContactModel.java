package com.byonchat.android.model;

public class UpdateContactModel {
    private String id;
    private String jid;
    private String name;
    private String status;
    private String date;
    private String contentStatus;
    private String contentImage;
    private String action;
    private String flag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContentStatus() {
        return contentStatus;
    }

    public void setContentStatus(String contentStatus) {
        this.contentStatus = contentStatus;
    }

    public String getContentImage() {
        return contentImage;
    }

    public void setContentImage(String contentImage) {
        this.contentImage = contentImage;
    }

    public String getAction(){
        return action;
    }

    public void setAction(String action){
        this.action = action;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag){
        this.flag = flag;
    }
}
