package com.byonchat.android.FragmentSLA;


import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.FragmentDinamicRoom.DinamicSLATaskActivity;
import com.byonchat.android.FragmentSLA.adapter.SLACyclerAdapter;
import com.byonchat.android.FragmentSLA.model.SLAModel;
import com.byonchat.android.R;
import com.byonchat.android.provider.RadioButtonCheckDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.byonchat.android.provider.RadioButtonCheckDB.COLUMN_COMMENT;
import static com.byonchat.android.provider.RadioButtonCheckDB.COLUMN_ID;
import static com.byonchat.android.provider.RadioButtonCheckDB.COLUMN_ID_DETAIL;
import static com.byonchat.android.provider.RadioButtonCheckDB.COLUMN_IMG;
import static com.byonchat.android.provider.RadioButtonCheckDB.COLUMN_OK;
import static com.byonchat.android.provider.RadioButtonCheckDB.TABLE_NAME;

/**
 * A simple {@link Fragment} subclass.
 */
public class ZhFourFragment extends Fragment {

    RecyclerView slaCycler;
    TextView textTitle;
    Button submit;
    ImageButton back;
    String title,content,idDetailForm,passGrade,bobot;
    SLACyclerAdapter adapter;
    Double value;

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
        slaCycler = view.findViewById(R.id.recy_zhsla);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayList<SLAModel> itemList = new ArrayList<>();
        if (content != null){
            try {
                JSONArray data = new JSONArray(content);
                for (int i = 0 ; i<data.length() ; i++){
                    JSONObject childObj = data.getJSONObject(i);
                    String id = childObj.getString("id");
                    String idContent = childObj.getString("id_content");
                    String label = childObj.getString("label");
                    itemList.add(new SLAModel(label,idContent,0,value/data.length(),true));
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        if (submit != null){
            submit.setOnClickListener(v -> {
                boolean letsGo = true;
                String[] ids = null;
                String id1=null,id2=null,id3=null,id4=null;
                for (int i = 0 ; i<itemList.size() ; i++){
                    String idContent = itemList.get(i).getDaleman();
                    if (i == 0){
                        ids = idContent.split("-");
                        id1 = ids[0];
                        id2 = ids[1];
                        id3 = ids[2];
                    }
                    letsGo = checkDB(idDetailForm,idContent);
                }
                if (letsGo){
                    try {
                        JSONArray arrayPertanyaan = new JSONArray();
                        List<Integer> lolos = new ArrayList<>();
                        for (int z = 0 ; z<itemList.size() ; z++){
                            JSONObject objPertanyaan = new JSONObject();
                            String idContent = itemList.get(z).getDaleman();
                            String[] id = idContent.split("-");
                            id4 = id[3];
                            int value = getOkFromDB(idDetailForm,idContent);
                            String img = getImgeB(idDetailForm, idContent);
                            String cmnt = getComment(idDetailForm,idContent);
                            if (value == 0){
                                if (img != null && cmnt != null){
                                    objPertanyaan.put("id",id4);
                                    objPertanyaan.put("v",value);
                                    objPertanyaan.put("f",img);
                                    objPertanyaan.put("n",cmnt);
                                    objPertanyaan.put("b",decimal.format(itemList.get(z).getValue()));
                                    arrayPertanyaan.put(objPertanyaan);
                                } else {
                                    lolos.add(z);
                                }
                            } else {
                                objPertanyaan.put("id",id4);
                                objPertanyaan.put("v",value);
                                objPertanyaan.put("f", img == null ? "" : img);
                                objPertanyaan.put("n", cmnt == null ? "" : cmnt);
                                objPertanyaan.put("b",decimal.format(itemList.get(z).getValue()));
                                arrayPertanyaan.put(objPertanyaan);
                            }
                        }
                        if (!(lolos.size() > 0)){
                            JSONArray arraySubsection = new JSONArray();
                            JSONObject objSubsection = new JSONObject();
                            objSubsection.put("id",id3);
                            objSubsection.put("pertanyaan",arrayPertanyaan);
                            arraySubsection.put(objSubsection);

                            JSONArray arraySection = new JSONArray();
                            JSONObject objSection = new JSONObject();
                            objSection.put("id",id2);
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

                            Log.w("ivana", "JSON : "+arrayParent.toString());
//                            Toast.makeText(getContext(),"Submit success",Toast.LENGTH_SHORT).show();
                           // ((DinamicSLATaskActivity)getActivity()).submitSLA(arrayParent.toString());
                        } else {
                            Toast.makeText(getContext(),"Harap isi note dan gambar jika memilih 'No' .",Toast.LENGTH_SHORT).show();
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

}
