package com.byonchat.android.tempSchedule;

public class Note {
    private String title;
    private String desc;
    private String startTime;
    private String endTime;
    private String id_detail;

    public Note (String title, String desc, String startTime, String endTime , String id_detail){
        this.title = title;
        this.desc = desc;
        this.startTime = startTime;
        this.endTime = endTime;
        this.id_detail = id_detail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getId_detail() {
        return id_detail;
    }

    public void setId_detail(String id_detail) {
        this.id_detail = id_detail;
    }
}
