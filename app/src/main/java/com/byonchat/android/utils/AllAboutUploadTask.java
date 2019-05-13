package com.byonchat.android.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.DataBaseHelper;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.tempSchedule.MyEventDatabase;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import static com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity.POSDETAIL;
import static com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity.POST_FOTO;
import static com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity.PULLDETAIL;
import static com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity.PULLMULIPLEDETAIL;
import static com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity.PULLMULIPLEDETAILUPDATE;

public class AllAboutUploadTask {

    private static AllAboutUploadTask instance = new AllAboutUploadTask();
    static Context context;
    private OnTaskCompleted taskCompleted;
    BotListDB db;
    String idDetail;
    String username;
    String idTab;
    String idListTaskMasterForm;
    Integer totalUpload = 0;
    ArrayList<String> prosesUpload = new ArrayList<>();
    String fromList;
    String calendar;
    String isReject;
    Boolean includeStatus;
    String startDate;
    String labelApprove;
    String labelDone;
    String customersId;
    String linkGetAsignTo;
    String patokanUpload;


    public static final SimpleDateFormat hourFormat = new SimpleDateFormat(
            "HH:mm:ss dd/MM/yyyy", Locale.getDefault());


    public AllAboutUploadTask getInstance(Context ctx) {
        context = ctx;

        return instance;
    }

    public void UploadTask(OnTaskCompleted _taskCompleted, String _idDetail, String _username, String _idTab, String patoakan) {
        this.taskCompleted = _taskCompleted;

        if (db == null) {
            db = BotListDB.getInstance(context);
        }

        idDetail = _idDetail;
        username = _username;
        idTab = _idTab;
        patokanUpload = patoakan;
        taskCompleted.onTaskProses("proses");


        Cursor cEdit2 = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "utility", "");

