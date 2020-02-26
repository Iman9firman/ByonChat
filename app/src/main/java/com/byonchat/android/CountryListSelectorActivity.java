package com.byonchat.android;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.byonchat.android.createMeme.FilteringImage;
import com.byonchat.android.list.ListViewCountryAdapter;
import com.byonchat.android.provider.Country;

import java.util.ArrayList;

public class CountryListSelectorActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText et;
    private ListView lv;
    ListViewCountryAdapter adapter;
    String[] name;
    String[] code;
    private int color = 0;
    ArrayList<Country> arraylist = new ArrayList<Country>();
    ImageButton btn_search;
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.county_list);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        color = getResources().getColor(R.color.colorPrimary);
        Bitmap back_default = FilteringImage.headerColor(getWindow(),CountryListSelectorActivity.this, color);
        Drawable back_draw_default = new BitmapDrawable(getResources(), back_default);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            toolbar.setBackground(back_draw_default);
        }else{
            toolbar.setBackgroundDrawable(back_draw_default);
        }

        lv  =   (ListView) findViewById(android.R.id.list);
        name = Country.title;
        code = Country.code;
        setSupportActionBar(toolbar);

        for (int i = 0; i < name.length; i++)
        {
            Country country = new Country(name[i], code[i]);
            // Binds all strings into an array
            arraylist.add(country);
        }

        // Pass results to ListViewAdapter Class
        adapter = new ListViewCountryAdapter(this, arraylist);

        // Binds the Adapter to the ListView
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0,
                                    View arg1, int position, long arg3)
            {

                Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
                intent.putExtra(RegistrationActivity.BUNDLE_KEY_CODE,
                        ( adapter.newList().get(position).getCodeContry().replaceFirst("\\+","")));
                intent.putExtra(RegistrationActivity.BUNDLE_KEY_NAME,
                        ( adapter.newList().get(position).getNameContry()));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                // Do something
                return true;
            }
         });

        return true;
    }


}