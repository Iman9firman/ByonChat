package com.byonchat.android.model;

public class AddChildFotoExModel {

    private String idDetail;
    private String username;
    private String idTab;
    private String status;
    private String types;
    private int expandedListPosition;
    private String expandedListText;
    private int childListPos;
    private String childListText;
    private String name;

    public AddChildFotoExModel(String a, String b, String d, String e, String f, int g, String h, int i, String c, String name) {
        idDetail = a;
        username = b;
        idTab = d;
        status = e;
        types = f;
        expandedListPosition = g;
        expandedListText = h;
        childListPos = i;
        childListText = c;
        name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdDetail() {
        return idDetail;
    }

    public void setIdDetail(String idDetail) {
        this.idDetail = idDetail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIdTab() {
        return idTab;
    }

    public void setIdTab(String idTab) {
        this.idTab = idTab;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public int getExpandedListPosition() {
        return expandedListPosition;
    }

    public void setExpandedListPosition(int expandedListPosition) {
        this.expandedListPosition = expandedListPosition;
    }

    public String getExpandedListText() {
        return expandedListText;
    }

    public void setExpandedListText(String expandedListText) {
        this.expandedListText = expandedListText;
    }

    public int getChildListPos() {
        return childListPos;
    }

    public void setChildListPos(int childListPos) {
        this.childListPos = childListPos;
    }

    public String getChildListText() {
        return childListText;
    }

    public void setChildListText(String childListText) {
        this.childListText = childListText;
    }


}

