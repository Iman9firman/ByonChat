package com.honda.android.model;

import com.multilevelview.models.RecyclerViewItem;

public class SLAISSItem extends RecyclerViewItem {

    int id = 0;
    String title = "";
    String comment = "";

    public SLAISSItem(int level){
        super(level);
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
