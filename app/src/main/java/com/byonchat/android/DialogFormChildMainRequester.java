package com.byonchat.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.byonchat.android.widget.CalendarDialog;
import com.googlecode.mp4parser.authoring.Edit;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lukmanpryg on 7/19/2016.
 */
public class DialogFormChildMainRequester extends Dialog implements View.OnClickListener/*, AdapterView.OnItemSelectedListener */ {

    ArrayList<MainPekerjaan> mainPekerjaans;
    ArrayList<SubPekerjaan> subPekerjaans;

    ArrayAdapter<String> spinnerArrayMainAdapter;
    ArrayAdapter<String> spinnerArraySubAdapter;
    ArrayList<String> spinnerArrayMain;
    ArrayList<String> spinnerArraySub;

    Spinner spinnerMain;
    Spinner spinnerSub;

    Button simpan;
    Button add, cancel;

    String subIdnya;
    String subNamenya;
    String pekerjaanNamenya;
    TextView valueAwal, valueAkhir;

    ImageButton btnDateAwal, btnDateAkhir;
    EditText jumlah, keterangan;

    MyDialogListener listener;
    Activity activity;
    ProgressDialog dialog;

    public DialogFormChildMainRequester(Activity activity) {
        super(activity);
        this.activity = activity;
        dialog = new ProgressDialog(activity);
        dialog.setMessage("Loading...");
    }

    private void getMainPekerjaan(String Url) {
        dialog.show();
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest sr = new StringRequest(Request.Method.POST, Url,
                response -> {
                    dialog.dismiss();
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String id = jsonArray.getJSONObject(i).getString("id");
                            String namaPekerjaan = jsonArray.getJSONObject(i).getString("nama_pekerjaan");
                            MainPekerjaan mP = new MainPekerjaan(id, namaPekerjaan);
                            spinnerArrayMain.add(namaPekerjaan);
                            mainPekerjaans.add(mP);

                        }
                        spinnerArrayMainAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error ->
                        dialog.dismiss());
        {

        }
        queue.add(sr);
    }

