package com.byonchat.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.DataBaseHelper;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.tempSchedule.MyEventDatabase;
import com.byonchat.android.utils.AndroidMultiPartEntity;

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
import java.util.Iterator;
import java.util.List;

public class DialogFormChildRequestDoc extends DialogFragment {

    String username = "";
    String idTab = "";
    String title = "";
    String description = "";
    LinearLayout linearLayout;
    Button mProceed, mCancel;

    public static DialogFormChildRequestDoc newInstance(String username, String idTab, String title, String description) {
        DialogFormChildRequestDoc f = new DialogFormChildRequestDoc();
        Bundle args = new Bundle();
        args.putString("username", username);
        args.putString("idTab", idTab);
        args.putString("title", title);
        args.putString("description", description);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idTab = getArguments().getString("idTab");
        username = getArguments().getString("username");
        title = getArguments().getString("title");
        description = getArguments().getString("description");

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialog = inflater.inflate(R.layout.dialog_form_child_layout, container, false);

        linearLayout = (LinearLayout) dialog.findViewById(R.id.linear);

        TextView textView = new TextView(getContext());
        textView.setText("You want to download the document");
        textView.setTextSize(15);

        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins(30, 10, 30, 0);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params2.setMargins(30, 10, 30, 40);

        linearLayout.addView(textView, params1);

        mProceed = (Button) dialog.findViewById(R.id.btn_proceed);
        mProceed.setText("Yes");
        mCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        mCancel.setText("Cancel");


        mProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
            HttpClient httpclient = new DefaultHttpClient();
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
