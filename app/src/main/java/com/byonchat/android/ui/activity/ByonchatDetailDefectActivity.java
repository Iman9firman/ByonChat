package com.byonchat.android.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.byonchat.android.DownloadFileByonchat;
import com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
import com.byonchat.android.FragmentDinamicRoom.FormChildAdapter;
import com.byonchat.android.FragmentDinamicRoom.ModelFormChild;
import com.byonchat.android.R;
import com.byonchat.android.ZoomImageViewActivity;
import com.byonchat.android.createMeme.FilteringImage;
import com.byonchat.android.provider.Message;
import com.byonchat.android.utils.DialogUtil;
import com.byonchat.android.utils.Validations;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sufficientlysecure.htmltextview.HtmlResImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class ByonchatDetailDefectActivity extends AppCompatActivity {
    LinearLayout linearLayout, masterlinearValue;
    JSONArray jsonArray;
    String title, color;
    String idTab;
    String idDetail;
    Map<Integer, String> idFormChildParent = new HashMap<Integer, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dinamic_room_task);

        title = getIntent().getStringExtra("tt");
        color = getIntent().getStringExtra("col");
        idTab = getIntent().getStringExtra("ii");
        idDetail = getIntent().getStringExtra("idTask");

        linearLayout = (LinearLayout) findViewById(R.id.linear);
        masterlinearValue = (LinearLayout) findViewById(R.id.linearValue);
        linearLayout.setVisibility(View.GONE);
        masterlinearValue.setVisibility(View.VISIBLE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_35dp);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#" + color)));
        FilteringImage.SystemBarBackground(getWindow(), Color.parseColor("#" + color));

        String data = getIntent().getStringExtra("data");
        resolveJson(data);
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

    protected void resolveJson(String jsonnya){
        try {
        JSONObject jsonRootObject = new JSONObject(jsonnya);

            JSONArray list_pull = jsonRootObject.getJSONArray("list_pull");
            getDinamic(""+list_pull);

        } catch (JSONException e) {
            Log.e("Error kecnerjkvivr",e.getMessage());
            e.printStackTrace();
        }
    }

    private void getDinamic(String contentValue){
        try {
            jsonArray = new JSONArray(contentValue);
            for (int ii = (jsonArray.length() - 1); ii >= 0; ii--) {

                JSONArray magic = new JSONArray(jsonArray.getJSONArray(ii).toString());

                JSONObject oContent = new JSONObject(magic.get(0).toString());
                JSONObject oContent2 = new JSONObject(magic.get(1).toString());
                JSONArray joContent = oContent2.getJSONArray("value_detail");
                String bc_user = oContent.getString("bc_user");

                JSONObject oUser = new JSONObject(bc_user);

                LinearLayout MMlinearValue = (LinearLayout) getLayoutInflater().inflate(R.layout.list_value_form_detail, null);
                LinearLayout linearDetailValue = (LinearLayout) MMlinearValue.findViewById(R.id.linearDetailValue);

                if (ii % 2 == 0) {
                    linearDetailValue.setBackgroundResource(R.drawable.backgroud_gray_young);
                } else {
                    linearDetailValue.setBackgroundResource(R.drawable.backgroud_gray);
                }

                masterlinearValue.addView(MMlinearValue);
                LinearLayout.LayoutParams parampps = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                parampps.setMargins(0, 0, 0, 10);
                MMlinearValue.setLayoutParams(parampps);

                if (magic.get(0).toString().contains("tab_name")) {
                    String tb = oContent.getString("tab_name");
                    //TextView tabFormName = (TextView) MMlinearValue.findViewById(R.id.tab_form_name);
                    LinearLayout linearLayoutTabName = (LinearLayout) MMlinearValue.findViewById(R.id.linearLayoutTabName);
                    TextView tabName = (TextView) MMlinearValue.findViewById(R.id.tabName);
                    tabName.setText(tb);
                    linearLayoutTabName.setVisibility(View.VISIBLE);
                    // tabFormName.setVisibility(View.VISIBLE);
                }

                Log.w("Checkkief udeg ziz","yapp");

                LinearLayout submitLinear = (LinearLayout) MMlinearValue.findViewById(R.id.linearSubmit);

                TextView textSubmit = new TextView(this);
                textSubmit.setText(Html.fromHtml("Submit From"));
                textSubmit.setTextSize(17);
                textSubmit.setLayoutParams(new TableRow.LayoutParams(0));

                TextView etVIn1 = (TextView) new TextView(getApplicationContext());
                etVIn1.setTextIsSelectable(true);
                etVIn1.setText(Html.fromHtml(oUser.getString("no_telp")));
                TextView etVIn2 = (TextView) new TextView(getApplicationContext());
                etVIn2.setTextIsSelectable(true);
                etVIn2.setText(Html.fromHtml(oUser.getString("name")));
                TextView etVIn3 = (TextView) new TextView(getApplicationContext());
                etVIn3.setTextIsSelectable(true);
                etVIn3.setText(Html.fromHtml(oUser.getString("divisi")));
                TextView etVIn4 = (TextView) new TextView(getApplicationContext());
                etVIn4.setTextIsSelectable(true);
                etVIn4.setText(Html.fromHtml(oUser.getString("jabatan")));
                TextView etVIn5 = (TextView) new TextView(getApplicationContext());
                etVIn5.setTextIsSelectable(true);
                etVIn5.setText(Html.fromHtml(oUser.getString("lokasi")));
                TextView etVIn6 = (TextView) MMlinearValue.findViewById(R.id.dateTxt);
                etVIn6.setTextIsSelectable(true);
                etVIn6.setText(oContent.getString("add_date"));

                LinearLayout lineIn = (LinearLayout) getLayoutInflater().inflate(R.layout.line_horizontal, null);
                LinearLayout.LayoutParams params11In = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params11In.setMargins(10, 10, 30, 0);
                LinearLayout.LayoutParams params22in = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params22in.setMargins(50, 0, 30, 0);
                LinearLayout.LayoutParams params22Last = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params22Last.setMargins(50, 10, 30, 30);

                submitLinear.addView(textSubmit, params11In);
                submitLinear.addView(lineIn, params11In);
                submitLinear.addView(etVIn1, params22in);
                submitLinear.addView(etVIn2, params22in);
                submitLinear.addView(etVIn3, params22in);
                submitLinear.addView(etVIn4, params22in);
                submitLinear.addView(etVIn5, params22Last);

                if (oContent.has("status")) {
                    if (!oContent.getString("status").equalsIgnoreCase("null") || oContent.getString("status") == null) {
                        TextView statusApprove = new TextView(this);
                        statusApprove.setText(Html.fromHtml("Status"));
                        statusApprove.setTextSize(17);
                        statusApprove.setLayoutParams(new TableRow.LayoutParams(0));

                        LinearLayout lineIn2 = (LinearLayout) getLayoutInflater().inflate(R.layout.line_horizontal, null);

                        TextView eeStat = (TextView) new TextView(getApplicationContext());
                        eeStat.setTextIsSelectable(true);
                        String sSttus = oContent.getString("status");

                        if (oContent.getString("status").equalsIgnoreCase("0")) {
                            sSttus = "Reject";
                        } else if (oContent.getString("status").equalsIgnoreCase("1")) {
                            sSttus = "Approve";
                        } else if (oContent.getString("status").equalsIgnoreCase("2")) {
                            sSttus = "Done";
                        }

                        eeStat.setText(sSttus);

                        submitLinear.addView(statusApprove, params11In);
                        submitLinear.addView(lineIn2, params11In);
                        submitLinear.addView(eeStat, params22Last);
                    }
                }

                LinearLayout linearValue = (LinearLayout) MMlinearValue.findViewById(R.id.linearValueDetail);

                for (int i = 0; i < joContent.length(); i++) {
                    final String label = joContent.getJSONObject(i).getString("label").toString();
                    final String value = joContent.getJSONObject(i).getString("value").toString();
                    final String type = joContent.getJSONObject(i).getString("type").toString();
                    final String idValue = joContent.getJSONObject(i).getString("id").toString();

                    /*if (showParentView(joContent, idValue, label)) {*/

                        if (!value.equalsIgnoreCase("-")) {

                            if (type.equalsIgnoreCase("dropdown_views")) {
                                TextView textV = new TextView(this);
                                textV.setText(Html.fromHtml(label));
                                textV.setTextSize(17);
                                textV.setLayoutParams(new TableRow.LayoutParams(0));

                                JSONObject jObject = new JSONObject(value);
                                Log.w("valuePairs", value);

                                String vl = jObject.getString("value");

                               /* if (jObject.has("dropdown_view_id")) {
                                    dropdownViewIdParent = jObject.getString("dropdown_view_id");
                                }*/

                                TextView etV = (TextView) new TextView(getApplicationContext());
                                etV.setTextIsSelectable(true);
                                etV.setText(Html.fromHtml(vl));
                                LinearLayout line = (LinearLayout) getLayoutInflater().inflate(R.layout.line_horizontal, null);
                                LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params11.setMargins(10, 10, 30, 0);
                                LinearLayout.LayoutParams params22 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params22.setMargins(50, 10, 30, 30);
                                linearValue.addView(textV, params11);
                                linearValue.addView(line, params11);
                                linearValue.addView(etV, params22);


                            } else if (type.equalsIgnoreCase("attach_api")) {

                                TextView textV = new TextView(this);
                                textV.setText(Html.fromHtml(label));
                                textV.setTextSize(17);
                                textV.setLayoutParams(new TableRow.LayoutParams(0));


                                LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.api_text_layout_form, null);
                                //final TextView valueFile = (TextView) linearLayout.findViewById(R.id.value);
                                HtmlTextView htmlTextView = (HtmlTextView) linearLayout.findViewById(R.id.value);
                                htmlTextView.setTextIsSelectable(true);

                                AVLoadingIndicatorView progress = (AVLoadingIndicatorView) linearLayout.findViewById(R.id.loader_progress);

                                if (!value.equalsIgnoreCase("")) {
                                    SaveMedia saveMedia = new SaveMedia();
                                    if (value.startsWith("Rp.")) {
                                        saveMedia.execute(new MyTaskParams(htmlTextView, progress, value));
                                    } else {
                                        saveMedia.execute(new MyTaskParams(htmlTextView, progress, value.replace(" ", "%")));
                                    }
                                }

                                LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params11.setMargins(10, 10, 30, 0);
                                LinearLayout.LayoutParams params22 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params22.setMargins(50, 10, 30, 30);
                                linearValue.addView(textV, params11);
                                linearValue.addView(linearLayout, params22);


                            } else if (type.equalsIgnoreCase("rear_camera") || type.equalsIgnoreCase("front_camera") || type.equalsIgnoreCase("signature")) {
                                TextView textView = new TextView(this);
                                textView.setText(Html.fromHtml(label));
                                textView.setTextSize(17);
                                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params2.setMargins(10, 10, 30, 0);
                                textView.setLayoutParams(params2);
                                linearValue.addView(textView);
                                LinearLayout line = (LinearLayout) getLayoutInflater().inflate(R.layout.line_horizontal, null);
                                linearValue.addView(line, params2);
                                LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.image_loader_layout_form, null);

                                final ImageView imageView = (ImageView) linearLayout.findViewById(R.id.value);
                                final AVLoadingIndicatorView progress = (AVLoadingIndicatorView) linearLayout.findViewById(R.id.loader_progress);

                                Picasso.with(getApplicationContext()).load(value).into(imageView);

                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        Intent intent = new Intent(getApplicationContext(), ZoomImageViewActivity.class);
                                        intent.putExtra(ZoomImageViewActivity.KEY_FILE, value);
                                        startActivity(intent);
                                    }
                                });


                                int width = getWindowManager().getDefaultDisplay().getWidth();
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, width / 2);
                                params.setMargins(5, 15, 0, 30);

                                linearLayout.setLayoutParams(params);
                                linearValue.addView(linearLayout);


                            } else if (type.equalsIgnoreCase("input_kodepos") || type.equalsIgnoreCase("dropdown_wilayah")) {
                                TextView textV = new TextView(this);
                                textV.setText(Html.fromHtml(label));
                                textV.setTextSize(17);
                                textV.setLayoutParams(new TableRow.LayoutParams(0));


                                TextView etV = (TextView) new TextView(getApplicationContext());
                                etV.setTextIsSelectable(true);

                                LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params11.setMargins(10, 10, 30, 0);
                                LinearLayout.LayoutParams params22 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params22.setMargins(50, 10, 30, 30);
                                linearValue.addView(textV, params11);
                                LinearLayout line = (LinearLayout) getLayoutInflater().inflate(R.layout.line_horizontal, null);
                                linearValue.addView(line, params11);
                                linearValue.addView(etV, params22);

                            } else if (type.equalsIgnoreCase("dropdown_dinamis") || type.equalsIgnoreCase("new_dropdown_dinamis") || type.equalsIgnoreCase("master_data")) {


                                if (!value.equalsIgnoreCase("-")) {

                                    TextView textV = new TextView(this);
                                    textV.setText(Html.fromHtml(label));
                                    textV.setTextSize(17);
                                    textV.setLayoutParams(new TableRow.LayoutParams(0));

                                    JSONObject jsonObject = new JSONObject(value);
                                    Iterator<String> keys = jsonObject.keys();


                                    LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    params11.setMargins(10, 10, 30, 0);
                                    LinearLayout.LayoutParams params22 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    params22.setMargins(50, 10, 30, 30);
                                    linearValue.addView(textV, params11);
                                    LinearLayout line = (LinearLayout) getLayoutInflater().inflate(R.layout.line_horizontal, null);
                                    linearValue.addView(line, params11);

                                    List<String> keysList = new ArrayList<String>();
                                    while (keys.hasNext()) {
                                        keysList.add(keys.next());
                                    }

                                    String longi = null, lanti = null;
                                    for (String aa : keysList) {
                                        if (jsonObject.getString(aa).startsWith("https://bb.byonchat.com/") && jsonObject.getString(aa).endsWith(".png")) {
                                            TextView etV = (TextView) new TextView(getApplicationContext());
                                            etV.setTextIsSelectable(true);
                                            etV.setText(String.valueOf(aa));
                                            linearValue.addView(etV, params22);

                                            LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.image_loader_layout_form, null);
                                            final ImageView imageView = (ImageView) linearLayout.findViewById(R.id.value);
                                            Picasso.with(getApplicationContext()).load(jsonObject.getString(aa)).into(imageView);
                                            final String abab = jsonObject.getString(aa);
                                            linearValue.addView(linearLayout, params22);
                                            imageView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                    Intent intent = new Intent(getApplicationContext(), ZoomImageViewActivity.class);
                                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE, abab);
                                                    startActivity(intent);
                                                }
                                            });


                                        } else {
                                            TextView etV = (TextView) new TextView(getApplicationContext());
                                            etV.setTextIsSelectable(true);
                                            etV.setText(String.valueOf(aa) + " = " + jsonObject.getString(aa));
                                            linearValue.addView(etV, params22);

                                        }

                                        if (String.valueOf(aa).equalsIgnoreCase("Longitude")) {
                                            longi = jsonObject.getString(aa);
                                        } else if (String.valueOf(aa).equalsIgnoreCase("Latitude")) {
                                            lanti = jsonObject.getString(aa);
                                        }
                                    }


                                    if (longi != null && lanti != null) {
                                        final Double l1 = Double.parseDouble(lanti);
                                        final Double l2 = Double.parseDouble(longi);

                                        ImageView imageView = new ImageView(this);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            imageView.setImageDrawable(getDrawable(R.drawable.ic_att_location));
                                        } else {
                                            imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_att_location));
                                        }


                                        imageView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + l1 + ","
                                                        + l2 + "(" + "Location" + ")");

                                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                                mapIntent.setPackage("com.google.android.apps.maps");
                                                startActivity(mapIntent);
                                            }
                                        });

                                        TextView textMap = new TextView(this);
                                        textMap.setText("Location");
                                        textMap.setTextSize(18);
                                        textMap.setLayoutParams(new TableRow.LayoutParams(0));
                                        linearValue.addView(textMap, params11);
                                        LinearLayout line2 = (LinearLayout) getLayoutInflater().inflate(R.layout.line_horizontal, null);
                                        linearValue.addView(line2, params11);
                                        int width = getWindowManager().getDefaultDisplay().getWidth();
                                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, width / 5);
                                        params.setMargins(5, 15, 0, 30);
                                        imageView.setLayoutParams(params);
                                        linearValue.addView(imageView);
                                    }
                                }


                            } else if (type.equalsIgnoreCase("checkbox")) {

                                TextView textV = new TextView(this);
                                textV.setText(Html.fromHtml(label));
                                textV.setTextSize(17);
                                textV.setLayoutParams(new TableRow.LayoutParams(0));


                                TextView etV = new TextView(getApplicationContext());
                                etV.setTextIsSelectable(true);

                                String aa = "";
                                JSONArray jsA = new JSONArray(value);
                                for (int ic = 0; ic < jsA.length(); ic++) {
                                    aa += "• " + jsA.getString(ic);
                                    if (ic < jsA.length() - 1) {
                                        aa += "\n";
                                    }
                                }
                                etV.setText(aa);

                                LinearLayout line = (LinearLayout) getLayoutInflater().inflate(R.layout.line_horizontal, null);

                                LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params11.setMargins(10, 10, 30, 0);
                                LinearLayout.LayoutParams params22 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params22.setMargins(50, 10, 30, 30);
                                linearValue.addView(textV, params11);
                                linearValue.addView(line, params11);
                                linearValue.addView(etV, params22);

                            } else if (type.equalsIgnoreCase("upload_document")) {
                                TextView textView = new TextView(this);
                                textView.setText(Html.fromHtml(label));
                                textView.setTextSize(17);
                                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params2.setMargins(30, 10, 30, 0);
                                textView.setLayoutParams(params2);
                                linearValue.addView(textView);

                                LinearLayout lilin = (LinearLayout) getLayoutInflater().inflate(R.layout.upload_doc_layout, null);
                                lilin.setLayoutParams(params2);
                                linearValue.addView(lilin);

                                final Button btnOption = (Button) lilin.findViewById(R.id.btn_browse);
                                final TextView valueFile = (TextView) lilin.findViewById(R.id.value);

                                valueFile.setText(value.substring(value.toString().lastIndexOf('/'), value.toString().length()));
                                btnOption.setText("View");
                                final String cc = value;
                                btnOption.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getApplicationContext(), DownloadFileByonchat.class);
                                        intent.putExtra("path", cc);
                                        intent.putExtra("nama_file", valueFile.getText().toString());
                                        startActivity(intent);
                                    }
                                });

                            } else if (type.equalsIgnoreCase("distance_estimation")) {
                                TextView textView = new TextView(this);
                                textView.setText(Html.fromHtml(label));
                                textView.setTextSize(17);
                                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params2.setMargins(30, 10, 30, 0);
                                params3.setMargins(30, 10, 30, 30);
                                textView.setLayoutParams(params2);
                                linearValue.addView(textView);

                                LinearLayout linearEstimasi = (LinearLayout) getLayoutInflater().inflate(R.layout.estimation_layout, null);
                                linearEstimasi.setLayoutParams(params2);
                                linearValue.addView(linearEstimasi);


                                TextView start = (TextView) linearEstimasi.findViewById(R.id.valuePickup);
                                TextView end = (TextView) linearEstimasi.findViewById(R.id.valueEnd);
                                TextView jarak = (TextView) linearEstimasi.findViewById(R.id.valueJarak);

                            } else if (type.equalsIgnoreCase("map")) {
                                TextView textV = new TextView(this);
                                textV.setText(Html.fromHtml(label));
                                textV.setTextSize(17);
                                textV.setLayoutParams(new TableRow.LayoutParams(0));

                                EditText etV = (EditText) getLayoutInflater().inflate(R.layout.edit_input_layout, null);
                                etV.setFocusable(false);
                                etV.setFocusableInTouchMode(false);

                                String valueMap = "";
                                try {
                                    valueMap = URLDecoder.decode(value, "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }

                                if (valueMap.contains("%20")) {

                                    try {
                                        valueMap = URLDecoder.decode(valueMap, "UTF-8");
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }

                                    String[] latlong = valueMap.split(" ");

                                    if (latlong.length > 4) {
                                        etV.setText(Html.fromHtml(valueMap.substring(latlong[0].length() + latlong[1].length() + 2, valueMap.length())));
                                    }

                                    final String finalValueMap = valueMap;
                                    etV.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            if (latlong.length > 3) {
                                                Uri gmmIntentUri = null;
                                                if (latlong[3].equalsIgnoreCase("")) {
                                                    gmmIntentUri = Uri.parse("geo:0,0?q=" + Double
                                                            .parseDouble(latlong[0]) + ","
                                                            + Double.parseDouble(latlong[1]) + "(" + "You" + ")");
                                                } else {
                                                    String loc = latlong[2] + "+,+" + latlong[3];
                                                    gmmIntentUri = Uri.parse("geo:0,0?q=" + Double.parseDouble(latlong[0]) + ","
                                                            + Double.parseDouble(latlong[1]) + "(" + loc + ")");
                                                }

                                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                                mapIntent.setPackage("com.google.android.apps.maps");
                                                startActivity(mapIntent);
                                            }
                                        }
                                    });

                                    LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    params11.setMargins(20, 10, 30, 0);
                                    LinearLayout.LayoutParams params22 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    params22.setMargins(20, 10, 30, 30);
                                    linearValue.addView(textV, params11);
                                    linearValue.addView(etV, params22);

                                } else {

                                    String[] latlong = valueMap.split(
                                            Message.LOCATION_DELIMITER);

                                    Log.w("Hau3", latlong.length + "");

                                    if (latlong.length > 4) {
                                        etV.setText(Html.fromHtml(latlong[3]));
                                    }

                                    final String finalValueMap = valueMap;
                                    etV.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String[] latlong = finalValueMap.split(
                                                    Message.LOCATION_DELIMITER);

                                            if (latlong.length > 3) {
                                                Uri gmmIntentUri = null;
                                                if (latlong[3].equalsIgnoreCase("")) {
                                                    gmmIntentUri = Uri.parse("geo:0,0?q=" + Double
                                                            .parseDouble(latlong[0]) + ","
                                                            + Double.parseDouble(latlong[1]) + "(" + "You" + ")");
                                                } else {
                                                    String loc = latlong[2] + "+,+" + latlong[3];
                                                    gmmIntentUri = Uri.parse("geo:0,0?q=" + Double.parseDouble(latlong[0]) + ","
                                                            + Double.parseDouble(latlong[1]) + "(" + loc + ")");
                                                }

                                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                                mapIntent.setPackage("com.google.android.apps.maps");
                                                startActivity(mapIntent);
                                            }
                                        }
                                    });

                                    LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    params11.setMargins(20, 10, 30, 0);
                                    LinearLayout.LayoutParams params22 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    params22.setMargins(20, 10, 30, 30);
                                    linearValue.addView(textV, params11);
                                    linearValue.addView(etV, params22);

                                }

                            } else if (type.equalsIgnoreCase("number")) {

                                TextView textV = new TextView(this);
                                textV.setText(Html.fromHtml(label));
                                textV.setTextSize(17);
                                textV.setLayoutParams(new TableRow.LayoutParams(0));


                                TextView etV = (TextView) new TextView(getApplicationContext());
                                etV.setTextIsSelectable(true);
                                etV.setText(Html.fromHtml(value));
                                LinearLayout line = (LinearLayout) getLayoutInflater().inflate(R.layout.line_horizontal, null);
                                LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params11.setMargins(10, 10, 30, 0);
                                LinearLayout.LayoutParams params22 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params22.setMargins(50, 10, 30, 30);
                            }  else if (type.equalsIgnoreCase("form_child")) {


                                JSONObject nn = new JSONObject(joContent.getJSONObject(i).toString());

                                idFormChildParent.put(Integer.valueOf(nn.getString("id")), joContent.getJSONObject(i).toString());

                                TextView textV = new TextView(this);
                                textV.setText(Html.fromHtml(label));
                                textV.setTextSize(17);
                                textV.setLayoutParams(new TableRow.LayoutParams(0));

                                String[] ff = idDetail.split("\\|");
                                String iidd = "";
                                if (ff.length == 2) {
                                    iidd = ff[1];
                                }

                                LinearLayout line = (LinearLayout) getLayoutInflater().inflate(R.layout.from_cild_layout_value, null);
                                Button addCild = (Button) line.findViewById(R.id.btn_add_cild);
                                ListView lv = (ListView) line.findViewById(R.id.listOrder);
                                TextView tQty = null;
                                TextView tPrice = null;
                                TableRow tableRow2 = null;

                                if (iidd.equalsIgnoreCase("1643") || iidd.equalsIgnoreCase("1628") || iidd.equalsIgnoreCase("1632") || iidd.equalsIgnoreCase("2021")
                                        || (Integer.valueOf(iidd) >= 2092)) {
                                    line = (LinearLayout) getLayoutInflater().inflate(R.layout.form_cild_two_layout_value, null);
                                    if (Integer.valueOf(iidd) >= 2092) {
                                        TextView nama = (TextView) line.findViewById(R.id.nama);
                                        nama.setVisibility(View.GONE);
                                    }
                                    lv = (ListView) line.findViewById(R.id.listOrder);
                                    addCild = (Button) line.findViewById(R.id.btn_add_cild);

                                    tQty = (TextView) line.findViewById(R.id.total_detail_order);
                                    tPrice = (TextView) line.findViewById(R.id.total_price_order);
                                    tableRow2 = (TableRow) line.findViewById(R.id.tableRow2);

                                } else {
                                    tQty = (TextView) line.findViewById(R.id.total_detail_order);
                                    tPrice = (TextView) line.findViewById(R.id.total_price_order);
                                    tableRow2 = (TableRow) line.findViewById(R.id.tableRow2);
                                }

                                lv.setOnTouchListener(new View.OnTouchListener() {
                                    // Setting on Touch Listener for handling the touch inside ScrollView
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        v.getParent().requestDisallowInterceptTouchEvent(true);
                                        return false;
                                    }
                                });

                                addCild.setVisibility(View.GONE);

                                final ArrayList<ModelFormChild> rowItems = new ArrayList<ModelFormChild>();
                                final ArrayList<String> labelDialog = new ArrayList<String>();
                                JSONArray jsonarray = new JSONArray(value);

                                if (Integer.valueOf(iidd) >= 2092) {
                                    for (int aaa = 0; aaa < jsonarray.length(); aaa++) {
                                        JSONObject jsonobject = jsonarray.getJSONObject(aaa);
                                        String urutan = jsonobject.getString("urutan");
                                        String titleUntuk = "";
                                        String decsUntuk = "";
                                        String priceUntuk = "";
                                        String data = jsonobject.getString("data");

                                        JSONArray jsonarrayChild = new JSONArray(data);
                                        boolean image = false;
                                        for (int bbb = 0; bbb < jsonarrayChild.length(); bbb++) {
                                            final String key = jsonarrayChild.getJSONObject(bbb).getString("key").toString();
                                            final String val = jsonarrayChild.getJSONObject(bbb).getString("value").toString();
                                            final String typ = jsonarrayChild.getJSONObject(bbb).getString("type").toString();
                                            String lab = "";
                                            if (jsonarrayChild.getJSONObject(bbb).has("label")) {
                                                lab = jsonarrayChild.getJSONObject(bbb).getString("label").toString();
                                            }


                                            String valUs = "";
                                            if (Message.isJSONValid(val)) {
                                                JSONObject jObject = null;
                                                try {
                                                    jObject = new JSONObject(val);

                                                    Iterator<String> keys = jObject.keys();
                                                    while (keys.hasNext()) {
                                                        String keyValue = (String) keys.next();
                                                        String valueString = jObject.getString(keyValue);
                                                        valUs += keyValue + " : " + valueString + "\n";
                                                    }

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            }

                                            if (valUs.length() == 0) {

                                                titleUntuk += lab + " : " + val + "\n";
                                            } else {
                                                titleUntuk += lab + "\n" + valUs;
                                            }


                                            if (typ.equalsIgnoreCase("front_camera") || typ.equalsIgnoreCase("rear_camera")) {
                                                image = true;
                                            }
                                        }
                                        if (image) {
                                            JSONArray jsonarrayChild2 = new JSONArray(data);
                                            for (int bbb = 0; bbb < jsonarrayChild.length(); bbb++) {
                                                String key = jsonarrayChild2.getJSONObject(bbb).getString("key").toString();
                                                String val = jsonarrayChild2.getJSONObject(bbb).getString("value").toString();
                                                String typ = jsonarrayChild2.getJSONObject(bbb).getString("type").toString();

                                                if (typ.equalsIgnoreCase("front_camera") || typ.equalsIgnoreCase("rear_camera")) {
                                                    titleUntuk = val;
                                                    priceUntuk = "image";
                                                    labelDialog.add(0, key);
                                                }

                                                if (typ.equalsIgnoreCase("dropdown")) {
                                                    titleUntuk = val;
                                                    priceUntuk = "standart";
                                                    labelDialog.add(0, key);
                                                }

                                                if (typ.equalsIgnoreCase("textarea")) {
                                                    decsUntuk = val;
                                                    if (labelDialog.size() > 1) {
                                                        labelDialog.add(1, key);
                                                    }
                                                }
                                            }
                                        } else {
                                            if (iidd.equalsIgnoreCase("2207") || iidd.equalsIgnoreCase("2206") || iidd.equalsIgnoreCase("2209") || iidd.equalsIgnoreCase("2397")) {

                                                JSONArray jsonarrayChild2 = new JSONArray(data);
                                                JSONObject ks0 = jsonarrayChild2.getJSONObject(0);
                                                JSONObject ks1 = jsonarrayChild2.getJSONObject(1);
                                                JSONObject ks2 = jsonarrayChild2.getJSONObject(2);

                                                JSONObject alas = ks1.getJSONObject("value");
                                                titleUntuk = alas.getString("Part ID") + " " + alas.getString("Nama Part") + "(" + ks0.getString("value") + ")";
                                                decsUntuk = ks2.getString("value");
                                                priceUntuk = alas.getString("AVE");


                                                line = (LinearLayout) getLayoutInflater().inflate(R.layout.from_cild_layout_value, null);
                                                lv = (ListView) line.findViewById(R.id.listOrder);
                                                addCild = (Button) line.findViewById(R.id.btn_add_cild);
                                                tQty = (TextView) line.findViewById(R.id.total_detail_order);
                                                tPrice = (TextView) line.findViewById(R.id.total_price_order);
                                                addCild.setVisibility(View.GONE);
                                            }

                                        }

                                        rowItems.add(new ModelFormChild(urutan, titleUntuk, decsUntuk, priceUntuk));
                                    }

                                } else {
                                    for (int aaa = 0; aaa < jsonarray.length(); aaa++) {
                                        JSONObject jsonobject = jsonarray.getJSONObject(aaa);
                                        String urutan = jsonobject.getString("urutan");
                                        String titleUntuk = "";
                                        String fotoUntuk = "";
                                        String decsUntuk = "";
                                        String priceUntuk = "";
                                        String data = jsonobject.getString("data");
                                        JSONArray jsonarrayChild = new JSONArray(data);
                                        for (int bbb = 0; bbb < jsonarrayChild.length(); bbb++) {
                                            final String key = jsonarrayChild.getJSONObject(bbb).getString("key").toString();
                                            final String val = jsonarrayChild.getJSONObject(bbb).getString("value").toString();
                                            final String typ = jsonarrayChild.getJSONObject(bbb).getString("type").toString();

                                            JSONArray jsA = null;
                                            String contentS = "";

                                            String cc = val;
                                            if (typ.equalsIgnoreCase("input_kodepos") || typ.equalsIgnoreCase("dropdown_wilayah")) {
                                                cc = jsoncreateC(val);
                                            }

                                            try {
                                                if (cc.startsWith("{")) {
                                                    if (!cc.startsWith("[")) {
                                                        cc = "[" + cc + "]";
                                                    }
                                                    jsA = new JSONArray(cc);
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            if (jsA != null) {
                                                if (typ.equalsIgnoreCase("distance_estimation") || typ.equalsIgnoreCase("dropdown_dinamis") || typ.equalsIgnoreCase("new_dropdown_dinamis") || typ.equalsIgnoreCase("ocr") || typ.equalsIgnoreCase("upload_document")) {
                                                    if (iidd.equalsIgnoreCase("1643")) {
                                                        titleUntuk = jsonResultType(val, "Nama");
                                                    } else {
                                                        titleUntuk = jsonResultType(val, "Name Detail");
                                                        if (titleUntuk.equalsIgnoreCase("")) {
                                                            titleUntuk = jsonResultType(val, "SKU");
                                                        }
                                                        if (titleUntuk.equalsIgnoreCase("")) {
                                                            titleUntuk = jsonResultType(val, "Nama");
                                                        }
                                                        if (titleUntuk.equalsIgnoreCase("")) {
                                                            titleUntuk = jsonResultType(val, "Nama Siswa");
                                                        }
                                                    }

                                                    fotoUntuk = jsonResultType(val, "Foto Siswa");

                                                } else {
                                                    try {
                                                        for (int ic = 0; ic < jsA.length(); ic++) {
                                                            final String icC = jsA.getJSONObject(ic).getString("c").toString();
                                                            contentS += icC + "|";
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                            } else {
                                                if (iidd.equalsIgnoreCase("1643") || iidd.equalsIgnoreCase("1628") || iidd.equalsIgnoreCase("1632")) {
                                                    decsUntuk = val;
                                                    priceUntuk = "";
                                                } else {
                                                    if (typ.equalsIgnoreCase("number") || typ.equalsIgnoreCase("currency")) {
                                                        decsUntuk = val;
                                                        labelDialog.add(1, key);
                                                    } else if (typ.equalsIgnoreCase("formula")) {
                                                        priceUntuk = val;
                                                    }

                                                    if (typ.equalsIgnoreCase("front_camera") || typ.equalsIgnoreCase("rear_camera")) {
                                                        titleUntuk = val;
                                                        priceUntuk = "image";
                                                        labelDialog.add(0, key);
                                                    }

                                                    if (typ.equalsIgnoreCase("dropdown")) {
                                                        titleUntuk = val;
                                                        decsUntuk = val;

                                                        priceUntuk = "standart";
                                                        labelDialog.add(0, key);
                                                    }

                                                    if (typ.equalsIgnoreCase("textarea")) {
                                                        decsUntuk = val;
                                                        if (labelDialog.size() > 1) {
                                                            labelDialog.add(1, key);
                                                        }
                                                    }
                                                }

                                            }
                                        }

                                        if (ff.length == 2) {
                                            iidd = ff[1];
                                        }

                                        if (iidd.equalsIgnoreCase("1643") || iidd.equalsIgnoreCase("1628") || iidd.equalsIgnoreCase("1632")) {
                                            rowItems.add(new ModelFormChild(urutan, titleUntuk, decsUntuk, ""));
                                        } else if (iidd.equalsIgnoreCase("1113")) {
                                            int nilai = 0;
                                            try {
                                                nilai = Integer.valueOf(priceUntuk != null ? priceUntuk.replace(".", "") : "0") / Integer.valueOf(decsUntuk != null ? decsUntuk.replace(".", "") : "0");
                                            } catch (Exception e) {
                                                nilai = 0;
                                            }
                                            rowItems.add(new ModelFormChild(urutan, titleUntuk, decsUntuk, String.valueOf(nilai)));
                                        } else {

                                            if (priceUntuk.equalsIgnoreCase("standart") && !fotoUntuk.equalsIgnoreCase("")) {
                                                priceUntuk = fotoUntuk;
                                            }
                                            rowItems.add(new ModelFormChild(urutan, titleUntuk, decsUntuk, priceUntuk));
                                        }
                                    }
                                }


                                if (ff.length == 2) {
                                    iidd = ff[1];
                                }

                                ArrayList<String> sini = new ArrayList();

                                if (iidd.equalsIgnoreCase("1643") || iidd.equalsIgnoreCase("1628") || iidd.equalsIgnoreCase("1632")) {
                                } else {
                                    Integer totalQ = 0;
                                    Integer totalP = 0;
                                    Double temp = 0.0;
                                    Boolean showTotal = false;
                                    for (ModelFormChild mfc : rowItems) {
                                        try {
                                            if (mfc.getTitle().contains(".jpg")) {
                                                sini.add("image");
                                            } else if (mfc.getPrice().equalsIgnoreCase("image")) {
                                                sini.add("image");
                                            } else if (mfc.getPrice().equalsIgnoreCase("standart")) {
                                                sini.add("standart");
                                            } else {
                                                if (mfc.getTitle().contains("Rincian")) {
                                                    showTotal = true;
                                                    tableRow2.setVisibility(View.VISIBLE);
                                                    String sambut[] = mfc.getTitle().split("Nominal :");
                                                    totalP = totalP + Integer.valueOf(sambut[1].trim().replace(",", ""));
                                                } else {
                                                    if (iidd.equalsIgnoreCase("2207") || iidd.equalsIgnoreCase("2206") || iidd.equalsIgnoreCase("2209") || iidd.equalsIgnoreCase("2397")) {
                                                        totalQ += Integer.valueOf(mfc.getDetail());
                                                        temp += Float.valueOf(mfc.getPrice()) * Integer.valueOf(mfc.getDetail());

                                                        tQty.setText(totalQ + "");
                                                        String totalHarga = new Validations().getInstance(getApplicationContext()).numberToCurency(temp + "");
                                                        tPrice.setText(totalHarga);

                                                    } else {
                                                        totalQ += Integer.valueOf(mfc.getDetail());
                                                        totalP += Integer.valueOf(mfc.getPrice().replace(".", "")) * Integer.valueOf(mfc.getDetail());
                                                    }

                                                }


                                            }

                                        } catch (Exception e) {
                                        }
                                    }

                                    if (totalQ > 0 && totalP > 0) {
                                        String totalHarga = new Validations().getInstance(getApplicationContext()).numberToCurency(totalP + "");
                                        tQty.setText(totalQ + "");
                                        tPrice.setText(totalHarga);
                                    }
                                    if (showTotal) {
                                        String totalHarga = new Validations().getInstance(getApplicationContext()).numberToCurency(totalP + "");
                                        tQty.setText("");
                                        tPrice.setText(totalHarga);
                                    }


                                }

                                if (sini.size() > 0) {
                                    if (sini.get(0).toString().equalsIgnoreCase("image")) {
                                        line = (LinearLayout) getLayoutInflater().inflate(R.layout.form_cild_two_layout_value, null);
                                        lv = (ListView) line.findViewById(R.id.listOrder);
                                        TextView nama = (TextView) line.findViewById(R.id.nama);
                                        addCild = (Button) line.findViewById(R.id.btn_add_cild);
                                        addCild.setVisibility(View.GONE);
                                        nama.setVisibility(View.GONE);

                                        lv.setOnTouchListener(new View.OnTouchListener() {
                                            // Setting on Touch Listener for handling the touch inside ScrollView
                                            @Override
                                            public boolean onTouch(View v, MotionEvent event) {
                                                v.getParent().requestDisallowInterceptTouchEvent(true);
                                                return false;
                                            }
                                        });

                                        final FormChildAdapter adapter = new FormChildAdapter(getApplicationContext(), rowItems, "value", true);
                                        lv.setAdapter(adapter);
                                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                                ModelFormChild modelFormChild = rowItems.get(position);
                                                if (labelDialog.size() == 1) {
                                                    DialogUtil.generateAlertDialogLeftImage(ByonchatDetailDefectActivity.this, modelFormChild.getTitle(), modelFormChild.getDetail(), "", labelDialog.get(0) != null ? labelDialog.get(0) : "").show();
                                                } else if (labelDialog.size() > 1) {
                                                    DialogUtil.generateAlertDialogLeftImage(ByonchatDetailDefectActivity.this, modelFormChild.getTitle(), modelFormChild.getDetail(), labelDialog.get(1) != null ? labelDialog.get(1) : "", labelDialog.get(0) != null ? labelDialog.get(0) : "").show();
                                                } else {
                                                    DialogUtil.generateAlertDialogLeftImage(ByonchatDetailDefectActivity.this, modelFormChild.getTitle(), modelFormChild.getDetail(), "", "").show();
                                                }
                                            }
                                        });

                                    } else {
                                        line = (LinearLayout) getLayoutInflater().inflate(R.layout.form_cild_two_layout_value, null);
                                        lv = (ListView) line.findViewById(R.id.listOrder);

                                        lv.setOnTouchListener(new View.OnTouchListener() {
                                            // Setting on Touch Listener for handling the touch inside ScrollView
                                            @Override
                                            public boolean onTouch(View v, MotionEvent event) {
                                                v.getParent().requestDisallowInterceptTouchEvent(true);
                                                return false;
                                            }
                                        });

                                        addCild = (Button) line.findViewById(R.id.btn_add_cild);
                                        addCild.setVisibility(View.GONE);
                                        TextView nama = (TextView) line.findViewById(R.id.nama);
                                        nama.setVisibility(View.GONE);

                                        final FormChildAdapter adapter = new FormChildAdapter(getApplicationContext(), rowItems, "value", false);
                                        lv.setAdapter(adapter);
                                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                                if (labelDialog.size() > 1) {
                                                    ModelFormChild modelFormChild = rowItems.get(position);
                                                    DialogUtil.generateAlertDialogLeftBOBO(ByonchatDetailDefectActivity.this, modelFormChild.getTitle(), modelFormChild.getDetail(), labelDialog.get(0) != null ? labelDialog.get(0) : "", labelDialog.get(1) != null ? labelDialog.get(1) : "", modelFormChild.getPrice()).show();
                                                } else {
                                                    ModelFormChild modelFormChild = rowItems.get(position);
                                                    DialogUtil.generateAlertDialogLeftNoImage(ByonchatDetailDefectActivity.this, modelFormChild.getTitle(), modelFormChild.getDetail(), "", "Nama").show();
                                                }
                                            }
                                        });
                                    }


                                } else {
                                    if (Integer.valueOf(iidd) >= 2092 && Integer.valueOf(iidd) != 2207 && Integer.valueOf(iidd) != 2206 && Integer.valueOf(iidd) != 2209 && Integer.valueOf(iidd) != 2397) {
                                        final FormChildAdapter adapter = new FormChildAdapter(getApplicationContext(), rowItems, "multiple", false);
                                        lv.setAdapter(adapter);
                                    } else {
                                        final FormChildAdapter adapter = new FormChildAdapter(getApplicationContext(), rowItems, "value", false);
                                        lv.setAdapter(adapter);
                                    }


                                    if (iidd.equalsIgnoreCase("1643") || iidd.equalsIgnoreCase("1628") || iidd.equalsIgnoreCase("1632")) {
                                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                                ModelFormChild modelFormChild = rowItems.get(position);
                                                DialogUtil.generateAlertDialogLeft(ByonchatDetailDefectActivity.this, modelFormChild.getTitle(), modelFormChild.getDetail()).show();
                                            }
                                        });
                                    }
                                }


                                LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params11.setMargins(20, 10, 30, 0);
                                LinearLayout.LayoutParams params22 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params22.setMargins(20, 10, 30, 30);
                                linearValue.addView(textV, params11);
                                LinearLayout lineview = (LinearLayout) getLayoutInflater().inflate(R.layout.line_horizontal, null);
                                linearValue.addView(lineview, params11);
                                linearValue.addView(line, params22);


                            } else {
                                TextView textV = new TextView(this);
                                textV.setText(Html.fromHtml(label));
                                textV.setTextSize(17);
                                textV.setLayoutParams(new TableRow.LayoutParams(0));


                                TextView etV = (TextView) new TextView(getApplicationContext());
                                etV.setTextIsSelectable(true);
                                etV.setText(Html.fromHtml(value));
                                LinearLayout line = (LinearLayout) getLayoutInflater().inflate(R.layout.line_horizontal, null);
                                LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params11.setMargins(10, 10, 30, 0);
                                LinearLayout.LayoutParams params22 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params22.setMargins(50, 10, 30, 30);
                                linearValue.addView(textV, params11);
                                linearValue.addView(line, params11);
                                linearValue.addView(etV, params22);
                            }
                        }

                }

            }

        } catch (Exception e){

        }
    }

    private String jsonResultType(String json, String type) {
        String hasil = "";
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jObject != null) {
            try {
                hasil = jObject.getString(type);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return hasil;
    }

    private String jsoncreateC(String content) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("c", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }



    private static class MyTaskParams {
        HtmlTextView textView;
        AVLoadingIndicatorView progress;
        String url;

        MyTaskParams(HtmlTextView _bar, AVLoadingIndicatorView _progress, String _url) {
            this.textView = _bar;
            this.progress = _progress;
            this.url = _url;
        }

        public HtmlTextView getTextView() {
            return textView;
        }

        public void setTextView(HtmlTextView textView) {
            this.textView = textView;
        }

        public AVLoadingIndicatorView getProgress() {
            return progress;
        }

        public void setProgress(AVLoadingIndicatorView progress) {
            this.progress = progress;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    private class SaveMedia extends AsyncTask<MyTaskParams, Void, MyTaskParams> {
        String aa = "";

        @Override
        protected MyTaskParams doInBackground(MyTaskParams... urls) {
            MyTaskParams aaa = new MyTaskParams(urls[0].getTextView(), urls[0].getProgress(), urls[0].getUrl());

            if (aaa.getUrl().startsWith("http")) {
                aa = GET(aaa.getUrl());
            } else {
                aa = aaa.getUrl();
            }


            return aaa;
        }

        @Override
        protected void onPostExecute(MyTaskParams result) {
            result.getProgress().setVisibility(View.GONE);
            HtmlTextView textView = result.getTextView();

            textView.setHtml(aa,
                    new HtmlResImageGetter(textView));
        }
    }


    public String GET(String url) {
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
            inputStream = httpResponse.getEntity().getContent();
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "-";
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

}
