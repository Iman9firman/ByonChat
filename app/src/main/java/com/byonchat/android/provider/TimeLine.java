package com.byonchat.android.provider;

import java.util.Date;

public class TimeLine {
    public static final String TABLE_NAME = "time_line";
    public static final String SEND_DATE = "send_date";
    public static final String STATUS = "status";
    public static final String JABBER_ID = "jid";
    public static final String NAME = "name";
    public static final String FLAG = "flag";

    private static String[] fieldNames;

    static {
        fieldNames = new String[5];
        fieldNames[0] = SEND_DATE;
        fieldNames[1] = JABBER_ID;
        fieldNames[2] = STATUS;
        fieldNames[3] = NAME;
        fieldNames[4] = FLAG;
    }

    private long id;
    private Date sendDate;
    private String dateFromJson;
    private String jabberId;
    private String status;
    private String name;
    private String flag;

    public TimeLine(String jabberId, String status, Date sendDate, String name, String flag) {
        this.sendDate = sendDate;
        this.status = status;
        this.jabberId = jabberId;
        this.name = name;
        this.flag = flag;
    }

    public TimeLine(String jabberId, String status, String dateFromJson, String name) {
        this.dateFromJson = dateFromJson;
        this.status = status;
        this.jabberId = jabberId;
        this.name = name;
    }

    public TimeLine(long id, String jabberId, String status, Date sendDate, String name, String flag) {
        this.id = id;
        this.sendDate = sendDate;
        this.status = status;
        this.jabberId = jabberId;
        this.name = name;
        this.flag = flag;
    }

    public TimeLine(long id, String jabberId, String status, String dateFromJson, String name) {
        this.id = id;
        this.dateFromJson = dateFromJson;
        this.status = status;
        this.jabberId = jabberId;
        this.name = name;
    }

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public String getDateFromJson(){
        return dateFromJson;
    }

    public void setDateFromJson(String dateFromJson){
        this.dateFromJson = dateFromJson;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getJabberId() {
        return jabberId;
    }

    public void setJabberId(String jabberId) {
        this.jabberId = jabberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
