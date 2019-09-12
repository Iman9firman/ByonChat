package com.honda.android.smsSolders;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.honda.android.R;
import com.innodroid.expandablerecycler.ExpandableRecyclerAdapter;

import java.util.List;

public class ReportingAdapter extends ExpandableRecyclerAdapter<ReportingAdapter.PeopleListItem> {
    public static final int TYPE_PERSON = 1001;
    Context ctx;

    public ReportingAdapter(Context context) {
        super(context);
        this.ctx = context;

    }

    public void addData(List<PeopleListItem> data){
        setItems(data);
    }

    public static class PeopleListItem extends ExpandableRecyclerAdapter.ListItem {
        public String Text;
        public String tempo;
        public String jumlah;
        public String noAwb;

        public PeopleListItem(String noC, String tglTempo, String jumlahBayar, String noawb) {
            super(TYPE_HEADER);

            Text = noC;
            tempo = tglTempo;
            jumlah = jumlahBayar;
            noAwb = noawb;
        }

        public PeopleListItem(String first) {
            super(TYPE_PERSON);

            Text = first;
        }
    }

    public class HeaderViewHolder extends ExpandableRecyclerAdapter.HeaderViewHolder {
        TextView name, tglTempo, harus,awb;
        RelativeLayout rootView;

        public HeaderViewHolder(View view) {
            super(view, (ImageView) view.findViewById(R.id.item_arrow));

            rootView = (RelativeLayout) view.findViewById(R.id.rootView);
            name = (TextView) view.findViewById(R.id.noCicilan);
            tglTempo = (TextView) view.findViewById(R.id.tglJatuhTempo);
            harus = (TextView) view.findViewById(R.id.harusBayar);
            awb = (TextView) view.findViewById(R.id.no_awb);
        }

        public void bind(int position) {
            super.bind(position);

            if (position % 2 == 0) {
                rootView.setBackgroundResource(R.color.transparent);
            } else {
                rootView.setBackgroundColor(Color.WHITE);
            }

            name.setText(visibleItems.get(position).Text);
            tglTempo.setText(visibleItems.get(position).tempo);
            harus.setText(visibleItems.get(position).jumlah);
            awb.setText(visibleItems.get(position).noAwb);
        }
    }

    public class PersonViewHolder extends ExpandableRecyclerAdapter.ViewHolder {
        TextView name;

        public PersonViewHolder(View view) {
            super(view);

            name = (TextView) view.findViewById(R.id.item_name);
        }

        public void bind(int position) {
            name.setText(visibleItems.get(position).Text);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new HeaderViewHolder(inflate(R.layout.item_header_sms, parent));
            case TYPE_PERSON:
            default:
                return new PersonViewHolder(inflate(R.layout.item_person_sms, parent));
        }
    }

    @Override
    public void onBindViewHolder(ExpandableRecyclerAdapter.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                ((HeaderViewHolder) holder).bind(position);
                break;
            case TYPE_PERSON:
            default:
                ((PersonViewHolder) holder).bind(position);
                break;
        }
    }

}
