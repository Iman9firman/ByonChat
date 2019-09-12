package com.honda.android.suggest;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.honda.android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lukmanpryg on 2/19/2016.
 */
public class SuggestionAdapterTag extends ArrayAdapter<String> {
    protected static final String TAG = "SuggestionAdapter";
    private List<String> suggestions;
    public SuggestionAdapterTag(Activity context, String nameFilter) {
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
                JsonParseTag jp=new JsonParseTag();
                if (constraint != null) {
                    List<SuggestGetSetTag> new_suggestions =jp.getParseJsonWCF(constraint.toString());
                    suggestions.clear();
                    for (int i=0;i<new_suggestions.size();i++) {
                        suggestions.add("#" + new_suggestions.get(i).getName());
                    }
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
