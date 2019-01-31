package com.byonchat.android.tabRequest;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.util.LogWriter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.byonchat.android.R;
import com.byonchat.android.listeners.RecyclerItemClickListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class RelieverListActivity extends AppCompatActivity {
    
    public static final String XTRA_RELIEVER_JSON = "XTRA_RELIEVER_JSON";
    public static final String XTRA_DETAILS_JSON = "XTRA_DETAILS_JSON";
    public static final String XTRA_LATITUDE = "XTRA_LATITUDE";
    public static final String XTRA_LONGITUDE = "XTRA_LONGITUDE";
    public static final String XTRA_MAX = "XTRA_MAX";

    private String resultJson = null;
    private String resultDetail = null;
    private String resultLatitude = null;
    private String resultLongitude = null;
    private int resultMax = 0;
    private ArrayList<Reliever> relievers ;

    private TextView textDetail;
    private Button butMap;
    private TextView textMaxSelect;
    private ConstraintLayout layoutEmpty;
    private TextView textNoResult;
    private ImageView imageCall;
    private RecyclerView recyclerReliever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reliever_list);
        setView();
        methodRecycler();
        methodMap();

    }

    private void setView(){
        textDetail = (TextView) findViewById(R.id.text_detail_relieverList);
        butMap = (Button) findViewById(R.id.but_map_relieverList);
        textMaxSelect = (TextView) findViewById(R.id.text_maxSelect_relieverList);
        layoutEmpty = (ConstraintLayout) findViewById(R.id.container_01_relieverList);
        textNoResult = (TextView) findViewById(R.id.text_noResult_relieverList);
        imageCall = (ImageView) findViewById(R.id.image_call_relieverList);
        recyclerReliever = (RecyclerView) findViewById(R.id.recyler_relieverList);

        resultJson = getIntent().getStringExtra(XTRA_RELIEVER_JSON);
        resultDetail = getIntent().getStringExtra(XTRA_DETAILS_JSON);
        resultLatitude = getIntent().getStringExtra(XTRA_LATITUDE);
        resultLongitude = getIntent().getStringExtra(XTRA_LONGITUDE);
        resultMax = Integer.valueOf(getIntent().getStringExtra(XTRA_MAX));

        textDetail.setText(resultDetail);
        textMaxSelect.setText("Max Selection : "+resultMax);
    }

    private void methodRecycler(){
        if (getList(resultJson,relievers).size() != 0){

            recyclerReliever.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
            recyclerReliever.setLayoutManager(new LinearLayoutManager(this));
            recyclerReliever.setAdapter(new RelieverListAdapter(this,getList(resultJson,relievers),resultMax));
            recyclerReliever.getAdapter().notifyDataSetChanged();

        } else {
            textNoResult.setText("No result found , please call ISS");
            recyclerReliever.setVisibility(View.GONE);
            textNoResult.setVisibility(View.VISIBLE);
            imageCall.setVisibility(View.VISIBLE);
        }
    }

    private void methodMap(){
        butMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent maps = new Intent(RelieverListActivity.this,MapsViewActivity.class);
                maps.putExtra(XTRA_RELIEVER_JSON,resultJson);
                maps.putExtra(XTRA_LATITUDE,resultLatitude);
                maps.putExtra(XTRA_LONGITUDE,resultLongitude);
                startActivity(maps);
            }
        });
        butMap.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent maps = new Intent(RelieverListActivity.this,MapsViewActivity.class);
                maps.putExtra("FLAG_OSM",888);
                startActivity(maps);
                return true;
            }
        });
    }

    public static ArrayList<Reliever> getList(String jsonresult , ArrayList<Reliever> relievers){
        relievers = new ArrayList<>();
        try {
            JSONArray result = new JSONArray(jsonresult);
            for (int i = 0; i< result.length() ; i++){
                JSONObject relieverJson = result.getJSONObject(i);
                Reliever reliever = new Reliever(
                        relieverJson.getString("id_officer"),
                        relieverJson.getString("id_client"),
                        relieverJson.getString("name"),
                        relieverJson.getString("telp_number"),
                        relieverJson.getString("last_lat"),
                        relieverJson.getString("last_long"),
                        relieverJson.getString("distance"),
                        relieverJson.getString("rating"),
                        false
                );
                relievers.add(reliever);
            }
            Collections.sort(relievers);
            return relievers;
        } catch (Exception e){
            Log.w("ivana", "Error : "+e.getMessage());

        }
        return relievers;
    }
}
