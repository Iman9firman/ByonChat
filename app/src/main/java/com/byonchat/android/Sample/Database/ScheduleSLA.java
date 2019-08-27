package com.byonchat.android.Sample.Database;

public class ScheduleSLA {
    public String id;
    public String jjt;
    public String frequency;
    public String floor;
    public String periode;
    public String startdate;
    public String finishdate;

    public String detail_area;

    public String iddata;
    public String user;
    public String startpic;
    public String progresspic;
    public String finishpic;

    public ScheduleSLA() {
    }

    public ScheduleSLA(String iddata, String jjt, String user, String startpic, String progresspic, String finishpic) {
        this.iddata = iddata;
        this.jjt = jjt;
        this.user = user;
        this.startpic = startpic;
        this.progresspic = progresspic;
        this.finishpic = finishpic;
    }

    public ScheduleSLA(String id, String user, String jjt, String frequency, String periode, String floor, String startdate, String finishdate, String detail_area) {
        this.id = id;
        this.jjt = jjt;
        this.user = user;
        this.frequency = frequency;
        this.floor = floor;
        this.periode = periode;
        this.startdate = startdate;
        this.finishdate = finishdate;
        this.detail_area = detail_area;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJjt() {
        return jjt;
    }

    public void setJjt(String jjt) {
        this.jjt = jjt;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getPeriode() {
        return periode;
    }

    public void setPeriode(String periode) {
        this.periode = periode;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getFinishdate() {
        return finishdate;
    }

    public void setFinishdate(String finishdate) {
        this.finishdate = finishdate;
    }

    public String getIddata() {
        return iddata;
    }

    public void setIddata(String iddata) {
        this.iddata = iddata;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getStartpic() {
        return startpic;
    }

    public void setStartpic(String startpic) {
        this.startpic = startpic;
    }

    public String getProgresspic() {
        return progresspic;
    }

    public void setProgresspic(String progresspic) {
        this.progresspic = progresspic;
    }

    public String getFinishpic() {
        return finishpic;
    }

    public void setFinishpic(String finishpic) {
        this.finishpic = finishpic;
    }

    public String getDetail_area() {
        return detail_area;
    }

    public void setDetail_area(String detail_area) {
        this.detail_area = detail_area;
    }
}
