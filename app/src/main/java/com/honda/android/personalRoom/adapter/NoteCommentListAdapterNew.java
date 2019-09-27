package com.honda.android.personalRoom.adapter;

import android.annotation.TargetApi;
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
import android.widget.Toast;

import com.honda.android.R;
import com.honda.android.communication.MessengerConnectionService;
import com.honda.android.personalRoom.NoteCommentActivityNew;
import com.honda.android.personalRoom.asynctask.ProfileSaveDescription;
import com.honda.android.personalRoom.model.CommentModel;
import com.honda.android.personalRoom.viewHolder.feedItemsHolder;
import com.honda.android.provider.MessengerDatabaseHelper;
import com.honda.android.utils.MyLeadingMarginSpan2;
import com.honda.android.utils.UtilsPD;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Lukmanpryg on 5/9/2016.
 */
public class NoteCommentListAdapterNew extends RecyclerView.Adapter<feedItemsHolder> {

    MessengerDatabaseHelper messengerHelper = null;
    private LayoutInflater inflater;
    private List<CommentModel> feedItems;
    RecyclerView mRecyclerView;
    Context contxt;
    private static int leftMargin = 0;
    private static final String URL_SAVE_LIKE_COMMENTS = "https://" + MessengerConnectionService.HTTP_SERVER + "/bc_voucher_client/webservice/proses/list_note_comment_like.php";
    private static final String URL_SAVE_DISLIKE_COMMENTS = "https://" + MessengerConnectionService.HTTP_SERVER + "/bc_voucher_client/webservice/proses/list_note_comment_dislike.php";
    private static final String URL_SAVE_LOVES_PERSONAL = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/proses/comment_like.php";
    private static final String URL_SAVE_DISLIKE_PERSONAL = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/proses/comment_dislike.php";
    /*NEW*/

    /*animate*/
    private static final int TYPE_HEADER = 2;
    private static final int TYPE_ITEM = 1;
    /*animate*/
    private Context mContext;

    public NoteCommentListAdapterNew(Context context, List<CommentModel> feedItems) {
        this.feedItems = feedItems;
        this.mContext = context;
    }


