package com.byonchat.android.FragmentDinamicRoom;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.byonchat.android.R;
import com.byonchat.android.adapter.CircularContactView;
import com.byonchat.android.provider.ContentRoom;
import com.byonchat.android.utils.OnLoadMoreListener;
import com.byonchat.android.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Iman Firmansyah on 3/21/2016.
 */
public class DinamicListTaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static String LOG_TAG = "DinamicListTaskAdapter";
    private ArrayList<ContentRoom> mDataset;
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
        context = ctx;
        PHOTO_TEXT_BACKGROUND_COLORS = ctx.getResources().getIntArray(R.array.contacts_text_background_colors);
    }

    @Override
    public int getItemViewType(int position) {
        return mDataset.get(position) != null ? VIEW_ITEM : VIEW_PROG;
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

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DataObjectHolder) {
            final String displayName = mDataset.get(position).getTitle();

            final int backgroundColorToUse = PHOTO_TEXT_BACKGROUND_COLORS[position
                    % PHOTO_TEXT_BACKGROUND_COLORS.length];
            if (TextUtils.isEmpty(displayName))
                ((DataObjectHolder) holder).imageText.setTextAndBackgroundColor(" ", backgroundColorToUse);
            else {
                final String characterToShow = TextUtils.isEmpty(displayName) ? "" : displayName.substring(0, 1).toUpperCase(Locale.getDefault());
                ((DataObjectHolder) holder).imageText.setTextAndBackgroundColor(characterToShow, backgroundColorToUse);
            }


            if (String.valueOf(mDataset.get(position).getStatus()).equalsIgnoreCase("") || String.valueOf(mDataset.get(position).getStatus()) == null) {
                ((DataObjectHolder) holder).status.setVisibility(View.INVISIBLE);
            } else {
                String sts = String.valueOf(mDataset.get(position).getStatus());

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

                    if (String.valueOf(mDataset.get(position).getStatus()).equalsIgnoreCase("new") || String.valueOf(mDataset.get(position).getStatus()).equalsIgnoreCase("4")) {

                    } else if (String.valueOf(mDataset.get(position).getStatus()).equalsIgnoreCase("draft") || String.valueOf(mDataset.get(position).getStatus()).equalsIgnoreCase("0")) {
                        mDrawableLetf.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
                    } else if (String.valueOf(mDataset.get(position).getStatus()).equalsIgnoreCase("proses") || String.valueOf(mDataset.get(position).getStatus()).equalsIgnoreCase("1")) {
                        mDrawableLetf.setColorFilter(Color.CYAN, PorterDuff.Mode.SRC_ATOP);
                    } else if (String.valueOf(mDataset.get(position).getStatus()).equalsIgnoreCase("failed") || String.valueOf(mDataset.get(position).getStatus()).equalsIgnoreCase("3")) {
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

            if (String.valueOf(mDataset.get(position).getTime()).equalsIgnoreCase("") || String.valueOf(mDataset.get(position).getTime()) == null) {
                ((DataObjectHolder) holder).dateInfo.setVisibility(View.INVISIBLE);
            } else {
                ((DataObjectHolder) holder).dateInfo.setText(Utility.parseDateToddMMyyyy(String.valueOf(mDataset.get(position).getTime())));
            }

            if (String.valueOf(mDataset.get(position).getContent()).equalsIgnoreCase("") || String.valueOf(mDataset.get(position).getContent()) == null) {
                ((DataObjectHolder) holder).label.setVisibility(View.INVISIBLE);
                ((DataObjectHolder) holder).textInfo.setVisibility(View.INVISIBLE);
                ((DataObjectHolder) holder).titleCenter.setVisibility(View.VISIBLE);
                ((DataObjectHolder) holder).titleCenter.setText(displayName);
            } else {

                ((DataObjectHolder) holder).label.setText(displayName);

                JSONObject jObject = null;
                try {
                    jObject = new JSONObject(String.valueOf(mDataset.get(position).getContent()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (jObject != null) {
                    ((DataObjectHolder) holder).label.setVisibility(View.VISIBLE);
                    ((DataObjectHolder) holder).titleCenter.setVisibility(View.INVISIBLE);
                    ((DataObjectHolder) holder).textInfo.setText(mDataset.get(position).getContent());
                } else {

                }
            }
        } else {

        }

    }

    public void addItem(ContentRoom dataObj, int index) {
        mDataset.add(dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

    public interface MyClickListenerLongClick {
        public void onLongClick(int position, View v);
    }
}