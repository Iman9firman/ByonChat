package com.honda.android.list;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.honda.android.R;
import com.honda.android.provider.MessengerDatabaseHelper;
import com.honda.android.provider.VouchersDetailModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Lukmanpryg on 6/30/2016.
 */
public class ListVoucherDetailAdapter extends BaseAdapter {
    private static ArrayList<VouchersDetailModel> catArrayList;
    private LayoutInflater inflater;
    private MessengerDatabaseHelper dbHelper;
    public Context context;

    public ListVoucherDetailAdapter(Context cntext, ArrayList<VouchersDetailModel> listVoucher) {
        this.context = cntext;
        this.catArrayList = listVoucher;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (dbHelper == null) {
            dbHelper = MessengerDatabaseHelper.getInstance(context);
        }
    }

    public void add(ArrayList<VouchersDetailModel> newresults) {
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

    public class ViewHolder {
        TextView title, nominal, tgl_valid;
        LinearLayout topPanelLeft, topPanelcenter, topPanelRight;
        ImageView icon;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_item_detail_vouchers, null);

            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.nominal = (TextView) convertView.findViewById(R.id.nominal);
            holder.tgl_valid = (TextView) convertView.findViewById(R.id.tgl_valid);
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
//            holder.value = (TextView) convertView.findViewById(R.id.value);
            holder.topPanelRight = (LinearLayout) convertView.findViewById(R.id.topPanelRight);
            holder.topPanelcenter = (LinearLayout) convertView.findViewById(R.id.topPanelcenter);
            holder.topPanelLeft = (LinearLayout) convertView.findViewById(R.id.topPanelLeft);
            Glide.with(context).load(catArrayList.get(position).getIcon()).into(holder.icon);

            String txtcolor = "";
            if (catArrayList.get(position).getTextcolor().toString().equalsIgnoreCase("") || catArrayList.get(position).getTextcolor().toString().equalsIgnoreCase("null")) {
                txtcolor = "ffffff";
            } else {
                txtcolor = catArrayList.get(position).getTextcolor();
            }

            holder.title.setText(catArrayList.get(position).getJudul());
            holder.title.setTextColor(Color.parseColor("#" + txtcolor));
            holder.tgl_valid.setText(catArrayList.get(position).getTgl_valid());
            holder.tgl_valid.setTextColor(Color.parseColor("#" + txtcolor));
            NumberFormat nf = NumberFormat.getNumberInstance(new Locale("in", "ID"));
            holder.nominal.setText("Rp " + String.valueOf(nf.format(Double.parseDouble(catArrayList.get(position).getValue()))));
            holder.nominal.setTextColor(Color.parseColor("#" + txtcolor));

            String color = "";
            if (catArrayList.get(position).getColor().toString().equalsIgnoreCase("") || catArrayList.get(position).getColor().toString().equalsIgnoreCase("null")) {
                color = "1e8cc4";
            } else {
                color = catArrayList.get(position).getColor();
            }
            Drawable mDrawableLetf = context.getResources().getDrawable(R.drawable.voucher_inner_left);
            mDrawableLetf.setColorFilter(Color.parseColor("#"+color), PorterDuff.Mode.SRC_ATOP);
            Drawable mDrawableRight = context.getResources().getDrawable(R.drawable.voucher_inner_right);
            mDrawableRight.setColorFilter(Color.parseColor("#"+color), PorterDuff.Mode.SRC_ATOP);
            Drawable mDrawableCenter = context.getResources().getDrawable(R.drawable.voucher_inner_center);
            mDrawableCenter.setColorFilter(Color.parseColor("#"+color), PorterDuff.Mode.SRC_ATOP);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.topPanelRight.setBackground(mDrawableRight);
                holder.topPanelcenter.setBackground(mDrawableCenter);
                holder.topPanelLeft.setBackground(mDrawableLetf);
            }


            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
            holder.title.setText(catArrayList.get(position).getJudul());
            NumberFormat nf = NumberFormat.getNumberInstance(new Locale("in", "ID"));
//            holder.value.setText("Rp. " + String.valueOf(nf.format(Double.parseDouble(catArrayList.get(position).getValue()))));

        }

        return convertView;
    }
}
