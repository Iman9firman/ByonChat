package com.byonchat.android.personalRoom.adapter;


import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.util.SortedList;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HeaderViewListAdapter;
import android.widget.Toast;

import com.byonchat.android.Manhera.Manhera;
import com.byonchat.android.NoteCommentActivityV2;
import com.byonchat.android.R;
import com.byonchat.android.ZoomImageViewActivity;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.personalRoom.asynctask.ProfileSaveDescription;
import com.byonchat.android.personalRoom.model.CommentModel;
import com.byonchat.android.personalRoom.model.NotesPhoto;
import com.byonchat.android.personalRoom.viewHolder.AttBeforeAfterViewHolder;
import com.byonchat.android.personalRoom.viewHolder.AttMultipleViewHolder;
import com.byonchat.android.personalRoom.viewHolder.AttSingleViewHolder;
import com.byonchat.android.personalRoom.viewHolder.LoaderViewHolder;
import com.byonchat.android.personalRoom.viewHolder.TextViewHolder;
import com.byonchat.android.personalRoom.viewHolder.feedItemsHolder;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.utils.MyLeadingMarginSpan2;
import com.byonchat.android.utils.UtilsPD;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.byonchat.android.helpers.Constants.EXTRA_PARENT;

/**
 * Created by Lukmanpryg on 5/9/2016.
 */
