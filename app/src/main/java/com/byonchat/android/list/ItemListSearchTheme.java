package com.byonchat.android.list;

/**
 * Created by Asus on 9/17/2014.
 */
public class ItemListSearchTheme {
    private String name;
    private String desc;
    private String logo;
    private String logoHeader;
    private String logoList;
    private String background;
    private String color;
    private String poin;

    public ItemListSearchTheme(String name, String desc, String logo,String logoList,String logoHeader,String color,String background,String poin) {
        this.name = name;
        this.desc = desc;
        this.logo = logo;
        this.logoList = logoList;
        this.logoHeader = logoHeader;
        this.color = color;
        this.poin = poin;
        this.background = background;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getLogo2() {
        return logoList;
    }

    public void setLogo2(String logo2) {
        this.logoList = logo2;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPoin() {
        return poin;
    }

    public void setPoin(String poin) {
        this.poin = poin;
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

    public String getLogoHeader() {
        return logoHeader;
    }

    public void setLogoHeader(String logoHeader) {
        this.logoHeader = logoHeader;
    }
}


