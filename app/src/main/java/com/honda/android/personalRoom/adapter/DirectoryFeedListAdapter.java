package com.honda.android.personalRoom.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.honda.android.ByonChatMainRoomActivity;
import com.honda.android.ConversationActivity;
import com.honda.android.MediaPlayCatalogActivity;
import com.honda.android.NewsDetailActivity;
import com.honda.android.R;
import com.honda.android.WebViewByonActivity;
import com.honda.android.communication.NetworkInternetConnectionStatus;
import com.honda.android.personalRoom.NoteCommentActivity;
import com.honda.android.personalRoom.model.NewsFeedItem;
import com.honda.android.personalRoom.viewHolder.feedItemsHolderNews;
import com.honda.android.provider.Message;
import com.honda.android.utils.MediaProcessingUtil;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by lukma on 3/7/2016.
 */
/*NEW*/
public class DirectoryFeedListAdapter extends RecyclerView.Adapter<DirectoryFeedListAdapter.feedItemsHolderDirec> {
    /*NEW*/
    private List<NewsFeedItem> feedItems;
    private DirectoryFeedListAdapter adapter;
    ImageLoader imageLoader;
    /*NEW*/
    Context mContext;
    private static final String TAG = DirectoryFeedListAdapter.class.getSimpleName();

    public DirectoryFeedListAdapter(Activity context) {
        feedItems = new ArrayList<>();
        mContext = context;
    }

    public void setData(List<NewsFeedItem> photoList) {
        feedItems.clear();
        feedItems.addAll(photoList);
        this.notifyItemRangeInserted(0, feedItems.size() - 1);
    }

    public feedItemsHolderDirec onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_room_chat_order, null);
        feedItemsHolderDirec fHolder;
        fHolder = new feedItemsHolderDirec(v);

        return fHolder;
    }

    public void onBindViewHolder(final feedItemsHolderDirec fItemsHolder, final int i) {
        final NewsFeedItem item = feedItems.get(i);

        fItemsHolder.txtName.setText(item.getTitle());

        String text = Html.fromHtml(item.getStatus()).toString();
        if (text.contains("<")) {
            text = Html.fromHtml(Html.fromHtml(text).toString()).toString();
        }
        fItemsHolder.txtInfo.setText(text);
        fItemsHolder.btnChat.setVisibility(View.GONE);

        Picasso.with(mContext).load(item.getImage()).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(fItemsHolder.view);
        fItemsHolder.relativeLayout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.getLevel() == 1) {
                    Intent intent = new Intent(mContext, ConversationActivity.class);
                    intent.putExtra(ConversationActivity.KEY_JABBER_ID, item.getUrl());
                    mContext.startActivity(intent);
                } else if (item.getLevel() == 2) {
                    JSONObject jObject = null;
                    try {
                        jObject = new JSONObject(item.getUrl());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (jObject != null) {
                        try {
                            if (jObject.has("u")) {

                                String username = jObject.getString("u");
                                String targetUrl = jObject.getString("p");

                                Intent intent = new Intent(mContext, ByonChatMainRoomActivity.class);
                                intent.putExtra(ConversationActivity.KEY_JABBER_ID, username);
                                intent.putExtra(ConversationActivity.KEY_TITLE, targetUrl);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.startActivity(intent);

                            } else {

                                Toast.makeText(mContext, "Cannot Open!", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            Toast.makeText(mContext, "Cannot Open!!", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    } else {
                        Intent intent2 = new Intent(mContext, ByonChatMainRoomActivity.class);
                        intent2.putExtra(ConversationActivity.KEY_JABBER_ID, item.getUrl());
                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent2);
                    }

                } else if (item.getLevel() == 3) {
                    if (NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)) {
                        Intent intent = new Intent(mContext, WebViewByonActivity.class);
                        intent.putExtra(WebViewByonActivity.KEY_LINK_LOAD, item.getUrl());
                        intent.putExtra(WebViewByonActivity.KEY_COLOR, item.getColorHeader());
                        mContext.startActivity(intent);
                    } else {
                        Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    public int getItemCount() {
        return (null != feedItems ? feedItems.size() : 0);
    }

    class feedItemsHolderDirec extends RecyclerView.ViewHolder {
        TextView txtName;
        TextView txtInfo;
        ImageView btnChat;
        RelativeLayout relativeLayout5;
        Target view;

        public feedItemsHolderDirec(View view) {
            super(view);
            this.relativeLayout5 = (RelativeLayout) view.findViewById(R.id.relativeLayout5);
            this.btnChat = (ImageView) view.findViewById(R.id.btnChat);
            this.view = (Target) view.findViewById(R.id.imagePhoto);
            this.txtName = (TextView) view.findViewById(R.id.textTitle);
            this.txtInfo = (TextView) view.findViewById(R.id.textInfo);
        }
    }

}