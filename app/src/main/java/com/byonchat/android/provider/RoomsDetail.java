package com.byonchat.android.provider;

/**
 * Created by Iman Firmansyah on 5/6/2016.
 */
public class RoomsDetail {

    String id;
    String parent_tab;
    String parent_room;
    String content;
    String flag_content;
    String flag_tab;
    String flag_room;

    public RoomsDetail(String id, String pTab, String pRoom, String ctn, String fContent, String fTab, String fRoom) {
        this.id = id;
        this.parent_tab = pTab;
        this.parent_room = pRoom;
        this.content = ctn;
        this.flag_content = fContent;
        this.flag_tab = fTab;
        this.flag_room = fRoom;
    }

    public RoomsDetail(String ctn, String fContent,String flag_tab) {
        this.content = ctn;
        this.flag_content = fContent;
        this.flag_tab = flag_tab;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParent_tab() {
        return parent_tab;
    }

    public void setParent_tab(String parent_tab) {
        this.parent_tab = parent_tab;
    }

    public String getParent_room() {
        return parent_room;
    }

    public void setParent_room(String parent_room) {
        this.parent_room = parent_room;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFlag_content() {
        return flag_content;
    }

    public void setFlag_content(String flag_content) {
        this.flag_content = flag_content;
    }

    public String getFlag_tab() {
        return flag_tab;
    }

    public void setFlag_tab(String flag_tab) {
        this.flag_tab = flag_tab;
    }

    public String getFlag_room() {
        return flag_room;
    }

    public void setFlag_room(String flag_room) {
        this.flag_room = flag_room;
    }
}