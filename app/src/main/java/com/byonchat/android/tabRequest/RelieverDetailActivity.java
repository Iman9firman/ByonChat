package com.byonchat.android.tabRequest;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.byonchat.android.ConversationActivity;
import com.byonchat.android.ISSActivity.Requester.ByonchatBaseMallKelapaGadingActivity;
import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.data.model.MkgServices;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.list.IconItem;
import com.byonchat.android.provider.ChatParty;
import com.byonchat.android.provider.Contact;
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
    TextView text_bc_user, text_email, text_gender, text_total_kerja;
    ImageView chat_this;
    String nama, noHp;

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

        text_bc_user  = (TextView) findViewById(R.id.text_real_number);
        text_email  = (TextView) findViewById(R.id.text_real_email);
        text_gender  = (TextView) findViewById(R.id.text_real_gender);
        text_total_kerja  = (TextView) findViewById(R.id.text_real_total_kerja);
        chat_this = (ImageView) findViewById(R.id.chat_this);

        String aa = getIntent().getStringExtra("IDRELIEVER");
        if (aa != null) {
            Map<String, String> params = new HashMap<>();
            params.put("id", aa);
            getDetail("https://bb.byonchat.com/ApiReliever/index.php/Reliever/detail", params);

        } else {
            Toast.makeText(getApplicationContext(), "Periksa kembali jaringan anda", Toast.LENGTH_SHORT).show();
            finish();
        }

        chat_this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatParty sample = new Contact(nama,noHp,"");
                IconItem item = new IconItem(noHp,nama,"","",sample);
                if (item.getJabberId().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(),"Nomor telepon reliever ini belum terdaftar",Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), ConversationActivity.class);
                    String jabberId = item.getJabberId();
                    intent.putExtra(ConversationActivity.KEY_JABBER_ID, jabberId);
                    intent.putExtra(Constants.EXTRA_ITEM, item);
                    startActivity(intent);
                }
            }
        });
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

//                        JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
//                        if (jsonArray.length() > 0) {
//                            for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jOb = jsonObject.getJSONObject("data");
//                                JSONObject jOb = jsonArray.getJSONObject(i);
                        noHp = jOb.getString("bc_user");
                        String rat = jOb.getString("rating");
                        float rating = 0;
                        if (!rat.equalsIgnoreCase("")) {
                            rating = Float.valueOf(jOb.getString("rating"));
                        }

                        String email = jOb.getString("email");
                        nama = jOb.getString("nama");
                        String foto = jOb.getString("foto");
                        String ttl = jOb.getString("ttl");
                        String nik = jOb.getString("nik");
                        String alamat = jOb.getString("alamat");
                        String join_date = jOb.getString("join_date");
                        String nohape = jOb.getString("no_hp");
                        String total_kerja = jOb.getString("total_kerja");
                        String gender;
                        if(jOb.has("gender")) {
                            gender = jOb.getString("gender");
                        }else {
                            gender = "-";
                        }

                        if (foto.equalsIgnoreCase("-") || foto.equalsIgnoreCase("")) {
                            foto = "https://" + MessengerConnectionService.F_SERVER + "/toboldlygowherenoonehasgonebefore/" + noHp + ".jpg";
                        }

                        Picasso.with(RelieverDetailActivity.this.getApplicationContext()).load(foto).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(image_detailReliever);

                        text_name_real_detailReliever.setText(nama);
                        text_nik_detailreleiver.setText(nik);
                        rating_detailReliever.setRating(rating);
                        text_birthDate_real_detailReliever.setText(ttl);
                        text_address_real_detailReliever.setText(alamat);
                        text_jobExp_real_detailReliever.setText("Sejak " + join_date);
                        text_bc_user.setText("+"+nohape);
                        text_gender.setText(gender);
                        text_email.setText(email);

                        if(total_kerja.equalsIgnoreCase("0") || total_kerja.equalsIgnoreCase("")){
                            text_total_kerja.setText("pekerja baru");
                        }else {
                            text_total_kerja.setText(total_kerja + "x bekerja");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Periksa kembali jaringan anda", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                },
                error -> {
                    Toast.makeText(getApplicationContext(), "Periksa kembali jaringan anda", Toast.LENGTH_SHORT).show();
                    finish();
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
