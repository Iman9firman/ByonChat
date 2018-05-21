package com.byonchat.android.list;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Lukmanpryg on 8/30/2016.
 */
public class ItemListTrending {

    private String id;
    private String name;
    private String type;
    private String color;

    public ItemListTrending(String id, String name, String type){
        this.id = id;
        this.name = name;
        this.type = type;
        this.color = getRandomColor();
    }

    public String getRandomColor() {
        ArrayList<String> colors = new ArrayList<>();
        colors.add("#ED7D31");
        colors.add("#00B0F0");
        colors.add("#FF0000");
        colors.add("#D0CECE");
        colors.add("#00B050");
        colors.add("#9999FF");
        colors.add("#FF5FC6");
        colors.add("#FFC000");
        colors.add("#7F7F7F");
        colors.add("#4800FF");

        return colors.get(new Random().nextInt(colors.size()));
    }

    public ItemListTrending(String name){
        this.name = name;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getId(){
        return id;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getType(){
        return type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
