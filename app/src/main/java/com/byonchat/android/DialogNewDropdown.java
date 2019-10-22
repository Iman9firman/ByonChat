package com.byonchat.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Scroller;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
import com.byonchat.android.model.Model;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.DataBaseDropDown;
import com.byonchat.android.provider.DataBaseHelper;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.utils.DialogUtil;
import com.byonchat.android.utils.Validations;
import com.toptoche.searchablespinnerlibrary.SearchableListDialog;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import static com.byonchat.android.FragmentDinamicRoom.FragmentRoomAPI.function;

/**
 * Created by Lukmanpryg on 7/19/2016.
 */
@SuppressLint("ValidFragment")
public class DialogNewDropdown extends DialogFragment {

    private Button mProceed, mCancel;
    private LinearLayout lParent;
    private ProgressBar loading;
    private finishListener listener;
    String type;

    private String cModel, cType, cWarna, cTipe_harga, cWilayah, cHarga, cKode, defaultValue;

    private ArrayList<Model> v1 = new ArrayList<>();
    private ArrayList<Model> v2 = new ArrayList<>();
    private ArrayList<Model> v3 = new ArrayList<>();
    private ArrayList<Model> v4 = new ArrayList<>();
    private ArrayList<Model> v5 = new ArrayList<>();

    public DialogNewDropdown(finishListener listener, String type, String defaultV) {
        super();
        this.listener = listener;
        this.type = type;
        this.defaultValue = defaultV;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialog = inflater.inflate(R.layout.dialog_form_child_layout, container, false);

        mProceed = (Button) dialog.findViewById(R.id.btn_proceed);
        mCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        lParent = (LinearLayout) dialog.findViewById(R.id.linear);
        loading = (ProgressBar) dialog.findViewById(R.id.loading);

        loading.setVisibility(View.VISIBLE);

        mProceed.setText("Save ");

        mProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!setjson().contains("--Please Select--")) {
                    listener.submitted(setjson());
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Harap isi semua field", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (new Validations().getInstance(getActivity()).getCars() != null) {
            try {
                JSONArray jsonArray = new JSONArray(new Validations().getInstance(getActivity()).getCars());
                buildDropdown(jsonArray.toString());
                return;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        new getCars(getActivity()).execute("https://bb.byonchat.com/bc_voucher_client/webservice/list_api/honda/convert_json_api_gzip.php", "106");
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }


    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Dialog);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private class getCars extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        String error = "";
        private Activity activity;
        private Context context;

        public getCars(Activity activity) {
            this.activity = activity;
            context = activity;
            dialog = new ProgressDialog(activity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Loading...");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            postData(params[0], params[1]);
            return null;
        }

        protected void onPostExecute(String result) {

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (loading.getVisibility() == View.VISIBLE) {
                loading.setVisibility(View.GONE);
            }
        }

        protected void onProgressUpdate(String... string) {
        }

        public void postData(String valueIWantToSend, String dealer) {
            // Create a new HttpClient and Post Header

            try {
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 13000);
                HttpConnectionParams.setSoTimeout(httpParameters, 15000);
                HttpClient httpclient = new DefaultHttpClient(httpParameters);
                HttpPost httppost = new HttpPost(valueIWantToSend);

                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("kode_dealer", dealer));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {

                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);
                    byte[] decodedBytes = Base64.decode(data, 0);

                    try {
                        String rr = decompress(decodedBytes);
                        new Validations().getInstance(context).setCarsTemp(rr);
                        JSONArray jsonArray = new JSONArray(rr);
                        buildDropdown(jsonArray.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    error = "Tolong periksa koneksi internet.";
                }

            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
        }

    }

