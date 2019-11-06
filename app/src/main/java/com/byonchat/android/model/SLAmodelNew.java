package com.byonchat.android.model;

import java.io.File;

public class SLAmodelNew {
    private String header = "";
    private String id_task;
    private String id, title, before, afterString;
    private String verif;
    private String ket = "";
    File after;

    public SLAmodelNew() {
    }

    public SLAmodelNew( String id_task, String header, String id, String title, String before, File after) {
        this.id_task = id_task;
        this.id = id;
        this.title = title;
        this.before = before;
        this.after = after;
        if (!header.equalsIgnoreCase("header")) {
            this.header = header;
        }
    }

    public SLAmodelNew(String header, String title, String before, String aftera, String verif) {
        this.id = id;
        this.title = title;
        this.before = before;
        this.afterString = aftera;
        this.verif = verif;
        this.header = header;
        if (!header.equalsIgnoreCase("header")) {
            this.header = header;
        }
    }

    public SLAmodelNew(String id_task, String header, String id, String title, String before, String aftera, String verif, String _ket) {
        this.id_task = id_task;
        this.id = id;
        this.title = title;
        this.before = before;
        this.afterString = aftera;
        this.verif = verif;
        this.ket = _ket;
        this.header = header;
        if (!header.equalsIgnoreCase("header")) {
            this.header = header;
        }
    }

    public SLAmodelNew(String id_task, String header, String id, String title, String before, File after, String aftera) {
        this.id_task = id_task;
        this.id = id;
        this.title = title;
        this.before = before;
        this.after = after;
        this.afterString = aftera;
        this.header = header;
        if (!header.equalsIgnoreCase("header")) {
            this.header = header;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_task() {
        return id_task;
    }

    public void setId_task(String id_task) {
        this.id_task = id_task;
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
