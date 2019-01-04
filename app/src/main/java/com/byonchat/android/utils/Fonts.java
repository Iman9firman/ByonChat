package com.byonchat.android.utils;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Fonts {
    public static String FONT_ROBOTO_BOLD = "roboto_bold";

    public void FontFamily(AssetManager mgr, TextView view, String fonts) {
        if (fonts.equalsIgnoreCase(FONT_ROBOTO_BOLD)) {
            Typeface typeface = Typeface.createFromAsset(mgr, "Roboto_Bold.ttf");
            view.setTypeface(typeface);
        }
    }

    public void FontFamily(AssetManager mgr, EditText view, String fonts) {

        if (fonts.equalsIgnoreCase(FONT_ROBOTO_BOLD)) {
            Typeface typeface = Typeface.createFromAsset(mgr, "Roboto_Bold.ttf");
            view.setTypeface(typeface);
        }
    }

    public void FontFamily(AssetManager mgr, Button view, String fonts) {

        if (fonts.equalsIgnoreCase(FONT_ROBOTO_BOLD)) {
            Typeface typeface = Typeface.createFromAsset(mgr, "Roboto_Bold.ttf");
            view.setTypeface(typeface);
        }
    }
}
