package com.byonchat.android.FragmentDinamicRoom;

/**
 * Created by Iman Firmansyah on 8/29/2016.
 */
public class ModelFormChild {

    private String id;
    private String title;
    private String detail;
    private String price;

    public ModelFormChild(String title, String detail) {
        this.title = title;
        this.detail = detail;
    }

    public ModelFormChild(String id,String title, String detail, String price) {
        this.id = id;
        this.title = title;
        this.detail = detail;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}