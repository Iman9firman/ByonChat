package com.byonchat.android.ui.adapter;

import android.content.Context;

import androidx.annotation.RequiresApi;

import android.widget.TextView;

import com.byonchat.android.R;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

@Layout(R.layout.text_view_with_relative)
public class NotifikasinoresultView {

    @View(R.id.text)
    TextView text;
    String textNya;

    public NotifikasinoresultView(Context mContext, String textN) {
        this.textNya = textN;
    }

    @RequiresApi(21)
    @Resolve
    private void onResolve() {
        text.setText(textNya);
    }
}