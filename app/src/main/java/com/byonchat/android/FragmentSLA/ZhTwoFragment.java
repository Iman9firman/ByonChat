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

import java.util.ArrayList;

import static com.byonchat.android.FragmentSLA.ZhOneFragment.loadFragmentFromFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ZhTwoFragment extends Fragment {

    RecyclerView slaCycler;
    TextView textTitle, numTitle;
    Button submit;
    ImageButton back;
    String title,content,idDetailForm,passGrade,bobot,fromId;
    SLACyclerAdapter adapter;
    Double value;

    public ZhTwoFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public ZhTwoFragment(String title , String content , String idDetailForm , Double value , String passGrade , String bobot , String fromId){
        this.title = title;
        this.idDetailForm = idDetailForm;
        this.content = content;
        this.value = value;
        this.passGrade = passGrade;
        this.bobot = bobot;
        this.fromId = fromId;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_zh_sla, container, false);
        submit = view.findViewById(R.id.submit_zhsla);
        back = view.findViewById(R.id.back_zhsla);
        back.setVisibility(View.VISIBLE);
        textTitle = view.findViewById(R.id.title_zhsla);
        numTitle = view.findViewById(R.id.number_zhsla);
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

                int countAll = 0;

                for (int i = 0 ; i<data.length() ; i++){
                    JSONObject childObj = data.getJSONObject(i);
                    String id = this.fromId+"-"+childObj.getString("id");
                    String label = childObj.getString("label");
                    String content = childObj.getJSONArray("data").toString();
                    JSONArray counting = new JSONArray(content);

                    int secount = 0;
                    int counter = 0;
                    String sama = "";
                    for (int j = 0 ; j<counting.length() ; j++){
                        JSONObject child1 = counting.getJSONObject(j);
                        String id2 = child1.getString("id");
                        JSONArray contentCh1 = child1.getJSONArray("data");
                        secount++;
                        for (int k = 0 ; k<contentCh1.length() ; k++){
                            String id3 = id+"-"+id2;
                            counter++;
                            if (listId.size() != 0){
                                for (int m = 0 ; m < listId.size() ; m++){
                                    if (listId.get(m).equalsIgnoreCase(id3)){
                                        counter--;
                                        if(!sama.equalsIgnoreCase(id3)){
                                            sama = id3;
                                            secount--;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    String countt = secount+"/"+counter;
                    countAll += counter;
                    SLAModel model = new SLAModel(label,content,countt,value/data.length(),false);
                    model.setId(id);
                    itemList.add(model);
                }
                numTitle.setText("("+countAll+")");
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
                loadFragmentFromFragment(ZhTwoFragment.this,new ZhThreeFragment(item.getTitle(),item.getDaleman(),idDetailForm,item.getValue(),passGrade,bobot,item.getId()),"ZhThree");
            });
            slaCycler.setAdapter(adapter);
            slaCycler.getAdapter().notifyDataSetChanged();

        }
    }

}
