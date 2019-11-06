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
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.byonchat.android.model.ScheduleList;
import com.byonchat.android.provider.DataBaseDropDown;
import com.byonchat.android.ui.activity.MainByonchatRoomBaseActivity;
import com.byonchat.android.utils.ExceptionHandler;
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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ByonchatScheduleSLAFragment extends Fragment {
    ScrollView scrollView;
    LinearLayout spanLayout;
    ImageView addLayout;
    TextView theChosen;
    String stChosen;
    Button submit;
    Activity mActivity;
    ArrayList<String> detaiArea = new ArrayList<>();
    ArrayList<String> kodeJJt = new ArrayList<>(); //list kode jjt nya saja
    EditText editParentTv;
    TextView editStart, editFinish;
    SearchableSpinner spinjjt, spinktr, spinpembobotan, spinsection, spinsubsec, spinperiod;
    String[] detailArea = new String[0];
    boolean change_position = false;
    public String username;

    ArrayList<ScheduleList> pembobotan = new ArrayList<>();
    ArrayList<ScheduleList> section = new ArrayList<>();
    ArrayList<ScheduleList> subsection = new ArrayList<>();

    DecimalFormat df2 = new DecimalFormat("#.##");

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
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler("ByonchatScheduleSLAFragment -> check log detail"));
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
        spinktr = (SearchableSpinner) view.findViewById(R.id.spinnerFreq);
        spinpembobotan = (SearchableSpinner) view.findViewById(R.id.spinnerPembobotan);
        spinsection = (SearchableSpinner) view.findViewById(R.id.spinnerSection);
        spinsubsec = (SearchableSpinner) view.findViewById(R.id.spinnerSubSection);
        spinperiod = (SearchableSpinner) view.findViewById(R.id.spinnerPeriod);
        theChosen = (TextView) view.findViewById(R.id.textxt);

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
            spinktr.setBackground(mActivity.getResources().getDrawable(R.drawable.spinner_background));
            spinpembobotan.setBackground(mActivity.getResources().getDrawable(R.drawable.spinner_background));
            spinsection.setBackground(mActivity.getResources().getDrawable(R.drawable.spinner_background));
            spinsubsec.setBackground(mActivity.getResources().getDrawable(R.drawable.spinner_background));
            spinperiod.setBackground(mActivity.getResources().getDrawable(R.drawable.spinner_background));
        }

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
//                String error = e.getMessage() + this;
//                Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(error));
            }
        }

        //List Frequency
        ArrayList<String> list_freq = new ArrayList<>();

        ArrayAdapter listAdp_freq = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, list_freq);
        listAdp_freq.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        list_freq.add("-- Pilih Keterangan --");

        //Adapter Pembobotan
        ArrayList<String> list_bobot = new ArrayList<>();

        ArrayAdapter listAdp_bobot = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, list_bobot);
        listAdp_bobot.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        list_bobot.add("-- Pilih Pembobotan --");

        //Adapter Section
        ArrayList<String> list_secs = new ArrayList<>();

        ArrayAdapter listAdp_secs = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, list_secs);
        listAdp_secs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        list_secs.add("-- Pilih Section --");

        //Adapter Subsection
        ArrayList<String> list_subsecs = new ArrayList<>();

        ArrayAdapter listAdp_subsecs = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, list_subsecs);
        listAdp_subsecs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        list_subsecs.add("-- Pilih Subsection --");

        //List Period
        ArrayList<String> list_perio = new ArrayList<>();

        ArrayAdapter listAdp_perio = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, list_perio);
        listAdp_perio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        list_perio.add("-- Pilih Periode --");

        editStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarDialog calendarDialog = new CalendarDialog(mActivity);
                calendarDialog.setListener(new CalendarDialog.MyDialogListener() {
                    @Override
                    public void userSelectedAValue(String value) {
                        editStart.setText(value);

                        if(!editFinish.getText().toString().equalsIgnoreCase("")) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date start = null, finish = null;
                            try {
                                start = dateFormat.parse(value);
                                finish = dateFormat.parse(editFinish.getText().toString());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            long msDiff = finish.getTime() - start.getTime();
                            long daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff);

                            list_perio.clear();
                            list_perio.add("-- Pilih Periode --");
                            if (daysDiff < 7) {
                                list_perio.add("One Time");
                            } else if (daysDiff >= 7 && daysDiff < 30) {
                                list_perio.add("One Time");
                                list_perio.add("Weekly");
                            } else if (daysDiff >= 30) {
                                list_perio.add("One Time");
                                list_perio.add("Weekly");
                                list_perio.add("Monthly");
                            }
                            listAdp_perio.notifyDataSetChanged();
                            theChosen.setText("");
                            spinperiod.setSelection(0);
                        }
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

                        if(!editStart.getText().toString().equalsIgnoreCase("")) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date start = null, finish = null;
                            try {
                                start = dateFormat.parse(editStart.getText().toString());
                                finish = dateFormat.parse(value);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            long msDiff = finish.getTime() - start.getTime();
                            long daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff);

                            list_perio.clear();
                            list_perio.add("-- Pilih Periode --");
                            if (daysDiff <= 7) {
                                list_perio.add("One Time");
                            } else if (daysDiff >= 8 && daysDiff < 30) {
                                list_perio.add("Weekly");
                            } else if (daysDiff >= 30) {
                                list_perio.add("Weekly");
                                list_perio.add("Monthly");
                            }
                            listAdp_perio.notifyDataSetChanged();
                            theChosen.setText("");
                            spinperiod.setSelection(0);
                        }
                    }

                    @Override
                    public void userCanceled() {
                    }
                });
                calendarDialog.show();
            }
        });

        spinjjt.setAdapter(listAdp);
        spinktr.setAdapter(listAdp_freq);
        spinpembobotan.setAdapter(listAdp_bobot);
        spinsection.setAdapter(listAdp_secs);
        spinsubsec.setAdapter(listAdp_subsecs);
        spinperiod.setAdapter(listAdp_perio);

        //Add List Selected Date
        spinperiod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SimpleDateFormat targetFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss (EEEE)");
                list_freq.clear();
                if(spinperiod.getSelectedItem().toString().equalsIgnoreCase("one time")){
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date start = null, finish = null;
                    try {
                        start = dateFormat.parse(editStart.getText().toString());
                        finish = dateFormat.parse(editFinish.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long msDiff = finish.getTime() - start.getTime();
                    long daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff);

                    GregorianCalendar gc = new GregorianCalendar();
                    gc.setTime(start);
                    Log.w("tdbretles",gc.getTime().toGMTString()+"");
                    stChosen = targetFormat.format(gc.getTime());
                    for(long i = 0; i < daysDiff; i++){
                        gc.add(Calendar.DATE,1);
                        Log.w("tdbretles",targetFormat.format(gc.getTime())+"");
                        stChosen = stChosen + "\n"+targetFormat.format(gc.getTime());
                    }
                    theChosen.setText(stChosen);

                    list_freq.add("-- Pilih Keterangan --");
                    list_freq.add("One Time");
                    list_freq.add("3 Bulan");
                    list_freq.add("6 Bulan");
                    list_freq.add("Tahunan");
                }else if(spinperiod.getSelectedItem().toString().equalsIgnoreCase("weekly")){
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date start = null, finish = null;
                    try {
                        start = dateFormat.parse(editStart.getText().toString());
                        finish = dateFormat.parse(editFinish.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long msDiff = finish.getTime() - start.getTime();
                    long daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff);

                    GregorianCalendar gc = new GregorianCalendar();
                    gc.setTime(start);
                    Log.w("tdbretles",gc.getTime()+"");
                    stChosen = targetFormat.format(gc.getTime());
                    for(long i = 0; i < daysDiff; i++){
                        gc.add(Calendar.DATE,7);
                        if(gc.getTime().before(finish)) {
                            Log.w("tdbretles", gc.getTime() + "");
                            stChosen = stChosen + "\n"+targetFormat.format(gc.getTime());
                        }
                    }
                    theChosen.setText(stChosen);

                    list_freq.add("-- Pilih Keterangan --");
                    list_freq.add("Weekly");
                }else if(spinperiod.getSelectedItem().toString().equalsIgnoreCase("monthly")){
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date start = null, finish = null;
                    try {
                        start = dateFormat.parse(editStart.getText().toString());
                        finish = dateFormat.parse(editFinish.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long msDiff = finish.getTime() - start.getTime();
                    long daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff);

                    GregorianCalendar gc = new GregorianCalendar();
                    gc.setTime(start);
                    Log.w("tdbretles",gc.getTime()+"");
                    stChosen = targetFormat.format(gc.getTime());
                    for(long i = 0; i < daysDiff; i++){
                        gc.add(Calendar.MONTH,1);
                        if(gc.getTime().before(finish)) {
                            Log.w("tdbretles", gc.getTime() + "");
                            stChosen = stChosen + "\n"+targetFormat.format(gc.getTime());
                        }
                    }
                    theChosen.setText(stChosen);

                    list_freq.add("-- Pilih Keterangan --");
                    list_freq.add("Monthly");
                }else {
                    list_freq.add("-- Pilih Keterangan --");
                    theChosen.setText("");
                }
                spinktr.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Add List Pembobotan When Spinner JJT Changes
        spinjjt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                list_bobot.clear();
                spinpembobotan.setSelection(0);
                list_bobot.add("-- Pilih Pembobotan --");
                if(spinjjt.getSelectedItemPosition() != 0) {
                    int jjt_pos = spinjjt.getSelectedItemPosition() - 1;
                    String kode_jjt = kodeJJt.get(jjt_pos);

                    getList_Like_SLA(kode_jjt);

                    for (int i = 0; i < pembobotan.size(); i++) {
                        list_bobot.add(pembobotan.get(i).getTitle());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //Add List Section When Spinner Bobot Changes
        spinpembobotan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                list_secs.clear();
                spinsection.setSelection(0);
                list_secs.add("-- Pilih Section --");
                if(spinpembobotan.getSelectedItemPosition() != 0) {
                    String bobot = spinpembobotan.getSelectedItem().toString();
                    String idBobot = "";
                    for (int i = 0; i < pembobotan.size(); i++) {
                        if(bobot.equalsIgnoreCase(pembobotan.get(i).getTitle())){
                            idBobot = pembobotan.get(i).getId();
                        }
                    }

                    for (int i = 0; i < section.size(); i++) {
                        if(idBobot.equalsIgnoreCase(section.get(i).getId_parent())){
                            list_secs.add(section.get(i).getTitle());
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //Add List SubSection When Spinner Section Changes
        spinsection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                list_subsecs.clear();
                spinsubsec.setSelection(0);
                list_subsecs.add("-- Pilih Subsection --");
                if(spinsection.getSelectedItemPosition() != 0)  {
                    String section1 = spinsection.getSelectedItem().toString();
                    String idSecs = "";
                    for (int i = 0; i < section.size(); i++) {
                        if(section1.equalsIgnoreCase(section.get(i).getTitle())){
                            idSecs = section.get(i).getId();
                        }
                    }

                    for (int i = 0; i < subsection.size(); i++) {
                        if(idSecs.equalsIgnoreCase(subsection.get(i).getId_parent())){
                            list_subsecs.add(subsection.get(i).getTitle());
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

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

                if (spinjjt.getSelectedItem().toString().equalsIgnoreCase("-- Pilih JJT --")) {
                    error = true;
                    Toast.makeText(mActivity, "Harap isi JJT", Toast.LENGTH_SHORT).show();
                }
                if (spinktr.getSelectedItem().toString().equalsIgnoreCase("-- Pilih Keterangan --")) {
                    error = true;
                    Toast.makeText(mActivity, "Harap isi Frequency", Toast.LENGTH_SHORT).show();
                }
                if (spinpembobotan.getSelectedItem().toString().equalsIgnoreCase("-- Pilih Pembobotan --")) {
                    error = true;
                    Toast.makeText(mActivity, "Harap isi Pembobotan", Toast.LENGTH_SHORT).show();
                }
                if (spinsection.getSelectedItem().toString().equalsIgnoreCase("-- Pilih Section --")) {
                    error = true;
                    Toast.makeText(mActivity, "Harap isi Section", Toast.LENGTH_SHORT).show();
                }
                if (spinsection.getSelectedItem().toString().equalsIgnoreCase("-- Pilih Subsection --")) {
                    error = true;
                    Toast.makeText(mActivity, "Harap isi Subsection", Toast.LENGTH_SHORT).show();
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

                            new InsertSchedule(mActivity).execute(linnk, kode_jjt, spinjjt.getSelectedItem().toString(), perioRes(spinktr.getSelectedItem().toString()),
                                    spinpembobotan.getSelectedItem().toString(), spinsection.getSelectedItem().toString(), spinsubsec.getSelectedItem().toString(),
                                    perioRes(spinperiod.getSelectedItem().toString()), editStart.getText().toString(), editFinish.getText().toString(), data_da);
                        } else {
                            Toast.makeText(getContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
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
            postData(params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[8], params[9], params[10]);
            return null;
        }

        protected void onPostExecute(String result) {
            if (error.length() > 0) {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
            }
        }

        protected void onProgressUpdate(String... string) {
        }

        public void postData(String link, String kd_jjt, String all_jjt, String ket, String bobot, String secs, String subsecs, String perio, String sd, String ed, String da) {
            try {
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 13000);
                HttpConnectionParams.setSoTimeout(httpParameters, 15000);
                HttpClient httpclient = new DefaultHttpClient(httpParameters);
                HttpPost httppost = new HttpPost(link);

                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("bc_user", username));
                nameValuePairs.add(new BasicNameValuePair("kode_jjt", all_jjt));
                nameValuePairs.add(new BasicNameValuePair("jjt_location", kd_jjt));

                nameValuePairs.add(new BasicNameValuePair("periode", perio));
                nameValuePairs.add(new BasicNameValuePair("keterangan", ket));

                nameValuePairs.add(new BasicNameValuePair("floor", "1"));
                nameValuePairs.add(new BasicNameValuePair("pembobotan", bobot));
                nameValuePairs.add(new BasicNameValuePair("section", secs));
                nameValuePairs.add(new BasicNameValuePair("sub_section", subsecs));
                nameValuePairs.add(new BasicNameValuePair("start_date", sd));
                nameValuePairs.add(new BasicNameValuePair("end_date", ed));

                for (int i = 0; i < detaiArea.size(); i++) {
                    nameValuePairs.add(new BasicNameValuePair("detail_area["+i+"]", detaiArea.get(i)));
                    Log.e("paramsend meterss","detail_area ["+i+"]"+"  --> "+ detaiArea.get(i));
                }

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);

                    mActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(mActivity,"Schedule has been created",Toast.LENGTH_SHORT).show();
                            mActivity.onBackPressed();
                        }
                    });

                    /*Intent detail = new Intent(mActivity, ScheduleSLAPeriod.class);
                    detail.putExtra("jt", all_jjt);
                    detail.putExtra("fq", spinktr.getSelectedItem().toString());
                    detail.putExtra("fl", spinpembobotan.getSelectedItem().toString());
                    detail.putExtra("pr", spinperiod.getSelectedItem().toString());
                    detail.putExtra("sd", editStart.getText().toString());
                    detail.putExtra("fd", editFinish.getText().toString());
                    detail.putExtra("da", detaiArea);
                    startActivity(detail);*/

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

    //Get List Pembobotan / Section / Subsection equals with Tab_SLA
    private void getList_Like_SLA(String kodeJJT){
        pembobotan.clear();
        section.clear();
        subsection.clear();

        DataBaseDropDown mDBDquerySLA = new DataBaseDropDown(mActivity, "sqlite_iss");


        String asiop[] = {"jt.id AS id_jjt", "pb.id AS id_pembobotan", "pb.nama_pembobotan AS nama_pembobotan", "pb.grade AS grade"
                , "s.id AS id_section", "s.title AS title_section", "ss.id AS id_subsection", "ss.title AS title_subsection", "p.id AS id_pertanyaan", "p.pertanyaan AS pertanyaan", "jt.pass_grade AS pass_gradeNya"};

        String asiap = "jjt jt\n" +
                "INNER JOIN jjt_checklists jtc ON jt.id=jtc.id_jjt\n" +
                "INNER JOIN pembobotan pb ON pb.id=jtc.id_pembobotan\n" +
                "INNER JOIN pembobotan_checklists pc ON pb.id=pc.id_pembobotan\n" +
                "INNER JOIN section s ON s.id=pc.id_section\n" +
                "INNER JOIN section_checklists sc ON s.id=sc.id_section\n" +
                "INNER JOIN sub_section ss ON ss.id=sc.id_subsection\n" +
                "INNER JOIN subsection_checklists sbc ON ss.id=sbc.id_subsection\n" +
                "INNER JOIN pertanyaan p ON p.id=sbc.id_pertanyaan";

        final Cursor css = mDBDquerySLA.getWritableDatabase().query(true, asiap, asiop, "jt.kode='" + kodeJJT + "'", null, null, null, null, null);

        if(css.moveToFirst()){
            String bobotIdOld = "";
            String sectionOld = "";
            String subSectionOld = "";

            try {

                do {
                    String idBobot = css.getString(1);
                    String idSection = css.getString(4);
                    String idSubSection = css.getString(6);

                    String namaBobot = css.getString(2);
                    String namaSection = css.getString(5);
                    String namaSubSection = css.getString(7);

                    if (bobotIdOld.equalsIgnoreCase(idBobot)) {
                        if (sectionOld.equalsIgnoreCase(idSection)) {
                            if (!subSectionOld.equalsIgnoreCase(idSubSection)) {
                                subSectionOld = idSubSection;

                                ScheduleList subsectioning = new ScheduleList(idSubSection, idSection, namaSubSection);
                                subsection.add(subsectioning);
                            }
                        } else {
                            subSectionOld = idSubSection;
                            sectionOld = idSection;

                            ScheduleList subsectioning = new ScheduleList(idSubSection, idSection, namaSubSection);
                            subsection.add(subsectioning);

                            ScheduleList sectioning = new ScheduleList(idSection, idBobot, namaSection);
                            section.add(sectioning);

                        }
                    } else {
                        bobotIdOld = idBobot;
                        sectionOld = idSection;
                        subSectionOld = idSubSection;

                        ScheduleList subsectioning = new ScheduleList(idSubSection, idSection, namaSubSection);
                        subsection.add(subsectioning);

                        ScheduleList sectioning = new ScheduleList(idSection, idBobot, namaSection);
                        section.add(sectioning);

                        ScheduleList bobot = new ScheduleList(idBobot, namaBobot);
                        pembobotan.add(bobot);
                    }

                } while (css.moveToNext());

            } catch (Exception e) {

            }
        }
    }

    public static String perioRes(String value){
        String result;
        if(value.equalsIgnoreCase("One Time")){
            result = "one_time";
        }else if(value.equalsIgnoreCase("Weekly")){
            result = "mingguan";
        }else if(value.equalsIgnoreCase("Monthly")){
            result = "bulanan";
        }else if(value.equalsIgnoreCase("Yearly")){
            result = "tahunan";
        }else if(value.equalsIgnoreCase("Tahunan")){
            result = "tahunan";
        }else if(value.equalsIgnoreCase("3 Bulan")){
            result = "3_bulanan";
        }else if(value.equalsIgnoreCase("6 Bulan")){
            result = "6_bulanan";
        }else if(value.equalsIgnoreCase("3 Monthly")){
            result = "3_bulanan";
        }else if(value.equalsIgnoreCase("6 Monthly")){
            result = "6_bulanan";
        }else {
            result = value;
        }

        return result;
    }

    public static String getPerioRes(String value){
        String result;
        if(value.equalsIgnoreCase("one_time")){
            result = "One Time";
        }else if(value.equalsIgnoreCase("mingguan")){
            result = "Weekly";
        }else if(value.equalsIgnoreCase("bulanan")){
            result = "Monthly";
        }else if(value.equalsIgnoreCase("tahunan")){
            result = "Yearly";
        }else if(value.equalsIgnoreCase("3_bulanan")){
            result = "3 Month";
        }else if(value.equalsIgnoreCase("6_bulanan")){
            result = "6 Month";
        }else {
            result = value;
        }

        return result;
    }
}
