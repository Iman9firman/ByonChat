package com.byonchat.android.tempSchedule;

public class Note {
    private String lokasi;
    private String keterangan;
    private String alasan;
    private String startTime;
    private String endTime;
    private String id_detail;
    private String status;
    private String warna;
    private boolean isSubmit = false;

    public Note (String lokasi, String startTime, String endTime ,String keterangan,String alasan, String id_detail , String status , boolean isSubmit , String warna){
        this.lokasi = lokasi;
        this.keterangan = keterangan;
        this.alasan = alasan;
        this.startTime = startTime;
        this.endTime = endTime;
        this.id_detail = id_detail;
        this.status = status;
        this.isSubmit = isSubmit;
        this.warna = warna;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getAlasan() {
        return alasan;
    }

    public void setAlasan(String alasan) {
        this.alasan = alasan;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isSubmit() {
        return isSubmit;
    }

    public void setSubmit(boolean submit) {
        isSubmit = submit;
    }

    public String getWarna() {
        return warna;
    }

    public void setWarna(String warna) {
        this.warna = warna;
    }
}
