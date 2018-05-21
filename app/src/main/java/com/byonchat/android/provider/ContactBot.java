package com.byonchat.android.provider;


public class ContactBot {
    public String id;
    public String name;
    public String desc;
    public String link;
    public String realname;
    public String type;

    public ContactBot(String name,String desc,String realname,String link, String type) {
        this.name = name;
        this.desc = desc;
        this.link = link;
        this.realname = realname;
        this.type = type;
    }
    public ContactBot(String id,String name,String desc,String realname,String link, String type) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.link = link;
        this.realname = realname;
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}