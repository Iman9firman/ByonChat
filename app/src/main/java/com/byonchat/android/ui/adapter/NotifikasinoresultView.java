package com.byonchat.android.ui.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.RequiresApi;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.byonchat.android.ISSActivity.Requester.ByonchatBaseMallKelapaGadingActivity;
import com.byonchat.android.R;
import com.byonchat.android.data.model.MkgServices;
import com.byonchat.android.tabRequest.MapsViewActivity;
import com.byonchat.android.tabRequest.RelieverDetailActivity;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

import java.util.HashMap;
import java.util.Map;

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