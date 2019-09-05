package com.byonchat.android.model;

/**
 * This Model for Modeling List in Insert Schedule SLA
 * Including (Pembobotan, Section, and Subsection)
 */

public class ScheduleList {
    String id;
    String id_parent;
    String title;

    public ScheduleList(String id, String id_parent, String title){
        this.id = id;
        this.id_parent = id_parent;
        this.title = title;
    }
    public ScheduleList(String id, String title){
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_parent() {
        return id_parent;
    }

    public void setId_parent(String id_parent) {
        this.id_parent = id_parent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

