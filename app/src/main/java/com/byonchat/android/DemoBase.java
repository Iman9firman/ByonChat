package com.byonchat.android;

import androidx.fragment.app.FragmentActivity;

/**
 * Created by Iman Firmansyah on 3/11/2016.
 */
public abstract class DemoBase extends FragmentActivity {

    protected String[] mMonths = new String[] {
            "Bogor", "Depok", "Jakarta" };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
    }
}