    public static String decompress(byte[] compressed) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
        GZIPInputStream gis = new GZIPInputStream(bis);
        BufferedReader br = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        gis.close();
        bis.close();
        return sb.toString();
    }

    private void buildDropdown(String json) throws JSONException {
        Model modelz = new Model("--Please Select--", "0", "0");
        v1.clear();
        v2.clear();
        v3.clear();
        v4.clear();
        v5.clear();
        v1.add(modelz);
        v2.add(modelz);
        v3.add(modelz);
        v4.add(modelz);
        v5.add(modelz);

        JSONArray jsonArray = new JSONArray(json);
        for (int i = 0; i < jsonArray.length(); i++) {
            String model = jsonArray.getJSONObject(i).getString("model").toString();
            JSONArray jsonArrayModel = new JSONArray(jsonArray.getJSONObject(i).getString("value"));
            Model model1 = new Model(model, i + "", i + ""); //Model
            v1.add(model1);

            for (int ii = 0; ii < jsonArrayModel.length(); ii++) {
                String type = jsonArrayModel.getJSONObject(ii).getString("tipe").toString();
                JSONArray jsonArrayType = new JSONArray(jsonArrayModel.getJSONObject(ii).getString("value"));
                Model type1 = new Model(type, ii + "", model); //Type
                v2.add(type1);

                for (int iii = 0; iii < jsonArrayType.length(); iii++) {
                    String warna = jsonArrayType.getJSONObject(iii).getString("warna").toString();
                    JSONArray jsonArrayWarna = new JSONArray(jsonArrayType.getJSONObject(iii).getString("value"));
                    Model warna1 = new Model(warna, iii + "", type); //Warna
                    v3.add(warna1);

                    for (int iv = 0; iv < jsonArrayWarna.length(); iv++) {
                        String tipe_harga = jsonArrayWarna.getJSONObject(iv).getString("tipe_harga").toString();
                        JSONArray jsonArrayTiHarga = new JSONArray(jsonArrayWarna.getJSONObject(iv).getString("value"));
                        Model tipe_harga1 = new Model(tipe_harga, iv + "", type + warna); //tipe Harga
                        v4.add(tipe_harga1);

                        for (int v = 0; v < jsonArrayTiHarga.length(); v++) {
                            String harga = jsonArrayTiHarga.getJSONObject(v).getString("harga").toString();
                            String wilayah = jsonArrayTiHarga.getJSONObject(v).getString("wilayah").toString();
                            String kode = jsonArrayTiHarga.getJSONObject(v).getString("kode").toString();
                            Model harga1 = new Model(wilayah + "//" + harga + "//" + kode, v + "", type + warna + tipe_harga); //Harga
                            v5.add(harga1);
                        }
                    }
                }
            }
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loading.setVisibility(View.GONE);
                addNewDropdown(v1, "Model", 0);
            }
        });
    }

    private void addNewDropdown(ArrayList<Model> spinn, String title, int index) {

        ArrayList<String> spinnerArray = new ArrayList<>();

        for (int i = 0; i < spinn.size(); i++) {
            if (title.equalsIgnoreCase("wilayah")) {
                String[] separated = spinn.get(i).getName().split("//");
                spinnerArray.add(separated[0]);
            } else {
                spinnerArray.add(spinn.get(i).getName());
            }
        }

        final LinearLayout spinerTitle = (LinearLayout) getLayoutInflater().inflate(R.layout.item_spiner_textview, null);
        TextView textViewFirst = (TextView) spinerTitle.findViewById(R.id.title);

        textViewFirst.setText(Html.fromHtml(title));
        textViewFirst.setTextSize(15);

        final SearchableSpinner spinnner = (SearchableSpinner) spinerTitle.findViewById(R.id.spinner);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            spinnner.setBackground(getResources().getDrawable(R.drawable.spinner_background));
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.simple_spinner_item_black, spinnerArray); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnner.setAdapter(spinnerArrayAdapter);

        lParent.addView(spinerTitle, index);

        try {
            JSONObject jsonObjectDefaultValue = new JSONObject(defaultValue);
            if (jsonObjectDefaultValue.has(title)) {
                spinnner.setSelection(spinnerArrayAdapter.getPosition(jsonObjectDefaultValue.getString(title)));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        spinnner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (!type.equalsIgnoreCase("1")) {
                    if (title.equalsIgnoreCase("model")) {
                        ArrayList<Model> vx1 = new ArrayList<>();
                        vx1.clear();
                        Model modelz = new Model("--Please Select--", "0", "0");
                        vx1.add(modelz);

                        cModel = (String) spinnner.getSelectedItem();

                        deleteFromParent(1);
                        deleteFromParent(2);
                        deleteFromParent(3);
                        deleteFromParent(4);
                        deleteFromParent(5);

                        if (!cModel.equalsIgnoreCase("--Please Select--")) {
                            for (int i = 0; i < v2.size(); i++) {
                                if (cModel.equals(v2.get(i).getId_parent())) {
                                    Model xxs = new Model(v2.get(i).getName(), v2.get(i).getId_self(), v2.get(i).getId_parent());
                                    vx1.add(xxs);
                                }
                            }
                            addNewDropdown(vx1, "Type", 1);
                        } else {
                            deleteFromParent(1);
                            deleteFromParent(2);
                            deleteFromParent(3);
                            deleteFromParent(4);
                            deleteFromParent(5);
                        }
                    }

                    if (title.equalsIgnoreCase("type")) {
                        ArrayList<Model> vx1 = new ArrayList<>();
                        vx1.clear();
                        Model modelz = new Model("--Please Select--", "0", "0");
                        vx1.add(modelz);

                        cType = (String) spinnner.getSelectedItem();

                        deleteFromParent(2);
                        deleteFromParent(3);
                        deleteFromParent(4);
                        deleteFromParent(5);

                        if (!cType.equalsIgnoreCase("--Please Select--")) {
                            for (int i = 0; i < v3.size(); i++) {
                                if (cType.equals(v3.get(i).getId_parent())) {
                                    Model xxs = new Model(v3.get(i).getName(), v3.get(i).getId_self(), v3.get(i).getId_parent());
                                    vx1.add(xxs);
                                }
                            }
                            addNewDropdown(vx1, "Warna", 2);
                        } else {
                            deleteFromParent(2);
                            deleteFromParent(3);
                            deleteFromParent(4);
                            deleteFromParent(5);
                        }
                    }

                    if (title.equalsIgnoreCase("warna")) {
                        ArrayList<Model> vx1 = new ArrayList<>();
                        vx1.clear();
                        Model modelz = new Model("--Please Select--", "0", "0");
                        vx1.add(modelz);

                        cWarna = (String) spinnner.getSelectedItem();

                        deleteFromParent(3);
                        deleteFromParent(4);
                        deleteFromParent(5);

                        if (!cWarna.equalsIgnoreCase("--Please Select--")) {

                            for (int i = 0; i < v4.size(); i++) {
                                String kode = cType + cWarna;
                                if (kode.equals(v4.get(i).getId_parent())) {
                                    Model xxs = new Model(v4.get(i).getName(), v4.get(i).getId_self(), v4.get(i).getId_parent());
                                    vx1.add(xxs);
                                }
                            }
                            addNewDropdown(vx1, "Price Type", 3);
                        } else {
                            deleteFromParent(3);
                            deleteFromParent(4);
                            deleteFromParent(5);
                        }
                    }

                    if (title.equalsIgnoreCase("price type")) {
                        ArrayList<Model> vx1 = new ArrayList<>();
                        vx1.clear();
                        Model modelz = new Model("--Please Select--", "0", "0");
                        vx1.add(modelz);

                        cTipe_harga = (String) spinnner.getSelectedItem();

                        deleteFromParent(4);
                        deleteFromParent(5);

                        if (!cTipe_harga.equalsIgnoreCase("--Please Select--")) {

                            for (int i = 0; i < v5.size(); i++) {
                                String kode = cType + cWarna + cTipe_harga;
                                if (kode.equals(v5.get(i).getId_parent())) {
                                    Model xxs = new Model(v5.get(i).getName(), v5.get(i).getId_self(), v5.get(i).getId_parent());
                                    vx1.add(xxs);
                                }
                            }
                            addNewDropdown(vx1, "Wilayah", 4);
                        } else {
                            deleteFromParent(4);
                            deleteFromParent(5);
                        }
                    }

                    if (title.equalsIgnoreCase("wilayah")) {
                        cWilayah = (String) spinnner.getSelectedItem();
                        LinearLayout linearText = new LinearLayout(getContext());
                        linearText.setOrientation(LinearLayout.VERTICAL);

                        TextView textView = new TextView(getContext());
                        textView.setText("Price");
                        textView.setTextSize(15);
                        textView.setTextColor(getResources().getColor(R.color.navigationBarColor));

                        TextView tvHarga = (TextView) getLayoutInflater().inflate(R.layout.text_input_layout, null);
                        tvHarga.setMinLines(1);

                        TextView textViewKode = new TextView(getContext());
                        textViewKode.setText("Kode");
                        textViewKode.setTextSize(15);
                        textViewKode.setTextColor(getResources().getColor(R.color.navigationBarColor));

                        TextView kode = (TextView) getLayoutInflater().inflate(R.layout.text_input_layout, null);
                        kode.setMinLines(1);

                        deleteFromParent(5);

                        if (!cWilayah.equalsIgnoreCase("--Please Select--")) {
                            for (int i = 0; i < spinn.size(); i++) {
                                String[] separated = spinn.get(i).getName().split("//");
                                if (cWilayah.equalsIgnoreCase(separated[0])) {
                                    tvHarga.setText("Rp. " + formatCurrency(separated[1]));
                                    cHarga = tvHarga.getText().toString();
                                    cKode = separated[2];
                                    kode.setText(cKode);
                                }
                            }
                            linearText.addView(textView);
                            linearText.addView(tvHarga);
                            linearText.addView(textViewKode);
                            linearText.addView(kode);

                            lParent.addView(linearText, 5);
                        }
                        defaultValue = "{}";
                    }
                } else {
                    cModel = (String) spinnner.getSelectedItem();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void deleteFromParent(int index) {
        try {
            lParent.removeViewAt(index);
        } catch (Exception e) {
            Log.w("Something wrong", e.getMessage());
        }
    }

    public static String formatCurrency(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(Double.parseDouble(amount));
    }

    public String setjson() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("Model", cModel);
            if (!type.equalsIgnoreCase("1")) {
                obj.put("Type", cType);
                obj.put("Warna", cWarna);
                obj.put("Price Type", cTipe_harga);
                obj.put("Wilayah", cWilayah);
                obj.put("Price", cHarga);
                obj.put("Kode", cKode);
            }


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return obj.toString();
    }

    public interface finishListener {
        public void submitted(String json);
    }

}
