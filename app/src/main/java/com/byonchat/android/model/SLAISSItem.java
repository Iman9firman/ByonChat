package com.byonchat.android.model;

import com.multilevelview.models.RecyclerViewItem;

public class SLAISSItem extends RecyclerViewItem {

    int id = 0;
    String title = "";
    String comment = "";
    String id_content = "";

    public SLAISSItem(int level) {
        super(level);
    }

    public String getId_content() {
        return id_content;
    }

    public void setId_content(String id_content) {
        this.id_content = id_content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