    private void getSubPekerjaan(String Url, Map<String, String> params2) {
        dialog.show();
        subPekerjaans.clear();
        spinnerArraySub.clear();
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest sr = new StringRequest(Request.Method.POST, Url,
                response -> {
                    dialog.dismiss();
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        spinnerArraySub.add("-Pilih sub posisi-");
                        SubPekerjaan sPs = new SubPekerjaan("0", "0");
                        subPekerjaans.add(sPs);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            String id = jsonArray.getJSONObject(i).getString("id");
                            String namaPekerjaan = jsonArray.getJSONObject(i).getString("nama_pekerjaan");

                            SubPekerjaan sP = new SubPekerjaan(id, namaPekerjaan);

                            spinnerArraySub.add(namaPekerjaan);
                            subPekerjaans.add(sP);

                            subIdnya = "";
                            subNamenya = "";
                        }

                        spinnerArraySubAdapter.notifyDataSetChanged();
                        spinnerSub.setSelection(0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error ->
                        dialog.dismiss()) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return params2;
            }
        };
        queue.add(sr);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getMainPekerjaan("https://bb.byonchat.com/ApiReliever/index.php/Pekerjaan/daftar");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_form_child_requester_layout);

        jumlah = (EditText) findViewById(R.id.txtKuota1Jumlah);
        keterangan = (EditText) findViewById(R.id.txtKeterangan);
        valueAkhir = (TextView) findViewById(R.id.value_akhir);
        valueAwal = (TextView) findViewById(R.id.value_awal);
        add = (Button) findViewById(R.id.btn_add_cild);
        cancel = (Button) findViewById(R.id.btn_cancel);
        spinnerSub = (Spinner) findViewById(R.id.spinner_sub);
        spinnerMain = (SearchableSpinner) findViewById(R.id.spinner_main);
        btnDateAwal = (ImageButton) findViewById(R.id.btn_date_awal);
        btnDateAkhir = (ImageButton) findViewById(R.id.btn_date_Akhir);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            spinnerMain.setBackground(activity.getResources().getDrawable(R.drawable.spinner_background));
            spinnerSub.setBackground(activity.getResources().getDrawable(R.drawable.spinner_background));
        }

        mainPekerjaans = new ArrayList<>();
        subPekerjaans = new ArrayList<>();


        spinnerArrayMain = new ArrayList<String>();

        spinnerArrayMainAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinnerArrayMain);
        spinnerArrayMainAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerArraySub = new ArrayList<String>();

        spinnerArraySubAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinnerArraySub);
        spinnerArraySubAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerMain.setAdapter(spinnerArrayMainAdapter);
        spinnerSub.setAdapter(spinnerArraySubAdapter);

        cancel.setOnClickListener(this);
        add.setOnClickListener(this);
        btnDateAwal.setOnClickListener(this);
        btnDateAkhir.setOnClickListener(this);

        spinnerMain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String idNya = mainPekerjaans.get(position).getId();
                pekerjaanNamenya = mainPekerjaans.get(position).getNamaPekerjaan();
                Map<String, String> params = new HashMap<>();
                params.put("id", idNya);

                getSubPekerjaan("https://bb.byonchat.com/ApiReliever/index.php/Pekerjaan/sub", params);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerSub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.w("gamungkin2", subPekerjaans.get(position).getId());
                subIdnya = subPekerjaans.get(position).getId();
                subNamenya = subPekerjaans.get(position).getNamaPekerjaan();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    @Override
    public void onClick(View v) {

        if (v == cancel) {
            listener.userCanceled();
            dismiss();
        }
        if (v == add) {
            if (subNamenya.length() == 0) {
                Toast.makeText(getContext(), "Harap Pilih Posisi", Toast.LENGTH_SHORT).show();
                return;
            }

            if (subIdnya.length() == 0) {
                Toast.makeText(getContext(), "Harap Pilih Sub Posisi", Toast.LENGTH_SHORT).show();
                return;
            }

            if (valueAwal.getText().toString().length() == 0) {
                Toast.makeText(getContext(), "Harap masukan jam mulai kerja", Toast.LENGTH_SHORT).show();
                return;
            }

            if (valueAkhir.getText().toString().length() == 0) {
                Toast.makeText(getContext(), "Harap masukan jam akhir kerja", Toast.LENGTH_SHORT).show();
                return;
            }

            if (jumlah.getText().toString().length() == 0) {
                Toast.makeText(getContext(), "Harap masukan Jumlah", Toast.LENGTH_SHORT).show();
                return;
            }

            if (keterangan.getText().toString().length() == 0) {
                Toast.makeText(getContext(), "Harap isi Keterangan", Toast.LENGTH_SHORT).show();
                return;
            }


            JSONObject jsonObject = new JSONObject();
            Log.w("gamungkin", subIdnya);
            Log.w("gamungkin", subIdnya);
            try {
                jsonObject.put("pekerjaan", pekerjaanNamenya);
                jsonObject.put("subPekerjaa", subNamenya);
                jsonObject.put("idSub", subIdnya);
                jsonObject.put("jadwalMulai", valueAwal.getText().toString());
                jsonObject.put("jadwalAkhir", valueAkhir.getText().toString());
                jsonObject.put("jumlah", jumlah.getText().toString());
                jsonObject.put("keterangan", keterangan.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            listener.userSelectedAValue(jsonObject.toString());
            dismiss();
        }

        if (v == btnDateAkhir) {
            CalendarDialog calendarDialog = new CalendarDialog(activity);
            calendarDialog.setListener(new CalendarDialog.MyDialogListener() {
                @Override
                public void userSelectedAValue(String value) {
                    valueAkhir.setText(value);
                }

                @Override
                public void userCanceled() {
                    //  Toast.makeText(getContext(), "Canceled", Toast.LENGTH_SHORT).show();
                }
            });
            calendarDialog.show();
        }

        if (v == btnDateAwal) {
            CalendarDialog calendarDialog = new CalendarDialog(activity);
            calendarDialog.setListener(new CalendarDialog.MyDialogListener() {
                @Override
                public void userSelectedAValue(String value) {
                    valueAwal.setText(value);
                }

                @Override
                public void userCanceled() {
                    // Toast.makeText(getContext(), "Canceled", Toast.LENGTH_SHORT).show();
                }
            });
            calendarDialog.show();
        }


    }

    class MainPekerjaan {

        String id;
        String namaPekerjaan;

        public MainPekerjaan(String id, String namaPekerjaan) {
            this.id = id;
            this.namaPekerjaan = namaPekerjaan;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNamaPekerjaan() {
            return namaPekerjaan;
        }

        public void setNamaPekerjaan(String namaPekerjaan) {
            this.namaPekerjaan = namaPekerjaan;
        }
    }

    class SubPekerjaan {

        String id;
        String namaPekerjaan;

        public SubPekerjaan(String id, String namaPekerjaan) {
            this.id = id;
            this.namaPekerjaan = namaPekerjaan;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNamaPekerjaan() {
            return namaPekerjaan;
        }

        public void setNamaPekerjaan(String namaPekerjaan) {
            this.namaPekerjaan = namaPekerjaan;
        }
    }

    public MyDialogListener getListener() {
        return listener;
    }

    public void setListener(MyDialogListener listener) {
        this.listener = listener;
    }

    public static interface MyDialogListener {
        public void userSelectedAValue(String value);

        public void userCanceled();
    }

}
