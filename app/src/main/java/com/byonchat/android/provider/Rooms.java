package com.byonchat.android.provider;

/**
 * Created by Iman Firmansyah on 5/6/2016.
 */
public class Rooms {

    String id;
    String username;
    String realname;
    String content;
    String color;
    String backdrop;
    String lastupdate;
    String first;
    String icon;
    String colorText;

    public Rooms(String id, String nUsername, String nRealname, String nCOntent, String nColor, String nBackdrop, String nLastupdate, String nIcon,String firstT,String colorT) {
        this.id = id;
        this.username = nUsername;
        this.realname = nRealname;
        this.content = nCOntent;
        this.color = nColor;
        this.backdrop = nBackdrop;
        this.lastupdate = nLastupdate;
        this.icon = nIcon;
        this.first = firstT;
        this.colorText = colorT;

    }

    public Rooms(String nUsername, String nRealname, String nCOntent, String nColor, String nBackdrop, String nLastupdate, String nIcon,String firstT,String colorT) {
        this.username = nUsername;
        this.realname = nRealname;
        this.content = nCOntent;
        this.color = nColor;
        this.backdrop = nBackdrop;
        this.lastupdate = nLastupdate;
        this.icon = nIcon;
        this.first = firstT;
        this.colorText = colorT;
    }

    public String getColorText() {
        return colorText;
    }

    public void setColorText(String colorText) {
        this.colorText = colorText;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getBackdrop() {
        return backdrop;
    }

    public void setBackdrop(String backdrop) {
        this.backdrop = backdrop;
    }

    public String getLastupdate() {
        return lastupdate;
    }

    public void setLastupdate(String lastupdate) {
        this.lastupdate = lastupdate;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}