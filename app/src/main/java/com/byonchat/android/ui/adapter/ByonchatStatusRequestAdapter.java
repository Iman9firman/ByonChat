package com.byonchat.android.ui.adapter;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.byonchat.android.DownloadFileByonchat;
import com.byonchat.android.ISSActivity.LoginDB.UserDB;
import com.byonchat.android.R;
import com.byonchat.android.data.model.Status;
import com.byonchat.android.ui.viewholder.ByonchatStatusViewHolder;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.gujun.android.taggroup.TagGroup;

public class ByonchatStatusRequestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    protected static final int VIEWTYPE_ITEM_TEXT = 1;

    protected List<Status> items;
    protected List<Status> itemsFiltered;

    protected Context context;
    protected ArrayFilter mFilter;
    protected String mSearchText;
    UserDB dbHelper;
    boolean downloaded = false;
    int num_loc = 0;

    protected OnLongItemClickListener longItemClickListener;

    public ByonchatStatusRequestAdapter(Context context,
                                        List<Status> items) {
        this.context = context;
        this.items = items;
        this.itemsFiltered = items;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        switch (viewType) {
            case VIEWTYPE_ITEM_TEXT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_status_request, parent, false);
                return new ByonchatStatusViewHolder(view, /*itemClickListener, */longItemClickListener);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_status_request, parent, false);
                return new ByonchatStatusViewHolder(view, /*itemClickListener, */longItemClickListener);
        }
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int i) {
        Status item = itemsFiltered.get(i);
        dbHelper = new UserDB(context);
        if (viewHolder instanceof ByonchatStatusViewHolder) {
            String title = item.title;

            if (mSearchText != null && !mSearchText.isEmpty()) {
                int startPos = title.toLowerCase(Locale.getDefault()).indexOf(mSearchText.toLowerCase(Locale.getDefault()));
                int endPos = startPos + mSearchText.length();

                if (startPos != -1) {
                    Spannable spannable = new SpannableString(title);
                    ColorStateList blueColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.BLUE});
                    TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.NORMAL, -1, blueColor, null);
                    spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ((ByonchatStatusViewHolder) viewHolder).vName.setText(spannable);
                } else {
                    ((ByonchatStatusViewHolder) viewHolder).vName.setText(title);
                }
            } else {
                ((ByonchatStatusViewHolder) viewHolder).vName.setText(title);
            }

            showFileImage(viewHolder);
            showTagView(viewHolder);


            ((ByonchatStatusViewHolder) viewHolder).vTimestamp.setText(item.timestamp);

            //SET MARQUEE WAITING
            ((ByonchatStatusViewHolder) viewHolder).vTextStatus.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            ((ByonchatStatusViewHolder) viewHolder).vTextStatus.setSingleLine(true);
            ((ByonchatStatusViewHolder) viewHolder).vTextStatus.setMarqueeRepeatLimit(-1);
            ((ByonchatStatusViewHolder) viewHolder).vTextStatus.setSelected(true);
            ((ByonchatStatusViewHolder) viewHolder).vTextStatus.canScrollHorizontally(View.SCROLL_AXIS_HORIZONTAL);

            try {
                ValidateHistory(viewHolder, i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String downol = "Dokumen Tidak Terkendali Jika Diunduh dan/atau Dicetak - Uncontrolled When Downloaded And/or Printed" + "\n" +
                    "Downloaded at " + item.timestamp + " By : " + dbHelper.getColValue(UserDB.EMPLOYEE_NAME) + " --- " + dbHelper.getColValue(UserDB.EMPLOYEE_NIK) + " --- 1st Approved By : " + dbHelper.getColValue(UserDB.ATASAN_1_NAMA) + ", and  2nd Approved By : " + dbHelper.getColValue(UserDB.ATASAN_2_NAMA) + "\n" +
                    "Controlled Copy";
            if (dbHelper.getColValue(UserDB.ATASAN_2_NIK).equalsIgnoreCase("")) {
                downol = "Dokumen Tidak Terkendali Jika Diunduh dan/atau Dicetak - Uncontrolled When Downloaded And/or Printed" + "\n" +
                        "Downloaded at " + item.timestamp + " By : " + dbHelper.getColValue(UserDB.EMPLOYEE_NAME) + " --- " + dbHelper.getColValue(UserDB.EMPLOYEE_NIK) + " --- Approved By : " + dbHelper.getColValue(UserDB.ATASAN_1_NAMA) + "\n" +
                        "Controlled Copy";
            }

            String finalDownol = downol;
            ((ByonchatStatusViewHolder) viewHolder).vTextDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.w("ParkingBreak", item.url + " --> " + downloaded/*+"  -->  "+dwn*/);
                    Log.w("Parking lt prestice 1st", item.id_history + " --> " + downloaded/*+"  -->  "+dwn*/);
//                    if(downloaded){
                    if (((ByonchatStatusViewHolder) viewHolder).vTextDownload.getText().toString().equalsIgnoreCase("VIEW")) {
                        File oldFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "ByonChatDoc");
                        File oldFile = new File(oldFolder, "ISS_request_" + item.id_request + ".pdf");

                        if (!oldFile.exists()) {
                            Log.w("Apaan ini prestice", oldFile + "");
                            Toast.makeText(context, "Can't found your file", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(context, DownloadFileByonchat.class);
                            intent.putExtra("path", item.url);
                            intent.putExtra("nama_file", "request_" + item.id_request);
                            intent.putExtra("download", finalDownol);
                            intent.putExtra("approving", item.id_history);
                            context.startActivity(intent);
                        }

                    } else {
                        File oldFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "ByonChatDoc");
                        File oldFile = new File(oldFolder, "request_" + item.id_request + ".pdf");
                        if (oldFile.exists()) {
                            oldFile.delete();
                        }
                        Intent intent = new Intent(context, DownloadFileByonchat.class);
                        intent.putExtra("path", item.url);
                        intent.putExtra("nama_file", "request_" + item.id_request);
                        intent.putExtra("download", finalDownol);
                        intent.putExtra("approving", item.id_history);
                        context.startActivity(intent);
                    }
                }
            });

        }
    }

    @SuppressLint("ResourceType")
    @TargetApi(Build.VERSION_CODES.M)
    private void ValidateHistory(final RecyclerView.ViewHolder viewHolder, final int i) throws JSONException {
        Status item = itemsFiltered.get(i);

        JSONArray history = new JSONArray(item.history);
        for (int ii = 0; ii < history.length(); ii++) {
            JSONObject jsonObject = history.getJSONObject(ii);
            String id = jsonObject.getString("id");
            String id_request = jsonObject.getString("id_request");
            String bc_user_approval = jsonObject.getString("bc_user_approval");
            String nama = jsonObject.getString("nama");
            String nik = jsonObject.getString("nik");
            String status = jsonObject.getString("status");
            String ord = jsonObject.getString("ord");
            String create_at = jsonObject.getString("create_at");

            if (status.equalsIgnoreCase("2")) {
                ((ByonchatStatusViewHolder) viewHolder).vTextStatus.setText("Rejected");
            }

            if (nik.equalsIgnoreCase("download")) {
                if (status.equalsIgnoreCase("1")) {
                    ((ByonchatStatusViewHolder) viewHolder).vTextDownload.setText("VIEW");
                    ((ByonchatStatusViewHolder) viewHolder).vTextDownload.setBackgroundResource(R.drawable.new_btn_room_lv_s);
                    downloaded = true;
                } else {
                    ((ByonchatStatusViewHolder) viewHolder).vTextDownload.setText("DOWNLOAD");
                    ((ByonchatStatusViewHolder) viewHolder).vTextDownload.setBackgroundResource(R.drawable.new_btn_room_lv_g);
                    downloaded = false;
                }
            }

            if (history.length() == 1) {
                ((ByonchatStatusViewHolder) viewHolder).vTextDownload.setVisibility(View.GONE);
                ((ByonchatStatusViewHolder) viewHolder).vTextStatus.setText("Tidak Ada Atasan");
            }

            if (history.length() == 2) {
                if (ord.equalsIgnoreCase("1")) {
                    if (status.equalsIgnoreCase("1")) {
                        ((ByonchatStatusViewHolder) viewHolder).vTextDownload.setVisibility(View.VISIBLE);
                        ((ByonchatStatusViewHolder) viewHolder).vTextStatus.setText("Approved");
                    } else if (status.equalsIgnoreCase("0")) {
                        ((ByonchatStatusViewHolder) viewHolder).vTextDownload.setVisibility(View.GONE);
                        ((ByonchatStatusViewHolder) viewHolder).vTextStatus.setText("Waiting " + nama);
                    } else {
                        ((ByonchatStatusViewHolder) viewHolder).vTextDownload.setVisibility(View.GONE);
                        ((ByonchatStatusViewHolder) viewHolder).vTextStatus.setText("Rejected");
                    }
                }
            } else if (history.length() == 3) {
                if (ord.equalsIgnoreCase("2")) {
                    if (status.equalsIgnoreCase("1")) {
                        ((ByonchatStatusViewHolder) viewHolder).vTextDownload.setVisibility(View.VISIBLE);
                        ((ByonchatStatusViewHolder) viewHolder).vTextStatus.setText("Approved");
                    } else {
                        ((ByonchatStatusViewHolder) viewHolder).vTextDownload.setVisibility(View.GONE);
                    }
                }
                if (ord.equalsIgnoreCase("1")) {
                    if (status.equalsIgnoreCase("0")) {
                        num_loc = 1;
                    } else if (status.equalsIgnoreCase("1")) {
                        num_loc = 2;
                    } else {
                        num_loc = 0;
                    }
                }


                if (num_loc == 1) {
                    if (ord.equalsIgnoreCase("1")) {
                        if (status.equalsIgnoreCase("0")) {
                            ((ByonchatStatusViewHolder) viewHolder).vTextStatus.setText("Waiting " + nama);
                        }
                    }
                } else if (num_loc == 2) {
                    if (ord.equalsIgnoreCase("2")) {
                        if (status.equalsIgnoreCase("0")) {
                            ((ByonchatStatusViewHolder) viewHolder).vTextStatus.setText("Waiting " + nama);
                        }
                    }
                }
            }
        }
    }

    private void showFileImage(RecyclerView.ViewHolder viewHolder) {
        Picasso.with(context).load("https://bb.byonchat.com/bc_voucher_client/public/list_attachment/icon-pdf.png").networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(((ByonchatStatusViewHolder) viewHolder).vIconView);
    }

    private String[] getTagView() {
        List<String> tagList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            String tag = "String " + i;
            tagList.add(tag);
        }

        return tagList.toArray(new String[]{});
    }

    private void showTagView(RecyclerView.ViewHolder viewHolder) {
        String[] tags = /*getTagView()*/ null;

        if (tags != null && tags.length > 0) {
            ((ByonchatStatusViewHolder) viewHolder).vTagGroup.setTags(tags);
        }

        ((ByonchatStatusViewHolder) viewHolder).vTagGroup.setOnTagClickListener(s -> {
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
        });
    }

    public List<Status> getData() {
        return itemsFiltered;
    }

    @Override
    public int getItemViewType(int position) {
        Status item = items.get(position);
        switch (item.type) {
            case Status.TYPE_TEXT:
                return VIEWTYPE_ITEM_TEXT;
            default:
                return VIEWTYPE_ITEM_TEXT;
        }
    }

    public int getItemCount() {
        return (null != itemsFiltered ? itemsFiltered.size() : 0);
    }

    public void setItems(List<Status> items) {
        this.items = items;
        this.itemsFiltered = items;
        notifyDataSetChanged();
    }

    public List<Status> getSelectedComments() {
        List<Status> selectedContacts = new ArrayList<>();
        int size = itemsFiltered.size();
        for (int i = size - 1; i >= 0; i--) {
            if (itemsFiltered.get(i).isSelected()) {
                selectedContacts.add(itemsFiltered.get(i));
            }
        }
        return selectedContacts;
    }

    public void addOrUpdate(Status e) {
        int i = findPosition(e);
        if (i >= 0) {
            if (!e.areContentsTheSame(items.get(i))) {
                e.isSelected = items.get(i).isSelected();
                items.set(i, e);
            }
            notifyItemChanged(i);
        } else {
            add(e);
        }
    }


    public void add(Status e) {
        items.add(0, e);
        notifyDataSetChanged();
    }

    public void remove(Status e) {
        int position = findPosition(e);
        remove(position);
    }

    public int findPosition(Status e) {
        if (items == null) {
            return -1;
        }

        int size = items.size();
        for (int i = 0; i < size; i++) {
            if (items.get(i).equals(e)) {
                return i;
            }
        }

        return -1;
    }

    public void remove(int position) {
        if (position >= 0 && position < items.size()) {
            items.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clearSelectedComments() {
        int size = itemsFiltered.size();
        for (int i = size - 1; i >= 0; i--) {
            if (itemsFiltered.get(i).isSelected()) {
                itemsFiltered.get(i).isSelected = false;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    private class ArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            String charString = charSequence.toString();
            if (charString.isEmpty()) {
                mSearchText = "";
                itemsFiltered = items;
            } else {
                mSearchText = charString;
                List<Status> filteredList = new ArrayList<>();
                for (Status row : items) {
                    if (row.title.toLowerCase(Locale.getDefault()).contains(charString.toLowerCase(Locale.getDefault()))) {
                        filteredList.add(row);
                    }
                }

                itemsFiltered = filteredList;
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = itemsFiltered;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            itemsFiltered = (ArrayList<Status>) filterResults.values;
            notifyDataSetChanged();

            /*setOnItemClickListener((view, position) -> {
                if (onPreviewItemClickListener != null) {
                    Status c = (Status) getData().get(position);
                    onPreviewItemClickListener.onItemPreviewClick((Status) getData().get(position));
                }
            });

            setOnLongItemClickListener((view, position) -> {
                if (onPreviewItemClickListener != null) {
                    Status c = (Status) getData().get(position);
                    onPreviewItemClickListener.onItemPreviewClick((Status) getData().get(position));
                }
            });*/
        }
    }

    public void setOnLongItemClickListener(OnLongItemClickListener longItemClickListener) {
        this.longItemClickListener = longItemClickListener;
    }

}
