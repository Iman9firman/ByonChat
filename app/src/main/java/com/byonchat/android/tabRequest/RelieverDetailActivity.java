package com.byonchat.android.tabRequest;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.byonchat.android.ISSActivity.Requester.ByonchatBaseMallKelapaGadingActivity;
import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.data.model.MkgServices;
import com.byonchat.android.ui.adapter.ChildRecyclerView;
import com.byonchat.android.ui.adapter.HeaderRecyclerView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RelieverDetailActivity extends AppCompatActivity {

    CircularImageView image_detailReliever;
    RatingBar rating_detailReliever;
    TextView text_name_real_detailReliever, text_birthDate_real_detailReliever, text_address_real_detailReliever, text_jobExp_real_detailReliever, text_nik_detailreleiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reliever_detail);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#022b95")));
        getSupportActionBar().setTitle("Detail Reliever");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.parseColor("#022b95"));
        }

        image_detailReliever = (CircularImageView) findViewById(R.id.image_detailReliever);
        rating_detailReliever = (RatingBar) findViewById(R.id.rating_detailReliever);
        text_name_real_detailReliever = (TextView) findViewById(R.id.text_name_real_detailReliever);
        text_birthDate_real_detailReliever = (TextView) findViewById(R.id.text_birthDate_real_detailReliever);
        text_address_real_detailReliever = (TextView) findViewById(R.id.text_address_real_detailReliever);
        text_jobExp_real_detailReliever = (TextView) findViewById(R.id.text_jobExp_real_detailReliever);
        text_nik_detailreleiver = (TextView) findViewById(R.id.text_nik_detailReliever);


        String aa = getIntent().getStringExtra("IDRELIEVER");
        if (aa != null) {
            Map<String, String> params = new HashMap<>();
            params.put("id", aa);
            getDetail("https://bb.byonchat.com/ApiReliever/index.php/Reliever/detail", params);

        }else{
            Map<String, String> params = new HashMap<>();
            params.put("id", 204+"");
            getDetail("https://bb.byonchat.com/ApiReliever/index.php/Reliever/detail", params);
        }
    }


    private void getDetail(String Url, Map<String, String> params2) {

        ProgressDialog rdialog = new ProgressDialog(RelieverDetailActivity.this);
        rdialog.setMessage("Loading...");
        rdialog.show();

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        StringRequest sr = new StringRequest(Request.Method.POST, Url,
                response -> {
                    rdialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        Log.w("Hihfioegh nututr", jsonObject+"");
//                        JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
//                        if (jsonArray.length() > 0) {
//                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jOb = jsonObject.getJSONObject("data");
//                                JSONObject jOb = jsonArray.getJSONObject(i);
                                String noHp = jOb.getString("bc_user");
                                float rating = Float.valueOf(jOb.getString("rating"));
                                String email = jOb.getString("email");
                                String nama = jOb.getString("nama");
                                String foto = jOb.getString("foto");
                                String ttl = jOb.getString("ttl");
                                String nik = jOb.getString("nik");
                                String join_date = jOb.getString("join_date");

                                if (foto.equalsIgnoreCase("-") || foto.equalsIgnoreCase("")) {
                                    foto =  "https://" + MessengerConnectionService.F_SERVER + "/toboldlygowherenoonehasgonebefore/" + noHp + ".jpg";
                                }

                                Picasso.with(RelieverDetailActivity.this.getApplicationContext()).load(foto).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(image_detailReliever);

                                text_name_real_detailReliever.setText(nama);
                                text_birthDate_real_detailReliever.setText(ttl);
                                text_address_real_detailReliever.setText("");
                                text_jobExp_real_detailReliever.setText("Sejak " + join_date);
                                text_nik_detailreleiver.setText(nik);
                                rating_detailReliever.setRating(rating);

//                            }
//                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                },
                error -> {
                    rdialog.dismiss();
                }
        ) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return params2;
            }
        };
        queue.add(sr);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
