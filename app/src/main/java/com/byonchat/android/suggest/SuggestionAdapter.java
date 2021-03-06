package com.byonchat.android.suggest;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.byonchat.android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lukmanpryg on 2/17/2016.
 */
public class SuggestionAdapter extends ArrayAdapter<String> {
    protected static final String TAG = "SuggestionAdapter";
    private List<String> suggestions;
    public SuggestionAdapter(Activity context, String nameFilter) {
        super(context, R.layout.list_textview_simple);
        suggestions = new ArrayList<String>();
    }

    @Override
    public int getCount() {
        return suggestions.size();
    }

    @Override
    public String getItem(int index) {
        return suggestions.get(index);
    }

    @Override
    public Filter getFilter() {
        Filter myFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                JsonParse jp=new JsonParse();
                if (constraint != null) {
                    // A class that queries a web API, parses the data and
                    // returns an ArrayList<GoEuroGetSet>
                    List<SuggestGetSet> new_suggestions =jp.getParseJsonWCF(constraint.toString());
                    suggestions.clear();
                    for (int i=0;i<new_suggestions.size();i++) {
                        /*suggestions.add("#" + new_suggestions.get(i).getName());*/
                        suggestions.add(new_suggestions.get(i).getName());
                    }

                    // Now assign the values and count to the FilterResults
                    // object
                    filterResults.values = suggestions;
                    filterResults.count = suggestions.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence contraint,
                                          FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return myFilter;
    }

}
