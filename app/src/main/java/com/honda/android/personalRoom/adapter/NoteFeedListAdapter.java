package com.honda.android.personalRoom.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.honda.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
import com.honda.android.R;
import com.honda.android.ZoomImageViewActivity;
import com.honda.android.communication.MessengerConnectionService;
import com.honda.android.personalRoom.FragmentMyNote;
import com.honda.android.personalRoom.FullScreenDetailPicture;
import com.honda.android.personalRoom.NoteCommentActivity;
import com.honda.android.personalRoom.NoteFeedImageView;
import com.honda.android.personalRoom.asynctask.ProfileSaveDescription;
import com.honda.android.personalRoom.model.NoteFeedItem;
import com.honda.android.personalRoom.viewHolder.feedItemsHolder;
import com.honda.android.provider.BotListDB;
import com.honda.android.provider.RoomsDetail;
import com.honda.android.utils.CustomVolleyRequestQueue;
import com.honda.android.utils.MyLeadingMarginSpan2;
import com.honda.android.utils.OnLoadMoreListener;
import com.honda.android.utils.UtilsPD;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by lukma on 3/7/2016.
 */
public class NoteFeedListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<NoteFeedItem> feedItems;
    private static final String URL_SAVE_LOVES = "https://" + MessengerConnectionService.HTTP_SERVER + "/bc_voucher_client/webservice/proses/list_note_like.php";
    private static final String URL_SAVE_DISLIKE = "https://" + MessengerConnectionService.HTTP_SERVER + "/bc_voucher_client/webservice/proses/list_note_dislike.php";
    private static int leftMargin = 0;
    ImageLoader imageLoader;
    Context mContext;
    private static final String TAG = NoteFeedListAdapter.class.getSimpleName();
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private int visibleThreshold = 5;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private int lastVisibleItem, totalItemCount;
    private static final String URL_SAVE_LOVES_PERSONAL = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/proses/note_like.php";
    private static final String URL_SAVE_DISLIKE_PERSONAL = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/proses/note_dislike.php";
    private BotListDB botListDB;

    public NoteFeedListAdapter(Context context, List<NoteFeedItem> feeditems, RecyclerView recyclerView) {
        if (botListDB==null){
            botListDB = BotListDB.getInstance(context);
        }
        mContext = context;
        feedItems = feeditems;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();

            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();

                            if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
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
        RecyclerView.ViewHolder holder;
        if (i == VIEW_ITEM) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_news_timeline, viewGroup, false);
            holder = new FeedItemsHolderNote(v);
        } else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.progress_item, viewGroup, false);
            holder = new ProgressbarHolder(v);
        }

        return holder;
    }


    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int i) {
        if (holder instanceof FeedItemsHolderNote) {
            final NoteFeedItem item = feedItems.get(i);
            if (item.getName2() != null) {
                ((FeedItemsHolderNote) holder).mHiddenComment.setText(item.getName2() + " : " + item.getComment2());
                int jComment = Integer.parseInt(item.getJumlahComment().toString());
                if (jComment > 0) {
                    ((FeedItemsHolderNote) holder).mHiddenComment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (item.getFlag()) {
                                Intent intent = new Intent(mContext, NoteCommentActivity.class);
                                intent.putExtra("userid", item.getUserid());
                                intent.putExtra("id_note", item.getId());
                                intent.putExtra("bc_user", item.getMyuserid());
                                intent.putExtra("flag", item.getFlag());
                                mContext.startActivity(intent);
                            } else {
                                Intent intent = new Intent(mContext, NoteCommentActivity.class);
                                intent.putExtra("userid", item.getUserid());
                                intent.putExtra("id_note", item.getId());
                                intent.putExtra("bc_user", item.getMyuserid());
                                intent.putExtra("id_room_tab", item.getIdRoomTab());
                                intent.putExtra("color", item.getColorHeader());
                                mContext.startActivity(intent);
                            }
                        }
                    });
                }
            } else {
                ((FeedItemsHolderNote) holder).mLinearHiddenComment.setVisibility(View.GONE);
            }

            ((FeedItemsHolderNote) holder).mTotalLoves.setText(item.getJumlahLove());
            ((FeedItemsHolderNote) holder).mTotalComments.setText(item.getJumlahComment());

            ((FeedItemsHolderNote) holder).mComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.getFlag()) {
                        Intent intent = new Intent(mContext, NoteCommentActivity.class);
                        intent.putExtra("userid", item.getUserid());
                        intent.putExtra("id_note", item.getId());
                        intent.putExtra("bc_user", item.getMyuserid());
                        intent.putExtra("flag", item.getFlag());
                        mContext.startActivity(intent);
                    } else {
                        Intent intent = new Intent(mContext, NoteCommentActivity.class);
                        intent.putExtra("userid", item.getUserid());
                        intent.putExtra("id_note", item.getId());
                        intent.putExtra("bc_user", item.getMyuserid());
                        intent.putExtra("id_room_tab", item.getIdRoomTab());
                        intent.putExtra("color", item.getColorHeader());
                        mContext.startActivity(intent);
                    }
                }
            });

            Drawable dIcon = mContext.getResources().getDrawable(R.drawable.news_top_left);
            leftMargin = dIcon.getIntrinsicWidth();

            String nama;
            if (item.getName().equalsIgnoreCase(null) || item.getName().equalsIgnoreCase("")) {
                nama = item.getUserid();
            } else {
                nama = item.getName();
            }
            SpannableString titleHeader = new SpannableString(Html.fromHtml("<b>" + nama + "</b>"));
            titleHeader.setSpan(new MyLeadingMarginSpan2(1, leftMargin), 0, titleHeader.length(), 0);
            ((FeedItemsHolderNote) holder).name.setText(titleHeader);

            SpannableString update = new SpannableString(Html.fromHtml("Updates on: " + item.getTimeStamp()));
            update.setSpan(new MyLeadingMarginSpan2(1, leftMargin), 0, update.length(), 0);
            ((FeedItemsHolderNote) holder).timestamp.setText(update);

            SpannableString pesan = new SpannableString(Html.fromHtml(item.getStatus()));
            pesan.setSpan(new MyLeadingMarginSpan2(1, leftMargin), 0, pesan.length(), 0);
            ((FeedItemsHolderNote) holder).statusMsg.setText(pesan);
            ((FeedItemsHolderNote) holder).mOverlayText.setText(pesan);

            ((FeedItemsHolderNote) holder).mOverlayText.getViewTreeObserver().addOnGlobalLayoutListener(((FeedItemsHolderNote) holder).mGlobalLayoutListener);

            ((FeedItemsHolderNote) holder).mExpandButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((FeedItemsHolderNote) holder).statusMsg.setVisibility(View.VISIBLE);
                    ((FeedItemsHolderNote) holder).mExpandLayout.expand();
                    ((FeedItemsHolderNote) holder).mExpandButton.setVisibility(View.GONE);
                    ((FeedItemsHolderNote) holder).mOverlayText.setVisibility(View.GONE);
                }
            });

            ((FeedItemsHolderNote) holder).mExpandButton.post(new Runnable() {
                @Override
                public void run() {
                    if (((FeedItemsHolderNote) holder).statusMsg.getLineCount() > 3) {
                        ((FeedItemsHolderNote) holder).mExpandLayout.setVisibility(View.VISIBLE);
                        ((FeedItemsHolderNote) holder).mExpandButton.setVisibility(View.VISIBLE);
                    } else {
                        ((FeedItemsHolderNote) holder).mExpandLayout.setVisibility(View.GONE);
                        ((FeedItemsHolderNote) holder).mExpandButton.setVisibility(View.GONE);
                    }
                }
            });


            if (item.getFlag()) {
                if (item.getUserDislike().equalsIgnoreCase("false")) {
                    ((FeedItemsHolderNote) holder).mBtNix.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new saveLikeNotesPersonal(holder).execute(item.getMyuserid(), item.getUserid(), item.getId(), item.getJumlahLove(), URL_SAVE_DISLIKE_PERSONAL);
                        }
                    });
                } else if (item.getUserDislike().equals("true")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((FeedItemsHolderNote) holder).mBtNix.setEnabled(false);
                    }
                }
            } else {
                if (item.getUserDislike().equalsIgnoreCase("false")) {
                    ((FeedItemsHolderNote) holder).mBtNix.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Cursor cursorValue = botListDB.getSingleRoomDetailFormWithFlag(item.getId(), item.getUserid(), item.getIdRoomTab(), "value");

                            if (cursorValue.getCount() > 0) {
                                final String contentValue = cursorValue.getString(cursorValue.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT));
                                if (!contentValue.equalsIgnoreCase("")) {
                                    try {
                                        JSONObject c = new JSONObject(contentValue);
                                        if (c.getString("user_dislike").equalsIgnoreCase("false")) {
                                            new saveLikeNotes(holder).execute(item.getMyuserid(), item.getUserid(), item.getId(), item.getJumlahLove(), item.getIdRoomTab(), URL_SAVE_DISLIKE);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    });
                } else if (item.getUserDislike().equals("true")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((FeedItemsHolderNote) holder).mBtNix.setEnabled(false);
                    }
                }
            }

            if (item.getFlag()) {
                if (item.getUserLike().equals("false")) {
                    ((FeedItemsHolderNote) holder).mBtLoves.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new saveLikeNotesPersonal(holder).execute(item.getMyuserid(), item.getUserid(), item.getId(), item.getJumlahLove(), URL_SAVE_LOVES_PERSONAL);
                        }
                    });
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((FeedItemsHolderNote) holder).mBtLoves.setBackground(mContext.getResources().getDrawable(R.drawable.button_timeline_background));
                    }

                } else if (item.getUserLike().equals("true")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((FeedItemsHolderNote) holder).mBtLoves.setBackground(mContext.getResources().getDrawable(R.drawable.button_timeline_unfocused_pressed));
                    }
                }
            } else {
                if (item.getUserLike().equals("false")) {
                    ((FeedItemsHolderNote) holder).mBtLoves.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Cursor cursorValue = botListDB.getSingleRoomDetailFormWithFlag(item.getId(), item.getUserid(), item.getIdRoomTab(), "value");

                            if (cursorValue.getCount() > 0) {
                                final String contentValue = cursorValue.getString(cursorValue.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT));
                                if (!contentValue.equalsIgnoreCase("")) {
                                    try {
                                        JSONObject c = new JSONObject(contentValue);
                                        if (c.getString("user_like").equalsIgnoreCase("false")) {
                                            new saveLikeNotes(holder).execute(item.getMyuserid(), item.getUserid(), item.getId(), item.getJumlahLove(), item.getIdRoomTab(), URL_SAVE_LOVES);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    });
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((FeedItemsHolderNote) holder).mBtLoves.setBackground(mContext.getResources().getDrawable(R.drawable.button_timeline_background));
                    }

                } else if (item.getUserLike().equals("true")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((FeedItemsHolderNote) holder).mBtLoves.setBackground(mContext.getResources().getDrawable(R.drawable.button_timeline_unfocused_pressed));
                        ((FeedItemsHolderNote) holder).mBtLoves.setEnabled(false);
                    }
                }
            }

            if (item.getProfilePic() != null) {
                Picasso.with(mContext).load(item.getProfilePic())
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .into(((FeedItemsHolderNote) holder).profilePic);
            } else {
                Picasso.with(mContext).load(R.drawable.ic_no_photo)
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .into(((FeedItemsHolderNote) holder).profilePic);
            }

            if (imageLoader == null)
                imageLoader = CustomVolleyRequestQueue.getInstance(mContext.getApplicationContext()).getImageLoader();

            if (item.getImage() != null) {
                ((FeedItemsHolderNote) holder).feedImageView.setImageUrl(item.getImage(), imageLoader);
                ((FeedItemsHolderNote) holder).feedImageView.setVisibility(View.VISIBLE);
                ((FeedItemsHolderNote) holder).feedImageView
                        .setResponseObserver(new NoteFeedImageView.ResponseObserver() {
                            @Override
                            public void onError() {
                            }

                            @Override
                            public void onSuccess() {
                            }
                        });
                ((FeedItemsHolderNote) holder).feedImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ZoomImageViewActivity.class);
                        intent.putExtra(ZoomImageViewActivity.KEY_FILE, item.getImage());
                        mContext.startActivity(intent);
                    }
                });

                if(item.getFlag()){
                    ((FeedItemsHolderNote) holder).feedImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, FullScreenDetailPicture.class);
                            intent.putExtra(FullScreenDetailPicture.PHOTO, item.getImage());
                            intent.putExtra(FullScreenDetailPicture.JABBER_ID, item.getUserid());
                            mContext.startActivity(intent);
                        }
                    });
                }
            } else {
                ((FeedItemsHolderNote) holder).feedImageView.setVisibility(View.GONE);
            }
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

    public NoteFeedListAdapter(Activity context) {
        feedItems = new ArrayList<>();
        mContext = context;
    }

    public void setData(List<NoteFeedItem> photoList) {
        feedItems.clear();
        feedItems.addAll(photoList);
        this.notifyItemRangeInserted(0, feedItems.size() - 1);
    }


    public class FeedItemsHolderNote extends RecyclerView.ViewHolder {
        public TextView mExpandButton;
        public ExpandableRelativeLayout mExpandLayout;
        public TextView mOverlayText;
        public TextView name, timestamp, statusMsg, mTotalLoves, mTotalComments, mHiddenComment, mLabelLoves;
        public NoteFeedImageView feedImageView;
        public LinearLayout mComments, mLinearHiddenComment, mLoading, mBtNix, mBtLoves;
        public Target profilePic;
        public ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener;

        public FeedItemsHolderNote(View view) {
            super(view);
            this.name = (TextView) view.findViewById(R.id.name);
            this.timestamp = (TextView) view.findViewById(R.id.timestamp);
            this.statusMsg = (TextView) view.findViewById(R.id.txtStatusMsg);
            this.profilePic = (Target) view.findViewById(R.id.profilePic);
            this.mTotalLoves = (TextView) view.findViewById(R.id.totalLoves);
            this.mTotalComments = (TextView) view.findViewById(R.id.totalComments);
            this.mHiddenComment = (TextView) view.findViewById(R.id.hiddenComment);
            this.feedImageView = (NoteFeedImageView) view.findViewById(R.id.feedImage1);
            this.mComments = (LinearLayout) view.findViewById(R.id.btComment);
            this.mLinearHiddenComment = (LinearLayout) view.findViewById(R.id.LinearHiddenComment);
            this.mLoading = (LinearLayout) view.findViewById(R.id.LinearLoading);
            this.mLabelLoves = (TextView) view.findViewById(R.id.labelLoves);
            this.mBtNix = (LinearLayout) view.findViewById(R.id.btNix);
            this.mBtLoves = (LinearLayout) view.findViewById(R.id.btLoves);

            mExpandButton = (TextView) view.findViewById(R.id.expandButton);

            mExpandLayout = (ExpandableRelativeLayout) view.findViewById(R.id.expandableLayout);
            mOverlayText = (TextView) view.findViewById(R.id.overlayText);
            mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mExpandLayout.move(mOverlayText.getHeight(), 0, null);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        mOverlayText.getViewTreeObserver().removeGlobalOnLayoutListener(mGlobalLayoutListener);
                    } else {
                        mOverlayText.getViewTreeObserver().removeOnGlobalLayoutListener(mGlobalLayoutListener);
                    }
                }
            };
        }
    }

    public class ProgressbarHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressbarHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        }
    }

    class saveLikeNotesPersonal extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
        String url = "";
        String myUser = "";
        String userName = "";
        String idNote = "";
        String lastCount = "";
        RecyclerView.ViewHolder holder;
        InputStream inputStream = null;

        public saveLikeNotesPersonal(RecyclerView.ViewHolder hh) {
            super();
            this.holder = hh;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog == null) {
                progressDialog = UtilsPD.createProgressDialog(mContext);
                progressDialog.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            this.myUser = params[0];
            this.userName = params[1];
            this.idNote = params[2];
            this.lastCount = params[3];
            this.url = params[4];

            HashMap<String, String> data = new HashMap<String, String>();
            data.put("userid", myUser);
            data.put("id_note", idNote);
            String result = profileSaveDescription.sendPostRequest(url, data);
            return result;
        }

        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            if (s.equals(null)) {
                Toast.makeText(mContext, "Check your internet connection", Toast.LENGTH_SHORT).show();
            } else {
                if (s.equalsIgnoreCase("1")) {
                    if (url.equalsIgnoreCase(URL_SAVE_LOVES_PERSONAL)) {
                        ((FeedItemsHolderNote) holder).mTotalLoves.setText(String.valueOf(Integer.valueOf(lastCount) + 1));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            ((FeedItemsHolderNote) holder).mBtLoves.setBackground(mContext.getResources().getDrawable(R.drawable.button_timeline_unfocused_pressed));
                        }
                        ((FeedItemsHolderNote) holder).mBtLoves.setEnabled(false);
                        Toast.makeText(mContext, "love Success", Toast.LENGTH_SHORT).show();
                    } else if (url.equalsIgnoreCase(URL_SAVE_DISLIKE_PERSONAL)) {
                        ((FeedItemsHolderNote) holder).mBtNix.setEnabled(false);
                        Toast.makeText(mContext, "Nix Success", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, "Please Try Again ...", Toast.LENGTH_SHORT).show();
                }
            }

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
                    Cursor cursorValue = botListDB.getSingleRoomDetailFormWithFlag(idNote, userName, roomTab, "value");

                    if (cursorValue.getCount() > 0) {
                        final String contentValue = cursorValue.getString(cursorValue.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT));
                        if (!contentValue.equalsIgnoreCase("")) {
                            try {
                                JSONObject c = new JSONObject(contentValue);
                                if (url.equalsIgnoreCase(URL_SAVE_LOVES)) {
                                    if (c.getString("user_like").equalsIgnoreCase("false")) {
                                        try {
                                            JSONObject j = DinamicRoomTaskActivity.function(c, "amount_of_like", String.valueOf(Integer.valueOf(c.getString("amount_of_like")) + 1));
                                            JSONObject own = DinamicRoomTaskActivity.function(j, "user_like", "true");
                                            RoomsDetail orderModel = new RoomsDetail(idNote, roomTab, userName, String.valueOf(own), "", "", "value");
                                            botListDB.updateDetailRoomWithFlagContent(orderModel);
                                            ((FeedItemsHolderNote) holder).mTotalLoves.setText(String.valueOf(Integer.valueOf(lastCount) + 1));
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                                ((FeedItemsHolderNote) holder).mBtLoves.setBackground(mContext.getResources().getDrawable(R.drawable.button_timeline_unfocused_pressed));
                                            }
                                            ((FeedItemsHolderNote) holder).mBtLoves.setEnabled(false);
                                            Toast.makeText(mContext, "Like Success", Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else if (url.equalsIgnoreCase(URL_SAVE_DISLIKE)) {
                                    if (c.getString("user_dislike").equalsIgnoreCase("false")) {
                                        try {
                                            JSONObject j = DinamicRoomTaskActivity.function(c, "amount_of_dislike", String.valueOf(Integer.valueOf(c.getString("amount_of_dislike")) + 1));
                                            JSONObject own = DinamicRoomTaskActivity.function(j, "user_dislike", "true");
                                            RoomsDetail orderModel = new RoomsDetail(idNote, roomTab, userName, String.valueOf(own), "", "", "value");
                                            botListDB.updateDetailRoomWithFlagContent(orderModel);
                                            ((FeedItemsHolderNote) holder).mBtNix.setEnabled(false);
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

