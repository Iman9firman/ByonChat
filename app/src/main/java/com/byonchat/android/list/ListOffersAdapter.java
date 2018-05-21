package com.byonchat.android.list;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.byonchat.android.R;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.VouchersModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;


/**
 * Created by Asus on 9/16/2014.
 */
public class ListOffersAdapter extends BaseAdapter
{
    private static ArrayList<VouchersModel> catArrayList;
    private LayoutInflater inflater;
    public Context context;
    private MessengerDatabaseHelper dbhelper;
    //Lukman+
    private ArrayList<VouchersModel> mListVoucher;
    public ListOffersAdapter(Context ctx, ArrayList<VouchersModel> listVoucher) {
        context = ctx;
        //Lukman+
        this.catArrayList = listVoucher;
//        this.catArrayList = new ArrayList<ItemListVoucher>();
        //Lukman-
        inflater  = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (dbhelper == null) {
            dbhelper = MessengerDatabaseHelper.getInstance(context);
        }
    }
    //Lukman-

    public void add(ArrayList<VouchersModel> newresults){
        this.catArrayList.addAll(newresults);
    }

    public int getCount() {
        return catArrayList.size();
    }

    public Object getItem(int position) {
        return catArrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder
    {
        TextView title,value;
        LinearLayout topPanelLeft,topPanelcenter,topPanelRight;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        final ViewHolder holder;
        if(convertView==null)
        {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_item_offers, null);

            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.value = (TextView) convertView.findViewById(R.id.value);
            holder.topPanelRight = (LinearLayout) convertView.findViewById(R.id.topPanelRight);
            holder.topPanelcenter = (LinearLayout) convertView.findViewById(R.id.topPanelcenter);
            holder.topPanelLeft = (LinearLayout) convertView.findViewById(R.id.topPanelLeft);

            String txtcolor ="";
            if(catArrayList.get(position).getTextcolor().toString().equalsIgnoreCase("") || catArrayList.get(position).getTextcolor().toString().equalsIgnoreCase("null")){
                txtcolor = "000000";
            }else{
                txtcolor = catArrayList.get(position).getTextcolor();
            }
            holder.title.setText(catArrayList.get(position).getJudul());
            holder.title.setTextColor(Color.parseColor("#"+txtcolor));
            NumberFormat nf = NumberFormat.getNumberInstance(new Locale("in", "ID"));
            holder.value.setText("Rp. " + String.valueOf(nf.format(Double.parseDouble(catArrayList.get(position).getValue()))));


            String color ="";
            if(catArrayList.get(position).getColor().toString().equalsIgnoreCase("") || catArrayList.get(position).getColor().toString().equalsIgnoreCase("null")){
                color = "ffffff";
            }else{
                color = catArrayList.get(position).getColor();
            }
            Drawable mDrawableLetf = context.getResources().getDrawable(R.drawable.offer_inner_left);
            mDrawableLetf.setColorFilter(Color.parseColor("#"+color), PorterDuff.Mode.SRC_ATOP);
            Drawable mDrawableRight = context.getResources().getDrawable(R.drawable.offer_inner_right);
            mDrawableRight.setColorFilter(Color.parseColor("#"+color), PorterDuff.Mode.SRC_ATOP);
            Drawable mDrawableCenter = context.getResources().getDrawable(R.drawable.offer_inner_center);
            mDrawableCenter.setColorFilter(Color.parseColor("#"+color), PorterDuff.Mode.SRC_ATOP);

            holder.topPanelRight.setBackground(mDrawableRight);
            holder.topPanelcenter.setBackground(mDrawableCenter);
            holder.topPanelLeft.setBackground(mDrawableLetf);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
            holder.title.setText(catArrayList.get(position).getJudul());
            NumberFormat nf = NumberFormat.getNumberInstance(new Locale("in", "ID"));
            holder.value.setText("Rp. " + String.valueOf(nf.format(Double.parseDouble(catArrayList.get(position).getValue()))));
        }

        return convertView;
    }
}
