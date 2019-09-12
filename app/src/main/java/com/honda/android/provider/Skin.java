package com.honda.android.provider;

import android.graphics.Bitmap;

public class Skin {
	 private long id;
	 private String title;
	 private String color;
	 private String desc;
     private Bitmap logo;
     private Bitmap logo_header;
     private Bitmap background;

    public Skin(String title, String desc, String color, Bitmap logo, Bitmap logo_header, Bitmap background) {
        this.title = title;
        this.desc = desc;
        this.color = color;
        this.logo = logo;
        this.logo_header = logo_header;
        this.background = background;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public long getId() {
	return id;
    }

    public void setId(long id) {
	this.id = id;
}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Bitmap getLogo() {
        return logo;
    }

    public void setLogo(Bitmap logo) {
        this.logo = logo;
    }

    public Bitmap getBackground() {
        return background;
    }

    public void setBackground(Bitmap background) {
        this.background = background;
    }

    public Bitmap getLogo_header() {
        return logo_header;
    }

    public void setLogo_header(Bitmap logo_header) {
        this.logo_header = logo_header;
    }
}