public class NoteCommentFollowUpListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_TEXT = 1;
    private static final int TYPE_ATT_SINGLE = 2;
    private static final int TYPE_ATT_MULTIPLE = 3;
    private static final int TYPE_ATT_BEFORE_AFTER = 4;
    private static final int TYPE_HEADER = 5;
    private static final int TYPE_LOADER = 6;

    MessengerDatabaseHelper messengerHelper = null;
    private LayoutInflater inflater;
    private List<CommentModel> feedItems;
    private ArrayList<CommentModel> parentItems;
    private String id_task;
    RecyclerView mRecyclerView;
    Context contxt;
    private ArrayList<NotesPhoto> notesPhotos;
    private PhotosAdapter photosAdapter;
    private LinearLayoutManager mLayoutManager;
    private static int leftMargin = 0;
    private String url = "";
    private static final String URL_SAVE_LIKE_COMMENTS = "https://" + MessengerConnectionService.HTTP_SERVER + "/bc_voucher_client/webservice/proses/list_note_comment_like.php";
    private static final String URL_SAVE_DISLIKE_COMMENTS = "https://" + MessengerConnectionService.HTTP_SERVER + "/bc_voucher_client/webservice/proses/list_note_comment_dislike.php";
    private static final String URL_SAVE_LOVES_PERSONAL = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/proses/comment_like.php";
    private static final String URL_SAVE_DISLIKE_PERSONAL = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/proses/comment_dislike.php";

    private Context mContext;
    protected boolean showLoader;
    private OnButtonClick mButtonClickListener;
    protected SortedList<CommentModel> data;

    public void setOnButtonClickListener(OnButtonClick listener) {
        mButtonClickListener = listener;
    }

    public interface OnButtonClick {
        void onButtonClick(int position);
    }

    public NoteCommentFollowUpListAdapter(Context context, List<CommentModel> feedItems, String id_task) {
        this.feedItems = feedItems;
        this.mContext = context;
        this.id_task = id_task;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_ATT_BEFORE_AFTER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_att_before_after, parent, false);
                return new AttBeforeAfterViewHolder(view);
            case TYPE_ATT_SINGLE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_att_single, parent, false);
                return new AttSingleViewHolder(view);
            case TYPE_ATT_MULTIPLE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_att_multiple, parent, false);
                return new AttMultipleViewHolder(view);
            case TYPE_HEADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_follow_up_timeline, parent, false);
                return new RecyclerHeaderViewHolder(view);
            case TYPE_LOADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loader_item_layout, parent, false);
                return new LoaderViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text, parent, false);
                return new TextViewHolder(view);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof LoaderViewHolder) {
            if (showLoader) {
                ((LoaderViewHolder) viewHolder).mProgressBar.setVisibility(View.VISIBLE);
            } else {
                ((LoaderViewHolder) viewHolder).mProgressBar.setVisibility(View.GONE);
            }

            return;
        }

        final CommentModel item = feedItems.get(i);
        setParentItems(item);

        if (viewHolder instanceof RecyclerHeaderViewHolder) {
            if (!item.getPhotoBefore().equalsIgnoreCase("null")) {
                ((RecyclerHeaderViewHolder) viewHolder).vFrameBeforeAfter.setVisibility(View.VISIBLE);

                Manhera.getInstance().get()
                        .load(item.getPhotoBefore())
                        .dontAnimate()
                        .into(((RecyclerHeaderViewHolder) viewHolder).vPhotoBefore);

                Manhera.getInstance().get()
                        .load(item.getPhotoAfter())
                        .dontAnimate()
                        .into(((RecyclerHeaderViewHolder) viewHolder).vPhotoAfter);


                ((RecyclerHeaderViewHolder) viewHolder).vPhotoBefore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!item.getPhotoBefore().equalsIgnoreCase("")) {
                            Intent intent = new Intent(mContext, ZoomImageViewActivity.class);
                            intent.putExtra(ZoomImageViewActivity.KEY_FILE, item.getPhotoBefore());
                            mContext.startActivity(intent);
                        }
                    }
                });

                ((RecyclerHeaderViewHolder) viewHolder).vPhotoAfter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!item.getPhotoAfter().equalsIgnoreCase("")) {
                            Intent intent = new Intent(mContext, ZoomImageViewActivity.class);
                            intent.putExtra(ZoomImageViewActivity.KEY_FILE, item.getPhotoAfter());
                            mContext.startActivity(intent);
                        }
                    }
                });

            } else if (!item.getPhotos().equalsIgnoreCase("[]")) {
                try {
                    JSONArray jPhotos = new JSONArray(item.getPhotos());
                    if (jPhotos.length() == 1) {
                        try {
                            JSONArray json = new JSONArray(item.getPhotos());
                            if (json.length() == 1) {
                                ((RecyclerHeaderViewHolder) viewHolder).vImgPreview.setVisibility(View.VISIBLE);
                                JSONObject jData = json.getJSONObject(0);
                                Picasso.with(mContext).load(jData.getString("photo")).into(((RecyclerHeaderViewHolder) viewHolder).vImgPreview);
                            } else {
                                ((RecyclerHeaderViewHolder) viewHolder).vImgPreview.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (jPhotos.length() > 1) {
                        ((RecyclerHeaderViewHolder) viewHolder).vImgPreview.setVisibility(View.GONE);
                        notesPhotos = new ArrayList<>();
                        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
                        ((RecyclerHeaderViewHolder) viewHolder).vRvPhotos.setLayoutManager(mLayoutManager);
                        ((RecyclerHeaderViewHolder) viewHolder).vRvPhotos.setItemAnimator(new DefaultItemAnimator());

                        try {
                            JSONArray json = new JSONArray(item.getPhotos());
                            if (json.length() > 1) {

                                for (int j = 0; j < json.length(); j++) {
                                    JSONObject jData = json.getJSONObject(j);
                                    File file = new File(jData.getString("photo"));
                                    NotesPhoto phot = new NotesPhoto(file, "", jData.getString("photo"));
                                    notesPhotos.add(phot);
                                }

                                photosAdapter = new PhotosAdapter(mContext, notesPhotos);
                                ((RecyclerHeaderViewHolder) viewHolder).vRvPhotos.setAdapter(photosAdapter);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (jPhotos.length() == 0) {
                        item.setType(CommentModel.TYPE_TEXT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                ((RecyclerHeaderViewHolder) viewHolder).vFrameBeforeAfter.setVisibility(View.GONE);
            }

            ((RecyclerHeaderViewHolder) viewHolder).mLinearHiddenComment.setVisibility(View.GONE);

            ((RecyclerHeaderViewHolder) viewHolder).mTotalLoves.setText(item.getJumlahLove());
            ((RecyclerHeaderViewHolder) viewHolder).mTotalComments.setText(item.getJumlahComment());

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
            ((RecyclerHeaderViewHolder) viewHolder).name.setText(titleHeader);

            SpannableString update = new SpannableString(Html.fromHtml("Updates on: " + item.getTimeStamp()));
            update.setSpan(new MyLeadingMarginSpan2(1, leftMargin), 0, update.length(), 0);
            ((RecyclerHeaderViewHolder) viewHolder).timestamp.setText(update);

            SpannableString pesan = new SpannableString(Html.fromHtml(item.getContent_comment()));
            pesan.setSpan(new MyLeadingMarginSpan2(1, leftMargin), 0, pesan.length(), 0);
            ((RecyclerHeaderViewHolder) viewHolder).statusMsg.setText(pesan);
            ((RecyclerHeaderViewHolder) viewHolder).mOverlayText.setText(pesan);

            ((RecyclerHeaderViewHolder) viewHolder).mOverlayText.getViewTreeObserver().addOnGlobalLayoutListener(((RecyclerHeaderViewHolder) viewHolder).mGlobalLayoutListener);

            ((RecyclerHeaderViewHolder) viewHolder).mExpandButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((RecyclerHeaderViewHolder) viewHolder).statusMsg.setVisibility(View.VISIBLE);
                    ((RecyclerHeaderViewHolder) viewHolder).mExpandLayout.expand();
                    ((RecyclerHeaderViewHolder) viewHolder).mExpandButton.setVisibility(View.GONE);
                    ((RecyclerHeaderViewHolder) viewHolder).mOverlayText.setVisibility(View.GONE);
                }
            });

            ((RecyclerHeaderViewHolder) viewHolder).mExpandButton.post(new Runnable() {
                @Override
                public void run() {
                    if (((RecyclerHeaderViewHolder) viewHolder).statusMsg.getLineCount() > 3) {
                        ((RecyclerHeaderViewHolder) viewHolder).mExpandLayout.setVisibility(View.VISIBLE);
                        ((RecyclerHeaderViewHolder) viewHolder).mExpandButton.setVisibility(View.VISIBLE);
                    } else {
                        ((RecyclerHeaderViewHolder) viewHolder).mExpandLayout.setVisibility(View.GONE);
                        ((RecyclerHeaderViewHolder) viewHolder).mExpandButton.setVisibility(View.GONE);
                    }
                }
            });

            if (item.getProfile_photo() != null) {
                Picasso.with(mContext).load(item.getProfile_photo())
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .into(((RecyclerHeaderViewHolder) viewHolder).profilePic);
            } else {
                Picasso.with(mContext).load(R.drawable.ic_no_photo)
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .into(((RecyclerHeaderViewHolder) viewHolder).profilePic);
            }

            ((RecyclerHeaderViewHolder) viewHolder).feedImageView.setVisibility(View.GONE);
        } else if (viewHolder instanceof AttBeforeAfterViewHolder) {
            if (!item.getPhotoBefore().equalsIgnoreCase("null")) {
                ((AttBeforeAfterViewHolder) viewHolder).vFrameBeforeAfter.setVisibility(View.VISIBLE);

                Manhera.getInstance().get()
                        .load(item.getPhotoBefore())
                        .placeholder(R.drawable.no_image)
                        .dontAnimate()
                        .into(((AttBeforeAfterViewHolder) viewHolder).vPhotoBefore);

                Manhera.getInstance().get()
                        .load(item.getPhotoAfter())
                        .placeholder(R.drawable.no_image)
                        .dontAnimate()
                        .into(((AttBeforeAfterViewHolder) viewHolder).vPhotoAfter);


                ((AttBeforeAfterViewHolder) viewHolder).vPhotoBefore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!item.getPhotoBefore().equalsIgnoreCase("")) {
                            Intent intent = new Intent(mContext, ZoomImageViewActivity.class);
                            intent.putExtra(ZoomImageViewActivity.KEY_FILE, item.getPhotoBefore());
                            mContext.startActivity(intent);
                        }
                    }
                });

                ((AttBeforeAfterViewHolder) viewHolder).vPhotoAfter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!item.getPhotoAfter().equalsIgnoreCase("")) {
                            Intent intent = new Intent(mContext, ZoomImageViewActivity.class);
                            intent.putExtra(ZoomImageViewActivity.KEY_FILE, item.getPhotoAfter());
                            mContext.startActivity(intent);
                        }
                    }
                });

            } else {
                ((AttBeforeAfterViewHolder) viewHolder).vFrameBeforeAfter.setVisibility(View.GONE);
            }

            ((AttBeforeAfterViewHolder) viewHolder).mBtNix.setVisibility(View.GONE);
            ((AttBeforeAfterViewHolder) viewHolder).mBtLoves.setVisibility(View.GONE);
            ((AttBeforeAfterViewHolder) viewHolder).dotA.setVisibility(View.GONE);
            ((AttBeforeAfterViewHolder) viewHolder).dotB.setVisibility(View.GONE);

            if (item.getName2() != null) {
                ((AttBeforeAfterViewHolder) viewHolder).mHiddenComment.setText(item.getName2() + " : " + item.getComment2());
                int jComment = Integer.parseInt(item.getJumlahComment().toString());
                if (jComment > 0) {
                    ((AttBeforeAfterViewHolder) viewHolder).bind(mContext, i, mButtonClickListener);
//                    ((AttBeforeAfterViewHolder) viewHolder).mHiddenComment.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (item.getFlag()) {
//                                Intent intent = new Intent(mContext, NoteCommentActivityV2.class);
//                                intent.putExtra("userid", item.getUserid());
//                                intent.putExtra("id_note", item.getId_note());
//                                intent.putExtra("id_comment", item.getId_comment());
//                                intent.putExtra("bc_user", item.getMyuserid());
//                                intent.putExtra("flag", item.getFlag());
//                                mContext.startActivity(intent);
//                            } else {
//                                Intent intent = new Intent(mContext, NoteCommentActivityV2.class);
//                                intent.putExtra("userid", item.getUserid());
//                                intent.putExtra("id_note", item.getId_note());
//                                intent.putExtra("id_comment", item.getId_comment());
//                                intent.putExtra("id_task", id_task);
//                                intent.putExtra("bc_user", item.getMyuserid());
//                                intent.putExtra("id_room_tab", item.getIdRoomTab());
//                                intent.putExtra("color", item.getHeaderColor());
//                                intent.putExtra("flag", item.getFlag());
//                                intent.putParcelableArrayListExtra(EXTRA_PARENT, parentItems);
//                                mContext.startActivity(intent);
//                            }
//                        }
//                    });
                }
            } else {
                ((AttBeforeAfterViewHolder) viewHolder).mLinearHiddenComment.setVisibility(View.GONE);
            }

            ((AttBeforeAfterViewHolder) viewHolder).mTotalLoves.setText(item.getJumlahLove());
            ((AttBeforeAfterViewHolder) viewHolder).mTotalComments.setText(item.getJumlahComment());

            ((AttBeforeAfterViewHolder) viewHolder).bind(mContext, i, mButtonClickListener);
