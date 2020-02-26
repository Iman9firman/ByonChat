package com.byonchat.android;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.byonchat.android.createMeme.FilteringImage;
import com.byonchat.android.list.SkinAdapter;
import com.byonchat.android.provider.IntervalDB;
import com.byonchat.android.provider.Skin;
import com.byonchat.android.utils.Validations;

import java.util.ArrayList;

public class SkinSelectorActivity extends AppCompatActivity implements SkinAdapter.AdapterCallback {

    private ListView lv;
    SkinAdapter adapter;
    ArrayList<Skin> skinArrayList = new ArrayList<Skin>();
    EditText inputSearch;
    ImageButton search;
    ImageButton btn_search;
    ImageButton btn_all_search;
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.skin_selector);
        getSupportActionBar().setBackgroundDrawable(new Validations().getInstance(getApplicationContext()).header(getWindow()));
   //     getSupportActionBar().setIcon(new Validations().getInstance(getApplicationContext()).logoCustome());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);
        lv = (ListView) findViewById(R.id.list_view);
        inputSearch = (EditText) findViewById(R.id.inputSearch);
        search = (ImageButton) findViewById(R.id.btn_search);
        btn_search = (ImageButton) findViewById(R.id.btn_search);
        btn_all_search = (ImageButton) findViewById(R.id.btn_all_search);
        btn_search.setBackground(new BitmapDrawable(getResources(), FilteringImage.viewAll(getApplicationContext(), Color.parseColor(new Validations().getInstance(getApplicationContext()).colorTheme(false)),R.drawable.ic_search)));
        btn_search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (inputSearch.getText().toString().length() > 0) {
                    finish();
                    Intent intent = new Intent(getApplicationContext(), SearchThemesActivity.class);
                    intent.putExtra("search", inputSearch.getText().toString());
                    intent.putExtra("suggested", "0");
                    startActivity(intent);
                }
            }
        });
        btn_all_search.setBackground(new BitmapDrawable(getResources(), FilteringImage.viewAll(getApplicationContext(), Color.parseColor(new Validations().getInstance(getApplicationContext()).colorTheme(false)),R.drawable.view_all)));
        btn_all_search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
                Intent intent = new Intent(getApplicationContext(), SearchThemesActivity.class);
                intent.putExtra("search","");
                intent.putExtra("suggested", "0");
                startActivity(intent);
            }
        });

        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text

                SkinSelectorActivity.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });





    }


    public void refresh(){
        IntervalDB db = new IntervalDB(getApplicationContext());
        db.open();

        skinArrayList = db.retriveallSkin();
        adapter = new SkinAdapter(SkinSelectorActivity.this,skinArrayList);
        lv.setAdapter(adapter);
        lv.setClickable(true);
    }
    @Override
    protected void onResume()
    {
        super.onResume();
         refresh();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMethodCallback() {
        refresh();
    }
}
