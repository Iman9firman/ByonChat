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
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.byonchat.android.DialogFormChildMain;
import com.byonchat.android.DialogFormChildMainRequester;
import com.byonchat.android.ISSActivity.LoginDB.UserDB;
import com.byonchat.android.ISSActivity.Requester.RequesterRatingActivity;
import com.byonchat.android.R;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.tabRequest.MapsViewActivity;
import com.byonchat.android.utils.ValidationsKey;
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
    View line_bottom;


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
        line_bottom = (View) findViewById(R.id.line_bottom);

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
                    }
                });
                dialogFormChildMainRequester.show();

            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkInternetConnectionStatus.getInstance(DinamicRoomSearchTaskActivity.this).isOnline(DinamicRoomSearchTaskActivity.this)) {
                   /* String wow = "[{\"id_request\":\"170\",\"request_status\":\"2\",\"kode_jjt\":\"ISS-00625F0001\",\"nama_jjt\":\"EKA HOSPITAL CLN\",\"jjt_lat\":\"-6.2985016\",\"jjt_long\":\"106.6695476\",\"sub_request\":[{\"id_sub_request\":\"178\",\"nama_pekerjaan\":\"Cleaner\",\"jumlah\":\"2\",\"request_detail\":[{\"id_request_detail\":\"918\",\"rating\":5,\"nama\":\"AHMAD SOLEMAN\",\"id_reliever\":\"65\",\"jarak\":11.466734999664,\"lat\":\"-6.20148373\",\"long\":\"106.63436890\",\"hp\":\"6281293939690\",\"status\":\"0\",\"total_kerja\":\"\"},{\"id_request_detail\":\"915\",\"rating\":5,\"nama\":\"FERI KARNIATI\",\"id_reliever\":\"48\",\"jarak\":14.520820268344,\"lat\":\"-6.18782234\",\"long\":\"106.73928070\",\"hp\":\"6282199247538\",\"status\":\"0\",\"total_kerja\":\"\"},{\"id_request_detail\":\"909\",\"rating\":5,\"nama\":\"MUHIDIN\",\"id_reliever\":\"27\",\"jarak\":16.619236165417,\"lat\":\"-6.29332352\",\"long\":\"106.81983185\",\"hp\":\"6282246006769\",\"status\":\"0\",\"total_kerja\":\"\"},{\"id_request_detail\":\"913\",\"rating\":5,\"nama\":\"SYAMSUL AZIS\",\"id_reliever\":\"41\",\"jarak\":19.431112064058,\"lat\":\"-6.22453022\",\"long\":\"106.82882690\",\"hp\":\"6282298743810\",\"status\":\"0\",\"total_kerja\":\"\"},{\"id_request_detail\":\"922\",\"rating\":5,\"nama\":\"NUR SALIMIN\",\"id_reliever\":\"70\",\"jarak\":19.695171120012,\"lat\":\"-6.22645521\",\"long\":\"106.83233643\",\"hp\":\"6282324302246\",\"status\":\"0\",\"total_kerja\":\"\"},{\"id_request_detail\":\"923\",\"rating\":5,\"nama\":\"ILHAM FAUZI\",\"id_reliever\":\"71\",\"jarak\":14.573812722949,\"lat\":\"-6.37537813\",\"long\":\"106.77635956\",\"hp\":\"6283136289516\",\"status\":\"0\",\"total_kerja\":\"\"},{\"id_request_detail\":\"920\",\"rating\":5,\"nama\":\"HASAN BASRI\",\"id_reliever\":\"67\",\"jarak\":12.489663021116,\"lat\":\"-6.26469183\",\"long\":\"106.77731323\",\"hp\":\"6285782284899\",\"status\":\"0\",\"total_kerja\":\"\"},{\"id_request_detail\":\"911\",\"rating\":5,\"nama\":\"SANDRI ZOHAL MERDESA\",\"id_reliever\":\"37\",\"jarak\":17.928846453725,\"lat\":\"-6.23335171\",\"long\":\"106.81793213\",\"hp\":\"6285810459346\",\"status\":\"0\",\"total_kerja\":\"\"},{\"id_request_detail\":\"916\",\"rating\":5,\"nama\":\"CATUR WAHYUDI\",\"id_reliever\":\"52\",\"jarak\":17.940851136857,\"lat\":\"-6.23789167\",\"long\":\"106.81998444\",\"hp\":\"6285814635491\",\"status\":\"0\",\"total_kerja\":\"\"},{\"id_request_detail\":\"925\",\"rating\":5,\"nama\":\"ACEP SUPRIADI\",\"id_reliever\":\"93\",\"jarak\":18.709599367913,\"lat\":\"-6.22188330\",\"long\":\"106.51883698\",\"hp\":\"6285883565464\",\"status\":\"0\",\"total_kerja\":\"\"},{\"id_request_detail\":\"926\",\"rating\":5,\"nama\":\"Jaya (ByonChat)\",\"id_reliever\":\"215\",\"jarak\":15.090096009804,\"lat\":\"-6.19780397\",\"long\":\"106.76107788\",\"hp\":\"6287771783888\",\"status\":\"0\",\"total_kerja\":\"\"},{\"id_request_detail\":\"912\",\"rating\":5,\"nama\":\"KRISTINA\",\"id_reliever\":\"38\",\"jarak\":18.972424764858,\"lat\":\"-6.23533678\",\"long\":\"106.82901001\",\"hp\":\"6287785349696\",\"status\":\"0\",\"total_kerja\":\"\"},{\"id_request_detail\":\"914\",\"rating\":5,\"nama\":\"SUNTAMA\",\"id_reliever\":\"46\",\"jarak\":14.711326116941,\"lat\":\"-6.18372440\",\"long\":\"106.73575592\",\"hp\":\"6287878877005\",\"status\":\"0\",\"total_kerja\":\"\"},{\"id_request_detail\":\"924\",\"rating\":5,\"nama\":\"AHMAD ANGGRI FAUJI\",\"id_reliever\":\"72\",\"jarak\":5.4291954589328,\"lat\":\"-6.27989674\",\"long\":\"106.71496582\",\"hp\":\"6289506594546\",\"status\":\"0\",\"total_kerja\":\"\"},{\"id_request_detail\":\"919\",\"rating\":5,\"nama\":\"ROBIATUL ADAWIYAH\",\"id_reliever\":\"66\",\"jarak\":18.843954509136,\"lat\":\"-6.22384930\",\"long\":\"106.82260895\",\"hp\":\"6289606795860\",\"status\":\"0\",\"total_kerja\":\"\"},{\"id_request_detail\":\"910\",\"rating\":5,\"nama\":\"SETIAWAN RAMDANI\",\"id_reliever\":\"33\",\"jarak\":16.435716939178,\"lat\":\"-6.27082300\",\"long\":\"106.81562805\",\"hp\":\"6289610601394\",\"status\":\"0\",\"total_kerja\":\"\"},{\"id_request_detail\":\"917\",\"rating\":5,\"nama\":\"TARONI\",\"id_reliever\":\"58\",\"jarak\":18.596306882179,\"lat\":\"-6.15645123\",\"long\":\"106.75835419\",\"hp\":\"6289662598085\",\"status\":\"0\",\"total_kerja\":\"\"},{\"id_request_detail\":\"921\",\"rating\":5,\"nama\":\"DENI SAPUTRO SUSWANTO\",\"id_reliever\":\"68\",\"jarak\":11.788849182905,\"lat\":\"-6.27138901\",\"long\":\"106.77266693\",\"hp\":\"628979138466\",\"status\":\"0\",\"total_kerja\":\"\"}]},{\"id_sub_request\":\"179\",\"nama_pekerjaan\":\"Gardener\",\"jumlah\":\"3\",\"request_detail\":[]}]}]";
//TESTING INI OYY
                    Intent intent5 = new Intent(getApplicationContext(), RequesterRatingActivity.class);
                    intent5.putExtra(Constants.EXTRA_COLOR, "006b9c");
                    intent5.putExtra(Constants.EXTRA_COLORTEXT, "004a6d");
                    intent5.putExtra(Constants.EXTRA_ITEM, wow);
                    startActivity(intent5);
                    finish();
*/
                    try {
                        if (!keperluan.isEmpty()) {
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
                            params.put("data", jsonArray.toString());
                            params.put("bc_user", contact.getJabberId());

                            Log.w("param1 popps",dua.get(spinner.getSelectedItemPosition()));
                            Log.w("param2 popps",jsonArray.toString());
                            Log.w("param3 popps",contact.getJabberId());

                            getSubPekerjaan("https://bb.byonchat.com/ApiReliever/index.php/Request", params);

                        } else {
                            Toast.makeText(DinamicRoomSearchTaskActivity.this, "Harap pilih reliever yang dibutuhkan", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(DinamicRoomSearchTaskActivity.this, "No Internet Akses", Toast.LENGTH_SHORT).show();
                    finish();
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
            Button btnCancel = linearEstimasi.findViewById(R.id.btnCancel);
            btnModify.setVisibility(View.GONE);
            btnCancel.setText("REMOVE");
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

        if (listRequest.getChildCount() > 0) {
            line_bottom.setVisibility(View.VISIBLE);
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
                        Intent intent5 = new Intent(getApplicationContext(), RequesterRatingActivity.class);
                        intent5.putExtra(Constants.EXTRA_COLOR, "006b9c");
                        intent5.putExtra(Constants.EXTRA_COLORTEXT, "004a6d");
                        JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
                        intent5.putExtra(Constants.EXTRA_ITEM, jsonArray.toString());
                        Log.w("Muse prestice hdi",jsonArray.toString());
                        startActivity(intent5);
                        finish();
                    } catch (JSONException e) {
                        Log.w("popps 4e","here"+e);
                        Log.w("popps 4o",response);
                        Toast.makeText(getApplicationContext(), "Terjadi kesalahan pada sistem , Harap ulangi beberapa saat", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> {
                    rdialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Periksa kembali jaringan anda", Toast.LENGTH_SHORT).show();
                    Log.w("popps 4","here"+error);
                }
        ) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return params2;
            }
        };
        sr.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                rdialog.dismiss();
                Log.e("HttpClient", "error: " + error.toString());
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
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