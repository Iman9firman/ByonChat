package com.byonchat.android.FragmentDinamicRoom;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.byonchat.android.R;
import com.byonchat.android.adapter.CircularContactView;
import com.byonchat.android.provider.ContentRoom;
import com.byonchat.android.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Iman Firmansyah on 3/21/2016.
 */
public class DinamicListTaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private static String LOG_TAG = "DinamicListTaskAdapter";
    private ArrayList<ContentRoom> mDataset;
    private ArrayList<ContentRoom> mDatasetFind;
    private String charString;
    private static MyClickListener myClickListener;
    private static MyClickListenerLongClick myClickListenerLongClick;
    public Context context;
    private final int[] PHOTO_TEXT_BACKGROUND_COLORS;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView label;
        CircularContactView imageText;
        TextView status;
        TextView titleCenter;
        TextView textInfo;
        TextView dateInfo;

        public DataObjectHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.titleText);
            imageText = (CircularContactView) itemView
                    .findViewById(R.id.imageText);
            imageText.getTextView().setTextColor(0xFFffffff);
            status = (TextView) itemView.findViewById(R.id.statusTxt);
            titleCenter = (TextView) itemView.findViewById(R.id.titleCenter);
            textInfo = (TextView) itemView.findViewById(R.id.textInfo);
            dateInfo = (TextView) itemView.findViewById(R.id.dateInfo);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    myClickListenerLongClick.onLongClick(getPosition(), v);
                    return true;
                }
            });
        }


        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getPosition(), v);
        }
    }

    public class ProgressbarHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressbarHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        }
    }

    public void setOnLongClickListener(MyClickListenerLongClick myClickListenerLongClick) {
        this.myClickListenerLongClick = myClickListenerLongClick;
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public DinamicListTaskAdapter(ArrayList<ContentRoom> myDataset, Context ctx) {
        mDataset = myDataset;
        mDatasetFind = myDataset;
        context = ctx;
        PHOTO_TEXT_BACKGROUND_COLORS = ctx.getResources().getIntArray(R.array.contacts_text_background_colors);
    }

    @Override
    public int getItemViewType(int position) {
        return mDatasetFind.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.dinamic_list_task_adapter, parent, false);
            vh = new DataObjectHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_item, parent, false);
            vh = new ProgressbarHolder(view);
        }
        return vh;
    }

    public List<ContentRoom> getData() {
        mDataset = mDatasetFind;
        return mDatasetFind;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DataObjectHolder) {
            final String displayName = mDatasetFind.get(position).getTitle();

            final int backgroundColorToUse = PHOTO_TEXT_BACKGROUND_COLORS[position
                    % PHOTO_TEXT_BACKGROUND_COLORS.length];
            if (TextUtils.isEmpty(displayName))
                ((DataObjectHolder) holder).imageText.setTextAndBackgroundColor(" ", backgroundColorToUse);
            else {
                final String characterToShow = TextUtils.isEmpty(displayName) ? "" : displayName.substring(0, 1).toUpperCase(Locale.getDefault());
                ((DataObjectHolder) holder).imageText.setTextAndBackgroundColor(characterToShow, backgroundColorToUse);
            }


            if (String.valueOf(mDatasetFind.get(position).getStatus()).equalsIgnoreCase("") || String.valueOf(mDatasetFind.get(position).getStatus()) == null) {
                ((DataObjectHolder) holder).status.setVisibility(View.INVISIBLE);
            } else {
                String sts = String.valueOf(mDatasetFind.get(position).getStatus());

                if (sts.equalsIgnoreCase("")) {
                    ((DataObjectHolder) holder).status.setVisibility(View.GONE);
                    return;
                } else {
                    ((DataObjectHolder) holder).status.setVisibility(View.VISIBLE);
                    if (sts.equalsIgnoreCase("0")) {
                        sts = "draft";
                    } else if (sts.equalsIgnoreCase("1")) {
                        sts = "proses";
                    } else if (sts.equalsIgnoreCase("2")) {
                        sts = "done";
                    } else if (sts.equalsIgnoreCase("3")) {
                        sts = "failed";
                    } else if (sts.equalsIgnoreCase("4")) {
                        sts = "new";
                    }

                    ((DataObjectHolder) holder).status.setText(sts);

                    Drawable mDrawableLetf = context.getResources().getDrawable(R.drawable.status_work);

                    if (String.valueOf(mDatasetFind.get(position).getStatus()).equalsIgnoreCase("new") || String.valueOf(mDatasetFind.get(position).getStatus()).equalsIgnoreCase("4")) {

                    } else if (String.valueOf(mDatasetFind.get(position).getStatus()).equalsIgnoreCase("draft") || String.valueOf(mDatasetFind.get(position).getStatus()).equalsIgnoreCase("0")) {
                        mDrawableLetf.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
                    } else if (String.valueOf(mDatasetFind.get(position).getStatus()).equalsIgnoreCase("proses") || String.valueOf(mDatasetFind.get(position).getStatus()).equalsIgnoreCase("1")) {
                        mDrawableLetf.setColorFilter(Color.CYAN, PorterDuff.Mode.SRC_ATOP);
                    } else if (String.valueOf(mDatasetFind.get(position).getStatus()).equalsIgnoreCase("failed") || String.valueOf(mDatasetFind.get(position).getStatus()).equalsIgnoreCase("3")) {
                        mDrawableLetf.setColorFilter(Color.MAGENTA, PorterDuff.Mode.SRC_ATOP);
                    } else {
                        if (sts.equalsIgnoreCase("Reject")) {
                            mDrawableLetf.setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                        }

                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((DataObjectHolder) holder).status.setBackground(mDrawableLetf);
                    }

                }
            }

            if (String.valueOf(mDatasetFind.get(position).getTime()).equalsIgnoreCase("") || String.valueOf(mDatasetFind.get(position).getTime()) == null) {
                ((DataObjectHolder) holder).dateInfo.setVisibility(View.INVISIBLE);
            } else {
                setFindedText(((DataObjectHolder) holder).dateInfo, Utility.parseDateToddMMyyyy(String.valueOf(mDatasetFind.get(position).getTime())));
            }

            if (String.valueOf(mDatasetFind.get(position).getContent()).equalsIgnoreCase("") || String.valueOf(mDatasetFind.get(position).getContent()) == null) {
                ((DataObjectHolder) holder).label.setVisibility(View.INVISIBLE);
                ((DataObjectHolder) holder).textInfo.setVisibility(View.INVISIBLE);
                ((DataObjectHolder) holder).titleCenter.setVisibility(View.VISIBLE);

                setFindedText(((DataObjectHolder) holder).titleCenter, displayName);
            } else {
                setFindedText(((DataObjectHolder) holder).label, displayName);

                JSONObject jObject = null;
                try {
                    jObject = new JSONObject(String.valueOf(mDatasetFind.get(position).getContent()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (jObject != null) {
                    ((DataObjectHolder) holder).label.setVisibility(View.VISIBLE);
                    ((DataObjectHolder) holder).titleCenter.setVisibility(View.INVISIBLE);

                    String content = mDatasetFind.get(position).getContent();

                    setFindedText(((DataObjectHolder) holder).textInfo, content);
                } else {

                }
            }
        } else {

        }

    }

    protected void setFindedText(TextView view, String args) {
        if (charString != null && !charString.isEmpty()) {
            int startPos = args.toLowerCase(Locale.getDefault()).indexOf(charString.toLowerCase(Locale.getDefault()));
            int endPos = startPos + charString.length();

            if (startPos != -1) {
                Spannable spannable = new SpannableString(args);
                ColorStateList blueColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.BLUE});
                TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.NORMAL, -1, blueColor, null);
                spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                view.setText(spannable);
            } else {
                view.setText(args);
            }
        } else {
            view.setText(args);
        }
    }

    public void addItem(ContentRoom dataObj, int index) {
        mDatasetFind.add(dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDatasetFind.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDatasetFind.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

    public interface MyClickListenerLongClick {
        public void onLongClick(int position, View v);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                charString = constraint.toString().toLowerCase(Locale.getDefault());

                if (charString.isEmpty()) {
                    results.values = mDataset;
                    results.count = mDataset.size();
                } else {
                    List<ContentRoom> filteredData = new ArrayList<>();
                    for (ContentRoom row : mDataset) {
                        if (row.getTitle().toString().toLowerCase(Locale.getDefault()).contains(charString)
                                || row.getContent().toString().toLowerCase(Locale.getDefault()).contains(charString)
                                || Utility.parseDateToddMMyyyy(String.valueOf(row.getTime())).toLowerCase(Locale.getDefault()).contains(charString)) {
                            filteredData.add(row);
                        }
                    }
                    results.values = filteredData;
                    results.count = filteredData.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mDatasetFind = (ArrayList<ContentRoom>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}