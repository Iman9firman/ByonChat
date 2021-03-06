package com.byonchat.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.adapter.GalleryAdapter;
import com.byonchat.android.adapter.RecyclerItemGalleryViewHolder;
import com.byonchat.android.http.JobCompleted;
import com.byonchat.android.http.RequestGet;
import com.byonchat.android.model.Image;
import com.byonchat.android.personalRoom.listener.HidingScrollListener;
import com.byonchat.android.personalRoom.model.PictureModel;
import com.byonchat.android.utils.HttpHelper;
import com.byonchat.android.utils.Utility;
import com.byonchat.android.utils.UtilsPD;
import com.byonchat.android.view.GridSpacingItemDecoration;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.byonchat.android.NoteCommentActivityV2.URL_LIST_NOTE_COMMENT;

public class GalleryTaskActivity extends AppCompatActivity {

    RecyclerView vRecyclerView;
    ProgressBar vProgressBar;
    Toolbar toolbar;
    TextView vTitle;
    private GridLayoutManager gridLayoutManager;
    private GridSpacingItemDecoration itemOffsetDecoration;

    String color;
    ArrayList<PictureModel> pictureModels = new ArrayList<>();
    private GalleryAdapter mAdapter;
    private int imageColumns;
    public static final int BeforeAfter = 700;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_task);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.BLACK);
        }

        vRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        vProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        toolbar = (Toolbar) findViewById(R.id.abMain);
        vTitle = (TextView) findViewById(R.id.title_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        vTitle.setText("Gallery");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        color = getIntent().getStringExtra("color");
        if (color != null) {
            toolbar.setBackgroundColor(Color.parseColor("#" + color));
        }

        pictureModels = new ArrayList<>();
        setRecyclerView();
        refreshItem();

        /*RequestGet getAsync = new RequestGet(new JobCompleted() {
            @Override
            public void onTaskBegin() {

            }

            @Override
            public void onTaskDone(String result) {

            }
        }, getApplicationContext());
        getAsync.execute("/bc_voucher_client/webservice/list_api/get_audit_rutin_galleries.php");*/
    }

    void setRecyclerView() {
        pictureModels = new ArrayList<PictureModel>();
        mAdapter = new GalleryAdapter(this, pictureModels, new RecyclerItemGalleryViewHolder.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                PictureModel item = pictureModels.get(position);
                if (getIntent().getExtras().containsKey("id_comment")) {
                    String userid = getIntent().getStringExtra("userid");
                    String id_note = getIntent().getStringExtra("id_note");
                    String id_comment = getIntent().getStringExtra("id_comment");
                    String id_task = getIntent().getStringExtra("id_task");
                    String bc_user = getIntent().getStringExtra("bc_user");
                    String idRoomTab = "";
                    if (getIntent().getExtras().containsKey("id_room_tab")) {
                        idRoomTab = getIntent().getStringExtra("id_room_tab");
                    }

                    Intent intent = new Intent(getApplicationContext(), GalleryBeforeAfterActivity.class);
                    intent.putExtra("userid", userid);
                    intent.putExtra("id_note", id_note);
                    intent.putExtra("id_task", id_task);
                    intent.putExtra("id_comment", id_comment);
                    intent.putExtra("bc_user", bc_user);
                    intent.putExtra("id_room_tab", idRoomTab);
                    intent.putExtra("color", getIntent().getStringExtra("color"));
                    intent.putExtra("url_before", item.getUrl());
                    intent.putExtra("api_url", getIntent().getStringExtra("api_url"));
                    startActivityForResult(intent, BeforeAfter);
                } else {
                    String userid = getIntent().getStringExtra("userid");
                    String id_note = getIntent().getStringExtra("id_note");
                    String bc_user = getIntent().getStringExtra("bc_user");
                    String id_task = getIntent().getStringExtra("id_task");
                    String idRoomTab = "";
                    if (getIntent().getExtras().containsKey("id_room_tab")) {
                        idRoomTab = getIntent().getStringExtra("id_room_tab");
                    }

                    Intent intent = new Intent(getApplicationContext(), GalleryBeforeAfterActivity.class);
                    intent.putExtra("userid", userid);
                    intent.putExtra("id_note", id_note);
                    intent.putExtra("id_task", id_task);
                    intent.putExtra("bc_user", bc_user);
                    intent.putExtra("id_room_tab", idRoomTab);
                    intent.putExtra("color", getIntent().getStringExtra("color"));
                    intent.putExtra("url_before", item.getUrl());
                    intent.putExtra("api_url", getIntent().getStringExtra("api_url"));
                    startActivityForResult(intent, BeforeAfter);
                }
            }
        }, new RecyclerItemGalleryViewHolder.OnItemLongClickListener() {
            @Override
            public void onItemLongPress(int position) {

            }
        });
        orientationBasedUI(getResources().getConfiguration().orientation);
        setItemDecoration(imageColumns);
        vRecyclerView.setAdapter(mAdapter);

        vRecyclerView.addOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
            }

            @Override
            public void onShow() {
            }
        });
    }

    private void orientationBasedUI(int orientation) {
        imageColumns = orientation == Configuration.ORIENTATION_PORTRAIT ? 4 : 6;

        int columns = imageColumns;
        gridLayoutManager = new GridLayoutManager(this, columns);
        vRecyclerView.setLayoutManager(gridLayoutManager);
        vRecyclerView.setHasFixedSize(true);
        setItemDecoration(columns);
    }

    private void setItemDecoration(int columns) {
        gridLayoutManager.setSpanCount(columns);
        if (itemOffsetDecoration != null)
            vRecyclerView.removeItemDecoration(itemOffsetDecoration);
        itemOffsetDecoration = new GridSpacingItemDecoration(columns, getResources().getDimensionPixelSize(R.dimen.item_padding), false);
        vRecyclerView.addItemDecoration(itemOffsetDecoration);
    }

    private void setImageAdapter(ArrayList<PictureModel> images) {
        mAdapter.setData(images);
        setItemDecoration(imageColumns);
        vRecyclerView.setAdapter(mAdapter);
    }

    void refreshList(String result) {
        if (Utility.isJSONValid(result)) {
            pictureModels.clear();
            try {
                JSONObject jObj = new JSONObject(result);
                JSONArray jArr = new JSONArray(jObj.getString("galleries"));
                if (jArr.length() > 0) {
                    for (int i = 0; i < jArr.length(); i++) {
                        String json = jArr.getString(i).toString();
                        PictureModel data = new PictureModel();
                        data.setUrl(json.toString());
                        data.setDrawable(0);
                        pictureModels.add(data);
                    }
                    mAdapter.notifyDataSetChanged();
                    showContent();
//                    setImageAdapter(pictureModels);
                } else {
                    showContent();
                    Toast.makeText(this, "Empty data.", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    void showContent() {
        vProgressBar.setVisibility(View.GONE);
        vRecyclerView.setVisibility(View.VISIBLE);
    }

    void refreshItem() {
        if (getIntent().getExtras().containsKey("id_comment")) {
            String userid = getIntent().getStringExtra("userid");
            String id_note = getIntent().getStringExtra("id_note");
            String id_comment = getIntent().getStringExtra("id_comment");
            String id_task = getIntent().getStringExtra("id_task");
            String bc_user = getIntent().getStringExtra("bc_user");
            String idRoomTab = "";
            if (getIntent().getExtras().containsKey("id_room_tab")) {
                idRoomTab = getIntent().getStringExtra("id_room_tab");
            }
            Log.w("intentIDTASKCOMMENT", id_task);
            new HATPost().execute(getIntent().getStringExtra("api_url"), id_task);

        } else {
            String userid = getIntent().getStringExtra("userid");
            String id_note = getIntent().getStringExtra("id_note");
            String bc_user = getIntent().getStringExtra("bc_user");
            String id_task = getIntent().getStringExtra("id_task");
            String idRoomTab = "";
            if (getIntent().getExtras().containsKey("id_room_tab")) {
                idRoomTab = getIntent().getStringExtra("id_room_tab");
            }
            Log.w("intentIDTASK", id_task);

            new HATPost().execute(getIntent().getStringExtra("api_url"), id_task);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BeforeAfter && resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent();
            if (data.getExtras().containsKey("id_comment")) {
                intent.putExtra("userid", data.getStringExtra("userid"));
                intent.putExtra("id_note", data.getStringExtra("id_note"));
                intent.putExtra("id_comment", data.getStringExtra("id_comment"));
                intent.putExtra("id_task", data.getStringExtra("id_task"));
                intent.putExtra("bc_user", data.getStringExtra("bc_user"));
                if (data.getExtras().containsKey("id_room_tab")) {
                    intent.putExtra("id_room_tab", data.getExtras().containsKey("id_room_tab"));
                }
                intent.putExtra("api_url", data.getStringExtra("api_url"));

                /*String userid = data.getStringExtra("userid");
                String id_note = data.getStringExtra("id_note");
                String id_comment = data.getStringExtra("id_comment");
                String id_task = data.getStringExtra("id_task");
                String bc_user = data.getStringExtra("bc_user");
                String idRoomTab = "";
                if (data.getExtras().containsKey("id_room_tab")) {
                    idRoomTab = data.getStringExtra("id_room_tab");
                }

                NoteCommentActivityV2.activity.getListCommentinComment(userid, id_note, id_comment, bc_user, idRoomTab, true);*/

            } else {
                intent.putExtra("userid", data.getStringExtra("userid"));
                intent.putExtra("id_note", data.getStringExtra("id_note"));
                intent.putExtra("id_task", data.getStringExtra("id_task"));
                intent.putExtra("bc_user", data.getStringExtra("bc_user"));
                if (data.getExtras().containsKey("id_room_tab")) {
                    intent.putExtra("id_room_tab", data.getExtras().containsKey("id_room_tab"));
                }
                intent.putExtra("api_url", data.getStringExtra("api_url"));
                /*String userid = data.getStringExtra("userid");
                String id_note = data.getStringExtra("id_note");
                String bc_user = data.getStringExtra("bc_user");
                String id_task = data.getStringExtra("id_task");
                String idRoomTab = "";
                if (data.getExtras().containsKey("id_room_tab")) {
                    idRoomTab = data.getStringExtra("id_room_tab");
                }
                NoteCommentActivityV2.activity.getListComment(userid, idRoomTab, id_note, bc_user, URL_LIST_NOTE_COMMENT, true);*/
            }
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class HATPost extends AsyncTask<String, Void, String> {
        final int REGISTRATION_TIMEOUT = 3 * 1000;
        final int WAIT_TIMEOUT = 30 * 1000;
        final HttpClient httpclient = new DefaultHttpClient();

        final HttpParams params = httpclient.getParams();
        HttpResponse httpResponse;
        String result = null;
        boolean error = false;
        InputStream inputStream = null;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {

            return POST(getApplicationContext(), params[0], params[1]);
        }

        public String POST(Context context, String... params) {

            try {
                HttpClient httpClient = HttpHelper
                        .createHttpClient(context);
                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost httpPost = new HttpPost(params[0]);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);
                nameValuePairs.add(new BasicNameValuePair("task_id", params[1]));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                httpResponse = httpclient.execute(httpPost);
                StatusLine statusLine = httpResponse.getStatusLine();

                Log.w("hasil", statusLine.getStatusCode() + "");
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    httpResponse.getEntity().writeTo(out);
                    out.close();
                    result = out.toString();
                } else {
                    error = true;
                    result = statusLine.getReasonPhrase();
                    httpResponse.getEntity().getContent().close();
                    throw new IOException(result);
                }

                Log.w(GalleryTaskActivity.class.getSimpleName(), params[0] + " ; " + result.toString());

            } catch (ClientProtocolException e) {
                result = e.getMessage();
                error = true;
            } catch (IOException e) {
                result = e.getMessage();
                error = true;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            refreshList(result);
            Log.w("hasilnya", result);
        }
    }
}
