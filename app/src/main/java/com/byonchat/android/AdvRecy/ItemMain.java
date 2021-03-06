package com.byonchat.android.AdvRecy;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemMain implements Parcelable {
    public int id;
    public String category_tab;
    public String tab_name;
    public String url_tembak;
    public String username;
    public String id_rooms_tab;
    public String color;
    public String colorText;
    public String targetURL;
    public String include_pull;
    public String include_latlong;
    public String status;
    public String name;
    public String icon;
    public String icon_name;
    public int iconTest;

    public ItemMain(int id, String category_tab, String tab_name, String url_tembak,
                    String include_pull, String username, String id_rooms_tab,
                    String color, String colorText, String targetURL, String include_latlong,
                    String status, String name, String icon, String icon_name) {
        this.id = id;
        this.category_tab = category_tab;
        this.tab_name = tab_name;
        this.url_tembak = url_tembak;
        this.include_pull = include_pull;
        this.username = username;
        this.id_rooms_tab = id_rooms_tab;
        this.color = color;
        this.colorText = colorText;
        this.targetURL = targetURL;
        this.include_latlong = include_latlong;
        this.status = status;
        this.name = name;
        this.icon = icon;
        this.icon_name = icon_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return tab_name;
    }

    public void setTitle(String tab_name) {
        this.tab_name = tab_name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(id);
        parcel.writeString(category_tab);
        parcel.writeString(tab_name);
        parcel.writeString(url_tembak);
        parcel.writeString(username);
        parcel.writeString(id_rooms_tab);
        parcel.writeString(color);
        parcel.writeString(colorText);
        parcel.writeString(targetURL);
        parcel.writeString(include_pull);
        parcel.writeString(include_latlong);
        parcel.writeString(status);
        parcel.writeString(name);
        parcel.writeString(icon);
        parcel.writeString(icon_name);
        parcel.writeInt(iconTest);
    }

    protected ItemMain(Parcel in) {
        id = in.readInt();
        category_tab = in.readString();
        tab_name = in.readString();
        url_tembak = in.readString();
        username = in.readString();
        id_rooms_tab = in.readString();
        color = in.readString();
        colorText = in.readString();
        targetURL = in.readString();
        include_pull = in.readString();
        include_latlong = in.readString();
        status = in.readString();
        name = in.readString();
        icon = in.readString();
        icon_name = in.readString();
        iconTest = in.readInt();
    }

    public static final Creator<ItemMain> CREATOR = new Creator<ItemMain>() {
        @Override
        public ItemMain createFromParcel(Parcel in) {
            return new ItemMain(in);
        }

        @Override
        public ItemMain[] newArray(int size) {
            return new ItemMain[size];
        }
    };
}
