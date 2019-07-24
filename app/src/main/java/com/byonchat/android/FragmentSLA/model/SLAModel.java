package com.byonchat.android.FragmentSLA.model;

public class SLAModel {
    private String id;
    private String title;
    private String daleman;
    private int count;
    private Double value;
    private boolean isItemToBeCheck;

    public SLAModel(String title,String content,int count,Double value,boolean isItemToBeCheck){
        this.title = title;
        this.daleman = content;
        this.count = count;
        this.value = value;
        this.isItemToBeCheck = isItemToBeCheck;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDaleman() {
        return daleman;
    }

    public void setDaleman(String daleman) {
        this.daleman = daleman;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public boolean isItemToBeCheck() {
        return isItemToBeCheck;
    }

    public void setItemToBeCheck(boolean itemToBeCheck) {
        isItemToBeCheck = itemToBeCheck;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
