package com.byonchat.android.personalRoom.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
import com.byonchat.android.MediaPlayCatalogActivity;
import com.byonchat.android.NewsDetailActivity;
import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.personalRoom.NoteCommentActivity;
import com.byonchat.android.personalRoom.asynctask.ProfileSaveDescription;
import com.byonchat.android.personalRoom.model.NewsFeedItem;
import com.byonchat.android.personalRoom.viewHolder.feedItemsHolderNews;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.utils.OnLoadMoreListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by lukma on 3/7/2016.
 */
public class NewsFeedListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String URL_SAVE_LOVES = "https://"+ MessengerConnectionService.HTTP_SERVER+"/bc_voucher_client/webservice/proses/list_note_like.php";
    private static final String URL_SAVE_DISLIKE = "https://"+ MessengerConnectionService.HTTP_SERVER+"/bc_voucher_client/webservice/proses/list_note_dislike.php";
    private List<NewsFeedItem> feedItems;
    private NewsFeedListAdapter adapter;
    ImageLoader imageLoader;
    Context mContext;
    private static final String TAG = NewsFeedListAdapter.class.getSimpleName();
    /**/
    private final int VIEW_REFRESH = 2;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private int visibleThreshold = 5;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private int lastVisibleItem, totalItemCount;
    BotListDB db;
    /**/

    public NewsFeedListAdapter(Activity activity, List<NewsFeedItem> feedItem, RecyclerView recyclerView) {
        if (db==null){
            db=BotListDB.getInstance(activity.getApplicationContext());
        }
        this.mContext = activity;
        feedItems = feedItem;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();

            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager
                                    .findLastVisibleItemPosition();
                            if (!loading
                                    && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                // End has been reached
                                // Do something
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                            }
                        }
                    });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return feedItems.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder fHolder;
        if (i == VIEW_ITEM) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.list_item_news, viewGroup, false);
            fHolder = new FeedItemsHolderNews(v);
        } else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.progress_item, viewGroup, false);

            fHolder = new ProgressbarHolder(v);
        }

        return fHolder;
    }



    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int i) {
        if (db==null){
            db=BotListDB.getInstance(mContext.getApplicationContext());
        }

        if (holder instanceof FeedItemsHolderNews) {
            final NewsFeedItem item =  feedItems.get(i);

            ((FeedItemsHolderNews) holder).name.setText(item.getTitle());
            ((FeedItemsHolderNews) holder).timestamp.setText("Updates on : " + item.getTimeStamp());
            ((FeedItemsHolderNews) holder).txtStatusMsg.setText(parsedMessageBody(Html.fromHtml(item.getStatus()).toString(), 100));

            ((FeedItemsHolderNews) holder).totalComments.setText(item.getJumlahComment());
            ((FeedItemsHolderNews) holder).totalLoves.setText(item.getJumlahLove());

            ((FeedItemsHolderNews) holder).btComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, NoteCommentActivity.class);
                    intent.putExtra("userid", item.getUserid());
                    intent.putExtra("id_note", item.getId());
                    intent.putExtra("bc_user", item.getMyuserid());
                    intent.putExtra("id_room_tab", item.getIdRoomTab());
                    intent.putExtra("color", item.getColorHeader());
                    mContext.startActivity(intent);
                }
            });

            if(item.getUserDislike()!=null){
                if(item.getUserDislike().equalsIgnoreCase("false")){
                    ((FeedItemsHolderNews) holder).btNix.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Cursor cursorValue = db.getSingleRoomDetailFormWithFlag(item.getId(), item.getUserid(), item.getIdRoomTab(), "value");

                            if (cursorValue.getCount() > 0) {
                                final String contentValue = cursorValue.getString(cursorValue.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT));
                                if(!contentValue.equalsIgnoreCase("")){
                                    try {
                                        JSONObject c = new JSONObject(contentValue);
                                        if (c.getString("user_dislike").equalsIgnoreCase("false")){
                                            Log.w("sudah","masuk");
                                            new saveLikeNotes(holder).execute(item.getMyuserid(),item.getUserid(),item.getId(),item.getJumlahLove(),item.getIdRoomTab(),URL_SAVE_DISLIKE);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    });
                }else if (item.getUserDislike().equals("true")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((FeedItemsHolderNews) holder).btNix.setEnabled(false);
                    }
                }
            }

            if(item.getUserDislike()!=null){
                if (item.getUserLike().equals("false")) {
                    ((FeedItemsHolderNews) holder).btLoves.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Cursor cursorValue = db.getSingleRoomDetailFormWithFlag(item.getId(), item.getUserid(), item.getIdRoomTab(), "value");

                            if (cursorValue.getCount() > 0) {
                                final String contentValue = cursorValue.getString(cursorValue.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT));
                                if(!contentValue.equalsIgnoreCase("")){
                                    try {
                                        JSONObject c = new JSONObject(contentValue);
                                        if (c.getString("user_like").equalsIgnoreCase("false")){
                                            new saveLikeNotes(holder).execute(item.getMyuserid(),item.getUserid(),item.getId(),item.getJumlahLove(),item.getIdRoomTab(),URL_SAVE_LOVES);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    });
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((FeedItemsHolderNews) holder).btLoves.setBackground(mContext.getResources().getDrawable(R.drawable.button_timeline_background));
                    }

                } else if (item.getUserLike().equals("true")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((FeedItemsHolderNews) holder).btLoves.setBackground(mContext.getResources().getDrawable(R.drawable.button_timeline_unfocused_pressed));
                        ((FeedItemsHolderNews) holder).btLoves.setEnabled(false);
                    }
                }
            }


            if (item.getImage().contains("http")) {
                Picasso.with(mContext).load(item.getImage()).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(((FeedItemsHolderNews) holder).iconView);
            } else {
                Picasso.with(mContext).load("http://img.youtube.com/vi/" + item.getImage() + "/0.jpg").networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(((FeedItemsHolderNews) holder).iconView);
            }

            if (item.getImage().contains("http")) {
                ((FeedItemsHolderNews) holder).layoutComment.setVisibility(View.VISIBLE);
            } else {
                ((FeedItemsHolderNews) holder).layoutComment.setVisibility(View.GONE);
            }

            ((FeedItemsHolderNews) holder).main_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.getImage().contains("http")) {
                        Intent intent = new Intent(mContext, NewsDetailActivity.class);
                        intent.putExtra("title", item.getTitle());
                        intent.putExtra("time", item.getTimeStamp());
                        intent.putExtra("content", item.getStatus());
                        intent.putExtra("image", item.getImage());
                        intent.putExtra("foto_file", item.getProfilePic());
                        intent.putExtra("color", item.getColorHeader());
                        intent.putExtra("totalComment", item.getJumlahComment() != null ? item.getJumlahComment() : "");
                        intent.putExtra("userDislike", item.getUserDislike() != null ? item.getUserDislike() : "");
                        intent.putExtra("userLike", item.getUserLike() != null ? item.getUserLike() : "");
                        intent.putExtra("totalLove", item.getJumlahLove() != null ? item.getJumlahLove() : "");
                        intent.putExtra("userid", item.getUserid() != null ? item.getUserid() : "");
                        intent.putExtra("id_note", item.getId() != null ? item.getId() : "");
                        intent.putExtra("bc_user", item.getMyuserid() != null ? item.getMyuserid() : "");
                        intent.putExtra("id_room_tab", item.getIdRoomTab() != null ? item.getIdRoomTab() : "");
                        intent.putExtra("coment", item.getComment2() != null ? item.getComment2() : "");
                        intent.putExtra("comentName", item.getName2() != null ? item.getName2() : "");
                        intent.putExtra("color", item.getColorHeader() != null ? item.getColorHeader() : "");
                        mContext.startActivity(intent);
                    } else {
                        Intent intent = new Intent(mContext, MediaPlayCatalogActivity.class);
                        Bundle bundle = new Bundle();
                        intent.putExtra("url", item.getImage());
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                    }
                }
            });
        } else {
            ((ProgressbarHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setLoaded() {
        loading = false;
    }

    public int getItemCount() {
        return feedItems == null ? 0 : feedItems.size();
    }

    public NewsFeedListAdapter(Activity context) {
        feedItems = new ArrayList<>();
        mContext = context;
    }

    public void setData(List<NewsFeedItem> photoList) {
        feedItems.clear();
        feedItems.addAll(photoList);
        this.notifyItemRangeInserted(0, feedItems.size() - 1);
    }

    public String parsedMessageBody(String message, int maxLen) {

        if (message.length() > maxLen) {
            message = message.substring(0, maxLen) + " ...";
        }

        String text = Html.fromHtml(message).toString();
        Pattern htmlPattern = Pattern.compile(".*\\<[^>]+>.*", Pattern.DOTALL);
        boolean isHTML = htmlPattern.matcher(message).matches();
        if (isHTML) {
            if (text.contains("<")) {
                text = Html.fromHtml(Html.fromHtml(message).toString()).toString();
            }
        } else {
            text = message;
        }

        if (text.length() > maxLen) {
            message = text.substring(0, maxLen) + " ...";
        } else {
            message = text;
        }
        return message;
    }

    public class FeedItemsHolderNews extends RecyclerView.ViewHolder {
        public LinearLayout main_content, layoutComment, btLoves, btComment,btNix;
        public TextView name, timestamp, txtStatusMsg, totalComments, totalLoves;
        public Target iconView;
        /*public TextView tvName;
        public TextView tvEmailId;*/


        public FeedItemsHolderNews(View view) {
            super(view);
            this.name = (TextView) view.findViewById(R.id.name);
            this.timestamp = (TextView) view.findViewById(R.id.timestamp);
            this.txtStatusMsg = (TextView) view.findViewById(R.id.txtStatusMsg);
            this.iconView = (Target) view.findViewById(R.id.imagePhoto);
            this.main_content = (LinearLayout) view.findViewById(R.id.main_content);
            this.layoutComment = (LinearLayout) view.findViewById(R.id.layoutComment);
            this.btLoves = (LinearLayout) view.findViewById(R.id.btLoves);
            this.btComment = (LinearLayout) view.findViewById(R.id.btComment);
            this.totalComments = (TextView) view.findViewById(R.id.totalComments);
            this.totalLoves = (TextView) view.findViewById(R.id.totalLoves);
            this.btNix = (LinearLayout) view.findViewById(R.id.btNix);
            /*tvName = (TextView) view.findViewById(R.id.tvName);
            tvEmailId = (TextView) view.findViewById(R.id.tvEmailId);*/
        }
    }

    public class ProgressbarHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressbarHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        }
    }

    public class RefreshHolder extends RecyclerView.ViewHolder{
        public ImageView refreshButton;

        public RefreshHolder(View view){
            super(view);
            refreshButton = (ImageView) view.findViewById(R.id.btn_refresh);
        }
    }

    class saveLikeNotes extends AsyncTask<String, Void, String> {
        ProgressDialog pDialog;
        ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
        String url = "";
        String myUser = "";
        String userName = "";
        String idNote = "";
        String lastCount = "";
        String roomTab = "";
        RecyclerView.ViewHolder holder;
        InputStream inputStream = null;

        public saveLikeNotes(RecyclerView.ViewHolder hh) {
            super();
            this.holder = hh;
        }

        @Override
        protected void onPreExecute() {
            pDialog = ProgressDialog.show(mContext, null,
                    "please wait...", false);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            this.myUser = params[0];
            this.userName = params[1];
            this.idNote = params[2];
            this.lastCount = params[3];
            this.roomTab = params[4];
            this.url = params[5];

            HashMap<String, String> data = new HashMap<String, String>();
            data.put("bc_user", myUser);
            data.put("id_note", idNote);
            String result = profileSaveDescription.sendPostRequest(url, data);
            return result;
        }

        protected void onPostExecute(String s) {
            pDialog.dismiss();
            if (s.equals(null)) {
                Toast.makeText(mContext, "Internet Problem.", Toast.LENGTH_SHORT).show();
            } else {
                if (s.equalsIgnoreCase("1")) {
                    Cursor cursorValue = db.getSingleRoomDetailFormWithFlag(idNote, userName, roomTab, "value");

                    if (cursorValue.getCount() > 0) {
                        final String contentValue = cursorValue.getString(cursorValue.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT));
                        if (!contentValue.equalsIgnoreCase("")) {
                            try {
                                JSONObject c = new JSONObject(contentValue);
                                if(url.equalsIgnoreCase(URL_SAVE_LOVES)){
                                    if (c.getString("user_like").equalsIgnoreCase("false")) {
                                        try {
                                            JSONObject j = DinamicRoomTaskActivity.function(c, "amount_of_like", String.valueOf(Integer.valueOf(c.getString("amount_of_like")) + 1));
                                            JSONObject own = DinamicRoomTaskActivity.function(j, "user_like", "true");
                                            RoomsDetail orderModel = new RoomsDetail(idNote, roomTab, userName, String.valueOf(own), "", "", "value");
                                            db.updateDetailRoomWithFlagContent(orderModel);
                                            ((FeedItemsHolderNews) holder).totalLoves.setText(String.valueOf(Integer.valueOf(lastCount) + 1));
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                                ((FeedItemsHolderNews) holder).btLoves.setBackground(mContext.getResources().getDrawable(R.drawable.button_timeline_unfocused_pressed));
                                            }
                                            ((FeedItemsHolderNews) holder).btLoves.setEnabled(false);
                                            Toast.makeText(mContext, "Like Success", Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }else if(url.equalsIgnoreCase(URL_SAVE_DISLIKE)){
                                    if (c.getString("user_dislike").equalsIgnoreCase("false")) {
                                        try {
                                            JSONObject j = DinamicRoomTaskActivity.function(c, "amount_of_dislike", String.valueOf(Integer.valueOf(c.getString("amount_of_dislike")) + 1));
                                            JSONObject own = DinamicRoomTaskActivity.function(j, "user_dislike", "true");
                                            RoomsDetail orderModel = new RoomsDetail(idNote, roomTab, userName, String.valueOf(own), "", "", "value");
                                            db.updateDetailRoomWithFlagContent(orderModel);
                                            ((FeedItemsHolderNews) holder).btNix.setEnabled(false);
                                            Toast.makeText(mContext, "Dislike Success", Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                } else {
                    Toast.makeText(mContext, "Please Try Again ...", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

}