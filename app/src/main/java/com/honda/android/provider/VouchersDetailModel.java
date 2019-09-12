package com.honda.android.provider;

/**
 * Created by Lukmanpryg on 6/29/2016.
 */
public class VouchersDetailModel {
    private String id;
    private String serial_number;
    private String pemilik;
    private String judul;
    private String value;
    private String tgl_valid;
    private String icon;
    private String category;
    private String color;
    private String textcolor;
    private String background;

    //Lukman+
    public VouchersDetailModel() {
    }

    //    public ItemListVoucher(String id, String name, String value, String type, String sub, String detail) {
//        this.id = id;
//        this.name = name;
//        this.value = value;
//        this.type = type;
//        this.sub = sub;
//        this.detail = detail;
//    }
    //Lukman-
    public VouchersDetailModel(String serial_number, String pemilik, String judul, String value, String tgl_valid, String icon, String category, String color, String textcolor, String background) {
        //Lukman+
        super();
        //Lukman-
        this.serial_number = serial_number;
        this.pemilik = pemilik;
        this.judul = judul;
        this.value = value;
        this.tgl_valid = tgl_valid;
        this.icon = icon;
        this.category = category;
        this.color = color;
        this.textcolor = textcolor;
        this.background = background;
    }

    public VouchersDetailModel(String id, String serial_number, String pemilik, String judul, String value, String tgl_valid, String icon, String category, String color, String textcolor, String background) {
        //Lukman+
        super();
        //Lukman-
        this.id = id;
        this.serial_number = serial_number;
        this.pemilik = pemilik;
        this.judul = judul;
        this.value = value;
        this.tgl_valid = tgl_valid;
        this.icon = icon;
        this.category = category;
        this.color = color;
        this.textcolor = textcolor;
        this.background = background;
    }

    public String getId() {
        return id;
    }

    public void setId(String id_card) {
        this.id = id_card;
    }

    public String getSerial_number(){
        return serial_number;
    }

    public void setSerial_number(String serial_number){
        this.serial_number = serial_number;
    }

    public String getPemilik(){
        return pemilik;
    }

    public void setPemilik(String pemilik){
        this.pemilik = pemilik;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTgl_valid(){
        return tgl_valid;
    }

    public void setTgl_valid(String tgl_valid){
        this.tgl_valid = tgl_valid;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTextcolor() {
        return textcolor;
    }

    public void setTextcolor(String textcolor) {
        this.textcolor = textcolor;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }
}
