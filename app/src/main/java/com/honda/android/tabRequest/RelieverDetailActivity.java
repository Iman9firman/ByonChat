package com.honda.android.tabRequest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
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
import com.honda.android.ConversationActivity;
import com.honda.android.R;
import com.honda.android.communication.MessengerConnectionService;
import com.honda.android.helpers.Constants;
import com.honda.android.list.IconItem;
import com.honda.android.provider.ChatParty;
import com.honda.android.provider.Contact;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RelieverDetailActivity extends AppCompatActivity {

    CircularImageView image_detailReliever;
    RatingBar rating_detailReliever;
    TextView text_name_real_detailReliever, text_birthDate_real_detailReliever, text_address_real_detailReliever, text_jobExp_real_detailReliever, text_nik_detailreleiver;
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
        chat_this = (ImageView) findViewById(R.id.chat_this);

        String aa = getIntent().getStringExtra("IDRELIEVER");
        if (aa != null) {
            Map<String, String> params = new HashMap<>();
            params.put("id", aa);
            getDetail("https://bb.byonchat.com/ApiReliever/index.php/Reliever/detail", params);

        } else {
            Toast.makeText(getApplicationContext(), "Periksa kembali jaringan anda", Toast.LENGTH_SHORT).show();
            Log.w("popps 1","here");
            finish();
        }

        chat_this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatParty sample = new Contact(nama,noHp,"");
                IconItem item = new IconItem(noHp,nama,"","",sample);
                if (item.getJabberId().equalsIgnoreCase("")) {
                } else {
                    Intent intent = new Intent(getApplicationContext(), ConversationActivity.class);
                    String jabberId = item.getJabberId();
                    intent.putExtra(ConversationActivity.KEY_JABBER_ID, jabberId);
                    intent.putExtra(Constants.EXTRA_ITEM, item);
//                    intent.putExtra(Constants.EXTRA_COLOR, "000000");
//                    intent.putExtra(Constants.EXTRA_COLORTEXT, "000000");
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
                    Log.w("nupo", response + "");
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        Log.w("nututr", jsonObject + "");
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

                        if (foto.equalsIgnoreCase("-") || foto.equalsIgnoreCase("")) {
                            foto = "https://" + MessengerConnectionService.F_SERVER + "/toboldlygowherenoonehasgonebefore/" + noHp + ".jpg";
                        }

                        Picasso.with(RelieverDetailActivity.this.getApplicationContext()).load(foto).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(image_detailReliever);

                        text_name_real_detailReliever.setText(nama);
                        text_birthDate_real_detailReliever.setText(ttl);
                        text_address_real_detailReliever.setText(alamat);
                        text_jobExp_real_detailReliever.setText("Sejak " + join_date);
                        text_nik_detailreleiver.setText(nik);
                        rating_detailReliever.setRating(rating);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.w("nupo2", e + "");
                        Toast.makeText(getApplicationContext(), "Periksa kembali jaringan anda", Toast.LENGTH_SHORT).show();
                        Log.w("popps 2","here");
                        finish();
                    }

                },
                error -> {
                    Toast.makeText(getApplicationContext(), "Periksa kembali jaringan anda", Toast.LENGTH_SHORT).show();
                    Log.w("popps 3","here");
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
