package com.honda.android.provider;

public class OffersModel {
        private long id;
        private String judul;
        private String value;
        private String type;
        private String sub;
        private String detail;
        private String icon;
        private String category;
        private String color;

    public OffersModel(long id, String judul, String value, String type, String sub, String detail, String icon, String category, String color) {
        this.id = id;
        this.judul = judul;
        this.value = value;
        this.type = type;
        this.sub = sub;
        this.detail = detail;
        this.icon = icon;
        this.category = category;
        this.color = color;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
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
}
