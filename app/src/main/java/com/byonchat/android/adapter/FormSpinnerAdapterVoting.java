package com.byonchat.android.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.byonchat.android.R;
import com.byonchat.android.widget.DynamicDialogSelfLayoutVoting;

import java.util.ArrayList;

public class FormSpinnerAdapterVoting extends ArrayAdapter<String> {

	private Activity context;
    ArrayList<String> data = null;

    public FormSpinnerAdapterVoting(Activity context, int resource, ArrayList<String> data)
    {
        super(context, resource, data);
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) 
    {   // Ordinary view in Spinner, we use android.R.layout.simple_spinner_item
    	
    	String item = data.get(position);
    	if(item.toLowerCase().equalsIgnoreCase("umum") ||
    			item.toLowerCase().equalsIgnoreCase("rahasia")){
    		DynamicDialogSelfLayoutVoting.posValueSifat = position;
    	}else{
    		DynamicDialogSelfLayoutVoting.posValueTimer = position;
    	}
    	//doing normal again for fieldpos
        return super.getView(position, convertView, parent);   
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {   // This view starts when we click the spinner.
        View row = convertView;
        if(row == null)
        {
            LayoutInflater inflater = context.getLayoutInflater();
            row = inflater.inflate(R.layout.form_spinner_item, parent, false);
        }

        String item = data.get(position);
        Log.i("== get drop down == ", item);
        if(item != null)
        {   // Parse the data from each object and set it.
            TextView textData = (TextView) row.findViewById(R.id.form_spinner_item_textview);
            textData.setText(item);
        }
        return row;
    }
    
    public View setLabelDropDownView(int position, View convertView, ViewGroup parent)
    {   // This view starts when we click the spinner.
        View row = convertView;
        if(row == null)
        {
            LayoutInflater inflater = context.getLayoutInflater();
            row = inflater.inflate(R.layout.form_spinner_item, parent, false);
        }
        String item = data.get(position);
        //Log.i("== set label item == ", item);
        if(item != null)
        {   // Parse the data from each object and set it.
            TextView textData = (TextView) row.findViewById(R.id.form_spinner_item_textview);
            textData.setText(item);
        }
        
        return row;
    }
    
}

