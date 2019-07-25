package com.byonchat.android.FragmentSLA;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static com.byonchat.android.FragmentSLA.ZhOneFragment.loadFragmentFromFragment;

public class ZhThreeFragment extends Fragment {

    RecyclerView slaCycler;
    TextView textTitle;
    Button submit;
    ImageButton back;
    String title,content,idDetailForm,passGrade,bobot,fromId;
    SLACyclerAdapter adapter;
    Double value;

    public ZhThreeFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public ZhThreeFragment(String title , String content , String idDetailForm , Double value , String passGrade , String bobot , String fromId){
        this.title = title;
        this.idDetailForm = idDetailForm;
        this.content = content;
        this.value = value;
        this.passGrade = passGrade;
        this.bobot = bobot;
        this.fromId = fromId;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_zh_sla, container, false);
        submit = view.findViewById(R.id.submit_zhsla);
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
                ArrayList<String> listId = ((DinamicSLATaskActivity)getActivity()).getListSubmittedId();
                JSONArray data = new JSONArray(content);
                for (int i = 0 ; i<data.length() ; i++){
                    JSONObject childObj = data.getJSONObject(i);
                    String id = this.fromId+"-"+childObj.getString("id");
                    String label = childObj.getString("label");
                    String content = childObj.getJSONArray("data").toString();
                    JSONArray counting = new JSONArray(content);
                    int counter = 0;
                    for (int j = 0 ; j<counting.length() ; j++){
                        counter++;
                    }
                    SLAModel model = new SLAModel(label,content,counter,value/data.length(),false);
                    itemList.add(model);
                    for (int k = 0; k<listId.size() ; k++){
                        if (id.equalsIgnoreCase(listId.get(k))){
                            itemList.remove(model);
                            itemList.add(new SLAModel(label,content,0,value/data.length(),false));
                        }
                    }
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        if (submit != null){
            submit.setOnClickListener(v -> Toast.makeText(getActivity(),"Submitted",Toast.LENGTH_SHORT).show());
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
            adapter.setClickListener((item, position) -> {
                if (item.getCount() != 0){
                    loadFragmentFromFragment(ZhThreeFragment.this,new ZhFourFragment(item.getTitle(),item.getDaleman(),idDetailForm,item.getValue(),passGrade,bobot),"ZhFour");
                } else {
                    Toast.makeText(getContext(),"Sudah dikerjakan",Toast.LENGTH_SHORT).show();
                }
            });
            slaCycler.setAdapter(adapter);
            slaCycler.getAdapter().notifyDataSetChanged();

        }
    }

}
