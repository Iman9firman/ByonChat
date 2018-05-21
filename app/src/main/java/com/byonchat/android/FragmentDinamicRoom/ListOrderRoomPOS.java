package com.byonchat.android.FragmentDinamicRoom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.byonchat.android.R;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ListOrderRoomPOS extends ArrayAdapter<ItemRoomDetail> {
    public ListOrderRoomPOS(Context context, ArrayList<ItemRoomDetail> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ItemRoomDetail user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_order_room_pos, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvHome = (TextView) convertView.findViewById(R.id.tvHome);
        // Populate the data into the template view using the data object
        tvName.setText(user.getQtyOrder()+"  "+ user.getName());
        tvHome.setText(addNumbers(String.valueOf(Integer.valueOf(user.getPrice())*Integer.valueOf(user.getQtyOrder()))));
        // Return the completed view to render on screen
        return convertView;
    }
    private String addNumbers(String hasil) {

        Locale indonesia = new Locale("id", "ID");
        NumberFormat numberFormatDutch = NumberFormat.getCurrencyInstance(indonesia);

        String c = numberFormatDutch.format(new BigDecimal(hasil.toString()));
        String format = c.replace("Rp", "");

        if(hasil.length()>0){
            return format;
        }else{
            return hasil = "0";
        }
    }


}