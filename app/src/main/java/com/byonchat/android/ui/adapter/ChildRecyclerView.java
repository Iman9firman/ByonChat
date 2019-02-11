package com.byonchat.android.ui.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.byonchat.android.FragmentDinamicRoom.DinamicRoomSearchTaskActivity;
import com.byonchat.android.ISSActivity.Requester.ByonchatBaseMallKelapaGadingActivity;
import com.byonchat.android.R;
import com.byonchat.android.data.model.MkgServices;
import com.byonchat.android.tabRequest.MapsViewActivity;
import com.byonchat.android.tabRequest.RelieverDetailActivity;
import com.byonchat.android.tabRequest.RelieverListActivity;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.apache.http.message.BasicNameValuePair;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Layout(R.layout.mkg_child_layout)
public class ChildRecyclerView implements RatingDialogListener {

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
                Log.w("masu22k", "euy");
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
            child_btn_cancel_approve.setText("Cancel");
            child_btn_cancel_approve.setEnabled(true);
            child_btn_cancel_approve.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(android.view.View v) {
                    Map<String, String> params = new HashMap<>();
                    params.put("id", data.id);
                    params.put("status", "5");
                    getDetail("https://bb.byonchat.com/ApiReliever/index.php/JobStatus", params, true);

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
            child_btn_cancel_approve.setText("Approve");
            child_btn_cancel_approve.setEnabled(true);
            child_btn_cancel_approve.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(android.view.View v) {
                    Log.w("hahai", data.id);

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
            child_btn_cancel_approve.setText("Approve");
            child_btn_cancel_approve.setEnabled(true);
            child_btn_cancel_approve.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(android.view.View v) {

                    new AppRatingDialog.Builder()
                            .setPositiveButtonText("Submit")
                            .setNegativeButtonText("Cancel")
                            .setNoteDescriptions(Arrays.asList("Very Bad", "Bad", "Good", "Very Good", "Excellent !!!"))
                            .setDefaultRating(2)
                            .setTitle("Rate this Reliever")
                            .setDescription("Please select some stars and give your feedback")
                            .setCommentInputEnabled(true)
                            .setStarColor(R.color.yelow)
                            .setTitleTextColor(R.color.black_alpha_50)
                            .setDescriptionTextColor(R.color.black_alpha_50)
                            .setHint("Please write your comment here ...")
                            .setCommentBackgroundColor(R.color.grayList)
                            .setCancelable(false)
                            .setCanceledOnTouchOutside(false)
                            .create((FragmentActivity) mContext)
                            .show();


                    Map<String, String> paramsLog = new HashMap<>();
                    paramsLog.put("id", data.id);
                    paramsLog.put("rating", "2");
                    paramsLog.put("note", "Bagus");

                    getDetail("https://bb.byonchat.com/ApiReliever/index.php/Rating/reliever", paramsLog, false);

                    Map<String, String> params = new HashMap<>();
                    params.put("id", data.id);
                    params.put("status", "6");
                    getDetail("https://bb.byonchat.com/ApiReliever/index.php/JobStatus", params, true);


                }
            });
        } else if (data.child_status.equalsIgnoreCase("5")) {
            status = "Cancel";
            child_bayangan.setVisibility(android.view.View.GONE);
            child_btn_cancel_approve.setEnabled(true);
            child_btn_cancel_approve.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(android.view.View v) {
                    Log.w("hahai", data.id);
                    Map<String, String> params = new HashMap<>();
                    params.put("id", data.id);
                    params.put("status", "5");
                    getDetail("https://bb.byonchat.com/ApiReliever/index.php/JobStatus", params, true);
                }
            });
        } else if (data.child_status.equalsIgnoreCase("6")) {
            status = "Done";
            child_bayangan.setVisibility(android.view.View.GONE);
            child_btn_cancel_approve.setVisibility(android.view.View.GONE);
        } else {
            child_bayangan.setVisibility(android.view.View.GONE);
            child_btn_cancel_approve.setVisibility(android.view.View.GONE);
        }

        child_text_status.setText(status);

        child_text_distance.setText("");
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

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int i, @NotNull String s) {
        Log.w("bau", "amis");
    }
}
