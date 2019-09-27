package com.honda.android.personalRoom.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.honda.android.R;
import com.honda.android.communication.MessengerConnectionService;
import com.honda.android.personalRoom.FullScreenDetailPicture;
import com.honda.android.personalRoom.NoteCommentActivity;
import com.honda.android.personalRoom.NoteFeedImageView;
import com.honda.android.personalRoom.asynctask.ProfileSaveDescription;
import com.honda.android.personalRoom.model.NoteFeedItem;
import com.honda.android.utils.CustomVolleyRequestQueue;
import com.honda.android.utils.MyLeadingMarginSpan2;
import com.honda.android.utils.UtilsPD;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lukma on 3/7/2016.
 */
/*NEW*/
public class NoteFeedListAdapterMine extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<NoteFeedItem> feedItems;
    private NoteFeedListAdapter adapter;
    private static final String URL_LIST_VIEW_COMMENT = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/view/comment.php";
    private static final String URL_SAVE_LOVES_PERSONAL = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/proses/note_like.php";
    private static final String URL_SAVE_DISLIKE_PERSONAL = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/proses/note_dislike.php";
    private static final String URL_LIKE_NOTE = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/proses/list_note_like.php";
    private String URL_LIST_VIEW_NOTE = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/view/my_note.php";
    private static int leftMargin = 0;
    ImageLoader imageLoader;
    private final int VIEW_ITEM = 1;
    private final int VIEW_BUTTON = 0;

    Context mContext;
    private static final String TAG = NoteFeedListAdapter.class.getSimpleName();

    public NoteFeedListAdapterMine(Activity context, ArrayList<NoteFeedItem> data) {
        this.feedItems = data;
        this.mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        return feedItems.get(position) != null ? VIEW_ITEM : VIEW_BUTTON;
    }

    public void setData(List<NoteFeedItem> photoList) {
        feedItems.clear();
        feedItems.addAll(photoList);
        this.notifyItemRangeInserted(0, feedItems.size() - 1);
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
//        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_news_timeline, null);
        RecyclerView.ViewHolder holder;
//        holder = new MyItemHolder(v);

        if (i == VIEW_ITEM) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.layout_news_timeline, viewGroup, false);
            holder = new MyItemHolder(v);
        } else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.button_note, viewGroup, false);
            holder = new ButtonUploadHolder(v);
        }
        return holder;
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int i) {
        if (holder instanceof MyItemHolder) {
            final NoteFeedItem item = feedItems.get(i);

            if (item.getName2() != null) {
                String nama;
                if (item.getName2().equalsIgnoreCase(null) || item.getName2().equalsIgnoreCase("")) {
                    nama = item.getUserid();
                } else {
                    nama = item.getName2();
                }
                ((MyItemHolder) holder).mHiddenComment.setText(nama + " : " + item.getComment2());
                int jComment = Integer.parseInt(item.getJumlahComment().toString());
                if (jComment > 0) {
                    ((MyItemHolder) holder).mHiddenComment.setOnClickListener(new View.OnClickListener() {
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
                ((MyItemHolder) holder).mLinearHiddenComment.setVisibility(View.GONE);
            }

            ((MyItemHolder) holder).mTotalLoves.setText(item.getJumlahLove());
            ((MyItemHolder) holder).mTotalComments.setText(item.getJumlahComment());

            ((MyItemHolder) holder).mComments.setOnClickListener(new View.OnClickListener() {
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
            ((MyItemHolder) holder).name.setText(titleHeader);

            SpannableString update = new SpannableString(Html.fromHtml("Updates on: " + item.getTimeStamp()));
            update.setSpan(new MyLeadingMarginSpan2(1, leftMargin), 0, update.length(), 0);
            ((MyItemHolder) holder).timestamp.setText(update);

            SpannableString pesan = new SpannableString(Html.fromHtml(item.getStatus()));
            pesan.setSpan(new MyLeadingMarginSpan2(1, leftMargin), 0, pesan.length(), 0);
            ((MyItemHolder) holder).statusMsg.setText(pesan);
            ((MyItemHolder) holder).mOverlayText.setText(pesan);

            ((MyItemHolder) holder).mOverlayText.getViewTreeObserver().addOnGlobalLayoutListener(((MyItemHolder) holder).mGlobalLayoutListener);

            ((MyItemHolder) holder).mExpandButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MyItemHolder) holder).statusMsg.setVisibility(View.VISIBLE);
                    ((MyItemHolder) holder).mExpandLayout.expand();
                    ((MyItemHolder) holder).mExpandButton.setVisibility(View.GONE);
                    ((MyItemHolder) holder).mOverlayText.setVisibility(View.GONE);
                }
            });

            ((MyItemHolder) holder).mExpandButton.post(new Runnable() {
                @Override
                public void run() {
                    if (((MyItemHolder) holder).statusMsg.getLineCount() > 3) {
                        ((MyItemHolder) holder).mExpandLayout.setVisibility(View.VISIBLE);
                        ((MyItemHolder) holder).mExpandButton.setVisibility(View.VISIBLE);
                    } else {
                        ((MyItemHolder) holder).mExpandLayout.setVisibility(View.GONE);
                        ((MyItemHolder) holder).mExpandButton.setVisibility(View.GONE);
                    }
                }
            });

            if (item.getUserLike().equals("false")) {
                ((MyItemHolder) holder).mLoves.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                    adapter = new NoteFeedListAdapterMine((Activity) mContext);
//                    adapter.setData(feedItems);
                        new saveLikeNotesPersonal(holder).execute(item.getMyuserid(), item.getUserid(), item.getId(), item.getJumlahLove(), URL_SAVE_LOVES_PERSONAL);
//                        adapter.notifyDataSetChanged();
                    }
                });
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ((MyItemHolder) holder).mBtLoves.setBackground(mContext.getResources().getDrawable(R.drawable.button_timeline_background));
                }

            } else if (item.getUserLike().equals("true")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ((MyItemHolder) holder).mBtLoves.setBackground(mContext.getResources().getDrawable(R.drawable.button_timeline_unfocused_pressed));
                }
            }

            if (item.getProfilePic() != null) {
                Picasso.with(mContext).load(item.getProfilePic()).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(((MyItemHolder) holder).profilePic);
            } else {
                Picasso.with(mContext).load(R.drawable.ic_no_photo).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(((MyItemHolder) holder).profilePic);
            }

            if (item.getUserDislike().equalsIgnoreCase("false")) {
                ((MyItemHolder) holder).mBtNix.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new saveLikeNotesPersonal(holder).execute(item.getMyuserid(), item.getUserid(), item.getId(), item.getJumlahLove(), URL_SAVE_DISLIKE_PERSONAL);
                    }
                });
            } else if (item.getUserDislike().equals("true")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ((MyItemHolder) holder).mBtNix.setEnabled(false);
                }
            }

            if (imageLoader == null)
                imageLoader = CustomVolleyRequestQueue.getInstance(mContext.getApplicationContext()).getImageLoader();

            if (item.getImage() != null) {
                ((MyItemHolder) holder).feedImageView.setImageUrl(item.getImage(), imageLoader);
                ((MyItemHolder) holder).feedImageView.setVisibility(View.VISIBLE);
                ((MyItemHolder) holder).feedImageView
                        .setResponseObserver(new NoteFeedImageView.ResponseObserver() {
                            @Override
                            public void onError() {
                            }

                            @Override
                            public void onSuccess() {
                            }
                        });


                if(item.getFlag()){
                    ((MyItemHolder) holder).feedImageView.setOnClickListener(new View.OnClickListener() {
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
                ((MyItemHolder) holder).feedImageView.setVisibility(View.GONE);
            }
        } else {
//            Picasso.with(mContext)
//                    .load(R.drawable.ic_cmr)
//                    .noFade()
//                    .placeholder(R.drawable.ic_cmr)
//                    .into(((ButtonUploadHolder) holder).mImg);
        }
    }

    public int getItemCount() {
        return (null != feedItems ? feedItems.size() : 0);
    }

    public class MyItemHolder extends RecyclerView.ViewHolder {
        public TextView mExpandButton;
        public ExpandableRelativeLayout mExpandLayout;
        public TextView mOverlayText;
        public TextView name, timestamp, statusMsg, mTotalLoves, mTotalComments, mHiddenComment, mLabelLoves;
        public NoteFeedImageView feedImageView;
        public LinearLayout mLoves, mComments, mLinearHiddenComment, mLoading, mBtNix, mBtLoves;
        public Target profilePic;
        public ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener;

        public MyItemHolder(View view) {
            super(view);
            this.name = (TextView) view.findViewById(R.id.name);
            this.timestamp = (TextView) view.findViewById(R.id.timestamp);
            this.statusMsg = (TextView) view.findViewById(R.id.txtStatusMsg);
            this.profilePic = (Target) view.findViewById(R.id.profilePic);
            this.mTotalLoves = (TextView) view.findViewById(R.id.totalLoves);
            this.mTotalComments = (TextView) view.findViewById(R.id.totalComments);
            this.mHiddenComment = (TextView) view.findViewById(R.id.hiddenComment);
            this.feedImageView = (NoteFeedImageView) view.findViewById(R.id.feedImage1);
            this.mLoves = (LinearLayout) view.findViewById(R.id.btLoves);
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

    public static class ButtonUploadHolder extends RecyclerView.ViewHolder {

        public ButtonUploadHolder(View itemView) {
            super(itemView);

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
                        ((MyItemHolder) holder).mTotalLoves.setText(String.valueOf(Integer.valueOf(lastCount) + 1));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            ((MyItemHolder) holder).mBtLoves.setBackground(mContext.getResources().getDrawable(R.drawable.button_timeline_unfocused_pressed));
                        }
                        ((MyItemHolder) holder).mBtLoves.setEnabled(false);
                        Toast.makeText(mContext, "Love Success", Toast.LENGTH_SHORT).show();
                    } else if (url.equalsIgnoreCase(URL_SAVE_DISLIKE_PERSONAL)) {
                        ((MyItemHolder) holder).mBtNix.setEnabled(false);
                        Toast.makeText(mContext, "Nix Success", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, "Please Try Again ...", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }
}