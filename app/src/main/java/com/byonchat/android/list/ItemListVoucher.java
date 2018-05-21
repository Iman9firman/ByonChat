package com.byonchat.android.list;

/**
 * Created by Asus on 9/17/2014.
 */
public class ItemListVoucher {
    private String id;
    private String name;
    private String value;
    private String type;
    private String sub;
    private String detail;
    private String icon;
    private String category;
    private String color;

    //Lukman+
    public ItemListVoucher() {
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

    public ItemListVoucher(String id, String name, String value, String type, String sub, String detail, String icon, String category, String color) {
        //Lukman+
        super();
        //Lukman-
        this.id = id;
        this.name = name;
        this.value = value;
        this.type = type;
        this.sub = sub;
        this.detail = detail;
        this.icon = icon;
        this.category = category;
        this.color = color;
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

    public String getId() {
        return id;
    }

    public void setId(String id_card) {
        this.id = id_card;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}