    public feedItemsHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_news_timeline, null);
        feedItemsHolder mh = new feedItemsHolder(v);
        final CommentModel item = feedItems.get(i);

        return mh;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void onBindViewHolder(final feedItemsHolder fItemsHolder, int i) {
        final CommentModel item = feedItems.get(i);

        fItemsHolder.mBtNix.setVisibility(View.GONE);
        fItemsHolder.mBtLoves.setVisibility(View.GONE);
        fItemsHolder.dotA.setVisibility(View.GONE);
        fItemsHolder.dotB.setVisibility(View.GONE);

        if (item.getName2() != null) {
            fItemsHolder.mHiddenComment.setText(item.getName2() + " : " + item.getComment2());
            int jComment = Integer.parseInt(item.getJumlahComment().toString());
            if (jComment > 0) {
                fItemsHolder.mHiddenComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.getFlag()) {
                            Intent intent = new Intent(mContext, NoteCommentActivityNew.class);
                            intent.putExtra("userid", item.getUserid());
                            intent.putExtra("id_note", item.getId_note());
                            intent.putExtra("id_comment", item.getId_comment());
                            intent.putExtra("bc_user", item.getMyuserid());
                            intent.putExtra("flag", item.getFlag());
                            mContext.startActivity(intent);
                        } else {
                            Intent intent = new Intent(mContext, NoteCommentActivityNew.class);
                            intent.putExtra("userid", item.getUserid());
                            intent.putExtra("id_note", item.getId_note());
                            intent.putExtra("id_comment", item.getId_comment());
                            intent.putExtra("bc_user", item.getMyuserid());
                            intent.putExtra("id_room_tab", item.getIdRoomTab());
                            intent.putExtra("color", item.getHeaderColor());
                            mContext.startActivity(intent);
                        }
                    }
                });
            }
        } else {
            fItemsHolder.mLinearHiddenComment.setVisibility(View.GONE);
        }

        fItemsHolder.mTotalLoves.setText(item.getJumlahLove());
        fItemsHolder.mTotalComments.setText(item.getJumlahComment());

        fItemsHolder.mComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.getFlag()) {
                    Intent intent = new Intent(mContext, NoteCommentActivityNew.class);
                    intent.putExtra("userid", item.getUserid());
                    intent.putExtra("id_note", item.getId_note());
                    intent.putExtra("id_comment", item.getId_comment());
                    intent.putExtra("bc_user", item.getMyuserid());
                    intent.putExtra("flag", item.getFlag());
                    mContext.startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, NoteCommentActivityNew.class);
                    intent.putExtra("userid", item.getUserid());
                    intent.putExtra("id_note", item.getId_note());
                    intent.putExtra("id_comment", item.getId_comment());
                    intent.putExtra("bc_user", item.getMyuserid());
                    intent.putExtra("id_room_tab", item.getIdRoomTab());
                    intent.putExtra("color", item.getHeaderColor());
                    mContext.startActivity(intent);
                }
            }
        });

        Drawable dIcon = mContext.getResources().getDrawable(R.drawable.news_top_left);
        leftMargin = dIcon.getIntrinsicWidth();

        String nama;
        if (item.getProfileName().equalsIgnoreCase(null) || item.getProfileName().equalsIgnoreCase("")) {
            nama = item.getUserid();
        } else {
            nama = item.getProfileName();
        }
        SpannableString titleHeader = new SpannableString(Html.fromHtml("<b>" + nama + "</b>"));
        titleHeader.setSpan(new MyLeadingMarginSpan2(1, leftMargin), 0, titleHeader.length(), 0);
        fItemsHolder.name.setText(titleHeader);

        SpannableString update = new SpannableString(Html.fromHtml("Updates on: " + item.getTimeStamp()));
        update.setSpan(new MyLeadingMarginSpan2(1, leftMargin), 0, update.length(), 0);
        fItemsHolder.timestamp.setText(update);

        SpannableString pesan = new SpannableString(Html.fromHtml(item.getContent_comment()));
        pesan.setSpan(new MyLeadingMarginSpan2(1, leftMargin), 0, pesan.length(), 0);
        fItemsHolder.statusMsg.setText(pesan);
        fItemsHolder.mOverlayText.setText(pesan);

        fItemsHolder.mOverlayText.getViewTreeObserver().addOnGlobalLayoutListener(fItemsHolder.mGlobalLayoutListener);

        fItemsHolder.mExpandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fItemsHolder.statusMsg.setVisibility(View.VISIBLE);
                fItemsHolder.mExpandLayout.expand();
                fItemsHolder.mExpandButton.setVisibility(View.GONE);
                fItemsHolder.mOverlayText.setVisibility(View.GONE);
            }
        });

        fItemsHolder.mExpandButton.post(new Runnable() {
            @Override
            public void run() {
                if (fItemsHolder.statusMsg.getLineCount() > 3) {
                    fItemsHolder.mExpandLayout.setVisibility(View.VISIBLE);
                    fItemsHolder.mExpandButton.setVisibility(View.VISIBLE);
                } else {
                    fItemsHolder.mExpandLayout.setVisibility(View.GONE);
                    fItemsHolder.mExpandButton.setVisibility(View.GONE);
                }
            }
        });

        if (item.getFlag()) {
            if (item.getUserLike().equals("false")) {
                fItemsHolder.mBtLoves.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.getUserLike().equalsIgnoreCase("false")) {
                            new saveLikeNotesPersonal(fItemsHolder).execute(item.getMyuserid(), item.getUserid(), item.getId_comment(), item.getJumlahLove(), URL_SAVE_LOVES_PERSONAL);
                        }
                    }
                });
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    fItemsHolder.mBtLoves.setBackground(mContext.getResources().getDrawable(R.drawable.button_timeline_background));
                }

            } else if (item.getUserLike().equals("true")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    fItemsHolder.mBtLoves.setBackground(mContext.getResources().getDrawable(R.drawable.button_timeline_unfocused_pressed));
                    fItemsHolder.mBtLoves.setEnabled(false);
                }
            }

            if (item.getUserDislike().equals("false")) {
                fItemsHolder.mBtNix.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.getUserDislike().equalsIgnoreCase("false")) {
                            new saveLikeNotesPersonal(fItemsHolder).execute(item.getMyuserid(), item.getUserid(), item.getId_comment(), item.getJumlahLove(), URL_SAVE_DISLIKE_PERSONAL);
                        }
                    }
                });
            } else if (item.getUserLike().equals("true")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    fItemsHolder.mBtNix.setEnabled(false);
                }
            }
        } else {
            if (item.getUserLike().equals("false")) {
                fItemsHolder.mBtLoves.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.getUserLike().equalsIgnoreCase("false")) {
                            new saveLikeNotes(fItemsHolder).execute(item.getMyuserid(), item.getUserid(), item.getId_comment(), item.getJumlahLove(), item.getIdRoomTab(), URL_SAVE_LIKE_COMMENTS);
                        }
                    }
                });
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    fItemsHolder.mBtLoves.setBackground(mContext.getResources().getDrawable(R.drawable.button_timeline_background));
                }

            } else if (item.getUserLike().equals("true")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    fItemsHolder.mBtLoves.setBackground(mContext.getResources().getDrawable(R.drawable.button_timeline_unfocused_pressed));
                    fItemsHolder.mBtLoves.setEnabled(false);
                }
            }

            if (item.getUserDislike().equals("false")) {
                fItemsHolder.mBtNix.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.getUserDislike().equalsIgnoreCase("false")) {
                            new saveLikeNotes(fItemsHolder).execute(item.getMyuserid(), item.getUserid(), item.getId_comment(), item.getJumlahLove(), item.getIdRoomTab(), URL_SAVE_DISLIKE_COMMENTS);
                        }
                    }
                });
            } else if (item.getUserLike().equals("true")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    fItemsHolder.mBtNix.setEnabled(false);
                }
            }
        }

        if (item.getProfile_photo() != null) {
            Picasso.with(mContext).load(item.getProfile_photo())
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .into(fItemsHolder.profilePic);
        } else {
            Picasso.with(mContext).load(R.drawable.ic_no_photo)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .into(fItemsHolder.profilePic);
        }

        fItemsHolder.feedImageView.setVisibility(View.GONE);

    }

    public int getItemCount() {
        return (null != feedItems ? feedItems.size() : 0);
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
        feedItemsHolder holder;
        InputStream inputStream = null;

        public saveLikeNotes(feedItemsHolder hh) {
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
            data.put("id_comment", idNote);
            String result = profileSaveDescription.sendPostRequest(url, data);
            return result;
        }

        protected void onPostExecute(String s) {
            pDialog.dismiss();
            if (s.equals(null)) {
                Toast.makeText(mContext, "Internet Problem.", Toast.LENGTH_SHORT).show();
            } else {
                if (s.equalsIgnoreCase("1")) {
                    if (url.equalsIgnoreCase(URL_SAVE_LIKE_COMMENTS)) {
                        holder.mTotalLoves.setText(String.valueOf(Integer.valueOf(lastCount) + 1));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            holder.mBtLoves.setBackground(mContext.getResources().getDrawable(R.drawable.button_timeline_unfocused_pressed));
                        }
                        holder.mBtLoves.setEnabled(false);
                        Toast.makeText(mContext, "Like Success", Toast.LENGTH_SHORT).show();
                    } else if (url.equalsIgnoreCase(URL_SAVE_DISLIKE_COMMENTS)) {
                        holder.mBtNix.setEnabled(false);
                        Toast.makeText(mContext, "Dislike Success", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(mContext, "Please Try Again ...", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    class saveLikeNotesPersonal extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
        String url = "";
        String myUser = "";
        String userName = "";
        String idComment = "";
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
            this.idComment = params[2];
            this.lastCount = params[3];
            this.url = params[4];

            HashMap<String, String> data = new HashMap<String, String>();
            data.put("userid", myUser);
            data.put("id_comment", idComment);
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
                        ((feedItemsHolder) holder).mTotalLoves.setText(String.valueOf(Integer.valueOf(lastCount) + 1));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            ((feedItemsHolder) holder).mBtLoves.setBackground(mContext.getResources().getDrawable(R.drawable.button_timeline_unfocused_pressed));
                        }
                        ((feedItemsHolder) holder).mBtLoves.setEnabled(false);
                        Toast.makeText(mContext, "Love Success", Toast.LENGTH_SHORT).show();
                    } else if (url.equalsIgnoreCase(URL_SAVE_DISLIKE_PERSONAL)) {
                        ((feedItemsHolder) holder).mBtNix.setEnabled(false);
                        Toast.makeText(mContext, "Nix Success", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, "Please Try Again ...", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }
}
