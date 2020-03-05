package com.byonchat.android;

import androidx.fragment.app.FragmentActivity;

import static com.byonchat.android.utils.Utility.reportCatch;

/**
 * Created by Iman Firmansyah on 3/11/2016.
 */
public abstract class DemoBase extends FragmentActivity {

    protected String[] mMonths = new String[] {
            "Bogor", "Depok", "Jakarta" };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }
}