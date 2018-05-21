package com.byonchat.android.list;

/**
 * Created by Asus on 9/17/2014.
 */
public class ItemListMemberCard {
    private String id_card;
    private String name;
    private String color_code;

    public ItemListMemberCard(String id_card, String name, String color_code) {
        this.id_card = id_card;
        this.name = name;
        this.color_code = color_code;
    }

    public String getId_card() {
        return id_card;
    }

    public void setId_card(String id_card) {
        this.id_card = id_card;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor_code() {
        return color_code;
    }

    public void setColor_code(String color_code) {
        this.color_code = color_code;
    }
}


