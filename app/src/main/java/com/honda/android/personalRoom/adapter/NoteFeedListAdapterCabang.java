package com.honda.android.personalRoom.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.honda.android.R;
import com.honda.android.personalRoom.NoteCommentActivity;
import com.honda.android.personalRoom.asynctask.ProfileSaveDescription;
import com.honda.android.personalRoom.model.CommentModel;
import com.honda.android.personalRoom.model.NoteFeedItem;
import com.honda.android.personalRoom.utils.Level;
import com.honda.android.personalRoom.viewHolder.feedItemsHolder;
import com.honda.android.utils.CustomVolleyRequestQueue;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lukma on 3/7/2016.
 */
/*NEW*/
public class NoteFeedListAdapterCabang extends RecyclerView.Adapter<feedItemsHolder>{
    /*NEW*/
//public class NoteFeedListAdapter  extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<NoteFeedItem> feedItems;
    //    private List<CommentModel> commentItems;
    private NoteCommentListAdapter adapter;
    private RecyclerView mRecyclerView;
    Context contxt;
    ImageLoader imageLoader;
    private static final String URL_LIST_VIEW_COMMENT = "https://b.byonchat.com/personal_room/webservice/view/comment.php";
    int level;
    feedItemsHolder feedItemsPool[];
    public String[] innerData = {"1", "2", "3", "4", "lima", "enam"};
    /*NEW*/
    private Context mContext;
    private static final String TAG = NoteFeedListAdapter.class.getSimpleName();

    public NoteFeedListAdapterCabang(Context context, List<NoteFeedItem> feedItems) {
        this.feedItems = feedItems;
        this.mContext = context;
    }

    public feedItemsHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feed_item, null);

        feedItemsHolder fHolder;
        fHolder = new feedItemsHolder(v);
       // fHolder.cabangRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        return fHolder;
    }

    public void onBindViewHolder(final feedItemsHolder fItemsHolder, final int i) {

        final NoteFeedItem item = feedItems.get(i);

     //   fItemsHolder.setLevel(item.getLevel());

        fItemsHolder.mComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, NoteCommentActivity.class);
