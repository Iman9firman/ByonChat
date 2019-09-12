package com.honda.android.list;

/**
 * Created by Lukmanpryg on 8/10/2016.
 */
public class contact {
    long id;
    String cid;
    String cname;

    public contact(long id, String cid, String cname){
        this.id = id;
        this.cid = cid;
        this.cname = cname;
    }

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public String getCid(){
        return cid;
    }

    public void setCid(String cid){
        this.cid = cid;
    }

    public String getCname(){
        return cname;
    }

    public void setCname(String cname){
        this.cname = cname;
    }
}
