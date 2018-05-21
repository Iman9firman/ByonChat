package com.byonchat.android.provider;

public class MembersDetail {
	 private long id;
     private String time;
	 private String barcode;
	 private String promo;
	 private String judulPromo;
     private String bonus;
     private String judulBonus;
     private String room;
     private String encodedDesc;
     private String foto;

    public MembersDetail( long id,String time, String barcode, String promo, String judulPromo, String bonus, String judulBonus, String room, String encodedDesc,String foto) {
        this.id = id;
        this.time = time;
        this.barcode = barcode;
        this.promo = promo;
        this.judulPromo = judulPromo;
        this.bonus = bonus;
        this.judulBonus = judulBonus;
        this.room = room;
        this.encodedDesc = encodedDesc;
        this.foto = foto;
    }
    public MembersDetail(String time, String barcode, String promo, String judulPromo, String bonus, String judulBonus, String room, String encodedDesc,String foto) {
        this.time = time;
        this.barcode = barcode;
        this.promo = promo;
        this.judulPromo = judulPromo;
        this.bonus = bonus;
        this.judulBonus = judulBonus;
        this.room = room;
        this.encodedDesc = encodedDesc;
        this.foto = foto;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getPromo() {
        return promo;
    }

    public void setPromo(String promo) {
        this.promo = promo;
    }

    public String getJudulPromo() {
        return judulPromo;
    }

    public void setJudulPromo(String judulPromo) {
        this.judulPromo = judulPromo;
    }

    public String getBonus() {
        return bonus;
    }

    public void setBonus(String bonus) {
        this.bonus = bonus;
    }

    public String getJudulBonus() {
        return judulBonus;
    }

    public void setJudulBonus(String judulBonus) {
        this.judulBonus = judulBonus;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getEncodedDesc() {
        return encodedDesc;
    }

    public void setEncodedDesc(String encodedDesc) {
        this.encodedDesc = encodedDesc;
    }
}
