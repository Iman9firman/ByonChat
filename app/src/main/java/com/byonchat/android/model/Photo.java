package com.byonchat.android.model;

import java.io.File;

public class Photo {
    private String header, id, title, before, afterString;
    private String verif;
    private String ket = "";
    File after;

    public Photo() {
    }

    public Photo(String id, String title, String before, File after) {
        this.id = id;
        this.title = title;
        this.before = before;
        this.after = after;
    }

    public Photo(String id, String title, String before, String aftera, String verif) {
        this.id = id;
        this.title = title;
        this.before = before;
        this.afterString = aftera;
        this.verif = verif;
    }

    public Photo(String id, String title, String before, String aftera, String verif, String _ket) {
        this.id = id;
        this.title = title;
        this.before = before;
        this.afterString = aftera;
        this.verif = verif;
        this.ket = _ket;
    }

    public Photo(String id, String title, String before, File after, String aftera) {
        this.id = id;
        this.title = title;
        this.before = before;
        this.after = after;
        this.afterString = aftera;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public File getAfter() {
        return after;
    }

    public void setAfter(File after) {
        this.after = after;
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    public String getAfterString() {
        return afterString;
    }

    public void setAfterString(String afterString) {
        this.afterString = afterString;
    }

    public String getVerif() {
        return verif;
    }

    public void setVerif(String verif) {
        this.verif = verif;
    }

    public String getKet() {
        return ket;
    }

    public void setKet(String ket) {
        this.ket = ket;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }
}