        if (cEdit2.getCount() > 0) {
            try {
                JSONObject jj = new JSONObject(cEdit2.getString(cEdit2.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT)));
                idListTaskMasterForm = jj.getString("idListTaskMasterForm");

                fromList = jj.getString("fromList");

                isReject = jj.getString("isReject");
                if (jj.has("calendar")) {
                    calendar = jj.getString("calendar");
                }

                if (jj.has("startDate")) {
                    startDate = jj.getString("startDate");
                }

                if (jj.has("linkGetAsignTo")) {
                    linkGetAsignTo = jj.getString("linkGetAsignTo");
                }

                if (jj.has("includeStatus")) {

                    String ss = jj.getString("includeStatus");
                    if (ss.equalsIgnoreCase("true")) {
                        includeStatus = true;
                    } else {
                        includeStatus = false;
                    }
                    labelApprove = jj.getString("labelApprove");
                    labelDone = jj.getString("labelDone");
                } else {
                    includeStatus = false;
                }


                customersId = jj.getString("customersId");


                uploadFileChild("pertama");
            } catch (JSONException e) {
                e.printStackTrace();
                taskCompleted.onTaskCompleted(20, "proses");
            }

        } else {
            taskCompleted.onTaskCompleted(20, "proses");
        }

    }


    private void uploadFileChild(String ainnu) {
        ArrayList<RoomsDetail> list = db.allRoomDetailFormWithFlag(idDetail, username, idTab, "cild");
        ArrayList<String> listUploadTotal = new ArrayList<>();
        ArrayList<String> listUpload = new ArrayList<>();

        if (ainnu.equalsIgnoreCase("pertama")) {
            for (int u = 0; u < list.size(); u++) {
                JSONArray jsA = null;
                String content = "";

                String cc = list.get(u).getContent();

                try {
                    if (cc.startsWith("{")) {
                        if (!cc.startsWith("[")) {
                            cc = "[" + cc + "]";
                        }
                        jsA = new JSONArray(cc);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                if (jsA != null) {
                    if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown_form")) {
                        try {
                            JSONObject jsonObject = new JSONObject(list.get(u).getContent());
                            Iterator<String> iter = jsonObject.keys();

                            while (iter.hasNext()) {
                                String key = iter.next();
                                try {
                                    JSONArray jsAdd = jsonObject.getJSONArray(key);
                                    for (int ic = 0; ic < jsAdd.length(); ic++) {
                                        JSONObject oContent = new JSONObject(jsAdd.get(ic).toString());

                                        JSONArray aa = new JSONArray();
                                        if (oContent.has("f")) {
                                            aa = oContent.getJSONArray("f");
                                        }

                                        if (aa.length() > 0) {
                                            for (int a = 0; a < aa.length(); a++) {
                                                if (aa.getJSONObject(a).getString("u").equalsIgnoreCase("")) {
                                                    listUploadTotal.add("1");
                                                }
                                            }
                                        }
                                    }

                                } catch (JSONException e) {
                                    // Something went wrong!
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("form_child")) {
                        try {
                            JSONArray jsonArray = new JSONArray(list.get(u).getContent());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONArray jsnobject = new JSONArray(jsonArray.getJSONObject(i).getString("data"));
                                for (int ii = 0; ii < jsnobject.length(); ii++) {
                                    JSONObject c = jsnobject.getJSONObject(ii);
                                    if (c.getString("type").equalsIgnoreCase("front_camera") || c.getString("type").equalsIgnoreCase("rear_camera")) {
                                        String aa[] = c.getString("value").toString().split(";");
                                        if (aa.length == 2) {
                                            if (aa[0].length() == 0) {
                                                listUploadTotal.add("1");
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }

            }

        }

        totalUpload = totalUpload + listUploadTotal.size();

        for (int u = 0; u < list.size(); u++) {
            JSONArray jsA = null;
            String content = "";

            String cc = list.get(u).getContent();

            try {
                if (cc.startsWith("{")) {
                    if (!cc.startsWith("[")) {
                        cc = "[" + cc + "]";
                    }
                    jsA = new JSONArray(cc);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            if (jsA != null) {
                if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown_form")) {
                    try {
                        JSONObject jsonObject = new JSONObject(list.get(u).getContent());
                        Iterator<String> iter = jsonObject.keys();

                        while (iter.hasNext()) {
                            String key = iter.next();
                            try {
                                JSONArray jsAdd = jsonObject.getJSONArray(key);
                                for (int ic = 0; ic < jsAdd.length(); ic++) {
                                    JSONObject oContent = new JSONObject(jsAdd.get(ic).toString());

                                    JSONArray aa = new JSONArray();
                                    if (oContent.has("f")) {
                                        aa = oContent.getJSONArray("f");
                                    }

                                    if (aa.length() > 0) {
                                        for (int a = 0; a < aa.length(); a++) {
                                            if (aa.getJSONObject(a).getString("u").equalsIgnoreCase("")) {
                                                listUpload.add("1");
                                                new UploadFileToServerCild().execute(
                                                        new ValidationsKey().getInstance(context).getTargetUrl(username) + POST_FOTO,
                                                        username,
                                                        idTab,
                                                        idListTaskMasterForm,
                                                        list.get(u).getContent(),
                                                        aa.getJSONObject(a).getString("r"),
                                                        jsAdd.get(ic).toString(),
                                                        a + "",
                                                        key,
                                                        list.get(u).getFlag_content(),
                                                        list.get(u).getFlag_tab(),
                                                        ic + "");
                                                return;
                                            }
                                        }
                                    }
                                }

                            } catch (JSONException e) {
                                // Something went wrong!
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {

                if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("form_child")) {
                    try {
                        JSONArray jsonArray = new JSONArray(list.get(u).getContent());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONArray jsnobject = new JSONArray(jsonArray.getJSONObject(i).getString("data"));
                            for (int ii = 0; ii < jsnobject.length(); ii++) {
                                JSONObject c = jsnobject.getJSONObject(ii);
                                if (c.getString("type").equalsIgnoreCase("front_camera") || c.getString("type").equalsIgnoreCase("rear_camera")) {
                                    String aa[] = c.getString("value").toString().split(";");
                                    if (aa.length == 2) {
                                        if (aa[0].length() == 0) {
                                            listUpload.add("1");
                                            String idLisTask = "";
                                            JSONObject cs = null;
                                            try {
                                                cs = new JSONObject(list.get(u).getFlag_content());
                                                idLisTask = cs.getString("a");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            new UploadFileToServer().execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + POST_FOTO, username, idTab, idLisTask, cc, c.getString("value").toString(), list.get(u).getId(), list.get(u).getParent_tab(), list.get(u).getParent_room(), aa[1], list.get(u).getFlag_content(), list.get(u).getFlag_tab(), list.get(u).getFlag_room());
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

               /*  nanti lagi
                else if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("rear_camera") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("rear_camera")) {
                    if (!Message.isJSONValid(list.get(u).getContent())) {
                        listUpload.add("1");
                        String idLisTask = "";
                        JSONObject c = null;
                        try {
                            c = new JSONObject(list.get(u).getFlag_content());
                            idLisTask = c.getString("a");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        new UploadFileToServerB().execute(POST_FOTO, username, idTab, idLisTask, list.get(u).getId(), list.get(u).getParent_tab(), list.get(u).getParent_room(), list.get(u).getContent(), list.get(u).getFlag_content(), list.get(u).getFlag_tab(), list.get(u).getFlag_room());
                        return;
                    }
                }*/

            }

        }

        if (listUpload.size() == 0) {
            Log.w("abdulMasalah", "1");
            if (fromList.equalsIgnoreCase("show")) {
                Log.w("abdulMasalah1", POSDETAIL);
                new posTask().execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + POSDETAIL, username, idTab, idDetail);
            } else if (fromList.equalsIgnoreCase("hide")) {
                Log.w("abdulMasalah2", PULLDETAIL);
                new posTask().execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + PULLDETAIL, username, idTab, idDetail);
            } else {
                Log.w("abdulMasalah", "2");
                if (idDetail != null || !idDetail.equalsIgnoreCase("")) {
                    Log.w("abdulMasalah", "3");
                    String[] ff = idDetail.split("\\|");
                    Log.w("abdulMasalah", "4");
                    if (ff.length == 2) {
                        Log.w("abdulMasalah", "5");
                        if (patokanUpload.length() > 0) {
                            Log.w("abdulMasalah6", PULLMULIPLEDETAILUPDATE);
                            new posTask().execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + PULLMULIPLEDETAILUPDATE, username, idTab, idDetail);
                        } else {
                            Log.w("abdulMasalah7", PULLMULIPLEDETAIL);
                            new posTask().execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + PULLMULIPLEDETAIL, username, idTab, idDetail);
                        }

                    } else {
                        Log.w("abdulMasalah8", "1");
                        new posTask().execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + POSDETAIL, username, idTab, idDetail);
                    }
                }
            }
        } else {
            uploadFileChild("looping");
        }
    }


    private class posTask extends AsyncTask<String, Integer, String> {

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
            taskCompleted.onTaskUpdate(0, "");
        }

        protected void onPostExecute(String result) {
            if (error.length() > 0) {
                taskCompleted.onTaskCompleted(20, error);
            }
        }

        protected void onProgressUpdate(Integer... progress) {
            taskCompleted.onTaskUpdate(progress[0], "Upload Value...");

        }

        public void postData(String valueIWantToSend, final String usr, final String idr, final String idDetail) {
            // Create a new HttpClient and Post Header
            prosesUpload.add("ada");
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(valueIWantToSend);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });


                ContentType contentType = ContentType.create("multipart/form-data");
                entity.addPart("username_room", new StringBody(usr));
                entity.addPart("id_rooms_tab", new StringBody(idr));
                entity.addPart("id_detail_tab", new StringBody(idDetail));
                Log.w("Entiti sajaha ke 1","usr : " +new StringBody(usr)+", idr : "+new StringBody(idr)+", idDetail : "+new StringBody(idDetail));

                Log.w("Kena lewat sajaha","Setuju 0");

                if (calendar != null) {
                    if (calendar.equalsIgnoreCase("true boi")) {

                        Log.w("Kena lewat sajaha","Setuju 1");
                        entity.addPart("selected_date", new StringBody(startDate));
                        Log.w("Entiti sajaha ke 2", ""+new StringBody(startDate));
                    }
                }

                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "assignTo", "");
                if (cEdit.getCount() > 0) {
                    String cc = cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));
                    String has = "";
                    if (cc.contains("All")) {
                        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(context);
                        Cursor curr = dataBaseHelper.selectAll("room", username, idTab);
                        if (curr.getCount() > 0) {
                            if (curr.moveToFirst()) {
                                do {
                                    if (has.length() == 0) {
                                        has = curr.getString(5);
                                    } else {
                                        has += "," + curr.getString(5);
                                    }

                                } while (curr.moveToNext());
                            }
                        }
                    } else {
                        String[] su = cc.split(",");
                        for (String ss : su) {
                            if (has.length() == 0) {
                                has = ss.split(" - ")[1];
                            } else {
                                has += "," + ss.split(" - ")[1];
                            }
                        }
                    }


                    entity.addPart("assign_to", new StringBody(has));
                    Log.w("Entiti sajaha ke 3", ""+new StringBody(has));

                }


                if (includeStatus) {
                    Cursor cucuTv = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "includeStatus", "");
                    if (cucuTv.getCount() > 0) {
                        String cucuTvi = cucuTv.getString(cucuTv.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));
                        String resultti = "0";
                        if (cucuTvi.equalsIgnoreCase(labelApprove)) {
                            resultti = "1";
                        } else if (cucuTvi.equalsIgnoreCase(labelDone)) {
                            resultti = "2";
                        }
                        entity.addPart("status_task", new StringBody(resultti));
                        Log.w("Entiti sajaha ke 4", ""+new StringBody(resultti));
                    }
                }


                if (!isReject.equalsIgnoreCase("")) {
                    entity.addPart("is_reject", new StringBody(isReject));
                    Log.w("Entiti sajaha ke 5", ""+new StringBody(isReject));
                }


                Cursor cursorParent = db.getSingleRoomDetailFormWithFlag(idDetail, usr, idr, "parent");


                if (cursorParent.getCount() > 0) {
                    if (!cursorParent.getString(cursorParent.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)).equalsIgnoreCase("")) {
                        entity.addPart("latlong_before", new StringBody(jsonResultType(cursorParent.getString(cursorParent.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "a")));
                        entity.addPart("latlong_after", new StringBody(jsonResultType(cursorParent.getString(cursorParent.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "b")));
                        Log.w("Entiti sajaha ke 6 a", "a");
                    } else {
                        entity.addPart("latlong_before", new StringBody("null"));
                        entity.addPart("latlong_after", new StringBody("null"));
                        Log.w("Entiti sajaha ke 6 b", "b");
                    }
                } else {
                    entity.addPart("latlong_before", new StringBody("null"));
                    entity.addPart("latlong_after", new StringBody("null"));
                    Log.w("Entiti sajaha ke 6 c", "c");
                }

                if (fromList.equalsIgnoreCase("hide") || fromList.equalsIgnoreCase("hideMultiple") || fromList.equalsIgnoreCase("showMultiple")) {

                    if (idDetail != null || !idDetail.equalsIgnoreCase("")) {
                        String[] ff = idDetail.split("\\|");
                        if (ff.length == 2) {
                            entity.addPart("parent_id", new StringBody(ff[1]));
                            entity.addPart("id_list_push", new StringBody(ff[0]));
                            Log.w("Entiti sajaha ke 7", ""+new StringBody(idDetail+""));
                        }
                    }
                }

                MessengerDatabaseHelper messengerHelper = null;
                if (messengerHelper == null) {
                    messengerHelper = MessengerDatabaseHelper.getInstance(context);
                }

                Contact contact = messengerHelper.getMyContact();
                entity.addPart("bc_user", new StringBody(contact.getJabberId()));
                Log.w("Entiti sajaha ke 8", ""+new StringBody(contact.getJabberId()));

                ArrayList<RoomsDetail> list = db.allRoomDetailFormWithFlag(idDetail, usr, idr, "cild");

                JSONArray jsonArrayKey = new JSONArray();
                JSONArray jsonArrayValue = new JSONArray();
                JSONArray jsonArrayType = new JSONArray();
                JSONArray jsonArrayDate = new JSONArray();

                for (int u = 0; u < list.size(); u++) {

                    JSONArray jsA = null;
                    String content = "";

                    String cc = list.get(u).getContent();

                    if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("input_kodepos") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown_wilayah")) {
                        cc = jsoncreateC(list.get(u).getContent());
                    } else if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("form_child")) {

                    } else if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown_form")) {
                        try {
                            JSONObject jsonObject = new JSONObject(cc);
                            Iterator<String> iter = jsonObject.keys();

                            JSONObject jsHead = new JSONObject();
                            JSONArray jsAU = new JSONArray();
                            while (iter.hasNext()) {
                                JSONObject joN = new JSONObject();
                                String key = iter.next();
                                try {
                                    JSONArray jsAdd = jsonObject.getJSONArray(key);
                                    JSONArray newJS = new JSONArray();
                                    String lastCusID = "";
                                    for (int ic = 0; ic < jsAdd.length(); ic++) {
                                        JSONObject oContent = new JSONObject(jsAdd.get(ic).toString());
                                        lastCusID = oContent.getString("iD");
                                        String val = oContent.getString("v");
                                        String not = oContent.getString("n");
                                        JSONArray aa = new JSONArray();
                                        if (oContent.has("f")) {
                                            aa = oContent.getJSONArray("f");
                                        }


                                        JSONObject jOdetail = new JSONObject();
                                        jOdetail.put("id", "49");
                                        jOdetail.put("val", val);
                                        jOdetail.put("note", not);
                                        jOdetail.put("foto", "IMG_28032019_100918_SCzuLKreNS.jpg");


                                        newJS.put(jOdetail);

                                    }
                                    JSONArray josArr = new JSONArray();
                                    JSONObject joNC = new JSONObject();
                                    joNC.put("id", "37");
                                    joNC.put("check", newJS);

                                    josArr.put(joNC);
                                    joN.put("id", "16");
                                    joN.put("subsec", josArr);
                                    jsAU.put(joN);

                                } catch (JSONException e) {
                                    // Something went wrong!
                                }
                            }

                            jsHead.put("outlet_id", customersId);
                            jsHead.put("sec", jsAU);


                            //{"outlet_id":"14","audit":[{"id":"49","checklists":[{"id":"16|37|49","val":"1","note":"","foto":[]}]}]}

                            cc = jsHead.toString();
                            Log.w("beruntung", cc);

                            /*cc = "{\n" +
                                    "  \"type\": \"dropdown_form\",\n" +
                                    "  \"value\": [\n" +
                                    "    {\n" +
                                    "      \"outlet_id\": \"14\",\n" +
                                    "      \"sec\": [\n" +
                                    "        {\n" +
                                    "          \"id\":\"16\",\n" +
                                    "          \"subsec\":[{\n" +
                                    "              \"id\": \"37\",\n" +
                                    "            \"check\": [{\n" +
                                    "              \"id\": \"49\",\n" +
                                    "              \"val\": \"\",\n" +
                                    "              \"note\": \"\",\n" +
                                    "              \"foto\": []\n" +
                                    "            }]\n" +
                                    "            }\n" +
                                    "          ]\n" +
                                    "        }\n" +
                                    "      ]\n" +
                                    "    }\n" +
                                    "  ]\n" +
                                    "}";*/


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        if (cc.startsWith("{")) {
                            if (!cc.startsWith("[")) {
                                cc = "[" + cc + "]";
                            }
                            jsA = new JSONArray(cc);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    if (jsA != null) {
                        if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("distance_estimation") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown_dinamis") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("new_dropdown_dinamis") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("ocr") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("upload_document")) {

                            jsonArrayKey.put(list.get(u).getFlag_tab());
                            jsonArrayValue.put(list.get(u).getContent());
                            jsonArrayDate.put("");
                            jsonArrayType.put(jsonResultType(list.get(u).getFlag_content(), "b"));

                        } else if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown_form")) {

                            jsonArrayKey.put(list.get(u).getFlag_tab());
                            jsonArrayValue.put(cc);
                            jsonArrayDate.put("");
                            jsonArrayType.put(jsonResultType(list.get(u).getFlag_content(), "b"));

                        } else {
                            if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("rear_camera") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("front_camera")) {

                                if (Message.isJSONValid(list.get(u).getContent())) {
                                    JSONObject jObject = null;
                                    try {
                                        jObject = new JSONObject(list.get(u).getContent());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    if (jObject != null) {
                                        try {
                                            String a = jObject.getString("a");
                                            String b = jObject.getString("b");

                                            jsonArrayKey.put(list.get(u).getFlag_tab());
                                            jsonArrayValue.put(a);
                                            jsonArrayDate.put(b);
                                            jsonArrayType.put(jsonResultType(list.get(u).getFlag_content(), "b"));

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    jsonArrayKey.put(list.get(u).getFlag_tab());
                                    jsonArrayValue.put(list.get(u).getContent());
                                    jsonArrayDate.put("");
                                    jsonArrayType.put(jsonResultType(list.get(u).getFlag_content(), "b"));

                                }


                            } else {
                                try {
                                    for (int ic = 0; ic < jsA.length(); ic++) {
                                        final String icC = jsA.getJSONObject(ic).getString("c").toString();
                                        content += icC + "|";
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                jsonArrayKey.put(list.get(u).getFlag_tab());
                                jsonArrayValue.put(content.substring(0, content.length() - 1));
                                jsonArrayDate.put("");
                                jsonArrayType.put(jsonResultType(list.get(u).getFlag_content(), "b"));

                            }


                        }
                    } else {
                        jsonArrayKey.put(list.get(u).getFlag_tab());
                        jsonArrayValue.put(list.get(u).getContent());
                        jsonArrayDate.put("");
                        jsonArrayType.put(jsonResultType(list.get(u).getFlag_content(), "b"));
                    }
                }


                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("key", jsonArrayKey);
                    jsonObject.put("value", jsonArrayValue);
                    jsonObject.put("type", jsonArrayType);
                    jsonObject.put("date", jsonArrayDate);

                    try {
                        File root = new File(Environment.getExternalStorageDirectory(), "ByonChat_Upload");
                        if (!root.exists()) {
                            root.mkdirs();
                        }
                        gpxfile = new File(root, usr + idr + idDetail + ".json");
                        FileWriter writer = new FileWriter(gpxfile);
                        writer.append(jsonObject.toString());
                        writer.flush();
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                entity.addPart("file_json", new FileBody(gpxfile, contentType, gpxfile.getName()));


                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    if (gpxfile.exists()) {
                     //   Toast.makeText(context,"Boleh kartun 0",Toast.LENGTH_SHORT).show();
                        gpxfile.delete();
                    }

                    final String data = EntityUtils.toString(r_entity);

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
                    if (gpxfile.exists()) {
                     //   Toast.makeText(context,"Boleh kartun 1",Toast.LENGTH_SHORT).show();
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
                if (gpxfile.exists()) {
                 //   Toast.makeText(context,"Boleh kartun 2",Toast.LENGTH_SHORT).show();
                   gpxfile.delete();
                }
                long date = System.currentTimeMillis();
                String dateString = hourFormat.format(date);

                RoomsDetail orderModel = new RoomsDetail(idDetail, idr, usr, dateString, "3", null, "parent");
                db.updateDetailRoomWithFlagContentParent(orderModel);
                taskCompleted.onTaskCompleted(20, "gagal upload");

            } catch (IOException e) {
                if (gpxfile.exists()) {
                   // Toast.makeText(context,"Boleh kartun 3",Toast.LENGTH_SHORT).show();
                   gpxfile.delete();
                }
                long date = System.currentTimeMillis();
                String dateString = hourFormat.format(date);

                RoomsDetail orderModel = new RoomsDetail(idDetail, idr, usr, dateString, "3", null, "parent");
                db.updateDetailRoomWithFlagContentParent(orderModel);
                taskCompleted.onTaskCompleted(20, "gagal upload");
            }
        }
    }


    private class UploadFileToServer extends AsyncTask<String, Integer, String> {
        long totalSize = 0;
        String valueSS, valueS, getId, getParent_tab, getParent_room, uri, getFlag_content, getFlag_tab, getFlag_room;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            taskCompleted.onTaskUpdate(0, "");
        }

        @Override
        protected String doInBackground(String... params) {
            return uploadFile(params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[8], params[9], params[10], params[11], params[12]);
        }

        protected void onProgressUpdate(Integer... progress) {
            taskCompleted.onTaskUpdate(progress[0], "Upload Image " + prosesUpload.size() + "/" + totalUpload);
        }

        @SuppressWarnings("deprecation")
        private String uploadFile(String URL, String username, String id_room, String id_list, String valueSS_, String valueS_, String getId_, String getParent_tab_, String getParent_room_, String _uri, String getFlag_content_, String getFlag_tab_, String getFlag_room_) {
            prosesUpload.add("ada");
            String responseString = null;
            valueSS = valueSS_;
            valueS = valueS_;
            getId = getId_;
            getParent_tab = getParent_tab_;
            getParent_room = getParent_room_;
            uri = _uri;
            getFlag_content = getFlag_content_;
            getFlag_tab = getFlag_tab_;
            getFlag_room = getFlag_room_;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(URL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));

                            }
                        });

                File sourceFile = new File(resizeAndCompressImageBeforeSend(context, uri, "fileUploadBC_" + new Date().getTime() + ".jpg"));
                if (!sourceFile.exists()) {
                    return "File not exists";
                }

                ContentType contentType = ContentType.create("image/jpeg");
                entity.addPart("username_room", new StringBody(username));
                entity.addPart("id_rooms_tab", new StringBody(id_room));
                entity.addPart("id_list_task", new StringBody(id_list));
                entity.addPart("value", new FileBody(sourceFile, contentType, sourceFile.getName()));


                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode == 200) {
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String message = jsonObject.getString("message");
                if (message.length() == 0) {
                    String fileNameServer = jsonObject.getString("filename");
                    String aadc = valueSS.replace("\"value\":\"" + (valueS.replace("/", "\\/")) + "\"", "\"value\":" + "\"" + fileNameServer + ";" + (uri.replace("/", "\\/")) + "\"");
                    RoomsDetail orderModel = new RoomsDetail(getId, getParent_tab, getParent_room, aadc, getFlag_content, getFlag_tab, getFlag_room);
                    db.updateDetailRoomWithFlagContent(orderModel);
                    uploadFileChild("looping");
                } else {
                    taskCompleted.onTaskCompleted(20, message);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }
    }


    private class UploadFileToServerCild extends AsyncTask<String, Integer, String> {
        long totalSize = 0;
        String ii;
        String ff;
        String jsonDetail;
        String jsonMaster;
        String pos;
        String posS;
        String key;
        String getFlag_content;
        String getFlag_tab;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            taskCompleted.onTaskUpdate(0, "");
        }

        @Override
        protected String doInBackground(String... params) {
            return uploadFile(params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[8], params[9], params[10], params[11]);
        }

        protected void onProgressUpdate(Integer... progress) {
            taskCompleted.onTaskUpdate(progress[0], "Upload Image " + prosesUpload.size() + "/" + totalUpload);
        }

        @SuppressWarnings("deprecation")
        private String uploadFile(String URL, String username, String id_room, String id_list, String to, String i, String c, String poss, String ky, String gfc, String gft, String ps) {
            prosesUpload.add("ada");
            String responseString = null;
            ii = "/storage/emulated/0/Pictures/com.byonchat.android" + i;
            ff = i;
            pos = poss;
            posS = ps;
            key = ky;
            getFlag_content = gfc;
            getFlag_tab = gft;
            key = ky;

            jsonDetail = c;
            jsonMaster = to;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(URL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(resizeAndCompressImageBeforeSend(context, ii, "fileUploadBC_" + new Date().getTime() + ".jpg"));

                if (!sourceFile.exists()) {
                    return "File not exists";
                }

                ContentType contentType = ContentType.create("image/jpeg");
                entity.addPart("username_room", new StringBody(username));
                entity.addPart("id_rooms_tab", new StringBody(id_room));
                entity.addPart("id_list_task", new StringBody(id_list));
                entity.addPart("value", new FileBody(sourceFile, contentType, sourceFile.getName()));


                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    String _response = EntityUtils.toString(r_entity); // content will be consume only once
                    return _response;
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                JSONObject jsonObject = new JSONObject(result);
                String message = jsonObject.getString("message");
                if (message.length() == 0) {
                    String fileNameServer = jsonObject.getString("filename");

                    JSONObject jsos = new JSONObject(jsonMaster);
                    JSONArray kk = jsos.getJSONArray(key);
                    JSONObject sasa = kk.getJSONObject(Integer.valueOf(posS));
                    JSONArray sas = sasa.getJSONArray("f");
                    JSONObject last = sas.getJSONObject(Integer.valueOf(pos));
                    last.put("u", fileNameServer);

                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, jsos.toString(), getFlag_content, getFlag_tab, "cild");
                    db.updateDetailRoomWithFlagContent(orderModel);
                    uploadFileChild("looping");
                } else {
                    taskCompleted.onTaskCompleted(20, message);
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
            super.onPostExecute(result);
        }
    }

    public static String resizeAndCompressImageBeforeSend(Context context, String filePath, String fileName) {
        final int MAX_IMAGE_SIZE = 100 * 1024; // max final file size in kilobytes
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, 400, 400);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap bmpPic = BitmapFactory.decodeFile(filePath, options);


        int compressQuality = 100; // quality decreasing by 5 every loop.
        int streamLength;
        do {
            ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream);
            byte[] bmpPicByteArray = bmpStream.toByteArray();
            streamLength = bmpPicByteArray.length;
            compressQuality -= 5;
        } while (streamLength >= MAX_IMAGE_SIZE);

        try {
            //save the resized and compressed file to disk cache
            FileOutputStream bmpFile = new FileOutputStream(context.getCacheDir() + fileName);
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpFile);
            bmpFile.flush();
            bmpFile.close();
        } catch (Exception e) {
        }
        //return the path of resized and compressed file
        return context.getCacheDir() + fileName;
    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        String debugTag = "MemoryInformation";
        // Image nin islenmeden onceki genislik ve yuksekligi
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


    private String jsoncreateC(String content) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("c", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }


    private String jsonResultType(String json, String type) {
        String hasil = "";
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jObject != null) {
            try {
                hasil = jObject.getString(type);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return hasil;
    }

    public interface OnTaskCompleted {
        void onTaskProses(String response);

        void onTaskUpdate(int response, String message);

        void onTaskCompleted(int status, String response);
    }


}



/*

  if (progressDialog.isIndeterminate()) {
          progressDialog.setIndeterminate(false);
          }
          progressDialog.setProgress(progress[0]);
          progressDialog.setMessage("Upload Image " + prosesUpload.size() + "/" + totalUpload);*/


    /*if (progressDialog.isIndeterminate()) {
            progressDialog.setIndeterminate(false);
            }
            progressDialog.setProgress(0);*/