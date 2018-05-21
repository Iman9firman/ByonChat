package com.byonchat.android.FragmentDinamicRoom;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.byonchat.android.R;
import com.byonchat.android.list.utilLoadImage.ImageLoader;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.RoomsDetail;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RoomDetailAdapter extends BaseAdapter implements Filterable {
    public ImageLoader imgMenu;
    private final LayoutInflater mInflater;
    Context ctx;
    BotListDB db ;
    private List<ItemRoomDetail> originalData = null;
    private List<ItemRoomDetail> filteredData = null;
    private ItemFilter mFilter = new ItemFilter();
    List<ItemRoomDetail> mItems = new ArrayList<ItemRoomDetail>();
    public RoomDetailAdapter(Context context,List<ItemRoomDetail> isi) {
        this.filteredData = isi ;
        this.originalData = isi ;
        ctx = context;
        imgMenu = new ImageLoader(context);
        mInflater = LayoutInflater.from(context);
        if(db==null){
            db = BotListDB.getInstance(context);
        }
    }

    public int getCount() {
        return filteredData.size();
    }

    public ItemRoomDetail getItem(int position) {
        return filteredData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        Button plus;
        Button minus;
        TextView name;
        TextView des;
        TextView qOrder = null;

        if (v == null) {
            v = mInflater.inflate(R.layout.list_item_room_pos_detail, viewGroup, false);
            v.setTag(R.id.Title, v.findViewById(R.id.Title));
            v.setTag(R.id.Description, v.findViewById(R.id.Description));
            v.setTag(R.id.qOrder, v.findViewById(R.id.qOrder));
            v.setTag(R.id.minus, v.findViewById(R.id.minus));
            v.setTag(R.id.plus, v.findViewById(R.id.plus));
        }

        name = (TextView) v.getTag(R.id.Title);
        des = (TextView) v.getTag(R.id.Description);
        final ItemRoomDetail item = getItem(i);
        name.setText(item.name);
        qOrder = (TextView) v.getTag(R.id.qOrder);
        minus = (Button) v.getTag(R.id.minus);
        plus = (Button) v.getTag(R.id.plus);

        Locale dutch2 = new Locale("id", "ID");
        NumberFormat numberFormatDutch2 = NumberFormat.getCurrencyInstance(dutch2);
        String c2 = numberFormatDutch2.format(new BigDecimal(Integer.valueOf(item.price)));
        name.setText(item.name);
        des.setText(c2.replace("Rp","Rp. "));
        qOrder.setHint("0");

        Cursor cursor = db.getSingleRoomDetailStatusSKU(String.valueOf(item.sku));
        if(cursor.getCount()>0) {
            String n = jsonToQty(cursor.getString(cursor.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT)));
            qOrder.setText(n);
        }else{
            qOrder.setText("0");
        }
        final TextView finalQOrder = qOrder;
        final Handler mHandler = new Handler() {

            @Override
            public void handleMessage(android.os.Message msg) {
                InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(finalQOrder.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        };

        finalQOrder.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mHandler.sendEmptyMessage(0);
                }
            }
        });
        finalQOrder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mHandler.sendEmptyMessage(0);
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int jumlah = 0;
                int nilai = Integer.valueOf(finalQOrder.getText().toString());
                if (nilai == 0) {
                } else {
                    int insert = nilai - 1;
                    jumlah = insert;
                    Cursor cursor = db.getSingleRoomDetailStatusSKU(String.valueOf(item.sku));
                    if (cursor.getCount() > 0) {
                        String n = jsonToQty(cursor.getString(cursor.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT)));
                        jumlah = Integer.valueOf(n) - 1;
                        db.deleteRoomsDetailByItemSKU(String.valueOf(item.sku));
                        if (insert > 0) {

                            RoomsDetail orderModel = new RoomsDetail(String.valueOf(item.id), "","11",jsonQ(String.valueOf(item.id),item.name, String.valueOf(insert), item.price,item.sku),"","",item.sku);
                            db.insertRoomsDetail(orderModel);
                        }
                    } else {
                        if (insert > 0) {
                            RoomsDetail orderModel = new RoomsDetail(String.valueOf(item.id), "","11",jsonQ(String.valueOf(item.id),item.name, String.valueOf(insert), item.price,item.sku),"","",item.sku);
                            db.insertRoomsDetail(orderModel);
                        }

                    }
                }
                finalQOrder.setText(String.valueOf(jumlah));
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int jumlah = 0;
                int nilai = Integer.valueOf(finalQOrder.getText().toString());
                int insert = nilai + 1;
                jumlah = insert;
                Cursor cursor = db.getSingleRoomDetailStatusSKU(String.valueOf(item.sku));
                if (cursor.getCount() > 0) {
                    String n = jsonToQty(cursor.getString(cursor.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT)));
                    jumlah = Integer.valueOf(n) + 1;
                    db.deleteRoomsDetailByItemSKU(String.valueOf(item.sku));
                    if(insert>0){
                        RoomsDetail orderModel = new RoomsDetail(String.valueOf(item.id), "","11",jsonQ(String.valueOf(item.id),item.name, String.valueOf(insert), item.price,item.sku),"","",item.sku);
                        db.insertRoomsDetail(orderModel);
                    }

                } else {
                    if(insert>0){
                        RoomsDetail orderModel = new RoomsDetail(String.valueOf(item.id), "","11",jsonQ(String.valueOf(item.id),item.name, String.valueOf(insert), item.price,item.sku),"","",item.sku);
                        db.insertRoomsDetail(orderModel);
                    }

                }
                finalQOrder.setText(String.valueOf(jumlah));
            }
        });

        return v;
    }


    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<ItemRoomDetail> list = originalData;

            int count = list.size();
            final ArrayList<ItemRoomDetail> nlist = new ArrayList<ItemRoomDetail>(count);

            ItemRoomDetail filterableString ;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i);
                if (filterableString.getName().toLowerCase().contains(filterString)) {
                    nlist.add(filterableString);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<ItemRoomDetail>) results.values;
            notifyDataSetChanged();
        }

    }

    public String jsonQ(String id,String name,String qty,String price,String sku) {

        JSONObject obj = new JSONObject();
        try {
            obj.put("id",id);
            obj.put("name",name);
            obj.put("qty",qty);
            obj.put("price",price);
            obj.put("sku",sku);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }

    public String jsonToQty(String object) {
        String jumlah = "";
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(object);
            jumlah = jObject.getString("qty");
        } catch (JSONException e) {
            e.printStackTrace();
        }
       return jumlah;
    }

    public String jsonToName(String object) {
        String name = "";
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(object);
            name = jObject.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return name;
    }

    public String jsonToPrice(String object) {
        String price = "";
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(object);
            price = jObject.getString("price");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return price;
    }

}