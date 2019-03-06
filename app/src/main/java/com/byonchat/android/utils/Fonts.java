package com.byonchat.android.utils;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Fonts {
    public static String FONT_ROBOTO_BOLD = "roboto_bold";
    public static String FONT_ROBOTO_ITALIC = "roboto_italic";
    public static String FONT_ROBOTO_LIGHT = "roboto_light";
    public static String FONT_ROBOTO_MEDIUM = "roboto_medium";
    public static String FONT_ROBOTO_REGULAR = "roboto_regular";
    public static String FONT_ROBOTO_THIN = "roboto_thin";

    public void FontFamily(AssetManager mgr, TextView view, String fonts) {
        if (fonts.equalsIgnoreCase(FONT_ROBOTO_BOLD)) {
            Typeface typeface = Typeface.createFromAsset(mgr, "Roboto_Bold.ttf");
            view.setTypeface(typeface);
        } else if (fonts.equalsIgnoreCase(FONT_ROBOTO_ITALIC)) {
            Typeface typeface = Typeface.createFromAsset(mgr, "roboto_italic.ttf");
            view.setTypeface(typeface);
        } else if (fonts.equalsIgnoreCase(FONT_ROBOTO_LIGHT)) {
            Typeface typeface = Typeface.createFromAsset(mgr, "roboto_light.ttf");
            view.setTypeface(typeface);
        } else if (fonts.equalsIgnoreCase(FONT_ROBOTO_MEDIUM)) {
            Typeface typeface = Typeface.createFromAsset(mgr, "roboto_medium.ttf");
            view.setTypeface(typeface);
        } else if (fonts.equalsIgnoreCase(FONT_ROBOTO_REGULAR)) {
            Typeface typeface = Typeface.createFromAsset(mgr, "roboto_regular.ttf");
            view.setTypeface(typeface);
        } else if (fonts.equalsIgnoreCase(FONT_ROBOTO_THIN)) {
            Typeface typeface = Typeface.createFromAsset(mgr, "roboto_thin.ttf");
            view.setTypeface(typeface);
        }
    }

    public void FontFamily(AssetManager mgr, EditText view, String fonts) {

        if (fonts.equalsIgnoreCase(FONT_ROBOTO_BOLD)) {
            Typeface typeface = Typeface.createFromAsset(mgr, "Roboto_Bold.ttf");
            view.setTypeface(typeface);
        } else if (fonts.equalsIgnoreCase(FONT_ROBOTO_ITALIC)) {
            Typeface typeface = Typeface.createFromAsset(mgr, "roboto_italic.ttf");
            view.setTypeface(typeface);
        } else if (fonts.equalsIgnoreCase(FONT_ROBOTO_LIGHT)) {
            Typeface typeface = Typeface.createFromAsset(mgr, "roboto_light.ttf");
            view.setTypeface(typeface);
        } else if (fonts.equalsIgnoreCase(FONT_ROBOTO_MEDIUM)) {
            Typeface typeface = Typeface.createFromAsset(mgr, "roboto_medium.ttf");
            view.setTypeface(typeface);
        } else if (fonts.equalsIgnoreCase(FONT_ROBOTO_REGULAR)) {
            Typeface typeface = Typeface.createFromAsset(mgr, "roboto_regular.ttf");
            view.setTypeface(typeface);
        } else if (fonts.equalsIgnoreCase(FONT_ROBOTO_THIN)) {
            Typeface typeface = Typeface.createFromAsset(mgr, "roboto_thin.ttf");
            view.setTypeface(typeface);
        }
    }

    public void FontFamily(AssetManager mgr, Button view, String fonts) {

        if (fonts.equalsIgnoreCase(FONT_ROBOTO_BOLD)) {
            Typeface typeface = Typeface.createFromAsset(mgr, "Roboto_Bold.ttf");
            view.setTypeface(typeface);
        } else if (fonts.equalsIgnoreCase(FONT_ROBOTO_ITALIC)) {
            Typeface typeface = Typeface.createFromAsset(mgr, "roboto_italic.ttf");
            view.setTypeface(typeface);
        } else if (fonts.equalsIgnoreCase(FONT_ROBOTO_LIGHT)) {
            Typeface typeface = Typeface.createFromAsset(mgr, "roboto_light.ttf");
            view.setTypeface(typeface);
        } else if (fonts.equalsIgnoreCase(FONT_ROBOTO_MEDIUM)) {
            Typeface typeface = Typeface.createFromAsset(mgr, "roboto_medium.ttf");
            view.setTypeface(typeface);
        } else if (fonts.equalsIgnoreCase(FONT_ROBOTO_REGULAR)) {
            Typeface typeface = Typeface.createFromAsset(mgr, "roboto_regular.ttf");
            view.setTypeface(typeface);
        } else if (fonts.equalsIgnoreCase(FONT_ROBOTO_THIN)) {
            Typeface typeface = Typeface.createFromAsset(mgr, "roboto_thin.ttf");
            view.setTypeface(typeface);
        }
    }
}
