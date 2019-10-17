package com.byonchat.android.FragmentSLA;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ZhOneFragment extends Fragment {

    RecyclerView slaCycler;
    TextView textTitle, numTitle;
    Button submit;
    ImageButton back;
    String title,content,idDetailForm,passGrade,bobot;
    SLACyclerAdapter adapter;

    public ZhOneFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public ZhOneFragment(String title , String content , String idDetailForm , String passGrade){
        this.title = title;
        this.idDetailForm = idDetailForm;
        this.content = content;
        this.passGrade = passGrade;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_zh_sla, container, false);
        submit = view.findViewById(R.id.submit_zhsla);
        back = view.findViewById(R.id.back_zhsla);
        back.setVisibility(View.INVISIBLE);
        textTitle = view.findViewById(R.id.title_zhsla);
        numTitle = view.findViewById(R.id.number_zhsla);
        slaCycler = view.findViewById(R.id.recy_zhsla);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayList<SLAModel> itemList = new ArrayList<>();

        if (slaCycler != null){
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(slaCycler.getContext(), ((LinearLayoutManager) layoutManager).getOrientation());
            slaCycler.setLayoutManager(layoutManager);
            slaCycler.addItemDecoration(dividerItemDecoration);
            adapter = new SLACyclerAdapter(getActivity(),itemList,idDetailForm);
            adapter.setClickListener((item, position) -> {
                loadFragmentFromFragment(ZhOneFragment.this,new ZhTwoFragment(item.getTitle(),item.getDaleman(),idDetailForm,item.getValue(),passGrade,bobot,item.getId()),"ZhTwo");

            });
            slaCycler.setAdapter(adapter);
            slaCycler.getAdapter().notifyDataSetChanged();
        }

        if (content != null){
            try {
                // ini mengambil id subsection yang sudah di kerjakan ( sama seperti di fragment 2 dan 3 )
                ArrayList<String> listId = ((DinamicSLATaskActivity)getActivity()).getListSubmittedId();

                JSONObject obj = new JSONObject(content);
                JSONArray data = obj.getJSONArray("data");

                int countAll = 0;

                for (int i = 0 ; i<data.length() ; i++){
                    JSONObject childObj = data.getJSONObject(i);
                    String id = childObj.getString("id");
                    String label = childObj.getString("label");
                    Double bobot = Double.valueOf(childObj.getString("bobot"));
                    this.bobot = bobot.toString();
                    String content = childObj.getJSONArray("data").toString();
                    JSONArray counting = new JSONArray(content);

                    Log.w("invisibbi",content);
                    //disini penghitungan jumlah item to be check yang belum di lakukan
                    int secount = 0;
                    int counter = 0;
                    String sama = "";
                    String sama2 = "";

                    //Section
                    for (int j = 0 ; j<counting.length() ; j++){
                        JSONObject child1 = counting.getJSONObject(j);
                        String id2 = child1.getString("id");
                        JSONArray contentCh1 = child1.getJSONArray("data");
                        secount++;
                        int jumlahSudah = 0;
                        //Subsection
                        for (int k = 0 ; k<contentCh1.length() ; k++){
                            JSONObject child2 = contentCh1.getJSONObject(k);
                            String id3 = child2.getString("id");
                            JSONArray contentCh2 = child2.getJSONArray("data");
                            //Pertanyaan
                            for (int l = 0 ; l<contentCh2.length();l++){
                                String id4 = id+"-"+id2+"-"+id3;
                                counter++;
                                if (listId.size() != 0){
                                    //Yang sudah di check
                                    for (int m = 0 ; m < listId.size() ; m++){
                                        if (listId.get(m).equalsIgnoreCase(id4)){
                                            counter--;
                                            Log.w("invisibbieda",listId.get(m));
                                            if(!sama.equalsIgnoreCase(id4)){
                                                Log.w("invisibbiedd",listId.get(m));
                                                jumlahSudah++;
                                                sama = id4; //Biar gak looping dengan id yang sama
//                                                if(contentCh1.length() == listId.size()){
//                                                    secount--;
//                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if(!sama2.equalsIgnoreCase(id2)){
                            sama2 = id2;
                            Log.w("invisibbier",jumlahSudah+"");
                            Log.w("invisibbiera",contentCh1.length()+"");
                            Log.w("invisibbier","-------------------------------");
                            if(contentCh1.length() == jumlahSudah){
                                secount--;
                            }
                        }
                    }
                    if(counter == 0){
                        secount = 0;
                    }
                    countAll += counter;
                    String countt = secount+"/"+counter;
                    SLAModel model = new SLAModel(label,content,countt,bobot,false);
                    model.setId(id);
                    itemList.add(model);
                }
                if (slaCycler.getAdapter() != null){
                    slaCycler.getAdapter().notifyDataSetChanged();
                }
                numTitle.setText("("+countAll+")");
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        if (submit != null){
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(),"Submitted",Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (title != null){
            if (textTitle != null){
                textTitle.setText(title);
                textTitle.setSelected(true);
            }
        }
    }

    public static void loadFragmentFromFragment(Fragment before,Fragment after,String tag){
        before.getFragmentManager().beginTransaction()
                .replace(((ViewGroup) before.getView().getParent()).getId(), after)
                .addToBackStack(tag)
                .commit();
    }
}
