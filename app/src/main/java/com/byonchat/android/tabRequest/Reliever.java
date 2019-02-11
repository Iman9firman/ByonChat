package com.byonchat.android.tabRequest;

public class Reliever implements Comparable<Reliever>{

    private String relieverIdOfficer;
    private String relieverId;
    private String relieverName;
    private String relieverPhoneNumber;
    private String relieverLastLat;
    private String relieverLastLng;
    private String relieverDistance;
    private String relieverRating;
    private boolean relieverSelected;

    public Reliever(String idOfficer,String id,String name,String phoneNumber,String lastLat,String lastLng,String distance,String rating,boolean selected){
        relieverIdOfficer = idOfficer;
        relieverId = id;
        relieverName = name;
        relieverPhoneNumber = phoneNumber;
        relieverLastLat = lastLat;
        relieverLastLng = lastLng;
        relieverDistance = distance;
        relieverRating = rating;
        relieverSelected = selected;
    }

    public String getRelieverIdOfficer() {
        return relieverIdOfficer;
    }

    public void setRelieverIdOfficer(String relieverIdOfficer) {
        this.relieverIdOfficer = relieverIdOfficer;
    }

    public String getRelieverId() {
        return relieverId;
    }

    public void setRelieverId(String relieverId) {
        this.relieverId = relieverId;
    }

    public String getRelieverName() {
        return relieverName;
    }

    public void setRelieverName(String relieverName) {
        this.relieverName = relieverName;
    }

    public String getRelieverPhoneNumber() {
        return relieverPhoneNumber;
    }

    public void setRelieverPhoneNumber(String relieverPhoneNumber) {
        this.relieverPhoneNumber = relieverPhoneNumber;
    }

    public String getRelieverLastLat() {
        return relieverLastLat;
    }

    public void setRelieverLastLat(String relieverLastLat) {
        this.relieverLastLat = relieverLastLat;
    }

    public String getRelieverLastLng() {
        return relieverLastLng;
    }

    public void setRelieverLastLng(String relieverLastLng) {
        this.relieverLastLng = relieverLastLng;
    }

    public String getRelieverDistance() {
        return relieverDistance;
    }

    public void setRelieverDistance(String relieverDistance) {
        this.relieverDistance = relieverDistance;
    }

    public String getRelieverRating() {
        return relieverRating;
    }

    public void setRelieverRating(String relieverRating) {
        this.relieverRating = relieverRating;
    }

    public boolean isRelieverSelected() {
        return relieverSelected;
    }

    public void setRelieverSelected(boolean relieverSelected) {
        this.relieverSelected = relieverSelected;
    }

    @Override
    public int compareTo(Reliever that) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            int result = Integer.compare(Integer.valueOf(that.relieverRating), Integer.valueOf(this.relieverRating));
            if (result == 0){
                result = Double.compare(Double.valueOf(this.relieverDistance),Double.valueOf(that.relieverDistance));
            }
            return result;
        }
        return 0;
    }
}
