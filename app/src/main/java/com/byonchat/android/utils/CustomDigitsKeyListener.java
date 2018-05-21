package com.byonchat.android.utils;

import android.text.InputType;
import android.text.method.DigitsKeyListener;

/**
 * Created by imanfirmansyah on 8/24/17.
 */

public class CustomDigitsKeyListener extends DigitsKeyListener
{
    public CustomDigitsKeyListener() {
        super(false, false);
    }

    public CustomDigitsKeyListener(boolean sign, boolean decimal) {
        super(sign, decimal);
    }

    public int getInputType() {
        return InputType.TYPE_CLASS_PHONE;
    }
}

