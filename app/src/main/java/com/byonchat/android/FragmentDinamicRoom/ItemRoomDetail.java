package com.byonchat.android.FragmentDinamicRoom;

/**
 * Created by Iman Firmansyah on 12/1/2015.
 */
public class ItemRoomDetail {
    public int id;
    public final String name;
    public String sku;
    public final String price;
    public final int qtyOrder;

    public ItemRoomDetail(String name,String price, int qtyOrder,String sku) {
        this.name = name;
        this.qtyOrder = qtyOrder;
        this.price = price;
        this.sku = sku;
    }
 public ItemRoomDetail(int id,String name,String price, int qtyOrder,String sku) {
        this.id=id;
        this.name = name;
        this.qtyOrder = qtyOrder;
        this.price = price;
        this.sku = sku;
    }

    public String getSku() {
        return sku;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public int getQtyOrder() {
        return qtyOrder;
    }
    }
