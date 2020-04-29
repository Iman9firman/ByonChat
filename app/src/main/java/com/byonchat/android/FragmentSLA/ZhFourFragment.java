package com.byonchat.android.FragmentSLA;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonToken;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.FragmentDinamicRoom.DinamicSLATaskActivity;
import com.byonchat.android.FragmentSLA.adapter.SLACyclerAdapter;
import com.byonchat.android.FragmentSLA.model.SLAModel;
import com.byonchat.android.R;
import com.byonchat.android.model.SLAmodelNew;
import com.byonchat.android.provider.RadioButtonCheckDB;
import com.byonchat.android.ui.activity.PustSLAFollowUpActivity;
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
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.byonchat.android.provider.RadioButtonCheckDB.COLUMN_COMMENT;
import static com.byonchat.android.provider.RadioButtonCheckDB.COLUMN_ID;
import static com.byonchat.android.provider.RadioButtonCheckDB.COLUMN_ID_DETAIL;
import static com.byonchat.android.provider.RadioButtonCheckDB.COLUMN_IMG;
import static com.byonchat.android.provider.RadioButtonCheckDB.COLUMN_OK;
import static com.byonchat.android.provider.RadioButtonCheckDB.TABLE_NAME;
import static com.byonchat.android.ui.activity.PustSLAFollowUpActivity.resizeAndCompressImageBeforeSend;

/**
 * A simple {@link Fragment} subclass.
 */
public class ZhFourFragment extends Fragment {

    RecyclerView slaCycler;
    TextView textTitle;
    CheckBox numTitle;
    Button submit;
    ImageButton back;
    String title,content,idDetailForm,passGrade,bobot;
    SLACyclerAdapter adapter;
    Double value;

    ArrayList<SLAModel> itemList = new ArrayList<>();
    ArrayList<String> imgList = new ArrayList<>();
    int counter = 0;
    String id1=null,id2=null,id3=null,id4=null;
    ProgressDialog dialog = null;

    DecimalFormat decimal = new DecimalFormat("#.##");

