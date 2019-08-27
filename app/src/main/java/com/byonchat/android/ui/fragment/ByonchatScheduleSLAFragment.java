package com.byonchat.android.ui.fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.ISSActivity.LoginDB.UserDB;
import com.byonchat.android.R;
import com.byonchat.android.Sample.ScheduleSLAPeriod;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.local.Byonchat;
import com.byonchat.android.ui.activity.MainByonchatRoomBaseActivity;
import com.byonchat.android.widget.CalendarDialog;
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
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ByonchatScheduleSLAFragment extends Fragment {
    ScrollView scrollView;
    LinearLayout spanLayout;
    ImageView addLayout;
    Button submit;
    Activity mActivity;
    ArrayList<String> detaiArea = new ArrayList<>();
    ArrayList<String> kodeJJt = new ArrayList<>(); //list kode jjt nya saja
    EditText editParentTv;
    TextView editStart, editFinish;
    SearchableSpinner spinjjt, spinfreq, spinfloor, spinperiod;
    String[] detailArea = new String[0];
    boolean change_position = false;
    public String username;

    public ByonchatScheduleSLAFragment() {

    }

    @SuppressLint("ValidFragment")
    public ByonchatScheduleSLAFragment(MainByonchatRoomBaseActivity activity) {
        this.mActivity = activity;
    }

    public static ByonchatScheduleSLAFragment newInstance(String myc, String tit, String utm, String usr, String idrtab, String color, MainByonchatRoomBaseActivity activity) {
        ByonchatScheduleSLAFragment fragment = new ByonchatScheduleSLAFragment(activity);
        Bundle args = new Bundle();
        args.putString("aa", tit);
        args.putString("bb", utm);
        args.putString("cc", usr);
        args.putString("dd", idrtab);
        args.putString("ee", myc);
        args.putString("col", color);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.form_submit_schedule_sla, container, false);

        spanLayout = (LinearLayout) view.findViewById(R.id.spanLayout);
        addLayout = (ImageView) view.findViewById(R.id.addLayout);
        submit = (Button) view.findViewById(R.id.button_submit);
        scrollView = (ScrollView) view.findViewById(R.id.scrollview);
        editStart = (TextView) view.findViewById(R.id.editStartDate);
        editFinish = (TextView) view.findViewById(R.id.editFinishDate);
        editParentTv = (EditText) view.findViewById(R.id.editParentTv);
        spinjjt = (SearchableSpinner) view.findViewById(R.id.spinnerJJT);
        spinfreq = (SearchableSpinner) view.findViewById(R.id.spinnerFreq);
        spinfloor = (SearchableSpinner) view.findViewById(R.id.spinnerFloor);
        spinperiod = (SearchableSpinner) view.findViewById(R.id.spinnerPeriod);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setContent();
        addAndDeleteLayout();
        submitTaskSchedule();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setContent(){
        username = Byonchat.getMessengerHelper().getMyContact().getJabberId();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            spinjjt.setBackground(mActivity.getResources().getDrawable(R.drawable.spinner_background));
            spinfreq.setBackground(mActivity.getResources().getDrawable(R.drawable.spinner_background));
            spinfloor.setBackground(mActivity.getResources().getDrawable(R.drawable.spinner_background));
            spinperiod.setBackground(mActivity.getResources().getDrawable(R.drawable.spinner_background));
        }

        editStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarDialog calendarDialog = new CalendarDialog(mActivity);
                calendarDialog.setListener(new CalendarDialog.MyDialogListener() {
                    @Override
                    public void userSelectedAValue(String value) {
                        editStart.setText(value);
                    }

                    @Override
                    public void userCanceled() {
                    }
                });
                calendarDialog.show();
            }
        });

        editFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarDialog calendarDialog = new CalendarDialog(mActivity);
                calendarDialog.setListener(new CalendarDialog.MyDialogListener() {
                    @Override
                    public void userSelectedAValue(String value) {
                        editFinish.setText(value);
                    }

                    @Override
                    public void userCanceled() {
                    }
                });
                calendarDialog.show();
            }
        });

        //List JJT
        ArrayList<String> list = new ArrayList<>();

        ArrayAdapter listAdp = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, list);
        listAdp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        String lat_long;
        UserDB userDB = UserDB.getInstance(mActivity);
        list.add("-- Pilih JJT --");

        Cursor cursorJJT = userDB.getSingle();
        if (cursorJJT.getCount() > 0) {
            String contentJJT = cursorJJT.getString(cursorJJT.getColumnIndexOrThrow(UserDB.EMPLOYEE_MULTICOST));
            try {
                JSONArray arr = new JSONArray(contentJJT);


                for (int as = 0; as < arr.length(); as++) {
                    JSONObject jo = arr.getJSONObject(as);
                    String cost_center = jo.getString("costcenter");
                    lat_long = jo.getString("latlng");
                    if (lat_long.equalsIgnoreCase("")) {
                        lat_long = "-6.1989168,106.7591713";
                    }
                    Log.w("farani", cost_center);
                    list.add(cost_center.substring(0, cost_center.indexOf("[")));
                    kodeJJt.add(arr.getString(as).substring(arr.getString(as).indexOf("[") + 1, arr.getString(as).indexOf("]")));
                }
//                    spinnerArraySla.add("ISS-01035F0011-FITNESS FIRST CIBUBUR JUNCTION");
//                    duaJJt.add("ISS-01035F0011");


            } catch (Exception e) {
            }
        }

        //List Frequency
        ArrayList<String> list_freq = new ArrayList<>();

        ArrayAdapter listAdp_freq = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, list_freq);
        listAdp_freq.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        list_freq.add("-- Pilih Frequency --");
        list_freq.add("Harian");
        list_freq.add("Mingguan");
        list_freq.add("Bulanan");
        list_freq.add("6 Bulanan");
        list_freq.add("Tahunan");

        //List Floor
        ArrayList<String> list_floor = new ArrayList<>();

        ArrayAdapter listAdp_floor = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, list_floor);
        listAdp_floor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        list_floor.add("-- Pilih Floor --");
        list_floor.add("Lt. 1");
        list_floor.add("Lt. 2");
        list_floor.add("Lt. 3");
        list_floor.add("Lt. 4");
        list_floor.add("Lt. 6");
        list_floor.add("Lt. 7");
        list_floor.add("Lt. 8");
        list_floor.add("Lt. 9");

        //List Period
        ArrayList<String> list_perio = new ArrayList<>();

        ArrayAdapter listAdp_perio = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, list_perio);
        listAdp_perio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        list_perio.add("-- Pilih Periode --");
        list_perio.add("Harian");
        list_perio.add("Mingguan");
        list_perio.add("Bulanan");
        list_perio.add("6 Bulanan");
        list_perio.add("Tahunan");

        spinjjt.setAdapter(listAdp);
        spinfreq.setAdapter(listAdp_freq);
        spinfloor.setAdapter(listAdp_floor);
        spinperiod.setAdapter(listAdp_perio);

    }

    public void addAndDeleteLayout(){
        addLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editParentTv.getText().toString().equalsIgnoreCase("")) {

                    detaiArea.add(editParentTv.getText().toString());

                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params2.weight = 2.0f;
                    params3.weight = 8.0f;

                    LinearLayout line1 = new LinearLayout(mActivity);
                    line1.setOrientation(LinearLayout.HORIZONTAL);
                    line1.setWeightSum(10);
                    line1.setGravity(Gravity.CENTER);
                    line1.setPadding(0, dpToPx(5), 0, dpToPx(5));

                    TextView edt = new TextView(mActivity);
                    edt.setBackground(getResources().getDrawable(R.drawable.rounder_editext));
                    edt.setPadding(dpToPx(5), dpToPx(5), dpToPx(5), dpToPx(5));
                    edt.setTextSize(20);
                    edt.setText(editParentTv.getText().toString());
                    line1.addView(edt, params2);

                    editParentTv.setText("");

                    ImageView img = new ImageView(mActivity);
                    img.setImageDrawable(getResources().getDrawable(R.drawable.min_sla));
                    line1.addView(img, params3);

                    spanLayout.addView(line1, params1);

                    img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            spanLayout.removeView(line1);
                            if (edt.getText() != null) {
                                detaiArea.remove(edt.getText().toString());
                            }
                        }
                    });

                    edt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                            final View formsView = inflater.inflate(R.layout.dialog_edit_text, null, false);
                            final EditText edit = (EditText) formsView.findViewById(R.id.edit);

                            if (edt.getText() != null) {
                                edit.setText(edt.getText().toString());
                            }

                            new AlertDialog.Builder(mActivity)
                                    .setView(formsView)
                                    .setTitle("Edit")
                                    .setPositiveButton("Save",
                                            new DialogInterface.OnClickListener() {
                                                @TargetApi(11)
                                                public void onClick(DialogInterface dialog, int id) {
                                                    if (edt.getText() != null) {
                                                        detaiArea.remove(edt.getText().toString());
                                                    }
                                                    edt.setText(edit.getText());
                                                    detaiArea.add(edit.getText().toString());
                                                    dialog.cancel();
                                                }
                                            })
                                    .show();
                        }
                    });
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            }
        });
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public void submitTaskSchedule(){
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean error = false;

                if(change_position){
                        Intent detail = new Intent(mActivity, ScheduleSLAPeriod.class);
                        detail.putExtra("jt", spinjjt.getSelectedItem().toString());
                        detail.putExtra("fq", spinfreq.getSelectedItem().toString());
                        detail.putExtra("fl", spinfloor.getSelectedItem().toString());
                        detail.putExtra("pr", spinperiod.getSelectedItem().toString());
                        detail.putExtra("sd", editStart.getText().toString());
                        detail.putExtra("fd", editFinish.getText().toString());
                        detail.putExtra("da", detaiArea);
                        startActivity(detail);
                }else {
                    if (spinjjt.getSelectedItem().toString().equalsIgnoreCase("-- Pilih JJT --")) {
                        error = true;
                        Toast.makeText(mActivity, "Harap isi JJT", Toast.LENGTH_SHORT).show();
                    }
                    if (spinfreq.getSelectedItem().toString().equalsIgnoreCase("-- Pilih Frequency --")) {
                        error = true;
                        Toast.makeText(mActivity, "Harap isi Frequency", Toast.LENGTH_SHORT).show();
                    }
                    if (spinfloor.getSelectedItem().toString().equalsIgnoreCase("-- Pilih Floor --")) {
                        error = true;
                        Toast.makeText(mActivity, "Harap isi Floor", Toast.LENGTH_SHORT).show();
                    }
                    if (spinperiod.getSelectedItem().toString().equalsIgnoreCase("-- Pilih Periode --")) {
                        error = true;
                        Toast.makeText(mActivity, "Harap isi Periode", Toast.LENGTH_SHORT).show();
                    }
                    if (editStart.getText().toString().equalsIgnoreCase("")) {
                        error = true;
                        Toast.makeText(mActivity, "Harap isi Start Date", Toast.LENGTH_SHORT).show();
                    }
                    if (editFinish.getText().toString().equalsIgnoreCase("")) {
                        error = true;
                        Toast.makeText(mActivity, "Harap isi Finish Date", Toast.LENGTH_SHORT).show();
                    }
                    if (detaiArea.size() == 0) {
                        error = true;
                        Toast.makeText(mActivity, "Harap isi Detail Area", Toast.LENGTH_SHORT).show();
                    }

                    if (!error) {

                        for (int i = 0; i < detaiArea.size(); i++) {
                            detailArea = new String[detaiArea.size()];
                            detailArea[i] = detaiArea.get(i);
                            String sac = detaiArea.get(i);
                        }
                        if (detaiArea.size() > 0) {
                            String pilih = "";
                            for (String is : detaiArea) {
                                pilih += is + ", ";
                            }

                            String data_da = pilih.substring(0, pilih.length() - 1);

                            String linnk = "https://bb.byonchat.com/bc_voucher_client/webservice/list_api/iss/schedule/schedule_insert.php";
                            if (NetworkInternetConnectionStatus.getInstance(getContext()).isOnline(getContext())) {

                                int jjt_pos = spinjjt.getSelectedItemPosition()-1;
                                String kode_jjt = kodeJJt.get(jjt_pos);

                                new InsertSchedule(mActivity).execute(linnk, spinjjt.getSelectedItem().toString(), kode_jjt, spinfreq.getSelectedItem().toString(),
                                        spinfloor.getSelectedItem().toString(), spinperiod.getSelectedItem().toString(), editStart.getText().toString(),
                                        editFinish.getText().toString(), data_da);
                            } else {
                                Toast.makeText(getContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        });
    }


    private class InsertSchedule extends AsyncTask<String, String, String> {
        String error = "";
        private Context context;
        ProgressDialog progressDialog;

        public InsertSchedule(Context activity) {
            context = activity;
        }

        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Uploading!");
            progressDialog.setMessage("Please wait a second");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            postData(params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[8]);
            return null;
        }

        protected void onPostExecute(String result) {
            if (error.length() > 0) {
                 Toast.makeText(context, error, Toast.LENGTH_LONG).show();
            }
        }

        protected void onProgressUpdate(String... string) {
        }

        public void postData(String link, String jjt, String kd_jjt, String freq, String floor, String perio, String sd, String ed, String da) {
            try {
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 13000);
                HttpConnectionParams.setSoTimeout(httpParameters, 15000);
                HttpClient httpclient = new DefaultHttpClient(httpParameters);
                HttpPost httppost = new HttpPost(link);

                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("bc_user", username));
                nameValuePairs.add(new BasicNameValuePair("kode_jjt", jjt));
                nameValuePairs.add(new BasicNameValuePair("jjt_location", kd_jjt));

                nameValuePairs.add(new BasicNameValuePair("frequency", freq));
                nameValuePairs.add(new BasicNameValuePair("floor", floor));
                nameValuePairs.add(new BasicNameValuePair("periode", perio));
                nameValuePairs.add(new BasicNameValuePair("start_date", sd));
                nameValuePairs.add(new BasicNameValuePair("end_date", ed));

                for (int i = 0; i < detaiArea.size(); i++) {
                    nameValuePairs.add(new BasicNameValuePair("detail_area["+i+"]", detaiArea.get(i)));
                    Log.e("Params meterss","detail_area ["+i+"]"+"  --> "+ detaiArea.get(i));
                }

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);

                    Intent detail = new Intent(mActivity, ScheduleSLAPeriod.class);
                    detail.putExtra("jt", spinjjt.getSelectedItem().toString());
                    detail.putExtra("fq", spinfreq.getSelectedItem().toString());
                    detail.putExtra("fl", spinfloor.getSelectedItem().toString());
                    detail.putExtra("pr", spinperiod.getSelectedItem().toString());
                    detail.putExtra("sd", editStart.getText().toString());
                    detail.putExtra("fd", editFinish.getText().toString());
                    detail.putExtra("da", detaiArea);
                    startActivity(detail);

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
            progressDialog.dismiss();
        }

    }
}