//            ((AttBeforeAfterViewHolder) viewHolder).mComments.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (item.getFlag()) {
//                        Intent intent = new Intent(mContext, NoteCommentActivityV2.class);
//                        intent.putExtra("userid", item.getUserid());
//                        intent.putExtra("id_note", item.getId_note());
//                        intent.putExtra("id_comment", item.getId_comment());
//                        intent.putExtra("bc_user", item.getMyuserid());
//                        intent.putExtra("flag", item.getFlag());
//                        mContext.startActivity(intent);
//                    } else {
//                        Intent intent = new Intent(mContext, NoteCommentActivityV2.class);
//                        intent.putExtra("userid", item.getUserid());
//                        intent.putExtra("id_note", item.getId_note());
//                        intent.putExtra("id_comment", item.getId_comment());
//                        intent.putExtra("id_task", id_task);
//                        intent.putExtra("bc_user", item.getMyuserid());
//                        intent.putExtra("id_room_tab", item.getIdRoomTab());
//                        intent.putExtra("color", item.getHeaderColor());
//                        intent.putExtra("flag", item.getFlag());
//                        intent.putParcelableArrayListExtra(EXTRA_PARENT, parentItems);
//                        mContext.startActivity(intent);
//                    }
//                }
//            });

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
            ((AttBeforeAfterViewHolder) viewHolder).name.setText(titleHeader);

            SpannableString update = new SpannableString(Html.fromHtml("Updates on: " + item.getTimeStamp()));
            update.setSpan(new MyLeadingMarginSpan2(1, leftMargin), 0, update.length(), 0);
            ((AttBeforeAfterViewHolder) viewHolder).timestamp.setText(update);

            String content = "";
            if (!item.getContent_comment().equalsIgnoreCase("null"))
                content = item.getContent_comment();
            SpannableString pesan = new SpannableString(Html.fromHtml(content));
            pesan.setSpan(new MyLeadingMarginSpan2(1, leftMargin), 0, pesan.length(), 0);
            ((AttBeforeAfterViewHolder) viewHolder).statusMsg.setText(pesan);
            ((AttBeforeAfterViewHolder) viewHolder).mOverlayText.setText(pesan);

            ((AttBeforeAfterViewHolder) viewHolder).mOverlayText.getViewTreeObserver().addOnGlobalLayoutListener(((AttBeforeAfterViewHolder) viewHolder).mGlobalLayoutListener);

            ((AttBeforeAfterViewHolder) viewHolder).mExpandButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((AttBeforeAfterViewHolder) viewHolder).statusMsg.setVisibility(View.VISIBLE);
                    ((AttBeforeAfterViewHolder) viewHolder).mExpandLayout.expand();
                    ((AttBeforeAfterViewHolder) viewHolder).mExpandButton.setVisibility(View.GONE);
                    ((AttBeforeAfterViewHolder) viewHolder).mOverlayText.setVisibility(View.GONE);
                }
            });

            ((AttBeforeAfterViewHolder) viewHolder).mExpandButton.post(new Runnable() {
                @Override
                public void run() {
                    if (((AttBeforeAfterViewHolder) viewHolder).statusMsg.getLineCount() > 3) {
                        ((AttBeforeAfterViewHolder) viewHolder).mExpandLayout.setVisibility(View.VISIBLE);
                        ((AttBeforeAfterViewHolder) viewHolder).mExpandButton.setVisibility(View.VISIBLE);
                    } else {
                        ((AttBeforeAfterViewHolder) viewHolder).mExpandLayout.setVisibility(View.GONE);
                        ((AttBeforeAfterViewHolder) viewHolder).mExpandButton.setVisibility(View.GONE);
                    }
                }
            });

            if (item.getFlag()) {
                if (item.getUserLike().equals("false")) {
                    ((AttBeforeAfterViewHolder) viewHolder).mBtLoves.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (item.getUserLike().equalsIgnoreCase("false")) {
                                new saveLikeNotesPersonal(((AttBeforeAfterViewHolder) viewHolder)).execute(item.getMyuserid(), item.getUserid(), item.getId_comment(), item.getJumlahLove(), URL_SAVE_LOVES_PERSONAL);
                            }
                        }
                    });
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((AttBeforeAfterViewHolder) viewHolder).mBtLoves.setBackground(mContext.getResources().getDrawable(R.drawable.button_timeline_background));
                    }

                } else if (item.getUserLike().equals("true")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((AttBeforeAfterViewHolder) viewHolder).mBtLoves.setBackground(mContext.getResources().getDrawable(R.drawable.button_timeline_unfocused_pressed));
                        ((AttBeforeAfterViewHolder) viewHolder).mBtLoves.setEnabled(false);
                    }
                }

                if (item.getUserDislike().equals("false")) {
                    ((AttBeforeAfterViewHolder) viewHolder).mBtNix.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (item.getUserDislike().equalsIgnoreCase("false")) {
                                new saveLikeNotesPersonal(((AttBeforeAfterViewHolder) viewHolder)).execute(item.getMyuserid(), item.getUserid(), item.getId_comment(), item.getJumlahLove(), URL_SAVE_DISLIKE_PERSONAL);
                            }
                        }
                    });
                } else if (item.getUserLike().equals("true")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((AttBeforeAfterViewHolder) viewHolder).mBtNix.setEnabled(false);
                    }
                }
            } else {
                if (item.getUserLike().equals("false")) {
                    ((AttBeforeAfterViewHolder) viewHolder).mBtLoves.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (item.getUserLike().equalsIgnoreCase("false")) {
//                            new saveLikeNotes(fItemsHolder).execute(item.getMyuserid(), item.getUserid(), item.getId_comment(), item.getJumlahLove(), item.getIdRoomTab(), URL_SAVE_LIKE_COMMENTS);
                            }
                        }
                    });
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((AttBeforeAfterViewHolder) viewHolder).mBtLoves.setBackground(mContext.getResources().getDrawable(R.drawable.button_timeline_background));
                    }

                } else if (item.getUserLike().equals("true")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((AttBeforeAfterViewHolder) viewHolder).mBtLoves.setBackground(mContext.getResources().getDrawable(R.drawable.button_timeline_unfocused_pressed));
                        ((AttBeforeAfterViewHolder) viewHolder).mBtLoves.setEnabled(false);
                    }
                }

                if (item.getUserDislike().equals("false")) {
                    ((AttBeforeAfterViewHolder) viewHolder).mBtNix.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (item.getUserDislike().equalsIgnoreCase("false")) {
//                            new saveLikeNotes(fItemsHolder).execute(item.getMyuserid(), item.getUserid(), item.getId_comment(), item.getJumlahLove(), item.getIdRoomTab(), URL_SAVE_DISLIKE_COMMENTS);
                            }
                        }
                    });
                } else if (item.getUserLike().equals("true")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((AttBeforeAfterViewHolder) viewHolder).mBtNix.setEnabled(false);
                    }
                }
            }

            if (item.getProfile_photo() != null) {
                Picasso.with(mContext).load(item.getProfile_photo())
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .into(((AttBeforeAfterViewHolder) viewHolder).profilePic);
            } else {
                Picasso.with(mContext).load(R.drawable.ic_no_photo)
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .into(((AttBeforeAfterViewHolder) viewHolder).profilePic);
            }

            ((AttBeforeAfterViewHolder) viewHolder).feedImageView.setVisibility(View.GONE);
        } else if (viewHolder instanceof AttMultipleViewHolder) {
            notesPhotos = new ArrayList<>();
            mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            ((AttMultipleViewHolder) viewHolder).vRvPhotos.setLayoutManager(mLayoutManager);
            ((AttMultipleViewHolder) viewHolder).vRvPhotos.setItemAnimator(new DefaultItemAnimator());

            ((AttMultipleViewHolder) viewHolder).vRvPhotos.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                /*if (mLayoutManager.findLastVisibleItemPosition() == mChannelListAdapter.getItemCount() - 1) {
                    loadNextChannelList();
                }*/
                }
            });
            try {
                JSONArray json = new JSONArray(item.getPhotos());
                if (json.length() > 1) {

                    for (int j = 0; j < json.length(); j++) {
                        JSONObject jData = json.getJSONObject(j);
                        File file = new File(jData.getString("photo"));
                        NotesPhoto phot = new NotesPhoto(file, "", jData.getString("photo"));
                        notesPhotos.add(phot);
                    }

                    Log.w("adaptergajalan1", notesPhotos.size() + "");
                    photosAdapter = new PhotosAdapter(mContext, notesPhotos);
                    ((AttMultipleViewHolder) viewHolder).vRvPhotos.setAdapter(photosAdapter);

                    photosAdapter.setOnItemClickListener(new PhotosAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(NotesPhoto notesPhoto) {
                            ((AttMultipleViewHolder) viewHolder).vImgPreview.setVisibility(View.VISIBLE);
                            url = notesPhoto.getUrl();
                            Picasso.with(mContext).load(notesPhoto.getUrl()).into(((AttMultipleViewHolder) viewHolder).vImgPreview);
                        }
                    });
                } else if (json.length() == 1) {
                    ((AttMultipleViewHolder) viewHolder).vRvPhotos.setVisibility(View.GONE);
                    ((AttMultipleViewHolder) viewHolder).vImgPreview.setVisibility(View.VISIBLE);
                    JSONObject jData = json.getJSONObject(0);
                    Picasso.with(mContext).load(jData.getString("photo")).into(((AttMultipleViewHolder) viewHolder).vImgPreview);
                } else {
                    ((AttMultipleViewHolder) viewHolder).vImgPreview.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ((AttMultipleViewHolder) viewHolder).vImgPreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!url.equalsIgnoreCase("")) {
                        Intent intent = new Intent(mContext, ZoomImageViewActivity.class);
                        intent.putExtra(ZoomImageViewActivity.KEY_FILE, url);
                        mContext.startActivity(intent);
                    }
                }
            });

            ((AttMultipleViewHolder) viewHolder).mBtNix.setVisibility(View.GONE);
            ((AttMultipleViewHolder) viewHolder).mBtLoves.setVisibility(View.GONE);
            ((AttMultipleViewHolder) viewHolder).dotA.setVisibility(View.GONE);
            ((AttMultipleViewHolder) viewHolder).dotB.setVisibility(View.GONE);

            if (item.getName2() != null) {
                ((AttMultipleViewHolder) viewHolder).mHiddenComment.setText(item.getName2() + " : " + item.getComment2());
                int jComment = Integer.parseInt(item.getJumlahComment().toString());
                if (jComment > 0) {
                    ((AttMultipleViewHolder) viewHolder).bind(mContext, i, mButtonClickListener);
//                    ((AttMultipleViewHolder) viewHolder).mHiddenComment.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (item.getFlag()) {
//                                Intent intent = new Intent(mContext, NoteCommentActivityV2.class);
//                                intent.putExtra("userid", item.getUserid());
//                                intent.putExtra("id_note", item.getId_note());
//                                intent.putExtra("id_comment", item.getId_comment());
//                                intent.putExtra("bc_user", item.getMyuserid());
//                                intent.putExtra("flag", item.getFlag());
//                                mContext.startActivity(intent);
//                            } else {
//                                Intent intent = new Intent(mContext, NoteCommentActivityV2.class);
//                                intent.putExtra("userid", item.getUserid());
//                                intent.putExtra("id_note", item.getId_note());
//                                intent.putExtra("id_comment", item.getId_comment());
//                                intent.putExtra("bc_user", item.getMyuserid());
//                                intent.putExtra("id_room_tab", item.getIdRoomTab());
//                                intent.putExtra("id_task", id_task);
//                                intent.putExtra("color", item.getHeaderColor());
//                                intent.putExtra("flag", item.getFlag());
//                                intent.putParcelableArrayListExtra(EXTRA_PARENT, parentItems);
//                                mContext.startActivity(intent);
//                            }
//                        }
//                    });
                }
            } else {
                ((AttMultipleViewHolder) viewHolder).mLinearHiddenComment.setVisibility(View.GONE);
            }

            ((AttMultipleViewHolder) viewHolder).mTotalLoves.setText(item.getJumlahLove());
            ((AttMultipleViewHolder) viewHolder).mTotalComments.setText(item.getJumlahComment());

            ((AttMultipleViewHolder) viewHolder).bind(mContext, i, mButtonClickListener);
