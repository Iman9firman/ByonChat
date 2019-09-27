package com.honda.android.FragmentDinamicRoom;

import android.app.Activity;
import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.honda.android.R;
import com.honda.android.utils.Validations;

import java.util.ArrayList;
import java.util.List;

public class FormChildAdapter extends BaseAdapter {

    Context context;
    List<ModelFormChild> rowItems;
    protected ArrayList items;
    String from = "";
    Boolean froms = false;

    FormChildAdapter(Context context, List<ModelFormChild> rowItems, String frm, Boolean frmB) {
        this.context = context;
        this.rowItems = rowItems;
        this.from = frm;
        this.froms = frmB;
    }

    @Override
    public int getCount() {
        return rowItems.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rowItems.indexOf(getItem(position));
    }

    /* private view holder class */
    private class ViewHolder {
        TextView title;
        TextView detail;
        TextView price;
    }

    private class ViewHolderImage {
        ImageView image;
        TextView detail;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        ViewHolderImage holderImage = null;
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            if (froms) {
                convertView = mInflater.inflate(R.layout.list_item_form_order_image, null);
                holderImage = new ViewHolderImage();
                holderImage.image = (ImageView) convertView.findViewById(R.id.image);
                holderImage.detail = (TextView) convertView.findViewById(R.id.desc);
                convertView.setTag(holderImage);
            } else {
                holder = new ViewHolder();
                if (from.equalsIgnoreCase("multiple")) {
                    convertView = mInflater.inflate(R.layout.list_item_form_order_multiple, null);
                } else if (from.equalsIgnoreCase("value")) {
                    convertView = mInflater.inflate(R.layout.list_item_form_order_value, null);
                } else {
                    Log.w("pes", "2");
                    convertView = mInflater.inflate(R.layout.list_item_form_order, null);
                }

                holder.title = (TextView) convertView.findViewById(R.id.title_order);
                holder.detail = (TextView) convertView.findViewById(R.id.detail_order);
                holder.price = (TextView) convertView.findViewById(R.id.price_order);

                convertView.setTag(holder);

            }
        } else {
            if (froms) {
                holderImage = (ViewHolderImage) convertView.getTag();
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

        }

        if (froms) {
            ModelFormChild row_pos = rowItems.get(position);
            String aa[] = row_pos.getTitle().toString().split(";");
          /*  if (from.equalsIgnoreCase("value")) {
                if (aa.length==2){
                    Picasso.with(context).load("https://bb.byonchat.com/bc_voucher_client/images/list_task/"+aa[0]).into(holderImage.image);
                }
            }else{
                if (aa.length==2){
                    Picasso.with(context).load("file://"+aa[1]).into(holderImage.image);
                }
            }*/
            holderImage.detail.setText(row_pos.getDetail());
        } else {

            ModelFormChild row_pos = rowItems.get(position);
            Double nilai = 0.0;
            String dodo = row_pos.getDetail();
            if (dodo.contains(" ")) {
                dodo = dodo.substring(0, dodo.indexOf(" "));
            }
            Log.w("dd", dodo);
            try {
                if (!row_pos.isText()) {
                    nilai = Double.parseDouble(dodo != null ? dodo.replace(",", "") : "0.0") * Double.parseDouble(row_pos.getPrice() != null ? row_pos.getPrice().replace(",", "") : "0.0");
                } else {
                    nilai = Double.parseDouble(row_pos.getPrice() != null ? row_pos.getPrice().replace(",", "") : "0.0");
                }
            } catch (Exception e) {
                nilai = 0.0;
            }

            String totalHarga = new Validations().getInstance(context).numberToCurency(String.valueOf(nilai));

            Log.w("sangkurian1", row_pos.getTitle() + "::" + row_pos.getDetail() + "::" + totalHarga);
            holder.title.setText(row_pos.getTitle());
            holder.detail.setText(row_pos.getDetail());
            holder.price.setText(totalHarga);

            if (totalHarga.equalsIgnoreCase("0") || totalHarga.equalsIgnoreCase("0.00")) {
                holder.price.setVisibility(View.GONE);
            } else {
                if (!from.equalsIgnoreCase("value")) {
                    if (!row_pos.isText()) {
                        holder.detail.setText(row_pos.getDetail() + " x " + new Validations().getInstance(context).numberToCurency(row_pos.getPrice()));
                    }
                }
            }
            holder.title.setSelected(true);
            holder.detail.setSelected(true);
        }


        return convertView;


    }


}
