package com.byonchat.android.provider;


public class RealNameRoom {
    public String id;
    public String name;
    public String realName;

    public RealNameRoom(String name, String realName) {
        this.name = name;
        this.realName = realName;
    }
    public RealNameRoom(String id, String name, String realName) {
        this.id = id;
        this.name = name;
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

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}