    public ZhFourFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public ZhFourFragment(String title , String content , String idDetailForm , Double value , String passGrade , String bobot){
        this.title = title;
        this.idDetailForm = idDetailForm;
        this.content = content;
        this.value = value;
        this.passGrade = passGrade;
        this.bobot = bobot;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_zh_sla, container, false);
        submit = view.findViewById(R.id.submit_zhsla);
        submit.setVisibility(View.VISIBLE);
        back = view.findViewById(R.id.back_zhsla);
        back.setVisibility(View.VISIBLE);
        textTitle = view.findViewById(R.id.title_zhsla);
        numTitle = view.findViewById(R.id.check_zhsla);
        slaCycler = view.findViewById(R.id.recy_zhsla);
        numTitle.setVisibility(View.VISIBLE);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (content != null){
            try {
                Log.w("getDataSLA",content);
                JSONArray data = new JSONArray(content);
                for (int i = 0 ; i<data.length() ; i++){
                    JSONObject childObj = data.getJSONObject(i);
                    String id = childObj.getString("id");
                    String idContent = childObj.getString("id_content");
                    String label = childObj.getString("label");
                    itemList.add(new SLAModel(label,idContent,"0",value/data.length(),true));
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        if (submit != null){
            submit.setOnClickListener(v -> {
                boolean letsGo = true;
                String[] ids = null;
                for (int i = 0 ; i<itemList.size() ; i++){
                    String idContent = itemList.get(i).getDaleman();
                    if (i == 0){
                        // ini mencatatan id pembobotan - section - subsection
                        ids = idContent.split("-");
                        id1 = ids[0];
                        id2 = ids[1];
                        id3 = ids[2];
                    }
                    // ini check apakah tercatat di database
                    letsGo = checkDB(idDetailForm,idContent);
                }
                if (letsGo){
                    try {
                        for (int z = 0 ; z<itemList.size() ; z++){
                            String idContent = itemList.get(z).getDaleman();
                            String image = getImgeB(idDetailForm,idContent);
                            if (image != "" && image != null){
                                // ini mencatat image yang ada di subsection per item to be check
                                imgList.add(image+";;"+idContent);
                            }
                        }

                        if (imgList.size() > 0){
                            // ini jika terdapat image , maka akan di upload terlebih dahulu
                            String res = chek();
                            if (!res.equalsIgnoreCase("")){
                                dialog = new ProgressDialog(getContext());
                                dialog.setMessage("Uploading Image ...");
                                dialog.show();
                                Log.w("showwdilog",imgList.get(0));
                                if (imgList.get(0).split(";;").length ==2){
                                    File checkFile = new File(imgList.get(0).split(";;")[0]);
                                    if (checkFile.exists()){
                                        new UploadFileToServerCild().execute("https://forward.byonchat.com:37001/1_345171158admin/bc_voucher_client/webservice/proses/file_processing.php",
                                                ((DinamicSLATaskActivity) getActivity()).getUsername(),
                                                "2613",
                                                "66989",
                                                imgList.get(0).split(";;")[0],
                                                imgList.get(0).split(";;")[1]);
                                        return;
                                    }
                                }

                            }

                            Toast.makeText(getContext(),"Harap isi gambar jika memilih 'No' .",Toast.LENGTH_SHORT).show();
                        } else {
                            // ini jika tidak ada image maka akan langsung submit sla nya
                            JSONArray arrayPertanyaan = new JSONArray();
                            List<Integer> lolos = new ArrayList<>();
                            for (int iv = 0 ; iv<itemList.size() ; iv++){
                                JSONObject objPertanyaan = new JSONObject();
                                String idContent = itemList.get(iv).getDaleman();
                                String[] id = idContent.split("-");
                                id4 = id[3];
                                int value = getOkFromDB(idDetailForm,idContent);
                                String img = getImgeB(idDetailForm, idContent);
                                String cmnt = getComment(idDetailForm,idContent);
                                if (value == 0){
                                    // ketika memilih NO
                                    if (img != null && cmnt != null){
                                        // harus terdapat image dan komentar
                                        objPertanyaan.put("id",id1+"-"+id2+"-"+id3+"-"+id4);
                                        objPertanyaan.put("v",value);
                                        objPertanyaan.put("f",itemList.get(iv).getImg());
                                        objPertanyaan.put("n",cmnt);
                                        objPertanyaan.put("b",decimal.format(itemList.get(iv).getValue()));
                                        arrayPertanyaan.put(objPertanyaan);
                                    } else {
                                        // jika tidak ada , maka akan terhitung gagal divalidasi
                                        lolos.add(iv);
                                    }
                                } else {
                                    // ketika memilih YES
                                    objPertanyaan.put("id",id1+"-"+id2+"-"+id3+"-"+id4);
                                    objPertanyaan.put("v",value);
                                    objPertanyaan.put("f", img == null ? "" : itemList.get(iv).getImg());
                                    objPertanyaan.put("n", cmnt == null ? "" : cmnt);
                                    objPertanyaan.put("b",decimal.format(itemList.get(iv).getValue()));
                                    arrayPertanyaan.put(objPertanyaan);
                                }
                            }
                            if (!(lolos.size() > 0)){
                                // ini jika validasi berhasil dan tidak ada yang kurang image serta komentar nya
                                JSONArray arraySubsection = new JSONArray();
                                JSONObject objSubsection = new JSONObject();
                                objSubsection.put("id",id1+"-"+id2+"-"+id3);
                                objSubsection.put("pertanyaan",arrayPertanyaan);
                                arraySubsection.put(objSubsection);

                                JSONArray arraySection = new JSONArray();
                                JSONObject objSection = new JSONObject();
                                objSection.put("id",id1+"-"+id2);
                                objSection.put("subsection",arraySubsection);
                                arraySection.put(objSection);

                                JSONArray arrayPembobotan = new JSONArray();
                                JSONObject objPembobotan = new JSONObject();
                                objPembobotan.put("id",id1);
                                objPembobotan.put("bobot",bobot);
                                objPembobotan.put("section",arraySection);
                                arrayPembobotan.put(objPembobotan);

                                JSONArray arrayParent = new JSONArray();
                                JSONObject objParent = new JSONObject();
                                objParent.put("grade",passGrade);
                                objParent.put("pembobotan",arrayPembobotan);
                                arrayParent.put(objParent);
//                            Toast.makeText(getContext(),"Submit success",Toast.LENGTH_SHORT).show();
                                ((DinamicSLATaskActivity)getActivity()).submitSLA(arrayParent.toString());
                            } else {
                                // ini jika validasi gagal
                                Toast.makeText(getContext(),"Harap isi note dan gambar jika memilih 'No' .",Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(),"Harap isi field dengan lengkap .",Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (back != null){
            back.setOnClickListener(v -> getFragmentManager().popBackStack());
        }
        if (title != null){
            if (textTitle != null){
                textTitle.setText(title);
                textTitle.setSelected(true);
            }
        }
        if (slaCycler != null){
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(slaCycler.getContext(), ((LinearLayoutManager) layoutManager).getOrientation());
            slaCycler.setLayoutManager(layoutManager);
            slaCycler.addItemDecoration(dividerItemDecoration);
            adapter = new SLACyclerAdapter(getActivity(),itemList,idDetailForm);
            slaCycler.setAdapter(adapter);
            slaCycler.getAdapter().notifyDataSetChanged();

        }

        numTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        numTitle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Toast.makeText(getContext(), "All Has Checked", Toast.LENGTH_SHORT).show();
                    for (int o = 0; o < itemList.size(); o++) {
                        if (checkDB(idDetailForm, itemList.get(o).getDaleman())) {
                            updateDB(idDetailForm, itemList.get(o).getDaleman(), 1, null, null);
                        } else {
                            insertDB(idDetailForm, itemList.get(o).getDaleman(), 1, null, null);
                        }
                    }
                }else {
                    removeDB();
                }
                slaCycler.getAdapter().notifyDataSetChanged();
            }
        });

    }

    private boolean checkDB(String idDetail, String id) {
        boolean isExist = false;

        RadioButtonCheckDB database = new RadioButtonCheckDB(getActivity());
        SQLiteDatabase db = database.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME
                + " WHERE id_detail = '" + idDetail + "' AND id_item =?", new String[]{String.valueOf(id)});
        while (cursor.moveToNext()) {
            isExist = true;
        }
        cursor.close();

        return isExist;
    }

    public void insertDB(String idDetail, String id, int value, String image, String comment) {
        RadioButtonCheckDB database = new RadioButtonCheckDB(getActivity());
        SQLiteDatabase db = database.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, id);
        values.put(COLUMN_ID_DETAIL, idDetail);
        values.put(COLUMN_OK, value);
        if (image != null) {
            values.put(COLUMN_IMG, image);
        }
        if (comment != null) {
            values.put(COLUMN_COMMENT, comment);
        }
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void updateDB(String idDetail, String id, int value, String image, String comment) {
        RadioButtonCheckDB database = new RadioButtonCheckDB(getActivity());
        SQLiteDatabase db = database.getWritableDatabase();

        ContentValues values = new ContentValues();
        if (image != null) {
            values.put(COLUMN_IMG, image);
        }

        if (comment != null) {
            values.put(COLUMN_COMMENT, comment);
        }

        if (image == null && comment == null) {
            values.put(COLUMN_OK, value);
        }

        db.update(TABLE_NAME, values, COLUMN_ID_DETAIL + " = '" + idDetail + "' AND " + COLUMN_ID + " =?",
                new String[]{String.valueOf(id)});
    }

    public void removeDB() {
        RadioButtonCheckDB database = new RadioButtonCheckDB(getActivity());
        database.getWritableDatabase().delete(TABLE_NAME, null, null);
    }

    private int getOkFromDB(String idDetail, String id) {
        int ok = 0;
        RadioButtonCheckDB database = new RadioButtonCheckDB(getActivity());
        SQLiteDatabase db = database.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_ID, COLUMN_OK},
                COLUMN_ID_DETAIL + " = '" + idDetail + "' AND " + COLUMN_ID + " =?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        while (cursor.moveToNext()) {
            ok = cursor.getInt(cursor.getColumnIndex(COLUMN_OK));
        }
        cursor.close();

        return ok;
    }

    private String getImgeB(String idDetail, String id) {
        String ok = "";
        RadioButtonCheckDB database = new RadioButtonCheckDB(getActivity());
        SQLiteDatabase db = database.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_ID_DETAIL, COLUMN_IMG},
                COLUMN_ID_DETAIL + " = '" + idDetail + "' AND " + COLUMN_ID + " =?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        while (cursor.moveToNext()) {
            ok = cursor.getString(cursor.getColumnIndex(COLUMN_IMG));
        }
        cursor.close();

        if (ok != null) {
            if (!ok.equalsIgnoreCase("")) {
                if (ok.split("\\|").length > 1) {
                    ok = ok.split("\\|")[0];
                }
            }

        }


        return ok;
    }

    private String getComment(String idDetail, String id) {
        String ok = "";
        RadioButtonCheckDB database = new RadioButtonCheckDB(getActivity());
        SQLiteDatabase db = database.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_ID_DETAIL, COLUMN_COMMENT},
                COLUMN_ID_DETAIL + " = '" + idDetail + "' AND " + COLUMN_ID + " =?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        while (cursor.moveToNext()) {
            ok = cursor.getString(cursor.getColumnIndex(COLUMN_COMMENT));
        }
        cursor.close();

