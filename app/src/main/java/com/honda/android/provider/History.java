package com.honda.android.provider;

public class History {
    public String id;
    public String date;
    public String desc;

    public History(String id, String date,String desc) {
        this.id = id;
        this.date = date;
        this.desc = desc;
    }

    public History( String date,String desc) {
        this.date = date;
        this.desc = desc;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}