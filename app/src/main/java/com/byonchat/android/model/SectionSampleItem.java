package com.byonchat.android.model;

import com.byonchat.android.AdvRecy.ItemMain;

import java.util.ArrayList;

public class SectionSampleItem {



    private String headerTitle;
    private ArrayList<ItemMain> allItemsInSection;


    public SectionSampleItem() {

    }
    public SectionSampleItem(String headerTitle, ArrayList<ItemMain> allItemsInSection) {
        this.headerTitle = headerTitle;
        this.allItemsInSection = allItemsInSection;
    }



    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public ArrayList<ItemMain> getAllItemsInSection() {
        return allItemsInSection;
    }

    public void setAllItemsInSection(ArrayList<ItemMain> allItemsInSection) {
        this.allItemsInSection = allItemsInSection;
    }


}