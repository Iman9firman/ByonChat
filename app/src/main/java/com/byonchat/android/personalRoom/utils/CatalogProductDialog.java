package com.byonchat.android.personalRoom.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Picture;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.byonchat.android.DownloadFileByonchat;
import com.byonchat.android.R;
import com.byonchat.android.personalRoom.adapter.ProductCatalogAdapter;
import com.byonchat.android.personalRoom.model.PictureModel;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class CatalogProductDialog extends Dialog implements
        android.view.View.OnClickListener {



    public Context c;
    public PictureModel i;
    public Dialog d;
    public ImageView thumb, open, share;

    public CatalogProductDialog(Context a, PictureModel i) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.i = i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.actifdialog_productcatalog);
        TextView title = (TextView) findViewById(R.id.id_title);
        TextView date = (TextView) findViewById(R.id.id_date);
        TextView desc = (TextView) findViewById(R.id.id_desc);
        thumb = (ImageView) findViewById(R.id.id_thumb);
        open = (ImageView) findViewById(R.id.openThis);
        share = (ImageView) findViewById(R.id.shareThis);

        title.setText(i.getTitle());
        date.setText(i.getTgl_upload());
        desc.setText(Html.fromHtml(i.getDescription()));
        String url = i.getUrl_thumb();
        Picasso.with(c)
                .load(url)
                .noFade()
                .placeholder(R.drawable.no_image)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .into(thumb);
        open.setOnClickListener(this);
        share.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.openThis:
                Intent intent = new Intent(c, DownloadFileByonchat.class);
                intent.putExtra("path", i.getUrl());
                c.startActivity(intent);
                break;
            case R.id.shareThis:
                Intent intentd = new Intent(c, ShareFileFromAPI.class);
                intentd.putExtra("path", i.getUrl());
                c.startActivity(intentd);
                break;
            default:
                break;
        }
        dismiss();
    }
}