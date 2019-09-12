package com.honda.android.tempSchedule;

public class Note {
    private String title;
    private String lokasi;
    private String ket_tambah;
    private String startTime;
    private String endTime;
    private String keterangan;
    private String no_spk;
    private String alasan;
    private String id_detail;
    private String status;
    private String warna;
    private boolean isSubmit = false;
    private String ket_status;

    public Note (String title,String lokasi,String ket_tambah, String startTime, String endTime ,String keterangan,String no_spk,String alasan, String id_detail , String status , boolean isSubmit , String warna , String ket_status){
        this.title = title;
        this.lokasi = lokasi;
        this.keterangan = keterangan;
        this.alasan = alasan;
        this.ket_tambah = ket_tambah;
        this.startTime = startTime;
        this.endTime = endTime;
        this.no_spk = no_spk;
        this.id_detail = id_detail;
        this.status = status;
        this.isSubmit = isSubmit;
        this.warna = warna;
        this.ket_status = ket_status;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKet_tambah() {
        return ket_tambah;
    }

    public void setKet_tambah(String ket_tambah) {
        this.ket_tambah = ket_tambah;
    }

    public String getNo_spk() {
        return no_spk;
    }

    public void setNo_spk(String no_spk) {
        this.no_spk = no_spk;
    }

    public String getKet_status() {
        return ket_status;
    }

    public void setKet_status(String ket_status) {
        this.ket_status = ket_status;
    }
}
