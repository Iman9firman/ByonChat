package com.byonchat.android;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.byonchat.android.ISSActivity.LoginDB.UserDB;
import com.byonchat.android.provider.MessengerDatabaseHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DialogFormChildRequestDoc extends DialogFragment {

    String username = "";
    String idTab = "";
    String title = "";
    String url = "";
    long id = 0;
    String description = "";
    LinearLayout linearLayout;
    Button mCancel, mOkay;
    MessengerDatabaseHelper databaseHelper;
    UserDB dbHelper;

    public static DialogFormChildRequestDoc newInstance(String username, String idTab, long idFile, String title, String description, String url) {
        DialogFormChildRequestDoc f = new DialogFormChildRequestDoc();
        Bundle args = new Bundle();
        args.putString("username", username);
        args.putString("idTab", idTab);
        args.putLong("id", idFile);
        args.putString("title", title);
        args.putString("description", description);
        args.putString("url", url);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idTab = getArguments().getString("idTab");
        username = getArguments().getString("username");
        id = getArguments().getLong("id");
        title = getArguments().getString("title");
        description = getArguments().getString("description");
        url = getArguments().getString("url");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialog = inflater.inflate(R.layout.dialog_form_child_layout, container, false);

        dbHelper = new UserDB(getContext());
        databaseHelper = MessengerDatabaseHelper.getInstance((FragmentActivity) getContext());

        linearLayout = (LinearLayout) dialog.findViewById(R.id.linear);
        linearLayout.setPadding(16, 16, 16, 16);

        RelativeLayout titleLayout = new RelativeLayout(getContext());
        titleLayout.setGravity(Gravity.CENTER);
        TextView titleView = new TextView(getContext());
        titleView.setText(title);
        titleView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        titleView.setSingleLine(true);
        titleView.setMarqueeRepeatLimit(-1);
        titleView.setSelected(true);
        titleView.setTextSize(20);
        titleView.canScrollHorizontally(View.SCROLL_AXIS_HORIZONTAL);
        titleView.setTypeface(Typeface.DEFAULT_BOLD);
        LinearLayout.LayoutParams titlePars = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams viewpas = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
        viewpas.setMargins(30, 15, 30, 15);
        titleLayout.addView(titleView, titlePars);
        View view = new View(getContext());
        view.setBackgroundColor(getActivity().getResources().getColor(R.color.black_alpha_50));

        TextView textView = new TextView(getContext());
        textView.setText("Request to download the document?");
        textView.setTextSize(15);

        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins(30, 10, 30, 0);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 200);
        params2.setMargins(30, 10, 30, 0);

        EditText editText = new EditText(getContext());
        editText.setHint("Information for approval *");
        editText.setTextSize(15);
        editText.setLines(8);
        editText.setPadding(10, 10, 10, 10);
        editText.setMaxLines(10);
        editText.setGravity(Gravity.TOP);
//        editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editText.setBackground(getActivity().getResources().getDrawable(R.drawable.rounder_editext));

        linearLayout.addView(titleLayout, params1);
        linearLayout.addView(view, viewpas);
        linearLayout.addView(textView, params1);
        linearLayout.addView(editText, params2);

        mCancel = (Button) dialog.findViewById(R.id.btn_proceed);
        mCancel.setText("Cancel");
        mOkay = (Button) dialog.findViewById(R.id.btn_cancel);
        mOkay.setText("Request");

        mOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> params = new HashMap<>();
                params.put("bc_user_requester", databaseHelper.getMyContact().getJabberId());
                params.put("id_file", id + "");
                params.put("link_file", url);
                params.put("keterangan", editText.getText() + "");
                params.put("nama_user_requester", dbHelper.getColValue(UserDB.EMPLOYEE_NAME));
                params.put("nik_requester", dbHelper.getColValue(UserDB.EMPLOYEE_NIK));
                params.put("nama_file", title);
                params.put("datas", jsonData());

                Log.w("ating yinguut", jsonData());
                if (editText.getText().toString().equalsIgnoreCase("")) {
                    editText.setError("Must be filled");
                } else {
                    getDialog().dismiss();
                    getDetail("https://bb.byonchat.com/ApiDocumentControl/index.php/Request", params, true);
                    Toast.makeText(getActivity(), "Mohon tunggu untuk approvement", Toast.LENGTH_SHORT).show();
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

    private String jsonData() {

        JSONArray datas = new JSONArray();
        try {
            if (!dbHelper.getColValue(UserDB.ATASAN_1_NIK).equalsIgnoreCase("")) {
                JSONObject approver = new JSONObject();
                approver.put("bc_user_approval", validasiNomer(dbHelper.getColValue(UserDB.ATASAN_1_PHONE)));
                approver.put("nama", dbHelper.getColValue(UserDB.ATASAN_1_NAMA));
                approver.put("nik", dbHelper.getColValue(UserDB.ATASAN_1_NIK));
                approver.put("order", "1");
                datas.put(approver);
            }
            if (!dbHelper.getColValue(UserDB.ATASAN_2_NIK).equalsIgnoreCase("")) {
                JSONObject approver2 = new JSONObject();
                approver2.put("bc_user_approval", validasiNomer(dbHelper.getColValue(UserDB.ATASAN_2_PHONE)));
                approver2.put("nama", dbHelper.getColValue(UserDB.ATASAN_2_NAMA));
                approver2.put("nik", dbHelper.getColValue(UserDB.ATASAN_2_NIK));
                approver2.put("order", "2");
                datas.put(approver2);
            }

                /*JSONObject approver = new JSONObject();
                approver.put("bc_user_approval","6287771783888");
                approver.put("nama","AGUSTINUS IRWANTO");
                approver.put("nik","00001");
                approver.put("order","1");
                datas.put(approver);

                JSONObject approver2 = new JSONObject();
                approver2.put("bc_user_approval","6281588888892");
                approver2.put("nama","RUSBANDI");
                approver2.put("nik","00002");
                approver2.put("order","2");
                datas.put(approver2)*/

        } catch (Exception e) {
            e.printStackTrace();
        }
        return datas.toString();
    }

    private void getDetail(String Url, Map<String, String> params2, Boolean hide) {
        ProgressDialog rdialog = new ProgressDialog((FragmentActivity) getActivity());
        rdialog.setMessage("Loading...");
        rdialog.show();

        RequestQueue queue = Volley.newRequestQueue((FragmentActivity) getActivity());

        StringRequest sr = new StringRequest(Request.Method.POST, Url,
                response -> {
                    rdialog.dismiss();
                    if (hide) {
                        Log.w("sukses harusee", response);
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
        queue.add(sr);
    }

    private String validasiNomer(String nomer) {
        if (nomer.startsWith("0")) {
            nomer = "62" + nomer.substring(1, nomer.length());
        } else if (nomer.startsWith("+")) {
            nomer = nomer.replace("+", "");
        }
        return nomer.trim();
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