//            ((AttMultipleViewHolder) viewHolder).mComments.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (item.getFlag()) {
//                        Intent intent = new Intent(mContext, NoteCommentActivityV2.class);
//                        intent.putExtra("userid", item.getUserid());
//                        intent.putExtra("id_note", item.getId_note());
//                        intent.putExtra("id_comment", item.getId_comment());
//                        intent.putExtra("bc_user", item.getMyuserid());
//                        intent.putExtra("flag", item.getFlag());
//                        mContext.startActivity(intent);
//                    } else {
//                        Intent intent = new Intent(mContext, NoteCommentActivityV2.class);
//                        intent.putExtra("userid", item.getUserid());
//                        intent.putExtra("id_note", item.getId_note());
//                        intent.putExtra("id_comment", item.getId_comment());
//                        intent.putExtra("id_task", id_task);
//                        intent.putExtra("bc_user", item.getMyuserid());
//                        intent.putExtra("id_room_tab", item.getIdRoomTab());
//                        intent.putExtra("color", item.getHeaderColor());
//                        intent.putExtra("flag", item.getFlag());
//                        intent.putParcelableArrayListExtra(EXTRA_PARENT, parentItems);
//                        mContext.startActivity(intent);
//                    }
//                }
//            });

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
            ((AttMultipleViewHolder) viewHolder).name.setText(titleHeader);

            SpannableString update = new SpannableString(Html.fromHtml("Updates on: " + item.getTimeStamp()));
            update.setSpan(new MyLeadingMarginSpan2(1, leftMargin), 0, update.length(), 0);
            ((AttMultipleViewHolder) viewHolder).timestamp.setText(update);

            String content = "";
            if (!item.getContent_comment().equalsIgnoreCase("null"))
                content = item.getContent_comment();
            SpannableString pesan = new SpannableString(Html.fromHtml(content));
            pesan.setSpan(new MyLeadingMarginSpan2(1, leftMargin), 0, pesan.length(), 0);
            ((AttMultipleViewHolder) viewHolder).statusMsg.setText(pesan);
            ((AttMultipleViewHolder) viewHolder).mOverlayText.setText(pesan);

            ((AttMultipleViewHolder) viewHolder).mOverlayText.getViewTreeObserver().addOnGlobalLayoutListener(((AttMultipleViewHolder) viewHolder).mGlobalLayoutListener);

            ((AttMultipleViewHolder) viewHolder).mExpandButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((AttMultipleViewHolder) viewHolder).statusMsg.setVisibility(View.VISIBLE);
                    ((AttMultipleViewHolder) viewHolder).mExpandLayout.expand();
                    ((AttMultipleViewHolder) viewHolder).mExpandButton.setVisibility(View.GONE);
                    ((AttMultipleViewHolder) viewHolder).mOverlayText.setVisibility(View.GONE);
                }
            });

            ((AttMultipleViewHolder) viewHolder).mExpandButton.post(new Runnable() {
                @Override
                public void run() {
                    if (((AttMultipleViewHolder) viewHolder).statusMsg.getLineCount() > 3) {
                        ((AttMultipleViewHolder) viewHolder).mExpandLayout.setVisibility(View.VISIBLE);
                        ((AttMultipleViewHolder) viewHolder).mExpandButton.setVisibility(View.VISIBLE);
                    } else {
                        ((AttMultipleViewHolder) viewHolder).mExpandLayout.setVisibility(View.GONE);
                        ((AttMultipleViewHolder) viewHolder).mExpandButton.setVisibility(View.GONE);
                    }
                }
            });

            if (item.getFlag()) {
                if (item.getUserLike().equals("false")) {
                    ((AttMultipleViewHolder) viewHolder).mBtLoves.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (item.getUserLike().equalsIgnoreCase("false")) {
                                new saveLikeNotesPersonal(((AttMultipleViewHolder) viewHolder)).execute(item.getMyuserid(), item.getUserid(), item.getId_comment(), item.getJumlahLove(), URL_SAVE_LOVES_PERSONAL);
                            }
                        }
                    });
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((AttMultipleViewHolder) viewHolder).mBtLoves.setBackground(mContext.getResources().getDrawable(R.drawable.button_timeline_background));
                    }

                } else if (item.getUserLike().equals("true")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((AttMultipleViewHolder) viewHolder).mBtLoves.setBackground(mContext.getResources().getDrawable(R.drawable.button_timeline_unfocused_pressed));
                        ((AttMultipleViewHolder) viewHolder).mBtLoves.setEnabled(false);
                    }
                }

                if (item.getUserDislike().equals("false")) {
                    ((AttMultipleViewHolder) viewHolder).mBtNix.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (item.getUserDislike().equalsIgnoreCase("false")) {
                                new saveLikeNotesPersonal(((AttMultipleViewHolder) viewHolder)).execute(item.getMyuserid(), item.getUserid(), item.getId_comment(), item.getJumlahLove(), URL_SAVE_DISLIKE_PERSONAL);
                            }
                        }
                    });
                } else if (item.getUserLike().equals("true")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((AttMultipleViewHolder) viewHolder).mBtNix.setEnabled(false);
                    }
                }
            } else {
                if (item.getUserLike().equals("false")) {
                    ((AttMultipleViewHolder) viewHolder).mBtLoves.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (item.getUserLike().equalsIgnoreCase("false")) {
//                            new saveLikeNotes(fItemsHolder).execute(item.getMyuserid(), item.getUserid(), item.getId_comment(), item.getJumlahLove(), item.getIdRoomTab(), URL_SAVE_LIKE_COMMENTS);
                            }
                        }
                    });
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((AttMultipleViewHolder) viewHolder).mBtLoves.setBackground(mContext.getResources().getDrawable(R.drawable.button_timeline_background));
                    }

                } else if (item.getUserLike().equals("true")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((AttMultipleViewHolder) viewHolder).mBtLoves.setBackground(mContext.getResources().getDrawable(R.drawable.button_timeline_unfocused_pressed));
                        ((AttMultipleViewHolder) viewHolder).mBtLoves.setEnabled(false);
                    }
                }

                if (item.getUserDislike().equals("false")) {
                    ((AttMultipleViewHolder) viewHolder).mBtNix.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (item.getUserDislike().equalsIgnoreCase("false")) {
//                            new saveLikeNotes(fItemsHolder).execute(item.getMyuserid(), item.getUserid(), item.getId_comment(), item.getJumlahLove(), item.getIdRoomTab(), URL_SAVE_DISLIKE_COMMENTS);
                            }
                        }
                    });
                } else if (item.getUserLike().equals("true")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((AttMultipleViewHolder) viewHolder).mBtNix.setEnabled(false);
                    }
                }
            }

            if (item.getProfile_photo() != null) {
                Picasso.with(mContext).load(item.getProfile_photo())
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .into(((AttMultipleViewHolder) viewHolder).profilePic);
            } else {
                Picasso.with(mContext).load(R.drawable.ic_no_photo)
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .into(((AttMultipleViewHolder) viewHolder).profilePic);
            }

            ((AttMultipleViewHolder) viewHolder).feedImageView.setVisibility(View.GONE);
        } else if (viewHolder instanceof AttSingleViewHolder) {
            try {
                JSONArray json = new JSONArray(item.getPhotos());
                if (json.length() == 1) {
                    ((AttSingleViewHolder) viewHolder).vImgPreview.setVisibility(View.VISIBLE);
                    JSONObject jData = json.getJSONObject(0);
                    Log.w("inisingleatt", jData.getString("photo"));
                    url = jData.getString("photo");
//                    Picasso.with(mContext).load(jData.getString("photo")).into(((AttSingleViewHolder) viewHolder).vImgPreview);
                    Manhera.getInstance().get()
                            .load(jData.getString("photo"))
                            .placeholder(R.drawable.no_image)
                            .dontAnimate()
                            .into(((AttSingleViewHolder) viewHolder).vImgPreview);
                } else {
                    ((AttSingleViewHolder) viewHolder).vImgPreview.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ((AttSingleViewHolder) viewHolder).vImgPreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!url.equalsIgnoreCase("")) {
                        Intent intent = new Intent(mContext, ZoomImageViewActivity.class);
                        intent.putExtra(ZoomImageViewActivity.KEY_FILE, url);
                        mContext.startActivity(intent);
                    }
                }
            });

            ((AttSingleViewHolder) viewHolder).mBtNix.setVisibility(View.GONE);
            ((AttSingleViewHolder) viewHolder).mBtLoves.setVisibility(View.GONE);
            ((AttSingleViewHolder) viewHolder).dotA.setVisibility(View.GONE);
            ((AttSingleViewHolder) viewHolder).dotB.setVisibility(View.GONE);

            if (item.getName2() != null) {
                ((AttSingleViewHolder) viewHolder).mHiddenComment.setText(item.getName2() + " : " + item.getComment2());
                int jComment = Integer.parseInt(item.getJumlahComment().toString());
                if (jComment > 0) {
                    ((AttSingleViewHolder) viewHolder).bind(mContext, i, mButtonClickListener);
//                    ((AttSingleViewHolder) viewHolder).mHiddenComment.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (item.getFlag()) {
//                                Intent intent = new Intent(mContext, NoteCommentActivityV2.class);
//                                intent.putExtra("userid", item.getUserid());
//                                intent.putExtra("id_note", item.getId_note());
//                                intent.putExtra("id_comment", item.getId_comment());
//                                intent.putExtra("bc_user", item.getMyuserid());
//                                intent.putExtra("flag", item.getFlag());
//                                mContext.startActivity(intent);
//                            } else {
//                                Intent intent = new Intent(mContext, NoteCommentActivityV2.class);
//                                intent.putExtra("userid", item.getUserid());
//                                intent.putExtra("id_note", item.getId_note());
//                                intent.putExtra("id_task", id_task);
//                                intent.putExtra("id_comment", item.getId_comment());
//                                intent.putExtra("bc_user", item.getMyuserid());
//                                intent.putExtra("id_room_tab", item.getIdRoomTab());
//                                intent.putExtra("color", item.getHeaderColor());
//                                intent.putExtra("flag", item.getFlag());
//                                intent.putParcelableArrayListExtra(EXTRA_PARENT, parentItems);
//                                mContext.startActivity(intent);
//                            }
//                        }
//                    });
                }
            } else {
                ((AttSingleViewHolder) viewHolder).mLinearHiddenComment.setVisibility(View.GONE);
            }

            ((AttSingleViewHolder) viewHolder).mTotalLoves.setText(item.getJumlahLove());
            ((AttSingleViewHolder) viewHolder).mTotalComments.setText(item.getJumlahComment());

            ((AttSingleViewHolder) viewHolder).bind(mContext, i, mButtonClickListener);
