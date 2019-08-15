package com.byonchat.android.ui.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
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

@Layout(R.layout.mkg_child_layout)
public class ChildRecyclerView {

    @View(R.id.child_name)
    TextView child_text_name;

    @View(R.id.child_status)
    TextView child_text_status;

    @View(R.id.child_distance)
    TextView child_text_distance;

    @View(R.id.child_contact)
    ImageView child_img_contact;

    @View(R.id.child_location)
    ImageView child_img_location;

    @View(R.id.child_button_cancel_approve)
    Button child_btn_cancel_approve;

    @View(R.id.child_bayangan)
    Button child_bayangan;

    private Context mContext;
    private MkgServices data;

    public ChildRecyclerView(Context mContext, MkgServices data) {
        this.mContext = mContext;
        this.data = data;
    }

    @Resolve
    private void onResolve() {

        child_img_contact.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent goToDetail = new Intent(mContext, RelieverDetailActivity.class);
                goToDetail.putExtra("IDRELIEVER", data.id_reliever);
                mContext.startActivity(goToDetail);
            }
        });

        child_btn_cancel_approve.setOnClickListener(v -> {

        });

        child_img_location.setOnClickListener(v -> {

            Intent maps = new Intent(mContext, MapsViewActivity.class);
            maps.putExtra("FLAG_OSM", 888);
            maps.putExtra("POSISI_AWAL", data.child_location);
            maps.putExtra("NOMER_BC", data.child_contact);
            mContext.startActivity(maps);
        });

        child_text_name.setText(data.child_name);
        String status = "";
        if (data.child_status.equalsIgnoreCase("0")) {
            status = "waiting";
            child_bayangan.setVisibility(android.view.View.GONE);
            child_btn_cancel_approve.setVisibility(android.view.View.VISIBLE);
            child_btn_cancel_approve.setText("Cancel");
            child_btn_cancel_approve.setEnabled(true);
            child_btn_cancel_approve.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(android.view.View v) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Cancel");
                    alertBuilder.setMessage("Reliever " + data.child_name);
                    alertBuilder.setPositiveButton("By Reliever ",
                            (dialogInterface, i) -> {
                                Map<String, String> params = new HashMap<>();
                                params.put("id", data.id);
                                params.put("status", "5");
                                getDetail("https://bb.byonchat.com/ApiReliever/index.php/JobStatus", params, true);
                            });
                    alertBuilder.setNegativeButton("By Requester",
                            (dialogInterface, i) -> {
                                Map<String, String> params = new HashMap<>();
                                params.put("id", data.id);
                                params.put("status", "5");
                                params.put("opsi", "hapus");
                                getDetail("https://bb.byonchat.com/ApiReliever/index.php/JobStatus", params, true);
                            });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                }
            });

        } else if (data.child_status.equalsIgnoreCase("1")) {
            status = "Waiting Checkin";
            child_bayangan.setVisibility(android.view.View.VISIBLE);
            child_bayangan.setText("Approve");
            child_btn_cancel_approve.setVisibility(android.view.View.GONE);
            child_btn_cancel_approve.setEnabled(false);
        } else if (data.child_status.equalsIgnoreCase("2")) {
            status = "Confirm Checkin";
            child_bayangan.setVisibility(android.view.View.GONE);
            child_btn_cancel_approve.setVisibility(android.view.View.VISIBLE);
            child_btn_cancel_approve.setText("Approve");
            child_btn_cancel_approve.setEnabled(true);
            child_btn_cancel_approve.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(android.view.View v) {
                    Map<String, String> params = new HashMap<>();
                    params.put("id", data.id);
                    params.put("status", "3");

                    getDetail("https://bb.byonchat.com/ApiReliever/index.php/JobStatus", params, true);

                }
            });
        } else if (data.child_status.equalsIgnoreCase("3")) {
            status = "Waiting CheckOut";
            child_bayangan.setVisibility(android.view.View.VISIBLE);
            child_bayangan.setText("Approve");
            child_btn_cancel_approve.setVisibility(android.view.View.GONE);
        } else if (data.child_status.equalsIgnoreCase("4")) {
            child_bayangan.setVisibility(android.view.View.GONE);
            status = "Confirm CheckOut";
            child_btn_cancel_approve.setVisibility(android.view.View.VISIBLE);
            child_btn_cancel_approve.setText("Approve");
            child_btn_cancel_approve.setEnabled(true);
            child_btn_cancel_approve.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(android.view.View v) {

                    ((ByonchatBaseMallKelapaGadingActivity) mContext).setRating(data.id);


                }
            });
        } else if (data.child_status.equalsIgnoreCase("5")) {
            status = "Cancel";
            child_bayangan.setVisibility(android.view.View.GONE);
            child_btn_cancel_approve.setVisibility(android.view.View.GONE);
            child_img_location.setVisibility(android.view.View.GONE);

          /*  child_bayangan.setVisibility(android.view.View.GONE);
            child_btn_cancel_approve.setEnabled(true);
            child_btn_cancel_approve.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(android.view.View v) {
                    Map<String, String> params = new HashMap<>();
                    params.put("id", data.id);
                    params.put("status", "5");
                    getDetail("https://bb.byonchat.com/ApiReliever/index.php/JobStatus", params, true);
                }
            });*/
        } else if (data.child_status.equalsIgnoreCase("6")) {
            status = "Done";
            child_bayangan.setVisibility(android.view.View.GONE);
            child_btn_cancel_approve.setVisibility(android.view.View.GONE);
            child_img_location.setVisibility(android.view.View.GONE);

        } else {
            child_bayangan.setVisibility(android.view.View.GONE);
            child_btn_cancel_approve.setVisibility(android.view.View.GONE);
            child_img_location.setVisibility(android.view.View.GONE);
        }

        child_text_status.setText(status);

        child_text_distance.setText(data.child_distance);
    }


    private void getDetail(String Url, Map<String, String> params2, Boolean hide) {
        ProgressDialog rdialog = new ProgressDialog(mContext);
        rdialog.setMessage("Loading...");
        rdialog.show();

        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest sr = new StringRequest(Request.Method.POST, Url,
                response -> {
                    rdialog.dismiss();
                    if (hide) {
                        Toast.makeText(mContext, "sukses", Toast.LENGTH_SHORT).show();
                        ByonchatBaseMallKelapaGadingActivity ss = (ByonchatBaseMallKelapaGadingActivity) mContext;
                        ss.finish();
                    }

                },
                error -> rdialog.dismiss()
        ) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return params2;
            }
        };
        queue.add(sr);
    }

}