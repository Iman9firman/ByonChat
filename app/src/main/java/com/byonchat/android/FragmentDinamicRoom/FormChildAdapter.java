package com.byonchat.android.FragmentDinamicRoom;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.list.IconItem;
import com.byonchat.android.list.utilLoadImage.ImageLoader;
import com.byonchat.android.list.utilLoadImage.TextLoader;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.Group;
import com.byonchat.android.utils.Validations;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
                nilai = Double.parseDouble(dodo != null ? dodo.replace(",", "") : "0.0") * Double.parseDouble(row_pos.getPrice() != null ? row_pos.getPrice().replace(",", "") : "0.0");
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
                    holder.detail.setText(row_pos.getDetail() + " x " + new Validations().getInstance(context).numberToCurency(row_pos.getPrice()));
                }
            }
            holder.title.setSelected(true);
            holder.detail.setSelected(true);
        }


        return convertView;


        /*Log.w("ada",position+"");

        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (froms) {
                Log.w("koo","1");
                convertView = mInflater.inflate(R.layout.list_item_form_order_image, null);
                ViewHolderImage holderImage = new ViewHolderImage();
                holderImage.image = (ImageView) convertView.findViewById(R.id.image);
                holderImage.detail = (TextView) convertView.findViewById(R.id.desc);
                convertView.setTag(holder);
                ModelFormChild row_pos = rowItems.get(position);
                String aa[] = row_pos.getTitle().toString().split(";");
                Log.w("koo",row_pos.getTitle().toString());
                Log.w("koo",row_pos.getPrice().toString());
                Log.w("koo",row_pos.getDetail().toString());
                holderImage.image.setImageBitmap(null);
                if (from.equalsIgnoreCase("value")) {
                    if (aa.length==2){
                        Picasso.with(context).load("https://bb.byonchat.com/bc_voucher_client/images/list_task/"+aa[0]).into(holderImage.image);
                    }
                }else{
                    if (aa.length==2){
                        Uri imgUri = Uri.parse(aa[1]);
                        Log.w("alini",imgUri.toString());
                        holderImage.image.setImageURI(imgUri);
                    }
                }
                holderImage.detail.setText(row_pos.getDetail());

            } else {
                if (from.equalsIgnoreCase("value")) {
                    convertView = mInflater.inflate(R.layout.list_item_form_order_value, null);
                } else {
                    Log.w("pes", "2");
                    convertView = mInflater.inflate(R.layout.list_item_form_order, null);
                }

                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.title_order);
                holder.detail = (TextView) convertView.findViewById(R.id.detail_order);
                holder.price = (TextView) convertView.findViewById(R.id.price_order);

                convertView.setTag(holder);

                ModelFormChild row_pos = rowItems.get(position);
                int nilai = 0;
                try {
                    nilai = Integer.valueOf(row_pos.getDetail() != null ? row_pos.getDetail() : "0") * Integer.valueOf(row_pos.getPrice().replace(".", "") != null ? row_pos.getPrice().replace(".", "") : "0");
                } catch (Exception e) {
                    nilai = 0;
                }

                String totalHarga = new Validations().getInstance(context).numberToCurency(String.valueOf(nilai));

                holder.title.setText(row_pos.getTitle());
                holder.detail.setText(row_pos.getDetail());
                holder.price.setText(totalHarga);

                Log.w("ga bisa", totalHarga);
                if (totalHarga.equalsIgnoreCase("0")) {
                    holder.price.setVisibility(View.GONE);
                }
                holder.title.setSelected(true);
                holder.detail.setSelected(true);

            }

        return convertView;*/
    }


    /*@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        System.out.println("getView " + position + " " + convertView);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item1, null);
            holder = new ViewHolder();
            holder.textView = (TextView)convertView.findViewById(R.id.text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.textView.setText(mData.get(position));
        return convertView;
    }*/
}
