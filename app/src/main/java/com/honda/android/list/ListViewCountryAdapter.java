package com.honda.android.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.honda.android.R;
import com.honda.android.provider.Country;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by Iman Firmansyah on 4/27/2015.
 */
public class ListViewCountryAdapter extends BaseAdapter {

    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    private List<Country> countryList = new ArrayList<Country>();
    private ArrayList<Country> arraylist = new ArrayList<Country>();

    public ListViewCountryAdapter(Context context,
                                  List<Country> countryListlist) {
        mContext = context;
        this.countryList = countryListlist;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<Country>();
        this.arraylist.addAll(countryListlist);
    }

    public class ViewHolder {
        TextView title;
        TextView code;
    }

    @Override
    public int getCount() {
        return countryList.size();
    }
    public List<Country> newList()    {
        List<Country> list = countryList;
        return(list);
    }

    @Override
    public Country getItem(int position) {
        return countryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.list_item_country, null);
            // Locate the TextViews in listview_item.xml
            holder.title = (TextView) view.findViewById(R.id.title);
            holder.code = (TextView) view.findViewById(R.id.code);
            // Locate the ImageView in listview_item.xml
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.title.setText(countryList.get(position).getNameContry());
        holder.code.setText(countryList.get(position).getCodeContry());

        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        countryList.clear();
        if (charText.length() == 0) {
            countryList.addAll(arraylist);
        } else {
            Pattern sPattern = Pattern.compile("^([1-9][0-9]{0,2})?(\\.[0-9]?)?$");

            if (sPattern.matcher(charText.replaceAll("\\+","")).matches()){
                for (Country cp : arraylist) {
                    if (cp.getCodeContry().toLowerCase(Locale.getDefault())
                            .contains(charText)) {
                        countryList.add(cp);
                    }
                }
            }else{
                for (Country cp : arraylist) {
                    if (cp.getNameContry().toLowerCase(Locale.getDefault())
                            .contains(charText)) {
                        countryList.add(cp);
                    }
                }
            }

        }
        notifyDataSetChanged();
    }

}
