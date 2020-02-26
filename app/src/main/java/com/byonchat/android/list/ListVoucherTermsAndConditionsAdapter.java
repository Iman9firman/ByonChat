package com.byonchat.android.list;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.byonchat.android.R;

import java.util.List;

/**
 * Created by Lukmanpryg on 7/25/2016.
 */
public class ListVoucherTermsAndConditionsAdapter extends RecyclerView.Adapter<ListVoucherTermsAndConditionsAdapter.MyItemHolder>{

    Context context;
    private List<ItemListVoucherTermsAndConditions> data;

    public class MyItemHolder extends RecyclerView.ViewHolder{
        TextView mNumber, mKeterangan;
        public MyItemHolder(View view){
            super(view);
            mNumber = (TextView) view.findViewById(R.id.number);
            mKeterangan = (TextView) view.findViewById(R.id.txtKeterangan);
        }
    }

    public ListVoucherTermsAndConditionsAdapter(Context context, List<ItemListVoucherTermsAndConditions> data){
        this.context = context;
        this.data = data;
    }

    @Override
    public MyItemHolder onCreateViewHolder(ViewGroup parent, int viewType){
//        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_voucher_terms_and_conditions, parent, false);
//        viewHolder = new MyItemHolder(v);

        return new MyItemHolder(v);
    }

    @Override
    public void onBindViewHolder(MyItemHolder holder, int position){
        ItemListVoucherTermsAndConditions item = data.get(position);
        holder.mNumber.setText((position+1)+". ");
        holder.mKeterangan.setText(item.getKeterangan());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
