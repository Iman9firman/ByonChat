package com.honda.android.provider;

import java.util.ArrayList;

/**
 * Created by Lukmanpryg on 7/28/2016.
 */
public interface VouchersListener {
    public void addCity(VouchersModel vouchersModel);

    public ArrayList<VouchersModel> retriveVouchers();

    public int retriveVouchersCount();
}
