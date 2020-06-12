package com.byonchat.android.ui.activity;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.byonchat.android.ISSActivity.LoginDB.UserDB;
import com.byonchat.android.R;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.DataBaseHelper;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.tempSchedule.MyEventDatabase;
import com.byonchat.android.utils.AndroidMultiPartEntity;
import com.byonchat.android.utils.ClientSSLSocketFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DialogRejectRequest extends DialogFragment {

    String id_hps = "";
    long id = 0;
    String description = "";
    LinearLayout linearLayout;
    Button mCancel, mOkay;
    MessengerDatabaseHelper databaseHelper;
    UserDB dbHelper;
    DialogRejectListener listener;

    public static DialogRejectRequest newInstance(String id) {
        DialogRejectRequest f = new DialogRejectRequest();
        Bundle args = new Bundle();
        args.putString("id", id);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id_hps = getArguments().getString("id");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialog = inflater.inflate(R.layout.dialog_form_child_layout, container, false);

        dbHelper = new UserDB(getContext());
        databaseHelper = MessengerDatabaseHelper.getInstance((FragmentActivity) getContext());

        linearLayout = (LinearLayout) dialog.findViewById(R.id.linear);
        linearLayout.setPadding(16,16,16,16);

        TextView textView = new TextView(getContext());
        textView.setText("Are you sure to delete your request?");
        textView.setTextSize(15);

        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins(30, 10, 30, 0);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 200);
        params2.setMargins(30, 10, 30, 0);

        linearLayout.addView(textView, params1);

        mCancel = (Button) dialog.findViewById(R.id.btn_proceed);
        mCancel.setText("Cancel");
        mOkay = (Button) dialog.findViewById(R.id.btn_cancel);
        mOkay.setText("Delete");

        mOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> params = new HashMap<>();
                params.put("id", id_hps);
                getDialog().dismiss();
                getDetail("https://bb.byonchat.com/ApiDocumentControl/index.php/Request/delete", params, true);
                Toast.makeText(getActivity(), "Request Deleted", Toast.LENGTH_SHORT).show();
                if (listener != null){
                    listener.onReject();
                }
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return dialog;
    }

    private String jsonData(){

        JSONArray datas = new JSONArray();
        JSONObject approver = new JSONObject();
        try {
            /*if(!dbHelper.getColValue(UserDB.ATASAN_1_NIK).equalsIgnoreCase("")){
                approver.put("bc_user_approval",dbHelper.getColValue(UserDB.ATASAN_1_PHONE));
                approver.put("nama",dbHelper.getColValue(UserDB.ATASAN_1_NAMA));
                approver.put("nik",dbHelper.getColValue(UserDB.ATASAN_1_NIK));
                approver.put("order","1");
                datas.put(approver);
            }
            if(!dbHelper.getColValue(UserDB.ATASAN_2_NIK).equalsIgnoreCase("")){
                approver.put("bc_user_approval",dbHelper.getColValue(UserDB.ATASAN_2_PHONE));
                approver.put("nama",dbHelper.getColValue(UserDB.ATASAN_2_NAMA));
                approver.put("nik",dbHelper.getColValue(UserDB.ATASAN_2_NIK));
                approver.put("order","2");
                datas.put(approver);
            }*/
            approver.put("bc_user_approval",databaseHelper.getMyContact().getJabberId());
            approver.put("nama",dbHelper.getColValue(UserDB.EMPLOYEE_NAME));
            approver.put("nik",dbHelper.getColValue(UserDB.EMPLOYEE_NIK));
            approver.put("order","1");
            datas.put(approver);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return datas.toString();
    }

    private void getDetail(String Url, Map<String, String> params2, Boolean hide) {
        ProgressDialog rdialog = new ProgressDialog((FragmentActivity) getActivity());
        rdialog.setMessage("Loading...");
        rdialog.show();

        RequestQueue queue = Volley.newRequestQueue((FragmentActivity) getActivity(), new HurlStack(null, ClientSSLSocketFactory.getSocketFactory()));
        StringRequest sr = new StringRequest(Request.Method.POST, Url,
                response -> {
                    rdialog.dismiss();
                    if (hide) {
                        Log.w("sukses harusee",response);
                        //Toast.makeText((FragmentActivity) getActivity(), "sukses", Toast.LENGTH_SHORT).show();
                        /*ByonchatBaseMallKelapaGadingActivity ss = (ByonchatBaseMallKelapaGadingActivity) (FragmentActivity) getActivity();
                        ss.finish();*/
                    }

                },
                error -> {
                    rdialog.dismiss();
                }
        ) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return params2;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(180000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    public DialogRejectListener getListener() {
        return listener;
    }

    public void setListener(DialogRejectListener listener) {
        this.listener = listener;
    }

    public interface DialogRejectListener{
        void onReject();
    }

    /*private class posTask extends AsyncTask<String, Integer, String> {

        String error = "";
        long totalSize = 0;
        File gpxfile = null;

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            postData(params[0], params[1], params[2], params[3]);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // taskCompleted.onTaskUpdate(0, "");
        }

        protected void onPostExecute(String result) {
           *//* if (error.length() > 0) {
                taskCompleted.onTaskCompleted(20, error);
            }*//*
        }

        protected void onProgressUpdate(Integer... progress) {
//            taskCompleted.onTaskUpdate(progress[0], "Upload Value...");

        }

        public void postData(String valueIWantToSend, final String usr, final String idr, final String idDetail) {
            HttpClient httpclient = HttpHelper.createHttpClient()()();
            HttpPost httppost = new HttpPost(valueIWantToSend);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                Log.w("asslams", (int) ((num / (float) totalSize) * 100) + "");
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });


                ContentType contentType = ContentType.create("multipart/form-data");
                entity.addPart("username_room", new StringBody(usr));
                entity.addPart("id_rooms_tab", new StringBody(idr));
                entity.addPart("id_detail_tab", new StringBody(idDetail));


                MessengerDatabaseHelper messengerHelper = null;
                if (messengerHelper == null) {
                    messengerHelper = MessengerDatabaseHelper.getInstance(getActivity());
                }

                Contact contact = messengerHelper.getMyContact();
                entity.addPart("bc_user", new StringBody(contact.getJabberId()));


                entity.addPart("file_json", new FileBody(gpxfile, contentType, gpxfile.getName()));


                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                Log.w("kask", statusCode + "");
                if (statusCode == 200) {
                    Log.w("berhasil", "hore");
                    if (gpxfile.exists()) {
                        gpxfile.delete();
                    }

                    final String data = EntityUtils.toString(r_entity);
                    Log.w("harr", data);

                    if (data.equalsIgnoreCase("0")) {
                        long date = System.currentTimeMillis();
                        String dateString = hourFormat.format(date);
                        RoomsDetail orderModel = new RoomsDetail(idDetail, idr, usr, dateString, "3", null, "parent");
                        db.updateDetailRoomWithFlagContentParent(orderModel);
                        taskCompleted.onTaskCompleted(20, "gagal upload");
                    } else if (data.equalsIgnoreCase("1")) {
                        db.deleteRoomsDetailbyId(idDetail, idTab, usr);

                        if (calendar != null) {
                            if (calendar.equalsIgnoreCase("true boi")) {
                                MyEventDatabase database = new MyEventDatabase(context);
                                SQLiteDatabase db;
                                db = database.getWritableDatabase();
                                String[] args = {idDetail};
                                db.delete(MyEventDatabase.TABLE_EVENT, MyEventDatabase.EVENT_ID_DETAIL + "=?", args);
                                db.close();
                            }
                        }
                        taskCompleted.onTaskCompleted(0, "success");
                    } else {
                        taskCompleted.onTaskCompleted(50, data);

                    }
                } else {
                    Log.w("gagal", "hore");
                    if (gpxfile.exists()) {
                        gpxfile.delete();
                    }
                    long date = System.currentTimeMillis();
                    String dateString = hourFormat.format(date);

                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, dateString, "3", null, "parent");
                    db.updateDetailRoomWithFlagContentParent(orderModel);

                    error = "Tolong periksa koneksi internet.1";
                    taskCompleted.onTaskCompleted(20, "gagal upload");
                }

            } catch (ClientProtocolException e) {
                Log.w("gagal1", e.toString());
                if (gpxfile.exists()) {
                    gpxfile.delete();
                }
                long date = System.currentTimeMillis();
                String dateString = hourFormat.format(date);

                RoomsDetail orderModel = new RoomsDetail(idDetail, idr, usr, dateString, "3", null, "parent");
                db.updateDetailRoomWithFlagContentParent(orderModel);
                taskCompleted.onTaskCompleted(20, "gagal upload");

            } catch (IOException e) {
                if (gpxfile.exists()) {
                    gpxfile.delete();
                }
                long date = System.currentTimeMillis();
                String dateString = hourFormat.format(date);

                RoomsDetail orderModel = new RoomsDetail(idDetail, idr, usr, dateString, "3", null, "parent");
                db.updateDetailRoomWithFlagContentParent(orderModel);
                taskCompleted.onTaskCompleted(20, "gagal upload");
            }
        }
    }*/


}
