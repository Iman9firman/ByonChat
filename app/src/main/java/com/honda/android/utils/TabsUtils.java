package com.honda.android.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.honda.android.R;

public class TabsUtils {

    public static View renderTabView(Context context, int icon, int badgeNumber) {
        RelativeLayout view = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.tab_badge, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ImageView ic_tab = (ImageView) view.findViewById(R.id.tab_image);
        ic_tab.setImageResource(icon);
        updateTabBadge((TextView) view.findViewById(R.id.tab_badge), badgeNumber);
        view.setEnabled(false);
        return view;
    }

    public static void updateTabBadge(TabLayout.Tab tab, int badgeNumber) {
        if (badgeNumber>99) badgeNumber = 99;
        updateTabBadge((TextView) tab.getCustomView().findViewById(R.id.tab_badge), badgeNumber);
    }
    public static void updateColor(TabLayout.Tab tab, Drawable color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            tab.getCustomView().findViewById(R.id.tab_image).setBackground(color);
        }
    }

    private static void updateTabBadge(TextView view, int badgeNumber) {

        if (badgeNumber > 0) {
            view.setVisibility(View.VISIBLE);
            view.setText(Integer.toString(badgeNumber));
        } else {
            view.setVisibility(View.GONE);
        }
    }
}