//            ((AttSingleViewHolder) viewHolder).mComments.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (item.getFlag()) {
//                        Intent intent = new Intent(mContext, NoteCommentActivityV2.class);
//                        intent.putExtra("userid", item.getUserid());
//                        intent.putExtra("id_note", item.getId_note());
//                        intent.putExtra("id_comment", item.getId_comment());
//                        intent.putExtra("bc_user", item.getMyuserid());
//                        intent.putExtra("flag", item.getFlag());
//                        mContext.startActivity(intent);
//                    } else {
//                        Intent intent = new Intent(mContext, NoteCommentActivityV2.class);
//                        intent.putExtra("userid", item.getUserid());
//                        intent.putExtra("id_note", item.getId_note());
//                        intent.putExtra("id_comment", item.getId_comment());
//                        intent.putExtra("bc_user", item.getMyuserid());
//                        intent.putExtra("id_task", id_task);
//                        intent.putExtra("id_room_tab", item.getIdRoomTab());
//                        intent.putExtra("color", item.getHeaderColor());
//                        intent.putExtra("flag", item.getFlag());
//                        intent.putParcelableArrayListExtra(EXTRA_PARENT, parentItems);
//                        mContext.startActivity(intent);
//                    }
//                }
//            });

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
            ((AttSingleViewHolder) viewHolder).name.setText(titleHeader);

            SpannableString update = new SpannableString(Html.fromHtml("Updates on: " + item.getTimeStamp()));
            update.setSpan(new MyLeadingMarginSpan2(1, leftMargin), 0, update.length(), 0);
            ((AttSingleViewHolder) viewHolder).timestamp.setText(update);

            String content = "";
            if (!item.getContent_comment().equalsIgnoreCase("null"))
                content = item.getContent_comment();
            SpannableString pesan = new SpannableString(Html.fromHtml(content));
            pesan.setSpan(new MyLeadingMarginSpan2(1, leftMargin), 0, pesan.length(), 0);
            ((AttSingleViewHolder) viewHolder).statusMsg.setText(pesan);
            ((AttSingleViewHolder) viewHolder).mOverlayText.setText(pesan);

            ((AttSingleViewHolder) viewHolder).mOverlayText.getViewTreeObserver().addOnGlobalLayoutListener(((AttSingleViewHolder) viewHolder).mGlobalLayoutListener);

            ((AttSingleViewHolder) viewHolder).mExpandButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((AttSingleViewHolder) viewHolder).statusMsg.setVisibility(View.VISIBLE);
                    ((AttSingleViewHolder) viewHolder).mExpandLayout.expand();
                    ((AttSingleViewHolder) viewHolder).mExpandButton.setVisibility(View.GONE);
                    ((AttSingleViewHolder) viewHolder).mOverlayText.setVisibility(View.GONE);
                }
            });

            ((AttSingleViewHolder) viewHolder).mExpandButton.post(new Runnable() {
                @Override
                public void run() {
                    if (((AttSingleViewHolder) viewHolder).statusMsg.getLineCount() > 3) {
                        ((AttSingleViewHolder) viewHolder).mExpandLayout.setVisibility(View.VISIBLE);
                        ((AttSingleViewHolder) viewHolder).mExpandButton.setVisibility(View.VISIBLE);
                    } else {
                        ((AttSingleViewHolder) viewHolder).mExpandLayout.setVisibility(View.GONE);
                        ((AttSingleViewHolder) viewHolder).mExpandButton.setVisibility(View.GONE);
                    }
                }
            });

            if (item.getFlag()) {
                if (item.getUserLike().equals("false")) {
                    ((AttSingleViewHolder) viewHolder).mBtLoves.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (item.getUserLike().equalsIgnoreCase("false")) {
                                new saveLikeNotesPersonal(((AttSingleViewHolder) viewHolder)).execute(item.getMyuserid(), item.getUserid(), item.getId_comment(), item.getJumlahLove(), URL_SAVE_LOVES_PERSONAL);
                            }
                        }
                    });
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((AttSingleViewHolder) viewHolder).mBtLoves.setBackground(mContext.getResources().getDrawable(R.drawable.button_timeline_background));
                    }

                } else if (item.getUserLike().equals("true")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((AttSingleViewHolder) viewHolder).mBtLoves.setBackground(mContext.getResources().getDrawable(R.drawable.button_timeline_unfocused_pressed));
                        ((AttSingleViewHolder) viewHolder).mBtLoves.setEnabled(false);
                    }
                }

                if (item.getUserDislike().equals("false")) {
                    ((AttSingleViewHolder) viewHolder).mBtNix.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (item.getUserDislike().equalsIgnoreCase("false")) {
                                new saveLikeNotesPersonal(((AttSingleViewHolder) viewHolder)).execute(item.getMyuserid(), item.getUserid(), item.getId_comment(), item.getJumlahLove(), URL_SAVE_DISLIKE_PERSONAL);
                            }
                        }
                    });
                } else if (item.getUserLike().equals("true")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((AttSingleViewHolder) viewHolder).mBtNix.setEnabled(false);
                    }
                }
            } else {
                if (item.getUserLike().equals("false")) {
                    ((AttSingleViewHolder) viewHolder).mBtLoves.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (item.getUserLike().equalsIgnoreCase("false")) {
//                            new saveLikeNotes(fItemsHolder).execute(item.getMyuserid(), item.getUserid(), item.getId_comment(), item.getJumlahLove(), item.getIdRoomTab(), URL_SAVE_LIKE_COMMENTS);
                            }
                        }
                    });
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((AttSingleViewHolder) viewHolder).mBtLoves.setBackground(mContext.getResources().getDrawable(R.drawable.button_timeline_background));
                    }

                } else if (item.getUserLike().equals("true")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((AttSingleViewHolder) viewHolder).mBtLoves.setBackground(mContext.getResources().getDrawable(R.drawable.button_timeline_unfocused_pressed));
                        ((AttSingleViewHolder) viewHolder).mBtLoves.setEnabled(false);
                    }
                }

                if (item.getUserDislike().equals("false")) {
                    ((AttSingleViewHolder) viewHolder).mBtNix.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (item.getUserDislike().equalsIgnoreCase("false")) {
//                            new saveLikeNotes(fItemsHolder).execute(item.getMyuserid(), item.getUserid(), item.getId_comment(), item.getJumlahLove(), item.getIdRoomTab(), URL_SAVE_DISLIKE_COMMENTS);
                            }
                        }
                    });
                } else if (item.getUserLike().equals("true")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((AttSingleViewHolder) viewHolder).mBtNix.setEnabled(false);
                    }
                }
            }

            if (item.getProfile_photo() != null) {
                Picasso.with(mContext).load(item.getProfile_photo())
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .into(((AttSingleViewHolder) viewHolder).profilePic);
            } else {
                Picasso.with(mContext).load(R.drawable.ic_no_photo)
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .into(((AttSingleViewHolder) viewHolder).profilePic);
            }

            ((AttSingleViewHolder) viewHolder).feedImageView.setVisibility(View.GONE);
        } else {
            ((TextViewHolder) viewHolder).mBtNix.setVisibility(View.GONE);
            ((TextViewHolder) viewHolder).mBtLoves.setVisibility(View.GONE);
            ((TextViewHolder) viewHolder).dotA.setVisibility(View.GONE);
            ((TextViewHolder) viewHolder).dotB.setVisibility(View.GONE);

            if (item.getName2() != null) {
                ((TextViewHolder) viewHolder).mHiddenComment.setText(item.getName2() + " : " + item.getComment2());
                int jComment = Integer.parseInt(item.getJumlahComment().toString());
                if (jComment > 0) {
                    ((TextViewHolder) viewHolder).bind(mContext, i, mButtonClickListener);
//                    ((TextViewHolder) viewHolder).mHiddenComment.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (item.getFlag()) {
//                                Intent intent = new Intent(mContext, NoteCommentActivityV2.class);
//                                intent.putExtra("userid", item.getUserid());
//                                intent.putExtra("id_note", item.getId_note());
//                                intent.putExtra("id_comment", item.getId_comment());
//                                intent.putExtra("bc_user", item.getMyuserid());
//                                intent.putExtra("flag", item.getFlag());
//                                mContext.startActivity(intent);
//                            } else {
//                                Intent intent = new Intent(mContext, NoteCommentActivityV2.class);
//                                intent.putExtra("userid", item.getUserid());
//                                intent.putExtra("id_note", item.getId_note());
//                                intent.putExtra("id_comment", item.getId_comment());
//                                intent.putExtra("bc_user", item.getMyuserid());
//                                intent.putExtra("id_task", id_task);
//                                intent.putExtra("id_room_tab", item.getIdRoomTab());
//                                intent.putExtra("color", item.getHeaderColor());
//                                intent.putExtra("flag", item.getFlag());
//                                intent.putParcelableArrayListExtra(EXTRA_PARENT, parentItems);
//                                mContext.startActivity(intent);
//                            }
//                        }
//                    });
                }
            } else {
                ((TextViewHolder) viewHolder).mLinearHiddenComment.setVisibility(View.GONE);
            }

            ((TextViewHolder) viewHolder).mTotalLoves.setText(item.getJumlahLove());
            ((TextViewHolder) viewHolder).mTotalComments.setText(item.getJumlahComment());

            ((TextViewHolder) viewHolder).bind(mContext, i, mButtonClickListener);
//            ((TextViewHolder) viewHolder).mComments.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (item.getFlag()) {
//                        Intent intent = new Intent(mContext, NoteCommentActivityV2.class);
//                        intent.putExtra("userid", item.getUserid());
//                        intent.putExtra("id_note", item.getId_note());
//                        intent.putExtra("id_comment", item.getId_comment());
//                        intent.putExtra("bc_user", item.getMyuserid());
//                        intent.putExtra("flag", item.getFlag());
//                        mContext.startActivity(intent);
//                    } else {
//                        Intent intent = new Intent(mContext, NoteCommentActivityV2.class);
//                        intent.putExtra("userid", item.getUserid());
//                        intent.putExtra("id_note", item.getId_note());
//                        intent.putExtra("id_comment", item.getId_comment());
//                        intent.putExtra("id_task", id_task);
//                        intent.putExtra("bc_user", item.getMyuserid());
//                        intent.putExtra("id_room_tab", item.getIdRoomTab());
//                        intent.putExtra("color", item.getHeaderColor());
//                        intent.putExtra("flag", item.getFlag());
//                        intent.putParcelableArrayListExtra(EXTRA_PARENT, parentItems);
//                        mContext.startActivity(intent);
//                    }
//                }
//            });

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
            ((TextViewHolder) viewHolder).name.setText(titleHeader);

            SpannableString update = new SpannableString(Html.fromHtml("Updates on: " + item.getTimeStamp()));
            update.setSpan(new MyLeadingMarginSpan2(1, leftMargin), 0, update.length(), 0);
            ((TextViewHolder) viewHolder).timestamp.setText(update);

            String content = "";
            if (!item.getContent_comment().equalsIgnoreCase("null"))
                content = item.getContent_comment();
            SpannableString pesan = new SpannableString(Html.fromHtml(content));
            pesan.setSpan(new MyLeadingMarginSpan2(1, leftMargin), 0, pesan.length(), 0);
            ((TextViewHolder) viewHolder).statusMsg.setText(pesan);
            ((TextViewHolder) viewHolder).mOverlayText.setText(pesan);

            ((TextViewHolder) viewHolder).mOverlayText.getViewTreeObserver().addOnGlobalLayoutListener(((TextViewHolder) viewHolder).mGlobalLayoutListener);

            ((TextViewHolder) viewHolder).mExpandButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TextViewHolder) viewHolder).statusMsg.setVisibility(View.VISIBLE);
                    ((TextViewHolder) viewHolder).mExpandLayout.expand();
                    ((TextViewHolder) viewHolder).mExpandButton.setVisibility(View.GONE);
                    ((TextViewHolder) viewHolder).mOverlayText.setVisibility(View.GONE);
                }
            });

            ((TextViewHolder) viewHolder).mExpandButton.post(new Runnable() {
                @Override
                public void run() {
                    if (((TextViewHolder) viewHolder).statusMsg.getLineCount() > 3) {
                        ((TextViewHolder) viewHolder).mExpandLayout.setVisibility(View.VISIBLE);
                        ((TextViewHolder) viewHolder).mExpandButton.setVisibility(View.VISIBLE);
                    } else {
                        ((TextViewHolder) viewHolder).mExpandLayout.setVisibility(View.GONE);
                        ((TextViewHolder) viewHolder).mExpandButton.setVisibility(View.GONE);
                    }
                }
            });

            if (item.getFlag()) {
                if (item.getUserLike().equals("false")) {
                    ((TextViewHolder) viewHolder).mBtLoves.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (item.getUserLike().equalsIgnoreCase("false")) {
                                new saveLikeNotesPersonal(((TextViewHolder) viewHolder)).execute(item.getMyuserid(), item.getUserid(), item.getId_comment(), item.getJumlahLove(), URL_SAVE_LOVES_PERSONAL);
                            }
                        }
                    });
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((TextViewHolder) viewHolder).mBtLoves.setBackground(mContext.getResources().getDrawable(R.drawable.button_timeline_background));
                    }

                } else if (item.getUserLike().equals("true")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((TextViewHolder) viewHolder).mBtLoves.setBackground(mContext.getResources().getDrawable(R.drawable.button_timeline_unfocused_pressed));
                        ((TextViewHolder) viewHolder).mBtLoves.setEnabled(false);
                    }
                }

                if (item.getUserDislike().equals("false")) {
                    ((TextViewHolder) viewHolder).mBtNix.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (item.getUserDislike().equalsIgnoreCase("false")) {
                                new saveLikeNotesPersonal(((TextViewHolder) viewHolder)).execute(item.getMyuserid(), item.getUserid(), item.getId_comment(), item.getJumlahLove(), URL_SAVE_DISLIKE_PERSONAL);
                            }
                        }
                    });
                } else if (item.getUserLike().equals("true")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((TextViewHolder) viewHolder).mBtNix.setEnabled(false);
                    }
                }
            } else {
                if (item.getUserLike().equals("false")) {
                    ((TextViewHolder) viewHolder).mBtLoves.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (item.getUserLike().equalsIgnoreCase("false")) {
//                            new saveLikeNotes(fItemsHolder).execute(item.getMyuserid(), item.getUserid(), item.getId_comment(), item.getJumlahLove(), item.getIdRoomTab(), URL_SAVE_LIKE_COMMENTS);
                            }
                        }
                    });
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((TextViewHolder) viewHolder).mBtLoves.setBackground(mContext.getResources().getDrawable(R.drawable.button_timeline_background));
                    }

                } else if (item.getUserLike().equals("true")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((TextViewHolder) viewHolder).mBtLoves.setBackground(mContext.getResources().getDrawable(R.drawable.button_timeline_unfocused_pressed));
                        ((TextViewHolder) viewHolder).mBtLoves.setEnabled(false);
                    }
                }

                if (item.getUserDislike().equals("false")) {
                    ((TextViewHolder) viewHolder).mBtNix.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (item.getUserDislike().equalsIgnoreCase("false")) {
//                            new saveLikeNotes(fItemsHolder).execute(item.getMyuserid(), item.getUserid(), item.getId_comment(), item.getJumlahLove(), item.getIdRoomTab(), URL_SAVE_DISLIKE_COMMENTS);
                            }
                        }
                    });
                } else if (item.getUserLike().equals("true")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((TextViewHolder) viewHolder).mBtNix.setEnabled(false);
                    }
                }
            }

            if (item.getProfile_photo() != null) {
                Picasso.with(mContext).load(item.getProfile_photo())
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .into(((TextViewHolder) viewHolder).profilePic);
            } else {
                Picasso.with(mContext).load(R.drawable.ic_no_photo)
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .into(((TextViewHolder) viewHolder).profilePic);
            }

            ((TextViewHolder) viewHolder).feedImageView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position != 0 && position == getItemCount() - 1) {
            return TYPE_LOADER;
        } else {
            CommentModel item = feedItems.get(position);
            if (item.getType() == CommentModel.TYPE_ATT_BEFORE_AFTER) {
                return TYPE_ATT_BEFORE_AFTER;
            } else if (item.getType() == CommentModel.TYPE_ATT_SINGLE) {
                return TYPE_ATT_SINGLE;
            } else if (item.getType() == CommentModel.TYPE_ATT_MULTIPLE) {
                return TYPE_ATT_MULTIPLE;
            } else if (item.getType() == CommentModel.TYPE_HEADER) {
                return TYPE_HEADER;
            } else {
                return TYPE_TEXT;
            }
        }
    }


    @Override
    public long getItemId(int position) {

        // loader can't be at position 0
        // loader can only be at the last position
        if (position != 0 && position == getItemCount() - 1) {

            // id of loader is considered as -1 here
            return -1;
        }
        return getYourItemId(position);
    }

    public long getYourItemId(int position) {
        return Long.valueOf(feedItems.get(position).getId_comment());
    }

    public void showLoading(boolean status) {
        showLoader = status;
    }

    void setParentItems(CommentModel item) {
        parentItems = new ArrayList<>();
        CommentModel itemlempar = new CommentModel();
        itemlempar.setIdRoomTab(item.getIdRoomTab());
        itemlempar.setHeaderColor(item.getHeaderColor());
        itemlempar.setId_note(item.getId_note());
        itemlempar.setId_comment(item.getId_comment());
        itemlempar.setMyuserid(item.getMyuserid());
        itemlempar.setUserid(item.getUserid());
        itemlempar.setProfileName(item.getProfileName());
        itemlempar.setPhotos(item.getPhotos());
        itemlempar.setPhotoBefore(item.getPhotoBefore());
        itemlempar.setPhotoAfter(item.getPhotoAfter());
        itemlempar.setProfile_photo(item.getProfile_photo());
        itemlempar.setHeaderColor(item.getHeaderColor());
        itemlempar.setJumlahLove(item.getJumlahLove());
        itemlempar.setJumlahNix(item.getJumlahNix());
        itemlempar.setJumlahComment(item.getJumlahComment());
        itemlempar.setContent_comment(item.getContent_comment());
        itemlempar.setTimeStamp(item.getTimeStamp());
        itemlempar.setParent_id(item.getParent_id());
        itemlempar.setUserLike(item.getUserLike());
        itemlempar.setUserDislike(item.getUserDislike());
        itemlempar.setFlag(item.getFlag());
        parentItems.add(itemlempar);
    }

    public int getItemCount() {
//        return (null != feedItems ? feedItems.size() : 0);

        if (feedItems == null || feedItems.size() == 0) {
            return 0;
        }

        return feedItems.size() + 1;
    }


    public void setItems(List<CommentModel> items) {
        feedItems = items;
    }

    public void update(final List<CommentModel> es) {
        for (CommentModel e : es) {
            int i = findPosition(e);
            if (i >= 0) {
                if (!e.areContentsTheSame(feedItems.get(i))) {
                    e.setJumlahComment(feedItems.get(i).getJumlahComment());
                    e.setName2(feedItems.get(i).getName2());
                    e.setComment2(feedItems.get(i).getComment2());
                    feedItems.set(i, e);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void update(final CommentModel e) {
        int i = findPosition(e);
        if (i >= 0) {
            if (!e.areContentsTheSame(feedItems.get(i))) {
                e.setJumlahComment(feedItems.get(i).getJumlahComment());
                e.setName2(feedItems.get(i).getName2());
                e.setComment2(feedItems.get(i).getComment2());
                feedItems.set(i, e);
            }
        }
        notifyDataSetChanged();
    }

    public int findPosition(CommentModel e) {
        if (feedItems == null) {
            return -1;
        }

        int size = feedItems.size();
        for (int i = 0; i < size; i++) {
            if (feedItems.get(i).equals(e)) {
                return i;
            }
        }

        return -1;
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
