package com.byonchat.android.provider;

public class ContentRoom {
	 private long id;
	 private String idHex;
     private String time;
	 private String status;
	 private String content;
	 private String title;
     private String metode;
     private String attach;

    public ContentRoom(){}

    public ContentRoom(long id, String time, String status, String content, String title, String metode, String attach) {
        this.id = id;
        this.time = time;
        this.status = status;
        this.content = content;
        this.title = title;
        this.metode = metode;
        this.attach = attach;
    }
    public ContentRoom(long id, String status) {
        this.id = id;
        this.status = status;
    }

    public ContentRoom(long id, String time,String content , String attach,String status, String metode) {
        this.id = id;
        this.time = time;
        this.content = content;
        this.status = status;
        this.metode = metode;
        this.attach = attach;
    }
    public ContentRoom(String idHex,String title, String time,String content , String attach,String status, String metode) {
        this.idHex = idHex;
        this.time = time;
        this.title = title;
        this.content = content;
        this.status = status;
        this.metode = metode;
        this.attach = attach;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMetode() {
        return metode;
    }

    public void setMetode(String metode) {
        this.metode = metode;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getIdHex() {
        return idHex;
    }

    public void setIdHex(String idHex) {
        this.idHex = idHex;
    }
}
