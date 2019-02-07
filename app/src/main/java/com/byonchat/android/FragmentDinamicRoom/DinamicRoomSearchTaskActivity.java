package com.byonchat.android.FragmentDinamicRoom;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.byonchat.android.DialogFormChildMain;
import com.byonchat.android.DialogFormChildMainRequester;
import com.byonchat.android.ISSActivity.LoginDB.UserDB;
import com.byonchat.android.ISSActivity.Requester.RequesterRatingActivity;
import com.byonchat.android.R;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.widget.CalendarDialog;
import com.itextpdf.text.pdf.parser.Line;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DinamicRoomSearchTaskActivity extends AppCompatActivity {

    LinearLayout listRequest;
    Button btnSubmit;
    ProgressDialog dialog;
    ArrayList<String> keperluan = new ArrayList<>();
    ArrayList<String> dua = new ArrayList<>();
    SearchableSpinner spinner;


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_dinamic_room_task_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Request Reliever");

        listRequest = (LinearLayout) findViewById(R.id.listRequest);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        spinner = (SearchableSpinner) findViewById(R.id.spinner);

        Button btnAddCild = (Button) findViewById(R.id.btn_add_cild);

        UserDB db = UserDB.getInstance(getApplicationContext());
        Cursor cursor = db.getSingle();
        if (cursor.getCount() > 0) {
            String content = cursor.getString(cursor.getColumnIndexOrThrow(UserDB.EMPLOYEE_MULTICOST));


            ArrayList<String> spinnerArray = new ArrayList<String>();
            String[] arr = content.split(",");

            for (int as = 0; as < arr.length; as++) {
                spinnerArray.add(arr[as].substring(0, arr[as].indexOf("[")));
                dua.add(arr[as].substring(arr[as].indexOf("[") + 1, arr[as].indexOf("]")));
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                spinner.setBackground(getResources().getDrawable(R.drawable.spinner_background));
            }
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerArrayAdapter);


        }

        btnAddCild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFormChildMainRequester dialogFormChildMainRequester = new DialogFormChildMainRequester(DinamicRoomSearchTaskActivity.this);
                dialogFormChildMainRequester.setListener(new DialogFormChildMainRequester.MyDialogListener() {
                    @Override
                    public void userSelectedAValue(String value) {
                        keperluan.add(value);
                        refresh();
                    }

                    @Override
                    public void userCanceled() {
                        Toast.makeText(getApplicationContext(), "Canceled", Toast.LENGTH_SHORT).show();
                    }
                });
                dialogFormChildMainRequester.show();

            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONArray jsonArray = new JSONArray();
                    for (String kk : keperluan) {
                        JSONObject joo = new JSONObject(kk);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id_pekerjaan", joo.getString("idSub"));
                        jsonObject.put("mulai", joo.getString("jadwalMulai"));
                        jsonObject.put("selesai", joo.getString("jadwalAkhir"));
                        jsonObject.put("jumlah", joo.getString("jumlah"));

                        jsonArray.put(jsonObject);
                    }
                    MessengerDatabaseHelper messengerHelper = null;
                    if (messengerHelper == null) {
                        messengerHelper = MessengerDatabaseHelper.getInstance(getApplicationContext());
                    }

                    Contact contact = messengerHelper.getMyContact();
                    Map<String, String> params = new HashMap<>();
                    params.put("jjt", dua.get(spinner.getSelectedItemPosition()));
                    Log.w("kasam1", dua.get(spinner.getSelectedItemPosition()));
                    Log.w("kasam2", jsonArray.toString());
                    Log.w("bc_user", contact.getJabberId());
                    params.put("data", jsonArray.toString());
                    params.put("bc_user", contact.getJabberId());

                    getSubPekerjaan("https://bb.byonchat.com/ApiReliever/index.php/Request", params);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    public void refresh() {
        listRequest.removeAllViews();

        int ia = 0;

        for (String kk : keperluan) {
            LinearLayout linearEstimasi = (LinearLayout) getLayoutInflater().inflate(R.layout.add_child_requester, null);

            TextView textDesc = linearEstimasi.findViewById(R.id.textDesc);

            Button btnModify = linearEstimasi.findViewById(R.id.btnModify);
            btnModify.setVisibility(View.GONE);
            Button btnCancel = linearEstimasi.findViewById(R.id.btnCancel);
            btnCancel.setText("DELETE");
            btnCancel.setVisibility(View.VISIBLE);


            int finalIa = ia;
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    keperluan.remove(finalIa);
                    listRequest.removeViewAt(finalIa);
                }
            });

            try {
                JSONObject jsonObject = new JSONObject(kk);
                textDesc.setText(jsonObject.getString("pekerjaan") + ", " + jsonObject.getString("subPekerjaa") + ", " + jsonObject.getString("jadwalMulai") + " - " + jsonObject.getString("jadwalAkhir")
                        + ", jumlah = " + jsonObject.getString("jumlah") + ", keterangan = " + jsonObject.getString("keterangan"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            listRequest.addView(linearEstimasi);
            ia++;
        }


    }


    private void getSubPekerjaan(String Url, Map<String, String> params2) {
        ProgressDialog rdialog = new ProgressDialog(DinamicRoomSearchTaskActivity.this);
        rdialog.setMessage("Loading...");
        rdialog.show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        StringRequest sr = new StringRequest(Request.Method.POST, Url,
                response -> {
                    rdialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("data")) {
                            Intent intent5 = new Intent(getApplicationContext(), RequesterRatingActivity.class);
                            intent5.putExtra(Constants.EXTRA_COLOR, "006b9c");
                            intent5.putExtra(Constants.EXTRA_COLORTEXT, "004a6d");
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
                            intent5.putExtra(Constants.EXTRA_ITEM, jsonArray.toString());
                            startActivity(intent5);
                            finish();
                        } else {
                            Toast.makeText(DinamicRoomSearchTaskActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:+6285891307575"));
                            startActivity(callIntent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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