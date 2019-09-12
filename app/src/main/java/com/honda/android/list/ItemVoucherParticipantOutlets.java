package com.honda.android.list;

/**
 * Created by Lukmanpryg on 7/22/2016.
 */
public class ItemVoucherParticipantOutlets {

    private String place;
    private long id;

    public ItemVoucherParticipantOutlets(String place, long id) {
        this.place = place;
        this.id = id;
    }

    public void setPlace(String place) {
        this.place = place;
    }
    public String getPlace() {
        return place;
    }

    public void setId(long id){
        this.id = id;
    }

    public long getId(){
        return id;
    }
}
