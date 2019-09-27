package com.honda.android.ui.activity;

import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.honda.android.ISSActivity.LoginDB.UserDB;
import com.honda.android.R;
import com.honda.android.helpers.Constants;

import me.grantland.widget.AutofitHelper;

public class ByonchatPDFPreviewActivity extends AppCompatActivity {

    LinearLayout layout;
    WebView webView;
    UserDB dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //disable screenshot
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_byonchat_pdf_preview);

        dbHelper = new UserDB(this);
        layout = (LinearLayout) findViewById(R.id.layout_watermark);
        webView = (WebView) findViewById(R.id.webView_test);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }
        });
        webView.loadUrl("https://docs.google.com/viewer?embedded=true&url=" + getIntent().getStringExtra(Constants.EXTRA_URL));
        layout.setWeightSum(4);
        layout.setBackgroundColor(Color.TRANSPARENT);

        for (int i = 1; i < 3; i++) {
            TextView nama = new TextView(this);
            TextView nick = new TextView(this);
            nama.setText(dbHelper.getColValue(UserDB.EMPLOYEE_NAME));
            nick.setText(dbHelper.getColValue(UserDB.EMPLOYEE_NIK));
            nama.setRotation(315);
            nick.setRotation(315);
            LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            par.setMargins(16, 16, 16, 16);
            nama.setLayoutParams(par);
            nick.setLayoutParams(par);
            nama.setGravity(Gravity.CENTER);
            nick.setGravity(Gravity.CENTER);
            nama.setTextSize(80);
            nick.setTextSize(80);
            nama.setMaxLines(1);
            nick.setMaxLines(1);
            nama.setTextColor(Color.parseColor("#20000000"));
            nick.setTextColor(Color.parseColor("#20000000"));
            layout.addView(nama);
            layout.addView(nick);
            AutofitHelper.create(nama);
            AutofitHelper.create(nick);
        }
    }
}

