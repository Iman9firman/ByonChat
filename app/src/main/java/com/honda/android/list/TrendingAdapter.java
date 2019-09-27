package com.honda.android.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.honda.android.R;
import com.honda.android.list.utilLoadImage.ImageLoader;
import com.honda.android.provider.RoomsDB;

import java.util.ArrayList;


public class TrendingAdapter extends BaseAdapter{

    private static ArrayList<ItemListTrending> catArrayList = new ArrayList<ItemListTrending>();
    private static ArrayList<ItemListTrending> catArray = new ArrayList<ItemListTrending>();
    private LayoutInflater inflater;
    public Context context;
    /*ContactsFilter mContactsFilter;*/
    public ImageLoader imageLoader;
    public boolean showTitle = false;
    String bold;
    private RoomsDB roomsDB;

    public TrendingAdapter(Context ctx, ArrayList<ItemListTrending> itemListTrendings, boolean showTitle) {
        context = ctx;
        this.showTitle = showTitle;
        this.catArrayList = itemListTrendings;
        this.catArray = itemListTrendings;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(context);

    }

    public TrendingAdapter(Context ctx, ArrayList<ItemListTrending> itemListTrendings, boolean showTitle, String b) {
        bold = b;
        context = ctx;
        this.showTitle = showTitle;
        this.catArrayList = itemListTrendings;
        this.catArray = itemListTrendings;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(context);

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

    public ArrayList<ItemListTrending> newList() {
        ArrayList<ItemListTrending> list = catArrayList;
        return (list);
    }


    public class ViewHolder {
        Button btnName;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            // convertView = inflater.inflate(R.layout.list_item_contact_bot, null);
            convertView = inflater.inflate(R.layout.list_item_trending_search, null);

            holder.btnName = (Button) convertView.findViewById(R.id.trending_name);
            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();

        holder.btnName.setText(catArrayList.get(position).getName());

        return convertView;
    }

    /*@Override
    public Filter getFilter() {
        if (mContactsFilter == null)
            mContactsFilter = new ContactsFilter();

        return mContactsFilter;
    }*/

    /*private class ContactsFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.values = catArray;
                results.count = catArray.size();
            } else {
                ArrayList<ContactBot> filteredContacts = new ArrayList<ContactBot>();
                for (ContactBot c : catArray) {
                    if (c.getRealname().contains(constraint.toString().replace(" ", "_"))) {
                        filteredContacts.add(c);
                    }
                }
                results.values = filteredContacts;
                results.count = filteredContacts.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            catArrayList = (ArrayList<ContactBot>) results.values;
            notifyDataSetChanged();
        }
    }*/

    public synchronized void refresAdapter(ArrayList<ItemListTrending> list) {
        catArrayList.clear();
        catArrayList.addAll(list);
        catArray.clear();
        catArray.addAll(list);
        notifyDataSetChanged();
    }

    public synchronized void refresAdapter() {
        catArrayList.clear();
        catArray.clear();
        notifyDataSetChanged();
    }
}
