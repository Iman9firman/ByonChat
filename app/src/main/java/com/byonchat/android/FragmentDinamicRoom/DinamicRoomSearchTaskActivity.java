package com.byonchat.android.FragmentDinamicRoom;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.byonchat.android.tabRequest.MapsViewActivity;
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
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#022b95")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.parseColor("#022b95"));
        }

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

                   /* JSONObject jsonObject = new JSONObject("{\"status\":\"1\",\"message\":\"succes\",\"data\":[{\"id_request\":\"51\",\"request_status\":\"0\",\"kode_jjt\":\"ISS-01349O0002\",\"nama_jjt\":\"Cleaner Jakarta - GRAHA ISS\",\"jjt_lat\":\"-6.2799206\",\"jjt_long\":\"106.712887\",\"sub_request\":[{\"id_sub_request\":\"58\",\"nama_pekerjaan\":\"Cleaner\",\"request_detail\":[{\"id_request_detail\":\"28\",\"rating\":5,\"nama\":\"Aziz\",\"id_reliever\":\"193\",\"jarak\":10.607576386624,\"lat\":\"-6.19751692\",\"long\":\"106.76124573\",\"hp\":\"6285322226666\",\"status\":\"0\",\"total_kerja\":0},{\"id_request_detail\":\"29\",\"rating\":5,\"nama\":\"Iman(Byonchat)\",\"id_reliever\":\"194\",\"jarak\":0.10827069351829,\"lat\":\"-6.27945089\",\"long\":\"106.71374512\",\"hp\":\"62858922221\",\"status\":\"0\",\"total_kerja\":0}]}]}]}");
                    if (jsonObject.has("data")) {
                        Intent intent5 = new Intent(getApplicationContext(), RequesterRatingActivity.class);
                        intent5.putExtra(Constants.EXTRA_COLOR, "022B96");
                        intent5.putExtra(Constants.EXTRA_COLORTEXT, "ffffff");
                        JSONArray jsonArrays = new JSONArray(jsonObject.getString("data"));
                        intent5.putExtra(Constants.EXTRA_ITEM, jsonArrays.toString());
                        startActivity(intent5);
                        finish();
                    }*/


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
                    Log.w("kampret", response);

                    rdialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("data")) {
                            //{"status":"1","message":"succes","data":[{"id_request":"51","request_status":"0","kode_jjt":"ISS-01349O0002","nama_jjt":"Cleaner Jakarta - GRAHA ISS","jjt_lat":"-6.2799206","jjt_long":"106.712887","sub_request":[{"id_sub_request":"58","nama_pekerjaan":"Cleaner","request_detail":[{"id_request_detail":"28","rating":5,"nama":"Aziz","id_reliever":"193","jarak":10.607576386624,"lat":"-6.19751692","long":"106.76124573","hp":"6285322226666","status":"0","total_kerja":0},{"id_request_detail":"29","rating":5,"nama":"Iman(Byonchat)","id_reliever":"194","jarak":0.10827069351829,"lat":"-6.27945089","long":"106.71374512","hp":"62858922221","status":"0","total_kerja":0}]}]}]}
                            Intent intent5 = new Intent(getApplicationContext(), RequesterRatingActivity.class);
                            intent5.putExtra(Constants.EXTRA_COLOR, "006b9c");
                            intent5.putExtra(Constants.EXTRA_COLORTEXT, "004a6d");
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
                            intent5.putExtra(Constants.EXTRA_ITEM, jsonArray.toString());
                            startActivity(intent5);
                            finish();
                        } else {
                            Toast.makeText(DinamicRoomSearchTaskActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
                          /*  Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:+6285891307575"));
                            startActivity(callIntent);*/
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                final AlertDialog.Builder alertbox = new AlertDialog.Builder(DinamicRoomSearchTaskActivity.this);
                alertbox.setMessage("Are you sure you want to exit?");
                alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        onBackPressed();
                    }
                });
                alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
                alertbox.show();


                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}