package com.honda.android.list;

/**
 * Created by Lukmanpryg on 7/25/2016.
 */
public class ItemListVoucherTermsAndConditions {

    private String keterangan;

    public ItemListVoucherTermsAndConditions(){
    }

    public ItemListVoucherTermsAndConditions(String keterangan){
        this.keterangan = keterangan;
    }

    public void setKeterangan(String keterangan){
        this.keterangan = keterangan;
    }

    public String getKeterangan(){
        return keterangan;
    }
}
