package com.byonchat.android.list;

/**
 * Created by Asus on 9/17/2014.
 */
public class ItemListSearchApps {
    private String name;
    private String desc;
    private String media;
    public String pcg;
    public String cls;
    public String link;

    public ItemListSearchApps(String name, String desc, String media,String pcg,String cls,String link) {
        this.name = name;
        this.desc = desc;
        this.media = media;
        this.pcg = pcg;
        this.cls = cls;
        this.link = link;
    }

    public String getPcg() {
        return pcg;
    }

    public void setPcg(String pcg) {
        this.pcg = pcg;
    }

    public String getCls() {
        return cls;
    }

    public void setCls(String cls) {
        this.cls = cls;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }
}