        return ok;
    }

    public SLACyclerAdapter getAdapter() {
        return adapter;
    }

    private class UploadFileToServerCild extends AsyncTask<String, Integer, String> {
        long totalSize = 0;
        String ii;
        String id;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return uploadFile(params[0], params[1], params[2], params[3], params[4], params[5]);
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        @SuppressWarnings("deprecation")
        private String uploadFile(String URL, String username, String id_room, String id_list, String value, String ids) {
            String responseString = null;
            ii = value;
            id = ids;

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

                java.io.File sourceFile = new java.io.File(resizeAndCompressImageBeforeSend(getContext(), ii, "fileUploadBC_" + new Date().getTime() + ".jpg"));

                        Log.w("inidilog","1: "+ii);
                        Log.w("inidilog","2: "+sourceFile);
                        Log.w("inidilog","3: "+sourceFile.getAbsoluteFile());
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

                if (sourceFile.exists()) {
                    sourceFile.delete();
                }

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
                    for (int i = 0 ; i<itemList.size() ; i++){
                        if (itemList.get(i).getDaleman().equalsIgnoreCase(id)){
                            itemList.get(i).setImg(fileNameServer);
                            File file = new File(ii);
                            if (file.exists()) {
//                                file.delete();
                            }
                        }
                    }
                }
                counter++;

                if (counter < imgList.size()){
                    // ini jika masih terdapat image , maka akan mengulang upload
                    Log.w("showwdilog...",imgList.get(0));
                    if (imgList.get(0).split(";;").length ==2) {
                        File checkFile = new File(imgList.get(0).split(";;")[0]);
                        if (checkFile.exists()) {
                            new UploadFileToServerCild().execute("https://forward.byonchat.com:37001/1_345171158admin/bc_voucher_client/webservice/proses/file_processing.php",
                                    ((DinamicSLATaskActivity) getActivity()).getUsername(),
                                    "2613",
                                    "66989",
                                    imgList.get(counter).split(";;")[0],
                                    imgList.get(counter).split(";;")[1]);
                            return;
                        }
                    }
                    Toast.makeText(getContext(),"Harap isi gambar jika memilih 'No' ...",Toast.LENGTH_SHORT).show();

                }else {

                    // jika sudah tidak ada image maka akan submit sla
                    if (dialog!=null){
                        dialog.dismiss();
                    }
                    String res = chek();
                    if (!res.equalsIgnoreCase("")){
                        ((DinamicSLATaskActivity)getActivity()).submitSLA(res);
                    }else {
                        Toast.makeText(getContext(),"...",Toast.LENGTH_SHORT).show();
                    }

                }




            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(),"....",Toast.LENGTH_SHORT).show();

            }
            super.onPostExecute(result);
        }
    }

    private String chek(){
        try {
        JSONArray arrayPertanyaan = new JSONArray();
        List<Integer> lolos = new ArrayList<>();
        for (int iv = 0 ; iv<itemList.size() ; iv++){
            JSONObject objPertanyaan = new JSONObject();
            String idContent = itemList.get(iv).getDaleman();
            String[] id = idContent.split("-");
            id4 = id[3];
            int value = getOkFromDB(idDetailForm,idContent);
            String img = getImgeB(idDetailForm, idContent);
            String cmnt = getComment(idDetailForm,idContent);
            if (value == 0){
                if (img != null && cmnt != null){

                        objPertanyaan.put("id",id1+"-"+id2+"-"+id3+"-"+id4);

                    objPertanyaan.put("v",value);
                    objPertanyaan.put("f",itemList.get(iv).getImg());
                    objPertanyaan.put("n",cmnt);
                    objPertanyaan.put("b",decimal.format(itemList.get(iv).getValue()));
                    arrayPertanyaan.put(objPertanyaan);
                } else {
                    lolos.add(iv);
                }
            } else {
                objPertanyaan.put("id",id1+"-"+id2+"-"+id3+"-"+id4);
                objPertanyaan.put("v",value);
                objPertanyaan.put("f", img == null ? "" : itemList.get(iv).getImg());
                objPertanyaan.put("n", cmnt == null ? "" : cmnt);
                objPertanyaan.put("b",decimal.format(itemList.get(iv).getValue()));
                arrayPertanyaan.put(objPertanyaan);
            }
        }
        if (!(lolos.size() > 0)){
            JSONArray arraySubsection = new JSONArray();
            JSONObject objSubsection = new JSONObject();
            objSubsection.put("id",id1+"-"+id2+"-"+id3);
            objSubsection.put("pertanyaan",arrayPertanyaan);
            arraySubsection.put(objSubsection);

            JSONArray arraySection = new JSONArray();
            JSONObject objSection = new JSONObject();
            objSection.put("id",id1+"-"+id2);
            objSection.put("subsection",arraySubsection);
            arraySection.put(objSection);

            JSONArray arrayPembobotan = new JSONArray();
            JSONObject objPembobotan = new JSONObject();
            objPembobotan.put("id",id1);
            objPembobotan.put("bobot",bobot);
            objPembobotan.put("section",arraySection);
            arrayPembobotan.put(objPembobotan);

            JSONArray arrayParent = new JSONArray();
            JSONObject objParent = new JSONObject();
            objParent.put("grade",passGrade);
            objParent.put("pembobotan",arrayPembobotan);
            arrayParent.put(objParent);
           return arrayParent.toString();
        } else {
            Toast.makeText(getContext(),"Harap isi note dan gambar jika memilih 'No' .",Toast.LENGTH_SHORT).show();
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
}