//                Toast.makeText(mContext, item.getId().toString(), Toast.LENGTH_SHORT).show();
                intent.putExtra("userid", item.getUserid());
                intent.putExtra("id_note", item.getId());
                mContext.startActivity(intent);
            }
        });

        if(i>0){
          //  fItemsHolder.WriteProfile.setVisibility(View.GONE);
//            feedItemsHolder.SubmitNotes.setVisibility(View.GONE);
        }

        if (imageLoader == null) imageLoader = CustomVolleyRequestQueue.getInstance(mContext.getApplicationContext()).getImageLoader();

        fItemsHolder.mTotalLoves.setText(item.getJumlahLove());
        fItemsHolder.mTotalComments.setText(item.getJumlahComment());
        fItemsHolder.name.setText(item.getName());

        // Converting timestamp into x ago format
        fItemsHolder.timestamp.setText(item.getTimeStamp());

        // Chcek for empty status message
        if (!TextUtils.isEmpty(item.getStatus())) {
            fItemsHolder.statusMsg.setText(item.getStatus());
            fItemsHolder.statusMsg.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            fItemsHolder.statusMsg.setVisibility(View.GONE);
        }

        // user profile pic
        /*if (item.getProfilePic() != null) {

            fItemsHolder.profilePic.setImageUrl(item.getProfilePic(), imageLoader);
            fItemsHolder.profilePic.setVisibility(View.VISIBLE);
        } else {
            // url is null, remove from the view
//            feedItemsHolder.profilePic.setVisibility(View.GONE);
            fItemsHolder.mLinearProfilePic.setVisibility(View.GONE);
            fItemsHolder.mLinearProfilePicd.setVisibility(View.VISIBLE);
            Resources res = contxt.getResources();
            Drawable drawable = res.getDrawable( R.drawable.ic_no_photo );
            fItemsHolder.profilePicDrawable.setImageDrawable(drawable);
            fItemsHolder.profilePicDrawable.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_no_photo));
            fItemsHolder.profilePicDrawable.setVisibility(View.VISIBLE);
        }*/

        if (item.getProfilePic() != null) {
            Picasso.with(mContext).load(item.getProfilePic())
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(fItemsHolder.profilePic);
        } else {
               /* // url is null, remove from the view
                fItemsHolder.mLinearProfilePic.setVisibility(View.GONE);
                fItemsHolder.mLinearProfilePicd.setVisibility(View.VISIBLE);
                Resources res = mContext.getResources();
                Drawable drawable = res.getDrawable(R.drawable.ic_no_photo);
                fItemsHolder.profilePicDrawable.setImageDrawable(drawable);
                fItemsHolder.profilePicDrawable.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_no_photo));
                fItemsHolder.profilePicDrawable.setVisibility(View.VISIBLE);*/
            Picasso.with(mContext).load(R.drawable.ic_no_photo)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .into(fItemsHolder.profilePic);
        }


        // Feed image
        if (item.getImage() != null) {
            fItemsHolder.feedImageView.setVisibility(View.VISIBLE);
        } else {
            fItemsHolder.feedImageView.setVisibility(View.GONE);
        }

        if (item.getName2() != null) {
            fItemsHolder.mHiddenComment.setText(item.getName2()+": "+item.getComment2());
            fItemsHolder.feedImageView.setVisibility(View.VISIBLE);

            int jComment = Integer.parseInt(item.getJumlahComment().toString());
            if( jComment > 0){
                fItemsHolder.mHiddenComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        feedItems = new ArrayList<NoteFeedItem>();
//                        adapter = new NoteCommentListAdapter(mContext, feedItems);
                        mRecyclerView = new RecyclerView(mContext);
//                        getListViewComments(item.getUserid(), item.getId(),fItemsHolder,i, feedItems, adapter, mRecyclerView);
                        feedItems = new ArrayList<NoteFeedItem>();
//                        adapter = new NoteCommentListAdapter(mContext, feedItems);
                        mRecyclerView.setAdapter(adapter);
//                        FragmentMyProfile fragment2 = new FragmentMyProfile();
//                        FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
//                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                        fragmentTransaction.replace(R.id.FragmentMyProfile, fragment2);
//                        fragmentTransaction.commit();
                    }
                });
            }
        } else {
            fItemsHolder.mHiddenComment.setVisibility(View.GONE);
            fItemsHolder.mLinearHiddenComment.setVisibility(View.GONE);
        }

        if(item.getLevel() == Level.LEVEL_ONE){
            level = Level.LEVEL_TWO;
        }else if(item.getLevel() == Level.LEVEL_TWO){
            level = Level.LEVEL_THREE;
        }
    }

    public int getItemCount() {
        return (null != feedItems ? feedItems.size() : 0);
    }

    private void getListViewComments(final String userid, String id_note, final feedItemsHolder feedItemsHolder, int i, final List<CommentModel> commentItems, final NoteCommentListAdapter adapter, final RecyclerView mRecyclerView) {
        class ambilComment extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
            String result = "";
            InputStream inputStream = null;

            @Override
            protected void onPreExecute() {
                feedItemsHolder.mLoading.setVisibility(View.GONE);
                feedItemsHolder.mHiddenComment.setVisibility(View.GONE);
                feedItemsHolder.mLinearHiddenComment.setVisibility(View.GONE);
                feedItemsHolder.mLoading.setVisibility(View.VISIBLE);
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("userid", params[0]);
                data.put("id_note", params[1]);
                String result = profileSaveDescription.sendPostRequest(URL_LIST_VIEW_COMMENT,data);
                return  result;
            }

            protected void onPostExecute(String s) {
                JSONArray dataJsonArr = null;
                if(s.equals(null)){
                    Toast.makeText(mContext, "Internet Problem.", Toast.LENGTH_SHORT).show();
                }else{
                    try{
                        JSONObject json = new JSONObject(s);
                        String id_note = json.getString("id_note");

                        dataJsonArr = json.getJSONArray("data");
                        for (int i = 0; i < dataJsonArr.length(); i++) {

                            JSONObject c = dataJsonArr.getJSONObject(i);

                            String id_comment = c.getString("id_comment");
                            String uid = c.getString("userid");
                            String profile_name = c.getString("profile_name");
                            String profile_photo = c.getString("profile_photo");
                            String amount_of_like = c.getString("amount_of_like");
                            String amount_of_dislike = c.getString("amount_of_dislike");
                            String amount_of_comment = c.getString("amount_of_comment");
                            String content_comment = c.getString("content_comment");
                            String tgl_comment = c.getString("tgl_comment");
                            String parent_id = c.getString("parent_id");

                            CommentModel citem = new CommentModel();
                            citem.setId_note(id_note);
                            citem.setId_comment(id_comment);
                            citem.setUserid(uid);
                            citem.setProfileName(profile_name);
                            String pPhoto = c.isNull("profile_photo") ? null : c.getString("profile_photo");
                            citem.setProfile_photo(pPhoto);
                            citem.setJumlahLove(amount_of_like);
                            citem.setJumlahNix(amount_of_dislike);
                            citem.setJumlahComment(amount_of_comment);
                            citem.setContent_comment(content_comment);
                            citem.setTimeStamp(tgl_comment);
                            String prntID = c.isNull("parent_id") ? null : c.getString("parent_id");
                            citem.setParent_id(prntID);

                            citem.setLevel(level);
                            commentItems.add(citem);

                        }
                        adapter.notifyDataSetChanged();

                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                    feedItemsHolder.mLoading.setVisibility(View.GONE);
                }
            }
        }
        ambilComment ru = new ambilComment();
        ru.execute(userid, id_note);
    }

}