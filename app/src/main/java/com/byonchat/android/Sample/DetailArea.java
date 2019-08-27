package com.byonchat.android.Sample;

public class DetailArea {
    private String title;
    private String period;
    private String frekuensi;
    private String start_date;
    private String finish_date;

    private String id_jjt;
    private String jjt_loc;
    private String jjt_code;

    private String id;
    private String img_start;
    private String img_proses;
    private String img_done;

    public DetailArea(String title, String period, String frekuensi, String start_date, String finish_date){
        this.title = title;
        this.period = period;
        this.frekuensi = frekuensi;
        this.start_date = start_date;
        this.finish_date = finish_date;
    }

    public DetailArea(String title, String period, String frekuensi){
        this.title = title;
        this.period = period;
        this.frekuensi = frekuensi;
    }

    public DetailArea(String id, String id_jjt, String title, String period, String frekuensi, String img_start, String img_proses, String img_done){
        this.id = id;
        this.id_jjt = id_jjt;
        this.title = title;
        this.period = period;
        this.frekuensi = frekuensi;
        this.img_start = img_start;
        this.img_proses = img_proses;
        this.img_done = img_done;
    }

    public DetailArea(String title){
        this.title = title;
    }

    public void setFinish_date(String finish_date) {
        this.finish_date = finish_date;
    }

    public String getFinish_date() {
        return finish_date;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getFrekuensi() {
        return frekuensi;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPeriod() {
        return period;
    }

    public void setFrekuensi(String frekuensi) {
        this.frekuensi = frekuensi;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getTitle() {
        return title;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setImg_start(String img_start) {
        this.img_start = img_start;
    }

    public String getImg_start() {
        return img_start;
    }

    public void setImg_proses(String img_proses) {
        this.img_proses = img_proses;
    }

    public String getImg_proses() {
        return img_proses;
    }

    public void setImg_done(String img_done) {
        this.img_done = img_done;
    }

    public String getImg_done() {
        return img_done;
    }

    public void setId_jjt(String id_jjt) {
        this.id_jjt = id_jjt;
    }

    public String getId_jjt() {
        return id_jjt;
    }

    public void setJjt_code(String jjt_code) {
        this.jjt_code = jjt_code;
    }

    public String getJjt_code() {
        return jjt_code;
    }

    public void setJjt_loc(String jjt_loc) {
        this.jjt_loc = jjt_loc;
    }

    public String getJjt_loc() {
        return jjt_loc;
    }
}
